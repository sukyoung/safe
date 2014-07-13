/******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.widl

import scala.collection.mutable.{HashMap => MHashMap, HashSet => MHashSet, ListBuffer}
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.cfg.{Node => CNode, InternalError => IError}
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.{SemanticsExpr => SE}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.models.builtin.{BuiltinArray, BuiltinDate}
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T, _}
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.scala_src.nodes._
import java.util.{List => JList}
import kr.ac.kaist.jsaf.nodes_util.IRFactory
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import scala.Some
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

class WIDLModel(cfg: CFG) extends Model(cfg) {
  val verbose = false

  ////////////////////////////////////////////////////////////////////////////////
  // Model Maps
  ////////////////////////////////////////////////////////////////////////////////
  private var map_fid = Map[FunctionId, String]()
  private var map_semantic = Map[String, SemanticFun]()
  private val map_presemantic = Map[String, SemanticFun]()
  private val map_def = Map[String, AccessFun]()
  private val map_use = Map[String, AccessFun]()

  // WType to Location map
  private var type2locMap = new MHashMap[String, Loc]
  private var functionType2locMap = new MHashMap[String, Loc]
  private var WIDLDateMockupLoc: Loc = 0

  def getType(typ: WType): Option[String] = WIDLTypeMap.getType(typ)

  ////////////////////////////////////////////////////////////////////////////////
  // Initialization List
  ////////////////////////////////////////////////////////////////////////////////
  type InitList =                               ListBuffer[LocPropMap]
  val initList =                                new InitList
  def applyInitList(heap: Heap): Heap = {
    var newHeap = heap
    for(locProps <- initList) {
      val (loc, props) = (locProps._1, locProps._2)
      /* List[(String, PropValue, Option[(Loc, Obj)], Option[FunctionId] */
      val prepareList = props.map(x => prepareForUpdate("WIDL", x._1, x._2))
      for(prepare <- prepareList) {
        val (name, propValue, obj, func) = prepare
        /* added function object to heap if any */
        obj match {
          case Some((loc, obj)) => newHeap = Heap(newHeap.map.updated(loc, obj))
          case None =>
        }
        /* update api function map */
        func match {
          case Some((fid, name)) => map_fid = map_fid + (fid -> name)
          case None => Unit
        }
      }
      /* api object */
      val obj = newHeap.map.get(loc) match {
        case Some(old) => prepareList.foldLeft(old)((o, prepare) => o.update(prepare._1, prepare._2))
        case None => prepareList.foldLeft(ObjEmpty)((o, prepare) => o.update(prepare._1, prepare._2))
      }
      /* added api object to heap */
      newHeap = Heap(newHeap.map.updated(loc, obj))
    }
    initList.clear()
    newHeap
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Making Mockup value for WIDL Type
  ////////////////////////////////////////////////////////////////////////////////
  // WARNING:
  //    Following functions are used both before and while analysis!
  //    DO NOT make new locations while refactoring following functions.
  //    If you do that, that can BREAK analysis
  def WType2Mockup(typ: WType): Value = typ match {
    case SWAnyType(info, suffix) => Value(PValueTop)
    case SWNamedType(info, suffix, name2) => name2 match {
      case "any" => Value(PValueTop)
      case "void" => Value(UndefTop)
      case "boolean" => Value(BoolTop)
      case "byte" | "octet" | "short" | "unsigned short" |
           "long" | "unsigned long" | "long long" | "unsigned long long" |
           "float" | "unrestricted float" | "double" | "unrestricted double" =>
        Value(NumTop)
      case "DOMString" => Value(StrTop)
      case "Date" => Value(WIDLDateMockupLoc)
      case _enum_ if WIDLTypeMap.enumMap.contains(_enum_) => {
        // enum
        val enumNode = WIDLTypeMap.enumMap(_enum_)
        var absStr: AbsString = StrBot
        val i = enumNode.getEnumValueList.iterator()
        while(i.hasNext) absStr+= AbsString.alpha(i.next().getStr)
        Value(absStr)
      }
      case _typedef_ if WIDLTypeMap.typedefMap.contains(_typedef_) => {
        // typedef
        WType2Mockup(WIDLTypeMap.typedefMap(_typedef_).getTyp)
      }
      case _interface_ if WIDLTypeMap.interfaceMap.contains(_interface_) => {
        Value(type2locMap(_interface_))
      }
      case _dictionary_ if WIDLTypeMap.dictionaryMap.contains(_dictionary_) => {
        // dictionary
        Value(type2locMap(_dictionary_))
      }
      case _callback_ if WIDLTypeMap.callbackMap.contains(_callback_) => {
        // callback
        Value(functionType2locMap(_callback_))
      }
      case _ => {
        Value(PValueTop)
      }
    }
    case SWArrayType(info, suffix, type2) => getType(type2) match {
      case Some(type3) => type2locMap.get(type3) match {
        case Some(l) => Value(l)
        case _ => Value(PValueTop)
      }
      case _ => Value(PValueTop)
    }
    case SWSequenceType(info, suffix, type2) => getType(type2) match {
      case Some(type3) => type2locMap.get(type3) match {
        case Some(l) => Value(l)
        case _ => Value(PValueTop)
      }
      case _ => Value(PValueTop)
    }
    case SWUnionType(info, suffix, types) =>
      types.foldLeft(ValueBot)((value, typ) => value + WType2Mockup(typ))
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Helper
  ////////////////////////////////////////////////////////////////////////////////
  def getNewLoc(h: Heap, ctx: Context, cfg: CFG, cp: ControlPoint, alloc: () => Int): (Heap, Context, Loc) = {
    val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
    val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
    if (set_addr.size > 1) throw new IError("API heap allocation: Size of env address is " + set_addr.size)
    val addr_env = (cp._1._1, set_addr.head)
    val addr = cfg.getAPIAddress(addr_env, alloc())
    val l = addrToLoc(addr, Recent)
    val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr)
    (h_1, ctx_1, l)
  }

  def mkDateObj: Obj = {
    ObjEmpty.
      update("@class",      PropValue(AbsString.alpha("Date"))).
      update("@proto",      PropValue(ObjectValue(Value(BuiltinDate.ProtoLoc), F, F, F))).
      update("@extensible", PropValue(T)).
      update("@primitive",  PropValue(Value(NumTop)))
  }

  def mkInterfaceObj(interf: WInterface): Obj = {
    val proto: Loc = interf.getParent.isSome match {
      case true => {
        val parentName = interf.getParent.unwrap().getName
        type2locMap.get(parentName) match {
          case Some(parentLoc) => parentLoc
          case _ => ObjProtoLoc // is it possible?
        }
      }
      case false => ObjProtoLoc
    }
    val initObj: Obj = Helper.NewObject(proto)
    toList(interf.getMembers).foldLeft(initObj)((obj, mem) => mem match {
      case SWConst(_, _, t, n, v) => {
        val constValue = PropValue(ObjectValue(WIDLHelper.WLiteral2Value(v), F, T, F))
        obj.update(n, constValue)
      }
      case attribute@SWAttribute(_, attrs, t, n, _) => {
        val isUnforgeable = WIDLHelper.isUnforgeable(attribute)
        val isWritable = AbsBool.alpha(!WIDLHelper.isReadOnly(mem.asInstanceOf[WMember]))
        val isConfigurable = AbsBool.alpha(!isUnforgeable)
        obj.update(n, PropValue(ObjectValue(WType2Mockup(t), isWritable, T, isConfigurable)))
        /*
        // If the attribute was declared with the [Unforgeable] extended attribute,
        // then the property exists on every object that implements the interface.
        // Otherwise, it exists on the interface’s interface prototype object.
        if(isUnforgeable) {
          // Implements case
          obj.update(n, PropValue(ObjectValue(WType2Mockup(t), isWritable, T, isConfigurable)))
        } else {
          // Assumption: prototype object has a property named n typed t
          obj.update(n, PropValue(ObjectValue(WType2Mockup(t), isWritable, T, isConfigurable)))
        }
        */
      }
      case SWOperation(_, _, _, returnType, n, args, _) => n match {
        case Some(name) => {
          // TODO: consider static function
          obj.update(name, PropValue(ObjectValue(Value(functionType2locMap(interf.getName+"."+name)), T, T, T)))
        }
        case None => obj
      }
      case _ => obj
    })
  }

  def mkDictionaryObj(dic: WDictionary): Obj = {
    val initObj: Obj = Helper.NewObject(ObjProtoLoc)
    toList(dic.getMembers).foldLeft(initObj)((obj, mem) => {
      obj.update(mem.getName, PropValue(ObjectValue(WType2Mockup(mem.getTyp), T, T, T)), AbsentTop)
    })
  }

  def mkArrayObj(typ: WType): Obj = {
    Helper.NewArrayObject(UInt).
      update("@default_number", PropValue(ObjectValue(WType2Mockup(typ), T, T, T)))
  }

  def mkFunctionProps(name: String, argSize: Int): PropMap = {
    val props = new PropMap
    props.put("@class",       AbsConstValue(PropValue(AbsString.alpha("Function"))))
    props.put("@proto",       AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F))))
    props.put("@extensible",  AbsConstValue(PropValue(T)))
    props.put("@scope",       AbsConstValue(PropValue(Value(NullTop))))
    props.put("@function",    AbsInternalFunc(name))
    props.put("@construct",   AbsInternalFunc(name + ".constructor"))
    props.put("@hasinstance", AbsConstValue(PropValue(Value(NullTop))))
    props.put("length",       AbsConstValue(PropValue(ObjectValue(Value(AbsNumber.alpha(argSize)), F, F, F))))
    props
  }

  def mkInterfOpProps(interf: String, op: WOperation): PropMap = op.getName.isSome match {
    case true => mkFunctionProps(interf+"."+op.getName.unwrap(), op.getArgs.size) //TODO: consider static function
    case false => { // unreachable for Samsung API
      new PropMap
    }
  }

  def mkCallbackProps(callback: WCallback): PropMap = {
    mkFunctionProps(callback.getName, callback.getArgs.size) //TODO: consider optional arguments
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Semantic Function
  ////////////////////////////////////////////////////////////////////////////////
  val returnTypeMap =                           new MHashMap[String, WType]
  def successorProvider(): () => Int = {
    var idx: Int = 0
    () => {
      idx = idx + 1
      idx
    }
  }
  def semantic(sem: Semantics, _heap: Heap, _context: Context, heapExc: Heap, contextExc: Context, cp: ControlPoint, cfg: CFG, funcName: String, args: CFGExpr): ((Heap, Context),(Heap, Context)) = {
    var heap: Heap = _heap
    var context: Context = _context
    // Constructor call
    if(funcName.endsWith(".constructor")) {
      var isCorrectArgument = true
      WIDLTypeMap.constructorMap.get(funcName.substring(0, funcName.length - 12)) match {
        case Some(constructorList) =>
          // Collect constructor argument sizes
          val constructorArgSizeSet = new MHashSet[(Int, Int)]
          for(constructor <- constructorList) {
            val args = constructor.getArgs
            val argMinSize = args.size - WIDLHelper.getOptionalParameterCount(args)
            val argMaxSize = args.size
            constructorArgSizeSet.add((argMinSize, argMaxSize))
          }
          // Check the argument size for each argument location
          val argLocSet = SemanticsExpr.V(args, heap, context)._1.locset
          for(argLoc <- argLocSet) {
            if(isCorrectArgument) {
              val absnum = heap(argLoc)("length")._1.objval.value.pvalue.numval
              absnum.getSingle match {
                case Some(n) if AbsNumber.isNum(absnum) =>
                  isCorrectArgument = constructorArgSizeSet.exists(size => n >= size._1 && n <= size._2)
                case _ =>
              }
            }
          }
        case None => isCorrectArgument = false
      }
      if(!isCorrectArgument) {
        // Throw TypeError
        val (newHeapExc, newContextExc) = Helper.RaiseException(heap, context, Set[Exception](TypeError))
        return ((HeapBot, ContextBot), (heapExc + newHeapExc, contextExc + newContextExc))
      }
    } else { // Assume that the function is always form of "[Interface].prototype.[Function]"
      val splitByDot: Array[String] = funcName.split('.')
      val interface: String = splitByDot(0)
      val func: String = splitByDot(splitByDot.size-1)
      val alloc: () => Int = successorProvider()
      /* TODO: overload resolution
      val overloadSet: List[WInterfaceMember] =
        WIDLTypeMap.getMembers(interface).filter(p => p._2._1.equals(func)).map(p => p._2._2)
      */
      val current: Option[(String, WInterfaceMember)] = WIDLTypeMap.getMembers(interface).find(p => p._2._1.equals(func)) match {
        case Some(pair) => Some(pair._2)
        case None => None
      }
      val params: JList[WArgument] = current match { // Now, we get the arguments of the API
        case Some((_, op: WOperation)) => op.getArgs
        case _ => null
      }
      if (params != null) { // We want to invoke callback functions that we get from arguments
        val dummyInfo = IRFactory.makeInfo(IRFactory.dummySpan("Model"))
        var containsCallback = false
        // 1. collect callback arguments & make arguments for each callbacks
        var loc_arg_set: Set[(Loc, Loc)] = Set()
        def aux(warg: JList[WArgument], callbackV: Value) = {
          // 1.1. get the actual callback function that we want to invoke from cfg
          val lset_f = callbackV._2.filter((l) => (T <= Helper.IsCallable(heap, l))) // location set

          // 1.2. make an argument object
          var argObj: Obj = Helper.NewArgObject(AbsNumber.alpha(params.size()))
          for (j <- 0 until warg.size) warg.get(j) match {
            case SWArgument(_, attrs, t, _, _) => {
              val v_j = WType2Mockup(t)
              val pv: PropValue = PropValue(ObjectValue(v_j, T, T, T))
              argObj = argObj.update(j.toString, pv)
            }
            case _ =>
          }
          argObj = argObj.update("callee", PropValue(ObjectValue(Value(lset_f), T, F, T)))
          val nl = getNewLoc(heap, context, cfg, cp, alloc)
          heap = nl._1
          context = nl._2
          heap = heap.update(nl._3, argObj)

          // 1.3. collect the callback functions with the arguments
          lset_f.foreach((l) => loc_arg_set += ((l, nl._3)))
        }
        for (i <- 0 until params.size()) params.get(i) match { // for each [param]eters in the WIDL specification
          case SWArgument(_, attrs, t, _, _) => {
            t match {
              case SWNamedType(_, _, typ) =>
                WIDLTypeMap.callbackMap.get(typ) match {
                  case Some(callback) => { // now we get the parameter which is a callback function
                    containsCallback = true
                    //System.out.format("now... %s\n", typ) // DEBUG
                    val callbackObj = SE.V(CFGLoad(dummyInfo, args, CFGString(i.toString)), heap, context)._1
                    WIDLTypeMap.interfaceMap.get(typ) match {
                      case Some(interf) => // not FunctionOnly
                        val mems: JList[WInterfaceMember] = interf.getMembers
                        callbackObj._2.foreach(l => {
                          try {
                            for (j <- 0 until mems.size()) {
                              val op: WOperation = mems.get(j).asInstanceOf[WOperation]
                              if (!(heap(l)(op.getName.unwrap())._1 </ PropValueBot)) throw new UnknownError()
                            }
                            for (j <- 0 until mems.size()) {
                              val op: WOperation = mems.get(j).asInstanceOf[WOperation]
                              val callbackPropV = heap(l)(op.getName.unwrap())
                              aux(op.getArgs, callbackPropV._1._1._1)
                            }
                          } catch {
                            case _ =>
                          }
                        })
                      case _ => // FunctionOnly
                        aux(callback.getArgs, callbackObj)
                    }
                  }
                  case _ =>
                }
              case _ =>
            }
          }
          case _ =>
        }
        // 2. add call edges to cfg for invoking callback function
        if (containsCallback) {
          //   2.1. set this value
          val l_this = GlobalLoc
          val v_this = Value(LocSet(l_this))
          //val (callee_this, h_temp, ctx_temp, es) = Helper.toObject(current_heap, current_context, v_this, )

          //   2.2. call the functions(1) with the argument object(2)
          val nl_2 = getNewLoc(heap, context, cfg, cp, alloc)
          heap = nl_2._1
          context = nl_2._2
          val l_r = nl_2._3
          val o_old = heap(SinglePureLocalLoc)
          val cc_caller = cp._2
          val n_aftercall = cfg.getAftercallFromCall(cp._1)
          val cp_aftercall = (n_aftercall, cc_caller)
          val n_aftercatch = cfg.getAftercatchFromCall(cp._1)
          val cp_aftercatch = (n_aftercatch, cc_caller)
          loc_arg_set.foreach(pair => {
            val l_f = pair._1
            val l_arg = pair._2
            val o_f = heap(l_f)
            o_f("@function")._1._3.foreach((fid) => {
              cc_caller.NewCallContext(heap, cfg, fid, l_r, v_this._2).foreach((pair) => {
                val (cc_new, o_new) = pair
                val o_new2 = o_new.
                  update(cfg.getArgumentsName(fid),
                  PropValue(ObjectValue(Value(LocSet(l_arg)), T, F, F))).
                  update("@scope", o_f("@scope")._1)
                sem.addCallEdge(cp, ((fid,LEntry), cc_new), ContextEmpty, o_new2)
                sem.addReturnEdge(((fid,LExit), cc_new), cp_aftercall, context, o_old)
                sem.addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, context, o_old)
              })
            })
          })

          // TODO: exceptional heap & context?
          /*val s_1 = (heapExc + h_e, contextExc + ctx_e)
          ((h_5, ctx_4), s_1)*/
        }
      }
    }
    // Function call
    returnTypeMap.get(funcName) match {
      case Some(returnType) =>
        initList.clear()
        return ((Helper.ReturnStore(applyInitList(heap), WType2Mockup(returnType)), context), (heapExc, contextExc))
      case None =>
    }
    ((heap, context), (heapExc, contextExc))
  }

  ////////////////////////////////////////////////////////////////////////////////
  // New Object
  ////////////////////////////////////////////////////////////////////////////////
  type LocPropMap =                             (Loc, PropMap)
  type PropMap =                                MHashMap[String, AbsProperty]
  val globalProps =                             new PropMap
  val newLocPropsMap =                          new MHashMap[String, LocPropMap]
  def newObjectLocProps(locName: String, protoLoc: Loc = ObjProtoLoc): LocPropMap = {
    val loc = newSystemLoc(locName, Recent)
    val props = new PropMap
    props.put("@class", AbsConstValue(PropValue(AbsString.alpha("Object"))))
    props.put("@proto", AbsConstValue(PropValue(ObjectValue(Value(protoLoc), F, F, F))))
    props.put("@extensible", AbsConstValue(PropValue(T)))

    val locProps: LocPropMap = (loc, props)
    initList.append(locProps)
    newLocPropsMap.put(locName, locProps)
    locProps
  }
  def newFunctionLocProps(locName: String, argSize: Int): (LocPropMap, LocPropMap) = {
    val protoLocProps = newObjectLocProps(locName + ".prototype")
    val loc = newSystemLoc(locName, Recent)
    val props = new PropMap
    props.put("@class", AbsConstValue(PropValue(AbsString.alpha("Function"))))
    props.put("@proto", AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F))))
    props.put("@extensible", AbsConstValue(PropValue(T)))
    props.put("@scope", AbsConstValue(PropValueNullTop))
    props.put("@function", AbsInternalFunc(locName))
    props.put("@construct", AbsInternalFunc(locName + ".constructor"))
    props.put("@hasinstance", AbsConstValue(PropValueNullTop))
    props.put("prototype", AbsConstValue(PropValue(ObjectValue(Value(protoLocProps._1), F, F, F))))
    props.put("length", AbsConstValue(PropValue(ObjectValue(Value(AbsNumber.alpha(argSize)), F, F, F))))

    val locProps: LocPropMap = (loc, props)
    initList.append(locProps)
    newLocPropsMap.put(locName, locProps)
    (locProps, protoLocProps)
  }
  def newArrayLocProps(locName: String, defaultNumber: Value): LocPropMap = {
    val loc = newSystemLoc(locName, Recent)
    val props = new PropMap
    props.put("@class", AbsConstValue(PropValue(AbsString.alpha("Array"))))
    props.put("@proto", AbsConstValue(PropValue(ObjectValue(Value(BuiltinArray.ProtoLoc), F, F, F))))
    props.put("@extensible", AbsConstValue(PropValue(T)))
    props.put("@default_number", AbsConstValue(PropValue(ObjectValue(defaultNumber, T, T, T))))
    props.put("length", AbsConstValue(PropValue(ObjectValue(Value(UInt), T, F, F))))

    val locProps: LocPropMap = (loc, props)
    initList.append(locProps)
    newLocPropsMap.put(locName, locProps)
    locProps
  }
  def newDateLocProps(locName: String): LocPropMap = {
    val loc = newSystemLoc(locName, Recent)
    val props = new PropMap
    props.put("@class", AbsConstValue(PropValue(AbsString.alpha("Date"))))
    props.put("@proto", AbsConstValue(PropValue(ObjectValue(Value(BuiltinDate.ProtoLoc), F, F, F))))
    props.put("@extensible", AbsConstValue(PropValue(T)))
    props.put("@primitive", AbsConstValue(PropValueNumTop))

    val locProps: LocPropMap = (loc, props)
    initList.append(locProps)
    newLocPropsMap.put(locName, locProps)
    locProps
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Create Web IDL Interface
  ////////////////////////////////////////////////////////////////////////////////
  val interfaceNameIndexMap =                   new MHashMap[String, Int]
  val createdInterfacesMap =                    new MHashMap[(Loc, String), (LocPropMap, LocPropMap)]
  def getNextInterfaceName(name: String): String = {
    val i = interfaceNameIndexMap.getOrElseUpdate(name, -1) + 1
    if(i >= 0) interfaceNameIndexMap.put(name, i)
    name + i
  }
  def createInterfaceFromName(targetLoc: Loc, interfaceName: String): (LocPropMap, LocPropMap) = {
    // Find interface node
    WIDLTypeMap.interfaceMap.get(interfaceName) match {
      case Some(interfaceNode) => createInterfaceFromNode(targetLoc, interfaceNode)
      case None =>
        if (verbose)
          System.out.println("* \"" + interfaceName + "\" is not an interface.")
        (null, null)
    }
  }
  def createInterfaceFromNode(targetLoc: Loc, interfaceNode: WInterface): (LocPropMap, LocPropMap) = {
    // Interface name
    val interfaceName = interfaceNode.getName
    // If this interface is already created then return it
    createdInterfacesMap.get((targetLoc, interfaceName)) match {
      case Some((locProps, protoLocProps)) => return (locProps, protoLocProps)
      case None =>
    }
    // Get the object for this interface
    val (locProps, protoLocProps) = if(targetLoc == -1) {
      // Create Object or Function Object
      if(!WIDLHelper.isCallback(interfaceNode)) newFunctionLocProps(interfaceName, /* Constructor argument size */ 0)
      else (newObjectLocProps(interfaceName), null)
    }
    else {
      // Use the provided object
      ((targetLoc, new PropMap), null)
    }
    createdInterfacesMap.put((targetLoc, interfaceName), (locProps, protoLocProps))
    initList.append(locProps)
    // Bind to global object
    if(!WIDLHelper.isNoInterfaceObject(interfaceNode)) {
      globalProps.put(interfaceName, AbsConstValue(PropValue(ObjectValue(locProps._1, T, F, T))))
    }
    // Insert semantic function and return type for a constructor
    val constructorName = interfaceName + ".constructor"
    map_semantic+= (constructorName -> semantic)
    returnTypeMap.put(constructorName, WIDLFactory.mkNamedType(interfaceName))
    // Members
    val i = interfaceNode.getMembers.iterator()
    while(i.hasNext) {
      val member = i.next().asInstanceOf[WMember]
      member match {
        // 4.4.5. Constants
        case SWConst(info, attrs, typ, name, value) =>
          val constValue = PropValue(ObjectValue(WIDLHelper.WLiteral2Value(value), F, T, F))
          locProps._2.put(name, AbsConstValue(constValue))
          if(protoLocProps != null) protoLocProps._2.put(name, AbsConstValue(constValue))
          else if (verbose)
            System.out.println("* SWConst (typ = " + typ + ", name1 = " + name + ", value = " + value + ") of \"" + interfaceName + "\" interface is not created.")
        // 4.4.6. Attributes
        case attribute@SWAttribute(info, attrs, typ1, name, exns) =>
          WIDLHelper.WType2Value(typ1, this) match {
            case Some(value) =>
              val isUnforgeable = WIDLHelper.isUnforgeable(attribute)
              val isWritable = AbsBool.alpha(!WIDLHelper.isReadOnly(member))
              val isConfigurable = AbsBool.alpha(!isUnforgeable)
              val absProp: AbsProperty = AbsConstValue(PropValue(ObjectValue(value, isWritable, T, isConfigurable)))
              // If the attribute was declared with the [Unforgeable] extended attribute,
              // then the property exists on every object that implements the interface.
              // Otherwise, it exists on the interface’s interface prototype object.
              if(isUnforgeable) {
                // Implements case
                if(targetLoc != -1) locProps._2.put(name, absProp)
              }
              else {
                // Not implements case
                if(targetLoc != -1) locProps._2.put(name, absProp)
                else protoLocProps._2.put(name, absProp)
              }
            case None =>
              if (verbose)
                System.out.println("* SWAttribute(typ1 = " + typ1 + ", name = " + name + ", exns = " + exns + ") of \"" + interfaceName + "\" interface is not created.")
          }
        // 4.4.7. Operations
        case operation@SWOperation(info, attrs, qualifiers, returnType, name, args, exns) =>
          name match {
            case Some(name) =>
              // Select target object
              val isCallback = WIDLHelper.isCallback(interfaceNode)
              val isStatic = WIDLHelper.isStatic(operation)
              val (locPropsSel, funcName) = (isCallback | isStatic) match {
                case true => (locProps, interfaceName + '.' + name)
                case false => (protoLocProps, interfaceName + ".prototype." + name)
              }
              // Argument size
              val argMinSize = args.length - WIDLHelper.getOptionalParameterCount(args)
              val argMaxSize = args.length
              WIDLTypeMap.argSizeMap.put(funcName, (argMinSize, argMaxSize))
              val containsCallback = args.exists(param => param match {
                case SWArgument(_, _, t, _, _) => {
                  t match {
                    case SWNamedType(_, _, typ) => WIDLTypeMap.callbackMap.get(typ) match {
                      case Some(callback) => true
                      case _ => false
                    }
                    case _ => false
                  }
                }
                case _ => false
              })
              if (containsCallback) // if the interface has callback argument
                locPropsSel._2.put(name, AbsBuiltinFuncAftercallOptional(funcName, args.length)) // T, T, T ?
              else
                locPropsSel._2.put(name, AbsBuiltinFunc(funcName, args.length)) // T, T, T ?
              // Insert semantic function and return type
              map_semantic+= (funcName -> semantic)
              returnTypeMap.put(funcName, returnType)
            case None =>
              if (verbose)
                System.out.println("* SWOperation (qualifiers + " + qualifiers + ", returnType = " + returnType + ", name = " + name + ", args = " + args + ", exns = " + exns + ") of \"" + interfaceName + "\" interface is not created.")
          }
      }
    }
    // If this interface inherits another interface
    if(interfaceNode.getParent.isSome) {
      val parentName = interfaceNode.getParent.unwrap().getName
      if(!newLocPropsMap.contains(parentName)) createInterfaceFromName(-1, interfaceNode.getParent.unwrap().getName)
      getRegisteredRecentLoc(parentName + ".prototype") match {
        case Some(parentProtoLoc) => locProps._2.put("@proto", AbsConstValue(PropValue(ObjectValue(Value(parentProtoLoc), F, F, F))))
        case None =>
          if (verbose)
            System.out.println("* \"" + parentName + ".prototype\" does not exist.")
      }
    }
    // If this interface implements another interface
    WIDLTypeMap.implementsMap.get(interfaceName) match {
      case Some(implementsNodeList) => for(implementsNode <- implementsNodeList) doImplements(protoLocProps._1, implementsNode)
      case none =>
    }
    // Return the created object(interface)
    (locProps, protoLocProps)
  }
  // 4.5. Implements statements
  def doImplements(targetLoc: Loc, implementsNode: WImplementsStatement): Unit = {
    WIDLTypeMap.interfaceMap.get(implementsNode.getParent) match {
      case Some(interfaceNode) => createInterfaceFromNode(targetLoc, interfaceNode)
      case None =>
        if (verbose)
          System.out.println("* \"" + implementsNode.getParent + "\" is not an interface.")
    }
  }

  /**
   * Note
   *   - "interface" and "implements" don't have cycles.
   *   - We cannot represent loc-top! ("any" type and "object" type has problem... ~_~)
   *   - If "Window" implements some interface then the interface.prototype's properties are copied
   *     to "Window" not to "Window.prototype". (Temporary wrong implementation)
   */
  def initialize(h: Heap): Heap = {
    // 1. initialize & set WType -> Loc maps
    // 1.1. initialize
    type2locMap = MHashMap[String, Loc]()
    functionType2locMap = MHashMap[String, Loc]()
    // 1.2. map type to predefined location
    WIDLDateMockupLoc = newSystemLoc("WIDLDateMockupLoc", Old)
    WIDLTypeMap.interfaceMap.foreach(kv => type2locMap.put(kv._1, newSystemLoc("WIDL"+kv._1+"MockupLoc", Old)))
    WIDLTypeMap.dictionaryMap.foreach(kv => type2locMap.put(kv._1, newSystemLoc("WIDL"+kv._1+"MockupLoc", Old)))
    WIDLTypeMap.callbackMap.foreach(kv => functionType2locMap.put(kv._1, newSystemLoc("WIDL"+kv._1+"MockupLoc", Old)))
    WIDLTypeMap.interfOperationMap.foreach(kv => functionType2locMap.put(kv._1, newSystemLoc("WIDL"+kv._1+"MockupLoc", Old)))
    WIDLTypeMap.arrayMap.foreach(kv => type2locMap.put(kv._1, newSystemLoc("WIDL"+kv._1+"MockupLoc", Old)))

    // 2. update object mockups (date, interface, dictionary, array)
    // 2.1. update a date mockup
    val h_0 = h.update(WIDLDateMockupLoc, mkDateObj)
    // 2.2. update interface mockups
    val h_1 = WIDLTypeMap.interfaceMap.foldLeft(h_0)((_h, kv) => {
      val name = kv._1
      val typ = kv._2
      _h.update(type2locMap(name), mkInterfaceObj(typ))
    })
    // 2.3. update dictionary mockups
    val h_2 = WIDLTypeMap.dictionaryMap.foldLeft(h_1)((_h, kv) => {
      val name = kv._1
      val typ = kv._2
      _h.update(type2locMap(name), mkDictionaryObj(typ))
    })
    val h_3 = WIDLTypeMap.arrayMap.foldLeft(h_2)((_h, kv) => {
      val name = kv._1
      val typ = kv._2
      _h.update(type2locMap(name), mkArrayObj(typ))
    })

    // 3. update function mockups
    // 3.1. update callback function mockups
    WIDLTypeMap.callbackMap.foreach(kv => {
      val name: String = kv._1
      val callback: WCallback = kv._2
      val loc: Loc = functionType2locMap(name)
      val locprop: LocPropMap = (loc, mkCallbackProps(callback))
      initList.append(locprop)
    })
    // 3.2. update interface operation mockups
    WIDLTypeMap.interfOperationMap.foreach(kv => {
      val name: String = kv._1
      val interf: String = name.split('.')(0)
      val op: WOperation = kv._2
      val loc: Loc = functionType2locMap(name)
      val locprop: LocPropMap = (loc, mkInterfOpProps(interf, op))

      op match {
        case operation@SWOperation(_, _, _, returnType, opname, args, _) =>
          opname match {
            case Some(opname) =>
              // Argument size
              val argMinSize = args.length - WIDLHelper.getOptionalParameterCount(args)
              val argMaxSize = args.length
              WIDLTypeMap.argSizeMap.put(name, (argMinSize, argMaxSize))
              val containsCallback = args.exists(param => param match {
                case SWArgument(_, _, t, _, _) => {
                  t match {
                    case SWNamedType(_, _, typ) => WIDLTypeMap.callbackMap.get(typ) match {
                      case Some(callback) => true
                      case _ => false
                    }
                    case _ => false
                  }
                }
                case _ => false
              })
              if (containsCallback) // if the interface has callback argument
                locprop._2.put("@function", AbsInternalFuncAftercallOptional(name)) // T, T, T ?
              else
                locprop._2.put("@function", AbsInternalFunc(name)) // T, T, T ?
              // Insert semantic function and return type
              map_semantic+= (name -> semantic)
              returnTypeMap.put(name, returnType)
            case None =>
          }
      }
      initList.append(locprop)
    })

    // 4. create interfaces except for no interface objects
    // Top-down from "Window" object
    WIDLTypeMap.implementsMap.get("Window") match {
      case Some(implementsNodeList) => for(implementsNode <- implementsNodeList) doImplements(GlobalLoc, implementsNode)
      case None =>
    }

    // Bind interfaces to "Window" object directly
    for((interfaceName, interfaceNode) <- WIDLTypeMap.interfaceMap) {
      if(!WIDLHelper.isNoInterfaceObject(interfaceNode)) createInterfaceFromNode(-1, interfaceNode)
    }

    if(globalProps.size > 0) initList.append((GlobalLoc, globalProps))

    ////////////////////////////////////////////////////////////////////////////////
    // Initialize Heap
    ////////////////////////////////////////////////////////////////////////////////
    applyInitList(h_3)
  }
  def getTypeFromLoc(loc: Loc): Option[String] = (type2locMap.find(kv => kv._2 == loc) match {
    case Some(kv) => Some(kv._1)
    case _ => None
  }) match {
    case Some(str) => Some(str)
    case _ => newLocPropsMap.find(kv => kv._2._1 == loc) match {
      case Some(kv) =>
        // Assumption: there's no type name which ends with decimal digits
        var cntN = 0
        while ('0' <= kv._1.charAt(kv._1.size - cntN - 1) && kv._1.charAt(kv._1.size - cntN - 1) <= '9') {
          cntN = cntN + 1
        }
        Some(kv._1.dropRight(cntN))
      case _ => None
    }
  }
  def addAsyncCall(cfg: CFG, loop_head: CNode): (List[CNode],List[CNode]) = (List(), List())
  def isModelFid(fid: FunctionId) = map_fid.contains(fid)
  def getFIdMap(): Map[FunctionId, String] = map_fid
  def getSemanticMap(): Map[String, SemanticFun] = map_semantic
  def getPreSemanticMap(): Map[String, SemanticFun] = map_presemantic
  def getDefMap(): Map[String, AccessFun] = map_def
  def getUseMap(): Map[String, AccessFun] = map_use
  def asyncSemantic(sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG,
                    name: String, list_addr: List[Address]): ((Heap, Context), (Heap, Context)) = {
    ((HeapBot, ContextBot),(HeapBot, ContextBot))
  }
  def asyncPreSemantic(sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG,
                       name: String, list_addr: List[Address]): (Heap, Context) = {
    (HeapBot, ContextBot)
  }
  def asyncDef(h: Heap, ctx: Context, cfg: CFG, name: String, list_addr: List[Address]): LPSet = LPBot
  def asyncUse(h: Heap, ctx: Context, cfg: CFG, name: String, list_addr: List[Address]): LPSet = LPBot
  def asyncCallgraph(h: Heap, inst: CFGInst, map: Map[CFGInst, Set[FunctionId]],
                     name: String, list_addr: List[Address]): Map[CFGInst, Set[FunctionId]] = Map()
}
