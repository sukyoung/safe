/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
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
import scala.collection.immutable.HashMap
import scala.collection.JavaConverters._
import kr.ac.kaist.safe.config.Config
import kr.ac.kaist.safe.analyzer.{ Worklist, Semantics, ControlPoint }
import kr.ac.kaist.safe.analyzer.console.command._
import kr.ac.kaist.safe.analyzer.domain.State
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.cfg_builder.AddressManager
import kr.ac.kaist.safe.util.Span

class Console(
    val cfg: CFG,
    val worklist: Worklist,
    val semantics: Semantics,
    val addrManager: AddressManager
) {
  ////////////////////////////////////////////////////////////////
  // private variables
  ////////////////////////////////////////////////////////////////

  private val reader = new ConsoleReader()
  private val out: PrintWriter = new PrintWriter(reader.getOutput)
  private var iter: Int = -1
  private var target: Target = TargetIter(0)
  private var cur: ControlPoint = _
  private var home: ControlPoint = _

  init

  ////////////////////////////////////////////////////////////////
  // API
  ////////////////////////////////////////////////////////////////

  def runFixpoint: Unit = {
    iter += 1
    cur = worklist.head
    home = cur
    val (block, cc) = (cur.node, cur.callContext)
    val find = target match {
      case TargetIter(k) => iter == k
      case TargetBlock(b) => b == block
    }
    find match {
      case true =>
        reader.setPrompt(
          toString(cur) + Config.LINE_SEP +
            s"Iter[$iter] > "
        )
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

  ////////////////////////////////////////////////////////////////
  // private helper
  ////////////////////////////////////////////////////////////////

  private def init: Unit = {
    val cmds = Console.commands.keys.asJavaCollection
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
        Console.commands.get(cmd) match {
          case Some(o) => o.run(this, args)
          case None => println(s"* $cmd: command not found"); None
        }
      }
    }
  }

  private def toString(cp: ControlPoint): String = {
    val block = cp.node
    val func = block.func
    val span = block.span
    val cc = cp.callContext
    s"<$func: $block, $cc> @${span.toString}"
  }
}

object Console {
  val commandList: List[Command] = List(
    CmdHelp,
    CmdNext,
    CmdJump,
    CmdPrint,
    CmdPrintResult,
    CmdRun,
    CmdRunInsts
  // CmdMove
  // CmdHome
  // CmdRun
  // CmdBreak
  // CmdBreakList
  // CmdRemoveBreak
  )
  val commands: Map[String, Command] = commandList.foldLeft(
    Map[String, Command]()
  ) { case (map, cmd) => map + (cmd.name -> cmd) }
}

sealed abstract class Target
case class TargetIter(iter: Int) extends Target
case class TargetBlock(block: CFGBlock) extends Target
