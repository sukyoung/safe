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

////////////////////////////////////////////////////////////////////////////////
// concrete lexical environment type
////////////////////////////////////////////////////////////////////////////////
trait LexEnv

////////////////////////////////////////////////////////////////////////////////
// lexical environment abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsLexEnv extends AbsDomain[LexEnv, AbsLexEnv]
trait AbsLexEnvUtil extends AbsDomainUtil[LexEnv, AbsLexEnv] {
  def apply(env: AbsNormalEnv): AbsLexEnv
  def apply(env: AbsNullEnv): AbsLexEnv
}

////////////////////////////////////////////////////////////////////////////////
// default lexical environment abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultLexEnv extends AbsLexEnvUtil {
  lazy val Bot = Dom(AbsNormalEnv.Bot, AbsNullEnv.Bot)
  lazy val Top = Dom(AbsNormalEnv.Top, AbsNullEnv.Top)

  def alpha(env: LexEnv): AbsLexEnv = env match {
    case (env: NormalEnv) => AbsNormalEnv(env)
    case (env: NullEnv) => AbsNullEnv(env)
  }

  def apply(env: AbsNormalEnv): AbsLexEnv = Bot.copy(normEnv = env)
  def apply(env: AbsNullEnv): AbsLexEnv = Bot.copy(nullEnv = env)

  case class Dom(
      normEnv: AbsNormalEnv,
      nullEnv: AbsNullEnv
  ) extends AbsLexEnv {
    def gamma: ConSet[LexEnv] = ConInf() // TODO more precise

    def getSingle: ConSingle[LexEnv] = ConMany() // TODO more precise

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def <=(that: AbsLexEnv): Boolean = {
      val right = check(that)
      this.normEnv <= right.normEnv &&
        this.nullEnv <= right.nullEnv
    }

    def +(that: AbsLexEnv): AbsLexEnv = {
      val right = check(that)
      Dom(
        this.normEnv + right.normEnv,
        this.nullEnv + right.nullEnv
      )
    }

    def <>(that: AbsLexEnv): AbsLexEnv = {
      val right = check(that)
      Dom(
        this.normEnv <> right.normEnv,
        this.nullEnv <> right.nullEnv
      )
    }

    override def toString: String = "" // TODO
  }
}
