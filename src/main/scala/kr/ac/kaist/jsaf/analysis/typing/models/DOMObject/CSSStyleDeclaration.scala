/*******************************************************************************
    Copyright (c) 2013-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMObject

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.Semantics
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr}
import kr.ac.kaist.jsaf.analysis.typing.{ControlPoint, Helper, PreHelper}
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.Shell

// Modeled based on W3C DOM Level 2 Style Specification
// www.w3.org/TR/2000/REC-DOM-Level-2-Style-20001113/  
// Chapter 2 Document Object Model CSS
object CSSStyleDeclaration extends DOM {
  private val name = "CSSStyleDeclaration"

  val loc_ins = newSystemRecentLoc(name + "Ins")
  /* predefined locations */
  val loc_ins2 = newSystemRecentLoc(name + "Ins")
  val loc_proto = newSystemRecentLoc(name + "Proto")

  /* instant object */
  private val prop_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("length", AbsConstValue(PropValue(ObjectValue(UInt, F, F, F)))),
    ("cssText", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
    (Str_default_number, AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T))))
    // TODO : "parentRule"
  )

  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    // API
    ("getPropertyValue",   AbsBuiltinFunc("CSSStyleDeclaration.getPropertyValue", 1)),
    ("getPropertyCSSValue",   AbsBuiltinFunc("CSSStyleDeclaration.getPropertyCSSValue", 1)),
    ("removeProperty",   AbsBuiltinFunc("CSSStyleDeclaration.removeProperty", 1)),
    ("getPropertyPriority",   AbsBuiltinFunc("CSSStyleDeclaration.getPropertyPriority", 1)),
    ("setProperty",   AbsBuiltinFunc("CSSStyleDeclaration.setProperty", 3)),
    ("item",   AbsBuiltinFunc("CSSStyleDeclaration.item", 1))
  )

  /* global */
  /* no constructor */
  /* initial property list */
  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = if(Shell.params.opt_Dommodel2) List(
    (loc_proto, prop_proto), (loc_ins, prop_ins))
  else List((loc_proto, prop_proto), (loc_ins2, prop_ins))

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("CSSStyleDeclaration.getPropertyValue" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (name </ StrBot) {
            ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("CSSStyleDeclaration.removeProperty" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (name </ StrBot) {
            ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("CSSStyleDeclaration.getPropertyPriority" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (name </ StrBot) {
            ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("CSSStyleDeclaration.setProperty" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val value = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          val priority = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          if (name </ StrBot && value </ StrBot && priority </ StrBot) {
            ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),

      ("CSSStyleDeclaration.item" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val n_index = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (n_index </ NumBot) {
            val lset_this = h(SinglePureLocalLoc)("@this")._2._2
            val n_length = lset_this.foldLeft[AbsNumber](NumBot)((n, l) =>
              n + Helper.toNumber(Helper.toPrimitive_better(h, Helper.Proto(h, l, AbsString.alpha("length")))))
            val s_index = Helper.toString(PValue(n_index))
            // Returns the indexth item in the collection.
            val v_return = lset_this.foldLeft(ValueBot)((v, l) => v + Helper.Proto(h, l, s_index))
            // If index is greater than or equal to the number of nodes in the list, this returns null.
            val v_null = AbsDomUtils.checkIndex(n_index, n_length)
            ((Helper.ReturnStore(h, v_return + v_null), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }))
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
  override def getInstance(cfg: CFG): Option[Loc] = Some(newRecentLoc())
  def getInsList(): List[(String, PropValue)] = List(
    ("@class", PropValue(AbsString.alpha("Object"))),
    ("@proto", PropValue(ObjectValue(Value(loc_proto), F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    ("length", PropValue(ObjectValue(UInt, F, F, F))),
    ("cssText", PropValue(ObjectValue(StrTop, T, T, T))),
    (Str_default_number, PropValue(ObjectValue(StrTop, T, T, T)))
    // TODO : "parentRule"
  )
}
