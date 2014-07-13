/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.bug_detector

import scala.collection.mutable.SynchronizedMap
import scala.collection.mutable.{HashMap => MHashMap}
import scala.collection.mutable.{HashSet => MHashSet}
import scala.collection.mutable.{ListBuffer => MList}
import scala.collection.immutable.{HashMap => IHashMap}
import kr.ac.kaist.jsaf.analysis.cfg.{Node => CNode}
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.CallContext._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.nodes.{Node => ANode}
import kr.ac.kaist.jsaf.nodes.ASTNode
import kr.ac.kaist.jsaf.nodes_util.NodeFactory
import kr.ac.kaist.jsaf.nodes_util.NodeRelation
import kr.ac.kaist.jsaf.nodes_util.NodeUtil

class StateManager(cfg: CFG, typing: TypingInterface, semantics: Semantics, varManager: VarManager = null) {
/*
class StateManager(bugDetector: BugDetector) {
  ////////////////////////////////////////////////////////////////////////////////
  // From BugDetector
  ////////////////////////////////////////////////////////////////////////////////
  val cfg                                       = bugDetector.cfg
  val typing                                    = bugDetector.typing
  val semantics                                 = bugDetector.semantics
  val varManager                                = bugDetector.varManager
*/

  ////////////////////////////////////////////////////////////////////////////////
  // Types
  ////////////////////////////////////////////////////////////////////////////////
  type cacheKeyType =                           (CNode, InstId, SensitivityFlagType)
  type cacheType =                              MHashMap[cacheKeyType, CState]

  ////////////////////////////////////////////////////////////////////////////////
  // States
  ////////////////////////////////////////////////////////////////////////////////
  val inCache =                                 new cacheType // with SynchronizedMap[cacheKeyType, CState]
  val outCache =                                new cacheType // with SynchronizedMap[cacheKeyType, CState]
  val cstateBot =                               new IHashMap[CallContext, State]

  private def getCState(inOut: Int, node: CNode, instId: InstId, sensitivityFlag: SensitivityFlagType): CState = {
    // Select cache
    val cache = inOut match {
      case 0 => inCache
      case 1 => outCache
    }

    // This is a condition key value.
    val cacheKey: cacheKeyType = (node, instId, sensitivityFlag)

    // Try to get the cached CState.
    cache.get(cacheKey) match {
      case Some(cstate) => return cstate // cache hit
      case None =>
    }

    // Get original CState from Table.
    var result: CState = new IHashMap[CallContext, State]()
    typing.readTable(node) match {
      case Some(cstate) =>
        // Merge states depending on sensitivity flag.
        for((callContext, state) <- cstate) {
          val filteredCallContext: CallContext = callContext.filterSensitivity(sensitivityFlag)
          val mergedState: State = result.get(filteredCallContext) match {
            case Some(previousMergedState) => previousMergedState + state
            case None => state
          }
          result+= (filteredCallContext -> mergedState)
        }
      case None =>
    }

    // Insert into the cache.
    cache.put(cacheKey, result)

    // Return the result.
    result
  }
  def getInputCState(node: CNode, instId: InstId = -1, sensitivityFlag: SensitivityFlagType = _MOST_SENSITIVE): CState = getCState(0, node, instId, sensitivityFlag)
  def getOutputCState(node: CNode, instId: InstId = -1, sensitivityFlag: SensitivityFlagType = _MOST_SENSITIVE): CState = getCState(1, node, instId, sensitivityFlag)

  private def getCState(inOut: Int, anode: ANode, sensitivityFlag: SensitivityFlagType): CState = {
    // Check NodeRelation's initialization
    if(!NodeRelation.isSet) throw new RuntimeException("NodeRelation is not set.")

    // Get the first corresponding CFGInst
    val inst: CFGInst = NodeRelation.ast2cfgMap.get(anode) match {
      case Some(cfgList) =>
        var insts = new MList[CFGInst]
        for(cfg <- cfgList) if(cfg.isInstanceOf[CFGInst]) insts.append(cfg.asInstanceOf[CFGInst])
        if(insts.length == 0) return cstateBot
        insts = insts.sortBy(inst => inst.getInstId)
        inOut match {
          case 0 => insts.head
          case 1 => insts.last
        }
      case None => return cstateBot
    }

    getCState(inOut, cfg.findEnclosingNode(inst), inst.getInstId, sensitivityFlag)
  }
  def getInputCState(anode: ANode, sensitivityFlag: SensitivityFlagType): CState = getCState(0, anode, sensitivityFlag)
  def getOutputCState(anode: ANode, sensitivityFlag: SensitivityFlagType): CState = getCState(1, anode, sensitivityFlag)

  private def getState(inOut: Int, anode: ANode): State = {
    val cstate = inOut match {
      case 0 => getInputCState(anode, _INSENSITIVE)
      case 1 => getOutputCState(anode, _INSENSITIVE)
    }
    if(cstate.size > 1) throw new RuntimeException("ASSERT(cstate.size <= 1)")
    else if(cstate.size == 0) StateBot
    else cstate.head._2
  }
  def getInputState(anode: ANode): State = getState(0, anode)
  def getOutputState(anode: ANode): State = getState(1, anode)

  ////////////////////////////////////////////////////////////////////////////////
  // ControlPoint
  ////////////////////////////////////////////////////////////////////////////////
  // ControlPoint relation. For example, (caller context -> callee context)
  val controlPointSuccMap: MHashMap[ControlPoint, MHashSet[ControlPoint]] = MHashMap() // (pred -> Set[succ])
  val controlPointPredMap: MHashMap[ControlPoint, MHashSet[ControlPoint]] = MHashMap() // (succ -> Set[pred])
  def insertControlPointRelation(pred: ControlPoint, succ: ControlPoint): Unit = {
    // (pred -> Set[succ])
    val succSetOption = controlPointSuccMap.get(pred)
    val succSet = if(succSetOption.isDefined) succSetOption.get else {
      val newSuccSet = new MHashSet[ControlPoint]()
      controlPointSuccMap.put(pred, newSuccSet)
      newSuccSet
    }
    succSet.add(succ)

    // (succ -> Set[pred])
    val predSetOption = controlPointPredMap.get(succ)
    val predSet = if(predSetOption.isDefined) predSetOption.get else {
      val newPredSet = new MHashSet[ControlPoint]()
      controlPointPredMap.put(succ, newPredSet)
      newPredSet
    }
    predSet.add(pred)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Constructor
  ////////////////////////////////////////////////////////////////////////////////
  {
    // Initialize cache
    // For each node
    for(node <- cfg.getNodes) {
      typing.readTable(node) match {
        case Some(cstate) =>
          // Insert the input CState of the node into the cache
          var cacheKey: cacheKeyType = (node, -1, _MOST_SENSITIVE)
          inCache.put(cacheKey, cstate)

          // Init CState
          var normalCState: CState = cstate
          //var exceptionState: State = StateBot

          cfg.getCmd(node) match {
            case Block(insts) =>
              // Analyze each instruction
              for(inst <- insts) {
                // Insert the instruction input CState into the cache
                cacheKey = (node, inst.getInstId, _MOST_SENSITIVE)
                inCache.put(cacheKey, normalCState)

                val previousNormalCState = normalCState
                normalCState = new IHashMap[CallContext, State]
                //exceptionState = StateBot
                val normals = new MHashSet[CallContext]
                val bottoms = new MHashSet[CallContext]
                for((callContext, state) <- previousNormalCState) {
                  if(cfg.getInfeasibleNodeMap.get((node, callContext)).isEmpty) {
                  val (newNormalState, newExceptionState) = semantics.I((node, callContext), inst, state.heap, state.context, HeapBot, ContextBot)
                  if(newNormalState._1 != HeapBot) normalCState+= (callContext -> State(newNormalState._1, newNormalState._2))
                  //if(exceptionState._1 != HeapBot) exceptionState+= State(newExceptionState._1, newExceptionState._2)

                  if(!insts.head.isInstanceOf[CFGAssert] && !(state.heap <= HeapBot)) {
                    if(!(newNormalState._1 <= HeapBot)) normals.add(callContext)
                    else bottoms.add(callContext)
                  }

                  // Insert variable info
                  if(varManager != null) varManager.insertInfo(node, inst, state)
                  }
                }

                //if(normals.size == 0) {
                val span = inst.getInfo match {
                  case Some(info) => info.getSpan.toString
                  case None => "?"
                }
                //for(callContext <- bottoms) {
                //  println("- HeapBot(" + node + "," + callContext + ") / " + span + " : [" + inst.getInstId + "] " + inst)
                //}
                //}

                // Insert the instruction output CState into the cache
                outCache.put(cacheKey, normalCState)
              }
            case _ =>
          }

          // Insert the instruction output CState into the cache
          cacheKey = (node, -1, _MOST_SENSITIVE)
          outCache.put(cacheKey, normalCState)
        case None =>
      }
    }

    // Initialize ControlPoint relation (copy from semantic.ipSuccMap)
    for((predControlPoint, succControlPointMap) <- semantics.ipSuccMap) {
      for((succControlPoint, _) <- succControlPointMap) {
        insertControlPointRelation(predControlPoint, succControlPoint)
      }
    }

    // Debug
    /*println("*** AST's CStates ***")
    var indent = 0
    def printAST(ast: ANode): Unit = {
      val inState = getInputState(ast)
      val outState = getOutputState(ast)
      for(i <- 0 until indent) print(' ')
      print("AST" + ast.getClass.getSimpleName + '[' + NodeRelation.getUID(ast) + ']')
      print(" : inState = " + (if(inState.heap.map.size == 0) "Bot" else inState.heap.map.size))
      println(", outState = " + (if(outState.heap.map.size == 0) "Bot" else outState.heap.map.size))
      NodeRelation.ast2cfgMap.get(ast) match {
        case Some(cfgList) => for(cfg <- cfgList) println(cfg.getClass().getSimpleName() + ": " + NodeRelation.cfgToString(cfg))
        case None =>
      }
      NodeRelation.astChildMap.get(ast) match {
        case Some(children) => indent+= 2; for(child <- children) printAST(child); indent-= 2
        case None =>
      }
    }
    printAST(NodeRelation.astRoot)
    println*/
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Dump
  ////////////////////////////////////////////////////////////////////////////////
  def dump(node: Node, inst: CFGInst, cstate: CState): Unit = {
    println("CState count = " + cstate.size)
    for((callContext, state) <- cstate) dump(node, inst, callContext, state)
  }

  def dump(node: Node, inst: CFGInst, callContext: CallContext, state: State): Unit = {
    val nodeStr = node.toString()
    val sb = new StringBuilder

    for (i <- 0 to 60 - nodeStr.length) sb.append("=")
    println("=========================================================================")
    println("========  " + nodeStr + "  " + sb.toString)
    println("- Command")
    println("    [" + inst.getInstId + "] " + inst.toString)

    val callContextStr = "(cc:" + callContext.toString + ")"

    if(state == StateBot) {
      println("Bottom " + callContextStr)
    }
    else {
      print("- Context " + callContextStr + " = ")
      println(DomainPrinter.printContext(0, state._2))

      println("- Heap " + callContextStr)
      println(DomainPrinter.printHeap(4, state._1, cfg))
    }
    println("=========================================================================")
  }
}
