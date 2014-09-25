/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.builtin

import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG, InternalError, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper=>AH}
import kr.ac.kaist.jsaf.bug_detector.BugKind
import kr.ac.kaist.jsaf.bug_detector.ToPropertyDescriptor
import kr.ac.kaist.jsaf.bug_detector.ToPropertyDescriptors
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object BuiltinObject extends ModelData {

  val ConstLoc = newSystemLoc("ObjectConst", Recent)
  //val ProtoLoc = newPreDefLoc("ObjectProto", Recent)

  private val prop_const: List[(String, AbsProperty)] = List(
    ("@class",                   AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto",                   AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F)))),
    ("@extensible",              AbsConstValue(PropValue(T))),
    ("@scope",                   AbsConstValue(PropValueNullTop)),
    ("@function",                AbsInternalFunc("Object")),
    ("@construct",               AbsInternalFunc("Object.constructor")),
    ("@hasinstance",             AbsConstValue(PropValueNullTop)),
    ("prototype",                AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("length",                   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, F, F)))),
    ("getPrototypeOf",           AbsBuiltinFunc("Object.getPrototypeOf", 1)),
    ("getOwnPropertyDescriptor", AbsBuiltinFunc("Object.getOwnPropertyDescriptor", 2)),
    ("getOwnPropertyNames",      AbsBuiltinFunc("Object.getOwnPropertyNames", 1)),
    ("create",                   AbsBuiltinFunc("Object.create", 2)),
    ("defineProperty",           AbsBuiltinFunc("Object.defineProperty", 3)),
    ("defineProperties",         AbsBuiltinFunc("Object.defineProperties", 2)),
    ("seal",                     AbsBuiltinFunc("Object.seal", 1)),
    ("freeze",                   AbsBuiltinFunc("Object.freeze", 1)),
    ("preventExtensions",        AbsBuiltinFunc("Object.preventExtensions", 1)),
    ("isSealed",                 AbsBuiltinFunc("Object.isSealed", 1)),
    ("isFrozen",                 AbsBuiltinFunc("Object.isFrozen", 1)),
    ("isExtensible",             AbsBuiltinFunc("Object.isExtensible", 1)),
    ("keys",                     AbsBuiltinFunc("Object.keys", 1))
  )

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(PValue(NullTop), F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(BoolTrue))),
    ("constructor",          AbsConstValue(PropValue(ObjectValue(ConstLoc, T, F, T)))),
    ("toString",             AbsBuiltinFunc("Object.prototype.toString", 0)),
    ("toLocaleString",       AbsBuiltinFunc("Object.prototype.toLocaleString", 0)),
    ("valueOf",              AbsBuiltinFunc("Object.prototype.valueOf", 0)),
    ("hasOwnProperty",       AbsBuiltinFunc("Object.prototype.hasOwnProperty", 1)),
    ("isPrototypeOf",        AbsBuiltinFunc("Object.prototype.isPrototypeOf", 1)),
    ("propertyIsEnumerable", AbsBuiltinFunc("Object.prototype.propertyIsEnumerable", 1))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (ConstLoc, prop_const), (ObjProtoLoc, prop_proto)
  )

  def toPropertyDescriptor(h: Heap, v_2: Value, toDescriptor: BugKind, abstraction: Boolean) =
    // 8.10.5 1.
    if ((v_2._1 </ PValueBot || v_2._2.isEmpty) && Config.typingInterface != null) {
      if(Shell.params.opt_DeveloperMode || !abstraction)
        Config.typingInterface.signal(Config.typingInterface.getSpan, toDescriptor, v_2._1.toString, null)
      Set[Exception](TypeError)
    // 8.10.5 7.b.
    } else {
      v_2._2.toSet.find(l => if (BoolTrue <= Helper.HasProperty(h, l, AbsString.alpha("get"))) {
                               val getter = Helper.Proto(h, l, AbsString.alpha("get"))
                               getter._2.toSet.find(ll =>
                                         BoolFalse <= Helper.IsCallable(h, ll) &&
                                         getter._1._1 <= UndefBot).isDefined ||
                                         getter._1._1 <= UndefBot && getter._1._1 <= UndefBot
                             } else false) match {
        case Some(l) =>
          val getter = Helper.Proto(h, l, AbsString.alpha("get"))
          val get = getter._2.toSet.find(ll => BoolFalse <= Helper.IsCallable(h, ll) &&
                                               getter._1._1 <= UndefBot) match {
                      case Some(ll) => h(ll).toString
                      case None => getter._1.toString
                    }

          if(Shell.params.opt_DeveloperMode || !abstraction)
            Config.typingInterface.signal(Config.typingInterface.getSpan, toDescriptor, get, null)
          Set[Exception](TypeError)
        case _ =>
          // 8.10.5 8.b.
          v_2._2.toSet.find(l => if (BoolTrue <= Helper.HasProperty(h, l, AbsString.alpha("set"))) {
                                   val setter = Helper.Proto(h, l, AbsString.alpha("set"))
                                   setter._2.toSet.find(ll =>
                                         BoolFalse <= Helper.IsCallable(h, ll) &&
                                         setter._1._1 <= UndefBot).isDefined ||
                                         setter._2.isEmpty && setter._1._1 <= UndefBot
                                 } else false) match {
            case Some(l) =>
              val setter = Helper.Proto(h, l, AbsString.alpha("set"))
              val set = setter._2.toSet.find(ll => BoolFalse <= Helper.IsCallable(h, ll) &&
                                                   setter._1._1 <= UndefBot) match {
                          case Some(ll) => h(ll).toString
                          case None => setter._1.toString
                        }
              if(Shell.params.opt_DeveloperMode || !abstraction)
                Config.typingInterface.signal(Config.typingInterface.getSpan, toDescriptor, set, null)
              Set[Exception](TypeError)
            case _ =>
              // 8.10.5 9.a.
              v_2._2.toSet.find(l => BoolTrue <= Helper.HasProperty(h, l, AbsString.alpha("value"))) match {
                case Some(l) =>
                  if(Shell.params.opt_DeveloperMode || !abstraction)
                    Config.typingInterface.signal(Config.typingInterface.getSpan, toDescriptor,
                                                Helper.Proto(h, l, AbsString.alpha("value"))._1.toString, null)
                  Set[Exception](TypeError)
                case _ =>
                  v_2._2.toSet.find(l => BoolTrue <= Helper.HasProperty(h, l, AbsString.alpha("writable"))) match {
                    case Some(l) =>
                      if(Shell.params.opt_DeveloperMode || !abstraction)
                        Config.typingInterface.signal(Config.typingInterface.getSpan, toDescriptor,
                                                    Helper.Proto(h, l, AbsString.alpha("writable"))._1.toString, null)
                      Set[Exception](TypeError)
                    case _ =>
                      ExceptionBot
                  }
              }
            }
          }
    }

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      "Object" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          // 15.2.2.1 new Object( [value] )
          val v = getArgValue(h, ctx, args, "0") // [value]

          // 1.a. If Type(value) is Object, then simply return value.
          //      We do not consider an implementation-dependent actions for a host object.
          val (v_1, h_1, ctx_1) =
            if (!v._2.isEmpty) (Value(v._2), h, ctx)
            else (ValueBot, HeapBot, ContextBot)

          // 1.b. If Type(value) is String, return ToObject(value)
          // 1.c. If Type(value) is Boolean, return ToObject(value)
          // 1.d. If Type(value) is Number, return ToObject(value)
          val (v_2, h_2, ctx_2, es) =
            if ((v._1._3 </ BoolBot) || (v._1._4 </ NumBot) || (v._1._5 </ StrBot)) {
              val _v_new = Value(PValue(UndefBot, NullBot, v._1._3, v._1._4, v._1._5))
              val (_v, _h, _ctx, _es) = Helper.toObject(h, ctx, _v_new, addr1)
              (_v, _h, _ctx, _es)
            } else {
              (ValueBot, HeapBot, ContextBot, ExceptionBot)
            }
          // 2. Assert: The argument value was not supplied or its type was Null or Undefined.
          val (v_3, h_3, ctx_3) =
            if ((v._1._1 </ UndefBot) || (v._1._2 </ NullBot)) {
              val (_h_1, _ctx_1) = Helper.Oldify(h, ctx, addr1)
              val _l_r = addrToLoc(addr1, Recent)
              val _h = Helper.allocObject(_h_1, ObjProtoSingleton, _l_r)
              (Value(_l_r), _h, _ctx_1)
            } else {
              (ValueBot, HeapBot, ContextBot)
            }

          val v_4 = v_1 + v_2 + v_3
          val h_4 = h_1 + h_2 + h_3
          val ctx_4 = ctx_1 + ctx_2 + ctx_3

          val (h_e, ctx_e) = Helper.RaiseException(h_2, ctx_2, es)
          val s = (he + h_e, ctxe + ctx_e)

          if (v_4 </ ValueBot)
            ((Helper.ReturnStore(h_4, v_4), ctx_4), s)
          else
            ((HeapBot, ContextBot), s)
        }),
      ("Object.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          // 15.2.2.1 new Object( [value] )
          val v = getArgValue(h, ctx, args, "0") // [value]

          // 1.a. If Type(value) is Object, then simply return value.
          //      We do not consider an implementation-dependent actions for a host object.
          val (v_1, h_1, ctx_1) =
            if (!v._2.isEmpty) (Value(v._2), h, ctx)
            else (ValueBot, HeapBot, ContextBot)

          // 1.b. If Type(value) is String, return ToObject(value)
          // 1.c. If Type(value) is Boolean, return ToObject(value)
          // 1.d. If Type(value) is Number, return ToObject(value)
          val (v_2, h_2, ctx_2) =
            if ((v._1._3 </ BoolBot) || (v._1._4 </ NumBot) || (v._1._5 </ StrBot)) {
              val _v_new = Value(PValue(UndefBot, NullBot, v._1._3, v._1._4, v._1._5))
              val o_1 =
                if (!(_v_new._1._5 <= StrBot)) Helper.NewString(v._1._5)
                else Obj.bottom
              val o_2 =
                if (!(_v_new._1._3 <= BoolBot)) Helper.NewBoolean(v._1._3)
                else Obj.bottom
              val o_3 =
                if (!(_v_new._1._4 <= NumBot)) Helper.NewNumber(v._1._4)
                else Obj.bottom
              val o = o_1 + o_2 + o_3
              val _h = lset_this.foldLeft(HeapBot)((_h, l) => _h + h.update(l, o))
              (Value(lset_this), _h, ctx)
            } else {
              (ValueBot, HeapBot, ContextBot)
            }
          // 2. Assert: The argument value was not supplied or its type was Null or Undefined.
          val (v_3, h_3, ctx_3) =
            if ((v._1._1 </ UndefBot) || (v._1._2 </ NullBot)) {
              val _h = lset_this.foldLeft(HeapBot)((_h, l) => _h + Helper.allocObject(h, ObjProtoSingleton, l))
              (Value(lset_this), _h, ctx)
            } else {
              (ValueBot, HeapBot, ContextBot)
            }

          val v_4 = v_1 + v_2 + v_3
          val h_4 = h_1 + h_2 + h_3
          val ctx_4 = ctx_1 + ctx_2 + ctx_3
          if (v_4 </ ValueBot)
            ((Helper.ReturnStore(h_4, v_4), ctx_4), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("Object.getPrototypeOf" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val es =
            if (v_1._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val v_2 = v_1._2.foldLeft(ValueBot)(
            (_v, l) => _v + h(l)("@proto")._1._1)
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          val (h_1, ctx_1) =
            if (v_2 </ ValueBot) (Helper.ReturnStore(h,v_2), ctx)
            else (HeapBot, ContextBot)
          ((h_1, ctx_1), (he + h_e, ctxe + ctx_e))
        })),
      "Object.getOwnPropertyDescriptor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val s_prop = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          val es =
            if (v_1._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val (propv, absent) = v_1._2.foldLeft[(PropValue, AbsBool)]((PropValueBot, BoolBot))((pva, l) => {
            val pv = h(l)(s_prop)
            val a = h(l).domIn(s_prop)
            (pva._1 + pv, pva._2 + a)})
          val (v_2, h_2, ctx_2) =
            if (BoolFalse <= absent || propv <= PropValueBot )
              (Value(UndefTop), h, ctx)
            else
              (ValueBot, HeapBot, ContextBot)
          val ov = propv._1
          val (v_3, h_3, ctx_3) =
            if (Value(PValue(UndefBot, ov._1._1._2, ov._1._1._3, ov._1._1._4, ov._1._1._5), ov._1._2) </ ValueBot) {
              val lset_env = h(SinglePureLocalLoc)("@env")._2._2
              val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
              if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
              val addr_env = (cp._1._1, set_addr.head)
              val addr1 = cfg.getAPIAddress(addr_env, 0)
              val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
              val l_r = addrToLoc(addr1, Recent)
              val o_new = Helper.NewObject(ObjProtoLoc)
              val o_1 =
                if (true) // isDataDescriptor(H(v),s)
                  o_new.
                    update("value", PropValue(ObjectValue(ov._1, BoolTrue, BoolTrue, BoolTrue))).
                    update("writable", PropValue(ObjectValue(ov._2, BoolTrue, BoolTrue, BoolTrue)))
                else
                  o_new
              val o_2 = o_1.
                update("enumerable", PropValue(ObjectValue(ov._3, BoolTrue, BoolTrue, BoolTrue))).
                update("configurable", PropValue(ObjectValue(ov._4, BoolTrue, BoolTrue, BoolTrue)))
              val h_2 = h_1.update(l_r, o_2)
              (Value(LocSet(l_r)), h_2, ctx_1)
            }
            else
              (ValueBot, HeapBot, ContextBot)
          val v_4 = v_2 + v_3
          val h_4 = h_2 + h_3
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          val (h_5, ctx_5) =
            if (v_4 </ ValueBot) (Helper.ReturnStore(h_4, v_4), ctx_2 + ctx_3)
            else (HeapBot, ContextBot)
          ((h_5, ctx_5), (he + h_e, ctxe + ctx_e))
        }),
      ("Object.getOwnPropertyNames" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v = getArgValue(h_1, ctx_1, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val o = v._2.foldLeft(Obj.bottom)((_o, l) => {
            val o_new = Helper.NewArrayObject(AbsNumber.alpha(h_1(l).getProps.size))
            val o_1 = h_1(l).getProps.foldLeft(o_new)((_o, s) => _o.update(NumStr, PropValue(ObjectValue(AbsString.alpha(s),BoolTrue,BoolTrue,BoolTrue))))
            val o_2 =
              if (h_1(l)(Str_default_number) </ PropValueBot)
                o_new.update(NumStr, PropValue(ObjectValue(NumStr,BoolTrue,BoolTrue,BoolTrue)))
              else
                Obj.bottom
            val o_3 =
              if (h_1(l)(Str_default_other) </ PropValueBot)
                o_new.update(NumStr, PropValue(ObjectValue(OtherStr,BoolTrue,BoolTrue,BoolTrue)))
              else
                Obj.bottom
            o_1 + o_2 + o_3
          })
          val (h_e, ctx_e) = Helper.RaiseException(h_1, ctx_1, es)
          if (o </ Obj.bottom) {
            val h_2 = h_1.update(l_r, o)
            ((Helper.ReturnStore(h_2, Value(l_r)), ctx_1), (he+h_e, ctxe+ctx_e))
          }
          else
            ((HeapBot, ContextBot), (he+h_e, ctxe+ctx_e))
        })),
      "Object.create" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r = addrToLoc(addr1, Recent)
          val (h1, ctx1) = Helper.Oldify(h, ctx, addr1)

          val v_1 = getArgValue(h1, ctx1, args, "0")
          val v_2 = getArgValue(h1, ctx1, args, "1")
          // 1. If Type(O) is not Object or Null throw a TypeError exception.
          val es_1 = if (v_1._1._1 </ UndefBot || v_1._1._3 </ BoolBot || v_1._1._4 </ NumBot || v_1._1._5 </ StrBot) Set[Exception](TypeError)
                     else ExceptionBot
          // 2. Let obj be the result of creating a new object as if by the expression new Object() where Object
          //    is the standard built-in constructor with that name
          // 3. Set the [[Prototype]] internal property of obj to O.
          val o_1 =
            if (v_1._1._2 </ NullBot) Helper.NewObject()
            else Obj.bottom
          val o_2 =
            if (!v_1._2.isEmpty) Helper.NewObject(v_1._2)
            else Obj.bottom
          val o = o_1 + o_2

          val (lset, h_1, ctx_1) =
            if (o </ Obj.bottom) {
              val h_2_ = h1.update(l_r, o)
              // 4. If the argument Properties is present and not undefined, add own properties to obj as if by calling
              //    the standard built-in function Object.defineProperties with arguments obj and Properties.
              val h_3_ =
                if (!v_2._2.isEmpty) {
                  val newobj = v_2._2.foldLeft(h_2_(l_r))((_obj, l_2) => _obj + Helper.DefineProperties(h_2_, l_r, l_2))
                  h_2_.update(l_r, newobj)
                }
                else
                  h_2_

              (LocSet(l_r), h_3_, ctx1)
            } else {
              (LocSetBot, HeapBot, ContextBot)
            }

          // 4. If the argument Properties is present and not undefined, add own properties to obj as if by calling
          //    the standard built-in function Object.defineProperties with arguments obj and Properties.
          val es_2 =
            if (v_2._1._2 </ NullBot || v_2._1._3 </ BoolBot || v_2._1._4 </ NumBot || v_2._1._5 </ StrBot) Set[Exception](TypeError)
            else ExceptionBot

          val es = es_1 ++ es_2
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          ((Helper.ReturnStore(h_1, Value(lset)), ctx_1), (he + h_e, ctxe + ctx_e))
        }),
      // 15.2.3.6
      ("Object.defineProperty" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val lset_callee = getArgValue(h, ctx, args, "callee")._2
          val abstraction = (lset_callee.size > 1)
          // 1.
          val es_1 =
            if (v_1._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          // 2.
          val s_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          // 3.
          val v_2 = getArgValue(h, ctx, args, "2")
          val es_2 = toPropertyDescriptor(h, v_2, ToPropertyDescriptor, abstraction)
          
          // 4.
          val h_1 =
            v_1._2.foldLeft(h)((_h, l_1) => {
              val newobj = v_2._2.foldLeft(h(l_1))((__obj, l_2) =>
                __obj + Helper.DefineProperty(h, l_1, s_name, l_2)) 
              _h.update(l_1, newobj)
            })
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es_1 ++ es_2)
          if (Value(v_1._2) </ ValueBot)
            ((Helper.ReturnStore(h_1, Value(v_1._2)), ctx), (he+h_e, ctxe+ctx_e))
          else
            ((HeapBot, ContextBot), (he+h_e, ctxe+ctx_e))
        })),
      // 15.2.3.7
      ("Object.defineProperties" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val lset_callee = getArgValue(h, ctx, args, "callee")._2
          val abstraction = (lset_callee.size > 1)
          // 1.
          val es_1 =
            if (v_1._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          // 2.
          val v_2 = getArgValue(h, ctx, args, "1")
          val es_2 =
            if (v_2._1 </ PValueBot) {
              if (Config.typingInterface != null)
                if(Shell.params.opt_DeveloperMode || !abstraction)
                  Config.typingInterface.signal(Config.typingInterface.getSpan, ToPropertyDescriptor, v_2._1.toString, null)
              Set[Exception](TypeError)
            } else {
              v_2._2.foldLeft(ExceptionBot)((x, l) =>
                                          {try{Helper.CollectOwnProps(h, LocSet(l))} catch{
                                             case e: InternalError => {h(l).getProps}}
                                          }.foldLeft(x)((x, s) => {
                                               val prop = AbsString.alpha(s)
                                               val v__1 = Helper.Proto(h, l, prop)
                                               v__1._2.foldLeft(x)((x, ll) => x++toPropertyDescriptor(h, v__1, ToPropertyDescriptors, abstraction))}))
            }
          val h_1 =
            v_1._2.foldLeft(h)((_h, l_1) => {
              val newobj = v_2._2.foldLeft(h(l_1))((__obj, l_2) => __obj + Helper.DefineProperties(h, l_1, l_2))
              _h.update(l_1, newobj)
            })
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es_1 ++ es_2)
          if (Value(v_1._2) </ ValueBot)
            ((Helper.ReturnStore(h_1, Value(v_1._2)), ctx), (he+h_e, ctxe+ctx_e))
          else
            ((HeapBot, ContextBot), (he+h_e, ctxe+ctx_e))
        })),
      ("Object.seal" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val h_1 = v._2.foldLeft(HeapBot)((_h, l) => {
            val obj = h(l)
            val obj_1 = obj.getProps.foldLeft(obj)((_o, s) => {
              val ov = _o(s)._1
              _o.update(s, PropValue(ObjectValue(ov._1,ov._2,ov._3,BoolFalse)))
            })
            val obj_2 = obj_1.update("@extensible", PropValue(BoolFalse))
            _h + h.update(l, obj_2)
          })
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          if (Value(v._2) </ ValueBot)
            ((Helper.ReturnStore(h_1, Value(v._2)), ctx), (he+h_e, ctxe+ctx_e))
          else
            ((HeapBot, ContextBot), (he+h_e, ctxe+ctx_e))
        })),
      ("Object.freeze" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val h_1 = v._2.foldLeft(HeapBot)((_h, l) => {
            val obj = h(l)
            val obj_1 = obj.getProps.foldLeft(obj)((_o, s) => {
              val ov = _o(s)._1
              _o.update(s, PropValue(ObjectValue(ov._1,BoolFalse,ov._3,BoolFalse)))
            })
            val obj_2 = obj_1.update("@extensible", PropValue(BoolFalse))
            _h + h.update(l, obj_2)
          })
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          if (Value(v._2) </ ValueBot)
            ((Helper.ReturnStore(h_1, Value(v._2)), ctx), (he+h_e, ctxe+ctx_e))
          else
            ((HeapBot, ContextBot), (he+h_e, ctxe+ctx_e))
        })),
      ("Object.preventExtensions" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val h_1 = v._2.foldLeft(HeapBot)((_h, l) =>
            _h + h.update(l, h(l).update("@extensible", PropValue(BoolFalse))))
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          if (Value(v._2) </ ValueBot)
            ((Helper.ReturnStore(h_1, Value(v._2)), ctx), (he+h_e, ctxe+ctx_e))
          else
            ((HeapBot, ContextBot), (he+h_e, ctxe+ctx_e))
        })),
      ("Object.isSealed" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val b = v._2.foldLeft[AbsBool](BoolBot)((_b, l) => {
            val o = h(l)
            val props = o.getProps
            val b_f =
              if (props.exists((s) => BoolTrue <= o(s)._1._4))
                BoolFalse
              else  BoolBot
            val b_t =
              if (props.forall((s) => BoolFalse <= o(s)._1._4)) {
                val v_ex = o("@extensible")._2
                if (Value(BoolTop) <= v_ex)  BoolTop
                else if (Value(BoolFalse) <= v_ex) BoolTrue
                else if (Value(BoolTrue) <= v_ex) BoolFalse
                else BoolBot
              }
              else
                BoolBot
            _b + b_f + b_t
          })
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          if (b </ BoolBot)
            ((Helper.ReturnStore(h, Value(b)), ctx), (he+h_e, ctxe+ctx_e))
          else
            ((HeapBot, ContextBot), (he+h_e, ctxe+ctx_e))
        })),
      ("Object.isFrozen" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val b = v._2.foldLeft[AbsBool](BoolBot)((_b, l) => {
            val o = h(l)
            val props = o.getProps
            val b_f =
              if (props.exists((s) => (BoolTrue <= o(s)._1._2 || BoolTrue <= o(s)._1._4)))
                BoolFalse
              else
                BoolBot
            val b_t =
              if (props.forall((s) => (BoolFalse <= o(s)._1._2 && BoolFalse <= o(s)._1._4))) {
                val v_ex = o("@extensible")._2
                if (Value(BoolTop) <= v_ex)  BoolTop
                else if (Value(BoolFalse) <= v_ex) BoolTrue
                else if (Value(BoolTrue) <= v_ex) BoolFalse
                else BoolBot
              }
              else
                BoolBot
            _b + b_f + b_t
          })
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          if (b </ BoolBot)
            ((Helper.ReturnStore(h, Value(b)), ctx), (he+h_e, ctxe+ctx_e))
          else
            ((HeapBot, ContextBot), (he+h_e, ctxe+ctx_e))
        })),
      ("Object.isExtensible" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val v_ex = v._2.foldLeft[Value](ValueBot)((_v, l) =>
            _v + h(l)("@extensible")._2)
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          if (v </ ValueBot)
            ((Helper.ReturnStore(h, v_ex), ctx), (he+h_e, ctxe+ctx_e))
          else
            ((HeapBot, ContextBot), (he+h_e, ctxe+ctx_e))
        })),
      "Object.keys" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r = addrToLoc(addr1, Recent)

          val v = getArgValue(h, ctx, args, "0")
          // 1. If the Type(O) is not Object, throw a TypeError exception.
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot

          val (h_2, ctx_2) =
            if (!v._2.isEmpty) {
              val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
              val o = try {
                val list = Helper.CollectOwnProps(h, v._2).toArray
                val o_new = Helper.NewArrayObject(AbsNumber.alpha(list.size))

                val o_1 = (0 to list.length - 1).foldLeft(o_new)((_o, i) => {
                  _o.update(AbsString.alpha(i.toString), PropValue(ObjectValue(AbsString.alpha(list(i)), BoolTrue, BoolTrue, BoolTrue)))
                })
                o_1
              } catch {
                case e: InternalError => {
                  v._2.foldLeft(Obj.bottom)((_o, l) => {
                    val map_enum = h_1(l).getProps.filter((kv) => BoolTrue <= h_1(l)(kv)._1._3 && !(kv.take(1) == "@"))
                    val o_new = Helper.NewArrayObject(UInt)
                    val o_1 = map_enum.foldLeft(o_new)((_o, kv) => _o.update(NumStr, PropValue(ObjectValue(AbsString.alpha(kv), BoolTrue, BoolTrue, BoolTrue))))
                    val o_2 =
                      if (h_1(l)(Str_default_number) </ PropValueBot)
                        o_new.update(NumStr, PropValue(ObjectValue(NumStr, BoolTrue, BoolTrue, BoolTrue)))
                      else
                        Obj.bottom
                    val o_3 =
                      if (h_1(l)(Str_default_other) </ PropValueBot)
                        o_new.update(NumStr, PropValue(ObjectValue(OtherStr, BoolTrue, BoolTrue, BoolTrue)))
                      else
                        Obj.bottom
                    o_1 + o_2 + o_3
                  })
                }
              }
              val h_2 = h_1.update(l_r, o)
              ((Helper.ReturnStore(h_2, Value(l_r)), ctx_1))
            } else {
              (HeapBot, ContextBot)
            }

          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          ((h_2, ctx_2), (he + h_e, ctxe + ctx_e))
        }),
      ("Object.prototype.toString"-> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val s = lset_this.foldLeft[AbsString](StrBot)((_s, l) => {
            val absstr = h(l)("@class")._2._1._5
            _s + (absstr.getAbsCase match {
              case AbsSingle =>
                AbsString.alpha("[object " + absstr.getSingle.get + "]")
              case AbsBot =>
                StrBot
              case _ =>
                OtherStr
                })})
          if (s </ StrBot)
            ((Helper.ReturnStore(h, Value(s)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("Object.prototype.toLocaleString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val s = lset_this.foldLeft[AbsString](StrBot)((_s, l) => {
            val absstr = h(l)("@class")._2._1._5
            _s + (absstr.getAbsCase match {
              case AbsSingle =>
                AbsString.alpha("[object " + absstr.getSingle.get + "]")
              case AbsBot =>
                StrBot
              case _ =>
                OtherStr
                })})
          if (s </ StrBot)
            ((Helper.ReturnStore(h, Value(s)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("Object.prototype.valueOf" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          if (Value(lset_this) </ ValueBot)
            ((Helper.ReturnStore(h, Value(lset_this)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("Object.prototype.hasOwnProperty" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          // 15.2.4.5 Object.prototype.hasOwnProperty(V)
          val s = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val b = lset_this.foldLeft[AbsBool](BoolBot)((b,l) => {
            b + Helper.HasOwnProperty(h, l, s)
          })
          if (b </ BoolBot)
            ((Helper.ReturnStore(h, Value(b)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("Object.prototype.isPrototypeOf" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val v = getArgValue(h, ctx, args, "0")
          val b_1 =
            if (v._1 </ PValueBot)
              BoolFalse
            else
              BoolBot
          val b_2 = v._2.foldLeft[AbsBool](BoolBot)((b,l) => {
            val v_proto = h(l)("@proto")._1._1
            val b_3 =
              if (NullTop <= v_proto._1._2)
                BoolFalse
              else
                BoolBot
            val b_4 = Operator.bopEq(Value(lset_this), Value(v_proto._2))._1._3
            b + b_3 + b_4})
          val b = b_1 + b_2
          if (b </ BoolBot)
            ((Helper.ReturnStore(h, Value(b)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("Object.prototype.propertyIsEnumerable" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val s = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val b =
            lset_this.foldLeft[AbsBool](BoolBot)((_b, l) => {
              val ov = h(l)(s)._1._1
              val hasProp = Helper.HasProperty(h, l, s)
              val b_1 =
                if (BoolFalse <= hasProp)
                  BoolFalse
                else
                  BoolBot
              val b_2 =
                if (BoolTrue <= hasProp)
                  Helper.ProtoProp(h, l, s)._1._3
                else
                  BoolBot
              _b + b_1 + b_2
            })
          if (b </ BoolBot)
            ((Helper.ReturnStore(h, Value(b)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("Object" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_env = h(PureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          // 15.2.2.1 new Object( [value] )
          val v = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) // [value]

          // 1.a. If Type(value) is Object, then simply return value.
          //      We do not consider an implementation-dependent actions for a host object.
          val (v_1, h_1, ctx_1) =
            if (!v._2.isEmpty) (Value(v._2), h, ctx)
            else (ValueBot, h, ctx)

          // 1.b. If Type(value) is String, return ToObject(value)
          // 1.c. If Type(value) is Boolean, return ToObject(value)
          // 1.d. If Type(value) is Number, return ToObject(value)
          val (v_2, h_2, ctx_2, es) =
            if ((v._1._3 </ BoolBot) || (v._1._4 </ NumBot) || (v._1._5 </ StrBot)) {
              val _v_new = Value(PValue(UndefBot, NullBot, v._1._3, v._1._4, v._1._5))
              val (_v, _h, _ctx, _es) = PreHelper.toObject(h_1, ctx_1, _v_new, addr1)
              (_v, _h, _ctx, _es)
            } else {
              (ValueBot, h_1, ctx_1, ExceptionBot)
            }
          // 2. Assert: The argument value was not supplied or its type was Null or Undefined.
          val (v_3, h_3, ctx_3) =
            if ((v._1._1 </ UndefBot) || (v._1._2 </ NullBot)) {
              val (_h_1, _ctx_1) = PreHelper.Oldify(h_2, ctx_2, addr1)
              val _l_r = addrToLoc(addr1, Recent)
              val _h = PreHelper.allocObject(_h_1, ObjProtoSingleton, _l_r)
              (Value(_l_r), _h, _ctx_1)
            } else {
              (ValueBot, h_2, ctx_2)
            }

          val v_4 = v_1 + v_2 + v_3
          // val h_4 = h_1 + h_2 + h_3
          val h_4 = h_3
          val ctx_4 = ctx_3

          val (h_e, ctx_e) = PreHelper.RaiseException(h_4, ctx_4, PureLocalLoc, es)
          val s = (he + h_e, ctxe + ctx_e)

          if (v_4 </ ValueBot)
            ((PreHelper.ReturnStore(h_4, PureLocalLoc, v_4), ctx_4), s)
          else
            ((h_4, ctx_4), s)
        })),
      ("Object.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._2._2
          // 15.2.2.1 new Object( [value] )
          val v = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) // [value]

          // 1.a. If Type(value) is Object, then simply return value.
          //      We do not consider an implementation-dependent actions for a host object.
          val (v_1, h_1, ctx_1) =
            if (!v._2.isEmpty) (Value(v._2), h, ctx)
            else (ValueBot, h, ctx)

          // 1.b. If Type(value) is String, return ToObject(value)
          // 1.c. If Type(value) is Boolean, return ToObject(value)
          // 1.d. If Type(value) is Number, return ToObject(value)
          val (v_2, h_2, ctx_2) =
            if ((v._1._3 </ BoolBot) || (v._1._4 </ NumBot) || (v._1._5 </ StrBot)) {
              val _v_new = Value(PValue(UndefBot, NullBot, v._1._3, v._1._4, v._1._5))
              val o_1 =
                if (!(_v_new._1._5 <= StrBot)) PreHelper.NewString(v._1._5)
                else Obj.bottom
              val o_2 =
                if (!(_v_new._1._3 <= BoolBot)) PreHelper.NewBoolean(v._1._3)
                else Obj.bottom
              val o_3 =
                if (!(_v_new._1._4 <= NumBot)) PreHelper.NewNumber(v._1._4)
                else Obj.bottom
              val o = o_1 + o_2 + o_3
              val _h = lset_this.foldLeft(h)((_h, l) => _h.update(l, o))
              (Value(lset_this), _h, ctx)
            } else {
              (ValueBot, h, ctx)
            }
          // 2. Assert: The argument value was not supplied or its type was Null or Undefined.
          val (v_3, h_3, ctx_3) =
            if ((v._1._1 </ UndefBot) || (v._1._2 </ NullBot)) {
              val _h = lset_this.foldLeft(h_2)((_h, l) => PreHelper.allocObject(_h, ObjProtoSingleton, l))
              (Value(lset_this), _h, ctx)
            } else {
              (ValueBot, h_2, ctx_2)
            }

          val v_4 = v_1 + v_2 + v_3
          //        val h_4 = h_1 + h_2 + h_3
          //        val ctx_4 = ctx_1 + ctx_2 + ctx_3
          val h_4 = h_3
          val ctx_4 = ctx_3
          if (v_4 </ ValueBot)
            ((PreHelper.ReturnStore(h_4, PureLocalLoc, v_4), ctx_4), (he, ctxe))
          else
            ((h_4, ctx_4), (he, ctxe))
        })),
      ("Object.getPrototypeOf" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)

          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val es =
            if (v_1._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val v_2 = v_1._2.foldLeft(ValueBot)(
            (_v, l) => _v + h(l)("@proto")._1._1)
          val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)
          val (h_1, ctx_1) =
            if (v_2 </ ValueBot) (PreHelper.ReturnStore(h_e, PureLocalLoc,v_2), ctx_e)
            else (h, ctx)
          ((h_1, ctx_1), (he + h_e, ctxe + ctx_e))
        })),
      "Object.getOwnPropertyDescriptor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_env = h(PureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val s_prop = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "1", PureLocalLoc)))
          val es =
            if (v_1._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val (propv, absent) = v_1._2.foldLeft[(PropValue, AbsBool)]((PropValueBot, BoolBot))((pva, l) => {
            val pv = h(l)(s_prop)
            val a = h(l).domIn(s_prop)
            (pva._1 + pv, pva._2 + a)})
          val (v_2, h_2, ctx_2) =
            if (BoolFalse <= absent || propv <= PropValueBot )
              (Value(UndefTop), h, ctx)
            else
              (ValueBot, h, ctx)
          val ov = propv._1
          val (v_3, h_3, ctx_3) =
            if (Value(PValue(UndefBot, ov._1._1._2, ov._1._1._3, ov._1._1._4, ov._1._1._5), ov._1._2) </ ValueBot) {
              val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, addr1)
              val l_r = addrToLoc(addr1, Recent)
              val o_new = PreHelper.NewObject(ObjProtoLoc)
              val o_1 =
                if (true) // isDataDescriptor(H(v),s)
                  o_new.
                    update("value", PropValue(ObjectValue(ov._1, BoolTrue, BoolTrue, BoolTrue))).
                    update("writable", PropValue(ObjectValue(ov._2, BoolTrue, BoolTrue, BoolTrue)))
                else
                  o_new
              val o_2 = o_1.
                update("enumerable", PropValue(ObjectValue(ov._3, BoolTrue, BoolTrue, BoolTrue))).
                update("configurable", PropValue(ObjectValue(ov._4, BoolTrue, BoolTrue, BoolTrue)))
              val h_2 = h_1.update(l_r, o_2)
              (Value(LocSet(l_r)), h_2, ctx_1)
            }
            else
              (ValueBot, h_2, ctx_2)
          val v_4 = v_2 + v_3
          //        val h_4 = h_2 + h_3
          val h_4 = h_3
          val (h_e, ctx_e) = PreHelper.RaiseException(h_4, ctx_3, PureLocalLoc, es)
          val (h_5, ctx_5) =
            if (v_4 </ ValueBot) (PreHelper.ReturnStore(h_e, PureLocalLoc, v_4), ctx_e)
            else (h_4, ctx_3)
          ((h_5, ctx_5), (he + h_e, ctxe + ctx_e))
        }),
      ("Object.getOwnPropertyNames" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_env = h(PureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, addr1)
          val v = getArgValue_pre(h_1, ctx_1, args, "0", PureLocalLoc)
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val o = v._2.foldLeft(Obj.empty)((_o, l) => {
            val o_new = PreHelper.NewArrayObject(AbsNumber.alpha(h_1(l).getProps.size))
            val o_1 = h_1(l).getProps.foldLeft(o_new)((_o, s) => _o.update(NumStr, PropValue(ObjectValue(AbsString.alpha(s),BoolTrue,BoolTrue,BoolTrue))))
            val o_2 =
              if (h_1(l)(Str_default_number) </ PropValueBot)
                o_new.update(NumStr, PropValue(ObjectValue(NumStr,BoolTrue,BoolTrue,BoolTrue)))
              else
                Obj.bottom
            val o_3 =
              if (h_1(l)(Str_default_other) </ PropValueBot)
                o_new.update(NumStr, PropValue(ObjectValue(OtherStr,BoolTrue,BoolTrue,BoolTrue)))
              else
                Obj.bottom
            o_1 + o_2 + o_3
          })
          val (h_e, ctx_e) = PreHelper.RaiseException(h_1, ctx_1, PureLocalLoc, es)
          if (o </ Obj.bottom) {
            val h_2 = h_1.update(l_r, o)
            ((PreHelper.ReturnStore(h_2, PureLocalLoc, Value(l_r)), ctx_1), (he+h_e, ctxe+ctx_e))
          }
          else
            ((h_1, ctx_1), (he+h_e, ctxe+ctx_e))
        })),
      "Object.create" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_env = h(PureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)

          val l_r = addrToLoc(addr1, Recent)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val v_2 = getArgValue_pre(h, ctx, args, "1", PureLocalLoc)

          // 1. If Type(O) is not Object or Null throw a TypeError exception.
          val es_1 = if (v_1._1._1 </ UndefBot || v_1._1._3 </ BoolBot || v_1._1._4 </ NumBot || v_1._1._5 </ StrBot) Set[Exception](TypeError)
          else ExceptionBot
          // 2. Let obj be the result of creating a new object as if by the expression new Object() where Object
          //    is the standard built-in constructor with that name
          // 3. Set the [[Prototype]] internal property of obj to O.
          val o_1 =
            if (v_1._1._2 </ NullBot) PreHelper.NewObject()
            else Obj.bottom
          val o_2 =
            if (!v_1._2.isEmpty) PreHelper.NewObject(v_1._2)
            else Obj.bottom
          val o = o_1 + o_2

          val (lset, h_1, ctx_1) =
            if (o </ Obj.bottom) {
              val (h_1_, ctx_1_) = PreHelper.Oldify(h, ctx, addr1)
              val h_2_ = h_1_.update(l_r, o)
              // 4. If the argument Properties is present and not undefined, add own properties to obj as if by calling
              //    the standard built-in function Object.defineProperties with arguments obj and Properties.
              val h_3_ =
                if (!v_2._2.isEmpty)
                  v_2._2.foldLeft(HeapBot)((_h, l_2) => _h + PreHelper.DefineProperties(h_2_, l_r, l_2))
                else
                  h_2_

              (LocSet(l_r), h_3_, ctx_1_)
            } else {
              (LocSetBot, HeapBot, ContextBot)
            }

          // 4. If the argument Properties is present and not undefined, add own properties to obj as if by calling
          //    the standard built-in function Object.defineProperties with arguments obj and Properties.
          val es_2 =
            if (v_2._1._2 </ NullBot || v_2._1._3 </ BoolBot || v_2._1._4 </ NumBot || v_2._1._5 </ StrBot) Set[Exception](TypeError)
            else ExceptionBot

          val es = es_1 ++ es_2
          val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx_1, PureLocalLoc, es)
          ((PreHelper.ReturnStore(h_1, PureLocalLoc, Value(lset)), ctx_1), (he + h_e, ctxe + ctx_e))
        }),
      ("Object.defineProperty" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)

          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val es_1 =
            if (v_1._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val s_name = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "1", PureLocalLoc)))
          val v_2 = getArgValue_pre(h, ctx, args, "2", PureLocalLoc)
          val es_2 =
            if (v_2._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val h_1 =
            v_1._2.foldLeft(h)((_h, l_1) =>
              v_2._2.foldLeft(_h)((__h, l_2) =>
                PreHelper.DefineProperty(__h, l_1, s_name, l_2)) )
          val (h_e, ctx_e) = PreHelper.RaiseException(h_1, ctx, PureLocalLoc, es_1 ++ es_2)
          if (Value(v_1._2) </ ValueBot)
            ((PreHelper.ReturnStore(h_e, PureLocalLoc, Value(v_1._2)), ctx_e), (he+h_e, ctxe+ctx_e))
          else
            ((h_1, ctx), (he+h_e, ctxe+ctx_e))
        })),
      ("Object.defineProperties" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)

          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val es_1 =
            if (v_1._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val v_2 = getArgValue_pre(h, ctx, args, "1", PureLocalLoc)
          val es_2 =
            if (v_2._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val h_1 =
            v_1._2.foldLeft(h)((_h, l_1) =>
              v_2._2.foldLeft(_h)((__h, l_2) => PreHelper.DefineProperties(__h, l_1, l_2)))
          val (h_e, ctx_e) = PreHelper.RaiseException(h_1, ctx, PureLocalLoc, es_1 ++ es_2)
          if (Value(v_1._2) </ ValueBot)
            ((PreHelper.ReturnStore(h_e, PureLocalLoc, Value(v_1._2)), ctx_e), (he+h_e, ctxe+ctx_e))
          else
            ((h_1, ctx), (he+h_e, ctxe+ctx_e))
        })),
      ("Object.seal" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)

          val v = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val h_1 = v._2.foldLeft(h)((_h, l) => {
            val obj = _h(l)
            val obj_1 = obj.getProps.foldLeft(obj)((_o, s) => {
              val ov = _o(s)._1
              _o.update(s, PropValue(ObjectValue(ov._1,ov._2,ov._3,BoolFalse)))
            })
            val obj_2 = obj_1.update("@extensible", PropValue(BoolFalse))
            _h.update(l, obj_2)
          })
          val (h_e, ctx_e) = PreHelper.RaiseException(h_1, ctx, PureLocalLoc, es)
          if (Value(v._2) </ ValueBot)
            ((PreHelper.ReturnStore(h_e, PureLocalLoc, Value(v._2)), ctx_e), (he+h_e, ctxe+ctx_e))
          else
            ((h_1, ctx), (he+h_e, ctxe+ctx_e))
        })),
      ("Object.freeze" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)

          val v = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val h_1 = v._2.foldLeft(h)((_h, l) => {
            val obj = _h(l)
            val obj_1 = obj.getProps.foldLeft(obj)((_o, s) => {
              val ov = _o(s)._1
              _o.update(s, PropValue(ObjectValue(ov._1,BoolFalse,ov._3,BoolFalse)))
            })
            val obj_2 = obj_1.update("@extensible", PropValue(BoolFalse))
            _h.update(l, obj_2)
          })
          val (h_e, ctx_e) = PreHelper.RaiseException(h_1, ctx, PureLocalLoc, es)
          if (Value(v._2) </ ValueBot)
            ((PreHelper.ReturnStore(h_e, PureLocalLoc, Value(v._2)), ctx_e), (he+h_e, ctxe+ctx_e))
          else
            ((h_1, ctx), (he+h_e, ctxe+ctx_e))
        })),
      ("Object.preventExtensions" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)

          val v = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val h_1 = v._2.foldLeft(h)((_h, l) =>
            _h.update(l, _h(l).update("@extensible", PropValue(BoolFalse))))
          val (h_e, ctx_e) = PreHelper.RaiseException(h_1, ctx, PureLocalLoc, es)
          if (Value(v._2) </ ValueBot)
            ((PreHelper.ReturnStore(h_e, PureLocalLoc, Value(v._2)), ctx_e), (he+h_e, ctxe+ctx_e))
          else
            ((h_1, ctx), (he+h_e, ctxe+ctx_e))
        })),
      ("Object.isSealed" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)

          val v = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val b = v._2.foldLeft[AbsBool](BoolBot)((_b, l) => {
            val o = h(l)
            val props = o.getProps
            val b_f =
              if (props.exists((s) => BoolTrue <= o(s)._1._4))
                BoolFalse
              else  BoolBot
            val b_t =
              if (props.forall((s) => BoolFalse <= o(s)._1._4)) {
                val v_ex = o("@extensible")._2
                if (Value(BoolTop) <= v_ex)  BoolTop
                else if (Value(BoolFalse) <= v_ex) BoolTrue
                else if (Value(BoolTrue) <= v_ex) BoolFalse
                else BoolBot
              }
              else
                BoolBot
            _b + b_f + b_t
          })
          val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)
          if (b </ BoolBot)
            ((PreHelper.ReturnStore(h_e, PureLocalLoc, Value(b)), ctx_e), (he+h_e, ctxe+ctx_e))
          else
            ((h, ctx), (he+h_e, ctxe+ctx_e))
        })),
      ("Object.isFrozen" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)

          val v = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val b = v._2.foldLeft[AbsBool](BoolBot)((_b, l) => {
            val o = h(l)
            val props = o.getProps
            val b_f =
              if (props.exists((s) => (BoolTrue <= o(s)._1._2 || BoolTrue <= o(s)._1._4)))
                BoolFalse
              else
                BoolBot
            val b_t =
              if (props.forall((s) => (BoolFalse <= o(s)._1._2 && BoolFalse <= o(s)._1._4))) {
                val v_ex = o("@extensible")._2
                if (Value(BoolTop) <= v_ex)  BoolTop
                else if (Value(BoolFalse) <= v_ex) BoolTrue
                else if (Value(BoolTrue) <= v_ex) BoolFalse
                else BoolBot
              }
              else
                BoolBot
            _b + b_f + b_t
          })
          val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)
          if (b </ BoolBot)
            ((PreHelper.ReturnStore(h_e, PureLocalLoc, Value(b)), ctx_e), (he+h_e, ctxe+ctx_e))
          else
            ((h, ctx), (he+h_e, ctxe+ctx_e))
        })),
      ("Object.isExtensible" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)

          val v = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val v_ex = v._2.foldLeft[Value](ValueBot)((_v, l) =>
            _v + h(l)("@extensible")._2)
          val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)
          if (v </ ValueBot)
            ((PreHelper.ReturnStore(h_e, PureLocalLoc, v_ex), ctx_e), (he+h_e, ctxe+ctx_e))
          else
            ((h, ctx), (he+h_e, ctxe+ctx_e))
        })),
      ("Object.keys" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_env = h(PureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, addr1)
          val v = getArgValue_pre(h_1, ctx_1, args, "0", PureLocalLoc)
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val o = v._2.foldLeft(Obj.bottom)((_o, l) => {
            val map_enum = h_1(l).getProps.filter((kv)=> BoolTrue <= h_1(l)(kv)._1._3)
            val o_new = PreHelper.NewArrayObject(AbsNumber.alpha(map_enum.size))
            map_enum.foldLeft(o_new)((_o, kv) => _o.update(NumStr, PropValue(ObjectValue(AbsString.alpha(kv),BoolTrue,BoolTrue,BoolTrue))))
          })
          val (h_e, ctx_e) = PreHelper.RaiseException(h_1, ctx_1, PureLocalLoc, es)
          if (o </ Obj.bottom) {
            val h_2 = h_e.update(l_r, o)
            ((PreHelper.ReturnStore(h_2, PureLocalLoc, Value(l_r)), ctx_1), (he+h_e, ctxe+ctx_e))
          }
          else
            ((h_1, ctx_1), (he+h_e, ctxe+ctx_e))
        })),
      ("Object.prototype.toString"-> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._2._2
          val s = lset_this.foldLeft[AbsString](StrBot)((_s, l) => {
            val absstr = h(l)("@class")._2._1._5
            _s + (absstr.getAbsCase match {
              case AbsSingle =>
                AbsString.alpha("[object " + absstr.getSingle.get + "]")
              case AbsBot =>
                StrBot
              case _ =>
                OtherStr
                })})
          if (s </ StrBot)
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(s)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("Object.prototype.toLocaleString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._2._2
          val s = lset_this.foldLeft[AbsString](StrBot)((_s, l) => {
            val absstr = h(l)("@class")._2._1._5
            _s + (absstr.getAbsCase match {
              case AbsSingle =>
                AbsString.alpha("[object " + absstr.getSingle.get + "]")
              case AbsBot =>
                StrBot
              case _ =>
                OtherStr
                })})
          if (s </ StrBot)
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(s)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("Object.prototype.valueOf" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._2._2
          if (Value(lset_this) </ ValueBot)
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(lset_this)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("Object.prototype.hasOwnProperty" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._2._2
          // 15.2.4.5 Object.prototype.hasOwnProperty(V)
          val s = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          val b = lset_this.foldLeft[AbsBool](BoolBot)((b,l) => {
            b + PreHelper.HasOwnProperty(h, l, s)
          })
          if (b </ BoolBot)
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(b)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("Object.prototype.isPrototypeOf" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._2._2
          val v = getArgValue_pre(h, ctx, args, "0", PureLocalLoc)
          val b_1 =
            if (v._1 </ PValueBot)
              BoolFalse
            else
              BoolBot
          val b_2 = v._2.foldLeft[AbsBool](BoolBot)((b,l) => {
            val v_proto = h(l)("@proto")._1._1
            val b_3 =
              if (NullTop <= v_proto._1._2)
                BoolFalse
              else
                BoolBot
            val b_4 = Operator.bopEq(Value(lset_this), Value(v_proto._2))._1._3
            b + b_3 + b_4})
          val b = b_1 + b_2
          if (b </ BoolBot)
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(b)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("Object.prototype.propertyIsEnumerable" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._2._2
          val s = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          val b =
            lset_this.foldLeft[AbsBool](BoolBot)((_b, l) => {
              val ov = h(l)(s)._1
              val b_1 =
                if (UndefTop <= ov._1._1._1)
                  BoolFalse
                else
                  BoolBot
              val b_2 =
                ov._3
              _b + b_1 + b_2
            })
          if (b </ BoolBot)
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(b)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        }))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      ("Object" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          //val addr_env = set_addr.head
          //val addr1 = cfg.getAPIAddress(addr_env, 0)
          val v = getArgValue(h, ctx, args, "0") // [value]
          val (lpset1, es) =
            if ((v._1._3 </ BoolBot) || (v._1._4 </ NumBot) || (v._1._5 </ StrBot)) {
              val _es =
                if (!(v._1._1 <= UndefBot) || !(v._1._2 <= NullBot)) Set[Exception](TypeError)
                else ExceptionBot
              val _v_new = Value(PValue(UndefBot, NullBot, v._1._3, v._1._4, v._1._5))
              (set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.toObject_def(h,ctx, _v_new , cfg.getAPIAddress((fid, a), 0))), _es)
            } else (LPBot, ExceptionBot)
          val LP2 =
            if ((v._1._1 </ UndefBot) || (v._1._2 </ NullBot)) {
              //val _l_r = addrToLoc(addr1, Recent)
              val LP2_1 = set_addr.foldLeft(LPBot)((lp,a) => lp ++ AH.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 0)))
              val LP2_2 = AH.NewObject_def.foldLeft(LPBot)((S,p) =>
                S ++ set_addr.foldLeft(LPBot)((lp,a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 0), Recent), p)))
              //AH.Oldify_def(h, ctx, addr1) ++
              // AH.NewObject_def.foldLeft(LPBot)((S,p) => S + ((_l_r, p)))
              LP2_1 ++ LP2_2
            } else LPBot
          val LP_3 = LPSet((SinglePureLocalLoc, "@return"))
          val LP_4 = AH.RaiseException_def(es)
          lpset1 ++ LP2 ++ LP_3 ++ LP_4
        })),
      ("Object.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val v = getArgValue(h, ctx, args, "0") // [value]
          val (lpset1, es) =
            if ((v._1._3 </ BoolBot) || (v._1._4 </ NumBot) || (v._1._5 </ StrBot)) {
              val _es =
                if (!(v._1._1 <= UndefBot) || !(v._1._2 <= NullBot)) Set[Exception](TypeError)
                else ExceptionBot
              val _v_new = Value(PValue(UndefBot, NullBot, v._1._3, v._1._4, v._1._5))
              val props1 =
                if (_v_new._1._5 </ StrBot) AH.NewString_def(v._1._5)
                else Set()
              val props2 =
                if (_v_new._1._3 </ BoolBot) AH.NewBoolean_def
                else Set()
              val props3 =
                if (_v_new._1._4 </ NumBot) AH.NewNumber_def
                else Set()
              val props = props1 ++ props2 ++ props3
              val lpset =
                lset_this.foldLeft(LPBot)((lpset, l) => lpset ++ props.foldLeft(lpset)((_lpset, p) => _lpset + (l, p)))
              (lpset, _es)
            } else (LPBot, ExceptionBot)
          val LP2 =
            if ((v._1._1 </ UndefBot) || (v._1._2 </ NullBot)) {
              lset_this.foldLeft(LPBot)((lpset, l) => lpset ++ AH.NewObject_def.foldLeft(lpset)((_lpset, p) => _lpset + (l, p)))
            } else LPBot

          val LP_3 = LPSet((SinglePureLocalLoc, "@return"))
          val LP_4 = AH.RaiseException_def(es)
          lpset1 ++ LP2 ++ LP_3 ++ LP_4
        })),
      ("Object.getPrototypeOf" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val es =
            if (v_1._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val v_2 = v_1._2.foldLeft(ValueBot)(
            (_v, l) => _v + h(l)("@proto")._1._1)
          val LP1 = AH.RaiseException_def(es)
          if (v_2 </ ValueBot) LP1 ++ LPSet((SinglePureLocalLoc, "@return"))
          else LP1
        })),
      ("Object.getOwnPropertyDescriptor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          //val addr_env = set_addr.head
          //val addr1 = cfg.getAPIAddress(addr_env, 0)
          //val l_r = addrToLoc(addr1, Recent)
          val v_1 = getArgValue(h, ctx, args, "0")
          val es =
            if (v_1._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          //val LP1 = AH.Oldify_def(h, ctx, addr1)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 0)))
          //val LP2 = LPSet((l_r, "value")) + (l_r, "writable") + (l_r, "enumerable") + (l_r, "configurable"))
          val LP2 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp + (addrToLoc(cfg.getAPIAddress((fid, a), 0), Recent), "value")
              + (addrToLoc(cfg.getAPIAddress((fid, a), 0), Recent), "writable")
              + (addrToLoc(cfg.getAPIAddress((fid, a), 0), Recent), "enumerable")
              + (addrToLoc(cfg.getAPIAddress((fid, a), 0), Recent), "configurable"))
          val LP3 = AH.RaiseException_def(es)
          LP1 ++ LP2 ++ LP3 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.getOwnPropertyNames" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          //val addr_env = set_addr.head
          //val addr1 = cfg.getAPIAddress(addr_env, 0)
          //val l_r = addrToLoc(addr1, Recent)
          //val LP1 = AH.Oldify_def(h, ctx, addr1)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 0)))
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP2 = v._2.foldLeft(LPBot)((lpset, l) =>
            lpset ++ AH.NewArrayObject_def.foldLeft(LPBot)((_lpset, p) =>
                _lpset ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 0), Recent), p)))
              ++ h(l).getProps.foldLeft((LPBot, 0))((_lpset_n, p) =>
              (_lpset_n._1 + (l, _lpset_n._2.toString), _lpset_n._2 + 1))._1)
          val LP3 = AH.RaiseException_def(es)
          LP1 ++ LP2 ++ LP3 + (SinglePureLocalLoc, "@return")
        })),
      "Object.create" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          //val addr_env = set_addr.head
          //val addr1 = cfg.getAPIAddress(addr_env, 0)
          //val l_r = addrToLoc(addr1, Recent)
          //val LP1 = AH.Oldify_def(h, ctx, addr1)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 0)))
          val v_1 = getArgValue(h, ctx, args, "0")
          val es_1 =
            if (v_1._1._1 </ UndefBot || v_1._1._3 </ BoolBot || v_1._1._4 </ NumBot || v_1._1._5 </ StrBot) Set[Exception](TypeError)
            else ExceptionBot

          val v_2 = getArgValue(h, ctx, args, "1")
          val es_2 =
            if (v_2._1._2 </ NullBot || v_2._1._3 </ BoolBot || v_2._1._4 </ NumBot || v_2._1._5 </ StrBot) Set[Exception](TypeError)
            else ExceptionBot

          //val LP2 = AH.NewObject_def.foldLeft(LPBot)((lpset, p) => lpset + (l_r, p))
          val LP2 = AH.NewObject_def.foldLeft(LPBot)((lpset, p) =>
            lpset ++ set_addr.foldLeft(LPBot)((lp, a) => lp +(addrToLoc(cfg.getAPIAddress((fid, a), 0), Recent), p)))
          val LP3 =
            if (!v_2._2.isEmpty)
                v_2._2.foldLeft(LPBot)((lpset, l) =>
                  lpset ++ set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.DefineProperties_def(h, addrToLoc(cfg.getAPIAddress((fid, a), 0), Recent), l)))
            else LPBot

          val LP4 = AH.RaiseException_def(es_1 ++ es_2)
          LP1 ++ LP2 ++ LP3 ++ LP4 +(SinglePureLocalLoc, "@return")
        }),
      ("Object.defineProperty" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val es_1 =
            if (v_1._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val s_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          val v_2 = getArgValue(h, ctx, args, "2")
          val es_2 =
            if (v_2._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP1 = v_1._2.foldLeft(LPBot)((lpset, l_1) =>
            lpset ++ v_2._2.foldLeft(LPBot)((_lpset, l_2) =>
              _lpset ++ AH.DefineProperty_def(h, l_1, s_name, l_2)) )
          val LP2 = AH.RaiseException_def(es_1 ++ es_2)
          LP1 ++ LP2 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.defineProperties" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val es_1 =
            if (v_1._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val v_2 = getArgValue(h, ctx, args, "1")
          val es_2 =
            if (v_2._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP1 = v_1._2.foldLeft(LPBot)((lpset, l_1) =>
            lpset ++ v_2._2.foldLeft(LPBot)((_lpset, l_2) => _lpset ++ AH.DefineProperties_def(h, l_1, l_2)))

          val LP2 = AH.RaiseException_def(es_1 ++ es_2)
          LP1 ++ LP2 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.seal" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP1 = v._2.foldLeft(LPBot)((lpset, l) => {
            val _LP1 = h(l).getProps.foldLeft(LPBot)((_lpset, p) => _lpset + (l, p))
            lpset ++ _LP1 + (l, "@extensible")
          })
          val LP2 = AH.RaiseException_def(es)
          LP1 ++ LP2 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.freeze" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP1 = v._2.foldLeft(LPBot)((lpset, l) => {
            val _LP1 = h(l).getProps.foldLeft(LPBot)((_lpset, p) => _lpset + (l, p))
            lpset ++ _LP1 + (l, "@extensible")
          })
          val LP2 = AH.RaiseException_def(es)
          LP1 ++ LP2 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.preventExtensions" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP1 = v._2.foldLeft(LPBot)((lpset, l) => lpset + (l, "@extensible"))
          val LP2 = AH.RaiseException_def(es)
          LP1 ++ LP2 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.isSealed" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          AH.RaiseException_def(es) + (SinglePureLocalLoc, "@return")
        })),
      ("Object.isFrozen" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          AH.RaiseException_def(es) + (SinglePureLocalLoc, "@return")
        })),
      ("Object.isExtensible" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          AH.RaiseException_def(es) + (SinglePureLocalLoc, "@return")
        })),
      ("Object.keys" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          //val addr_env = set_addr.head
          //val addr1 = cfg.getAPIAddress(addr_env, 0)
          //val l_r = addrToLoc(addr1, Recent)
          //val LP1 = AH.Oldify_def(h, ctx, addr1)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.Oldify_def(h,ctx, cfg.getAPIAddress((fid, a), 0)))
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP2 = v._2.foldLeft(LPBot)((lpset, l) =>
            lpset ++ AH.NewArrayObject_def.foldLeft(LPBot)((_lpset, p) =>
              _lpset ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 0), Recent), p)))
              ++ h(l).getProps.foldLeft((LPBot, 0))((_lpset_n, p) =>
              (_lpset_n._1 + (l, _lpset_n._2.toString), _lpset_n._2 + 1))._1)
          val LP3 = AH.RaiseException_def(es)
          LP1 ++ LP2 ++ LP3 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.prototype.toString" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Object.prototype.toLocaleString" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Object.prototype.valueOf" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Object.prototype.hasOwnProperty" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Object.prototype.isPrototypeOf" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Object.prototype.propertyIsEnumerable" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      ("Object" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          //val addr_env = set_addr.head
          //val addr1 = cfg.getAPIAddress(addr_env, 0)
          val v = getArgValue(h, ctx, args, "0") // [value]
          val LP1 = getArgValue_use(h, ctx, args, "0")
          val (lpset2, es) =
            if ((v._1._3 </ BoolBot) || (v._1._4 </ NumBot) || (v._1._5 </ StrBot)) {
              val _es =
                if (!(v._1._1 <= UndefBot) || !(v._1._2 <= NullBot)) Set[Exception](TypeError)
                else ExceptionBot
              val _v_new = Value(PValue(UndefBot, NullBot, v._1._3, v._1._4, v._1._5))
              //(AH.toObject_use(h,ctx, _v_new, addr1), _es)
              (set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.toObject_use(h,ctx, _v_new, cfg.getAPIAddress((fid, a), 0))), _es)
            } else (LPBot, ExceptionBot)
          val LP3 =
            if ((v._1._1 </ UndefBot) || (v._1._2 </ NullBot)) {
              //AH.Oldify_use(h, ctx, addr1)
              set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.Oldify_use(h,ctx, cfg.getAPIAddress((fid, a), 0)))
            } else LPBot
          val LP4 = LPSet((SinglePureLocalLoc, "@return"))
          val LP5 = AH.RaiseException_use(es)
          LP1 ++ lpset2 ++ LP3 ++ LP4 ++ LP5
        })),
      ("Object.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          /* may def */
          val v = getArgValue(h, ctx, args, "0") // [value]
          val LP1 = getArgValue_use(h, ctx, args, "0")
          val (lpset2, es) =
            if ((v._1._3 </ BoolBot) || (v._1._4 </ NumBot) || (v._1._5 </ StrBot)) {
              val _es =
                if (!(v._1._1 <= UndefBot) || !(v._1._2 <= NullBot)) Set[Exception](TypeError)
                else ExceptionBot
              val _v_new = Value(PValue(UndefBot, NullBot, v._1._3, v._1._4, v._1._5))
              val props1 =
                if (_v_new._1._5 </ StrBot) AH.NewString_def(v._1._5)
                else Set()
              val props2 =
                if (_v_new._1._3 </ BoolBot) AH.NewBoolean_def
                else Set()
              val props3 =
                if (_v_new._1._4 </ NumBot) AH.NewNumber_def
                else Set()
              val props = props1 ++ props2 ++ props3
              val lpset =
                lset_this.foldLeft(LPBot)((lpset, l) => lpset ++ props.foldLeft(lpset)((_lpset, p) => _lpset + (l, p)))
              (lpset, _es)
            } else (LPBot, ExceptionBot)
          val LP3 =
            if ((v._1._1 </ UndefBot) || (v._1._2 </ NullBot)) {
              lset_this.foldLeft(LPBot)((lpset, l) => lpset ++ AH.NewObject_def.foldLeft(lpset)((_lpset, p) => _lpset + (l, p)))
            } else LPBot

          val LP4 = LPSet((SinglePureLocalLoc, "@return"))
          val LP5 = AH.RaiseException_use(es)
          LP1 ++ lpset2 ++ LP3 ++ LP4 ++ LP5 + (SinglePureLocalLoc, "@this")
        })),
      ("Object.getPrototypeOf" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val LP1 = getArgValue_use(h, ctx, args, "0")
          val es =
            if (v_1._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP2 = v_1._2.foldLeft(LPBot)((lpset, l) => lpset + (l, "@proto"))
          val LP3 = AH.RaiseException_use(es)
          LP1 ++ LP2 ++ LP3 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.getOwnPropertyDescriptor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          //val addr_env = set_addr.head
          //val addr1 = cfg.getAPIAddress(addr_env, 0)
          val v_1 = getArgValue(h, ctx, args, "0")
          val s_prop = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          val LP1 = getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1")
          val es =
            if (v_1._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP2 = v_1._2.foldLeft(LPBot)((lpset, l) => lpset ++ AH.absPair(h, l, s_prop))
          //val LP3 = AH.Oldify_use(h, ctx, addr1)
          val LP3 = set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 0)))
          val LP4 = AH.RaiseException_use(es)
          LP1 ++ LP2 ++ LP3 ++ LP4 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.getOwnPropertyNames" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          //val addr_env = set_addr.head
          //val addr1 = cfg.getAPIAddress(addr_env, 0)
          //val l_r = addrToLoc(addr1, Recent)
          //val LP1 = AH.Oldify_use(h, ctx, addr1)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 0)))
          val v = getArgValue(h, ctx, args, "0")
          val LP2 = getArgValue_use(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP3 = v._2.foldLeft(LPBot)((lpset, l) => {
            val props = h(l).getProps
            lpset ++ props.foldLeft(LPBot)((_lpset, p) => _lpset + (l, p)) + (l, Str_default_number) + (l, Str_default_number)
          })
          val LP4 = AH.RaiseException_use(es)
          LP1 ++ LP2 ++ LP3 ++ LP4 + (SinglePureLocalLoc, "@return")
        })),
      "Object.create" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          //val addr_env = set_addr.head
          //val addr1 = cfg.getAPIAddress(addr_env, 0)
          //val l_r = addrToLoc(addr1, Recent)
          //val LP1 = AH.Oldify_use(h, ctx, addr1)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 0)))
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 = getArgValue(h, ctx, args, "1")
          val LP2 = getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1")
          val es_1 =
            if (v_1._1._1 </ UndefBot || v_1._1._3 </ BoolBot || v_1._1._4 </ NumBot || v_1._1._5 </ StrBot) Set[Exception](TypeError)
            else ExceptionBot
          val es_2 =
            if (v_2._1._2 </ NullBot || v_2._1._3 </ BoolBot || v_2._1._4 </ NumBot || v_2._1._5 </ StrBot) Set[Exception](TypeError)
            else ExceptionBot

          //val LP3 = v_2._2.foldLeft(LPBot)((lpset, l_2) => lpset ++ AH.DefineProperties_use(h, l_r, l_2))
          val LP3 = v_2._2.foldLeft(LPBot)((lpset, l_2) =>
            lpset ++ set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.DefineProperties_use(h, addrToLoc(cfg.getAPIAddress((fid, a), 0), Recent), l_2)))
          val LP4 = AH.RaiseException_use(es_1 ++ es_2)
          LP1 ++ LP2 ++ LP3 ++ LP4 +(SinglePureLocalLoc, "@return")
        }),
      ("Object.defineProperty" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val s_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          val v_2 = getArgValue(h, ctx, args, "2")
          val LP1 = getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1") ++ getArgValue_use(h, ctx, args, "2")
          val es_1 =
            if (v_1._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val es_2 =
            if (v_2._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP2 =
            v_1._2.foldLeft(LPBot)((lpset, l_1) =>
              lpset ++ v_2._2.foldLeft(LPBot)((_lpset, l_2) =>
                _lpset ++ AH.DefineProperty_use(h, l_1, s_name, l_2)++ AH.DefineProperty_def(h, l_1, s_name, l_2)) )
          val LP3 = AH.RaiseException_use(es_1 ++ es_2)
          LP1 ++ LP2 ++ LP3 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.defineProperties" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 = getArgValue(h, ctx, args, "1")
          val LP1 = getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1")
          val es_1 =
            if (v_1._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val es_2 =
            if (v_2._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP2 =
            v_1._2.foldLeft(LPBot)((lpset, l_1) =>
              lpset ++ v_2._2.foldLeft(LPBot)((_lpset, l_2) =>
                _lpset ++ AH.DefineProperties_use(h, l_1, l_2) ++ AH.DefineProperties_def(h, l_1, l_2)))
          val LP3 = AH.RaiseException_use(es_1 ++ es_2)
          LP1 ++ LP2 ++ LP3 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.seal" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 = getArgValue_use(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP2 = v._2.foldLeft(LPBot)((lpset, l) =>
            lpset ++ h(l).getProps.foldLeft(LPBot)((_lpset, p) => _lpset + (l, p)) + (l, "@extensible"))
          val LP3 = AH.RaiseException_use(es)
          LP1 ++ LP2 ++ LP3 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.freeze" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 = getArgValue_use(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP2 = v._2.foldLeft(LPBot)((lpset, l) =>
            lpset ++ h(l).getProps.foldLeft(LPBot)((_lpset, p) => _lpset + (l, p)) + (l, "@extensible"))
          val LP3 = AH.RaiseException_use(es)
          LP1 ++ LP2 ++ LP3 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.preventExtensions" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 = getArgValue_use(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP2 = v._2.foldLeft(LPBot)((lpset, l) => lpset + (l, "@extensible"))
          val LP3 = AH.RaiseException_use(es)
          LP1 ++ LP2 ++ LP3 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.isSealed" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 = getArgValue_use(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP2 = v._2.foldLeft(LPBot)((lpset, l) => {
            val props = h(l).getProps
            lpset ++ props.foldLeft(LPBot)((_lpset, p) => _lpset + (l, p)) + (l, "@extensible")
          })
          val LP3 = AH.RaiseException_use(es)
          LP1 ++ LP2 ++ LP3 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.isFrozen" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 = getArgValue_use(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP2 = v._2.foldLeft(LPBot)((lpset, l) => {
            val props = h(l).getProps
            lpset ++ props.foldLeft(LPBot)((_lpset, p) => _lpset + (l, p)) + (l, "@extensible")
          })
          val LP3 = AH.RaiseException_use(es)
          LP1 ++ LP2 ++ LP3 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.isExtensible" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 = getArgValue_use(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP2 = v._2.foldLeft(LPBot)((lpset, l) => lpset + (l, "@extensible"))
          val LP3 = AH.RaiseException_use(es)
          LP1 ++ LP2 ++ LP3 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.keys" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          //val addr_env = set_addr.head
          //val addr1 = cfg.getAPIAddress(addr_env, 0)
          //val l_r = addrToLoc(addr1, Recent)
          //val LP1 = AH.Oldify_use(h, ctx, addr1)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.Oldify_use(h,ctx, cfg.getAPIAddress((fid, a), 0)))
          val v = getArgValue(h, ctx, args, "0")
          val LP2 = getArgValue_use(h, ctx, args, "0")
          val es =
            if (v._1 </ PValueBot) Set[Exception](TypeError)
            else ExceptionBot
          val LP3 = v._2.foldLeft(LPBot)((lpset, l) => {
            val props = h(l).getProps
            lpset ++ props.foldLeft(LPBot)((_lpset, p) => _lpset + (l, p)) + (l, Str_default_number) + (l, Str_default_number)
          })
          val LP4 = AH.RaiseException_use(es)
          LP1 ++ LP2 ++ LP3 ++ LP4 + (SinglePureLocalLoc, "@return")
        })),
      ("Object.prototype.toString" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) => lpset + (l, "@class"))
          LP1 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
        })),
      ("Object.prototype.toLocaleString" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) => lpset + (l, "@class"))
          LP1 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
        })),
      ("Object.prototype.valueOf" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return")) + (SinglePureLocalLoc, "@this")
        })),
      ("Object.prototype.hasOwnProperty" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val v = getArgValue(h, ctx, args, "0") // V
          val p = Helper.toString(Helper.toPrimitive_better(h, v))

          val LP_1 = getArgValue_use(h, ctx, args, "0")
          val LP_2 = lset_this.foldLeft(LPBot)((S, l) => S ++ AH.HasOwnProperty_use(h, l, p))

          LP_1 ++ LP_2 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
        })),
      ("Object.prototype.isPrototypeOf" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val v = getArgValue(h, ctx, args, "0")
          val LP1 = getArgValue_use(h, ctx, args, "0")
          val LP2 = v._2.foldLeft(LPBot)((lpset, l) => lpset + (l, "@proto"))
          LP1 ++ LP2 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
        })),
      ("Object.prototype.propertyIsEnumerable" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val s = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx,  args, "0")))
          val LP1 = getArgValue_use(h, ctx, args, "0")
          val LP2 = lset_this.foldLeft(LPBot)((lpset, l) => lpset ++ AH.absPair(h, l, s))
          LP1 ++ LP2 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
        }))
    )
  }
}
