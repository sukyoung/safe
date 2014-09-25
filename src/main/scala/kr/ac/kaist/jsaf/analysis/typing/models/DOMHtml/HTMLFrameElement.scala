/*******************************************************************************
    Copyright (c) 2013-2014, KAIST, S-Core.
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
import kr.ac.kaist.jsaf.Shell

object HTMLFrameElement extends DOM {
  private val name = "HTMLFrameElement"

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
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )
   
  /* instance */
  private val prop_ins: List[(String, AbsProperty)] = 
       HTMLElement.getInsList2() ++ List(
      ("@class",    AbsConstValue(PropValue(AbsString.alpha("Object")))),
      ("@proto",    AbsConstValue(PropValue(ObjectValue(loc_proto, F, F, F)))),
      ("@extensible", AbsConstValue(PropValue(BoolTrue))),
      // DOM Level 1
      ("frameBorder", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("longDesc", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("marginHeight", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("marginWidth", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("name", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("noResize", AbsConstValue(PropValue(ObjectValue(BoolTop, T, T, T)))),
      ("scrolling", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("src", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("contentDocument", AbsConstValue(PropValue(ObjectValue(Value(HTMLDocument.loc_ins), F, T, T))))
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

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = if(Shell.params.opt_Dommodel2) List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global), (loc_ins, prop_ins)

  ) else List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)  ) 

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
  override def getInstance(cfg: CFG): Option[Loc] = Some(newRecentLoc())
  /* list of properties in the instance object */
  override def getInsList(node: Node): List[(String, PropValue)] = node match {
    case e: Element => 
      // This object has all properties of the HTMLElement object 
      HTMLElement.getInsList(node) ++ List(
      ("@class",    PropValue(AbsString.alpha("Object"))),
      ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
      ("@extensible", PropValue(BoolTrue)),
      // DOM Level 1
      ("frameBorder", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("frameBorder")), T, T, T))),
      ("longDesc", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("longDesc")), T, T, T))),
      ("marginHeight", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("marginHeight")), T, T, T))),
      ("marginWidth", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("marginWidth")), T, T, T))),
      ("name", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("name")), T, T, T))),
      ("noResize",   PropValue(ObjectValue((if(e.getAttribute("noResize")=="true") T else F), T, T, T))),
      ("scrolling", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("scrolling")), T, T, T))),
      ("src", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("src")), T, T, T))))
      // TODO: 'contentDocument' in DOM Level 2
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }
 
  def getInsList(frameBorder: PropValue, longDesc: PropValue, marginHeight: PropValue, marginWidth: PropValue, name: PropValue, 
                 noResize: PropValue, scrolling: PropValue, src: PropValue, xpath: PropValue): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    // DOM Level 1
    ("frameBorder", frameBorder),
    ("longDesc", longDesc),
    ("marginHeight", marginHeight),
    ("marginWidth", marginWidth),
    ("name", name),
    ("noResize", noResize),
    ("scrolling", scrolling),
    ("src", src),
    ("xpath", xpath)
  )
  
  override def default_getInsList(): List[(String, PropValue)] = {    
    val frameBorder = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val longDesc = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val marginHeight = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val marginWidth = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val name = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val noResize = PropValue(ObjectValue(BoolFalse, T, T, T))
    val scrolling = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val src = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val xpath = PropValue(ObjectValue(AbsString.alpha(""), F, F, F))
    // This object has all properties of the HTMLElement object 
    HTMLElement.default_getInsList ::: 
      getInsList(frameBorder, longDesc, marginHeight, marginWidth, name, noResize, scrolling, src, xpath)
  }
}

