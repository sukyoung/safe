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
import kr.ac.kaist.jsaf.{Shell, ShellParameters}

object Helper {
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
        case Some(vs) if s.isAllNums =>
          vs.foldLeft[AbsBool](BoolBot)((r, v) => {
          val num = v.toDouble
          r + AbsBool.alpha(0 <= num && num < scala.math.pow(2, 32) -1)
          })
        case Some(_) if s.isAllOthers => BoolFalse
        case Some(_) => BoolBot
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

  def CanPutHelp(h: Heap, l_1: Loc, s: AbsString, l_2: Loc): AbsBool = {
    var visited = LocSetBot

    def iter(h: Heap, l_1: Loc, s: AbsString, l_2: Loc): AbsBool = {
      if (visited(l_1)) BoolBot
      else {
        visited += l_1
        val b_1 =
          if (BoolFalse <= h(l_1).domIn(s)) {
            val v_proto = h(l_1)("@proto")._1._1
            val b_3 =
              if (v_proto._1._2 </ NullBot)
                h(l_2)("@extensible")._2._1._3
              else
                BoolBot
            b_3 +
            v_proto._2.foldLeft(BoolBot: AbsBool)((b, l) => b + iter(h,l,s,l_2))
          } else {
            BoolBot
          }
        val b_2 =
          if (BoolTrue <= h(l_1).domIn(s))
            h(l_1)(s)._1._2
          else
            BoolBot

        b_1 + b_2
      }
    }

    iter(h, l_1, s, l_2)
  }


  def CreateMutableBinding(h: Heap, id: CFGId, v: Value): Heap = {
    val x = id.getText
    id.getVarKind match {
      case PureLocalVar =>
        val pv = PropValue(ObjectValue(v, BoolBot, BoolBot, BoolFalse))
        h.update(SinglePureLocalLoc, h(SinglePureLocalLoc).update(x, pv))
      case CapturedVar =>
        val pv = PropValue(ObjectValue(v, BoolTrue, BoolBot, BoolFalse))
        h(SinglePureLocalLoc)("@env")._2._2.foldLeft(HeapBot)((hh, l) => {
          hh + h.update(l, h(l).update(x, pv))
        })
      case CapturedCatchVar =>
        val pv = PropValue(ObjectValue(v, BoolBot, BoolBot, BoolFalse))
        h.update(CollapsedLoc, h(CollapsedLoc).update(x, pv))
      case GlobalVar =>
        val pv = PropValue(ObjectValue(v, BoolTrue, BoolTrue, BoolFalse))
        if(BoolTrue == Helper.HasProperty(h, GlobalLoc, AbsString.alpha(x))) h
        else h.update(GlobalLoc, h(GlobalLoc).update(x, pv))
    }
  }

  def VarStore(h: Heap, id: CFGId, v: Value): Heap = {
    val x = id.getText

    id.getVarKind match {
      case PureLocalVar =>
        val pv = PropValue(ObjectValue(v, BoolBot, BoolBot, BoolFalse))
        h.update(SinglePureLocalLoc, h(SinglePureLocalLoc).update(x, pv))
      case CapturedVar =>
        h(SinglePureLocalLoc)("@env")._2._2.foldLeft(HeapBot)((hh, l) => {
          hh + VarStoreL(h, l, x, v)
        })
      case CapturedCatchVar =>
        val pv = PropValue(ObjectValue(v, BoolBot, BoolBot, BoolFalse))
        h.update(CollapsedLoc, h(CollapsedLoc).update(x, pv))
      case GlobalVar => {
        val h_1 =
          if (BoolTrue <= CanPutVar(h, x))
            VarStoreG(h, x, v)
          else
            HeapBot
        val h_2 =
          if (BoolFalse <= CanPutVar(h, x))
            h
          else
            HeapBot

        (h_1 + h_2)
      }
    }
  }

  def VarStoreL(h: Heap, l: Loc, x: String, v: Value): Heap = {
    var visited = LocSetBot
    def visit(l: Loc): Heap = {
      if (visited.contains(l)) HeapBot
      else {
        visited += l
        val env = h(l)
        val has_x = env.domIn(x)
        val h_1 =
          if (BoolTrue <= has_x) {
            env(x)._1._2.getPair match {
              case (AbsSingle, Some(true)) =>
                  val pv = PropValue(ObjectValue(v, BoolTrue, BoolBot, BoolFalse))
                  h.update(l, env.update(x, pv))
              case (AbsSingle, Some(false)) => h
              case _ =>
                throw new InternalError("Writable attribute must be exact for variables in local env.")
            }
          } else {
            HeapBot
          }
        val h_2 =
          if (BoolFalse <= has_x) {
            val lset_outer = env("@outer")._2._2
            lset_outer.foldLeft(HeapBot)((hh, l_outer) => hh + visit(l_outer))
          } else {
            HeapBot
          }
        h_1 + h_2
      }
    }
    
    visit(l)
  }

  def VarStoreG(h: Heap, x: String, v: Value) = {
    val l_g = GlobalLoc
    // case 1
    val h_1 = if (BoolFalse <= h(l_g).domIn(x)) {
      PropStore(h, l_g, AbsString.alpha(x), v)
    } else { HeapBot }

    // case 2
    val h_2 = if (BoolTrue <= h(l_g).domIn(x)) {
      val ov_old = h(l_g)(x)._1
      val ov_new = ObjectValue(v, ov_old._2, ov_old._3, ov_old._4)
      val o = h(l_g).update(x, PropValue(ov_new))
      h.update(l_g, o)
    } else { HeapBot }

    h_1 + h_2
  }
  /*
  def PropStore(h: Heap, l: Loc, s: AbsString, v: Value): Heap = {
    val test = h(l).domIn(s)
    val h_1 =
      if (BoolFalse <= test)
        h.update(l, h(l).update(s, PropValue(ObjectValue(v,BoolTrue,BoolTrue,BoolTrue))))
      else
        HeapBot
    val h_2 =
      if (BoolTrue <= test) {
        val ov_old = h(l)(s)._1._1
        h.update(l, h(l).update(s, PropValue(ObjectValue(v, ov_old._2, ov_old._3, ov_old._4))))
      }
      else
        HeapBot
    h_1 + h_2
  }*/
  def PropStore(h: Heap, l: Loc, s: AbsString, v: Value): Heap = {
    h(l).domIn(s).getPair match {
      case (AbsTop, _) =>
        val ov_old = h(l)(s)._1
        val propv = PropValue(ObjectValue(v, ov_old._2 + BoolTrue, ov_old._3 + BoolTrue, ov_old._4 + BoolTrue))
        h.update(l, h(l).update(s, propv))
      case (AbsSingle, Some(true)) =>
        val ov_old = h(l)(s)._1
        h.update(l, h(l).update(s, PropValue(ObjectValue(v, ov_old._2, ov_old._3, ov_old._4))))
      case (AbsSingle, Some(false)) =>
        h.update(l, h(l).update(s, PropValue(ObjectValue(v,BoolTrue,BoolTrue,BoolTrue))))
      case (AbsBot, _) =>
        HeapBot
      case _ => throw new InternalError("AbsBool does not have an abstract value for multiple values.")
    }
  }
  def PropStoreWeak(h: Heap, l: Loc, s: AbsString, v: Value): Heap = {
    h(l).domIn(s).getPair match {
      case (AbsTop, _) =>
        val ov_old = h(l)(s)._1
        val propv = PropValue(ObjectValue(v, ov_old._2 + BoolTrue, ov_old._3 + BoolTrue, ov_old._4 + BoolTrue))
        Heap(h.map.weakUpdated(l, h(l).update(s, propv)))
      case (AbsSingle, Some(true))  =>
        val ov_old = h(l)(s)._1
        Heap(h.map.weakUpdated(l, h(l).update(s, PropValue(ObjectValue(v, ov_old._2, ov_old._3, ov_old._4)))))
      case (AbsSingle, Some(false))  =>
        Heap(h.map.weakUpdated(l, h(l).update(s, PropValue(ObjectValue(v,BoolTrue,BoolTrue,BoolTrue)))))
      case (AbsBot, _) => 
        HeapBot
      case _ => throw new InternalError("AbsBool does not have an abstract value for multiple values.")
    }
  }
 
  def ReturnStore(h: Heap, v: Value):Heap = {
    h.update(SinglePureLocalLoc, h(SinglePureLocalLoc).update("@return", PropValue(v)))
  }

  def Delete(h: Heap, l: Loc, s: AbsString): (Heap, AbsBool) = {
    val (h_1, b_1) =
      if ((BoolTrue <= HasOwnProperty(h, l, s)) && (BoolFalse <= h(l)(s)._1._4))
        (h, BoolFalse)
      else
        (HeapBot, BoolBot)
    val (h_2, b_2) =
      if (  (BoolTrue <= HasOwnProperty(h, l, s) && BoolFalse != h(l)(s)._1._4)// BoolBot should be included because @exception property also could be deleted.
          || BoolFalse <= HasOwnProperty(h, l, s))
        (h.update(l, h(l) - s), BoolTrue)
      else
        (HeapBot, BoolBot)
    (h_1 + h_2, b_1 + b_2)
  }

  def Lookup(h: Heap, id: CFGId): (Value,Set[Exception]) = {
    val x = id.getText
    id.getVarKind match {
      case PureLocalVar => (h(SinglePureLocalLoc)(x)._1._1, ExceptionBot)
      case CapturedVar =>
        val v = h(SinglePureLocalLoc)("@env")._2._2.foldLeft(ValueBot)((vv, l) => {
          vv + LookupL(h, l, x)
        })
        (v, ExceptionBot)
      case CapturedCatchVar => (h(CollapsedLoc)(x)._1._1, ExceptionBot)
      case GlobalVar => LookupG(h, x)
    }
  }

  def LookupL(h: Heap, l: Loc, x: String): Value = {
    var visited = LocSetBot
    def visit(l: Loc): Value = {
      if (visited.contains(l)) ValueBot
      else {
        visited += l
        val env = h(l)
        val has_x = env.domIn(x)
        val v_1 =
          if (BoolTrue <= has_x) {
            env(x)._1._1
          } else {
            ValueBot
          }
        val v_2 =
          if (BoolFalse <= has_x) {
            val lset_outer = env("@outer")._2._2
            lset_outer.foldLeft(ValueBot)((v, l_outer) => v + visit(l_outer))
          } else {
            ValueBot
          }
        v_1 + v_2
      }
    }

    visit(l)
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
      (ValueBot, ExceptionBot)
    }
  }

  def LookupBase(h: Heap, id: CFGId): LocSet = {
    val x = id.getText
    id.getVarKind match {
      case PureLocalVar => LocSet(SinglePureLocalLoc)
      case CapturedVar =>
        h(SinglePureLocalLoc)("@env")._2._2.foldLeft(LocSetBot)((ll, l) => {
          ll ++ LookupBaseL(h, l, x)
        })
      case CapturedCatchVar => CollapsedSingleton
      case GlobalVar => LookupBaseG(h, x)
    }
  }

  def LookupBaseL(h: Heap, l: Loc, x: String): LocSet = {
    var visited = LocSetBot
    def visit(l: Loc): LocSet = {
      if (visited.contains(l)) LocSetBot
      else {
        visited += l
        val env = h(l)
        val has_x = env.domIn(x)
        val lset_1 =
          if (BoolTrue <= has_x) {
            LocSet(l)
          } else {
            LocSetBot
          }
        val lset_2 =
          if (BoolFalse <= has_x) {
            val lset_outer = env("@outer")._2._2
            lset_outer.foldLeft(LocSetBot)((lset, l_outer) => lset ++ visit(l_outer))
          } else {
            LocSetBot
          }
        lset_1 ++ lset_2
      }
    }
    
    visit(l)
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

  // TODO: optimize
  def ProtoBase(h: Heap, l: Loc, s: AbsString): LocSet  = {
    var visited = LocSetBot

    def iter(h: Heap, l: Loc, s: AbsString): LocSet  = {
      if (visited(l)) LocSetBot
      else {
        visited += l
        val lset_1 =
          if (BoolTrue <= h(l).domIn(s))
            LocSet(l)
          else
            LocSetBot
        val lset_2 =
          if (BoolFalse <= h(l).domIn(s)) {
            val lset_proto = h(l)("@proto")._1._1._2
            lset_proto.foldLeft[LocSet](LocSetBot)((lset_3, l_proto) => {
              val lset_4 = iter(h,l_proto,s)
              lset_3 ++ lset_4
            })
          } else {
            LocSetBot
          }
        lset_1 ++ lset_2
      }
    }

    iter(h, l, s)
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

  //
  def HasProperty(h: Heap, l: Loc, s: AbsString): AbsBool = {
    var visited = LocSetBot

    def iter(h: Heap, l: Loc, s: AbsString): AbsBool = {
      if (visited(l)) BoolBot
      else {
        visited += l
        val test = HasOwnProperty(h,l,s)
        val b_1 =
          if (BoolTrue <= test) {
            BoolTrue
          } else {
            BoolBot
          }
        val b_2 =
          if (BoolFalse <= test) {
            val v_proto = h(l)("@proto")._1._1
            val b_3 =
              if (v_proto._1._2 </ NullBot) {
                BoolFalse
              } else {
                BoolBot
              }
            b_3 + v_proto._2.foldLeft[AbsBool](BoolBot)((b,l_proto) => {
              b + iter(h,l_proto,s)
            })
          } else {
            BoolBot
          }
        b_1 + b_2
      }
    }

    iter(h, l, s)
  }

  def HasOwnProperty(h: Heap, l: Loc, s: AbsString): AbsBool = {
    h(l).domIn(s)
  }

  def Proto(h: Heap, l: Loc, s: AbsString): Value  = {
    var visited = LocSetBot

    def iter(h: Heap, l: Loc, s: AbsString): Value = {
      if (visited.contains(l)) ValueBot
      else {
        visited += l
        val test = h(l).domIn(s)
        val v_1 =
          if (BoolTrue <= test) {
            val v= h(l)(s)._1._1
            v
          } else {
            ValueBot
          }
        val v_2 =
          if (BoolFalse <= test) {
            val v_proto = h(l)("@proto")._1._1
            val v_3 =
              if (v_proto._1._2 </ NullBot) {
                Value(PValue(UndefTop))
              } else {
                ValueBot
              }
            v_3 + v_proto._2.foldLeft(ValueBot)((v,l_proto) => v + iter(h, l_proto, s))
          } else {
            ValueBot
          }
        v_1 + v_2
      }
    }

    iter(h, l, s)
  }

  def ProtoProp(h: Heap, l: Loc, s: AbsString): PropValue  = {
    var visited = LocSetBot

    def iter(h: Heap, l: Loc, s: AbsString): PropValue = {
      if (visited.contains(l)) PropValueBot
      else {
        visited += l
        val test = h(l).domIn(s)
        val v_1: PropValue = if (BoolTrue <= test) h(l)(s) else PropValueBot
        val v_2: PropValue =
          if (BoolFalse <= test) {
            val v_proto = h(l)("@proto").objval.value
            v_proto.locset.foldLeft(PropValueBot)((v, l_proto) => v + iter(h, l_proto, s))
          }
          else PropValueBot
        v_1 + v_2
      }
    }

    iter(h, l, s)
  }

  def NewObject(): Obj =
    Obj.empty.
      update("@class", PropValue(AbsString.alpha("Object"))).
      update("@extensible", PropValue(BoolTrue))

  def NewObject(l: Loc): Obj =
    Obj.empty.
    update("@class", PropValue(AbsString.alpha("Object"))).
    update("@proto", PropValue(ObjectValue(Value(l), BoolFalse, BoolFalse, BoolFalse))).
    update("@extensible", PropValue(BoolTrue))

  def NewObject(lset: LocSet): Obj =
    Obj.empty.
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

  def NewFunctionObject(fid: Option[FunctionId], cid: Option[FunctionId], env: Value,
                        l: Option[Loc], w: AbsBool, e: AbsBool, c: AbsBool, n: AbsNumber): Obj = {
    val o_1 = Obj.empty.
      update("@class", PropValue(AbsString.alpha("Function"))).
      update("@proto", PropValue(ObjectValue(Value(FunctionProtoLoc), BoolFalse, BoolFalse, BoolFalse))).
      update("@extensible", PropValue(BoolTrue)).
      update("@scope", PropValue(env)).
      update("length", PropValue(ObjectValue(n, BoolFalse, BoolFalse, BoolFalse)))
    val o_1_1 = if(Config.domMode)
                // Non-standard but browsers support the 'arguments' property of a function object
                o_1.update("arguments", PropValue(ObjectValue(Value(NullTop), BoolFalse, BoolFalse, BoolFalse)))
              else o_1
    val o_2 = fid match {
      case Some(id) => o_1_1.update("@function", PropValue(ObjectValueBot, FunSet(id)))
      case None => o_1_1
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
      case Some(loc) => o_4.update("prototype", PropValue(ObjectValue(Value(loc), w, e, c)))
      case None => o_4
    }
    o_5
  }

  def NewArrayObject(n: AbsNumber): Obj =
    Obj.empty.
    update("@class", PropValue(AbsString.alpha("Array"))).
    update("@proto", PropValue(ObjectValue(BuiltinArray.ProtoLoc, BoolFalse, BoolFalse, BoolFalse))).
    update("@extensible", PropValue(BoolTrue)).
    update("length", PropValue(ObjectValue(n, BoolTrue, BoolFalse, BoolFalse)))

  def NewArgObject(n: AbsNumber): Obj =
    Obj.empty.
    update("@class", PropValue(AbsString.alpha("Arguments"))).
    update("@proto", PropValue(ObjectValue(ObjProtoLoc, BoolFalse, BoolFalse, BoolFalse))).
    update("@extensible", PropValue(BoolTrue)).
    update("length", PropValue(ObjectValue(n, BoolTrue, BoolFalse, BoolTrue)))

  // 9.11 IsCallable
  def IsCallable(h: Heap, v: Value): AbsBool = {
    val b_1 = if (v._1._1 </ UndefBot) BoolFalse else BoolBot
    val b_2 = if (v._1._2 </ NullBot) BoolFalse else BoolBot
    val b_3 = if (v._1._3 </ BoolBot) BoolFalse else BoolBot
    val b_4 = if (v._1._4 </ NumBot) BoolFalse else BoolBot
    val b_5 = if (v._1._5 </ StrBot) BoolFalse else BoolBot
    val b_6 = v._2.foldLeft(AbsBool.bot)((b, l) => b + IsCallable(h, l))

    b_1 + b_2 + b_3 + b_4 + b_5 + b_6
  }

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
    (b_1 + b_2) // + IsBound(h, l)
  }
  
  def IsBound(h: Heap, l: Loc): AbsBool = {
    val b_1 =
      if (BoolTrue <= h(l).domIn("@target_function"))
        BoolTrue
      else
        BoolBot
    val b_2 =
      if (BoolFalse <= h(l).domIn("@target_function"))
        BoolFalse
      else
        BoolBot
    (b_1 + b_2)
  }
  
  // append two arguements
  def concat(arg_prev: Obj, arg_next: Obj): Obj = {
    val n_prev_arglen = Operator.ToUInt32(arg_prev("length")._1._1)

    AbsNumber.getUIntSingle(n_prev_arglen) match {
      // when previous argument has a precise number of property...
      case Some(n_prev_ind) => {
        val n_len = Operator.ToUInt32(arg_next("length")._1._1)
        AbsNumber.getUIntSingle(n_len) match {
          case Some(n) => {
            (0 until n.toInt).foldLeft(arg_prev)((o_new, i)=>
              o_new.update(Helper.toString(Operator.bopPlus(Value(n_prev_arglen), Value(AbsNumber.alpha(i)))._1),
                           arg_next(i.toString)))
            .update(AbsString.alpha("length"), PropValue(ObjectValue(Value(AbsNumber.alpha(n_prev_ind + n)), BoolTrue, BoolFalse, BoolTrue)))
          }
          case _ => n_len.getAbsCase match {
            case AbsBot => Obj.bottom
            case _ =>
              val v_all = arg_next(NumStr)
              arg_prev.update(NumStr, v_all)
                      .update(AbsString.alpha("length"), PropValue(ObjectValue(Value(UInt), BoolTrue, BoolFalse, BoolTrue)))
          }
        }
      }
      case _ => n_prev_arglen.getAbsCase match {
        case AbsBot => Obj.bottom
        case _ =>
          // store all arg_next to arg_prev...
          val v_all = arg_next(NumStr)
          arg_prev.update(NumStr, v_all)
                  .update(AbsString.alpha("length"), PropValue(ObjectValue(Value(UInt), BoolTrue, BoolFalse, BoolTrue)))
      }
    }
  }
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
    b_1 + b_2
  }

  def HasInstance(h: Heap, l: Loc): AbsBool = {
    val b_1 =
      if (BoolTrue <= h(l).domIn("@hasinstance"))
        BoolTrue
      else
        BoolBot
    val b_2 =
      if (BoolFalse <= h(l).domIn("@hasinstance"))
        BoolFalse
      else
        BoolBot
    b_1 + b_2
  }

  def allocObject(h: Heap, ls_v: LocSet, l_r: Loc) = {
    val o_new = Helper.NewObject(ls_v)
    val h_2 = h.update(l_r, o_new)
    h_2
  }

  def NewString(primitive_value: AbsString) = {
    val o_new = Helper.NewObject(BuiltinString.ProtoLoc)

    val s = primitive_value

    // update properties of a String instance
    val o_new_1 = o_new.update("@class", PropValue(AbsString.alpha("String"))).
      update("@primitive", PropValue(primitive_value))

    s.gamma match {
      case Some(set) =>
        set.foldLeft(Obj.bottom)((obj, str) => {
          val length = str.length
          val newobj = (0 until length).foldLeft(o_new_1)((_o, _i) =>
            _o.update(_i.toString, PropValue(ObjectValue(AbsString.alpha(str.charAt(_i).toString), BF, BT, BF))))
          obj + newobj.update("length", PropValue(ObjectValue(Value(AbsNumber.alpha(length)), BF, BF, BF)))
        })
      case None =>
        o_new_1.update(NumStr, PropValue(ObjectValue(StrTop, BF, BT, BF))).
          update("length", PropValue(ObjectValue(Value(s.length), BF, BF, BF)))
    }
  }

  def NewNumber(primitive_value: AbsNumber) = {
    val o_new = Helper.NewObject(BuiltinNumber.ProtoLoc)

    // update properties of a Number instance
    o_new.update("@class", PropValue(AbsString.alpha("Number"))).
      update("@primitive", PropValue(primitive_value))
  }

  def NewBoolean(primitive_value: AbsBool) = {
    val o_new = Helper.NewObject(BuiltinBoolean.ProtoLoc)

    // update properties of a Boolean instance
    o_new.update("@class", PropValue(AbsString.alpha("Boolean"))).
      update("@primitive", PropValue(primitive_value))
  }

  def NewDate(primitive_value: Value) = {
    val o_new = Helper.NewObject(BuiltinDate.ProtoLoc)

    // update properties of a Date instance
    o_new.update("@class", PropValue(AbsString.alpha("Date"))).
      update("@primitive", PropValue(primitive_value))
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

  def inherit(h: Heap, l_1: Loc, l_2: Loc): Value = {
    var visited = LocSetBot
    def iter(h: Heap, l_1: Loc, l_2: Loc): Value = {
      if (visited(l_1)) ValueBot
      else {
        visited += l_1
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
            v_1 + v_proto._2.foldLeft[Value](ValueBot)((v,l) => v + iter(h, l, l_2))
          }
          else
            Value(BoolBot)
        v_1 + v_2
      }
    }

    iter(h, l_1, l_2)
  }


  def RaiseException(h:Heap, ctx:Context, es:Set[Exception]): (Heap,Context) = {
    if (es.isEmpty)
      (HeapBot, ContextBot)
    else {
      val v_old = h(SinglePureLocalLoc)("@exception_all")._2
      val v_e = Value(PValueBot,
                      es.foldLeft(LocSetBot)((lset,exc)=> lset + NewExceptionLoc(exc)))
      val h_1 = h.update(SinglePureLocalLoc,
                         h(SinglePureLocalLoc).update("@exception", PropValue(v_e)).
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

  def Oldify(h: Heap, ctx: Context, a: Address): (Heap, Context) = {
    if (ctx.isBottom) (HeapBot, ContextBot)
    else {
      val l_r = addrToLoc(a, Recent)
      val l_o = addrToLoc(a, Old)
      val h_1 =
        if (h.domIn(l_r))
          h.update(l_o, h(l_r)).remove(l_r).subsLoc(l_r, l_o)
        else
          h.subsLoc(l_r, l_o)
      val ctx_1 = ctx.subsLoc(l_r, l_o)
      (h_1, ctx_1)
    }
  }

  def FixOldify(ctx: Context, obj: Obj, mayOld: AddrSet, mustOld: AddrSet): (Context, Obj) = {
    if (ctx.isBottom) (ContextBot, Obj.bottom)
    else {
      mayOld.foldLeft((ctx, obj))((res, a) => {
        val l_r = addrToLoc(a, Recent)
        val l_o = addrToLoc(a, Old)
        if (mustOld(a)) {
          val ctx_new = res._1.subsLoc(l_r, l_o)
          val obj_new = res._2.subsLoc(l_r, l_o)
          (ctx_new, obj_new)
        } else {
          val ctx_new = res._1.weakSubsLoc(l_r, l_o)
          val obj_new = res._2.weakSubsLoc(l_r, l_o)
          (ctx_new, obj_new)
        }
      })
    }
  }
/*
  def DefineProperties(h: Heap, l_1: Loc, l_2: Loc): Heap = {
    val props = h(l_2).getProps
    props.foldLeft(HeapBot)((_h, s) => {
      val prop = AbsString.alpha(s)
      val v_1 = Proto(h, l_2, prop)
      _h + v_1._2.foldLeft(HeapBot)((__h, l) => __h + DefineProperty(h, l_1, prop, l))
    })
  }
 */
  def DefineProperties(h: Heap, l_1: Loc, l_2: Loc): Obj = {
    val props = try{ CollectOwnProps(h, LocSet(l_2)) } catch{                        
       case e: InternalError => {h(l_2).getProps}}

    props.foldLeft(h(l_1))((_obj, s) => {
      val prop = AbsString.alpha(s)
      val v_1 = Proto(h, l_2, prop)
      v_1._2.foldLeft(_obj)((__obj, l) => __obj + DefineProperty(h, l_1, prop, l))
    })
  }
 
  def DefineProperty(h: Heap, l_1: Loc, s: AbsString, l_2: Loc) : Obj = {
    val v_val = Proto(h, l_2, AbsString.alpha("value"))
    val b_w = toBoolean(Proto(h, l_2, AbsString.alpha("writable")))
    val b_e = toBoolean(Proto(h, l_2, AbsString.alpha("enumerable")))
    val b_c = toBoolean(Proto(h, l_2, AbsString.alpha("configurable")))
    h(l_1).update(s, PropValue(ObjectValue(v_val, b_w, b_e, b_c)))
  }

  /* built-in helper */
/*  def DefineProperty(h: Heap, l_1: Loc, s: AbsString, l_2: Loc) : Heap = {
    val v_val = Proto(h, l_2, AbsString.alpha("value"))
    val b_w = toBoolean(Proto(h, l_2, AbsString.alpha("writable")))
    val b_e = toBoolean(Proto(h, l_2, AbsString.alpha("enumerable")))
    val b_c = toBoolean(Proto(h, l_2, AbsString.alpha("configurable")))
    h.update(l_1, h(l_1).update(s, PropValue(ObjectValue(v_val, b_w, b_e, b_c))))
  }
*/

  /* can be called by both of flow (in)sensitive analysis. */
  def toNumber(pv: PValue): AbsNumber = {
    if (Config.preAnalysis) {
      PreHelper.toNumber(pv)
    } else {
      val pv1 = if (pv._1.isTop) NaN else NumBot
      val pv2 = if (pv._2.isTop) AbsNumber.alpha(+0) else NumBot
      val pv3 = pv._3.getPair match {
        case (AbsTop, _) => UInt
        case (AbsBot, _) => NumBot
        case (AbsSingle, Some(true)) => AbsNumber.alpha(1)
        case (AbsSingle, Some(false)) => AbsNumber.alpha(+0)
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
  }

  def toObject(h: Heap, ctx: Context, v: Value, a_new: Address): (Value, Heap, Context, Set[Exception]) = {
    if (Config.preAnalysis) {
      PreHelper.toObject(h, ctx, v, a_new)
    } else {
      // 9.9 ToObject
      val lset = v._2

      val o_1 =
        if (!(v._1._5 <= StrBot)) Helper.NewString(v._1._5)
        else Obj.bottom
      val o_2 =
        if (!(v._1._3 <= BoolBot)) Helper.NewBoolean(v._1._3)
        else Obj.bottom
      val o_3 =
        if (!(v._1._4 <= NumBot)) Helper.NewNumber(v._1._4)
        else Obj.bottom
      val es =
        if (!(v._1._1 <= UndefBot) || !(v._1._2 <= NullBot)) Set[Exception](TypeError)
        else Set[Exception]()
      val o = o_1 + o_2 + o_3

      val l_r = addrToLoc(a_new, Recent)
        val (lset_1, h_2, ctx_2) =
          if (o </ Obj.bottom) {
            val (h_1, ctx_1) = Helper.Oldify(h, ctx, a_new)
            (LocSet(l_r), h_1.update(l_r, o), ctx_1)
          } else {
            (LocSetBot, HeapBot, ContextBot)
          }

      val (lset_2, h_3, ctx_3) =
        if (!lset.isEmpty) {
          (lset, h, ctx)
        } else {
          (LocSetBot, HeapBot, ContextBot)
        }

      val lset_3 = lset_1 ++ lset_2
      val h_4 = h_2 + h_3
      val ctx_4 = ctx_2 + ctx_3

      (Value(lset_3), h_4, ctx_4, es)
    }
  }

  def toString(pv: PValue): AbsString = {
    if (Config.preAnalysis) {
      PreHelper.toString(pv)
    } else {
      val pv1 = absUndefToString(pv._1)
      val pv2 = absNullToString(pv._2)
      val pv3 = absBoolToString(pv._3)
      val pv4 = absNumberToString(pv._4)
      val pv5 = pv._5

      pv1 + pv2 + pv3 + pv4 + pv5
    }
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
      case (AbsSingle, Some(true)) => set += AbsString.alpha("true")
      case (AbsSingle, Some(false)) => set += AbsString.alpha("false")
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
    set.filter(s => !set.exists(o => s != o && s <= o))
  }

  def toBoolean(v: Value): AbsBool = {
    if (Config.preAnalysis) {
      PreHelper.toBoolean(v)
    } else {
      val b1 = if (v._1._1.isTop) BoolFalse else BoolBot
      val b2 = if (v._1._2.isTop) BoolFalse else BoolBot
      val b3 = v._1._3
      val b4 = v._1._4.getPair match {
        case (AbsTop, _) => BoolTop
        case (AbsBot, _) => BoolBot
        case (AbsSingle, _) => v._1._4.toBoolean
        case (AbsMulti, _) if AbsNumber.isInfinity(v._1._4) => BoolTrue
        case (AbsMulti, _) => BoolTop
      }
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
  }

  /**
   * Default toString method of {Boolean, Number, String} object.
   * @param h heap
   * @param lset location set of object
   * @return AbsString
   */
  def defaultToString(h: Heap, lset: LocSet): AbsString = {
    val lset_bool = lset.filter(l => AbsString.alpha("Boolean") <= h(l)("@class")._2._1._5)
    val lset_num = lset.filter(l => AbsString.alpha("Number") <= h(l)("@class")._2._1._5)
    val lset_string = lset.filter(l => AbsString.alpha("String") <= h(l)("@class")._2._1._5)
    val lset_regexp = lset.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._2._1._5)
    val lset_others = lset.filter(l => {
      val v = h(l)("@class")._2._1._5
      val b = AbsString.alpha("Boolean")
      val n = AbsString.alpha("Number")
      val s = AbsString.alpha("String")
      val r = AbsString.alpha("RegExp")
      v != b && v != n && v != s && v != r
    })

    val others = lset_others.foldLeft[AbsString](StrBot)((_s, l) => _s + h(l)("@class")._2._1._5)
    val b = lset_bool.foldLeft[AbsBool](BoolBot)((_b, l) => _b + h(l)("@primitive")._2._1._3)
    val n = lset_num.foldLeft[AbsNumber](NumBot)((_v, _l) => _v + h(_l)("@primitive")._2._1._4)
    val (s_src, b_g, b_i, b_m) =
      lset_regexp.foldLeft
        [(AbsString, AbsBool, AbsBool, AbsBool)]((StrBot, BoolBot, BoolBot, BoolBot))((s, l) => {
        (s._1 + h(l)("source")._1._1._1._5,
          s._2 + h(l)("global")._1._1._1._3,
          s._3 + h(l)("ignoreCase")._1._1._1._3,
          s._4 + h(l)("multiline")._1._1._1._3)
      })

    val s_1 = lset_string.foldLeft[AbsString](StrBot)((_s, l) => _s + h(l)("@primitive")._2._1._5)
    val s_2 = absBoolToString(b)
    val s_3 = absNumberToString(n)

    val s_4 = (s_src.getSingle, b_g.getSingle, b_i.getSingle, b_m.getSingle) match {
      case (Some(s), Some(g), Some(i), Some(m)) => {
        val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")
        AbsString.alpha("/"+s+"/"+flags)
      }
      case _ if s_src </ StrBot && b_g </ BoolBot && b_i </ BoolBot && b_m </ BoolBot => StrTop
      case _ => StrBot
    }

    // TODO default toString semantics for an Object value.
    val s_5 = others.getSingle match {
      case Some(s) => 
       if(Shell.params.command ==ShellParameters.CMD_WEBAPP_BUG_DETECTOR)
         AbsString.alpha("[object "+s+"]")
       else StrTop
      case None if others <= StrBot => StrBot
      case None => StrTop
    }
    s_1 + s_2 + s_3 + s_4 + s_5
  }
  /**
   * Default [[DefalutValue]]("Number") method of {Boolean, Number, String, Date} object.
   * @param h heap
   * @param lset location set of object
   * @return AbsString
   */
  def defaultValueNumber(h: Heap, lset: LocSet): PValue = {
    val lset_bool = lset.filter(l => AbsString.alpha("Boolean") <= h(l)("@class")._2._1._5)
    val lset_num = lset.filter(l => AbsString.alpha("Number") <= h(l)("@class")._2._1._5)
    val lset_string = lset.filter(l => AbsString.alpha("String") <= h(l)("@class")._2._1._5)
    val lset_date = lset.filter(l => AbsString.alpha("Date") <= h(l)("@class")._2._1._5)
    val lset_regexp = lset.filter(l => AbsString.alpha("RegExp") <= h(l)("@class")._2._1._5)
    val lset_others = lset.filter(l => {
      val v = h(l)("@class")._2._1._5
      val b = AbsString.alpha("Boolean")
      val n = AbsString.alpha("Number")
      val d = AbsString.alpha("Date")
      val s = AbsString.alpha("String")
      val r = AbsString.alpha("RegExp")
      v != b && v != n && v != s && v != r && v != d
    })

    val others = lset_others.foldLeft[AbsString](StrBot)((_s, l) => _s + h(l)("@class")._2._1._5)
    val b = lset_bool.foldLeft[AbsBool](BoolBot)((_b, l) => _b + h(l)("@primitive")._2._1._3)
    val n = lset_num.foldLeft[AbsNumber](NumBot)((_v, _l) => _v + h(_l)("@primitive")._2._1._4)
    val n2 = lset_date.foldLeft[AbsNumber](NumBot)((_v, _l) => _v + h(_l)("@primitive")._2._1._4)
    val (s_src, b_g, b_i, b_m) =
      lset_regexp.foldLeft
        [(AbsString, AbsBool, AbsBool, AbsBool)]((StrBot, BoolBot, BoolBot, BoolBot))((s, l) => {
        (s._1 + h(l)("source")._1._1._1._5,
          s._2 + h(l)("global")._1._1._1._3,
          s._3 + h(l)("ignoreCase")._1._1._1._3,
          s._4 + h(l)("multiline")._1._1._1._3)
      })

    val s_1 = lset_string.foldLeft[AbsString](StrBot)((_s, l) => _s + h(l)("@primitive")._2._1._5)
    val s_2 = b
    val s_3 = n
    val s_4 = n2

    val s_5 = (s_src.getSingle, b_g.getSingle, b_i.getSingle, b_m.getSingle) match {
      case (Some(s), Some(g), Some(i), Some(m)) => {
        val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")
        AbsString.alpha("/"+s+"/"+flags)
      }
      case _ if s_src </ StrBot && b_g </ BoolBot && b_i </ BoolBot && b_m </ BoolBot => StrTop
      case _ => StrBot
    }

    // TODO default toString semantics for an Object value.
    val s_6 = others.getSingle match {
      case Some(s) => 
       if(Shell.params.command ==ShellParameters.CMD_WEBAPP_BUG_DETECTOR)
         AbsString.alpha("[object "+s+"]")
       else StrTop
      case None if others <= StrBot => StrBot
      case None => StrTop
    }
    PValue(s_1) + PValue(s_2) + PValue(s_3) + PValue(s_4) + PValue(s_5) + PValue(s_6)
  }

  def objToPrimitive(objs:LocSet, hint:String): PValue = {
    if (Config.preAnalysis) {
      PreHelper.objToPrimitive(objs, hint)
    } else {
      if(objs.isEmpty)	PValueBot
      else {
        hint match {
          case "Number" =>	PValue(NumTop)
          case "String" =>	PValue(StrTop)
        }
      }
    }
  }

  def objToPrimitive_better(h: Heap, objs:LocSet, hint:String): PValue = {
    if(objs.isEmpty)	PValueBot
    else {
      hint match {
        case "Number" =>
          PValue(toNumber(defaultValueNumber(h, objs))) //PValue(NumTop)
        case "String" =>	PValue(defaultToString(h, objs))
      }
    }
  }

  def toPrimitive(v: Value): PValue = {
    if (Config.preAnalysis) {
      PreHelper.toPrimitive(v)
    } else {
      v._1 + objToPrimitive(v._2, "String")
    }
  }

  def toPrimitive_better(h: Heap, v: Value): PValue = {
    v._1 + objToPrimitive_better(h, v._2, "String")
  }

  // v_env is either LocSet or NullTop
  def NewPureLocal(v_env: Value, lset_this: LocSet): Obj = {
    if (Config.preAnalysis) {
      PreHelper.NewPureLocal(v_env, lset_this)
    } else {
      Obj.empty.
        update("@env", PropValue(v_env)).
        update("@this", PropValue(Value(lset_this))).
        update("@exception", PropValueBot).
        update("@exception_all", PropValueBot).
        update("@return", PropValueUndefTop)
    }
  }

  def NewRegExp(source: AbsString, g: AbsBool, i: AbsBool, m: AbsBool): Obj = {
    Obj.empty.
      update("@class", PropValue(AbsString.alpha("RegExp"))).
      update("@proto", PropValue(ObjectValue(Value(newSystemLoc("RegExpProto", Recent)), BoolFalse, BoolFalse, BoolFalse))).
      update("@extensible", PropValue(BoolTrue)).
      update("source", PropValue(ObjectValue(Value(source), BoolFalse, BoolFalse, BoolFalse))).
      update("global", PropValue(ObjectValue(Value(g), BoolFalse, BoolFalse, BoolFalse))).
      update("ignoreCase", PropValue(ObjectValue(Value(i), BoolFalse, BoolFalse, BoolFalse))).
      update("multiline", PropValue(ObjectValue(Value(m), BoolFalse, BoolFalse, BoolFalse))).
      update("lastIndex", PropValue(ObjectValue(AbsNumber.alpha(0), BoolTrue, BoolFalse, BoolFalse)))
  }

  def CollectProps(h: Heap, lset: LocSet): Set[String] = {
    if (lset.size != 1) {
      throw new InternalError("not a concrete case")
    } else {
      val l = lset.head
      if (isOldLoc(l)) throw new InternalError("not a concrete case")
      val o = h(l)
      val v_proto = o("@proto")._1._1

      val list =
        if (v_proto._1._2 </ NullBot) Set()
        else CollectProps(h, v_proto._2)

      val myset = o.getProps.filter(s => {
        val enum = o(s)._1._3
        enum.getSingle match {
          case Some(b) => b
          case None => throw new InternalError("not a concrete case")
        }})

      list ++ myset
    }
  }

  def CollectOwnProps(h: Heap, lset: LocSet): Set[String] = {
    if (lset.size != 1) {
      throw new InternalError("not a concrete case")
    } else {
      val l = lset.head
      if (isOldLoc(l)) throw new InternalError("not a concrete case")
      val o = h(l)

      o.getProps.filter(s => {
        val enum = o(s)._1._3
        enum.getSingle match {
          case Some(b) => b
          case None if enum.isBottom => false
          case _ => throw new InternalError("not a concrete case")
        }})
    }
  }

  def heapDiff(orig: Heap, diff: Heap, isPrint: Boolean): Set[(Loc, Option[String])] = {
    val locs = orig.map.keySet ++ diff.map.keySet
    locs.foldLeft(Set[(Loc, Option[String])]())((lpset, loc) => {
      // check original heap for loc
      orig.map.get(loc) match {
        case Some(o_orig) =>
          diff.map.get(loc) match {
            case Some(o_diff) =>
              val props = o_orig.getAllProps ++ o_diff.getAllProps
              props.foldLeft(lpset)((lpset, prop) => {
                val o_prop = o_orig(prop)
                val d_prop = o_diff(prop)
                val o_abs = o_orig.domIn(prop)
                val d_abs = o_diff.domIn(prop)
                if(!((o_prop <= d_prop) && (d_prop <= o_prop) && (o_abs <= d_abs) && (d_abs <= o_abs))) {
                  if(isPrint) {
                    println("\n===================================================================")
                    println("* " + DomainPrinter.printLoc(loc) + "("+prop+") has different value")
                    println("  orignal heap with location : " + DomainPrinter.printLoc(loc))
                    println("     " +DomainPrinter.printLoc(loc)+"("+prop+") ObjValue ["+o_prop._1._2+","+o_prop._1._3+","+o_prop._1._4+"]-> "+ DomainPrinter.printValue(o_prop._1._1))
                    println("  diff heap with location : " + DomainPrinter.printLoc(loc))
                    println("     " +DomainPrinter.printLoc(loc)+"("+prop+") ObjValue ["+d_prop._1._2+","+d_prop._1._3+","+d_prop._1._4+"]-> "+ DomainPrinter.printValue(d_prop._1._1))
                    println("===================================================================")
                  }
                  lpset + ((loc,Some(prop)))
                } else lpset
              })
            // loc is removed loc... possible?
            case None =>
              if(isPrint) println("* Location " + DomainPrinter.printLoc(loc) + " does not exist in diff Heap")
              lpset + ((loc, None))
          }
        // loc is newly added loc
        case None =>
          if(orig <= HeapBot) {
            // ignore initial steps
            lpset
          } else {
            if(isPrint) println("* Location " + DomainPrinter.printLoc(loc) + " does not exist in orig Heap")
            lpset + ((loc,None))
          }
      }
    })
  }
  
  private val locclone = Shell.params.opt_LocClone
  // for Address refinement
  private var ccCount = 0
  private var callContextMap: Map[CallContext, Int] = Map[CallContext, Int]()
  private val maxProgramAddr = newProgramAddr() 
  private val shift = if(locclone) (() => {
    var maxAddr = maxProgramAddr 
    var retShift = 0
    while (maxAddr > 0) {
      maxAddr = maxAddr >> 1
      retShift = retShift + 1
    }
    // Set Mask value in domain/package.
    setMaskValue(retShift + 1) 
    retShift + 1
  })() else 0



  def extendAddr(addr: Int, n: Int): Int = addr | (n << shift)
 
  def callContextToNumber(cc: CallContext): Int = {
    if (callContextMap contains cc) callContextMap(cc)
    else {
      val old = ccCount
      callContextMap = callContextMap + (cc -> old)
      ccCount = ccCount + 1
      old
    }
  }


}
