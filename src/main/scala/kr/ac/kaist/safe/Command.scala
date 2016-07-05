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
import kr.ac.kaist.safe.phase._
import kr.ac.kaist.safe.util.ArgParser

sealed trait Command {
  val name: String
  def apply(args: List[String]): Try[Any]
}

sealed abstract class CommandObj[Result](
    override val name: String,
    pList: PhaseList[Result]
) extends Command {
  def apply(args: List[String]): Try[Result] = {
    val safeConfig = SafeConfig(this)
    val parser = new ArgParser(this, safeConfig)
    pList.getRunner(parser).flatMap {
      case runner => parser(args).flatMap {
        case _ => Safe(runner(_), safeConfig)
      }
    }
  }

  override def toString: String = pList.nameList.reverse.mkString(" >> ")

  def >>[C <: Config, R](phase: PhaseObj[Result, C, R]): PhaseList[R] = pList >> phase
}

// base command
case object CmdBase extends CommandObj("", PhaseNil)

// commands
case object CmdParse extends CommandObj("parse", CmdBase >> Parse)
case object CmdASTRewrite extends CommandObj("astRewrite", CmdParse >> ASTRewrite)
case object CmdCompile extends CommandObj("compile", CmdASTRewrite >> Compile)
case object CmdCFGBuild extends CommandObj("cfgBuild", CmdCompile >> CFGBuild)
case object CmdAnalyze extends CommandObj("analyze", CmdCFGBuild >> Analyze)
case object CmdHelp extends CommandObj("help", CmdBase >> Help)
