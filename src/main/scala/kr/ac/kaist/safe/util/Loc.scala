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

package kr.ac.kaist.safe.util

import scala.util.{ Try, Success, Failure }
import kr.ac.kaist.safe.errors.error.{ NoRecencyTag, NoLoc }

// location
case class Loc(address: Address, recency: RecencyTag = Recent) {
  override def toString: String = s"${recency}${address}"
}

object Loc {
  def parse(str: String): Try[Loc] = {
    val pgmPattern = "(#|##)([0-9]+)".r
    val sysPattern = "(#|##)([0-9a-zA-Z.]+)".r
    str match {
      case pgmPattern(prefix, idStr) =>
        RecencyTag.parse(prefix).map(Loc(ProgramAddr(idStr.toInt), _))
      case sysPattern(prefix, name) =>
        RecencyTag.parse(prefix).map(Loc(SystemAddr(name), _))
      case str => Failure(NoLoc(str))
    }
  }
  implicit def ordering[B <: Loc]: Ordering[B] = Ordering.by(_.address)
}

// system location
object SystemLoc {
  def apply(name: String, recency: RecencyTag = Recent): Loc =
    Loc(SystemAddr(name), recency)
}

// recency tag
sealed abstract class RecencyTag(prefix: String) {
  override def toString: String = prefix
}
object RecencyTag {
  def parse(prefix: String): Try[RecencyTag] = prefix match {
    case "#" => Success(Recent)
    case "##" => Success(Old)
    case str => Failure(NoRecencyTag(str))
  }
}

case object Recent extends RecencyTag("#")
case object Old extends RecencyTag("##")
