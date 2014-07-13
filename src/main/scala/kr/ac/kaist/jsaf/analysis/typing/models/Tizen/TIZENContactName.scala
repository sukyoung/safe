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

object TIZENContactName extends Tizen {
  private val name = "ContactName"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.ContactName.constructor")),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T)))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.ContactName.constructor" -> (
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
            update("@proto", PropValue(ObjectValue(Value(TIZENContactName.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("displayName", PropValue(ObjectValue(Value(NullTop), F, T, T)))

          val (h_3, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 0 =>
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
              val h_3 = h_2.update(l_r1, o_arr)
              val o_new2 = o_new.
                update("prefix", PropValue(ObjectValue(Value(NullTop), T, T, T))).
                update("suffix", PropValue(ObjectValue(Value(NullTop), T, T, T))).
                update("firstName", PropValue(ObjectValue(Value(NullTop), T, T, T))).
                update("middleName", PropValue(ObjectValue(Value(NullTop), T, T, T))).
                update("lastName", PropValue(ObjectValue(Value(NullTop), T, T, T))).
                update("nicknames", PropValue(ObjectValue(Value(l_r1), T, T, T))).
                update("phoneticFirstName", PropValue(ObjectValue(Value(NullTop), T, T, T))).
                update("phoneticLastName", PropValue(ObjectValue(Value(NullTop), T, T, T)))
              val h_4 = lset_this.foldLeft(h_3)((_h, l) => _h.update(l, o_new2))
              (h_4, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 1 =>
              val v_1 = getArgValue(h_2, ctx_2, args, "0")
              val es =
                if (v_1._1 </ PValueBot)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot

              val (obj, es_1) = v_1._2.foldLeft((o_new, TizenHelper.TizenExceptionBot))((_o, l) => {
                val v_1 = Helper.Proto(h_2, l, AbsString.alpha("prefix"))
                val v_2 = Helper.Proto(h_2, l, AbsString.alpha("suffix"))
                val v_3 = Helper.Proto(h_2, l, AbsString.alpha("firstName"))
                val v_4 = Helper.Proto(h_2, l, AbsString.alpha("middleName"))
                val v_5 = Helper.Proto(h_2, l, AbsString.alpha("lastName"))
                val v_6 = Helper.Proto(h_2, l, AbsString.alpha("nicknames"))
                val v_7 = Helper.Proto(h_2, l, AbsString.alpha("phoneticFirstName"))
                val v_8 = Helper.Proto(h_2, l, AbsString.alpha("phoneticLastName"))
                val es_1 =
                  if (v_1._1._1 </ UndefTop && v_1._1._5 <= StrBot)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_1 =
                  if (v_1._1._5 </ StrBot)
                    _o._1.update("prefix", PropValue(ObjectValue(Value(v_1._1._5), T, T, T)))
                  else _o._1.update("prefix", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                val es_2 =
                  if (v_2._1._1 </ UndefTop && v_2._1._5 <= StrBot)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_2 =
                  if (v_2._1._5 </ StrBot)
                    _o._1.update("suffix", PropValue(ObjectValue(Value(v_2._1._5), T, T, T)))
                  else _o._1.update("suffix", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                val es_3 =
                  if (v_3._1._1 </ UndefTop && v_3._1._5 <= StrBot)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_3 =
                  if (v_3._1._5 </ StrBot)
                    _o._1.update("firstName", PropValue(ObjectValue(Value(v_3._1._5), T, T, T)))
                  else _o._1.update("firstName", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                val es_4 =
                  if (v_4._1._1 </ UndefTop && v_4._1._5 <= StrBot)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_4 =
                  if (v_4._1._5 </ StrBot)
                    _o._1.update("middleName", PropValue(ObjectValue(Value(v_4._1._5), T, T, T)))
                  else _o._1.update("middleName", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                val es_5 =
                  if (v_5._1._1 </ UndefTop && v_5._1._5 <= StrBot)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_5 =
                  if (v_5._1._5 </ StrBot)
                    _o._1.update("lastName", PropValue(ObjectValue(Value(v_5._1._5), T, T, T)))
                  else _o._1.update("lastName", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                val (o_6, es_6) =
                  if (v_6._1._1 <= UndefBot) {
                    val es_ = v_6._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                      val n_length = Operator.ToUInt32(Helper.Proto(h_2, ll, AbsString.alpha("length")))
                      val ess = n_length.getAbsCase match {
                        case AbsBot => TizenHelper.TizenExceptionBot
                        case _ => AbsNumber.getUIntSingle(n_length) match {
                          case Some(n) => {
                            val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                              val vi = Helper.Proto(h_2, ll, AbsString.alpha(i.toString))
                              val esi =
                                if (vi._1._5 <= StrBot) Set[WebAPIException](TypeMismatchError)
                                else TizenHelper.TizenExceptionBot
                              _e ++ esi
                            })
                            es__
                          }
                          case _ => {
                            val vi = Helper.Proto(h_2, ll, AbsString.alpha("@default_number"))
                            val esi =
                              if (vi._1._5 <= StrBot) Set[WebAPIException](TypeMismatchError)
                              else TizenHelper.TizenExceptionBot
                            esi
                          }
                        }
                      }
                      _es ++ ess
                    })
                    (_o._1.update("nicknames", PropValue(ObjectValue(Value(v_6._2), T, T, T))), es_)
                  }
                  else (_o._1.update("nicknames", PropValue(ObjectValue(Value(l_r1), T, T, T))), TizenHelper.TizenExceptionBot)
                val es_7 =
                  if (v_7._1._1 </ UndefTop && v_7._1._5 <= StrBot)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_7 =
                  if (v_7._1._5 </ StrBot)
                    _o._1.update("phoneticFirstName", PropValue(ObjectValue(Value(v_7._1._5), T, T, T)))
                  else _o._1.update("phoneticFirstName", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                val es_8 =
                  if (v_8._1._1 </ UndefTop && v_8._1._5 <= StrBot)
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val o_8 =
                  if (v_8._1._5 </ StrBot)
                    _o._1.update("phoneticLastName", PropValue(ObjectValue(Value(v_8._1._5), T, T, T)))
                  else _o._1.update("phoneticLastName", PropValue(ObjectValue(Value(NullTop), T, T, T)))
                (o_1 + o_2 + o_3 + o_4 + o_5 + o_6 + o_7 + o_8,
                  _o._2 ++ es_1 ++ es_2 ++ es_3 ++ es_4 ++ es_5 ++ es_6 ++ es_7 ++ es_8)
              })
              val h_3 = lset_this.foldLeft(h_2)((_h, l) => _h.update(l, obj))
              (h_3, es ++ es_1)
            case _ =>
              (h_2, TizenHelper.TizenExceptionBot)
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1)
          ((Helper.ReturnStore(h_3, Value(lset_this)), ctx_2), (he + h_e, ctxe + ctx_e))
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
