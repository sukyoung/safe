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

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.util._

// value abstract domain
trait ValueDomain extends AbsDomain[Value] {
  def apply(pvalue: AbsPValue): Elem
  def apply(locset: LocSet): Elem
  def apply(pvalue: AbsPValue, locset: LocSet): Elem

  // abstract value element
  type Elem <: ElemTrait

  // abstract value element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    val pvalue: AbsPValue
    val locset: LocSet

    /* substitute from by to */
    def subsLoc(from: Loc, to: Loc): Elem
    /* weakly substitute from by to, that is keep from together */
    def weakSubsLoc(from: Loc, to: Loc): Elem
    /* remove locations */
    def remove(locs: Set[Loc]): Elem

    // TODO working but a more simple way exists with modifying getBase
    def getThis(h: AbsHeap): LocSet

    def typeCount: Int
  }
}
