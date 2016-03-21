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

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import scala.util.parsing.combinator._
import kr.ac.kaist.safe.safe_util.{ NodeUtil => NU, AddressManager, DefaultAddressManager }
import kr.ac.kaist.safe.useful.Useful

class Config(
    val command: String,
    val opt_OutFileName: String,
    val opt_Time: Boolean,
    val opt_unrollingCount: Int,
    val FileNames: List[String],
    val opt_Verbose: Boolean,
    val addrManager: AddressManager
) {
}

object Config {
  def apply(args: List[String]): Config = {
    var command: String = "usage"
    var possibleOptions: List[String] = Nil
    var opt_OutFileName: String = null
    var opt_Time: Boolean = false
    var opt_Verbose: Boolean = false
    var opt_unrollingCount: Int = 0
    var FileNames: List[String] = Nil

    val commandMap = Map(
      "usage" -> Nil,
      "parse" -> List("verbose", "out", "time"),
      "unparse" -> List("verbose", "out"),
      "astRewrite" -> List("verbose", "out"),
      "compile" -> List("verbose", "out", "time"),
      "cfg" -> List("verbose", "out", "unroll"),
      "help" -> Nil
    )

    val regexCommand = commandMap.keys.foldRight("") { (a: String, b: String) => a + "|" + b }.dropRight(1).r

    object CommandLineArgumentParser extends RegexParsers {

      lazy val cmdParser: Parser[Unit] = phrase(cmd) | phrase(cmdErr)
      lazy val cmd = (regexCommand) ^^ { (s) => command = s; possibleOptions = commandMap(s) }
      lazy val cmdErr = "([a-z0-9])+".r ^^ { (s) => Console.err.println("Error: The command '" + s.toString + "' is not available."); System.exit(1) }

      lazy val number = "[0-9]+".r ^^ { _.toInt }
      lazy val fileName = (".*").r ^^ { (s) => FileNames = s :: FileNames }
      lazy val verbose = ("-" ~> "verbose") ^^ { (s) => opt_Verbose = true }
      lazy val out = ("-" ~> "out" <~ "=") ~ (".*").r ^^ { case o ~ fn => opt_OutFileName = fn }
      lazy val time = ("-" ~> "time") ^^ { (s) => opt_Time = true }
      lazy val unroll = ("-" ~> "unroll" <~ "=") ~ number ^^ { case s ~ n => opt_unrollingCount = n }
      lazy val optErr = "-".r ~> ".*".r ^^ { (s) => Console.err.println("Error: The option '" + s.toString + "' is not available for the command '" + command + "'."); System.exit(1) }

      lazy val optionMap = Map(
        "out" -> out,
        "time" -> time,
        "unroll" -> unroll,
        "filename" -> fileName,
        "verbose" -> verbose
      )

      def getArgument(args: List[String]): Unit = {
        parse(cmdParser, args(0)) getOrElse null

        val optParser = if (!command.equals("usage"))
          (possibleOptions.foldRight(phrase(optErr)) { (a: String, b: Parser[Unit]) => phrase(optionMap(a)) | b }) | phrase(fileName)
        else
          (optionMap.keys.foldRight(phrase(optErr)) { (a: String, b: Parser[Unit]) => phrase(optionMap(a)) | b }) | phrase(fileName)
        for (optString <- args.tail) parse(optParser, optString)
      }
    }

    CommandLineArgumentParser.getArgument(args)

    new Config(command, opt_OutFileName, opt_Time, opt_unrollingCount, FileNames, opt_Verbose, new DefaultAddressManager)
  }

  ////////////////////////////////////////////////////////////////
  // Global Value
  ////////////////////////////////////////////////////////////////

  // Maximum length of printable instruction of CFGNode
  val MAX_INST_PRINT_SIZE = 10000

  // Base project directory root
  val BASE_DIR = System.getenv("SAFE_HOME") match {
    case null | "" => throw new Error("Could not find SAFE_HOME.")
    case s => s
  }

  // Predefined variables
  val PRED_VARS = List(
    // 4.2 Language Overview
    "Object",
    "Function",
    "Array",
    "String",
    "Boolean",
    "Number",
    "Math",
    "Date",
    "RegExp",
    "JSON",
    "Error",
    "EvalError",
    "RangeError",
    "ReferenceError",
    "SyntaxError",
    "TypeError",
    "URIError",
    // 15.1.1 Value Properties of the Global Object
    "NaN",
    "Infinity",
    "undefined",
    // predefined constant variables from IR
    NU.varTrue,
    NU.varOne,
    NU.freshGlobalName("global")
  )

  // Predefined functions
  val PRED_FUNS = List(
    // 15.1.2 Function Properties of the Global Object
    "eval",
    "parseInt",
    "parseFloat",
    "isNaN",
    "isFinite",
    // 15.1.3 URI Handling Function Properties
    "decodeURI",
    "decodeURIComponent",
    "encodeURI",
    "encodeURIComponent"
  )

  // All predefined variables and functions
  val PRED_ALL = PRED_VARS ++ PRED_FUNS
}
