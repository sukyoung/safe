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

import kr.ac.kaist.safe.exceptions.ParserError
import kr.ac.kaist.safe.exceptions.UserError
import kr.ac.kaist.safe.shell.ParseMain
import kr.ac.kaist.safe.shell.ASTRewriteMain
import kr.ac.kaist.safe.shell.CompileMain
import kr.ac.kaist.safe.shell.CFGMain

object Safe {
  ////////////////////////////////////////////////////////////////////////////////
  // Settings and Environment variables
  ////////////////////////////////////////////////////////////////////////////////
  var debug: Boolean = false
  var printTimeTitle: String = null
  private var startTime: Long = _

  ////////////////////////////////////////////////////////////////////////////////
  // Main Entry point
  ////////////////////////////////////////////////////////////////////////////////
  /**
   * Main entry point for the safe main.
   * In order to support accurate testing of error messages, this method immediately
   * forwards to its two parameter helper method.
   * *** Please do not directly add code to this method, as it will interfere with testing.
   * *** Tests will silently fail.
   * *** Instead, add code to its helper method.
   */
  @throws(classOf[Throwable])
  def main(tokens: Array[String]): Unit = {
    // Call the internal main function
    main(false, tokens);
  }

  /**
   * Helper method that allows main to be called from tests
   * (without having to worry about System.exit).
   */
  @throws(classOf[Throwable])
  def main(runFromTests: Boolean, tokens: Array[String]): Unit = {
    var return_code = -1

    // If there is no parameter then just print a usage message.
    if (tokens.length == 0) printUsageMessage
    else return_code = subMain(tokens)

    // If there is an error and this main function is not called by the test
    //   then call the System.exit function to return the error code.
    if (return_code != 0 && !runFromTests) System.exit(return_code)
  }

  @throws(classOf[Throwable])
  def subMain(tokens: Array[String]): Int = {
    // Now match the assembled string.
    var return_code = 0
    try {
      // Parse parameters
      val config = Config(tokens.toList)
      // Set the start time.
      startTime = System.currentTimeMillis
      if (config.command.equals("parse")) {
        return_code = ParseMain.parse(config)
        if (config.opt_Time) printTimeTitle = "Parsing"
      } else if (config.command.equals("astRewrite")) {
        return_code = ASTRewriteMain.doit(config)
      } else if (config.command.equals("compile")) {
        return_code = CompileMain.doit(config)
        if (config.opt_Time) printTimeTitle = "Compilation"
      } else if (config.command.equals("cfg")) {
        return_code = CFGMain.cfgBuilder(config)
      } else if (config.command.equals("help")) printHelpMessage
    } catch {
      case e: ParserError => System.err.println(e)
      case e: UserError =>
        System.err.println(e)
        return_code = -1
    }
    // Print elapsed time.
    if (printTimeTitle != null)
      println(printTimeTitle + " took " + (System.currentTimeMillis - startTime) + "ms.")
    return_code
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Usage and Help messages
  ////////////////////////////////////////////////////////////////////////////////
  /**
   * Helper method to print usage message.
   */
  private def printUsageMessage: Unit =
    System.err.print(
      "Usage:\n" +
        " parse [-out=outfile] [-time] infile.js ...\n" +
        " astRewrite [-out=outfile] infile.js ...\n" +
        " compile [-out=outfile] [-time] infile.js ...\n" +
        " cfg [-out=outfile] infile.js ...\n" +
        " help\n"
    )

  /**
   * Helper method to print help message.
   */
  private def printHelpMessage: Unit = {
    System.err.print(
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
