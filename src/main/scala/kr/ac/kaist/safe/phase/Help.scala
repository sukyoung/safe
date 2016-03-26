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
        " parse [-out=outfile] [-time] infile.js ...\n" +
        " astRewrite [-out=outfile] infile.js ...\n" +
        " compile [-out=outfile] [-time] infile.js ...\n" +
        " cfg [-out=outfile] infile.js ...\n" +
        " help\n"
    )

  // TODO auto gen
  // Print help message.
  def printHelpMessage: Unit = {
    Console.err.print(
      "Invoked as script: safe args\n" +
        "Invoked by java: java ... kr.ac.kaist.safe.Safe args\n" +
        "safe parse [-out=outfile] [-time] infile.js ...\n" +
        "  Parses files. If parsing succeeds the message \"Ok\" will be printed.\n" +
        "  The files are concatenated in the given order before being parsed.\n" +
        "  If -out=outfile is given, the parsed AST will be written to the outfile.\n" +
        "  If -time is given, the time it takes will be printed.\n" +
        "\n" +
        "safe astRewrite [-out=outfile] infile.js ...\n" +
        "  Rewrites AST in JavaScript source files (hoister, disambiguater, withRewriter).\n" +
        "  The files are concatenated in the given order before being parsed.\n" +
        "  If -out=outfile is given, the disambiguated AST will be written to the outfile.\n" +
        "\n" +
        "safe compile [-out=outfile] [-time] infile.js ...\n" +
        "  Translates JavaScript source files to IR.\n" +
        "  If the compilation succeeds the message \"Ok\" will be printed.\n" +
        "  The files are concatenated in the given order before being parsed.\n" +
        "  If -out=outfile is given, the resulting IR will be written to the outfile.\n" +
        "  If -time is given, the time it takes will be printed.\n" +
        "\n" +
        "safe cfg [-out=outfile] [-unroll=number] somefile.js ...\n" +
        "  Builds a control flow graph for JavaScript source files.\n" +
        "  The files are concatenated in the given order before being parsed.\n" +
        "  If -out=outfile is given, the resulting CFG will be written to the outfile.\n" +
        "  If -unroll=number is given, the resulting CFG will unroll loops number times.\n" +
        "\n"
    )
  }
}
