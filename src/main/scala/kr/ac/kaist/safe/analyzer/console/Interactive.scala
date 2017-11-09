/*
 * ****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ***************************************************************************
 */

package kr.ac.kaist.safe.analyzer.console

import kr.ac.kaist.safe.analyzer.console.command._
import kr.ac.kaist.safe.analyzer.{ ControlPoint, Semantics, Worklist }
import kr.ac.kaist.safe.nodes.cfg.{ CFG, CFGBlock }
import kr.ac.kaist.safe.phase.HeapBuildConfig

import scala.collection.immutable.TreeSet

trait Interactive {
  val cfg: CFG
  val sem: Semantics
  val config: HeapBuildConfig
  var iter: Int = -1

  ////////////////////////////////////////////////////////////////
  // private variables
  ////////////////////////////////////////////////////////////////

  protected var target: Target = TargetStart
  protected var cur: ControlPoint = _
  protected var home: ControlPoint = _
  protected var breakList: TreeSet[CFGBlock] = TreeSet()

  ////////////////////////////////////////////////////////////////
  // API
  ////////////////////////////////////////////////////////////////

  def worklist: Worklist = sem.worklist
  def runFixpoint: Unit
  def prepareToRunFixpoint: Boolean = {
    iter += 1
    cur = worklist.head
    home = cur
    val block = cur.block
    (target match {
      case TargetStart => iter == 0
      case TargetIter(k) => iter == k
      case _ => false
    }) || breakList(block)
  }

  def runCmd(line: String): CmdResult = {
    line match {
      case null =>
        target = NoTarget; CmdResultBreak()
      case "" =>
        target = TargetIter(iter + 1); CmdResultBreak()
      case _ =>
        val list = line.trim.split("\\s+").toList
        val cmd = list.head
        val args = list.tail
        Command.cmdMap.get(cmd) match {
          case Some(o) =>
            o.run(this, args) match {
              case Some(TargetStart) =>
                target = TargetStart
                iter = -1
                CmdResultRestart
              case Some(t) =>
                target = t
                CmdResultBreak(o.result())
              case None => CmdResultContinue(o.result())
            }
          case None => CmdResultContinue(s"* $cmd: command not found")
        }
    }
  }
  def runFinished(): Unit = println("* analysis finished")

  def getIter: Int = iter
  def getCurCP: ControlPoint = cur
  def moveCurCP(block: CFGBlock): Unit

  def goHome(): Unit = {
    if (cur != home) cur = home
  }

  def addBreak(block: CFGBlock): Unit = breakList += block
  def getBreakList: List[CFGBlock] = breakList.toList
  def removeBreak(block: CFGBlock): Unit = breakList -= block

  def getPrompt: String
}

sealed abstract class CmdResult(
    val output: String = ""
) {
  override def toString: String = output
}

case class CmdResultContinue(override val output: String = "") extends CmdResult(output)
case class CmdResultBreak(override val output: String = "") extends CmdResult(output)
case object CmdResultRestart extends CmdResult
