/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
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
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue

object TIZENSyncProfileInfo extends Tizen {
  private val name = "SyncProfileInfo"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.SyncProfileInfo.constructor")),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue)))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.SyncProfileInfo.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val v_1 = getArgValue(h, ctx, args, "0")
          val v_2 = getArgValue(h, ctx, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val es_1 =
            if (v_1._1._5 <= StrBot) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (b_1, es_2) = TizenHelper.instanceOf(h, v_2, Value(TIZENSyncInfo.loc_proto))
          val es_3 =
            if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_new = Obj.empty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENSyncProfileInfo.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("profileId", PropValue(ObjectValue(Value(NullTop), F, T, T))).
            update("profileName", PropValue(ObjectValue(Value(v_1._1._5), F, T, T))).
            update("syncInfo", PropValue(ObjectValue(Value(v_2._2), F, T, T)))
          val (h_2, es) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n <= 1 =>
              (h, Set[WebAPIException](TypeMismatchError))
            case Some(n) if n == 2 =>
              val o_new2 = o_new.
                update("serviceInfo", PropValue(ObjectValue(Value(NullTop), F, T, T)))
              val h_2 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new2))
              (h_2, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 3 =>
              val v_3 = getArgValue(h, ctx, args, "2")
              val es1 = v_3._2.foldLeft(TizenHelper.TizenExceptionBot)((_e, ll) => {
                val n_length = Operator.ToUInt32(Helper.Proto(h, ll, AbsString.alpha("length")))
                val es2 = n_length.getAbsCase match {
                  case AbsBot => _e
                  case _ => AbsNumber.getUIntSingle(n_length) match {
                    case Some(n) => {
                      val e__ = (0 until n.toInt).foldLeft(_e)((__e, i) => {
                        val vi = Helper.Proto(h, ll, AbsString.alpha(i.toString))
                        val (b_2, es3) = TizenHelper.instanceOf(h, vi, Value(TIZENSyncServiceInfo.loc_proto))
                        val es4 =
                          if (b_2._1._3 <= F)
                            Set[WebAPIException](TypeMismatchError)
                          else TizenHelper.TizenExceptionBot
                        __e ++ es3 ++ es4
                      })
                      e__
                    }
                    case _ => {
                      val vi = Helper.Proto(h, ll, AbsString.alpha(Str_default_number))
                      val (b_2, es3) = TizenHelper.instanceOf(h, vi, Value(TIZENSyncServiceInfo.loc_proto))
                      val es4 =
                        if (b_2._1._3 <= F)
                          Set[WebAPIException](TypeMismatchError)
                        else TizenHelper.TizenExceptionBot
                      es3 ++ es4
                    }
                  }
                }
                _e ++ es2
              })
              val o_new2 = o_new.
                update("serviceInfo", PropValue(ObjectValue(Value(v_3._2), F, T, T)))
              val h_2 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new2))
              (h_2, es1)
            case _ => (h, TizenHelper.TizenExceptionBot)
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ es_2 ++ es_3)
          ((Helper.ReturnStore(h_2, Value(lset_this)), ctx), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}