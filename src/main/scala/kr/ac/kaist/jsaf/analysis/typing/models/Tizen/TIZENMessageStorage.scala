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


object TIZENMessageStorage extends Tizen {
  private val name = "MessageStorage"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("addDraftMessage", AbsBuiltinFunc("tizen.MessageStorage.addDraftMessage", 3)),
    ("findMessages", AbsBuiltinFunc("tizen.MessageStorage.findMessages", 6)),
    ("removeMessages", AbsBuiltinFunc("tizen.MessageStorage.removeMessages", 3)),
    ("updateMessages", AbsBuiltinFunc("tizen.MessageStorage.updateMessages", 3)),
    ("findConversations", AbsBuiltinFunc("tizen.MessageStorage.findConversations", 6)),
    ("removeConversations", AbsBuiltinFunc("tizen.MessageStorage.removeConversations", 3)),
    ("findFolders", AbsBuiltinFunc("tizen.MessageStorage.findFolders", 3)),
    ("addMessagesChangeListener", AbsBuiltinFunc("tizen.MessageStorage.addMessagesChangeListener", 2)),
    ("addConversationsChangeListener", AbsBuiltinFunc("tizen.MessageStorage.addConversationsChangeListener", 2)),
    ("addFoldersChangeListener", AbsBuiltinFunc("tizen.MessageStorage.addFoldersChangeListener", 2)),
    ("removeChangeListener", AbsBuiltinFunc("tizen.MessageStorage.removeChangeListener", 1))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.MessageStorage.addDraftMessage" -> (
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
          val (b_1, es_1) = TizenHelper.instanceOf(h_1, v_1, Value(TIZENMessage.loc_proto))
          val es_2 =
            if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot

          val (h_2, es) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val es1 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              (h_2, es1)
            case Some(n) if n >= 3 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val v_3 = getArgValue(h_1, ctx_1, args, "2")
              val es1 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es2 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr1)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r1))
              (h_4, es1 ++ es2)
            case _ =>
              (h_1, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ es_2 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.MessageStorage.findMessages" -> (
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
          val v_2 = getArgValue(h_2, ctx_2, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val (b_1, es_1) = TizenHelper.instanceOf(h_2, v_1, Value(TIZENAttributeFilter.loc_proto))
          val (b_2, es_2) = TizenHelper.instanceOf(h_2, v_1, Value(TIZENCompositeFilter.loc_proto))
          val (b_3, es_3) = TizenHelper.instanceOf(h_2, v_1, Value(TIZENAttributeRangeFilter.loc_proto))
          val es_4 =
            if (b_1._1._3 <= F && b_2._1._3 <= F && b_3._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_5 =
            if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
            update("0", PropValue(ObjectValue(Value(TIZENmessaging.loc_msgarr), T, T, T)))
          val h_3 = h_2.update(l_r1, o_arr1)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("MsgArraySuccessCB"), Value(v_2._2), Value(l_r1))

          val (h_5, es) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 3 =>
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val es1 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
              (h_6, es1)
            case Some(n) if n == 4 =>
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val v_4 = getArgValue(h_4, ctx_2, args, "3")
              val es1 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_4, es2) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENSortMode.loc_proto))
              val es3 =
                if (b_4._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
              (h_6, es1 ++ es2 ++ es3)
            case Some(n) if n == 5 =>
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val v_4 = getArgValue(h_4, ctx_2, args, "3")
              val v_5 = getArgValue(h_4, ctx_2, args, "4")
              val es1 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_4, es2) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENSortMode.loc_proto))
              val es3 =
                if (b_4._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es4 =
                if (v_5._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
              (h_6, es1 ++ es2 ++ es3 ++ es4)
            case Some(n) if n >= 6 =>
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val v_4 = getArgValue(h_4, ctx_2, args, "3")
              val v_5 = getArgValue(h_4, ctx_2, args, "4")
              val v_6 = getArgValue(h_4, ctx_2, args, "5")
              val es1 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_4, es2) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENSortMode.loc_proto))
              val es3 =
                if (b_4._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es4 =
                if (v_5._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es5 =
                if (v_6._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
              (h_6, es1 ++ es2 ++ es3 ++ es4 ++ es5)
            case _ =>
              (h_4, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ es_2 ++ es_3 ++ es_4 ++ es_5 ++ est)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.MessageStorage.removeMessages" -> (
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

          val es_ = v_1._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
            val n_length = Operator.ToUInt32(Helper.Proto(h_1, ll, AbsString.alpha("length")))
            val ess = n_length.getAbsCase match {
              case AbsBot =>
                TizenHelper.TizenExceptionBot
              case _ => AbsNumber.getUIntSingle(n_length) match {
                case Some(n) => {
                  val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                    val vi = Helper.Proto(h_1, ll, AbsString.alpha(i.toString))
                    val (b_1, esj) = TizenHelper.instanceOf(h_1, vi, Value(TIZENMessage.loc_proto))
                    val esi =
                      if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    _e ++ esj ++ esi
                  })
                  es__
                }
                case _ => {
                  val vi = Helper.Proto(h_1, ll, AbsString.alpha(Str_default_number))
                  val (b_1, esj) = TizenHelper.instanceOf(h_1, vi, Value(TIZENMessage.loc_proto))
                  val esi =
                    if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  esi ++ esj
                }
              }
            }
            _es ++ ess
          })

          val (h_2, es) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val es1 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              (h_2, es1)
            case Some(n) if n >= 3 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val v_3 = getArgValue(h_1, ctx_1, args, "2")
              val es1 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es2 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr1)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r1))
              (h_4, es1 ++ es2)
            case _ =>
              (h_1, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_ ++ es ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.MessageStorage.updateMessages" -> (
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

          val es_ = v_1._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
            val n_length = Operator.ToUInt32(Helper.Proto(h_1, ll, AbsString.alpha("length")))
            val ess = n_length.getAbsCase match {
              case AbsBot =>
                TizenHelper.TizenExceptionBot
              case _ => AbsNumber.getUIntSingle(n_length) match {
                case Some(n) => {
                  val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                    val vi = Helper.Proto(h_1, ll, AbsString.alpha(i.toString))
                    val (b_1, esj) = TizenHelper.instanceOf(h_1, vi, Value(TIZENMessage.loc_proto))
                    val esi =
                      if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    _e ++ esj ++ esi
                  })
                  es__
                }
                case _ => {
                  val vi = Helper.Proto(h_1, ll, AbsString.alpha(Str_default_number))
                  val esi =
                    if (vi._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  esi
                }
              }
            }
            _es ++ ess
          })

          val (h_2, es) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val es1 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              (h_2, es1)
            case Some(n) if n >= 3 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val v_3 = getArgValue(h_1, ctx_1, args, "2")
              val es1 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es2 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr1)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r1))
              (h_4, es1 ++ es2)
            case _ =>
              (h_1, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_ ++ es ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.MessageStorage.findConversations" -> (
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
          val v_2 = getArgValue(h_2, ctx_2, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val (b_1, es_1) = TizenHelper.instanceOf(h_2, v_1, Value(TIZENAttributeFilter.loc_proto))
          val (b_2, es_2) = TizenHelper.instanceOf(h_2, v_1, Value(TIZENCompositeFilter.loc_proto))
          val (b_3, es_3) = TizenHelper.instanceOf(h_2, v_1, Value(TIZENAttributeRangeFilter.loc_proto))
          val es_4 =
            if (b_1._1._3 <= F && b_2._1._3 <= F && b_3._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_5 =
            if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
            update("0", PropValue(ObjectValue(Value(TIZENmessaging.loc_msgconvarr), T, T, T)))
          val h_3 = h_2.update(l_r1, o_arr1)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("MsgConvArraySuccessCB"), Value(v_2._2), Value(l_r1))

          val (h_5, es) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 3 =>
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val es1 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
              (h_6, es1)
            case Some(n) if n == 4 =>
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val v_4 = getArgValue(h_4, ctx_2, args, "3")
              val es1 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_4, es2) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENSortMode.loc_proto))
              val es3 =
                if (b_4._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
              (h_6, es1 ++ es2 ++ es3)
            case Some(n) if n == 5 =>
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val v_4 = getArgValue(h_4, ctx_2, args, "3")
              val v_5 = getArgValue(h_4, ctx_2, args, "4")
              val es1 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_4, es2) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENSortMode.loc_proto))
              val es3 =
                if (b_4._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es4 =
                if (v_5._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
              (h_6, es1 ++ es2 ++ es3 ++ es4)
            case Some(n) if n >= 6 =>
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val v_4 = getArgValue(h_4, ctx_2, args, "3")
              val v_5 = getArgValue(h_4, ctx_2, args, "4")
              val v_6 = getArgValue(h_4, ctx_2, args, "5")
              val es1 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_4, es2) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENSortMode.loc_proto))
              val es3 =
                if (b_4._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es4 =
                if (v_5._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es5 =
                if (v_6._1._4 </ NumTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
              (h_6, es1 ++ es2 ++ es3 ++ es4 ++ es5)
            case _ =>
              (h_4, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ es_2 ++ es_3 ++ es_4 ++ es_5 ++ est)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.MessageStorage.removeConversations" -> (
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

          val es_ = v_1._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
            val n_length = Operator.ToUInt32(Helper.Proto(h_1, ll, AbsString.alpha("length")))
            val ess = n_length.getAbsCase match {
              case AbsBot =>
                TizenHelper.TizenExceptionBot
              case _ => AbsNumber.getUIntSingle(n_length) match {
                case Some(n) => {
                  val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                    val vi = Helper.Proto(h_1, ll, AbsString.alpha(i.toString))
                    val (b_1, esj) = TizenHelper.instanceOf(h_1, vi, Value(TIZENMessageConversation.loc_proto))
                    val esi =
                      if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    _e ++ esj ++ esi
                  })
                  es__
                }
                case _ => {
                  val vi = Helper.Proto(h_1, ll, AbsString.alpha(Str_default_number))
                  val esi =
                    if (vi._1._5 </ StrTop) Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  esi
                }
              }
            }
            _es ++ ess
          })

          val (h_2, es) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val es1 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              (h_2, es1)
            case Some(n) if n >= 3 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val v_3 = getArgValue(h_1, ctx_1, args, "2")
              val es1 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es2 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr1)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r1))
              (h_4, es1 ++ es2)
            case _ =>
              (h_1, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_ ++ es ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.MessageStorage.findFolders" -> (
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
          val v_2 = getArgValue(h_2, ctx_2, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val (b_1, es_1) = TizenHelper.instanceOf(h_2, v_1, Value(TIZENAttributeFilter.loc_proto))
          val (b_2, es_2) = TizenHelper.instanceOf(h_2, v_1, Value(TIZENCompositeFilter.loc_proto))
          val (b_3, es_3) = TizenHelper.instanceOf(h_2, v_1, Value(TIZENAttributeRangeFilter.loc_proto))
          val es_4 =
            if (b_1._1._3 <= F && b_2._1._3 <= F && b_3._1._3 <= F)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_5 =
            if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot

          val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
            update("0", PropValue(ObjectValue(Value(TIZENmessaging.loc_msgfolderarr), T, T, T)))
          val h_3 = h_2.update(l_r1, o_arr)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("MsgFolderArrSuccessCB"), Value(v_2._2), Value(l_r1))

          val (h_5, es) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 3 =>
              val v_3 = getArgValue(h_4, ctx_2, args, "2")
              val es1 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr1)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("MsgFolderArrSuccessCB"), Value(v_2._2), Value(l_r1))
              val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
              (h_7, es1)
            case _ =>
              (h_4, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ es_2 ++ es_3 ++ es_4 ++ es_5 ++ est)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.MessageStorage.addMessagesChangeListener" -> (
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

          val (h_2, es) = v_1._2.foldLeft((h_1, TizenHelper.TizenExceptionBot))((_he, l) => {
            val v1 = Helper.Proto(_he._1, l, AbsString.alpha("messagesadded"))
            val v2 = Helper.Proto(_he._1, l, AbsString.alpha("messagesupdated"))
            val v3 = Helper.Proto(_he._1, l, AbsString.alpha("messagesremoved"))
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
              update("0", PropValue(ObjectValue(Value(TIZENmessaging.loc_msgarr), T, T, T)))
            val h_2 = _he._1.update(l_r1, o_arr)
            val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("MsgsChangeCB.messagesadded"), Value(v1._2), Value(l_r1))
            val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("MsgsChangeCB.messagesupdated"), Value(v2._2), Value(l_r1))
            val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("MsgsChangeCB.messagesremoved"), Value(v3._2), Value(l_r1))
            (h_5, _he._2 ++ es1 ++ es2 ++ es3)
          })

          val ess = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n >= 2 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val (b_1, es_1) = TizenHelper.instanceOf(h_2, v_2, Value(TIZENAttributeFilter.loc_proto))
              val (b_2, es_2) = TizenHelper.instanceOf(h_2, v_2, Value(TIZENCompositeFilter.loc_proto))
              val (b_3, es_3) = TizenHelper.instanceOf(h_2, v_2, Value(TIZENAttributeRangeFilter.loc_proto))
              val es_4 =
                if (b_1._1._3 <= F && b_2._1._3 <= F && b_3._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              es_1 ++ es_2 ++ es_3 ++ es_4
            case _ => TizenHelper.TizenExceptionBot
          }
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ ess ++ est)
          ((Helper.ReturnStore(h_2, Value(NumTop)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.MessageStorage.addConversationsChangeListener" -> (
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

          val (h_2, es) = v_1._2.foldLeft((h_1, TizenHelper.TizenExceptionBot))((_he, l) => {
            val v1 = Helper.Proto(_he._1, l, AbsString.alpha("conversationsadded"))
            val v2 = Helper.Proto(_he._1, l, AbsString.alpha("conversationsupdated"))
            val v3 = Helper.Proto(_he._1, l, AbsString.alpha("conversationsremoved"))
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
              update("0", PropValue(ObjectValue(Value(TIZENmessaging.loc_msgconvarr), T, T, T)))
            val h_2 = _he._1.update(l_r1, o_arr)
            val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("MsgConvsChangeCB.conversationsadded"), Value(v1._2), Value(l_r1))
            val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("MsgConvsChangeCB.conversationsupdated"), Value(v2._2), Value(l_r1))
            val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("MsgConvsChangeCB.conversationsremoved"), Value(v3._2), Value(l_r1))
            (h_5, _he._2 ++ es1 ++ es2 ++ es3)
          })

          val ess = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n >= 2 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val (b_1, es_1) = TizenHelper.instanceOf(h_2, v_2, Value(TIZENAttributeFilter.loc_proto))
              val (b_2, es_2) = TizenHelper.instanceOf(h_2, v_2, Value(TIZENCompositeFilter.loc_proto))
              val (b_3, es_3) = TizenHelper.instanceOf(h_2, v_2, Value(TIZENAttributeRangeFilter.loc_proto))
              val es_4 =
                if (b_1._1._3 <= F && b_2._1._3 <= F && b_3._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              es_1 ++ es_2 ++ es_3 ++ es_4
            case _ => TizenHelper.TizenExceptionBot
          }
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ ess ++ est)
          ((Helper.ReturnStore(h_2, Value(NumTop)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.MessageStorage.addFoldersChangeListener" -> (
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

          val (h_2, es) = v_1._2.foldLeft((h_1, TizenHelper.TizenExceptionBot))((_he, l) => {
            val v1 = Helper.Proto(_he._1, l, AbsString.alpha("foldersadded"))
            val v2 = Helper.Proto(_he._1, l, AbsString.alpha("foldersupdated"))
            val v3 = Helper.Proto(_he._1, l, AbsString.alpha("foldersremoved"))
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
              update("0", PropValue(ObjectValue(Value(TIZENmessaging.loc_msgfolderarr), T, T, T)))
            val h_2 = _he._1.update(l_r1, o_arr)
            val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("MsgFoldersChangeCB.foldersadded"), Value(v1._2), Value(l_r1))
            val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("MsgFoldersChangeCB.foldersupdated"), Value(v2._2), Value(l_r1))
            val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("MsgFoldersChangeCB.foldersremoved"), Value(v3._2), Value(l_r1))
            (h_5, _he._2 ++ es1 ++ es2 ++ es3)
          })

          val ess = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n >= 2 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val (b_1, es_1) = TizenHelper.instanceOf(h_2, v_2, Value(TIZENAttributeFilter.loc_proto))
              val (b_2, es_2) = TizenHelper.instanceOf(h_2, v_2, Value(TIZENCompositeFilter.loc_proto))
              val (b_3, es_3) = TizenHelper.instanceOf(h_2, v_2, Value(TIZENAttributeRangeFilter.loc_proto))
              val es_4 =
                if (b_1._1._3 <= F && b_2._1._3 <= F && b_3._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              es_1 ++ es_2 ++ es_3 ++ es_4
            case _ => TizenHelper.TizenExceptionBot
          }
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ ess ++ est)
          ((Helper.ReturnStore(h_2, Value(NumTop)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.MessageStorage.removeChangeListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 <= NumBot)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
