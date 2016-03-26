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

package kr.ac.kaist.safe.config

import kr.ac.kaist.safe.phase.{ PhaseHelper, Parse, ASTRewrite, Compile, CFGBuild, Help }

object Command {
  val cmdMap: Map[String, Command] = Map(
    "parse" -> CmdParse,
    "astRewrite" -> CmdASTRewrite,
    "compile" -> CmdCompile,
    "cfgBuild" -> CmdCFGBuild,
    "help" -> CmdHelp
  )
}

sealed abstract class Command(name: String, val phaseHelper: PhaseHelper) {
  override def toString: String = name
}
case object CmdParse extends Command("parse", Parse)
case object CmdASTRewrite extends Command("astRewrite", ASTRewrite)
case object CmdCompile extends Command("compile", Compile)
case object CmdCFGBuild extends Command("cfgBuild", CFGBuild)
case object CmdHelp extends Command("help", Help)
