/*******************************************************************************
    Copyright (c) 2013, KAIST, S-Core.
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

object HTMLAreaElement extends DOM {
  private val name = "HTMLAreaElement"

  /* predefined locatoins */
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
      ("accessKey", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("accessKey")), T, T, T))),
      ("alt", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("alt")), T, T, T))),
      ("coords", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("coords")), T, T, T))),
      ("href", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("href")), T, T, T))),
      ("noHref",   PropValue(ObjectValue((if(e.getAttribute("noHref")=="true") T else F), T, T, T))),
      ("shape", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("shape")), T, T, T))),
      ("tabIndex",  PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("tabIndex")))), T, T, T))),
      ("target", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("target")), T, T, T))))
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }
 
  def getInsList(accessKey: PropValue, alt: PropValue, coords: PropValue, href: PropValue, noHref: PropValue, 
                 shape: PropValue, tabIndex: PropValue, target: PropValue): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    // DOM Level 1
    ("accessKey", accessKey),
    ("alt", alt),
    ("coords", coords),
    ("href", href),
    ("noHref", noHref),
    ("shape", shape),
    ("tabIndex", tabIndex),
    ("target", target)
  )
  
  override def default_getInsList(): List[(String, PropValue)] = {   
    val accessKey = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val alt = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val coords = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val href = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val noHref = PropValue(ObjectValue(BoolFalse, T, T, T))
    val shape = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val tabIndex = PropValue(ObjectValue(NumTop, T, T, T))
    val target = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    // This object has all properties of the HTMLElement object 
    HTMLElement.default_getInsList ::: 
      getInsList(accessKey, alt, coords, href, noHref, shape, tabIndex, target)
  }

}
