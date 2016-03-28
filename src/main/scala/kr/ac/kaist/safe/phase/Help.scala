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
      "Usage:\n" +
        " parse [-parse:out=outfile] [-parse:verbose] infile.js ...\n" +
        " astRewrite [-astRewrite:out=outfile] [-astRewrite:verbose] infile.js ...\n" +
        " compile [-compile:out=outfile] [-compile:verbose] infile.js ...\n" +
        " cfgBuild [-cfgBuild:out=outfile] [-cfgBuild:verbose] [-cfgBuild:unroll=number] infile.js ...\n" +
        " help\n"
    )

  // TODO auto gen
  // Print help message.
  def printHelpMessage: Unit = {
    Console.err.print(
      "Invoked as script: safe args\n" +
        "Invoked by java: java ... kr.ac.kaist.safe.Safe args\n" +
        "safe parse [-parse:out=outfile] [-parse:verbose] infile.js ...\n" +
        "  Parses files.\n" +
        "  If -parse:out=outfile is given, the parsed AST will be written to the outfile.\n" +
        "  If -parse:verbose is given, messages during parsing are printed.\n" +
        "\n" +
        "safe astRewrite [-astRewrite:out=outfile] [-astRewrite:verbose] infile.js ...\n" +
        "  Rewrites AST in JavaScript source files (hoister, disambiguater, withRewriter).\n" +
        "  If -astRewrite:out=outfile is given, the disambiguated AST will be written to the outfile.\n" +
        "  If -astRewrite:verbose is given, messages during rewriting AST are printed.\n" +
        "\n" +
        "safe compile [-compile:out=outfile] [-compile:verbose] infile.js ...\n" +
        "  Translates JavaScript source files to IR.\n" +
        "  If -compile:out=outfile is given, the resulting IR will be written to the outfile.\n" +
        "  If -compile:verbose is given, messages during compilation are printed.\n" +
        "\n" +
        "safe cfgBuild [-cfgBuild:out=outfile] [-cfgBuild:verbose] [-cfgBuild:unroll=number] infile.js ...\n" +
        "  Builds a control flow graph for JavaScript source files.\n" +
        "  The files are concatenated in the given order before being parsed.\n" +
        "  If -cfgBuild:out=outfile is given, the resulting CFG will be written to the outfile.\n" +
        "  If -cfgBuild:verbose is given, messages during compilation are printed.\n" +
        "  If -cfgBuild:unroll=number is given, the resulting CFG will unroll loops number times.\n" +
        "\n"
    )
  }
}
