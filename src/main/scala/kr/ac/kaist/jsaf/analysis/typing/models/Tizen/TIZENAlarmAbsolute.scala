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
import kr.ac.kaist.jsaf.analysis.typing.models.builtin.BuiltinDate
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context

object TIZENAlarmAbsolute extends Tizen {
  val name = "AlarmAbsolute"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")

  /* constructor */
  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto)
  )
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.AlarmAbsolute.constructor")),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("getNextScheduledDate", AbsBuiltinFunc("tizen.AlarmAbsolute.getNextScheduledDate", 0))
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.AlarmAbsolute.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r = addrToLoc(addr1, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val v_1 = getArgValue(h_1, ctx_1, args, "0")
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))
          val (b_1, es_1) = TizenHelper.instanceOf(h_1, v_1, Value(BuiltinDate.ProtoLoc))
          val es_2 =
            if (b_1._1._3 <= F) {
              Set[WebAPIException](TypeMismatchError)
            }
            else TizenHelper.TizenExceptionBot

          val o_new = Obj.empty.update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENAlarmAbsolute.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("id", PropValue(ObjectValue(Value(NullTop), F, T, T)))

          val (h_2, es_3) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 1 =>
              val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
              val h_2 = h_1.update(l_r, o_arr)
              // case for "new tizen.AlarmAbsolute(date)"
              val o_new2 = o_new.
                update("date", PropValue(ObjectValue(Value(v_1._2), F, T, T))).
                update("period", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                update("daysOfTheWeek", PropValue(ObjectValue(Value(l_r), F, T, T)))
              val h_3 = lset_this.foldLeft(h_2)((_h, l) => _h.update(l, o_new2))
              (h_3, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_2 = getArgValue(h_1, ctx_1, args, "1")
              if (v_2._1._4 </ NumBot) {
                // case for "new tizen.AlarmAbsolute(date, period)"
                val o_arr = Helper.NewArrayObject(AbsNumber.alpha(0))
                val h_2 = h_1.update(l_r, o_arr)
                val o_new2 = o_new.
                  update("date", PropValue(ObjectValue(Value(v_1._2), F, T, T))).
                  update("period", PropValue(ObjectValue(Value(v_2._1._4), F, T, T))).
                  update("daysOfTheWeek", PropValue(ObjectValue(Value(l_r), F, T, T)))
                val h_3 = lset_this.foldLeft(h_2)((_h, l) => _h.update(l, o_new2))
                (h_3, TizenHelper.TizenExceptionBot)
              }
              else {
                // case for "new tizen.AlarmAbsolute(date, daysOfTheWeek)"
                val es =
                  if (v_2._2.exists((l) => Helper.IsArray(h_1, l) <= F))
                    Set[WebAPIException](TypeMismatchError)
                  else TizenHelper.TizenExceptionBot
                val es_ = v_2._2.foldLeft(TizenHelper.TizenExceptionBot)((_es, ll) => {
                  val n_length = Operator.ToUInt32(Helper.Proto(h_1, ll, AbsString.alpha("length")))
                  val ess = n_length.getAbsCase match {
                    case AbsBot =>
                      TizenHelper.TizenExceptionBot
                    case _ => AbsNumber.getUIntSingle(n_length) match {
                      case Some(n) => {
                        val es__ = (0 until n.toInt).foldLeft(TizenHelper.TizenExceptionBot)((_e, i) => {
                          val vi = Helper.Proto(h_1, ll, AbsString.alpha(i.toString))
                          val esi =
                            if (vi._1._5 != AbsString.alpha("MO") && vi._1._5 != AbsString.alpha("TU") &&
                              vi._1._5 != AbsString.alpha("WE") && vi._1._5 != AbsString.alpha("TH") &&
                              vi._1._5 != AbsString.alpha("FR") && vi._1._5 != AbsString.alpha("SA") &&
                              vi._1._5 != AbsString.alpha("SU"))
                              Set[WebAPIException](TypeMismatchError)
                            else TizenHelper.TizenExceptionBot
                          _e ++ esi
                        })
                        es__
                      }
                      case _ => {
                        val vi = Helper.Proto(h_1, ll, AbsString.alpha(Str_default_number))
                        val esi =
                          if (vi._1._5 != AbsString.alpha("MO") && vi._1._5 != AbsString.alpha("TU") &&
                            vi._1._5 != AbsString.alpha("WE") && vi._1._5 != AbsString.alpha("TH") &&
                            vi._1._5 != AbsString.alpha("FR") && vi._1._5 != AbsString.alpha("SA") &&
                            vi._1._5 != AbsString.alpha("SU"))
                            Set[WebAPIException](TypeMismatchError)
                          else TizenHelper.TizenExceptionBot
                        esi
                      }
                    }
                  }
                  _es ++ ess
                })
                val o_new2 = o_new.
                  update("date", PropValue(ObjectValue(Value(v_1._2), F, T, T))).
                  update("period", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                  update("daysOfTheWeek", PropValue(ObjectValue(Value(v_2._2), F, T, T)))
                val h_2 = lset_this.foldLeft(h_1)((_h, l) => _h.update(l, o_new2))
                (h_2, es ++ es_)
              }
            case _ => {
              (h_1, TizenHelper.TizenExceptionBot)
            }
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1 ++ es_2 ++ es_3)
          ((Helper.ReturnStore(h_2, Value(lset_this)), ctx_1), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.AlarmAbsolute.getNextScheduledDate" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          val h_1 =
            if (lset_this.exists((l) => Helper.Proto(h, l, AbsString.alpha("id"))._1._5 </ StrBot)) {
              Helper.ReturnStore(h, Value(TIZENtizen.loc_date))
            }
            else {
              Helper.ReturnStore(h, Value(NullTop))
            }
          val est = Set[WebAPIException](SecurityError, UnknownError)
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, est)
          ((h_1, ctx), (he + h_e, ctxe + ctx_e))
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
