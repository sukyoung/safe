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

package kr.ac.kaist.safe

import scala.util.Try
import scala.collection.immutable.HashMap
import kr.ac.kaist.safe.phase._
import kr.ac.kaist.safe.errors.error.NoMode
import kr.ac.kaist.safe.util.ArgParser
import kr.ac.kaist.safe.nodes.ast.Program
import kr.ac.kaist.safe.nodes.ir.IRRoot
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.domain._

sealed trait Command {
  val name: String
  def apply(args: List[String], testMode: Boolean): Try[Any]
}

class CommandObj[Result](
    override val name: String,
    pList: PhaseList[Result],
    modeMap: Map[String, PhaseList[Result]] = HashMap[String, PhaseList[Result]]()
) extends Command {
  def apply(
    args: List[String],
    testMode: Boolean = false
  ): Try[Result] = {
    val safeConfig = SafeConfig(this, testMode = testMode)
    val parser = new ArgParser(this, safeConfig)
    val modePattern = "--(.+)".r
    (args match {
      case modePattern(mode) :: remain => modeMap.get(mode) match {
        case Some(pl) => (pl, remain)
        case None => throw NoMode(name, mode)
      }
      case _ => (pList, args)
    }) match {
      case (pList, args) => pList.getRunner(parser).flatMap {
        case runner => parser(args).flatMap {
          case _ => Safe(this, runner(_), safeConfig)
        }
      }
    }
  }

  def display(res: Result): Unit = ()

  override def toString: String = modeMap.foldLeft(pList.toString) {
    case (str, (mode, pList)) => s"$str$LINE_SEP--$mode: " + pList.toString
  }

  def >>[C <: Config, R](phase: PhaseObj[Result, C, R]): PhaseList[R] = pList >> phase
}

// base command
case object CmdBase extends CommandObj("", PhaseNil)

// parse
case object CmdParse extends CommandObj("parse", CmdBase >> Parse) {
  override def display(program: Program): Unit = println(program.toString(0))
}

// astRewrite
case object CmdASTRewrite extends CommandObj("astRewrite", CmdParse >> ASTRewrite) {
  override def display(program: Program): Unit = println(program.toString(0))
}

// compile
case object CmdCompile extends CommandObj("compile", CmdASTRewrite >> Compile) {
  override def display(ir: IRRoot): Unit = println(ir.toString(0))
}

// cfgBuild
case object CmdCFGBuild extends CommandObj("cfgBuild", CmdCompile >> CFGBuild) {
  override def display(cfg: CFG): Unit = println(cfg.toString(0))
}

// cfgLoad
case object CmdCFGLoad extends CommandObj("cfgLoad", CmdBase >> CFGLoader) {
  override def display(cfg: CFG): Unit = println(cfg.toString(0))
}

// analyze
case object CmdAnalyze extends CommandObj("analyze", CmdCFGBuild >> Analyze, HashMap(
  "cfgFromJson" -> (CmdCFGLoad >> Analyze)
)) {
  override def display(result: (CFG, Int, TracePartition, Semantics)): Unit = {
    val (cfg, iters, _, sem) = result

    println(s"- # of iteration: $iters")
    // function info.
    val userFuncs = cfg.getUserFuncs
    println(s"- # of user functions: ${userFuncs.length}")
    val unreachableList = userFuncs.filter(f => sem.getState(f.entry).isEmpty)
    if (!unreachableList.isEmpty) {
      println(s"  * There are ${unreachableList.length} unreachable user functions:")
      unreachableList.foreach(func => {
        val str = func.name + " @ " + func.span
        println(s"    $str")
      })
    }
    val modelFuncs = cfg.getAllFuncs.filter(func => {
      !func.isUser && !sem.getState(func.entry).isEmpty
    })
    if (!modelFuncs.isEmpty) {
      println(s"  * ${modelFuncs.length} modeling functions are used:")
      modelFuncs.foreach(func => println(s"    ${func.name}"))
    }

    // block info.
    val blocks = cfg.getAllBlocks.filter(!sem.getState(_).isEmpty)
    println(s"- # of touched blocks: ${blocks.length}")
    val userBlocks = blocks.filter(_.func.isUser)
    println(s"    user blocks: ${userBlocks.length}")
    println(s"    modeling blocks: ${blocks.length - userBlocks.length}")

    // instruction info.
    val insts = blocks.foldLeft(0)(_ + _.getInsts.length)
    println(s"- # of instructions: $insts")
  }
}

// bugDetect
case object CmdBugDetect extends CommandObj("bugDetect", CmdAnalyze >> BugDetect) {
  override def display(cfg: CFG): Unit = ()
}

// jsModelRewrite
case object CmdJSModelRewrite extends CommandObj("jsModelRewrite", CmdBase >> JSModelRewrite)

// interpret
case object CmdInterpret extends CommandObj("interpret", CmdCompile >> Interpret)

// help
case object CmdHelp extends CommandObj("help", CmdBase >> Help)
