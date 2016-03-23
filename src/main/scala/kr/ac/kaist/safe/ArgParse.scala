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

import kr.ac.kaist.safe.proc.{ Procedure }
import scala.util.parsing.combinator._

object ArgParse {
  def apply(args: List[String]): Option[(Config, Procedure)] = args match {
    case str :: args => Command.cmdMap.get(str) match {
      case Some(cmd) =>
        val config = Config(cmd)
        val proc = cmd.procHelper.create
        ArgParser(config, proc, args)
      case None => noCmdError(str)
    }
    case _ => noInputError
  }

  // Argument parser by using Scala RegexParsers.
  private object ArgParser extends RegexParsers {
    def apply(config: Config, proc: Procedure, args: List[String]): Option[(Config, Procedure)] = {
      (config.getOptMap, proc.getOptMap) match {
        case (Some(c), Some(p)) if (c.keySet intersect p.keySet).isEmpty =>
          val success = Some(())
          val cmd = config.command
          val optMap = c ++ p

          // Basic parsing rule.
          val str = ".*".r ^^ { s => s }
          val optError = ("-" ~> "[^=]+".r <~ "=") ~ str ^^ { case o ~ s => noOptError(o, cmd) }
          val simpleOptError = ("-" ~> str) ^^ { o => noOptError(o, cmd) }
          val fileName = str ^^ { s => config.fileNames = s :: config.fileNames; success }

          // Create parser.
          val parser: Parser[Option[Unit]] = optMap.foldRight(
            phrase(optError) | phrase(simpleOptError) | phrase(fileName)
          ) {
              case ((opt, list), prev) => list.foldRight(prev) {
                case ((optRegex, argRegex, fun), prev) =>
                  lazy val rule = optRegex ~ argRegex ^^ {
                    case _ ~ s => fun(s) match {
                      case Some(_) => success
                      case None => None
                    }
                  }
                  phrase(rule) | prev
              }
            }

          // Parsing arguments.
          args.foldLeft[Option[Unit]](success) {
            case (result, arg) => result match {
              case Some(_) => parse(parser, arg).get
              case None => None
            }
          }.map(_ => (config, proc))
        case _ => optConflictError
      }
    }
  }

  // Print error message and return None.
  private def error(msg: String): None.type = {
    Console.err.println(msg)
    None
  }

  // Errors.
  private def noInputError: None.type = error("Please input command.")
  private def noCmdError(str: String): None.type =
    error("Command '" + str + "' does not exists.")
  private def noOptError(str: String, cmd: Command): None.type =
    error("The option '-" + str + "' is not available in the command '" + cmd + "'.")
  private def noOptArgError(opt: String, str: String): None.type =
    error("The option '-" + opt + "' cannot have the value '" + str + "'.")
  private def optConflictError: None.type = error("Config option conflict.")
}
