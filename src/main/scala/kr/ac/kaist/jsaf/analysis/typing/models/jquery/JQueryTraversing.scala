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
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc

object JQueryTraversing extends ModelData {

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("add",          AbsBuiltinFunc("jQuery.prototype.add", 2)),
    ("addBack",      AbsBuiltinFunc("jQuery.prototype.addBack", 1)),
    ("andSelf",      AbsBuiltinFunc("jQuery.prototype.andSelf", 1)),
    ("children",     AbsBuiltinFunc("jQuery.prototype.children", 2)),
    ("closest",      AbsBuiltinFunc("jQuery.prototype.closest", 2)),
    ("contents",     AbsBuiltinFunc("jQuery.prototype.contents", 2)),
    ("each",         AbsBuiltinFunc("jQuery.prototype.each", 2)),
    ("end",          AbsBuiltinFunc("jQuery.prototype.end", 0)),
    ("eq",           AbsBuiltinFunc("jQuery.prototype.eq", 1)),
    ("filter",       AbsBuiltinFunc("jQuery.prototype.filter", 1)),
    ("find",         AbsBuiltinFunc("jQuery.prototype.find", 1)),
    ("first",        AbsBuiltinFunc("jQuery.prototype.first", 0)),
    ("has",          AbsBuiltinFunc("jQuery.prototype.has", 1)),
    ("is",           AbsBuiltinFunc("jQuery.prototype.is", 1)),
    ("last",         AbsBuiltinFunc("jQuery.prototype.last", 0)),
    ("map",          AbsBuiltinFunc("jQuery.prototype.map", 1)),
    ("next",         AbsBuiltinFunc("jQuery.prototype.next", 2)),
    ("nextAll",      AbsBuiltinFunc("jQuery.prototype.nextAll", 2)),
    ("nextUntil",    AbsBuiltinFunc("jQuery.prototype.nextUntil", 2)),
    ("not",          AbsBuiltinFunc("jQuery.prototype.not", 1)),
    ("offsetParent", AbsBuiltinFunc("jQuery.prototype.offsetParent", 0)),
    ("parent",       AbsBuiltinFunc("jQuery.prototype.parent", 2)),
    ("parents",      AbsBuiltinFunc("jQuery.prototype.parents", 2)),
    ("parentsUntil", AbsBuiltinFunc("jQuery.prototype.parentsUntil", 2)),
    ("prev",         AbsBuiltinFunc("jQuery.prototype.prev", 2)),
    ("prevAll",      AbsBuiltinFunc("jQuery.prototype.prevAll", 2)),
    ("prevUntil",    AbsBuiltinFunc("jQuery.prototype.prevUntil", 2)),
    ("siblings",     AbsBuiltinFunc("jQuery.prototype.siblings", 2)),
    ("slice",        AbsBuiltinFunc("jQuery.prototype.slice", 0))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (JQuery.ProtoLoc, prop_proto)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      "jQuery.prototype.children" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* new addr */
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* new loc */
          val l_ret = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)

          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2

          // ignore filtering, val v_arg = getArgValue(h, ctx, args, "0")

          val lset_child = lset_this.foldLeft(LocSetBot)((ls1, l1) => {
            val n_len = Helper.Proto(h_1, l1, AbsString.alpha("length"))._1._4
            n_len.getSingle match {
              case Some(len) =>
                val lset_elem = (0 until len.toInt).foldLeft(LocSetBot)((ls, i) =>
                  ls ++ Helper.Proto(h, l1, AbsString.alpha(i.toString))._2
                )
                val lset_children = lset_elem.foldLeft(LocSetBot)((ls2, l2) => {
                  val lset_ns = Helper.Proto(h_1, l2, AbsString.alpha("childNodes"))._2
                  ls2 ++ lset_ns.foldLeft(LocSetBot)((ls3, l3) => {
                    ls3 ++ Helper.Proto(h_1, l3, NumStr)._2
                  })
                })
                ls1 ++ lset_children
              case None =>
                if (n_len </ NumBot) {
                  val lset_elem = Helper.Proto(h_1, l1, NumStr)._2
                  val lset_children = lset_elem.foldLeft(LocSetBot)((ls2, l2) => {
                    val lset_ns = Helper.Proto(h, l2, AbsString.alpha("childNodes"))._2
                    ls2 ++ lset_ns.foldLeft(LocSetBot)((ls3, l3) => {
                      ls3 ++ Helper.Proto(h_1, l3, NumStr)._2
                    })
                  })
                  ls1 ++ lset_children
                }
                else
                  ls1
            }
          })

          if (!lset_child.isEmpty) {
            val o_new = JQueryHelper.NewJQueryObject(UInt).update(NumStr, PropValue(ObjectValue(Value(lset_child), T, T, T)))
            val h_2 = h_1.update(l_ret, o_new)
            val h_3 = JQueryHelper.pushStack(h_2, lset_this, LocSet(l_ret))
            ((Helper.ReturnStore(h_3, Value(l_ret)), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
      ("jQuery.prototype.find" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* new addr */
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* new loc */
          val l_ret = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)

          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2

          // ignore HTMLelement, jQuery obejct
          val s_selector = getArgValue(h, ctx, args, "0")._1._5

          /* imprecise semantic */
          val lset_elems = lset_this.foldLeft(LocSetBot)((ls, l) => ls ++ Helper.Proto(h, l, NumStr)._2)
          val lset_find = lset_elems.foldLeft(LocSetBot)((ls, l) => ls ++ DOMHelper.querySelectorAll(h, l, s_selector))


          if (!lset_this.isEmpty) {
            val o_jq =
              if (!lset_find.isEmpty)
                JQueryHelper.NewJQueryObject(UInt).update(NumStr, PropValue(ObjectValue(Value(lset_find),T,T,T)))
              else
                JQueryHelper.NewJQueryObject
            val h_2 = h_1.update(l_ret, o_jq)
            val h_3 = JQueryHelper.pushStack(h_2, lset_this, LocSet(l_ret))
            ((Helper.ReturnStore(h_3, Value(l_ret)), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.first" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* new addr */
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* new loc */
          val l_ret = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)

          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2

          val lset_elems = lset_this.foldLeft(LocSetBot)((ls, l) => ls ++ Helper.Proto(h, l, AbsString.alpha("0"))._2)

          if (!lset_this.isEmpty) {
            val o_jq =
              if (!lset_elems.isEmpty)
                JQueryHelper.NewJQueryObject(1).update(AbsString.alpha("0"), PropValue(ObjectValue(Value(lset_elems),T,T,T)))
              else
                JQueryHelper.NewJQueryObject
            val h_2 = h_1.update(l_ret, o_jq)
            val h_3 = JQueryHelper.pushStack(h_2, lset_this, LocSet(l_ret))
            ((Helper.ReturnStore(h_3, Value(l_ret)), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),

      ("jQuery.prototype.is" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          // imprecise semantic
          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* argument */
          val v_arg = getArgValue(h, ctx, args, "0")
          if (v_arg </ ValueBot)
            ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      "jQuery.prototype.next" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* new addr */
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* new loc */
          val l_ret = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)

          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2

          // ignore filtering, val v_arg = getArgValue(h, ctx, args, "0")

          val lset_next = lset_this.foldLeft(LocSetBot)((ls1, l1) => {
            val n_len = Helper.Proto(h_1, l1, AbsString.alpha("length"))._1._4
            n_len.getSingle match {
              case Some(len) =>
                val lset_elem = (0 until len.toInt).foldLeft(LocSetBot)((ls, i) =>
                  ls ++ Helper.Proto(h, l1, AbsString.alpha(i.toString))._2
                )
                val lset_sibling = lset_elem.foldLeft(LocSetBot)((ls2, l2) =>
                  ls2 ++ DOMHelper.getNextElementSibling(h, l2)
                )
                ls1 ++ lset_sibling
              case None =>
                if (n_len </ NumBot) {
                  val lset_elem = Helper.Proto(h_1, l1, NumStr)._2
                  val lset_sibling = lset_elem.foldLeft(LocSetBot)((ls2, l2) =>
                    ls2 ++ DOMHelper.getNextElementSibling(h, l2)
                  )
                  ls1 ++ lset_sibling
                }
                else
                  ls1
            }
          })

          if (!lset_next.isEmpty) {
            val o_new = JQueryHelper.NewJQueryObject(UInt).update(NumStr, PropValue(ObjectValue(Value(lset_next), T, T, T)))
            val h_2 = h_1.update(l_ret, o_new)
            val h_3 = JQueryHelper.pushStack(h_2, lset_this, LocSet(l_ret))
            ((Helper.ReturnStore(h_3, Value(l_ret)), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
      "jQuery.prototype.parent" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* new addr */
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* new loc */
          val l_ret = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)

          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2

          // ignore filtering, val v_arg = getArgValue(h, ctx, args, "0")

          val lset_parent = lset_this.foldLeft(LocSetBot)((ls1, l1) => {
            val n_len = Helper.Proto(h_1, l1, AbsString.alpha("length"))._1._4
            n_len.getSingle match {
              case Some(len) =>
                val lset_elem = (0 until len.toInt).foldLeft(LocSetBot)((ls, i) =>
                  ls ++ Helper.Proto(h, l1, AbsString.alpha(i.toString))._2
                )
                val lset_par = lset_elem.foldLeft(LocSetBot)((ls2, l2) =>
                  ls2 ++ Helper.Proto(h_1, l2, AbsString.alpha("parentNode"))._2
                )
                ls1 ++ lset_par
              case None =>
                if (n_len </ NumBot) {
                  val lset_elem = Helper.Proto(h_1, l1, NumStr)._2
                  val lset_par = lset_elem.foldLeft(LocSetBot)((ls2, l2) =>
                    ls2 ++ Helper.Proto(h_1, l2, AbsString.alpha("parentNode"))._2
                  )
                  ls1 ++ lset_par
                }
                else
                  ls1
            }
          })

          if (!lset_parent.isEmpty) {
            val o_new = JQueryHelper.NewJQueryObject(UInt).update(NumStr, PropValue(ObjectValue(Value(lset_parent), T, T, T)))
            val h_2 = h_1.update(l_ret, o_new)
            val h_3 = JQueryHelper.pushStack(h_2, lset_this, LocSet(l_ret))
            ((Helper.ReturnStore(h_3, Value(l_ret)), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
      "jQuery.prototype.parents" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* new addr */
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* new loc */
          val l_ret = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)

          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2

          // ignore filtering, val v_arg = getArgValue(h, ctx, args, "0")

          val lset_parents = lset_this.foldLeft(LocSetBot)((ls1, l1) => {
            val n_len = Helper.Proto(h_1, l1, AbsString.alpha("length"))._1._4
            n_len.getSingle match {
              case Some(len) =>
                val lset_elem = (0 until len.toInt).foldLeft(LocSetBot)((ls, i) =>
                  ls ++ Helper.Proto(h, l1, AbsString.alpha(i.toString))._2
                )
                val lset_par = lset_elem.foldLeft(LocSetBot)((ls2, l2) => {
                  val lset_parent = Helper.Proto(h_1, l2, AbsString.alpha("parentNode"))._2
                  val lset = lset_parent.foldLeft(LocSetBot)((ls3, l3) => ls3 ++ DOMHelper.getParents(h_1, l3))
                  ls2 ++ lset
                })
                ls1 ++ lset_par
              case None =>
                if (n_len </ NumBot) {
                  val lset_elem = Helper.Proto(h_1, l1, NumStr)._2
                  val lset_par = lset_elem.foldLeft(LocSetBot)((ls2, l2) => {
                    val lset_parent = Helper.Proto(h_1, l2, AbsString.alpha("parentNode"))._2
                    val lset = lset_parent.foldLeft(LocSetBot)((ls3, l3) => ls3 ++ DOMHelper.getParents(h_1, l3))
                    ls2 ++ lset
                  })
                  ls1 ++ lset_par
                }
                else
                  ls1
            }
          })

          if (!lset_parents.isEmpty) {
            val o_new = JQueryHelper.NewJQueryObject(UInt).update(NumStr, PropValue(ObjectValue(Value(lset_parents), T, T, T)))
            val h_2 = h_1.update(l_ret, o_new)
            val h_3 = JQueryHelper.pushStack(h_2, lset_this, LocSet(l_ret))
            ((Helper.ReturnStore(h_3, Value(l_ret)), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
      "jQuery.prototype.siblings" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* new addr */
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          /* new loc */
          val l_ret = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)

          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2

          // ignore filtering, val v_arg = getArgValue(h, ctx, args, "0")

          val lset_siblings = lset_this.foldLeft(LocSetBot)((ls1, l1) => {
            val n_len = Helper.Proto(h_1, l1, AbsString.alpha("length"))._1._4
            n_len.getSingle match {
              case Some(len) =>
                val lset_elem = (0 until len.toInt).foldLeft(LocSetBot)((ls, i) =>
                  ls ++ Helper.Proto(h, l1, AbsString.alpha(i.toString))._2
                )
                val lset_parent = lset_elem.foldLeft(LocSetBot)((ls2, l2) =>
                  ls2 ++ Helper.Proto(h_1, l2, AbsString.alpha("parentNode"))._2
                )
                val lset_children = lset_parent.foldLeft(LocSetBot)((ls2, l2) => {
                  val lset_ns = Helper.Proto(h_1, l2, AbsString.alpha("childNodes"))._2
                  ls2 ++ lset_ns.foldLeft(LocSetBot)((ls3, l3) => {
                    ls3 ++ Helper.Proto(h_1, l3, NumStr)._2
                  })
                })
                ls1 ++ lset_children
              case None =>
                if (n_len </ NumBot) {
                  val lset_elem = Helper.Proto(h_1, l1, NumStr)._2
                  val lset_parent = lset_elem.foldLeft(LocSetBot)((ls2, l2) =>
                    ls2 ++ Helper.Proto(h_1, l2, AbsString.alpha("parentNode"))._2
                  )
                  val lset_children = lset_parent.foldLeft(LocSetBot)((ls2, l2) => {
                    val lset_ns = Helper.Proto(h_1, l2, AbsString.alpha("childNodes"))._2
                    ls2 ++ lset_ns.foldLeft(LocSetBot)((ls3, l3) => {
                      ls3 ++ Helper.Proto(h_1, l3, NumStr)._2
                    })
                  })
                  ls1 ++ lset_children
                }
                else
                  ls1
            }
          })

          if (!lset_siblings.isEmpty) {
            val o_new = JQueryHelper.NewJQueryObject(UInt).update(NumStr, PropValue(ObjectValue(Value(lset_siblings), T, T, T)))
            val h_2 = h_1.update(l_ret, o_new)
            val h_3 = JQueryHelper.pushStack(h_2, lset_this, LocSet(l_ret))
            ((Helper.ReturnStore(h_3, Value(l_ret)), ctx_1), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map()
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map()
  }
}
