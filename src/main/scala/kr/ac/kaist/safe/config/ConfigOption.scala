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

import scala.util.{ Try, Success, Failure }
import scala.collection.immutable.HashMap
import kr.ac.kaist.safe.phase.OptRegexMap

sealed abstract class OptionKind
case class BoolOption(assign: () => Unit) extends OptionKind
case class NumOption(assign: Int => Unit) extends OptionKind
case class StrOption(assign: String => Unit) extends OptionKind
case class ListOption(assign: List[String] => Unit) extends OptionKind

trait ConfigOption {
  val prefix: String
  val options: List[(String, OptionKind)]
  lazy val optRegexMap: Try[OptRegexMap] = {
    options.foldLeft[Try[OptRegexMap]](Success(HashMap())) {
      case (Success(map), (opt, kind)) if !map.contains(opt) =>
        val name = prefix + opt
        Success(map + (name -> (kind match {
          case BoolOption(ass) => List(
            (("-" + name).r, "".r, (_: String) => Success(ass())),
            (("-" + name + "=").r, ".*".r, (_: String) => Failure(ExtraArgError(name)))
          )
          case NumOption(ass) => List(
            (("-" + name + "=").r, "[0-9]+".r, (s: String) => Success(ass(s.toInt))),
            (("-" + name + "=").r, ".*".r, (_: String) => Failure(NoNumArgError(name))),
            (("-" + name).r, "".r, (_: String) => Failure(NoNumArgError(name)))
          )
          case StrOption(ass) => List(
            (("-" + name + "=").r, ".+".r, (s: String) => Success(ass(s))),
            (("-" + name + "=").r, ".*".r, (_: String) => Failure(NoStrArgError(name))),
            (("-" + name).r, "".r, (_: String) => Failure(NoStrArgError(name)))
          )
          case ListOption(ass) => List(
            (("-" + name + "=").r, "[[^,]+,]+".r, (s: String) => Success(ass(s.split(",").toList))),
            (("-" + name + "=").r, ".*".r, (_: String) => Failure(NoListArgError(name))),
            (("-" + name).r, "".r, (_: String) => Failure(NoListArgError(name)))
          )
        })))
      case (Success(_), (opt, _)) => Failure(OptAlreadyExistError(opt))
      case (fail, _) => fail
    }
  }

  sealed abstract class ConfigOptionError(msg: String) extends Error(msg)
  case class ExtraArgError(name: String) extends ConfigOptionError({
    s"The option '$name' does not need an argument."
  })
  case class NoNumArgError(name: String) extends ConfigOptionError({
    s"The option '$name' needs a number argument."
  })
  case class NoStrArgError(name: String) extends ConfigOptionError({
    s"The option '$name' needs a string argument."
  })
  case class NoListArgError(name: String) extends ConfigOptionError({
    s"The option '$name' needs at least one string argument."
  })
  case class OptAlreadyExistError(name: String) extends ConfigOptionError({
    s"Option conflict: '$name' alreay exists in option list."
  })
}
