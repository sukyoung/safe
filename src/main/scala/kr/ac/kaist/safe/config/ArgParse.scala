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

import kr.ac.kaist.safe.phase.Phase
import scala.util.parsing.combinator._

object ArgParse {
  def apply(args: List[String]): Option[(Config, Phase)] = args match {
    case str :: args => Command.cmdMap.get(str) match {
      case Some(cmd) =>
        val config = Config(cmd)
        val phase = cmd.phaseHelper.create
        ArgParser(config, phase, args)
      case None => noCmdError(str)
    }
    case _ => noInputError
  }

  // Argument parser by using Scala RegexParsers.
  private object ArgParser extends RegexParsers {
    def apply(config: Config, phase: Phase, args: List[String]): Option[(Config, Phase)] = {
      (config.getOptMap, phase.getOptMap) match {
        case (Some(c), Some(p)) if (c.keySet intersect p.keySet).isEmpty =>
          val success = Some(())
          val cmd = config.command
          val optMap = c ++ p

          // Basic parsing rules.
          val str = ".*".r ^^ { s => s }
          val optError = ("-" ~> "[^=]+".r <~ "=") ~ str ^^ { case o ~ s => noOptError(o, cmd) }
          val simpleOptError = ("-" ~> str) ^^ { o => noOptError(o, cmd) }
          val fileName = str ^^ { s => config.fileNames = s :: config.fileNames; success }

          // Create a parser.
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
          }.map(_ => (config, phase))
        case _ => optConflictError
      }
    }
  }

  // Print an error message and return None.
  private def error(msg: String): None.type = {
    Console.err.println(msg)
    None
  }

  // Errors.
  private def noInputError: None.type = error("Please input a command.")
  private def noCmdError(str: String): None.type =
    error("Command '" + str + "' does not exist.")
  private def noOptError(str: String, cmd: Command): None.type =
    error("The option '-" + str + "' is not available for the command '" + cmd + "'.")
  private def noOptArgError(opt: String, str: String): None.type =
    error("The option '-" + opt + "' cannot have the value '" + str + "'.")
  private def optConflictError: None.type = error("Config option conflict.")
}
