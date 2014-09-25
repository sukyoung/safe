/*******************************************************************************
    Copyright (c) 2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMSvg

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.models.DOMCore.DOMNode
import kr.ac.kaist.jsaf.analysis.typing.models.DOMCore.DOMElement
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import scala.Some
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

// Modeled based on W3C Scalable Vector Graphics (SVG) 1.1 (Second Edition)
// Section 4.5.1 Interface SVGElement
object SVGElement extends DOM {
  private val name = "SVGElement"

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
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(DOMElement.loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue)))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
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
   
  def getInsList(id: PropValue, xmlbase: PropValue): List[(String, PropValue)] = {
    List(("id", id), 
    ("xml", xmlbase)
    // TODO: 'ownerSVGElement', 'viewportElement'
    )
  }

  override def default_getInsList(): List[(String, PropValue)] =
    getInsList(PropValue(ObjectValue(AbsString.alpha(""), T, T, T)),
               PropValue(ObjectValue(StrTop, T, T, T)))

}
