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
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object HTMLAnchorElement extends DOM {
  private val name = "HTMLAnchorElement"

  /* predefined locatoins */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")


  /* constructor or object*/
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
    ("blur",  AbsBuiltinFunc("HTMLAnchorElement.submit", 0)),
    ("focus", AbsBuiltinFunc("HTMLAnchorElement.reset", 0))
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
      ("HTMLAnchorElement.blur" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("HTMLAnchorElement.focus" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("HTMLAnchorElement.blur" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("HTMLAnchorElement.focus" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
        }))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      ("HTMLAnchorElement.blur" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("HTMLAnchorElement.focus" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      ("HTMLAnchorElement.blur" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("HTMLAnchorElement.focus" -> (
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
        ("charset", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("charset")), T, T, T))),
        ("coords", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("coords")), T, T, T))),
        ("href", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("href")), T, T, T))),
        ("hreflang", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("hreflang")), T, T, T))),
        ("name", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("name")), T, T, T))),
        ("rel", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("rel")), T, T, T))),
        ("rev", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("rev")), T, T, T))),
        ("shape", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("shape")), T, T, T))),
        ("tabIndex",  PropValue(ObjectValue(Helper.toNumber(PValue(AbsString.alpha(e.getAttribute("tabIndex")))), T, T, T))),
        ("target", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("target")), T, T, T))),
        ("type", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("type")), T, T, T))),
        // HTML5: WHATWG Livng Standard - Section 4.6.1 The a element
        ("ping", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("ping")), T, T, T))),
        ("text", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("text")), T, T, T))),
        ("protocol", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("protocol")), T, T, T))),
          // origin: readonly
        ("origin", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("origin")), F, T, T))),
        ("host", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("host")), F, T, T))),
        ("hostname", PropValue(ObjectValue(OtherStr, T, T, T))),
        ("port", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("port")), T, T, T))),
        ("pathname", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("pathname")), T, T, T))),
        ("search", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("search")), T, T, T))),
        ("hash", PropValue(ObjectValue(AbsString.alpha(e.getAttribute("hash")), T, T, T))))
        // Non-modeled properties : 'download', 'password', 'query' in HTML5 (not found in real-world code) 
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }

  def getInsList(accessKey: PropValue, charset: PropValue, coords: PropValue, href: PropValue, hreflang: PropValue,
                 name: PropValue, rel: PropValue, rev: PropValue, shape: PropValue, tabIndex: PropValue,
                 target: PropValue, ttype: PropValue): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    // DOM Level 1
    ("accessKey", accessKey),
    ("charset", charset),
    ("coords", coords),
    ("href", href),
    ("hreflang", hreflang),
    ("name", name),
    ("rel", rel),
    ("rev", rev),
    ("shape", shape),
    ("tabIndex", tabIndex),
    ("target", target),
    ("type", ttype)
  )

  override def default_getInsList(): List[(String, PropValue)] = {
    val accessKey = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val charset = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val coords = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val href = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val hreflang = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val name = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val rel = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val rev = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val shape = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val tabIndex = PropValue(ObjectValue(NumTop, T, T, T))
    val target = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    val ttype = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))
    // This object has all properties of the HTMLElement object
    HTMLElement.default_getInsList :::
      getInsList(accessKey, charset, coords, href, hreflang,  name, rel, rev, shape, tabIndex, target, ttype)
  }

}
