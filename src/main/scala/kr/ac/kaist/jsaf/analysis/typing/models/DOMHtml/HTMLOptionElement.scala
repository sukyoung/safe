/*******************************************************************************
    Copyright (c) 2013-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import org.w3c.dom.Node
import org.w3c.dom.Element
import kr.ac.kaist.jsaf.analysis.typing.Helper
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.Shell

object HTMLOptionElement extends DOM {
  private val name = "HTMLOptionElement"

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
      ("form", AbsConstValue(PropValue(ObjectValue(Value(HTMLFormElement.loc_ins), F, T, T)))),
      ("text", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
      ("disabled", AbsConstValue(PropValue(ObjectValue(BoolTop, T, T, T)))),
      ("label", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("selected", AbsConstValue(PropValue(ObjectValue(BoolTop, T, T, T)))),
      ("value", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("defaultSelected", AbsConstValue(PropValue(ObjectValue(BoolTop, T, T, T)))),
      ("index", AbsConstValue(PropValue(ObjectValue(NumTop, F, T, T))))
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
      ("text", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("text")), F, T, T))),
      ("disabled",   PropValue(ObjectValue((if(e.getAttribute("disabled")=="true") T else F), T, T, T))),
      ("label", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("label")), T, T, T))),
      ("selected",   PropValue(ObjectValue((if(e.getAttribute("selected")=="true") T else F), T, T, T))),
      ("value", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("value")), T, T, T))),
      // Modified in DOM Level 2
      ("defaultSelected",   PropValue(ObjectValue((if(e.getAttribute("defaultSelected")=="true") T else F), T, T, T))),
      ("index",  PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("index")))), F, T, T))))
      // TODO: 'form' in DOM Level 1
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }
   
  def getInsList(text: PropValue, disabled: PropValue, label: PropValue, selected: PropValue,
                 value: PropValue, defaultSelected: PropValue, index: PropValue, xpath: PropValue): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    // DOM Level 1
    ("text",   text),
    ("disabled",  disabled), 
    ("label",   label),
    ("selected", selected),
    ("value",   value),
    // DOM Level 2
    ("defaultSelected", defaultSelected), 
    ("index",   index),
    ("xpath", xpath)
   )
  
  override def default_getInsList(): List[(String, PropValue)] = { 
    val text = PropValue(ObjectValue(AbsString.alpha(""), F, T, T))
    val disabled = PropValue(ObjectValue(BoolFalse, T, T, T))
    val label = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val selected = PropValue(ObjectValue(BoolFalse, T, T, T))
    val value = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val defaultSelected = PropValue(ObjectValue(BoolFalse, T, T, T))
    val index = PropValue(ObjectValue(NumTop, T, T, T))
    val xpath = PropValue(ObjectValue(AbsString.alpha(""), F, F, F))
    // This object has all properties of the HTMLElement object 
    HTMLElement.default_getInsList ::: 
      getInsList(text, disabled, label, selected, value, defaultSelected, index, xpath)
  }

}
