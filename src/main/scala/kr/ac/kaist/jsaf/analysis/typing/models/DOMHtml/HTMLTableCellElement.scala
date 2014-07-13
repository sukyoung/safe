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

object HTMLTableCellElement extends DOM {
  private val name = "HTMLTableCellElement"

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
      ("cellIndex",  PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("cellIndex")))), F, T, T))),
      ("abbr", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("abbr")), T, T, T))),
      ("align", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("align")), T, T, T))),
      ("axis", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("axis")), T, T, T))),
      ("bgColor", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("bgColor")), T, T, T))),
      ("ch", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("ch")), T, T, T))),
      ("chOff", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("chOff")), T, T, T))),
      ("colSpan",  PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("colSpan")))), T, T, T))),
      ("headers", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("headers")), T, T, T))),
      ("height", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("height")), T, T, T))),
      ("noWrap",   PropValue(ObjectValue((if(e.getAttribute("noWrap")=="true") T else F), T, T, T))),
      ("rowSpan",  PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("rowSpan")))), T, T, T))),
      ("scope", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("scope")), T, T, T))),
      ("vAlign", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("vAlign")), T, T, T))),
      ("width", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("width")), T, T, T))))
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }
   
  def getInsList(cellIndex: PropValue, abbr: PropValue, align: PropValue, axis: PropValue, bgColor: PropValue,
                 ch: PropValue, chOff: PropValue, colSpan: PropValue, headers: PropValue, height: PropValue, 
                 noWrap: PropValue, rowSpan: PropValue, scope: PropValue, vAlign: PropValue, width: PropValue): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    // DOM Level 1
    ("cellIndex", cellIndex ),
    ("abbr", abbr),
    ("align", align),
    ("axis", axis),
    ("bgColor", bgColor),
    ("ch", ch),
    ("chOff", chOff),
    ("colSpan", colSpan),
    ("headers", headers),
    ("height", height),
    ("noWrap",  noWrap),
    ("rowSpan", rowSpan),
    ("scope", scope),
    ("vAlign", vAlign),
    ("width", width)
  )
  
  override def default_getInsList(): List[(String, PropValue)] = {    
    val cellIndex = PropValue(ObjectValue(NumTop, F, T, T))
    val abbr = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val align = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val axis = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val bgColor = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val ch = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val chOff = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val colSpan = PropValue(ObjectValue(NumTop, T, T, T))
    val headers = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val height = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val noWrap = PropValue(ObjectValue(BoolFalse, T, T, T))
    val rowSpan = PropValue(ObjectValue(NumTop, T, T, T))
    val scope = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val vAlign = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val width = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    // This object has all properties of the HTMLElement object 
    HTMLElement.default_getInsList ::: 
      getInsList(cellIndex, abbr, align, axis, bgColor, ch, chOff, colSpan, headers, height, noWrap,rowSpan, scope, vAlign, width)
  }

}
