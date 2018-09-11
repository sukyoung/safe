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

import kr.ac.kaist.safe.nodes.cfg.Call
import kr.ac.kaist.safe.analyzer.domain.Loc

////////////////////////////////////////////////////////////////////////////////
// allocation callsite abstraction
////////////////////////////////////////////////////////////////////////////////
case class AllocCallSite(
    loc: Loc,
    calls: List[Call]
) extends Loc {
  override def toString: String = s"${loc}:ACS[${calls.mkString(",")}]"
}
object AllocCallSite {
  def apply(name: String, calls: List[Call]): AllocCallSite =
    AllocCallSite(PredAllocSite(name), calls)
}
