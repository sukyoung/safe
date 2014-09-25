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
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.Shell

object HTMLTableElement extends DOM {
  private val name = "HTMLTableElement"

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
      ("rows", AbsConstValue(PropValue(ObjectValue(Value(HTMLCollection.loc_ins), F, T, T)))),
      ("tBodies", AbsConstValue(PropValue(ObjectValue(Value(HTMLCollection.loc_ins), F, T, T)))),
      ("align", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
      ("bgColor", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
      ("border", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
      ("cellPadding", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
      ("cellSpacing", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
      ("frame", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("rules", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("summary", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("width", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("caption", AbsConstValue(PropValue(ObjectValue(Value(HTMLTableCaptionElement.loc_ins), T, T, T)))),
      ("tHead", AbsConstValue(PropValue(ObjectValue(Value(HTMLTableSectionElement.loc_ins), T, T, T)))),
      ("tFoot", AbsConstValue(PropValue(ObjectValue(Value(HTMLTableSectionElement.loc_ins), T, T, T))))
    )


  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(HTMLElement.loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("createTHead",   AbsBuiltinFunc("HTMLTableElement.createTHead", 0)),
    ("deleteTHead",   AbsBuiltinFunc("HTMLTableElement.deleteTHead", 0)),
    ("createTFoot",   AbsBuiltinFunc("HTMLTableElement.createTFoot", 0)),
    ("deleteTFoot",   AbsBuiltinFunc("HTMLTableElement.deleteTFoot", 0)),
    ("createCaption", AbsBuiltinFunc("HTMLTableElement.createCaption", 0)),
    ("deleteCaption", AbsBuiltinFunc("HTMLTableElement.deleteCaption", 0)),
    ("insertRow",     AbsBuiltinFunc("HTMLTableElement.insertRow", 1)),
    ("deleteRow",     AbsBuiltinFunc("HTMLTableElement.deleteRow", 1))
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
    Map(
      //TODO: not yet implemented
      //case "HTMLTableElement.createTHead" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.deleteTHead" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.createTFoot" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.deleteTFoot" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.createCaption" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.deleteCaption" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.insertRow" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.deleteRow" => ((h,ctx),(he,ctxe))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //TODO: not yet implemented
      //case "HTMLTableElement.createTHead" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.deleteTHead" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.createTFoot" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.deleteTFoot" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.createCaption" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.deleteCaption" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.insertRow" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.deleteRow" => ((h,ctx),(he,ctxe))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      //case "HTMLTableElement.createTHead" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.deleteTHead" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.createTFoot" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.deleteTFoot" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.createCaption" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.deleteCaption" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.insertRow" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.deleteRow" => ((h,ctx),(he,ctxe))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      //case "HTMLTableElement.createTHead" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.deleteTHead" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.createTFoot" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.deleteTFoot" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.createCaption" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.deleteCaption" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.insertRow" => ((h,ctx),(he,ctxe))
      //case "HTMLTableElement.deleteRow" => ((h,ctx),(he,ctxe))
    )
  }

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
      ("align", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("align")), T, T, T))),
      ("bgColor", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("bgColor")), T, T, T))),
      ("border", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("border")), T, T, T))),
      ("cellPadding", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("cellPadding")), T, T, T))),
      ("cellSpacing", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("cellSpacing")), T, T, T))),
      ("frame", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("frame")), T, T, T))),
      ("rules", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("rules")), T, T, T))),
      ("summary", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("summary")), T, T, T))),
      ("width", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("width")), T, T, T))))
      // TODO: 'caption', 'tHead', 'tFont', in DOM Level 2, 'rows', 'tBodies' in DOM Level 1
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }
   
  def getInsList(align: PropValue, bgColor: PropValue, border: PropValue, cellPadding: PropValue, cellSpacing: PropValue,
                 frame: PropValue, rules: PropValue, summary: PropValue, width: PropValue, xpath: PropValue): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    // DOM Level 1
    ("align", align),
    ("bgColor", bgColor),
    ("border", border),
    ("cellPadding", cellPadding),
    ("cellSpacing", cellSpacing),
    ("frame", frame),
    ("rules", rules),
    ("summary", summary),
    ("width", width),
    ("xpath", xpath)
  )
  
  override def default_getInsList(): List[(String, PropValue)] = {    
    val align = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val bgColor = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val border = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val cellPadding = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val cellSpacing = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val frame = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val rules = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val summary = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val width = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val xpath = PropValue(ObjectValue(AbsString.alpha(""), F, F, F))
    // This object has all properties of the HTMLElement object 
    HTMLElement.default_getInsList ::: 
      getInsList(align, bgColor, border, cellPadding, cellSpacing, frame, rules, summary, width, xpath)
  }

}
