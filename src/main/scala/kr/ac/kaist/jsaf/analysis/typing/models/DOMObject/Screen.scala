/*******************************************************************************
    Copyright (c) 2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMObject

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import org.w3c.dom.Node
import org.w3c.dom.Element
import kr.ac.kaist.jsaf.analysis.typing.models.DOMCore.DOMNode
import kr.ac.kaist.jsaf.analysis.typing.models.DOMCore.DOMElement
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import scala.Some
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

// non-standard object
object Screen extends DOM {
  private val name = "screen"

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
    ("availTop", AbsConstValue(PropValue(ObjectValue(UInt, F, T, T)))),
    ("availLeft", AbsConstValue(PropValue(ObjectValue(UInt, F, T, T)))),
    ("availHeight", AbsConstValue(PropValue(ObjectValue(UInt, F, T, T)))),
    ("availWidth", AbsConstValue(PropValue(ObjectValue(UInt, F, T, T)))),
    ("colorDepth", AbsConstValue(PropValue(ObjectValue(UInt, F, T, T)))),
    ("height", AbsConstValue(PropValue(ObjectValue(UInt, F, T, T)))),
    ("left", AbsConstValue(PropValue(ObjectValue(UInt, F, T, T)))),
    ("top", AbsConstValue(PropValue(ObjectValue(UInt, F, T, T)))),
    ("width", AbsConstValue(PropValue(ObjectValue(UInt, F, T, T)))),
    ("pixelDepth", AbsConstValue(PropValue(ObjectValue(UInt, F, T, T))))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_ins, T, F, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto), (loc_ins, prop_ins), (GlobalLoc, prop_global)
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
  override def getInstance(cfg: CFG): Option[Loc] = Some(newRecentLoc())
  /* list of properties in the instance object */
}
