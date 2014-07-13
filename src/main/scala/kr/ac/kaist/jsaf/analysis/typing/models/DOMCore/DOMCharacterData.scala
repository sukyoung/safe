/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMCore

import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T, _}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.cfg._
import org.w3c.dom.Node
import org.w3c.dom.CharacterData
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object DOMCharacterData extends DOM {
  private val name = "CharacterData"

  /* predefined locatoins */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")

  /* constructor */
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
    ("substringData", AbsBuiltinFunc("DOMCharacterData.substringData", 2)),
    ("appendData",    AbsBuiltinFunc("DOMCharacterData.appendData", 1)),
    ("insertData",    AbsBuiltinFunc("DOMCharacterData.insertData", 2)),
    ("deleteData",    AbsBuiltinFunc("DOMCharacterData.deleteData", 2)),
    ("replaceData",   AbsBuiltinFunc("DOMCharacterData.replaceData", 3))
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
      ("DOMCharacterData.substringData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* arguments */
          val n_offset = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val n_count  = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          if (n_offset </ NumBot || n_count </ NumBot)
          /* imprecise semantic */
            ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMCharacterData.appendData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val s_arg = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if (s_arg </ StrBot) {
            /* imprecise semantic */
            val h1 = lset_this.foldLeft(h)((hh, l) => {
              val hhh = Helper.PropStore(hh, l, AbsString.alpha("data"), Value(StrTop))
              Helper.PropStore(hhh, l, AbsString.alpha("length"), Value(UInt))
            })
            ((Helper.ReturnStore(h1, Value(UndefTop)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMCharacterData.insertData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val n_offset = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val s_arg    = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          if (s_arg </ StrBot || n_offset </ NumBot) {
            /* imprecise semantic */
            val h1 = lset_this.foldLeft(h)((hh, l) => {
              val hhh = Helper.PropStore(hh, l, AbsString.alpha("data"), Value(StrTop))
              Helper.PropStore(hhh, l, AbsString.alpha("length"), Value(UInt))
            })
            ((Helper.ReturnStore(h1, Value(UndefTop)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMCharacterData.deleteData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val n_offset = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val n_count  = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          if (n_offset </ NumBot || n_count </ NumBot) {
            /* imprecise semantic */
            val h1 = lset_this.foldLeft(h)((hh, l) => {
              val hhh = Helper.PropStore(hh, l, AbsString.alpha("data"), Value(StrTop))
              Helper.PropStore(hhh, l, AbsString.alpha("length"), Value(UInt))
            })
            ((Helper.ReturnStore(h1, Value(UndefTop)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("DOMCharacterData.replaceData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* arguments */
          val n_offset = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          val n_count  = Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "1")))
          val s_arg    = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "2")))
          if (n_offset </ NumBot || n_count </ NumBot || s_arg </ StrBot) {
            /* imprecise semantic */
            val h1 = lset_this.foldLeft(h)((hh, l) => {
              val hhh = Helper.PropStore(hh, l, AbsString.alpha("data"), Value(StrTop))
              Helper.PropStore(hhh, l, AbsString.alpha("length"), Value(UInt))
            })
            ((Helper.ReturnStore(h1, Value(UndefTop)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("DOMCharacterData.substringData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* arguments */
          val n_offset = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          val n_count  = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "1", PureLocalLoc)))
          if (n_offset </ NumBot || n_count </ NumBot)
          /* imprecise semantic */
            ((PreHelper.ReturnStore(h, PureLocalLoc, Value(StrTop)), ctx), (he, ctxe))
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMCharacterData.appendData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          /* arguments */
          val s_arg = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          if (s_arg </ StrBot) {
            /* imprecise semantic */
            val h1 = lset_this.foldLeft(h)((hh, l) => {
              val hhh = PreHelper.PropStore(hh, l, AbsString.alpha("data"), Value(StrTop))
              PreHelper.PropStore(hhh, l, AbsString.alpha("length"), Value(UInt))
            })
            ((PreHelper.ReturnStore(h1, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMCharacterData.insertData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          /* arguments */
          val n_offset = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          val s_arg    = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "1", PureLocalLoc)))
          if (s_arg </ StrBot || n_offset </ NumBot) {
            /* imprecise semantic */
            val h1 = lset_this.foldLeft(h)((hh, l) => {
              val hhh = PreHelper.PropStore(hh, l, AbsString.alpha("data"), Value(StrTop))
              PreHelper.PropStore(hhh, l, AbsString.alpha("length"), Value(UInt))
            })
            ((PreHelper.ReturnStore(h1, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMCharacterData.deleteData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          /* arguments */
          val n_offset = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          val n_count  = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "1", PureLocalLoc)))
          if (n_offset </ NumBot || n_count </ NumBot) {
            /* imprecise semantic */
            val h1 = lset_this.foldLeft(h)((hh, l) => {
              val hhh = PreHelper.PropStore(hh, l, AbsString.alpha("data"), Value(StrTop))
              PreHelper.PropStore(hhh, l, AbsString.alpha("length"), Value(UInt))
            })
            ((PreHelper.ReturnStore(h1, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        })),
      ("DOMCharacterData.replaceData" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          /* arguments */
          val n_offset = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "0", PureLocalLoc)))
          val n_count  = PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "1", PureLocalLoc)))
          val s_arg    = PreHelper.toString(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, "2", PureLocalLoc)))
          if (n_offset </ NumBot || n_count </ NumBot || s_arg </ StrBot) {
            /* imprecise semantic */
            val h1 = lset_this.foldLeft(h)((hh, l) => {
              val hhh = PreHelper.PropStore(hh, l, AbsString.alpha("data"), Value(StrTop))
              PreHelper.PropStore(hhh, l, AbsString.alpha("length"), Value(UInt))
            })
            ((PreHelper.ReturnStore(h1, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
          }
          else
            ((h, ctx), (he, ctxe))
        }))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      ("DOMCharacterData.substringData" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("DOMCharacterData.appendData" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) => {
            lpset + (l, "data") + (l, "length")
          })
          LP1 + (SinglePureLocalLoc, "@return")
        })),
      ("DOMCharacterData.insertData" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) => {
            lpset + (l, "data") + (l, "length")
          })
          LP1 + (SinglePureLocalLoc, "@return")
        })),
      ("DOMCharacterData.deleteData" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) => {
            lpset + (l, "data") + (l, "length")
          })
          LP1 + (SinglePureLocalLoc, "@return")
        })),
      ("DOMCharacterData.replaceData" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) => {
            lpset + (l, "data") + (l, "length")
          })
          LP1 + (SinglePureLocalLoc, "@return")
        }))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      ("DOMCharacterData.substringData" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val LP1 = getArgValue_use(h, ctx, args, "0")
          val LP2 = getArgValue_use(h, ctx, args, "1")
          LP1 ++ LP2 + (SinglePureLocalLoc, "@return")
        })),
      ("DOMCharacterData.appendData" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val LP1 = getArgValue_use(h, ctx, args, "0")
          val LP2 = lset_this.foldLeft(LPBot)((lpset, l) => {
            lpset + (l, "data") + (l, "length")
          })
          LP1 ++ LP2 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
        })),
      ("DOMCharacterData.insertData" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val LP1 = getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1")
          val LP2 = lset_this.foldLeft(LPBot)((lpset, l) => {
            lpset + (l, "data") + (l, "length")
          })
          LP1 ++ LP2 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
        })),
      ("DOMCharacterData.deleteData" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val LP1 = getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1")
          val LP2 = lset_this.foldLeft(LPBot)((lpset, l) => {
            lpset + (l, "data") + (l, "length")
          })
          LP1 ++ LP2 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
        })),
      ("DOMCharacterData.replaceData" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val LP1 = getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1") ++ getArgValue_use(h, ctx, args, "2")
          val LP2 = lset_this.foldLeft(LPBot)((lpset, l) => {
            lpset + (l, "data") + (l, "length")

          })
          LP1 ++ LP2 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
        }))
    )
  }

  /* instance */
  override def getInstance(cfg: CFG): Option[Loc] = Some(newRecentLoc())
  /* list of properties in the instance object */
  override def getInsList(node: Node): List[(String, PropValue)] = node match {
    case c: CharacterData =>
      // This instance object has all properties of the Node object
      DOMNode.getInsList(node) ++ List(
        // DOM Level 1
        ("data",   PropValue(ObjectValue(AbsString.alpha(c.getData), T, T, T))))
    case _ => {
      System.err.println("* Warning: " + node.getNodeName + " cannot be an instance of CharacterData.")
      List()
    }
  }

  def getInsList(data: PropValue): List[(String, PropValue)] = List(("data", data))
}
