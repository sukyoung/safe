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


object TIZENNFCAdapter extends Tizen {
  private val name = "NFCAdapter"
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
    ("setPowered", AbsBuiltinFunc("tizen.NFCAdapter.setPowered",3)),
    ("setTagListener", AbsBuiltinFunc("tizen.NFCAdapter.setTagListener",2)),
    ("setPeerListener", AbsBuiltinFunc("tizen.NFCAdapter.setPeerListener",1)),
    ("unsetTagListener", AbsBuiltinFunc("tizen.NFCAdapter.unsetTagListener",0)),
    ("unsetPeerListener", AbsBuiltinFunc("tizen.NFCAdapter.unsetPeerListener",0)),
    ("getCachedMessage", AbsBuiltinFunc("tizen.NFCAdapter.getCachedMessage",0))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.NFCAdapter.setPowered" -> (
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
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val es =
            if (v_1._1._3 </ BoolTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_2, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              val h_2 = lset_this.foldLeft(HeapBot)((_h, l) => {
                (_h + Helper.PropStore(h_1, l, AbsString.alpha("powered"), Value(v_1._1._3)))
              })
              (h_2, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val es1 =
                if (v_2._1 <= PValueTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es2 =
                if (v_2._2.exists((l) =>  Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = lset_this.foldLeft(HeapBot)((_h, l) => {
                (_h + Helper.PropStore(h_1, l, AbsString.alpha("powered"), Value(v_1._1._3)))
              })
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              (h_3, es1 ++ es2)
            case Some(n) if n == 3 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val v_3 = getArgValue(h_1, ctx_1, args, "2")
              val es1 =
                if (v_2._1 <= PValueTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es2 =
                if (v_2._2.exists((l) =>  Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es3 =
                if (v_3._1 <= PValueTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es4 =
                if (v_3._2.exists((l) =>  Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = lset_this.foldLeft(HeapBot)((_h, l) => {
                (_h + Helper.PropStore(h_1, l, AbsString.alpha("powered"), Value(v_1._1._3)))
              })
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_serviceNotAvailableerr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr2)
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r1))
              (h_5, es1 ++ es2 ++ es3 ++ es4)
            case _ => {
              (HeapBot, TizenHelper.TizenExceptionBot)
            }
          }
          val est = Set[WebAPIException](SecurityError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.NFCAdapter.setTagListener" -> (
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
          val (h_2, es) = v_1._2.foldLeft((h_1, TizenHelper.TizenExceptionBot))((_he, l) => {
            val v1 = Helper.Proto(_he._1, l, AbsString.alpha("onattach"))
            val v2 = Helper.Proto(_he._1, l, AbsString.alpha("ondetach"))
            val es1 =
              if (v1._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val es2 =
              if (v2._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENnfc.loc_nfctag), T, T, T)))
            val h_2 = _he._1.update(l_r1, o_arr)
            val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("NFCTagDetectCB.onattach"), Value(v1._2), Value(l_r1))
            val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("NFCTagDetectCB.ondetach"), Value(v2._2), Value(UndefTop))
            (h_4, _he._2 ++ es1 ++ es2)
          })

          val es_1 = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_2, ctx_1, args, "1")
              val es1 =
                if (v_2._2.exists((l) => Helper.IsArray(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              es1
            case _ => TizenHelper.TizenExceptionBot
          }
          val est = Set[WebAPIException](SecurityError, UnknownError, ServiceNotAvailableError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.NFCAdapter.setPeerListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v = getArgValue(h_1, ctx_1, args, "0")
          val (h_2, es) = v._2.foldLeft((h_1, TizenHelper.TizenExceptionBot))((_he, l) => {
            val v1 = Helper.Proto(_he._1, l, AbsString.alpha("onattach"))
            val v2 = Helper.Proto(_he._1, l, AbsString.alpha("ondetach"))
            val es1 =
              if (v1._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val es2 =
              if (v2._2.exists((l) => Helper.IsCallable(_he._1, l) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(TIZENnfc.loc_nfcpeer), T, T, T)))
            val h_2 = _he._1.update(l_r1, o_arr)
            val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("NFCPeerDetectCB.onattach"), Value(v1._2), Value(l_r1))
            val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("NFCPeerDetectCB.ondetach"), Value(v2._2), Value(UndefTop))
            (h_4, _he._2 ++ es1 ++ es2)
          })
          val est = Set[WebAPIException](SecurityError, UnknownError, ServiceNotAvailableError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.NFCAdapter.unsetTagListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](SecurityError, UnknownError, ServiceNotAvailableError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.NFCAdapter.unsetPeerListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](SecurityError, UnknownError, ServiceNotAvailableError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.NFCAdapter.getCachedMessage" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](SecurityError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(PValue(NullTop), LocSet(TIZENnfc.loc_ndefmsg))), ctx), (he + h_e, ctxe + ctx_e))
        }
        ))

    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
