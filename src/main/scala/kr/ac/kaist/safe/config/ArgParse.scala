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
// Rename Success and Failure to avoid name conflicts with ParseResult
import scala.util.{ Try, Success => Succ, Failure => Fail }

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
      (config.optRegexMap, phase.optRegexMap) match {
        case (Succ(c), Succ(p)) if (c.keySet intersect p.keySet).isEmpty =>
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
                parse(parser, arg).getOrElse(Fail(NoExhaustiveError()))
              case fail => fail
            }
          }.map(_ => (config, phase))
        case (Succ(_), Succ(_)) => Fail(OptConflictError())
        case (Fail(f), _) => Fail(f)
        case (_, Fail(f)) => Fail(f)
      }
    }
  }

  sealed abstract class ArgParseError(msg: String) extends Error(msg)
  case class NoCmdError(str: String) extends ArgParseError({
    s"Command '$str' does not exist."
  })
  case class NoInputError() extends ArgParseError("Please input a command.")
  case class NoOptError(str: String, cmd: String) extends ArgParseError({
    s"The option '-$str' is not available for the command '$cmd'."
  })
  case class NoOptArgError(opt: String, str: String) extends ArgParseError({
    s"The option '-$opt' cannot have the value '$str'."
  })
  case class OptConflictError() extends ArgParseError("Config option conflict.")
  case class NoExhaustiveError() extends ArgParseError({
    "Argument parser is not exhaustive."
  })
}
