/*******************************************************************************
    Copyright (c) 2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/
package kr.ac.kaist.jsaf.analysis.typing.models.Tizen

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._


import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.models.AbsInternalFunc
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue

object TIZENAlarmRelative extends Tizen {
  val name = "AlarmRelative"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")

  /* constructor */
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto)
  )
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.AlarmRelative.constructor")),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("getRemainingSeconds", AbsBuiltinFunc("tizen.AlarmRelative.getRemainingSeconds", 0))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.AlarmRelative.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val delay = getArgValue(h, ctx, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val es =
            if (delay._1._4 </ NumTop) {
              Set[WebAPIException](TypeMismatchError)
            }
            else TizenHelper.TizenExceptionBot
          val o_new = ObjEmpty.update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENAlarmRelative.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("id", PropValue(ObjectValue(Value(NullTop), T, T, T)))
          val (h_1, es1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              // case for "new tizen.AlarmRelative(delay)"
              val o_new2 = o_new.
                update("delay", PropValue(ObjectValue(Value(delay._1._4), F, T, T))).
                update("period", PropValue(ObjectValue(Value(NullTop), T, T, T)))
              val h_1 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new2))
              (h_1, TizenHelper.TizenExceptionBot)

            case Some(n) if n >= 2 =>
              val period = getArgValue(h, ctx, args, "1")
              // case for "new tizen.AlarmRelative(delay, period)"
              val o_new2 = o_new.
                update("delay", PropValue(ObjectValue(Value(delay._1._4), F, T, T))).
                update("period", PropValue(ObjectValue(Value(period._1._4), F, T, T)))
              val h_1 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new2))
              (h_1, TizenHelper.TizenExceptionBot)
            case _ => {
              (h, TizenHelper.TizenExceptionBot)
            }
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es1)
          ((Helper.ReturnStore(h_1, Value(lset_this)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.AlarmRelative.getRemainingSeconds" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val h_1 =
            if (lset_this.exists((l) => Helper.Proto(h, l, AbsString.alpha("id"))._1._5 </ StrBot)) {
              Helper.ReturnStore(h, Value(NumTop))
            }
            else {
              Helper.ReturnStore(h, Value(NullTop))
            }
          val est = Set[WebAPIException](SecurityError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((h_1, ctx), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }
  override def getDefMap(): Map[String, AccessFun] = {
    Map()
  }
  override def getUseMap(): Map[String, AccessFun] = {
    Map()
  }
}