/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing

import scala.collection.immutable.HashMap
import scala.collection.immutable.HashSet
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.debug.DebugConsoleDSparse
import kr.ac.kaist.jsaf.Shell

class DSparseFixpoint(cfg: CFG, env: DSparseEnv, worklist: Worklist, inTable: Table, quiet: Boolean, locclone: Boolean) {
  private val sem = new Semantics(cfg, worklist, locclone)
  def getSemantics = sem
  var count = 0
  var duset: DUSet = Map()
  var time = 0.0
  var isTimeout = false
  var startTime: Long = 0

  def compute(du: DUSet): Unit = {
    Config.setDebugger(Shell.params.opt_debugger)

    // Analysis start time
    startTime = System.nanoTime()

    duset = du
    worklist.add(((cfg.getGlobalFId, LEntry), CallContext.globalCallContext), None, false)

    if (Config.debugger)
      DebugConsoleDSparse.initialize(cfg, worklist, sem, inTable, env)

    if(!quiet) System.out.println()
    loop()
    if(!quiet) System.out.println()
    if(isTimeout) System.out.println("*** Analysis time out! (" + Shell.params.opt_Timeout + " sec)")
    if(!quiet) System.out.println("# edge recovering time: "+time)
    
    if (Config.debugger)
      DebugConsoleDSparse.runFinished()
  }

  private var cache = HashMap[Int, (ControlPoint, State, State, State)]()

  private def makeKey(cp: ControlPoint, s: State): Int = {
    ((cp.hashCode() % 128) << 7 + (s.hashCode() % 128))
  }

  private def cmdsize(cmd: Cmd) = {
    cmd match {
      case Block(i) => i.length
      case _ => 1
    }
  }

   val debug_fid = cfg.getGlobalFId
   val debug_loc = LocSet(SinglePureLocalLoc) //LocSet(addrToLoc(3606,Recent))
  private def loop(): Unit = {
    while (!worklist.isEmpty) {
      if (!quiet)
        System.out.print("\r  Sparse Iteration: "+count+"        ")
      worklist.dump()

      if (Config.debugger)
        DebugConsoleDSparse.runFixpoint(count)

      count = count+1

      val (cp, callerCPSetOpt) = worklist.getHead()
      val (fg, ddg) = env.getFlowGraph(cp._1._1, cp._2)

      // Analysis timeout check
      if(Shell.params.opt_Timeout > 0) {
        if(isTimeout) return
        if((System.nanoTime() - startTime) / 1000000000 > Shell.params.opt_Timeout) {isTimeout = true; return}
      }

      val inS = readTable(cp)

      // System.out.println("\n== InHeap ==\n" + DomainPrinter.printHeap(4,inS._1, true))

      // val (outS, outES) = sem.C(cp, cfg.getCmd(cp._1), inS)
      val cmd = cfg.getCmd(cp._1)
      val (outS, outES) =
        if (15 < cmdsize(cmd)) {
          val key = makeKey(cp, inS)
          cache.get(key) match {
            case Some((cpc, inSc, outSc, outESc)) if ((cpc == cp) && (inSc == inS)) => (outSc, outESc)
            case _ => {
              val (outSo, outESo) = sem.C(cp, cmd, inS)
              if (outSo != StateBot)
                cache += (key -> (cp, inS, outSo, outESo))
              (outSo, outESo)
            }
          }
        } else {
          sem.C(cp, cmd, inS)
        }

      val recover_start = System.nanoTime()
      val edges =
        if (outS._1 != HeapBot && !cfg.getCalls.contains(cp._1)) {
          env.recoverOutEdges(fg, cp._1)
        } else {
          HashSet[(Node,Node)]()
        }
      val excEdges =
        if (outES._1 != HeapBot) {
          env.recoverOutExcEdge(fg, cp._1)
        } else {
          HashSet[(Node,Node)]()
        }

      if (!edges.isEmpty || !excEdges.isEmpty) {
        val recovered = env.recover_intra_dugraph(fg, ddg, edges, excEdges) - cp._1
        recovered.foreach(node => worklist.add((node, cp._2), None, false))
      }
      val recover_time = (System.nanoTime() - recover_start) / 1000000000.0
      time += recover_time

      if (outS._1 != HeapBot) {
        // System.out.println("\n== Heap ==\n" + DomainPrinter.printHeap(4,outS._1, cfg))
        val succs = ddg.getNormalSuccs(cp._1)
        // Propagate normal output state (outS) along normal edges.
        succs.foreach(node => {
          val cp_succ = (node, cp._2)
          val oldS = readTable(cp_succ)
          val succ_set = ddg.getDUSet(cp._1, node)
          val outS2 = outS.restrict(succ_set)
          /*
          if(cp._1._1 == debug_fid) {
          System.out.println("Propagates a heap from "+cp + " -> ("+node)
          System.out.println("Old State for "+cp_succ+"==")
          System.out.println(DomainPrinter.printHeap(4, oldS.restrict(debug_loc)._1, cfg))
          System.out.println("Merge State== ")// + DomainPrinter.printLocSet(succ_set))
          System.out.println(DomainPrinter.printHeap(4, (outS2.restrict(debug_loc))._1, cfg))
          }
          */
          if (!(outS2 <= oldS)) {
            val newS = oldS + outS2
          /*
          if(cp._1._1 == debug_fid) {
            System.out.println("Merged State by propagation "+cp_succ+"== ")// + DomainPrinter.printLocSet(succ_set))
            System.out.println(DomainPrinter.printHeap(4, newS.restrict(debug_loc)._1, cfg))
          }
          */
            worklist.add(cp._1, cp_succ, None, false)
            updateTable(cp_succ, newS)
          }
        })

        if (cfg.getCalls.contains(cp._1)) {
          // Propagate normal output state along call/after-call edges.
          val bypass_set = env.getBypassingSet(cp)
          if (!bypass_set.isEmpty) {
            val cp_succ = (cfg.getAftercallFromCall(cp._1), cp._2)
            val oldS = readTable(cp_succ)
            val outS2 = outS.restrict(bypass_set)
          /*
          if(cp._1._1 == debug_fid) {
          System.out.println("Propagates a heap from "+cp + " -> "+cp_succ._1)
          System.out.println("Old State for "+cp_succ+"==")
          System.out.println(DomainPrinter.printHeap(4, oldS.restrict(debug_loc)._1, cfg))
          System.out.println("Merge State== ")// + DomainPrinter.printLocSet(bypass_set))
          System.out.println(DomainPrinter.printHeap(4, (outS2.restrict(debug_loc))._1, cfg))
          }
          */
            if (!(outS2 <= oldS)) {
              val newS = oldS + outS2
          /*
          if(cp._1._1 == debug_fid) {
            System.out.println("Merged State== ")// + DomainPrinter.printLocSet(bypass_set))
            System.out.println(DomainPrinter.printHeap(4, newS.restrict(debug_loc)._1, cfg))
          }
          */
              worklist.add(cp._1, cp_succ, None, false)
              updateTable(cp_succ, newS)
            }
          }
        }
        
        // bypassingset of call->aftercatch is stored when exitexc node is analyzed
        if (cfg.getCalls.contains(cp._1)) {
          // Propagate exception output state along call/after-call edges.
          val bypass_set = env.getBypassingExcSet(cp)
          if (!bypass_set.isEmpty) {
            val cp_exc = (cfg.getAftercatchFromCall(cp._1), cp._2)
            val oldS = readTable(cp_exc)
            val outS2 = outS.restrict(bypass_set)
          /*
          if(cp._1._1 == debug_fid) {
          System.out.println("Propagates a heap from "+cp +" -> "+cp_exc._1)
          System.out.println("Old State for "+cp_exc+"==")
          System.out.println(DomainPrinter.printHeap(4, oldS.restrict(debug_loc)._1, cfg))
          System.out.println("Merge State== ")// + DomainPrinter.printLocSet(bypass_set))
          System.out.println(DomainPrinter.printHeap(4, outS2.restrict(debug_loc)._1, cfg))
          }
          */
            if (!(outS2 <= oldS)) {
              val newS = oldS + outS2
          /*
          if(cp._1._1 == debug_fid) {
            System.out.println("Merged State== ")// + DomainPrinter.printLocSet(bypass_set))
            System.out.println(DomainPrinter.printHeap(4, newS.restrict(debug_loc)._1, cfg))
          }
          */
              worklist.add(cp._1, cp_exc, None, false)
              updateTable(cp_exc, newS)
            }
          }
        }
      }

      if (outES._1 != HeapBot) {
        // System.out.println("\n== ExcHeap ==\n" + DomainPrinter.printHeap(4,outES._1, true))
        val esucc = ddg.getExcSucc(cp._1)

        // Propagate exception output state (outES) along exception edges.
        // 1) If successor is catch, current exception value is assigned to catch variable and
        //    previous exception values are restored.
        // 2) If successor is finally, current exception value is propagated further along
        //    finally block's "normal" edges.
        esucc match {
          case Some(node) => {
            val cp_succ = (node, cp._2)
            val oldS = readTable(cp_succ)
            val succ_set = ddg.getExcDUSet(cp._1, node)
            val outES2 = outES.restrict(succ_set)
/*
          if(cp._1._1 == debug_fid) {
            System.out.println("Propagates a excheap from "+cp +" -> "+node)
            System.out.println(DomainPrinter.printHeap(4, outES2.restrict(debug_loc)._1, cfg))
          }
*/
            if (!(outES2 <= oldS)) {
              val newES = oldS + outES2
              worklist.add(cp._1, cp_succ, None, false)
              updateTable(cp_succ, newES)
            }
          }
          case None => ()
        }
      }

      // Propagate along inter-procedural edges
      // This step must be performed after evaluating abstract transfer function
      // because 'call' instruction can add inter-procedural edges.
      sem.getIPSucc(cp) match {
        case None => ()
        case Some(succMap) =>
          succMap.foreach(kv => {

            // bypassing if IP edge is exception flow.
//            val cp_aftercall = kv._1
            val cp_succ = kv._1
/*
              cp._1._2 match {
                case LExitExc => {
                  val n_aftercall = kv._1._1
                  cfg.getExcSucc.get(n_aftercall) match {
                    case None => throw new InternalError("After-call node must have exception successor")
                    case Some(node) => (node, kv._1._2)
                  }
                }
                case _ => kv._1
              }
*/
            val oldS = readTable(cp_succ)
            val outS_E = sem.E(cp, cp_succ, kv._2._1, kv._2._2, outS)

            // if cp is after-call, call-to-after-call cfg must be recovered.
            (cp) match {
              case ((_, LExit), _) => {
                // cp_succ is cp_aftercall
                if (outS._1 != HeapBot) {
                  val recover_start = System.nanoTime()
                  val n_call = cfg.getCallFromAftercall(cp_succ._1)
                  val call = (n_call, cp_succ._2)
                  // System.out.println("try to recover normal: "+call)
                  if (env.updateBypassing(call, cp._1._1)) {
                    worklist.add(cp._1, call, None, false)
                  }
                  val (fg, ddg) = env.getFlowGraph(call._1._1, call._2)
                  val edges = env.recoverOutAftercall(fg, call._1)
                  if (!edges.isEmpty) {
                    val recovered = env.recover_intra_dugraph(fg, ddg, edges, Set())
                    recovered.foreach(node => worklist.add((node, call._2), None, false))
                  }
                  val recover_time = (System.nanoTime() - recover_start) / 1000000000.0
                  time += recover_time
                }
              }
              case ((_, LExitExc), _) => {
                // cp_succ is cp_aftercatch
                if (outS._1 != HeapBot) {
                  val recover_start = System.nanoTime()
                  val n_call = cfg.getCallFromAftercatch(cp_succ._1)
                  val call = (n_call, cp_succ._2)
                  // System.out.println("try to recover exception: "+call)
                  if (env.updateBypassingExc(call, cp._1._1)) {
                    worklist.add(cp._1, call, None, false)
                  }
                  val (fg, ddg) = env.getFlowGraph(call._1._1, call._2)
/*
                  // recover CFG edge of call.
                  val edges = env.recoverOutEdges(fg, call._1)
                  // recover EFG edge of aftercall.
                  val exc_edges = env.recoverOutExcEdge(fg, cp_aftercall._1)

                  if (!edges.isEmpty || !exc_edges.isEmpty) {
                    val recovered = env.recover_intra_dugraph(fg, ddg, edges, exc_edges)
                    recovered.foreach(node => worklist.add((node, call._2)))
                  }
*/
/*
                  // get after-catch node
                  val cp_aftercatch = cfg.getExcSucc.get(cp_aftercall._1) match {
                    case None => throw new InternalError("After-call node must have exception successor")
                    case Some(node) => (node, cp_aftercall._2)
                  }
                  // recover CFG edge of call.
                  //val edges = env.recoverOutEdges(fg, call._1)
                  // edge : call -> aftercatch for normal flow
                  val edges = HashSet[(Node, Node)]((n_call, cp_aftercatch._1))

                  // recover EFG edge of aftercall.
                  // val exc_edges = env.recoverOutExcEdge(fg, cp_aftercall._1)

                  if (!fg.isCallExcRecovered(n_call)) {
                    // call -> aftercatch normal edge should be added on flow graph
                    fg.callExcRecovered(n_call)
                    // val recovered = env.recover_intra_dugraph(fg, ddg, edges, exc_edges)
                    val recovered = env.recover_intra_dugraph(fg, ddg, edges, Set())
                    recovered.foreach(node => worklist.add((node, call._2)))
                  }
*/
                  val edges = env.recoverOutAftercatch(fg, call._1)
                  if (!edges.isEmpty) {
                    val recovered = env.recover_intra_dugraph(fg, ddg, edges, Set())
                    recovered.foreach(node => worklist.add((node, call._2), None, false))
                  }

                  val recover_time = (System.nanoTime() - recover_start) / 1000000000.0
                  time += recover_time
                }
              }
              case _ => ()
            }

            // Localization
            val outS_E2 =
              if (env.optionLocalization && cp_succ._1._2 == LEntry) {
                val useset = env.getLocalizationSet(cp_succ._1._1)
                State(outS_E._1.restrict(useset), outS_E._2)
              } else {
                outS_E
              }
/*
          if(cp_succ._1._1 == debug_fid) {
          System.out.println("Propagates a heap from "+cp + " -> "+cp_succ + " using IP edge")
          System.out.println("Old State for "+cp_succ+"==")
          System.out.println(DomainPrinter.printHeap(4, oldS.restrict(debug_loc)._1, cfg))
          System.out.println("Merge State== ")// + DomainPrinter.printLocSet(bypass_set))
          System.out.println(DomainPrinter.printHeap(4, outS_E2.restrict(debug_loc)._1, cfg))
          }
*/
            if (!(outS_E2 <= oldS)) {
              val newS = oldS + outS_E2
/*
          if(cp_succ._1._1 == debug_fid) {
          System.out.println("Merged State== ")// + DomainPrinter.printLocSet(bypass_set))
          System.out.println(DomainPrinter.printHeap(4, newS.restrict(debug_loc)._1, cfg))
          }
*/
              worklist.add(cp._1, cp_succ, None, false)
              updateTable(cp_succ, newS)
            }
          })
      }
    }
  }

  private def readTable(cp: ControlPoint): State = {
    inTable.get(cp._1) match {
      case None => StateBot
      case Some(map) => map.get(cp._2) match {
        case None => StateBot
        case Some(state) => state
      }
    }
  }

  private def updateTable(cp: ControlPoint, state: State): Unit = {
    inTable.get(cp._1) match {
      case None =>
        inTable.update(cp._1, HashMap((cp._2, state)))
      case Some(map) =>
        inTable.update(cp._1, map.updated(cp._2, state))
    }
  }
}
