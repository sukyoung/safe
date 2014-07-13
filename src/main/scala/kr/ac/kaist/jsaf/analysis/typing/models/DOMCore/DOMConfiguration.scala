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
import kr.ac.kaist.jsaf.analysis.typing.{Semantics, ControlPoint, Helper, PreHelper}
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object DOMConfiguration extends DOM {
  private val name = "DOMConfiguration"

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
    ("setParameter",    AbsBuiltinFunc("DOMConfiguration.setParameter", 2)),
    ("getParameter",    AbsBuiltinFunc("DOMConfiguration.getParameter", 1)),
    ("canSetParameter", AbsBuiltinFunc("DOMConfiguration.canSetParameter", 2))
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
      ("DOMConfiguration.setParameter" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val s_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val lset_value  = getArgValue(h, ctx, args, "1")._2
          if (s_name </ StrBot || !lset_value.isEmpty)
          /* imprecise semantic */
          // do nothing???
            ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      // case "DOMConfiguration.getParameter" =>
      ("DOMConfiguration.canSetParameter" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val s_name = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val lset_value  = getArgValue(h, ctx, args, "1")._2
          if (s_name </ StrBot || !lset_value.isEmpty)
          /* imprecise semantic */
            ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("DOMConfiguration.setParameter" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val s_name = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          val lset_value  = getArgValue_pre(h, ctx, args, "1", PureLocalLoc)._2
          if (s_name </ StrBot || !lset_value.isEmpty)
          /* imprecise semantic */
          // do nothing???
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      // case "DOMConfiguration.getParameter" =>
      ("DOMConfiguration.canSetParameter" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val s_name = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          val lset_value  = getArgValue_pre(h, ctx, args, "1", PureLocalLoc)._2
          if (s_name </ StrBot || !lset_value.isEmpty)
          /* imprecise semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(BoolTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        }))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      ("DOMConfiguration.setParameter" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      // case "DOMConfiguration.getParameter" =>
      ("DOMConfiguration.canSetParameter" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      ("DOMConfiguration.setParameter" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val LP1 = getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1")
          LP1 + (SinglePureLocalLoc, "@return")
        })),
      // case "DOMConfiguration.getParameter" =>
      ("DOMConfiguration.canSetParameter" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val LP1 = getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1")
          LP1 + (SinglePureLocalLoc, "@return")
        }))
    )
  }

  /* instance */
  //def instantiate() = Unit // not yet implemented
  // intance of DOMConfiguration should have 'parameterNames'  property
}
