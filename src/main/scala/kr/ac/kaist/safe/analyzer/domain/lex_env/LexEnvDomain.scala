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

// lexical environment abstract domain
trait LexEnvDomain extends AbsDomain[LexEnv] {
  def apply(
    record: AbsEnvRec,
    outer: LocSet = LocSet.Bot,
    nullOuter: AbsAbsent = AbsAbsent.Top
  ): Elem

  // 10.2.2.1 GetIdentifierReference(lex, name, strict)
  // + 8.7 GetBase(V)
  def getIdBase(
    locSet: LocSet,
    name: String,
    strict: Boolean
  )(st: AbsState): AbsValue

  // 10.2.2.1 GetIdentifierReference(lex, name, strict)
  // + 8.7.1 GetValue(V)
  def getId(
    locSet: LocSet,
    name: String,
    strict: Boolean
  )(st: AbsState): (AbsValue, Set[Exception])

  // 10.2.2.1 GetIdentifierReference(lex, name, strict)
  // + 8.7.2 PutValue(V, W)
  def setId(
    locSet: LocSet,
    name: String,
    value: AbsValue,
    strict: Boolean
  )(st: AbsState): (AbsState, Set[Exception])

  // 10.2.2.2 NewDeclarativeEnvironment(E)
  def NewDeclarativeEnvironment(locSet: LocSet): Elem

  // 10.2.2.3 NewObjectEnvironment (O, E)
  // XXX: we do not support

  // create new pure-local lexical environment.
  def newPureLocal(locSet: LocSet): Elem

  // abstract lexical environment element
  type Elem <: ElemTrait

  // abstract lexical environment element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    val record: AbsEnvRec
    val outer: LocSet
    val nullOuter: AbsAbsent

    def copy(
      record: AbsEnvRec = this.record,
      outer: LocSet = this.outer,
      nullOuter: AbsAbsent = this.nullOuter
    ): Elem

    // substitute from by to
    def subsLoc(from: Loc, to: Loc): Elem

    // weak substitute from by to
    def weakSubsLoc(from: Loc, to: Loc): Elem

    // remove locations
    def remove(locs: Set[Loc]): Elem
  }
}
