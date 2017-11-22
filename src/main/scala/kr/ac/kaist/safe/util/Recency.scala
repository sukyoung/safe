/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.util

import kr.ac.kaist.safe.analyzer.domain.Loc
import spray.json._

////////////////////////////////////////////////////////////////////////////////
// recency abstraction
////////////////////////////////////////////////////////////////////////////////
case class Recency(
    loc: Loc,
    recency: RecencyTag = Recent
) extends Loc {
  override def toString: String = s"${recency}${loc}"
  def toJson: JsValue = JsObject(
    ("loc", loc.toJson),
    ("recency", recency.toJson)
  )
}
object Recency {
  def apply(name: String, recency: RecencyTag): Recency =
    Recency(PredAllocSite(name), recency)
}

// recency tag
sealed abstract class RecencyTag(prefix: String) {
  override def toString: String = prefix
  def toJson: JsValue = JsString(prefix)
}
object RecencyTag {
  def fromJson(v: JsValue): Option[RecencyTag] = v match {
    case JsString("R") => Some(Recent)
    case JsString("O") => Some(Old)
    case _ => None
  }
}
case object Recent extends RecencyTag("R")
case object Old extends RecencyTag("O")
