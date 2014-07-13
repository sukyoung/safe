/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMCore

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import org.w3c.dom.Node
import org.w3c.dom.Element
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, InternalError, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml.{HTMLDocument, HTMLCollection}
import kr.ac.kaist.jsaf.analysis.typing.models.DOMObject.{ClientRect, ClientRectList}
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object DOMElement extends DOM {
  private val name = "Element"

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
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(DOMNode.loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("getAttribute",           AbsBuiltinFunc("DOMElement.getAttribute", 1)),
    ("setAttribute",           AbsBuiltinFunc("DOMElement.setAttribute", 2)),
    ("removeAttribute",        AbsBuiltinFunc("DOMElement.removeAttribute", 1)),
    ("getAttributeNode",       AbsBuiltinFunc("DOMElement.getAttributeNode", 1)),
    ("setAttributeNode",       AbsBuiltinFunc("DOMElement.setAttributeNode", 1)),
    ("removeAttributeNode",    AbsBuiltinFunc("DOMElement.removeAttributeNode", 1)),
    ("getElementsByTagName",   AbsBuiltinFunc("DOMElement.getElementsByTagName", 1)),
    ("getAttributeNS",         AbsBuiltinFunc("DOMElement.getAttributeNS", 2)),
    ("setAttributeNS",         AbsBuiltinFunc("DOMElement.setAttributeNS", 3)),
    ("removeAttributeNS",      AbsBuiltinFunc("DOMElement.removeAttributeNS", 2)),
    ("getAttributeNodeNS",     AbsBuiltinFunc("DOMElement.getAttributeNodeNS", 2)),
    ("setAttributeNodeNS",     AbsBuiltinFunc("DOMElement.setAttributeNodeNS", 1)),
    ("getElementsByTagNameNS", AbsBuiltinFunc("DOMElement.getElementsByTagNameNS", 2)),
    ("hasAttribute",           AbsBuiltinFunc("DOMElement.hasAttribute", 1)),
    ("hasAttributeNS",         AbsBuiltinFunc("DOMElement.hasAttributeNS", 2)),
    ("setIdAttribute",         AbsBuiltinFunc("DOMElement.setIdAttribute", 2)),
    ("setIdAttributeNS",       AbsBuiltinFunc("DOMElement.setIdAttributeNS", 3)),
    ("setIdAttributeNode",     AbsBuiltinFunc("DOMElement.setIdAttributeNode", 2)),
    ("querySelector",           AbsBuiltinFunc("DOMElement.querySelector", 0)),
    ("querySelectorAll",        AbsBuiltinFunc("DOMElement.querySelectorAll", 0)),
    // WHATWG DOM
    ("getElementsByClassName",      AbsBuiltinFunc("DOMElement.getElementsByClassName", 2)),
    // W3C CSSOM View Module
    ("getClientRects",      AbsBuiltinFunc("DOMElement.getClientRects", 0)),
    ("getBoundingClientRect",      AbsBuiltinFunc("DOMElement.getBoundingClientRect", 0)),
    ("scrollInfoView",      AbsBuiltinFunc("DOMElement.scrollIntoView", 1)),
    // Non-standard
    ("webkitMatchesSelector",           AbsBuiltinFunc("DOMElement.webkitMatchesSelector", 1))
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
      //TODO: not yet implemented
      ("DOMElement.getAttribute" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val attr_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0"))).toLowerCase
          // get attribute
          val v_ret = DOMHelper.getAttribute(h, lset_this, attr_name)
          if(v_ret </ ValueBot)
            ((Helper.ReturnStore(h, v_ret), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMElement.setAttribute" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)
          val addr5 = cfg.getAPIAddress(addr_env, 4)
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          // For 'Attr' node
          val l_attr = addrToLoc(addr1, Recent)
          // For 'Text' node
          val l_text = addrToLoc(addr2, Recent)
          // For NamedNodeList for 'childNodes' of the Attr and text nodes
          val l_child1 = addrToLoc(addr3, Recent)
          val l_child2 = addrToLoc(addr4, Recent)
          // For classTable look-up entry
          val l_classentry = addrToLoc(addr5, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
          val (h_4, ctx_4) = Helper.Oldify(h_3, ctx_3, addr4)
          val (h_5, ctx_5) = Helper.Oldify(h_4, ctx_4, addr5)
          /* arguments */
          val attr_name = Helper.toString(Helper.toPrimitive_better(h_5, getArgValue(h_5, ctx_5, args, "0"))).toLowerCase
          val attr_val = Helper.toString(Helper.toPrimitive_better(h_5, getArgValue(h_5, ctx_5, args, "1")))

          /* imprecise semantics : no exception handling */
          if(attr_name </ StrBot || attr_val </StrBot) {
            val h_6 = DOMHelper.setAttribute(h_5, lset_this, l_attr, l_text, l_child1, l_child2, l_classentry, attr_name, attr_val)
            ((h_6, ctx_5), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMElement.removeAttribute" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val attr_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0"))).toLowerCase
          if(attr_name </ StrBot){
            // remove attribute
            val h1 = DOMHelper.removeAttribute(h, lset_this, attr_name)
            ((Helper.ReturnStore(h1, Value(UndefTop)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),

      //case "DOMElement.getAttributeNode" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setAttributeNode" => ((h, ctx), (he, ctxe))
      //case "DOMElement.removeAttributeNode" => ((h, ctx), (he, ctxe))
      ("DOMElement.getElementsByTagName" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
         /* imprecise modeling */
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r = addrToLoc(addr1, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)

          /* arguments */
          val tagname = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (tagname </ StrBot) {
            val lset = lset_this.foldLeft(LocSetBot)((lset, l) => lset ++ DOMHelper.findByTag(h_1, l, tagname))
            val proplist = HTMLCollection.getInsList(0) 
            val obj = proplist.foldLeft(ObjEmpty)((o, p) => o.update(p._1, p._2))
            val newobj = if(lset.size==0) obj
                         else if(lset.size==1) {
                           obj.update("length", PropValue(ObjectValue(Value(AbsNumber.alpha(1)), F, T, T))).update(
                                       "0", PropValue(ObjectValue(Value(lset), T, T, T)))
                         }
                         else {
                           obj.update("length", PropValue(ObjectValue(Value(UInt), F, T, T))).update(
                                       NumStr, PropValue(ObjectValue(Value(lset), T, T, T)))
                         }
            val h_2 = h_1.update(l_r, newobj)
            ((Helper.ReturnStore(h_2, Value(l_r)), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      //case "DOMElement.getAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.removeAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.getAttributeNodeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setAttributeNodeNS" => ((h, ctx), (he, ctxe))
      ("DOMElement.getElementsByTagNameNS" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* arguments */
          val s_ns = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val s_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          if (s_ns </ StrBot && s_name </ StrBot) {
            val obj_table = h(TagTableLoc)
            val propv_element = obj_table(s_name.toUpperCase)
            val (h_1, ctx_1, v_empty) =
              if (propv_element._2 </ AbsentBot) {
                val l_r = addrToLoc(addr1, Recent)
                val (_h, _ctx)  = Helper.Oldify(h, ctx, addr1)
                /* empty NodeList */
                val o_empty = Obj(DOMNodeList.getInsList(0).foldLeft(ObjEmpty.map)((o,pv) =>
                  o.updated(pv._1, (pv._2, AbsentBot))))
                val _h1 = _h.update(l_r, o_empty)
                (_h1, _ctx, Value(l_r))
              } else (h, ctx, ValueBot)
            /* imprecise semantic */
            ((Helper.ReturnStore(h_1, propv_element._1._1._1 + v_empty), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMElement.hasAttribute" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val attr_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0"))).toLowerCase
          if(attr_name </ StrBot) {
            val attr_val = lset_this.foldLeft(ValueBot)((v, l_this) => {
              // read the list of attributes in the current node
              val attributes_lset = Helper.Proto(h, l_this, AbsString.alpha("attributes"))._2
              val attr_val_1= attributes_lset.foldLeft(ValueBot)((v, l_attributes) => {
                v + Value(Helper.HasOwnProperty(h, l_attributes, attr_name))})
              v + attr_val_1
            })
            ((Helper.ReturnStore(h, attr_val), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      //case "DOMElement.hasAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setIdAttribute" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setIdAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setIdAttributeNode" => ((h, ctx), (he, ctxe))
      "DOMElement.querySelector" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)

          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val s_selector = getArgValue(h, ctx, args, "0")._1._5
          if (s_selector </ StrBot) {
            val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
            // start here
            val l_result = addrToLoc(addr1, Recent)
            val lset_find = lset_this.foldLeft(LocSetBot)((ls, l) => ls ++ DOMHelper.querySelectorAll(h_1, l, s_selector))
            val (h_ret, v_ret) =
              if (lset_find.isEmpty)
                (h_1, Value(NullTop))
              else {
                val o_result = Helper.NewObject(ObjProtoLoc)
                  .update("0", PropValue(ObjectValue(Value(lset_find), T, T, T)))
                  .update("length", PropValue(ObjectValue(AbsNumber.alpha(0), T, T, T)))
                val h_2 = h_1.update(l_result, o_result)
                (h_2, Value(lset_find))
              }
            ((Helper.ReturnStore(h_ret, v_ret), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
      "DOMElement.querySelectorAll" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)

          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val s_selector = getArgValue(h, ctx, args, "0")._1._5
          if (s_selector </ StrBot) {
            val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
            // start here
            val l_result = addrToLoc(addr1, Recent)
            val lset_find = lset_this.foldLeft(LocSetBot)((ls, l) => ls ++ DOMHelper.querySelectorAll(h_1, l, s_selector))
            val (h_ret, v_ret) =
              if (lset_find.isEmpty) {
                val o_result = Helper.NewObject(ObjProtoLoc)
                  .update("length", PropValue(ObjectValue(Value(AbsNumber.alpha(0)), T, T, T)))
                val h_2 = h_1.update(l_result, o_result)
                (h_2, Value(l_result))

              }
              else if(lset_find.size==1) {
                val o_result = Helper.NewObject(ObjProtoLoc)
                  .update("0", PropValue(ObjectValue(Value(lset_find), T, T, T)))
                  .update("length", PropValue(ObjectValue(Value(AbsNumber.alpha(1)), F, F, F)))
                val h_2 = h_1.update(l_result, o_result)
                (h_2, Value(l_result))
              }

              else {
                val o_result = Helper.NewObject(ObjProtoLoc)
                  .update(NumStr, PropValue(ObjectValue(Value(lset_find), T, T, T)))
                  .update("length", PropValue(ObjectValue(Value(UInt), F, F, F)))
                val h_2 = h_1.update(l_result, o_result)
                (h_2, Value(l_result))
              }
            ((Helper.ReturnStore(h_ret, v_ret), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
      ("DOMElement.getElementsByClassName" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* arguments */
          val s_class = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (s_class </ StrBot) {
            val propv_element = h(ClassTableLoc)(s_class)
            val (h_1, ctx_1, v_empty) =
              if (propv_element._2 </ AbsentBot) {
                val l_r = addrToLoc(addr1, Recent)
                val (_h, _ctx)  = Helper.Oldify(h, ctx, addr1)
                /* empty NodeList */
                val o_empty = Obj(DOMNodeList.getInsList(0).foldLeft(ObjEmpty.map)((o,pv) =>
                  o.updated(pv._1, (pv._2, AbsentBot))))
                val _h1 = _h.update(l_r, o_empty)
                (_h1, _ctx, Value(l_r))
              } else (h, ctx, ValueBot)
            /* imprecise semantic */
            ((Helper.ReturnStore(h_1, propv_element._1._1._1 + v_empty), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMElement.getClientRects" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
           ((Helper.ReturnStore(h, Value(ClientRectList.loc_ins)), ctx), (he, ctxe))
        })),

      ("DOMElement.getBoundingClientRect" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
           ((Helper.ReturnStore(h, Value(ClientRect.loc_ins)), ctx), (he, ctxe))
        })),

      // could be more precise
      ("DOMElement.webkitMatchesSelector" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* argument */
          val selector = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if(selector </ StrBot)
            ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //TODO: not yet implemented
      ("DOMElement.getAttribute" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          /* arguments */
          val attr_name = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc))).toLowerCase
          if(attr_name </ StrBot) {
            val attr_val = lset_this.foldLeft(ValueBot)((v, l_this) => {
              // read the list of attributes in the current node
              val attributes_lset = PreHelper.Proto(h, l_this, AbsString.alpha("attributes"))._2
              val attr_val_1= attributes_lset.foldLeft(ValueBot)((v, l_attributes) => {
                val attr_val_2 = PreHelper.HasOwnProperty(h, l_attributes, attr_name).getPair match {
                  // in case that the current node does not have an attribute with the given name
                  case (AbsSingle, Some(b)) if !b => Value(NullTop)
                  // in case that the current node may have an attribute with the given name
                  case _ =>
                    val attr_lset = PreHelper.Proto(h, l_attributes, attr_name)._2
                    attr_lset.foldLeft(ValueBot)((v, l_attr) => {
                      PreHelper.Proto(h, l_attr, AbsString.alpha("value")) + v
                    })
                }
                v + attr_val_2
              })
              v + attr_val_1
            })
            ((PreHelper.ReturnStore(h, PureLocalLoc, attr_val), ctx), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMElement.setAttribute" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_env = h(PureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          // For 'Attr' node
          val l_attr = addrToLoc(addr1, Recent)
          // For 'Text' node
          val l_text = addrToLoc(addr2, Recent)
          // For NamedNodeList for 'childNodes' of the Attr and text nodes
          val l_child1 = addrToLoc(addr3, Recent)
          val l_child2 = addrToLoc(addr4, Recent)
          val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = PreHelper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = PreHelper.Oldify(h_2, ctx_2, addr3)
          val (h_4, ctx_4) = PreHelper.Oldify(h_3, ctx_3, addr4)
          /* arguments */
          val attr_name = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h_4, ctx, args, "0", PureLocalLoc))).toLowerCase
          val attr_val = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h_4, ctx, args, "1", PureLocalLoc)))

          /* imprecise semantics : no exception handling */
          if(attr_name </ StrBot || attr_val </StrBot) {
            val name = PropValue(ObjectValue(attr_name, F, T, T))
            val value = PropValue(ObjectValue(attr_val, T, T, T))
            // create a new Attr node object
            val attr_obj_list = DOMAttr.default_getInsList(name, value, PropValue(ObjectValue(l_child1, F, T, T)), PropValue(ObjectValue(l_text, F, T, T)))
            val attr_obj = attr_obj_list.foldLeft(ObjEmpty) ((obj, v) => obj.update(AbsString.alpha(v._1), v._2))
            // create a new text node object
            val text_obj_list = DOMText.default_getInsList(value, PropValue(ObjectValue(l_attr, F, T, T)), PropValue(ObjectValue(l_child2, F, T, T)))
            val text_obj = text_obj_list.foldLeft(ObjEmpty) ((obj, v) => obj.update(AbsString.alpha(v._1), v._2))

            // objects for 'childNodes' of the Attr node
            val child_obj_list1 = DOMNamedNodeMap.getInsList(1)
            val child_obj1 = child_obj_list1.foldLeft(ObjEmpty.update(AbsString.alpha("0"), PropValue(ObjectValue(l_text, T, T, T))))((obj, v) =>
              obj.update(AbsString.alpha(v._1), v._2))
            // objects for 'childNodes' of the Text node
            val child_obj_list2 = DOMNamedNodeMap.getInsList(0)
            val child_obj2 = child_obj_list2.foldLeft(ObjEmpty)((obj, v) => obj.update(AbsString.alpha(v._1), v._2))

            val h_5 = lset_this.foldLeft(h_4)((h_in, l_this) => {
              // update 'className' property if the value of the 'class' attribute would be changed
              val thisobj = h_in(l_this)
              val className = PreHelper.Proto(h_in, l_this, AbsString.alpha("className"))
              val h_in1 = attr_name.getAbsCase match {
                case AbsTop =>
                  // join the old value and new value
                  val thisobj_new = thisobj.update(AbsString.alpha("className"), value + PropValue(className))
                  h_in.update(l_this, thisobj_new)
                case AbsBot => h_in
                case _ if attr_name.isAllNums => h_in
                case _ => attr_name.gamma match {
                  case Some(vs) =>
                    if(vs.contains("class")) {
                      if(vs.size == 1) {
                        // update 'className' property with a new value
                        val thisobj_new = thisobj.update(AbsString.alpha("className"), value)
                        h_in.update(l_this, thisobj_new)
                      }
                      else {
                        // join the old value and new value
                        val thisobj_new = thisobj.update(AbsString.alpha("className"), value + PropValue(className))
                        h_in.update(l_this, thisobj_new)
                      }
                    }
                    else h_in
                  case None =>
                    // join the old value and new value
                    val thisobj_new = thisobj.update(AbsString.alpha("className"), value + PropValue(className))
                    h_in.update(l_this, thisobj_new)
                }
              }

              // read the list of attributes in the current node
              val attributes_lset = PreHelper.Proto(h_in1, l_this, AbsString.alpha("attributes"))._2
              attributes_lset.foldLeft(h_in1)((h_in2, l_attributes) => {
                val attributes_obj = h_in2(l_attributes)
                val length_pval = attributes_obj("length")._1._1._1._1
                // increate 'length' of 'attributes' by 1
                val length_val = AbsNumber.getUIntSingle(PreHelper.toNumber(length_pval)) match {
                  case Some(v) => AbsNumber.alpha(v+1)
                  case _ => PreHelper.toNumber(length_pval)
                }
                val attributes_obj_new =
                  attributes_obj.update(attr_name, PropValue(ObjectValue(l_attr, T, T, T))).
                    update(PreHelper.toString(length_pval), PropValue(ObjectValue(l_attr, T, T, T))).
                    update(AbsString.alpha("length"), PropValue(ObjectValue(length_val, T, T, T)))
                // update heap
                h_in2.update(l_attr, attr_obj).update(l_text, text_obj).update(l_attributes, attributes_obj_new).update(l_child1, child_obj1).update(l_child2, child_obj2)
              })
            })
            ((h_5, ctx_4), (he, ctxe))
          }
          else
            ((h_4, ctx_4), (he, ctxe))
        })),
      // case "DOMElement.removeAttribute" => ((h, ctx), (he, ctxe))
      //case "DOMElement.getAttributeNode" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setAttributeNode" => ((h, ctx), (he, ctxe))
      //case "DOMElement.removeAttributeNode" => ((h, ctx), (he, ctxe))
      ("DOMElement.getElementsByTagName" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_env = h(PureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* arguments */
          val s_name = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          if (s_name </ StrBot) {
            val obj_table = h(TagTableLoc)
            val propv_element = obj_table(s_name.toUpperCase)
            val (h_1, ctx_1, v_empty) =
              if (propv_element._2 </ AbsentBot) {
                val l_r = addrToLoc(addr1, Recent)
                val (_h, _ctx)  = PreHelper.Oldify(h, ctx, addr1)
                /* empty NodeList */
                val o_empty = Obj(DOMNodeList.getInsList(0).foldLeft(ObjEmpty.map)((o,pv) =>
                  o.updated(pv._1, (pv._2, AbsentBot))))
                val _h1 = _h.update(l_r, o_empty)
                (_h1, _ctx, Value(l_r))
              } else (h, ctx, ValueBot)
            /* imprecise semantic */
            ((PreHelper.ReturnStore(h_1, PureLocalLoc, propv_element._1._1._1 + v_empty), ctx_1), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        })),
      //case "DOMElement.getAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.removeAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.getAttributeNodeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setAttributeNodeNS" => ((h, ctx), (he, ctxe))
      ("DOMElement.getElementsByTagNameNS" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_env = h(PureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* arguments */
          val s_ns = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          val s_name = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "1", PureLocalLoc)))
          if (s_ns </ StrBot && s_name </ StrBot) {
            val obj_table = h(TagTableLoc)
            val propv_element = obj_table(s_name.toUpperCase)
            val (h_1, ctx_1, v_empty) =
              if (propv_element._2 </ AbsentBot) {
                val l_r = addrToLoc(addr1, Recent)
                val (_h, _ctx)  = PreHelper.Oldify(h, ctx, addr1)
                /* empty NodeList */
                val o_empty = Obj(DOMNodeList.getInsList(0).foldLeft(ObjEmpty.map)((o,pv) =>
                  o.updated(pv._1, (pv._2, AbsentBot))))
                val _h1 = _h.update(l_r, o_empty)
                (_h1, _ctx, Value(l_r))
              } else (h, ctx, ValueBot)
            /* imprecise semantic */
            ((PreHelper.ReturnStore(h_1, PureLocalLoc, propv_element._1._1._1 + v_empty), ctx_1), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMElement.hasAttribute" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          /* arguments */
          val attr_name = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc))).toLowerCase
          if(attr_name </ StrBot) {
            val attr_val = lset_this.foldLeft(ValueBot)((v, l_this) => {
              // read the list of attributes in the current node
              val attributes_lset = PreHelper.Proto(h, l_this, AbsString.alpha("attributes"))._2
              val attr_val_1= attributes_lset.foldLeft(ValueBot)((v, l_attributes) => {
                v + Value(PreHelper.HasOwnProperty(h, l_attributes, attr_name))})
              v + attr_val_1
            })
            ((PreHelper.ReturnStore(h, PureLocalLoc, attr_val), ctx), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        }))
      //case "DOMElement.hasAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setIdAttribute" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setIdAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setIdAttributeNode" => ((h, ctx), (he, ctxe))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      ("DOMElement.getAttribute" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMElement.setAttribute" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          val addr_env = (fid, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)
          // For 'Attr' node
          val l_attr = addrToLoc(addr1, Recent)
          // For 'Text' node
          val l_text = addrToLoc(addr2, Recent)
          // For NamedNodeList for 'childNodes' of the Attr and text nodes
          val l_child1 = addrToLoc(addr3, Recent)
          val l_child2 = addrToLoc(addr4, Recent)
          //val LP1 = AccessHelper.Oldify_def(h, ctx, addr1)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AccessHelper.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 0)))
          //val LP2 = AccessHelper.Oldify_def(h, ctx, addr2)
          val LP2 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AccessHelper.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 1)))
          //val LP3 = AccessHelper.Oldify_def(h, ctx, addr3)
          val LP3 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AccessHelper.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 2)))
          //val LP4 = AccessHelper.Oldify_def(h, ctx, addr4)
          val LP4 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AccessHelper.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 3)))
          /* arguments */
          val attr_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0"))).toLowerCase
          val attr_val = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))

          /* imprecise semantics : no exception handling */
          if(attr_name </ StrBot || attr_val </StrBot) {
            val name = PropValue(ObjectValue(attr_name, F, T, T))
            val value = PropValue(ObjectValue(attr_val, T, T, T))
            // create a new Attr node object
            val attr_obj_list = DOMAttr.default_getInsList(name, value, PropValue(ObjectValue(l_child1, F, T, T)), PropValue(ObjectValue(l_text, F, T, T)))
            val LP5 = attr_obj_list.foldLeft(LPBot)((lpset, v) =>
            //lpset + (l_attr, v._1))
              lpset ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 0),Recent), v._1)))
            // create a new text node object
            val text_obj_list = DOMText.default_getInsList(value, PropValue(ObjectValue(l_attr, F, T, T)), PropValue(ObjectValue(l_child2, F, T, T)))
            val LP6 = text_obj_list.foldLeft(LPBot)((lpset, v) =>
            //lpset + (l_text, v._1))
              lpset ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 1),Recent), v._1)))

            // objects for 'childNodes' of the Attr node
            val child_obj_list1 = DOMNamedNodeMap.getInsList(1)
            val LP_child1 = set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 2),Recent), "0"))
            val LP7 = child_obj_list1.foldLeft(LP_child1)((lpset, v) =>
            //lpset + (l_child1, v._1))
              lpset ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 2),Recent), v._1)))
            // objects for 'childNodes' of the Text node
            val child_obj_list2 = DOMNamedNodeMap.getInsList(0)
            val LP8 = child_obj_list2.foldLeft(LPBot)((lpset, v) =>
            //lpset + (l_child2, v._1))
              lpset ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 3),Recent), v._1)))

            val LP9 = lset_this.foldLeft(LPBot)((lpset, l_this) => {
              // read the list of attributes in the current node
              val attributes_lset = Helper.Proto(h, l_this, AbsString.alpha("attributes"))._2
              attributes_lset.foldLeft(lpset + (l_this, "className"))((lpset2, l_attributes) => {
                val attributes_obj = h(l_attributes)
                val length_pval = attributes_obj("length")._1._1._1._1
                lpset2 ++ AccessHelper.absPair(h, l_attributes, attr_name) ++ AccessHelper.absPair(h, l_attributes, Helper.toString(length_pval)) + (l_attributes, "length")
              })
            })
            LP1 ++ LP2 ++ LP3 ++ LP4 ++ LP5 ++ LP6 ++ LP7 ++ LP8 ++ LP9 + (SinglePureLocalLoc, "@return")
          }
          else
            LP1 ++ LP2 ++ LP3 ++ LP4
        })),
      // case "DOMElement.removeAttribute" => ((h, ctx), (he, ctxe))
      //case "DOMElement.getAttributeNode" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setAttributeNode" => ((h, ctx), (he, ctxe))
      //case "DOMElement.removeAttributeNode" => ((h, ctx), (he, ctxe))
      ("DOMElement.getElementsByTagName" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          /* arguments */
          val s_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (s_name </ StrBot) {
            val obj_table = h(TagTableLoc)
            val propv_element = obj_table(s_name.toUpperCase)
            val LP1 =
              if (propv_element._2 </ AbsentBot) {
                //val l_r = addrToLoc(addr1, Recent)
                //val LP1_1  = AccessHelper.Oldify_def(h, ctx, addr1)
                val LP1_1 = set_addr.foldLeft(LPBot)((lp, a) =>
                  lp ++ AccessHelper.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 0)))
                /* empty NodeList */
                val LP1_2 = DOMNodeList.getInsList(0).foldLeft(LPBot)((lpset, pv) =>
                //lpset + (l_r, pv._1))
                  lpset ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 0),Recent), pv._1)))
                LP1_1 ++ LP1_2
              } else LPBot
            LP1 + (SinglePureLocalLoc, "@return")
          }
          else
            LPBot
        })),
      //case "DOMElement.getAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.removeAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.getAttributeNodeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setAttributeNodeNS" => ((h, ctx), (he, ctxe))
      ("DOMElement.getElementsByTagNameNS" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          /* arguments */
          val s_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (s_name </ StrBot) {
            val obj_table = h(TagTableLoc)
            val propv_element = obj_table(s_name.toUpperCase)
            val LP1 =
              if (propv_element._2 </ AbsentBot) {
                //val l_r = addrToLoc(addr1, Recent)
                //val LP1_1  = AccessHelper.Oldify_def(h, ctx, addr1)
                val LP1_1 = set_addr.foldLeft(LPBot)((lp, a) =>
                  lp ++ AccessHelper.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 0)))
                /* empty NodeList */
                val LP1_2 = DOMNodeList.getInsList(0).foldLeft(LPBot)((lpset, pv) =>
                //lpset + (l_r, pv._1))
                  lpset ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 0),Recent), pv._1)))
                LP1_1 ++ LP1_2
              } else LPBot
            LP1 + (SinglePureLocalLoc, "@return")
          }
          else
            LPBot
        })),
      ("DOMElement.hasAttribute" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
      //case "DOMElement.hasAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setIdAttribute" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setIdAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setIdAttributeNode" => ((h, ctx), (he, ctxe))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      ("DOMElement.getAttribute" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val attr_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val LP1 = getArgValue_use(h, ctx, args, "0")
          if(attr_name </ StrBot) {
            val LP2 = lset_this.foldLeft(LPBot)((lpset, l_this) => {
              // read the list of attributes in the current node
              val attributes_lset = Helper.Proto(h, l_this, AbsString.alpha("attributes"))._2
              val LP2_1 = AccessHelper.Proto_use(h, l_this, AbsString.alpha("attributes"))
              val LP2_2 = attributes_lset.foldLeft(LPBot)((lpset, l_attributes) => {
                val LP2_3 = AccessHelper.HasOwnProperty_use(h, l_attributes, attr_name)
                val LP2_4 = Helper.HasOwnProperty(h, l_attributes, attr_name).getPair match {
                  // in case that the current node does not have an attribute with the given name
                  case (AbsSingle, Some(b)) if !b => LPBot
                  // in case that the current node may have an attribute with the given name
                  case _ =>
                    val attr_lset = Helper.Proto(h, l_attributes, attr_name)._2
                    val LP2_5 = AccessHelper.Proto_use(h, l_attributes, attr_name)
                    val LP2_6 = attr_lset.foldLeft(LPBot)((lpset, l_attr) => {
                      AccessHelper.Proto_use(h, l_attr, AbsString.alpha("value")) ++ lpset
                    })
                    LP2_5 ++ LP2_6
                }
                lpset ++ LP2_3 ++ LP2_4
              })
              lpset ++ LP2_1  ++ LP2_2
            })
            LP1 ++ LP2 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
          }
          else
            LP1
        })),
      ("DOMElement.setAttribute" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          // For 'Attr' node
          //val l_attr = addrToLoc(addr1, Recent)
          // For 'Text' node
          //val l_text = addrToLoc(addr2, Recent)
          // For NamedNodeList for 'childNodes' of the Attr and text nodes
          //val l_child1 = addrToLoc(addr3, Recent)
          //val l_child2 = addrToLoc(addr4, Recent)
          //val LP1 = AccessHelper.Oldify_use(h, ctx, addr1)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AccessHelper.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 0)))
          //val LP2 = AccessHelper.Oldify_use(h, ctx, addr2)
          val LP2 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AccessHelper.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 1)))
          //val LP3 = AccessHelper.Oldify_use(h, ctx, addr3)
          val LP3 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AccessHelper.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 2)))
          //val LP4 = AccessHelper.Oldify_use(h, ctx, addr4)
          val LP4 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AccessHelper.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 3)))
          /* arguments */
          val attr_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0"))).toLowerCase
          val attr_val = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))

          val LP5 = getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1")

          /* imprecise semantics : no exception handling */
          if(attr_name </ StrBot || attr_val </StrBot) {
            val LP6 = lset_this.foldLeft(LPBot)((lpset, l_this) => {
              // read the list of attributes in the current node
              val attributes_lset = Helper.Proto(h, l_this, AbsString.alpha("attributes"))._2
              val LP6_1 = AccessHelper.Proto_use(h, l_this, AbsString.alpha("attributes"))
              attributes_lset.foldLeft(lpset ++ LP6_1 + (l_this, "className"))((lpset, l_attributes) => {
                lpset + (l_attributes, "length")
              })
            })
            LP1 ++ LP2 ++ LP3 ++ LP4 ++ LP5 ++ LP6 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
          }
          else
            LP1 ++ LP2 ++ LP3 ++ LP4 ++ LP5
        })),
      // case "DOMElement.removeAttribute" => ((h, ctx), (he, ctxe))
      //case "DOMElement.getAttributeNode" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setAttributeNode" => ((h, ctx), (he, ctxe))
      //case "DOMElement.removeAttributeNode" => ((h, ctx), (he, ctxe))
      ("DOMElement.getElementsByTagName" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          /* arguments */
          val s_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val LP1 = getArgValue_use(h, ctx, args, "0")
          if (s_name </ StrBot) {
            val obj_table = h(TagTableLoc)
            val propv_element = obj_table(s_name.toUpperCase)
            val LP2 = AccessHelper.absPair(h, TagTableLoc, s_name.toUpperCase)
            val LP3 =
              if (propv_element._2 </ AbsentBot) {
                //val l_r = addrToLoc(addr1, Recent)
                //AccessHelper.Oldify_use(h, ctx, addr1)
                set_addr.foldLeft(LPBot)((lp, a) =>
                  lp ++ AccessHelper.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 0)))
              } else LPBot
            LP1 ++ LP2 ++ LP3 + (SinglePureLocalLoc, "@return")
          }
          else
            LP1
        })),
      //case "DOMElement.getAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.removeAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.getAttributeNodeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setAttributeNodeNS" => ((h, ctx), (he, ctxe))
      ("DOMElement.getElementsByTagNameNS" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          /* arguments */
          val s_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val LP1 = getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1")
          if (s_name </ StrBot) {
            val obj_table = h(TagTableLoc)
            val propv_element = obj_table(s_name.toUpperCase)
            val LP2 = AccessHelper.absPair(h, TagTableLoc, s_name.toUpperCase)
            val LP3 =
              if (propv_element._2 </ AbsentBot) {
                //val l_r = addrToLoc(addr1, Recent)
                //AccessHelper.Oldify_use(h, ctx, addr1)
                set_addr.foldLeft(LPBot)((lp, a) =>
                  lp ++ AccessHelper.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 0)))
              } else LPBot
            LP1 ++ LP2 ++ LP3 + (SinglePureLocalLoc, "@return")
          }
          else
            LP1
        })),
      ("DOMElement.hasAttribute" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val attr_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0"))).toLowerCase
          val LP1 = getArgValue_use(h, ctx, args, "0")
          if(attr_name </ StrBot) {
            val LP2 = lset_this.foldLeft(LPBot)((lpset, l_this) => {
              // read the list of attributes in the current node
              val attributes_lset = Helper.Proto(h, l_this, AbsString.alpha("attributes"))._2
              val LP2_1 = AccessHelper.Proto_use(h, l_this, AbsString.alpha("attributes"))
              attributes_lset.foldLeft(lpset++LP2_1)((lpset, l_attributes) => {
                lpset ++ AccessHelper.HasOwnProperty_use(h, l_attributes, attr_name)})
            })
            LP1 ++ LP2 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
          }
          else
            LP1
        }))
      //case "DOMElement.hasAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setIdAttribute" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setIdAttributeNS" => ((h, ctx), (he, ctxe))
      //case "DOMElement.setIdAttributeNode" => ((h, ctx), (he, ctxe))
    )
  }

  /* instance */
  override def getInstance(cfg: CFG): Option[Loc] = Some(newRecentLoc())
  /* list of properties in the instance object */
  override def getInsList(node: Node): List[(String, PropValue)] = node match {
    case e: Element =>
      // This instance object has all properties of the Node object
      DOMNode.getInsList(node) ++ List(
        // DOM Level 1
        ("tagName",   PropValue(ObjectValue(AbsString.alpha(e.getTagName), F, T, F))),
        // non-standard
        ("scrollTop", PropValue(ObjectValue(UInt, T, T, T))),
        ("scrollLeft", PropValue(ObjectValue(UInt, T, T, T))),
        ("scrollWidth", PropValue(ObjectValue(UInt, F, T, T))),
        ("scrollHeight", PropValue(ObjectValue(UInt, F, T, T))),
        ("offsetParent", PropValue(ObjectValue(NullTop, F, T, T))),
        ("offsetTop", PropValue(ObjectValue(UInt, F, T, T))),
        ("offsetLeft", PropValue(ObjectValue(UInt, F, T, T))),
        ("offsetWidth", PropValue(ObjectValue(UInt, F, T, T))),
        ("offsetHeight", PropValue(ObjectValue(UInt, F, T, T))),
        ("clientTop", PropValue(ObjectValue(UInt, F, T, T))),
        ("clientLeft", PropValue(ObjectValue(UInt, F, T, T))),
        ("clientWidth", PropValue(ObjectValue(UInt, F, T, T))),
        ("clientHeight", PropValue(ObjectValue(UInt, F, T, T))),
        ("onclick", PropValue(ObjectValue(NullTop, T, T, T))),
        ("onload", PropValue(ObjectValue(NullTop, T, T, T)))
        // 'style' property is updated in the DOMBuilder module
      )
    // TODO: schemaTypeInfo in DOM Level 3
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot have instance objects.")
      List()
    }
  }
  def getInsList(tagName: PropValue, scrollTop: PropValue, scrollLeft: PropValue, scrollWidth: PropValue, scrollHeight: PropValue,
                 offsetParent: PropValue, offsetTop: PropValue, offsetLeft: PropValue, offsetWidth: PropValue, offsetHeight: PropValue,
                 clientTop: PropValue, clientLeft: PropValue, clientWidth: PropValue, clientHeight: PropValue, onclick: PropValue,
                 onload: PropValue): List[(String, PropValue)] = List(
    ("tagName", tagName),
    ("scrollTop", scrollTop),
    ("scrollLeft", scrollLeft),
    ("scrollWidth", scrollWidth),
    ("scrollHeight", scrollHeight),
    ("offsetParent", offsetParent),
    ("offsetTop", offsetTop),
    ("offsetLeft", offsetLeft),
    ("offsetWidth", offsetWidth),
    ("offsetHeight", offsetHeight),
    ("clientTop", clientTop),
    ("clientLeft", clientLeft),
    ("clientWidth", clientWidth),
    ("clientHeight", clientHeight),
    ("onclick", onclick),
    ("onload", onload)
  )

  def getInsList(tagName: PropValue): List[(String, PropValue)] = {
    val nodeName = tagName
    val nodeValue = PropValue(ObjectValue(NullTop, T, T, T))
    val nodeType = PropValue(ObjectValue(AbsNumber.alpha(DOMNode.ELEMENT_NODE), F, T, T))
    val parentNode = PropValue(ObjectValue(NullTop, F, T, T))
    val childNodes = PropValue(ObjectValue(NullTop, F, T, T))
    val firstChild = PropValue(ObjectValue(NullTop, F, T, T))
    val lastChild = PropValue(ObjectValue(NullTop, F, T, T))
    val previousSibling = PropValue(ObjectValue(NullTop, F, T, T))
    val nextSibling = PropValue(ObjectValue(NullTop, F, T, T))
    val ownerDocument = PropValue(ObjectValue(HTMLDocument.getInstance().get, F, T, T))
    val namespaceURI = PropValue(ObjectValue(NullTop, F, T, T))
    val prefix = PropValue(ObjectValue(NullTop, T, T, T))
    val localName = PropValue(ObjectValue(NullTop, F, T, T))
    val textContent = PropValue(ObjectValue(AbsString.alpha(""), T, T, T))

    // This instance object has all properties of the Node object
    DOMNode.getInsList(nodeName, nodeValue, nodeType, parentNode, childNodes, firstChild, lastChild,
      previousSibling, nextSibling, ownerDocument, namespaceURI, prefix, localName, textContent) ++ List(
      ("tagName", tagName),
      ("scrollTop", PropValue(ObjectValue(UInt, T, T, T))),
      ("scrollLeft", PropValue(ObjectValue(UInt, T, T, T))),
      ("scrollWidth", PropValue(ObjectValue(UInt, F, T, T))),
      ("scrollHeight", PropValue(ObjectValue(UInt, F, T, T))),
      ("offsetParent", PropValue(ObjectValue(NullTop, F, T, T))),
      ("offsetTop", PropValue(ObjectValue(UInt, F, T, T))),
      ("offsetLeft", PropValue(ObjectValue(UInt, F, T, T))),
      ("offsetWidth", PropValue(ObjectValue(UInt, F, T, T))),
      ("offsetHeight", PropValue(ObjectValue(UInt, F, T, T))),
      ("clientTop", PropValue(ObjectValue(UInt, F, T, T))),
      ("clientLeft", PropValue(ObjectValue(UInt, F, T, T))),
      ("clientWidth", PropValue(ObjectValue(UInt, F, T, T))),
      ("clientHeight", PropValue(ObjectValue(UInt, F, T, T))),
      ("onclick", PropValue(ObjectValue(NullTop, T, T, T))),
      ("onload", PropValue(ObjectValue(NullTop, T, T, T)))
     )
    // TODO: schemaTypeInfo in DOM Level 3
  }
}
