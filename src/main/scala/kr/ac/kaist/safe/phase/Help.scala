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

package kr.ac.kaist.safe.phase

import kr.ac.kaist.safe.config.Config

// Help phase struct.
case class Help() extends Phase(None, None) {
  override def apply(config: Config): Unit = Help.printHelpMessage
}

// Help phase helper.
object Help extends PhaseHelper {
  def create: Help = Help()

  // TODO auto gen
  // Print usage message.
  def printUsageMessage: Unit =
    Console.err.print(
      "Usage:" + Config.LINE_SEP +
        " parse infile.js ..." + Config.LINE_SEP +
        " astRewrite [-astRewrite:out=outfile] [-astRewrite:verbose] infile.js ..." + Config.LINE_SEP +
        " compile [-compile:out=outfile] [-compile:verbose] infile.js ..." + Config.LINE_SEP +
        " cfgBuild [-cfgBuild:out=outfile] [-cfgBuild:verbose] infile.js ..." + Config.LINE_SEP +
        " help" + Config.LINE_SEP
    )

  // TODO auto gen
  // Print help message.
  def printHelpMessage: Unit = {
    Console.err.print(
      "Invoked as script: safe args" + Config.LINE_SEP +
        "Invoked by java: java ... kr.ac.kaist.safe.Safe args" + Config.LINE_SEP +
        "safe parse infile.js ..." + Config.LINE_SEP +
        "  Parses files." + Config.LINE_SEP +
        Config.LINE_SEP +
        "safe astRewrite [-astRewrite:out=outfile] [-astRewrite:verbose] infile.js ..." + Config.LINE_SEP +
        "  Rewrites AST in JavaScript source files (hoister, disambiguater, withRewriter)." + Config.LINE_SEP +
        "  If -astRewrite:out=outfile is given, the disambiguated AST will be written to the outfile." + Config.LINE_SEP +
        "  If -astRewrite:verbose is given, messages during rewriting AST are printed." + Config.LINE_SEP +
        Config.LINE_SEP +
        "safe compile [-compile:out=outfile] [-compile:verbose] infile.js ..." + Config.LINE_SEP +
        "  Translates JavaScript source files to IR." + Config.LINE_SEP +
        "  If -compile:out=outfile is given, the resulting IR will be written to the outfile." + Config.LINE_SEP +
        "  If -compile:verbose is given, messages during compilation are printed." + Config.LINE_SEP +
        Config.LINE_SEP +
        "safe cfgBuild [-cfgBuild:out=outfile] [-cfgBuild:verbose] infile.js ..." + Config.LINE_SEP +
        "  Builds a control flow graph for JavaScript source files." + Config.LINE_SEP +
        "  The files are concatenated in the given order before being parsed." + Config.LINE_SEP +
        "  If -cfgBuild:out=outfile is given, the resulting CFG will be written to the outfile." + Config.LINE_SEP +
        "  If -cfgBuild:verbose is given, messages during compilation are printed." + Config.LINE_SEP +
        Config.LINE_SEP
    )
  }
}
