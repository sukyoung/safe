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


object TIZENNDEFMessage extends Tizen {
  private val name = "NDEFMessage"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.NDEFMessage.constructor")),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("toByte", AbsBuiltinFunc("tizen.NDEFMessage.toByte", 0))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.NDEFMessage.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_2, ctx_2) = Helper.Oldify(h, ctx, addr1)
          val n_arglen = Operator.ToUInt32(getArgValue(h_2, ctx_2, args, "length"))

          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENNDEFMessage.loc_proto), F, F, F))).
            update("@extensible", PropValue(T))

          val (h_3, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 0 =>
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
              val h_3 = h_2.update(l_r1, o_arr)
              val o_new2 = o_new.
                update("recordCount", PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, T, T))).
                update("records", PropValue(ObjectValue(Value(l_r1), F, T, T)))
              val h_4 = lset_this.foldLeft(h_3)((_h, l) => _h.update(l, o_new2))
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n >= 1 =>
              val v_1 = getArgValue(h_2, ctx_2, args, "0")
              val es1 =
                if (v_1._2.exists((l) => Helper.IsArray(h_2, l) <= F))
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot

              val (obj, es_) = v_1._2.foldLeft((o_new, TizenHelper.TizenExceptionBot))((_o, ll) => {
                val n_length = Operator.ToUInt32(Helper.Proto(h_2, ll, AbsString.alpha("length")))
                val (__o, ess) = n_length.getAbsCase match {
                  case AbsBot =>
                    (_o._1, TizenHelper.TizenExceptionBot)
                  case _ => AbsNumber.getUIntSingle(n_length) match {
                    case Some(n) => {
                      val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                        val vi = Helper.Proto(h_2, ll, AbsString.alpha(i.toString))
                        val (b_1, esj) = TizenHelper.instanceOf(h_2, vi, Value(TIZENNDEFRecord.loc_proto))
                        val esi =
                          if (b_1._1._3 <= F && vi._1._4 </ NumTop)
                            Set[WebAPIException](TypeMismatchError)
                          else TizenHelper.TizenExceptionBot
                        _e ++ esj ++ esi
                      })
                      val o__ = _o._1.
                        update("recordCount", PropValue(ObjectValue(Value(AbsNumber.alpha(n)), F, T, T))).
                        update("records", PropValue(ObjectValue(Value(ll), F, T, T)))
                      (o__, es__)
                    }
                    case _ => {
                      val vi = Helper.Proto(h_2, ll, AbsString.alpha("@default_number"))
                      val (b_1, esj) = TizenHelper.instanceOf(h_2, vi, Value(TIZENNDEFRecord.loc_proto))
                      val esi =
                        if (b_1._1._3 <= F && vi._1._4 </ NumTop)
                          Set[WebAPIException](TypeMismatchError)
                        else TizenHelper.TizenExceptionBot
                      val o__ = _o._1.
                        update("recordCount", PropValue(ObjectValue(Value(UInt), F, T, T))).
                        update("records", PropValue(ObjectValue(Value(ll), F, T, T)))
                      (o__, esi ++ esj)
                    }
                  }
                }
                (__o, _o._2 ++ ess)
              })
              val h_3 = lset_this.foldLeft(h_2)((_h, l) => _h.update(l, obj))
              (h_3, es1 ++ es_)
            case _ => (h_2, TizenHelper.TizenExceptionBot)
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1)
          ((Helper.ReturnStore(h_3, Value(lset_this)), ctx_2), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.NDEFMessage.toByte" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val o_arr = Helper.NewArrayObject(UInt).
            update("@default_number", PropValue(ObjectValue(Value(NumTop), T, T, T)))
          val h_2 = h_1.update(l_r1, o_arr)
          val est = Set[WebAPIException](UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((Helper.ReturnStore(h_2, Value(l_r1)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        ))
    )
  }

  override def getPreSemanticMap(): Map[String, SemanticFun] = {Map()}
  override def getDefMap(): Map[String, AccessFun] = {Map()}
  override def getUseMap(): Map[String, AccessFun] = {Map()}
}
