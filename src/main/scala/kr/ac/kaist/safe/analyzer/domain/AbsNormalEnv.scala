/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.analyzer.domain.Utils._

/* 10.2 Lexical Environments */

////////////////////////////////////////////////////////////////////////////////
// concrete normal lexical environment type
////////////////////////////////////////////////////////////////////////////////
case class NormalEnv(record: EnvRec, outer: Loc) extends LexEnv

////////////////////////////////////////////////////////////////////////////////
// normal lexical environment abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsNormalEnv extends AbsDomain[NormalEnv, AbsNormalEnv] {
  // 10.2.2.1 GetIdentifierReference(lex, name, strict) + 8.7.1 GetValue (V)
  // def getId(name: String)(st: State): (AbsValue, Set[Exception])

  // 10.2.2.1 GetIdentifierReference(lex, name, strict) + 8.7.2 PutValue (V, W)
  // def setId(name: String, value: AbsValue)(st: State): (State, Set[Exception])

  // 10.2.2.2 NewDeclarativeEnvironment(E)
  // def NewDeclarativeEnvironment: NormalEnv

  // 10.2.2.3 NewObjectEnvironment (O, E)
  // XXX: we do not support
}

trait AbsNormalEnvUtil extends AbsDomainUtil[NormalEnv, AbsNormalEnv]

////////////////////////////////////////////////////////////////////////////////
// default normal lexical environment abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultNormalEnv extends AbsNormalEnvUtil {
  lazy val Bot = Dom(AbsEnvRec.Bot, AbsLoc.Bot)
  lazy val Top = Dom(AbsEnvRec.Top, AbsLoc.Top)

  def alpha(env: NormalEnv): AbsNormalEnv =
    Dom(AbsEnvRec(env.record), AbsLoc(env.outer))

  def apply(record: AbsEnvRec, outer: AbsLoc): AbsNormalEnv = Dom(record, outer)

  case class Dom(record: AbsEnvRec, outer: AbsLoc) extends AbsNormalEnv {
    def gamma: ConSet[NormalEnv] = ConInf() // TODO more precise

    def getSingle: ConSingle[NormalEnv] = ConMany() // TODO more precise

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def <=(that: AbsNormalEnv): Boolean = {
      val right = check(that)
      this.record <= right.record &&
        this.outer <= right.outer
    }

    def +(that: AbsNormalEnv): AbsNormalEnv = {
      val right = check(that)
      Dom(
        this.record + right.record,
        this.outer + right.outer
      )
    }

    def <>(that: AbsNormalEnv): AbsNormalEnv = {
      val right = check(that)
      Dom(
        this.record <> right.record,
        this.outer <> right.outer
      )
    }

    override def toString: String = "" // TODO

    // def getId(name: String)(st: State): (AbsValue, Set[Exception])
    // def setId(name: String, value: AbsValue)(st: State): (State, Set[Exception])
    // def NewDeclarativeEnvironment: NormalEnv
  }
}
