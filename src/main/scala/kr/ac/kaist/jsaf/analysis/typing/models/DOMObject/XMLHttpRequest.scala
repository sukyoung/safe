/*******************************************************************************
    Copyright (c) 2013-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMObject

import kr.ac.kaist.jsaf.analysis.typing.Semantics
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr}
import kr.ac.kaist.jsaf.analysis.typing.{ControlPoint, Helper}
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

// Modeled based on Mozilla DOM Reference
// https://developer.mozilla.org/en-US/docs/Web/API/XMLHttpRequest
object XMLHttpRequest extends DOM {
  private val name = "XMLHttpRequest"

  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  val loc_ins = newSystemRecentLoc(name + "Ins")

  /* constructor */
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("@construct", AbsInternalFunc("XMLHttpRequest.constructor")), 
    ("length", AbsConstValue(PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, F, F)))),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    // Field
    ("UNSENT", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0), F, T, T)))),
    ("OPENED", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, T, T)))),
    ("HEADERS_REICEIVED", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(2), F, T, T)))),
    ("LOADING", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(3), F, T, T)))),
    ("DONE", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(4), F, T, T)))),
    // API
    ("abort",   AbsBuiltinFunc("XMLHttpRequest.abort", 0)),
    ("getAllResponseHeaders",   AbsBuiltinFunc("XMLHttpRequest.getAllResponseHeaders", 0)),
    ("getResponseHeader",   AbsBuiltinFunc("XMLHttpRequest.getResponseHeader", 1)),
    ("open",   AbsBuiltinFunc("XMLHttpRequest.open", 5)),
    ("overrideMimeType",   AbsBuiltinFunc("XMLHttpRequest.overrideMimeType", 1)),
    ("send",   AbsBuiltinFunc("XMLHttpRequest.send", 1)),
    ("setRequestHeader",   AbsBuiltinFunc("XMLHttpRequest.setRequestHeader", 2))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
  )
  /* no constructor */
  /* initial property list */
  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      // constructor
      ("XMLHttpRequest.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {           
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
         
          // create a new XMLHttpRequest Object
          val h_1 = lset_this.foldLeft(h)((_h, l) => {
            val newobj = default_getInsList.foldLeft(Helper.NewObject(loc_proto))((obj, prop) => 
              obj.update(prop._1, prop._2)
            )
            _h.update(l, newobj)
          })
          ((Helper.ReturnStore(h_1, Value(lset_this)), ctx), (he, ctxe)) 
        }
      )),
      ("XMLHttpRequest.abort" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {           
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))        
        }
      )),
      ("XMLHttpRequest.getAllResponseHeaders" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {           
          ((Helper.ReturnStore(h, Value(StrTop) + Value(NullTop)), ctx), (he, ctxe))        
        }
      )),
      ("XMLHttpRequest.getResponseHeader" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {           
          /* arguments */
          val header = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if(header </ StrBot)
            ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))        
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }
      )),
      ("XMLHttpRequest.open" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {           
          /* arguments */
          val method = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val url = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          if(method </ StrBot && method </ StrBot) {
            val lset_this = h(SinglePureLocalLoc)("@this")._2._2
            val h_1 = lset_this.foldLeft(h)((_h, l) => {
              val newobj = _h(l).update(
                 "readyState", PropValue(ObjectValue(AbsNumber.alpha(1), T, T, T))).update(
                 "responseText", PropValue(ObjectValue(Value(StrTop) + Value(NullTop), F, T, T))).update(
                 "status", PropValue(ObjectValue(UInt, F, T, T))).update(
                 "statusText", PropValue(ObjectValue(StrTop, F, T, T)))
               _h.update(l, newobj)
                                 
            })
            ((Helper.ReturnStore(h_1, Value(UndefTop)), ctx), (he, ctxe))  
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }
      )),
      ("XMLHttpRequest.overrideMimeType" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {           
          /* arguments */
          val mimeType = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if(mimeType </ StrBot)
            ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))        
          else
            ((HeapBot, ContextBot), (he, ctxe))

          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))        
        }
      )),
      ("XMLHttpRequest.send" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {           
          /* arguments */
          val method = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val url = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          if(method </ StrBot && method </ StrBot) {
            val lset_this = h(SinglePureLocalLoc)("@this")._2._2
            val h_1 = lset_this.foldLeft(h)((_h, l) => {
              val newobj = _h(l).update(
                 "readyState", PropValue(ObjectValue(UInt, T, T, T))).update(
                 "responseText", PropValue(ObjectValue(Value(StrTop) + Value(NullTop), F, T, T))).update(
                 "status", PropValue(ObjectValue(UInt, F, T, T))).update(
                 "statusText", PropValue(ObjectValue(StrTop, F, T, T)))
               _h.update(l, newobj)
                                 
            })
            ((Helper.ReturnStore(h_1, Value(UndefTop)), ctx), (he, ctxe))  
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))          
        }
      )),
      ("XMLHttpRequest.setRequestHeader" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {           
          /* arguments */
          val header = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val value = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          if(header </ StrBot && value </ StrBot) {
            ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))  
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }
      ))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
    )
  }

  /* instance */
   
  def getInsList(readyState: PropValue, responseText: PropValue, responseXML: PropValue, status: PropValue,
                 statusText: PropValue, timeout: PropValue, withCredentials: PropValue): List[(String, PropValue)] = List(
    ("@class", PropValue(AbsString.alpha("Object"))),
    ("@proto", PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    ("readyState", readyState),
    ("responseText", responseText),
    ("responseXML", responseXML),
    ("status", status),
    ("statusText", statusText),
    ("timeout", timeout),
    ("withCredentials", withCredentials)
  )
  
  override def default_getInsList(): List[(String, PropValue)] = {
    val readyState = PropValue(ObjectValue(AbsNumber.alpha(0), T, T, T))
    val responseText = PropValue(ObjectValue(AbsString.alpha(""), F, T, T))
    val responseXML = PropValue(ObjectValue(NullTop, F, T, T))
    val status = PropValue(ObjectValue(AbsNumber.alpha(0), T, T, T))
    val statusText = PropValue(ObjectValue(AbsString.alpha(""), F, T, T))
    val timeout = PropValue(ObjectValue(UInt, T, T, T))
    val withCredentials = PropValue(ObjectValue(BoolFalse, T, T, T))
    getInsList(readyState, responseText, responseXML, status, statusText, timeout, withCredentials)
  }

}
