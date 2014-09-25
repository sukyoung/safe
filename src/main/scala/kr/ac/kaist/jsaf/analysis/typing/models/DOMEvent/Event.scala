/*******************************************************************************
    Copyright (c) 2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMEvent

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.{ControlPoint, Helper, Semantics}
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr}
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object Event extends DOM {
  private val name = "Event"

  /* predefined locatoins */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  val loc_ins = newSystemRecentLoc(name + "Ins")

  /* constructor */
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("length", AbsConstValue(PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, F, F)))),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("AT_TARGET",       AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, T, T)))),
    ("BUBBLING_PHASE",  AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(2), F, T, T)))),
    ("CAPTURING_PHASE", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(3), F, T, T))))
  )

  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("stopPropagation", AbsBuiltinFunc("Event.stopPropagation", 0)),
    ("preventDefault",  AbsBuiltinFunc("Event.preventDefault", 0)),
    ("initEvent",       AbsBuiltinFunc("Event.initEvent", 3))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      // do nothing : we do not consider the event bubbling and capturing
      ("Event.stopPropagation" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("Event.preventDefault" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        }))
      //case "Event.initEvent"
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //case "Event.stopPropagation"
      //case "Event.preventDefault"
      //case "Event.initEvent"
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      //case "Event.stopPropagation"
      //case "Event.preventDefault"
      //case "Event.initEvent"
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //case "Event.stopPropagation"
      //case "Event.preventDefault"
      //case "Event.initEvent"
    )
  }

  /* instance */
  def getInstList(lset_currenttarget: LocSet, lset_target: LocSet): List[(String, PropValue)] = {
    List(
      ("type", PropValue(ObjectValue(Value(OtherStr), F,T,F))),
      ("target", PropValue(ObjectValue(Value(lset_target), F,T,F))),
      ("currentTarget", PropValue(ObjectValue(Value(lset_currenttarget), F,T,F))),
      ("eventPhase", PropValue(ObjectValue(Value(UInt), F,T,F))),
      ("bubbles", PropValue(ObjectValue(Value(BoolFalse), F,T,F))),
      ("cancelable", PropValue(ObjectValue(Value(BoolFalse), F,T,F))),
      ("defaultPrevented", PropValue(ObjectValue(Value(BoolFalse), F,T,F))),
      ("timeStamp", PropValue(ObjectValue(Value(DOMEventTimeLoc), F,T,F))),
      // Non-standard : used by IE
      ("srcElement", PropValue(ObjectValue(Value(lset_target), F,T,F))),
      ("returnValue", PropValue(ObjectValue(Value(BoolTrue), F,T,F)))
    )
  }
  val instProps = Set("type", "target", "currentTarget", "eventPhase", "bubbles", "cancelable", "defaultPrevented", "timeStamp", "srcElement")

}
