/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
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

object HTMLLinkElement extends DOM {
  private val name = "HTMLLinkElement"

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
      ("disabled", AbsConstValue(PropValue(ObjectValue(BoolTop, T, T, T)))),
      ("charset", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("href", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("hreflang", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("media", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("rel", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("rev", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("target", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("type", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T))))
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
      ("disabled",   PropValue(ObjectValue((if(e.getAttribute("disabled")=="true") T else F), T, T, T))),
      ("charset",   PropValue(ObjectValue(AbsString.alpha(e.getAttribute("charset")), T, T, T))),
      ("href",   PropValue(ObjectValue(AbsString.alpha(e.getAttribute("href")), T, T, T))),
      ("hreflang",   PropValue(ObjectValue(AbsString.alpha(e.getAttribute("hreflang")), T, T, T))),
      ("media",   PropValue(ObjectValue(AbsString.alpha(e.getAttribute("media")), T, T, T))),
      ("rel",   PropValue(ObjectValue(AbsString.alpha(e.getAttribute("rel")), T, T, T))),
      ("rev",   PropValue(ObjectValue(AbsString.alpha(e.getAttribute("rev")), T, T, T))),
      ("target",   PropValue(ObjectValue(AbsString.alpha(e.getAttribute("target")), T, T, T))),
      ("type",   PropValue(ObjectValue(AbsString.alpha(e.getAttribute("type")), T, T, T))))
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }
   
  def getInsList(disabled: PropValue, charset: PropValue, href: PropValue, hreflang: PropValue,
                 media: PropValue, rel: PropValue, rev: PropValue, target: PropValue, ttype: PropValue, xpath: PropValue): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    // DOM Level 1
    ("disabled", disabled),
    ("charset",  charset), 
    ("href",   href),
    ("hreflang", hreflang), 
    ("media",   media), 
    ("rel",   rel),
    ("rev",   rev),
    ("target",  target),
    ("type",   ttype),
    ("xpath", xpath)
   )
  
  override def default_getInsList(): List[(String, PropValue)] = { 
    val disabled = PropValue(ObjectValue(BoolFalse, T, T, T))
    val charset = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val href = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val hreflang = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val media = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val rel = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val rev = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val target = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val ttype = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val xpath = PropValue(ObjectValue(AbsString.alpha(""), F, F, F))
    // This object has all properties of the HTMLElement object 
    HTMLElement.default_getInsList ::: 
      getInsList(disabled, charset, href, hreflang, media, rel, rev, target, ttype, xpath)
  }
}
