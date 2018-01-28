/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.nodes.cfg

import scala.collection.mutable.{ HashMap => MHashMap, Map => MMap }
import kr.ac.kaist.safe.analyzer.domain.AbsState
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.TracePartition
import kr.ac.kaist.safe.analyzer.models.SemanticFun
import kr.ac.kaist.safe.{ LINE_SEP, MAX_INST_PRINT_SIZE }
import kr.ac.kaist.safe.util._

sealed trait CFGBlock {
  val func: CFGFunction
  val id: BlockId

  // outer loop
  var outerLoop: Option[LoopHead] = None

  def isOutBlock: Boolean = this match {
    case NormalBlock(_, label) if label.isOutLabel => true
    case Exit(_) | ExitExc(_) => true
    case _ => false
  }

  protected var iidCount: InstId = 0
  def getIId: InstId = iidCount

  // edges incident with this cfg node
  protected val succs: MMap[CFGEdgeType, List[CFGBlock]] = MHashMap()
  protected val preds: MMap[CFGEdgeType, List[CFGBlock]] = MHashMap()
  def getAllSucc: Map[CFGEdgeType, List[CFGBlock]] = succs.toMap
  def getAllPred: Map[CFGEdgeType, List[CFGBlock]] = preds.toMap

  def getSucc(edgeType: CFGEdgeType): List[CFGBlock] = succs.getOrElse(edgeType, Nil)
  def getPred(edgeType: CFGEdgeType): List[CFGBlock] = preds.getOrElse(edgeType, Nil)

  // add edge
  def addSucc(edgeType: CFGEdgeType, node: CFGBlock): Unit = succs(edgeType) = node :: succs.getOrElse(edgeType, Nil)
  def addPred(edgeType: CFGEdgeType, node: CFGBlock): Unit = preds(edgeType) = node :: preds.getOrElse(edgeType, Nil)

  // get inst.
  def getInsts: List[CFGInst] = Nil

  // get string of succs
  protected def getSuccsStr: String = {
    succs.toSeq.map {
      case (_, succ) => succ.map {
        case Entry(_) => "Entry"
        case Exit(_) => "Exit"
        case ExitExc(_) => "ExitExc"
        case b => s"[${b.id}]"
      }.mkString(", ")
    }.mkString(", ") match {
      case "" => ""
      case res => " -> " + res
    }
  }

  // equals
  override def equals(other: Any): Boolean = other match {
    case (block: CFGBlock) =>
      block.func == func &&
        block.id == id
    case _ => false
  }

  // toString
  override def toString: String
  def toString(indent: Int): String = {
    val pre = "  " * indent
    val s: StringBuilder = new StringBuilder
    s.append(pre).append(toString())
      .append(getSuccsStr)
      .append(LINE_SEP)
    s.toString
  }

  // span
  def span: Span

  // hash code
  override def hashCode: Int = (func.id << 16) + (id + 3)
}
object CFGBlock {
  implicit def node2nodelist(node: CFGBlock): List[CFGBlock] = List(node)
  implicit def ordering[B <: CFGBlock]: Ordering[B] = Ordering.by {
    case block => (block.func.id, block.id)
  }
}

// entry, exit, exception exit
case class Entry(func: CFGFunction) extends CFGBlock {
  val id: BlockId = -1
  override def toString: String = s"Entry[$id]"
  def span: Span = func.span.copy(end = func.span.begin)
}
case class Exit(func: CFGFunction) extends CFGBlock {
  val id: BlockId = -2
  override def toString: String = s"Exit[$id]"
  def span: Span = func.span.copy(begin = func.span.end)
}
case class ExitExc(func: CFGFunction) extends CFGBlock {
  val id: BlockId = -3
  override def toString: String = s"ExitExc[$id]"
  def span: Span = func.span.copy(begin = func.span.end)
}

// call, after-call, after-catch
case class Call(func: CFGFunction) extends CFGBlock {
  val id: BlockId = func.getBId
  private var iAfterCall: AfterCall = _
  private var iAfterCatch: AfterCatch = _
  private var iCallInst: CFGCallInst = _
  def afterCall: AfterCall = iAfterCall
  def afterCatch: AfterCatch = iAfterCatch
  def callInst: CFGCallInst = iCallInst
  override def toString: String = s"Call[$id]"
  override def toString(indent: Int): String = {
    val pre = "  " * indent
    val s: StringBuilder = new StringBuilder
    s.append(pre).append(toString)
    s.append(getSuccsStr).append(LINE_SEP)
      .append(pre).append(s"  [${callInst.id}] $callInst").append(LINE_SEP)
    s.toString
  }
  def span: Span = callInst.span
  override def getInsts: List[CFGInst] = List(callInst)
}
object Call {
  def apply(func: CFGFunction, callInstCons: Call => CFGCallInst, retVar: CFGId): Call = {
    val call = Call(func)
    call.iAfterCall = AfterCall(func, retVar, call)
    call.iAfterCatch = AfterCatch(func, call)
    call.iCallInst = callInstCons(call)
    call.iidCount += 1
    call
  }
}
case class AfterCall(func: CFGFunction, retVar: CFGId, call: Call) extends CFGBlock {
  val id: BlockId = func.getBId + 1
  override def toString: String = s"AfterCall[$id]"
  def span: Span = call.callInst.span.copy(begin = call.callInst.span.end)
}
case class AfterCatch(func: CFGFunction, call: Call) extends CFGBlock {
  val id: BlockId = func.getBId + 2
  override def toString: String = s"AfterCatch[$id]"
  def span: Span = call.callInst.span.copy(begin = call.callInst.span.end)
}

// normal block
case class NormalBlock(func: CFGFunction, label: LabelKind = NoLabel) extends CFGBlock {
  // block id
  val id: BlockId = func.getBId

  // inst list
  private var insts: List[CFGNormalInst] = Nil
  override def getInsts: List[CFGNormalInst] = insts

  // create inst
  def createInst(instCons: NormalBlock => CFGNormalInst): CFGNormalInst = {
    val inst: CFGNormalInst = instCons(this)
    iidCount += 1
    insts ::= inst
    inst
  }

  // toString
  override def toString: String = s"$label[$id]"
  override def toString(indent: Int): String = {
    val pre = "  " * indent
    val s: StringBuilder = new StringBuilder
    s.append(pre).append(toString)
    s.append(getSuccsStr).append(LINE_SEP)
    val instLen = insts.length
    instLen > MAX_INST_PRINT_SIZE match {
      case true =>
        s.append(pre)
          .append(s"  A LOT!!! $instLen instructions are not printed here.")
          .append(LINE_SEP)
      case false => insts.reverseIterator.foreach {
        case inst =>
          s.append(pre)
            .append(s"  [${inst.id}] $inst")
            .append(LINE_SEP)
      }
    }
    s.toString
  }

  // span
  def span: Span = {
    val fileName = func.span.fileName
    val (begin, end) = insts match {
      case head :: _ => (insts.last.span.begin, head.span.end)
      case Nil => (SourceLoc(), SourceLoc()) // TODO return correct span
    }
    Span(fileName, begin, end)
  }
}

// loop head
case class LoopHead(func: CFGFunction, span: Span) extends CFGBlock {
  // block id
  val id: BlockId = func.getBId

  // out blocks
  var outBlocks: List[CFGBlock] = Nil

  // inst list
  override def getInsts: List[CFGNormalInst] = Nil

  // toString
  override def toString: String = s"LoopHead[$id]"
  override def toString(indent: Int): String = {
    val pre = "  " * indent
    val s: StringBuilder = new StringBuilder
    s.append(pre).append(toString)
    s.append(getSuccsStr).append(LINE_SEP)
    s.toString
  }
}

case class ModelBlock(func: CFGFunction, sem: SemanticFun) extends CFGBlock {
  val id: BlockId = func.getBId
  override def toString: String = s"Model[$id]"
  val span: Span = Span() // TODO set meaningful Span
}
