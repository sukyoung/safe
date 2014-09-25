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

object HTMLSelectElement extends DOM {
  private val name = "HTMLSelectElement"

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
      ("type", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
      ("selectedIndex", AbsConstValue(PropValue(ObjectValue(NumTop, T, T, T)))),
      ("value", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("form", AbsConstValue(PropValue(ObjectValue(Value(HTMLFormElement.loc_ins), F, T, T)))),
      ("disabled", AbsConstValue(PropValue(ObjectValue(BoolTop, T, T, T)))),
      ("multiple", AbsConstValue(PropValue(ObjectValue(BoolTop, T, T, T)))),
      ("name", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
      ("size", AbsConstValue(PropValue(ObjectValue(NumTop, T, T, T)))),
      ("tabIndex", AbsConstValue(PropValue(ObjectValue(NumTop, T, T, T)))),
      ("length", AbsConstValue(PropValue(ObjectValue(NumTop, T, T, T)))),
      ("options", AbsConstValue(PropValue(ObjectValue(Value(HTMLOptionsCollection.loc_ins), F, T, T))))
    )
  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(HTMLElement.loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("add",    AbsBuiltinFunc("HTMLSelectElement.add", 2)),
    ("remove", AbsBuiltinFunc("HTMLSelectElement.remove", 1)),
    ("blur",   AbsBuiltinFunc("HTMLSelectElement.blur", 0)),
    ("focus",  AbsBuiltinFunc("HTMLSelectElement.focus", 0))
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
      //case "HTMLSelectElement.add"    => ((h,ctx),(he,ctxe))
      //case "HTMLSelectElement.remove" => ((h,ctx),(he,ctxe))
      ("HTMLSelectElement.blur" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("HTMLSelectElement.focus" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //TODO: not yet implemented
      //case "HTMLSelectElement.add"    => ((h,ctx),(he,ctxe))
      //case "HTMLSelectElement.remove" => ((h,ctx),(he,ctxe))
      ("HTMLSelectElement.blur" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("HTMLSelectElement.focus" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
        }))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      //case "HTMLSelectElement.add"    => ((h,ctx),(he,ctxe))
      //case "HTMLSelectElement.remove" => ((h,ctx),(he,ctxe))
      ("HTMLSelectElement.blur" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("HTMLSelectElement.focus" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      //case "HTMLSelectElement.add"    => ((h,ctx),(he,ctxe))
      //case "HTMLSelectElement.remove" => ((h,ctx),(he,ctxe))
      ("HTMLSelectElement.blur" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("HTMLSelectElement.focus" -> (
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
        ("type", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("type")), F, T, T))),
        ("selectedIndex",  PropValue(ObjectValue(Value(NumTop), T, T, T))),
        ("value", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("value")), T, T, T))),
        ("disabled",   PropValue(ObjectValue((if(e.getAttribute("disabled")=="true") T else F), T, T, T))),
        ("multiple",   PropValue(ObjectValue((if(e.getAttribute("multiple")=="true") T else F), T, T, T))),
        ("name", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("name")), T, T, T))),
        ("size",  PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("size")))), T, T, T))),
        ("tabIndex",  PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("tabIndex")))), T, T, T))),
        ("form", PropValue(ObjectValue(NullTop, F, T, T))),
        // Modified in DOM Level 2
        ("length",     PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("length")))), T, T, T))))
        // 'options' in DOM Level 2 is updated in DOMHelper.modelNode
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }

  def getInsList(ttype: PropValue, selectedIndex: PropValue, value: PropValue, disabled: PropValue,
                 multiple: PropValue, name: PropValue, size: PropValue, tabIndex: PropValue, 
                 form: PropValue, length: PropValue, xpath: PropValue): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    // DOM Level 1
    ("type",   ttype),
    ("selectedIndex",  selectedIndex),
    ("value",   value),
    ("disabled", disabled),
    ("multiple",   multiple),
    ("name", name),
    ("size",   size),
    ("tabIndex",   tabIndex),
    ("form",   form),
    // DOM Level 2
    ("length",  length),
    ("xpath", xpath)
  )

  override def default_getInsList(): List[(String, PropValue)] = {
    val ttype = PropValue(ObjectValue(AbsString.alpha(""), F, T, T))
    val selectedIndex= PropValue(ObjectValue(NumTop, T, T, T))
    val value = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val disabled = PropValue(ObjectValue(BoolFalse, T, T, T))
    val multiple = PropValue(ObjectValue(BoolFalse, T, T, T))
    val name = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val size = PropValue(ObjectValue(NumTop, T, T, T))
    val tabIndex = PropValue(ObjectValue(NumTop, T, T, T))
    val form = PropValue(ObjectValue(NullTop, F, T, T))
    val length = PropValue(ObjectValue(NumTop, T, T, T))
    val xpath = PropValue(ObjectValue(AbsString.alpha(""), F, F, F))
    // This object has all properties of the HTMLElement object 
    HTMLElement.default_getInsList :::
      getInsList(ttype, selectedIndex, value, disabled, multiple, name, size, tabIndex, form, length, xpath)
  }

}
