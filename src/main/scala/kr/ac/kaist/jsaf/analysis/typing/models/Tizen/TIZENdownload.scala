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

object TIZENdownload extends Tizen {
  private val name = "download"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_download
  val loc_proto = newSystemRecentLoc(name + "Proto")

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto)
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
    ("start", AbsBuiltinFunc("tizen.download.start", 2)),
    ("cancel", AbsBuiltinFunc("tizen.download.cancel", 1)),
    ("pause", AbsBuiltinFunc("tizen.download.pause", 1)),
    ("resume", AbsBuiltinFunc("tizen.download.resume", 1)),
    ("getState", AbsBuiltinFunc("tizen.download.getState", 1)),
    ("getDownloadRequest", AbsBuiltinFunc("tizen.download.getDownloadRequest", 1)),
    ("getMIMEType", AbsBuiltinFunc("tizen.download.getMIMEType", 1)),
    ("setListener", AbsBuiltinFunc("tizen.download.setListener", 2))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.download.start" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)
          val addr5 = cfg.getAPIAddress(addr_env, 4)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val l_r3 = addrToLoc(addr3, Recent)
          val l_r4 = addrToLoc(addr4, Recent)
          val l_r5 = addrToLoc(addr5, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
          val (h_4, ctx_4) = Helper.Oldify(h_3, ctx_3, addr4)
          val (h_5, ctx_5) = Helper.Oldify(h_4, ctx_4, addr5)
          val v_1 = getArgValue(h_5, ctx_5, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_5, ctx_5, args, "length"))
          val (b_1, es_1) = TizenHelper.instanceOf(h_5, v_1, Value(TIZENDownloadRequest.loc_proto))
          val es_2 =
            if (b_1._1._3 <= F) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_6, es_3) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n >= 2 =>
              val v_2 = getArgValue(h_5, ctx_5, args, "1")
              val es1 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_5, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (h_6, es_3) = v_2._2.foldLeft((h_5, TizenHelper.TizenExceptionBot))((_he, l) => {
                val v1 = Helper.Proto(_he._1, l, AbsString.alpha("onprogress"))
                val v2 = Helper.Proto(_he._1, l, AbsString.alpha("onpaused"))
                val v3 = Helper.Proto(_he._1, l, AbsString.alpha("oncanceled"))
                val v4 = Helper.Proto(_he._1, l, AbsString.alpha("oncompleted"))
                val v5 = Helper.Proto(_he._1, l, AbsString.alpha("onfailed"))
                val es1 =
                  if (v1._2.exists((ll) => Helper.IsCallable(_he._1, ll) <= F))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val es2 =
                  if (v2._2.exists((ll) => Helper.IsCallable(_he._1, ll) <= F))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val es3 =
                  if (v3._2.exists((ll) => Helper.IsCallable(_he._1, ll) <= F))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val es4 =
                  if (v4._2.exists((ll) => Helper.IsCallable(_he._1, ll) <= F))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val es5 =
                  if (v5._2.exists((ll) => Helper.IsCallable(_he._1, ll) <= F))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(3)).
                  update("0", PropValue(ObjectValue(Value(NumTop), T, T, T))).
                  update("1", PropValue(ObjectValue(Value(UInt), T, T, T))).
                  update("2", PropValue(ObjectValue(Value(UInt), T, T, T)))
                val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(NumTop), T, T, T)))
                val o_arr3 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(NumTop), T, T, T)))
                val o_arr4 = Helper.NewArrayObject(AbsNumber.alpha(2)).
                  update("0", PropValue(ObjectValue(Value(NumTop), T, T, T))).
                  update("1", PropValue(ObjectValue(Value(NumTop), T, T, T)))
                val o_arr5 = Helper.NewArrayObject(AbsNumber.alpha(2)).
                  update("0", PropValue(ObjectValue(Value(NumTop), T, T, T))).
                  update("1", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                val h_6 = _he._1.
                  update(l_r1, o_arr1).
                  update(l_r2, o_arr2).
                  update(l_r3, o_arr3).
                  update(l_r4, o_arr4).
                  update(l_r5, o_arr5)
                val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("DownloadCB.onprogress"), Value(v1._2), Value(l_r1))
                val h_8 = TizenHelper.addCallbackHandler(h_7, AbsString.alpha("DownloadCB.onpaused"), Value(v2._2), Value(l_r2))
                val h_9 = TizenHelper.addCallbackHandler(h_8, AbsString.alpha("DownloadCB.oncanceled"), Value(v3._2), Value(l_r3))
                val h_10 = TizenHelper.addCallbackHandler(h_9, AbsString.alpha("DownloadCB.oncompleted"), Value(v4._2), Value(l_r4))
                val h_11 = TizenHelper.addCallbackHandler(h_10, AbsString.alpha("DownloadCB.onfailed"), Value(v5._2), Value(l_r5))
                (h_11, _he._2 ++ es1 ++ es2 ++ es3 ++ es4 ++ es5)
              })
              (h_6, es1 ++ es_3)
            case _ => (h_5, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ est)
          ((Helper.ReturnStore(h_6, Value(NumTop)), ctx_5), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.download.cancel" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val es =
            if (n_arglen == AbsNumber.alpha(0)) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_1 =
            if (v._1._4 </ NumTop) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](UnknownError, NotSupportedError, NotFoundError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.download.pause" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val es =
            if (n_arglen == AbsNumber.alpha(0)) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_1 =
            if (v._1._4 </ NumTop) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](UnknownError, NotSupportedError, NotFoundError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.download.resume" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val es =
            if (n_arglen == AbsNumber.alpha(0)) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_1 =
            if (v._1._4 </ NumTop) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](UnknownError, NotSupportedError, NotFoundError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.download.getState" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val es =
            if (n_arglen == AbsNumber.alpha(0)) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_1 =
            if (v._1._4 </ NumTop) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val v1 = Value(AbsString.alpha("QUEUED") + AbsString.alpha("DOWNLOADING") + AbsString.alpha("PAUSED") +
            AbsString.alpha("CANCELED") + AbsString.alpha("COMPLETED") + AbsString.alpha("FAILED"))
          val est = Set[WebAPIException](UnknownError, NotSupportedError, NotFoundError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((Helper.ReturnStore(h, v1), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.download.getDownloadRequest" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v = getArgValue(h_1, ctx_1, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))
          val es =
            if (n_arglen == AbsNumber.alpha(0)) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_1 =
            if (v._1._4 </ NumTop) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENDownloadRequest.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("url", PropValue(ObjectValue(Value(StrTop), T, T, T))).
            update("destination", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T))).
            update("fileName", PropValue(ObjectValue(Value(PValue(UndefBot, NullTop, BoolBot, NumBot, StrTop)), T, T, T))).
            update("networkType", PropValue(ObjectValue(Value(AbsString.alpha("CELLULAR") + AbsString.alpha("WIFI") +
                                                             AbsString.alpha("ALL")), T, T, T))).
            update("httpHeader", PropValue(ObjectValue(Value(NullTop), T, T, T)))
          val h_2 = h_1.update(l_r1, o_new)
          val est = Set[WebAPIException](UnknownError, NotSupportedError, NotFoundError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.download.getMIMEType" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h, ctx, args, "length"))
          val es =
            if (n_arglen == AbsNumber.alpha(0)) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_1 =
            if (v._1._4 </ NumTop) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](UnknownError, NotSupportedError, NotFoundError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.download.setListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)
          val addr5 = cfg.getAPIAddress(addr_env, 4)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val l_r3 = addrToLoc(addr3, Recent)
          val l_r4 = addrToLoc(addr4, Recent)
          val l_r5 = addrToLoc(addr5, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
          val (h_4, ctx_4) = Helper.Oldify(h_3, ctx_3, addr4)
          val (h_5, ctx_5) = Helper.Oldify(h_4, ctx_4, addr5)
          val v_1 = getArgValue(h_5, ctx_5, args, "0")
          val v_2 = getArgValue(h_5, ctx_5, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h_5, ctx_5, args, "length"))
          val es =
            if (n_arglen == AbsNumber.alpha(0)) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_1 =
            if (v_1._1._4 </ NumTop) Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_6, es_2) = v_2._2.foldLeft((h_5, TizenHelper.TizenExceptionBot))((_he, l) => {
            val v1 = Helper.Proto(_he._1, l, AbsString.alpha("onprogress"))
            val v2 = Helper.Proto(_he._1, l, AbsString.alpha("onpaused"))
            val v3 = Helper.Proto(_he._1, l, AbsString.alpha("oncanceled"))
            val v4 = Helper.Proto(_he._1, l, AbsString.alpha("oncompleted"))
            val v5 = Helper.Proto(_he._1, l, AbsString.alpha("onfailed"))
            val es1 =
              if (v1._2.exists((ll) => Helper.IsCallable(_he._1, ll) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val es2 =
              if (v2._2.exists((ll) => Helper.IsCallable(_he._1, ll) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val es3 =
              if (v3._2.exists((ll) => Helper.IsCallable(_he._1, ll) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val es4 =
              if (v4._2.exists((ll) => Helper.IsCallable(_he._1, ll) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val es5 =
              if (v5._2.exists((ll) => Helper.IsCallable(_he._1, ll) <= F))
                Set[WebAPIException](TypeMismatchError)
              else TizenHelper.TizenExceptionBot
            val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(3)).
              update("0", PropValue(ObjectValue(Value(v_1._1._4), T, T, T))).
              update("1", PropValue(ObjectValue(Value(UInt), T, T, T))).
              update("2", PropValue(ObjectValue(Value(UInt), T, T, T)))
            val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(v_1._1._4), T, T, T)))
            val o_arr3 = Helper.NewArrayObject(AbsNumber.alpha(1)).
              update("0", PropValue(ObjectValue(Value(v_1._1._4), T, T, T)))
            val o_arr4 = Helper.NewArrayObject(AbsNumber.alpha(2)).
              update("0", PropValue(ObjectValue(Value(v_1._1._4), T, T, T))).
              update("1", PropValue(ObjectValue(Value(StrTop), T, T, T)))
            val o_arr5 = Helper.NewArrayObject(AbsNumber.alpha(2)).
              update("0", PropValue(ObjectValue(Value(v_1._1._4), T, T, T))).
              update("1", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
            val h_6 = _he._1.
              update(l_r1, o_arr1).
              update(l_r2, o_arr2).
              update(l_r3, o_arr3).
              update(l_r4, o_arr4).
              update(l_r5, o_arr5)
            val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("DownloadCB.onprogress"), Value(v1._2), Value(l_r1))
            val h_8 = TizenHelper.addCallbackHandler(h_7, AbsString.alpha("DownloadCB.onpaused"), Value(v2._2), Value(l_r2))
            val h_9 = TizenHelper.addCallbackHandler(h_8, AbsString.alpha("DownloadCB.oncanceled"), Value(v3._2), Value(l_r3))
            val h_10 = TizenHelper.addCallbackHandler(h_9, AbsString.alpha("DownloadCB.oncompleted"), Value(v4._2), Value(l_r4))
            val h_11 = TizenHelper.addCallbackHandler(h_10, AbsString.alpha("DownloadCB.onfailed"), Value(v5._2), Value(l_r5))
            (h_11, _he._2 ++ es1 ++ es2 ++ es3 ++ es4 ++ es5)
          })
          val est = Set[WebAPIException](UnknownError, NotSupportedError, NotFoundError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ es_2 ++ est)
          ((h_6, ctx_5), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
