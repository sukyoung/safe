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

object JQueryAttribute extends ModelData {
  //private val prop_const: List[(String, AbsProperty)] = List()
  val ValHooksLoc = newSystemLoc("jQueryValHooks", Recent)
  val CheckboxLoc = newSystemLoc("jQueryValHooks.checkbox", Recent)
  val OptionLoc = newSystemLoc("jQueryValHooks.option", Recent)
  val RadioLoc = newSystemLoc("jQueryValHooks.radio", Recent)
  val SelectLoc = newSystemLoc("jQueryValHooks.select", Recent)

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("addClass",    AbsBuiltinFunc("jQuery.prototype.addClass", 1)),
    ("attr",        AbsBuiltinFunc("jQuery.prototype.attr", 2)),
    ("hasClass",    AbsBuiltinFunc("jQuery.prototype.hasClass", 1)),
    ("html",        AbsBuiltinFunc("jQuery.prototype.html", 1)),
    ("prop",        AbsBuiltinFunc("jQuery.prototype.prop", 2)),
    ("removeAttr",  AbsBuiltinFunc("jQuery.prototype.removeAttr", 1)),
    ("removeClass", AbsBuiltinFunc("jQuery.prototype.removeClass", 1)),
    ("removeProp",  AbsBuiltinFunc("jQuery.prototype.removeProp", 1)),
    ("toggleClass", AbsBuiltinFunc("jQuery.prototype.toggleClass", 2)),
    ("val",         AbsBuiltinFunc("jQuery.prototype.val", 1))
  )

  private val prop_const: List[(String, AbsProperty)] = List(
    ("valHooks",        AbsConstValue(PropValue(ObjectValue(Value(ValHooksLoc), T, T, T))))
  )
  
  private val prop_valhooks: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(BoolTrue))),
    ("checkbox",        AbsConstValue(PropValue(ObjectValue(Value(CheckboxLoc), T, T, T)))),
    ("option",        AbsConstValue(PropValue(ObjectValue(Value(OptionLoc), T, T, T)))),
    ("radio",        AbsConstValue(PropValue(ObjectValue(Value(RadioLoc), T, T, T)))),
    ("select",        AbsConstValue(PropValue(ObjectValue(Value(SelectLoc), T, T, T))))
  )
  
  private val prop_checkbox: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(BoolTrue))),
    ("set", AbsBuiltinFunc("jQuery.valHooks.checkbox.set", 2))
  )
  
  private val prop_option: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(BoolTrue))),
    ("get", AbsBuiltinFunc("jQuery.valHooks.option.get", 1))
  )
  
  private val prop_radio: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(BoolTrue))),
    ("set", AbsBuiltinFunc("jQuery.valHooks.radio.set", 2))
  )
  
  private val prop_select: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(BoolTrue))),
    ("set", AbsBuiltinFunc("jQuery.valHooks.select.set", 2))
  )


  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (JQuery.ProtoLoc, prop_proto), (JQuery.ConstLoc, prop_const), (ValHooksLoc, prop_valhooks),
    (CheckboxLoc, prop_checkbox), (OptionLoc, prop_option), (RadioLoc, prop_radio), (SelectLoc, prop_select)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      "jQuery.prototype.addClass" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* new addr */
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)

          /* new loc */
          val l_attr = addrToLoc(addr1, Recent)
          val l_text = addrToLoc(addr2, Recent)
          val l_child1 = addrToLoc(addr3, Recent)
          val l_child2 = addrToLoc(addr4, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
          val (h_4, ctx_4) = Helper.Oldify(h_3, ctx_3, addr4)

          /* jQuery object */
          val lset_this = h_4(SinglePureLocalLoc)("@this")._2._2
          /* 1st argument */
          val v_arg = getArgValue(h_4, ctx, args, "0")
          val s_arg = v_arg._1._5

          val (h_ret1, v_ret1) =
            if (s_arg </ StrBot) {
              val h3 = lset_this.foldLeft(h_4)((h1, l1) => {
                val lset_elem = Helper.Proto(h1, l1, NumStr)._2
                lset_elem.foldLeft(h1)((h2, l2) => {
                  val v_class = DOMHelper.getAttribute(h2, l2, AbsString.alpha("class"))
                  val s_class = v_class._1._5
                  val h2_1 =
                    if (s_class </ StrBot)
                      (s_class.gamma, s_arg.gamma) match {
                        case (Some(_), Some(_)) =>
                          val s_new = s_class.trim.concat(AbsString.alpha(" ")).concat(s_arg.trim)
                          DOMHelper.setAttribute(h2, l2, l_attr, l_text, l_child1, l_child2, AbsString.alpha("class"), s_new)
                        case (None, _) | (_, None) =>
                          if (s_class </ StrBot && s_arg </ StrBot)
                            h2
                          else
                            HeapBot
                      }
                    else
                      HeapBot
                  val h2_2 =
                    if (v_class._1._2 </ NullBot)
                      DOMHelper.setAttribute(h2, l2, l_attr, l_text, l_child1, l_child2, AbsString.alpha("class"), s_arg)
                    else
                      HeapBot
                  h2_1 + h2_2
                })
              })
              (h3, Value(lset_this))
            }
            else
              (HeapBot, ValueBot)

          val (h_ret2, v_ret2) =
            if (!v_arg._2.isEmpty)
              (h_4, Value(lset_this))
            else
              (HeapBot, ValueBot)

          val h_ret = h_ret1 + h_ret2
          if (!(h_ret <= HeapBot)) {
            ((Helper.ReturnStore(h_ret, v_ret1 + v_ret2), ctx_4), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
      "jQuery.prototype.attr" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* new addr */
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)
          val addr5 = cfg.getAPIAddress(addr_env, 4)
          /* new loc */
          val l_attr = addrToLoc(addr1, Recent)
          val l_text = addrToLoc(addr2, Recent)
          val l_child1 = addrToLoc(addr3, Recent)
          val l_child2 = addrToLoc(addr4, Recent)
          val l_tableentry = addrToLoc(addr5, Recent)

          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          /* 1st argument */
          val v_arg1 = getArgValue(h, ctx, args, "0")
          val v_arg2 = getArgValue(h, ctx, args, "1")

          // only 1st argument
          // get
          val (h_ret1, v_ret1, ctx_ret1) =
            if (v_arg2._1._1 </ UndefBot && v_arg1._1._5 </ StrBot && UndefTop <= v_arg2._1._1) {
              val v1 = lset_this.foldLeft(ValueBot)((v, l) => {
                val lset_first = Helper.Proto(h, l, AbsString.alpha("0"))._2
                v + lset_first.foldLeft(ValueBot)((vv, ll) =>
                  vv + DOMHelper.getAttribute(h, ll, v_arg1._1._5))
              })
              (h, v1, ctx)
            }
            else
              (HeapBot, ValueBot, ContextBot)

          // has 2nd argument
          // set
          val (h_ret2, v_ret2, ctx_ret2) =
            if (v_arg2._1._1 <= UndefBot && v_arg2 </ ValueBot && v_arg1._1._5 </ StrBot) {
              val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
              val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
              val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
              val (h_4, ctx_4) = Helper.Oldify(h_3, ctx_3, addr4)
              val (h_5, ctx_5) = Helper.Oldify(h_4, ctx_4, addr5)
              val lset_elem = lset_this.foldLeft(LocSetBot)((ls, l) => ls ++ Helper.Proto(h_5, l, NumStr)._2)
              val h1 = DOMHelper.setAttribute(h_5, lset_elem, l_attr, l_text, l_child1, l_child2, l_tableentry, v_arg1._1._5, v_arg2._1._5)
              (h1, Value(lset_this), ctx_5)
            }
            else
              (HeapBot, ValueBot, ContextBot)

          val v_ret = v_ret1 + v_ret2
          if (v_ret </ ValueBot) {
            val h_ret = h_ret1 + h_ret2
            val ctx_ret = ctx_ret1 + ctx_ret2
            ((Helper.ReturnStore(h_ret, v_ret), ctx_ret), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
      ("jQuery.prototype.hasClass" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          /* 1st argument */
          val s_arg = getArgValue(h, ctx, args, "0")._1._5

          val b_ret = s_arg.gamma match {
            case Some(_) =>
              lset_this.foldLeft[AbsBool](BoolBot)((b, l) => {
                val lset_elem = h(l)(NumStr)._1._1._2
                val b1 =
                  lset_elem.foldLeft[AbsBool](BoolBot)((r, ll) => {
                    val s_prop = DOMHelper.getAttribute(h, ll, AbsString.alpha("class"))._1._5
                    r + (s_prop.gamma match {
                      case Some(_) =>
                        s_prop.contains(s_arg)
                      case None =>
                        if (s_prop <= StrBot)
                          BoolBot
                        else
                          BoolTrue
                    })
                  })
                val b2 =
                  lset_elem.foldLeft[AbsBool](BoolBot)((r, ll) => {
                    val s_prop = DOMHelper.getAttribute(h, ll, AbsString.alpha("class"))._1._5
                    r + (s_prop.gamma match {
                      case Some(_) =>
                        !s_prop.contains(s_arg)
                      case None =>
                        if (s_prop <= StrBot)
                          BoolBot
                        else
                          BoolFalse
                    })
                  })
                b + b1 + b2
              })
            case None =>
              if (s_arg <= StrBot)
                BoolBot
              else
                BoolTop
          }
          if (b_ret </ BoolBot)
            ((Helper.ReturnStore(h, Value(b_ret)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.html" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          /* 1st argument */
          val v_arg = getArgValue(h, ctx, args, "0")

          // no arguments
          // get
          val v_ret1 =
            if (v_arg._1._1 </ UndefBot)
              Value(StrTop)
            else
              ValueBot

          // has 1st argument
          // set
          // TODO: unsound
          val v_ret2 =
            if (v_arg._1._1 <= UndefBot && v_arg </ ValueBot)
              Value(lset_this)
            else
              ValueBot

          val v_ret = v_ret1 + v_ret2
          if (v_ret </ ValueBot) {
            ((Helper.ReturnStore(h, v_ret), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.prop" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          /* 1st argument */
          val v_arg1 = getArgValue(h, ctx, args, "0")
          val v_arg2 = getArgValue(h, ctx, args, "1")

          // only 1st argument
          // get
          val (h_ret1, v_ret1) =
          if (v_arg2._1._1 </ UndefBot && v_arg1._1._5 </ StrBot && UndefTop <= v_arg2._1._1) {
            val v1 = lset_this.foldLeft(ValueBot)((v, l) => {
              val lset_first = Helper.Proto(h, l, AbsString.alpha("0"))._2
              v + lset_first.foldLeft(ValueBot)((vv, ll) =>
                vv + Helper.Proto(h, ll, v_arg1._1._5)
              )
            })
            (h, v1)
          }
          else
            (HeapBot, ValueBot)

          // has 2nd argument
          // set
          val (h_ret2, v_ret2) =
            if (v_arg2._1._1 <= UndefBot && v_arg2 </ ValueBot && v_arg1._1._5 </ StrBot) {
              val lset_elem = lset_this.foldLeft(LocSetBot)((ls, l) => ls ++ Helper.Proto(h, l, NumStr)._2)
              val h1 = lset_elem.foldLeft(h)((_h, l) =>
                Helper.PropStore(_h, l, v_arg1._1._5, v_arg2)
              )
              (h1, Value(lset_this))
          }
          else
              (HeapBot, ValueBot)

          val v_ret = v_ret1 + v_ret2
          if (v_ret </ ValueBot) {
            val h_ret = h_ret1 + h_ret2
            ((Helper.ReturnStore(h_ret, v_ret), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      "jQuery.prototype.removeClass" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* new addr */
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)
          /* new loc */
          val l_attr = addrToLoc(addr1, Recent)
          val l_text = addrToLoc(addr2, Recent)
          val l_child1 = addrToLoc(addr3, Recent)
          val l_child2 = addrToLoc(addr4, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
          val (h_4, ctx_4) = Helper.Oldify(h_3, ctx_3, addr4)

          /* jQuery object */
          val lset_this = h_4(SinglePureLocalLoc)("@this")._2._2
          /* 1st argument */
          val v_arg = getArgValue(h_4, ctx, args, "0")
          val s_arg = v_arg._1._5

          val (h_ret1, v_ret1) =
            if (s_arg </ StrBot) {
              val h3 = lset_this.foldLeft(h_4)((h1, l1) => {
                val lset_elem = Helper.Proto(h1, l1, NumStr)._2
                lset_elem.foldLeft(h1)((h2, l2) => {
                  val v_class = DOMHelper.getAttribute(h2, l2, AbsString.alpha("class"))
                  val s_class = v_class._1._5
                  val h2_1 =
                    if (s_class </ StrBot)
                      (s_class.gamma, s_arg.gamma) match {
                        case (Some(s_oriSet), Some(s_removeSet)) =>
                          var h3: Heap = HeapBot
                          for(s_ori <- s_oriSet) for(s_remove <- s_removeSet) {
                            val lset_class = s_ori.split(" ").toList
                            val s_arg = s_remove.trim
                            val s_new = lset_class.foldLeft("")((c, s) => {
                              val tok = s.trim
                              if (tok.length > 0 && s != s_arg)
                                c + " " + tok
                              else
                                c
                            })
                            // empty string check?
                            h3+= DOMHelper.setAttribute(h2, l2, l_attr, l_text, l_child1, l_child2, AbsString.alpha("class"), AbsString.alpha(s_new))
                          }
                          h3
                        case (None, _) | (_, None) =>
                          if (s_class </ StrBot && s_arg </ StrBot)
                            h2
                          else
                            HeapBot
                      }
                    else
                      HeapBot
                  val h2_2 =
                    if (v_class._1._2 </ NullBot)
                      h2
                    else
                      HeapBot
                  h2_1 + h2_2
                })
              })
              (h3, Value(lset_this))
            }
            else
              (HeapBot, ValueBot)

          val (h_ret2, v_ret2) =
            if (!v_arg._2.isEmpty)
              (h_4, Value(lset_this))
            else
              (HeapBot, ValueBot)

          val h_ret = h_ret1 + h_ret2
          if (!(h_ret <= HeapBot)) {
            ((Helper.ReturnStore(h_ret, v_ret1 + v_ret2), ctx_4), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
      ("jQuery.prototype.removeProp" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* jQuery object */
          val lset_this = h(SinglePureLocalLoc)("@this")._2._2
          /* 1st argument */
          val v_arg1 = getArgValue(h, ctx, args, "0")

          val lset_elem = lset_this.foldLeft(LocSetBot)((ls, l) => ls ++ Helper.Proto(h, l, NumStr)._2)
          val h_ret =
            lset_elem.foldLeft(h)((_h, l) => Helper.Delete(_h, l, v_arg1._1._5)._1)
          if (!lset_this.isEmpty) {
            ((Helper.ReturnStore(h_ret, Value(lset_this)), ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      "jQuery.prototype.toggleClass" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* new addr */
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)
          /* new loc */
          val l_attr = addrToLoc(addr1, Recent)
          val l_text = addrToLoc(addr2, Recent)
          val l_child1 = addrToLoc(addr3, Recent)
          val l_child2 = addrToLoc(addr4, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
          val (h_4, ctx_4) = Helper.Oldify(h_3, ctx_3, addr4)

          /* jQuery object */
          val lset_this = h_4(SinglePureLocalLoc)("@this")._2._2
          /* 1st argument */
          val v_arg = getArgValue(h_4, ctx, args, "0")
          val s_arg = v_arg._1._5

          val (h_ret1, v_ret1) =
            if (s_arg </ StrBot) {
              val h3 = lset_this.foldLeft(h_4)((h1, l1) => {
                val lset_elem = Helper.Proto(h1, l1, NumStr)._2
                lset_elem.foldLeft(h1)((h2, l2) => {
                  val v_class = DOMHelper.getAttribute(h2, l2, AbsString.alpha("class"))
                  val s_class = v_class._1._5
                  val h2_1 =
                    if (s_class </ StrBot)
                      (s_class.gamma, s_arg.gamma) match {
                        case (Some(s_oriSet), Some(sSet)) =>
                          var h3: Heap = HeapBot
                          for(s_ori <- s_oriSet) for(s <- sSet) {
                            val s_target = s_ori.trim
                            val s_arg = s.trim
                            val s_new =
                              if (s_target.contains(s_arg)) {
                                // remove
                                val lset_class = s_ori.split(" ").toList
                                lset_class.foldLeft("")((c, s) => {
                                  val tok = s.trim
                                  if (tok.length > 0 && s != s_arg)
                                    c + " " + tok
                                  else
                                    c
                                })
                              }
                              else //add
                                s_target + " " + s_arg
                            // empty string check?
                            h3+= DOMHelper.setAttribute(h2, l2, l_attr, l_text, l_child1, l_child2, AbsString.alpha("class"), AbsString.alpha(s_new))
                          }
                          h3
                        case (None, _) | (_, None) =>
                          if (s_class </ StrBot && s_arg </ StrBot)
                            h2
                          else
                            HeapBot
                      }
                    else
                      HeapBot
                  val h2_2 =
                    if (v_class._1._2 </ NullBot)
                      h2
                    else
                      HeapBot
                  h2_1 + h2_2
                })
              })
              (h3, Value(lset_this))
            }
            else
              (HeapBot, ValueBot)

          val (h_ret2, v_ret2) =
            if (!v_arg._2.isEmpty)
              (h_4, Value(lset_this))
            else
              (HeapBot, ValueBot)

          val h_ret = h_ret1 + h_ret2
          if (!(h_ret <= HeapBot)) {
            ((Helper.ReturnStore(h_ret, v_ret1 + v_ret2), ctx_4), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }),
        ("jQuery.prototype.val" -> (
          (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
            /* jQuery object */
            val lset_this = h(SinglePureLocalLoc)("@this")._2._2
            /* 1st argument */
            val v_arg1 = getArgValue(h, ctx, args, "0")

            // no arguments
            // get
            val (h_ret1, v_ret1) =
              if (v_arg1._1._1 </ UndefBot) {
                val v1 = lset_this.foldLeft(ValueBot)((v, l) => {
                  val lset_first = Helper.Proto(h, l, AbsString.alpha("0"))._2
                  v + lset_first.foldLeft(ValueBot)((vv, ll) =>
                    vv + Helper.Proto(h, ll, AbsString.alpha("value"))
                  )
                })
                (h, v1)
              }
              else
                (HeapBot, ValueBot)

            // has 1st argument
            // set
            // TODO: ignore function, array
            val (h_ret2, v_ret2) =
              if (v_arg1._1._1 <= UndefBot && v_arg1 </ ValueBot && v_arg1._1._5 </ StrBot) {
                val lset_elem = lset_this.foldLeft(LocSetBot)((ls, l) => ls ++ Helper.Proto(h, l, NumStr)._2)
                val h1 = lset_elem.foldLeft(h)((_h, l) =>
                  Helper.PropStore(_h, l, AbsString.alpha("value"), v_arg1)
                )
                (h1, Value(lset_this))
              }
              else
                (HeapBot, ValueBot)

            val v_ret = v_ret1 + v_ret2
            if (v_ret </ ValueBot) {
              val h_ret = h_ret1 + h_ret2
              ((Helper.ReturnStore(h_ret, v_ret), ctx), (he, ctxe))
            }
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
