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

import kr.ac.kaist.safe.nodes.cfg._

// state abstract domain
trait StateDomain extends AbsDomain[State] {
  def apply(heap: AbsHeap, context: AbsContext): Elem

  // abstract state element
  type Elem <: ElemTrait

  // abstract state element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    val heap: AbsHeap
    val context: AbsContext

    def raiseException(excSet: Set[Exception]): Elem
    def oldify(loc: Loc): Elem

    // Lookup
    def lookup(id: CFGId): (AbsValue, Set[Exception])
    def lookupBase(id: CFGId): AbsValue

    // Store
    def varStore(id: CFGId, value: AbsValue): Elem

    // Update location
    def createMutableBinding(id: CFGId, value: AbsValue): Elem

    // delete
    def delete(loc: Loc, str: String): (Elem, AbsBool)

    // toString
    def toStringAll: String
    def toStringLoc(loc: Loc): Option[String]
  }
}
