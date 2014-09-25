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

object TIZENBluetoothAdapter extends Tizen {
  val name = "BluetoothAdapter"
  val loc_proto = newSystemRecentLoc(name + "Proto")

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("setName", AbsBuiltinFunc("tizen.BluetoothAdapter.setName", 3)),
    ("setPowered", AbsBuiltinFunc("tizen.BluetoothAdapter.setPowered", 3)),
    ("setVisible", AbsBuiltinFunc("tizen.BluetoothAdapter.setVisible", 4)),
    ("discoverDevices", AbsBuiltinFunc("tizen.BluetoothAdapter.discoverDevices", 2)),
    ("stopDiscovery", AbsBuiltinFunc("tizen.BluetoothAdapter.stopDiscovery", 2)),
    ("getKnownDevices", AbsBuiltinFunc("tizen.BluetoothAdapter.getKnownDevices", 2)),
    ("getDevice", AbsBuiltinFunc("tizen.BluetoothAdapter.getDevice", 3)),
    ("createBonding", AbsBuiltinFunc("tizen.BluetoothAdapter.createBonding", 3)),
    ("destroyBonding", AbsBuiltinFunc("tizen.BluetoothAdapter.destroyBonding", 3)),
    ("registerRFCOMMServiceByUUID", AbsBuiltinFunc("tizen.BluetoothAdapter.registerRFCOMMServiceByUUID", 4))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      "tizen.BluetoothAdapter.setName" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val name = getArgValue(h_1, ctx_1, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))
          val es =
            if (name._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_2, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              if (lset_this.exists((l) => h_1(l)("powered")._2._1._3 <= T)) {
                val h_2 = lset_this.foldLeft(h_1)((_h, l) => {
                  Helper.PropStore(_h, l, AbsString.alpha("name"), Value(name._1._5))
                })
                (h_2, TizenHelper.TizenExceptionBot)
              }
              else {
                (HeapBot, TizenHelper.TizenExceptionBot)
              }
            case Some(n) if n == 2 =>
              val sucCB = getArgValue(h_1, ctx_1, args, "1")
              val es_1 =
                if (sucCB._2.exists((l) => Helper.IsCallable(h_1, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              if (lset_this.exists((l) => h_1(l)("powered")._2._1._3 <= T)) {
                val h_2 = lset_this.foldLeft(h_1)((_h, l) => {
                  Helper.PropStore(_h, l, AbsString.alpha("name"), Value(name._1._5))
                })
                val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
                (h_3, es_1)
              }
              else {
                (HeapBot, es_1)
              }
            case Some(n) if n == 3 =>
              val sucCB = getArgValue(h_1, ctx_1, args, "1")
              val errCB = getArgValue(h_1, ctx_1, args, "2")
              val es_1 =
                if (sucCB._2.exists((l) => F <= Helper.IsCallable(h_1, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_2 =
                if (errCB._2.exists((l) => F <= Helper.IsCallable(h_1, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_6 =
                if (lset_this.exists((l) => T <= h_1(l)("powered")._2._1._3)) {
                val h_2 = lset_this.foldLeft(h_1)((_h, l) => {
                  Helper.PropStore(_h, l, AbsString.alpha("name"), Value(name._1._5))
                })
                val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                val h_3 = h_2.update(l_r1, o_arr)
                val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
                val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
                  h_5
                } else HeapBot
              val h_7 =
                if (lset_this.exists((l) => F <= h_1(l)("powered")._2._1._3)) {
                val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_serviceNotAvailableerr)), T, T, T)))
                val h_2 = h_1.update(l_r1, o_arr)
                val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
                h_3
                } else HeapBot
              (h_6 + h_7, es_1 ++ es_2)

            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        ),
      "tizen.BluetoothAdapter.setPowered" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val state = getArgValue(h_1, ctx_1, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))
          val es =
            if (state._1._3 </ BoolTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_2, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              val h_2 = lset_this.foldLeft(h_1)((_h, l) => {
                Helper.PropStore(_h, l, AbsString.alpha("powered"), Value(state._1._3))
              })
              (h_2, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val sucCB = getArgValue(h_1, ctx_1, args, "1")
              val es_1 =
                if (sucCB._2.exists((l) => F <= Helper.IsCallable(h_1, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = lset_this.foldLeft(h_1)((_h, l) => {
                Helper.PropStore(_h, l, AbsString.alpha("powered"), Value(state._1._3))
              })
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
              (h_3, es_1)
            case Some(n) if n >= 3 =>
              val sucCB = getArgValue(h_1, ctx_1, args, "1")
              val errCB = getArgValue(h_1, ctx_1, args, "2")
              val es_1 =
                if (sucCB._2.exists((l) => F <= Helper.IsCallable(h_1, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_2 =
                if (errCB._2.exists((l) => F <= Helper.IsCallable(h_1, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_2 = lset_this.foldLeft(h_1)((_h, l) => {
                Helper.PropStore(_h, l, AbsString.alpha("powered"), Value(state._1._3))
              })
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_serviceNotAvailableerr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr)
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
              (h_5, es_1 ++ es_2)
            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        ),
      "tizen.BluetoothAdapter.setVisible" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val mode = getArgValue(h_1, ctx_1, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))
          val es =
            if (mode._1._3 </ BoolTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_2, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              if (lset_this.exists((l) => T <= h_1(l)("powered")._2._1._3)) {
                val h_2 = lset_this.foldLeft(h_1)((_h, l) => {
                  Helper.PropStore(_h, l, AbsString.alpha("visible"), Value(mode._1._3))
                })
                (h_2, TizenHelper.TizenExceptionBot)
              }
              else {
                (HeapBot, TizenHelper.TizenExceptionBot)
              }
            case Some(n) if n == 2 =>
              val sucCB = getArgValue(h_1, ctx_1, args, "1")
              val es_1 =
                if (sucCB._2.exists((l) => F <= Helper.IsCallable(h_1, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              if (lset_this.exists((l) => T <= h_1(l)("powered")._2._1._3)) {
                val h_2 = lset_this.foldLeft(h_1)((_h, l) => {
                  Helper.PropStore(_h, l, AbsString.alpha("visible"), Value(mode._1._5))
                })
                val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
                (h_3, es_1)
              }
              else {
                (HeapBot, es_1)
              }
            case Some(n) if n == 3 =>
              val sucCB = getArgValue(h_1, ctx_1, args, "1")
              val errCB = getArgValue(h_1, ctx_1, args, "2")
              val es_1 =
                if (sucCB._2.exists((l) => F <= Helper.IsCallable(h_1, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_2 =
                if (errCB._2.exists((l) => F <= Helper.IsCallable(h_1, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot

              val h_6 =
                if (lset_this.exists((l) => T <= h_1(l)("powered")._2._1._3)) {
                  val h_2 = lset_this.foldLeft(h_1)((_h, l) => {
                    Helper.PropStore(_h, l, AbsString.alpha("visible"), Value(mode._1._5))
                  })
                  val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_invalidValueserr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                  val h_3 = h_2.update(l_r1, o_arr)
                  val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
                  val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
                  h_5
                } else HeapBot

              val h_7 =
                if (lset_this.exists((l) => F <= h_1(l)("powered")._2._1._3)) {
                val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_serviceNotAvailableerr)), T, T, T)))
                val h_2 = h_1.update(l_r1, o_arr)
                val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
                h_3
              } else HeapBot

              (h_6 + h_7, es_1 ++ es_2)

            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        ),
      "tizen.BluetoothAdapter.discoverDevices" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
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
          val eventCB = getArgValue(h_4, ctx_4, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_4, ctx_4, args, "length"))

          val (h_6, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              if (lset_this.exists((l) => T <= h_4(l)("powered")._2._1._3)) {
                val (h_5, es) = eventCB._2.foldLeft((h_4, TizenHelper.TizenExceptionBot))((_he, l) => {
                  val v1 = Helper.Proto(_he._1, l, AbsString.alpha("onstarted"))
                  val v2 = Helper.Proto(_he._1, l, AbsString.alpha("ondevicefound"))
                  val v3 = Helper.Proto(_he._1, l, AbsString.alpha("ondevicedisappeared"))
                  val v4 = Helper.Proto(_he._1, l, AbsString.alpha("onfinished"))
                  val es1 =
                    if (v1._2.exists((l) => F <= Helper.IsCallable(_he._1, l)))
                      Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  val es2 =
                    if (v2._2.exists((l) => F <= Helper.IsCallable(_he._1, l)))
                      Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  val es3 =
                    if (v3._2.exists((l) => F <= Helper.IsCallable(_he._1, l)))
                      Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  val es4 =
                    if (v4._2.exists((l) => F <= Helper.IsCallable(_he._1, l)))
                      Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(TIZENbluetooth.loc_btdevice), T, T, T)))
                  val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(StrTop), T, T, T)))
                  val o_arr3 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(TIZENbluetooth.loc_btdevicearr), T, T, T)))
                  val h_5 = _he._1.update(l_r1, o_arr1).update(l_r2, o_arr2).update(l_r3, o_arr3)
                  val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("BluetoothDiscvDevsSuccessCB.onstarted"), Value(v1._2), Value(UndefTop))
                  val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("BluetoothDiscvDevsSuccessCB.ondevicefound"), Value(v2._2), Value(l_r1))
                  val h_8 = TizenHelper.addCallbackHandler(h_7, AbsString.alpha("BluetoothDiscvDevsSuccessCB.ondevicedisappeared"), Value(v3._2), Value(l_r2))
                  val h_9 = TizenHelper.addCallbackHandler(h_8, AbsString.alpha("BluetoothDiscvDevsSuccessCB.onfinished"), Value(v4._2), Value(l_r3))
                  (h_9, _he._2 ++ es1 ++ es2 ++ es3 ++ es4)
                })
                (h_5, es)
              }
              else {
                (HeapBot, TizenHelper.TizenExceptionBot)
              }
            case Some(n) if n >= 2 =>
              val errCB = getArgValue(h_4, ctx_4, args, "1")
              val es_1 =
                if (errCB._2.exists((l) => F <= Helper.IsCallable(h_4, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              if (lset_this.exists((l) => T <= h_4(l)("powered")._2._1._3)) {
                val (h_5, es) = eventCB._2.foldLeft((h_4, TizenHelper.TizenExceptionBot))((_he, l) => {
                  val v1 = Helper.Proto(_he._1, l, AbsString.alpha("onstarted"))
                  val v2 = Helper.Proto(_he._1, l, AbsString.alpha("ondevicefound"))
                  val v3 = Helper.Proto(_he._1, l, AbsString.alpha("ondevicedisappeared"))
                  val v4 = Helper.Proto(_he._1, l, AbsString.alpha("onfinished"))
                  val es1 =
                    if (v1._2.exists((l) => F <= Helper.IsCallable(_he._1, l)))
                      Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  val es2 =
                    if (v2._2.exists((l) => F <= Helper.IsCallable(_he._1, l)))
                      Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  val es3 =
                    if (v3._2.exists((l) => F <= Helper.IsCallable(_he._1, l)))
                      Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  val es4 =
                    if (v4._2.exists((l) => F <= Helper.IsCallable(_he._1, l)))
                      Set[WebAPIException](TypeMismatchError)
                    else TizenHelper.TizenExceptionBot
                  val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(TIZENbluetooth.loc_btdevice), T, T, T)))
                  val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(StrTop), T, T, T)))
                  val o_arr3 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(TIZENbluetooth.loc_btdevicearr), T, T, T)))
                  val h_5 = _he._1.update(l_r1, o_arr1).update(l_r2, o_arr2).update(l_r3, o_arr3)
                  val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("BluetoothDiscvDevsSuccessCB.onstarted"), Value(v1._2), Value(UndefTop))
                  val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("BluetoothDiscvDevsSuccessCB.ondevicefound"), Value(v2._2), Value(l_r1))
                  val h_8 = TizenHelper.addCallbackHandler(h_7, AbsString.alpha("BluetoothDiscvDevsSuccessCB.ondevicedisappeared"), Value(v3._2), Value(l_r2))
                  val h_9 = TizenHelper.addCallbackHandler(h_8, AbsString.alpha("BluetoothDiscvDevsSuccessCB.onfinished"), Value(v4._2), Value(l_r3))
                  (h_9, _he._2 ++ es1 ++ es2 ++ es3 ++ es4)
                })
                val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                val h_6 = h_5.update(l_r4, o_arr)
                val h_7 = TizenHelper.addCallbackHandler(h_6, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r4))
                (h_7, es ++ es_1)
              }
              else {
                val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_serviceNotAvailableerr)), T, T, T)))
                val h_5 = h_4.update(l_r4, o_arr)
                val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r4))
                (h_6, es_1)
              }
            case _ => (h_4, TizenHelper.TizenExceptionBot)
          }

          val est = Set[WebAPIException](SecurityError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ est)
          ((h_6, ctx_4), (he + h_e, ctxe + ctx_e))
        }
        ),
      "tizen.BluetoothAdapter.stopDiscovery" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))

          val (h_2, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 0 =>
              (h_1, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 1 =>
              val sucCB = getArgValue(h_1, ctx_1, args, "0")
              val es_1 =
                if (sucCB._2.exists((l) => F <= Helper.IsCallable(h_1, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              if (lset_this.exists((l) => T <= h_1(l)("powered")._2._1._3)) {
                val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
                (h_2, es_1)
              }
              else {
                (HeapBot, es_1)
              }
            case Some(n) if n >= 2 =>
              val sucCB = getArgValue(h_1, ctx_1, args, "0")
              val errCB = getArgValue(h_1, ctx_1, args, "1")
              val es_1 =
                if (sucCB._2.exists((l) => F <=Helper.IsCallable(h_1, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_2 =
                if (errCB._2.exists((l) => F <= Helper.IsCallable(h_1, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot

              val h_5 =
                if (lset_this.exists((l) => T <= h_1(l)("powered")._2._1._3)) {
                  val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                  val h_2 = h_1.update(l_r1, o_arr)
                  val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
                  val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
                  h_4
                } else HeapBot

              val h_6 =
                if (lset_this.exists((l) => F <= h_1(l)("powered")._2._1._3)) {
                  val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_serviceNotAvailableerr)), T, T, T)))
                  val h_2 = h_1.update(l_r1, o_arr)
                  val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
                  h_3
                } else HeapBot

              (h_5 + h_6, es_1 ++ es_2)
            case _ => (h_1, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ est)
          ((h_2, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        ),
      "tizen.BluetoothAdapter.getKnownDevices" -> (
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
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val sucCB = getArgValue(h_2, ctx_2, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val es_1 =
            if (sucCB._2.exists((l) => F <= Helper.IsCallable(h_2, l)))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_3, es_2) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              if (lset_this.exists((l) => T <= h_2(l)("powered")._2._1._3)) {
                val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(TIZENbluetooth.loc_btdevicearr), T, T, T)))
                val h_3 = h_2.update(l_r1, o_arr1)
                val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("BluetoothDevArraySuccessCB"), Value(sucCB._2), Value(l_r1))
                (h_4, TizenHelper.TizenExceptionBot)
              }
              else {
                (HeapBot, TizenHelper.TizenExceptionBot)
              }
            case Some(n) if n >= 2 =>
              val errCB = getArgValue(h_2, ctx_2, args, "1")
              val es_1 =
                if (errCB._2.exists((l) => F <= Helper.IsCallable(h_2, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val h_6 =
                if (lset_this.exists((l) => T <= h_2(l)("powered")._2._1._3)) {
                  val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(TIZENbluetooth.loc_btdevicearr), T, T, T)))
                  val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                  val h_3 = h_2.update(l_r1, o_arr1).update(l_r2, o_arr)
                  val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r2))
                  val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("BluetoothDevArraySuccessCB"), Value(sucCB._2), Value(l_r1))
                  h_5
                } else HeapBot

              val h_7 =
                if (lset_this.exists((l) => F <= h_2(l)("powered")._2._1._3)) {
                  val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_serviceNotAvailableerr)), T, T, T)))
                  val h_3 = h_2.update(l_r2, o_arr)
                  val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r2))
                  h_4
                } else HeapBot

              (h_6 + h_7, es_1)
            case _ => (h_2, TizenHelper.TizenExceptionBot)
          }

          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ est)
          ((h_3, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        ),
      "tizen.BluetoothAdapter.getDevice" -> (
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
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val addr = getArgValue(h_2, ctx_2, args, "0")
          val sucCB = getArgValue(h_2, ctx_2, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val es_1 =
            if (addr._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_2 =
            if (sucCB._2.exists((l) => F <= Helper.IsCallable(h_2, l)))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_3, es_3) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              if (lset_this.exists((l) => T <= h_2(l)("powered")._2._1._3)) {
                val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(TIZENbluetooth.loc_btdevice), T, T, T)))
                val h_3 = h_2.update(l_r1, o_arr1)
                val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("BluetoothDevSuccessCB"), Value(sucCB._2), Value(l_r1))
                (h_4, TizenHelper.TizenExceptionBot)
              }
              else {
                (HeapBot, TizenHelper.TizenExceptionBot)
              }
            case Some(n) if n >= 3 =>
              val errCB = getArgValue(h_2, ctx_2, args, "2")
              val es_1 =
                if (errCB._2.exists((l) => F <= Helper.IsCallable(h_2, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot

              val h_6 =
                if (lset_this.exists((l) => T <= h_2(l)("powered")._2._1._3)) {
                  val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(TIZENbluetooth.loc_btdevice), T, T, T)))
                  val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                  val h_3 = h_2.update(l_r1, o_arr1).update(l_r2, o_arr)
                  val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("BluetoothDevSuccessCB"), Value(sucCB._2), Value(l_r1))
                  val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r2))
                  h_5
                } else HeapBot

              val h_7 =
                if (lset_this.exists((l) => F <= h_2(l)("powered")._2._1._3)) {
                  val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_serviceNotAvailableerr)), T, T, T)))
                  val h_3 = h_2.update(l_r2, o_arr)
                  val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r2))
                  h_4
                } else HeapBot

              (h_6 + h_7, es_1)
            case _ => (h_2, TizenHelper.TizenExceptionBot)
          }

          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ est)
          ((h_3, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        ),
      "tizen.BluetoothAdapter.createBonding" -> (
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
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val addr = getArgValue(h_2, ctx_2, args, "0")
          val sucCB = getArgValue(h_2, ctx_2, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val es_1 =
            if (addr._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_2 =
            if (sucCB._2.exists((l) => F <= Helper.IsCallable(h_2, l)))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_3, es_3) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              if (lset_this.exists((l) => T <= h_2(l)("powered")._2._1._3)) {
                val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(TIZENbluetooth.loc_btdevice), T, T, T)))
                val h_3 = h_2.update(l_r1, o_arr1)
                val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("BluetoothDevSuccessCB"), Value(sucCB._2), Value(l_r1))
                (h_4, TizenHelper.TizenExceptionBot)
              }
              else {
                (HeapBot, TizenHelper.TizenExceptionBot)
              }
            case Some(n) if n >= 3 =>
              val errCB = getArgValue(h_2, ctx_2, args, "2")
              val es_1 =
                if (errCB._2.exists((l) => F <= Helper.IsCallable(h_2, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot

              val h_6 =
                if (lset_this.exists((l) => T <= h_2(l)("powered")._2._1._3)) {
                  val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(TIZENbluetooth.loc_btdevice), T, T, T)))
                  val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                  val h_3 = h_2.update(l_r1, o_arr1).update(l_r2, o_arr)
                  val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("BluetoothDevSuccessCB"), Value(sucCB._2), Value(l_r1))
                  val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r2))
                  h_5
                } else HeapBot

              val h_7 =
                if (lset_this.exists((l) => F <= h_2(l)("powered")._2._1._3)) {
                  val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_serviceNotAvailableerr)), T, T, T)))
                  val h_3 = h_2.update(l_r2, o_arr)
                  val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r2))
                  h_4
                } else HeapBot

              (h_6 + h_7, es_1)
            case _ => (h_2, TizenHelper.TizenExceptionBot)
          }

          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ est)
          ((h_3, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        ),
      "tizen.BluetoothAdapter.destroyBonding" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val addr = getArgValue(h_1, ctx_1, args, "0")
          val sucCB = getArgValue(h_1, ctx_1, args, "1")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))
          val es_1 =
            if (addr._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_2 =
            if (sucCB._2.exists((l) => F <= Helper.IsCallable(h_1, l)))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_3, es_3) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 2 =>
              if (lset_this.exists((l) => T <= h_1(l)("powered")._2._1._3)) {
                val h_2 = TizenHelper.addCallbackHandler(h_1, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
                (h_2, TizenHelper.TizenExceptionBot)
              }
              else {
                (HeapBot, TizenHelper.TizenExceptionBot)
              }
            case Some(n) if n >= 3 =>
              val errCB = getArgValue(h_1, ctx_1, args, "2")
              val es_1 =
                if (errCB._2.exists((l) => F <= Helper.IsCallable(h_1, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot

              val h_5 =
                if (lset_this.exists((l) => T <= h_1(l)("powered")._2._1._3)) {
                  val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                  val h_2 = h_1.update(l_r1, o_arr)
                  val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
                  val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
                  h_4
                } else HeapBot

              val h_6 =
                if (lset_this.exists((l) => T <= h_1(l)("powered")._2._1._3)) {
                  val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_serviceNotAvailableerr)), T, T, T)))
                  val h_2 = h_1.update(l_r1, o_arr)
                  val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
                  h_3
                } else HeapBot

              (h_5 + h_6, es_1)
            case _ => (h_1, TizenHelper.TizenExceptionBot)
          }

          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3 ++ est)
          ((h_3, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        ),
      "tizen.BluetoothAdapter.registerRFCOMMServiceByUUID" -> (
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
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val uuid = getArgValue(h_2, ctx_2, args, "0")
          val name = getArgValue(h_2, ctx_2, args, "1")
          val sucCB = getArgValue(h_2, ctx_2, args, "2")
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))
          val es =
            if (uuid._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_1 =
            if (name._1._5 </ StrTop)
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val es_2 =
            if (sucCB._2.exists((l) => F <= Helper.IsCallable(h_2, l)))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_3, es_3) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 3 =>
              /* check if bluetooth device is turned on/off */
              if (lset_this.exists((l) => T <= h_2(l)("powered")._2._1._3)) {
                val h_3 = Helper.PropStore(h_2, TIZENbluetooth.loc_btservhandler, AbsString.alpha("uuid"), Value(uuid._1._5))
                val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                  update("0", PropValue(ObjectValue(Value(TIZENbluetooth.loc_btservhandler), T, T, T)))
                val h_4 = h_3.update(l_r2, o_arr2)
                val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("BTServiceSuccessCB"), Value(sucCB._2), Value(l_r2))
                (h_5, TizenHelper.TizenExceptionBot)
              }
              else {
                (HeapBot, TizenHelper.TizenExceptionBot)
              }
            case Some(n) if n >= 4 =>
              val errCB = getArgValue(h_2, ctx_2, args, "3")
              val es_3 =
                if (errCB._2.exists((l) => F <= Helper.IsCallable(h_2, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot

              val h_7 =
                if (lset_this.exists((l) => T <= h_2(l)("powered")._2._1._3)) {
                  val h_3 = Helper.PropStore(h_2, TIZENbluetooth.loc_btservhandler, AbsString.alpha("uuid"), Value(uuid._1._5))
                  val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(TIZENbluetooth.loc_btservhandler), T, T, T)))
                  val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_notFounderr) ++ LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
                  val h_4 = h_3.update(l_r1, o_arr).update(l_r2, o_arr2)
                  val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
                  val h_6 = TizenHelper.addCallbackHandler(h_5, AbsString.alpha("BTServiceSuccessCB"), Value(sucCB._2), Value(l_r2))
                  h_6
                } else HeapBot

              val h_8 =
                if (lset_this.exists((l) => F <= h_2(l)("powered")._2._1._3)) {
                  val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                    update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_serviceNotAvailableerr)), T, T, T)))
                  val h_3 = h_2.update(l_r1, o_arr)
                  val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
                  h_4
                } else HeapBot

              (h_7 + h_8, es_3)
            case _ =>
              (h_2, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ es_1 ++ es_2 ++ es_3 ++ est)
          ((h_3, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )
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

object TIZENBluetoothServiceHandler extends Tizen {
  val name = "BluletoothServiceHandler"
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
    ("unregister", AbsBuiltinFunc("tizen.BluetoothServiceHandler.unregister", 2))
  )
  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      "tizen.BluetoothServiceHandler.unregister" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))
          val h_2 = lset_this.foldLeft(h_1)((_h, l) => {
            Helper.PropStore(_h, l, AbsString.alpha("isConnected"), Value(AbsBool.alpha(false)))
          })
          val (h_3, es) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 0 =>
              (h_2, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 1 =>
              val sucCB = getArgValue(h_2, ctx_1, args, "0")
              val es_1 =
                if (sucCB._2.exists((l) => F <= Helper.IsCallable(h_2, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              /* register success Callback */
              val h_3 = TizenHelper.addCallbackHandler(h_2, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
              (h_3, es_1)
            case Some(n) if n >= 2 =>
              val sucCB = getArgValue(h_2, ctx_1, args, "0")
              val errCB = getArgValue(h_2, ctx_1, args, "1")
              val es_1 =
                if (sucCB._2.exists((l) => F <= Helper.IsCallable(h_2, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val es_2 =
                if (errCB._2.exists((l) => F <= Helper.IsCallable(h_2, l)))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(1)).
                update("0", PropValue(ObjectValue(Value(LocSet(TIZENtizen.loc_unknownerr)), T, T, T)))
              val h_3 = h_2.update(l_r1, o_arr)
              val h_4 = TizenHelper.addCallbackHandler(h_3, AbsString.alpha("errorCB"), Value(errCB._2), Value(l_r1))
              val h_5 = TizenHelper.addCallbackHandler(h_4, AbsString.alpha("successCB"), Value(sucCB._2), Value(UndefTop))
              (h_5, es_1 ++ es_2)
            case _ =>
              (HeapBot, TizenHelper.TizenExceptionBot)
          }
          val est = Set[WebAPIException](SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es ++ est)
          ((h_3, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )
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
