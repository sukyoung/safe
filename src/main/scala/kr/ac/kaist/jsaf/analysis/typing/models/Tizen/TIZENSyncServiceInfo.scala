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
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context

object TIZENSyncServiceInfo extends Tizen {
  private val name = "SyncServiceInfo"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.SyncServiceInfo.constructor")),
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
       ("tizen.SyncServiceInfo.constructor" -> (
         (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
           val lset_this = h(SinglePureLocalLoc)("@this")._2._2
           val v_1 = getArgValue(h, ctx, args, "0")
           val v_2 = getArgValue(h, ctx, args, "1")
           val v_3 = getArgValue(h, ctx, args, "2")
           val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
           val es_1 =
             if (v_1._1._3 <= BoolBot) Set[WebAPIException](TypeMismatchError)
             else TizenHelper.TizenExceptionBot
           val es_2 =
             if (v_2._1._5 != AbsString.alpha("CONTACT") && v_2._1._5 != AbsString.alpha("EVENT"))
               Set[WebAPIException](TypeMismatchError)
             else TizenHelper.TizenExceptionBot
           val es_3 =
             if (v_3._1._5 <= StrBot) Set[WebAPIException](TypeMismatchError)
             else TizenHelper.TizenExceptionBot
           val o_new = Obj.empty.
             update("@class", PropValue(AbsString.alpha("Object"))).
             update("@proto", PropValue(ObjectValue(Value(TIZENSyncServiceInfo.loc_proto), F, F, F))).
             update("@extensible", PropValue(T)).
             update("enable", PropValue(ObjectValue(Value(v_1._1._3), F, T, T))).
             update("serviceType", PropValue(ObjectValue(Value(v_2._1._5), F, T, T))).
             update("serverDatabaseUri", PropValue(ObjectValue(Value(v_3._1._5), F, T, T)))
           val (h_2, es) = AbsNumber.getUIntSingle(n_arglen) match {
             case Some(n) if n <= 2 =>
               (h, Set[WebAPIException](TypeMismatchError))
             case Some(n) if n == 3 =>
               val o_new2 = o_new.
                 update("id", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                 update("password", PropValue(ObjectValue(Value(NullTop), F, T, T)))
               val h_2 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new2))
               (h_2, TizenHelper.TizenExceptionBot)
             case Some(n) if n == 4 =>
               val v_4 = getArgValue(h, ctx, args, "3")
               val es1 =
                 if (v_4._1._5 <= StrBot) Set[WebAPIException](TypeMismatchError)
                 else TizenHelper.TizenExceptionBot
               val o_new2 = o_new.
                 update("id", PropValue(ObjectValue(Value(v_4._1._5), F, T, T))).
                 update("password", PropValue(ObjectValue(Value(NullTop), F, T, T)))
               val h_2 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new2))
               (h_2, es1)
             case Some(n) if n == 5 =>
               val v_4 = getArgValue(h, ctx, args, "3")
               val v_5 = getArgValue(h, ctx, args, "4")
               val es1 =
                 if (v_4._1._5 <= StrBot) Set[WebAPIException](TypeMismatchError)
                 else TizenHelper.TizenExceptionBot
               val es2 =
                 if (v_5._1._5 <= StrBot) Set[WebAPIException](TypeMismatchError)
                 else TizenHelper.TizenExceptionBot
               val o_new2 = o_new.
                 update("id", PropValue(ObjectValue(Value(v_4._1._5), F, T, T))).
                 update("password", PropValue(ObjectValue(Value(v_5._1._5), F, T, T)))
               val h_2 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new2))
               (h_2, es1 ++ es2)
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