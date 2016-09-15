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
import scala.collection.immutable.{ HashSet, HashMap }

/* 10.2 Lexical Environments */

////////////////////////////////////////////////////////////////////////////////
// concrete lexical environment type
////////////////////////////////////////////////////////////////////////////////
case class LexEnv(record: EnvRec, outer: Option[Loc])

////////////////////////////////////////////////////////////////////////////////
// lexical environment abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsLexEnv extends AbsDomain[LexEnv, AbsLexEnv] {
  val record: AbsEnvRec
  val outer: AbsLoc
  val nullOuter: AbsAbsent

  def copyWith(
    record: AbsEnvRec = this.record,
    outer: AbsLoc = this.outer,
    nullOuter: AbsAbsent = this.nullOuter
  ): AbsLexEnv

  // 10.2.2.1 GetIdentifierReference(lex, name, strict) + 8.7.1 GetValue (V)
  def getId(name: String, strict: Boolean)(st: State): (AbsValue, Set[Exception])

  // 10.2.2.1 GetIdentifierReference(lex, name, strict) + 8.7.2 PutValue (V, W)
  // def setId(name: String, value: AbsValue)(st: State): (State, Set[Exception])

  // substitute locR by locO
  def subsLoc(locR: Loc, locO: Loc): AbsLexEnv

  // weak substitute locR by locO
  def weakSubsLoc(locR: Loc, locO: Loc): AbsLexEnv
}

trait AbsLexEnvUtil extends AbsDomainUtil[LexEnv, AbsLexEnv] {
  def apply(
    record: AbsEnvRec,
    outer: AbsLoc = AbsLoc.Bot,
    nullOuter: AbsAbsent = AbsAbsent.Top
  ): AbsLexEnv

  // 10.2.2.2 NewDeclarativeEnvironment(E)
  def NewDeclarativeEnvironment(locSet: AbsLoc): AbsLexEnv

  // 10.2.2.3 NewObjectEnvironment (O, E)
  // XXX: we do not support

  // create new pure-local lexical environment.
  def newPureLocal(locSet: AbsLoc, thisLocSet: AbsLoc): AbsLexEnv
}

////////////////////////////////////////////////////////////////////////////////
// default lexical environment abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultLexEnv extends AbsLexEnvUtil {
  lazy val Bot = Dom(AbsEnvRec.Bot, AbsLoc.Bot, AbsAbsent.Bot)
  lazy val Top = Dom(AbsEnvRec.Top, AbsLoc.Top, AbsAbsent.Top)

  def alpha(env: LexEnv): AbsLexEnv = env.outer match {
    case None => Dom(AbsEnvRec(env.record), AbsLoc.Bot, AbsAbsent.Top)
    case Some(loc) => Dom(AbsEnvRec(env.record), AbsLoc(loc), AbsAbsent.Bot)
  }

  def apply(
    record: AbsEnvRec,
    outer: AbsLoc,
    nullOuter: AbsAbsent
  ): AbsLexEnv = Dom(record, outer, nullOuter)

  case class Dom(
      record: AbsEnvRec,
      outer: AbsLoc,
      nullOuter: AbsAbsent
  ) extends AbsLexEnv {
    def gamma: ConSet[LexEnv] = ConInf() // TODO more precise

    def getSingle: ConSingle[LexEnv] = ConMany() // TODO more precise

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def <=(that: AbsLexEnv): Boolean = {
      val right = check(that)
      this.record <= right.record &&
        this.outer <= right.outer &&
        this.nullOuter <= right.nullOuter
    }

    def +(that: AbsLexEnv): AbsLexEnv = {
      val right = check(that)
      Dom(
        this.record + right.record,
        this.outer + right.outer,
        this.nullOuter + right.nullOuter
      )
    }

    def <>(that: AbsLexEnv): AbsLexEnv = {
      val right = check(that)
      Dom(
        this.record <> right.record,
        this.outer <> right.outer,
        this.nullOuter <> right.nullOuter
      )
    }

    override def toString: String = {
      record.toString +
        s"outer: $outer, $nullOuter"
    }

    def copyWith(
      record: AbsEnvRec = this.record,
      outer: AbsLoc = this.outer,
      nullOuter: AbsAbsent = this.nullOuter
    ): AbsLexEnv = Dom(record, outer, nullOuter)

    def getId(name: String, strict: Boolean)(st: State): (AbsValue, Set[Exception]) = {
      var visited = AbsLoc.Bot
      val heap = st.heap
      val ctx = st.context
      var excSet = ExcSetEmpty
      def visit(env: AbsLexEnv): AbsValue = {
        val envRec = env.record
        val exists = envRec.HasBinding(name)(heap)

        if (nullOuter.isTop) excSet += ReferenceError

        exists.map[AbsValue](thenV = {
          val (v, e) = envRec.GetBindingValue(name, strict)(heap)
          excSet ++ e
          v
        }, elseV = {
          env.outer.foldLeft(AbsValue.Bot) {
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

    // TODO
    // def setId(name: String, value: AbsValue)(st: State): (State, Set[Exception])

    def subsLoc(locR: Loc, locO: Loc): AbsLexEnv =
      Dom(record.subsLoc(locR, locO), outer.subsLoc(locR, locO), nullOuter)

    def weakSubsLoc(locR: Loc, locO: Loc): AbsLexEnv =
      Dom(record.weakSubsLoc(locR, locO), outer.subsLoc(locR, locO), nullOuter)
  }

  def NewDeclarativeEnvironment(outer: AbsLoc): AbsLexEnv =
    Dom(AbsDecEnvRec.Empty, outer, AbsAbsent.Bot)

  def newPureLocal(outer: AbsLoc, thisLocSet: AbsLoc): AbsLexEnv = {
    val envRec = AbsDecEnvRec(HashMap(
      "@this" -> (AbsBinding(thisLocSet), AbsAbsent.Bot),
      "@exception" -> (AbsBinding(AbsUndef.Top), AbsAbsent.Top),
      "@exception_all" -> (AbsBinding(AbsUndef.Top), AbsAbsent.Top),
      "@return" -> (AbsBinding(AbsUndef.Top), AbsAbsent.Bot)
    ))
    Dom(envRec, outer, AbsAbsent.Bot)
  }
}
