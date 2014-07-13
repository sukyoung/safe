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

object TIZENAttributeFilter extends Tizen {
  val name = "AttributeFilter"
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
    ("@construct",               AbsInternalFunc("tizen.AttributeFilter.constructor")),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.AttributeFilter.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val attrname = getArgValue(h, ctx, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val o_new = ObjEmpty.update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENAttributeFilter.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("attributeName", PropValue(ObjectValue(Value(Helper.toString(attrname._1)), F, T, T)))
          val (h_1, es) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              // case for "new tizen.AttributeFilter(attrname)"
              val o_new2 = o_new.
                update("matchFlag", PropValue(ObjectValue(Value(AbsString.alpha("EXACTLY")), F, T, T))).
                update("matchValue", PropValue(ObjectValue(Value(UndefTop), F, T, T)))
              val h_1 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new2))
              (h_1, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              // case for "new tizen.AttributeFilter(attrname, matchflg)"
              val matchflg = getArgValue(h, ctx, args, "1")
              /* matchflg checking */
              val es_1 =
                if (matchflg._1._5 != AbsString.alpha("EXACTLY") &&
                  matchflg._1._5 != AbsString.alpha("FULLSTRING") &&
                  matchflg._1._5 != AbsString.alpha("CONTAINS") && matchflg._1._5 != AbsString.alpha("STARTSWITH") &&
                  matchflg._1._5 != AbsString.alpha("ENDSWITH") && matchflg._1._5 != AbsString.alpha("EXISTS"))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_new2 = o_new.
                update("matchFlag", PropValue(ObjectValue(Value(Helper.toString(matchflg._1)), F, T, T))).
                update("matchValue", PropValue(ObjectValue(Value(UndefTop), F, T, T)))
              val h_1 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new2))
              (h_1, es_1)
            case Some(n) if n == 3 =>
              // case for "new tizen.AttributeFilter(attrname, matchflg, matchval)"
              val matchflg = getArgValue(h, ctx, args, "1")
              val matchval = getArgValue(h, ctx, args, "2")
              /* matchflg checking */
              val es_1 =
                if (matchflg._1._3 <= BoolTop || matchflg._1._4 <= NumTop || (matchflg._1._5 != AbsString.alpha("EXACTLY") &&
                  matchflg._1._5 != AbsString.alpha("FULLSTRING") &&
                  matchflg._1._5 != AbsString.alpha("CONTAINS") && matchflg._1._5 != AbsString.alpha("STARTSWITH") &&
                  matchflg._1._5 != AbsString.alpha("ENDSWITH") && matchflg._1._5 != AbsString.alpha("EXISTS")))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_new2 = o_new.
                update("matchFlag", PropValue(ObjectValue(Value(matchflg._1._5), F, T, T))).
                update("matchValue", PropValue(ObjectValue(Value(PValue(matchval._1._1, matchval._1._2, matchval._1._3, matchval._1._4,
                matchval._1._5), matchval._2), F, T, T)))
              val h_1 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new2))
              (h_1, es_1)
            case _ => {
              (h, TizenHelper.TizenExceptionBot)
            }
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((Helper.ReturnStore(h_1, Value(lset_this)), ctx), (he + h_e, ctxe + ctx_e))
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