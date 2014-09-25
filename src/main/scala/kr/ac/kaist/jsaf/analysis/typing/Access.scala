/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing

import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper=>AH}
import kr.ac.kaist.jsaf.analysis.typing.{SemanticsExpr => SE}
import kr.ac.kaist.jsaf.scala_src.useful.WorkTrait
import scala.collection.immutable.HashMap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.domain.State
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.models.ModelManager
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

class Access(cfg: CFG, cg: Map[CFGInst, Set[FunctionId]], state_org: State) {
  val dusetLock: AnyRef = new AnyRef()
  var duset: DUSet = HashMap()
  def result = duset

  def getState(fid: FunctionId): State = {
    val ph = state_org._1
    val h = ph.update(SinglePureLocalLoc, ph(cfg.getMergedPureLocal(fid)))
    State(h, state_org._2)
  }

  def process(): Unit = process(false)
  def process(quiet: Boolean): Unit = {
    val reachableFuncs = cg.foldLeft(Set(cfg.getGlobalFId))((s, kv) => s ++ kv._2)
    val programNodes = reachableFuncs.foldLeft(List[Node]())((S, fid) => S ++ cfg.getReachableNodes(fid))
    if (!quiet)
      System.out.println("# Reachable Nodes: " + programNodes.length);

    // Initialize WorkManager
    if(Shell.params.opt_MultiThread) Shell.workManager.initialize() // Multi-thread
    else Shell.workManager.initialize(null, 1) // Single-thread

    // Push works
    for(node <- programNodes) Shell.workManager.pushWork(new AccessWork(cfg, node))

    // Wait until all works are finished.
    Shell.workManager.waitFinishEvent()

    // Deinitialize WorkManager
    Shell.workManager.deinitialize()

    if (!quiet)
      System.err.println("  The size of du: "+ duset.foldLeft(0)((i, pair) => i + (pair._2._1.toSet.size) + (pair._2._2.toSet.size)))
  }

  class AccessWork(cfg:CFG, node: Node) extends WorkTrait {
    override def doit(): Unit = {
      val state = getState(node._1)
      val du: (Node, (LPSet, LPSet)) = cfg.getCmd(node) match {
        case Entry => {
          val (fid, l) = node
          val h = state._1
          val ctx = state._2
          val x_argvars = cfg.getArgVars(fid)
          val x_localvars = cfg.getLocalVars(fid)
          val lset_arg = h(SinglePureLocalLoc)(cfg.getArgumentsName(fid))._1._1._2
          val env = h(SinglePureLocalLoc)("@env")._2._2
          var i = 0
          // def
          val LPd_1 = x_argvars.foldLeft(LPBot)((lp, x) => {
            lp ++ AH.CreateMutableBinding_def(h, env, x)
          })
          val LPd_2 = x_localvars.foldLeft(LPBot)((lp, x) => {
            lp ++ AH.CreateMutableBinding_def(h, env, x)
          })
          val LPd_3 = LPSet(Set((ContextLoc, "3"), (ContextLoc, "4")))
          val LPd_4 = LPSet((SinglePureLocalLoc, cfg.getArgumentsName(fid)))
          val LPd_5 = env.foldLeft(LPBot)((lp, l) => lp + ((l, "@outer")))

          val LPd = LPd_1 ++ LPd_2 ++ LPd_3 ++ LPd_4 ++ LPd_5
          // use
          val LPu_1 = LPSet((SinglePureLocalLoc, cfg.getArgumentsName(fid)))
          val LPu_2 = x_argvars.foldLeft(LPBot)((lp, x) => {
            val lp_3 = lset_arg.foldLeft(lp)((lp_2, l_arg) => {
              lp_2 ++ AH.Proto_use(h, l_arg, AbsString.alpha(i.toString))
            })
            i = i + 1
            val lp_4 = AH.CreateMutableBinding_use(h, env, x)
            lp_3 ++ lp_4
          })
          val LPu_3 = x_localvars.foldLeft(LPBot)((lp, x) => {
            lp ++ AH.CreateMutableBinding_use(h, env, x)
          })
          val LPu = LPu_1 ++ LPu_2 ++ LPu_3
          (node, ((LPd, LPu)))
        }
        case Exit => {
          val LPu_1 = LPSet((SinglePureLocalLoc, "@return"))
          val LPu_2 =
            if (node._1 == cfg.getGlobalFId)
              LPSet((GlobalLoc, "__result"))
            else
              LPBot
          val LPu_3 = LPSet(Set((ContextLoc, "3"), (ContextLoc, "4")))
          val LPu = LPu_1 ++ LPu_2 ++ LPu_3

          // Note: #PureLocal must not be passed between functions.
          val hold_purelocal = LPSet((SinglePureLocalLoc, "@temp"))
          val hold_context = LPSet(Set((ContextLoc, "3"), (ContextLoc, "4")))
          (node, ((hold_purelocal ++ hold_context, LPu)))
        }
        case ExitExc => {
          val LPu_1 = LPSet((SinglePureLocalLoc, "@exception"))
          val LPu_2 = LPSet((SinglePureLocalLoc, "@exception_all"))
          val LPu_3 = LPSet(Set((ContextLoc, "3"), (ContextLoc, "4")))
          val LPu = LPu_1 ++ LPu_2 ++ LPu_3

          // Note: #PureLocal must not be passed between functions.
          val hold_purelocal = LPSet((SinglePureLocalLoc, "@temp"))
          val hold_context = LPSet(Set((ContextLoc, "3"), (ContextLoc, "4")))
          (node, ((hold_purelocal ++ hold_context, LPu)))
        }
        case Block(insts) => {
          val du = insts.foldLeft((LPBot, LPBot))((S, i) => {
            val returnVar =
              cfg.getReturnVar(node) match {
                case Some(id) => AH.VarStore_def(state._1, state._1(SinglePureLocalLoc)("@env")._2._2, id)
                case None => LPBot
              }

            val defset = Access.I_def(cfg, i, state._1, state._2) ++ returnVar
            val useset = Access.I_use(cfg, i, state._1, state._2)// -- S._1

            // if an instruction can make an exception state, defsets of following instructions must be included in useset.
            // now, we always merge defsets to useset(sound but not efficient).
            (S._1 ++ defset, S._2 ++ useset ++ defset)
          })

          (node, du)
        }
      }

      dusetLock.synchronized {
        duset += (du._1 -> du._2)

        // after-call node
        if (cfg.getAftercalls.contains(node)) {
          // Notes
          // 1. Both of #Context and #PureLocal are defined by an edge transfer function.
          // 2. @temp stands for all the properties in #PureLocal.
          duset += (node -> (du._2._1 ++ LPSet(Set((SinglePureLocalLoc, "@temp"), (ContextLoc, "3"), (ContextLoc, "4"))), du._2._2))
        } else if (cfg.getAftercatches.contains(node)) {
          // Notes
          // 1. Both of #Context and #PureLocal are defined by an edge transfer function.
          // 2. @temp stands for all the properties in #PureLocal.
          duset += (node -> (du._2._1 ++ LPSet(Set((SinglePureLocalLoc, "@temp"), (ContextLoc, "3"), (ContextLoc, "4"))), du._2._2))
        }
      }
    }
  }
}

object Access {
  def I_def(cfg:CFG, i: CFGInst, h: Heap, ctx: Context): LPSet = {
    i match {
      case CFGAlloc(_, _, x, e, a_new) => {
        val l_r = addrToLoc(a_new, Recent)
        val es = e match {
          case None => ExceptionBot
          case Some(proto) => {
            val (_,es_) = SE.V(proto, h, ctx)
              es_
            }
          }
        val LP_1 = AH.Oldify_def(h,ctx,a_new)
        val LP_2 = AH.NewObject_def.foldLeft(LPBot)((S,p) => S + ((l_r, p)))
        val LP_3 = AH.VarStore_def(h,h(SinglePureLocalLoc)("@env")._2._2,x)
        val LP_4 = AH.RaiseException_def(es)
        LP_1 ++ LP_2 ++ LP_3 ++ LP_4
      }
      case CFGAllocArray(_, _, x, n, a_new) => {
        val l_r = addrToLoc(a_new, Recent)
        val LP_1 = AH.Oldify_def(h,ctx,a_new)
        val LP_2 = AH.NewArrayObject_def.foldLeft(LPBot)((S,p) => S + ((l_r, p)))
        val LP_3 = AH.VarStore_def(h,h(SinglePureLocalLoc)("@env")._2._2,x)
        LP_1 ++ LP_2 ++ LP_3
      }
      case CFGAllocArg(_, _, x, n, a_new) => {
        val l_r = addrToLoc(a_new, Recent)
        val LP_1 = AH.Oldify_def(h,ctx,a_new)
        val LP_2 = AH.NewArgObject_def.foldLeft(LPBot)((S,p) => S + ((l_r, p)))
        val LP_3 = AH.VarStore_def(h,h(SinglePureLocalLoc)("@env")._2._2,x)
        LP_1 ++ LP_2 ++ LP_3
      }
      case CFGExprStmt(_, _, x, e) => {
        val (v,es) = SE.V(e, h, ctx)
        val LP_1 = AH.VarStore_def(h,h(SinglePureLocalLoc)("@env")._2._2,x)
        val LP_2 = AH.RaiseException_def(es)
        LP_1 ++ LP_2
      }
      case CFGDelete(_, _, x_1, expr) =>
        expr match {
          case CFGVarRef(_, x_2) => {
            val lset_base = Helper.LookupBase(h, x_2)
            val ax_2 = AbsString.alpha(x_2)
            val LP_1 = AH.VarStore_def(h,h(SinglePureLocalLoc)("@env")._2._2,x_1)
            val LP_2 = lset_base.foldLeft(LPBot)((S,l_base) =>
              S ++ AH.Delete_def(h,l_base, ax_2))
            LP_1 ++ LP_2
          }
          case _ => {
            val (v, es) = SE.V(expr, h, ctx)
            val LP_1 = AH.VarStore_def(h,h(SinglePureLocalLoc)("@env")._2._2,x_1)
            val LP_2 = AH.RaiseException_def(es)
            LP_1 ++ LP_2
          }
        }
      case CFGDeleteProp(_, _, x, e_1, e_2) => {
        // lset must not be empty because obj is coming through <>toObject.
        val lset = SE.V(e_1, h, ctx)._1._2
        val (v, es) = SE.V(e_2, h, ctx)
        val sset = Helper.toStringSet(Helper.toPrimitive_better(h, v))

        val LP_1 = AH.VarStore_def(h,h(SinglePureLocalLoc)("@env")._2._2,x)
        val LP_2 =
          lset.foldLeft(LPBot)((S_1, l) => {
            sset.foldLeft(S_1)((S_2, s) => {
              S_2 ++ AH.Delete_def(h,l,s)
            })
          })
        val LP_3 = AH.RaiseException_def(es)

        LP_1 ++ LP_2 ++ LP_3
      }
      case CFGStore(_, _, e_1, e_2, e_3) => {
        // TODO: toStringSet should be used in more optimized way
        val (lpset1, es_1) = {
          val (v_index, es_index) = SE.V(e_2, h, ctx)
          if (v_index <= ValueBot) (LPBot, es_index)
          else {
            val (v_rhs, es_rhs) = SE.V(e_3, h, ctx)
            if (v_rhs <= ValueBot) (LPBot, es_index ++ es_rhs)
            else {
              // lset must not be empty because obj is coming through <>toObject.
              val lset = SE.V(e_1, h, ctx)._1._2

              // interate over set of strings for index
              val sset = Helper.toStringSet(Helper.toPrimitive_better(h, v_index))
              val (lpset2, es_2) = sset.foldLeft(LPBot, es_index ++ es_rhs)((res, s) => {
                // non-array objects
                val lset_narr = lset.filter(l => (BoolFalse <= Helper.IsArray(h, l)) && BoolTrue <= Helper.CanPut(h, l, s))
                // array objects
                val lset_arr = lset.filter(l => (BoolTrue <= Helper.IsArray(h, l)) && BoolTrue <= Helper.CanPut(h, l, s))
                // store for non-array object
                val LP_narr = lset_narr.foldLeft(LPBot)((_lpset, l) => _lpset ++ AH.PropStore_def(h, l, s))
                // 15.4.5.1 [[DefineOwnProperty]] of Array
                val (lpset_arr, ex) = lset_arr.foldLeft((LPBot, ExceptionBot))((_lpex, l) => {
                  // 3. s is length
                  val (lpset_length, ex_len) =
                    if (AbsString.alpha("length") <= s) {
                      val v_newLen = Value(Operator.ToUInt32(v_rhs))
                      val n_oldLen = h(l)("length")._1._1._1._4 // number
                      val b_g = (n_oldLen < v_newLen._1._4)
                      val b_eq = (n_oldLen === v_newLen._1._4)
                      val b_canputLen = Helper.CanPut(h, l, AbsString.alpha("length"))
                      // 3.d
                      val n_value = Helper.toNumber(v_rhs._1) + Helper.toNumber(Helper.objToPrimitive(v_rhs._2, "Number"))
                      val ex_len =
                        if (BoolFalse <= (n_value === v_newLen._1._4)) Set[Exception](RangeError)
                        else Set[Exception]()
                      val h_normal =
                        if (BoolTrue <= (n_value === v_newLen._1._4)) {
                          // 3.f
                          val LP1 =
                            if ((BoolTrue <= b_g || BoolTrue <= b_eq) && BoolTrue <= b_canputLen)
                              AH.PropStore_def(h, l, AbsString.alpha("length"))
                            else LPBot
                          // 3.j, 3.l
                          val LP2 =
                            if (BoolFalse <= b_g && BoolTrue <= b_canputLen) {
                              val _LP1 = AH.PropStore_def(h, l, AbsString.alpha("length"))
                              (v_newLen._1._4.getSingle, n_oldLen.getSingle) match {
                                case (Some(n1), Some(n2)) =>
                                  (n1.toInt until n2.toInt).foldLeft(_LP1)((__lpset, i) =>
                                    __lpset ++ AH.Delete_def(h, l, AbsString.alpha(i.toString)))
                                case _ =>
                                  if (v_newLen._1._4 <= NumBot || n_oldLen <= NumBot) LPBot
                                  else _LP1 ++ AH.Delete_def(h, l, NumStr)
                              }
                            }
                            else LPBot
                          LP1 ++ LP2
                        }
                        else
                          LPBot
                      (h_normal, ex_len)
                    }
                    else
                      (LPBot, ExceptionBot)
                  // 4. s is array index
                  val lpset_index =
                    if (BoolTrue <= Helper.IsArrayIndex(s)) {
                      val n_oldLen = h(l)("length")._1._1._1._4 // number
                      val n_index = Operator.ToUInt32(Value(Helper.toNumber(PValue(s))))
                      val b_g = (n_oldLen < n_index)
                      val b_eq = (n_oldLen === n_index)
                      val b_canputLen = Helper.CanPut(h, l, AbsString.alpha("length"))
                      // 4.c
                    val LP1 =
                      if (BoolTrue <= (n_index < n_oldLen))  AH.PropStore_def(h, l, s)
                      else LPBot
                    // 4.e
                    val LP2 =
                      if ((BoolTrue <= b_g || BoolTrue <= b_eq) && BoolTrue <= b_canputLen)
                      AH.PropStore_def(h, l, s) ++ AH.PropStore_def(h, l, AbsString.alpha("length"))
                      else LPBot
                    LP1 ++ LP2
                    }
                    else LPBot
                  // 5. other
                  val lpset_normal =
                    if (s != AbsString.alpha("length") && BoolFalse <= Helper.IsArrayIndex(s))
                      AH.PropStore_def(h, l, s)
                    else LPBot
                  (_lpex._1 ++ lpset_length ++ lpset_index ++ lpset_normal, _lpex._2 ++ ex_len)
                })
                (res._1 ++ LP_narr ++ lpset_arr, res._2 ++ ex)
              })
              (lpset2, es_2)
            }
          }
        }
        val LP2 = AH.RaiseException_def(es_1)
        lpset1 ++ LP2
      }
      case CFGFunExpr(_, _, x_1, None, fid, a_new1, a_new2, None) => {
        val l_r1 = addrToLoc(a_new1, Recent)
        val l_r2 = addrToLoc(a_new2, Recent)
        val LP_1 = AH.Oldify_def(h,ctx,a_new1)
        val LP_2 = AH.Oldify_def(h,ctx,a_new2)
        val LP_3 = AH.NewFunctionObject_def.foldLeft(LPBot)((S,p) => S + ((l_r1, p)))
        val LP_4 = AH.NewObject_def.foldLeft(LPBot)((S,p) => S + ((l_r2, p)))
        val LP_5 = LPSet((l_r2, "constructor"))
        val LP_6 = AH.VarStore_def(h,h(SinglePureLocalLoc)("@env")._2._2,x_1)
        LP_1 ++ LP_2 ++ LP_3 ++ LP_4 ++ LP_5 ++ LP_6
      }
      case CFGFunExpr(_, _, x_1, Some(name), fid, a_new1, a_new2, Some(a_new3)) => {
        val x_2 = name.getText
        val l_r1 = addrToLoc(a_new1, Recent)
        val l_r2 = addrToLoc(a_new2, Recent)
        val l_r3 = addrToLoc(a_new3, Recent)
        val LP_1 = AH.Oldify_def(h,ctx,a_new1)
        val LP_2 = AH.Oldify_def(h,ctx,a_new2)
        val LP_3 = AH.Oldify_def(h,ctx,a_new3)
        val LP_4 = AH.NewFunctionObject_def.foldLeft(LPBot)((S,p) => S + ((l_r1, p)))
        val LP_5 = AH.NewObject_def.foldLeft(LPBot)((S,p) => S + ((l_r2, p)))
        val LP_6 = LPSet((l_r2, "constructor"))
        val LP_7 = AH.NewDeclEnvRecord_def.foldLeft(LPBot)((S,p) => S + ((l_r3, p)))
        val LP_8 = LPSet((l_r3, x_2))
        val LP_9 = AH.VarStore_def(h,h(SinglePureLocalLoc)("@env")._2._2,x_1)
        LP_1 ++ LP_2 ++ LP_3 ++ LP_4 ++ LP_5 ++ LP_6 ++ LP_7 ++ LP_8 ++ LP_9
      }
      case CFGConstruct(_, _, e_1, e_2, e_3, a_new, b_new) => {
        // exception handling
        val (v_1, es_1) = SE.V(e_1, h, ctx)
        val cond = v_1._2.exists((l) => BoolFalse <= Helper.HasConstruct(h,l))
        val es_2 =
          if (cond) {
            Set(TypeError)
          } else {
            ExceptionBot
          }
        val es_3 =
          if (v_1._1 </ PValueBot) {
            Set(TypeError)
          } else {
            ExceptionBot
          }
        val es = es_1 ++ es_2 ++ es_3
        val v_arg = SE.V(e_3, h, ctx)._1
        val LP_1 = AH.Oldify_def(h,ctx,a_new)
        val LP_2 = v_arg._2.foldLeft(LPBot)((S,l) => S + ((l, "callee")))
        val LP_3 = AH.RaiseException_def(es)
        LP_1 ++ LP_2 ++ LP_3
      }
      case CFGCall(_, _, e_1, e_2, e_3, a_new, b_new) => {
        // exception handling
        val (v_1, es_1) = SE.V(e_1, h, ctx)
        val cond = v_1._2.exists((l) => BoolFalse <= Helper.IsCallable(h,l))
        val es_2 =
          if (cond) {
            Set(TypeError)
          } else {
            ExceptionBot
          }
        val es_3 =
          if (v_1._1 </ PValueBot) {
            Set(TypeError)
          } else {
            ExceptionBot
          }
        val es = es_1 ++ es_2 ++ es_3
        val v_arg = SE.V(e_3, h, ctx)._1
        val LP_1 = AH.Oldify_def(h,ctx,a_new)
        val LP_2 = v_arg._2.foldLeft(LPBot)((S,l) => S + ((l, "callee")))
        val LP_3 = AH.RaiseException_def(es)
        LP_1 ++ LP_2 ++ LP_3
      }
      case CFGAssert(_, info, expr, _) => {
        V_use(expr, h, ctx)
      }
      case CFGCatch(_, _, name) => {
        val LP_1 = AH.CreateMutableBinding_def(h,h(SinglePureLocalLoc)("@env")._2._2, name)
        val LP_2 = LPSet((SinglePureLocalLoc, "@exception"))
        LP_1 ++ LP_2
      }
      case CFGReturn(_, _, expr) => {
        val LP_1 = expr match {
          case None => LPBot
          case Some(e) => {
            val (v,es) = SE.V(e, h, ctx)
            AH.RaiseException_def(es)
          }
        }
        val LP_2 = LPSet((SinglePureLocalLoc, "@return"))

        LP_1 ++ LP_2
      }
      case CFGThrow(_, _, expr) => {
        val (v,es) = SE.V(expr, h, ctx)
        val LP_1 = LPSet(Set((SinglePureLocalLoc, "@exception"),(SinglePureLocalLoc, "@exception_all")))
        val LP_2 = AH.RaiseException_def(es)

        LP_1 ++ LP_2
      }
      case CFGInternalCall(_, _, lhs, fun, arguments, loc) => {
        (fun.toString, arguments, loc)  match {
          case ("<>Global<>toObject", List(expr), Some(a_new)) => {
            val (v,es) = SE.V(expr, h, ctx)
            val LP_1 = AH.VarStore_def(h,h(SinglePureLocalLoc)("@env")._2._2,lhs)
            val LP_2 = AH.toObject_def(h,ctx,v,a_new)
            val LP_3 = AH.RaiseException_def(es)
            LP_1 ++ LP_2 ++ LP_3
          }
          case ("<>Global<>isObject", List(expr), None) => {
            val (v,es) = SE.V(expr, h, ctx)
            val LP_1 = AH.VarStore_def(h,h(SinglePureLocalLoc)("@env")._2._2,lhs)
            val LP_2 = AH.RaiseException_def(es)
            LP_1 ++ LP_2
          }
          case ("<>Global<>toNumber", List(expr), None) => {
            val (v,es) = SE.V(expr, h, ctx)
            val LP_1 = AH.VarStore_def(h,h(SinglePureLocalLoc)("@env")._2._2,lhs)
            val LP_2 = AH.RaiseException_def(es)
            LP_1 ++ LP_2
          }
          case ("<>Global<>getBase", List(expr_2), None) => {
            AH.VarStore_def(h,h(SinglePureLocalLoc)("@env")._2._2,lhs)
          }
          case ("<>Global<>iteratorInit", List(expr), Some(a_new)) => {
            if (Config.defaultForinUnrollingCount == 0) {
              LPBot
            } else {
              val l_new = addrToLoc(a_new, Recent)
              val LP_1 = LPSet(Set((l_new, "index"), (l_new, "length")))
              val LP_2 = AH.absPair(h, l_new, AbsString.NumTop)
              val LP_3 = AH.VarStore_def(h, h(SinglePureLocalLoc)("@env")._2._2, lhs)
              LP_1 ++ LP_2 ++ LP_3
            }
          }
          case ("<>Global<>iteratorHasNext", List(expr_2, expr_3), None) => {
            if (Config.defaultForinUnrollingCount == 0) {
              AH.VarStore_def(h,h(SinglePureLocalLoc)("@env")._2._2,lhs)
            } else {
              AH.VarStore_def(h,h(SinglePureLocalLoc)("@env")._2._2,lhs)
            }
          }
          case ("<>Global<>iteratorNext", List(expr_2, expr_3), None) => {
            if (Config.defaultForinUnrollingCount == 0) {
              AH.VarStore_def(h,h(SinglePureLocalLoc)("@env")._2._2,lhs)
            } else {
              val (v_iter, _) = SE.V(expr_3, h, ctx)
              val lset = v_iter._2
              val LP_1 = lset.foldLeft(LPBot)((lp, l) => lp + ((l, "index")))
              val LP_2 = AH.VarStore_def(h, h(SinglePureLocalLoc)("@env")._2._2, lhs)
              LP_1 ++ LP_2
            }
          }
          case _ => {
            if (!Config.quietMode)
              System.out.println(fun.toString)
            throw new NotYetImplemented()
          }
        }
      }
      case CFGAPICall(_, model, fun, args) => {
        val def_map = ModelManager.getModel(model).getDefMap()
        def_map.get(fun) match {
          case Some(f) =>
            f(h, ctx, cfg, fun, args, cfg.findEnclosingNode(i)._1)
          case None =>
            if (!Config.quietMode)
              System.err.println("* Warning: def. info. of the API function '"+fun+"' are not defined.")
            LPBot
        }
      }
      case CFGAsyncCall(_, _, model, call_type, addr1, addr2, addr3) => {
        ModelManager.getModel(model).asyncDef(h, ctx, cfg, call_type, List(addr1, addr2, addr3))
      }
      case _ => LPBot
    }
  }

  def I_use(cfg:CFG, i: CFGInst, h: Heap, ctx: Context): LPSet = {
    i match {
      case CFGAlloc(_, _, x, e, a_new) => {
        val l_r = addrToLoc(a_new, Recent)
        val es = e match {
          case None => ExceptionBot
          case Some(proto) => {
            val (_,es_) = SE.V(proto, h, ctx)
              es_
            }
          }
        val LP_1 = AH.Oldify_use(h,ctx,a_new)
        val LP_2 =
          e match {
            case None => LPBot
            case Some(e) => V_use(e, h, ctx)
          }
        val LP_3 = AH.VarStore_use(h,h(SinglePureLocalLoc)("@env")._2._2,x)
        val LP_4 = AH.RaiseException_use(es)
        LP_1 ++ LP_2 ++ LP_3 ++ LP_4 + ((SinglePureLocalLoc, "@env"))
      }
      case CFGAllocArray(_, _, x, n, a_new) => {
        val LP_1 = AH.Oldify_use(h,ctx,a_new)
        val LP_2 = AH.VarStore_use(h,h(SinglePureLocalLoc)("@env")._2._2,x)
        LP_1 ++ LP_2 + ((SinglePureLocalLoc, "@env"))
      }
      case CFGAllocArg(_, _, x, n, a_new) => {
        val LP_1 = AH.Oldify_use(h,ctx,a_new)
        val LP_2 = AH.VarStore_use(h,h(SinglePureLocalLoc)("@env")._2._2,x)
        LP_1 ++ LP_2 + ((SinglePureLocalLoc, "@env"))
      }
      case CFGExprStmt(_, _, x, e) => {
        val (v,es) = SE.V(e, h, ctx)
        val LP_1 = AH.VarStore_use(h,h(SinglePureLocalLoc)("@env")._2._2,x)
        val LP_2 = V_use(e,h,ctx)
        val LP_3 = AH.RaiseException_use(es)
        LP_1 ++ LP_2 ++ LP_3 + ((SinglePureLocalLoc, "@env"))
      }
      case CFGDelete(_, _, x_1, expr) => {
        expr match {
          case CFGVarRef(_, x_2) => {
            val lset_base = Helper.LookupBase(h, x_2)
            val ax_2 = AbsString.alpha(x_2)
            val LP_1 = AH.VarStore_use(h,h(SinglePureLocalLoc)("@env")._2._2,x_1)
            val LP_2 = AH.LookupBase_use(h,h(SinglePureLocalLoc)("@env")._2._2,x_2)
            val LP_3 = lset_base.foldLeft(LPBot)((S,l_base) =>
              S ++ AH.Delete_use(h,l_base, ax_2))
            LP_1 ++ LP_2 ++ LP_3 + ((SinglePureLocalLoc, "@env"))
          }
          case _ => {
            val (v, es) = SE.V(expr, h, ctx)
            val LP_1 = AH.VarStore_use(h,h(SinglePureLocalLoc)("@env")._2._2,x_1)
            val LP_2 = V_use(expr,h,ctx)
            val LP_3 = AH.RaiseException_use(es)
            LP_1 ++ LP_2 ++ LP_3 + ((SinglePureLocalLoc, "@env"))
          }
        }
      }
      case CFGDeleteProp(_, _, x, e_1, e_2) => {
        // lset must not be empty because obj is coming through <>toObject.
        val lset = SE.V(e_1, h, ctx)._1._2
        val (v, es) = SE.V(e_2, h, ctx)
        val sset = Helper.toStringSet(Helper.toPrimitive_better(h, v))

        val LP_1 = AH.VarStore_use(h,h(SinglePureLocalLoc)("@env")._2._2,x)
        val LP_2 = V_use(e_1, h, ctx)
        val LP_3 = V_use(e_2, h, ctx)
        val LP_4 =
          lset.foldLeft(LPBot)((S_1, l) => {
            sset.foldLeft(S_1)((S_2, s) => {
              S_2 ++ AH.Delete_use(h,l,s)
            })
          })
        val LP_5 = AH.RaiseException_use(es)
        val LP_6 = AH.toPrimitive_use(h, v)

        LP_1 ++ LP_2 ++ LP_3 ++ LP_4 ++ LP_5 ++ LP_6 + ((SinglePureLocalLoc, "@env"))
      }
      case CFGStore(_, _, e_1, e_2, e_3) => {
        // TODO: toStringSet should be used in more optimized way
        val (v_rhs, es_rhs) = SE.V(e_3, h, ctx)
        val lset = SE.V(e_1, h, ctx)._1._2
        val (v_index, es_index) = SE.V(e_2, h, ctx)
        val sset = Helper.toStringSet(Helper.toPrimitive_better(h, v_index))
        val tset =
          lset.filter((l) => {
            sset.exists((s) => {
              BoolTrue <= Helper.CanPut(h,l,s) // BoolFalse <= IsArray(h,l)
            })
          })

        val LP_1 =
          tset.foldLeft(LPBot)((S_1,l) => {
            sset.foldLeft(S_1)((S_2,s) => {
              S_2 ++ AH.PropStore_use(h,l,s)
            })
          })

        val LP_2 = V_use(e_1, h, ctx)
        val LP_3 = V_use(e_2, h, ctx)
        val LP_4 = V_use(e_3, h, ctx)

        val LP_5 =
          lset.foldLeft(LPBot)((S_1,l) => {
            sset.foldLeft(S_1)((S_2,s) => {
              S_2 ++ AH.CanPut_use(h, l, s)
            }) ++ AH.IsArray_use(h,l)
          })

        // non-array objects
        val lset_narr =
          lset.filter(l => {
            (BoolFalse <= Helper.IsArray(h, l)) &&
            sset.exists(s => BoolTrue <= Helper.CanPut(h, l, s))
          })

        val LP_narr =
          lset_narr.foldLeft(LPBot)((S_1,l) => {
            sset.foldLeft(S_1)((S_2,s) => {
              S_2 ++ AH.PropStore_use(h,l,s)
            })
          })

        // array objects
        val lset_arr =
          lset.filter(l => {
            (BoolTrue <= Helper.IsArray(h, l)) &&
            sset.exists(s => BoolTrue <= Helper.CanPut(h, l, s))
          })

        val LP_arr =
          lset_arr.foldLeft(LPBot)((S_1,l) => {
            sset.foldLeft(S_1)((S_2,s) => {
              S_2 ++ AH.PropStore_use(h, l, s)
            }) ++
            AH.PropStore_use(h, l, AbsString.alpha("length")) ++
            AH.CanPut_use(h, l, AbsString.alpha("length")) ++
            LPSet((l, "length")) ++
            AH.Delete_use(h, l, NumStr)
          })

        // RangeError
        val n_value = Helper.toNumber(v_rhs._1) + Helper.toNumber(Helper.objToPrimitive(v_rhs._2, "Number"))
        val v_newLen = Value(Operator.ToUInt32(v_rhs))
        val es_len =
          if (BoolFalse <= (n_value === v_newLen._1._4)) Set[Exception](RangeError)
          else Set[Exception]()
        val LP_6 = AH.RaiseException_use(es_index ++ es_rhs ++ es_len)
        val LP_7 = AH.toPrimitive_use(h, v_index)

        LP_1 ++ LP_2 ++ LP_3 ++ LP_4 ++ LP_5 ++ LP_6 ++ LP_7 ++ LP_narr ++ LP_arr ++
        LPSet(Set((SinglePureLocalLoc, "@env"), (SinglePureLocalLoc, "@this")))
      }
      case CFGFunExpr(_, _, x_1, None, fid, a_new1, a_new2, None) => {
        val LP_1 = AH.Oldify_use(h,ctx,a_new1)
        val LP_2 = AH.Oldify_use(h,ctx,a_new2)
        val LP_3 = AH.VarStore_use(h, h(SinglePureLocalLoc)("@env")._2._2, x_1)

        LP_1 ++ LP_2 ++ LP_3 + ((SinglePureLocalLoc, "@env"))
      }
      case CFGFunExpr(_, _, x_1, Some(name), fid, a_new1, a_new2, Some(a_new3)) => {
        val LP_1 = AH.Oldify_use(h,ctx,a_new1)
        val LP_2 = AH.Oldify_use(h,ctx,a_new2)
        val LP_3 = AH.Oldify_use(h,ctx,a_new3)
        val LP_4 = AH.VarStore_use(h, h(SinglePureLocalLoc)("@env")._2._2, x_1)

        LP_1 ++ LP_2 ++ LP_3 ++ LP_4 + ((SinglePureLocalLoc, "@env"))
      }
      case CFGConstruct(_, _, e_1, e_2, e_3, a_new, b_new) => {
        val (v_1, es_1) = SE.V(e_1, h, ctx)
        val v_arg = SE.V(e_3, h, ctx)._1
        val lset_f = v_1._2.filter((l) => BoolTrue <= Helper.HasConstruct(h,l))

        // exception handling
        val cond = v_1._2.exists((l) => BoolFalse <= Helper.HasConstruct(h,l))
        val es_2 =
          if (cond) {
            Set(TypeError)
          } else {
            ExceptionBot
          }
        val es_3 =
          if (v_1._1 </ PValueBot) {
            Set(TypeError)
          } else {
            ExceptionBot
          }
        val es = es_1 ++ es_2 ++ es_3

        val LP_1 = AH.Oldify_use(h, ctx, a_new)
        val LP_2 = V_use(e_1,h,ctx)
        val LP_3 = V_use(e_2,h,ctx)
        val LP_4 = V_use(e_3,h,ctx)
        val LP_5 = v_1._2.foldLeft(LPBot)((S, l) => S ++ AH.HasConstruct_use(h,l))
        val LP_6 = AH.getThis_use(h, SE.V(e_2, h, ctx)._1)
        val LP_7 = lset_f.foldLeft(LPBot)((S, l_f) => S + ((l_f, "@construct")))
        val LP_8 = v_arg._2.foldLeft(LPBot)((S, l) => S + ((l, "callee")))
        val LP_9 = AH.RaiseException_use(es)

        // because of PureLocal object is weak updated in edges, all the element are needed
        val LP_10 = h(SinglePureLocalLoc).getAllProps.foldLeft(LPBot)((S, kv) => S + ((SinglePureLocalLoc, kv)))
        val LP_11 = LPSet(Set((ContextLoc, "3"), (ContextLoc, "4")))

        LP_1 ++ LP_2 ++ LP_3 ++ LP_4 ++ LP_5 ++ LP_6 ++ LP_7 ++ LP_8 ++ LP_9 ++ LP_10 ++ LP_11
      }
      case CFGCall(_, _, e_1, e_2, e_3, a_new, b_new) => {
        val (v_1, es_1) = SE.V(e_1, h, ctx)
        val v_arg = SE.V(e_3, h, ctx)._1
        val lset_f = v_1._2.filter((l) => BoolTrue <= Helper.IsCallable(h,l))

        // exception handling
        val cond = v_1._2.exists((l) => BoolFalse <= Helper.IsCallable(h,l))
        val es_2 =
          if (cond) {
            Set(TypeError)
          } else {
            ExceptionBot
          }
        val es_3 =
          if (v_1._1 </ PValueBot) {
            Set(TypeError)
          } else {
            ExceptionBot
          }
        val es = es_1 ++ es_2 ++ es_3

        val LP_1 = AH.Oldify_use(h, ctx, a_new)
        val LP_2 = V_use(e_1,h,ctx)
        val LP_3 = V_use(e_2,h,ctx)
        val LP_4 = V_use(e_3,h,ctx)
        val LP_5 = v_1._2.foldLeft(LPBot)((S, l) => S ++ AH.IsCallable_use(h,l))
        val LP_6 = AH.getThis_use(h, SE.V(e_2, h, ctx)._1)
        val LP_7 = lset_f.foldLeft(LPBot)((S, l_f) => S + ((l_f, "@function")))
        val LP_8 = v_arg._2.foldLeft(LPBot)((S, l) => S + ((l, "callee")))
        val LP_9 = AH.RaiseException_use(es)

        // because of PureLocal object is weak updated in edges, all the element are needed
        val LP_10 = h(SinglePureLocalLoc).getAllProps.foldLeft(LPBot)((S, kv) => S + ((SinglePureLocalLoc, kv)))
        val LP_11 = LPSet(Set((ContextLoc, "3"), (ContextLoc, "4")))

        LP_1 ++ LP_2 ++ LP_3 ++ LP_4 ++ LP_5 ++ LP_6 ++ LP_7 ++ LP_8 ++ LP_9 ++ LP_10 ++ LP_11
      }
      case CFGAssert(_, info, expr, _) => {
        V_use(expr, h, ctx) + ((GlobalLoc, "@class"))
      }
      case CFGCatch(_, _, name) => {
        val LP_1 = AH.CreateMutableBinding_use(h,h(SinglePureLocalLoc)("@env")._2._2, name)
        val LP_2 = LPSet(Set((SinglePureLocalLoc, "@exception_all"), (SinglePureLocalLoc, "@exception")))

        LP_1 ++ LP_2 + ((SinglePureLocalLoc, "@env"))
      }
      case CFGReturn(_, _, expr) => {
        val LP = expr match {
          case None => LPBot
          case Some(e) => {
            val (v,es) = SE.V(e, h, ctx)
            val LP_1 = V_use(e, h, ctx)
            val LP_2 = AH.RaiseException_use(es)
            LP_1 ++ LP_2
          }
        }
        LP + ((SinglePureLocalLoc, "@return"))
      }
      case CFGThrow(_, _, e) => {
        val (v,es) = SE.V(e, h, ctx)
        val LP_1 = V_use(e, h, ctx)
        val LP_2 = AH.RaiseException_use(es)

        LP_1 ++ LP_2 + ((SinglePureLocalLoc, "@exception_all"))
      }
      case CFGInternalCall(_, _, lhs, fun, arguments, loc) => {
        (fun.toString, arguments, loc)  match {
          case ("<>Global<>toObject", List(expr), Some(a_new)) => {
            val (v,es) = SE.V(expr, h, ctx)
            val LP_1 = V_use(expr, h, ctx)
            val LP_2 = AH.VarStore_use(h, h(SinglePureLocalLoc)("@env")._2._2, lhs)
            val LP_3 = AH.toObject_use(h, ctx, v, a_new)
            val LP_4 = AH.RaiseException_use(es)

            LP_1 ++ LP_2 ++ LP_3 ++ LP_4 + ((SinglePureLocalLoc, "@env"))
          }
          case ("<>Global<>isObject", List(expr), None) => {
            val (v,es) = SE.V(expr, h, ctx)
            val LP_1 = V_use(expr, h, ctx)
            val LP_2 = AH.VarStore_use(h, h(SinglePureLocalLoc)("@env")._2._2, lhs)
            val LP_3 = AH.RaiseException_use(es)

            LP_1 ++ LP_2 ++ LP_3 + ((SinglePureLocalLoc, "@env"))
          }
          case ("<>Global<>toNumber", List(expr), None) => {
            val (v,es) = SE.V(expr, h, ctx)
            val LP_1 = V_use(expr, h, ctx)
            val LP_2 = AH.VarStore_use(h, h(SinglePureLocalLoc)("@env")._2._2, lhs)
            val LP_3 = AH.RaiseException_use(es)
            val LP_4 = AH.toPrimitive_use(h, v)

            LP_1 ++ LP_2 ++ LP_3 ++ LP_4 + ((SinglePureLocalLoc, "@env"))
          }
          case ("<>Global<>getBase", List(expr_2), None) => {
            val x_2 = expr_2.asInstanceOf[CFGVarRef].id

            val LP_1 = AH.VarStore_use(h, h(SinglePureLocalLoc)("@env")._2._2, lhs)
            val LP_2 = AH.LookupBase_use(h, h(SinglePureLocalLoc)("@env")._2._2, x_2)

            LP_1 ++ LP_2 + ((SinglePureLocalLoc, "@env"))
          }
          case ("<>Global<>iteratorInit", List(expr), Some(a_new)) => {
            if (Config.defaultForinUnrollingCount == 0) {
              LPBot
            } else {
              val (v,_) = SE.V(expr, h, ctx)
              val v_obj = Value(PValue(UndefBot, NullBot, v._1._3, v._1._4, v._1._5), v._2)
              val (v_1, h_1, _, _) = Helper.toObject(h, ctx, v_obj, a_new)
              val lset = v_1._2

              val LP_1 = V_use(expr, h, ctx)
              val LP_2 = AH.toObject_use(h, ctx, v_obj, a_new)
              val LP_3 = AH.CollectProps_use(h_1, lset)
              val LP_4 = AH.VarStore_use(h, h(SinglePureLocalLoc)("@env")._2._2, lhs)

              LP_1 ++ LP_2 ++ LP_3 ++ LP_4
            }
          }
          case ("<>Global<>iteratorHasNext", List(expr_2, expr_3), None) => {
            if (Config.defaultForinUnrollingCount == 0) {
              AH.VarStore_use(h, h(SinglePureLocalLoc)("@env")._2._2, lhs) + ((SinglePureLocalLoc, "@env"))
            } else {
              val (v,_) = SE.V(expr_3, h, ctx)
              val lset = v._2

              val LP_1 = V_use(expr_3, h, ctx)
              val LP_2 = lset.foldLeft(LPBot)((lp, l) => lp ++ AH.absPair(h, l, AbsString.NumTop) + ((l, "index")))
              val LP_3 = AH.VarStore_use(h, h(SinglePureLocalLoc)("@env")._2._2, lhs)

              LP_1 ++ LP_2 ++ LP_3
            }
          }
          case ("<>Global<>iteratorNext", List(expr_2, expr_3), None) => {
            if (Config.defaultForinUnrollingCount == 0) {
              AH.VarStore_use(h, h(SinglePureLocalLoc)("@env")._2._2, lhs) + ((SinglePureLocalLoc, "@env"))
            } else {
              val (v,_) = SE.V(expr_3, h, ctx)
              val lset = v._2

              val LP_1 = V_use(expr_3, h, ctx)
              val LP_2 = lset.foldLeft(LPBot)((lp, l) => lp ++ AH.absPair(h, l, AbsString.NumTop) + ((l, "index")))
              val LP_3 = AH.VarStore_use(h, h(SinglePureLocalLoc)("@env")._2._2, lhs)
              LP_1 ++ LP_2 ++ LP_3
            }
          }
          case _ => {
            if (!Config.quietMode)
              System.out.println(fun.toString)
            throw new NotYetImplemented()
          }
        }
      }
      case CFGAPICall(_, model, fun, args) => {
        val use_map = ModelManager.getModel(model).getUseMap()
        use_map.get(fun) match {
          case Some(f) =>
            f(h, ctx, cfg, fun, args, cfg.findEnclosingNode(i)._1)
          case None =>
            if (!Config.quietMode)
              System.err.println("* Warning: use. info. of the API function '"+fun+"' are not defined.")
            LPBot
        }
      }
      case CFGAsyncCall(_, _, model, call_type, addr1, addr2, addr3) => {
        ModelManager.getModel(model).asyncUse(h, ctx, cfg, call_type, List(addr1, addr2, addr3))
      }
      case _ => LPBot
    }
  }

  def V_use(e: CFGExpr, h: Heap, ctx: Context): LPSet = {
    e match {
      case CFGVarRef(info, id) => {
        LPSet((SinglePureLocalLoc, "@env")) ++ AH.Lookup_use(h,h(SinglePureLocalLoc)("@env")._2._2,id)
      }
      case CFGBin(info, e_1, op, e_2) => {
        op.getText match {
          case "instanceof" =>
            val (v_1,es_1) = SE.V(e_1,h,ctx)
            val (v_2,es_2) = SE.V(e_2,h,ctx)
            val lset_1 = v_1._2
            val lset_2 = v_2._2
            val lset_3 = lset_2.filter((l) => BoolTrue <= Helper.HasConstruct(h, l))
            val aproto = AbsString.alpha("prototype")
            val v_proto = lset_3.foldLeft(ValueBot)((v, l) => v + Helper.Proto(h,l,aproto))
            val lset_4 = v_proto._2
            val LP_1 = lset_2.foldLeft(LPBot)((S, l) => S + ((l, "@construct")))
            val LP_2 = lset_3.foldLeft(LPBot)((S, l) => S ++ AH.Proto_use(h,l,aproto))
            val LP_3 = lset_1.foldLeft(LPBot)((S1, l_1) =>
              S1 ++ lset_4.foldLeft(LPBot)((S2, l_2) => S2 ++ AH.inherit_use(h,l_1,l_2)))
            val LP_4 = V_use(e_1, h, ctx)
            val LP_5 = V_use(e_2, h, ctx)
            LP_1 ++ LP_2 ++ LP_3 ++ LP_4 ++ LP_5
          case "in" => {
            val (v_1,es_1) = SE.V(e_1,h,ctx)
            val (v_2,es_2) = SE.V(e_2,h,ctx)
            val s = Helper.toString(Helper.toPrimitive_better(h, v_1))
            val LP_1 = V_use(e_1, h, ctx)
            val LP_2 = V_use(e_2, h, ctx)
            val LP_3 = v_2._2.foldLeft(LPBot)((S, l) => S ++ AH.HasProperty_use(h,l,s))
            val LP_4 = AH.toPrimitive_use(h, v_1)
            LP_1 ++ LP_2 ++ LP_3 ++ LP_4
          }
          case _ => V_use(e_1, h, ctx) ++ V_use(e_2, h, ctx)
        }
      }
      case CFGUn(info, op, e) => {
        op.getText match {
          case "typeof" =>
            val (v,es) = SE.V(e,h,ctx)
            val LP = AH.TypeTag_use(h,v)
            V_use(e, h, ctx) ++ LP
          case _ => V_use(e, h, ctx)
        }
      }
      case CFGLoad(info, e_1, e_2) => {
        val lset = SE.V(e_1,h,ctx)._1._2
        val (v, es) = SE.V(e_2,h,ctx)
        val sset = Helper.toStringSet(Helper.toPrimitive_better(h, v))
        val LP_1 =
          lset.foldLeft(LPBot)((S_1, l) => {
            sset.foldLeft(S_1)((S_2, s) => {
              S_2 ++ AH.Proto_use(h,l,s)
            })
          })
        val LP_2 = AH.toPrimitive_use(h, v)
        LP_1 ++ LP_2 ++ V_use(e_1, h, ctx) ++ V_use(e_2, h, ctx)
      }
      case CFGThis(_) => LPSet((SinglePureLocalLoc, "@this"))
      case _ => LPBot
    }
  }
  def heap_diff(h1: Heap, h2: Heap): LPSet = {
    val keys = h1.map.keySet ++ h2.map.keySet

    keys.foldLeft(LPBot)((S, key) => {
      val S2 = (h1.map.contains(key), h2.map.contains(key)) match {
        case (true,true) => S ++ obj_diff(key, h1(key), h2(key))
        case (true, false) => S ++ h1(key).getAllProps.foldLeft(LPBot)((S, v) => S + ((key, v)))
        case (false, true) => S ++ h2(key).getAllProps.foldLeft(LPBot)((S, v) => S + ((key, v)))
        case _ => LPBot
      }
      S ++ S2
    })
  }

  def obj_diff(l: Loc, o1: Obj, o2: Obj): LPSet = {
    val keys = o1.getAllProps ++ o2.getAllProps
    keys.foldLeft(LPBot)((S, key) => {
      val pv1 = o1(key)
      val pv2 = o2(key)
      val as1 = o1.domIn(key)
      val as2 = o2.domIn(key)

      if ((pv1 </ pv2 || pv2 </ pv1) || (as1 </ as2 || as2 </ as1))
        S + ((l,key))
      else S
    })
  }

  def heap_check(h_org: Heap, h_res: Heap, defset: LocSet): Boolean = {
    h_org.restrict(defset) == h_res.restrict(defset)
  }

  def heap_check(h_org: Heap, h_res: Heap, defset: LPSet): Boolean = {
    h_org.restrict(defset) == h_res.restrict(defset)
  }
}
