/*******************************************************************************
    Copyright (c) 2013-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml5

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr}
import kr.ac.kaist.jsaf.analysis.typing.{ControlPoint, Helper, PreHelper, Semantics}
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import scala.Some
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

// Modeled based on WHATWG HTML Living Standard 
// Section 11.2.1 The Storage Interface.
object Storage extends DOM {
  private val name = "Storage"

  /* predefined locations */
  val loc_ins = newSystemRecentLoc(name + "Ins")
  // for window.sessionStorage
  val loc_ins2 = newSystemRecentLoc(name + "Ins2")
  val loc_proto = newSystemRecentLoc(name + "Proto")

  /* instant object */
  private val prop_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    // property
    ("length", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, F, F)))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, F, F)))),
    ("@default_other", AbsConstValue(PropValue(ObjectValue(Value(StrTop), F, F, F))))
  )

  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    // API
    ("key",   AbsBuiltinFunc("Storage.key", 1)),
    ("getItem",   AbsBuiltinFunc("Storage.getItem", 1)),
    ("setItem",   AbsBuiltinFunc("Storage.setItem", 2)),
    ("removeItem",   AbsBuiltinFunc("Storage.removeItem", 1)),
    ("clear",   AbsBuiltinFunc("Storage.clear", 0))
  )

  /* no constructor */
  /* initial property list */
  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_ins, prop_ins), (loc_ins2, prop_ins), (loc_proto, prop_proto)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("Storage.key" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val n_index = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (n_index </ NumBot) {
            val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
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
        })),
      ("Storage.getItem" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val key = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (key </ StrBot) {
            val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
            val value = lset_this.foldLeft[AbsString](StrBot)((n, l) =>
              n + Helper.toString(Helper.toPrimitive_better(h, Helper.Proto(h, l, key))))
            val v_null = if(value <= StrBot) Value(NullTop) else ValueBot
            ((Helper.ReturnStore(h, Value(value) + v_null), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("Storage.setItem" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val key = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val value = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          if (key </ StrBot && value </ StrBot) {
            val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
            val h_1 = lset_this.foldLeft(h)((_h, l) =>
              Helper.PropStore(_h, l, key, Value(value)))
            ((Helper.ReturnStore(h_1, Value(UndefTop)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("Storage.removeItem" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val key = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (key </ StrBot) {
            val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
            val h_1 = lset_this.foldLeft(h)((_h, l) =>
              Helper.Delete(_h, l, key)._1)
            ((Helper.ReturnStore(h_1, Value(UndefTop)), ctx), (he, ctxe))
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
  def getInstance(): Option[Loc] = Some (loc_ins)
}
