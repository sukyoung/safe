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

import kr.ac.kaist.safe.analyzer.domain.Loc

////////////////////////////////////////////////////////////////////////////////
// recency abstraction
////////////////////////////////////////////////////////////////////////////////
case class Recency(
    loc: Loc,
    recency: RecencyTag
) extends Loc {
  override def toString: String = s"${recency}${loc}"
}
object Recency {
  def apply(name: String, recency: RecencyTag): Recency =
    Recency(PredAllocSite(name), recency)
}

// recency tag
sealed abstract class RecencyTag(prefix: String) {
  override def toString: String = prefix
}
case object Recent extends RecencyTag("R")
case object Old extends RecencyTag("O")
