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

import kr.ac.kaist.jsaf.analysis.typing.models.builtin.BuiltinArray
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap

object TIZENcallhistory extends Tizen {
  private val name = "callhistory"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_callhistory
  val loc_proto = newSystemRecentLoc(name + "Proto")

  val loc_chentry: Loc = newSystemLoc("CHEntry", Old)
  val loc_chentryarr: Loc = newSystemLoc("CHEntryArr", Old)
  val loc_remoteparty: Loc = newSystemLoc("RemoteParty", Old)
  val loc_remotepartyarr: Loc = newSystemLoc("RemotePartyArr", Old)

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto), (loc_chentry, prop_chentry_ins),
    (loc_chentryarr, prop_chentryarr_ins), (loc_remoteparty, prop_remoteparty_ins),
    (loc_remotepartyarr, prop_remotepartyarr_ins)
  )
  /* constructor or object*/
  private val prop_obj: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T)))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("find", AbsBuiltinFunc("tizen.callhistory.find", 6)),
    ("remove", AbsBuiltinFunc("tizen.callhistory.remove", 1)),
    ("removeBatch", AbsBuiltinFunc("tizen.callhistory.removeBatch", 3)),
    ("removeAll", AbsBuiltinFunc("tizen.callhistory.removeAll", 2)),
    ("addChangeListener", AbsBuiltinFunc("tizen.callhistory.addChangeListener", 1)),
    ("removeChangeListener", AbsBuiltinFunc("tizen.callhistory.removeChangeListener", 1))
  )

  private val prop_chentry_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENCallHistoryEntry.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("uid", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("type", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("features", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("remoteParties", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("startTime", AbsConstValue(PropValue(ObjectValue(Value(TIZENtizen.loc_date), T, T, T)))),
    ("duration", AbsConstValue(PropValue(ObjectValue(Value(UInt), T, T, T)))),
    ("direction", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T))))
  )

  private val prop_chentryarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(loc_chentry), T, T, T))))
  )

  private val prop_remoteparty_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENRemoteParty.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("remoteParty", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("personId", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T))))
  )

  private val prop_remotepartyarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(loc_remoteparty), T, T, T))))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.callhistory.find" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
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
            update("0", PropValue(ObjectValue(Value(TIZENcallhistory.loc_chentryarr), T, T, T)))
          val h_3 = h_2.update(l_r1, o_arr)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("CHEntryArraySuccessCB"), Value(v_1._2), Value(l_r1))

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
            case Some(n) if n == 4 =>
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
            case Some(n) if n == 5 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val v_4 = getArgValue(h_4, ctx_2, args, "3")
              val v_5 = getArgValue(h_4, ctx_2, args, "4")
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
              val es_9 =
                if (v_5._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6 ++ es_7 ++ es_8 ++ es_9)
            case Some(n) if n >= 6 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val v_4 = getArgValue(h_4, ctx_2, args, "3")
              val v_5 = getArgValue(h_4, ctx_2, args, "4")
              val v_6 = getArgValue(h_4, ctx_2, args, "5")
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
              val es_9 =
                if (v_5._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_10 =
                if (v_6._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6 ++ es_7 ++ es_8 ++ es_9 ++ es_10)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))

        }
        )),
      ("tizen.callhistory.remove" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val (b_1, es) = TizenHelper.instanceOf(h, v_1, Value(TIZENCallHistoryEntry.loc_proto))
          val es_1 =
            if (b_1._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](InvalidValuesError, UnknownError, SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.callhistory.removeBatch" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
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
                    val (b_1, esj) = TizenHelper.instanceOf(h_1, vi, Value(TIZENCallHistoryEntry.loc_proto))
                    val esi =
                      if (b_1._1._3 <= F)
                        Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    _e ++ esj ++ esi
                  })
                  es__
                }
                case _ => {
                  val vi = Helper.Proto(h_1, ll, AbsString.alpha(Str_default_number))
                  val (b_1, esj) = TizenHelper.instanceOf(h_1, vi, Value(TIZENCallHistoryEntry.loc_proto))
                  val esi =
                    if (b_1._1._3 <= F)
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
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
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
      ("tizen.callhistory.removeAll" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))

          val (h_2, es) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              val v_1 = getArgValue(h_1, ctx_1, args, "0")
              val es_1 =
                if (v_1._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(v_1._2), Value(UndefTop))
              (h_2, es_1)
            case Some(n) if n >= 2 =>
              val v_1 = getArgValue(h_1, ctx_1, args, "0")
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val es_1 =
                if (v_1._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_1._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r1))
              (h_4, es_1 ++ es_2)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.callhistory.addChangeListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val l_r3 = addrToLoc(addr3, Recent)
          val l_r4 = addrToLoc(addr4, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
          val (h_4, ctx_4) = Helper.Oldify(h_3, ctx_3, addr4)
          val v_1 = getArgValue(h_4, ctx_4, args, "0")
          val es_1 =
            if (v_1._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_5, es_2) = v_1._2.foldLeft((h_4, TizenHelper.TizenExceptionBot))((_he, l) => {
            val v1 = Helper.Proto(_he._1, l, AbsString.alpha("onadded"))
            val v2 = Helper.Proto(_he._1, l, AbsString.alpha("onchanged"))
            val v3 = Helper.Proto(_he._1, l, AbsString.alpha("onremoved"))
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
            val o_strarr = Helper.NewArrayObject(UInt).update(Str_default_number, PropValue(ObjectValue(Value(StrTop), T, T, T)))
            val h_5 = _he._1.update(l_r1, o_strarr)
            val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENcallhistory.loc_chentryarr), T, T, T)))
            val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENcallhistory.loc_chentryarr), T, T, T)))
            val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(l_r1), T, T, T)))
            val h_6 = h_5.update(l_r2, o_arr).update(l_r3, o_arr1).update(l_r4, o_arr2)
            val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("CHChangeCB.onadded"), Value(v1._2), Value(l_r2))
            val h_8 = TizenHelper.addCallbackHandler(h_7, AbsString.alpha("CHChangeCB.onchanged"), Value(v2._2), Value(l_r3))
            val h_9 = TizenHelper.addCallbackHandler(h_8, AbsString.alpha("CHChangeCB.onremoved"), Value(v3._2), Value(l_r4))
            (h_9, _he._2 ++ es1 ++ es2 ++ es3)
          })
          val est = Set[WebAPIException](InvalidValuesError, UnknownError, SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((Helper.ReturnStore(h_5, Value(UInt)), ctx_4), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.callhistory.removeChangeListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val es =
            if (v_1._1._4 </ NumTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](InvalidValuesError, UnknownError, SecurityError, NotSupportedError)
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

object TIZENCallHistoryEntry extends Tizen {
  private val name = "CallHistoryEntry"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
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

object TIZENRemoteParty extends Tizen {
  private val name = "RemoteParty"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T)))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
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
