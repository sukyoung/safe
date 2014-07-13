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

object TIZENCalendarAttendee extends Tizen {
  val name = "CalendarAttendee"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.CalendarAttendee.constructor")),
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
      ("tizen.CalendarAttendee.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val n_arglen = Operator.ToUInt32(getArgValue(h_1, ctx_1, args, "length"))

          val o_contRef = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENContactRef.loc_proto), F, F, F))).
            update("@extensible", PropValue(T)).
            update("addressBookId", PropValue(ObjectValue(Value(AbsString.alpha("")), F, T, T))).
            update("contactId", PropValue(ObjectValue(Value(AbsString.alpha("")), F, T, T)))
          val h_2 = h_1.update(l_r1, o_contRef)

          val o_new = ObjEmpty.
            update("@class", PropValue(AbsString.alpha("Object"))).
            update("@proto", PropValue(ObjectValue(Value(TIZENCalendarAttendee.loc_proto), F, F, F))).
            update("@extensible", PropValue(T))

          val (h_3, es_1) = AbsNumber.getUIntSingle(n_arglen) match {
            case Some(n) if n == 0 =>
              val o_new2 = o_new.
                update("uri", PropValue(ObjectValue(Value(AbsString.alpha("")), F, T, T))).
                update("name", PropValue(ObjectValue(Value(AbsString.alpha("")), F, T, T))).
                update("role", PropValue(ObjectValue(Value(AbsString.alpha("REQ_PARTICIPANT")), F, T, T))).
                update("status", PropValue(ObjectValue(Value(AbsString.alpha("PENDING")), F, T, T))).
                update("RSVP", PropValue(ObjectValue(Value(F), F, T, T))).
                update("type", PropValue(ObjectValue(Value(AbsString.alpha("INDIVIDUAL")), F, T, T))).
                update("group", PropValue(ObjectValue(Value(AbsString.alpha("")), F, T, T))).
                update("delegatorURI", PropValue(ObjectValue(Value(AbsString.alpha("")), F, T, T))).
                update("delegateURI", PropValue(ObjectValue(Value(AbsString.alpha("")), F, T, T))).
                update("contactRef", PropValue(ObjectValue(Value(l_r1), F, T, T)))
              val h_3 = lset_this.foldLeft(h_2)((_h, l) => _h.update(l, o_new2))
              (h_3, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 1 =>
              val v_1 = getArgValue(h_2, ctx_1, args, "0")
              val o_new2 = o_new.
                update("uri", PropValue(ObjectValue(Value(Helper.toString(v_1._1)), F, T, T))).
                update("name", PropValue(ObjectValue(Value(AbsString.alpha("")), F, T, T))).
                update("role", PropValue(ObjectValue(Value(AbsString.alpha("REQ_PARTICIPANT")), F, T, T))).
                update("status", PropValue(ObjectValue(Value(AbsString.alpha("PENDING")), F, T, T))).
                update("RSVP", PropValue(ObjectValue(Value(F), F, T, T))).
                update("type", PropValue(ObjectValue(Value(AbsString.alpha("INDIVIDUAL")), F, T, T))).
                update("group", PropValue(ObjectValue(Value(AbsString.alpha("")), F, T, T))).
                update("delegatorURI", PropValue(ObjectValue(Value(AbsString.alpha("")), F, T, T))).
                update("delegateURI", PropValue(ObjectValue(Value(AbsString.alpha("")), F, T, T))).
                update("contactRef", PropValue(ObjectValue(Value(l_r1), F, T, T)))
              val h_3 = lset_this.foldLeft(h_2)((_h, l) => _h.update(l, o_new2))
              (h_3, TizenHelper.TizenExceptionBot)
            case Some(n) if n == 2 =>
              val v_1 = getArgValue(h_2, ctx_1, args, "0")
              val v_2 = getArgValue(h_2, ctx_1, args, "1")
              val es =
                if (v_2._1 <= PValueTop)
                  Set[WebAPIException](TypeMismatchError)
                else TizenHelper.TizenExceptionBot
              val (v_3, v_4, v_5, v_6, v_7, v_8, v_9, v_10, v_11) =
                v_2._2.foldLeft((ValueBot, ValueBot, ValueBot, ValueBot, ValueBot, ValueBot, ValueBot, ValueBot, ValueBot))((_v, l) => {
                (_v._1 + Helper.LookupL(h_2, l, "name"), _v._2 + Helper.LookupL(h_2, l, "role"), _v._3 + Helper.LookupL(h_2, l, "status"),
                  _v._4 + Helper.LookupL(h_2, l, "RSVP"), _v._5 + Helper.LookupL(h_2, l, "type"), _v._6 + Helper.LookupL(h_2, l, "group"),
                  _v._7 + Helper.LookupL(h_2, l, "delegatorURI"), _v._8 + Helper.LookupL(h_2, l, "delegateURI"), _v._9 + Helper.LookupL(h_2, l, "contactRef"))
              })
              val o_new2 = o_new.
                update("uri", PropValue(ObjectValue(Value(Helper.toString(v_1._1)), F, T, T))).
                update("name", PropValue(ObjectValue(Value(v_3._1._5), F, T, T))).
                update("role", PropValue(ObjectValue(Value(v_4._1._5), F, T, T))).
                update("status", PropValue(ObjectValue(Value(v_5._1._5), F, T, T))).
                update("RSVP", PropValue(ObjectValue(Value(v_6._1._3), F, T, T))).
                update("type", PropValue(ObjectValue(Value(v_7._1._5), F, T, T))).
                update("group", PropValue(ObjectValue(Value(v_8._1), F, T, T))).
                update("delegatorURI", PropValue(ObjectValue(Value(v_9._1), F, T, T))).
                update("delegateURI", PropValue(ObjectValue(Value(v_10._1), F, T, T))).
                update("contactRef", PropValue(ObjectValue(Value(v_11._2), F, T, T)))
              val h_3 = lset_this.foldLeft(h_2)((_h, l) => _h.update(l, o_new2))
              (h_3, es)
            case _ => {
              (h_2, TizenHelper.TizenExceptionBot)
            }
          }
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es_1)
          ((Helper.ReturnStore(h_3, Value(lset_this)), ctx_1), (he + h_e, ctxe + ctx_e))
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
