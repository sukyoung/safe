/*******************************************************************************
    Copyright (c) 2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMSvg

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.Helper
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import kr.ac.kaist.jsaf.analysis.typing.models.DOMCore.{DOMElement, DOMNode, DOMNodeList}

object SVGTopElement extends DOM {
  private val name = "SVGTopElement"
  
  /* predefined locations */
  val loc_proto = ObjProtoLoc  // dummy
  
  private val elementList = List(
    SVGSVGElement
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List()

  val proto_locset = elementList.foldLeft(LocSetBot)((lset, e) => lset + e.loc_proto)
 
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
  
  // this object has all properites of all svg elements
  override def default_getInsList(): List[(String, PropValue)] = {
    val proplist = elementList.foldLeft[List[(String, PropValue)]](List())((propl, ele) =>
      propl:::ele.default_getInsList()
    )
    // this object has all properties in DOMElement
    DOMElement.getInsList(PropValue(ObjectValue(StrTop, F, T, T))):::proplist
  }
}
