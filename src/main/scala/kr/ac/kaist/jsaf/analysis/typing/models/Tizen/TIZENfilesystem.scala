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

object TIZENfilesystem extends Tizen {
  private val name = "filesystem"
  /* predefined locations */
  val loc_obj = TIZENtizen.loc_filesystem
  val loc_proto = newSystemRecentLoc(name + "Proto")

  val loc_file: Loc = newSystemLoc("File", Old)
  val loc_filearr: Loc = newSystemLoc("FileArr", Old)
  val loc_filesysstorage: Loc = newSystemLoc("FileSystemStorage", Old)
  val loc_filesysstoragearr: Loc = newSystemLoc("FileSystemStorageArr", Old)
  val loc_filestream: Loc = newSystemLoc("FileStream", Old)

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_obj, prop_obj), (loc_proto, prop_proto), (loc_file, prop_file_ins), (loc_filesysstorage, prop_filesysstorage_ins),
    (loc_filesysstoragearr, prop_filesysstoragearr_ins), (loc_filearr, prop_filearr_ins), (loc_filestream, prop_filestream_ins)
  )
  /* constructor or object*/
  private val prop_obj: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("maxPathLength", AbsConstValue(PropValueNumTop))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("resolve", AbsBuiltinFunc("tizen.filesystem.resolve", 4)),
    ("getStorage", AbsBuiltinFunc("tizen.filesystem.getStorage", 3)),
    ("listStorages", AbsBuiltinFunc("tizen.filesystem.listStorages", 2)),
    ("addStorageStateChangeListener", AbsBuiltinFunc("tizen.filesystem.addStorageStateChangeListener", 2)),
    ("removeStorageStateChangeListener", AbsBuiltinFunc("tizen.filesystem.removeStorageStateChangeListener", 1))
  )

  private val prop_file_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENFile.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("parent", AbsConstValue(PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_file)), T, T, T)))),
    ("readOnly", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("isFile", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("isDirectory", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("created", AbsConstValue(PropValue(ObjectValue(Value(TIZENtizen.loc_date), F, T, T)))),
    ("modified", AbsConstValue(PropValue(ObjectValue(Value(TIZENtizen.loc_date), F, T, T)))),
    ("path", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("name", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("fullPath", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, T, T)))),
    ("fileSize", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefTop, NullBot, BoolBot, UInt, StrBot)), F, T, T)))),
    ("length", AbsConstValue(PropValue(ObjectValue(Value(PValue(UndefTop, NullBot, BoolBot, NumTop, StrBot)), F, T, T))))
  )

  private val prop_filearr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_file), T, T, T))))
  )

  private val prop_filesysstorage_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENFileSystemStorage.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("label", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
    ("type", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("INTERNAL") + AbsString.alpha("EXTERNAL")), T, T, T)))),
    ("state", AbsConstValue(PropValue(ObjectValue(Value(AbsString.alpha("MOUNTED") + AbsString.alpha("REMOVED") +
      AbsString.alpha("UNMOUNTABLE")), T, T, T))))
  )

  private val prop_filesysstoragearr_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Array")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(BuiltinArray.ProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, T, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(loc_filesysstorage), T, T, T))))
  )

  private val prop_filestream_ins: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(TIZENFileStream.loc_proto, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("eof", AbsConstValue(PropValue(ObjectValue(Value(BoolTop), F, T, T)))),
    ("position", AbsConstValue(PropValue(ObjectValue(Value(NumTop), T, T, T)))),
    ("bytesAvailable", AbsConstValue(PropValue(ObjectValue(Value(NumTop), F, T, T))))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.filesystem.resolve" -> (
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
          val v_2 = getArgValue(h_3, ctx_3, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h_3, ctx_3, args, "length"))
          val es_1 =
            if (v_1._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_2 =
            if (v_2._2.exists((l) => Helper.IsCallable(h_3, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot

          // new File
          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENFile.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("parent", PropValue(ObjectValue(Value(PValue(NullTop), LocSet(loc_file)), F, T, T))).
            update("isFile", PropValue(ObjectValue(Value(BoolTop), F, T, T))).
            update("isDirectory", PropValue(ObjectValue(Value(BoolTop), F, T, T))).
            update("created", PropValue(ObjectValue(Value(TIZENtizen.loc_date), F, T, T))).
            update("modified", PropValue(ObjectValue(Value(TIZENtizen.loc_date), F, T, T))).
            update("path", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("name", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("fullPath", PropValue(ObjectValue(Value(StrTop), F, T, T))).
            update("fileSize", PropValue(ObjectValue(Value(UInt), F, T, T))).
            update("length", PropValue(ObjectValue(Value(NumTop), F, T, T)))

          val (h_4, es_3) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              val o_new2 = o_new.update("readOnly", PropValue(ObjectValue(Value(F), F, T, T)))
              val h_4 = h_3.update(l_r1, o_new2)
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(l_r1), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr1)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("FileSuccessCB"), Value(v_2._2), Value(l_r2))
              (h_6, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 3 =>
              val v_3 = getArgValue(h_3, ctx_3, args, "2")
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_3, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_new2 = o_new.update("readOnly", PropValue(ObjectValue(Value(F), F, T, T)))
              val h_4 = h_3.update(l_r1, o_new2)
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(l_r1), T, T, T)))
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr1).update(l_r3, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("FileSuccessCB"), Value(v_2._2), Value(l_r2))
              val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r3))
              (h_7, es_3)
            case Some(n) if n >= 4 =>
              val v_3 = getArgValue(h_3, ctx_3, args, "2")
              val v_4 = getArgValue(h_3, ctx_3, args, "3")
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_3, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_4 =
                if (v_4._1._5 != AbsString.alpha("r") && v_4._1._5 != AbsString.alpha("rw") && v_4._1._5 != AbsString.alpha("w") &&
                  v_4._1._5 != AbsString.alpha("a"))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_new2 =
                if (v_4._1._5 == AbsString.alpha("r"))
                  o_new.update("readOnly", PropValue(ObjectValue(Value(T), F, T, T)))
                else o_new.update("readOnly", PropValue(ObjectValue(Value(F), F, T, T)))
              val h_4 = h_3.update(l_r1, o_new2)
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(l_r1), T, T, T)))
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr1).update(l_r3, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("FileSuccessCB"), Value(v_2._2), Value(l_r2))
              val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r3))
              (h_7, es_3 ++ es_4)
            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ est)
          ((h_4, ctx_3), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.filesystem.getStorage" -> (
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
          val v_2 = getArgValue(h_3, ctx_3, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h_3, ctx_3, args, "length"))
          val es_1 =
            if (v_1._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_2 =
            if (v_2._2.exists((l) => Helper.IsCallable(h_3, l) <= F))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot

          // new FileStorage
          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENFileSystemStorage.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("label", PropValue(ObjectValue(Value(v_1._1._5), F, T, T))).
            update("type", PropValue(ObjectValue(Value(AbsString.alpha("INTERNAL") + AbsString.alpha("EXTERNAL")), F, T, T))).
            update("state", PropValue(ObjectValue(Value(AbsString.alpha("MOUNTED") + AbsString.alpha("REMOVED") +
                                                       AbsString.alpha("UNMOUNTABLE")), F, T, T)))
          val h_4 = h_3.update(l_r1, o_new)
          val (h_5, es_3) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(l_r1), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr1)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("FileSystemStorSuccessCB"), Value(v_2._2), Value(l_r2))
              (h_6, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 3 =>
              val v_3 = getArgValue(h_4, ctx_3, args, "2")
              val es_3 =
                if (v_3._2.exists((l) => Helper.IsCallable(h_4, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(l_r1), T, T, T)))
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_5 = h_4.update(l_r2, o_arr1).update(l_r3, o_arr2)
              val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("FileSystemStorSuccessCB"), Value(v_2._2), Value(l_r2))
              val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("errorCB"), Value(v_3._2), Value(l_r3))
              (h_7, es_3)
            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ est)
          ((h_5, ctx_3), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.filesystem.listStorages" -> (
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
                update("0", PropValue(ObjectValue(Value(TIZENfilesystem.loc_filesysstoragearr), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr1)
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("FileSystemStorArrSuccessCB"), Value(v_1._2), Value(l_r1))
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 2 =>
              val v_2 = getArgValue(h_2, ctx_2, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(TIZENfilesystem.loc_filesysstoragearr), T, T, T)))
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr1).update(l_r2, o_arr2)
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("FileSystemStorArrSuccessCB"), Value(v_1._2), Value(l_r1))
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_5, es_2)
            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_3, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.filesystem.addStorageStateChangeListener" -> (
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
                update("0", PropValue(ObjectValue(Value(TIZENfilesystem.loc_filesysstorage), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr1)
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("FileSystemStorSuccessCB"), Value(v_1._2), Value(l_r1))
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 2 =>
              val v_2 = getArgValue(h_2, ctx_2, args, "1")
              val es_2 =
                if (v_2._2.exists((l) => Helper.IsCallable(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(TIZENfilesystem.loc_filesysstorage), T, T, T)))
              val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr1).update(l_r2, o_arr2)
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("FileSystemStorSuccessCB"), Value(v_1._2), Value(l_r1))
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("errorCB"), Value(v_2._2), Value(l_r2))
              (h_5, es_2)
            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((Helper.ReturnStore(h_3, Value(NumTop)), ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.filesystem.removeStorageStateChangeListener" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val es =
            if (v._1._4 </ NumTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val est = Set[WebAPIException](SecurityError, NotSupportedError, InvalidValuesError, UnknownError, NotFoundError)
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
