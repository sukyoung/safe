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

// execution context abstract domain
trait ContextDomain extends AbsDomain[Context] {
  val Empty: Elem
  def apply(
    map: Map[Loc, AbsLexEnv],
    merged: LocSet,
    thisBinding: AbsValue
  ): Elem

  // abstract context element
  type Elem <: ElemTrait

  // abstract context element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    // lookup
    def apply(loc: Loc): Option[AbsLexEnv]
    def apply(locSet: Set[Loc]): AbsLexEnv
    def apply(locSet: LocSet): AbsLexEnv
    def getOrElse(loc: Loc, default: AbsLexEnv): AbsLexEnv
    def getOrElse[T](loc: Loc)(default: T)(f: AbsLexEnv => T): T

    // context update
    def weakUpdate(loc: Loc, env: AbsLexEnv): Elem
    def update(loc: Loc, env: AbsLexEnv): Elem

    // substitute location
    def subsLoc(from: Loc, to: Loc): Elem

    def remove(locs: Set[Loc]): Elem

    def alloc(loc: Loc): Elem

    def getLocSet: LocSet
    def getMerged: LocSet

    def domIn(loc: Loc): Boolean

    def setThisBinding(thisBinding: AbsValue): Elem

    def getMap: Map[Loc, AbsLexEnv]

    def thisBinding: AbsValue

    def toStringLoc(loc: Loc): Option[String]

    // delete
    def delete(loc: Loc, str: String): (Elem, AbsBool)

    // pure local environment
    def pureLocal: AbsLexEnv
    def subsPureLocal(env: AbsLexEnv): Elem

    // location concrete check
    def isConcrete(loc: Loc): Boolean
  }
}
