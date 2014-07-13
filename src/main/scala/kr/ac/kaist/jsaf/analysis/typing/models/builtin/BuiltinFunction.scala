/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.builtin

import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, LEntry, LExit, LExitExc, InternalError, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.{PreSemanticsExpr => PSE, AccessHelper=>AH}
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.Shell

object BuiltinFunction extends ModelData {

  val ConstLoc = newSystemLoc("FunctionConst", Recent)
  //val ProtoLoc = newPreDefLoc("FunctionProto", Recent)

  private val prop_const: List[(String, AbsProperty)] = List(
    ("@class",                   AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto",                   AbsConstValue(PropValue(ObjectValue(Value(LocSet(FunctionProtoLoc)), F, F, F)))),
    ("@extensible",              AbsConstValue(PropValue(T))),
    ("@scope",                   AbsConstValue(PropValueNullTop)),
    ("@function",                AbsInternalFunc("Function.constructor")),
    ("@construct",               AbsInternalFunc("Function.constructor")),
    ("@hasinstance",             AbsConstValue(PropValueNullTop)),
    ("prototype",                AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F)))),
    ("length",                   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), F, F, F))))
  )

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(BoolTrue))),
    ("@function",            AbsInternalFunc("Function.prototype")),
    ("constructor",          AbsConstValue(PropValue(ObjectValue(FunctionProtoLoc, T, F, T)))),
    ("length",               AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0), F, F, F)))),
    ("toString",             AbsBuiltinFunc("Function.prototype.toString", 0)),
    ("apply",                AbsBuiltinFuncAftercall("Function.prototype.apply", 2)),
    ("call",                 AbsBuiltinFuncAftercall("Function.prototype.call", 1)),
    // ("bind",                 AbsBuiltinFuncAftercall("Function.prototype.bind", 1))
    ("bind",                 AbsBuiltinFunc("Function.prototype.bind", 1))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (ConstLoc, prop_const), (FunctionProtoLoc, prop_proto)
  )

  val locclone = Shell.params.opt_LocClone
  
  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("Function.constructor"-> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          // update "length" property to this object.
          val h_1 = lset_this.foldLeft(HeapBot)((_h,l) =>
            _h + Helper.PropStore(h, l, AbsString.alpha("length"), Value(NumTop)))
          ((h_1, ctx), (he, ctxe))
        })),
      ("Function.prototype"-> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("Function.prototype.toString"-> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val es =
            if (lset_this.exists((l) => h(l)("@class")._1._2._1._5 != AbsString.alpha("Function")))
              Set[Exception](TypeError)
            else
              ExceptionBot
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he + h_e, ctxe + ctx_e))
        })),
      ("Function.prototype.apply"-> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)
//          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent) // new pure local
          val l_r3 = addrToLoc(addr3, Recent) // newly generated arguments variable
          val (h_1, ctx_1) = (h, ctx) // Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, addr3)
          val lset_this = h_3(SinglePureLocalLoc)("@this")._1._2._2

          // 1.
//          val cond = lset_this.exists((l) => BoolFalse <= Helper.IsCallable(h_3,l))
          val cond = lset_this.exists(l => {
            if(BoolFalse <= Helper.IsCallable(h_3,l)) BoolFalse <= Helper.IsBound(h_3,l)
            else false
          })
          val es1 =
            if (cond) Set[Exception](TypeError)
            else Set[Exception]()
          val lset_f = lset_this.filter((l) => BoolTrue <= Helper.IsCallable(h_3,l))
          val lset_tarf = lset_this.filter(l => BoolTrue <= Helper.IsBound(h_3,l))

          // 2. create empty Arguments object
          val v_arg = getArgValue(h_3, ctx_3, args, "1")
          val o_arg1 =
            if (v_arg._1._1 </ UndefBot || v_arg._1._2 </ NullBot)  Helper.NewArgObject(AbsNumber.alpha(0))
            else  ObjBot

          // 3.
          val v_arg1 = Value(PValue(UndefBot, NullBot, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2)
          val (v_arg2, es2) =
            if (v_arg1._1 </ PValueBot)
              (Value(PValueBot, v_arg1._2), Set[Exception](TypeError))
            else
              (v_arg1, Set[Exception]())

          // 4. ~ 8. create Arguments object with argArray
          val o_arg2 =
            v_arg2._2.foldLeft(ObjBot)((_o, l) => {
              val n_arglen = Operator.ToUInt32(Helper.Proto(h_3, l, AbsString.alpha("length")))
              _o + (n_arglen.getAbsCase match {
                case AbsBot => ObjBot
                case _ => AbsNumber.getUIntSingle(n_arglen) match {
                  case Some(n_len) =>
                    val o = Helper.NewArgObject(AbsNumber.alpha(n_len))
                    (0 until n_len.toInt).foldLeft(o)((_o, i) => {
                      val value = Helper.Proto(h_3, l, AbsString.alpha(i.toString))
                      val propv = PropValue(ObjectValue(value, BoolTrue, BoolTrue, BoolTrue))
                      _o.update(i.toString, propv)
                    })
                  case _ =>
                    val value = Helper.Proto(h_3, l, NumStr)
                    val propv = PropValue(ObjectValue(value, BoolTrue, BoolTrue, BoolTrue))
                    Helper.NewArgObject(n_arglen).update(NumStr, propv)
                }
              })
            })
          val o_arg3_ = o_arg1 + o_arg2

          //////////////////////////////////////////////////
          // check whether there is a bound function or not
          val o_arg3 =
            if(lset_tarf.isEmpty) {
              o_arg3_
            } else {
              val lset_target_args = lset_tarf.foldLeft(LBot)((lset, l_tf) => {
                h_3(l_tf)("@bound_args")._1._2._2 ++ lset
              })
              val target_args = lset_target_args.foldLeft(ObjBot)((obj, l_ta) => obj + h_3(l_ta))
              Helper.concat(target_args, o_arg3_)
            }
          //////////////////////////////////////////////////

          val v_arg3 = Value(l_r3)
          val h_4 = h_3.update(l_r3, o_arg3)

          // *  in our own semantics, this value should be object
          val v_this = getArgValue(h_4, ctx_3, args, "0")
          val lset_argthis = Helper.getThis(h_4, v_this)
          val v_this2 =  Value(PValue(UndefBot, NullBot, v_this._1._3, v_this._1._4, v_this._1._5), lset_argthis)
          val (callee_this, h_5, ctx_5, es3) = Helper.toObject(h_4, ctx_3, v_this2, addr4)

          // XXX: stop if thisArg or arguments is LocSetBot(ValueBot)
          if(v_this2 == ValueBot || v_arg == ValueBot) ((h, ctx), (he, ctxe))
          else {
            val o_old = h_5(SinglePureLocalLoc)
            val cc_caller = cp._2
            val n_aftercall = cfg.getAftercallFromCall(cp._1)
            val cp_aftercall = (n_aftercall, cc_caller)
            val n_aftercatch = cfg.getAftercatchFromCall(cp._1)
            val cp_aftercatch = (n_aftercatch, cc_caller)
            
            //////////////////////////////////////////////////
            // check whether there is a bound function or not
            lset_tarf.foreach((l_f) => {
              val o_f = h_5(l_f)
              val l_this = o_f("@bound_this")._1._2._2
              val (fids, scope_locs) = o_f("@target_function")._1._2._2.foldLeft((FunSetBot, LocSetBot))((fidslocs, l) => ((fidslocs._1 ++ h_5(l)("@function")._1._3, fidslocs._2 ++ h_5(l)("@scope")._1._2._2)))
//              o_f("@target_function")._1._3.foreach((fid) => {
              fids.foreach((fid) => {
                cc_caller.NewCallContext(h, cfg, fid, l_r2, l_this).foreach((pair) => {
                  val (cc_new, o_new) = pair
                  val o_new2 = o_new.
                    update(cfg.getArgumentsName(fid),
                    PropValue(ObjectValue(v_arg3, BoolTrue, BoolFalse, BoolFalse))).
                    update("@scope", PropValue(ObjectValueBot, Value(scope_locs), FunSetBot))
//                    update("@scope", o_f("@scope")._1)
                  sem.addCallEdge(cp, ((fid,LEntry), cc_new), ContextEmpty, o_new2)
                  sem.addReturnEdge(((fid,LExit), cc_new), cp_aftercall, ctx_5, o_old)
                  sem.addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_5, o_old)
                })
              })
            })
            //////////////////////////////////////////////////
            
            lset_f.foreach((l_f) => {
              val o_f = h_5(l_f)
              o_f("@function")._1._3.foreach((fid) => {
                cc_caller.NewCallContext(h, cfg, fid, l_r2, callee_this._2).foreach((pair) => {
                  val (cc_new, o_new) = pair
                  val o_new2 = o_new.
                    update(cfg.getArgumentsName(fid),
                    PropValue(ObjectValue(v_arg3, BoolTrue, BoolFalse, BoolFalse))).
                    update("@scope", o_f("@scope")._1)
                  sem.addCallEdge(cp, ((fid,LEntry), cc_new), ContextEmpty, o_new2)
                  sem.addReturnEdge(((fid,LExit), cc_new), cp_aftercall, ctx_5, o_old)
                  sem.addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_5, o_old)
                })
              })
            })

            val (h_e, ctx_e) = Helper.RaiseException(h_5, ctx_5, es1++es2++es3)
            val s_1 = (he + h_e, ctxe + ctx_e)

            val h_6 = v_arg3._2.foldLeft(HeapBot)((hh, l) => {
              hh + h_5.update(l, h_5(l).update("callee",
                PropValue(ObjectValue(Value(lset_f ++ lset_tarf), BoolTrue, BoolFalse, BoolTrue))))
            })

            ((h_6, ctx_5), s_1)
          }})),
      ("Function.prototype.call"-> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr2_1 = if (locclone && Config.loopSensitive) Helper.extendAddr(addr2, Helper.callContextToNumber(cp._2)) else addr2
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)
          val l_r1 = addrToLoc(addr1, Recent) // newly generated arguments objects
          val l_r2 = addrToLoc(addr2_1, Recent) // new purelocal
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2_1)
          val lset_this = h_2(SinglePureLocalLoc)("@this")._1._2._2

          // 1.
//          val cond = lset_this.exists((l) => BoolFalse <= Helper.IsCallable(h_2,l))
          val cond = lset_this.exists(l => {
            if(BoolFalse <= Helper.IsCallable(h_1,l)) BoolFalse <= Helper.IsBound(h_1,l)
            else false
          })
          val es =
            if (cond) Set[Exception](TypeError)
            else Set[Exception]()
          val (h_e, ctx_e) = Helper.RaiseException(h_2, ctx_2, es)
          val lset_f = lset_this.filter((l) => BoolTrue <= Helper.IsCallable(h_2,l))
          val lset_tarf = lset_this.filter(l => BoolTrue <= Helper.IsBound(h_2,l))

          // 2., 3. create Arguments object
          val len = Operator.bopMinus(getArgValue(h_2, ctx_2, args, "length"), Value(AbsNumber.alpha(1)))
          val np = len._1._4
          val o_arg = Helper.NewArgObject(np)
          val o_arg_1_ =
            np.getSingle match {
              case Some(n) => (0 until n.toInt).foldLeft(o_arg)((_o, i) =>
                _o.update(AbsString.alpha(i.toString), PropValue(ObjectValue(getArgValue(h_2, ctx_2, args, (i+1).toString), BoolTrue, BoolTrue, BoolTrue))))
              case None =>
                if (np <= NumBot)
                  ObjBot
                else
                  o_arg.update(NumStr, PropValue(ObjectValue(getArgValueAbs(h_2, ctx_2, args, NumStr), BoolTrue, BoolTrue, BoolTrue)))
            }
          //////////////////////////////////////////////////
          // check whether there is a bound function or not
          val o_arg_1 =
            if(lset_tarf.isEmpty) {
              o_arg_1_
            } else {
              val lset_target_args = lset_tarf.foldLeft(LBot)((lset, l_tf) => {
                h_2(l_tf)("@bound_args")._1._2._2 ++ lset
              })
              val target_args = lset_target_args.foldLeft(ObjBot)((obj, l_ta) => obj + h_2(l_ta))
              Helper.concat(target_args, o_arg_1_)
            }
          //////////////////////////////////////////////////
          val h_3 = h_2.update(l_r1, o_arg_1)
          val v_arg = Value(l_r1)


          val v_this = getArgValue(h_3, ctx_2, args, "0")
          val lset_argthis = Helper.getThis(h_3, v_this)
          val v_this2 =  Value(PValue(UndefBot, NullBot, v_this._1._3, v_this._1._4, v_this._1._5), lset_argthis)
          val (callee_this, h_4, ctx_4, es3) = Helper.toObject(h_3, ctx_2, v_this2, addr4)


          val o_old = h_4(SinglePureLocalLoc)
          val cc_caller = cp._2
          val n_aftercall = cfg.getAftercallFromCall(cp._1)
          val cp_aftercall = (n_aftercall, cc_caller)
          val n_aftercatch = cfg.getAftercatchFromCall(cp._1)
          val cp_aftercatch = (n_aftercatch, cc_caller)
          //////////////////////////////////////////////////
          // check whether there is a bound function or not
          lset_tarf.foreach((l_f) => {
            val o_f = h_4(l_f)
            val l_this = o_f("@bound_this")._1._2._2
            val (fids, scope_locs) = o_f("@target_function")._1._2._2.foldLeft((FunSetBot, LocSetBot))((fidslocs, l) => ((fidslocs._1 ++ h_4(l)("@function")._1._3, fidslocs._2 ++ h_4(l)("@scope")._1._2._2)))
//            o_f("@target_function")._1._3.foreach((fid) => {
            fids.foreach((fid) => {
              cc_caller.NewCallContext(h, cfg, fid, l_r2, l_this, Some(addr2)).foreach((pair) => {
                val (cc_new, o_new) = pair
                val o_new2 = o_new.
                  update(cfg.getArgumentsName(fid),
                  PropValue(ObjectValue(v_arg, BoolTrue, BoolFalse, BoolFalse))).
//                  update("@scope", o_f("@scope")._1)
                    update("@scope", PropValue(ObjectValueBot, Value(scope_locs), FunSetBot))
                sem.addCallEdge(cp, ((fid,LEntry), cc_new), ContextEmpty, o_new2)
                sem.addReturnEdge(((fid,LExit), cc_new), cp_aftercall, ctx_4, o_old)
                sem.addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_4, o_old)
              })
            })
          })
          //////////////////////////////////////////////////
          lset_f.foreach((l_f) => {
            val o_f = h_4(l_f)
            o_f("@function")._1._3.foreach((fid) => {
              cc_caller.NewCallContext(h, cfg, fid, l_r2, callee_this._2, Some(addr2)).foreach((pair) => {
                val (cc_new, o_new) = pair
                val o_new2 = o_new.
                  update(cfg.getArgumentsName(fid),
                  PropValue(ObjectValue(v_arg, BoolTrue, BoolFalse, BoolFalse))).
                  update("@scope", o_f("@scope")._1)
                sem.addCallEdge(cp, ((fid,LEntry), cc_new), ContextEmpty, o_new2)
                sem.addReturnEdge(((fid,LExit), cc_new), cp_aftercall, ctx_4, o_old)
                sem.addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_4, o_old)
              })
            })
          })
          val h_5 = v_arg._2.foldLeft(HeapBot)((hh, l) => {
            hh + h_4.update(l, h_4(l).update("callee",
              PropValue(ObjectValue(Value(lset_f ++ lset_tarf), BoolTrue, BoolFalse, BoolTrue))))
          })
          val s_1 = (he + h_e, ctxe + ctx_e)
          ((h_5, ctx_4), s_1)
        })),
      ("Function.prototype.bind"-> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0) // new internal list of argument values
          val addr2 = cfg.getAPIAddress(addr_env, 1) // return object(bound function object)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, addr2)

          val lset_this = h_2(SinglePureLocalLoc)("@this")._1._2._2

          // 1. && 2.
          val cond = lset_this.exists((l) => !((BoolFalse <= (Helper.IsCallable(h_2,l))) || (BoolFalse <=  Helper.IsBound(h_2, l))))
          val es =
            if (cond) Set[Exception](TypeError)
            else Set[Exception]()
          val (h_e, ctx_e) = Helper.RaiseException(h_2, ctx_2, es)
          val lset_f = lset_this.filter((l) => BoolTrue <= Helper.IsCallable(h_2,l))
          val lset_tarf = lset_this.filter((l) => BoolTrue <= Helper.IsBound(h_2,l))
          // compute target function fids
//          val target_fids = (lset_f++lset_tarf).foldLeft(FunSetBot)((fidset, l_f) => h_2(l_f)("@function")._1._3 ++ h_2(l_f)("@target_function")._1._3 ++ fidset)
          // compute target locations which is function object
          val target_functions_locs = lset_f++lset_tarf.foldLeft(LocSetBot)((locs, l_f) => {
            h_2(l_f)("@target_function")._1._2._2 ++
            locs
          })
          
          // 3. new internal list of all of the argument values
          // remove this binding property('0') from length
          val origin_len = (lset_f ++ lset_tarf).foldLeft(ValueBot)((v_len, l_f) => h_2(l_f)("length")._1._1._1 + v_len)
          val len = 
              Operator.bopMinus(getArgValue(h_2, ctx_2, args, "length"), Value(AbsNumber.alpha(1)))
          val np = len._1._4
          val o_arg = Helper.NewArgObject(np)
          
          // compute previously bounded "@bound_args" by bind function
          val prev_bound_arg =
            if(!lset_tarf.isEmpty) {
              lset_tarf.foldLeft(ObjBot)((obj, l_f) => {
                h_2(l_f)("@bound_args")._1._2._2.foldLeft(obj)((obj_, l_arg) => obj_ + h_2(l_arg))
              })
            }
            else {
              // no bounded arguments
              Helper.NewArgObject(AbsNumber.alpha(0))
            }
          
          val o_arg_1 = 
            np.getSingle match {
              case Some(n) => (0 until n.toInt).foldLeft(o_arg)((_o, i) =>
                _o.update(AbsString.alpha(i.toString), PropValue(ObjectValue(getArgValue(h_2, ctx_2, args, (i+1).toString), BoolTrue, BoolTrue, BoolTrue))))
              case None =>
                if (np <= NumBot)
                  ObjBot
                else
                  o_arg.update(NumStr, PropValue(ObjectValue(getArgValueAbs(h_2, ctx_2, args, NumStr), BoolTrue, BoolTrue, BoolTrue)))
            }
          val new_arg = Helper.concat(prev_bound_arg, o_arg_1)
          val h_3 = h_2.update(l_r1, new_arg)
          val v_arg = Value(l_r1)

          val v_this = getArgValue(h_3, ctx_2, args, "0") // + prev_this

          // 15. length ~ 17.
          // origin_len - len
          val o_f_len = Operator.bopMinus(origin_len, len)

          // 4. F be a new native ECAMScript object
          // make bound function object 7~15
          val o_f = ObjEmpty.
//                        update("@target_function", PropValue(ObjectValueBot, ValueBot, target_fids)). // 7. [[TargetFunction]]
                        update("@target_function", PropValue(ObjectValueBot, Value(PValueBot, target_functions_locs), FunSetBot)). // 7. [[TargetFunction]]
                        update("@bound_this", PropValue(v_this)). // 8. [[BoundThis]]
                        update("@bound_args", PropValue(v_arg)). // 9. [[BoundArgs]]
                        update("@class", PropValue(AbsString.alpha("Function"))). // 10. [[Class]]
                        update("@proto", PropValue(ObjectValue(Value(FunctionProtoLoc), BoolFalse, BoolFalse, BoolFalse))). // 11. [[Prototype]]
                        update("length", PropValue(ObjectValue(o_f_len, BoolTrue, BoolFalse, BoolFalse))). // 15~17. length
                        update("@extensible", PropValue(BoolTrue)) // 18. [[Extensible]]
          // 12. [[Call]] affects semantics of CFGCall...?
          // 13. [[Construct]] ??
          // 14. [[HasInstance]]
          // 19. ~ 21 is for getter/setter of caller/arguments
          
          val h_4 = h_3.update(l_r2, o_f)
          val s_1 = (he + h_e, ctxe + ctx_e)
          ((Helper.ReturnStore(h_4, Value(l_r2)), ctx), s_1)
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("Function.constructor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          // update "length" property to this object.
          val h_1 = lset_this.foldLeft(HeapBot)((_h,l) =>
            _h + PreHelper.PropStore(h, l, AbsString.alpha("length"), Value(NumTop)))
          ((h_1, ctx), (he, ctxe))
        })),
      ("Function.prototype" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(UndefTop)), ctx), (he, ctxe))
        })),
      ("Function.prototype.toString"-> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_this = h(PureLocalLoc)("@this")._1._2._2
          val es =
            if (lset_this.exists((l) => h(l)("@class")._1._2._1._5 != AbsString.alpha("Function")))
              Set[Exception](TypeError)
            else
              ExceptionBot
          val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(StrTop)), ctx), (he + h_e, ctxe + ctx_e))
        })),
      ("Function.prototype.apply" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_env = h(PureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val l_r3 = addrToLoc(addr3, Recent)
          val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = PreHelper.Oldify(h_1, ctx_1, addr2)
          val (h_3, ctx_3) = PreHelper.Oldify(h_2, ctx_2, addr3)
          val lset_this = h_3(PureLocalLoc)("@this")._1._2._2

          // 1.
          val cond = lset_this.exists((l) => BoolFalse <= PreHelper.IsCallable(h_3,l))
          val es1 =
            if (cond) Set[Exception](TypeError)
            else Set[Exception]()
          val lset_f = lset_this.filter((l) => BoolTrue <= PreHelper.IsCallable(h_3,l))

          // 2. create empty Arguments object
          val v_arg = getArgValue_pre(h_3, ctx_3, args, "1", PureLocalLoc)
          val o_arg1 =
            if (v_arg._1._1 </ UndefBot || v_arg._1._2 </ NullBot)  PreHelper.NewArgObject(AbsNumber.alpha(0))
            else  ObjBot

          // 3.
          val v_arg1 = Value(PValue(UndefBot, NullBot, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2)
          val (v_arg2, es2) =
            if (v_arg1._1 </ PValueBot)
              (Value(PValueBot, v_arg1._2), Set[Exception](TypeError))
            else
              (v_arg1, Set[Exception]())

          // 4. ~ 8. create Arguments object with argArray
          val o_arg2 =
            v_arg2._2.foldLeft(ObjBot)((_o, l) => {
              val n_arglen = Operator.ToUInt32(PreHelper.Proto(h_3, l, AbsString.alpha("length")))
              _o + (n_arglen.getAbsCase match {
                case AbsBot => ObjBot
                case _ => AbsNumber.getUIntSingle(n_arglen) match {
                  case Some(n_len) =>
                    val o = PreHelper.NewArgObject(AbsNumber.alpha(n_len))
                    (0 until n_len.toInt).foldLeft(o)((_o, i) => {
                      val value = PreHelper.Proto(h_3, l, AbsString.alpha(i.toString))
                      val propv = PropValue(ObjectValue(value, BoolTrue, BoolTrue, BoolTrue))
                      _o.update(i.toString, propv)
                    })
                  case _ =>
                    val value = PreHelper.Proto(h_3, l, NumStr)
                    val propv = PropValue(ObjectValue(value, BoolTrue, BoolTrue, BoolTrue))
                    PreHelper.NewArgObject(n_arglen).update(NumStr, propv)
                }
              })
            })
          val o_arg3 = o_arg1 + o_arg2

          val v_arg3 = Value(l_r3)
          val h_4 = h_3.update(l_r3, o_arg3)

          // *  in our own semantics, this value should be object
          val v_this = getArgValue_pre(h_4, ctx_3, args, "0", PureLocalLoc)
          val lset_argthis = PreHelper.getThis(h_4, v_this)
          val v_this2 =  Value(PValue(UndefBot, NullBot, v_this._1._3, v_this._1._4, v_this._1._5), lset_argthis)
          val (callee_this, h_5, ctx_5, es3) = PreHelper.toObject(h_4, ctx_3, v_this2, addr4)

          val o_old = h_5(PureLocalLoc)
          val cc_caller = cp._2
          val n_aftercall = cfg.getAftercallFromCall(cp._1)
          val cp_aftercall = (n_aftercall, cc_caller)
          val n_aftercatch = cfg.getAftercatchFromCall(cp._1)
          val cp_aftercatch = (n_aftercatch, cc_caller)
          lset_f.foreach((l_f) => {
            val o_f = h_5(l_f)
            o_f("@function")._1._3.foreach((fid) => {
              cc_caller.NewCallContext(h, cfg, fid, l_r2, callee_this._2).foreach((pair) => {
                val (cc_new, o_new) = pair
                val o_new2 = o_new.
                  update(cfg.getArgumentsName(fid),
                  PropValue(ObjectValue(v_arg3, BoolTrue, BoolFalse, BoolFalse))).
                  update("@scope", o_f("@scope")._1)
                sem.addCallEdge(cp, ((fid,LEntry), cc_new), ContextEmpty, o_new2)
                sem.addReturnEdge(((fid,LExit), cc_new), cp_aftercall, ctx_5, o_old)
                sem.addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_5, o_old)
              })
            })
          })

          val (h_e, ctx_e) = PreHelper.RaiseException(h_5, ctx_5, PureLocalLoc, es1++es2++es3)
          val h_6 = v_arg3._2.foldLeft(h_e)((hh, l) => {
            hh.update(l, hh(l).update("callee",
              PropValue(ObjectValue(Value(lset_f), BoolTrue, BoolFalse, BoolTrue))))
          })

          ((h_6, ctx_e), (he, ctxe))
        })),
      ("Function.prototype.call" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val lset_env = h(PureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          val addr_env = (cp._1._1, set_addr.head)
          val addr1 = cfg.getAPIAddress(addr_env, 0)
          val addr2 = cfg.getAPIAddress(addr_env, 1)
          val addr3 = cfg.getAPIAddress(addr_env, 2)
          val addr4 = cfg.getAPIAddress(addr_env, 3)
          val l_r1 = addrToLoc(addr1, Recent)
          val l_r2 = addrToLoc(addr2, Recent)
          val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, addr1)
          val (h_2, ctx_2) = PreHelper.Oldify(h_1, ctx_1, addr2)
          val lset_this = h_2(PureLocalLoc)("@this")._1._2._2

          // 1.
          val cond = lset_this.exists((l) => BoolFalse <= PreHelper.IsCallable(h_2,l))
          val es =
            if (cond) Set[Exception](TypeError)
            else Set[Exception]()
          val (h_e, ctx_e) = PreHelper.RaiseException(h_2, ctx_2, PureLocalLoc, es)
          val lset_f = lset_this.filter((l) => BoolTrue <= PreHelper.IsCallable(h_2,l))


          // 2., 3. create Arguments object
          val len = Operator.bopMinus(getArgValue_pre(h_2, ctx_2, args, "length", PureLocalLoc), Value(AbsNumber.alpha(1)))
          val np = len._1._4
          val o_arg = PreHelper.NewArgObject(np)
          val o_arg_1 =
            np.getSingle match {
              case Some(n) => (0 until n.toInt).foldLeft(o_arg)((_o, i) =>
                _o.update(AbsString.alpha(i.toString), PropValue(ObjectValue(getArgValue_pre(h_2, ctx_2, args, (i+1).toString, PureLocalLoc), BoolTrue, BoolTrue, BoolTrue))))
              case None =>
                val argLocSet = PSE.V(args, h, ctx, PureLocalLoc)._1._2
                argLocSet.foldLeft(o_arg)((_o, l) => _o.update(NumStr, h_2(l)(NumStr)._1))
            }
          val h_3 = h_2.update(l_r1, o_arg_1)
          val v_arg = Value(l_r1)


          val v_this = getArgValue_pre(h_3, ctx_2, args, "0", PureLocalLoc)
          val lset_argthis = PreHelper.getThis(h_3, v_this)
          val v_this2 =  Value(PValue(UndefBot, NullBot, v_this._1._3, v_this._1._4, v_this._1._5), lset_argthis)
          val (callee_this, h_4, ctx_4, es3) = PreHelper.toObject(h_3, ctx_2, v_this2, addr4)


          val o_old = h_4(PureLocalLoc)
          val cc_caller = cp._2
          val n_aftercall = cfg.getAftercallFromCall(cp._1)
          val cp_aftercall = (n_aftercall, cc_caller)
          val n_aftercatch = cfg.getAftercatchFromCall(cp._1)
          val cp_aftercatch = (n_aftercatch, cc_caller)
          lset_f.foreach((l_f) => {
            val o_f = h_4(l_f)
            o_f("@function")._1._3.foreach((fid) => {
              cc_caller.NewCallContext(h, cfg, fid, l_r2, callee_this._2).foreach((pair) => {
                val (cc_new, o_new) = pair
                val o_new2 = o_new.
                  update(cfg.getArgumentsName(fid),
                  PropValue(ObjectValue(v_arg, BoolTrue, BoolFalse, BoolFalse))).
                  update("@scope", o_f("@scope")._1)
                sem.addCallEdge(cp, ((fid,LEntry), cc_new), ContextEmpty, o_new2)
                sem.addReturnEdge(((fid,LExit), cc_new), cp_aftercall, ctx_4, o_old)
                sem.addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_4, o_old)
              })
            })
          })
          val h_5 = v_arg._2.foldLeft(h_4)((hh, l) => {
            hh.update(l, hh(l).update("callee",
              PropValue(ObjectValue(Value(lset_f), BoolTrue, BoolFalse, BoolTrue))))
          })

          val s_1 = (he + h_e, ctxe + ctx_e)
          ((h_5, ctx_4), s_1)

        }))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      ("Function.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          lset_this.foldLeft(LPBot)((lpset, l) =>
            lpset ++ AH.PropStore_def(h, l, AbsString.alpha("length")))
        })),
      ("Function.prototype" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Function.prototype.toString" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val es =
            if (lset_this.exists((l) => h(l)("@class")._1._2._1._5 != AbsString.alpha("Function")))
              Set[Exception](TypeError)
            else
              ExceptionBot
          AH.RaiseException_def(es) + (SinglePureLocalLoc, "@return")
        })),
      ("Function.prototype.apply" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          //val addr1 = cfg.getAPIAddress(addr_env, 0)
          //val addr2 = cfg.getAPIAddress(addr_env, 1)
          //val addr3 = cfg.getAPIAddress(addr_env, 2)
          //val addr4 = cfg.getAPIAddress(addr_env, 3)
          //val LP1 = AH.Oldify_def(h, ctx, addr1) ++ AH.Oldify_def(h, ctx, addr2) ++ AH.Oldify_def(h, ctx, addr3)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AH.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 0))
              ++ AH.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 1))
              ++ AH.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 2))
          )
          val cond = lset_this.exists((l) => BoolFalse <= Helper.IsCallable(h,l))
          val es1 =
            if (cond) Set[Exception](TypeError)
            else Set[Exception]()

          val v_arg = getArgValue(h, ctx, args, "1")
          val v_arg1 = Value(PValue(UndefBot, NullBot, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2)
          val (v_arg2, es2) =
            if (v_arg1._1 </ PValueBot)
              (Value(PValueBot, v_arg1._2), Set[Exception](TypeError))
            else
              (v_arg1, Set[Exception]())

          val LP2 =
            if (!v_arg2._2.isEmpty) {
              v_arg2._2.foldLeft(LPBot)((lpset, l) => {
                val n_arglen = Operator.ToUInt32(Helper.Proto(h, l, AbsString.alpha("length")))
                val _LP1 = n_arglen.getAbsCase match {
                  case AbsBot => LPBot
                  case _ => AbsNumber.getUIntSingle(n_arglen) match {
                    case Some(n_len) =>
                      (0 until n_len.toInt).foldLeft(LPBot)((_lpset, i) =>
                        _lpset ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 2),Recent), i.toString)))
                    case _ =>
                      set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.absPair(h, addrToLoc(cfg.getAPIAddress((fid, a), 2),Recent), NumStr))
                  }
                }
                lpset ++ _LP1
              })
            }
            else
              LPBot

          val v_this = getArgValue(h, ctx, args, "0")
          val lset_argthis = Helper.getThis(h, v_this)
          val v_this2 =  Value(PValue(UndefBot, NullBot, v_this._1._3, v_this._1._4, v_this._1._5), lset_argthis)
          //val LP3 = AH.toObject_def(h, ctx, v_this2, addr4)
          val LP3 = set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.toObject_def(h, ctx, v_this2, cfg.getAPIAddress((fid, a), 3)))
          //val LP4 = LPSet((l_r3, "callee"))
          val LP4 = set_addr.foldLeft(LPBot)((lp, a) => lp ++ LPSet(addrToLoc(cfg.getAPIAddress((fid, a), 3),Recent), "callee"))

          val LP5 = AH.RaiseException_def(es1 ++ es2)

          LP1 ++ LP2 ++ LP3 ++ LP4 ++ LP5 + (SinglePureLocalLoc, "@return")
        })),
      ("Function.prototype.call" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          //val addr_env = set_addr.head
          //val addr1 = cfg.getAPIAddress(addr_env, 0)
          //val addr2 = cfg.getAPIAddress(addr_env, 1)
          //val addr3 = cfg.getAPIAddress(addr_env, 2)
          //val addr4 = cfg.getAPIAddress(addr_env, 3)
          //val l_r1 = addrToLoc(addr1, Recent)
          //val l_r2 = addrToLoc(addr2, Recent)
          //val LP1 = AH.Oldify_def(h, ctx, addr1) ++ AH.Oldify_def(h, ctx, addr2)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AH.Oldify_def(h, ctx, cfg.getAPIAddress((fid, a), 0))
              ++ AH.Oldify_def(h, ctx, cfg.getAPIAddress((fid,a), 1))
          )

          // 1.
          val cond = lset_this.exists((l) => BoolFalse <= Helper.IsCallable(h,l))
          val es =
            if (cond) Set[Exception](TypeError)
            else Set[Exception]()

          // 2., 3. create Arguments object
          val len = Operator.bopMinus(getArgValue(h, ctx, args, "length"), Value(AbsNumber.alpha(1)))
          val LP2 = len._1._4.getSingle match {
            case Some(n) =>
              (0 until n.toInt).foldLeft(LPBot)((lpset, i) =>
                lpset  ++ set_addr.foldLeft(LPBot)((lp, a) => lp + (addrToLoc(cfg.getAPIAddress((fid, a), 0),Recent), i.toString)))
            case None => LPBot
          }

          val v_this = getArgValue(h, ctx, args, "0")
          val lset_argthis = Helper.getThis(h, v_this)
          val v_this2 =  Value(PValue(UndefBot, NullBot, v_this._1._3, v_this._1._4, v_this._1._5), lset_argthis)
          //val LP3 = AH.toObject_def(h, ctx, v_this2, addr4)
          val LP3 = set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.toObject_def(h, ctx, v_this2, cfg.getAPIAddress((fid, a), 3)))

          //val LP4 = LPSet((l_r1, "callee"))
          val LP4 = set_addr.foldLeft(LPBot)((lp, a) => lp ++ LPSet(addrToLoc(cfg.getAPIAddress((fid, a), 0),Recent), "callee"))

          val LP5 = AH.RaiseException_def(es)

          LP1 ++ LP2 ++ LP3 ++ LP4 ++ LP5 + (SinglePureLocalLoc, "@return")
        }))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      ("Function.constructor" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          lset_this.foldLeft(LPBot)((lpset, l) =>
            lpset ++ AH.PropStore_use(h, l, AbsString.alpha("length"))) + (SinglePureLocalLoc, "@this")
        })),
      ("Function.prototype" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Function.prototype.toString" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val es =
            if (lset_this.exists((l) => h(l)("@class")._1._2._1._5 != AbsString.alpha("Function")))
              Set[Exception](TypeError)
            else
              ExceptionBot
          val LP1 = lset_this.foldLeft(LPBot)((lpset, l) => lpset + (l, "@class"))
          val LP2 = AH.RaiseException_use(es)
          LP1 ++ LP2 + (SinglePureLocalLoc, "@return") + (SinglePureLocalLoc, "@this")
        })),
      ("Function.prototype.apply" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          //val addr1 = cfg.getAPIAddress(addr_env, 0)
          //val addr2 = cfg.getAPIAddress(addr_env, 1)
          //val addr3 = cfg.getAPIAddress(addr_env, 2)
          //val addr4 = cfg.getAPIAddress(addr_env, 3)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AH.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 0))
              ++ AH.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 1))
              ++ AH.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 2))
          )

          // 1.
          val cond = lset_this.exists((l) => BoolFalse <= Helper.IsCallable(h,l))
          val es1 =
            if (cond) Set[Exception](TypeError)
            else Set[Exception]()
          val lset_f = lset_this.filter((l) => BoolTrue <= Helper.IsCallable(h,l))

          // 2. create empty Arguments object
          val v_arg = getArgValue(h, ctx, args, "1")
          val LP2 = getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1")

          // 3.
          val v_arg1 = Value(PValue(UndefBot, NullBot, v_arg._1._3, v_arg._1._4, v_arg._1._5), v_arg._2)
          val (v_arg2, es2) =
            if (v_arg1._1 </ PValueBot)
              (Value(PValueBot, v_arg1._2), Set[Exception](TypeError))
            else
              (v_arg1, Set[Exception]())

          // 4. ~ 8. create Arguments object with argArray
          val LP3 =
            if (!v_arg2._2.isEmpty) {
              v_arg2._2.foldLeft(LPBot)((lpset, l) => {
                val n_arglen = Operator.ToUInt32(Helper.Proto(h, l, AbsString.alpha("length")))
                val _LP1 = AH.Proto_use(h, l, AbsString.alpha("length"))
                val _LP2 = n_arglen.getAbsCase match {
                  case AbsBot => LPBot
                  case _ => AbsNumber.getUIntSingle(n_arglen) match {
                    case Some(n_len) =>
                      (0 until n_len.toInt).foldLeft(LPBot)((_lpset, i) =>
                        _lpset ++ AH.Proto_use(h, l, AbsString.alpha(i.toString)))
                    case _ => AH.Proto_use(h, l, NumStr)
                  }
                }
                lpset ++ _LP1 ++ _LP2
              })
            }
            else
              LPBot

          // *  in our own semantics, this value should be object
          val v_this = getArgValue(h, ctx, args, "0")
          val lset_argthis = Helper.getThis(h, v_this)
          val v_this2 =  Value(PValue(UndefBot, NullBot, v_this._1._3, v_this._1._4, v_this._1._5), lset_argthis)
          val LP4 = set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.toObject_use(h, ctx, v_this2, cfg.getAPIAddress((fid, a), 3)))

          val LP5 = AH.getThis_use(h, v_this)

          val LP6 = lset_f.foldLeft(LPBot)((lpset, l_f) => lpset + (l_f, "@function") + (l_f, "@scope"))

          val LP7 = AH.RaiseException_def(es1 ++ es2)

          LP1 ++ LP2 ++ LP3 ++ LP4 ++ LP5 ++ LP6 ++ LP7 +
            (SinglePureLocalLoc, "@return") + (ContextLoc, "1") + (SinglePureLocalLoc, "@this")
        })),
      ("Function.prototype.call" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val lset_this = h(SinglePureLocalLoc)("@this")._1._2._2
          val lset_env = h(SinglePureLocalLoc)("@env")._1._2._2
          val set_addr = lset_env.foldLeft[Set[Address]](Set())((a, l) => a + locToAddr(l))
          //if (set_addr.size > 1) throw new InternalError("API heap allocation: Size of env address is " + set_addr.size)
          //val addr1 = cfg.getAPIAddress(addr_env, 0)
          //val addr2 = cfg.getAPIAddress(addr_env, 1)
          //val addr3 = cfg.getAPIAddress(addr_env, 2)
          //val addr4 = cfg.getAPIAddress(addr_env, 3)
          //val LP1 = AH.Oldify_use(h, ctx, addr1) ++ AH.Oldify_use(h, ctx, addr2)
          val LP1 = set_addr.foldLeft(LPBot)((lp, a) =>
            lp ++ AH.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 0))
              ++ AH.Oldify_use(h, ctx, cfg.getAPIAddress((fid, a), 1))
          )

          // 1.
          val cond = lset_this.exists((l) => BoolFalse <= Helper.IsCallable(h,l))
          val es =
            if (cond) Set[Exception](TypeError)
            else Set[Exception]()
          val lset_f = lset_this.filter((l) => BoolTrue <= Helper.IsCallable(h,l))

          // 2., 3. create Arguments object
          val len = Operator.bopMinus(getArgValue(h, ctx, args, "length"), Value(AbsNumber.alpha(1)))
          val LP2 = getArgValue_use(h, ctx, args, "length") ++ getArgValue_use(h, ctx, args, "0")
          val LP3 =
            len._1._4.getSingle match {
              case Some(n) =>
                (0 until n.toInt).foldLeft(LPBot)((lpset, i) =>
                  lpset ++ getArgValue_use(h, ctx, args, (i+1).toString))
              case None => LPBot
            }

          val v_this = getArgValue(h, ctx, args, "0")
          val lset_argthis = Helper.getThis(h, v_this)
          val v_this2 =  Value(PValue(UndefBot, NullBot, v_this._1._3, v_this._1._4, v_this._1._5), lset_argthis)

          val LP4 = set_addr.foldLeft(LPBot)((lp, a) => lp ++ AH.toObject_use(h, ctx, v_this2, cfg.getAPIAddress((fid, a), 3)))

          val LP5 = AH.getThis_use(h, v_this)

          val LP6 = lset_f.foldLeft(LPBot)((lpset, l_f) => lpset + (l_f, "@function") + (l_f, "@scope"))

          val LP7 = AH.RaiseException_def(es)

          LP1 ++ LP2 ++ LP3 ++ LP4 ++ LP5 ++ LP6 ++ LP7 + (SinglePureLocalLoc, "@return") + (ContextLoc, "1") + (SinglePureLocalLoc, "@this")
        }))
    )
  }
}
