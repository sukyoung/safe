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

import scala.util.{ Success, Failure }
import kr.ac.kaist.safe.phase.{ Config, ArgRegex }
import kr.ac.kaist.safe.errors.error._

sealed abstract class OptionKind[PhaseConfig <: Config] {
  def argRegexList(name: String): List[ArgRegex[PhaseConfig]]
  def postfix: String
}

case class BoolOption[PhaseConfig <: Config](
    assign: PhaseConfig => Unit
) extends OptionKind[PhaseConfig] {
  def argRegexList(name: String): List[ArgRegex[PhaseConfig]] = List(
    (("-" + name).r, "".r, (c, _) => Success(assign(c))),
    (("-" + name + "=").r, ".*".r, (c, _) => Failure(ExtraArgError(name)))
  )
  def postfix: String = ""
}

case class NumOption[PhaseConfig <: Config](
    assign: (PhaseConfig, Int) => Unit
) extends OptionKind[PhaseConfig] {
  def argRegexList(name: String): List[ArgRegex[PhaseConfig]] = List(
    (("-" + name + "=").r, "[0-9]+".r, (c, s) => Success(assign(c, s.toInt))),
    (("-" + name + "=").r, ".*".r, (_, _) => Failure(NoNumArgError(name))),
    (("-" + name).r, "".r, (_, _) => Failure(NoNumArgError(name)))
  )
  def postfix: String = "={number}"
}

case class StrOption[PhaseConfig <: Config](
    assign: (PhaseConfig, String) => Unit
) extends OptionKind[PhaseConfig] {
  def argRegexList(name: String): List[ArgRegex[PhaseConfig]] = List(
    (("-" + name + "=").r, ".+".r, (c, s) => Success(assign(c, s))),
    (("-" + name + "=").r, ".*".r, (_, _) => Failure(NoStrArgError(name))),
    (("-" + name).r, "".r, (_, _) => Failure(NoStrArgError(name)))
  )
  def postfix: String = "={string}"
}

case class ListOption[PhaseConfig <: Config](
    assign: (PhaseConfig, List[String]) => Unit
) extends OptionKind[PhaseConfig] {
  def argRegexList(name: String): List[ArgRegex[PhaseConfig]] = List(
    (("-" + name + "=").r, "[[^,]+,]+".r, (c, s) => Success(assign(c, s.split(",").toList))),
    (("-" + name + "=").r, ".*".r, (_, _) => Failure(NoListArgError(name))),
    (("-" + name).r, "".r, (_, _) => Failure(NoListArgError(name)))
  )
  def postfix: String = "={string1},{string2},..."
}

