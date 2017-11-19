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

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.util._

// value abstract domain
trait ValueDomain extends AbsDomain[Value] {
  def apply(pvalue: AbsPValue): Elem
  def apply(locset: AbsLoc): Elem
  def apply(pvalue: AbsPValue, locset: AbsLoc): Elem

  // abstract value element
  type Elem <: ElemTrait

  // abstract value element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    val pvalue: AbsPValue
    val locset: AbsLoc

    /* substitute locR by locO */
    def subsLoc(locR: Recency, locO: Recency): Elem
    /* weakly substitute locR by locO, that is keep locR together */
    def weakSubsLoc(locR: Recency, locO: Recency): Elem
    // TODO working but a more simple way exists with modifying getBase
    def getThis(h: AbsHeap): AbsLoc

    def typeCount: Int
  }
}
