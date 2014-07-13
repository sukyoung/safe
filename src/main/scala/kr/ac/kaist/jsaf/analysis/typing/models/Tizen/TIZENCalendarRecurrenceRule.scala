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

object TIZENCalendarRecurrenceRule extends Tizen {
  private val name = "CalendarRecurrenceRule"
  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("@scope",                      AbsConstValue(PropValueNullTop)),
    ("@construct",               AbsInternalFunc("tizen.CalendarRecurrenceRule.constructor")),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue)))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
       ("tizen.CalendarRecurrenceRule.constructor" -> (
         (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
           val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
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
           val (h_4, ctx_4) = Helper.Oldify(h_2, ctx_2, addr3)
           val n_arglen = Operator.ToUInt32(getArgValue(h_4, ctx_4, args, "length"))

           val o_new = ObjEmpty.
             update("@class", PropValue(AbsString.alpha("Object"))).
             update("@proto", PropValue(ObjectValue(Value(TIZENCalendarRecurrenceRule.loc_proto), F, F, F))).
             update("@extensible", PropValue(T))

           val (h_5, es) = AbsNumber.getUIntSingle(n_arglen) match {
             case Some(n) if n == 1 =>
               val v_1 = getArgValue(h_4, ctx_4, args, "0")
               val es =
                 if (v_1._1._5 </ AbsString.alpha("DAILY") && v_1._1._5 </ AbsString.alpha("WEEKLY") &&
                 v_1._1._5 </ AbsString.alpha("MONTHLY") && v_1._1._5 </ AbsString.alpha("YEARLY"))
                   Set[WebAPIException](TypeMismatchError)
                 else TizenHelper.TizenExceptionBot
               val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(0))
               val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(0))
               val o_arr3 = Helper.NewArrayObject(AbsNumber.alpha(0))
               val h_5 = h_4.update(l_r1, o_arr1).update(l_r2, o_arr2).update(l_r3, o_arr3)
               val o_new2 = o_new.
                 update("frequency", PropValue(ObjectValue(Value(v_1._1._5), F, T, T))).
                 update("interval", PropValue(ObjectValue(Value(AbsNumber.alpha(1)), F, T, T))).
                 update("untilDate", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                 update("occurrenceCount", PropValue(ObjectValue(Value(AbsNumber.alpha(-1)), F, T, T))).
                 update("daysOfTheWeek", PropValue(ObjectValue(Value(l_r1), F, T, T))).
                 update("setPositions", PropValue(ObjectValue(Value(l_r2), F, T, T))).
                 update("exceptions", PropValue(ObjectValue(Value(l_r3), F, T, T)))
               val h_6 = lset_this.foldLeft(h_5)((_h, l) => _h.update(l, o_new2))
               (h_6, es)
             case Some(n) if n == 2 =>
               val v_1 = getArgValue(h_4, ctx_4, args, "0")
               val v_2 = getArgValue(h_4, ctx_4, args, "1")
               val es =
                 if (v_1._1._5 </ AbsString.alpha("DAILY") && v_1._1._5 </ AbsString.alpha("WEEKLY") &&
                   v_1._1._5 </ AbsString.alpha("MONTHLY") && v_1._1._5 </ AbsString.alpha("YEARLY"))
                   Set[WebAPIException](TypeMismatchError)
                 else TizenHelper.TizenExceptionBot
               val (obj, es_1) = v_2._2.foldLeft((ObjEmpty, TizenHelper.TizenExceptionBot))((_o, l) => {
                 val v_3 = Helper.LookupL(h_4, l, "interval")
                 val o_new2 =
                   if (v_3 </ ValueBot && v_3._1._4 <= NumTop)
                     ObjEmpty.update("interval", PropValue(ObjectValue(Value(v_3._1._4), F, T, T)))
                   else if (v_3 </ ValueBot && v_3._1._4 </ NumTop)
                     ObjEmpty.update("interval", PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, T, T)))
                   else ObjEmpty.update("interval", PropValue(ObjectValue(Value(AbsNumber.alpha(1)), F, T, T)))
                 val v_4 = Helper.LookupL(h_4, l, "untilDate")
                 val (b_1, es_2) = TizenHelper.instanceOf(h_4, v_4, Value(TIZENTZDate.loc_cons))
                 val es_3 =
                   if (v_4 </ ValueBot && b_1._1._3 <= F)
                     Set[WebAPIException](TypeMismatchError)
                   else TizenHelper.TizenExceptionBot
                 val o_new3 = ObjEmpty.update("untilDate", PropValue(ObjectValue(Value(v_4._2), F, T, T)))
                 val v_5 = Helper.LookupL(h_4, l, "occurrenceCount")
                 val o_new4 =
                   if (v_5 </ ValueBot & v_5._1._4 <= NumTop)
                     ObjEmpty.update("occurrenceCount", PropValue(ObjectValue(Value(v_5._1._4), F, T, T)))
                   else if (v_5 </ ValueBot && v_5._1._4 </ NumTop)
                     ObjEmpty.update("occurrenceCount", PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, T, T)))
                   else ObjEmpty
                 val v_6 = Helper.LookupL(h_4, l, "daysOfTheWeek")
                 val o_new5 =
                   if (v_6 </ ValueBot)
                     ObjEmpty.update("daysOfTheWeek", PropValue(ObjectValue(Value(v_6._2), F, T, T)))
                   else ObjEmpty
                 val v_7 = Helper.LookupL(h_4, l, "setPositions")
                 val o_new6 =
                   if (v_7 </ ValueBot)
                     ObjEmpty.update("setPositions", PropValue(ObjectValue(Value(v_7._2), F, T, T)))
                   else ObjEmpty
                 val v_8 = Helper.LookupL(h_4, l, "exceptions")
                 val o_new7 =
                   if (v_8 </ ValueBot)
                     ObjEmpty.update("exceptions", PropValue(ObjectValue(Value(v_8._2), F, T, T)))
                   else ObjEmpty
                 (_o._1 + o_new2 + o_new3 + o_new4 + o_new5 + o_new6 + o_new7, _o._2 ++ es_2 ++ es_3)
               })
               val o_arr1 = Helper.NewArrayObject(AbsNumber.alpha(0))
               val o_arr2 = Helper.NewArrayObject(AbsNumber.alpha(0))
               val o_arr3 = Helper.NewArrayObject(AbsNumber.alpha(0))
               val h_5 = h_4.update(l_r1, o_arr1).update(l_r2, o_arr2).update(l_r3, o_arr3)
               val o_new2 = o_new.
                 update("frequency", PropValue(ObjectValue(Value(v_1._1._5), F, T, T))).
                 update("interval", PropValue(ObjectValue(Value(AbsNumber.alpha(1)), F, T, T))).
                 update("untilDate", PropValue(ObjectValue(Value(NullTop), F, T, T))).
                 update("occurrenceCount", PropValue(ObjectValue(Value(AbsNumber.alpha(-1)), F, T, T))).
                 update("daysOfTheWeek", PropValue(ObjectValue(Value(l_r1), F, T, T))).
                 update("setPositions", PropValue(ObjectValue(Value(l_r2), F, T, T))).
                 update("exceptions", PropValue(ObjectValue(Value(l_r3), F, T, T)))
               val o_new3 = o_new2 + obj
               val h_6 = lset_this.foldLeft(h_5)((_h, l) => _h.update(l, o_new3))
               (h_6, es ++ es_1)
             case _ => {
               (h_4, TizenHelper.TizenExceptionBot)
             }
           }
           val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
           ((Helper.ReturnStore(h_5, Value(lset_this)), ctx_4), (he + h_e, ctxe + ctx_e))
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
