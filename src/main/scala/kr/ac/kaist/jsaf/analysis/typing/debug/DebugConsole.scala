/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.debug

import jline.console.ConsoleReader
import java.io.PrintWriter
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain._
import scala.collection.mutable.{HashMap => MHashMap}
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.debug.commands._
import kr.ac.kaist.jsaf.analysis.cfg.Block
import kr.ac.kaist.jsaf.analysis.typing.domain.State
import kr.ac.kaist.jsaf.analysis.cfg.LBlock
import jline.console.completer.StringsCompleter
import scala.collection.JavaConverters._

class DebugConsole(cfg: CFG, worklist: Worklist, sem: Semantics, table: Table) {
  val reader = new ConsoleReader()
  val out: PrintWriter = new PrintWriter(reader.getOutput)
  val command = new MHashMap[String,Command]()

  /**
   * Environments for debugger
   */
  var target: Int = 0
  var targetNode: Option[Node] = None
  var current: ControlPoint = null
  var home: ControlPoint = null
  var iter: Int = 0
  def getCFG = cfg
  def getWorklist = worklist
  def getSemantics = sem
  def getTable = table

  /**
   * Register commands to this console.
   * @param list a list of commands
   */
  protected def register(list: List[Command]): Unit = {
    list.foreach(cmd => {
      val key = cmd.name
      if (command.contains(key)) System.err.println("* Warning in debugger: "+key+" is already registered")
      else command += key -> cmd
    })
  }

  /**
   * Initialize
   */
  def initialize() = {
    register(List(new CmdHelp, new CmdNext, new CmdJump, new CmdPrint, new CmdHome, new CmdMove, new CmdPrintResult))
    updateCompletor()
    runCmd("help", Array[String]())
  }

  def updateCompletor() = {
    reader.addCompleter(new StringsCompleter(command.keys.asJavaCollection))
  }

  def readTable(cp: ControlPoint): State = {
    table.get(cp._1) match {
      case None => StateBot
      case Some(map) => map.get(cp._2) match {
        case None => StateBot
        case Some(state) => state
      }
    }
  }

  protected def getLinenumber(cp: ControlPoint): (String, String, String) = {
    cp._1._2 match {
      case LEntry | LExit | LExitExc => {
        val fid = cp._1._1
        val info = cfg.getFuncInfo(fid)
        val start_line = info.getSpan.getBegin.getLine.toString
        val end_line = info.getSpan.getEnd.getLine.toString
        val filename = info.getSpan.getBegin.getFileNameOnly
        (filename, start_line, end_line)
      }
      case LBlock(_) => {
        val cmd = cfg.getCmd(cp._1)
        cmd match {
          case Block(insts) if insts.length > 0 => {
            val start_info = insts.head.getInfo
            val end_info = insts.takeRight(1).head.getInfo

            val start_line = start_info match {
              case Some(info) => info.getSpan.getBegin.getLine.toString
              case None => "_"
            }
            val end_line = end_info match {
              case Some(info) => info.getSpan.getBegin.getLine.toString
              case None => "_"
            }
            val filename = start_info match {
              case Some(info) => info.getSpan.getBegin.getFileNameOnly
              case None => "_"
            }
            (filename, start_line, end_line)
          }
          case _ => ("_", "_", "_")
        }
      }
    }
  }

  def runFixpoint(count: Int): Unit = {
    if (!worklist.isEmpty) {
      current = worklist.head
      home = current
      targetNode match {
        case Some(tn) if tn.equals(current._1) => {
          targetNode = None
          target = -1
        }
        case _ => ()
      }
    }

    if (count >= target) {
      System.out.println()
      iter = count

      var line: String = ""

      while (count >= target) {
        val linenumber = getLinenumber(current)
        reader.setPrompt("iteration(#): "+iter+", current: "+current+"\n%s~%s @%s: ".format(linenumber._2, linenumber._3, linenumber._1))
        line = reader.readLine()

        if (line != null) {
          val cmd = parse(line)
          runCmd(cmd._1, cmd._2)

          out.flush()
        }
      }
    }
  }

  def parse(line: String) = {
    val list = line.split(" ")
    (list.head, list.tail)
  }

  def runCmd(cmd: String, args: Array[String]) = {
    command.get(cmd) match {
      case Some(o) => o.run(this, args)
      case None => System.out.println(cmd+": command not found")
    }
  }
}

object DebugConsole {
  /**
   * Singleton object
   */
  var console: DebugConsole = null

  def initialize(cfg: CFG, worklist: Worklist, sem: Semantics, table: Table) = {
    console = new DebugConsole(cfg, worklist, sem, table)
    console.initialize()
  }

  def runFixpoint(count: Int) = console.runFixpoint(count)
  def runFinished() = {
    console.target = -1
    console.runFixpoint(console.iter)
  }
}
