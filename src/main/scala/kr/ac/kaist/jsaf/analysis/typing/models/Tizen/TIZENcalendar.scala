/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.Tizen

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._



import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T, _}


object TIZENCalendar extends Tizen {
  val name = "Calendar"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("get", AbsBuiltinFunc("tizen.Calendar.get", 1)),
    ("add", AbsBuiltinFunc("tizen.Calendar.add", 1)),
    ("addBatch", AbsBuiltinFunc("tizen.Calendar.addBatch", 3)),
    ("update", AbsBuiltinFunc("tizen.Calendar.update", 2)),
    ("updateBatch", AbsBuiltinFunc("tizen.Calendar.updateBatch", 4)),
    ("remove", AbsBuiltinFunc("tizen.Calendar.remove", 1)),
    ("removeBatch", AbsBuiltinFunc("tizen.Calendar.removeBatch", 3)),
    ("find", AbsBuiltinFunc("tizen.Calendar.find", 4)),
    ("addChangeListener", AbsBuiltinFunc("tizen.Calendar.addChangeListener", 1)),
    ("removeChangeListener", AbsBuiltinFunc("tizen.Calendar.removeChangeListener", 1))
  )
  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.Calendar.get" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val (b_1, es_1) = TizenHelper.instanceOf(h, v, Value(TIZENCalendarEventId.loc_proto))
          val es_2 =
            if (b_1._1._3 <= F && v._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val h_1 =
            if (b_1._1._3 <= T)
              Helper.ReturnStore(h, Value(TIZENcalendarObj.loc_calevent))
            else h
          val h_2 =
            if (v._1._5 </ StrBot)
              Helper.ReturnStore(h, Value(TIZENcalendarObj.loc_caltask))
            else h
          val est = Set[WebAPIException](SecurityError, NotSupportedError, UnknownError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_1 + h_2, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.Calendar.add" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")
          val (b_1, es) = TizenHelper.instanceOf(h_1, v_1, Value(TIZENCalendarItem.loc_proto))
          val es_1 =
            if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENCalendarEventId.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("rid", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("uid", PropValue(ObjectValue(Value(StrTop), F, T, T)))
          val h_2 = h_1.update(l_r1, o_new)
          val h_3 = v_1._2.foldLeft(h_2)((_h, l) => {
            _h + Helper.PropStore(_h, l, AbsString.alpha("id"), Value(PValue(StrTop), LocSet(l_r1)))
          })
          val est = Set[WebAPIException](SecurityError, NotSupportedError, UnknownError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h_3, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.Calendar.addBatch" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
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
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
          val v_1 = getArgValue(h_3, ctx_3, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_3, ctx_3, args, "length"))
          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENCalendarEventId.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("rid", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("uid", PropValue(ObjectValue(Value(StrTop), F, T, T)))
          val h_4 = h_3.update(l_r1, o_new)

          val (h_5, es_1) = v_1._2.foldLeft(h_4, TizenHelper.TizenExceptionBot)((_hs, ll) => {
            val n_length = Operator.ToUInt32(Helper.Proto(_hs._1, ll, AbsString.alpha("length")))
            val (h_, ess) = n_length.getAbsCase match {
              case AbsBot =>
                (_hs._1, TizenHelper.TizenExceptionBot)
              case _ => AbsNumber.getUIntSingle(n_length) match {
                case Some(n) => {
                  val (h__, es__) = (0 until n.toInt).foldLeft((_hs._1, TizenHelper.TizenExceptionBot))((_he, i) => {
                    val vi = Helper.Proto(_he._1, ll, AbsString.alpha(i.toString))
                    val (b_1, esj) = TizenHelper.instanceOf(_he._1, vi, Value(TIZENCalendarItem.loc_proto))
                    val esi =
                      if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    val h = vi._2.foldLeft(_he._1)((hh, lj) => {
                      hh + Helper.PropStore(hh, lj, AbsString.alpha("id"), Value(PValue(StrTop), LocSet(l_r1)))
                    })
                    (h,
                      _he._2 ++ esj ++ esi)
                  })
                  (h__, es__)
                }
                case _ => {
                  val vi = Helper.Proto(_hs._1, ll, AbsString.alpha("@default_number"))
                  val (b_1, esj) = TizenHelper.instanceOf(_hs._1, vi, Value(TIZENCalendarItem.loc_proto))
                  val esi =
                    if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  val h = vi._2.foldLeft(_hs._1)((hh, lj) => {
                    hh + Helper.PropStore(hh, lj, AbsString.alpha("id"), Value(PValue(StrTop), LocSet(l_r1)))
                  })
                  (h,
                    esi ++ esj)
                }
              }
            }
            (_hs._1 + h_, _hs._2 ++ ess)
          })

          val (h_6, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_5, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_5, ctx_3, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_5, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(v_1._2), T, T, T)))
              val h_6 = h_5.update(l_r2, o_arr)
              val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("CalendarItemArraySuccessCB"), Value(v_2._2), Value(l_r2))
              (h_7, es_2)
            case Some(n) if n >= 3 =>
              val v_2 = getArgValue(h_5, ctx_3, args, "1")
              val v_3 = getArgValue(h_5, ctx_3, args, "2")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_5, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_5, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(v_1._2), T, T, T)))
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_6 = h_5.update(l_r2, o_arr).update(l_r3, o_arr2)
              val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("CalendarItemArraySuccessCB"), Value(v_2._2), Value(l_r2))
              val h_8 = TizenHelper.addCallbackHandler(h_7, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r3))
              (h_8, es_2 ++ es_3)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_6, ctx_3), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.Calendar.update" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val (b_1, es) = TizenHelper.instanceOf(h, v_1, Value(TIZENCalendarItem.loc_proto))
          val es_1 =
            if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_2 = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h, ctx, args, "1")
              val es_3 =
                if (v_2._1._3 </ BoolTop) Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              es_3
            case _ => {
              TizenHelper.TizenExceptionBot
            }
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError, UnknownError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ es_2 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.Calendar.updateBatch" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))

          val es_1 = v_1._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
            val n_length = Operator.ToUInt32(Helper.Proto(h_1, ll, AbsString.alpha("length")))
            val ess = n_length.getAbsCase match {
              case AbsBot =>
                TizenHelper.TizenExceptionBot
              case _ => AbsNumber.getUIntSingle(n_length) match {
                case Some(n) => {
                  val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                    val vi = Helper.Proto(h_1, ll, AbsString.alpha(i.toString))
                    val (b_1, esj) = TizenHelper.instanceOf(h_1, vi, Value(TIZENCalendarItem.loc_proto))
                    val esi =
                      if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    _e ++ esj ++ esi
                  })
                  es__
                }
                case _ => {
                  val vi = Helper.Proto(h_1, ll, AbsString.alpha("@default_number"))
                  val (b_1, esj) = TizenHelper.instanceOf(h_1, vi, Value(TIZENCalendarItem.loc_proto))
                  val esi =
                    if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  esi ++ esj
                }
              }
            }
            _es ++ ess
          })

          val (h_2, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_1, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              (h_2, es_2)
            case Some(n) if n == 3 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val v_3 = getArgValue(h_1, ctx_1, args, "2")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r1))
              (h_4, es_2 ++ es_3)
            case Some(n) if n >= 4 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val v_3 = getArgValue(h_1, ctx_1, args, "2")
              val v_4 = getArgValue(h_1, ctx_1, args, "3")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_4 =
                if (v_4._1._3 </ BoolTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r1))
              (h_4, es_2 ++ es_3 ++ es_4)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.Calendar.remove" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val (b_1, es) = TizenHelper.instanceOf(h, v_1, Value(TIZENCalendarEventId.loc_proto))
          val es_1 =
            if (v_1._1._5 </ StrTop && b_1._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, NotSupportedError, UnknownError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.Calendar.removeBatch" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))

          val es_1 = v_1._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
            val n_length = Operator.ToUInt32(Helper.Proto(h_1, ll, AbsString.alpha("length")))
            val ess = n_length.getAbsCase match {
              case AbsBot =>
                TizenHelper.TizenExceptionBot
              case _ => AbsNumber.getUIntSingle(n_length) match {
                case Some(n) => {
                  val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                    val vi = Helper.Proto(h_1, ll, AbsString.alpha(i.toString))
                    val (b_1, esj) = TizenHelper.instanceOf(h_1, vi, Value(TIZENCalendarEventId.loc_proto))
                    val esi =
                      if (b_1._1._3 <= F && vi._1._5 </ StrTop)
                        Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    _e ++ esj ++ esi
                  })
                  es__
                }
                case _ => {
                  val vi = Helper.Proto(h_1, ll, AbsString.alpha("@default_number"))
                  val (b_1, esj) = TizenHelper.instanceOf(h_1, vi, Value(TIZENCalendarEventId.loc_proto))
                  val esi =
                    if (b_1._1._3 <= F && vi._1._5 </ StrTop)
                      Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  esi ++ esj
                }
              }
            }
            _es ++ ess
          })

          val (h_2, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_1, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              (h_2, es_2)
            case Some(n) if n >= 3 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val v_3 = getArgValue(h_1, ctx_1, args, "2")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r1))
              (h_4, es_2 ++ es_3)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.Calendar.find" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val v_1 = getArgValue(h_2, ctx_2, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val es_1 =
            if (v_1._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
            update("0", PropValue(ObjectValue(Value(TIZENcalendarObj.loc_calitemarr), T, T, T)))
          val h_3 = h_2.update(l_r1, o_arr)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("CalendarItemArraySuccessCB"), Value(v_1._2), Value(l_r1))

          val (h_5, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2)
            case Some(n) if n == 3 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_1, es_3) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENAttributeFilter.loc_proto))
              val (b_2, es_4) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENAttributeRangeFilter.loc_proto))
              val (b_3, es_5) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENCompositeFilter.loc_proto))
              val es_6 =
                if (b_1._1._3 <= F && b_2._1._3 <= F && b_3._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6)
            case Some(n) if n >= 4 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val v_4 = getArgValue(h_4, ctx_2, args, "3")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_1, es_3) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENAttributeFilter.loc_proto))
              val (b_2, es_4) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENAttributeRangeFilter.loc_proto))
              val (b_3, es_5) = TizenHelper.instanceOf(h_4, v_3, Value(TIZENCompositeFilter.loc_proto))
              val es_6 =
                if (b_1._1._3 <= F && b_2._1._3 <= F && b_3._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_4, es_7) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENSortMode.loc_proto))
              val es_8 =
                if (b_4._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6 ++ es_7 ++ es_8)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))

        }
        )),
      ("tizen.Calendar.addChangeListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
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
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
          val v_1 = getArgValue(h_3, ctx_3, args, "0")
          val es_1 =
            if (v_1._2.exists((l) => Helper.IsCallable(h_3, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_4, es_2) = v_1._2.foldLeft((h_3, TizenHelper.TizenExceptionBot))((_he, l) => {
            val v1 = Helper.Proto(_he._1, l, AbsString.alpha("onitemsadded"))
            val v2 = Helper.Proto(_he._1, l, AbsString.alpha("onitemsupdated"))
            val v3 = Helper.Proto(_he._1, l, AbsString.alpha("onitemsremoved"))
            val es1 =
              if (v1._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val es2 =
              if (v2._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val es3 =
              if (v3._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENcalendarObj.loc_calitemarr), T, T, T)))
            val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENcalendarObj.loc_calitemarr), T, T, T)))
            val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENcalendarObj.loc_calitemidarr), T, T, T)))
            val h_4 = _he._1.update(l_r1, o_arr).update(l_r2, o_arr1).update(l_r3, o_arr2)
            val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("CalChangeCB.onitemsadded"), Value(v1._2), Value(l_r1))
            val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("CalChangeCB.onitemsupdated"), Value(v2._2), Value(l_r2))
            val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("CalChangeCB.onitemsremoved"), Value(v3._2), Value(l_r3))
            (h_7, _he._2 ++ es1 ++ es2 ++ es3)
          })
          val est = Set[WebAPIException](SecurityError, NotSupportedError, UnknownError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((Helper.ReturnStore(h_4, Value(NumTop)), ctx_3), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.Calendar.removeChangeListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val es =
            if (v_1._1._4 </ NumTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, NotSupportedError, UnknownError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
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
