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

import scala.util.parsing.combinator._
// Rename Success and Failure to avoid name conflicts with ParseResult
import scala.util.{ Try, Success => Succ, Failure => Fail }
import kr.ac.kaist.safe.phase.Phase
import kr.ac.kaist.safe.errors.error._

object ArgParse {
  def apply(args: List[String]): Try[(Config, Phase)] = args match {
    case str :: args => Command.cmdMap.get(str) match {
      case Some(cmd) =>
        val config = Config(cmd)
        val phase = cmd.phaseHelper.create
        ArgParser(config, phase, args)
      case None => Fail(NoCmdError(str))
    }
    case _ => Fail(NoInputError())
  }

  // Argument parser by using Scala RegexParsers.
  private object ArgParser extends RegexParsers {
    def apply(config: Config, phase: Phase, args: List[String]): Try[(Config, Phase)] = {
      config.optRegexMap.flatMap(c => {
        phase.optRegexMap.flatMap(p => {
          (c.keySet intersect p.keySet).isEmpty match {
            case true => {
              val success = Succ(())
              val cmd = config.command.toString
              val optMap = c ++ p

              // Basic parsing rules.
              val str = ".*".r ^^ { s => s }
              val optError: Parser[Try[Unit]] = ("-" ~> "[^=]+".r <~ "=") ~ str ^^ { case o ~ s => Fail(NoOptError(o, cmd)) }
              val simpleOptError: Parser[Try[Unit]] = ("-" ~> str) ^^ { o => Fail(NoOptError(o, cmd)) }
              val fileName: Parser[Try[Unit]] = str ^^ { s => config.fileNames = s :: config.fileNames; success }

              // Create a parser.
              val parser: Parser[Try[Unit]] = optMap.foldRight(
                phrase(optError) | phrase(simpleOptError) | phrase(fileName)
              ) {
                  case ((opt, list), prev) => list.foldRight(prev) {
                    case ((optRegex, argRegex, fun), prev) =>
                      lazy val rule: Parser[Try[Unit]] = optRegex ~ argRegex ^^ {
                        case _ ~ s => fun(s)
                      }
                      phrase(rule) | prev
                  }
                }

              // Parsing arguments.
              args.foldLeft[Try[Unit]](success) {
                case (result, arg) => result match {
                  case Succ(_) =>
                    parse(parser, arg).get
                  case fail => fail
                }
              }.map(_ => (config, phase))
            }
            case false => Fail(OptConflictError)
          }
        })
      })
    }
  }
}
