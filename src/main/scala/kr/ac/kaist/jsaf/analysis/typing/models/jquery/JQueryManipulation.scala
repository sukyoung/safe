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
import scala.Some
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG}
import scala.Some
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.DOMCore.DOMNodeList

object JQueryManipulation extends ModelData {

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("after",        AbsBuiltinFunc("jQuery.prototype.after", 0)),
    ("append",       AbsBuiltinFunc("jQuery.prototype.append", 0)),
    ("appendTo",     AbsBuiltinFunc("jQuery.prototype.appendTo", 1)),
    ("before",       AbsBuiltinFunc("jQuery.prototype.before", 0)),
    ("clone",        AbsBuiltinFunc("jQuery.prototype.clone", 2)),
    ("detach",       AbsBuiltinFunc("jQuery.prototype.detach", 1)),
    ("empty",        AbsBuiltinFunc("jQuery.prototype.empty", 0)),
    ("insertAfter",  AbsBuiltinFunc("jQuery.prototype.insertAfter", 1)),
    ("insertBefore", AbsBuiltinFunc("jQuery.prototype.insertBefore", 1)),
    ("prepend",      AbsBuiltinFunc("jQuery.prototype.prepend", 0)),
    ("prependTo",    AbsBuiltinFunc("jQuery.prototype.prependTo", 1)),
    ("remove",       AbsBuiltinFunc("jQuery.prototype.remove", 2)),
    ("replaceAll",   AbsBuiltinFunc("jQuery.prototype.replaceAll", 1)),
    ("replaceWith",  AbsBuiltinFunc("jQuery.prototype.replaceWith", 1)),
    ("text",         AbsBuiltinFunc("jQuery.prototype.text", 1)),
    ("unwrap",       AbsBuiltinFunc("jQuery.prototype.unwrap", 0)),
    ("wrap",         AbsBuiltinFunc("jQuery.prototype.wrap", 1)),
    ("wrapAll",      AbsBuiltinFunc("jQuery.prototype.wrapAll", 1)),
    ("wrapInner",    AbsBuiltinFunc("jQuery.prototype.wrapInner", 1))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (JQuery.ProtoLoc, prop_proto)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("jQuery.prototype.after" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {

          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* 1st argument */
          val v_arg1 = getArgValue(h, ctx, args, "0")
          val lset_arg = v_arg1._2

          // ignore function, HTMLString, 2nd argument
          if(v_arg1._1._5 </ StrBot){
            System.err.println("* Warning: jQuery.prototype.after(HTMLString) has not been modeled yet")
          }

          // argument is HTMLElement
          val lset_html = lset_arg.filter((l) => T <= Helper.HasProperty(h, l, AbsString.alpha("nodeType")))
          val h_1 =
            if (!lset_html.isEmpty)
              lset_this.foldLeft(h)((hh, ll) => {
                val n_len = Helper.Proto(hh, ll, AbsString.alpha("length"))._1._4
                n_len.getSingle match {
                  case Some(len) =>
                    (0 until len.toInt).foldLeft(hh)((hhh, i) => {
                      val lset_current = Helper.Proto(hhh, ll, AbsString.alpha(i.toString))._2
                      val lset_target = lset_current.foldLeft(LocSetBot)((ls, l) =>
                        ls ++ Helper.Proto(hhh, l, AbsString.alpha("parentNode"))._2)
                      DOMTree.appendChild(hhh, lset_target, lset_html)
                    })
                  case None =>
                    if (n_len </ NumBot) {
                      val lset_current = Helper.Proto(hh, ll, NumStr)._2
                      val lset_target = lset_current.foldLeft(LocSetBot)((ls, l) =>
                        ls ++ Helper.Proto(hh, l, AbsString.alpha("parentNode"))._2)
                      DOMTree.appendChild(hh, lset_target, lset_html)
                    }
                    else
                      hh
                }
              })
            else
              HeapBot

          // argument is jQuery object
          val lset_jq = lset_arg.filter((l) => T <= Helper.HasProperty(h, l, AbsString.alpha("jquery")))
          val h_2 =
            if (!lset_jq.isEmpty)
              lset_this.foldLeft(h)((h1, l1) => {
                val n_len = Helper.Proto(h1, l1, AbsString.alpha("length"))._1._4
                n_len.getSingle match {
                  case Some(len) =>
                    (0 until len.toInt).foldLeft(h1)((h2, i) => {
                      val lset_current = Helper.Proto(h2, l1, AbsString.alpha(i.toString))._2
                      val lset_parent = lset_current.foldLeft(LocSetBot)((ls, l) =>
                        ls ++ Helper.Proto(h2, l, AbsString.alpha("parentNode"))._2)
                      lset_jq.foldLeft(h2)((h3, l3) => {
                        val n_len = Helper.Proto(h3, l3, AbsString.alpha("length"))._1._4
                        n_len.getSingle match {
                          case Some(len) =>
                            (0 until len.toInt).foldLeft(h3)((h4, j) => {
                              val lset_child = Helper.Proto(h4, l3, AbsString.alpha(j.toString))._2
                              DOMTree.appendChild(h4, lset_parent, lset_child)
                            })
                          case None =>
                            if (n_len </ NumBot) {
                              val lset_child = Helper.Proto(h3, l3, NumStr)._2
                              // now length is top
                              val h5 = lset_parent.foldLeft(h3)((h4, l4) => Helper.PropStore(h4, l4, AbsString.alpha("length"), Value(UInt)))
                              DOMTree.appendChild(h5, lset_parent, lset_child)
                            }
                            else
                              h3
                        }
                      })
                    })
                  case None =>
                    if (n_len </ NumBot) {
                      val lset_current = Helper.Proto(h1, l1, NumStr)._2
                      val lset_target = lset_current.foldLeft(LocSetBot)((ls, l) =>
                        ls ++ Helper.Proto(h1, l, AbsString.alpha("parentNode"))._2)
                      lset_jq.foldLeft(h1)((h2, l2) => {
                        val n_len = Helper.Proto(h2, l2, AbsString.alpha("length"))._1._4
                        n_len.getSingle match {
                          case Some(len) =>
                            (0 until len.toInt).foldLeft(h2)((h4, i) => {
                              val lset_child = Helper.Proto(h4, l2, AbsString.alpha(i.toString))._2
                              DOMTree.appendChild(h4, lset_target, lset_child)
                            })
                          case None =>
                            if (n_len </ NumBot) {
                              val lset_child = Helper.Proto(h2, l2, NumStr)._2
                              DOMTree.appendChild(h2, lset_target, lset_child)
                            }
                            else
                              h2
                        }
                      })
                    }
                    else
                      h1
                }
              })
            else
              HeapBot

          if (!lset_html.isEmpty || !lset_jq.isEmpty) {
            val h_ret = h_1 + h_2
            ((Helper.ReturnStore(h_ret, Value(lset_this)), ctx), (he, ctxe))
          }
          else if(v_arg1._1._5 </ StrBot){
            ((h, ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.append" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {

          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* 1st argument */
          val v_arg1 = getArgValue(h, ctx, args, "0")
          val lset_arg = v_arg1._2

          // ignore function, HTMLString, 2nd argument
          if(v_arg1._1._5 </ StrBot){
            System.err.println("* Warning: jQuery.prototype.appendTo(Selector or HTMLString) has not been modeled yet")
          }

          // argument is HTMLElement
          val lset_html = lset_arg.filter((l) => T <= Helper.HasProperty(h, l, AbsString.alpha("nodeType")))
          val h_1 =
            if (!lset_html.isEmpty)
              lset_this.foldLeft(h)((hh, ll) => {
                val n_len = Helper.Proto(hh, ll, AbsString.alpha("length"))._1._4
                n_len.getSingle match {
                  case Some(len) =>
                    (0 until len.toInt).foldLeft(hh)((hhh, i) => {
                      val lset_target = Helper.Proto(hhh, ll, AbsString.alpha(i.toString))._2
                      DOMTree.appendChild(hhh, lset_target, lset_html)
                    })
                  case None =>
                    if (n_len </ NumBot) {
                      val lset_target = Helper.Proto(hh, ll, NumStr)._2
                      DOMTree.appendChild(hh, lset_target, lset_html)
                    }
                    else
                      hh
                }
              })
            // argument is HTMLString
                  /*
            else if lset_arg is HTMLString
//HTML string to insert at the end of each element in the set of matched elements.
//append:function(){return this.domManip(arguments,!0,function(a){this.nodeType===1&&this.appendChild(a)})},
*/
            else
              HeapBot

          // argument is jQuery object
          val lset_jq = lset_arg.filter((l) => T <= Helper.HasProperty(h, l, AbsString.alpha("jquery")))
          val h_2 =
            if (!lset_jq.isEmpty)
              lset_this.foldLeft(h)((h1, l1) => {
                val n_len = Helper.Proto(h1, l1, AbsString.alpha("length"))._1._4
                n_len.getSingle match {
                  case Some(len) =>
                    (0 until len.toInt).foldLeft(h1)((h2, i) => {
                      val lset_parent = Helper.Proto(h2, l1, AbsString.alpha(i.toString))._2
                      lset_jq.foldLeft(h2)((h3, l3) => {
                        val n_len = Helper.Proto(h3, l3, AbsString.alpha("length"))._1._4
                        n_len.getSingle match {
                          case Some(len) =>
                            (0 until len.toInt).foldLeft(h3)((h4, j) => {
                              val lset_child = Helper.Proto(h4, l3, AbsString.alpha(j.toString))._2
                              DOMTree.appendChild(h4, lset_parent, lset_child)
                            })
                          case None =>
                            if (n_len </ NumBot) {
                              val lset_child = Helper.Proto(h3, l3, NumStr)._2
                              // now length is top
                              val h5 = lset_parent.foldLeft(h3)((h4, l4) => Helper.PropStore(h4, l4, AbsString.alpha("length"), Value(UInt)))
                              DOMTree.appendChild(h5, lset_parent, lset_child)
                            }
                            else
                              h3
                        }
                      })
                    })
                  case None =>
                    if (n_len </ NumBot) {
                      val lset_target = Helper.Proto(h1, l1, NumStr)._2
                      lset_jq.foldLeft(h1)((h2, l2) => {
                        val n_len = Helper.Proto(h2, l2, AbsString.alpha("length"))._1._4
                        n_len.getSingle match {
                          case Some(len) =>
                            (0 until len.toInt).foldLeft(h2)((h4, i) => {
                              val lset_child = Helper.Proto(h4, l2, AbsString.alpha(i.toString))._2
                              DOMTree.appendChild(h4, lset_target, lset_child)
                            })
                          case None =>
                            if (n_len </ NumBot) {
                              val lset_child = Helper.Proto(h2, l2, NumStr)._2
                              DOMTree.appendChild(h2, lset_target, lset_child)
                            }
                            else
                              h2
                        }
                      })
                    }
                    else
                      h1
                }
              })
            else
              HeapBot

          if (!lset_html.isEmpty || !lset_jq.isEmpty) {
            val h_ret = h_1 + h_2
            ((Helper.ReturnStore(h_ret, Value(lset_this)), ctx), (he, ctxe))
          }
          else if(v_arg1._1._5 </ StrBot){
            ((h, ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.appendTo" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {

          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* 1st argument */
          val v_arg1 = getArgValue(h, ctx, args, "0")
          val lset_arg = v_arg1._2

          // ignore HTMLString
          if(v_arg1._1._5 </ StrBot){
            System.err.println("* Warning: jQuery.prototype.appendTo(Selector or HTMLString) has not been modeled yet")
          }
          // argument is HTMLElement
          val lset_html = lset_arg.filter((l) => T <= Helper.HasProperty(h, l, AbsString.alpha("nodeType")))
          val h_1 =
            if (!lset_html.isEmpty)
              lset_this.foldLeft(h)((hh, ll) => {
                val n_len = Helper.Proto(hh, ll, AbsString.alpha("length"))._1._4
                n_len.getSingle match {
                  case Some(len) =>
                    (0 until len.toInt).foldLeft(hh)((hhh, i) => {
                      val lset_child = Helper.Proto(hhh, ll, AbsString.alpha(i.toString))._2
                      // append
                      DOMTree.appendChild(hhh, lset_html, lset_child)
                    })
                  case None =>
                    if (n_len </ NumBot) {
                      val lset_child = Helper.Proto(hh, ll, NumStr)._2
                      // now length is top
                      val _h = lset_html.foldLeft(hh)((_h, l) => Helper.PropStore(_h, l, AbsString.alpha("length"), Value(UInt)))
                      // append
                      DOMTree.appendChild(_h, lset_html, lset_child)
                    }
                    else
                      hh
                }
              })
            else
              HeapBot

          // argument is jQuery object
          val lset_jq = lset_arg.filter((l) => T <= Helper.HasProperty(h, l, AbsString.alpha("jquery")))
          val h_2 =
            if (!lset_jq.isEmpty)
              lset_this.foldLeft(h)((h1, l1) => {
                val n_len = Helper.Proto(h1, l1, AbsString.alpha("length"))._1._4
                n_len.getSingle match {
                  case Some(len) =>
                    (0 until len.toInt).foldLeft(h1)((h2, i) => {
                      val lset_child = Helper.Proto(h2, l1, AbsString.alpha(i.toString))._2
                      lset_jq.foldLeft(h2)((h3, l3) => {
                        val n_len = Helper.Proto(h3, l3, AbsString.alpha("length"))._1._4
                        n_len.getSingle match {
                          case Some(len) =>
                            (0 until len.toInt).foldLeft(h3)((h4, j) => {
                              val lset_parent = Helper.Proto(h4, l3, AbsString.alpha(j.toString))._2
                              DOMTree.appendChild(h4, lset_parent, lset_child)
                            })
                          case None =>
                            if (n_len </ NumBot) {
                              val lset_parent = Helper.Proto(h3, l3, NumStr)._2
                              DOMTree.appendChild(h3, lset_parent, lset_child)
                            }
                            else
                              h3
                        }
                      })
                    })
                  case None =>
                    if (n_len </ NumBot) {
                      val lset_child = Helper.Proto(h1, l1, NumStr)._2
                      lset_jq.foldLeft(h1)((h2, l2) => {
                        val n_len = Helper.Proto(h2, l2, AbsString.alpha("length"))._1._4
                        n_len.getSingle match {
                          case Some(len) =>
                            (0 until len.toInt).foldLeft(h2)((h4, i) => {
                              val lset_target = Helper.Proto(h4, l2, AbsString.alpha(i.toString))._2
                              DOMTree.appendChild(h4, lset_target, lset_child)
                            })
                          case None =>
                            if (n_len </ NumBot) {
                              val lset_target = Helper.Proto(h2, l2, NumStr)._2
                              DOMTree.appendChild(h2, lset_target, lset_child)
                            }
                            else
                              h2
                        }
                      })
                    }
                    else
                      h1
                }
              })
            else
              HeapBot

          if (!lset_html.isEmpty || !lset_jq.isEmpty) {
            val h_ret = h_1 + h_2
            ((Helper.ReturnStore(h_ret, Value(lset_this)), ctx), (he, ctxe))
          }
          else if(v_arg1._1._5 </ StrBot){
            ((h, ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
     ("jQuery.prototype.empty" -> (
      (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
        /* jQuery object */
        val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2

        val h_ret = lset_this.foldLeft(h)((h1, l1) => {
          val lset_parent = Helper.Proto(h1, l1, NumStr)._2
          lset_parent.foldLeft(h1)((h2, l2) => {
            val lset_ns = Helper.Proto(h2, l2, AbsString.alpha("childNodes"))._2
            lset_ns.foldLeft(h2)((h3, l3) => h3.update(l3, DOMHelper.NewChildNodeListObj(0)))
          })
        })

        ((Helper.ReturnStore(h_ret, Value(lset_this)), ctx), (he, ctxe))
      })),
      ("jQuery.prototype.prepend" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* 1st argument */
          val v_arg1 = getArgValue(h, ctx, args, "0")
          val lset_arg = v_arg1._2

          // ignore function, HTMLString, 2nd argument

          // argument is HTMLElement
          val lset_html = lset_arg.filter((l) => T <= Helper.HasProperty(h, l, AbsString.alpha("nodeType")))
          val h_1 =
            if (!lset_html.isEmpty)
              lset_this.foldLeft(h)((hh, ll) => {
                val n_len = Helper.Proto(hh, ll, AbsString.alpha("length"))._1._4
                n_len.getSingle match {
                  case Some(len) =>
                    (0 until len.toInt).foldLeft(hh)((hhh, i) => {
                      val lset_target = Helper.Proto(hhh, ll, AbsString.alpha(i.toString))._2
                      DOMTree.prependChild(hhh, lset_target, lset_html)
                    })
                  case None =>
                    if (n_len </ NumBot) {
                      val lset_target = Helper.Proto(hh, ll, NumStr)._2
                      DOMTree.prependChild(hh, lset_target, lset_html)
                    }
                    else
                      hh
                }
              })
            else
              HeapBot

          // argument is jQuery object
          val lset_jq = lset_arg.filter((l) => T <= Helper.HasProperty(h, l, AbsString.alpha("jquery")))
          val h_2 =
            if (!lset_jq.isEmpty)
              lset_this.foldLeft(h)((h1, l1) => {
                val n_len = Helper.Proto(h1, l1, AbsString.alpha("length"))._1._4
                n_len.getSingle match {
                  case Some(len) =>
                    (0 until len.toInt).foldLeft(h1)((h2, i) => {
                      val lset_parent = Helper.Proto(h2, l1, AbsString.alpha(i.toString))._2
                      lset_jq.foldLeft(h2)((h3, l3) => {
                        val n_len = Helper.Proto(h3, l3, AbsString.alpha("length"))._1._4
                        n_len.getSingle match {
                          case Some(len) =>
                            (0 until len.toInt).foldLeft(h3)((h4, j) => {
                              val lset_child = Helper.Proto(h4, l3, AbsString.alpha(j.toString))._2
                              DOMTree.prependChild(h4, lset_parent, lset_child)
                            })
                          case None =>
                            if (n_len </ NumBot) {
                              val lset_child = Helper.Proto(h3, l3, NumStr)._2
                              // now length is top
                              val h5 = lset_parent.foldLeft(h3)((h4, l4) => Helper.PropStore(h4, l4, AbsString.alpha("length"), Value(UInt)))
                              DOMTree.prependChild(h5, lset_parent, lset_child)
                            }
                            else
                              h3
                        }
                      })
                    })
                  case None =>
                    if (n_len </ NumBot) {
                      val lset_target = Helper.Proto(h1, l1, NumStr)._2
                      lset_jq.foldLeft(h1)((h2, l2) => {
                        val n_len = Helper.Proto(h2, l2, AbsString.alpha("length"))._1._4
                        n_len.getSingle match {
                          case Some(len) =>
                            (0 until len.toInt).foldLeft(h2)((h4, i) => {
                              val lset_child = Helper.Proto(h4, l2, AbsString.alpha(i.toString))._2
                              DOMTree.prependChild(h4, lset_target, lset_child)
                            })
                          case None =>
                            if (n_len </ NumBot) {
                              val lset_child = Helper.Proto(h2, l2, NumStr)._2
                              DOMTree.prependChild(h2, lset_target, lset_child)
                            }
                            else
                              h2
                        }
                      })
                    }
                    else
                      h1
                }
              })
            else
              HeapBot

          if (!lset_html.isEmpty || !lset_jq.isEmpty) {
            val h_ret = h_1 + h_2
            ((Helper.ReturnStore(h_ret, Value(lset_this)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.remove" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2

          if (!lset_this.isEmpty) {
            val h_ret = lset_this.foldLeft(h)((h1, l1) => {
              val n_len = Helper.Proto(h1, l1, AbsString.alpha("length"))._1._4
              n_len.getSingle match {
                case Some(n) =>
                  (0 until n.toInt).foldLeft(h1)((h2, i) => {
                    val lset_child = Helper.Proto(h2, l1, AbsString.alpha(i.toString))._2
                    val lset_parent = lset_child.foldLeft(LocSetBot)((lset, _l) =>
                      lset ++ Helper.Proto(h2, _l, AbsString.alpha("parentNode"))._2)
                    DOMTree.removeChild(h2, lset_parent, lset_child)
                  })
                case None =>
                  if (n_len </ NumBot) {
                    val lset_child = Helper.Proto(h1, l1, NumStr)._2
                    val lset_parent = lset_child.foldLeft(LocSetBot)((lset, _l) =>
                      lset ++ Helper.Proto(h1, _l, AbsString.alpha("parentNode"))._2)
                    DOMTree.removeChild(h1, lset_parent, lset_child)
                  }
                  else
                    HeapBot
              }
            })
            ((Helper.ReturnStore(h_ret, Value(lset_this)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))

        })),
      ("jQuery.prototype.text" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          /* 1st argument */
          val v_arg1 = getArgValue(h, ctx, args, "0")

          val v_ret1 =
            if (v_arg1._1._1 </ UndefBot)
              Value(StrTop)
            else
              ValueBot

          val v_ret2 =
            if (v_arg1._1._1 <= UndefBot && v_arg1 </ ValueBot)
              // TODO: unsound, igrnoe DOM manipulation
              Value(lset_this)
            else
              ValueBot

          val v_ret = v_ret1 + v_ret2

          if (v_ret </ ValueBot)
            ((Helper.ReturnStore(h, v_ret), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))

        }))
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
