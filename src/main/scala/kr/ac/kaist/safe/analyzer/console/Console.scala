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

package kr.ac.kaist.safe.analyzer.console

import jline.console.ConsoleReader
import jline.console.completer._
import java.io.PrintWriter
import scala.collection.immutable.{ HashMap, TreeSet }
import scala.collection.JavaConverters._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.analyzer.{ Worklist, Semantics, ControlPoint, TracePartition }
import kr.ac.kaist.safe.analyzer.console.command._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util.Span
import kr.ac.kaist.safe.phase.HeapBuildConfig

class Console(
    val cfg: CFG,
    val worklist: Worklist,
    val sem: Semantics,
    val config: HeapBuildConfig,
    private var iter: Int = -1
) {
  ////////////////////////////////////////////////////////////////
  // private variables
  ////////////////////////////////////////////////////////////////

  private val reader = new ConsoleReader()
  private val out: PrintWriter = new PrintWriter(reader.getOutput)
  private var target: Target = TargetIter(iter + 1)
  private var cur: ControlPoint = _
  private var home: ControlPoint = _
  private var breakList: TreeSet[CFGBlock] = TreeSet()

  init

  ////////////////////////////////////////////////////////////////
  // API
  ////////////////////////////////////////////////////////////////

  def runFixpoint: Unit = {
    iter += 1
    cur = worklist.head
    home = cur
    val block = cur.block
    val find = (target match {
      case TargetIter(k) => iter == k
      case TargetBlock(b) => b == block
    }) || breakList(block)
    find match {
      case true =>
        this.setPrompt
        while ({
          println
          val line = reader.readLine
          val loop = runCmd(line) match {
            case Some(t) =>
              target = t; false
            case None => true
          }
          out.flush
          loop
        }) {}
      case false =>
    }
  }

  def runFinished: Unit = println("* analysis finished")

  def getIter: Int = iter

  def getCurCP: ControlPoint = cur
  def goHome: Unit = {
    if (cur == home) {
      println("* here is already original control point.")
    } else {
      cur = home
      println("* reset the current control point.")
      this.setPrompt
    }
  }
  def moveCurCP(block: CFGBlock): Unit = {
    val tpList: List[TracePartition] = sem.getState(block).toList.map {
      case (tp, _) => tp
    }
    val len: Int = tpList.length
    val fid: Int = block.func.id
    tpList match {
      case Nil => println(s"* no call-context in function[$fid] $block")
      case tp :: Nil => {
        cur = ControlPoint(block, tp)
        println(s"* current control point changed.")
        this.setPrompt
      }
      case _ => {
        reader.setPrompt(
          tpList.zipWithIndex.map {
            case (tp, idx) => s"[$idx] $tp" + LINE_SEP
          }.mkString + s"select call context index > "
        )
        while ({
          println
          reader.readLine match {
            case null => {
              println
              println("* current control point not changed.")
              false
            }
            case "" => true
            case line => !line.forall(_.isDigit) || (line.toInt match {
              case idx if idx < len => {
                cur = ControlPoint(block, tpList(idx))
                println(s"* current control point changed.")
                false
              }
              case _ => println(s"* given index is out of bound $len"); true
            })
          }
        }) {}
        this.setPrompt
      }
    }
  }

  def addBreak(block: CFGBlock): Unit = breakList += block
  def getBreakList: List[CFGBlock] = breakList.toList
  def removeBreak(block: CFGBlock): Unit = breakList -= block

  ////////////////////////////////////////////////////////////////
  // private helper
  ////////////////////////////////////////////////////////////////

  private def init: Unit = {
    val cmds = Console.commands.map(_.name).asJavaCollection
    reader.addCompleter(new StringsCompleter(cmds))
    // TODO extend aggregator for sub-command
    // reader.addCompleter(new AggregateCompleter(
    //   new ArgumentCompleter(new StringsCompleter("asdf"), new StringsCompleter("sdf"), new NullCompleter()),
    //   new ArgumentCompleter(new StringsCompleter("wer"), new NullCompleter())
    // ))
    runCmd("help")
  }

  private def runCmd(line: String): Option[Target] = {
    line match {
      case null =>
        println; Some(TargetIter(-1)) // run
      case "" => Some(TargetIter(iter + 1))
      case _ => {
        val list = line.trim.split("\\s+").toList
        val cmd = list.head
        val args = list.tail
        Console.cmdMap.get(cmd) match {
          case Some(o) => o.run(this, args)
          case None => println(s"* $cmd: command not found"); None
        }
      }
    }
  }

  private def toString(cp: ControlPoint): String = {
    val block = cp.block
    val func = block.func.simpleName
    val span = block.span
    val tp = cp.tracePartition
    s"<$func: $block, $tp> @${span.toString}"
  }

  private def setPrompt: Unit = {
    reader.setPrompt(
      toString(cur) + LINE_SEP +
        s"Iter[$iter] > "
    )
  }
}

object Console {
  val commands: List[Command] = List(
    CmdHelp,
    CmdNext,
    CmdJump,
    CmdPrint,
    CmdPrintResult,
    CmdRunInsts,
    CmdMove,
    CmdHome,
    CmdRun,
    CmdBreak,
    CmdBreakList,
    CmdBreakRemove,
    CmdFindBot,
    CmdDump
  )
  val cmdMap: Map[String, Command] = commands.foldLeft(
    Map[String, Command]()
  ) { case (map, cmd) => map + (cmd.name -> cmd) }
}

sealed abstract class Target
case class TargetIter(iter: Int) extends Target
case class TargetBlock(block: CFGBlock) extends Target
