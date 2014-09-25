/*******************************************************************************
    Copyright (c) 2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.ts

import scala.collection.mutable.{HashMap => MHashMap, HashSet => MHashSet, ListBuffer}
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.cfg.{Node => CNode}
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.{SemanticsExpr => SE}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml.HTMLElement
import kr.ac.kaist.jsaf.analysis.typing.models.builtin.{BuiltinArray, BuiltinDate}
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T, _}
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.bug_detector.TSChecker

class TSModel(cfg: CFG) extends Model(cfg) {
  val verbose = false

  ////////////////////////////////////////////////////////////////////////////////
  // Model Maps
  ////////////////////////////////////////////////////////////////////////////////
  private var map_fid = Map[FunctionId, String]()
  private var map_semantic = Map[String, SemanticFun]()
  private val map_presemantic = Map[String, SemanticFun]()
  private val map_def = Map[String, AccessFun]()
  private val map_use = Map[String, AccessFun]()

  // TSType to Location map
  private var type2locMap = new MHashMap[String, Loc]
  private var functionType2locMap = new MHashMap[String, Loc]
  private var arrayType2locMap = new MHashMap[String, Loc]
  private var objectType2locMap = new MHashMap[String, Loc]
  private var moduleType2locMap = new MHashMap[String, Loc]

  ////////////////////////////////////////////////////////////////////////////////
  // Initialization List
  ////////////////////////////////////////////////////////////////////////////////
  type InitList = ListBuffer[LocPropMap]
  type PropMap = MHashMap[String, AbsProperty]
  type LocPropMap = (Loc, PropMap)

  val globalProps = new PropMap
  val initList = new InitList

  def applyInitList(heap: Heap): Heap = {
    var newHeap = heap
    for(locProps <- initList) {
      val (loc, props) = (locProps._1, locProps._2)
      // List[(String, PropValue, Option[(Loc, Obj)], Option[FunctionId]
      val prepareList = props.map(x => prepareForUpdate("TS", x._1, x._2))
      for(prepare <- prepareList) {
        val (name, propValue, obj, func) = prepare
        // added function object to heap if any
        obj match {
          case Some((loc, obj)) => newHeap = Heap(newHeap.map.updated(loc, obj))
          case None =>
        }
        // update api function map
        func match {
          case Some((fid, name)) => map_fid = map_fid + (fid -> name)
          case None => Unit
        }
      }
      // api object
      val obj = newHeap.map.get(loc) match {
        case Some(old) => prepareList.foldLeft(old)((o, prepare) => o.update(prepare._1, prepare._2))
        case None => prepareList.foldLeft(Obj.empty)((o, prepare) => o.update(prepare._1, prepare._2))
      }
      // added api object to heap
      newHeap = Heap(newHeap.map.updated(loc, obj))
    }
    initList.clear()
    newHeap
  }

  def findRefLoc(path: String, name: String): Loc = {
    var path_arr: Array[String] = if(path==""){Array[String]()}else{path.split('.')}
    var opt:Option[Loc] = None

    opt = type2locMap.get((path_arr:+name).mkString("."))
    while (path_arr.length>0 && opt == None){
      path_arr = path_arr.dropRight(1)
      opt = type2locMap.get((path_arr:+name).mkString("."))
    }

    opt match{
      case Some(loc) => loc
      case None => 0
    }
  }

  def weakProtoUpdate(obj:Obj, propv: PropValue): Obj = {
    obj + obj.update(Prop_proto, propv)
  }

  def TSType2Mockup(path:String, name:String, typ: Type): Value = typ match {
    case SAnyT(info) => Value(PValueTop)
    case SNumberT(info) => Value(NumTop)
    case SStringT(info) => Value(StrTop)
    case SBoolT(info) => Value(BoolTop)
    case SVoidT(info) => Value(UndefTop)
    case SFunctionType(info, tparams, params, typ) => {
      if (path == ""){
        Value(functionType2locMap(name))
      }else{
        Value(functionType2locMap(path+"."+name))
      }
    }
    case SCallSig(info, tparams, params, typ) => {
      if (path == ""){
        Value(functionType2locMap(name))
      }else{
        Value(functionType2locMap(path+"."+name))
      }
    }
    case SArrayType(info, typ) => {
      if (path == ""){
        Value(arrayType2locMap(name))
      }else{
        Value(arrayType2locMap(path+"."+name))
      }
    }
    case SObjectType(info, mems) => {
      if (path == ""){
        Value(objectType2locMap(name))
      }else{
        Value(objectType2locMap(path+"."+name))
      }
    }
    case STypeRef(info, name, args) =>
      Value(findRefLoc(path, name.getText))
    case _ => Value(PValueTop)
  }

  def mkIntfObj(path:String,  intf:IntfDecl): Obj = {
    var initObj: Obj = Helper.NewObject(ObjProtoLoc)
    TSTypeMap.callMap.get(path) match {
      case Some(sig) => {
        map_semantic += (path -> semantic)
        initObj = mkFunctionObject(path)
      }
      case None =>
    }
    TSTypeMap.idxMap.get(path) match {
      case Some(sig) => sig match {
        case SIndexSig(info, id, annot, num) =>{
          initObj = Helper.NewArrayObject(NumTop)
            .update(Str_default_number, PropValue(ObjectValue(TSType2Mockup(path, "", annot), T, T, T)))
        }
      }
      case None =>
    }
    val obj_0: Obj  = toList(intf.getTyp.getMembers).foldLeft(initObj)((obj, mem) => mem match {
      case SPropertySig (info, prop, opt, typ) => {
        val name: String = prop match { 
          case SPropId(info,id) => id.getText
        }
        typ match {
          case Some(t) =>
            obj.update(name, PropValue(ObjectValue(TSType2Mockup(path, name, t), F, F, F)))
          case None => obj
        }
      }
      case SMethodSig (info, prop, opt, sig) => {
        val name: String = prop match {
          case SPropId(info,id) => id.getText
        }
        obj.update(name, PropValue(ObjectValue(Value(functionType2locMap(path+"."+name)), F, F, F)))
      }
      case _ => obj
    })
    val obj_1: Obj = toList(intf.getExt).foldLeft(obj_0)((obj, typ) => typ match {
      case typRef@STypeRef(info, name, args) => {
        val temp = weakProtoUpdate(obj, PropValue(ObjectValue(TSType2Mockup("","",typRef), F, F, F)))
        temp
      }
    })
    obj_1
  }

  def mkArrayObj(path:String, arr:ArrayType): Obj = {
    val obj: Obj = Helper.NewArrayObject(NumTop)
    arr match{
      case SArrayType(info, typ) => {
        obj.update(Str_default_number, PropValue(ObjectValue(TSType2Mockup(path, "", typ), T, T, T)))
      }
    }
  }

  def mkObj(path:String, node:ObjectType, h:Heap, l:Loc): Heap = {
    var obj: Obj = Obj.bottom
    toList(node.getMembers).foreach (mem => {
      mem match{
        case mem@SCallSig(info, tparams, params, typ) => {
          map_semantic += (path -> semantic)
          typ match {
            case Some(returnType) =>
              returnTypeMap.put(path, returnType)
            case None =>
          }
          obj = mkFunctionObject(path)
        }
        case mem@SIndexSig(info, id, annot, num) => {
          obj = Helper.NewArrayObject(NumTop)
            .update(Str_default_number, PropValue(ObjectValue(TSType2Mockup(path, "", annot), T, T, T)))
        }
        case _ =>
      }
    })
    if (obj == Obj.bottom){
      obj = Helper.NewObject(ObjProtoLoc)
    }
    val (newObj, h_0): (Obj, Heap) = toList(node.getMembers).foldLeft((obj, h))((_kv, mem) => {
      val _obj:Obj = _kv._1
      val _h:Heap = _kv._2
      mem match {
        case mem@SPropertySig (info, prop, opt, typ) => {
          val name: String = prop match {
            case SPropId(info,id) => id.getText
          }
          typ match {
            case Some(t) => t match {
              case t@SFunctionType(info, tparams, params, typ) => {
                val loc = newSystemLoc("TS"+path+"."+name+"MockupLoc", Old)
                val argSize = params.size
                map_semantic += (path+"."+name -> semantic)
                returnTypeMap.put(path+"."+name, typ)
                (_obj.update(name, PropValue(ObjectValue(Value(loc), F, F, F))),
                _h.update(loc, mkFunctionObject(path+"."+name)))
              }
              case t@SArrayType(info, typ) => {
                val loc = newSystemLoc("TS"+path+"."+name+"MockupLoc", Old)
                (_obj.update(name, PropValue(ObjectValue(Value(loc), F, F, F))),
                _h.update(loc, mkArrayObj(path+"."+name,t)))
              }
              case t@SObjectType(getInfo, getMembers) => {
                val loc = newSystemLoc("TS"+path+"."+name+"MockupLoc", Old)
                (_obj.update(name, PropValue(ObjectValue(Value(loc), F, F, F))),
                mkObj(path+"."+name,t,_h,loc))
              }
              case _ =>{
                (_obj.update(name, PropValue(ObjectValue(TSType2Mockup(path, name, t), F, F, F))), _h)
              }
            }
            case None => (_obj, _h)
          }
        }
        case mem@SMethodSig(info, prop, opt, sig) => {
          val name: String = prop match {
            case SPropId(info,id) => id.getText
          }
          sig match {
            case SCallSig (info, tparams, params, typ) => {
              val loc = newSystemLoc("TS"+path+"."+name+"MockupLoc", Old)
              val argSize = params.size
              map_semantic += (path+"."+name -> semantic)
              typ match {
                case Some(returnType) => returnTypeMap.put(path+"."+name, returnType)
                case None =>
              }
              (_obj.update(name, PropValue(ObjectValue(Value(loc), F, F, F))),
                _h.update(loc, mkFunctionObject(path+"."+name)))
            }
          }
        }
        case mem@SConstructSig (info, tparams, params, typ) => {
          val constructorName = path+".constructor"
          val loc = newSystemLoc("TS"+constructorName+"MockupLoc", Old)
          val argSize = params.size
          map_semantic+= (constructorName -> semantic)
          typ match{
            case Some(t) => returnTypeMap.put(constructorName, t)
            case None =>
          }
          (_obj.update("@construct", PropValue(ObjectValue(Value(loc), F, F, F))),
            _h.update(loc, mkFunctionObject(constructorName)))
        }
        case _ => (_obj, _h)
      }
    })
    val h_1 = h_0.update(l, newObj)
    h_1
  }

  def globalAdd (h: Heap, name: String, value: Value): Heap = {
    val arr: Array[String] = name.split('.')
    var h_0: Heap = h
    var total:String = ""
    var final_loc:Loc = arr.dropRight(1).reverse.foldLeft(GlobalLoc)((_l,mod) => {
      total += mod
      val loc: Loc = moduleType2locMap.get(total) match {
        case Some(loc) => loc
        case None => {
          val obj = h_0.map(_l)
          val loc = newSystemLoc("TS"+total+"MockupLoc", Old)
          h_0 = h_0.update(loc,Helper.NewObject(ObjProtoLoc))
          h_0 = Heap(h_0.map.updated(_l,obj.update(mod,PropValue(ObjectValue(Value(loc), F, F, F)))))
          moduleType2locMap.put(total,loc)
          loc
        }
      }
      total += "."
      loc
    })
    val obj = h_0.map(final_loc)
    var h_1 = Heap(h_0.map.updated(final_loc,obj.update(arr.reverse(0), PropValue(ObjectValue(value, F, F, F)))))
    h_1
  }

  def initialize(h: Heap): Heap = {
    // 1. initialize & create TSType -> Loc maps
    // 1.1. initialize
    type2locMap = MHashMap[String, Loc]()
    functionType2locMap = MHashMap[String, Loc]()
    arrayType2locMap = MHashMap[String, Loc]()
    objectType2locMap = MHashMap[String, Loc]()
    moduleType2locMap = MHashMap[String, Loc]()

    // 1.2. map type to predefined location
    TSTypeMap.intfMap.foreach(kv => type2locMap.put(kv._1, newSystemLoc("TS"+kv._1+"MockupLoc", Old)))
    TSTypeMap.clsMap.foreach(kv => type2locMap.put(kv._1, newSystemLoc("TS"+kv._1+"MockupLoc", Old)))
    TSTypeMap.enumMap.foreach(kv => type2locMap.put(kv._1, newSystemLoc("TS"+kv._1+"MockupLoc", Old)))
    TSTypeMap.intfFunMap.foreach(kv => functionType2locMap.put(kv._1, newSystemLoc("TS"+kv._1+"MockupLoc", Old)))
    TSTypeMap.globalFunMap.foreach(kv => functionType2locMap.put(kv._1, newSystemLoc("TS"+kv._1+"MockupLoc", Old)))
    TSTypeMap.arrayMap.foreach(kv => arrayType2locMap.put(kv._1, newSystemLoc("TS"+kv._1+"MockupLoc", Old)))
    TSTypeMap.objMap.foreach(kv => objectType2locMap.put(kv._1, newSystemLoc("TS"+kv._1+"MockupLoc", Old)))

    // 2. update type mockups (interface, class)
    // 2.1. update interface mockups
    val h_0 = TSTypeMap.intfMap.foldLeft(h)((_h, kv) => {
      val name = kv._1
      val typ = kv._2
      _h.update(type2locMap(name), mkIntfObj(name, typ))
    })

    // TODO 2.2. update class mockups

    // 3. update function mockups
    // 3.1. update interface member function mockups
    TSTypeMap.intfFunMap.foreach(kv => {
      val name: String = kv._1
      val loc: Loc = functionType2locMap(name)
      val locprop: LocPropMap = (loc, mkFunctionProps(name))
      initList.append(locprop)

      // add map semantic
      map_semantic += (name -> semantic)
    })

    // 3.2. update global function mockups
    val h_1 = TSTypeMap.globalFunMap.foldLeft(h_0)((_h, kv) => {
      val name: String = kv._1
      val h:Heap = globalAdd(_h, name, Value(functionType2locMap(name)))

      val loc: Loc = functionType2locMap(name)
      val locprop: LocPropMap = (loc, mkFunctionProps(name))
      initList.append(locprop)

      // add map semantic
      map_semantic += (name -> semantic)
      h
    })

    // 4. update array mockups
    val h_2 = TSTypeMap.arrayMap.foldLeft(h_1)((_h, kv) => {
      val name: String = kv._1
      val arr: ArrayType = kv._2
      globalAdd(_h, name, TSType2Mockup("", name, arr))
        .update(arrayType2locMap(name), mkArrayObj(name, arr))
      })

    // 5. update object mockups
    val h_3 = TSTypeMap.objMap.foldLeft(h_2)((_h, kv) => {
      val name: String = kv._1
      val obj: ObjectType = kv._2
      mkObj(name, obj, globalAdd(_h, name, TSType2Mockup("", name, obj)), objectType2locMap(name))
    })

    // 6. Bind global variables to "Window" object directly
    val h_4 = TSTypeMap.vardb.foldLeft(h_3)((_h, kv) => {
      val name: String = kv._1
      val node: Type = kv._2
      globalAdd(_h, name, TSType2Mockup("", name, node))
    })

    if(globalProps.size > 0) initList.append((GlobalLoc, globalProps))

    applyInitList(h_4)
  }

  def mkFunctionObject(name: String): Obj = {
    val fid = makeAPICFG("TS", name)
    val obj = Obj.empty.
      update("@class",        PropValue(AbsString.alpha("Function"))).
      update("@proto",        PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F))).
      update("@extensible",   PropValue(T)).
      update("@scope",        PropValue(Value(NullTop))).
      update("@function",     PropValue(ObjectValueBot, FunSet(fid))).
      update("@hasinstance",  PropValue(Value(NullTop)))
      map_fid = map_fid + (fid -> name)
    obj
  }

  def mkFunctionProps(name: String): PropMap = {
    val props = new PropMap
    props.put("@class",       AbsConstValue(PropValue(AbsString.alpha("Function"))))
    props.put("@proto",       AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F))))
    props.put("@extensible",  AbsConstValue(PropValue(T)))
    props.put("@scope",       AbsConstValue(PropValue(Value(NullTop))))
    props.put("@function",    AbsInternalFunc(name))
    props.put("@construct",   AbsInternalFunc(name + ".constructor"))
    props.put("@hasinstance", AbsConstValue(PropValue(Value(NullTop))))
    props
  }

  def TSType2Value(typ: Type, h: Heap, ctx: Context, alloc: () => Int): (Value, Heap, Context) = typ match {
    case SAnyT(info) => (Value(PValueTop), h, ctx)
    case SNumberT(info) => (Value(NumTop), h, ctx)
    case SStringT(info) => (Value(StrTop), h, ctx)
    case SBoolT(info) => (Value(BoolTop), h, ctx)
    case SVoidT(info) => (Value(UndefTop), h, ctx)
    case SArrayType(info, typ) => {
      val (v_1, h_1, ctx_1) = TSType2Value(typ, h, ctx, alloc)
      val obj: Obj = Helper.NewArrayObject(NumTop).
        update(Str_default_number, PropValue(ObjectValue(v_1, T, T, T)))
      val l = newRecentLoc()
      val h_2 = h.update(l, obj)
      (Value(l), h_2, ctx_1)
    }
    case STypeRef(info, name, args) =>{
      name.getText match {
        case "HTMLElement" => {
          var obj = Helper.NewObject(HTMLElement.loc_proto)
          obj = HTMLElement.default_getInsList().foldLeft(obj)((_obj, kv) => _obj.update(kv._1,kv._2))
          val l = newRecentLoc()
          val h_1 = h.update(l, obj)
          return (Value(l), h_1, ctx)
        }
        case _ =>
      }
      (Value(findRefLoc("", name.getText)), h, ctx)
    }
    case _ => (Value(PValueTop), h, ctx)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Semantic Function
  ////////////////////////////////////////////////////////////////////////////////
  val returnTypeMap = new MHashMap[String, Type]
  def successorProvider(): () => Int = {
    var idx: Int = 0
    () => {
      idx = idx + 1
      idx
    }
  }
  def properArgSize (argSize: (Int, Int), num: Int): Option[String] = {
    if (argSize._2 == -1 && argSize._1 > num) {
      Some("equal or more than "+argSize._1)
    } else if (argSize._2 != -1 && (num < argSize._1 || num > argSize._2)) {
      if (argSize._1 == argSize._2)
        Some(argSize._1.toString)
      else
        Some(argSize._1+" to "+argSize._2)
    } else {
      None
    }
  }
  def isAbsent(value: Value): Boolean = {
    ((value.pvalue.undefval.isBottom)
    && (value.pvalue.nullval.isBottom)
    && (value.pvalue.boolval.isBottom)
    && (value.pvalue.strval.isBottom)
    && (value.pvalue.numval.isBottom)
    && (value.locset.size==0))
  }
  def checkType(given: Value, expected: Type, heap: Heap): Boolean = {
    val undefval = given.pvalue.undefval
    val nullval = given.pvalue.nullval
    val boolval = given.pvalue.boolval
    val strval = given.pvalue.strval
    val numval = given.pvalue.numval
    val locset = given.locset

    expected match {
      case SAnyT(info) => true
      case SNumberT(info) => !numval.isBottom
      case SStringT(info) => !strval.isBottom
      case SBoolT(info) => !boolval.isBottom
      case SVoidT(info) => !undefval.isBottom
      case SFunctionType(info, tparams, params, typ) => {
        for (funLoc <- locset) {
          for (fid <- heap(funLoc)("@function").funid) {
            val plen = params.length
            val alen = heap(funLoc)("length").objval.value.pvalue.numval.getSingle match {
              case Some(v) => v
              case None => -1
            }
            if (plen == alen){
              return true
            }
          }
        }
        false
      }
      case SArrayType(info, typ) => {
        for (arrLoc <- locset) {
          Helper.IsArray(heap, arrLoc).getSingle match {
            case Some(b) => {
              if (b) {
                val arr = heap(arrLoc)
                val length = arr("length").objval.value.pvalue.numval.getSingle match {
                  case Some(v) => v.toInt
                  case None => -1
                }
                for (i <- 0 until length) {
                  if(!checkType(arr(i.toString).objval.value, typ, heap)) {
                    return false
                  }
                }
                return true
              }
            }
            case None =>
          }
        }
        false
      }
      case SObjectType(info, mems) => {
        for (objLoc <- locset) {
          Helper.IsObject(heap, objLoc).getSingle match {
            case Some(b) => {
              if (b) {
                val obj = heap(objLoc)
                for (mem <- mems) mem match {
                  case mem@SPropertySig (info, prop, opt, typ@Some(t)) => {
                    val name: String = prop match {
                      case SPropId(info,id) => id.getText
                    }
                    if(isAbsent(obj(name).objval.value)) {
                      if(!opt) {
                        return false
                      }
                    } else {
                      if(!checkType(obj(name).objval.value, t, heap)) {
                        return false
                      }
                    }
                  }
                  case mem@SMethodSig(info, prop, opt, sig) => {
                    val name: String = prop match {
                      case SPropId(info,id) => id.getText
                    }
                    if(isAbsent(obj(name).objval.value)){
                      if(!opt) {
                        return false
                      }
                    } else {
                      if(!checkType(obj(name).objval.value, sig, heap)) {
                        return false
                      }
                    }
                  }
                  case mem@SConstructSig (info, tparams, params, typ) => {
                  }
                  case _ =>
                }
                return true
              }
            }
            case None =>
          }
        }
        false
      }
      case SCallSig(info, tparams, params, typ) => {
        for (funLoc <- locset) {
          for (fid <- heap(funLoc)("@function").funid) {
            val plen = params.length
            val alen = heap(funLoc)("length").objval.value.pvalue.numval.getSingle match {
              case Some(v) => v
              case None => -1
            }
            if (plen == alen){
              return true
            }
          }
        }
        false
      }
      case typ@STypeRef(info, name, args) => {
        for (objLoc <- locset) {
          Helper.IsObject(heap, objLoc).getSingle match {
            case Some(b) => {
              if (b) {
                if (checkBuiltIn(name.getText)){
                  return true
                }
                TSTypeMap.intfMap(name.getText) match {
                  case node@SIntfDecl(_, _, _, _, ty@SObjectType(_, mems)) => {
                    val obj = heap(objLoc)
                    for (mem <- mems) mem match {
                      case mem@SPropertySig (info, prop, opt, typ@Some(t)) => {
                        val name: String = prop match {
                          case SPropId(info,id) => id.getText
                        }
                        if(isAbsent(obj(name).objval.value)) {
                          if(!opt) {
                            return false
                          }
                        } else {
                          if(!checkType(obj(name).objval.value, t, heap)) {
                            return false
                          }
                        }
                      }
                      case mem@SMethodSig(info, prop, opt, sig) => {
                        val name: String = prop match {
                          case SPropId(info,id) => id.getText
                        }
                        if(isAbsent(obj(name).objval.value)) {
                          if(!opt) {
                            return false
                          }
                        } else {
                          if(!checkType(obj(name).objval.value, sig, heap)) {
                            return false
                          }
                        }
                      }
                      case _ =>
                    }
                    return true
                  }
                }
              }
            }
            case None =>
          }
        }
        false
      }
      case _ => false
    }
  }
  def checkBuiltIn(name: String): Boolean = {
    name match {
      case "Function" => true
      case "HTMLElement" => true
      case "Object" => true
      case "Element" => true
      case "Text" => true
      case "Node" => true
      case "T" => true
      case "XMLDocument" => true
      case _ => false
    }
  }

  def semantic(sem: Semantics, _heap: Heap, _context: Context, heapExc: Heap, contextExc: Context, cp: ControlPoint, cfg: CFG, funcName: String, _args: CFGExpr): ((Heap, Context),(Heap, Context)) = {
    var heap: Heap = _heap
    var context: Context = _context
    var errors: Boolean = false
    var warnings: Boolean = false
    var returnType: Option[Type] = None

    val argLocSet = SE.V(_args, heap, context)._1.locset
    var argsObj: Obj = Helper.NewObject(ObjProtoLoc)
    for(argLoc <- argLocSet) {
      argsObj = heap(argLoc)
    }
    val numOfArg:Int = AbsNumber.getUIntSingle(argsObj("length").objval.value.pvalue.numval) match {
      case Some(n) => n.toInt
      case None => -1
    }
    val args:List[Value] = Range(0,numOfArg).foldLeft(List[Value]())((_args, k) => {
      val obj = argsObj(k.toString).objval.value
      _args:+obj
    })

    var funList: Option[List[Type]] = None
    TSTypeMap.globalFunMap.get(funcName) match {
      case lst@Some(_) => funList = lst
      case None =>
    }
    TSTypeMap.intfFunMap.get(funcName) match {
      case lst@Some(_) => funList = lst
      case None =>
    }
    TSTypeMap.callMap.get(funcName) match {
      case lst@Some(_) => funList = lst
      case None =>
    }

    errors = true
    funList match {
      case Some(lst) => {
        lst.foreach(fun => {
          if (errors) {
            errors = false
            var (info, tparams, params) = fun match{
              case SFunctionType(info, tparams, params, typ) => {
                returnType = Some(typ)
                (info, tparams, params)
              }
              case SCallSig(info, tparams, params, typ) => {
                returnType = typ
                (info, tparams, params)
              }
            }

            var numOfParam: Int = params.length
            var numOfOptional: Int = 0
            var hasRest: Boolean = false

            var argSize: (Int, Int) = (-1, -1)

              for (i <- 0 until params.size) params.get(i) match {
              case SParam(_, _, _, typ, _, opt, rest) => {
                if (rest) {
                  hasRest = true
                  if (i < numOfArg) {
                    typ match {
                      case Some(t) => {
                        t match {
                          case SArrayType(info, _t) => {
                            for (j <- i until numOfArg) {
                              val arg = args.get(j)
                              if(!checkType(arg, _t, heap)) {
                                errors = true
                              }
                            }
                          }
                          case _ =>
                        }
                      }
                      case None =>
                    }
                  }
                } else {
                  if (i < numOfArg) {
                    typ match {
                      case Some(t) => {
                        val arg = args.get(i)
                          if(!checkType(arg, t, heap)) {
                          errors = true
                        }
                      }
                      case None =>
                    }
                  }
                }
                if (opt) {
                  numOfOptional+=1
                }
              }
            }

            if (!hasRest) {
              argSize = (numOfParam-numOfOptional, numOfParam)
            } else {
              argSize = (numOfParam-numOfOptional-1, -1)
            }

            properArgSize(argSize, numOfArg) match {
              case Some(str) => {
                errors = true
              }
              case None =>
            }
          }
        })
        if (errors) return ((HeapBot, ContextBot), (heapExc, contextExc))
      }
      case None =>
    }

    returnType match {
      case Some(ret) => {
        val (ret_v, ret_h, ret_ctx) = TSType2Value(ret, heap, context, successorProvider())
        ((Helper.ReturnStore(ret_h, ret_v), ret_ctx), (heapExc, contextExc))
      }
      case None => {
        returnTypeMap.get(funcName) match {
          case Some(ret) => {
            val (ret_v, ret_h, ret_ctx) = TSType2Value(ret, heap, context, successorProvider())
            ((Helper.ReturnStore(ret_h, ret_v), ret_ctx), (heapExc, contextExc))
          }
          case None => {
            ((heap, context), (heapExc, contextExc))
          }
        }
      }
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
