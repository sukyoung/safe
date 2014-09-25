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

object TIZENMessageAttachment extends Tizen {
  private val name = "MessageAttachment"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.MessageAttachment.constructor")),
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
      ("tizen.MessageAttachment.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val v_1 = getArgValue(h, ctx, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val es =
            if (v_1._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_new = Obj.empty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENMessageAttachment.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("id", PropValue(ObjectValue(Value(NullTop), F, T, T))).
            update("messageId", PropValue(ObjectValue(Value(NullTop), F, T, T)))
          val (h_2, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              val o_new2 = o_new.
                update("mimeType", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("filePath", PropValue(ObjectValue(Value(v_1._1._5), F, T, T)))
              val h_2 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new2))
              (h_2, Set[WebAPIException](TypeMismatchError))
            case Some(n) if n >= 2 =>
              val v_2 = getArgValue(h, ctx, args, "1")
              val es_1 =
                if (v_2._1._2 </NullTop || v_2._1._5 </ StrTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_new2 = o_new.
                update("mimeType", PropValue(ObjectValue(Value(PValue(UndefBot, v_2._1._2, BoolBot, NumBot, v_2._1._5)), F, T, T))).
                update("filePath", PropValue(ObjectValue(Value(v_1._1._5), F, T, T)))
              val h_2 = lset_this.foldLeft(h)((_h, l) => _h.update(l, o_new2))
              (h_2, es_1)
            case _ => {
              (h, TizenHelper.TizenExceptionBot)
            }
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1)
          ((Helper.ReturnStore(h_2, Value(lset_this)), ctx), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}