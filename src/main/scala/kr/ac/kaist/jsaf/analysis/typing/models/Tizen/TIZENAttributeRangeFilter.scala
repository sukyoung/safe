/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
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

object TIZENAttributeRangeFilter extends Tizen {
  val name = "AttributeRangeFilter"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")

  /* constructor */
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto)
  )
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.AttributeRangeFilter.constructor")),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("attributeName", AbsConstValue(PropValueUndefTop)),
    ("initialValue", AbsConstValue(PropValueUndefTop)),
    ("endValue", AbsConstValue(PropValueUndefTop))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.AttributeRangeFilter.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val attrname = getArgValue(h, ctx, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))

          val o_new = Obj.empty.update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENAttributeRangeFilter.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("attributeName", PropValue(ObjectValue(Value(Helper.toString(attrname._1)), F, T, T)))

          val (h_2, es) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              // case for "new tizen.AttributeRangeFilter(attrname)"
              val o_new2 = o_new.
                update("initialValue", PropValue(ObjectValue(Value(UndefTop), F, T, T))).
                update("endValue", PropValue(ObjectValue(Value(UndefTop), F, T, T)))
              val h_1 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new2))
              (h_1, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              // case for "new tizen.AttributeRangeFilter(attrname, matchflg)"
              val initVal = getArgValue(h, ctx, args, "1")
              val o_new2 = o_new.
                update("initialValue", PropValue(ObjectValue(Value(PValue(initVal._1._1, initVal._1._2, initVal._1._3,
                  initVal._1._4, initVal._1._5), initVal._2), F, T, T))).
                update("endValue", PropValue(ObjectValue(Value(UndefTop), F, T, T)))
              val h_1 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new2))
              (h_1, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 3 =>
              // case for "new tizen.AttributeRangeFilter(attrname, matchflg, matchval)"
              val initVal = getArgValue(h, ctx, args, "1")
              val endVal = getArgValue(h, ctx, args, "2")
              val o_new2 = o_new.
                update("initialValue", PropValue(ObjectValue(Value(PValue(initVal._1._1, initVal._1._2, initVal._1._3,
                initVal._1._4, initVal._1._5), initVal._2), F, T, T))).
                update("endValue", PropValue(ObjectValue(Value(PValue(endVal._1._1, endVal._1._2, endVal._1._3,
                endVal._1._4, endVal._1._5), endVal._2), F, T, T)))
              val h_1 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new2))
              (h_1, TizenHelper.TizenExceptionBot)
            case _ => {
              (h, TizenHelper.TizenExceptionBot)
            }
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((Helper.ReturnStore(h_2, Value(lset_this)), ctx), (he + h_e, ctxe + ctx_e))
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