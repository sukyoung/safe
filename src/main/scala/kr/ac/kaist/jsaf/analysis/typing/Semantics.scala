/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing

import scala.collection.immutable.HashSet
import scala.collection.mutable.{HashMap => MHashMap, HashSet => MHashSet, Stack => MStack}
import scala.collection.immutable.HashMap
import scala.collection.mutable.{Map => MMap}
import kr.ac.kaist.jsaf.analysis.visualization.Visualization
import kr.ac.kaist.jsaf.{Shell, ShellParameters}

import kr.ac.kaist.jsaf.analysis.asserts._
import kr.ac.kaist.jsaf.analysis.asserts.{ASSERTHelper => AH}
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.bug_detector.Range15_4_5_1
import kr.ac.kaist.jsaf.nodes_util.{EJSOp, IRFactory, NodeUtil => NU, DOMStatistics}
import kr.ac.kaist.jsaf.nodes.IROp
import kr.ac.kaist.jsaf.analysis.typing.{SemanticsExpr => SE}
import kr.ac.kaist.jsaf.analysis.typing.{PreSemanticsExpr => PSE}
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolTrue => BTrue, BoolFalse => BFalse}
import kr.ac.kaist.jsaf.analysis.typing.models.{DOMHelper, ModelManager, JQueryModel}
import kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml.HTMLTopElement
import kr.ac.kaist.jsaf.analysis.typing.models.jquery.JQuery
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.Shell

class Semantics(cfg: CFG, worklist: Worklist, locclone: Boolean) {
  // Inter-procedural edge set.
  // These edges are added while processing call instruction.
  val ipSuccMap: MMap[ControlPoint, MMap[ControlPoint, (Context,Obj)]] = MHashMap()
  val ipPredMap: MMap[ControlPoint, MHashSet[ControlPoint]] = MHashMap()
  def getIPSucc(cp: ControlPoint): Option[MMap[ControlPoint, (Context,Obj)]] = ipSuccMap.get(cp)
  def getIPPred(cp: ControlPoint): Option[MHashSet[ControlPoint]] = ipPredMap.get(cp)

  // Heap bottoms
  val heapBotMap: MHashSet[(ControlPoint, CFGInst)] = MHashSet()

  val dummyInfo = IRFactory.makeInfo(IRFactory.dummySpan("CFGSemantics"))

  def getArgValue(h : Heap, ctx: Context, args: CFGExpr, x : String):Value = {
    SE.V(CFGLoad(dummyInfo, args, CFGString(x)), h, ctx)._1
  }
  // for debugging implementation using heap testing
  var preState: State = null
  var preCFG: CFG = null
  var compareOption = false
  def setCompare(state: State, cfg: CFG): Unit = {
    preState = state
    preCFG = cfg
    compareOption = true
  }
  // Semantics of inter-procedural edge from cp1 to cp2 with context label ctx.
  def E(cp1: ControlPoint, cp2: ControlPoint, ctx: Context, obj: Obj, s: State): State = {
    cp2 match {
      case ((_, LEntry),_) =>
        /*
        if(s._1 <= HeapBot) {
         System.out.println("== "+cp1 +" -> "+cp2+" ==")
         System.out.println(DomainPrinter.printHeap(4, s._1, cfg))
         System.out.println("== Object ==")
         System.out.println(DomainPrinter.printObj(4, obj))
        }*/
        // call edge
        if (s._1 == HeapBot) {
          StateBot
        } else {
          val env_obj = Helper.NewDeclEnvRecord(obj("@scope")._2)
          val obj2 = obj - "@scope"
          val h1 = s._1
          val h2 = h1.remove(SinglePureLocalLoc)
          val h3 = h2.update(SinglePureLocalLoc, obj2)
          val h4 = obj2("@env")._2._2.foldLeft(HeapBot)((hh, l_env) => {
            hh + h3.update(l_env, env_obj)
          })
          State(h4, ctx)
        }

      case _ => cp1 match {
        case ((_, LExit),_) =>
//          System.out.println("== "+cp1 +" -> "+cp2+" ==")
//          System.out.println(DomainPrinter.printHeap(4, s._1, cfg))
//          System.out.println(DomainPrinter.printContext(4, s._2))
//          System.out.println("== Object ==")
//          System.out.println(DomainPrinter.printObj(4, obj))
          // exit return edge
          if (s._1 == HeapBot || s._2.isBottom) {
            StateBot
          } else {
            val returnVar = cfg.getReturnVar(cp2._1) match {
              case Some(x) => x
              case None => throw new InternalError("After-call node must have return variable")
            }
            val h1 = s._1
            val ctx1 = s._2
            val (ctx2, obj1) = Helper.FixOldify(ctx, obj, ctx1._3, ctx1._4)
            if (ctx2.isBottom) StateBot
            else {
              val v = h1(SinglePureLocalLoc)("@return")._2
              val h2 = h1.update(SinglePureLocalLoc, obj1)
              val h3 = Helper.VarStore(h2, returnVar, v)
              State(h3, ctx2)
            }
          }

        case ((_, LExitExc),_) =>
          // exit-exc return edge
          if (s._1 == HeapBot || s._2.isBottom) {
            StateBot
          } else {
            val h1 = s._1
            val ctx1 = s._2
            val (ctx2, obj1) = Helper.FixOldify(ctx, obj, ctx1._3, ctx1._4)
            if (ctx2.isBottom) StateBot
            else {
              val v = h1(SinglePureLocalLoc)("@exception")._2
              val v_old = obj1("@exception_all")._2
              val h2 = h1.update(SinglePureLocalLoc,
                                 obj1.update("@exception", PropValue(v)).
                                      update("@exception_all", PropValue(v + v_old)))
              State(h2, ctx2)
            }
          }

        case _ => throw new InternalError("Inter-procedural edge must be call or return edge.")
      }
    }
  }

  def M(cp: ControlPoint, i: CFGInst, h: Heap, ctx: Context, he: Heap, ctxe: Context, inTable: Table = MHashMap()) = {
    if (h == HeapBot) {
      (((h, ctx), (he, ctxe)), BoolBot)
    } else {
      val s = i match {
        /* Loop */
        case CFGCond(_, info, expr, isEvent) => {
          val (v_cond, es) = SE.V(expr, h, ctx)
          val cond = Helper.toBoolean(v_cond)
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          if(isEvent) 
            (((h, ctx), (he, ctxe)), BoolTop)
          else
            (((h, ctx), (he, ctxe)), cond)
/*
          if(BoolTop <= cond) {
            (((h, ctx), (he, ctxe)), true)
          }
          else {
            (((h, ctx), (he, ctxe)), false)
          }*/
        }
        case _ => 
            (((h, ctx), (he, ctxe)), BoolBot)
      }
      // System.out.println("out heap#####\n" + DomainPrinter.printHeap(4, s._1._1, cfg))
      s
    }
  }

  def L(cp: ControlPoint, c: Cmd, s: State, inTable: Table = MHashMap()): AbsBool = {
    val h = s._1
    val ctx = s._2
    var tt: AbsBool =BoolBot
    if (h <= HeapBot) {
      BoolBot
    } else {
    c match {
        case Entry => BoolBot
        case Exit => BoolBot
        case ExitExc => BoolBot

        case Block(insts) => {
          insts.foldLeft(((h, ctx), (HeapBot, ContextBot)))(
            (states, inst) => {
              val r = M(cp, inst, states._1._1, states._1._2, states._2._1, states._2._2, inTable)
              tt = tt + r._2
              r._1
                         })
        }
      }
      tt
    }
  }

  def C(cp: ControlPoint, c: Cmd, s: State, inTable: Table = MHashMap()): (State, State) = {
    val h = s._1
    val ctx = s._2

    if (h <= HeapBot) {
      (StateBot, StateBot)
    } else {
      val ((h_1, ctx_1), (he_1, ctxe_1)) = c match {
        case Entry =>
          val (fid, _) = cp._1
          val x_argvars = cfg.getArgVars(fid)
          val x_localvars = cfg.getLocalVars(fid)
          val lset_arg = h(SinglePureLocalLoc)(cfg.getArgumentsName(fid))._1._1._2
          var i = 0
          val h_n = x_argvars.foldLeft(h)((hh, x) => {
            val v_i = lset_arg.foldLeft(ValueBot)((vv, l_arg) => {
              vv + Helper.Proto(hh, l_arg, AbsString.alpha(i.toString))
            })
            i = i + 1
            Helper.CreateMutableBinding(hh, x, v_i)
          })
          val h_m = x_localvars.foldLeft(h_n)((hh, x) => {
            Helper.CreateMutableBinding(hh, x, Value(UndefTop))
          })
          ((h_m, ctx), (HeapBot, ContextBot))

        case Exit => 
          val h1 = 
            // restore the 'arguments' property of the function object
            if(Config.domMode) {
              val (fid, _) = cp._1
              val arguments_loc = h(SinglePureLocalLoc)(cfg.getArgumentsName(fid))._1._1._2
              val fun_loc = arguments_loc.foldLeft(LocSetBot)((lset, l) =>
                lset ++ Helper.Proto(h, l, AbsString.alpha("callee"))._2
              )
              fun_loc.foldLeft(h)((hh, l) => {
                val obj = hh(l).update("arguments", PropValue(ObjectValue(Value(NullTop), BFalse, BFalse, BFalse)))
                hh.update(l, obj)
              })
            }
            else h

          ((h1, ctx), (HeapBot, ContextBot))
        case ExitExc => 
          val h1 = 
            // restore the 'arguments' property of the function object
            if(Config.domMode) {
              val (fid, _) = cp._1
              val arguments_loc = h(SinglePureLocalLoc)(cfg.getArgumentsName(fid))._1._1._2
              val fun_loc = arguments_loc.foldLeft(LocSetBot)((lset, l) =>
                lset ++ Helper.Proto(h, l, AbsString.alpha("callee"))._2
              )
              fun_loc.foldLeft(h)((hh, l) => {
                val obj = hh(l).update("arguments", PropValue(ObjectValue(Value(NullTop), BFalse, BFalse, BFalse)))
                hh.update(l, obj)
              })
            }
            else h

          ((h1, ctx), (HeapBot, ContextBot))

        case Block(insts) => {
          insts.foldLeft(((h, ctx), (HeapBot, ContextBot)))(
            (states, inst) => {
              val ((h_new, ctx_new), (he_new, ctxe_new)) = I(cp, inst, states._1._1, states._1._2, states._2._1, states._2._2, inTable)
              // System.out.println("##### Instruction : " + inst)
              // System.out.println("in heap#####\n" + DomainPrinter.printHeap(4, states._1._1, cfg))
              //System.out.println("out heap#####\n" + DomainPrinter.printHeap(4, h_new, cfg))
              // System.out.println("outexc heap#####\n" + DomainPrinter.printHeap(4, he_new, cfg))
              // System.out.println("out context#####\n" + DomainPrinter.printContext(4, ctx_new))
              if(compareOption) {  
                // change heap location for preanalyzed PureLocalLoc
                val _obj = h_new(SinglePureLocalLoc)
                val _h_new = h_new.remove(SinglePureLocalLoc).update(preCFG.getMergedPureLocal(cp._1._1), _obj) 
                
                if (!(_h_new <= preState._1) && !Config.quietMode) { // && ctx_new <= preState._2)) {
                System.err.println("\n\n *** PreAnalyzed Heap should include analyzed heap using current option ***")
                System.err.println("   = Instruction : " + inst)
                /*
                System.err.println("   * dump heap")
                System.err.println(DomainPrinter.printHeap(4, _h_new, cfg))
                */
                // get all locations in a dense heap
                // pre-analyzed heap should include all dense heap
                val lset = _h_new.map.keySet
                lset.foreach(l => {
                  // for each object
                  val o1 = _h_new(l)
                  val preHeap = preState._1
                  preHeap.map.get(l) match {
                    case Some(o2) => {
                      // for each property
                      val props = o1.getAllProps ++ o2.getAllProps
                  props.foreach((prop) => {
                    val pv_1 = o1(prop)
                    val pv_2 = o2(prop)
                    if (pv_1._1 <= pv_2._1 && pv_1._2 <= pv_2._2) {
                          // dense <= pre && pre <= dense which means that equals...
                          // dense <= pre && pre </ dense which means that pre is greater than dense OK!
                    } else {
                      if (pv_2._1 <= pv_1._1 && pv_2._2 <= pv_1._2 && !Config.quietMode) {
                        // dense </ pre && pre <= dense
                        System.err.println("more imprecise result for ("+DomainPrinter.printLoc(l)+"("+l+"),"+prop+")")
                        System.err.println("dense   : "+ DomainPrinter.printObj(0, o1.restrict_(Set(prop))))
                        System.err.println("pre     : "+ DomainPrinter.printObj(0, o2.restrict_(Set(prop))))
                      } else if (!Config.quietMode) {
                        // dense </ pre && pre </ dense
                        System.err.println("different result for ("+DomainPrinter.printLoc(l)+"("+l+"),"+prop+")")
                        System.err.println("dense   : "+ DomainPrinter.printObj(0, o1.restrict_(Set(prop))))
                        System.err.println("pre     : "+ DomainPrinter.printObj(0, o2.restrict_(Set(prop))))
                      }
                    }
                  })
                }
                case None => {
                  if (!Config.quietMode)
                    System.err.println("location "+DomainPrinter.printLoc(l)+" is missing in sparse-ddg result.")
                }
                  }
                })
              }}
              ((h_new, ctx_new), (he_new, ctxe_new))
              // val h_merged = states._1._1 + h_new
              // val c_merged = states._1._2 + ctx_new
              // val lpdefset = Access.I_def(inst, h_merged, c_merged)
              // val lpuseset = Access.I_use(inst, h_merged, c_merged)
              // val defset = lpdefset.toSet
              // val useset = lpuseset.toSet

              // val realdef = (Access.heap_diff(states._1._1, h_new)).toSet
              // if ((!realdef.subsetOf(defset)) && (h_new != HeapBot)){
              //   val omitted = realdef &~ defset
              //   System.err.println("* Warning: access-analysis defset is unsound for "+inst)
              //   System.err.println("== defset ==")
              //   defset.foreach((v) => System.err.println("("+v._1 + ", "+ v._2 +")"))
              //   System.err.println("== real defset ==")
              //   realdef.foreach((v) => System.err.println("("+v._1 + ", "+ v._2 +")"))
              //   System.err.println("== missing defset ==")
              //   omitted.foreach((v) => System.err.println("("+v._1 + ", "+ v._2 +")"))
              //   System.err.println("== Before ==")
              //   System.err.println(DomainPrinter.printHeap(4, states._1._1))
              //   System.err.println("== After ==")
              //   System.err.println(DomainPrinter.printHeap(4, h_new))
              // }

              // if (useset.isEmpty) {
              //   System.err.println("* Warning: useset semantic function is missing for "+inst)
              // } else {
              //   val ((h_use, ctx_use), (he_use, ctxe_use)) =
              //     try {
              //       I(cp, inst, states._1._1.restrict(lpuseset), states._1._2.restrict(lpuseset), states._2._1, states._2._2)
              //     } catch {
              //       case e => {
              //         System.err.println("* Warning: access-analysis useset is unsound for "+inst)
              //         System.err.println("== restricted heap ==")
              //         System.err.println(DomainPrinter.printHeap(4, states._1._1.restrict(lpuseset)))
              //         System.err.println("== defset ==")
              //         defset.foreach((v) => System.err.println("("+v._1 + ", "+ v._2 +")"))
              //         System.err.println("== useset ==")
              //         useset.foreach((v) => System.err.println("("+v._1 + ", "+ v._2 +")"))
              //         System.err.println("== Heap ==")
              //         System.err.println(DomainPrinter.printHeap(4, states._1._1))
              //         e.printStackTrace()
              //         ((h_new, ctx_new), (he_new, ctxe_new))
              //       }
              //     }

              //   val check = try {
              //     Access.heap_check(h_new, h_use, lpdefset)
              //   } catch {
              //     case e => {
              //       e.printStackTrace()
              //     }
              //     true
              //   }
              //   if (!check) {
              //     System.err.println("* Warning: access-analysis useset is unsound for "+inst)
              //     System.err.println("== restricted heap ==")
              //     System.err.println(DomainPrinter.printHeap(4, states._1._1.restrict(lpuseset)))
              //     System.err.println("== defset ==")
              //     defset.foreach((v) => System.err.println("("+v._1 + ", "+ v._2 +")"))
              //     System.err.println("== useset ==")
              //     useset.foreach((v) => System.err.println("("+v._1 + ", "+ v._2 +")"))
              //     System.err.println("== restricted output Heap ==")
              //     System.err.println(DomainPrinter.printHeap(4, h_use))
              //     System.err.println("== normal output Heap ==")
              //     System.err.println(DomainPrinter.printHeap(4, h_new))
              //     System.err.println("== normal input heap ==")
              //     System.err.println(DomainPrinter.printHeap(4, states._1._1))
              //   }
              // }
              // ((h_new, ctx_new), (he_new, ctxe_new))
              /*   the end of test code for access analysis */
            })
        }
      }
       // System.out.println("in heap#####\n" + DomainPrinter.printHeap(4, h, cfg))
       // System.out.println("out heap#####\n" + DomainPrinter.printHeap(4, h_1, cfg))
       // System.out.println("outexc heap#####\n" + DomainPrinter.printHeap(4, he_1, cfg))
      (State(h_1, ctx_1), State(he_1, ctxe_1))
    }
  }

  private def readTable(cp: ControlPoint, inTable: Table): State = {
    inTable.get(cp._1) match {
      case None => StateBot
      case Some(map) => map.get(cp._2) match {
        case None => StateBot
        case Some(state) => state
      }   
    }   
  }
  
  private def updateTable(cp: ControlPoint, state: State, inTable: Table): Unit = {
    inTable.get(cp._1) match {
      case None =>
        inTable.update(cp._1, HashMap((cp._2, state)))
      case Some(map) =>
        inTable.update(cp._1, map.updated(cp._2, state))
    }   
  }


  def I(cp: ControlPoint, i: CFGInst, h: Heap, ctx: Context, he: Heap, ctxe: Context, inTable: Table = MHashMap()) = {
    def noStop(oh: Heap, octx: Context): (Heap, Context) = {
      if (Config.noStopMode) {
        val ohp = if (oh <= HeapBot) h else oh
        val octxp = if (octx <= ContextBot) ctx else octx
        (ohp, octxp)
      } else (oh, octx)
    }
    // for debug
//    System.out.println("\nInstruction: "+i)
//    System.out.println("in heap#####\n" + DomainPrinter.printHeap(4, h, cfg))
//    System.out.println("in context#####\n" + DomainPrinter.printContext(4, ctx))
    if (h == HeapBot) {
      ((h, ctx), (he, ctxe))
    } else {
      val s = i match {
        case CFGAlloc(_, _, x, e, a_new) => {
          val a_new2 = if (locclone) Helper.extendAddr(a_new, Helper.callContextToNumber(cp._2)) else a_new
          val l_r = addrToLoc(a_new2, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, a_new2)
          val (ls_v, es) = e match {
            case None => (ObjProtoSingleton, ExceptionBot)
            case Some(proto) => {
              val (v,es_) = SE.V(proto, h_1, ctx_1)
              if (v._1 </ PValueBot)
                (v._2 ++ ObjProtoSingleton, es_)
              else
                (v._2, es_)
            }
          }
          val h_2 = Helper.allocObject(h_1, ls_v, l_r)
          val h_3 = Helper.VarStore(h_2, x, Value(l_r))
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          val s = (he + h_e, ctxe + ctx_e)

          (noStop(h_3, ctx_1), s)
        }
        case CFGAllocArray(_, _, x, n, a_new) => {
          val a_new2 = if (locclone) Helper.extendAddr(a_new, Helper.callContextToNumber(cp._2)) else a_new
          val l_r = addrToLoc(a_new2, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, a_new2)
          val np = AbsNumber.alpha(n.toInt)
          val h_2 = h_1.update(l_r, Helper.NewArrayObject(np))
          val h_3 = Helper.VarStore(h_2, x, Value(l_r))
          (noStop(h_3, ctx_1), (he, ctxe))
        }
        case CFGAllocArg(_, _, x, n, a_new) => {
          val a_new2 = if (locclone) Helper.extendAddr(a_new, Helper.callContextToNumber(cp._2)) else a_new
          val l_r = addrToLoc(a_new2, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, a_new2)
          val np = AbsNumber.alpha(n.toInt)
          val h_2 = h_1.update(l_r, Helper.NewArgObject(np))
          val h_3 = Helper.VarStore(h_2, x, Value(l_r))
          (noStop(h_3, ctx_1), (he, ctxe))
        }
        case CFGExprStmt(_, _, x, e) => {
          val (v,es) = SE.V(e, h, ctx)
          locCountCheck(i, v)
          val (h_1, ctx_1) =
            if (v </ ValueBot) {
              (Helper.VarStore(h, x, v), ctx)
            } else {
              (HeapBot, ContextBot)
            }
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          (noStop(h_1, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        case CFGDelete(_, _, lhs, expr) =>
          expr match {
            case CFGVarRef(_, id) => {
              val lset_base = Helper.LookupBase(h, id)
              val (h_1, b) =
                if (lset_base.isEmpty) {
                  (h, BoolTrue)
                } else {
                  val abs_id = AbsString.alpha(id.toString)
                  lset_base.foldLeft[(Heap,AbsBool)](HeapBot, BoolBot)((v, l_base) => {
                    val (h_d, b_d) = Helper.Delete(h, l_base, abs_id)
                    (v._1 + h_d, v._2 + b_d)
                  })
                }
              val h_2 = Helper.VarStore(h_1, lhs, Value(b))
              (noStop(h_2, ctx), (he, ctxe))
            }

            case _ => {
              val (v, es) = SE.V(expr, h, ctx)
              val (h_1, ctx_1) =
                if (v </ ValueBot) {
                  (Helper.VarStore(h, lhs, Value(BoolTrue)), ctx)
                } else {
                  (HeapBot, ContextBot)
                }
              val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
              (noStop(h_1, ctx_1), (he + h_e, ctxe + ctx_e))
            }
          }
        case CFGDeleteProp(_, _, lhs, obj, index) => {
          val (v_index, es) = SE.V(index, h, ctx)
          val (h_2, ctx_2) =
            if (v_index <= ValueBot) (HeapBot, ContextBot)
            else {
              // lset must not be empty because obj is coming through <>toObject.
              val lset = SE.V(obj, h, ctx)._1._2

              val sset = Helper.toStringSet(Helper.toPrimitive_better(h, v_index))
              val (h_1, b) = lset.foldLeft[(Heap, AbsBool)](HeapBot, BoolBot)((res1, l) => {
                sset.foldLeft(res1)((res2, s) => {
                  val (h_,b_) = Helper.Delete(h,l,s)
                  (res2._1 + h_, res2._2 + b_)
                })
              })
              (Helper.VarStore(h_1, lhs, Value(b)), ctx)
            }
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          (noStop(h_2, ctx_2), (he + h_e, ctxe + ctx_e))
        }
        case CFGStore(id, info, obj, index, rhs) => {
          // unique key
          val key = (cp, id, id) // (FunctionId, InstructionId, InstructionId)  
          
          // TODO: toStringSet should be used in more optimized way
          val (h_1, ctx_1, es_1) = {
            val (v_index, es_index) = SE.V(index, h, ctx)
            if (v_index <= ValueBot) (HeapBot, ContextBot, es_index)
            else {
              val (v_rhs, es_rhs) = SE.V(rhs, h, ctx)
              locCountCheck(i, v_rhs)
              if (v_rhs <= ValueBot) (HeapBot, ContextBot, es_index ++ es_rhs)
              else {
                // lset must not be empty because obj is coming through <>toObject.
                val lset = SE.V(obj, h, ctx)._1._2
                // iterate over set of strings for index
                val sset = Helper.toStringSet(Helper.toPrimitive_better(h, v_index))
                val (h_2, ctx_2, es_2) = sset.foldLeft((HeapBot, ctx, es_index ++ es_rhs))((res, s) => {
                //if(s.gamma.isEmpty) {
                //  println("StrTopCase : " + s)
               // }
                  // non-array objects
                  val lset_narr = lset.filter(l => (BFalse <= Helper.IsArray(h, l)) && BTrue <= Helper.CanPut(h, l, s))
                  // array objects
                  val lset_arr = lset.filter(l => (BTrue <= Helper.IsArray(h, l)) && BTrue <= Helper.CanPut(h, l, s))
                  // can not store
                  val h_cantput =
                    if (lset.exists((l) => BFalse <= Helper.CanPut(h, l, s))) h
                    else HeapBot
                  // store for non-array object
                  //val h_narr = lset_narr.foldLeft(HeapBot)((_h, l) => _h + Helper.PropStore(h, l, s, v_rhs))

                  val (h_dom, ctx1) = if(Config.domPropMode) {
                    /* DOM property update: some side effects such as DOM tree update might happen */
                    val lset_dom = lset_narr.filter(l => HTMLTopElement.getInsLoc(h).contains(l))
                    val (_h, _ctx1) = if(lset_dom.size==0) (h, ContextBot)
                      else if(lset_dom.size==1) DOMHelper.updateDOMProp(h, ctx, lset_dom.head, s, v_rhs, cfg, key)
                      else { 
                        lset_dom.foldLeft((h, ctx))((hc, l) => {
                              val (_h1, _ctx2) = DOMHelper.updateDOMProp(h, ctx, l, s, v_rhs, cfg, key)
                              (hc._1 + _h1, hc._2 + _ctx2)
                              })
                    }
                    (_h, _ctx1)
                  }  else (h, ContextBot)

                  val h_narr =
                    if (lset_narr.size == 0)
                      HeapBot
                    else if (lset_narr.size == 1)
                      Helper.PropStore(h_dom, lset_narr.head, s, v_rhs)
                    else {
                      lset_narr.foldLeft(h_dom)((hh, l) => Helper.PropStoreWeak(hh, l, s, v_rhs))
                    }
                  // 15.4.5.1 [[DefineOwnProperty]] of Array
                  val (h_arr, ex) = lset_arr.foldLeft((HeapBot, ExceptionBot))((_hex, l) => {
                    // 3. s is length
                    val (h_length, ex_len) =
                      if (AbsString.alpha("length") <= s) {
                        val v_newLen = Value(Operator.ToUInt32(v_rhs))
                        val n_oldLen = h(l)("length")._1._1._1._4 // number
                        val b_g = n_oldLen < v_newLen._1._4
                        val b_eq = n_oldLen === v_newLen._1._4
                        val b_canputLen = Helper.CanPut(h, l, AbsString.alpha("length"))
                        // 3.d
                        val n_value = Helper.toNumber(v_rhs._1) + Helper.toNumber(Helper.objToPrimitive(v_rhs._2, "Number"))
                        val ex_len =
                          { val eq = (n_value === v_newLen._1._4)
                          if (BFalse <= eq) {
                            if (Config.typingInterface != null)
                              if(Shell.params.opt_DeveloperMode || eq <= BFalse)
                                Config.typingInterface.signal(info.getSpan, Range15_4_5_1, v_newLen._1._4.toString, n_value.toString)
                            Set[Exception](RangeError)
                          } else Set[Exception]()
                          }
                        val h_normal =
                          if (BTrue <= (n_value === v_newLen._1._4)) {
                            // 3.f
                          val h1 =
                            if ((BTrue <= b_g || BTrue <= b_eq) && BTrue <= b_canputLen)
                              Helper.PropStore(h, l, AbsString.alpha("length"), v_rhs)
                            else HeapBot
                          // 3.g
                          val h2 =
                            if (BFalse <= b_canputLen) h
                            else HeapBot
                          // 3.j, 3.l
                          val h3 =
                            if (BFalse <= b_g && BTrue <= b_canputLen) {
                              val _h1 = Helper.PropStore(h, l, AbsString.alpha("length"), v_rhs)
                              (v_newLen._1._4.getSingle, n_oldLen.getSingle) match {
                                case (Some(n1), Some(n2)) =>
                                  (n1.toInt until n2.toInt).foldLeft(_h1)((__h, i) =>
                                    Helper.Delete(__h, l, AbsString.alpha(i.toString))._1)
                                case _ =>
                                  if (v_newLen._1._4 <= NumBot || n_oldLen <= NumBot)
                                    HeapBot
                                  else
                                    Helper.Delete(_h1, l, NumStr)._1
                              }
                            }
                            else HeapBot
                          h1 + h2 + h3
                          }
                          else
                            HeapBot
                        (h_normal, ex_len)
                      }
                      else
                        (HeapBot, ExceptionBot)
                    // 4. s is array index
                    val h_index =
                      if (BTrue <= Helper.IsArrayIndex(s)) {
                      val n_oldLen = h(l)("length")._1._1._1._4 // number
                      val n_index = Operator.ToUInt32(Value(Helper.toNumber(PValue(s))))
                      val b_g = n_oldLen < n_index
                      val b_eq = n_oldLen === n_index
                      val b_canputLen = Helper.CanPut(h, l, AbsString.alpha("length"))
                      // 4.b
                      val h1 =
                        if ((BTrue <= b_g || BTrue <= b_eq) && BFalse <= b_canputLen)  h
                        else HeapBot
                        // 4.c
                      val h2 =
                        if (BTrue <= (n_index < n_oldLen))  Helper.PropStore(h, l, s, v_rhs)
                        else HeapBot
                      // 4.e
                      val h3 =
                        if ((BTrue <= b_g || BTrue <= b_eq) && BTrue <= b_canputLen) {
                          val _h3 = Helper.PropStore(h, l, s, v_rhs)
                        val v_newIndex = Operator.bopPlus(Value(n_index), Value(AbsNumber.alpha(1)))
                        Helper.PropStore(_h3, l, AbsString.alpha("length"), v_newIndex)
                        }
                        else HeapBot
                      h1 + h2 + h3
                      }
                      else
                        HeapBot
                    // 5. other
                    val h_normal =
                      if (s != AbsString.alpha("length") && BFalse <= Helper.IsArrayIndex(s)) Helper.PropStore(h, l, s, v_rhs)
                      else HeapBot
                    (_hex._1 + h_length + h_index + h_normal, _hex._2 ++ ex_len)
                  })
                  (res._1 + h_cantput + h_narr + h_arr, res._2 + ctx1, res._3 ++ ex)
                })
                (h_2, ctx_2, es_2)
              }
            }
          }
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es_1)
          (noStop(h_1, ctx_1) , (he + h_e, ctxe + ctx_e))
        }

        case CFGFunExpr(_, _, lhs, None, fid, a_new1, a_new2, None) => {
          val a_new3 = if (locclone) Helper.extendAddr(a_new1, Helper.callContextToNumber(cp._2)) else a_new1
          val a_new4 = if (locclone) Helper.extendAddr(a_new2, Helper.callContextToNumber(cp._2)) else a_new2
          val l_r1 = addrToLoc(a_new3, Recent)
          val l_r2 = addrToLoc(a_new4, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, a_new3)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, a_new4)
          val o_new = Helper.NewObject(ObjProtoLoc)
          val n = AbsNumber.alpha(cfg.getArgVars(fid).length)
          val fvalue = Value(PValueBot, LocSet(l_r1))
          val scope = h_2(SinglePureLocalLoc)("@env")._2
          val h_3 = h_2.update(l_r1, Helper.NewFunctionObject(fid, scope, l_r2, n))

          val pv = PropValue(ObjectValue(fvalue, BTrue, BFalse, BTrue))
          val h_4 = h_3.update(l_r2, o_new.update("constructor", pv, exist = true))

          val h_5 = Helper.VarStore(h_4, lhs, fvalue)
          (noStop(h_5, ctx_2), (he, ctxe))
        }
        case CFGFunExpr(_, _, lhs, Some(name), fid, a_new1, a_new2, Some(a_new3)) => {
          val a_new4 = if (locclone) Helper.extendAddr(a_new1, Helper.callContextToNumber(cp._2)) else a_new1
          val a_new5 = if (locclone) Helper.extendAddr(a_new2, Helper.callContextToNumber(cp._2)) else a_new2
          val a_new6 = if (locclone) Helper.extendAddr(a_new3, Helper.callContextToNumber(cp._2)) else a_new3
          val l_r1 = addrToLoc(a_new4, Recent)
          val l_r2 = addrToLoc(a_new5, Recent)
          val l_r3 = addrToLoc(a_new6, Recent)
          val (h_1, ctx_1) = Helper.Oldify(h, ctx, a_new4)
          val (h_2, ctx_2) = Helper.Oldify(h_1, ctx_1, a_new5)
          val (h_3, ctx_3) = Helper.Oldify(h_2, ctx_2, a_new6)
          val o_new = Helper.NewObject(ObjProtoLoc)
          val n = AbsNumber.alpha(cfg.getArgVars(fid).length)
          val scope = h_3(SinglePureLocalLoc)("@env")._2
          val o_env = Helper.NewDeclEnvRecord(scope)
          val fvalue = Value(PValueBot, LocSet(l_r1))
          val h_4 = h_3.update(l_r1, Helper.NewFunctionObject(fid, Value(l_r3), l_r2, n))
          val h_5 = h_4.update(l_r2, o_new.update("constructor", PropValue(ObjectValue(fvalue, BTrue, BFalse, BTrue)), exist = true))
          val h_6 = h_5.update(l_r3, o_env.update(name, PropValue(ObjectValue(fvalue, BFalse, BoolBot, BFalse))))
          val h_7 = Helper.VarStore(h_6, lhs, fvalue)
          (noStop(h_7, ctx_3), (he, ctxe))
        }
        case CFGConstruct(_, info, cons, thisArg, arguments, a_new, b_new) => {
          // cons, thisArg and arguments must not be bottom
          //val a_new2 = if (locclone) extendAddr(a_new, Helper.callContextToNumber(cp._2)) else a_new
          val a_new2 = if (locclone) Helper.extendAddr(a_new, Helper.callContextToNumber(cp._2)) else a_new
          val l_r = addrToLoc(a_new2, Recent)
          val (h_1_, ctx_1_) = Helper.Oldify(h, ctx, a_new2)
          val (v_1, es_1) = SE.V(cons, h_1_, ctx_1_)
          val lset = v_1._2
          val lset_f = lset.filter(l => BTrue <= Helper.HasConstruct(h_1_,l))
          val lset_tarf = lset.filter(l => BTrue <= Helper.IsBound(h_1_,l))
          val lset_this = Helper.getThis(h_1_, SE.V(thisArg, h_1_, ctx_1_)._1)
          val v_arg = SE.V(arguments, h_1_, ctx_1_)._1
          // XXX: stop if thisArg or arguments is LocSetBot(ValueBot)
          if(lset_this == LocSetBot || v_arg == ValueBot) ((h, ctx), (he, ctxe))
          else {
          val o_old = h_1_(SinglePureLocalLoc)
          val cc_caller = cp._2
          val n_aftercall = cfg.getAftercallFromCall(cp._1)
          val cp_aftercall = (n_aftercall, cc_caller)
          val n_aftercatch = cfg.getAftercatchFromCall(cp._1)
          val cp_aftercatch = (n_aftercatch, cc_caller)
          
          // for bind constructor
          val l_r_arg = addrToLoc(b_new, Recent)
          val (h_1, ctx_1) =
            if(lset_tarf.isEmpty) {
              (h_1_, ctx_1_)
            } else {
              val (h_, ctx_) = Helper.Oldify(h_1_, ctx_1_, b_new)
              // merge all arguments in lset_target_function
              val origin_args = v_arg._2.foldLeft(Obj.bottom)((obj, o_l) => obj + h_(o_l))
              val lset_target_args = lset_tarf.foldLeft(LBot)((lset, l_tf) => {
                h_(l_tf)("@bound_args")._2._2 ++ lset
              })
              val target_args = lset_target_args.foldLeft(Obj.bottom)((obj, l_ta) => obj + h_(l_ta))
              val new_args = Helper.concat(target_args, origin_args)
              (h_.update(l_r_arg, new_args), ctx_)
            }
          
          lset_tarf.foreach {l_f => {
            val o_f = h_1(l_f)
//            val fids = o_f("@target_function")._1._3
            val (fids, scope_locs) = o_f("@target_function")._2._2.foldLeft((FunSetBot, LocSetBot))((fidslocs, l) => ((fidslocs._1 ++ h_1(l)("@function")._3, fidslocs._2 ++ h_1(l)("@scope")._2._2)))
            fids.foreach {fid => {
              if (Config.typingInterface != null && !cfg.isUserFunction(fid))
                Config.typingInterface.setSpan(info.getSpan)
              val ccset = cc_caller.NewCallContext(h, cfg, fid, l_r, lset_this, Some(a_new))
              ccset.foreach {case (cc_new, o_new) => {
                val value = PropValue(ObjectValue(Value(l_r_arg), BTrue, BFalse, BFalse))
                val o_new2 =
                  o_new.
                    update(cfg.getArgumentsName(fid), value, exist = true).
//                    update("@scope", o_f("@scope"))
                    update("@scope", PropValue(ObjectValue(Value(scope_locs), BoolBot, BoolBot, BoolBot), FunSetBot))
                addCallEdge(cp, ((fid,LEntry), cc_new), ContextEmpty, o_new2)
                addReturnEdge(((fid,LExit), cc_new), cp_aftercall, ctx_1, o_old)
                addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_1, o_old)
              }}
            }}
          }}
          
          
          // for actual constructor
          lset_f.foreach {l_f => {
            val o_f = h_1(l_f)
            val fids = o_f("@construct")._3
            fids.foreach {fid => {
              if (Config.typingInterface != null && !cfg.isUserFunction(fid))
                Config.typingInterface.setSpan(info.getSpan)
              val ccset = cc_caller.NewCallContext(h, cfg, fid, l_r, lset_this, Some(a_new))
              ccset.foreach {case (cc_new, o_new) => {
                val value = PropValue(ObjectValue(v_arg, BTrue, BFalse, BFalse))
                val o_new2 =
                  o_new.
                    update(cfg.getArgumentsName(fid), value, exist = true).
                    update("@scope", o_f("@scope"))
                addCallEdge(cp, ((fid,LEntry), cc_new), ContextEmpty, o_new2)
                addReturnEdge(((fid,LExit), cc_new), cp_aftercall, ctx_1, o_old)
                addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_1, o_old)
              }}
            }}
          }}
          val h_2 = v_arg._2.foldLeft(HeapBot)((hh, l) => {
            val pv = PropValue(ObjectValue(Value(lset_f ++ lset_tarf), BTrue, BFalse, BTrue))
            hh + h_1.update(l, h_1(l).update("callee", pv))
          })

          // exception handling
          val cond = lset.exists(l => 
            if(BFalse <= Helper.HasConstruct(h_1,l)) BFalse <= Helper.IsBound(h_1, l)
            else false)
          val es_2 =
            if (cond) Set(TypeError)
            else ExceptionBot
          val es_3 =
            if (v_1._1 </ PValueBot) Set(TypeError)
            else ExceptionBot
          
          val es = es_1 ++ es_2 ++ es_3
          val (h_e, ctx_e) = Helper.RaiseException(h_1, ctx_1, es)

          val s_1 = (he + h_e, ctxe + ctx_e)
          
          // update the 'arguments' property of the function object
          val h_3 = if(Config.domMode) {
                      lset_f.foldLeft(h_2)((hh, l) => {
                        val obj = hh(l).update("arguments", PropValue(ObjectValue(v_arg, BFalse, BFalse, BFalse)))
                        hh.update(l, obj)
                      })
                    }
                    else h_2

          val h_4 =
            if (lset_f.isEmpty && lset_tarf.isEmpty) HeapBot
            else h_3

          (noStop(h_4, ctx_1), s_1)
        }}
        case CFGCall(_, info, fun, thisArg, arguments, a_new, b_new) => {
          // cons, thisArg and arguments must not be bottom
          val a_new2 = if (locclone & Config.loopSensitive) Helper.extendAddr(a_new, Helper.callContextToNumber(cp._2)) else a_new
          val l_r = addrToLoc(a_new2, Recent)
          val (h_1_, ctx_1_) = Helper.Oldify(h, ctx, a_new2)
          val (v_1, es_1) = SE.V(fun, h_1_, ctx_1_)
          val lset = v_1._2
          val lset_f = lset.filter(l => BTrue <= Helper.IsCallable(h_1_,l))
          val lset_tarf = lset.filter(l => BTrue <= Helper.IsBound(h_1_,l))
          val lset_this = Helper.getThis(h_1_, SE.V(thisArg, h_1_, ctx_1_)._1)
          val v_arg = SE.V(arguments, h_1_, ctx_1_)._1
          // XXX: stop if thisArg or arguments is LocSetBot(ValueBot)
          if(lset_this == LocSetBot || v_arg == ValueBot) ((h, ctx), (he, ctxe))
          else {
          val o_old = h_1_(SinglePureLocalLoc)
          val cc_caller = cp._2
          val n_aftercall = cfg.getAftercallFromCall(cp._1)
          val cp_aftercall = (n_aftercall, cc_caller)
          val n_aftercatch = cfg.getAftercatchFromCall(cp._1)
          val cp_aftercatch = (n_aftercatch, cc_caller)
          
          // for bind call
          val l_r_arg = addrToLoc(b_new, Recent)
          val (h_1, ctx_1) =
            if(lset_tarf.isEmpty) {
              (h_1_, ctx_1_)
            } else {
              val (h_, ctx_) = Helper.Oldify(h_1_, ctx_1_, b_new)
              // merge all arguments in lset_target_function
              val origin_args = v_arg._2.foldLeft(Obj.bottom)((obj, o_l) => obj + h_(o_l))
              val lset_target_args = lset_tarf.foldLeft(LBot)((lset, l_tf) => {
                h_(l_tf)("@bound_args")._2._2 ++ lset
              })
              val target_args = lset_target_args.foldLeft(Obj.bottom)((obj, l_ta) => obj + h_(l_ta))
              val new_args = Helper.concat(target_args, origin_args)
              (h_.update(l_r_arg, new_args), ctx_)
            }

          lset_tarf.foreach {l_f => {
            val o_f = h_1(l_f)
            val (fids, scope_locs) = o_f("@target_function")._2._2.foldLeft((FunSetBot, LocSetBot))((fidslocs, l) => ((fidslocs._1 ++ h_1(l)("@function")._3, fidslocs._2 ++ h_1(l)("@scope")._2._2)))
            val l_this = o_f("@bound_this")._2._2
            fids.foreach {fid => {
              if (Config.typingInterface != null && !cfg.isUserFunction(fid))
                Config.typingInterface.setSpan(info.getSpan)
              val ccset = cc_caller.NewCallContext(h, cfg, fid, l_r, l_this, Some(a_new))
              ccset.foreach {case (cc_new, o_new) => {
                val value = PropValue(ObjectValue(Value(l_r_arg), BTrue, BFalse, BFalse))
                   
                val o_new2 =
                  o_new.
                    update(cfg.getArgumentsName(fid), value, exist = true).
                    update("@scope", PropValue(ObjectValue(Value(scope_locs), BoolBot, BoolBot, BoolBot), FunSetBot))
                addCallEdge(cp, ((fid,LEntry), cc_new), ContextEmpty, o_new2)
                addReturnEdge(((fid,LExit), cc_new), cp_aftercall, ctx_1, o_old)
                addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_1, o_old)
              }}
            }}
          }}
           
          //if(lset_f.size > 20) {
          //  println("lset_size " + lset_f.size + " more than 20!! at node : " + cfg.findEnclosingNode(i))
          //  throw new RuntimeException("*** lset_size!!!")
          //}

          // for actual call
          lset_f.foreach {l_f => {
            val o_f = h_1(l_f)
            val fids = o_f("@function")._3
            fids.foreach {fid => {
              if (Config.typingInterface != null && !cfg.isUserFunction(fid))
                Config.typingInterface.setSpan(info.getSpan)
              val ccset = cc_caller.NewCallContext(h, cfg, fid, l_r, lset_this, Some(a_new))
              ccset.foreach {case (cc_new, o_new) => {
                val value = PropValue(ObjectValue(v_arg, BTrue, BFalse, BFalse))
                val o_new2 =
                  o_new.
                    update(cfg.getArgumentsName(fid), value, exist = true).
                    update("@scope", o_f("@scope"))
                addCallEdge(cp, ((fid,LEntry), cc_new), ContextEmpty, o_new2)
                addReturnEdge(((fid,LExit), cc_new), cp_aftercall, ctx_1, o_old)
                addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_1, o_old)
              }}
            }}
          }}
          val h_2 = v_arg._2.foldLeft(HeapBot)((hh, l) => {
            val pv = PropValue(ObjectValue(Value(lset_f ++ lset_tarf), BTrue, BFalse, BTrue))
            hh + h_1.update(l, h_1(l).update("callee", pv))
          })

          // exception handling
          val cond = lset.exists(l => {
            if(BFalse <= Helper.IsCallable(h_1,l)) BFalse <= Helper.IsBound(h_1,l)
            else false
          })
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
          val (h_e, ctx_e) = Helper.RaiseException(h_1, ctx_1, es)

          val s_1 = (he + h_e, ctxe + ctx_e)

          // update the 'arguments' property of the function object
          val h_3 = if(Config.domMode) {
                      lset_f.foldLeft(h_2)((hh, l) => {
                        val obj = hh(l).update("arguments", PropValue(ObjectValue(v_arg, BFalse, BFalse, BFalse)))
                        hh.update(l, obj)
                      })
                    }
                    else h_2

          val h_4 =
            if (lset_f.isEmpty && lset_tarf.isEmpty) HeapBot
            else h_3

          (noStop(h_4, ctx_1), s_1)
        }}
        /* Assert */
        case CFGAssert(_, info, expr, _) => {
          if(Config.assertMode)
            B(info, expr, h, ctx, he, ctxe, i, cfg, cp)
          else
            (noStop(h, ctx), (he, ctxe))
        }
        /* Loop */
        case CFGCond(_, info, expr, loopexit) => {
          val (v_cond, es) = SE.V(expr, h, ctx)
          val cond = Helper.toBoolean(v_cond)
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          (noStop(h, ctx), (he + h_e, ctxe + ctx_e))

          //if(BoolTop <= cond) {
          //  val old_s = readTable(cp, inTable)
          //  val new_s = new State(old_s._1 + h, old_s._2)
          //  updateTable(cp, new_s, inTable)
          //  ((h, ctx), (he + h_e, ctxe + ctx_e))

            //cfg.addEdge(cfg.findEnclosingNode(i), loopexit)
          //  ((old_s._1, ctx), (he + h_e, ctxe + ctx_e))

          //}
          //else {
            //cfg.removeEdge(cfg.findEnclosingNode(i), loopexit)
          //  ((h, ctx), (he + h_e, ctxe + ctx_e))
          //}
          //else
          //  ((h, ctx), (he + h_e, ctxe + ctx_e))

          //if(BoolTrue <= cond && cond <= BoolTrue) {
          //  System.err.println("* Warning: unrolling for loop :")
            // build a new cfg for the body statement
            /*
            val n_cond = cfg.findEnclosingNode(i)
            val n_cond_succ = cfg.getSucc(n_cond)
            n_cond_succ.foreach(x => cfg.removeEdge(n_cond, x))
            val n_assert = cfg.newBlock(fid)
            cfg.addInst(n_assert, CFGAssert(cfg.newInstId, info, expr, true))
            cfg.addEdge(n_cond, n_assert)
            val cfgb = new CFGBuilder(body)
            val (ns1, lmap1) = cfgb.translateStmt_p(body, cfg, List(n_assert), lmap, fid)
            val n_newcond = cfg.newBlock(fid)
            val lmap2 = lmap1.updated("#throw", lmap1("#throw") + n_assert + n_newcond)
            cfg.addInst(n_newcond, CFGCond(cfg.newInstId, info, expr, body, loophead, lmap2, fid))
            cfg.addEdge(ns1, n_newcond)
            n_cond_succ.foreach(x => cfg.addEdge(n_newcond, x))
            val fileName: String = "ccfg"


            System.out.println("\nSeparating graphs...")
                    val vs: Visualization = new Visualization(null, fileName, Shell.toOption("ccfg"), cfg)
                            vs.run(false)
            */
          //  System.err.println("* Warning: end loop :")
          //  val old_s = readTable(cp, inTable)
          //  val new_s = new State(HeapBot, old_s._2)
          //  updateTable(cp, new_s, inTable)
          //  ((h, ctx), (he + h_e, ctxe + ctx_e))
          //}
          //else {
          //  System.err.println("* Warning: getting out of loop")
            // cfg.addEdge(cfg.findEnclosingNode(i), loophead)
          //  ((h, ctx), (he + h_e, ctxe + ctx_e))
          //}

          /*
          expr match {
            case CFGBin(in, first, op, second) if op.getKind == EJSOp.BIN_COMP_REL_LESS =>
              val (v_cond, es) = SE.V(expr, h, ctx)
              val cond = Helper.toBoolean(v_cond)
              val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
              if(BoolTrue <= cond && cond <= BoolTrue) {
                // build a new cfg for the body statement
                val n_cond = cfg.findEnclosingNode(i)
                val n_cond_succ = cfg.getSucc(n_cond)
                n_cond_succ.foreach(x => cfg.removeEdge(n_cond, x))
                val n_assert = cfg.newBlock(fid)
                cfg.addInst(n_assert, CFGAssert(cfg.newInstId, in, expr, true))
                cfg.addEdge(n_cond, n_assert)
                val cfgb = new CFGBuilder(body)
                val (ns1, lmap1) = cfgb.translateStmt_p(body, cfg, List(n_assert), lmap, fid)
                val n_newcond = cfg.newBlock(fid)
                val lmap2 = lmap1.updated("#throw", lmap1("#throw") + n_assert + n_newcond)
                cfg.addInst(n_newcond, CFGCond(cfg.newInstId, info, expr, body, loophead, lmap2, fid))
                cfg.addEdge(ns1, n_newcond)
                n_cond_succ.foreach(x => cfg.addEdge(n_newcond, x))
                ((h, ctx), (he + h_e, ctxe + ctx_e))
              }
              else {
                cfg.addEdge(cfg.findEnclosingNode(i), loophead)
                ((h, ctx), (he + h_e, ctxe + ctx_e))
              }
            case _ =>
              cfg.addEdge(cfg.findEnclosingNode(i), loophead)
              ((h, ctx), (he, ctxe))
          }*/
        }

        case CFGCatch(_, _, name) => {
          val v_old = h(SinglePureLocalLoc)("@exception_all")._2
          val h_1 = Helper.CreateMutableBinding(h, name, h(SinglePureLocalLoc)("@exception")._2)
          val new_obj = h_1(SinglePureLocalLoc).update("@exception", PropValue(v_old))
          val h_2 = h_1.update(SinglePureLocalLoc, new_obj)
          (noStop(h_2, ctx), (HeapBot, ContextBot))
        }
        case CFGReturn(_, _, expr) => {
          val (v,es) =
            expr match {
              case Some(e) => SE.V(e, h, ctx)
              case None => (Value(UndefTop),Set[Exception]())
            }
          val (h_1, ctx_1) =
            if (v </ ValueBot) {
              (h.update(SinglePureLocalLoc, h(SinglePureLocalLoc).update("@return", PropValue(v))), ctx)
            } else {
              (HeapBot, ContextBot)
            }
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          (noStop(h_1, ctx_1), (he + h_e, ctxe + ctx_e))
        }
        case CFGThrow(_, _, expr) => {
          val (v,es) = SE.V(expr,h,ctx)
          val v_old = h(SinglePureLocalLoc)("@exception_all")._2
          val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
          val new_obj =
            h(SinglePureLocalLoc).
              update("@exception", PropValue(v)).
              update("@exception_all", PropValue(v + v_old)).
              update("@return", PropValueUndefTop)
          val h_1 = h.update(SinglePureLocalLoc, new_obj)

          (noStop(HeapBot, ContextBot), (h_1 + h_e, ctx + ctx_e))
        }
        case CFGInternalCall(_, info, lhs, fun, arguments, loc) => {
          (fun.toString, arguments, loc)  match {
            case ("<>Global<>toObject", List(expr), Some(a_new)) => {
              val (v,es_1) = SE.V(expr, h, ctx)
              val (h_3, ctx_3, es_3) =
                if (v </ ValueBot) {
                  val (v_1, h_1, ctx_1, es_2) = Helper.toObject(h, ctx, v, a_new)
                  val (h_2, ctx_2) =
                    if (v_1 </ ValueBot) {
                      (Helper.VarStore(h_1, lhs, v_1), ctx_1)
                    } else {
                      (HeapBot, ContextBot)
                    }
                  (h_2, ctx_2, es_1 ++ es_2)
                } else {
                  (HeapBot, ContextBot, es_1)
                }
              val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es_3)
              (noStop(h_3, ctx_3), (he + h_e, ctxe + ctx_e))
            }
            case ("<>Global<>isObject", List(expr), None) => {
              val (v,es) = SE.V(expr, h, ctx)
              val (h_1, ctx_1) =
                if (v </ ValueBot) {
                  val b_1 =
                    if (!v._2.isEmpty) BoolTrue
                    else BoolBot
                  val b_2 =
                    if (v._1 </ PValueBot) BoolFalse
                    else BoolBot
                  val b = b_1 + b_2
                  (Helper.VarStore(h, lhs, Value(b)), ctx)
                } else {
                  (HeapBot, ContextBot)
                }
              val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
              (noStop(h_1, ctx_1), (he + h_e, ctxe + ctx_e))
            }
            case ("<>Global<>toNumber", List(expr), None) => {
              val (v,es) = SE.V(expr, h, ctx)
              val (h_1, ctx_1) =
                if (v </ ValueBot) {
                  val pv = Helper.toPrimitive_better(h, v)
                  (Helper.VarStore(h, lhs, Value(Helper.toNumber(pv))), ctx)
                } else {
                  (HeapBot, ContextBot)
                }
              val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
              (noStop(h_1, ctx_1), (he + h_e, ctxe + ctx_e))
            }
            case ("<>Global<>toBoolean", List(expr), None) => {
              val (v,es) = SE.V(expr, h, ctx)
              val (h_1, ctx_1) =
                if (v </ ValueBot) {
                  (Helper.VarStore(h, lhs, Value(Helper.toBoolean(v))), ctx)
                } else {
                  (HeapBot, ContextBot)
                }
              val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
              (noStop(h_1, ctx_1), (he + h_e, ctxe + ctx_e))
            }
            case ("<>Global<>getBase", List(expr_2), None) => {
              val x_2 = expr_2.asInstanceOf[CFGVarRef].id
              val lset_base = Helper.LookupBase(h, x_2)
              (noStop(Helper.VarStore(h, lhs, Value(lset_base)), ctx), (he, ctxe))
            }
            case ("<>Global<>iteratorInit", List(expr), Some(a_new)) => {              
              val a_new2 = if (locclone) Helper.extendAddr(a_new, Helper.callContextToNumber(cp._2)) else a_new
              if (Config.defaultForinUnrollingCount == 0) {
                (noStop(h, ctx), (he, ctxe))
              } else {
                // Works only if for-in loops are unrolled.
                val (v,_) = SE.V(expr, h, ctx)
                val v_obj = Value(PValue(UndefBot, NullBot, v._1._3, v._1._4, v._1._5), v._2)
                val (v_1, h_1, ctx_1, _) = Helper.toObject(h, ctx, v_obj, a_new2)
                if (!(h_1 <= HeapBot)) {
                  val lset = v_1._2
                  val init_obj = Obj.empty.update("index", PropValue(AbsNumber.alpha(0)))

                  val list_obj: Obj =
                    if (lset.size > 0) {
                      try {
                        val props = Helper.CollectProps(h_1, lset)
                        //                    System.out.println("* iteratorInit size: "+props.size)
                        // TODO The order of properties should be fixed.
                        val array = props.toArray
                        (0 to array.length - 1).foldLeft(init_obj)((o, i) => {
                          o.update(i.toString, PropValue(AbsString.alpha(array(i))))
                        })
                      } catch {
                        // not a concrete case
                        case e: InternalError => {
                          init_obj.update(AbsString.NumTop, PropValue(StrTop))
                        }
                      }
                    } else {
                      init_obj.update(AbsString.NumTop, PropValue(StrTop))
                    }

                  val list_obj_2 = if (v._1._1 </ UndefBot || v._1._2 </ NullBot) {
                    // if a given object is nullable, the first iteration can be canceled.
                    val ov = list_obj("0")
                    list_obj.update("0", ov).absentTop("0")
                  } else {
                    list_obj
                  }

                  // reuse a_new. therefore, drop 'h_1, ctx_1'.
                  val l_new = addrToLoc(a_new2, Recent)
                  val h_2 = h.update(l_new, list_obj_2)
                  val h_3 = Helper.VarStore(h_2, lhs, Value(l_new))
                  ((h_3, ctx), (he, ctxe))
                } else if (v._1._1 </ UndefBot || v._1._2 </ NullBot) {
                  val init_obj = Obj.empty.update("index", PropValue(AbsNumber.alpha(0)))

                  val l_new = addrToLoc(a_new, Recent)
                  val h_2 = h.update(l_new, init_obj)
                  val h_3 = Helper.VarStore(h_2, lhs, Value(l_new))
                  (noStop(h_3, ctx), (he, ctxe))
                } else {
                  (noStop(HeapBot, ContextBot), (HeapBot, ContextBot))
                }
              }
            }
            case ("<>Global<>iteratorHasNext", List(expr_2, expr_3), None) => {
              if (Config.defaultForinUnrollingCount == 0) {
                (noStop(Helper.VarStore(h, lhs, Value(PValue(BoolTop))), ctx), (he, ctxe))
              } else {
                // exception can be ignored since expr_3 is a temporal variable.
                val (v_iter, _) = SE.V(expr_3, h, ctx)
                val lset = v_iter._2

                if (lset.size > 0) {
                  if(lset.size != 1)
                ((Helper.VarStore(h, lhs, Value(PValue(BoolTop))), ctx), (he, ctxe))

                  else {
                  // lset must be a single location.
                  assert(lset.size == 1)
                  val l_obj = lset.head
                  val o = h(l_obj)

                  val idx = o("index")._2._1._4
                  val s_idx: AbsString = Helper.toString(PValue(idx))
                  val pv = o(s_idx)
                  val name = pv._2._1._5
                  val absent = o.domIn(s_idx)

                  val b_1 =
                    if (!(name <= StrBot)) AbsBool.alpha(true)
                    else BoolBot
                  val b_2 =
                    if (BoolFalse <= absent) AbsBool.alpha(false)
                    else BoolBot
                  val b = b_1 + b_2

                  val (h_1, ctx_1) =
                    if (b </ BoolBot) {
                      (Helper.VarStore(h, lhs, Value(PValue(b))), ctx)
                    } else {
                      (HeapBot, ContextBot)
                    }

                  ((h_1, ctx_1), (he, ctxe))
                }} else {
                  (noStop(HeapBot, ContextBot), (HeapBot, ContextBot))
                }
              }
            }
            case ("<>Global<>iteratorNext", List(expr_2, expr_3), None) => {
              if (Config.defaultForinUnrollingCount == 0) {
                (noStop(Helper.VarStore(h, lhs, Value(PValue(StrTop))), ctx), (he, ctxe))
              } else {
                val (v_iter, _) = SE.V(expr_3, h, ctx)
                val lset = v_iter._2

                if (lset.size > 0) {
                  if(lset.size != 1)
                ((Helper.VarStore(h, lhs, Value(PValue(StrTop))), ctx), (he, ctxe))

                  else {
                  // lset must be a single location.
                  assert(lset.size == 1)
                  val l_obj = lset.head
                  val o = h(l_obj)

                  // increasing iterator index
                  val idx = o("index")._2._1._4
                  val s_idx: AbsString = Helper.toString(PValue(idx))
                  val next_idx = idx.getSingle match {
                    case Some(n) => AbsNumber.alpha(n + 1)
                    case None => idx
                  }
                  // index strong update
                  val h_1 = h.update(l_obj, h(l_obj).update("index", PropValue(next_idx)))

                  val name = o(s_idx)._2._1._5
                  val h_2 = Helper.VarStore(h_1, lhs, Value(name))

                  (noStop(h_2, ctx), (he, ctxe))
                }} else {
                  (noStop(HeapBot, ContextBot), (HeapBot, ContextBot))
                }
              }
            }
            case _ => {
              if (!Config.quietMode)
                System.out.println(fun.toString)
              throw new NotYetImplemented()
            }
          }
        }
        case CFGNoOp(_, info, _) => {
          val filename = info.getSpan.getFileNameOnly
          // jQuery modeling : add an heap information for jQuery
          if(Config.jqMode && NU.isModeledLibrary(filename)){
            val globalObj = h(GlobalLoc)
            val env_obj = Obj.empty.update("_$", globalObj("$")).update("_jQuery", globalObj("jQuery"))
            val newGlobalObj = globalObj.update("$", PropValue(ObjectValue(Value(JQuery.ConstLoc), BFalse, BFalse, BFalse))).update(
                                                "jQuery", PropValue(ObjectValue(Value(JQuery.ConstLoc), BFalse, BFalse, BFalse)))
            val newheap = h.update(GlobalLoc, newGlobalObj).update(JQuery.EnvLoc, env_obj)
            (noStop(newheap, ctx), (he, ctxe))
          }
          else 
            (noStop(h, ctx), (he, ctxe))
        }
        case CFGAPICall(info, model, fun, args) => {
          val semantics = ModelManager.getModel(model).getSemanticMap()
          if(Shell.params.opt_Domstat && model == "DOM") { 
            val lset_callee = getArgValue(h, ctx, args, "callee")._2
            val abstraction = lset_callee.size > 1
            if(!abstraction)
              DOMStatistics.addAPI(fun)
          }
          semantics.get(fun) match {
            case Some(f) =>
              val ((h2, ctx2), (he2, ctxe2)) = f(this, h, ctx, he, ctxe, cp, cfg, fun, args)
              (noStop(h2, ctx2), (he2, ctxe2))
            case None =>
              if (!Config.quietMode)
                System.err.println("* Warning: Semantics of the API function '"+fun+"' are not defined.")
              ((h,ctx), (he, ctxe))
          }
        }
        case CFGAsyncCall(_, _, model, call_type, addr1, addr2, addr3) => {
          val ((h2, ctx2), (he2, ctxe2)) = ModelManager.getModel(model).asyncSemantic(this, h, ctx, he, ctxe, cp, cfg, call_type, List(addr1, addr2, addr3))
          (noStop(h2, ctx2), (he2, ctxe2))
        }
      }
      // System.out.println("out heap#####\n" + DomainPrinter.printHeap(4, s._1._1, cfg))

      // Collect bottom heaps
      if(Shell.params.opt_BottomDump && s._1._1 == HeapBot && !i.isInstanceOf[CFGAssert]) insertHeapBottom(cp, i)

      s
    }
  }


  // Adds inter-procedural call edge from call-node cp1 to entry-node cp2.
  // Edge label ctx records callee context, which is joined if the edge existed already.
  def addCallEdge(cp1: ControlPoint, cp2: ControlPoint, ctx: Context, obj: Obj) = {
    ipSuccMap.get(cp1) match {
      case None =>
        ipSuccMap.update(cp1, MHashMap((cp2, (ctx, obj))))
      case Some(map2) =>
        map2.synchronized {
          map2.get(cp2) match {
            case None =>
              map2.update(cp2, (ctx, obj))
            case Some((old_ctx, old_obj)) =>
              map2.update(cp2, (old_ctx + ctx, old_obj + obj))
          }
        }
    }

    ipPredMap.get(cp2) match {
      case None => ipPredMap.update(cp2, MHashSet(cp1))
      case Some(set) => set.synchronized { set.add(cp1) }
    }
  }

  // Adds inter-procedural return edge from exit or exit-exc node cp1 to after-call node cp2.
  // Edge label ctx records caller context, which is joined if the edge existed already.
  // If change occurs, cp1 is added to worklist as side-effect.
  def addReturnEdge(cp1: ControlPoint, cp2: ControlPoint, ctx: Context, obj: Obj): Unit = {
    ipSuccMap.synchronized {
      ipSuccMap.get(cp1) match {
        case None =>
          ipSuccMap.update(cp1, MHashMap((cp2, (ctx, obj))))
          worklist.add(cp1)
        case Some(map2) =>
          map2.synchronized {
            map2.get(cp2) match {
              case None =>
                map2.update(cp2, (ctx, obj))
                worklist.add(cp1)
              case Some((old_ctx, old_obj)) =>
                var changed = false
                val new_ctx =
                  if (ctx <= old_ctx) old_ctx
                  else {
                    changed = true
                    old_ctx + ctx
                  }
                val new_obj =
                  if (obj <= old_obj) old_obj
                  else {
                    changed = true
                    old_obj + obj
                  }
                if (changed) {
                  map2.update(cp2, (new_ctx, new_obj))
                  worklist.add(cp1)
                }
            }
          }
      }
    }

    ipPredMap.synchronized {
      ipPredMap.get(cp2) match {
        case None => ipPredMap.update(cp2, MHashSet(cp1))
        case Some(set) => set.synchronized { set.add(cp1) }
      }
    }
  }

  // Assert semantics
  def B(info:Info, expr: CFGExpr, h: Heap, ctx: Context, he: Heap, ctxe: Context, inst: CFGInst, cfg: CFG, cp: ControlPoint) = {
    def noStop(oh: Heap, octx: Context): (Heap, Context) = {
      if (Config.noStopMode) {
        val ohp = if (oh <= HeapBot) h else oh
        val octxp = if (octx <= ContextBot) ctx else octx
        (ohp, octxp)
      } else (oh, octx)
    }

    val relSet = expr match {
      case CFGBin(i, first, op, second) if AH.isRelationalOperator(op) =>
        getRel(expr, State(h, ctx)) ++ getRel(CFGBin(i, second, AH.reflectiveIROp(op), first), State(h, ctx))
      case CFGBin(_, first, op, second) if AH.isObjectOperator(op) =>
        HashSet(RelExpr(first, op, second))
      case _ => getRel(expr, State(h, ctx))
    }

    // transform notIn and notInstanceof to ! in ! instanceof to evaluate them.
    val (v, es) = expr match{
      case CFGBin(i, first, op, second) if op.getKind == EJSOp.BIN_COMP_REL_NOTIN =>
        SE.V(CFGUn(i, IRFactory.makeOp("!"), CFGBin(i, first, IRFactory.makeOp("in"), second)), h, ctx)
      case CFGBin(i, first, op, second) if op.getKind == EJSOp.BIN_COMP_REL_NOTINSTANCEOF =>
        SE.V(CFGUn(i, IRFactory.makeOp("!"), CFGBin(i, first, IRFactory.makeOp("instanceof"), second)), h, ctx)
      case _ =>
        SE.V(expr, h, ctx)
    }

    val (h_e, ctx_e) = Helper.RaiseException(h, ctx, es)
    if(BoolTrue <= Helper.toBoolean(v)) {
        /*
      if(!Shell.params.opt_DeveloperMode && BoolTop <= Helper.toBoolean(v)){
        cfg.getCondEndNodeMap.get(cp._1) match {
          case Some(n) =>
            cfg.updateInfeasibleNodeMap(cp, true)
            cfg.addCondEndNode(n)
          case None => ()
        }
      }
        */
      (relSet.foldLeft((h, ctx))((s12, re) => {
        val ((h_b, ctx_b)) = s12
        val ((h_a, ctx_a)) = X(re, h, ctx)
        (h_b <> h_a, ctx_b <> ctx_a)
      }), (he + h_e, ctxe + ctx_e))
    }
    else if (BoolFalse <= Helper.toBoolean(v))
      ((HeapBot, ContextBot), (he + h_e, ctxe + ctx_e))
    else
      (noStop(HeapBot, ContextBot), (he + h_e, ctxe + ctx_e))
  }

  def X(re: RelExpr, h: Heap, ctx: Context):(Heap, Context) = {
    re match {
      case RelExpr(first, op, second) if AH.isConstantPruing(op, second) =>
        val v1 = SE.V(first, h, ctx)._1
        PruningConst(first match {
          case id@CFGVarRef(_, _) => Id(id)
          case prop@CFGLoad(_, _, _) => Prop(prop)
          case _ => throw new InternalError("e1 of RelExpr must be a PrunExpression")
        }, v1, op, second, h, ctx)

      case RelExpr(first, op, second) if AH.isRelationalOperator(op) =>
        val v1 = SE.V(first, h, ctx)._1
        val v2 = SE.V(second, h, ctx)._1
        Pruning(first match {
          case id@CFGVarRef(_, _) => Id(id)
          case prop@CFGLoad(_, _, _) => Prop(prop)
          case _ => throw new InternalError("e1 of RelExpr must be a PrunExpression")
        }, v1, op, v2, h, ctx)
      case RelExpr(first, op, second) if AH.isObjectOperator(op) =>
        Pruning(re, h, ctx)
      case _ => (h, ctx)
    }
  }

  def PruningConst(pe: PrunExpr, v1: Value, op: IROp, const: CFGExpr, h: Heap, ctx: Context):(Heap, Context) = {
    val (lset_base, s) = pe match {
      case Id(id: CFGVarRef) =>
        val s_ = AbsString.alpha(id.toString)
        val lset = Helper.LookupBase(h, id.id)
        (lset, s_)
      case Prop(CFGLoad(_, obj: CFGExpr, index: CFGExpr)) =>
        val l_loc = SE.V(obj, h, ctx)._1._2
        val s_ = Helper.toString(Helper.toPrimitive_better(h, SE.V(index, h, ctx)._1))
        // optimization.
//        val lset = l_loc.foldLeft(LocSetBot)((lset, l) => lset ++ Helper.ProtoBase(h, l, s))
        val lset =
          if (l_loc.size > 1) l_loc // here, l_loc is incorrect. However, if lset_base.size > 1, lset value will be ignored.
          else if (l_loc.size == 1) Helper.ProtoBase(h, l_loc.head, s_)
          else LocSetBot
        (lset, s_)
    }

    (lset_base.size, s.getSingle) match {
      case (1, Some(v)) =>
        val l = lset_base.head
        val pva = h(l)(s)
        val ov = pva._1
        val pv = op.getKind match {
          // in constant null and undefined for != operator, undefined and null are bottom for the lvalue
          case EJSOp.BIN_COMP_EQ_NEQUAL =>
            val pv = ov._1._1
            PValue(pv._3) + PValue(pv._4) + PValue(pv._5)
          case EJSOp.BIN_COMP_EQ_SNEQUAL =>
            val pv = ov._1._1
            const match {
              // for null, null is bottom
              case CFGNull() => PValue(pv._1) + PValue(pv._3) + PValue(pv._4) + PValue(pv._5)
              // otherwise undefined, undefined is bottom
              case _ => PValue(pv._2) + PValue(pv._3) + PValue(pv._4) + PValue(pv._5)
            }
          case _ => throw new InternalError("It is not possible constant pruning.")
        }
        val propv = PropValue(ObjectValue(Value(pv, ov._1._2), ov._2, ov._3, ov._4), pva._3)
        val h_new = h.update(l, h(l).update(v, propv))
        (h_new, ctx)
      case _ => (h, ctx)
    }
  }  
  
  def Pruning(re: RelExpr, h: Heap, ctx: Context):(Heap, Context) = {
    val (e1, op, e2) = re match {
      case RelExpr(first, op_, second) => (first, op_, second)
    }
    val (v1, v2) = (SE.V(e1, h, ctx)._1, SE.V(e2, h, ctx)._1)
    val s = Helper.toString(Helper.toPrimitive_better(h, v1))
    val L_base = op.getKind match {
      case EJSOp.BIN_COMP_REL_IN =>
        // optimization
        // v2._2.foldLeft(LocSetBot)((l_set, l) => l_set ++ Helper.ProtoBase(h, l, s))
        if (v2._2.size > 1) v2._2
        else if (v2._2.size == 1) Helper.ProtoBase(h, v2._2.head, s)
        else LocSetBot
      case EJSOp.BIN_COMP_REL_NOTIN =>
        v2._2
      case EJSOp.BIN_COMP_REL_INSTANCEOF =>
        v2._2
      case EJSOp.BIN_COMP_REL_NOTINSTANCEOF =>
        v2._2
      case _ => throw new InternalError("Pruning2 function receives only object operators")
    }

    L_base.size match {
      case 1 => (op.getKind match {
        case EJSOp.BIN_COMP_REL_IN => s.getSingle match {
          case Some(_) =>
              // make property definitely exist
              // TODO Need to be check: h.update(L_base.head, h(L_base.head).update(s, h(L_base.head)(s)._1))
              h.update(L_base.head, h(L_base.head).update(s, h(L_base.head)(s)))
          case _ => h
        }
        case EJSOp.BIN_COMP_REL_NOTIN => s.getSingle match {
          case Some(x) =>
              // remove property. it may be definitely absent or possibly absent(when default value exist)
              AH.DeleteAll(h, L_base.head, s)
          case _ => h
        }
        case EJSOp.BIN_COMP_REL_INSTANCEOF if v1._2.size == 1 =>
          AH.PruneInstanceof(v1._2.head, L_base.head, BoolTrue, h)
        case EJSOp.BIN_COMP_REL_NOTINSTANCEOF if v1._2.size == 1 =>
          AH.PruneInstanceof(v1._2.head, L_base.head, BoolFalse, h)
        case _ => h
      }, ctx)
      case _ => (h, ctx)
    }
  }

  def Pruning(pe: PrunExpr, v1: Value, op: IROp, v2: Value, h: Heap, ctx: Context):(Heap, Context) = {
    val (lset_base, s) = pe match {
      case Id(id: CFGVarRef) =>
        val s = AbsString.alpha(id.toString)
        val lset = Helper.LookupBase(h, id.id)
        (lset, s)
      case Prop(CFGLoad(_, obj: CFGExpr, index: CFGExpr)) =>
        val s = Helper.toString(Helper.toPrimitive_better(h, SE.V(index, h, ctx)._1))
        val l_loc = SE.V(obj, h, ctx)._1._2
        val lset = l_loc.foldLeft(LocSetBot)((lset, l) => lset ++ Helper.ProtoBase(h, l, s))
        (lset, s)
    }

    (lset_base.size, s.getSingle) match {
      case (1, Some(x)) =>
        val l = lset_base.head
        val o = AH.K(op, h(l), s, v2, v1._2)
        (h.update(l, o), ctx)
      case _ => (h, ctx)
    }
  }
  // get Relational Expressions
  def getRel(expr: CFGExpr, s: State): Set[RelExpr] = {
    expr match {
      case CFGBin(info, first, op, second) =>
        first match {
          // $e <> e'
          case id@CFGVarRef(_, _: CFGUserId) => HashSet(RelExpr(first, op, second))
          case prop@CFGLoad(_, _, _) => HashSet(RelExpr(first, op, second))
          case CFGBin(inInfo, e1, op1, e2) if validity(e1, e2, second, s) =>
            op1.getKind match {
              // (e1 + e2) <> second
              case EJSOp.ETC_PLUS => getRel(CFGBin(info, e1, op, CFGBin(dummyInfo, second, IRFactory.makeOp("-"), e2)), s) ++
                                     getRel(CFGBin(info, e2, op, CFGBin(dummyInfo, second, IRFactory.makeOp("-"), e1)), s)
              // (e1 - e2) <> second
              case EJSOp.ETC_MINUS => getRel(CFGBin(info, e1, op, CFGBin(dummyInfo, second, IRFactory.makeOp("+"), e2)), s) ++
                                      getRel(CFGBin(info, e2, AH.reflectiveIROp(op), CFGBin(dummyInfo, second, IRFactory.makeOp("-"), e1)), s)
              // (n * e2) <> second
              case EJSOp.BIN_ARITH_MUL_MULTIPLICATION if e1.isInstanceOf[CFGNumber] =>
                getRel(CFGBin(info, CFGBin(inInfo, e2, op1, e1), op, second), s)
              // (e1 * n) <> second and n > 0
              case EJSOp.BIN_ARITH_MUL_MULTIPLICATION if e2.isInstanceOf[CFGNumber] && e2.asInstanceOf[CFGNumber].toNumber > 0 =>
                getRel(CFGBin(info, e1, op, CFGBin(dummyInfo, second, IRFactory.makeOp("/"), e2)), s)
              // (e1 * n) <> second and n < 0
              case EJSOp.BIN_ARITH_MUL_MULTIPLICATION if e2.isInstanceOf[CFGNumber] && e2.asInstanceOf[CFGNumber].toNumber < 0 =>
                getRel(CFGBin(info, e1, AH.reflectiveIROp(op), CFGBin(dummyInfo, second, IRFactory.makeOp("/"), e2)), s)
              // (e1 / n) <> second and n > 0
              case EJSOp.BIN_ARITH_MUL_DIVISION if e2.isInstanceOf[CFGNumber] && e2.asInstanceOf[CFGNumber].toNumber > 0 =>
                getRel(CFGBin(info, e1, op, CFGBin(dummyInfo, second, IRFactory.makeOp("*"), e2)), s)
              // (e1 / n) <> second and n < 0
              case EJSOp.BIN_ARITH_MUL_DIVISION if e2.isInstanceOf[CFGNumber] && e2.asInstanceOf[CFGNumber].toNumber > 0 =>
                getRel(CFGBin(info, e1, AH.reflectiveIROp(op), CFGBin(dummyInfo, second, IRFactory.makeOp("*"), e2)), s)
              case _ => HashSet[RelExpr]()
            }
          case _ => HashSet[RelExpr]()
        }
      case _ => HashSet[RelExpr]()
    }
  }
  def validity(expr: CFGExpr, s:State):Boolean = {
    val (h, ctx) = (s._1, s._2)
    val v = SE.V(expr, h, ctx)._1
    if (v._1._1 <= UndefBot && v._1._2 <= NullBot && (v._1._4 <= UInt || v._1._4 <= NUInt) &&
        v._1._5 <= StrBot && v._2.isEmpty)
      true
    else
      false
  }
  def validity(expr1: CFGExpr, expr2: CFGExpr, s:State):Boolean = {
    validity( expr1, s) && validity(expr2, s)
  }
  def validity(expr1: CFGExpr, expr2: CFGExpr, expr3: CFGExpr, s:State):Boolean = {
    validity(expr1, s) && validity(expr2, s) && validity(expr3, s)
  }

  // E function for Preanalysis
  def PreE(cp1: ControlPoint, cp2: ControlPoint, ctx: Context, obj: Obj, s: State): State = {
    cp2 match {
      case ((_, LEntry),_) =>
        // System.out.println("== "+cp1 +" -> "+cp2+" ==")
        // System.out.println(DomainPrinter.printHeap(4, s._1))
        // System.out.println("== Object ==")
        // System.out.println(DomainPrinter.printObj(4, obj))
        // call edge
        if (s._1 == HeapBot) StateBot
        else {
          // make new decl env
          val env_obj = PreHelper.NewDeclEnvRecord(obj("@scope")._2)
          // obj2 is new PureLocal
          val obj2 = obj // - "@scope"
          val h1 = s._1
          // weak update PureLocal
          val h2 = h1.update(cfg.getPureLocal(cp2), obj2)
          // add new env rec to direct l_env to env_obj
          val h3 = obj2("@env")._2._2.foldLeft(h2)((hh, l_env) => {
            hh.update(l_env, env_obj)
          })
          State(h3, ctx + s._2)
        }

      case _ => cp1 match {
        case ((_, LExit),_) =>
          // System.out.println("== "+cp1 +" -> "+cp2+" ==")
          // System.out.println(DomainPrinter.printHeap(4, s._1))
          // System.out.println("== Object ==")
          // System.out.println(DomainPrinter.printObj(4, obj))
          // exit return edge
          if (s._1 == HeapBot) StateBot
          else {
            val returnVar = cfg.getReturnVar(cp2._1) match {
              case Some(x) => x
              case None => throw new InternalError("After-call node must have return variable")
            }
            val h1 = s._1
            val ctx1 = s._2
            val v = h1(cfg.getPureLocal(cp1))("@return")._2
            val (ctx2, obj1) = PreHelper.FixOldify(ctx + ctx1, obj, ctx1._3, ctx1._4)
            val h2 = h1.update(cfg.getPureLocal(cp2), obj1)
            val h3 = PreHelper.VarStore(h2, cfg.getPureLocal(cp2), returnVar, v)

            // System.out.println("===result===")
            // System.out.println(DomainPrinter.printHeap(4, h3))
            State(h3, ctx2)
          }

        case ((_, LExitExc),_) =>
          // exit-exc return edge
          if (s._1 == HeapBot) StateBot
          else {
            val h1 = s._1
            val ctx1 = s._2
            val v = h1(cfg.getPureLocal(cp1))("@exception")._2
            val (ctx2, obj1) = PreHelper.FixOldify(ctx + ctx1, obj, ctx1._3, ctx1._4)
            val v_old = obj1("@exception_all")._2
            val h2 = h1.update(cfg.getPureLocal(cp2),
                               obj1.update("@exception", PropValue(v)).
                                    update("@exception_all", PropValue(v + v_old)))
            State(h2, ctx2)
          }

        case _ => throw new InternalError("Inter-procedural edge must be call or return edge.")
      }
    }
  }

  // C function for Preanalysis
  def PreC(cp: ControlPoint, c: Cmd, s: State): State = {
    val h = s._1
    val ctx = s._2
    val PureLocalLoc = cfg.getPureLocal(cp)
    /*
    if (h <= HeapBot)
      StateBot, StateBot)
    else {
    */
      val (h_1, ctx_1) = c match {
        case Entry =>
          val (fid, _) = cp._1
          val x_argvars = cfg.getArgVars(fid)
          val x_localvars = cfg.getLocalVars(fid)
          val lset_arg = h(PureLocalLoc)(cfg.getArgumentsName(fid))._1._1._2
          var i = 0
          val h_n = x_argvars.foldLeft(h)((hh, x) => {
            val v_i = lset_arg.foldLeft(ValueBot)((vv, l_arg) => {
              vv + PreHelper.Proto(hh, l_arg, AbsString.alpha(i.toString))
            })
            i = i + 1
            PreHelper.CreateMutableBinding(hh, PureLocalLoc, x, v_i)
          })
          val h_m = x_localvars.foldLeft(h_n)((hh, x) => {
            PreHelper.CreateMutableBinding(hh, PureLocalLoc, x, Value(UndefTop))
          })
          (h_m, ctx)

        case Exit => (h, ctx)
        case ExitExc => (h, ctx)

        case Block(insts) =>
          System.out.println("instruction#: "+insts.length)
          insts.foldLeft((h, ctx))(
            (states, inst) => {
              // for debug
              //       System.out.println("***************************************************************************")
              //       System.out.println("===========================  Before ===============================")
              //       System.out.println("- Instruction : " + inst)
              //       System.out.print("- Context " + " = ")
              //       System.out.println(DomainPrinter.printContext(0, states._2))
              //       System.out.println("- In Heap " )
              //       System.out.println(DomainPrinter.printHeap(4, states._1, cfg))

              val (h_1, ctx_1) = PreI(cp, inst, states._1, states._2)
              // if( !(State(states._1, states._2) <= State(h_1, ctx_1)) ) {
              //       System.out.println("***************************************************************************")
              //       System.out.println("===========================  Before instr State  ==========================")
              //       System.out.println("- Instr : " + inst)
              //       System.out.print("- Context " + " = ")
              //       System.out.println(DomainPrinter.printContext(0, states._2))
              //       System.out.println("- Heap " )
              //       System.out.println(DomainPrinter.printHeap(4, states._1))

              //       System.out.println("=========================================================================")
              //       System.out.println()
              //       System.out.println("===========================  Current instr State  ========================")
              //       System.out.println("- Instr : " + inst)
              //       System.out.print("- Context " + " = ")
              //       System.out.println(DomainPrinter.printContext(0, ctx_1))
              //       System.out.println("- Out Heap " )
              //        System.out.println(DomainPrinter.printHeap(4, h_1, cfg))
              //       System.out.println("=========================================================================")
              //       System.out.println()
              // }
              (h_1, ctx_1)
            })
      }
      State(h_1, ctx_1)
    // }
  }

  // I function for preanalysis
  def PreI(cp: ControlPoint, i: CFGInst, h: Heap, ctx: Context): (Heap, Context)= {
    // for debug
    // System.out.println("\nInstruction: "+i)
    // System.out.println("in heap#####\n" + DomainPrinter.printHeap(4, h, cfg, 0))

    val PureLocalLoc = cfg.getPureLocal(cp)
    val s = i match {
      case CFGAlloc(_, _, x, e, a_new) => {
        val l_r = addrToLoc(a_new, Recent)
        val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, a_new)
        val (ls_v, es) = e match {
          case None => (ObjProtoSingleton, ExceptionBot)
          case Some(proto) => {
            val (v,es_) = PSE.V(proto, h_1, ctx_1, PureLocalLoc)
            if (v._1 </ PValueBot) {
              (v._2 ++ ObjProtoSingleton, es_)
            } else {
              (v._2, es_)
            }
          }
        }
        val h_2 = PreHelper.allocObject(h_1, ls_v, l_r)
        val h_3 = PreHelper.VarStore(h_2, PureLocalLoc, x, Value(l_r))
        val (h_e, ctx_e) = PreHelper.RaiseException(h_3, ctx_1, PureLocalLoc, es)

          (h_e, ctx_e)
      }
      case CFGAllocArray(_, _, x, n, a_new) => {
        val l_r = addrToLoc(a_new, Recent)
        val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, a_new)
        val np = AbsNumber.alpha(n.toInt)
        val h_2 = h_1.update(l_r, PreHelper.NewArrayObject(np))
        val h_3 = PreHelper.VarStore(h_2, PureLocalLoc, x, Value(l_r))
          (h_3, ctx_1)
      }
      case CFGAllocArg(_, _, x, n, a_new) => {
        val l_r = addrToLoc(a_new, Recent)
        val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, a_new)
        val np = AbsNumber.alpha(n.toInt)
        val h_2 = h_1.update(l_r, PreHelper.NewArgObject(np))
        val h_3 = PreHelper.VarStore(h_2, PureLocalLoc, x, Value(l_r))
          (h_3, ctx_1)
      }
      case CFGExprStmt(_, _, x, e) => {
        val (v,es) = PSE.V(e, h, ctx, PureLocalLoc)
        val (h_1, ctx_1) =
          if (v </ ValueBot) {
            (PreHelper.VarStore(h, PureLocalLoc, x, v), ctx)
          } else {
            (h, ctx)
          }
        val (h_e, ctx_e) = PreHelper.RaiseException(h_1, ctx_1, PureLocalLoc, es)
          (h_e, ctx_e)
      }
      case CFGDelete(_, _, lhs, expr) =>
        expr match {
          case CFGVarRef(_, id) => {
            val lset_base = PreHelper.LookupBase(h, PureLocalLoc, id)
            val (h_1, b) =
              if (lset_base.isEmpty) (h, BoolTrue)
              else {
                val abs_id = AbsString.alpha(id.toString)
                lset_base.foldLeft[(Heap,AbsBool)](h, BoolBot)((v, l_base) => {
                  val (h_d, b_d) = PreHelper.Delete(v._1, l_base, abs_id)
                  (h_d, v._2 + b_d)
                })
              }
            val h_2 = PreHelper.VarStore(h_1, PureLocalLoc, lhs, Value(b))
              (h_2, ctx)
          }

          case _ => {
            val (v, es) = PSE.V(expr, h, ctx, PureLocalLoc)
            val (h_1, ctx_1) =
              if (v </ ValueBot)
                (PreHelper.VarStore(h, PureLocalLoc, lhs, Value(BoolTrue)), ctx)
              else
                (h, ctx)
            val (h_e, ctx_e) = PreHelper.RaiseException(h_1, ctx_1, PureLocalLoc, es)
              (h_e, ctx_e)
          }
        }

      case CFGDeleteProp(_, _, lhs, obj, index) => {
        val (v_index, es) = PSE.V(index, h, ctx, PureLocalLoc)
        val (h_2, ctx_2) =
          if (v_index <= ValueBot) (h, ctx)
          else {
            // lset must not be empty because obj is coming through <>toObject.
            val lset = PSE.V(obj, h, ctx, PureLocalLoc)._1._2

            val sset = PreHelper.toStringSet(PreHelper.toPrimitive(v_index))
            val (h_1, b) = lset.foldLeft[(Heap, AbsBool)](h, BoolBot)((res1, l) => {
              sset.foldLeft(res1)((res2, s) => {
                val (h_, b_) = PreHelper.Delete(res2._1, l, s)
                (h_, res2._2 + b_)
              })
            })
            (PreHelper.VarStore(h_1, PureLocalLoc, lhs, Value(b)), ctx)
          }
        val (h_e, ctx_e) = PreHelper.RaiseException(h_2, ctx_2, PureLocalLoc, es)
        (h_e, ctx_e)
      }

      case CFGStore(_, _, obj, index, rhs) => {
        // TODO: toStringSet should be used in more optimized way
        // TODO: optimize. lset seems very big.
        val (h_1, ctx_1, es_1) = {
          val (v_index, es_index) = PSE.V(index, h, ctx, PureLocalLoc)
          if (v_index <= ValueBot) (h, ctx, es_index)
          else {
            val (v_rhs, es_rhs) = PSE.V(rhs, h, ctx, PureLocalLoc)
            if (v_rhs <= ValueBot) (h, ctx, es_index ++ es_rhs)
            else {
              // lset must not be empty because obj is coming through <>toObject.
              val lset = PSE.V(obj, h, ctx, PureLocalLoc)._1._2

              // iterate over set of strings for index
              val sset = PreHelper.toStringSet(PreHelper.toPrimitive(v_index))
              val (h_2, es_2) = sset.foldLeft((h, es_index ++ es_rhs))((res, s) => {
                // non-array objects
                val lset_narr = lset.filter(l => (BFalse <= PreHelper.IsArray(h, l)) && BTrue <= PreHelper.CanPut(h, l, s))
                // array objects
                val lset_arr = lset.filter(l => (BTrue <= PreHelper.IsArray(h, l)) && BTrue <= PreHelper.CanPut(h, l, s))
                // store for non-array object
                val h_narr = lset_narr.foldLeft(h)((_h, l) => _h + PreHelper.PropStore(h, l, s, v_rhs))
                // 15.4.5.1 [[DefineOwnProperty]] of Array
                val (h_arr, ex) = lset_arr.foldLeft((h, ExceptionBot))((_hex, l) => {
                  // 3. s is length
                  val (h_length, ex_len) =
                    if (AbsString.alpha("length") <= s) {
                      val v_newLen = Value(Operator.ToUInt32(v_rhs))
                      val n_oldLen = h(l)("length")._1._1._1._4 // number
                      val b_g = n_oldLen < v_newLen._1._4
                      val b_eq = n_oldLen === v_newLen._1._4
                      val b_canputLen = PreHelper.CanPut(h, l, AbsString.alpha("length"))
                      // 3.d
                      val n_value = PreHelper.toNumber(v_rhs._1) + PreHelper.toNumber(PreHelper.objToPrimitive(v_rhs._2, "Number"))
                      val ex_len =
                        if (BFalse <= (n_value === v_newLen._1._4)) Set[Exception](RangeError)
                        else Set[Exception]()
                      val h_normal =
                        if (BTrue <= (n_value === v_newLen._1._4)) {
                          // 3.f
                        val h1 =
                          if ((BTrue <= b_g || BTrue <= b_eq) && BTrue <= b_canputLen)
                            PreHelper.PropStore(h, l, AbsString.alpha("length"), v_rhs)
                          else h
                        // 3.g
                        val h2 =
                          if (BFalse <= b_canputLen) h
                          else h
                        // 3.j, 3.l
                        val h3 =
                          if (BFalse <= b_g && BTrue <= b_canputLen) {
                            val _h1 = PreHelper.PropStore(h, l, AbsString.alpha("length"), v_rhs)
                            (v_newLen._1._4.getSingle, n_oldLen.getSingle) match {
                              case (Some(n1), Some(n2)) =>
                                (n1.toInt until n2.toInt).foldLeft(_h1)((__h, i) =>
                                  PreHelper.Delete(__h, l, AbsString.alpha(i.toString))._1)
                              case _ =>
                                if (v_newLen._1._4 <= NumBot || n_oldLen <= NumBot)
                                  h
                                else
                                  PreHelper.Delete(_h1, l, NumStr)._1
                            }
                          }
                          else h
                        h1 + h2 + h3
                        }
                        else
                          h
                      (h_normal, ex_len)
                    }
                    else
                      (h, ExceptionBot)
                  // 4. s is array index
                  val h_index =
                    if (BTrue <= PreHelper.IsArrayIndex(s)) {
                    val n_oldLen = h(l)("length")._1._1._1._4 // number
                    val n_index = Operator.ToUInt32(Value(PreHelper.toNumber(PValue(s))))
                    val b_g = n_oldLen < n_index
                    val b_eq = n_oldLen === n_index
                    val b_canputLen = PreHelper.CanPut(h, l, AbsString.alpha("length"))
                    // 4.b
                    val h1 =
                      if ((BTrue <= b_g || BTrue <= b_eq) && BFalse <= b_canputLen)  h
                      else h
                    val h2 =
                      if (BTrue <= b_canputLen) {
                        // 4.c
                        val __h1 = PreHelper.PropStore(h, l, s, v_rhs)
                        // 4.e
                        if (BTrue <= b_g || BTrue <= b_eq) {
                        val v_newIndex = Operator.bopPlus(Value(n_index), Value(AbsNumber.alpha(1)))
                        PreHelper.PropStore(__h1, l, AbsString.alpha("length"), v_newIndex)
                        }
                        else __h1
                      }
                      else h
                    h1 + h2
                    }
                    else
                      h
                  // 5. other
                  val h_normal =
                    if (s != AbsString.alpha("length") && BFalse <= PreHelper.IsArrayIndex(s)) PreHelper.PropStore(h, l, s, v_rhs)
                    else h
                  (_hex._1 + h_length + h_index + h_normal, _hex._2 ++ ex_len)
                })
                (res._1 + h_narr + h_arr, res._2 ++ ex)
              })
              (h_2, ctx, es_2)
            }
          }
        }
        val (h_e, ctx_e) = PreHelper.RaiseException(h_1, ctx_1, PureLocalLoc, es_1)
        (h_e, ctx_e)
      }

      case CFGFunExpr(_, _, lhs, None, fid, a_new1, a_new2, None) => {
        val a_new3 = if (locclone) Helper.extendAddr(a_new1, Helper.callContextToNumber(cp._2)) else a_new1
        val a_new4 = if (locclone) Helper.extendAddr(a_new2, Helper.callContextToNumber(cp._2)) else a_new2
        val l_r1 = addrToLoc(a_new3, Recent)
        val l_r2 = addrToLoc(a_new4, Recent)
        val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, a_new1)
        val (h_2, ctx_2) = PreHelper.Oldify(h_1, ctx_1, a_new2)
        val o_new = PreHelper.NewObject(ObjProtoLoc)
        val n = AbsNumber.alpha(cfg.getArgVars(fid).length)
        val fvalue = Value(PValueBot, LocSet(l_r1))
        val scope = h_2(PureLocalLoc)("@env")._2
        val h_3 = h_2.update(l_r1, PreHelper.NewFunctionObject(fid, scope, l_r2, n))
        val h_4 = h_3.update(l_r2, o_new.update("constructor",
          PropValue(ObjectValue(fvalue, BoolTrue, BoolFalse, BoolTrue)), exist = true))
        val h_5 = PreHelper.VarStore(h_4, PureLocalLoc, lhs, fvalue)
        (h_5, ctx_2)
      }
      case CFGFunExpr(_, _, lhs, Some(name), fid, a_new1, a_new2, Some(a_new3)) => {
        val a_new4 = if (locclone) Helper.extendAddr(a_new1, Helper.callContextToNumber(cp._2)) else a_new1
        val a_new5 = if (locclone) Helper.extendAddr(a_new2, Helper.callContextToNumber(cp._2)) else a_new2
        val a_new6 = if (locclone) Helper.extendAddr(a_new3, Helper.callContextToNumber(cp._2)) else a_new3
        val l_r1 = addrToLoc(a_new4, Recent)
        val l_r2 = addrToLoc(a_new5, Recent)
        val l_r3 = addrToLoc(a_new6, Recent)
        val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, a_new1)
        val (h_2, ctx_2) = PreHelper.Oldify(h_1, ctx_1, a_new2)
        val (h_3, ctx_3) = PreHelper.Oldify(h_2, ctx_2, a_new3)
        val o_new = PreHelper.NewObject(ObjProtoLoc)
        val n = AbsNumber.alpha(cfg.getArgVars(fid).length)
        val scope = h_3(PureLocalLoc)("@env")._2
        val o_env = PreHelper.NewDeclEnvRecord(scope)
        val fvalue = Value(PValueBot, LocSet(l_r1))
        val h_4 = h_3.update(l_r1, PreHelper.NewFunctionObject(fid, Value(l_r3), l_r2, n))
        val h_5 = h_4.update(l_r2, o_new.update("constructor",
          PropValue(ObjectValue(fvalue, BoolTrue, BoolFalse, BoolTrue)), exist = true))
        val h_6 = h_5.update(l_r3, o_env.update(name,
          PropValue(ObjectValue(fvalue, BoolFalse, BoolBot, BoolFalse))))
        val h_7 = PreHelper.VarStore(h_6, PureLocalLoc, lhs, fvalue)
        (h_7, ctx_3)
      }
      case CFGConstruct(_, _, cons, thisArg, arguments, a_new, b_new) => {
        // cons, thisArg and arguments must not be bottom
        val l_r = addrToLoc(a_new, Recent)
        val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, a_new)
        val (v_1, es_1) = PSE.V(cons, h_1, ctx_1, PureLocalLoc)
        val lset = v_1._2
        val lset_f = lset.filter((l) => BoolTrue <= PreHelper.HasConstruct(h_1,l))
        val lset_this = PreHelper.getThis(h_1, PSE.V(thisArg, h_1, ctx_1, PureLocalLoc)._1)
        // val lset_this = PreHelper.getThis(h_1, SE.V(thisArg, h_1, ctx_1, PureLocalLoc)._1) ++
        //                 h(PureLocalLoc)("@this")._2._2 // set this value with current this value
        val v_arg = PSE.V(arguments, h_1, ctx_1, PureLocalLoc)._1
        val o_old = h_1(PureLocalLoc)
        val cc_caller = cp._2
        val n_aftercall = cfg.getAftercallFromCall(cp._1)
        val cp_aftercall = (n_aftercall, cc_caller)
        val n_aftercatch = cfg.getAftercatchFromCall(cp._1)
        val cp_aftercatch = (n_aftercatch, cc_caller)
        lset_f.foreach((l_f) => {
          val o_f = h_1(l_f)
          o_f("@construct")._3.foreach((fid) => {
            cc_caller.NewCallContext(h, cfg, fid, l_r, lset_this).foreach((pair) => {
              val (cc_new, o_new) = pair
              val o_new2 = o_new.
                update(cfg.getArgumentsName(fid),
                       PropValue(ObjectValue(v_arg, BoolTrue, BoolFalse, BoolFalse)), exist = true).
                update("@scope", o_f("@scope"))

              addCallEdge(cp, ((fid,LEntry), cc_new), ContextEmpty, o_new2)
              addReturnEdge(((fid,LExit), cc_new), cp_aftercall, ctx_1, o_old)
              addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_1, o_old)
            })
          })
        })
        val h_2 = v_arg._2.foldLeft(h_1)((hh, l) => {
          hh.update(l, hh(l).update("callee",
            PropValue(ObjectValue(Value(lset_f), BoolTrue, BoolFalse, BoolTrue))))
        })

        // exception handling
        val cond = lset.exists((l) => BoolFalse <= PreHelper.HasConstruct(h_1,l))
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
        val (h_e, ctx_e) = PreHelper.RaiseException(h_2, ctx_1, PureLocalLoc, es)
        (h_e, ctx_e)
      }
      case CFGCall(_, _, fun, thisArg, arguments, a_new, b_new) => {
        // cons, thisArg and arguments must not be bottom
        val l_r = addrToLoc(a_new, Recent)
        val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, a_new)
        val (v_1, es_1) = PSE.V(fun, h_1, ctx_1, PureLocalLoc)
        val lset = v_1._2
        val lset_f = lset.filter((l) => BoolTrue <= PreHelper.IsCallable(h_1,l))
        val lset_this = PreHelper.getThis(h_1, PSE.V(thisArg, h_1, ctx_1, PureLocalLoc)._1)
        // val lset_this = PreHelper.getThis(h_1, SE.V(thisArg, h_1, ctx_1, PureLocalLoc)._1) ++
        //                 h(PureLocalLoc)("@this")._2._2 // set this value with current this value
        val v_arg = PSE.V(arguments, h_1, ctx_1, PureLocalLoc)._1
        val o_old = h_1(PureLocalLoc)
        val cc_caller = cp._2
        val n_aftercall = cfg.getAftercallFromCall(cp._1)
        val cp_aftercall = (n_aftercall, cc_caller)
        val n_aftercatch = cfg.getAftercatchFromCall(cp._1)
        val cp_aftercatch = (n_aftercatch, cc_caller)
        val propv_arg = PropValue(ObjectValue(v_arg, BoolTrue, BoolFalse, BoolFalse))
        lset_f foreach (l_f => {
          val o_f = h_1(l_f)
          val fids = o_f("@function")._3
          fids foreach (fid => {
            val ccset = cc_caller.NewCallContext(h, cfg, fid, l_r, lset_this)
            ccset.foreach {case (cc_new, o_new) => {
              val o_new2 = o_new.update(cfg.getArgumentsName(fid), propv_arg, exist = true).
              update("@scope", o_f("@scope"))
              addCallEdge(cp, ((fid,LEntry), cc_new), ContextEmpty, o_new2)
              addReturnEdge(((fid,LExit), cc_new), cp_aftercall, ctx_1, o_old)
              addReturnEdge(((fid, LExitExc), cc_new), cp_aftercatch, ctx_1, o_old)
            }}
          })
        })
        val h_2 = v_arg._2.foldLeft(h_1)((hh, l) => {
          hh.update(l, hh(l).update("callee",
            PropValue(ObjectValue(Value(lset_f), BoolTrue, BoolFalse, BoolTrue))))
        })

        // exception handling
        val cond = lset.exists((l) => BoolFalse <= PreHelper.IsCallable(h_1,l))
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
        val (h_e, ctx_e) = PreHelper.RaiseException(h_2, ctx_1, PureLocalLoc, es)
        (h_e, ctx_e)
      }
      /* Assert */
      case CFGAssert(_, info, expr, _) => {
        val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx, PureLocalLoc, PSE.V(expr, h, ctx, PureLocalLoc)._2)
        (h_e, ctx_e)
      }
      case CFGCatch(_, _, name) => {
        val v_old = h(PureLocalLoc)("@exception_all")._2
        val h_1 = PreHelper.CreateMutableBinding(h, PureLocalLoc, name, h(PureLocalLoc)("@exception")._2)
        val h_2 = h_1.update(PureLocalLoc,
                             h_1(PureLocalLoc).update("@exception", PropValue(v_old)))
        (h_2, ctx)
      }
      case CFGReturn(_, _, expr) => {
        val (v,es) = expr match {
          case Some(e) => PSE.V(e, h, ctx, PureLocalLoc)
          case None => (Value(UndefTop),Set[Exception]())
        }
        val (h_1, ctx_1) =
          if (v </ ValueBot) {
            (h.update(PureLocalLoc, h(PureLocalLoc).update("@return", PropValue(v))), ctx)
          } else {
            (h, ctx)
          }
        val (h_e, ctx_e) = PreHelper.RaiseException(h_1, ctx_1, PureLocalLoc, es)
        (h_e, ctx_e)
      }
      case CFGThrow(_, _, expr) => {
        val (v,es) = PSE.V(expr,h,ctx, PureLocalLoc)
        val v_old = h(PureLocalLoc)("@exception_all")._2
        val (h_e, ctx_e) = PreHelper.RaiseException(h, ctx, PureLocalLoc, es)
        val h_1 = h_e.update(PureLocalLoc,
                           h_e(PureLocalLoc).update("@exception", PropValue(v + v_old)).
                                           update("@exception_all", PropValue(v + v_old)))
        (h_1, ctx_e)
      }
      case CFGInternalCall(_, _, lhs, fun, arguments, loc) => {
        (fun.toString, arguments, loc)  match {
          case ("<>Global<>toObject", List(expr), Some(a_new)) => {
            val (v,es_1) = PSE.V(expr, h, ctx, PureLocalLoc)
            val (h_1, ctx_1, es_2) =
              if (v </ ValueBot) {
                val (v_1, h_2, ctx_, es_) = PreHelper.toObject(h, ctx, v, a_new)
                val h_3 = PreHelper.VarStore(h_2, PureLocalLoc, lhs, v_1)
                (h_3, ctx_, es_)
              } else {
                (h, ctx, Set[Exception]())
              }
            val es = es_1 ++ es_2
            val (h_e, ctx_e) = PreHelper.RaiseException(h_1, ctx_1, PureLocalLoc, es)
            (h_e, ctx_e)
          }
          case ("<>Global<>isObject", List(expr), None) => {
            val (v,es) = PSE.V(expr, h, ctx, PureLocalLoc)
            val (h_1, ctx_1) =
              if (v </ ValueBot) {
                val b_1 =
                  if (!v._2.isEmpty) BoolTrue
                  else BoolBot
                val b_2 =
                  if (v._1 </ PValueBot) BoolFalse
                  else BoolBot
                val b = b_1 + b_2
                (PreHelper.VarStore(h, PureLocalLoc, lhs, Value(b)), ctx)
              } else {
                (h, ctx)
              }
            val (h_e, ctx_e) = PreHelper.RaiseException(h_1, ctx_1, PureLocalLoc, es)
            (h_e, ctx_e)
          }
          case ("<>Global<>toNumber", List(expr), None) => {
            val (v,es) = PSE.V(expr, h, ctx, PureLocalLoc)
            val (h_1, ctx_1) =
              if (v </ ValueBot) {
                val pv = PreHelper.toPrimitive(v)
                (PreHelper.VarStore(h, PureLocalLoc, lhs, Value(PreHelper.toNumber(pv))), ctx)
              } else {
                (h, ctx)
              }
            val (h_e, ctx_e) = PreHelper.RaiseException(h_1, ctx_1, PureLocalLoc, es)
            (h_e, ctx_e)
          }
          case ("<>Global<>toBoolean", List(expr), None) => {
            val (v,es) = PSE.V(expr, h, ctx, PureLocalLoc)
            val (h_1, ctx_1) =
              if (v </ ValueBot) {
                (PreHelper.VarStore(h, PureLocalLoc, lhs, Value(PreHelper.toBoolean(v))), ctx)
              } else {
                (h, ctx)
              }
            val (h_e, ctx_e) = PreHelper.RaiseException(h_1, ctx_1, PureLocalLoc, es)
            (h_e, ctx_e)
          }
          case ("<>Global<>getBase", List(expr_2), None) => {
            val x_2 = expr_2.asInstanceOf[CFGVarRef].id
            val lset_base = PreHelper.LookupBase(h, PureLocalLoc, x_2)
            (PreHelper.VarStore(h, PureLocalLoc, lhs, Value(lset_base)), ctx)
          }
          case ("<>Global<>iteratorInit", List(expr), Some(a_new)) => {
            if (Config.defaultForinUnrollingCount == 0) {
              (h, ctx)
            } else {
              // Works only if for-in loops are unrolled.
              val (v,_) = PSE.V(expr, h, ctx, PureLocalLoc)
              val v_obj = Value(PValue(UndefBot, NullBot, v._1._3, v._1._4, v._1._5), v._2)
              val (v_1, h_1, _, _) = PreHelper.toObject(h, ctx, v_obj, a_new)
              if (!(h_1 <= HeapBot)) {
                val lset = v_1._2
                val init_obj = Obj.empty.update("index", PropValue(AbsNumber.alpha(0)))

                val list_obj: Obj =
                  if (lset.size > 0) {
                    try {
                      val props = Helper.CollectProps(h_1, lset)
                      //                    System.out.println("* iteratorInit size: "+props.size)
                      // TODO The order of properties should be fixed.
                      val array = props.toArray
                      (0 to array.length - 1).foldLeft(init_obj)((o, i) => {
                        o.update(i.toString, PropValue(AbsString.alpha(array(i))))
                      })
                    } catch {
                      // not a concrete case
                      case e: InternalError => {
                        init_obj.update(AbsString.NumTop, PropValue(StrTop))
                      }
                    }
                  } else {
                    init_obj.update(AbsString.NumTop, PropValue(StrTop))
                  }

                val list_obj_2 = if (v._1._1 </ UndefBot || v._1._2 </ NullBot) {
                  // if a given object is nullable, the first iteration can be canceled.
                  val ov = list_obj("0")
                  list_obj.update("0", ov).absentTop("0")
                } else {
                  list_obj
                }

                // reuse a_new. therefore, drop 'h_1, ctx_1'.
                val l_new = addrToLoc(a_new, Recent)
                val h_2 = h.update(l_new, list_obj_2)
                val h_3 = PreHelper.VarStore(h_2, PureLocalLoc, lhs, Value(l_new))
                (h_3, ctx)
              } else {
                (HeapBot, ContextBot)
              }
            }
          }
          case ("<>Global<>iteratorHasNext", List(expr_2, expr_3), None) => {
            if (Config.defaultForinUnrollingCount == 0) {
              (PreHelper.VarStore(h, PureLocalLoc, lhs, Value(PValue(BoolTop))), ctx)
            } else {
              // exception can be ignored since expr_3 is a temporal variable.
              val (v_iter, _) = PSE.V(expr_3, h, ctx, PureLocalLoc)
              val lset = v_iter._2

              if (lset.size > 0) {
                // lset must be a single location.
                assert(lset.size == 1)
                val l_obj = lset.head
                val o = h(l_obj)

                val idx = o("index")._2._1._4
                val s_idx: AbsString = PreHelper.toString(PValue(idx))
                val pv = o(s_idx)
                val name = pv._2._1._5
                val absent = o.domIn(s_idx)

                val b_1 =
                  if (!(name <= StrBot)) AbsBool.alpha(true)
                  else BoolBot
                val b_2 =
                  if (BoolFalse <= absent) AbsBool.alpha(false)
                  else BoolBot
                val b = b_1 + b_2

                val (h_1, ctx_1) =
                  if (b </ BoolBot) {
                    (PreHelper.VarStore(h, PureLocalLoc, lhs, Value(PValue(b))), ctx)
                  } else {
                    (HeapBot, ContextBot)
                  }

                (h_1, ctx_1)
              } else {
                (HeapBot, ContextBot)
              }
            }
          }
          case ("<>Global<>iteratorNext", List(expr_2, expr_3), None) => {
            if (Config.defaultForinUnrollingCount == 0) {
              (PreHelper.VarStore(h, PureLocalLoc, lhs, Value(PValue(StrTop))), ctx)
            } else {
              val (v_iter, _) = PSE.V(expr_3, h, ctx, PureLocalLoc)
              val lset = v_iter._2

              if (lset.size > 0) {
                // lset must be a single location.
                assert(lset.size == 1)
                val l_obj = lset.head
                val o = h(l_obj)

                // increasing iterator index
                val idx = o("index")._2._1._4
                val s_idx: AbsString = PreHelper.toString(PValue(idx))
                val next_idx = idx.getSingle match {
                  case Some(n) => AbsNumber.alpha(n + 1)
                  case None => idx
                }
                // index strong update
                val h_1 = h.update(l_obj, h(l_obj).update("index", PropValue(next_idx)))

                val name = o(s_idx)._2._1._5
                val h_2 = PreHelper.VarStore(h_1, PureLocalLoc, lhs, Value(name))

                (h_2, ctx)
              } else {
                (HeapBot, ContextBot)
              }
            }
          }
          case _ => {
            if (!Config.quietMode)
              System.out.println(fun.toString)
            throw new NotYetImplemented()
          }
        }
      }
      case CFGNoOp(_, _, _) => {
        (h, ctx)
      }
      case CFGAPICall(_, model, fun, args) => {
        val semantics = ModelManager.getModel(model).getPreSemanticMap()
        val result = semantics.get(fun) match {
          case Some(f) =>
            f(this, h, ctx, h, ctx, cp, cfg, fun, args)
          case None =>
            if (!Config.quietMode)
              System.err.println("* Warning: Pre-semantics of the API function '"+fun+"' are not defined.")
            ((h,ctx), (h, ctx))
        }
        (result._1._1 + result._2._1, result._1._2 + result._2._2)
      }
      case CFGAsyncCall(_, _, model, call_type, addr1, addr2, addr3) => {
        ModelManager.getModel(model).asyncPreSemantic(this, h, ctx, h, ctx, cp, cfg, call_type, List(addr1, addr2, addr3))
      }
    }
    //System.out.println("out heap#####\n" + DomainPrinter.printHeap(4, s._1._1))
    s
//  }
  }

  private def locCountCheck(i: CFGInst, v: Value): Unit = {
    if(Shell.params.opt_MaxLocCount == 0) return
    if(v.locset.size >= Shell.params.opt_MaxLocCount) {
      throw new MaxLocCountError("[" + i.getInstId + "] " + i + " => " + DomainPrinter.printLocSet(v.locset))
    }
  }

  def insertHeapBottom(cp: ControlPoint, i: CFGInst): Unit = {
    // use ip predecessor (alternative way)
    var isChanged = false
    val maxSize = 4
    var depth = 0

    heapBotMap += ((cp, i))
  }

  def dumpHeapBottoms(): Unit = {
    var indent = 0
    def printStack(cp: ControlPoint, inst: CFGInst): Unit = {
      // Instruction info
      val source: String = if (inst == null) "" else {
        val instSpanString = inst.getInfo match {
          case Some(info) => "(" + info.getSpan.getFileNameOnly + ":" + info.getSpan.getBegin.getLine + ":" + info.getSpan.getBegin.column() + ")"
          case None => ""
        }
        "[" + inst.getInstId + "] " + inst.toString() + " " + instSpanString
      }

      // Function info
      /*val funcId = cp._1
      val funcName = if (funcId == c.getCFG.getGlobalFId) "global function"
      else {
        var tempFuncName = c.getCFG.getFuncName(funcId)
        val index = tempFuncName.lastIndexOf("@")
        if (index != -1) tempFuncName = tempFuncName.substring(0, index)
        "function " + tempFuncName
      }
      val funcSpan = c.getCFG.getFuncInfo(funcId).getSpan()
      val funcSpanBegin = funcSpan.getBegin()
      val funcSpanEnd = funcSpan.getEnd()*/

      printf("  %d> ", indent); for(i <- 0 until indent) printf("  "); indent+= 1

      printf("%s" + /*" in %s(%s:%d:%d~%d:%d)," +*/ " %s\n",
        source,
        //funcName, funcSpan.getFileNameOnly, funcSpanBegin.getLine, funcSpanBegin.column(), funcSpanEnd.getLine, funcSpanEnd.column(),
        cp)
    }

    println("** Heap Bottom List **")
    println("=========================================================================")
    for(kv <- heapBotMap) {
      val (cp, inst) = kv
      println("- HeapBot at " + cp)
      indent = 0
      printStack(cp, inst)
    }
    println("=========================================================================")
  }
}
