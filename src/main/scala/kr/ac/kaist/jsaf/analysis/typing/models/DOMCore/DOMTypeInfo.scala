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

object DOMTypeInfo extends DOM {
  private val name = "TypeInfo"

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
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("DERIVATION_RESTRICTION", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x00000001), F, T, T)))),
    ("DERIVATION_EXTENSION",   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x00000002), F, T, T)))),
    ("DERIVATION_UNION",       AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x00000004), F, T, T)))),
    ("DERIVATION_LIST",        AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0x00000008), F, T, T))))
  )

  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("isDerivedFrom", AbsBuiltinFunc("DOMTypeInfo.isDerivedFrom", 3))
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
      ("DOMTypeInfo.isDerivedFrom" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val s_name   = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val s_ns     = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          val n_method = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "2")))
          if (s_name </StrBot || s_ns </ StrBot || n_method </ NumBot)
          /* imprecise semantic */
            ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("DOMTypeInfo.isDerivedFrom" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val s_name   = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          val s_ns     = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "1", PureLocalLoc)))
          val n_method = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "2", PureLocalLoc)))
          if (s_name </StrBot || s_ns </ StrBot || n_method </ NumBot)
          /* imprecise semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(BoolTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        }))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      ("DOMTypeInfo.isDerivedFrom" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      ("DOMTypeInfo.isDerivedFrom" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1") ++
            getArgValue_use(h, ctx, args, "2") + (SinglePureLocalLoc, "@return")
        }))
    )
  }

  /* instance */
  //def instantiate() = Unit // not yet implemented
  // intance of DOMTypeInfo should have 'typeName', 'typeNamespace' property
}
