/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.Tizen

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._


import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing._


object TIZENDownloadRequest extends Tizen {
  private val name = "DownloadRequest"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.DownloadRequest.constructor")),
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
      ("tizen.DownloadRequest.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_2, ctx_2) = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_2, ctx_2, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))

          val o_new = Obj.empty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENDownloadRequest.loc_proto), F, F, F))).
            update("@extensible", PropValue(T))

          val (h_3, es) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 0 =>
              (h_2, Set[WebAPIException](TypeMismatchError))
            case Some(n) if n == 1 =>
              val h_3 = h_2.update(l_r1, Obj.empty)
              val o_new2 = o_new.
                update("url", PropValue(ObjectValue(Value(Helper.toString(v_1._1)), T, T, T))).
                update("destination", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T))).
                update("fileName", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T))).
                update("networkType", PropValue(ObjectValue(Value(AbsString.alpha("ALL")), T, T, T))).
                update("httpHeader", PropValue(ObjectValue(Value(l_r1), T, T, T)))
              val h_4 = lset_this.foldLeft(h_3)((_h, l) => _h.update(l, o_new2))
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_2, ctx_2, args, "1")
              val h_3 = h_2.update(l_r1, Obj.empty)
              val o_new2 = o_new.
                update("url", PropValue(ObjectValue(Value(Helper.toString(v_1._1)), T, T, T))).
                update("destination", PropValue(ObjectValue(Value(Helper.toString(v_2._1)), T, T, T))).
                update("fileName", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T))).
                update("networkType", PropValue(ObjectValue(Value(AbsString.alpha("ALL")), T, T, T))).
                update("httpHeader", PropValue(ObjectValue(Value(l_r1), T, T, T)))
              val h_4 = lset_this.foldLeft(h_3)((_h, l) => _h.update(l, o_new2))
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 3 =>
              val v_2 = getArgValue(h_2, ctx_2, args, "1")
              val v_3 = getArgValue(h_2, ctx_2, args, "2")
              val h_3 = h_2.update(l_r1, Obj.empty)
              val o_new2 = o_new.
                update("url", PropValue(ObjectValue(Value(Helper.toString(v_1._1)), T, T, T))).
                update("destination", PropValue(ObjectValue(Value(Helper.toString(v_2._1)), T, T, T))).
                update("fileName", PropValue(ObjectValue(Value(Helper.toString(v_3._1)), T, T, T))).
                update("networkType", PropValue(ObjectValue(Value(AbsString.alpha("ALL")), T, T, T))).
                update("httpHeader", PropValue(ObjectValue(Value(l_r1), T, T, T)))
              val h_4 = lset_this.foldLeft(h_3)((_h, l) => _h.update(l, o_new2))
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 4 =>
              val v_2 = getArgValue(h_2, ctx_2, args, "1")
              val v_3 = getArgValue(h_2, ctx_2, args, "2")
              val v_4 = getArgValue(h_2, ctx_2, args, "3")
              val h_3 = h_2.update(l_r1, Obj.empty)
              val o_new2 =
                if (v_4._1._5 </ AbsString.alpha("CELLULAR") && v_4._1._5 </ AbsString.alpha("WIFI") &&
                  v_4._1._5 </ AbsString.alpha("ALL"))
                  o_new.update("networkType", PropValue(ObjectValue(Value(AbsString.alpha("ALL")), T, T, T)))
                else o_new.update("networkType", PropValue(ObjectValue(Value(v_4._1._5), T, T, T)))
              val o_new3 = o_new2.
                update("url", PropValue(ObjectValue(Value(Helper.toString(v_1._1)), T, T, T))).
                update("destination", PropValue(ObjectValue(Value(Helper.toString(v_2._1)), T, T, T))).
                update("fileName", PropValue(ObjectValue(Value(Helper.toString(v_3._1)), T, T, T))).
                update("httpHeader", PropValue(ObjectValue(Value(l_r1), T, T, T)))
              val h_4 = lset_this.foldLeft(h_3)((_h, l) => _h.update(l, o_new3))
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 5 =>
              val v_2 = getArgValue(h_2, ctx_2, args, "1")
              val v_3 = getArgValue(h_2, ctx_2, args, "2")
              val v_4 = getArgValue(h_2, ctx_2, args, "3")
              val v_5 = getArgValue(h_2, ctx_2, args, "4")
              val es1 =
                if (v_5._2.exists((ll) => Helper.IsObject(h_2, ll) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_new2 =
                if (v_4._1._5 </ AbsString.alpha("CELLULAR") && v_4._1._5 </ AbsString.alpha("WIFI") &&
                  v_4._1._5 </ AbsString.alpha("ALL"))
                  o_new.update("networkType", PropValue(ObjectValue(Value(AbsString.alpha("ALL")), T, T, T)))
                else o_new.update("networkType", PropValue(ObjectValue(Value(v_4._1._5), T, T, T)))
              val o_new3 = o_new2.
                update("url", PropValue(ObjectValue(Value(Helper.toString(v_1._1)), T, T, T))).
                update("destination", PropValue(ObjectValue(Value(Helper.toString(v_2._1)), T, T, T))).
                update("fileName", PropValue(ObjectValue(Value(Helper.toString(v_3._1)), T, T, T))).
                update("httpHeader", PropValue(ObjectValue(Value(v_5._2), T, T, T)))
              val h_3 = lset_this.foldLeft(h_2)((_h, l) => _h.update(l, o_new3))
              (h_3, es1)
            case _ => {
              (h_2, TizenHelper.TizenExceptionBot)
            }
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((Helper.ReturnStore(h_3, Value(lset_this)), ctx_2), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
