/*******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.widl

import scala.collection.mutable.{ HashMap => MHashMap, Map => MMap, ListBuffer => MListBuffer }
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{ NodeFactory => NF, Span, SourceLocRats }
import kr.ac.kaist.jsaf.scala_src.nodes._
import java.util.{List => JList}

// libraries = ["webapis.tv.channel", ...]
object WIDLTypeMap extends Walker {
  ////////////////////////////////////////////////////////////////////////////////
  // Database
  ////////////////////////////////////////////////////////////////////////////////
  // api |-> definitions
  var dbs: MMap[String, List[WDefinition]] = MHashMap[String, List[WDefinition]]()

  ////////////////////////////////////////////////////////////////////////////////
  // WIDL Nodes
  ////////////////////////////////////////////////////////////////////////////////
  var constructorMap = new MHashMap[String, MListBuffer[WEAConstructor]]
  var enumMap = new MHashMap[String, WEnum]
  var interfaceMap = new MHashMap[String, WInterface]
  var callbackMap = new MHashMap[String, WCallback]
  var implementsMap = new MHashMap[String, MListBuffer[WImplementsStatement]]
  var typedefMap = new MHashMap[String, WTypedef]
  var dictionaryMap = new MHashMap[String, WDictionary]
  var interfOperationMap = new MHashMap[String, WOperation]
  var arrayMap = new MHashMap[String, WType]
  // interface |-> parent interfaces
  var impdbs: MMap[String, List[String]] = MHashMap[String, List[String]]()
  // interface |-> variables
  var vardbs: MMap[String, List[(String, WInterfaceMember)]] = MHashMap[String, List[(String, WInterfaceMember)]]()
  // (interface, variable) |-> type
  var typedbs: MMap[(String, String), String] = MHashMap[(String, String), String]()
  // interface |-> functions
  var fundbs: MMap[String, List[(String, WInterfaceMember)]] = MHashMap[String, List[(String, WInterfaceMember)]]()


  ////////////////////////////////////////////////////////////////////////////////
  // Function Argument Size
  ////////////////////////////////////////////////////////////////////////////////
  var argSizeMap = new MHashMap[String, (Int, Int)]

  def getType(typ: WType): Option[String] = typ match {
    case SWNamedType(_, _, n) => Some(n)
    case SWArrayType(_, _, t) => getType(t) match {
      case Some(n) => Some(n+"[]")
      case _ => Some("[]")
    }
    case _ => None // Not yet implemented : In case of other types...?
  }

  def addAllMembers(now: String, wdefs: List[WDefinition]): Unit = {
    wdefs.foreach(wdef => wdef match {
      case SWModule(_, _, n, list) => addAllMembers(n, list);
      case SWInterface(_, _, n, _, list) => addAllInterfaceMembers(n, list);
      case SWImplementsStatement(_, _, n, m) =>
        if (impdbs.contains(n)) impdbs.update(n, m :: impdbs(n))
        else impdbs.update(n, List(m))
      case _ => None
    })
  }

  def addAllInterfaceMembers(now: String, wintmems: List[WInterfaceMember]): Unit = {
    wintmems.foreach(wintmem => wintmem match {
      case SWConst(_, _, t, n, _) => getType(t) match {
        case Some(typ) =>
          typedbs.update((now, n), typ)
          if (vardbs.contains(now)) vardbs.update(now, (n, wintmem) :: vardbs(now))
          else vardbs.update(now, List((n, wintmem)))
        case _ =>
      }
      case SWAttribute(_, _, t, n, _) => getType(t) match {
        case Some(typ) =>
          typedbs.update((now, n), typ)
          if (vardbs.contains(now)) vardbs.update(now, (n, wintmem) :: vardbs(now))
          else vardbs.update(now, List((n, wintmem)))
        case _ =>
      }
      case SWOperation(_, _, _, _, n, _, _) => n match {
        case Some(n) =>
          if (fundbs.contains(now)) fundbs.update(now, (n, wintmem) :: fundbs(now))
          else fundbs.update(now, List((n, wintmem)))
        case None =>
      }
      case _ =>
    })
  }

  def readDB(api: String) = dbs.get(api) match {
    case None =>
      val db = DBToWIDL.readDB(api)
      dbs.update(api, db)
      addAllMembers("Window", db); db
    case Some(db) => db
  }

  private val sl = new SourceLocRats("WIDLChecker", 0, 0, 0)
  private val span = new Span(sl, sl)
  def freshName(name: String) = "__WIDLChecker__" + name
  def mkId(name: String) = NF.makeId(span, name)
  def mkFreshId(name: String) = NF.makeId(span, freshName(name))

  def initAll() = {
    // Initialize variables
    dbs = MHashMap[String, List[WDefinition]]()
    constructorMap = new MHashMap[String, MListBuffer[WEAConstructor]]
    enumMap = new MHashMap[String, WEnum]
    interfaceMap = new MHashMap[String, WInterface]
    callbackMap = new MHashMap[String, WCallback]
    implementsMap = new MHashMap[String, MListBuffer[WImplementsStatement]]
    typedefMap = new MHashMap[String, WTypedef]
    dictionaryMap = new MHashMap[String, WDictionary]
    interfOperationMap = new MHashMap[String, WOperation]
    arrayMap = new MHashMap[String, WType]
    argSizeMap = new MHashMap[String, (Int, Int)]
    impdbs = MHashMap[String, List[String]]()
    vardbs = MHashMap[String, List[(String, WInterfaceMember)]]()
    typedbs = MHashMap[(String, String), String]()
    fundbs = MHashMap[String, List[(String, WInterfaceMember)]]()
  }

  def setLibraries(libraries: List[String]): Unit = {
    // Reset
    initAll()
    // Read libraries (databases)
    for (lib <- libraries) readDB(lib)
    typedbs.update(("top-level", "window"), "Window")
    // Collect some nodes
    walkWIDL()
  }

  def walkWIDL(): Unit = {
    object widlWalker extends WIDLWalker {
      override def walkUnit(node: Any): Unit = {
        node match {
          case node@SWInterface(info, attrs, name, parent, members) => {
            for(attr <- attrs) {
              attr match {
                case const: WEAConstructor => constructorMap.getOrElseUpdate(name, new MListBuffer).append(const)
                case _ =>
              }
            }
            members.foreach(mem => mem match {
              case node@SWOperation(_, _, _, _, n, _, _) => n match {
                case Some(opname) => interfOperationMap.put(name+"."+opname, node)
                case None => // unreachable
              }
              case _ =>
            })
            interfaceMap.put(name, node)
            if (WIDLHelper.isCallback(node)){ // TODO: refactoring
              val mem: WInterfaceMember = node.getMembers().get(0)
              val op: WOperation = mem.asInstanceOf[WOperation]
              val callback: WCallback = WIDLFactory.mkCallback(info.getSpan, name, op.getTyp, op.getArgs)
              callbackMap.put(name, callback)
            }
          }
          case node@SWCallback(info, attrs, name, parent, args) => callbackMap.put(name, node)
          case node@SWDictionary(info, attrs, name, parent, members) => dictionaryMap.put(name, node)
          case node@SWEnum(info, attrs, name, enumValueList) => enumMap.put(name, node)
          case node@SWTypedef(info, attrs, typ, name) => typedefMap.put(name, node)
          case node@SWImplementsStatement(info, attrs, name, parent) => implementsMap.getOrElseUpdate(name, new MListBuffer).append(node)
          case node@SWArrayType(info, suffix, type2) => getType(type2) match {
            case Some(name) => arrayMap.put(name, type2)
            case _ =>
          }
          case node@SWSequenceType(info, suffix, type2) => getType(type2) match {
            case Some(name) => arrayMap.put(name, type2)
            case _ =>
          }
          case _ =>
        }
        super.walkUnit(node)
      }
    }
    for((dbNames, widlList) <- dbs) for(widl <- widlList) widlWalker.walkUnit(widl)
  }

  // find a pair of an owner type and a member
  def getMembers(typ: String): List[(String, (String, WInterfaceMember))] =
    impdbs.getOrElse(typ, Nil).foldLeft(vardbs.getOrElse(typ, Nil).map(v => (typ, v)) ++
      fundbs.getOrElse(typ, Nil).map(f => (typ, f)))((res, p) => res ++ getMembers(p))

  def getAPI(obj: LHS, y: String): Option[String] = obj match {
    case SDot(_, dot: LHS, SId(_, x, _, _)) => getAPI(dot, x) match {
      case Some(typ) =>
        (getMembers(typ).find(p => p._2._1.equals(x))) match {
          case Some((ty, _)) => Some(typedbs(ty, x))
          case None => None
        }
      case None => None
    }
    case SVarRef(_, SId(_, x, _, _)) if x.equals("webapis") =>
      try {
        Some(typedbs(("WindowWebAPIs", "webapis")))
      } catch {
        case e => None
      }
    case SVarRef(_, SId(_, x, _, _)) if x.equals("tizen") =>
      try {
        Some(typedbs(("TizenObject", "tizen")))
      } catch {
        case e => None
      }
    case _ => None
  }
}
