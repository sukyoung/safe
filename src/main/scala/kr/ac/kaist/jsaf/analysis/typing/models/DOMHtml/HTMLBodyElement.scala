/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import org.w3c.dom.Node
import org.w3c.dom.Element
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._


object HTMLBodyElement extends DOM {
  private val name = "HTMLBodyElement"

  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")

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
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(HTMLElement.loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue)))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)
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


  /* semantics */
  // no function
 
  /* instance */
  // predefined location : only one 'HTMLBodyElement' element can be present in the heap
  val loc_ins = newSystemRecentLoc("HTMLBodyElement")
  override def getInstance(cfg: CFG): Option[Loc] = Some(loc_ins)
  def getInstance(): Option[Loc] = Some(loc_ins)
  /* list of properties in the instance object */
  override def getInsList(node: Node): List[(String, PropValue)] = node match {
    case e: Element => 
      // This object has all properties of the HTMLElement object 
      HTMLElement.getInsList(node) ++ List(
      ("@class",    PropValue(AbsString.alpha("Object"))),
      ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
      ("@extensible", PropValue(BoolTrue)),
      // DOM Level 1
      ("aLink",     PropValue(ObjectValue(AbsString.alpha(e.getAttribute("aLink")), T, T, T))),
      ("background", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("background")), T, T, T))),
      ("bgColor",   PropValue(ObjectValue(AbsString.alpha(e.getAttribute("bgColor")), T, T, T))),
      ("link",      PropValue(ObjectValue(AbsString.alpha(e.getAttribute("link")), T, T, T))),
      ("text",   PropValue(ObjectValue(AbsString.alpha(e.getAttribute("text")), T, T, T))),
      ("vLink",   PropValue(ObjectValue(AbsString.alpha(e.getAttribute("vLink")), T, T, T))))
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }
   
  def getInsList(aLink: PropValue, background: PropValue, bgColor: PropValue, link: PropValue,
                                                            text: PropValue, vLink: PropValue): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    // DOM Level 1
    ("aLink", aLink),
    ("background",  background), 
    ("bgColor",   bgColor),
    ("link", link), 
    ("text",   text), 
    ("vLink",   vLink)
   )
  
  override def default_getInsList(): List[(String, PropValue)] = { 
    val aLink = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val background = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val bgColor = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val link = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val text = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val vLink = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    // This object has all properties of the HTMLElement object 
    HTMLElement.default_getInsList ::: 
      getInsList(aLink, background, bgColor, link, text, vLink)
  }

}
