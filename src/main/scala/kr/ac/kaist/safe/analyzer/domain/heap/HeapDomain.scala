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

// heap abstract domain
trait HeapDomain extends AbsDomain[Heap] {
  def apply(map: Map[Loc, AbsObj]): Elem

  // abstract heap element
  type Elem <: ElemTrait

  // abstract heap element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    // lookup
    def get(loc: Loc): AbsObj
    def get(locSet: AbsLoc): AbsObj

    // heap update
    def weakUpdate(loc: Loc, obj: AbsObj): Elem
    def update(loc: Loc, obj: AbsObj): Elem

    // remove location
    def remove(loc: Loc): Elem

    // substitute locR by locO
    def subsLoc(locR: Recency, locO: Recency): Elem
    def oldify(loc: Loc): Elem
    def domIn(loc: Loc): Boolean

    // toString
    def toStringAll: String
    def toStringLoc(loc: Loc): Option[String]

    // predicates
    def hasConstruct(loc: Loc): AbsBool
    def hasInstance(loc: Loc): AbsBool
    def isArray(loc: Loc): AbsBool
    def isObject(loc: Loc): Boolean
    def canPutVar(x: String): AbsBool

    // proto
    def protoBase(loc: Loc, absStr: AbsStr): AbsLoc

    // store
    def propStore(loc: Loc, absStr: AbsStr, value: AbsValue): Elem

    // update location
    def delete(loc: Loc, absStr: AbsStr): (Elem, AbsBool)

    // get all map
    def getMap: Option[Map[Loc, AbsObj]]

    // location concrete check
    def isConcrete(loc: Loc): Boolean
  }
}
