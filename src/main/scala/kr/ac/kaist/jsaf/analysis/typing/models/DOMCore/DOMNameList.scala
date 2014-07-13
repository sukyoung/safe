/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMCore

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing.{Semantics, ControlPoint, Helper, PreHelper}
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object DOMNameList extends DOM {
  private val name = "NameList"

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
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("getName",         AbsBuiltinFunc("DOMNameList.getName", 1)),
    ("getNamespaceURI", AbsBuiltinFunc("DOMNameList.getNamespaceURI", 1)),
    ("contains",        AbsBuiltinFunc("DOMNameList.contains", 1)),
    ("containsNS",      AbsBuiltinFunc("DOMNameList.containsNS", 2))
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
      ("DOMNameList.getName" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val n_index = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (n_index </ NumBot)
          /* imprecise semantic */
            ((Helper.ReturnStore(h, Value(StrTop) + Value(NullTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNameList.getNamespaceURI" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val n_index = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (n_index </ NumBot)
          /* imprecise semantic */
            ((Helper.ReturnStore(h, Value(StrTop) + Value(NullTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNameList.contains" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val s_str = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (s_str </ StrBot)
          /* imprecise semantic */
            ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMNameList.containsNS" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val s_uri = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val s_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          if (s_uri </ StrBot || s_uri </ s_name)
          /* imprecise semantic */
            ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("DOMNameList.getName" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val n_index = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          if (n_index </ NumBot)
          /* imprecise semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(StrTop) + Value(NullTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNameList.getNamespaceURI" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val n_index = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          if (n_index </ NumBot)
          /* imprecise semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(StrTop) + Value(NullTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNameList.contains" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val s_str = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          if (s_str </ StrBot)
          /* imprecise semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(BoolTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMNameList.containsNS" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val s_uri = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          val s_name = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "1", PureLocalLoc)))
          if (s_uri </ StrBot || s_uri </ s_name)
          /* imprecise semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(BoolTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        }))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      ("DOMNameList.getName" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNameList.getNamespaceURI" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNameList.contains" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMNameList.containsNS" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      ("DOMNameList.getName" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + (SinglePureLocalLoc, "@return")
        })),
      ("DOMNameList.getNamespaceURI" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + (SinglePureLocalLoc, "@return")
        })),
      ("DOMNameList.contains" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + (SinglePureLocalLoc, "@return")
        })),
      ("DOMNameList.containsNS" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1") + (SinglePureLocalLoc, "@return")
        }))
    )
  }

  /* instance */
  //def instantiate() = Unit // not yet implemented
  // intance of DOMNameList should have 'length' property
}
