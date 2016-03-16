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
    val addrManager: AddressManager
) {
  val predVars = List(
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

  val predVarsAll = predVars

  val predFuns = List(
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

  val predAll = predVarsAll ++ predFuns

  def predContains(name: String): Boolean =
    predVarsAll.contains(name) || predFuns.contains(name)
}

object Config {
  val MAX_INST_PRINT_SIZE = 10000
  def safeAutoHome(): String = {
    var s = ""
    s = System.getenv("SAFE_HOME")
    if (s == null || s.equals("")) {
      throw new Error("Could not find SAFE_HOME.")
    }
    Useful.windowPathToUnixPath(s)
  }
  val SAFE_HOME = safeAutoHome
  val basedir = searchDef("BASEDIR", "BASEDIR", SAFE_HOME)
  def searchDef(asProp: String, asEnv: String, defaultValue: String): String = {
    var result = System.getProperty(asProp)
    if (result == null) result = System.getenv(asEnv)
    if (result == null) result = defaultValue
    result
  }

  def apply(args: List[String]): Config = {
    var command: String = "usage"
    var possibleOptions: List[String] = Nil
    var opt_OutFileName: String = null
    var opt_Time: Boolean = false
    var opt_unrollingCount: Int = 0
    var FileNames: List[String] = Nil

    val commandMap = Map(
      "usage" -> Nil,
      "parse" -> List("out", "time"),
      "unparse" -> List("out"),
      "astRewrite" -> List("out"),
      "compile" -> List("out", "time"),
      "cfg" -> List("out", "unroll"),
      "help" -> Nil
    )

    val regexCommand = commandMap.keys.foldRight("") { (a: String, b: String) => a + "|" + b }.dropRight(1).r

    object CommandLineArgumentParser extends RegexParsers {

      lazy val cmdParser: Parser[Unit] = phrase(cmd) | phrase(cmdErr)
      lazy val cmd = (regexCommand) ^^ { (s) => command = s; possibleOptions = commandMap(s) }
      lazy val cmdErr = "([a-z0-9])+".r ^^ { (s) => Console.err.println("Error: The command '" + s.toString + "' is not available."); System.exit(1) }

      lazy val number = "[0-9]+".r ^^ { _.toInt }
      lazy val fileName = (".*").r ^^ { (s) => FileNames = s :: FileNames }
      lazy val out = ("-" ~> "out" <~ "=") ~ (".*").r ^^ { case o ~ fn => opt_OutFileName = fn }
      lazy val time = ("-" ~> "time") ^^ { (s) => opt_Time = true }
      lazy val unroll = ("-" ~> "unroll" <~ "=") ~ number ^^ { case s ~ n => opt_unrollingCount = n }
      lazy val optErr = "-".r ~> ".*".r ^^ { (s) => Console.err.println("Error: The option '" + s.toString + "' is not available for the command '" + command + "'."); System.exit(1) }

      lazy val optionMap = Map(
        "out" -> out,
        "time" -> time,
        "unroll" -> unroll,
        "filename" -> fileName
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

    new Config(command, opt_OutFileName, opt_Time, opt_unrollingCount, FileNames, new DefaultAddressManager)
  }
}
