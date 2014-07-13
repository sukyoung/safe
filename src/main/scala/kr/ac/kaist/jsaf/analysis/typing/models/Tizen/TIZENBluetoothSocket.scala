/*******************************************************************************
    Copyright (c) 2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.Tizen

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._


import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG}
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T, _}

object TIZENBluetoothSocket extends Tizen {
  val name = "BluetoothSocket"
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
    ("writeData", AbsBuiltinFunc("tizen.BluetoothSocket.writeData", 1)),
    ("readData", AbsBuiltinFunc("tizen.BluetoothSocket.readData", 0)),
    ("close", AbsBuiltinFunc("tizen.BluetoothSocket.close", 0))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.BluetoothSocket.writeData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val data = getArgValue(h, ctx, args, "0")
          val es_ = data._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
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
          val est = Set[WebAPIException](UnknownError, SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_ ++ est)
          ((Helper.ReturnStore(h, Value(UInt)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.BluetoothSocket.readData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val est = Set[WebAPIException](UnknownError, SecurityError, NotSupportedError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h, Value(TIZENbluetooth.loc_shortarr)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.BluetoothSocket.close" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val h_1 = lset_this.foldLeft(h)((_h, l) => {
            Helper.PropStore(_h, l, AbsString.alpha("state"), Value(AbsString.alpha("CLOSED")))
          })
          val h_2 = lset_this.foldLeft(h_1)((_h, l) => {
            Helper.PropStore(_h, l, AbsString.alpha("peer"), Value(NullTop))
          })
          val h_3 = lset_this.foldLeft(h_2)((_h, l) => {
            val v1 = Helper.Proto(_h, l, AbsString.alpha("onclose"))
            v1._2.foldLeft(_h)((__h, l) => {
              if (Helper.IsCallable(__h, l) <= T)
                TizenHelper.addCallbackHandler(__h, AbsString.alpha("successCB"), Value(v1._2), Value(UndefTop))
              else __h
            })
          })
          val est = Set[WebAPIException](UnknownError, SecurityError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((h_3, ctx), (he + h_e, ctxe + ctx_e))
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