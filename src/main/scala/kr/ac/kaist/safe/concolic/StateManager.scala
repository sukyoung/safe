///**
// * *****************************************************************************
// * Copyright (c) 2016, KAIST.
// * All rights reserved.
// *
// * Use is subject to license terms.
// *
// * This distribution may include materials developed by third parties.
// * ****************************************************************************
// */
//
//package kr.ac.kaist.safe.concolic
//
//import scala.collection.immutable.{HashMap => IHashMap}
//import scala.collection.mutable.{HashMap => MHashMap, HashSet => MHashSet, ListBuffer => MList}
//import kr.ac.kaist.safe.analyzer._
//import kr.ac.kaist.safe.analyzer.domain._
//import kr.ac.kaist.safe.nodes.ast.ASTNode
//import kr.ac.kaist.safe.nodes.cfg._
//import kr.ac.kaist.safe.phase._
//import kr.ac.kaist.safe.util.{ NodeUtil => NU }
//
//
//class StateManager(cfg: CFG, semantics: Semantics, varManager: VarManager = null) {
///*
//class StateManager(bugDetector: BugDetector) {
//  ////////////////////////////////////////////////////////////////////////////////
//  // From BugDetector
//  ////////////////////////////////////////////////////////////////////////////////
//  val cfg                                       = bugDetector.cfg
//  val typing                                    = bugDetector.typing
//  val semantics                                 = bugDetector.semantics
//  val varManager                                = bugDetector.varManager
//*/
//
//  val typing = Analyze
//
//  ////////////////////////////////////////////////////////////////////////////////
//  // Types
//  ////////////////////////////////////////////////////////////////////////////////
//
//  type CState =                                 Map[TracePartition, AbsState]
//
//  // TODO MV simplified cacheKeyType, was originally: type cacheKeyType =  (CNode, InstId, SensitivityFlagType)
//  type cacheKeyType =                           (CFGBlock, InstId)
//  type cacheType =                              MHashMap[cacheKeyType, CState]
//
//  ////////////////////////////////////////////////////////////////////////////////
//  // States
//  ////////////////////////////////////////////////////////////////////////////////
//  val inCache =                                 new cacheType // with SynchronizedMap[cacheKeyType, CState]
//  val outCache =                                new cacheType // with SynchronizedMap[cacheKeyType, CState]
//  val cstateBot =                               new IHashMap[TracePartition, State]
//
//  private def getCState(inOut: Int, block: CFGBlock, instId: InstId): CState = {
//    // Select cache
//    val cache = inOut match {
//      case 0 => inCache
//      case 1 => outCache
//    }
//
//    // This is a condition key value.
//    val cacheKey: cacheKeyType = (block, instId)
//
//    // Try to get the cached CState.
//    cache.get(cacheKey) match {
//      case Some(cstate) => return cstate // cache hit
//      case None =>
//    }
//
//    // Get original CState from Table.
//    var result: CState = new IHashMap[TracePartition, AbsState]()
//    val cstate = semantics.getState(block)
//    // Merge states depending on sensitivity flag.
//    for((callContext, state) <- cstate) {
//      val mergedState: AbsState = result.get(callContext) match {
//        case Some(previousMergedState) => previousMergedState + state
//        case None => state
//      }
//      result += (callContext -> mergedState)
//    }
//
//    // Insert into the cache.
//    cache.put(cacheKey, result)
//
//    // Return the result.
//    result
//  }
//
//  def getInputCState(block: CFGBlock, instId: InstId = -1): CState = getCState(0, block, instId)
//  def getOutputCState(block: CFGBlock, instId: InstId = -1): CState = getCState(1, block, instId)
//
//
//  ////////////////////////////////////////////////////////////////////////////////
//  // ControlPoint
//  ////////////////////////////////////////////////////////////////////////////////
//  // ControlPoint relation. For example, (caller context -> callee context)
//  val controlPointSuccMap: MHashMap[ControlPoint, MHashSet[ControlPoint]] = MHashMap() // (pred -> Set[succ])
//  val controlPointPredMap: MHashMap[ControlPoint, MHashSet[ControlPoint]] = MHashMap() // (succ -> Set[pred])
//  def insertControlPointRelation(pred: ControlPoint, succ: ControlPoint): Unit = {
//    // (pred -> Set[succ])
//    val succSetOption = controlPointSuccMap.get(pred)
//    val succSet = if(succSetOption.isDefined) succSetOption.get else {
//      val newSuccSet = new MHashSet[ControlPoint]()
//      controlPointSuccMap.put(pred, newSuccSet)
//      newSuccSet
//    }
//    succSet.add(succ)
//
//    // (succ -> Set[pred])
//    val predSetOption = controlPointPredMap.get(succ)
//    val predSet = if(predSetOption.isDefined) predSetOption.get else {
//      val newPredSet = new MHashSet[ControlPoint]()
//      controlPointPredMap.put(succ, newPredSet)
//      newPredSet
//    }
//    predSet.add(pred)
//  }
//
//  ////////////////////////////////////////////////////////////////////////////////
//  // Constructor
//  ////////////////////////////////////////////////////////////////////////////////
//  {
//    // Initialize cache
//    // For each block
//    for(block <- cfg.getUserBlocks) {
//      val cstate = semantics.getState(block)
//      // Insert the input CState of the block into the cache
//      var cacheKey: cacheKeyType = (block, -1)
//      inCache.put(cacheKey, cstate)
//
//      // Init CState
//      var normalCState: CState = cstate
//      //var exceptionState: State = StateBot
//
//      for(inst <- block.getInsts) {
//        // Insert the instruction input CState into the cache
//        cacheKey = (block, inst.id)
//        inCache.put(cacheKey, normalCState)
//
//        val previousNormalCState = normalCState
//        normalCState = new IHashMap[TracePartition, AbsState]
//        //exceptionState = StateBot
//        val normals = new MHashSet[TracePartition]
//        val bottoms = new MHashSet[TracePartition]
//        for((callContext, state) <- previousNormalCState) {
//          if(cfg.getInfeasibleNodeMap.get((block, callContext)).isEmpty) {
//            val (newNormalState, newExceptionState) = semantics.I((block, callContext), inst, state.heap, state.context, HeapBot, ContextBot)
//            if(newNormalState._1 != HeapBot) normalCState+= (callContext -> State(newNormalState._1, newNormalState._2))
//            //if(exceptionState._1 != HeapBot) exceptionState+= State(newExceptionState._1, newExceptionState._2)
//
//            if(!insts.head.isInstanceOf[CFGAssert] && !(state.heap <= HeapBot)) {
//              if(!(newNormalState._1 <= HeapBot)) normals.add(callContext)
//              else bottoms.add(callContext)
//            }
//
//            // Insert variable info
//            if(varManager != null) varManager.insertInfo(block, inst, state)
//          }
//        }
//
//        //if(normals.size == 0) {
//        //for(callContext <- bottoms) {
//        //  println("- HeapBot(" + block + "," + callContext + ") / " + span + " : [" + inst.getInstId + "] " + inst)
//        //}
//        //}
//
//        // Insert the instruction output CState into the cache
//        outCache.put(cacheKey, normalCState)
//      }
//
//      // Insert the instruction output CState into the cache
//      cacheKey = (block, -1)
//      outCache.put(cacheKey, normalCState)
//    }
//
//    // Initialize ControlPoint relation (copy from semantic.ipSuccMap)
//    for((predControlPoint, succControlPointMap) <- semantics.ipSuccMap) {
//      for((succControlPoint, _) <- succControlPointMap) {
//        insertControlPointRelation(predControlPoint, succControlPoint)
//      }
//    }
//
//    // Debug
//    /*println("*** AST's CStates ***")
//    var indent = 0
//    def printAST(ast: ASTNode): Unit = {
//      val inState = getInputState(ast)
//      val outState = getOutputState(ast)
//      for(i <- 0 until indent) print(' ')
//      print("AST" + ast.getClass.getSimpleName + '[' + NodeRelation.getUID(ast) + ']')
//      print(" : inState = " + (if(inState.heap.map.size == 0) "Bot" else inState.heap.map.size))
//      println(", outState = " + (if(outState.heap.map.size == 0) "Bot" else outState.heap.map.size))
//      NodeRelation.ast2cfgMap.get(ast) match {
//        case Some(cfgList) => for(cfg <- cfgList) println(cfg.getClass().getSimpleName() + ": " + NodeRelation.cfgToString(cfg))
//        case None =>
//      }
//      NodeRelation.astChildMap.get(ast) match {
//        case Some(children) => indent+= 2; for(child <- children) printAST(child); indent-= 2
//        case None =>
//      }
//    }
//    printAST(NodeRelation.astRoot)
//    println*/
//  }
//}
