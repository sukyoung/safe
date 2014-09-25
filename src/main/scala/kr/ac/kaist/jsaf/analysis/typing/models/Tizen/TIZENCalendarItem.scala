/*******************************************************************************
    Copyright (c) 2013-2014, S-Core.
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

/* Abstract object */
object TIZENCalendarItem extends Tizen {
  private val name = "CalendarItem"
  /* predefined locations */
  val loc_proto = newSystemRecentLoc(name + "Proto")
  /* prototype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("CallbackObject")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible",                 AbsConstValue(PropValue(T))),
    ("convertToString", AbsBuiltinFunc("tizen.CalendarItem.convertToString", 1)),
    ("clone", AbsBuiltinFunc("tizen.CalendarItem.clone", 0))
  )

  override def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )

  override def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("tizen.CalendarItem.convertToString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0")
          val es =
            if (v_1._1._5 </ AbsString.alpha("ICALENDAR_20") || v_1._1._5 </ AbsString.alpha("VCALENDAR_20"))
              Set[WebAPIException](TypeMismatchError)
            else TizenHelper.TizenExceptionBot
          val (h_e, ctx_e) = TizenHelper.TizenRaiseException(h, ctx, es)
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he + h_e, ctxe + ctx_e))
        }
        )),
      ("tizen.CalendarItem.clone" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r1 = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2

          val o_new = lset_this.foldLeft(Obj.empty)((o, l) => o + h_1(l))
          val h_2 = h_1.update(l_r1, o_new)

          val h_3 = h_2.update(l_r1, h_2(l_r1).update(AbsString.alpha("id"), PropValue(ObjectValue(Value(NullTop), F, T, T))))
          ((Helper.ReturnStore(h_3, Value(l_r1)), ctx_1), (he, ctxe))
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
