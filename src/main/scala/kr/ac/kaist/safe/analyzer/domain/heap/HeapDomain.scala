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
import kr.ac.kaist.safe.nodes.cfg.CFGId

// heap abstract domain
trait HeapDomain extends AbsDomain[Heap] {
  def apply(map: Map[Loc, AbsObj], merged: LocSet): Elem

  val Empty: Elem

  // abstract heap element
  type Elem <: ElemTrait

  // abstract heap element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    // lookup
    def get(loc: Loc): AbsObj
    def get(locSet: LocSet): AbsObj

    // heap update
    def weakUpdate(loc: Loc, obj: AbsObj): Elem
    def update(loc: Loc, obj: AbsObj): Elem

    // substitute from by to
    def subsLoc(from: Loc, to: Loc): Elem

    def remove(locs: Set[Loc]): Elem

    def alloc(loc: Loc): Elem

    def getLocSet: LocSet
    def getMerged: LocSet

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
    def protoBase(loc: Loc, absStr: AbsStr): LocSet

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
