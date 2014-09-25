/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing

import scala.collection.immutable.HashSet
import kr.ac.kaist.jsaf.analysis.cfg.{CFGId, FunctionId, GlobalVar, PureLocalVar, CapturedVar, CapturedCatchVar, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolTrue => BT, BoolFalse => BF}
import kr.ac.kaist.jsaf.analysis.typing.models.builtin._
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object PreHelper {
  def VarStore(h: Heap, PureLocalLoc: Loc, id: CFGId, v: Value): Heap = {
    val x = id.getText
    id.getVarKind match {
      case PureLocalVar =>
        h.update(PureLocalLoc, h(PureLocalLoc).update(x,
          PropValue(ObjectValue(v, BoolBot, BoolBot, BoolFalse))))
      case CapturedVar =>
        h(PureLocalLoc)("@env")._2._2.foldLeft(h)((hh, l) => {
          VarStoreL(hh, l, x, v)
        })
      case CapturedCatchVar =>
        h.update(CollapsedLoc, h(CollapsedLoc).update(x,
          PropValue(ObjectValue(v, BoolBot, BoolBot, BoolFalse))))
      case GlobalVar => {
        val h_1 =
          if (BoolTrue <= CanPutVar(h, x))
            VarStoreG(h, x, v)
          else
            h
        h_1
      }
    }
  }

  def VarStoreL(h: Heap, l: Loc, x: String, v: Value): Heap = {
    var visited = LocSetBot
    def visit(h: Heap, l: Loc): Heap = {
      if (visited.contains(l)) h
      else {
        visited += l
        val env = h(l)
        val has_x = env.domIn(x)
        val h_1 =
          if (BoolTrue <= has_x) {
            env(x)._1._2.getPair match {
              case (AbsSingle, Some(b)) => 
                if (b) {
                  val pv = PropValue(ObjectValue(v, BoolTrue, BoolBot, BoolFalse))
                  h.update(l, env.update(x, pv))
                } else h
              case _ =>
                throw new InternalError("Writable attribute must be exact for variables in local env.")
            }
          } else {
            h
          }
        val h_2 =
          if (BoolFalse <= has_x) {
            val lset_outer = env("@outer")._2._2
            lset_outer.foldLeft(h_1)((hh, l_outer) => visit(hh, l_outer))
          } else {
            h_1
          }
        h_2
      }
    }
    
    visit(h, l)
  }
  
  def VarStoreG(h: Heap, x: String, v: Value) = {
    val l_g = GlobalLoc
    // case 1
    val h_1 = if (BoolFalse <= h(l_g).domIn(x)) {
      PropStore(h, l_g, AbsString.alpha(x), v)
    } else { h }

    // case 2
    val h_2 = if (BoolTrue <= h_1(l_g).domIn(x)) {
      val ov_old = h_1(l_g)(x)._1
      val ov_new = ObjectValue(ov_old._1 + v, ov_old._2, ov_old._3, ov_old._4)
      val o = h_1(l_g).update(x, PropValue(ov_new))
      h_1.update(l_g, o)
    } else { h_1 }
    h_2
  }

  def PropStore(h: Heap, l: Loc, s: AbsString, v: Value): Heap = {
    val test = h(l).domIn(s)
    val h_1 =
      if (BoolFalse <= test) {
        h.update(l, h(l).update(s, PropValue(ObjectValue(v,BoolTrue,BoolTrue,BoolTrue))))
      }
      else
        h
    val h_2 =
      if (BoolTrue <= test) {
        val ov_old = h_1(l)(s)._1
        h_1.update(l, h_1(l).update(s, PropValue(ObjectValue(v, ov_old._2, ov_old._3, ov_old._4))))
      }
      else
        h_1
    h_2
  }

  def ReturnStore(h: Heap, PureLocalLoc: Loc, v: Value):Heap = {
    h.update(PureLocalLoc, h(PureLocalLoc).update("@return", PropValue(v)))
  }

  def Delete(h: Heap, l: Loc, s: AbsString): (Heap, AbsBool) = {
    val (h_1, b_1) =
      if ((BoolTrue <= HasOwnProperty(h, l, s)) && (BoolFalse <= h(l)(s)._1._4))
        (h, BoolFalse)
      else
        (h, BoolBot)
    val (h_2, b_2) =
      if (  (BoolTrue <= HasOwnProperty(h_1, l, s) && BoolFalse != h_1(l)(s)._1._4)// BoolBot should be included because @exception property also could be deleted.
          || BoolFalse <= HasOwnProperty(h_1, l, s)) {
        (h_1.update(l, h_1(l) - s), BoolTrue)
//        (PropStore(_h, l,  AbsString.alpha("length"), Value(UInt)), BoolTrue)
      }
      else
        (h_1, BoolBot)
    (h_2, b_1 + b_2)
  }

  def toObject(h: Heap, ctx: Context, v: Value, a_new: Address): (Value, Heap, Context, Set[Exception]) = {
    // 9.9 ToObject
    val lset = v._2

    val o_1 =
      if (!(v._1._5 <= StrBot)) NewString(v._1._5)
      else Obj.bottom
    val o_2 =
      if (!(v._1._3 <= BoolBot)) NewBoolean(v._1._3)
      else Obj.bottom
    val o_3 =
      if (!(v._1._4 <= NumBot)) NewNumber(v._1._4)
      else Obj.bottom
    val es =
      if (!(v._1._1 <= UndefBot) || !(v._1._2 <= NullBot)) Set[Exception](TypeError)
      else Set[Exception]()
    val o = o_1 + o_2 + o_3

    val (h_1, _ctx_1) = Oldify(h, ctx, a_new)
    val (h_2, lset_2, ctx_1) =
      if (o </ Obj.bottom) {
        val l_r = addrToLoc(a_new, Recent)
        (h_1.update(l_r, o), lset + l_r, _ctx_1)
      } else {
        (h_1, lset, _ctx_1)
      }
    (Value(lset_2), h_2, ctx_1, es)
  }

  def allocObject(h: Heap, ls_v: LocSet, l_r: Loc) = {
    val o_new = ls_v.foldLeft[Obj](Obj.bottom)((obj,l_p) => obj + NewObject(l_p))
    val h_2 = h.update(l_r, o_new)
      h_2
  }

  def NewFunctionObject(fid: Option[FunctionId], cid: Option[FunctionId], env: Value,
                        l: Option[Loc], w: AbsBool, e: AbsBool, c: AbsBool, n: AbsNumber): Obj = {
    val o_1 = Obj.empty.
      update("@class", PropValue(AbsString.alpha("Function"))).
      update("@proto", PropValue(ObjectValue(Value(FunctionProtoLoc), BoolFalse, BoolFalse, BoolFalse))).
      update("@extensible", PropValue(BoolTrue)).
      update("@scope", PropValue(env)).
      update("length", PropValue(ObjectValue(n, BoolFalse, BoolFalse, BoolFalse)), exist = true)
    val o_2 = fid match {
      case Some(id) => o_1.update("@function", PropValue(ObjectValueBot, FunSet(id)))
      case None => o_1
    }
    val o_3 = cid match {
      case Some(id) => o_2.update("@construct", PropValue(ObjectValueBot, FunSet(id)))
      case None => o_2
    }
    val o_4 = l match {
      case Some(loc) => o_3.update("@hasinstance", PropValueNullTop)
      case None => o_3
    }

    val o_5 = l match {
      case Some(loc) => o_4.update("prototype", PropValue(ObjectValue(Value(loc), w, e, c)), exist = true)
      case None => o_4
    }
    o_5
  }

  def Proto(h: Heap, l: Loc, s: AbsString): Value  = {
    var visited = LocSetBot
    def _Proto(h: Heap, l: Loc, s: AbsString): Value  = {
      if(visited(l)) ValueBot
      else {
        visited = visited + l
        val test = h(l).domIn(s)
        val v_1 =
          if (BoolTrue <= test) {
            val v= h(l)(s)._1._1
            v
          }
          else
            ValueBot
        val v_2 =
          if (BoolFalse <= test) {
            val v_proto = h(l)("@proto")._1._1
            val v_3 =
              if (v_proto._1._2 </ NullBot)
                Value(PValue(UndefTop))
              else
                ValueBot
            v_3 + (v_proto._2.foldLeft(ValueBot)((v,l_proto) => v + _Proto(h, l_proto, s)))
          }
          else
            ValueBot
        v_1 + v_2
      }
    }
    _Proto(h, l, s)
    // val v = _Proto(h, l, s)
    // if(v <= ValueBot) Value(UndefTop)
    // else v
  }

  def Proto(h: Heap, lset: LocSet, s: AbsString): Value  = {
    var visited = LocSetBot
    def _Proto(h: Heap, l: Loc, s: AbsString): Value  = {
      if(visited(l)) ValueBot
      else {
        visited = visited + l
        val test = h(l).domIn(s)
        val v_1 =
          if (BoolTrue <= test) {
            val v= h(l)(s)._1._1
            v
          }
          else
            ValueBot
        val v_2 =
          if (BoolFalse <= test) {
            val v_proto = h(l)("@proto")._1._1
            val v_3 =
              if (v_proto._1._2 </ NullBot)
                Value(PValue(UndefTop))
              else
                ValueBot
            v_3 + (v_proto._2.foldLeft(ValueBot)((v,l_proto) => v + _Proto(h, l_proto, s)))
          }
          else
            ValueBot
        v_1 + v_2
      }
    }
    lset.foldLeft(ValueBot)((v, l) => v + _Proto(h, l, s))
    // val v = _Proto(h, l, s)
    // if(v <= ValueBot) Value(UndefTop)
    // else v
  }

  def CanPutHelp(h: Heap, l_1: Loc, s: AbsString, l_2: Loc): AbsBool = {
    var visited = LocSetBot
    def _CanPutHelp(h: Heap, l_1: Loc, s: AbsString, l_2: Loc): AbsBool = {
      if(visited(l_1)) BoolBot
      else {
        visited = visited + l_1
        val b_1 =
          if (BoolFalse <= h(l_1).domIn(s)) {
            val v_proto = h(l_1)("@proto")._1._1
            val b_3 =
              if (v_proto._1._2 </ NullBot)
                h(l_2)("@extensible")._2._1._3
              else
                BoolBot
            b_3 + (v_proto._2.foldLeft(BoolBot: AbsBool)((b, l) => b + _CanPutHelp(h,l,s,l_2)))
          }
          else
            BoolBot
        val b_2 =
          if (BoolTrue <= h(l_1).domIn(s))
            h(l_1)(s)._1._2
          else
            BoolBot
        b_1 + b_2
      }
    }
    _CanPutHelp(h, l_1, s, l_2)
  }

  def HasProperty(h: Heap, l: Loc, s: AbsString): AbsBool = {
    var visited = LocSetBot
    def _HasProperty(h: Heap, l: Loc, s: AbsString): AbsBool = {
      if(visited(l)) BoolBot
      else {
        visited = visited + l
        val test = HasOwnProperty(h,l,s)
        val b_1 =
          if (BoolTrue <= test)
            BoolTrue
          else
            BoolBot
        val b_2 =
          if (BoolFalse <= test) {
            val v_proto = h(l)("@proto")._1._1
            val b_3 =
              if (v_proto._1._2 </ NullBot)
                BoolFalse
              else
                BoolBot
            b_3 + (v_proto._2.foldLeft[AbsBool](BoolBot)((b,l_proto) => b + _HasProperty(h,l_proto,s)))
          }
          else
            BoolBot
        b_1 + b_2
      }
    }
    _HasProperty(h, l, s)
  }

  def inherit(h: Heap, l_1: Loc, l_2: Loc): Value = {
    var visited = LocSetBot
    def _inherit(h: Heap, l_1: Loc, l_2: Loc): Value = {
      if(visited(l_1)) Value(BoolBot)
      else {
        visited = visited + l_1
        val v_eq = Operator.bopSEq(Value(l_1), Value(l_2))
        val v_1 =
          if (BoolTrue <= v_eq._1._3)
            Value(BoolTrue)
          else
            Value(BoolBot)
        val v_2 =
          if (BoolFalse <= v_eq._1._3) {
            val v_proto = h(l_1)("@proto")._1._1
            val v_1 =
              if (v_proto._1._2 </ NullBot)
                Value(BoolFalse)
              else
                Value(BoolBot)
            v_1 + v_proto._2.foldLeft[Value](ValueBot)((v,l) => v + _inherit(h, l, l_2))
          }
          else
            Value(BoolBot)
        v_1 + v_2
      }
    }
    _inherit(h, l_1, l_2)
  }

  def inherit(h: Heap, lset_1: LocSet, l_2: Loc): Value = {
    var visited = LocSetBot
    def _inherit(h: Heap, l_1: Loc, l_2: Loc): Value = {
      if(visited(l_1)) Value(BoolBot)
      else {
        visited = visited + l_1
        val v_eq = Operator.bopSEq(Value(l_1), Value(l_2))
        val v_1 =
          if (BoolTrue <= v_eq._1._3)
            Value(BoolTrue)
          else
            Value(BoolBot)
        val v_2 =
          if (BoolFalse <= v_eq._1._3) {
            val v_proto = h(l_1)("@proto")._1._1
            val v_1 =
              if (v_proto._1._2 </ NullBot)
                Value(BoolFalse)
              else
                Value(BoolBot)
            v_1 + v_proto._2.foldLeft[Value](ValueBot)((v,l) => v + _inherit(h, l, l_2))
          }
          else
            Value(BoolBot)
        v_1 + v_2
      }
    }
    lset_1.foldLeft(ValueBot)((vv, l_1) => {
      if (vv._1._3 == BoolTop) vv
      else vv + inherit(h, l_1, l_2)
    })
  }

  def Oldify(h: Heap, ctx: Context, a: Address): (Heap, Context) = {
    (h, Context(LocSetBot, LocSetBot, ctx._3 + a, ctx._4))
  }


  def FixOldify(ctx: Context, obj: Obj, mayOld: AddrSet, mustOld: AddrSet): (Context, Obj) = {
    (Context(LocSetBot, LocSetBot, ctx._3 ++ mayOld, ctx._4), obj)
  }


  /* built-in helper */
  def DefineProperty(h: Heap, l_1: Loc, s: AbsString, l_2: Loc) : Heap = {
    val v_val = Proto(h, l_2, AbsString.alpha("value"))
    val b_w = toBoolean(Proto(h, l_2, AbsString.alpha("writable")))
    val b_e = toBoolean(Proto(h, l_2, AbsString.alpha("enumerable")))
    val b_c = toBoolean(Proto(h, l_2, AbsString.alpha("configurable")))
    if(NumStr <= s || OtherStr <= s)
      h.update(l_1, h(l_1).update(s, PropValue(ObjectValue(v_val, BoolTop, BoolTop, BoolTop))))
    else
      h.update(l_1, h(l_1).update(s, PropValue(ObjectValue(v_val, b_w, b_e, b_c))))
  }


  def IsObject(h: Heap, l: Loc): AbsBool = {
    h(l).domIn("@class")
  }
  
  def IsArray(h: Heap, l: Loc): AbsBool = {
    val b1 = 
      if (AbsString.alpha("Array") <= h(l)("@class")._2._1._5)
        BoolTrue
      else
        BoolBot
    val b2 = 
      if (AbsString.alpha("Array") != h(l)("@class")._2._1._5)
        BoolFalse
      else
        BoolBot
    b1 + b2
  }
  
  def IsArrayIndex(s: AbsString): AbsBool = {
    s.getAbsCase match {
      case AbsBot => BoolBot
      case AbsTop => BoolTop
      case _ => s.gamma match {
        case Some(vs) =>
          (s.isAllNums, s.isAllOthers) match {
            case (false, false) => BoolBot
            case (false, true) => BoolFalse
            case (true, true) => BoolTop
            case (true, false) =>
              vs.foldLeft[AbsBool](BoolBot)((r, v) => {
                val num = v.toDouble
                r + AbsBool.alpha(0 <= num && num < scala.math.pow(2, 32) - 1)
              })
          }
        case None => BoolTop
      }
    }
  }

  def CanPutVar(h: Heap, x: String) = {
    if (h.domIn(GlobalLoc)) {
      val b_1 =
        if (BoolTrue <= h(GlobalLoc).domIn(x))
          h(GlobalLoc)(x)._1._2
        else BoolBot

      val b_2 =
        if (BoolFalse <= h(GlobalLoc).domIn(x))
          CanPut(h,GlobalLoc,AbsString.alpha(x))
        else
          BoolBot
      b_1 + b_2
    } else {
      BoolBot
    }
  }

  def CanPut(h: Heap, l: Loc, s: AbsString) = {
    CanPutHelp(h,l,s,l)
  }

  def CreateMutableBinding(h: Heap, PureLocalLoc: Loc, id: CFGId, v: Value): Heap = {
    val x = id.getText
    id.getVarKind match {
      case PureLocalVar =>
        h.update(PureLocalLoc, h(PureLocalLoc).update(x,
          PropValue(ObjectValue(v, BoolBot, BoolBot, BoolFalse))))
      case CapturedVar =>
        h(PureLocalLoc)("@env")._2._2.foldLeft(h)((hh, l) => {
          hh.update(l, hh(l).update(x,
            PropValue(ObjectValue(v, BoolTrue, BoolBot, BoolFalse))))
        })
      case CapturedCatchVar =>
        h.update(CollapsedLoc, h(CollapsedLoc).update(x,
          PropValue(ObjectValue(v, BoolBot, BoolBot, BoolFalse))))
      case GlobalVar =>
        h.update(GlobalLoc, h(GlobalLoc).update(x,
          PropValue(ObjectValue(v, BoolTrue, BoolTrue, BoolFalse))))
    }
  }

  def Lookup(h: Heap, PureLocalLoc: Loc, id: CFGId): (Value,Set[Exception]) = {
    val x = id.getText
    id.getVarKind match {
      case PureLocalVar => (h(PureLocalLoc)(x)._1._1, ExceptionBot)
      case CapturedVar =>
        val v = h(PureLocalLoc)("@env")._2._2.foldLeft(ValueBot)((vv, l) => {
          vv + Helper.LookupL(h, l, x)
        })
        (v, ExceptionBot)
      case CapturedCatchVar => (h(CollapsedLoc)(x)._1._1, ExceptionBot)
      case GlobalVar => LookupG(h, x)
    }
  }

  def LookupG(h: Heap, x: String): (Value,Set[Exception]) = {
    if (h.domIn(GlobalLoc)) {
      val v_1 =
        if (BoolTrue <= h(GlobalLoc).domIn(x))
          h(GlobalLoc)(x)._1._1
        else
          ValueBot
      val lset_proto = h(GlobalLoc)("@proto")._1._1._2
      val (v_2, es) =
        if (BoolFalse <= h(GlobalLoc).domIn(x)) {
          val exc = lset_proto.foldLeft(ExceptionBot)(
            (exc, l_proto) => {
              if (BoolFalse <= HasProperty(h, l_proto, AbsString.alpha(x))) {
                exc + ReferenceError
              } else {
                exc
              }
            })
          val v_3 = lset_proto.foldLeft(ValueBot)(
            (v_3, l_proto) => {
              if (BoolTrue <= HasProperty(h, l_proto, AbsString.alpha(x))) {
                v_3 + Proto(h, l_proto, AbsString.alpha(x))
              } else {
                v_3
              }
            })
          (v_3, exc)
        } else {
          (ValueBot, ExceptionBot)
        }
      (v_1 + v_2, es)
    } else {
      (ValueBot, Set[Exception]())
    }
  }

  def LookupBase(h: Heap, PureLocalLoc: Loc, id: CFGId): LocSet = {
    val x = id.getText
    id.getVarKind match {
      case PureLocalVar => LocSet(PureLocalLoc)
      case CapturedVar =>
        h(PureLocalLoc)("@env")._2._2.foldLeft(LocSetBot)((ll, l) => {
          ll ++ Helper.LookupBaseL(h, l, x)
        })
      case CapturedCatchVar => CollapsedSingleton
      case GlobalVar => LookupBaseG(h, x)
    }
  }

  def LookupBaseG(h: Heap, x: String): LocSet = {
    val lset_1 =
      if (BoolTrue <= h(GlobalLoc).domIn(x))
        GlobalSingleton
      else
        LocSetBot
    val lset_2 =
      if (BoolFalse <= h(GlobalLoc).domIn(x)) {
        val lset_proto = h(GlobalLoc)("@proto")._1._1._2
        lset_proto.foldLeft(LocSetBot)(
          (lset_3, l_proto) => {
            lset_3 ++ ProtoBase(h, l_proto, AbsString.alpha(x))
          })
      } else {
        LocSetBot
      }
    lset_1 ++ lset_2
  }

  def ProtoBase(h: Heap, l: Loc, s: AbsString): LocSet  = {
    var visited = LocSetBot
    def _ProtoBase(h: Heap, l: Loc, s: AbsString): LocSet  = {
      if(visited(l)) LocSetBot
      else {
        visited = visited + l
        val lset_1 =
          if (BoolTrue <= h(l).domIn(s))
            LocSet(l)
          else
            LocSetBot
        val lset_2 =
          if (BoolFalse <= h(l).domIn(s)) {
            val lset_proto = h(l)("@proto")._1._1._2
            lset_proto.foldLeft[LocSet](LocSetBot){case(lset_3,l_proto) =>
              val lset_4 = _ProtoBase(h,l_proto,s)
                lset_3 ++ lset_4}
          }
          else
            LocSetBot
        lset_1 ++ lset_2
      }
    }
    _ProtoBase(h, l, s)
  }

  def TypeTag(h: Heap, v: Value): AbsString = {
    val s_1 =
      if (!(v._1._4 <= NumBot))
        AbsString.alpha("number")
      else
        StrBot
    val s_2 =
      if (!(v._1._3 <= BoolBot))
        AbsString.alpha("boolean")
      else
        StrBot
    val s_3 =
      if (!(v._1._5 <= StrBot))
        AbsString.alpha("string")
      else
        StrBot
    val s_4 =
      if (!(v._2.subsetOf(LocSetBot)) && (BoolFalse <= v._2.foldLeft[AbsBool](BoolBot)((bool, l) => bool + IsCallable(h,l))))
        AbsString.alpha("object")
      else
        StrBot
    val s_5 =
      if (!(v._2.subsetOf(LocSetBot)) && (BoolTrue <= v._2.foldLeft[AbsBool](BoolBot)((bool, l) => bool + IsCallable(h,l))))
        AbsString.alpha("function")
      else
        StrBot
    val s_6 =
      if (!(v._1._2 <= NullBot))
        AbsString.alpha("object")
      else
        StrBot
    val s_7 =
      if (!(v._1._1 <= UndefBot))
        AbsString.alpha("undefined")
      else
        StrBot
    s_1 + s_2 + s_3 + s_4 + s_5 + s_6 + s_7
  }

  def HasOwnProperty(h: Heap, l: Loc, s: AbsString): AbsBool = {
    h(l).domIn(s)
  }

  def NewObject(): Obj = Obj.empty.
    update("@class", PropValue(AbsString.alpha("Object"))).
    update("@extensible", PropValue(BoolTrue))

  def NewObject(l: Loc): Obj = Obj.empty.
    update("@class", PropValue(AbsString.alpha("Object"))).
    update("@proto", PropValue(ObjectValue(Value(l), BoolFalse, BoolFalse, BoolFalse))).
    update("@extensible", PropValue(BoolTrue))

  def NewObject(lset: LocSet): Obj = Obj.empty.
    update("@class", PropValue(AbsString.alpha("Object"))).
    update("@proto", PropValue(ObjectValue(Value(lset), BoolFalse, BoolFalse, BoolFalse))).
    update("@extensible", PropValue(BoolTrue))

  def NewFunctionObject(fid: FunctionId, env: Value, l: Loc, n: AbsNumber): Obj = {
    NewFunctionObject(Some(fid), Some(fid), env, Some(l), n)
  }

  def NewFunctionObject(fid: Option[FunctionId], cid: Option[FunctionId], env: Value,
                        l: Option[Loc], n: AbsNumber): Obj = {
    NewFunctionObject(fid, cid, env, l, BoolTrue, BoolFalse, BoolFalse, n)
  }

  def NewArrayObject(n: AbsNumber): Obj = Obj.empty.
    update("@class", PropValue(AbsString.alpha("Array"))).
    update("@proto", PropValue(ObjectValue(BuiltinArray.ProtoLoc, BoolFalse, BoolFalse, BoolFalse))).
    update("@extensible", PropValue(BoolTrue)).
    update("length", PropValue(ObjectValue(n, BoolTrue, BoolFalse, BoolFalse)), exist = true)

  def NewArgObject(n: AbsNumber): Obj = Obj.empty.
    update("@class", PropValue(AbsString.alpha("Arguments"))).
    update("@proto", PropValue(ObjectValue(ObjProtoLoc, BoolFalse, BoolFalse, BoolFalse))).
    update("@extensible", PropValue(BoolTrue)).
    update("length", PropValue(ObjectValue(n, BoolTrue, BoolFalse, BoolTrue)), exist = true)

  def IsCallable(h: Heap, l: Loc): AbsBool = {
    val b_1 =
      if (BoolTrue <= h(l).domIn("@function"))
        BoolTrue
      else
        BoolBot
    val b_2 =
      if (BoolFalse <= h(l).domIn("@function"))
        BoolFalse
      else
        BoolBot
    (b_1 + b_2)
  }

  // v_env is either LocSet or NullTop
  def NewPureLocal(v_env: Value, lset_this: LocSet): Obj = Obj.empty.
    update("@env", PropValue(v_env)).
    update("@this", PropValue(Value(lset_this))).
    update("@exception", PropValueBot).
    update("@exception_all", PropValueBot).
    update("@return", PropValueUndefTop)

  def NewDeclEnvRecord(outer_env: Value): Obj = {
    Obj.empty.update("@outer", PropValue(outer_env))
  }

  def HasConstruct(h: Heap, l: Loc): AbsBool = {
    val b_1 =
      if (BoolTrue <= h(l).domIn("@construct"))
        BoolTrue
      else
        BoolBot
    val b_2 =
      if (BoolFalse <= h(l).domIn("@construct"))
        BoolFalse
      else
        BoolBot
    (b_1 + b_2)
  }

  def toNumber(pv: PValue): AbsNumber = {
    val pv1 = if (pv._1.isTop) NaN else NumBot
    val pv2 = if (pv._2.isTop) AbsNumber.alpha(+0) else NumBot
    val pv3 = pv._3.getPair match {
      case (AbsTop, _) => UInt
      case (AbsBot, _) => NumBot
      case (AbsSingle, Some(b)) => if (b) AbsNumber.alpha(1) else AbsNumber.alpha(+0)
      case _ => throw new InternalError("AbsBool does not have an abstract value for multiple values.")
    }
    val pv4 = pv._4
    val pv5 = pv._5.gamma match {
      case Some(vs) =>
        vs.foldLeft[AbsNumber](NumBot)((r, v) => {
          r + (v.trim match {
            case "" => AbsNumber.alpha(0)
            case str if AbsString.isHex(str) => AbsNumber.alpha((str+"p0").toDouble)
            case _ => try {AbsNumber.alpha(v.toDouble)} catch {case ne: NumberFormatException => NaN}
          })
        })
      case None =>
        pv._5.getAbsCase match {
          case AbsBot => NumBot
          case _ => NumTop
        }
    }
    (pv1 + pv2 + pv3 + pv4 + pv5)
  }

  def NewString(primitive_value: AbsString): Obj = {
    val o_new = NewObject(BuiltinString.ProtoLoc)

    val s = primitive_value
    val v_len = s.length

    // update properties of a String instance
    val o_new_1 = o_new.update("@class", PropValue(AbsString.alpha("String"))).
      update("@primitive", PropValue(primitive_value)).
      update("length", PropValue(ObjectValue(Value(v_len), BF, BF, BF)))

    AbsNumber.getUIntSingle(v_len) match {
      case Some(length) => {
        (0 until length.toInt).foldLeft(o_new_1)((_o, _i) =>
          _o.update(_i.toString(), PropValue(ObjectValue(s.charAt(AbsNumber.alpha(_i)), BF, BT, BF)), exist = true))
      }
      case _ => o_new_1.update(NumStr, PropValue(ObjectValue(StrTop, BF, BT, BF)))
    }
  }

  def NewNumber(primitive_value: AbsNumber): Obj = {
    val o_new = NewObject(BuiltinNumber.ProtoLoc)

    // update properties of a Number instance
    o_new.update("@class", PropValue(AbsString.alpha("Number"))).
      update("@primitive", PropValue(primitive_value))
  }

  def NewBoolean(primitive_value: AbsBool): Obj = {
    val o_new = NewObject(BuiltinBoolean.ProtoLoc)

    // update properties of a Boolean instance
    o_new.update("@class", PropValue(AbsString.alpha("Boolean"))).
      update("@primitive", PropValue(primitive_value))
  }

  def NewDate(primitive_value: Value): Obj = {
    val o_new = NewObject(BuiltinDate.ProtoLoc)

    // update properties of a Date instance
    o_new.update("@class", PropValue(AbsString.alpha("Date"))).
      update("@primitive", PropValue(primitive_value))
  }

  def toString(pv: PValue): AbsString = {
    val pv1 = absUndefToString(pv._1)
    val pv2 = absNullToString(pv._2)
    val pv3 = absBoolToString(pv._3)
    val pv4 = absNumberToString(pv._4)
    val pv5 = pv._5

    pv1 + pv2 + pv3 + pv4 + pv5
  }

  def toStringSet(pv: PValue): Set[AbsString] = {
    var set = HashSet[AbsString]()
    
    // collect strings from each PValue component
    if (pv._1.isTop) set += AbsString.alpha("undefined")
    
    if (pv._2.isTop) set += AbsString.alpha("null")
    
    pv._3.getPair match {
      case (AbsTop, _) =>
        set += AbsString.alpha("true")
        set += AbsString.alpha("false")
      case (AbsBot, _) => ()
      case (AbsSingle, Some(b)) => set += AbsString.alpha(b.toString)
      case _ => throw new InternalError("AbsBool does not have an abstract value for multiple values.")
    }
    
    pv._4.getPair match {
      case (AbsTop, _) => set += NumStr
      case (AbsBot, _) => ()
      case (AbsSingle, _) => set += pv._4.toAbsString
      case (AbsMulti, _) if AbsNumber.isInfinity(pv._4) =>
        set += AbsString.alpha("Infinity")
        set += AbsString.alpha("-Infinity")
      case (AbsMulti, _) => set += NumStr
    }
    
    pv._5.getAbsCase match {
      case AbsBot => ()
      case _ => set += pv._5
    }

    // remove redundancies
    if (set(StrTop)) set = HashSet[AbsString](StrTop)
    else {
      val hasNumStr = set(NumStr)
      val hasOtherStr = set(OtherStr)
      if (hasNumStr || hasOtherStr) {
        set = set.filter(s => s.gamma match {
          case Some(vs) =>
            (s.isAllNums, s.isAllOthers) match {
              case (true, true) => !hasNumStr && !hasNumStr
              case (true, false) => !hasNumStr
              case (false, true) => !hasOtherStr
              case (false, false) => true
            }
          case None => true
        })
      }
    }
    
    // return AbsString set
    set
  }

  def toBoolean(v: Value): AbsBool = {
    val b1 = if (v._1._1.isTop) BoolFalse else BoolBot
    val b2 = if (v._1._2.isTop) BoolFalse else BoolBot
    val b3 = v._1._3
    val b4 = v._1._4.getPair match {
      case (AbsTop, _) => BoolTop
      case (AbsBot, _) => BoolBot
      case (AbsSingle, _) => v._1._4.toBoolean
      case (AbsMulti, _) if AbsNumber.isInfinity(v._1._4) => BoolTrue
      case (AbsMulti, _) => BoolTop }
    val b5 = if(v._1._5.isAllNums) BoolTrue
      else v._1._5.getAbsCase match {
        case AbsTop => BoolTop
        case AbsBot => BoolBot
        case _ => v._1._5.gamma match {
          case Some(vs) => vs.foldLeft[AbsBool](BoolBot)((r, v) => r + AbsBool.alpha(v != ""))
          case None => BoolTop
        }
      }
    val b6 = if (v._2.isEmpty) BoolBot else BoolTrue

    (b1 + b2 + b3 + b4 + b5 + b6)
  }

  def objToPrimitive(objs:LocSet, hint:String): PValue = {
    if(objs.isEmpty)	PValueBot
    else {
      hint match {
        case "Number" =>	PValue(NumTop)
        case "String" =>	PValue(StrTop)
      }
    }
  }

  def toPrimitive(v: Value): PValue = {
    v._1 + objToPrimitive(v._2, "String")
  }

  def getThis(h:Heap, v: Value): LocSet = {
    // This semantic is a part of "10.4.3 Entering Function Code".

    // 2.a. if thisArg is null or undefined, set the ThisBinding to the global object.
    val lset_1 =
      if (NullTop <= v._1._2 || UndefTop <= v._1._1) GlobalSingleton
      else LocSetBot

    // 3. if Type(thisArg) is not Object, set the ThisBinding to ToObject(thisArg).
    // We do not need this step because ToObject has been inserted in IR translation.

    // 4. Else set the ThisBinding to thisArg.
    var foundDeclEnvRecord = false
    val lset_3 = v._2.foldLeft[LocSet](LocSetBot)((lset, l) => {
      val isObj = IsObject(h,l)
      if (BoolFalse <= isObj) foundDeclEnvRecord = true
      if (BoolTrue <= isObj) lset + l else lset
    })

    // 2.b. if thisArg is DeclEnvRecord, set the ThisBinding to the global object.
    // In ECMA spec, thisArg has been processed with ImplicitThisValue before "10.4.3".
    // ImplicitThisValue is always undefined except for ObjEnvRecord created by With statement.
    // So, as we rewrite With statement, we have no need for ImplicitThisValue.
    // Instead, we check for DeclEnvRecord directly in getThis.
    val lset_2 =
      if (foundDeclEnvRecord) GlobalSingleton
      else LocSetBot

    lset_1 ++ lset_2 ++ lset_3
  }


  def RaiseException(h:Heap, ctx:Context, PureLocalLoc: Loc, es:Set[Exception]): (Heap,Context) = {
    if (es.isEmpty)
      (h, ctx)
    else {
      val v_old = h(PureLocalLoc)("@exception_all")._2
      val v_e = Value(PValueBot,
                      es.foldLeft(LocSetBot)((lset,exc)=> lset + NewExceptionLoc(exc)))
      val h_1 = h.update(PureLocalLoc,
                         h(PureLocalLoc).update("@exception", PropValue(v_e)).
                                         update("@exception_all", PropValue(v_e + v_old)))
      (h_1,ctx)
    }
  }

  def NewExceptionLoc(exc: Exception): Loc = {
    exc match {
      case Error => BuiltinError.ErrLoc
      case EvalError => BuiltinError.EvalErrLoc
      case RangeError => BuiltinError.RangeErrLoc
      case ReferenceError => BuiltinError.RefErrLoc
      case SyntaxError => BuiltinError.SyntaxErrLoc
      case TypeError => BuiltinError.TypeErrLoc
      case URIError => BuiltinError.URIErrLoc
    }
  }

  def DefineProperties(h: Heap, l_1: Loc, l_2: Loc): Heap = {
    val props = h(l_2).getProps
    props.foldLeft(h)((_h, s) => {
      val prop = AbsString.alpha(s)
      val v_1 = Proto(_h, l_2, prop)
      v_1._2.foldLeft(_h)((__h, l) => DefineProperty(__h, l_1, prop, l))
    })
  }
}
