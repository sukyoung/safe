/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing

import scala.collection.immutable.HashMap
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.debug.DebugConsole
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.scala_src.useful.WorkTrait
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

class Fixpoint(cfg: CFG, worklist: Worklist, inTable: Table, quiet: Boolean, locclone: Boolean) {
  private val sem = new Semantics(cfg, worklist, locclone)
  def getSemantics = sem
  var count = 0
  val countRef = new AnyRef
  var isLocCountExceeded = false
  var locCountExceededMessage = ""
  var isTimeout = false
  var startTime: Long = 0

  var loopInTable: Table = inTable.map(x=>x)

  def compute(): Unit = {
    Config.setDebugger(Shell.params.opt_debugger)

    // Analysis start time
    startTime = System.nanoTime()

    // Single-thread option has a priority
    if(Shell.params.opt_SingleThread || !Shell.params.opt_MultiThread) {
      /** Single-thread */

      // Debugger
      if (Config.debugger) DebugConsole.initialize(cfg, worklist, sem, inTable)

      // Add entry node
      worklist.add(((cfg.getGlobalFId, LEntry), CallContext.globalCallContext))

      // Loop
      loop()

      // Library mode
      if (Config.libMode) {
        libraryLoop()
        loop()
      }
      System.out.println()

      if (Config.debugger) DebugConsole.runFinished()
    }
    else {
      /** Multi-thread */

      // Initialize WorkManager
      Shell.workManager.initialize()

      // Add entry node
      worklist.setUseWorkManager(true, new FixpointWork)
      worklist.add(((cfg.getGlobalFId, LEntry), CallContext.globalCallContext))

      // Wait until all works are finished.
      Shell.workManager.waitFinishEvent()
      if(!worklist.isEmpty) throw new RuntimeException("*** Worklist is not empty! (1)")

      // Library mode
      if (Config.libMode) {
        libraryLoop()

        // Wait until all works are finished.
        Shell.workManager.waitFinishEvent()
        if(!worklist.isEmpty) throw new RuntimeException("*** Worklist is not empty! (2)")
      }
      System.out.println()

      // Deinitialize WorkManager
      Shell.workManager.deinitialize()
    }

    if(isLocCountExceeded) {
      System.out.println("*** Max location count(" + Shell.params.opt_MaxLocCount + ") is exceeded!")
      System.out.println("  " + locCountExceededMessage)
    }
    if(isTimeout) System.out.println("*** Analysis time out! (" + Shell.params.opt_Timeout + " sec)")

    if (Shell.params.opt_ExitDump) {
      System.out.println("** Dump Exit Heap **\n=========================================================================")
      System.out.println(DomainPrinter.printHeap(4, readTable(((cfg.getGlobalFId,LExit),CallContext.globalCallContext))._1, cfg))
      System.out.println("=========================================================================")
    }

    if (Shell.params.opt_BottomDump) sem.dumpHeapBottoms()
  }

  var timeSum: Long = 0
  class FixpointWork extends WorkTrait {
    override def doit(): Unit = {
      // Worklist check
      if(worklist.isEmpty) return

      // Worklist.head print
      if(!quiet) {
        System.out.print("\n  Dense Iteration: " + count + "(" + worklist.getSize + ")   ")
        worklist.dump()
      }

      // Debugger is used for only single-thread mode
      if (Config.debugger)
        DebugConsole.runFixpoint(count)

      // Iteration count
      count+= 1

      // Get a work
      val cp = worklist.getHead()

      // Analysis termination check
      if(isLocCountExceeded || isTimeout) return
      if(Shell.params.opt_Timeout > 0) {
        if((System.nanoTime() - startTime) / 1000000000 > Shell.params.opt_Timeout) {isTimeout = true; return}
      }

      // Read a state
      val inS = readTable(cp)

      // Execute
      val (outS, outES) = try {
        sem.C(cp, cfg.getCmd(cp._1), inS)
      }
      catch {
        case e: MaxLocCountError =>
          if(locCountExceededMessage == "") locCountExceededMessage = e.getMessage
          isLocCountExceeded = true
          return
      }

      // Propagate call site's output state to aftercall node when there is no callee. for -nostop option.
      if (Config.noStopMode && cfg.getCalls.contains(cp._1)) {
        sem.getIPSucc(cp) match {
          case None =>
            val acnode = cfg.getAftercallFromCall(cp._1)

            // When a call is ignored by -nostop, its return value is "undefined".
            val returnVar = cfg.getReturnVar(acnode) match {
              case Some(x) => x
              case None => throw new InternalError("After-call node must have return variable")
            }
            val h1 = outS._1
            val ctx1 = outS._2
            val v = Value(UndefTop)
            val h2 = Helper.VarStore(h1, returnVar, v)
            val outS2 = State(h2, ctx1)

            val cp_succ = (acnode, cp._2)
            val oldS = readTable(cp_succ)
            if (!(outS2 <= oldS)) {
              val newS = oldS + outS2
              updateTable(cp_succ, newS)
              worklist.add(cp_succ)
            }
          case _ =>
        }
      }

      // Propagate normal output state (outS) along normal edges.
      val succs = cfg.getSucc_Lock(cp._1)
      succs.foreach(node => {
        val cp_succ = (node, cp._2)
        val oldS = readTable(cp_succ)
        if(!(outS <= oldS)) {
          if(!cfg.getCondEndNodes.contains(node)){
            val prednodes = cfg.getAllPred(node)
            if(prednodes.size !=0 &&prednodes.forall(n => !cfg.getInfeasibility((n,cp._2)).isEmpty)){
              cfg.updateInfeasibleNodeMap(cp_succ, true)
            }
          }
          val newS = if(cfg.getAllPred(cp_succ._1).size <= 1) outS else oldS + outS
          updateTable(cp_succ, newS)
          worklist.add(cp_succ)
        }
      })
      
      if(Config.loopSensitive) {
        val startT = System.nanoTime()
        // Propagation through loop edge
        val lsuccs = cfg.getLoopSucc.get(cp._1) match { 
          case Some(e) => e
          case None => Map()
        }
        lsuccs.foreach(node => { 
          val cp_succ = (node, cp._2)
          val loop_cond = sem.L(cp_succ, cfg.getCmd(cp_succ._1), outS, inTable)
          val (newcontext, abstraction) = cp._2.newLoopContext(node, true, loop_cond)
          val new_cpsucc: ControlPoint = (node, newcontext)
          val oldS2 = readTable(new_cpsucc)
          val oldS3 = readLoopInTable(new_cpsucc)
          if(!(outS <= oldS2)) {
            if(!cfg.getInfeasibility(cp).isEmpty && cfg.getInfeasibility(cp).get){
              cfg.updateInfeasibleNodeMap(new_cpsucc, true)
            }
            val newS = if(abstraction == true) outS + oldS2
                       else outS            
            updateTable(new_cpsucc, newS)
            worklist.add(new_cpsucc)
          }
          if(!(outS <= oldS3)) {
            updateLoopInTable(new_cpsucc, outS + oldS2)
          }
        })
        
        // Propagation through loopIter edge
        val lisuccs = cfg.getLoopIterSucc.get(cp._1) match { 
          case Some(e) => e
          case None => Map()
        }
        lisuccs.foreach(node => { // TODO: Multi-thread safety check
          val cp_succ = (node, cp._2)
          val cmd = cfg.getCmd(cp_succ._1) 
          val oldS = readTable(cp_succ)
          val old_loopcond = sem.L(cp_succ, cmd, oldS, inTable)
          val new_loopcond = sem.L(cp_succ, cmd, outS, inTable)
          val (newcontext, abstraction) = cp._2.newLoopContext(node, false, new_loopcond, old_loopcond)
          val new_cpsucc: ControlPoint = (node, newcontext)          
          val oldS2 = readTable(new_cpsucc)
          val oldS3 = readLoopInTable(new_cpsucc)
          if(!(outS <= oldS2)) {
            val newS = if(abstraction == true) oldS3 + outS
                       else outS
            if(!cfg.getInfeasibility(cp).isEmpty && cfg.getInfeasibility(cp).get){
              cfg.updateInfeasibleNodeMap(new_cpsucc, true)
            }
            updateTable(new_cpsucc, newS)
            //loopIterTable = loopIterTable + (node -> (cp_succ, callerCPSetOpt, false))
            //println("updating loop iter table : " + node)
            worklist.add(new_cpsucc)
          }
      
          if(!(outS <= oldS3)){
            updateLoopInTable(new_cpsucc, outS + oldS3)
          }
        })

        // Propagation through loopReturn edge (for the return statement)
        val lrsuccs = cfg.getLoopReturnSucc.get(cp._1) match { 
          case Some(e) => e
          case None => Map()
        }
        lrsuccs.foreach(node => {
          val cp_succ = (node, cp._2)
          val loophead = cfg.getLoopCondPred(cp._1)
          // normal return statement
          if(loophead.size == 0){
            val oldS = readTable(cp_succ)
            if(!(outS <= oldS)) {
              val prednodes = cfg.getAllPred(node)
              if(prednodes.size !=0 &&prednodes.forall(n => !cfg.getInfeasibility((n,cp._2)).isEmpty)){
                cfg.updateInfeasibleNodeMap(cp_succ, true)
              }
              val newS = if(cfg.getAllPred_loop(cp_succ._1).size <= 1) outS else oldS + outS
              updateTable(cp_succ, newS)
              worklist.add(cp_succ)
            }
          }
          else {
            loophead.foreach (n => {
              val loop_cp_succ = (n, cp._2)
              val cmd = cfg.getCmd(n) 
              val oldS = readTable(loop_cp_succ)
              val loopcond = sem.L(loop_cp_succ, cmd, oldS, inTable)
              val (newcontext, abstraction) = cp._2.newLoopContext(n, false, loopcond, BoolBot, false, false, true)
              val new_cpsucc: ControlPoint = (node, newcontext)          
              val oldS2 = readTable(new_cpsucc)
              if(!(outS <= oldS2)) {
                val prednodes = cfg.getAllPred(cp_succ._1)
                if(prednodes.size !=0 &&prednodes.forall(n => !cfg.getInfeasibility((n,cp._2)).isEmpty)){
                  cfg.updateInfeasibleNodeMap(new_cpsucc, true)
                }
                val newS = if(abstraction == true) oldS2 + outS
                           else outS
                updateTable(new_cpsucc, newS)
                //loopIterTable = loopIterTable + (node -> (cp_succ, callerCPSetOpt, false))
                //println("updating loop iter table : " + node)
                worklist.add(new_cpsucc)
              }
            })
          }
        })

        
        // Propagation through loopBreak edge (for the break statement)
        val lbsuccs = cfg.getLoopBreakSucc.get(cp._1) match { 
          case Some(e) => e
          case None => Map()
        }
        lbsuccs.foreach(node => { // TODO: Multi-thread safety check
          val cp_succ = (node, cp._2)
          val loophead = cfg.getLoopCondPred(cp._1)
          loophead.foreach (n => {
            val loop_cp_succ = (n, cp._2)
            val cmd = cfg.getCmd(n) 
            val oldS = readTable(loop_cp_succ)
            val loopcond = sem.L(loop_cp_succ, cmd, oldS, inTable)
            val (newcontext, abstraction) = cp._2.newLoopContext(n, false, loopcond, BoolBot, true)
            val new_cpsucc: ControlPoint = (node, newcontext)          
            val oldS2 = readTable(new_cpsucc)
            if(!(outS <= oldS2)) {
               val prednodes = cfg.getAllPred(cp_succ._1)
               if(prednodes.size !=0 &&prednodes.forall(n => !cfg.getInfeasibility((n,cp._2)).isEmpty)){
               //if(cfg.getAllPred(cp_succ._1).size == 1 && !cfg.getInfeasibility(cp).isEmpty && cfg.getInfeasibility(cp).get){
                 cfg.updateInfeasibleNodeMap(new_cpsucc, true)
               }
              val newS = if(abstraction == true) oldS2 + outS
                         else outS
              updateTable(new_cpsucc, newS)
              //loopIterTable = loopIterTable + (node -> (cp_succ, callerCPSetOpt, false))
              //println("updating loop iter table : " + node)
              worklist.add(new_cpsucc)
            }
          })
        })
        
        // Propagation through loopOut edge 
        val losuccs = cfg.getLoopOutSucc.get(cp._1) match { 
          case Some(e) => e
          case None => Map()
        }
        losuccs.foreach(node => { // TODO: Multi-thread safety check
          val cmd = cfg.getCmd(cp._1) 
          val oldS = readTable(cp)
          val loopcond = sem.L(cp, cmd, oldS, inTable)
          val (newcontext, abstraction) = cp._2.newLoopContext(cp._1, false, loopcond, BoolBot, false, true)
          val new_cpsucc: ControlPoint = (node, newcontext)          
          val oldS2 = readTable(new_cpsucc)
          if(BoolFalse <= loopcond)
            if(!(outS <= oldS2)) {
              val prednodes = cfg.getAllPred(node)
              if(prednodes.size !=0 &&prednodes.forall(n => !cfg.getInfeasibility((n,cp._2)).isEmpty)){
              //if(cfg.getAllPred(node).size == 1 && !cfg.getInfeasibility(cp).isEmpty && cfg.getInfeasibility(cp).get){
                 cfg.updateInfeasibleNodeMap(new_cpsucc, true)
              }
              updateTable(new_cpsucc, outS)
              //loopIterTable = loopIterTable + (node -> (cp_succ, callerCPSetOpt, false))
              //println("updating loop iter table : " + node)
              worklist.add(new_cpsucc)
            }
        })

        val endTime = System.nanoTime()

        timeSum += endTime - startT
      }

      // Propagate exception output state (outES) along exception edges.
      // 1) If successor is catch, current exception value is assigned to catch variable and
      //    previous exception values are restored.
      // 2) If successor is finally, current exception value is propagated further along
      //    finally block's "normal" edges.
      val esucc = cfg.getExcSucc_Lock(cp._1)
      esucc match {
        case Some(node) =>
          val cp_succ = (node, cp._2)
          val oldES = readTable(cp_succ)
          if(!(outES <= oldES)) {
            val newES = oldES + outES
            updateTable(cp_succ, newES)
            worklist.add(cp_succ)
          }
        case None => ()
      }

      // Propagate along inter-procedural edges
      // This step must be performed after evaluating abstract transfer function
      // because 'call' instruction can add inter-procedural edges.
      sem.getIPSucc(cp) match {
        case None => ()
        case Some(succMap) =>
          succMap.foreach(kv => {
            // bypassing if IP edge is exception flow.
            val cp_succ = kv._1
            val oldS = readTable(cp_succ)
            val outS2 = sem.E(cp, cp_succ, kv._2._1, kv._2._2, outS)
            if(!(outS2 <= oldS)) {
              val predNodes = sem.getIPPred(cp_succ)
              if(!predNodes.isEmpty && predNodes.get.size != 0 && predNodes.get.forall(ccp => !cfg.getInfeasibility(ccp).isEmpty)){
                 cfg.updateInfeasibleNodeMap(cp_succ, true)
              }
              val newS = oldS + outS2

              updateTable(cp_succ, newS)
              worklist.add(cp_succ)
            }
          })
      }
    }
  }

  private def loop(): Unit = {
    val work = new FixpointWork
    while(!worklist.isEmpty) work.doit()
    //if(Config.loopSensitive){
    //  println()
    //  println("Total time for loop : " + timeSum/1000000000.0 + "(s)")
    //}
  }

  private def libraryLoop(): Unit = {
    System.out.println("\n* Library Mode *");
    // exit node of global function
    val globalNode = (cfg.getGlobalFId, LExit)
    // global call context
    val globalCC = CallContext.globalCallContext
    // state of global exit node
    val exitState = readTable((globalNode, globalCC))
    if (exitState <= StateBot) return
    val exitHeap = exitState._1
    // this value for library function
    val lset_this = LocSet(LibModeObjTopLoc)
    exitHeap.map.foreach((kv) => {
      val obj = kv._2
      obj("@class")._2._1._5.getSingle match {
        case Some(s) if s == "Function" =>
          obj("@function")._3.foreach((fid) => {
            if (cfg.isUserFunction(fid)) {
              val l_r = newRecentLoc()
              val ccset = globalCC.NewCallContext(HeapBot, cfg, fid, l_r, lset_this)
          ccset.foreach {case (cc_new, o_new) => {
              val o_arg = Obj.empty.
              update(NumStr, PropValue(ObjectValue(LibModeValueTop,BoolTrue,BoolTrue,BoolTrue))).
              update(OtherStr, PropValueBot).
              update("@class", PropValue(AbsString.alpha("Arguments"))).
              update("@proto", PropValue(ObjectValue(ObjProtoLoc, BoolFalse, BoolFalse, BoolFalse))).
              update("@extensible", PropValue(BoolTrue)).
              update("length", PropValue(ObjectValue(UInt, BoolTrue, BoolFalse, BoolTrue)))
              val l_arg = newRecentLoc()
              val v_arg = Value(l_arg)
              val value = PropValue(ObjectValue(v_arg, BoolTrue, BoolFalse, BoolFalse))
              val o_new2 = o_new.update(cfg.getArgumentsName(fid), value).
                update("@scope", obj("@scope"))

              val env_obj = Helper.NewDeclEnvRecord(o_new2("@scope")._2)

              val obj2 = o_new2 - "@scope"
              val h1 = exitState._1.update(l_arg, o_arg) // arguments object update
              val h2 = h1.remove(SinglePureLocalLoc)
              val h3 = h2.update(SinglePureLocalLoc, obj2)
              val h4 = obj2("@env")._2._2.foldLeft(HeapBot)((hh, l_env) => {
                hh + h3.update(l_env, env_obj) })
                // state set up
                updateTable(((fid, LEntry), cc_new), State(h4, ContextEmpty))
                // add to worklist
                worklist.add(((fid, LEntry), cc_new))
              }}
            }
          })
        case _ =>
      }
    })
  }
  
  private def readTable(cp: ControlPoint): State = inTable.synchronized {
    inTable.get(cp._1) match {
      case None => StateBot
      case Some(map) => map.get(cp._2) match {
        case None => StateBot
        case Some(state) => state
      }
    }
  }
  
  private def updateTable(cp: ControlPoint, state: State): Unit = inTable.synchronized {
    inTable.get(cp._1) match {
      case None =>
        inTable.update(cp._1, HashMap((cp._2, state)))
      case Some(map) =>
        inTable.update(cp._1, map.updated(cp._2, state))
    }
  }
  
  private def readLoopInTable(cp: ControlPoint): State = loopInTable.synchronized {
    loopInTable.get(cp._1) match {
      case None => StateBot
      case Some(map) => map.get(cp._2) match {
        case None => StateBot
        case Some(state) => state
      }
    }
  }
  
  private def updateLoopInTable(cp: ControlPoint, state: State): Unit = loopInTable.synchronized {
    loopInTable.get(cp._1) match {
      case None =>
        loopInTable.update(cp._1, HashMap((cp._2, state)))
      case Some(map) =>
        loopInTable.update(cp._1, map.updated(cp._2, state))
    }
  }

}
