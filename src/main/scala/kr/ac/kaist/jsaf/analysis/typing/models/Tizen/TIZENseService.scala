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

import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.builtin.BuiltinArray

object TIZENseService extends Tizen {
  private val name = "seService"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_seService
  val loc_proto = newSystemRecentLoc(name + "Proto")

  val loc_reader: Loc = newSystemLoc("Reader", Old)
  val loc_readerarr: Loc = newSystemLoc("ReaderArr", Old)
  val loc_session: Loc = newSystemLoc("Session", Old)
  val loc_channel: Loc = newSystemLoc("Channel", Old)

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto), (loc_reader, prop_reader_ins), (loc_readerarr, prop_readerarr_ins),
    (loc_session, prop_session_ins), (loc_channel, prop_channel_ins)
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
    ("getReaders", AbsBuiltinFunc("tizen.seService.getReaders",2)),
    ("registerSEListener", AbsBuiltinFunc("tizen.seService.registerSEListener",1)),
    ("unregisterSEListener", AbsBuiltinFunc("tizen.seService.unregisterSEListener",1)),
    ("shutdown", AbsBuiltinFunc("tizen.seService.shutdown",0))
  )

  private val prop_reader_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENReader.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("isPresent", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T))))
  )

  private val prop_readerarr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(Value(loc_reader), T, T, T))))
  )

  private val prop_session_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENSession.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("isClosed", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T))))
  )

  private val prop_channel_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENChannel.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("isBasicChannel", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T))))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.seService.getReaders" -> (
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
            update("0", PropValue(ObjectValue(Value(TIZENseService.loc_readerarr), T, T, T)))
          val h_3 = h_2.update(l_r1, o_arr)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("ReaderArrSuccessCB"), Value(v_1._2), Value(l_r1))

          val (h_5, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 2 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.seService.registerSEListener" -> (
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
          val (h_3, es) = v_1._2.foldLeft((h_2, TizenHelper.TizenExceptionBot))((_he, l) => {
            val v1 = Helper.Proto(_he._1, l, AbsString.alpha("onSEReady"))
            val v2 = Helper.Proto(_he._1, l, AbsString.alpha("onSENotReady"))
            val es1 =
              if (v1._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val es2 =
              if (v2._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENseService.loc_reader), T, T, T)))
            val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENseService.loc_reader), T, T, T)))
            val h_4 = _he._1.update(l_r1, o_arr).update(l_r2, o_arr1)
            val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("SEChangeListener.onSEReady"), Value(v1._2), Value(l_r1))
            val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("SEChangeListener.onSENotReady"), Value(v2._2), Value(l_r2))
            (h_6, _he._2 ++ es1 ++ es2)
          })
          val est = Set[WebAPIException](SecurityError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((Helper.ReturnStore(h_3, Value(UInt)), ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.seService.unregisterSEListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 </NumTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, UnknownError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.seService.shutdown" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](SecurityError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENReader extends Tizen {
  private val name = "Reader"
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
    ("getName", AbsBuiltinFunc("tizen.Reader.getName",0)),
    ("openSession", AbsBuiltinFunc("tizen.Reader.openSession",2)),
    ("closeSessions", AbsBuiltinFunc("tizen.Reader.closeSessions",0))
  )


  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.Reader.getName" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](SecurityError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.Reader.openSession" -> (
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
            update("0", PropValue(ObjectValue(Value(TIZENseService.loc_session), T, T, T)))
          val h_3 = h_2.update(l_r1, o_arr)
          val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("SessionSuccessCB"), Value(v_1._2), Value(l_r1))

          val (h_5, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 2 =>
              val v_2 = getArgValue(h_4, ctx_2, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_IOerr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2)
            case _ => (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.Reader.closeSessions" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](SecurityError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        ))
   )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENSession extends Tizen {
  private val name = "Session"
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
    ("openBasicChannel", AbsBuiltinFunc("tizen.Session.openBasicChannel",3)),
    ("openLogicalChannel", AbsBuiltinFunc("tizen.Session.openLogicalChannel",3)),
    ("getATR", AbsBuiltinFunc("tizen.Session.getATR",0)),
    ("close", AbsBuiltinFunc("tizen.Session.close",0)),
    ("closeChannels", AbsBuiltinFunc("tizen.Session.closeChannels",0))
  )


  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
        ("tizen.Session.openBasicChannel" -> (
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
            val es_1 =
              if (v_1._2.exists((l) => Helper.IsArray(h_2, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val es_2 = v_1._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
              val n_length = Operator.ToUInt32(Helper.Proto(h_2, ll, AbsString.alpha("length")))
              val ess = n_length.getAbsCase match {
                case AbsBot =>
                  TizenHelper.TizenExceptionBot
                case _ => AbsNumber.getUIntSingle(n_length) match {
                  case Some(n) => {
                    val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                      val vi = Helper.Proto(h_2, ll, AbsString.alpha(i.toString))
                      val esi =
                        if (vi._1._4 </ NumTop) Set[WebAPIException](TypeMismatchError)
                        else TizenHelper.TizenExceptionBot
                      _e ++ esi
                    })
                    es__
                  }
                  case _ => {
                    val vi = Helper.Proto(h_2, ll, AbsString.alpha(Str_default_number))
                    val esi =
                      if (vi._1._4 </ NumTop) Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    esi
                  }
                }
              }
              _es ++ ess
            })

            val es_3 =
              if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENseService.loc_channel), T, T, T)))
            val h_3 = h_2.update(l_r1, o_arr)
            val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("ChannelSuccessCB"), Value(v_2._2), Value(l_r1))
            val (h_5, es_4) = AbsNumber.getUIntSingle(n_arglen) match {
              case Some(n) if n == 2 => (h_4, TizenHelper.TizenExceptionBot)
              case Some(n) if n >= 3 =>
                val v_3 = getArgValue(h_4, ctx_2, args, "2")
                val es1 =
                  if (v_3._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                val h_5 = h_4.update(l_r2, o_arr1)
                val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
                (h_6, es1)
              case _ =>
                (HeapBot, TizenHelper.TizenExceptionBot)
            }
            val est = Set[WebAPIException](SecurityError, InvalidValuesError)
            val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ es_4 ++ est)
            ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))
          }
          )),
        ("tizen.Session.openLogicalChannel" -> (
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
            val es_1 =
              if (v_1._2.exists((l) => Helper.IsArray(h_2, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val es_2 = v_1._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
              val n_length = Operator.ToUInt32(Helper.Proto(h_2, ll, AbsString.alpha("length")))
              val ess = n_length.getAbsCase match {
                case AbsBot =>
                  TizenHelper.TizenExceptionBot
                case _ => AbsNumber.getUIntSingle(n_length) match {
                  case Some(n) => {
                    val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                      val vi = Helper.Proto(h_2, ll, AbsString.alpha(i.toString))
                      val esi =
                        if (vi._1._4 </ NumTop) Set[WebAPIException](TypeMismatchError)
                        else TizenHelper.TizenExceptionBot
                      _e ++ esi
                    })
                    es__
                  }
                  case _ => {
                    val vi = Helper.Proto(h_2, ll, AbsString.alpha(Str_default_number))
                    val esi =
                      if (vi._1._4 </ NumTop) Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    esi
                  }
                }
              }
              _es ++ ess
            })

            val es_3 =
              if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENseService.loc_channel), T, T, T)))
            val h_3 = h_2.update(l_r1, o_arr)
            val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("ChannelSuccessCB"), Value(v_2._2), Value(l_r1))
            val (h_5, es_4) = AbsNumber.getUIntSingle(n_arglen) match {
              case Some(n) if n == 2 => (h_4, TizenHelper.TizenExceptionBot)
              case Some(n) if n >= 3 =>
                val v_3 = getArgValue(h_4, ctx_2, args, "2")
                val es1 =
                  if (v_3._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                val h_5 = h_4.update(l_r2, o_arr1)
                val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
                (h_6, es1)
              case _ =>
                (HeapBot, TizenHelper.TizenExceptionBot)
            }
            val est = Set[WebAPIException](SecurityError, InvalidValuesError)
            val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ es_4 ++ est)
            ((h_5, ctx_2), (he + h_e, ctxe + ctx_e))
          }
          )),
        ("tizen.Session.getATR" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            val lset_env = h(SinglePureLocalLoc)("@env")._2._2
            val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
            if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
            val addr_env = (cp._1._1, set_addr.head)
            val addr1 = cfg.getAPIAddress(addr_env, 0)
            val l_r1 = addrToLoc(addr1, Recent)
            val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
            val o_arr = Helper.NewArrayObject(UInt).
              update(Str_default_number, PropValue(ObjectValue(Value(NumTop), T, T, T)))
            val h_2 = h_1.update(l_r1, o_arr)
            val est = Set[WebAPIException](SecurityError, UnknownError)
            val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
            ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
          }
          )),
        ("tizen.Session.close" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            val est = Set[WebAPIException](SecurityError, UnknownError)
            val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
            ((h, ctx), (he + h_e, ctxe + ctx_e))
          }
          )),
        ("tizen.Session.closeChannels" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            val est = Set[WebAPIException](SecurityError, UnknownError)
            val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
            ((h, ctx), (he + h_e, ctxe + ctx_e))
          }
          ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENChannel extends Tizen {
  private val name = "Channel"
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
    ("close", AbsBuiltinFunc("tizen.Channel.close",0)),
    ("transmit", AbsBuiltinFunc("tizen.Channel.transmit",3))
  )


  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
        ("tizen.Channel.close" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            val est = Set[WebAPIException](SecurityError, UnknownError)
            val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
            ((h, ctx), (he + h_e, ctxe + ctx_e))
          }
          )),
        ("tizen.Channel.transmit" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
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
            val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
            val v_1 = getArgValue(h_3, ctx_3, args, "0")
            val v_2 = getArgValue(h_3, ctx_3, args, "1")
            val n_arglen = Operator.ToUInt32(getArgValue(h_3, ctx_3, args, "length"))
            val es_1 =
              if (v_1._2.exists((l) => Helper.IsArray(h_3, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val es_2 = v_1._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
              val n_length = Operator.ToUInt32(Helper.Proto(h_3, ll, AbsString.alpha("length")))
              val ess = n_length.getAbsCase match {
                case AbsBot =>
                  TizenHelper.TizenExceptionBot
                case _ => AbsNumber.getUIntSingle(n_length) match {
                  case Some(n) => {
                    val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                      val vi = Helper.Proto(h_3, ll, AbsString.alpha(i.toString))
                      val esi =
                        if (vi._1._4 </ NumTop) Set[WebAPIException](TypeMismatchError)
                        else TizenHelper.TizenExceptionBot
                      _e ++ esi
                    })
                    es__
                  }
                  case _ => {
                    val vi = Helper.Proto(h_3, ll, AbsString.alpha(Str_default_number))
                    val esi =
                      if (vi._1._4 </ NumTop) Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    esi
                  }
                }
              }
              _es ++ ess
            })

            val es_3 =
              if (v_2._2.exists((l) => Helper.IsCallable(h_3, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val o_arr = Helper.NewArrayObject(UInt).
              update(Str_default_number, PropValue(ObjectValue(Value(UInt), T, T, T)))
            val h_4 = h_3.update(l_r1, o_arr)
            val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(l_r1), T, T, T)))
            val h_5 = h_4.update(l_r2, o_arr1)
            val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("TransmitSuccessCB"), Value(v_2._2), Value(l_r2))
            val (h_7, es_4) = AbsNumber.getUIntSingle(n_arglen) match {
              case Some(n) if n == 2 => (h_6, TizenHelper.TizenExceptionBot)
              case Some(n) if n >= 3 =>
                val v_3 = getArgValue(h_6, ctx_3, args, "2")
                val es1 =
                  if (v_3._2.exists((l) => Helper.IsCallable(h_6, l) <= F))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_securityerr) ++ LocSet(TIZENtizen.loc_IOerr) ++ LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                val h_7 = h_6.update(l_r3, o_arr2)
                val h_8 = TizenHelper.addCallbackHandler(h_7, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r3))
                (h_8, es1)
              case _ =>
                (HeapBot, TizenHelper.TizenExceptionBot)
            }
            val est = Set[WebAPIException](SecurityError)
            val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ es_4 ++ est)
            ((h_7, ctx_3), (he + h_e, ctxe + ctx_e))
          }
          ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
