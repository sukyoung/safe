/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
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
import kr.ac.kaist.jsaf.analysis.typing.models.DOMCore.DOMElement
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.analysis.typing.models.DOMObject.CSSStyleDeclaration

object HTMLElement extends DOM {
  private val name = "HTMLElement"

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
       DOMElement.getInsList2() ++ List(
      ("@class",    AbsConstValue(PropValue(AbsString.alpha("Object")))),
      ("@proto",    AbsConstValue(PropValue(ObjectValue(loc_proto, F, F, F)))),
      ("@extensible", AbsConstValue(PropValue(BoolTrue))),
      // DOM Level 1
      ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
      ("title", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
      ("lang", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
      ("dir", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
      ("className", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
      ("innerHTML", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
      ("outerHTML", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
      ("style", AbsConstValue(PropValue(ObjectValue(Value(CSSStyleDeclaration.loc_ins), T, T, T))))
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

  override def getProto(): Option[Loc] = Some(loc_proto) 

  /* semantics */  
  // no function

  /* instance */
  override def getInstance(cfg: CFG): Option[Loc] = Some(newRecentLoc())
  /* list of properties in the instance object */
  override def getInsList(node: Node): List[(String, PropValue)] = node match {
    case e: Element => 
      // This instance object has all properties of the Element object
      DOMElement.getInsList(node) ++ List(
      // DOM Level 1
      ("id",   PropValue(ObjectValue(AbsString.alpha(e.getAttribute("id")), T, T, T))),
      ("title",   PropValue(ObjectValue(AbsString.alpha(e.getAttribute("title")), T, T, T))),
      ("lang",   PropValue(ObjectValue(AbsString.alpha(e.getAttribute("lang'")), T, T, T))),
      ("dir",   PropValue(ObjectValue(AbsString.alpha(e.getAttribute("dir")), T, T, T))),
      ("className",   PropValue(ObjectValue(AbsString.alpha(e.getAttribute("class")), T, T, T))),
      // TODO : setting 'innerHTML' and 'outerHTML' should affect the DOM tree 
      ("innerHTML",   PropValue(ObjectValue(StrTop, T, T, T))),
      ("outerHTML",   PropValue(ObjectValue(StrTop, T, T, T)))
     )
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }

  def getInsList2(): List[(String, AbsProperty)] =
      DOMElement.getInsList2() ++ List(
      // DOM Level 1
      ("id", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
      ("title", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
      ("lang", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
      ("dir", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
      ("className", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
      ("innerHTML", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
      ("outerHTML", AbsConstValue(PropValue(ObjectValue(Value(StrTop), T, T, T)))),
      ("style", AbsConstValue(PropValue(ObjectValue(Value(CSSStyleDeclaration.loc_ins), T, T, T)))),
      ("xpath", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, F, F))))
    )

  
  def getInsList(id: PropValue, title: PropValue, lang: PropValue, dir: PropValue, className: PropValue,
                 innerHTML: PropValue, outerHTML: PropValue, xpath: PropValue): List[(String, PropValue)] = {
    // DOM Level 1
    List(("id", id), 
    ("title", title),
    ("lang", lang), 
    ("dir", dir),
    ("className", className),
    ("innerHTML", innerHTML),
    ("outerHTML", outerHTML),
    ("xpath", xpath)
    )
  }

  override def default_getInsList(): List[(String, PropValue)] =
    getInsList(PropValue(ObjectValue(AbsString.alpha(""), T, T, T)),
               PropValue(ObjectValue(AbsString.alpha(""), T, T, T)),
               PropValue(ObjectValue(AbsString.alpha(""), T, T, T)),
               PropValue(ObjectValue(AbsString.alpha(""), T, T, T)),
               PropValue(ObjectValue(AbsString.alpha(""), T, T, T)),
               PropValue(ObjectValue(AbsString.alpha(""), T, T, T)),
               PropValue(ObjectValue(AbsString.alpha(""), T, T, T)),
               PropValue(ObjectValue(AbsString.alpha(""), F, F, F)))

}
