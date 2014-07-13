/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing

import java.util.{List => JList}
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.nodes_util.NodeUtil
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.{PreSemanticsExpr => SE}
import kr.ac.kaist.jsaf.bug_detector.BugInfo
import kr.ac.kaist.jsaf.nodes_util.Span
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.analysis.lib.getSet

class PreTyping(_cfg: CFG, quiet: Boolean, standAlone: Boolean) extends TypingInterface {
  def env = null
  def cfg = _cfg
  var programNodes = _cfg.getNodes // without built-ins
  var fset_builtin: Map[FunctionId, String] = Map()
  override def builtinFset() = fset_builtin

  var errors = List[BugInfo]()
  def getErrors: JList[BugInfo] = toJavaList(errors)
  def signal(span: Span, bugKind: Int, msg1: String, msg2: String): Unit =
    errors ++= List(new BugInfo(span, bugKind, msg1, msg2))
  var _span: Span = null
  def getSpan = _span
  def setSpan(span: Span): Unit = {
    _span = span
    val num = errors.reverse.takeWhile(e => (e.span == null || NodeUtil.isDummySpan(e.span))).length
    if (num > 0) {
      val (front, back) = errors.splitAt(errors.length - num)
      errors = front ++ back.map(e => new BugInfo(span, e.bugKind, e.arg1, e.arg2))
    }
  }

  var numIter = 0
  var elapsedTime = 0.0d
  var state = StateBot
  override def getMergedState = state

  private var sem: Option[Semantics] = None
  def getSem = sem match {
    case None => throw new InternalError("Do the analysis first.")
    case Some(s) => s
  }

  // main entry point
  override def analyze(init: InitHeap): Unit = {
    // Adjust context-sensitivity if not running in stand-alone mode.
    val old = Config.contextSensitivityMode
    if (!standAlone) {
      // Enforce context-insensitivity if disabled or TAJS-style context-sensitivity is used.
      // Equal or strictly less precise context-sensitivity must be used for pre-analysis.
      // However, for TAJS-style context-sensitivity, the precision is incomparable.
      if (!Config.preContextSensitiveMode || 
          Config.contextSensitivityMode == Config.Context_OneObjectTAJS) {
        Config.setContextSensitivityMode(Config.Context_Insensitive)
      } 
    }

    // Initialize call context for context-sensitivity
    CallContext.initialize

    val initHeap = init.getInitHeapPre()
    val initContext = ContextEmpty
    val initState = State(initHeap, initContext)

    fset_builtin = ModelManager.getFIdMap()

    val s = System.nanoTime

    val worklist = Worklist.computes(cfg, quiet)
    val fixpoint = new PreFixpoint(cfg, worklist, initState, quiet)
    fixpoint.compute()
    sem = Some(fixpoint.getSemantics)
    state = fixpoint.result

    Config.setContextSensitivityMode(old);

    if (!quiet) {
      numIter = fixpoint.count
      System.out.println("# Pre-analysis Fixpoint iteration(#): "+numIter)
      elapsedTime = (System.nanoTime - s) / 1000000000.0;
      System.out.format("# Time for Pre-analysis(s): %.2f\n", new java.lang.Double(elapsedTime))
    }

    // System.out.println("== Flow-insensitive analysis result ==")
    // System.out.println(DomainPrinter.printHeap(4,state._1))
    // System.out.println(DomainPrinter.printContext(4,state._2))
  }

  override def dump_callgraph() = {
    val cg = computeCallGraph()
    // computes callgraph
    val callgraph: Map[FunctionId, Set[FunctionId]] =
      cg.foldLeft[Map[FunctionId, Set[FunctionId]]](Map())((m, kv) => {
        val caller = cfg.findEnclosingNode(kv._1)._1
        m.get(caller) match {
          case Some(callees) => m + (caller -> (kv._2 ++ callees))
          case None => m + (caller -> kv._2)
        }
      })

    System.out.println("== Callgraph ==")
    System.out.println("digraph \"DirectedGraph\" {")
    callgraph.foreach(kv => {
      val caller = kv._1
      kv._2.foreach(callee => {
        System.out.println(caller + "->" + callee + ";")
      })
    })
    System.out.println("}")
  }

  /**
   * Computes the call graph of the analyzed program.
   * The returned graph format is successor edge map from
   * caller instruction (CFGCall or CFGConstruct) to set of callee functions.
   * Calling context information is not considered in this method.
   * For example, if a call instruction i invokes function g1 in context cc1 and g2 in cc2,
   * the returned graph just indicates that i calls both g1 and g2.
   * Note that the returned map might not contain entries for some caller instructions
   * if they are dead code or no valid functions were ever called at them.
   *
   * @returns the map from caller to set of callees.
   */
  override def computeCallGraph(): Map[CFGInst, Set[FunctionId]] = {
    getSem.ipSuccMap.foldLeft[Map[CFGInst, Set[FunctionId]]](Map())(
      (map, n) => {
        val cp_caller = n._1
        n._2.foldLeft(map)((map_2, succ) => {
          val cp_succ = succ._1
          cfg.getCmd(cp_caller._1) match {
            case Entry => throw new InternalError("Weird call edge: "+cp_caller+" to "+cp_succ)
            case Exit | ExitExc => map_2
            case Block(insts) if insts.size > 0 => {
              val i = insts.last
              val succs = getSet(map_2, i)
              map_2 + (i -> (succs + cp_succ._1._1))
            }
            case Block(insts) => throw new InternalError("Weird call edge(caller node doesn't have instruction): "+cp_caller+" to "+cp_succ)

          }
        })
      }
    )
  }

//  override def computeCallGraph(): Map[CFGInst, Set[FunctionId]] = {
//    cfg.getNodes.foldLeft[Map[CFGInst, Set[FunctionId]]](Map())(
//      (map, n) =>
//        cfg.getCmd(n) match {
//          case Entry | Exit | ExitExc => map
//          case Block(insts) =>
//            val PureLocalLoc = cfg.getMergedPureLocal(n._1)
//            if (insts.isEmpty) map
//            else {
//              val i = insts.last
//              i match {
//                case CFGCall(_, _, fun, _, _, _) => {
//                  val h = state._1
//                  if (h.domIn(PureLocalLoc)) {
//                    val ctx = state._2
//                    val lset = SE.V(fun, h, ctx, PureLocalLoc)._1._2
//                    lset.foldLeft(map)((_m, l) => {
//                      if (BoolTrue <= PreHelper.IsCallable(h,l)) {
//                        _m.get(i) match {
//                          case None => _m + (i -> h(l)("@function")._1._3.toSet)
//                          case Some(set) => _m + (i -> (set ++ h(l)("@function")._1._3.toSet))
//                        }
//                      } else {
//                        _m
//                      }
//                    })
//                  } else {
//                    map
//                  }
//                }
//                case CFGConstruct(_, _, cons, _, _, _) => {
//                  val h = state._1
//                  if (h.domIn(PureLocalLoc)) {
//                    val ctx = state._2
//                    val lset = SE.V(cons, h, ctx, PureLocalLoc)._1._2
//                    lset.foldLeft(map)((_m, l) => {
//                      if (BoolTrue <= PreHelper.HasConstruct(h,l)) {
//                        _m.get(i) match {
//                          case None => _m + (i -> h(l)("@construct")._1._3.toSet)
//                          case Some(set) => _m + (i -> (set ++ h(l)("@construct")._1._3.toSet))
//                        }
//                      } else {
//                        _m
//                      }
//                    })
//                  } else {
//                    map
//                  }
//                }
//                case CFGAPICall(_, _, "Function.prototype.apply", args) => {
//                  val h = state._1
//                  val ctx = state._2
//                  if (h.domIn(PureLocalLoc)) {
//                    //val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, addr1)
//                    //val (h_2, ctx_2) = PreHelper.Oldify(h_1, ctx_1, addr2)
//                    //val (h_3, ctx_3) = PreHelper.Oldify(h_2, ctx_2, addr3)
//                    val h_3 = h
//                    val lset_this = h_3(PureLocalLoc)("@this")._1._2._2
//                    lset_this.foldLeft(map)((_m, l) => {
//                      if (BoolTrue <= PreHelper.IsCallable(h_3,l)) {
//                        _m.get(i) match {
//                          case None => _m + (i -> h_3(l)("@function")._1._3.toSet)
//                          case Some(set) => _m + (i -> (set ++ h_3(l)("@function")._1._3.toSet))
//                        }
//                      } else {
//                        _m
//                      }
//                    })
//                  } else {
//                    map
//                  }
//                }
//                case CFGAPICall(_, _,"Function.prototype.call", args) => {
//                  val h = state._1
//                  val ctx = state._2
//                  if (h.domIn(PureLocalLoc)) {
//                    //val (h_1, ctx_1) = PreHelper.Oldify(h, ctx, addr1)
//                    //val (h_2, ctx_2) = PreHelper.Oldify(h_1, ctx_1, addr2)
//                    val h_2 = h
//                    val lset_this = h_2(PureLocalLoc)("@this")._1._2._2
//                    lset_this.foldLeft(map)((_m, l) => {
//                      if (BoolTrue <= PreHelper.IsCallable(h_2,l)) {
//                        _m.get(i) match {
//                          case None => _m + (i -> h_2(l)("@function")._1._3.toSet)
//                          case Some(set) => _m + (i -> (set ++ h_2(l)("@function")._1._3.toSet))
//                        }
//                      } else {
//                        _m
//                      }
//                    })
//                  } else {
//                    map
//                  }
//                }
//                // computes the call graph for event functions
//                case CFGAsyncCall(_,_, model, call_type, addr1, addr2, addr3) => {
//                  ModelManager.getModel(model).asyncCallgraph(state._1, i, map, call_type, List(addr1, addr2, addr3))
//                }
//                case _ => map
//              }
//            }
//        })
//  }

  override def dump() {
    System.out.println("===========================  Final State  ===============================")
    System.out.print("- Context " + " = ")
    System.out.println(DomainPrinter.printContext(0, state._2))

    System.out.println("- Heap " )
    System.out.println(DomainPrinter.printHeap(4, state._1, cfg))
    System.out.println("=========================================================================")
    System.out.println()
  }
}
