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

// Modeled based on W3C DOM CSS
// Section 2.2 CSS Fundamental Interfaces
object CSSStyleSheet extends DOM {
  private val name = "CSSStyleSheet"

  /* predefined locations */
  val loc_ins = newSystemRecentLoc(name + "Ins")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  
  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(StyleSheet.loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("insertRule",   AbsBuiltinFunc("CSSStyleSheet.insertRule", 2)),
    ("deleteRule",   AbsBuiltinFunc("CSSStyleSheet.deleteRule", 1))
  )
  /* instance */
  private val prop_ins: List[(String, AbsProperty)] = 
  // has all properties of 'StyleSheet'
  StyleSheet.getInstList() ++
  List(
    ("@class",    AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",    AbsConstValue(PropValue(ObjectValue(loc_proto, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue)))
    // TODO: ownerRule, cssRules
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
