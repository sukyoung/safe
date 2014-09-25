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

object TIZENCalendarTask extends Tizen {
  private val name = "CalendarTask"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  val loc_parent = TIZENCalendarItem.loc_proto
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.CalendarTask.constructor")),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_parent), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue)))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.CalendarTask.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val l_r3 = addrToLoc(addr3, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_4, ctx_4) = Helper.Oldify(h_2, ctx_2, addr3)
          val n_arglen = Operator.ToUInt32(getArgValue(h_4, ctx_4, args, "length"))

          val o_new = Obj.empty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENCalendarEvent.loc_proto), F, F, F))).
            update("@extensible", PropValue(T))

          val (h_5, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 0 =>
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(0))
              val o_arr3 = Helper.NewArrayObject(AbsNumber.alpha(0))
              val h_5 = h_4.update(l_r1, o_arr).update(l_r2, o_arr2).update(l_r3, o_arr3)
              val o_new2 = o_new.
                update("id", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("calendarId", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("lastModificationDate", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("description", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T))).
                update("summary", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T))).
                update("isAllDay", PropValue(ObjectValue(Value(F), T, T, T))).
                update("startDate", PropValue(ObjectValue(Value(UndefTop), T, T, T))).
                update("duration", PropValue(ObjectValue(Value(UndefTop), T, T, T))).
                update("location", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T))).
                update("geolocation", PropValue(ObjectValue(Value(UndefTop), T, T, T))).
                update("organizer", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T))).
                update("visibility", PropValue(ObjectValue(Value(AbsString.alpha("PUBLIC")), T, T, T))).
                update("status", PropValue(ObjectValue(Value(AbsString.alpha("TENTATIVE")), T, T, T))).
                update("priority", PropValue(ObjectValue(Value(AbsString.alpha("LOW")), T, T, T))).
                update("alarms", PropValue(ObjectValue(Value(l_r3), T, T, T))).
                update("categories", PropValue(ObjectValue(Value(l_r1), T, T, T))).
                update("attendees", PropValue(ObjectValue(Value(l_r2), T, T, T))).
                update("dueDate", PropValue(ObjectValue(Value(UndefTop), F, T, T))).
                update("completedDate", PropValue(ObjectValue(Value(UndefTop), T, T, T))).
                update("progress", PropValue(ObjectValue(Value(AbsNumber.alpha(0)), T, T, T)))
              val h_6 = lset_this.foldLeft(h_5)((_h, l) => _h.update(l, o_new2))
              (h_6, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 1 =>
              val v = getArgValue(h_4, ctx_4, args, "0")
              val es =
                if (v._1 <= PValueTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(0))
              val o_arr3 = Helper.NewArrayObject(AbsNumber.alpha(0))
              val h_5 = h_4.update(l_r1, o_arr).update(l_r2, o_arr2).update(l_r3, o_arr3)
              val (obj, es_1) = v._2.foldLeft((o_new, TizenHelper.TizenExceptionBot))((_o, l) => {
                val v_1 = Helper.Proto(h_5, l, AbsString.alpha("dueDate"))
                val v_2 = Helper.Proto(h_5, l, AbsString.alpha("completedDate"))
                val v_3 = Helper.Proto(h_5, l, AbsString.alpha("progress"))
                val v_4 = Helper.Proto(h_5, l, AbsString.alpha("description"))
                val v_5 = Helper.Proto(h_5, l, AbsString.alpha("summary"))
                val v_6 = Helper.Proto(h_5, l, AbsString.alpha("isAllDay"))
                val v_7 = Helper.Proto(h_5, l, AbsString.alpha("startDate"))
                val v_8 = Helper.Proto(h_5, l, AbsString.alpha("duration"))
                val v_9 = Helper.Proto(h_5, l, AbsString.alpha("location"))
                val v_10 = Helper.Proto(h_5, l, AbsString.alpha("geolocation"))
                val v_11 = Helper.Proto(h_5, l, AbsString.alpha("organizer"))
                val v_12 = Helper.Proto(h_5, l, AbsString.alpha("visibility"))
                val v_13 = Helper.Proto(h_5, l, AbsString.alpha("status"))
                val v_14 = Helper.Proto(h_5, l, AbsString.alpha("priority"))
                val v_15 = Helper.Proto(h_5, l, AbsString.alpha("alarms"))
                val v_16 = Helper.Proto(h_5, l, AbsString.alpha("categories"))
                val v_17 = Helper.Proto(h_5, l, AbsString.alpha("attendees"))
                val (b_1, es_1) = TizenHelper.instanceOf(h_5, v_1, Value(TIZENTZDate.loc_proto))
                val es_2 =
                  if (v_1._1._1 </ UndefTop && b_1._1._3 <= F)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_1 =
                  if (b_1._1._3 <= T)
                    _o._1.update("dueDate", PropValue(ObjectValue(Value(v_1._2), T, T, T)))
                  else _o._1.update("dueDate", PropValue(ObjectValue(Value(UndefTop), T, T, T)))
                val (b_2, es_3) = TizenHelper.instanceOf(h_5, v_2, Value(TIZENTZDate.loc_proto))
                val es_4 =
                  if (v_2._1._1 </ UndefTop && b_2._1._3 <= F)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_2 =
                  if (b_2._1._3 <= T)
                    _o._1.update("completedDate", PropValue(ObjectValue(Value(v_2._2), T, T, T)))
                  else _o._1.update("completedDate", PropValue(ObjectValue(Value(UndefTop), T, T, T)))
                val es_5 =
                  if (v_3._1._1 </ UndefTop && v_3._1._4 </ NumTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_3 =
                  if (v_3._1._4 </ NumBot)
                    _o._1.update("progress", PropValue(ObjectValue(Value(v_3._1._4), T, T, T)))
                  else _o._1.update("progress", PropValue(ObjectValue(Value(AbsNumber.alpha(0)), T, T, T)))
                val es_6 =
                  if (v_4._1._1 </ UndefTop && v_4._1._5 </ StrTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_4 =
                  if (v_4._1._5 </ StrBot)
                    _o._1.update("description", PropValue(ObjectValue(Value(v_4._1._5), T, T, T)))
                  else _o._1.update("description", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T)))
                val es_7 =
                  if (v_5._1._1 </ UndefTop && v_5._1._5 </ StrTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_5 =
                  if (v_5._1._5 </ StrBot)
                    _o._1.update("summary", PropValue(ObjectValue(Value(v_5._1._5), T, T, T)))
                  else _o._1.update("summary", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T)))
                val es_8 =
                  if (v_6._1._1 </ UndefTop && v_6._1._3 </ BoolTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_6 =
                  if (v_6._1._3 </ BoolBot)
                    _o._1.update("isAllDay", PropValue(ObjectValue(Value(v_6._1._3), T, T, T)))
                  else _o._1.update("isAllDay", PropValue(ObjectValue(Value(F), T, T, T)))
                val (b_3, es_9) = TizenHelper.instanceOf(h_5, v_7, Value(TIZENTZDate.loc_proto))
                val es_10 =
                  if (v_7._1._1 </ UndefTop && b_3._1._3 <= F)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_7 =
                  if (b_3._1._3 <= T)
                    _o._1.update("startDate", PropValue(ObjectValue(Value(v_7._2), T, T, T)))
                  else _o._1.update("startDate", PropValue(ObjectValue(Value(UndefTop), T, T, T)))
                val (b_4, es_11) = TizenHelper.instanceOf(h_5, v_8, Value(TIZENTimeDuration.loc_proto))
                val es_12 =
                  if (v_8._1._1 </ UndefTop && b_4._1._3 <= F)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_8 =
                  if (b_4._1._3 <= T)
                    _o._1.update("duration", PropValue(ObjectValue(Value(v_8._2), T, T, T)))
                  else _o._1.update("duration", PropValue(ObjectValue(Value(UndefTop), T, T, T)))
                val es_13 =
                  if (v_9._1._1 </ UndefTop && v_9._1._5 </ StrTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_9 =
                  if (v_9._1._5 </ StrBot)
                    _o._1.update("location", PropValue(ObjectValue(Value(v_9._1._5), T, T, T)))
                  else _o._1.update("location", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T)))
                val (b_5, es_14) = TizenHelper.instanceOf(h_5, v_10, Value(TIZENSimpleCoordinates.loc_proto))
                val es_15 =
                  if (v_10._1._1 </ UndefTop && b_5._1._3 <= F)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_10 =
                  if (b_5._1._3 <= T)
                    _o._1.update("geolocation", PropValue(ObjectValue(Value(v_10._2), T, T, T)))
                  else _o._1.update("geolocation", PropValue(ObjectValue(Value(UndefTop), T, T, T)))
                val es_16 =
                  if (v_11._1._1 </ UndefTop && v_11._1._5 </ StrTop)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_11 =
                  if (v_11._1._5 </ StrBot)
                    _o._1.update("organizer", PropValue(ObjectValue(Value(v_11._1._5), T, T, T)))
                  else _o._1.update("organizer", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T)))
                val es_17 =
                  if (v_12._1._1 <= UndefBot && v_12._1._5 </ AbsString.alpha("PUBLIC") && v_12._1._5 </ AbsString.alpha("PRIVATE") &&
                    v_12._1._5 </ AbsString.alpha("CONFIDENTIAL"))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_12 =
                  if (v_12._1._1 </ UndefBot)
                    _o._1.update("visibility", PropValue(ObjectValue(Value(AbsString.alpha("PUBLIC")), T, T, T)))
                  else _o._1.update("visibility", PropValue(ObjectValue(Value(v_12._1._5), T, T, T)))
                val es_18 =
                  if (v_13._1._1 </ UndefBot || (v_13._1._5 </ AbsString.alpha("TENTATIVE") && v_13._1._5 </ AbsString.alpha("CONFIRMED") &&
                    v_13._1._5 </ AbsString.alpha("CANCELLED") && v_13._1._5 </ AbsString.alpha("NEEDS_ACTION") &&
                    v_13._1._5 </ AbsString.alpha("IN_PROCESS") && v_13._1._5 </ AbsString.alpha("COMPLETED")))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_13 =
                  if (v_13._1._1 </ UndefBot)
                    _o._1.update("status", PropValue(ObjectValue(Value(AbsString.alpha("TENTATIVE")), T, T, T)))
                  else _o._1.update("status", PropValue(ObjectValue(Value(v_13._1._5), T, T, T)))
                val es_19 =
                  if (v_14._1._1 </ UndefTop && v_14._1._5 </ AbsString.alpha("HIGH") && v_14._1._5 </ AbsString.alpha("MEDIUM") &&
                    v_14._1._5 </ AbsString.alpha("LOW"))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_14 =
                  if (v_14._1._1 </ UndefBot)
                    _o._1.update("priority", PropValue(ObjectValue(Value(AbsString.alpha("LOW")), T, T, T)))
                  else _o._1.update("priority", PropValue(ObjectValue(Value(v_14._1._5), T, T, T)))
                val (o_15, es_20) =
                  if (v_15._1._1 <= UndefBot) {
                    val es_ = v_15._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                      val n_length = Operator.ToUInt32(Helper.Proto(h_5, ll, AbsString.alpha("length")))
                      val ess = n_length.getAbsCase match {
                        case AbsBot => TizenHelper.TizenExceptionBot
                        case _ => AbsNumber.getUIntSingle(n_length) match {
                          case Some(n) => {
                            val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                              val vi = Helper.Proto(h_5, ll, AbsString.alpha(i.toString))
                              val (bi, esi) = TizenHelper.instanceOf(h_5, vi, Value(TIZENCalendarAlarm.loc_proto))
                              val esii =
                                if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                                else TizenHelper.TizenExceptionBot
                              _e ++ esi ++ esii
                            })
                            es__
                          }
                          case _ => {
                            val vi = Helper.Proto(h_5, ll, AbsString.alpha(Str_default_number))
                            val (bi, esi) = TizenHelper.instanceOf(h_5, vi, Value(TIZENCalendarAlarm.loc_proto))
                            val esii =
                              if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                              else TizenHelper.TizenExceptionBot
                            esi ++ esii
                          }
                        }
                      }
                      _es ++ ess
                    })
                    (_o._1.update("alarms", PropValue(ObjectValue(Value(v_15._2), T, T, T))), es_)
                  }
                  else (_o._1.update("alarms", PropValue(ObjectValue(Value(l_r3), T, T, T))), TizenHelper.TizenExceptionBot)
                val (o_16, es_21) =
                  if (v_16._1._1 <= UndefBot) {
                    val es_ = v_16._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                      val n_length = Operator.ToUInt32(Helper.Proto(h_5, ll, AbsString.alpha("length")))
                      val ess = n_length.getAbsCase match {
                        case AbsBot => TizenHelper.TizenExceptionBot
                        case _ => AbsNumber.getUIntSingle(n_length) match {
                          case Some(n) => {
                            val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                              val vi = Helper.Proto(h_5, ll, AbsString.alpha(i.toString))
                              val esi =
                                if (vi._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
                                else TizenHelper.TizenExceptionBot
                              _e ++ esi
                            })
                            es__
                          }
                          case _ => {
                            val vi = Helper.Proto(h_5, ll, AbsString.alpha(Str_default_number))
                            val esi =
                              if (vi._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
                              else TizenHelper.TizenExceptionBot
                            esi
                          }
                        }
                      }
                      _es ++ ess
                    })
                    (_o._1.update("categories", PropValue(ObjectValue(Value(v_16._2), T, T, T))), es_)
                  }
                  else (_o._1.update("categories", PropValue(ObjectValue(Value(l_r1), T, T, T))), TizenHelper.TizenExceptionBot)
                val (o_17, es_22) =
                  if (v_17._1._1 <= UndefBot) {
                    val es_ = v_17._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                      val n_length = Operator.ToUInt32(Helper.Proto(h_5, ll, AbsString.alpha("length")))
                      val ess = n_length.getAbsCase match {
                        case AbsBot => TizenHelper.TizenExceptionBot
                        case _ => AbsNumber.getUIntSingle(n_length) match {
                          case Some(n) => {
                            val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                              val vi = Helper.Proto(h_5, ll, AbsString.alpha(i.toString))
                              val (bi, esi) = TizenHelper.instanceOf(h_5, vi, Value(TIZENCalendarAttendee.loc_proto))
                              val esii =
                                if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                                else TizenHelper.TizenExceptionBot
                              _e ++ esi ++ esii
                            })
                            es__
                          }
                          case _ => {
                            val vi = Helper.Proto(h_5, ll, AbsString.alpha(Str_default_number))
                            val (bi, esi) = TizenHelper.instanceOf(h_5, vi, Value(TIZENCalendarAttendee.loc_proto))
                            val esii =
                              if (bi._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                              else TizenHelper.TizenExceptionBot
                            esi ++ esii
                          }
                        }
                      }
                      _es ++ ess
                    })
                    (_o._1.update("attendees", PropValue(ObjectValue(Value(v_17._2), T, T, T))), es_)
                  }
                  else (_o._1.update("attendees", PropValue(ObjectValue(Value(l_r2), T, T, T))), TizenHelper.TizenExceptionBot)

                (_o._1 + o_1 + o_2 + o_3 + o_4 + o_5 + o_6 + o_7 + o_8 + o_9 + o_10 + o_11 + o_12 + o_13 + o_14 + o_15 + o_16 + o_17,
                  _o._2 ++ es_1 ++ es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6 ++ es_7 ++ es_8 ++ es_9 ++ es_10 ++ es_11 ++ es_12 ++
                    es_13 ++ es_14 ++ es_15 ++ es_16 ++ es_17 ++ es_18 ++ es_19 ++ es_20 ++ es_21 ++ es_22)
              })

              val o_new2 = obj.
                update("id", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("calendarId", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("lastModificationDate", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("isDetached", PropValue(ObjectValue(Value(F), F, T, T)))
              val h_6 = lset_this.foldLeft(h_5)((_h, l) => _h.update(l, o_new2))
              (h_6, es ++ es_1)
            case Some(n) if n == 2 =>
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(0))
              val o_arr3 = Helper.NewArrayObject(AbsNumber.alpha(0))
              val h_5 = h_4.update(l_r1, o_arr).update(l_r2, o_arr2).update(l_r3, o_arr3)
              val o_new2 = o_new.
                update("id", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("calendarId", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("lastModificationDate", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("description", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T))).
                update("summary", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T))).
                update("isAllDay", PropValue(ObjectValue(Value(F), T, T, T))).
                update("startDate", PropValue(ObjectValue(Value(UndefTop), T, T, T))).
                update("duration", PropValue(ObjectValue(Value(UndefTop), T, T, T))).
                update("location", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T))).
                update("geolocation", PropValue(ObjectValue(Value(UndefTop), T, T, T))).
                update("organizer", PropValue(ObjectValue(Value(AbsString.alpha("")), T, T, T))).
                update("visibility", PropValue(ObjectValue(Value(AbsString.alpha("PUBLIC")), T, T, T))).
                update("status", PropValue(ObjectValue(Value(AbsString.alpha("TENTATIVE")), T, T, T))).
                update("priority", PropValue(ObjectValue(Value(AbsString.alpha("LOW")), T, T, T))).
                update("alarms", PropValue(ObjectValue(Value(l_r3), T, T, T))).
                update("categories", PropValue(ObjectValue(Value(l_r1), T, T, T))).
                update("attendees", PropValue(ObjectValue(Value(l_r2), T, T, T))).
                update("dueDate", PropValue(ObjectValue(Value(UndefTop), F, T, T))).
                update("completedDate", PropValue(ObjectValue(Value(UndefTop), T, T, T))).
                update("progress", PropValue(ObjectValue(Value(AbsNumber.alpha(0)), T, T, T)))
              val h_6 = lset_this.foldLeft(h_5)((_h, l) => _h.update(l, o_new2))
              (h_6, TizenHelper.TizenExceptionBot)
            case _ => {
              (h_4, TizenHelper.TizenExceptionBot)
            }
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1)
          ((Helper.ReturnStore(h_5, Value(lset_this)), ctx_4), (he + h_e, ctxe + ctx_e))
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
