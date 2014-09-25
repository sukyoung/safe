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
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object MouseEvent extends DOM {
  private val name = "MouseEvent"

  /* predefined locations */
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
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(UIEvent.loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("initMouseEvent", AbsBuiltinFunc("MouseEvent.initMouseEvent", 15))
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
      //case "MouseEvent.initMouseEvent"
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //case "MouseEvent.initMouseEvent"
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      //case "MouseEvent.initMouseEvent"
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //case "MouseEvent.initMouseEvent"
    )
  }

  /* instance */
  def getInstList(lset_currenttarget: LocSet, lset_target: LocSet): List[(String, PropValue)] = {
    // this object has all properties of the UIEvent object
    UIEvent.getInstList(lset_currenttarget, lset_target) ++ 
    List(
      // Introduced in DOM Level 2
      ("screenX", PropValue(ObjectValue(Value(NumTop), F,T,F))),
      ("screenY", PropValue(ObjectValue(Value(NumTop), F,T,F))),
      ("pageX", PropValue(ObjectValue(Value(NumTop), F,T,F))),
      ("pageY", PropValue(ObjectValue(Value(NumTop), F,T,F))),
      ("clientX", PropValue(ObjectValue(Value(NumTop), F,T,F))),
      ("clientY", PropValue(ObjectValue(Value(NumTop), F,T,F))),
      ("x", PropValue(ObjectValue(Value(NumTop), F,T,F))),
      ("y", PropValue(ObjectValue(Value(NumTop), F,T,F))),
      ("offsetX", PropValue(ObjectValue(Value(NumTop), F,T,F))),
      ("offsetY", PropValue(ObjectValue(Value(NumTop), F,T,F))),
      ("ctrlKey", PropValue(ObjectValue(Value(BoolTop), F,T,F))),
      ("shiftKey", PropValue(ObjectValue(Value(BoolTop), F,T,F))),
      ("altKey", PropValue(ObjectValue(Value(BoolTop), F,T,F))),
      ("metaKey", PropValue(ObjectValue(Value(BoolTop), F,T,F))),
      ("button", PropValue(ObjectValue(Value(UInt), F,T,F)))
    //("relateTarget", ...) 
    )
  }
  
  val instProps = Set("screenX", "screenY", "pageX", "pageY", "clientX", "clientY", "x", "y", "offsetX", "offsetY", "ctrlKey", "shiftKey", "altKey", "metaKey", "button")
}
