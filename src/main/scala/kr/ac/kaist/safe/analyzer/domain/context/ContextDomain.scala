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

// execution context abstract domain
trait ContextDomain extends AbsDomain[Context] {
  val Empty: Elem
  def apply(
    map: Map[Loc, AbsLexEnv],
    old: OldASiteSet,
    thisBinding: AbsValue
  ): Elem

  // abstract context element
  type Elem <: ElemTrait

  // abstract context element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    // lookup
    def apply(loc: Loc): Option[AbsLexEnv]
    def apply(locSet: Set[Loc]): AbsLexEnv
    def apply(locSet: AbsLoc): AbsLexEnv
    def getOrElse(loc: Loc, default: AbsLexEnv): AbsLexEnv
    def getOrElse[T](loc: Loc)(default: T)(f: AbsLexEnv => T): T

    // context update
    def weakUpdate(loc: Loc, env: AbsLexEnv): Elem
    def update(loc: Loc, env: AbsLexEnv): Elem

    // remove location
    def remove(loc: Loc): Elem

    // substitute locR by locO
    def subsLoc(locR: Recency, locO: Recency): Elem

    def oldify(loc: Loc): Elem

    def domIn(loc: Loc): Boolean

    def setOldASiteSet(old: OldASiteSet): Elem

    def setThisBinding(thisBinding: AbsValue): Elem

    def getMap: Map[Loc, AbsLexEnv]

    def old: OldASiteSet

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
