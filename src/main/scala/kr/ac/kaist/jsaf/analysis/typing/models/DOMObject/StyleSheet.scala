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

// Modeled based on W3C DOM Style Sheets
// Section 1.2 Style Sheet Interfaces
object StyleSheet extends DOM {
  private val name = "StyleSheet"

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
    ("type", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
    ("disabled", AbsConstValue(PropValue(ObjectValue(BoolTop, T, T, T)))),
    ("href", AbsConstValue(PropValue(ObjectValue(Value(StrTop) + Value(NullTop) , F, T, T)))),
    ("title", AbsConstValue(PropValue(ObjectValue(Value(StrTop) + Value(NullTop), F, T, T))))
    // TODO: 'ownerNode', 'parentStyleSheet', 'media'
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
  def getInstList(): List[(String, AbsProperty)] = 
    List(
      ("type", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
      ("disabled", AbsConstValue(PropValue(ObjectValue(BoolTop, T, T, T)))),
      ("href", AbsConstValue(PropValue(ObjectValue(Value(StrTop) + Value(NullTop) , F, T, T)))),
      ("title", AbsConstValue(PropValue(ObjectValue(Value(StrTop) + Value(NullTop), F, T, T))))
    )
  

}
