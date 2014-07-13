/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.models.jquery

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper => AH, _}
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG, FunctionId}

object JQueryEffect extends ModelData {
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("animate",     AbsBuiltinFunc("jQuery.prototype.animate", 4)),
    ("delay",       AbsBuiltinFunc("jQuery.prototype.delay", 2)),
    ("fadeIn",      AbsBuiltinFunc("jQuery.prototype.fadeIn", 3)),
    ("fadeOut",     AbsBuiltinFunc("jQuery.prototype.fadeOut", 3)),
    ("fadeTo",      AbsBuiltinFunc("jQuery.prototype.fadeTo", 4)),
    ("fadeToggle",  AbsBuiltinFunc("jQuery.prototype.fadeToggle", 3)),
    ("finish",      AbsBuiltinFunc("jQuery.prototype.finish", 1)),
    ("hide",        AbsBuiltinFunc("jQuery.prototype.hide", 3)),
    ("show",        AbsBuiltinFunc("jQuery.prototype.show", 3)),
    ("slideDown",   AbsBuiltinFunc("jQuery.prototype.slideDown", 3)),
    ("slideToggle", AbsBuiltinFunc("jQuery.prototype.slideToggle", 3)),
    ("slideUp",     AbsBuiltinFunc("jQuery.prototype.slideUp", 3)),
    ("stop",        AbsBuiltinFunc("jQuery.prototype.stop", 3))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (JQuery.ProtoLoc, prop_proto)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    List("animate", "delay", "fadeIn", "fadeOut", "fadeTo", "fadeToggle", "finish", "hide", "show",
      "slideDown", "slideToggle", "slideUp", "stop").foldLeft[Map[String, SemanticFun]](Map())((_m, name) =>
      _m + ("jQuery.prototype." + name -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // do nothing
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          ((Helper.ReturnStore(h, Value(lset_this)), ctx), (he, ctxe))
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    List("animate", "delay", "fadeIn", "fadeOut", "fadeTo", "fadeToggle", "finish", "hide", "show",
      "slideDown", "slideToggle", "slideUp", "stop").foldLeft[Map[String, SemanticFun]](Map())((_m, name) =>
      _m + ("jQuery.prototype." + name -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // do nothing
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(lset_this)), ctx), (he, ctxe))
        }))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    List("animate", "delay", "fadeIn", "fadeOut", "fadeTo", "fadeToggle", "finish", "hide", "show",
      "slideDown", "slideToggle", "slideUp", "stop").foldLeft[Map[String, AccessFun]](Map())((_m, name) =>
      _m + ("jQuery.prototype." + name -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    List("animate", "delay", "fadeIn", "fadeOut", "fadeTo", "fadeToggle", "finish", "hide", "show",
      "slideDown", "slideToggle", "slideUp", "stop").foldLeft[Map[String, AccessFun]](Map())((_m, name) =>
      _m + ("jQuery.prototype." + name -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return")) + (SinglePureLocalLoc, "@this")
        }))
    )
  }
}
