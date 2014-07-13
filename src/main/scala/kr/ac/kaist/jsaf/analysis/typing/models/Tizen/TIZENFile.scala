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

object TIZENFile extends Tizen {
  private val name = "File"
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
    ("toURI", AbsBuiltinFunc("tizen.File.toURI", 0)),
    ("listFiles", AbsBuiltinFunc("tizen.File.listFiles", 3)),
    ("openStream", AbsBuiltinFunc("tizen.File.openStream", 4)),
    ("readAsText", AbsBuiltinFunc("tizen.File.readAsText", 3)),
    ("copyTo", AbsBuiltinFunc("tizen.File.copyTo", 5)),
    ("moveTo", AbsBuiltinFunc("tizen.File.moveTo", 5)),
    ("createDirectory", AbsBuiltinFunc("tizen.File.createDirectory", 1)),
    ("createFile", AbsBuiltinFunc("tizen.File.createFile", 1)),
    ("resolve", AbsBuiltinFunc("tizen.File.resolve", 1)),
    ("deleteDirectory", AbsBuiltinFunc("tizen.File.deleteDirectory", 4)),
    ("deleteFile", AbsBuiltinFunc("tizen.File.deleteFile", 3))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.File.toURI" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.File.listFiles" -> (
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
          val (h_4, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(TIZENfilesystem.loc_filearr), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr1)
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("FileArrSuccessCB"), Value(v_1._2), Value(l_r1))
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_2, ctx_2, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(TIZENfilesystem.loc_filearr), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr1)
              val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1))
              // for IOError
              val h_4 =
                if (lset_this.exists((l) => Helper.Proto(h_3, l, AbsString.alpha("isDirectory"))._1._3 <= F)){
                  val o_arr3 = o_arr2.
                    update("0", PropValue(ObjectValue(Value(TIZENtizen.loc_IOerr), T, T, T)))
                  h_3.update(l_r2, o_arr3)
                }
                else {
                  //for other error types
                  val o_arr3 = o_arr2.
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                  h_3.update(l_r2, o_arr3)
                }
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("FileArrSuccessCB"), Value(v_1._2), Value(l_r1))
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2)
            case Some(n) if n >= 3 =>
              val v_2 = getArgValue(h_2, ctx_2, args, "1")
              val v_3 = getArgValue(h_2, ctx_2, args, "2")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (b_1, es_3) = TizenHelper.instanceOf(h_2, v_3, Value(TIZENFileFilter.loc_proto))
              val es_4 =
                if (b_1._1._3 <= F)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(TIZENfilesystem.loc_filearr), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr1)
              val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1))
              // for IOError
              val h_4 =
                if (lset_this.exists((l) => Helper.Proto(h_3, l, AbsString.alpha("isDirectory"))._1._3 <= F)){
                  val o_arr3 = o_arr2.
                    update("0", PropValue(ObjectValue(Value(TIZENtizen.loc_IOerr), T, T, T)))
                  h_3.update(l_r2, o_arr3)
                }
                else {
                  //for other error types
                  val o_arr3 = o_arr2.
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                  h_3.update(l_r2, o_arr3)
                }
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("FileArrSuccessCB"), Value(v_1._2), Value(l_r1))
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2 ++ es_3 ++ es_4)
            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_4, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.File.openStream" -> (
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
          val v_2 = getArgValue(h_2, ctx_2, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val es_1 =
            if (v_1._1._5 != AbsString.alpha("r") && v_1._1._5 != AbsString.alpha("rw") && v_1._1._5 != AbsString.alpha("w") &&
              v_1._1._5 != AbsString.alpha("a"))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_2 =
            if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_3, es_3) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(TIZENfilesystem.loc_filestream), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr1)
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("FileStreamSuccessCB"), Value(v_2._2), Value(l_r1))
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 3 =>
              val v_3 = getArgValue(h_2, ctx_2, args, "2")
              val es_2 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(TIZENfilesystem.loc_filestream), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr1)
              val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1))
              // for IOError
              val h_4 =
                if (lset_this.exists((l) => Helper.Proto(h_3, l, AbsString.alpha("isDirectory"))._1._3 <= T)){
                  val o_arr3 = o_arr2.
                    update("0", PropValue(ObjectValue(Value(TIZENtizen.loc_IOerr), T, T, T)))
                  h_3.update(l_r2, o_arr3)
                }
                else {
                  //for other error types
                  val o_arr3 = o_arr2.
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                  h_3.update(l_r2, o_arr3)
                }
              val h_5 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("FileStreamSuccessCB"), Value(v_2._2), Value(l_r1))
              val h_6 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
              (h_6, es_2)
            case Some(n) if n >= 4 =>
              val v_3 = getArgValue(h_2, ctx_2, args, "2")
              val v_4 = getArgValue(h_2, ctx_2, args, "3")
              val es_2 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_4._1._2 </ NullTop && v_4._1._5 </ StrTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(TIZENfilesystem.loc_filestream), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr1)
              val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1))
              // for IOError
              val h_4 =
                if (lset_this.exists((l) => Helper.Proto(h_3, l, AbsString.alpha("isDirectory"))._1._3 <= T)){
                  val o_arr3 = o_arr2.
                    update("0", PropValue(ObjectValue(Value(TIZENtizen.loc_IOerr), T, T, T)))
                  h_3.update(l_r2, o_arr3)
                }
                else {
                  //for other error types
                  val o_arr3 = o_arr2.
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                  h_3.update(l_r2, o_arr3)
                }
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("FileStreamSuccessCB"), Value(v_2._2), Value(l_r1))
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r2))
              (h_6, es_2 ++ es_3)
            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ est)
          ((h_3, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.File.readAsText" -> (
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
          val (h_3, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(StrTop), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr1)
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("FileStringSuccessCB"), Value(v_1._2), Value(l_r1))
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_2, ctx_2, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(StrTop), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr1)
              val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1))
              // for IOError
              val h_4 =
                if (lset_this.exists((l) => Helper.Proto(h_3, l, AbsString.alpha("isDirectory"))._1._3 <= T)){
                  val o_arr3 = o_arr2.
                    update("0", PropValue(ObjectValue(Value(TIZENtizen.loc_IOerr), T, T, T)))
                  h_3.update(l_r2, o_arr3)
                }
                else {
                  //for other error types
                  val o_arr3 = o_arr2.
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                  h_3.update(l_r2, o_arr3)
                }
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("FileStringSuccessCB"), Value(v_1._2), Value(l_r1))
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2)
            case Some(n) if n >= 3 =>
              val v_2 = getArgValue(h_2, ctx_2, args, "1")
              val v_3 = getArgValue(h_2, ctx_2, args, "2")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_3 =
                if (v_3._1._2 </ NullTop && v_3._1._5 </ StrTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(StrTop), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr1)
              val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1))
              // for IOError
              val h_4 =
                if (lset_this.exists((l) => Helper.Proto(h_3, l, AbsString.alpha("isDirectory"))._1._3 <= T)){
                  val o_arr3 = o_arr2.
                    update("0", PropValue(ObjectValue(Value(TIZENtizen.loc_IOerr), T, T, T)))
                  h_3.update(l_r2, o_arr3)
                }
                else {
                  //for other error types
                  val o_arr3 = o_arr2.
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                  h_3.update(l_r2, o_arr3)
                }
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("FileStringSuccessCB"), Value(v_1._2), Value(l_r1))
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_6, es_2 ++ es_3)
            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_3, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.File.copyTo" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")
          val v_2 = getArgValue(h_1, ctx_1, args, "1")
          val v_3 = getArgValue(h_1, ctx_1, args, "2")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))
          val es_1 =
            if (v_1._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_2 =
            if (v_2._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_3 =
            if (v_3._1._3 </ BoolTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_2, es_4) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 3 =>
              (h_1, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 4 =>
              val v_4 = getArgValue(h_1, ctx_1, args, "3")
              val es_4 =
                if (v_4._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(v_4._2), Value(UndefTop))
              (h_2, es_4)
            case Some(n) if n >= 5 =>
              val v_4 = getArgValue(h_1, ctx_1, args, "3")
              val v_5 = getArgValue(h_1, ctx_1, args, "4")
              val es_4 =
                if (v_4._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_5 =
                if (v_5._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_notFounderr) ++
                LocSet(TIZENtizen.loc_IOerr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr1)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_4._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(v_5._2), Value(l_r1))
              (h_4, es_4 ++ es_5)
            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ es_4 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.File.moveTo" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")
          val v_2 = getArgValue(h_1, ctx_1, args, "1")
          val v_3 = getArgValue(h_1, ctx_1, args, "2")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))
          val es_1 =
            if (v_1._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_2 =
            if (v_2._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_3 =
            if (v_3._1._3 </ BoolTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_2, es_4) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 3 =>
              (h_1, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 4 =>
              val v_4 = getArgValue(h_1, ctx_1, args, "3")
              val es_4 =
                if (v_4._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(v_4._2), Value(UndefTop))
              (h_2, es_4)
            case Some(n) if n >= 5 =>
              val v_4 = getArgValue(h_1, ctx_1, args, "3")
              val v_5 = getArgValue(h_1, ctx_1, args, "4")
              val es_4 =
                if (v_4._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_5 =
                if (v_5._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_notFounderr) ++
                LocSet(TIZENtizen.loc_IOerr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr1)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_4._2), Value(UndefTop))
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(v_5._2), Value(l_r1))
              (h_4, es_4 ++ es_5)
            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ es_4 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.File.createDirectory" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v = getArgValue(h_1, ctx_1, args, "0")
          val es_1 =
            if (v._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else
              TizenHelper.TizenExceptionBot
          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENFile.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("parent", PropValue(ObjectValue(Value(PValue(NullTop), LocSet(TIZENfilesystem.loc_file)), F, T, T))).
            update("readOnly", PropValue(ObjectValue(Value(F), F, T, T))).
            update("isFile", PropValue(ObjectValue(Value(F), F, T, T))).
            update("isDirectory", PropValue(ObjectValue(Value(T), F, T, T))).
            update("created", PropValue(ObjectValue(Value(TIZENtizen.loc_date), F, T, T))).
            update("modified", PropValue(ObjectValue(Value(NullTop), F, T, T))).
            update("path", PropValue(ObjectValue(Value(v._1._5), F, T, T))).
            update("name", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("fullPath", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("fileSize", PropValue(ObjectValue(Value(UndefTop), F, T, T))).
            update("length", PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, T, T)))
          val h_2 = h_1.update(l_r1, o_new)
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.File.createFile" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v = getArgValue(h_1, ctx_1, args, "0")
          val es_1 =
            if (v._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else
              TizenHelper.TizenExceptionBot
          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENFile.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("parent", PropValue(ObjectValue(Value(PValue(NullTop), LocSet(TIZENfilesystem.loc_file)), F, T, T))).
            update("readOnly", PropValue(ObjectValue(Value(F), F, T, T))).
            update("isFile", PropValue(ObjectValue(Value(T), F, T, T))).
            update("isDirectory", PropValue(ObjectValue(Value(F), F, T, T))).
            update("created", PropValue(ObjectValue(Value(TIZENtizen.loc_date), F, T, T))).
            update("modified", PropValue(ObjectValue(Value(NullTop), F, T, T))).
            update("path", PropValue(ObjectValue(Value(v._1._5), F, T, T))).
            update("name", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("fullPath", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("fileSize", PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, T, T))).
            update("length", PropValue(ObjectValue(Value(UndefTop), F, T, T)))
          val h_2 = h_1.update(l_r1, o_new)
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.File.resolve" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es_1 =
            if (v._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else
              TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1)
          ((Helper.ReturnStore(h, Value(TIZENfilesystem.loc_file)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.File.deleteDirectory" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")
          val v_2 = getArgValue(h_1, ctx_1, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))
          val es_1 =
            if (v_1._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_2 =
            if (v_2._1._3 </ BoolTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_2, es_3) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              (h_1, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 3 =>
              val v_3 = getArgValue(h_1, ctx_1, args, "2")
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(v_3._2), Value(UndefTop))
              (h_2, es_3)
            case Some(n) if n >= 4 =>
              val v_3 = getArgValue(h_1, ctx_1, args, "2")
              val v_4 = getArgValue(h_1, ctx_1, args, "3")
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_4 =
                if (v_4._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr1)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_3._2), Value(UndefTop))
              val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1))
              // for IOError
              val h_4 =
                if (lset_this.exists((l) => Helper.Proto(h_3, l, AbsString.alpha("isDirectory"))._1._3 <= T)){
                  val o_arr3 = o_arr2.
                    update("0", PropValue(ObjectValue(Value(TIZENtizen.loc_IOerr), T, T, T)))
                  h_3.update(l_r1, o_arr3)
                }
                else {
                  //for other error types
                  val o_arr3 = o_arr2.
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                  h_3.update(l_r1, o_arr3)
                }
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("errorCB"), Value(v_4._2), Value(l_r1))
              (h_5, es_3 ++ es_4)
            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.File.deleteFile" -> (
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
          val es_1 =
            if (v_1._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_2, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              (h_1, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val es_3 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              (h_2, es_3)
            case Some(n) if n >= 3 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              val v_3 = getArgValue(h_1, ctx_1, args, "2")
              val es_3 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_4 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_2 = h_1.update(l_r1, o_arr1)
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(v_2._2), Value(UndefTop))
              val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1))
              // for IOError
              val h_4 =
                if (lset_this.exists((l) => Helper.Proto(h_3, l, AbsString.alpha("isDirectory"))._1._3 <= T)){
                  val o_arr3 = o_arr2.
                    update("0", PropValue(ObjectValue(Value(TIZENtizen.loc_IOerr), T, T, T)))
                  h_3.update(l_r1, o_arr3)
                }
                else {
                  //for other error types
                  val o_arr3 = o_arr2.
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                  h_3.update(l_r1, o_arr3)
                }
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r1))
              (h_5, es_3 ++ es_4)
            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, UnknownError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        ))

    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENFileSystemStorage extends Tizen {
  private val name = "FileSystemStorage"
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
    Map(

    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENFileFilter extends Tizen {
  private val name = "FileFilter"
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
    Map(

    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}

object TIZENFileStream extends Tizen {
  private val name = "FileStream"
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
    ("close", AbsBuiltinFunc("tizen.FileStream.close", 0)),
    ("read", AbsBuiltinFunc("tizen.FileStream.read", 1)),
    ("readBytes", AbsBuiltinFunc("tizen.FileStream.readBytes", 1)),
    ("readBase64", AbsBuiltinFunc("tizen.FileStream.readBase64", 1)),
    ("write", AbsBuiltinFunc("tizen.FileStream.write", 1)),
    ("writeBytes", AbsBuiltinFunc("tizen.FileStream.writeBytes", 1)),
    ("writeBase64", AbsBuiltinFunc("tizen.FileStream.writeBase64", 1))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.FileStream.close" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((h, ctx), (he, ctxe))
        }
        )),
      ("tizen.FileStream.read" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 </ NumTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, NotSupportedError, IOError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.FileStream.readBytes" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val v = getArgValue(h_1, ctx_1, args, "0")
          val es =
            if (v._1._4 </ NumTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val o_arr = Helper.NewArrayObject(UInt).
            update("@default_number", PropValue(ObjectValue(Value(NumTop), T, T, T)))
          val h_2 = h_1.update(l_r1, o_arr)
          val est = Set[WebAPIException](SecurityError, NotSupportedError, IOError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.FileStream.readBase64" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 </ NumTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, NotSupportedError, IOError, InvalidValuesError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.FileStream.write" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, NotSupportedError, IOError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.FileStream.writeBytes" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es_1 =
            if (v._2.exists((l) => Helper.IsArray(h, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot

          val es_2 = v._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
            val n_length = Operator.ToUInt32(Helper.Proto(h, ll, AbsString.alpha("length")))
            val ess = n_length.getAbsCase match {
              case AbsBot =>
                TizenHelper.TizenExceptionBot
              case _ => AbsNumber.getUIntSingle(n_length) match {
                case Some(n) => {
                  val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                    val vi = Helper.Proto(h, ll, AbsString.alpha(i.toString))
                    val esi =
                      if (vi._1._4 </ NumTop) Set[WebAPIException](TypeMismatchError)
                      else TizenHelper.TizenExceptionBot
                    _e ++ esi
                  })
                  es__
                }
                case _ => {
                  val vi = Helper.Proto(h, ll, AbsString.alpha("@default_number"))
                  val esi =
                    if (vi._1._4 </ NumTop) Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  esi
                }
              }
            }
            _es ++ ess
          })
          val est = Set[WebAPIException](SecurityError, NotSupportedError, IOError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.FileStream.writeBase64" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es_1 =
            if (v._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, NotSupportedError, IOError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ est)
          ((h, ctx), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
