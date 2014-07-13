/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing

import java.io.File
import java.util.{List => JList}

import scala.collection.immutable.HashMap
import scala.collection.mutable.{HashMap => MHashMap}
import scala.util.parsing.json.JSONObject

import kr.ac.kaist.jsaf.analysis.cfg.FunctionId
import kr.ac.kaist.jsaf.analysis.cfg.Node
import kr.ac.kaist.jsaf.analysis.cfg.Block
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import kr.ac.kaist.jsaf.analysis.cfg.CFGCall
import kr.ac.kaist.jsaf.analysis.cfg.CFGConstruct
import kr.ac.kaist.jsaf.analysis.cfg.CFGInst
import kr.ac.kaist.jsaf.analysis.cfg.Entry
import kr.ac.kaist.jsaf.analysis.cfg.Exit
import kr.ac.kaist.jsaf.analysis.cfg.ExitExc
import kr.ac.kaist.jsaf.analysis.cfg.LEntry
import kr.ac.kaist.jsaf.analysis.cfg.LExit
import kr.ac.kaist.jsaf.analysis.cfg.LExitExc
import kr.ac.kaist.jsaf.analysis.cfg.InternalError
import kr.ac.kaist.jsaf.analysis.typing.domain.ContextBot
import kr.ac.kaist.jsaf.analysis.typing.domain.ContextEmpty
import kr.ac.kaist.jsaf.analysis.typing.domain.GlobalLoc
import kr.ac.kaist.jsaf.analysis.typing.domain.HeapBot
import kr.ac.kaist.jsaf.analysis.typing.domain.Loc
import kr.ac.kaist.jsaf.analysis.typing.domain.ObjBot
import kr.ac.kaist.jsaf.analysis.typing.domain.PropValueBot
import kr.ac.kaist.jsaf.analysis.typing.domain.Recent
import kr.ac.kaist.jsaf.analysis.typing.domain.SinglePureLocalLoc
import kr.ac.kaist.jsaf.analysis.typing.domain.StateBot
import kr.ac.kaist.jsaf.analysis.typing.domain.ValueBot
import kr.ac.kaist.jsaf.analysis.typing.domain.AbsString
import kr.ac.kaist.jsaf.analysis.typing.domain.Absent
import kr.ac.kaist.jsaf.analysis.typing.domain.BoolTrue
import kr.ac.kaist.jsaf.analysis.typing.domain.DomainPrinter
import kr.ac.kaist.jsaf.analysis.typing.domain.State
import kr.ac.kaist.jsaf.analysis.typing.domain.Value
import kr.ac.kaist.jsaf.analysis.typing.models.ModelManager
import kr.ac.kaist.jsaf.analysis.typing.{SemanticsExpr => SE}
import kr.ac.kaist.jsaf.bug_detector.BugInfo
import kr.ac.kaist.jsaf.nodes_util.Span
import kr.ac.kaist.jsaf.nodes_util.NodeUtil
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

class DSparseTyping(_cfg: CFG, quiet: Boolean, locclone: Boolean) extends TypingInterface {
  val _env = new DSparseEnv(_cfg)

  override def env: Environment = _env
  def cfg = _cfg
  var programNodes = _cfg.getNodes // without built-ins
  val inTable: Table = MHashMap()
  var fset_builtin: Map[FunctionId, String] = Map()
  var state = StateBot
  override def getMergedState = state

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

  def checkTable(dense: Table): Unit = {
    System.out.println("== Check Table ==")
    // soundness check
    inTable.foreach((nc) => {
      val node = nc._1
      val cstate = nc._2
      val dense_cstate =
        dense.get(node) match {
          case Some(s) => s
          case None => Map[CallContext, State]()
        }
      cstate.foreach((cs) => {
        val callcontext = cs._1
        val state = cs._2
        val dense_state =
          dense_cstate.get(callcontext) match {
            case Some(s) => s
            case None => StateBot
          }
        val heap_sparse = state._1
        val heap_dense = dense_state._1

        if (heap_sparse == HeapBot && heap_dense != HeapBot && (!env.isEmptyNode(node))) {
          System.out.println("* Warning: sparse result is bottom at "+node)
        }

        val lset = heap_sparse.map.keySet
        lset.foreach((l) => {
          val o1 = heap_sparse(l)
          heap_dense.map.get(l) match {
            case Some(o2) => {
              val props = o1.map.keySet ++ o2.map.keySet
              props.foreach((prop) => {
                val pv_1 = o1(prop)
                val pv_2 = o2(prop)
                if (pv_2._1 </ pv_1._1 && pv_2._2 </ pv_1._2) {
                  System.out.println("at ("+node+", "+callcontext+"), more precise result for ("+l+","+prop+")")
                  System.out.println("sparse: "+ DomainPrinter.printObj(0, o1.restrict_(Set(prop))))
                  System.out.println("dense : "+ DomainPrinter.printObj(0, o2.restrict_(Set(prop))))
                } else if (pv_1._1 </ pv_2._1 && pv_1._2 </ pv_2._2) {
                  System.out.println("at ("+node+", "+callcontext+"), more imprecise result for ("+l+","+prop+")")
                  System.out.println("sparse: "+ DomainPrinter.printObj(0, o1.restrict_(Set(prop))))
                  System.out.println("dense : "+ DomainPrinter.printObj(0, o2.restrict_(Set(prop))))
                } else if (pv_1._1 <= pv_2._1 && pv_1._2 <= pv_2._2) {
                  // same
                } else {
                  System.out.println("at ("+node+", "+callcontext+"), different result for ("+l+","+prop+")")
                  System.out.println("sparse: "+ DomainPrinter.printObj(0, o1.restrict_(Set(prop))))
                  System.out.println("dense : "+ DomainPrinter.printObj(0, o2.restrict_(Set(prop))))
                }
              })
            }
            case None => {
              System.out.println("at ("+node+", "+callcontext+"), location "+l+" is missing in dense result.")
            }
          }
        })
      })
    })


    try {
      val cs = inTable((0, LExit))
      val exit_state = cs(CallContext.globalCallContext)
      val exit_heap = exit_state._1
      if (exit_heap == HeapBot) {
        System.out.println("* Exit heap is bottom!")
      } else {
        val exit_heap_d = exit_state._1

        val g = exit_heap(GlobalLoc)
        val g_d = exit_heap_d(GlobalLoc)
        val props = g.map.keySet
        (0 to 20).foreach((i) => {
          val result = "__result"+i
          if (props.contains(result)) {
            val v_result = g(result)
            val v_result_d = g_d(result)
            if (v_result != v_result_d) {
              System.out.println("different result for "+result)
            }
          }
        })
      }
    } catch {
      case _: Throwable => System.out.println("* Exit heap is bottom!")
    }
  }

  var numIter = 0
  var elapsedTime = 0.0d
  private var sem: Option[Semantics] = None
  def getSem: Semantics = sem match {
    case None => throw new InternalError("Do the analysis first.")
    case Some(s) => s
  }

  // main entry point
  override def analyze(init: InitHeap, duset: DUSet): Unit = {
    val initHeap = init.getInitHeap()
    val initContext = ContextEmpty
    val initState = State(initHeap, initContext)

    fset_builtin = ModelManager.getFIdMap()

    // Initialize call context for context-sensitivity
    CallContext.initialize


    inTable.update((cfg.getGlobalFId, LEntry),
                   HashMap((CallContext.globalCallContext, initState)))

    val worklist = Worklist.computesSparse(_env.getInterDDG, quiet)
    val s = System.nanoTime

    val fixpoint = new DSparseFixpoint(cfg, _env, worklist, inTable, quiet, locclone)

    fixpoint.compute(duset)
    sem = Some(fixpoint.getSemantics)

    numIter = fixpoint.count
    if (!quiet)
      System.out.println("# Fixpoint iteration(#): "+numIter)
    elapsedTime = (System.nanoTime - s) / 1000000000.0
    if (!quiet)
      System.out.format("# Time for analysis(s): %.2f\n", new java.lang.Double(elapsedTime))

    if (!quiet)
      System.out.println("# In/Out Heaps")
    inTable.foreach((nc) => {
      val node = nc._1
      val cstate = nc._2
      cstate.foreach((cs) => {
        val callcontext = cs._1
        val state = cs._2
        val cp = (node, callcontext)
        // System.out.println(cp)
        val cmd = cfg.getCmd(cp._1)
        val (outS, outES) = getSem.C(cp, cmd, state)
        // System.out.println("== In Heap ==")
        // System.out.println(DomainPrinter.printHeap(4, state._1, true))
        // System.out.println("== Out Heap ==")
        // System.out.println(DomainPrinter.printHeap(4, outS._1, true))
        if (state != StateBot && (outS._1 == HeapBot && outES._1 == HeapBot) && !quiet) {
          System.out.println("* Warning: result state is bottom at "+cp)
          System.out.println(cmd)
        }
      })
    })
    if (!quiet) {
      System.out.println("== Error functions ==")
      val functions = cfg.getFunctionIds
      functions.foreach(fid => {
        (inTable.get((fid, LEntry)), inTable.get((fid, LExit)), inTable.get((fid, LExitExc))) match {
          case (Some(cs), Some(cs_exit), Some(cs_exitexc)) => {
            cs.keySet.foreach(cc => {
              (cs.get(cc), cs_exit.get(cc), cs_exitexc.get(cc)) match {
                case (Some(ss), Some(s_exit), Some(s_exitexc)) => {
                  if (ss._1 != HeapBot) {
                    if (s_exit._1 == HeapBot && s_exitexc._1 == HeapBot) {
                      System.out.println("* Warning: Return state of function "+fid+" is bottom.")
                    }
                  }
                }
                case (Some(ss), None, None) => {
                  if (ss._1 != HeapBot) {
                    System.out.println("* Warning: Return state of function "+fid+" is bottom.")
                  }
                }
                case _ => ()
              }
            })
          }
          case (Some(cs), Some(cs_exit), None) => {
            cs.keySet.foreach(cc => {
              (cs.get(cc), cs_exit.get(cc)) match {
                case (Some(ss), Some(s_exit)) => {
                  if (ss._1 != HeapBot) {
                    if (s_exit._1 == HeapBot) {
                      System.out.println("* Warning: Return state of function "+fid+" is bottom.")
                    }
                  }
                }
                case (Some(ss), None) => {
                  if (ss._1 != HeapBot) {
                    System.out.println("* Warning: Return state of function "+fid+" is bottom.")
                  }
                }
                case _ => ()
              }
            })
          }
          case (Some(cs), None, Some(cs_exitexc)) => {
            cs.keySet.foreach(cc => {
              (cs.get(cc), cs_exitexc.get(cc)) match {
                case (Some(ss), Some(s_exitexc)) => {
                  if (ss._1 != HeapBot) {
                    if (s_exitexc._1 == HeapBot) {
                      System.out.println("* Warning: Return state of function "+fid+" is bottom.")
                    }
                  }
                }
                case (Some(ss), None) => {
                  if (ss._1 != HeapBot) {
                    System.out.println("* Warning: Return state of function "+fid+" is bottom.")
                  }
                }
                case _ => ()
              }
            })
          }
          case (Some(cs), None, None) => {
            System.out.println("* Warning: Return state of function "+fid+" is bottom.")
          }
          case (_, _, _) => ()
        }
      })
      // cfg.dump_dugraph()
    }
  }

  ///////////////
  // Query API //
  ///////////////

  // Low-level interface ------------------------------------------------------

  override def builtinFset() = fset_builtin

  override def getTable: Table = inTable

  /**
   * Reads the analysis result for the given control point.
   * The control point consists of node and call context.
   * This method returns the state before executing the given node in the given call context.
   * If the control point was not analyzed (i.e. dead code) bottom state is returned.
   *
   * @param cp the control point to get state for
   * @return the state
   */
  override def readTable(cp: ControlPoint): State = {
    inTable.get(cp._1) match {
      case None => StateBot
      case Some(map) => map.get(cp._2) match {
        case None => StateBot
        case Some(s) => s
      }
    }
  }
  
  /**
   * Reads the analysis result for the given node.
   * This method returns the context state(CState) before executing the given node. 
   * If the node was not analyzed (i.e. dead code) None is returned.
   * 
   * @param n the node to get state for
   * @return None or wrapped CState
   */
  override def readTable(n: Node): Option[CState] = {
    inTable.get(n)
  }

  // State getters ------------------------------------------------------------

  /**
   * Returns the state before program execution starts.
   * Note that single state is returned because global code does not have multiple contexts.
   *
   * @return the single dense state
   */
  override def getStateBeforeProgram: State = {
    /* before state of global entry */
    mergeState(getStateBeforeNode((cfg.getGlobalFId,LEntry)))
  }

  /**
   * Returns the state after executing all global code.
   * Bottom state is returned if definite uncaught exception occurred during the execution.
   * Note that single state is returned because global code does not have multiple contexts.
   *
   * @return the single dense state
   */
  override def getStateAfterProgram: State = {
    /* before state of global exit */
    mergeState(getStateBeforeNode((cfg.getGlobalFId,LExit)))
  }

  /**
   * Returns the uncaught exception state after executing all global code.
   * Bottom state is returned if there was no uncaught exception.
   * Note that single state is returned because global code does not have multiple contexts.
   *
   * @return the single dense state
   */
  override def getExcStateAfterProgram: State = {
    /* before state of global exc exit */
    mergeState(getStateBeforeNode((cfg.getGlobalFId,LExitExc)))
  }

  /**
   * Returns the state after executing all code in the given file.
   * Bottom state is returned if definite uncaught exception occurred during the execution.
   * Note that single state is returned because global code does not have multiple contexts.
   *
   * @param file the filename to get state for
   * @return the single dense state
   */
  override def getStateAfterFile(file: String): State = {
    val f = new File(file)
    mergeState(getStateAfterInst(cfg.getNoOp(f.getCanonicalPath)))
  }

  /**
   * Returns the entry context-sensitive states in which the given function executed.
   * States for different calling contexts are returned as a map.
   * The entry node performs formal argument binding and local variable creation and
   * this method returns the states after executing the entry node.
   * Note that only non-bottom states are included in the map, which means empty map is returned
   * if the function was not executed at all.
   *
   * @param fid the function id to get state for
   * @return the map from call contexts to dense states
   */
  override def getStateAtFunctionEntry(fid: FunctionId): CState = {
    getStateAfterNode((fid, LEntry))
  }

  /**
   * Returns the context-sensitive states after executing the given function.
   * States for different calling contexts are returned as a map.
   * The execution context (environment, this) corresponds to the given function, not caller.
   * Note that only non-bottom states are included in the map, which means empty map is returned
   * if the function was not executed at all or had definite exception for all contexts.
   *
   * @param fid the function id to get state for
   * @return the map from call contexts to dense states
   */
  override def getStateAtFunctionExit(fid: FunctionId): CState = {
    getStateBeforeNode((fid, LExit))
  }

  /**
   * Returns the states representing uncaught exceptions caused by executing the given function.
   * States for different calling contexts are returned as a map.
   * The execution context (environment, this) corresponds to the given function, not caller.
   * Note that only non-bottom states are included in the map, which means empty map is returned
   * if the function was dead or did not have any uncaught exception for all contexts.
   *
   * @param fid the function id to get exception state for
   * @return the map from each call context to the dense state
   */
  override def getExcStateAtFunctionExit(fid: FunctionId): CState = {
    getStateBeforeNode((fid, LExitExc))
  }

  /**
   * Returns the context-sensitive states before executing the given CFG node.
   * States for different calling contexts are returned as a map.
   * Note that only non-bottom states are included in the map, which means empty map is returned
   * if the node was dead code for all contexts.
   *
   * @param node the node to get state for
   * @return the map from call contexts to dense states
   */
  override def getStateBeforeNode(node: Node): CState = {
    inTable.get(node) match {
      case None => Map()
      case Some(map) => map
    }
  }

  /**
   * Returns the context-sensitive states after executing the given CFG node.
   * States for different calling contexts are returned as a map.
   * Note that only non-bottom states are included in the map, which means empty map is returned
   * if the node was dead or had definite exception for all contexts.
   * For call and construction instructions, CFG semantics does not include
   * the execution of callee functions.
   * So, this method returns the states just before entering the callee functions
   * if the given node ends with such instructions.
   *
   * @param node the node to get state for
   * @return the map from each call context to the dense state
   */
  override def getStateAfterNode(node: Node): CState = {
    val cstate = getStateBeforeNode(node)
    cstate.foldLeft[CState](Map())(
      (cs, kv) => {
        val (s, _) = getSem.C((node,kv._1), cfg.getCmd(node), kv._2)
        if (s._1 <= HeapBot)
          cs
        else
          cs + (kv._1 -> s)
      })
  }

  /**
   * Returns the context-sensitive states after executing the given file name and line number.
   * States for different calling contexts are returned as a map.
   * Note that only non-bottom states are included in the map, which means empty map is returned
   * if given line was dead code for all contexts.
   *
   * @param file file name to get state
   * @param line line number to get state
   * @return the map from each call context to the dense state
   */
  override def getStateAfterLine(file: String, line: Int): CState = {
    val f = new File(file)
    val file_full = f.getCanonicalPath
    val insts = cfg.getNodes.foldLeft[List[CFGInst]](List())((list, node) =>
      cfg.getCmd(node) match { case Block(is) => list ++ is
                               case _ => list })
    val sortedInsts = insts.sortWith((i_1, i_2) =>
      (i_1.getInfo, i_2.getInfo ) match {
        case (Some(info1), Some(info2)) =>
           info1.getSpan.getEnd.column > info2.getSpan.getEnd.column
        case (Some(_), None) => false
        case (None, Some(_)) => true
        case (None, None) => true })
    val lastInst = sortedInsts.find((i) =>
      i.getInfo match {
        case Some(info) =>
          val end = info.getSpan.getEnd
          (end.getFileName == file_full) && (end.getLine <= line)
        case None => false })
    lastInst match {
      case Some(i) => getStateAfterInst(i)
      case _ => Map()
    }
  }

  /**
   * Returns the context-sensitive states before executing the given instruction.
   * States for different calling contexts are returned as a map.
   * Note that only non-bottom states are included in the map, which means empty map is returned
   * if the instruction was dead code for all contexts.
   *
   * @param inst the instruction to get state for
   * @return the map from each call context to the dense state
   */
  override def getStateBeforeInst(inst: CFGInst): CState = {
    val node = cfg.findEnclosingNode(inst)
    cfg.getCmd(node) match {
      case Block(insts) =>
        val is = insts.take(insts.indexOf(inst))
        is.foldLeft[CState](getStateBeforeNode(node))((cs, i) =>
          cs.foldLeft[CState](Map())((_cs, kv) => {
            val (s,_) = getSem.I((node, kv._1), i, kv._2._1, kv._2._2, HeapBot, ContextBot)
            if (s._1 <= HeapBot)
              _cs
            else
              _cs + (kv._1 -> State(s._1, s._2))
          }))
      case _ => throw new InternalError("Only Block node can have instructions")
    }
  }

  /**
   * Returns the context-sensitive states after executing the given instruction.
   * States for different calling contexts are returned as a map.
   * Note that only non-bottom states are included in the map, which means empty map is returned
   * if the instruction was dead or had definite exception for all contexts.
   * For call and construction instructions, CFG semantics does not include
   * the execution of callee functions.
   * So, this method returns the states just before entering the callee functions.
   *
   * @param inst the instruction to get state for
   * @return the map from each call context to the dense state
   */
  override def getStateAfterInst(inst: CFGInst): CState = {
    val node = cfg.findEnclosingNode(inst)
    cfg.getCmd(node) match {
      case Block(insts) =>
        val is = insts.take(insts.indexOf(inst)+1)
        is.foldLeft[CState](getStateBeforeNode(node))((cs, i) =>
          cs.foldLeft[CState](Map())((_cs, kv) => {
            val (s,_) = getSem.I((node, kv._1), i, kv._2._1, kv._2._2, HeapBot, ContextBot)
            if (s._1 <= HeapBot)
              _cs
            else
              _cs + (kv._1 -> State(s._1, s._2))
          }))
      case _ => throw new InternalError("Only Block node can have instructions")
    }
  }

  /**
   * Returns the states representing exceptions caused by executing the given instruction.
   * States for different calling contexts are returned as a map.
   * Note that only non-bottom states are included in the map, which means empty map is returned
   * if the instruction was dead or did not cause any exception for all contexts.
   * For call and construction instructions, CFG semantics does not include
   * the execution of callee functions.
   * So, the returned states do not include the uncaught exceptions of callee functions.
   *
   * @param inst the instruction to get exception state for
   * @return the map from each call context to the dense state
   */
  override def getExcStateAfterInst(inst: CFGInst): CState = {
    val node = cfg.findEnclosingNode(inst)
    cfg.getCmd(node) match {
      case Block(insts) =>
        val cs = getStateBeforeInst(inst)
        cs.foldLeft[CState](Map())((_cs, kv) => {
          val (_, es) = getSem.I((node, kv._1), inst, kv._2._1, kv._2._2, HeapBot, ContextBot)
          if (es._1 <= HeapBot)
            _cs
          else
            _cs + (kv._1 -> State(es._1, es._2))
        })
      case _ => throw new InternalError("Only Block node can have instructions")
    }
  }

  /**
   * Merges the given context-sensitive states to single state.
   *
   * @param the map from each call context to the dense state
   * @return the single dense state
   */
  override def mergeState(cstate: CState): State = {
    cstate.foldLeft(StateBot)((s, kv)=> s + kv._2)
  }

  /**
   * Chooses the state corresponding to the given call context.
   * If the given context-sensitive states do not have state for the given context,
   * bottom state is returned.
   *
   * @param the map from each call context to the dense state
   * @return the single dense state
   */
  override def chooseState(cstate: CState, cc: CallContext): State = {
    cstate.get(cc) match {
      case None => StateBot
      case Some(s) => s
    }
  }

  // Value reading methods ----------------------------------------------------

  /**
   * Reads the variable in the given context-sensitive states.
   * Values from different calling contexts are merged.
   * If the states are bottom or the variable is undefined global, bottom value is returned.
   * Warning: the behavior is unspecified if the id is not valid in the given states.
   *
   * @param cstate the input context-sensitive states
   * @param id the variable
   * @return the merged value
   */
  // def readVariable(cstate: CState, id: CFGId): Value = {
  //   cstate.foldLeft(ValueBot)((v, kv) => v +
  //     (if (kv._2._1 <= HeapBot)
  //       ValueBot
  //     else
  //       Helper.Lookup(kv._2._1, id)._1))
  // }

  /**
   * Reads the variable in the given state.
   * If the state is bottom or the variable is undefined global, bottom value is returned.
   * Warning: the behavior is unspecified if the id is not valid in the given states.
   *
   * @param state the input state
   * @param id the variable
   * @return the value
   */
  // def readVariable(state: State, id: CFGId): Value = {
  //   if (state._1 <= HeapBot)
  //     ValueBot
  //   else
  //     Helper.Lookup(state._1, id)._1
  // }

  /**
   * Reads the property of the objects in the given context-sensitive states.
   * Values from different calling contexts are merged.
   * If the states are bottom, bottom value is returned.
   * If the property is possibly absent, undefined value is included in the result.
   * Note that properties from prototype chain are considered in the result.
   * Warning: the behavior is unspecified if the given locations are not valid objects
   * in the given states.
   *
   * @param cstate the input context-sensitive states
   * @param lset the set of locations holding the objects
   * @param s the property name in abstract string form
   * @return the merged value
   */
  def readProperty(cstate: CState, lset: Set[Loc], s: AbsString): Value = {
    cstate.foldLeft(ValueBot)((v, kv) =>
      v + readProperty(kv._2, lset, s))
  }

  /**
   * Reads the property of the objects in the given state.
   * If the state is bottom, bottom value is returned.
   * If the property is possibly absent, undefined value is included in the result.
   * Note that properties from prototype chain are considered in the result.
   * Warning: the behavior is unspecified if the given locations are not valid objects
   * in the given state.
   *
   * @param state the input state
   * @param lset the set of locations holding the objects
   * @param s the property name in abstract string form
   * @return the value
   */
  def readProperty(state: State, lset: Set[Loc], s: AbsString): Value = {
    if (state._1 <= HeapBot)
      ValueBot
    else
      lset.foldLeft(ValueBot)((v, l) =>
        v + Helper.Proto(state._1, l, s))
  }

  /**
   * Reads the exception value in the given context-sensitive states.
   * Values from different calling contexts are merged.
   * If the states are bottom, i.e. no exception occurred, bottom value is returned.
   *
   * @param cstate the input context-sensitive states
   * @return the merged exception value
   */
  def readException(cstate: CState): Value = {
    cstate.foldLeft(ValueBot)((v, kv) =>
      v + readException(kv._2))
  }

  /**
   * Reads the exception value in the given state.
   * If the state is bottom, i.e. no exception occurred, bottom value is returned.
   *
   * @param state the input state
   * @return the exception value
   */
  def readException(state: State): Value = {
    if (state._1 <= HeapBot)
      ValueBot
    else
      state._1(SinglePureLocalLoc)("@exception")._1._2
  }

  // High-level information computation methods -------------------------------

  /**
   * Computes the property list of the objects in the given state.
   * The returned information is a tuple option consisting of the following three values.
   * 1) List of concrete user-defined property name and absent tag pairs.
   * 2) Flag indicating whether some properties were merged into @default_number.
   * 3) Flag indicating whether some properties were merged into @default_other.
   * Objects from different calling contexts and locations are merged, which means
   * that properties only present in some of those objects will be tagged as absent.
   * If the state is bottom, None is returned.
   * Warning: the behavior is unspecified if the given locations are not valid objects
   * in the given state.
   *
   * @param state the input state
   * @param lset the set of locations holding the objects
   * @return the option of the computed list
   */
  def computePropertyList(state: State, lset: Set[Loc]):
      Option[(List[(String, Absent)], Boolean, Boolean)] = {
    if (state <= StateBot)
      None
    else {
      val h = state._1
      val obj =
        if (lset.size == 0)
          ObjBot
        else if (lset.size == 1)
          h(lset.head)
        else
          lset.tail.foldLeft(h(lset.head))((o, l) => o + h(l))
      val props = obj.getProps.foldLeft[List[(String, Absent)]](List())(
        (list, p) => (p, obj(p)._2)::list)
      System.out.println(obj)
      Some((props,
        !(obj("@default_number")._1 <= PropValueBot),
        !(obj("@default_other")._1 <= PropValueBot)))
    }
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
   * @return the map from caller to set of callees.
   */
  override def computeCallGraph(): Map[CFGInst, Set[FunctionId]] = {
    cfg.getNodes.foldLeft[Map[CFGInst, Set[FunctionId]]](Map())(
      (map, n) =>
        cfg.getCmd(n) match {
          case Entry | Exit | ExitExc => map
          case Block(insts) =>
            if (insts.isEmpty) map
            else {
              val i = insts.last
              i match {
                case CFGCall(_, _, fun, _, _, _, _) =>
                  val cstate = getStateBeforeInst(i)
                  cstate.foldLeft(map)(
                    (m, kv) => {
                      val h = kv._2._1
                      val ctx = kv._2._2
                      val lset = SE.V(fun, h, ctx)._1._2
                      lset.foldLeft(m)(
                        (_m, l) =>
                          if (BoolTrue <= Helper.IsCallable(h,l)) {
                            _m.get(i) match {
                              case None => _m + (i -> h(l)("@function")._1._3.toSet)
                              case Some(set) => _m + (i -> (set ++ h(l)("@function")._1._3.toSet))
                            }
                          }
                          else
                            _m)})
                case CFGConstruct(_, _, cons, _, _, _, _) =>
                  val cstate = getStateBeforeInst(i)
                  cstate.foldLeft(map)(
                    (m, kv) => {
                      val h = kv._2._1
                      val ctx = kv._2._2
                      val lset = SE.V(cons, h, ctx)._1._2
                      lset.foldLeft(m)(
                        (_m, l) =>
                          if (BoolTrue <= Helper.HasConstruct(h,l)) {
                            _m.get(i) match {
                              case None => _m + (i -> h(l)("@construct")._1._3.toSet)
                              case Some(set) => _m + (i -> (set ++ h(l)("@construct")._1._3.toSet))
                            }
                          }
                          else
                            _m)})
                case _ => map
              }
            }
        })
  }

  /**
   * Computes the prototype hierarchy from the given state.
   * For each object in the state, the returned map records the set of direct prototypes
   * (not all objects in the prototype chain).
   * Warning: There can be prototype chain cycle because of analysis imprecision.
   *
   * @param state the state in which the hierarchy is to be computed
   * @param builtin the flag whether result includes built-in objects
   * @return the map from each location to set of prototypes
   */
  override def computePrototypeHierarchy(state: State): Map[Loc, Set[Loc]] = {
    state._1.map.foldLeft[Map[Loc, Set[Loc]]](Map())(
      (map, kv) =>
        if (kv._2.map.contains("@proto"))
          map + (kv._1 -> kv._2("@proto")._1._1._1._2.toSet)
        else
          map)

  }

  /**
   * Retrieve function name of given location from input state.
   * If given location dose not point function object, returns empty set
   *
   * @param state input state
   * @param loc input location
   * @return set of function name
   */
  override def getFuncNameByLoc(state:State, loc: Loc): Set[String] = {
    val h = state._1
    h(loc)("@class")._1._2._1._5.getSingle match {
      case Some(str) if str =="Function" =>
        if (h(loc).map.contains("@function")) {
        val fset = h(loc)("@function")._1._3
        fset.toSet.map((fid) => cfg.getFuncName(fid))
      }
      else
        Set()
      case _ => Set()
    }
  }
  /**
   * Integrate old location and recent location
   * @param s input state
   * @return state
   */
  override def integrateRecentState(s:State) : State = {
    var h = HeapBot
    s._1.map.foreach(kv => {
      val (loc, obj) = kv
      // built-in
      if(locToAddr(loc) < 0) h = h.update(loc, obj)
      // user defined
      else if(h.domIn(addrToLoc(locToAddr(loc), Recent))) {
          h = h.update(addrToLoc(locToAddr(loc), Recent), obj + h(addrToLoc(locToAddr(loc), Recent)))
      } else {
        h = h.update(addrToLoc(locToAddr(loc), Recent), obj)
      }
    })
    // substitute old locations to recent locations
    s._1.map.foreach(kv => {
      val (loc, _) = kv
      if(locToAddr(loc) >= 0 && isOldLoc(loc)) h = h.subsLoc(loc, addrToLoc(locToAddr(loc), Recent))
    })
    State(h, s._2)
  }

  ////////////////
  // Statistics //
  ////////////////

  override def statistics(statdump: Boolean): Unit = {
    val stat = new Statistics(cfg, fset_builtin, inTable, locclone)
    stat.calculate()
    if (statdump)
      stat.printDump()
    stat.printTable()
  }

  override def dump() {
    for (node <- cfg.getNodes) {
      val nodeStr = node.toString()
      val sb = new StringBuilder

      inTable.get(node) match {
        case None =>
          if (!(fset_builtin.contains(node._1))) {
            for (i <- 0 to 60 - nodeStr.length) sb.append("=")
            System.out.println("========  " + nodeStr + "  " + sb.toString)
            cfg.getCmd(node) match {
              case Block(insts) =>
                System.out.println("- Command")
              for (inst <- insts) {
                System.out.println("    [" + inst.getInstId + "] " + inst.toString)
              }
              System.out.println()
              case _ => ()
            }
            System.out.println("- Bottom (cc:ALL)")
            System.out.println("=========================================================================")
            System.out.println()
          }
        case Some(map) =>
          for (i <- 0 to 60 - nodeStr.length) sb.append("=")
          System.out.println("========  " + nodeStr + "  " + sb.toString)

          cfg.getCmd(node) match {
            case Block(insts) =>
              System.out.println("- Command")
            for (inst <- insts) {
              System.out.println("    [" + inst.getInstId + "] " + inst.toString)
            }
            System.out.println()
            case _ => ()
          }

          var first = true
          var prevBottom = true

          map.foreach(kv => {
            val cc = kv._1
            val ccStr = "(cc:" + cc.toString + ")"
            val state = kv._2

            if (state == StateBot) {
              if (!prevBottom) System.out.println()
              System.out.println("Bottom " + ccStr)
              prevBottom = true
            } else {
              if (!first) System.out.println()
              System.out.print("- Context " + ccStr + " = ")
              System.out.println(DomainPrinter.printContext(0, state._2))

              System.out.println("- Heap " + ccStr)
              System.out.println(DomainPrinter.printHeap(4, state._1, cfg))
/*
              val out_s = sem.get.C((node,cc), cfg.getCmd(node), state)

              System.out.println("- Out Normal Heap " + ccStr)
              System.out.println(DomainPrinter.printHeap(4, out_s._1._1, cfg))
              if(out_s._2._1 != HeapBot) {
                System.out.println("- Out Exc Heap " + ccStr)
                System.out.println(DomainPrinter.printHeap(4, out_s._2._1, cfg))
              }
*/
              first = false
              prevBottom = false
            }
          })
          System.out.println("=========================================================================")
          System.out.println()
      }
    }
  }

  def dumpVisual():String = {
    var textMap:Map[String, String] = new HashMap()
//
//    for (key <- cfg.getNodes) {
//      val keyStr = key._2 match {
//        case LBlock(id) =>  "Block"+id
//        case LExit =>  "Exit"+key._1
//        case LEntry =>  "Entry"+key._1
//        case LExitExc =>  "ExitExc"+key._1
//      }
//      val sb = new StringBuilder
//
//      val state = inTable(key)._1
//      val excState = inTable(key)._2
//
//      // if the function is built-in function and its state is bottom, skip printing it.
//      if (!(fset_builtin.contains(key._1) && state == StateBot && excState == StateBot)) {
//
//        cfg.getCmd(key) match {
//          case Block(insts) =>
//            sb.append("<h5><a herf=\"#\">Command</a></h3>")
//            sb.append("<div><ul>")
//            for (inst <- insts) {
//            	sb.append("<li>" + inst.toString + "</li>")
//            }
//            sb.append("</ul></div>")
//          case _ => ()
//        }
//
//        // normal state
//        var first = true;
//        if (state != StateBot) {
//          sb.append("<h5><a herf=\"#\">Context</a></h3>")
//          sb.append("<div><ul>")
//          sb.append("<li>"+DomainPrinter.printContext(4, state._2).replace("\n","</li><li>")+"</li>")
//          sb.append("</ul></div>")
//
//          sb.append("<h5><a herf=\"#\">Heap</a></h3>")
//          sb.append("<div><ul>")
//          sb.append("<li>"+DomainPrinter.printHeap(4, state._1).replace("\n","</li><li>")+"</li>")
//          sb.append("</ul></div>")
//
//          first = false;
//        }
//
//        // exception state
//        if (excState != StateBot) {
//          if (!first) sb.append("<li></li>");
//          sb.append("<h5><a herf=\"#\">Exception Context</a></h3>")
//          sb.append("<div><ul>")
//          sb.append("<li>"+DomainPrinter.printContext(4, excState._2).replace("\n","</li><li>")+"</li>")
//          sb.append("</ul></div>")
//
//          sb.append("<h5><a herf=\"#\">Exception Heap</a></h3>")
//          sb.append("<div><ul>")
//          sb.append("<li>"+DomainPrinter.printHeap(4, excState._1).replace("\n","</li><li>")+"</li>")
//          sb.append("</ul></div>")
//          first = false;
//        }
//
//        if (first) sb.append("<div><li>Bot</li></div>");
//        textMap += (keyStr -> sb.toString)
//      }
//    }
    val json = JSONObject(textMap)
    json.toString()
  }
}
