/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.Tizen

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._


import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T, _}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._

import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap

object TIZENStatusNotification extends Tizen {
  private val name = "StatusNotification"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.StatusNotification.constructor")),
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
      ("tizen.StatusNotification.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_3, ctx_3) = Helper.Oldify(h_1, ctx_1, addr2)
          val n_arglen = Operator.ToUInt32(getArgValue(h_3, ctx_3, args, "length"))
          val v_1 = getArgValue(h_3, ctx_3, args, "0")
          val v_2 = getArgValue(h_3, ctx_3, args, "1")
          val es_1 =
            if (v_1._1._5 </ AbsString.alpha("SIMPLE") && v_1._1._5 </ AbsString.alpha("THUMBNAIL") &&
              v_1._1._5 </ AbsString.alpha("ONGOING") && v_1._1._5 </ AbsString.alpha("PROGRESS"))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_2 =
            if (v_2._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot

          val o_new = Obj.empty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENStatusNotification.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("statusType", PropValue(ObjectValue(Value(v_1._1._5), F, T, T))).
            update("title", PropValue(ObjectValue(Value(v_2._1._5), T, T, T)))

          val (h_4, es_3) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(0))
              val h_4 = h_3.update(l_r1, o_arr).update(l_r2, o_arr2)
              val o_new2 = o_new.
                update("id", PropValue(ObjectValue(Value(UndefTop), F, T, T))).
                update("type", PropValue(ObjectValue(Value(AbsString.alpha("STATUS")), F, T, T))).
                update("postedTime", PropValue(ObjectValue(Value(TIZENtizen.loc_date), F, T, T))).
                update("content", PropValue(ObjectValue(Value(NullTop), T, T, T))).
                update("iconPath", PropValue(ObjectValue(Value(NullTop), T, T, T))).
                update("subIconPath", PropValue(ObjectValue(Value(NullTop), T, T, T))).
                update("number", PropValue(ObjectValue(Value(NullTop), T, T, T))).
                update("detailInfo", PropValue(ObjectValue(Value(l_r1), T, T, T))).
                update("backgroundImagePath", PropValue(ObjectValue(Value(NullTop), T, T, T))).
                update("thumbnails", PropValue(ObjectValue(Value(l_r2), T, T, T))).
                update("soundPath", PropValue(ObjectValue(Value(NullTop), T, T, T))).
                update("vibration", PropValue(ObjectValue(Value(F), T, T, T))).
                update("appControl", PropValue(ObjectValue(Value(NullTop), T, T, T))).
                update("appId", PropValue(ObjectValue(Value(NullTop), T, T, T))).
                update("progressType", PropValue(ObjectValue(Value(AbsString.alpha("PERCENTAGE")), T, T, T))).
                update("progressValue", PropValue(ObjectValue(Value(NullTop), T, T, T)))
              val h_5 = lset_this.foldLeft(h_4)((_h, l) => _h.update(l, o_new2))
              (h_5, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 3 =>
              val v_3 = getArgValue(h_3, ctx_3, args, "2")
              val es =
                if (v_3._1 <= PValueTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(0))
              val h_4 = h_3.update(l_r1, o_arr).update(l_r2, o_arr2)
              val (obj, es_1) = v_3._2.foldLeft((o_new, TizenHelper.TizenExceptionBot))((_o, l) => {
                val v1 = Helper.Proto(h_4, l, AbsString.alpha("content"))
                val v2 = Helper.Proto(h_4, l, AbsString.alpha("iconPath"))
                val v3 = Helper.Proto(h_4, l, AbsString.alpha("soundPath"))
                val v4 = Helper.Proto(h_4, l, AbsString.alpha("vibration"))
                val v5 = Helper.Proto(h_4, l, AbsString.alpha("appControl"))
                val v6 = Helper.Proto(h_4, l, AbsString.alpha("appId"))
                val v7 = Helper.Proto(h_4, l, AbsString.alpha("progressType"))
                val v8 = Helper.Proto(h_4, l, AbsString.alpha("progressValue"))
                val v9 = Helper.Proto(h_4, l, AbsString.alpha("number"))
                val v10 = Helper.Proto(h_4, l, AbsString.alpha("subIconPath"))
                val v11 = Helper.Proto(h_4, l, AbsString.alpha("detailInfo"))
                val v12 = Helper.Proto(h_4, l, AbsString.alpha("backgroundImagePath"))
                val v13 = Helper.Proto(h_4, l, AbsString.alpha("thumbnails"))
                val es1 =
                  if (v1._1._1 </ UndefTop && v1._1._5 </ StrTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_1 =
                  if (v1._1._1 </ UndefBot)
                    _o._1.update("content", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                  else _o._1.update("content", PropValue(ObjectValue(Value(v1._1._5), T, T, T)))
                val es2 =
                  if (v2._1._1 </ UndefTop && v2._1._5 </ StrTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_2 =
                  if (v2._1._1 </ UndefBot)
                    _o._1.update("iconPath", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                  else _o._1.update("iconPath", PropValue(ObjectValue(Value(v2._1._5), T, T, T)))
                val es3 =
                  if (v3._1._1 </ UndefTop && v3._1._5 </ StrTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_3 =
                  if (v3._1._1 </ UndefBot)
                    _o._1.update("soundPath", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                  else _o._1.update("soundPath", PropValue(ObjectValue(Value(v3._1._5), T, T, T)))
                val es4 =
                  if (v4._1._1 </ UndefTop && v4._1._3 </ BoolTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_4 =
                  if (v4._1._1 </ UndefBot)
                    _o._1.update("vibration", PropValue(ObjectValue(Value(F), T, T, T)))
                  else _o._1.update("vibration", PropValue(ObjectValue(Value(v4._1._3), T, T, T)))
                val (b_2, es5) = TizenHelper.instanceOf(h_4, v5, Value(TIZENApplicationControl.loc_proto))
                val es6 =
                  if (v5._1._1 </ UndefTop && b_2._1._3 <= F)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_5 =
                  if (b_2._1._3 <= T)
                    _o._1.update("appControl", PropValue(ObjectValue(Value(v5._2), T, T, T)))
                  else _o._1.update("appControl", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                val es7 =
                  if (v6._1._1 </ UndefTop && v6._1._5 </ StrTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_6 =
                  if (v6._1._1 </ UndefBot)
                    _o._1.update("appId", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                  else _o._1.update("appId", PropValue(ObjectValue(Value(v6._1._5), T, T, T)))
                val es8 =
                  if (v7._1._1 </ UndefTop && v7._1._5 </ AbsString.alpha("PERCENTAGE") && v7._1._5 </ AbsString.alpha("BYTE"))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_7 =
                  if (v7._1._1 </ UndefBot)
                    _o._1.update("progressType", PropValue(ObjectValue(Value(AbsString.alpha("PERCENTAGE")), T, T, T)))
                  else _o._1.update("progressType", PropValue(ObjectValue(Value(v7._1._5), T, T, T)))
                val es9 =
                  if (v8._1._1 </ UndefTop && v8._1._4 </ NumTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_8 =
                  if (v8._1._1 </ UndefBot)
                    _o._1.update("progressValue", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                  else _o._1.update("progressValue", PropValue(ObjectValue(Value(v8._1._4), T, T, T)))
                val es10 =
                  if (v9._1._1 </ UndefTop && v9._1._4 </ NumTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_9 =
                  if (v9._1._1 </ UndefBot)
                    _o._1.update("number", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                  else _o._1.update("number", PropValue(ObjectValue(Value(v9._1._4), T, T, T)))
                val es11 =
                  if (v10._1._1 </ UndefTop && v10._1._5 </ StrTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_10 =
                  if (v10._1._1 </ UndefBot)
                    _o._1.update("subIconPath", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                  else _o._1.update("subIconPath", PropValue(ObjectValue(Value(v10._1._5), T, T, T)))
                val (o_11, es12) =
                  if (v11._1._1 <= UndefBot) {
                    val es_ = v11._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                      val n_length = Operator.ToUInt32(Helper.Proto(h_4, ll, AbsString.alpha("length")))
                      val ess = n_length.getAbsCase match {
                        case AbsBot => TizenHelper.TizenExceptionBot
                        case _ => AbsNumber.getUIntSingle(n_length) match {
                          case Some(n) => {
                            val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                              val vi = Helper.Proto(h_4, ll, AbsString.alpha(i.toString))
                              val (bi, esi) = TizenHelper.instanceOf(h_4, vi, Value(TIZENNotificationDetailInfo.loc_proto))
                              val esii =
                                if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                                else TizenHelper.TizenExceptionBot
                              _e ++ esi ++ esii
                            })
                            es__
                          }
                          case _ => {
                            val vi = Helper.Proto(h_4, ll, AbsString.alpha(Str_default_number))
                            val (bi, esi) = TizenHelper.instanceOf(h_4, vi, Value(TIZENNotificationDetailInfo.loc_proto))
                            val esii =
                              if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                              else TizenHelper.TizenExceptionBot
                            esi ++ esii
                          }
                        }
                      }
                      _es ++ ess
                    })
                    (_o._1.update("detailInfo", PropValue(ObjectValue(Value(v11._2), T, T, T))), es_)
                  }
                  else (_o._1.update("detailInfo", PropValue(ObjectValue(Value(l_r1), T, T, T))), TizenHelper.TizenExceptionBot)
                val es13 =
                  if (v12._1._1 </ UndefTop && v12._1._5 </ StrTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_12 =
                  if (v12._1._1 </ UndefBot)
                    _o._1.update("backgroundImagePath", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                  else _o._1.update("backgroundImagePath", PropValue(ObjectValue(Value(v12._1._5), T, T, T)))
                val (o_13, es14) =
                  if (v13._1._1 <= UndefBot) {
                    val es_ = v11._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                      val n_length = Operator.ToUInt32(Helper.Proto(h_4, ll, AbsString.alpha("length")))
                      val ess = n_length.getAbsCase match {
                        case AbsBot => TizenHelper.TizenExceptionBot
                        case _ => AbsNumber.getUIntSingle(n_length) match {
                          case Some(n) => {
                            val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                              val vi = Helper.Proto(h_4, ll, AbsString.alpha(i.toString))
                              val esi =
                                if (vi._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
                                else TizenHelper.TizenExceptionBot
                              _e ++ esi
                            })
                            es__
                          }
                          case _ => {
                            val vi = Helper.Proto(h_4, ll, AbsString.alpha(Str_default_number))
                            val esi =
                              if (vi._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
                              else TizenHelper.TizenExceptionBot
                            esi
                          }
                        }
                      }
                      _es ++ ess
                    })
                    (_o._1.update("thumbnails", PropValue(ObjectValue(Value(v13._2), T, T, T))), es_)
                  }
                  else (_o._1.update("thumbnails", PropValue(ObjectValue(Value(l_r2), T, T, T))), TizenHelper.TizenExceptionBot)
                (o_1 + o_2 + o_3 + o_4 + o_5 + o_6 + o_7 + o_8 + o_9 + o_10 + o_11 + o_12 + o_13,
                  _o._2 ++ es1 ++ es2 ++ es3 ++ es4 ++ es5 ++ es6 ++ es7 ++ es8 ++ es9 ++ es10 ++ es11 ++ es12 ++
                    es13 ++ es14)
              })
              val o_new2 = obj.
                update("id", PropValue(ObjectValue(Value(UndefTop), F, T, T))).
                update("type", PropValue(ObjectValue(Value(AbsString.alpha("STATUS")), F, T, T))).
                update("postedTime", PropValue(ObjectValue(Value(TIZENtizen.loc_date), F, T, T)))
              val h_5 = lset_this.foldLeft(h_4)((_h, l) => _h.update(l, o_new2))
              (h_5, es ++ es_1)
            case _ => {
              (h_3, TizenHelper.TizenExceptionBot)
            }
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3)
          ((Helper.ReturnStore(h_4, Value(lset_this)), ctx_3), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
