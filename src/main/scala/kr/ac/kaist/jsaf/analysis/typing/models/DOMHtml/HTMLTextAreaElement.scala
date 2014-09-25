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
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing.{Semantics, ControlPoint, Helper, PreHelper}
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.Shell

object HTMLTextAreaElement extends DOM {
  private val name = "HTMLTextAreaElement"

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
private val prop_ins: List[(String, AbsProperty)] = 
       HTMLElement.getInsList2() ++ List(
      ("@class",    AbsConstValue(PropValue(AbsString.alpha("Object")))),
      ("@proto",    AbsConstValue(PropValue(ObjectValue(loc_proto, F, F, F)))),
      ("@extensible", AbsConstValue(PropValue(BoolTrue))),
      // DOM Level 1
      ("defaultValue", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("defaultChecked", AbsConstValue(PropValue(ObjectValue(BoolTop, T, T, T)))),
      ("form", AbsConstValue(PropValue(ObjectValue(Value(HTMLFormElement.loc_ins), F, T, T)))),
      ("accessKey", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("cols", AbsConstValue(PropValue(ObjectValue(NumTop, T, T, T)))),
      ("disabled", AbsConstValue(PropValue(ObjectValue(BoolTop, T, T, T)))),
      ("name", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("readOnly", AbsConstValue(PropValue(ObjectValue(BoolTop, T, T, T)))),
      ("rows", AbsConstValue(PropValue(ObjectValue(NumTop, T, T, T)))),
      ("tabIndex", AbsConstValue(PropValue(ObjectValue(NumTop, T, T, T)))),
      ("type", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
      ("value", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("defaultValue", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T))))
    )

  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(HTMLElement.loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("blur",   AbsBuiltinFunc("HTMLTextAreaElement.blur", 0)),
    ("focus",  AbsBuiltinFunc("HTMLTextAreaElement.focus", 0)),
    ("select", AbsBuiltinFunc("HTMLTextAreaElement.select", 0))
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
      ("HTMLTextAreaElement.blur" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("HTMLTextAreaElement.focus" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("HTMLTextAreaElement.select" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("HTMLTextAreaElement.blur" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("HTMLTextAreaElement.focus" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("HTMLTextAreaElement.select" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
        }))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      ("HTMLTextAreaElement.blur" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("HTMLTextAreaElement.focus" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("HTMLTextAreaElement.select" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      ("HTMLTextAreaElement.blur" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("HTMLTextAreaElement.focus" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("HTMLTextAreaElement.select" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
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
        ("accessKey", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("accessKey")), T, T, T))),
        ("cols",  PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("cols")))), T, T, T))),
        ("disabled",   PropValue(ObjectValue((if(e.getAttribute("disabled")=="true") T else F), T, T, T))),
        ("name", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("name")), T, T, T))),
        ("readOnly",   PropValue(ObjectValue((if(e.getAttribute("readOnly")=="true") T else F), T, T, T))),
        ("rows",  PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("rows")))), T, T, T))),
        ("tabIndex",  PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("tabIndex")))), T, T, T))),
        ("type", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("type")), F, T, T))),
        ("value", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("value")), F, T, T))),
        ("form", PropValue(ObjectValue(NullTop, F, T, T))),
        // Modified in DOM Level 2
        ("defaultValue", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("defaultValue")), T, T, T))),
        // HTML5
        ("placeholder",   PropValue(ObjectValue((if(e.getAttribute("placeholder")!=null) AbsString.alpha(e.getAttribute("placeholder")) else AbsString.alpha("")), T, T, T)))
       )
    // TODO: 'form' in DOM Level 1
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }

  def getInsList(accessKey: PropValue, cols: PropValue, disabled: PropValue, name: PropValue, readOnly: PropValue,
                 rows: PropValue, tabIndex: PropValue, ttype: PropValue, value: PropValue, 
                 form: PropValue, defaultValue: PropValue, placeholder: PropValue, xpath: PropValue): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    // DOM Level 1
    ("accessKey", accessKey),
    ("cols", cols),
    ("disabled", disabled),
    ("name", name),
    ("readOnly", readOnly),
    ("rows", rows),
    ("tabIndex", tabIndex),
    ("type", ttype),
    ("value", value),
    ("form", form),
    // Modified in DOM Level 2
    ("defaultValue", defaultValue),
    // HTML5
    ("placeholder", placeholder),
    ("xpath", xpath)
  )

  override def default_getInsList(): List[(String, PropValue)] = {
    val accessKey = PropValue(ObjectValue(AbsString.alpha(""), F, T, T))
    val cols= PropValue(ObjectValue(NumTop, T, T, T))
    val disabled = PropValue(ObjectValue(BoolFalse, T, T, T))
    val name = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val readOnly = PropValue(ObjectValue(BoolFalse, T, T, T))
    val rows = PropValue(ObjectValue(NumTop, T, T, T))
    val tabIndex = PropValue(ObjectValue(NumTop, T, T, T))
    val ttype = PropValue(ObjectValue(AbsString.alpha(""), F, T, T))
    val value = PropValue(ObjectValue(AbsString.alpha(""), F, T, T))
    val form = PropValue(ObjectValue(NullTop, F, T, T))
    val defaultValue = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val placeholder = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val xpath = PropValue(ObjectValue(AbsString.alpha(""), F, F, F))
    // This object has all properties of the HTMLElement object 
    HTMLElement.default_getInsList :::
      getInsList(accessKey, cols, disabled, name, readOnly, rows, tabIndex, ttype, value, form, defaultValue, placeholder, xpath)
  }

}
