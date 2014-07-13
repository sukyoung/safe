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
import kr.ac.kaist.jsaf.analysis.typing.{Semantics, ControlPoint, Helper, PreHelper}
import org.w3c.dom.Node
import org.w3c.dom.Element
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object HTMLInputElement extends DOM {
  private val name = "HTMLInputElement"

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
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("blur",   AbsBuiltinFunc("HTMLInputElement.blur", 0)),
    ("focus",  AbsBuiltinFunc("HTMLInputElement.focus", 0)),
    ("select", AbsBuiltinFunc("HTMLInputElement.select", 0)),
    ("click",  AbsBuiltinFunc("HTMLInputElement.click", 0))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("HTMLInputElement.blur" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("HTMLInputElement.focus" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("HTMLInputElement.select" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("HTMLInputElement.click" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("HTMLInputElement.blur" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("HTMLInputElement.focus" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("HTMLInputElement.select" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("HTMLInputElement.click" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
        }))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      ("HTMLInputElement.blur" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("HTMLInputElement.focus" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("HTMLInputElement.select" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("HTMLInputElement.click" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      ("HTMLInputElement.blur" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("HTMLInputElement.focus" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("HTMLInputElement.select" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("HTMLInputElement.click" -> (
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
        ("defaultValue", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("defaultValue")), T, T, T))),
        ("defaultChecked",   PropValue(ObjectValue((if(e.getAttribute("defaultChecked")=="true") T else F), T, T, T))),
        ("accept", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("accept")), T, T, T))),
        ("accessKey", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("accessKey")), T, T, T))),
        ("align", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("align")), T, T, T))),
        ("alt", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("alt")), T, T, T))),
        ("checked",   PropValue(ObjectValue((if(e.getAttribute("checked")=="true") T else F), T, T, T))),
        ("disabled",   PropValue(ObjectValue((if(e.getAttribute("disabled")=="true") T else F), T, T, T))),
        ("maxLength",  PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("maxLength")))), T, T, T))),
        ("name", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("name")), T, T, T))),
        ("readOnly",   PropValue(ObjectValue((if(e.getAttribute("readOnly")=="true") T else F), T, T, T))),
        ("src", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("src")), T, T, T))),
        ("tabIndex",  PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("tabIndex")))), T, T, T))),
        ("useMap", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("useMap")), T, T, T))),
        ("value", PropValue(ObjectValue(StrTop, T, T, T))),
        ("form", PropValue(ObjectValue(NullTop, F, T, T))),
        // Modified in DOM Level 2
        ("size",  PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("size")))), T, T, T))),
        ("type", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("type")), T, T, T))),
        // HTML5
        ("autofocus",   PropValue(ObjectValue((if(e.getAttribute("autofocus")!="false") T else F), T, T, T))),
        ("placeholder",   PropValue(ObjectValue((if(e.getAttribute("placeholder")!=null) Value(AbsString.alpha(e.getAttribute("placeholder"))) else Value(AbsString.alpha(""))), T, T, T)))
      )
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }

  def getInsList(defaultValue: PropValue, defaultChecked: PropValue, accept: PropValue, accessKey: PropValue, align: PropValue,
                 alt: PropValue, checked: PropValue, disabled: PropValue, maxLength: PropValue, name: PropValue, readOnly: PropValue,
                 src: PropValue, tabIndex: PropValue, useMap: PropValue, value: PropValue, 
                 form: PropValue, size: PropValue, typee: PropValue, placeholder: PropValue ): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    ("defaultValue", defaultValue),
    ("defaultChecked", defaultChecked),
    ("accept", accept),
    ("accessKey", accessKey),
    ("align", align),
    ("alt", alt),
    ("checked", checked),
    ("disabled", disabled),
    ("maxLength", maxLength),
    ("name", name),
    ("readOnly", readOnly),
    ("src", src),
    ("tabIndex", tabIndex),
    ("useMap", useMap),
    ("value", value),
    ("form", form),
    ("size", size),
    ("type", typee),
    ("placeholder", placeholder)
  )

  override def default_getInsList(): List[(String, PropValue)] =
  // This object has all properties of the HTMLElement object
    HTMLElement.default_getInsList ::: getInsList(
      PropValue(ObjectValue(AbsString.alpha(""), T, T, T)),
      PropValue(ObjectValue(BoolFalse, T, T, T)),
      PropValue(ObjectValue(AbsString.alpha(""), T, T, T)),
      PropValue(ObjectValue(AbsString.alpha(""), T, T, T)),
      PropValue(ObjectValue(AbsString.alpha(""), T, T, T)),
      PropValue(ObjectValue(AbsString.alpha(""), T, T, T)),
      PropValue(ObjectValue(BoolFalse, T, T, T)),
      PropValue(ObjectValue(BoolFalse, T, T, T)),
      PropValue(ObjectValue(UInt, T, T, T)),
      PropValue(ObjectValue(AbsString.alpha(""), T, T, T)),
      PropValue(ObjectValue(BoolFalse, T, T, T)),
      PropValue(ObjectValue(AbsString.alpha(""), T, T, T)),
      PropValue(ObjectValue(AbsNumber.alpha(0), T, T, T)),
      PropValue(ObjectValue(AbsString.alpha(""), T, T, T)),
      PropValue(ObjectValue(StrTop, T, T, T)),
      PropValue(ObjectValue(NullTop, F, T, T)),
      PropValue(ObjectValue(UInt, T, T, T)),
      PropValue(ObjectValue(AbsString.alpha("text"), T, T, T)),
      PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
      )
}
