/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.util

// Rename Success and Failure to avoid name conflicts with ParseResult
import scala.util.{ Try, Success => Succ, Failure => Fail }
import scala.util.parsing.combinator._
import scala.io.Source
import kr.ac.kaist.safe.{ Safe, Command, SafeConfig }
import kr.ac.kaist.safe.phase.{ PhaseOption, Config }
import kr.ac.kaist.safe.errors.error._
import spray.json._

// Argument parser by using Scala RegexParsers.
class ArgParser(cmd: Command, safeConfig: SafeConfig) extends RegexParsers {
  private val success: Try[Unit] = Succ(())
  var ruleList: List[Parser[Try[Unit]]] = Nil

  var optNameSet: Set[String] = Set()

  addRule(safeConfig, "", Safe.options)

  def addRule[PhaseConfig <: Config](
    config: PhaseConfig,
    prefix: String,
    options: List[PhaseOption[PhaseConfig]]
  ): Try[Unit] = {
    options.foldRight[Try[Unit]](success) {
      case ((opt, kind, _), res) => res.flatMap {
        case _ => {
          val optName = prefix + (if (prefix == "") "" else ":") + opt
          optNameSet(optName) match {
            case true => {
              Fail(OptAlreadyExistError(optName))
            }
            case false => {
              optNameSet += optName
              kind.argRegexList(optName).reverseIterator.foreach {
                case (optRegex, argRegex, fun) =>
                  val cur: Parser[Try[Unit]] = (optRegex) ~> (argRegex) ^^ {
                    case s => fun(config, s)
                  }
                  ruleList ::= cur
              }
              success
            }
          }
        }
      }
    }
  }

  // Parsing arguments.
  def apply(args: List[String]): Try[Unit] = {
    var jsonArgs: List[String] = Nil
    val str = ".*".r ^^ { s => s }

    // add arguments from JSON
    def addArg(prefix: String, value: (String, JsValue)): Unit = value match {
      case (opt, JsBoolean(true)) => jsonArgs ::= s"-$prefix$opt"
      case (opt, JsBoolean(false)) =>
      case (opt, JsNumber(num)) => jsonArgs ::= s"-$prefix$opt=$num"
      case (opt, JsString(str)) if !str.isEmpty => jsonArgs ::= s"-$prefix$opt=$str"
      // TODO case (opt, JsArray(lst)) =>
      case (opt, jsValue) => NoSupportError(jsValue.toString)
    }

    // setting options using a JSON file.
    lazy val json: Parser[Try[Unit]] = ("-config=" ~> str) ^^ {
      case fileName => Try({
        Source.fromFile(fileName)("UTF-8").mkString.parseJson match {
          case (obj: JsObject) => obj.fields.foreach {
            case (phase, value: JsObject) => {
              if (Safe.phases.map(_.name).contains(phase))
                value.fields.foreach(addArg(s"$phase:", _))
              else throw NoPhaseError(phase)
            }
            case ("file", JsArray(lst)) => lst.foreach {
              case JsString(fileName) => jsonArgs ::= fileName
              case value => throw NoFileName(value.toString)
            }
            case ("file", value) => throw NoFileList(value.toString)
            case pair => addArg("", pair)
          }
          case value => throw NoObjError(value.toString)
        }
      })
    }

    // no option error
    lazy val optError: Parser[Try[Unit]] = ("-" ~> "[^=]+".r <~ "=") ~ str ^^ {
      case o ~ s => Fail(NoOptError(o, cmd))
    }
    lazy val simpleOptError: Parser[Try[Unit]] = ("-" ~> str) ^^ {
      o => Fail(NoOptError(o, cmd))
    }

    // a filename list
    lazy val fileName: Parser[Try[Unit]] = str ^^ {
      s => safeConfig.fileNames = s :: safeConfig.fileNames; success
    }

    // Generate a parser.
    val parser: Parser[Try[Unit]] = phrase(json) | ruleList.foldRight(
      phrase(optError) | phrase(simpleOptError) | phrase(fileName)
    ) { case (rule, prev) => phrase(rule) | prev }

    var result = success

    result = args.foldLeft[Try[Unit]](result) {
      case (result, arg) => result.flatMap {
        case _ => {
          var result = parse(parser, arg).get
          result = jsonArgs.foldLeft[Try[Unit]](result) {
            case (result, arg) => result.flatMap {
              case _ => parse(parser, arg).get
            }
          }
          jsonArgs = Nil
          result
        }
      }
    }

    safeConfig.fileNames = safeConfig.fileNames.reverse

    if (safeConfig.fileNames.map(FileKind(_)).exists(_ == HTMLFile))
      safeConfig.html = true

    result
  }
}
