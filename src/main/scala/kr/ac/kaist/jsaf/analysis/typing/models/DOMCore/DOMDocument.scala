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
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, InternalError, FunctionId}
import org.w3c.dom.Document
import org.w3c.dom.Node
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml5.DOMLocation
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml.{HTMLTopElement, HTMLElement, HTMLCollection}
import kr.ac.kaist.jsaf.analysis.typing.models.DOMObject.{CSSStyleDeclaration, StyleSheetList}
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.Shell

object DOMDocument extends DOM {
  private val name = "Document"

  /* predefined locations */
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
    ("createElement",               AbsBuiltinFunc("DOMDocument.createElement", 1)),
    ("createDocumentFragment",      AbsBuiltinFunc("DOMDocument.createDocumentFragment", 0)),
    ("createTextNode",              AbsBuiltinFunc("DOMDocument.createTextNode", 1)),
    ("createComment",               AbsBuiltinFunc("DOMDocument.createComment", 1)),
    ("createCDATASection",          AbsBuiltinFunc("DOMDocument.createCDATASection", 1)),
    ("createProcessingInstruction", AbsBuiltinFunc("DOMDocument.createProcessingInstruction", 2)),
    ("createAttribute",             AbsBuiltinFunc("DOMDocument.createAttribute", 1)),
    ("createEntityReference",       AbsBuiltinFunc("DOMDocument.createEntityReference", 1)),
    ("getElementsByTagName",        AbsBuiltinFunc("DOMDocument.getElementsByTagName", 1)),
    ("importNode",                  AbsBuiltinFunc("DOMDocument.importNode", 2)),
    ("createElementNS",             AbsBuiltinFunc("DOMDocument.createElementNS", 2)),
    ("createAttributeNS",           AbsBuiltinFunc("DOMDocument.createAttributeNS", 2)),
    ("getElementsByTagNameNS",      AbsBuiltinFunc("DOMDocument.getElementsByTagNameNS", 1)),
    ("getElementById",              AbsBuiltinFunc("DOMDocument.getElementById", 2)),
    ("getElementsByClassName",      AbsBuiltinFunc("DOMDocument.getElementsByClassName", 2)),
    ("adoptNode",                   AbsBuiltinFunc("DOMDocument.adoptNode", 1)),
    ("normalizeDocument",           AbsBuiltinFunc("DOMDocument.normalizeDocument", 0)),
    ("renameNode",                  AbsBuiltinFunc("DOMDocument.renameNode", 3)),
    ("querySelector",               AbsBuiltinFunc("DOMDocument.querySelector", 0)),
    ("querySelectorAll",            AbsBuiltinFunc("DOMDocument.querySelectorAll", 0))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)
  )
  val locclone = Shell.params.opt_LocClone

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //TODO: not yet implemented
      ("DOMDocument.createElement" -> (
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
          val addr1_1 = if (locclone) Helper.extendAddr(addr1, Helper.callContextToNumber(cp._2)) else addr1
          val addr2_1 = if (locclone) Helper.extendAddr(addr2, Helper.callContextToNumber(cp._2)) else addr2
          val addr3_1 = if (locclone) Helper.extendAddr(addr3, Helper.callContextToNumber(cp._2)) else addr3
          val addr4_1 = if (locclone) Helper.extendAddr(addr4, Helper.callContextToNumber(cp._2)) else addr4
          val addr5_1 = if (locclone) Helper.extendAddr(addr5, Helper.callContextToNumber(cp._2)) else addr5
          val l_r = addrToLoc(addr1_1, Recent)
          val l_nodes = addrToLoc(addr2_1, Recent)
          val l_attributes = addrToLoc(addr3_1, Recent)
          val l_style = addrToLoc(addr4_1, Recent)
          val l_children = addrToLoc(addr5_1, Recent)
          val h1 = HTMLTopElement.setInsLoc(h, l_r)
          val (h_1, ctx_1)  = Helper.Oldify(h1, ctx, addr1_1)
          val (h_2, ctx_2)  = Helper.Oldify(h_1, ctx_1, addr2_1)
          val (h_3, ctx_3)  = Helper.Oldify(h_2, ctx_2, addr3_1)
          val (h_4, ctx_4)  = Helper.Oldify(h_3, ctx_3, addr4_1)
          val (h_5, ctx_5)  = Helper.Oldify(h_4, ctx_4, addr5_1)

          val s_tag = Helper.toString(Helper.toPrimitive_better(h_5, getArgValue(h_5, ctx_5, args, "0")))

          // DOMException object with the INVALID_CHARACTER_ERR exception code
          val es = Set(DOMException.INVALID_CHARACTER_ERR)
          val (he_1, ctxe_1) = DOMHelper.RaiseDOMException(h_5, ctx_5, es)

          // object for 'childNodes' property
          val childNodes_list = DOMNodeList.getInsList(0)
          val childNodes = childNodes_list.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))
          
          // object for 'attributes' property
          val attributes_list = DOMNamedNodeMap.getInsList(0)
          val attributes = attributes_list.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))

          // object for 'style' property
          val style_list = CSSStyleDeclaration.getInsList()
          val style = style_list.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))
          
          // object for 'children' property
          val children_list = HTMLCollection.getInsList(0)
          val children = children_list.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))

          /* imprecise semantics */
          s_tag.getAbsCase match {
            case AbsBot => // StrBot
              ((HeapBot, ContextBot), (he, ctxe))
            // may cause the INVALID_CHARACTER_ERR exception
            case _ if s_tag.isAllNums => // NumStr | NumStrSingle(s)
              ((HeapBot, ContextBot), (he + he_1, ctxe + ctxe_1))
            case _ => s_tag.gamma match {
              case Some(s_tagSet) => // OtherStrSingle(s)
                if(s_tagSet.isEmpty){
                  throw new InternalError("empty set domain")
                }
                val tag = s_tagSet.head
                val obj = if(AbsString.isNum(tag)) ObjEmpty
                          else {
                            val tag_name = tag.toUpperCase
                            val element_obj_proplist = DOMElement.getInsList(PropValue(ObjectValue(AbsString.alpha(tag_name), F, T, T))):::DOMHelper.getInsList(tag_name)
                            val element_obj = element_obj_proplist.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))
                            element_obj
                          }
                val element_obj_result = s_tagSet.tail.foldLeft(obj)((r, s_tag) => {
                  if(AbsString.isNum(s_tag)) r
                  else {
                    val tag_name = s_tag.toUpperCase
                    val element_obj_proplist = DOMElement.getInsList(PropValue(ObjectValue(AbsString.alpha(tag_name), F, T, T))):::DOMHelper.getInsList(tag_name)
                    val element_obj = element_obj_proplist.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))
                    r + element_obj
                  }
                })    
                // 'childNodes', 'attributes', 'style' update
                val element_obj_result2 = element_obj_result
                      .update("childNodes", PropValue(ObjectValue(l_nodes, F, T, T)))
                      .update("attributes", PropValue(ObjectValue(l_attributes, F, T, T)))
                      .update("style", PropValue(ObjectValue(l_style, T, T, T)))
                      .update("children", PropValue(ObjectValue(l_children, F, T, T)))

                val h_6 = h_5.update(l_nodes, childNodes).update(l_attributes, attributes).update(l_style, style).update(l_r, element_obj_result2)
				val h_7 = HTMLTopElement.setInsLoc(h_6, l_r)
                ((Helper.ReturnStore(h_7, Value(l_r)), ctx_5), (he, ctxe))
              case None => // StrTop | OtherStr
                val element_obj_proplist = HTMLTopElement.default_getInsList()
                val element_obj = element_obj_proplist.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2, AbsentTop))
                // 'childNodes', 'attributes', 'style' update
                val element_obj_up = element_obj
                  .update("childNodes", PropValue(ObjectValue(l_nodes, F, T, T)))
                  .update("attributes", PropValue(ObjectValue(l_attributes, F, T, T)))
                  .update("style", PropValue(ObjectValue(l_style, T, T, T)))
                  .update("children", PropValue(ObjectValue(l_children, F, T, T)))
                val h_6 = h_5.update(l_nodes, childNodes).update(l_attributes, attributes).update(l_style, style).update(l_r, element_obj_up)
				val h_7 = HTMLTopElement.setInsLoc(h_6, l_r)
                ((Helper.ReturnStore(h_7, Value(l_r)), ctx_5), (he + he_1, ctxe + ctxe_1))
                // cause the INVALID_CHARACTER_ERR exception
            }
          }
        })),
      // Based on WHATWG DOM Living Standard Section 6.5 Interface Document
      // Unsound: no excepting handling
      ("DOMDocument.createElementNS" -> (
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
          val l_r = addrToLoc(addr1, Recent)
          val l_nodes = addrToLoc(addr2, Recent)
          val l_attributes = addrToLoc(addr3, Recent)
          val l_style = addrToLoc(addr4, Recent)
          val l_children = addrToLoc(addr5, Recent)
          val h1 = HTMLTopElement.setInsLoc(h, l_r)
          val (h_1, ctx_1)  = Helper.Oldify(h1, ctx, addr1)
          val (h_2, ctx_2)  = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3)  = Helper.Oldify(h_2, ctx_2, addr3)
          val (h_4, ctx_4)  = Helper.Oldify(h_3, ctx_3, addr4)
          val (h_5, ctx_5)  = Helper.Oldify(h_4, ctx_4, addr5)

          val s_namespace = Helper.toString(Helper.toPrimitive_better(h_5, getArgValue(h_5, ctx_5, args, "0")))
          // 1. If namespace is the empty string, set it to null
          // WHATWG HTML Living Standard Section 2.8 Namespaces
          // The HTML namespace is: http://www.w3.org/1999/xhtml
          // The MathML namespace is: http://www.w3.org/1998/Math/MathML
          // The SVG namespace is: http://www.w3.org/2000/svg
          // The XLink namespace is: http://www.w3.org/1999/xlink
          // The XML namespace is: http://www.w3.org/XML/1998/namespace
          // The XMLNS namespace is: http://www.w3.org/2000/xmlns/
          
          val (v_namespace, namespace) = s_namespace.getAbsCase match {
            case _ if s_namespace.isAllNums => (Value(s_namespace), "HTML")
            case AbsBot => (ValueBot, "")
            case AbsSingle => //OtherStrSingle(s) => 
              val s = s_namespace.getSingle.get
              if (s=="") (Value(NullTop), "HTML")
              else {
                val n = s match {
                  case "http://www.w3.org/1998/Math/MathML" => "MathML"
                  case "http://www.w3.org/2000/svg" => "SVG"
                  case "http://www.w3.org/1999/xlink" => "XLink"
                  case "http://www.w3.org/XML/1998/namespace" => "XML"
                  case "http://www.w3.org/XML/2000/xmlns" => "XMLNS"
                  case _ => "HTML"
                }
                (Value(s_namespace), n)
              }
            case _ => //StrTop | OtherStr =>
              (Value(s_namespace) + Value(NullTop), "all")
          }
          val s_name = Helper.toString(Helper.toPrimitive_better(h_4, getArgValue(h_4, ctx_4, args, "1")))
          //4 if qualifiedName contains a ":" (U+003E), 
          //  then split the string on it and let prefix be the part before and localName the part after. 
          //  Otherwise, let prefix be null and localName be qualifiedName.
          val (prefix, localName) = s_name.getAbsCase match {
            case _ if s_name.isAllNums => (Value(NullTop), Value(s_name))
            case AbsBot => (ValueBot, ValueBot)
            case _ => s_name.gamma match {
              case Some(s_nameSet) => // OtherStrSingle(s)
                s_nameSet.foldLeft[(Value, Value)]((ValueBot, ValueBot))((r, s) => {
                  if(!AbsString.isNum(s)) {
                    val ss = s.span(c => c != ':')
                    if(ss._2 == "")
                      (r._1 + Value(NullTop), r._2 + Value(s_name))
                    else
                      (r._1 + Value(AbsString.alpha(ss._1)), r._2 + Value(AbsString.alpha(ss._2.tail)))
                  }
                  else (r._1 + Value(NullTop), r._2 + Value(s_name))
                })
              case None => // StrTop | OtherStr
                (Value(s_name) + Value(NullTop), Value(s_name))
            }
          }
          if(v_namespace </ ValueBot && prefix </ ValueBot && localName </ ValueBot) {
            // 5. If prefix is not null and namespace is null, throw a "NamespaceError" exception.
            val es = Set(DOMException.NAMESPACE_ERR)
            val (he_1, ctxe_1) = DOMHelper.RaiseDOMException(h_4, ctx_4, es)
            if(prefix._1._2 <= NullBot && v_namespace <= Value(NullTop)){
              System.err.println("* Warning: the NamespaceError exception has occurred in the 'document.createElementNS' call.")
              ((HeapBot, ContextBot), (he + he_1, ctxe + ctxe_1))
            }
            else {
              val (he_2, ctxe_2) = if(v_namespace._1._2 </ NullBot && prefix._1._2 <= NullBot) {
                System.err.println("* Warning: the NamespaceError exception may occurr in the 'document.createElementNS' call.")
                DOMHelper.RaiseDOMException(h_4, ctx_4, es)
              }
              else (HeapBot, ContextBot)
              // 10. Return a new element that implements interface, with no attributes, namespace set to namespace, 
              //     namespace prefix set to prefix, local name set to localName, and node document set to the context object.
              // object for 'childNodes' property
              val childNodes_list = DOMNodeList.getInsList(0)
              val childNodes = childNodes_list.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))
          
              // object for 'attributes' property
              val attributes_list = DOMNamedNodeMap.getInsList(0)
              val attributes = attributes_list.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))

              // object for 'style' property
              val style_list = CSSStyleDeclaration.getInsList()
              val style = style_list.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))
               
              // object for 'children' property
              val children_list = HTMLCollection.getInsList(0)
              val children = children_list.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))

              val element_obj_proplist = if(namespace == "HTML") {
                                           HTMLElement.default_getInsList ++ List(
                                            ("@class", PropValue(AbsString.alpha("Object"))),
                                            ("@proto", PropValue(ObjectValue(HTMLElement.getProto.get, F, F, F))),
                                            ("@extensible", PropValue(BoolTrue)),
                                            ("children", PropValue(ObjectValue(l_children, F, T, T)))
                                            )
                                         }
                                         else if(namespace == "SVG") {
                                           DOMHelper.default_getInsListSVG(Helper.toString(Helper.toPrimitive_better(h_4, localName)))
                                         }
                                         else {
                                           System.err.println("* Warning: Namespaces other than 'HTML' and 'SVG' are not modeled in the semantics of 'document.createElementNS'.")
                                           HTMLElement.default_getInsList ++ List(
                                            ("@class", PropValue(AbsString.alpha("Object"))),
                                            ("@proto", PropValue(ObjectValue(HTMLElement.getProto.get, F, F, F))),
                                            ("@extensible", PropValue(BoolTrue)))
                                         }
              val element_obj_proplist2 = DOMElement.getInsList(PropValue(ObjectValue(s_name, F, T, T))) ++ element_obj_proplist              
              val element_obj = element_obj_proplist2.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))
              // 'childNodes', 'attributes', 'style' update
              val element_obj_up = element_obj.update("childNodes", PropValue(ObjectValue(l_nodes, F, T, T))).update(
                                                       "attributes", PropValue(ObjectValue(l_attributes, F, T, T))).update(
                                                       "style", PropValue(ObjectValue(l_style, T, T, T)))
              // 'namespaceURI', 'prefix', 'localName' update  
              val element_obj_up1 = element_obj_up.update("namespaceURI", PropValue(ObjectValue(v_namespace, F, T, T))).update(
                                                          "prefix", PropValue(ObjectValue(prefix, T, T, T))).update(
                                                          "localName", PropValue(ObjectValue(localName, F, T, T)))
              val h_6= h_5.update(l_nodes, childNodes).update(l_attributes, attributes).update(l_style, style).update(l_r, element_obj_up1)
              val h_7 = HTMLTopElement.setInsLoc(h_6, l_r)
              ((Helper.ReturnStore(h_7, Value(l_r)), ctx_5), (he + he_2, ctxe + ctxe_2))
            
            }
                                    
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMDocument.createDocumentFragment" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val l_r = addrToLoc(addr1, Recent)
          val l_nodes = addrToLoc(addr2, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2)  = Helper.Oldify(h_1, ctx_1, addr2)

          val obj_proplist = DOMDocumentFragment.default_getInsList
          val obj = obj_proplist.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))
          // 'childNodes' update
          val childNodes_list = DOMNodeList.getInsList(0)
          val childNodes = childNodes_list.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))
          val obj_up = obj.update("childNodes", PropValue(ObjectValue(l_nodes, F, T, T)))
          // heap update
          val h_3= h_2.update(l_nodes, childNodes).update(l_r, obj_up)
          // returns a DocumentFragment node
          ((Helper.ReturnStore(h_3, Value(l_r)), ctx_2), (he, ctxe))
         })),
      ("DOMDocument.createTextNode" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val l_r = addrToLoc(addr1, Recent)
          val l_nodes = addrToLoc(addr2, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2)  = Helper.Oldify(h_1, ctx_1, addr2)

          // argument
          val s_data = Helper.toString(Helper.toPrimitive_better(h_2, getArgValue(h_2, ctx_2, args, "0")))

          if(s_data </ StrBot) {
            val text_obj_proplist = DOMText.default_getInsList(PropValue(ObjectValue(s_data, T, T, T)),
                PropValue(ObjectValue(NullTop, F, T, T)), PropValue(ObjectValue(Value(l_nodes), F, T, T)))
            val text_obj = text_obj_proplist.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))
            // 'childNodes' update
            val childNodes_list = DOMNodeList.getInsList(0)
            val childNodes = childNodes_list.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))
            // heap update
            val h_3= h_2.update(l_nodes, childNodes).update(l_r, text_obj)
            // returns a comment node
            ((Helper.ReturnStore(h_3, Value(l_r)), ctx_2), (he, ctxe))
          }
          else 
            ((HeapBot, ContextBot), (he, ctxe))
         })),

      ("DOMDocument.createComment" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val l_r = addrToLoc(addr1, Recent)
          val l_nodes = addrToLoc(addr2, Recent)
          val (h_1, ctx_1)  = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2)  = Helper.Oldify(h_1, ctx_1, addr2)

          // argument
          val s_data = Helper.toString(Helper.toPrimitive_better(h_2, getArgValue(h_2, ctx_2, args, "0")))

          if(s_data </ StrBot) {
            val comment_obj_proplist = DOMComment.getInsList(PropValue(ObjectValue(s_data, T, T, T)))
            val comment_obj = comment_obj_proplist.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))
            // 'childNodes' update
            val childNodes_list = DOMNodeList.getInsList(0)
            val childNodes = childNodes_list.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))
            val comment_obj_up = comment_obj.update("childNodes", PropValue(ObjectValue(l_nodes, F, T, T)))
            // heap update
            val h_3= h_2.update(l_nodes, childNodes).update(l_r, comment_obj_up)
            // returns a comment node
            ((Helper.ReturnStore(h_3, Value(l_r)), ctx_2), (he, ctxe))
          }
          else 
            ((HeapBot, ContextBot), (he, ctxe))
         })),
      //case "DOMDocument.createCDATASection" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createProcessingInstruction" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createAttribute" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createEntityReference" => ((h, ctx), (he, ctxe))
      ("DOMDocument.getElementsByTagName" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* imprecise modeling */
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
            val lset = DOMHelper.findByTag(h_1, tagname)
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

/*          
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* arguments */
          val s_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (s_name </ StrBot) {
            val obj_table = h(TagTableLoc)
            val propv_element = obj_table(s_name.toUpperCase)
            val (h_1, ctx_1, v_empty) =
              if (propv_element._2 </ AbsentBot) {
                val l_r = addrToLoc(addr1, Recent)
                val (_h, _ctx)  = Helper.Oldify(h, ctx, addr1)
                /* empty NodeList */
                val o_empty = Obj(HTMLCollection.getInsList(0).foldLeft(ObjEmpty.map)((o,pv) =>
                  o.updated(pv._1, (pv._2, AbsentBot))))
                val _h1 = _h.update(l_r, o_empty)
                (_h1, _ctx, Value(l_r))
              } else (h, ctx, ValueBot)
            /* imprecise semantic */
            ((Helper.ReturnStore(h_1, propv_element._1._1._1 + v_empty), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe)) */
        })),
      //case "DOMDocument.importNode" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createElementNS" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createAttributeNS" => ((h, ctx), (he, ctxe))
      ("DOMDocument.getElementsByTagNameNS" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* arguments */
          val s_ns = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val s_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          if (s_ns </StrBot && s_name </ StrBot) {
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
      ("DOMDocument.getElementById" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val s_id = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (s_id </ StrBot) {
            val lset_find = DOMHelper.findById(h, s_id)
            val v_null = if(!s_id.isConcrete || !lset_find.exists(l => {h(l)(AbsString.alpha("id"))._1.objval.value.pvalue.strval.isConcrete})) 
                            Value(NullTop) 
                         else ValueBot
            /* imprecise semantic */
            ((Helper.ReturnStore(h, Value(lset_find) + v_null), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMDocument.getElementsByClassName" -> (
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
      //case "DOMDocument.adoptNode" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.normalizeDocument" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.renameNode" => ((h, ctx), (he, ctxe))
      "DOMDocument.querySelector" -> (
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
                val v_null = if (!s_selector.isConcrete) Value(NullTop) else ValueBot
                (h_2, Value(lset_find) + v_null)
              }
            ((Helper.ReturnStore(h_ret, v_ret), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
      "DOMDocument.querySelectorAll" -> (
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
              else {
                val o_result = Helper.NewObject(ObjProtoLoc)
                  .update(NumStr, PropValue(ObjectValue(Value(lset_find), T, T, T)))
                  .update("length", PropValue(ObjectValue(Value(UInt), T, T, T)))
                val h_2 = h_1.update(l_result, o_result)
                val v_null = if (!s_selector.isConcrete) Value(NullTop) else ValueBot
                (h_2, Value(lset_find) + v_null)
              }
            ((Helper.ReturnStore(h_ret, v_ret), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //TODO: not yet implemented
      ("DOMDocument.createElement" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_env = h(PureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val l_r = addrToLoc(addr1, Recent)
          val l_nodes = addrToLoc(addr2, Recent)
          val (h_1, ctx_1)  = PreHelper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2)  = PreHelper.Oldify(h_1, ctx_1, addr2)

          val s_tag = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h_2, ctx_2, args, "0", PureLocalLoc)))

          // DOMException object with the INVALID_CHARACTER_ERR exception code
          val es = Set(DOMException.INVALID_CHARACTER_ERR)
          val (he_1, ctxe_1) = DOMHelper.PreRaiseDOMException(h_2, ctx_2, PureLocalLoc, es)
          /* imprecise semantics */
          s_tag.getAbsCase match {
            case _ if s_tag.isAllNums =>
              ((h_2, ctx_2), (he + he_1, ctxe + ctxe_1))
            case AbsBot =>
              ((h_2, ctx_2), (he, ctxe))
            case AbsSingle => //OtherStrSingle(s) =>
              val s = s_tag.getSingle.get
              println("OtherStrSinglne")
              val tag_name = s.toUpperCase
              if(DOMHelper.isValidHtmlTag(tag_name)) {
                val element_obj_proplist = DOMElement.getInsList(PropValue(ObjectValue(AbsString.alpha(tag_name), F, T, T))):::DOMHelper.getInsList(tag_name)
                val element_obj = element_obj_proplist.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))
                // 'childNodes' update
                val childNodes_list = DOMNodeList.getInsList(0)
                val childNodes = childNodes_list.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))
                val element_obj_up = element_obj.update("childNodes", PropValue(ObjectValue(l_nodes, F, T, T)))
                val h_3= h_2.update(l_nodes, childNodes).update(l_r, element_obj_up)
                ((PreHelper.ReturnStore(h_3, PureLocalLoc, Value(l_r)), ctx_2), (he, ctxe))
              }
              // An invalid tag name causes the INVALID_CHARACTER_ERR exception
              else
                ((h_2, ctx_2), (he + he_1, ctxe + ctxe_1))
            // may cause the INVALID_CHARACTER_ERR exception
            case _ => //StrTop | OtherStr =>
              val element_obj_proplist = HTMLTopElement.default_getInsList()
              val element_obj = element_obj_proplist.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2, AbsentTop))
              // 'childNodes' update
              val childNodes_list = DOMNodeList.getInsList(0)
              val childNodes = childNodes_list.foldLeft(ObjEmpty)((x, y) => x.update(y._1, y._2))
              val element_obj_up = element_obj.update("childNodes", PropValue(ObjectValue(l_nodes, F, T, T)))
              val h_3= h_2.update(l_nodes, childNodes).update(l_r, element_obj_up)
              ((PreHelper.ReturnStore(h_3, PureLocalLoc, Value(l_r)), ctx_2), (he + he_1, ctxe + ctxe_1))
            // cause the INVALID_CHARACTER_ERR exception
          }
        })),
      //case "DOMDocument.createDocumentFragment" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createTextNode" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createComment" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createCDATASection" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createProcessingInstruction" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createAttribute" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createEntityReference" => ((h, ctx), (he, ctxe))
      ("DOMDocument.getElementsByTagName" -> (
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
      //case "DOMDocument.importNode" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createElementNS" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createAttributeNS" => ((h, ctx), (he, ctxe))
      ("DOMDocument.getElementsByTagNameNS" -> (
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
          if (s_ns </StrBot && s_name </ StrBot) {
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
      ("DOMDocument.getElementById" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val s_id = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          if (s_id </ StrBot) {
            val obj_table = h(IdTableLoc)
            val propv_element = obj_table(s_id)
            val v_null = if (propv_element._2 </ AbsentBot || !s_id.isConcrete) Value(NullTop) else ValueBot
            /* imprecise semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, propv_element._1._1._1 + v_null), ctx), (he, ctxe))
          }
          else {
            ((h, ctx), (he, ctxe))
          }
        })),
      ("DOMDocument.getElementsByClassName" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_env = h(PureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* arguments */
          val s_class = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
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
            ((PreHelper.ReturnStore(h_1, PureLocalLoc, propv_element._1._1._1 + v_empty), ctx_1), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        }))
      //case "DOMDocument.adoptNode" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.normalizeDocument" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.renameNode" => ((h, ctx), (he, ctxe))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      ("DOMDocument.createElement" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //val l_r = addrToLoc(addr1, Recent)
          //val l_nodes = addrToLoc(addr2, Recent)
          //val LP1 = AccessHelper.Oldify_def(h, ctx, addr1)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AccessHelper.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 0)))
          //val LP2 = AccessHelper.Oldify_def(h, ctx, addr2)
          val LP2 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AccessHelper.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 1)))

          val s_tag = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))

          // DOMException object with the INVALID_CHARACTER_ERR exception code
          val es = Set(DOMException.INVALID_CHARACTER_ERR)
          val LP3 = DOMHelper.RaiseDOMException_def(es)

          /* imprecise semantics */
          val LP4 = s_tag.getAbsCase match {
            // cause the INVALID_CHARACTER_ERR exception
            case _ if s_tag.isAllNums => LPBot
            case AbsBot => LPBot
            case AbsSingle => //OtherStrSingle(s) =>
              val s = s_tag.getSingle.get
              val tag_name = s.toUpperCase
              if(DOMHelper.isValidHtmlTag(tag_name)) {
                val element_obj_proplist = DOMElement.getInsList(PropValue(ObjectValue(AbsString.alpha(tag_name), F, T, T))):::DOMHelper.getInsList(tag_name)
                //val LP4_1 = element_obj_proplist.foldLeft(LPBot)((lpset, prop) => lpset + (l_r, prop._1))
                val LP4_1 = element_obj_proplist.foldLeft(LPBot)((lpset, prop) =>
                  lpset ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 0),Recent), prop._1)))
                // 'childNodes' update
                val childNodes_list = DOMNodeList.getInsList(0)
                //val LP4_2 = childNodes_list.foldLeft(LPBot)((lpset, prop) => lpset + (l_nodes, prop._1))
                val LP4_2 = childNodes_list.foldLeft(LPBot)((lpset, prop) =>
                  lpset ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 1),Recent), prop._1)))
                //LP4_1 ++ LP4_2 + (l_r, "childNodes")
                LP4_1 ++ LP4_2 ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 0),Recent), "childNodes"))
              }
              // An invalid tag name causes the INVALID_CHARACTER_ERR exception
              else
                LPBot

            // may cause the INVALID_CHARACTER_ERR exception
            case _ => //StrTop | OtherStr =>
              val element_obj_proplist = HTMLTopElement.default_getInsList
              val LP4_1 = element_obj_proplist.foldLeft(LPBot)((lpset, prop) =>
                lpset ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 0),Recent), prop._1)))
              // 'childNodes' update
              val childNodes_list = DOMNodeList.getInsList(0)
              val LP4_2 = childNodes_list.foldLeft(LPBot)((lpset, prop) =>
                lpset ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 1),Recent), prop._1)))
              //LP4_1 ++ LP4_2 + (l_r, "childNodes")
              LP4_1 ++ LP4_2 ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 0),Recent), "childNodes"))

          }
          LP1 ++ LP2 ++ LP3 ++ LP4 + (SinglePureLocalLoc, "@return")
        })),
      //case "DOMDocument.createDocumentFragment" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createTextNode" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createComment" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createCDATASection" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createProcessingInstruction" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createAttribute" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createEntityReference" => ((h, ctx), (he, ctxe))
      ("DOMDocument.getElementsByTagName" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      //case "DOMDocument.importNode" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createElementNS" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createAttributeNS" => ((h, ctx), (he, ctxe))
      ("DOMDocument.getElementsByTagNameNS" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMDocument.getElementById" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMDocument.getElementsByClassName" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
      //case "DOMDocument.adoptNode" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.normalizeDocument" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.renameNode" => ((h, ctx), (he, ctxe))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      ("DOMDocument.createElement" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //val l_r = addrToLoc(addr1, Recent)
          //val l_nodes = addrToLoc(addr2, Recent)
          //val LP1 = AccessHelper.Oldify_use(h, ctx, addr1)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AccessHelper.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 0)))
          //val LP2 = AccessHelper.Oldify_use(h, ctx, addr2)
          val LP2 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AccessHelper.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 1)))

          val s_tag = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val LP3 = getArgValue_use(h, ctx, args, "0")

          // DOMException object with the INVALID_CHARACTER_ERR exception code
          val es = Set(DOMException.INVALID_CHARACTER_ERR)
          val LP4 = DOMHelper.RaiseDOMException_use(es)
          LP1 ++ LP2 ++ LP3 ++ LP4 + (SinglePureLocalLoc, "@return")
        })),
      /* imprecise semantics */
      //case "DOMDocument.createDocumentFragment" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createTextNode" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createComment" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createCDATASection" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createProcessingInstruction" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createAttribute" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createEntityReference" => ((h, ctx), (he, ctxe))
      ("DOMDocument.getElementsByTagName" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          /* arguments */
          val s_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          getArgValue_use(h, ctx, args, "0") ++ AccessHelper.absPair(h, TagTableLoc, s_name) + (SinglePureLocalLoc, "@return")
        })),

      //case "DOMDocument.importNode" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createElementNS" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.createAttributeNS" => ((h, ctx), (he, ctxe))
      ("DOMDocument.getElementsByTagNameNS" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val s_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1") ++
            AccessHelper.absPair(h, TagTableLoc, s_name) + (SinglePureLocalLoc, "@return")
        })),
      ("DOMDocument.getElementById" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val s_id = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          getArgValue_use(h, ctx, args, "0") ++ AccessHelper.absPair(h, IdTableLoc, s_id) + (SinglePureLocalLoc, "@return")
        })),
      ("DOMDocument.getElementsByClassName" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val s_class = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          getArgValue_use(h, ctx, args, "0") ++ AccessHelper.absPair(h, ClassTableLoc, s_class) + (SinglePureLocalLoc, "@return")
        }))

      //case "DOMDocument.adoptNode" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.normalizeDocument" => ((h, ctx), (he, ctxe))
      //case "DOMDocument.renameNode" => ((h, ctx), (he, ctxe))
    )
  }
  /* instance */

  /* list of properties in the instance object */
  override def getInsList(node: Node): List[(String, PropValue)] = node match {
    case d: Document =>
      val xmlEncoding = d.getXmlEncoding;
      // This instance object has all properties of the Node object
      DOMNode.getInsList(node) ++ List(
        // Introduced in DOM Level 3
        ("inputEncoding",   PropValue(ObjectValue(AbsString.alpha(d.getInputEncoding), F, T, T))),
        ("xmlEncoding",   PropValue(ObjectValue(AbsString.alpha(if(xmlEncoding!=null) xmlEncoding else ""), F, T, T))),
        ("xmlStandalone",   PropValue(ObjectValue((if(d.getXmlStandalone==true) T else F), T, T, T))),
        ("xmlVersion",   PropValue(ObjectValue(AbsString.alpha(d.getXmlVersion), T, T, T))),
        ("strictErrorChecking",   PropValue(ObjectValue((if(d.getStrictErrorChecking==true) T else F), F, T, T))),
        ("documentURI",   PropValue(ObjectValue(AbsString.alpha(DOMHelper.getDocumentURI), F, T, T))),
        // WHATWG HTML5 : location object
        ("location",   PropValue(ObjectValue(DOMLocation.getInstance.get, F, T, T))),
        
        ("defaultView", PropValue(ObjectValue(Value(GlobalLoc), F, T, T))),
        ("readyState", PropValue(ObjectValue(OtherStr, F, T, T))),
        ("domain", PropValue(ObjectValue(OtherStr, T, T, T))),
        // WHATWG DOM
        ("head", PropValue(ObjectValue(NullTop, F, T, T))),
        ("lastModified", PropValue(ObjectValue(StrTop, F, T, T))),
        ("characterSet", PropValue(ObjectValue(OtherStr, F, T, T))),
        ("URL", PropValue(ObjectValue(OtherStr, F, T, T))),
        // DOM event
        ("onselectstart", PropValue(ObjectValue(Value(NullTop), T, T, T))),
        ("onreadystatechange", PropValue(ObjectValue(Value(NullTop), T, T, T))),
        // DOM Style
        ("styleSheets", PropValue(ObjectValue(StyleSheetList.loc_ins, F, T, T))),
        // Non-standard
        ("webkitVisibilityState", PropValue(ObjectValue(OtherStr, T, T, T)))
      )
    // 'documentElement' is updated after the HTMLHtmlElement node is created
    // TODO: 'implementation' in DOM Level 1, 'doctype' in DOM Level 3
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot be an instance of Document.")
      List()
    }
  }
}
