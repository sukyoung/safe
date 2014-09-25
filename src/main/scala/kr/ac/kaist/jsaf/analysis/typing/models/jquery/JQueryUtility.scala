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
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFuncAftercall
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap

object JQueryUtility extends ModelData {
  private val EventLoc = newSystemLoc("jQuery.event", Recent)
  private val EventSpecialLoc = newSystemLoc("jQuery.event.special", Recent)
  private val EventSpecialDefaultLoc = newSystemLoc("jQuery.event.special.default", Recent)
  private val BrowserLoc = newSystemLoc("jQuery.browser", Recent)
  private val ExprLoc = newSystemLoc("jQuery.expr", Recent)
  private val ExprDefaultLoc = newSystemLoc("jQuery.expr.default", Recent)
  
private val prop_const: List[(String, AbsProperty)] = List(
    ("contains",      AbsBuiltinFunc("jQuery.contains", 2)),
    ("each",          AbsBuiltinFuncAftercall("jQuery.each", 3)),
    ("extend",        AbsBuiltinFunc("jQuery.extend", 0)),
    ("globalEval",    AbsBuiltinFunc("jQuery.globalEval", 1)),
    ("grep",          AbsBuiltinFunc("jQuery.grep", 3)),
    ("inArray",       AbsBuiltinFunc("jQuery.inArray", 3)),
    ("isArray",       AbsBuiltinFunc("jQuery.isArray", 1)),
    ("isEmptyObject", AbsBuiltinFunc("jQuery.isEmptyObject", 1)),
    ("isFunction",    AbsBuiltinFunc("jQuery.isFunction", 1)),
    ("isNumeric",     AbsBuiltinFunc("jQuery.isNumeric", 1)),
    ("isPlainObject", AbsBuiltinFunc("jQuery.isPlainObject", 1)),
    ("isWindow",      AbsBuiltinFunc("jQuery.isWindow", 1)),
    ("isXMLDoc",      AbsBuiltinFunc("jQuery.isXMLDoc", 1)),
    ("makeArray",     AbsBuiltinFunc("jQuery.makeArray", 2)),
    ("map",           AbsBuiltinFuncCallback("jQuery.map", 3)),
    ("merge",         AbsBuiltinFunc("jQuery.merge", 2)),
    ("noop",          AbsBuiltinFunc("jQuery.noop", 0)),
    ("now",           AbsBuiltinFunc("jQuery.now", 0)),
    ("parseHTML",     AbsBuiltinFunc("jQuery.parseHTML", 3)),
    ("parseJSON",     AbsBuiltinFunc("jQuery.parseJSON", 1)),
    ("parseXML",      AbsBuiltinFunc("jQuery.parseXML", 1)),
    ("trim",          AbsBuiltinFunc("jQuery.trim", 1)),
    ("type",          AbsBuiltinFunc("jQuery.type", 1)),
    ("unique",        AbsBuiltinFunc("jQuery.unique", 1)),

    // for special EVENT 
    ("event",         AbsConstValue(PropValue(ObjectValue(EventLoc, T, T, T)))),
    // for browser compatibility ccheck
    ("browser",       AbsConstValue(PropValue(ObjectValue(BrowserLoc, T, T, T)))),
    // for browser compatibility ccheck
    ("expr",       AbsConstValue(PropValue(ObjectValue(ExprLoc, T, T, T))))
  )

////////////////////////////////////////////////////////
// jQuery.expr
  private val prop_expr: List[(String, AbsProperty)] = List(
    ("@class",      AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",      AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    // TODO: '::', 'attrHandle', 'filter', 'filters', 'find', 'match', 'preFilter', 'pseudos', 'preFilters', 'relative', 'setFilters'
    ("createPseudo",        AbsBuiltinFunc("jQuery.expr.createPseudo", 1)),
    (Str_default_other,       AbsConstValue(PropValue(ObjectValue(ExprDefaultLoc, BoolTop, BoolTop, BoolTop))))
  )
  private val prop_expr_default: List[(String, AbsProperty)] = List(
    ("@class",      AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",      AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue)))
  )
///////////////////////////////////////////////////////
// jQuery.event.special object 
  
  private val prop_event: List[(String, AbsProperty)] = List(
    ("@class",      AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",      AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("special",      AbsConstValue(PropValue(ObjectValue(EventSpecialLoc, T, T, T))))
  )
  private val prop_event_special: List[(String, AbsProperty)] = List(
    ("@class",      AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",      AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    (Str_default_other,       AbsConstValue(PropValue(ObjectValue(EventSpecialDefaultLoc, BoolTop, BoolTop, BoolTop))))
  )
  private val prop_event_special_default: List[(String, AbsProperty)] = List(
    ("@class",      AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",      AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue)))
  )
/////////////////////////////////////////////////////////
  private val prop_browser: List[(String, AbsProperty)] = List(
    ("@class",       AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",       AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible",  AbsConstValue(PropValue(BoolTrue))),
    (Str_default_other,       AbsConstValue(PropValue(ObjectValue(Value(BoolTop)+Value(StrTop), BoolTop, BoolTop, BoolTop))))
  )
////////////////////////////////////////////////////////

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("extend",   AbsBuiltinFunc("jQuery.prototype.extend", 0))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (JQuery.ConstLoc, prop_const), (JQuery.ProtoLoc, prop_proto), (EventLoc, prop_event),
    (EventSpecialLoc, prop_event_special), (BrowserLoc, prop_browser), (EventSpecialDefaultLoc, prop_event_special_default),
    (ExprLoc, prop_expr), (ExprDefaultLoc, prop_expr_default)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {

    Map(
      "jQuery.each" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* new addr */
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          /* new loc */
          val l_arg = addrToLoc(addr1, Recent)
          val l_cc = addrToLoc(addr2, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)

          /* target */
          val lset_target = getArgValue(h_3, ctx_3, args, "0")._2
          /* fun */
          val lset_fun = getArgValue(h_3, ctx_3, args, "1")._2.filter((l) => BoolTrue <= Helper.IsCallable(h, l))

          val b_isarr = lset_target.foldLeft[AbsBool](BoolBot)((b, l) => b + JQueryHelper.isArraylike(h_3, l))
          // arg1
          val v_index =
            b_isarr.getPair match {
              case (AbsTop, _) => Value(UInt) + Value(StrTop)
              case (AbsSingle, Some(b)) => if (b) Value(UInt) else Value(StrTop)
              case (AbsBot, _) => ValueBot
              case _ => throw new InternalError("AbsBool does not have an abstract value for multiple values.")
            }
          val s_index = Helper.toString(v_index._1)
          // arg2
          val v_elem = lset_target.foldLeft(ValueBot)((v, l) => v + Helper.Proto(h_3, l, s_index))

          // create Arguments object
          val o_arg = Helper.NewArgObject(AbsNumber.alpha(2))
            .update("0", PropValue(ObjectValue(v_index, T, T, T)))
            .update("1", PropValue(ObjectValue(v_elem, T, T, T)))
          val h_4 = h_3.update(l_arg, o_arg)
          val v_arg = Value(l_arg)

          val lset_argthis = Helper.getThis(h_4, v_elem)
          val v_this2 = Value(PValue(UndefBot, NullBot, v_elem._1._3, v_elem._1._4, v_elem._1._5), lset_argthis)
          val (callee_this, h_5, ctx_5, es3) = Helper.toObject(h_4, ctx_3, v_this2, addr3)


          val o_old = h_5(SinglePureLocalLoc)
          val cc_caller = cp._2
          val n_aftercall = cfg.getAftercallFromCall(cp._1)
          val cp_aftercall = (n_aftercall, cc_caller)
          val n_aftercatch = cfg.getAftercatchFromCall(cp._1)
          val cp_aftercatch = (n_aftercatch, cc_caller)
          lset_fun.foreach((l_f) => {
            val o_f = h_5(l_f)
            o_f("@function")._3.foreach((fid) => {
              cc_caller.NewCallContext(h, cfg, fid, l_cc, callee_this._2).foreach((pair) => {
                val (cc_new, o_new) = pair
                val o_new2 = o_new.
                  update(cfg.getArgumentsName(fid),
                  PropValue(ObjectValue(v_arg, BoolTrue, BoolFalse, BoolFalse))).
                  update("@scope", o_f("@scope"))
                sem.addCallEdge(cp, ((fid, LEntry), cc_new), ContextEmpty, o_new2)
                sem.addReturnEdge(((fid, LExit), cc_new), cp_aftercall, ctx_5, o_old)
                sem.addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_5, o_old)
              })
            })
          })
          val h_6 = v_arg._2.foldLeft(HeapBot)((hh, l) => {
            hh + h_5.update(l, h_5(l).update("callee",
              PropValue(ObjectValue(Value(lset_fun), BoolTrue, BoolFalse, BoolTrue))))
          })

          ((h_6, ctx_5), (he, ctxe))
        }),
      ("jQuery.isArray" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v = getArgValue(h, ctx, args, "0")
          val b_1 =
            if (v._1 </ PValueBot) BoolFalse
            else BoolBot
          val b_2 = v._2.foldLeft[AbsBool](BoolBot)((_b, l) => {
            // XXX : Check whether it is correct or not
            if (!h.domIn(l)) BoolBot
            else  {
              val _b1 =
                if (AbsString.alpha("Array") <= h(l)("@class")._2._1._5) BoolTrue
                else BoolBot
              val _b2 =
                if (AbsString.alpha("Array") </ h(l)("@class")._2._1._5) BoolFalse
                else BoolBot
              _b + _b1 + _b2}})
          val b = b_1 + b_2
          if (b </ BoolBot)
            ((Helper.ReturnStore(h, Value(b)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.map.init" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          
          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val l_r = addrToLoc(addr1, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          
          val v_elems = getArgValue(h, ctx, args, "0")._2
          val v_callbackfn = getArgValue(h, ctx, args, "1")


          val o_ret = Helper.NewArrayObject(AbsNumber.alpha(0))

          // var ret = [];
          val h_2 = Helper.PropStore(h_1, SinglePureLocalLoc, AbsString.alpha("ret"), Value(l_r))
          val h_3 = h_2.update(l_r, o_ret)
          
          val b_isArray = v_elems.foldLeft(BoolBot)((b, l) => b + JQueryHelper.isArraylike(h_3, l))

          // var isArray = isArraylike(elems)
          val h_4 =  Helper.PropStore(h_3, SinglePureLocalLoc, AbsString.alpha("isArray"), Value(b_isArray))

          // If IsCallable(callbackfn) is false, throw a TypeError exception.
          val es =
            if (BoolFalse <= Helper.IsCallable(h_1, v_callbackfn))
              Set[Exception](TypeError)
            else
              ExceptionBot

          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)

          ((h_4, ctx_1), (he + h_e, ctxe + ctx_e))
        })),
      ("jQuery.map.call" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_this = h(SinglePureLocalLoc)("@this")._2

          val lset_env = h(SinglePureLocalLoc)("@env")._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)

          val v_elems = getArgValue(h, ctx, args, "0")
          val v_callbackfn = getArgValue(h, ctx, args, "1")
          val v_args = getArgValue(h, ctx, args, "2")

          val b_isArray = h(SinglePureLocalLoc)("isArray")._1._1._1._3

          val addr1 = cfg.getAPIAddress(addr_env, 1)
          val addr2 = cfg.getAPIAddress(addr_env, 2)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)


          val cond = v_callbackfn._2.exists((l) => BoolFalse <= Helper.IsCallable(h_2, l))
          val es =
            if (cond) Set[Exception](TypeError)
            else Set[Exception]()
          val (h_e, ctx_e) = Helper.RaiseException(h_2, ctx_2, es)
          val lset_f = v_callbackfn._2.filter((l) => BoolTrue <= Helper.IsCallable(h_2, l))
           
          val lset_elems = v_elems._2
          val (value, index) = b_isArray.getAbsCase match {
            case AbsBot => (ValueBot, ValueBot)
            case AbsSingle if BoolTrue <= b_isArray =>
              (lset_elems.foldLeft(ValueBot)((v, l) => v + Helper.Proto(h_2, l, absNumberToString(AbsNumber.naturalNumbers))), Value(AbsNumber.naturalNumbers))
            case _ => // AbsTop, AbsMulti, AbsSingle(false)
              (lset_elems.foldLeft(ValueBot)((v, l) => v + Helper.Proto(h_2, l, StrTop)), Value(StrTop))
          }
      
          val s_1 = (he + h_e, ctxe + ctx_e)
          if(value </ ValueBot) {
            // create Arguments object
            val o_arg =
              Helper.NewArgObject(AbsNumber.alpha(3))
                .update(AbsString.alpha("0"), PropValue(ObjectValue(value, T, T, T))) // elems[i]
                .update(AbsString.alpha("1"), PropValue(ObjectValue(index, T, T, T))) // i
                .update(AbsString.alpha("2"), PropValue(ObjectValue(v_args, T, T, T))) // arg

            val h_3 = h_2.update(l_r1, o_arg)
            val v_arg = Value(l_r1)

            val callee_this = Value(GlobalSingleton)

            val o_old = h_3(SinglePureLocalLoc)
            val cc_caller = cp._2
            val n_aftercall = cfg.getAftercallFromCall(cp._1)
            val cp_aftercall = (n_aftercall, cc_caller)
            val n_aftercatch = cfg.getAftercatchFromCall(cp._1)
            val cp_aftercatch = (n_aftercatch, cc_caller)
            lset_f.foreach((l_f) => {
              val o_f = h_3(l_f)
              o_f("@function")._3.foreach((fid) => {
                cc_caller.NewCallContext(h, cfg, fid, l_r2, callee_this._2).foreach((pair) => {
                  val (cc_new, o_new) = pair
                  val o_new2 = o_new.
                    update(cfg.getArgumentsName(fid),
                    PropValue(ObjectValue(v_arg, BoolTrue, BoolFalse, BoolFalse))).
                    update("@scope", o_f("@scope"))
                  sem.addCallEdge(cp, ((fid, LEntry), cc_new), ContextEmpty, o_new2)
                  sem.addReturnEdge(((fid, LExit), cc_new), cp_aftercall, ctx_2, o_old)
                  sem.addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_2, o_old)
                })
              })
            })
            val h_4 = v_arg._2.foldLeft(HeapBot)((hh, l) => {
              hh + h_3.update(l, h_3(l).update("callee",
                PropValue(ObjectValue(Value(lset_f), BoolTrue, BoolFalse, BoolTrue))))
            })
            
            ((h_4, ctx_2), s_1)
          }
          else {
            ((HeapBot, ContextBot), s_1)
          }
        })),

      ("jQuery.map.ret" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val b_isArray = h(SinglePureLocalLoc)("isArray")._1._1._1._3
          val index = b_isArray.getAbsCase match {
            case AbsBot => StrBot
            case AbsSingle if BoolTrue <= b_isArray =>
              absNumberToString(AbsNumber.naturalNumbers)
            case _ =>
              StrTop
          }
          if(index </ StrBot){
            val v_return = Helper.Proto(h, SinglePureLocalLoc, AbsString.alpha("temp"))
            val v_ret = Helper.Proto(h, SinglePureLocalLoc, AbsString.alpha("ret"))

            val h_1 = v_ret._2.foldLeft(h)((_h, l) => {
              var _h1 = Helper.PropStore(_h, l, index, v_return)
              Helper.PropStore(_h1, l, AbsString.alpha("length"), Value(AbsNumber.naturalNumbers))
            })
            val v_ret2 = Helper.Proto(h_1, SinglePureLocalLoc, AbsString.alpha("ret"))
            // TODO : flattening any nested arrays
            val h_2 = Helper.PropStore(h_1, SinglePureLocalLoc, AbsString.alpha("temp"), v_ret2)

            ((h_2, ctx), (he, ctxe))
          }
          else
            ((HeapBot, ContextBot), (he, ctxe))

        })),
      ("jQuery.extend" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val n_len = getArgValue(h, ctx, args, "length")._1._4
          val (h_ret, v_ret) =
            n_len.getSingle match {
              case Some(n) =>
                val list_args = (0 until n.toInt).foldLeft(List[Value]())((list, i) => list :+ getArgValue(h, ctx, args, i.toString))
                JQueryHelper.extend(h, list_args)
              case None =>
                if (n_len </ NumBot)
                // giveup, unsound
                  (h, Value(h(SinglePureLocalLoc)("@this")._2._2))
                else
                  (HeapBot, ValueBot)
            }
          if (!(h_ret <= HeapBot))
            ((Helper.ReturnStore(h_ret, v_ret), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.prototype.extend" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val n_len = getArgValue(h, ctx, args, "length")._1._4
          val (h_ret, v_ret) =
            n_len.getSingle match {
              case Some(n) =>
                val list_args = (0 until n.toInt).foldLeft(List[Value]())((list, i) => list :+ getArgValue(h, ctx, args, i.toString))
                JQueryHelper.extend(h, list_args)
              case None =>
                if (n_len </ NumBot)
                // giveup, unsound
                  (h, Value(h(SinglePureLocalLoc)("@this")._2._2))
                else
                  (HeapBot, ValueBot)
            }
          if (!(h_ret <= HeapBot))
            ((Helper.ReturnStore(h_ret, v_ret), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.trim" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val s_arg = getArgValue(h, ctx, args, "0")._1._5
          val v_ret = s_arg.getSingle match {
            case Some(_) =>
              Value(s_arg.trim)
            case None =>
              if (s_arg </ StrBot)
                Value(StrTop)
              else
                ValueBot
          }
          if (s_arg </ StrBot)
            ((Helper.ReturnStore(h, v_ret), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      ("jQuery.type" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val pv_arg = v_arg._1
          val s_1 =
            if (pv_arg._1 </ UndefBot)
              AbsString.alpha("undefined")
            else
              StrBot
          val s_2 =
            if (pv_arg._2 </ NullBot)
              AbsString.alpha("null")
            else
              StrBot
          val s_3 =
            if (pv_arg._3 </ BoolBot)
              AbsString.alpha("boolean")
            else
              StrBot
          val s_4 =
            if (pv_arg._4 </ NumBot)
              AbsString.alpha("number")
            else
              StrBot
          val s_5 =
            if (pv_arg._5 </ StrBot)
              AbsString.alpha("string")
            else
              StrBot
          // [[class]] ?
          val s_6 = v_arg._2.foldLeft[AbsString](StrBot)((s, l) => {
            val s_class = h(l)("@class")._2._1._5
            s_class.getSingle match {
              case Some(name) =>
                if (name.contains("Error"))
                  s + AbsString.alpha("error")
                else if (name.equals("Arguments"))
                  s + AbsString.alpha("object")
                else
                  s + s_class.toLowerCase
              case None =>
                // should not be happen
                s
            }

          })
          val s_ret = s_1 + s_2 + s_3 + s_4 + s_5 + s_6
          if (s_ret </ StrBot)
            ((Helper.ReturnStore(h, Value(s_ret)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        })),
      // should be refined
      ("jQuery.isFunction" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_arg = getArgValue(h, ctx, args, "0")
          val b1 = if(v_arg._1._1 </ UndefBot || v_arg._1._2 </ NullBot || v_arg._1._3 </ BoolBot || 
                      v_arg._1._4 </ NumBot || v_arg._1._5 </ StrBot) F else T
          //val b2 = if(v_arg._1._2 </ NullBot) F else T
          //val b3 = if(v_arg._1._3 </ BoolBot) F else T
          //val b4 = if(v_arg._1._4 </ NumBot) F else T
          //val b5 = if(v_arg._1._5 </ StrBot) F else T
          val b2 = v_arg._2.foldLeft(BoolBot:AbsBool)((b,l) => {
            if(AbsString.alpha("Function") <= h(l)(AbsString.alpha("@class"))._2._1._5 &&
               T <= h(l).domIn("@function") && h(l)("@function")._1._1._2.isEmpty) b + T
               else b + F
          })
            ((Helper.ReturnStore(h, Value(b1+b2)), ctx), (he, ctxe))
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
