/*******************************************************************************
    Copyright (c) 2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMObject

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import scala.Some
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

// Modeled based on W3C CSSOM View Module
// Section 10.2 The ClientRect Interface
object ClientRect extends DOM {
  private val name = "ClientRect"

  /* predefined locations */
  val loc_ins = newSystemRecentLoc(name + "Ins")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  
  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue)))
  )
  /* instance */
  private val prop_ins: List[(String, AbsProperty)] = List(
    ("@class",    AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",    AbsConstValue(PropValue(ObjectValue(loc_proto, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("top", AbsConstValue(PropValue(ObjectValue(NumTop, F, T, T)))),
    ("right", AbsConstValue(PropValue(ObjectValue(NumTop, F, T, T)))),
    ("bottom", AbsConstValue(PropValue(ObjectValue(NumTop, F, T, T)))),
    ("left", AbsConstValue(PropValue(ObjectValue(NumTop, F, T, T)))),
    ("width", AbsConstValue(PropValue(ObjectValue(NumTop, F, T, T)))),
    ("height", AbsConstValue(PropValue(ObjectValue(NumTop, F, T, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto), (loc_ins, prop_ins)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map()
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map()
  }

  override def getProto(): Option[Loc] = Some(loc_proto) 

  /* semantics */  
  // no function

  /* instance */
}
