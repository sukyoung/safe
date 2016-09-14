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
trait AbsLexEnv extends AbsDomain[LexEnv, AbsLexEnv] {
  val normEnv: AbsNormalEnv
  val nullEnv: AbsNullEnv

  // 10.2.2.1 GetIdentifierReference(lex, name, strict) + 8.7.1 GetValue (V)
  def getId(name: String, strict: Boolean)(st: State): (AbsValue, Set[Exception])

  // 10.2.2.1 GetIdentifierReference(lex, name, strict) + 8.7.2 PutValue (V, W)
  // def setId(name: String, value: AbsValue)(st: State): (State, Set[Exception])

  // 10.2.2.2 NewDeclarativeEnvironment(E)
  // def NewDeclarativeEnvironment: NormalEnv

  // 10.2.2.3 NewObjectEnvironment (O, E)
  // XXX: we do not support

  // substitute locR by locO
  def subsLoc(locR: Loc, locO: Loc): AbsLexEnv

  // weak substitute locR by locO
  def weakSubsLoc(locR: Loc, locO: Loc): AbsLexEnv
}
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

    override def toString: String = normEnv.toString

    def getId(name: String, strict: Boolean)(st: State): (AbsValue, Set[Exception]) = {
      var visited = AbsLoc.Bot
      val heap = st.heap
      val ctx = st.context
      var excSet = ExcSetEmpty
      def visit(env: AbsLexEnv): AbsValue = {
        val envRec = env.normEnv.record
        val exists = envRec.HasBinding(name)(heap)

        if (nullEnv.isTop) excSet += ReferenceError

        exists.map[AbsValue](thenV = {
          val (v, e) = envRec.GetBindingValue(name, strict)(heap)
          excSet ++ e
          v
        }, elseV = {
          env.normEnv.outer.foldLeft(AbsValue.Bot) {
            case (v, loc) => {
              val newV =
                if (visited.contains(loc)) AbsValue.Bot
                else {
                  visited += loc
                  visit(ctx.getOrElse(loc, AbsLexEnv.Bot))
                }
              v + newV
            }
          }
        })(AbsValue)
      }
      (visit(this), excSet)
    }
    // def setId(name: String, value: AbsValue)(st: State): (State, Set[Exception])
    // def NewDeclarativeEnvironment: NormalEnv

    def subsLoc(locR: Loc, locO: Loc): AbsLexEnv =
      Dom(normEnv.subsLoc(locR, locO), nullEnv)

    def weakSubsLoc(locR: Loc, locO: Loc): AbsLexEnv =
      Dom(normEnv.weakSubsLoc(locR, locO), nullEnv)
  }
}
