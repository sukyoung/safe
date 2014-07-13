/*******************************************************************************
    Copyright (c) 2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.Tizen

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._


import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr}
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T, _}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.models.AbsInternalFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context

object TIZENCalendarAlarm extends Tizen {
  val name = "CalendarAlarm"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.CalendarAlarm.constructor")),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T)))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.CalendarAlarm.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 = getArgValue(h, ctx, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val es =
            if (n_arglen == 0)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (b_1, es_1) = TizenHelper.instanceOf(h, v_1, Value(TIZENTZDate.loc_proto))
          val (b_2, es_2) = TizenHelper.instanceOf(h, v_1, Value(TIZENTimeDuration.loc_proto))
          val es_3 =
            if (b_1._1._3 <= F && b_2._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot

          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENBookmarkFolder.loc_proto), F, F, F))).
            update("@extensible", PropValue(T))
          val o_new2 =
            if (b_1._1._3 <= T && b_2._1._3 <= F)
              o_new.update("absoluteDate", PropValue(ObjectValue(Value(v_1._2), F, T, T))).
                    update("before", PropValue(ObjectValue(Value(UndefTop), F, T, T))).
                    update("method", PropValue(ObjectValue(Value(v_2._1._5), F, T, T)))
            else o_new
          val o_new3 =
            if (b_2._1._3 <= T && b_1._1._3 <= F)
              o_new2.update("absoluteDate", PropValue(ObjectValue(Value(UndefTop), F, T, T))).
                update("before", PropValue(ObjectValue(Value(v_1._2), F, T, T))).
                update("method", PropValue(ObjectValue(Value(v_2._1._5), F, T, T)))
            else o_new2

          val (h_2, es_4) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              val o_new4 = o_new3.update("description", PropValue(ObjectValue(Value(UndefTop), F, T, T)))
              val h_2 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new4))
              (h_2, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 3 =>
              val v_3 = getArgValue(h, ctx, args, "2")
              val es_5 =
                if (v_3._1 </ PValueTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_new4 = o_new3.update("description", PropValue(ObjectValue(Value(Helper.toString(v_3._1)), F, T, T)))
              val h_2 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new4))
              (h_2, es_5)
            case _ => {
              (h, TizenHelper.TizenExceptionBot)
            }
          }
          val h_3 =
            if (n_arglen == 0) HeapBot
            else Helper.ReturnStore(h_2, Value(lset_this))
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ es_2 ++ es_3 ++ es_4)
          ((h_3, ctx), (he + h_e, ctxe + ctx_e))
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