/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing

import scala.collection.immutable.{ TreeMap, TreeSet, HashSet, HashMap, Stack => IStack }
import scala.collection.mutable.{ HashMap => MHashMap, HashSet => MHashSet, Stack => MStack }
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.lib.graph.DGraph
import kr.ac.kaist.jsaf.analysis.lib.WorkTreeSet
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.scala_src.useful.WorkTrait

/**
 * Worklist manager
 */
abstract class Worklist {
  ////////////////////////////////////////////////////////////////////////////////
  // Variables
  ////////////////////////////////////////////////////////////////////////////////
  private var cfg: CFG = null
  private var order: OrderMap = null
  private var headorder: OrderMap = TreeMap[Node, Int]()
  private var backedges: HashMap[Node, HashSet[Node]] = HashMap()
  private var quiet: Boolean = false

  ////////////////////////////////////////////////////////////////////////////////
  // Abstract Functions
  ////////////////////////////////////////////////////////////////////////////////
  def head: ControlPoint
  def isEmpty: Boolean
  def getSize: Int
  def getWorkList: WorkTreeSet
  def toString: String
  protected def insertWork(work: OrderEntry): Unit
  protected def removeHead: ControlPoint

  ////////////////////////////////////////////////////////////////////////////////
  // Initialization
  ////////////////////////////////////////////////////////////////////////////////
  // Dense init
  def init(cfg: CFG, order: OrderMap, quiet: Boolean): Unit = {
    this.cfg = cfg
    this.order = order
    this.quiet = quiet
  }

  // Sparse init
  def init(cfg: CFG, order: OrderMap, quiet: Boolean, headorder: OrderMap, backedges: HashMap[Node, HashSet[Node]]): Unit = {
    init(cfg, order, quiet)
    this.headorder = headorder
    this.backedges = backedges
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Add a Work
  ////////////////////////////////////////////////////////////////////////////////
  def add(cp: ControlPoint, callerCPSetOpt: Option[CPStackSet], increaseRefCount: Boolean): Unit = this.synchronized {
    val ov = if(order == null) 0 else order.getOrElse(cp._1, 0) // 0 => case for an empty block
    insertWork((ov, cp))
    addCallerCPSet(cp, callerCPSetOpt, increaseRefCount)
    if(useWorkManager) Shell.workManager.pushWork(workTrait)
  }

  def add(origin: Node, cp: ControlPoint, callerCPSetOpt: Option[CPStackSet], increaseRefCount: Boolean): Unit = this.synchronized {
    backedges.get(cp._1) match {
      case Some(backnodes) if backnodes.contains(origin) =>
        insertWork((headorder(cp._1), cp))
        addCallerCPSet(cp, callerCPSetOpt, increaseRefCount)
        if(useWorkManager) Shell.workManager.pushWork(workTrait)
      case Some(backnodes) => add(cp, callerCPSetOpt, increaseRefCount)
      case _ => add(cp, callerCPSetOpt, increaseRefCount)
    }
  }

  def add(cp_pred: ControlPoint, cp: ControlPoint, cfg: CFG, callerCPSetOpt: Option[CPStackSet], increaseRefCount: Boolean, updateTable: Unit => Unit): Boolean = this.synchronized {
    var isWorkAdded = false
    callerCPSetOpt match {
      case Some(callerCPSet) =>
        cp match {
          // Call -> Entry
          case ((_, LEntry), _) =>
            val newCallerCPStackSet = new CPStackSet
            for(callerCPStack <- callerCPSet) newCallerCPStackSet.add(callerCPStack.push(cp_pred))
            updateTable()
            add(cp, Some(newCallerCPStackSet), increaseRefCount)
            isWorkAdded = true
          // Exit or ExitExc -> Aftercall
          case _ =>
            var doesExist = false
            val newCallerCPStackSet = new CPStackSet
            for(callerCPStack <- callerCPSet) {
              val topCP = callerCPStack.top
              if(cp._1 == cfg.getAftercallFromCallMap.getOrElse(topCP._1, null) ||
                 cp._1 == cfg.getAftercatchFromCallMap.getOrElse(topCP._1, null)) {
                doesExist = true
                if(callerCPStack.size > 1) newCallerCPStackSet.add(callerCPStack.pop)
              }
            }
            if(doesExist) {
              updateTable()
              if(newCallerCPStackSet.size == 0) add(cp, None, increaseRefCount)
              else add(cp, Some(newCallerCPStackSet), increaseRefCount)
              isWorkAdded = true
            }
        }
      case None =>
        updateTable()
        cp match {
          // Call -> Entry
          case ((_, LEntry), _) => add(cp, Some(MHashSet(IStack(cp_pred))), increaseRefCount)
          // Exit or ExitExc -> Aftercall
          case _ => add(cp, None, increaseRefCount)
        }
        isWorkAdded = true
    }
    isWorkAdded
  }

  ////////////////////////////////////////////////////////////////////////////////
  // etc.
  ////////////////////////////////////////////////////////////////////////////////
  def getOrder(): OrderMap = order
  def getHead(): (ControlPoint, Option[CPStackSet]) = this.synchronized {
    val cp: ControlPoint = this.synchronized { removeHead }
    val callerCPStackSet = callerCPMap.get(cp) match {
      case Some(callerCPStackSetRef) =>
        if(callerCPStackSetRef.refCount == 1) {
          callerCPMap.remove(cp)
          cpStackSetRefPool.push(callerCPStackSetRef)
        }
        else callerCPStackSetRef.refCount-= 1
        if(callerCPStackSetRef.cpStackSet != null) Some(callerCPStackSetRef.cpStackSet) else None
      case None =>
        callerCPMap.remove(cp)
        None
    }
    (cp, callerCPStackSet)
  }
  def dump() = if (!quiet) System.out.print("next: " + head + "                ")

  ////////////////////////////////////////////////////////////////////////////////
  // Caller ControlPoint Stack Set
  ////////////////////////////////////////////////////////////////////////////////
  class CPStackSetRef {
    var refCount = 1
    var cpStackSet: CPStackSet = null
  }
  private val callerCPMap = new MHashMap[ControlPoint, CPStackSetRef]
  private val cpStackSetRefPool = new MStack[CPStackSetRef]
  private def getNewCPStackSetRef(_cpStackSet: CPStackSet = null): CPStackSetRef = {
    val cpStackSetRef = if(cpStackSetRefPool.isEmpty) new CPStackSetRef else cpStackSetRefPool.pop()
    cpStackSetRef.refCount = 1
    cpStackSetRef.cpStackSet = _cpStackSet
    cpStackSetRef
  }
  private def addCallerCPSet(cp: ControlPoint, callerCPSetOpt: Option[CPStackSet], increaseRefCount: Boolean): Unit = {
    callerCPSetOpt match {
      case Some(callerCPSet) =>
        callerCPMap.get(cp) match {
          case Some(prevCallerCPSetRef) =>
            if(increaseRefCount) prevCallerCPSetRef.refCount+= 1
            if(prevCallerCPSetRef.cpStackSet == null) prevCallerCPSetRef.cpStackSet = callerCPSet
            else prevCallerCPSetRef.cpStackSet++= callerCPSet
          case None => callerCPMap.put(cp, getNewCPStackSetRef(callerCPSet))
        }
      case None => callerCPMap.put(cp, getNewCPStackSetRef())
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // For WorkManager (Thread library)
  ////////////////////////////////////////////////////////////////////////////////
  private var useWorkManager = false
  private var workTrait: WorkTrait = null
  def setUseWorkManager(_useWorkManager: Boolean, _workTrait: WorkTrait): Unit = {
    useWorkManager = _useWorkManager
    workTrait = _workTrait
  }
}

object Worklist {
  ////////////////////////////////////////////////////////////////////////////////
  // Worklist Order Types
  ////////////////////////////////////////////////////////////////////////////////
  final val WORKLIST_ORDER_DEFAULT: Int = 0
  final val WORKLIST_ORDER_FIFO: Int = 1
  final val WORKLIST_ORDER_LIFO: Int = 2
  final val WORKLIST_ORDER_COUNT: Int = 3

  ////////////////////////////////////////////////////////////////////////////////
  // Worklist Computes
  ////////////////////////////////////////////////////////////////////////////////
  def computes(cfg: CFG) : Worklist = computes(Shell.params.opt_WorklistOrder, cfg, false)
  def computes(cfg: CFG, quiet: Boolean) : Worklist = computes(Shell.params.opt_WorklistOrder, cfg, quiet)
  def computes(orderType: Int, cfg: CFG, quiet: Boolean) : Worklist = {
    val startTime = System.nanoTime
    var worklist: Worklist = null
    orderType match {
      case WORKLIST_ORDER_DEFAULT =>
        val empty = TreeMap[Node, Int]()
        val (map, _) = cfg.getNodes.foldLeft((empty, 0))((m, n) => (m._1 + (n -> m._2), m._2 + 1))
        worklist = new WorklistDefault
        worklist.init(cfg, map, quiet)
      case WORKLIST_ORDER_FIFO =>
        worklist = new WorklistFIFO
        worklist.init(cfg, null, quiet)
      case WORKLIST_ORDER_LIFO =>
        worklist = new WorklistLIFO
        worklist.init(cfg, null, quiet)
      case WORKLIST_ORDER_COUNT =>
        worklist = new WorklistCount
        worklist.init(cfg, null, quiet)
    }
    if (!quiet) {
      val elapsedTime = (System.nanoTime - startTime) / 1000000000.0
      System.out.format("# Time for worklist order computation(s): %.2f\n", new java.lang.Double(elapsedTime))
    }
    worklist
  }

  def computesSparse(interDDG: DGraph[Node], quiet: Boolean): Worklist = {
    val s = System.nanoTime
    var map = TreeMap[Node, Int]()
    var headmap = TreeMap[Node, Int]()
    var backedges = HashMap[Node, HashSet[Node]]()
    var order_i = 0

    def findLoophead(g: DGraph[Node]): Node = {
      // check back-edges to find a loop head.
      var refs: HashMap[Node, Int] = g.getNodes.foldLeft(HashMap[Node,Int]())((M, n) => M + (n -> 0))
      g.succs.foreach(kv => {
        kv._2.foreach(n => refs += (n -> (refs(n) + 1)))
      })
      val (entry, _) = refs.foldLeft(refs.head)((h, ref) => if (ref._2 > h._2) ref else h)
      entry
    }

    def makeSCCGraph(g: DGraph[Node]): Unit = {
      val nodes = g.sccs
      def getNode(n: Node) = nodes.filter(ns => ns.contains(n)).head

      // constructs abstract graph for a given graph.
      val entry = getNode(g.entry)
      val (max, ntoi) = nodes.foldLeft((0, HashMap[HashSet[Node], Int]()))((pair, n) => (pair._1 + 1, pair._2 + (n -> pair._1)))
      val nodes_i = (0 to (max-1)).foldLeft(HashSet[Int]())((S, i) => S + i)
      val entry_i = ntoi(entry)
      val iton = new Array[HashSet[Node]](max)
      ntoi.foreach(kv => iton(kv._2) = kv._1)
      val edges_i =
        g.getNodes.foldLeft(HashSet[(Int, Int)]())((S, n) => {
          val succs = g.getSuccs(n)
          val src = getNode(n)
          succs.foldLeft(S)((S_, n2) => {
            val dst = getNode(n2)
            S_ + ((ntoi(src), ntoi(dst)))
          })
        })
      val agraph = DGraph.fromEdges[Int](nodes_i, entry_i, edges_i)

      // computes topological order for the abstract graph.
      agraph.topologicalOrder.foreach(n => {
        val sets: HashSet[Node] = iton(n)
        if (sets.size > 1) {
          // travers each of concrete graph
          val subgraph = DGraph.pruning(g,sets)
          val loophead = findLoophead(subgraph)
          val backnodes = subgraph.removeInedges(loophead)

          backedges += (loophead -> backnodes)
          subgraph.entry = loophead
          makeSCCGraph(subgraph)
        } else {
          map += (sets.head -> order_i)
          order_i += 1
        }
      })

      headmap += (g.entry -> order_i)
      order_i += 1
    }

    makeSCCGraph(interDDG.prunedGraph)

    val wl = new WorklistDefault
    wl.init(null, map, quiet, headmap, backedges)

    val elapsedTime = (System.nanoTime - s) / 1000000000.0
    if (!quiet)
      System.out.format("# Time for worklist order computation(s): %.2f\n", new java.lang.Double(elapsedTime))
    wl
  }
}
