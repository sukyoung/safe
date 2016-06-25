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

import kr.ac.kaist.safe.phase.{ PhaseHelper, Parse, ASTRewrite, Compile, CFGBuild, Analyze, Help }

object Command {
  val cmdMap: Map[String, Command] = Map(
    CmdParse.name -> CmdParse,
    CmdASTRewrite.name -> CmdASTRewrite,
    CmdCompile.name -> CmdCompile,
    CmdCFGBuild.name -> CmdCFGBuild,
    CmdAnalyze.name -> CmdAnalyze,
    CmdHelp.name -> CmdHelp
  )
}

sealed abstract class Command(val name: String, val phaseHelper: PhaseHelper) {
  override def toString: String = name
  def usage: String = ""
  def help: String = ""
}
case object CmdParse extends Command("parse", Parse) {
  override def usage: String =
    s" [-$name:out=outfile] infile.js ..."
  override def help: String = {
    val s: StringBuilder = new StringBuilder
    "  Parses files." + Config.LINE_SEP
    s.append(s"  If -$name:out=outfile is given, the parsed AST will be written to the outfile.")
    s.append(Config.LINE_SEP)
    s.toString
  }
}
case object CmdASTRewrite extends Command("astRewrite", ASTRewrite) {
  override def usage: String =
    s" [-$name:out=outfile] [-$name:verbose] infile.js ..."
  override def help: String = {
    val s: StringBuilder = new StringBuilder
    s.append("  Rewrites AST in JavaScript source files (hoister, disambiguater, withRewriter).")
    s.append(Config.LINE_SEP)
    s.append(s"  If -$name:out=outfile is given, the disambiguated AST will be written to the outfile.")
    s.append(Config.LINE_SEP)
    s.append(s"  If -$name:verbose is given, messages during rewriting AST are printed.")
    s.append(Config.LINE_SEP)
    s.toString
  }
}
case object CmdCompile extends Command("compile", Compile) {
  override def usage: String =
    s" [-$name:out=outfile] [-$name:verbose] infile.js ..."
  override def help: String = {
    val s: StringBuilder = new StringBuilder
    s.append("  Translates JavaScript source files to IR.")
    s.append(Config.LINE_SEP)
    s.append(s"  If -$name:out=outfile is given, the resulting IR will be written to the outfile.")
    s.append(Config.LINE_SEP)
    s.append(s"  If -$name:verbose is given, messages during compilation are printed.")
    s.append(Config.LINE_SEP)
    s.toString
  }
}
case object CmdCFGBuild extends Command("cfgBuild", CFGBuild) {
  override def usage: String =
    s" [-$name:out=outfile] [-$name:verbose] infile.js ..."
  override def help: String = {
    val s: StringBuilder = new StringBuilder
    s.append("  Builds a control flow graph for JavaScript source files.")
    s.append(Config.LINE_SEP)
    s.append("  The files are concatenated in the given order before being parsed.")
    s.append(Config.LINE_SEP)
    s.append(s"  If -$name:out=outfile is given, the resulting CFG will be written to the outfile.")
    s.append(Config.LINE_SEP)
    s.append(s"  If -$name:verbose is given, messages during compilation are printed.")
    s.append(Config.LINE_SEP)
    s.toString
  }
}

case object CmdAnalyze extends Command("analyze", Analyze) {
  override def usage: String =
    s" [-$name:out=outfile] [-$name:verbose] infile.js ..."
  override def help: String = {
    val s: StringBuilder = new StringBuilder
    s.append("  Analyze the JavaScript source files.")
    s.append(Config.LINE_SEP)
    s.append(s"  If -$name:out=outfile is given, the analysis results will be written to the outfile.")
    s.append(Config.LINE_SEP)
    s.append(s"  If -$name:verbose is given, messages during compilation are printed.")
    s.append(Config.LINE_SEP)
    s.append(s"  If -$name:maxStrSetSize=n is given, the analyzer will use the AbsString Set domain with given size limit n.")
    s.append(Config.LINE_SEP)
    s.toString
  }
}
case object CmdHelp extends Command("help", Help)
