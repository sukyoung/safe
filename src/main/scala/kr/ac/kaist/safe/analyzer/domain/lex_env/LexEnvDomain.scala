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

// lexical environment abstract domain
trait LexEnvDomain extends AbsDomain[LexEnv] {
  def apply(
    record: AbsEnvRec,
    outer: AbsLoc = AbsLoc.Bot,
    nullOuter: AbsAbsent = AbsAbsent.Top
  ): Elem

  // 10.2.2.1 GetIdentifierReference(lex, name, strict)
  // + 8.7 GetBase(V)
  def getIdBase(
    locSet: AbsLoc,
    name: String,
    strict: Boolean
  )(st: AbsState): AbsValue

  // 10.2.2.1 GetIdentifierReference(lex, name, strict)
  // + 8.7.1 GetValue(V)
  def getId(
    locSet: AbsLoc,
    name: String,
    strict: Boolean
  )(st: AbsState): (AbsValue, Set[Exception])

  // 10.2.2.1 GetIdentifierReference(lex, name, strict)
  // + 8.7.2 PutValue(V, W)
  def setId(
    locSet: AbsLoc,
    name: String,
    value: AbsValue,
    strict: Boolean
  )(st: AbsState): (AbsState, Set[Exception])

  // 10.2.2.2 NewDeclarativeEnvironment(E)
  def NewDeclarativeEnvironment(locSet: AbsLoc): Elem

  // 10.2.2.3 NewObjectEnvironment (O, E)
  // XXX: we do not support

  // create new pure-local lexical environment.
  def newPureLocal(locSet: AbsLoc): Elem

  // abstract lexical environment element
  type Elem <: ElemTrait

  // abstract lexical environment element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    val record: AbsEnvRec
    val outer: AbsLoc
    val nullOuter: AbsAbsent

    def copy(
      record: AbsEnvRec = this.record,
      outer: AbsLoc = this.outer,
      nullOuter: AbsAbsent = this.nullOuter
    ): Elem

    // substitute locR by locO
    def subsLoc(locR: Recency, locO: Recency): Elem

    // weak substitute locR by locO
    def weakSubsLoc(locR: Recency, locO: Recency): Elem
  }
}
