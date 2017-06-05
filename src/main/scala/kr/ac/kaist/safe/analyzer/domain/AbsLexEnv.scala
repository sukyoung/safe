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

import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.LINE_SEP
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

  // substitute locR by locO
  def subsLoc(locR: Recency, locO: Recency): AbsLexEnv

  // weak substitute locR by locO
  def weakSubsLoc(locR: Recency, locO: Recency): AbsLexEnv
}

trait AbsLexEnvUtil extends AbsDomainUtil[LexEnv, AbsLexEnv] {
  def apply(
    record: AbsEnvRec,
    outer: AbsLoc = AbsLoc.Bot,
    nullOuter: AbsAbsent = AbsAbsent.Top
  ): AbsLexEnv

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
  def NewDeclarativeEnvironment(locSet: AbsLoc): AbsLexEnv

  // 10.2.2.3 NewObjectEnvironment (O, E)
  // XXX: we do not support

  // create new pure-local lexical environment.
  def newPureLocal(locSet: AbsLoc): AbsLexEnv
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
      val s = new StringBuilder
      var lst: List[String] = Nil
      s.append(record).append(LINE_SEP)
      if (!outer.isBottom) lst ::= outer.toString
      if (!nullOuter.isBottom) lst ::= "null"
      s.append("* Outer: ").append(lst.mkString(", "))
      s.toString
    }

    def copyWith(
      record: AbsEnvRec = this.record,
      outer: AbsLoc = this.outer,
      nullOuter: AbsAbsent = this.nullOuter
    ): AbsLexEnv = Dom(record, outer, nullOuter)

    def subsLoc(locR: Recency, locO: Recency): AbsLexEnv =
      Dom(record.subsLoc(locR, locO), outer.subsLoc(locR, locO), nullOuter)

    def weakSubsLoc(locR: Recency, locO: Recency): AbsLexEnv =
      Dom(record.weakSubsLoc(locR, locO), outer.subsLoc(locR, locO), nullOuter)
  }

  def getIdBase(
    locSet: AbsLoc,
    name: String,
    strict: Boolean
  )(st: AbsState): AbsValue = {
    var visited = AbsLoc.Bot
    val heap = st.heap
    val ctx = st.context
    def visit(loc: Loc): AbsValue = {
      if (visited.contains(loc)) AbsValue.Bot
      else {
        visited += loc
        val env = ctx.getOrElse(loc, AbsLexEnv.Bot)
        val envRec = env.record
        val exists = envRec.HasBinding(name)(heap)
        exists.map[AbsValue](thenV = {
          AbsLoc(loc)
        }, elseV = {
          var initV: AbsValue =
            if (env.nullOuter.isTop) AbsUndef.Top
            else AbsValue.Bot
          env.outer.foldLeft(initV) {
            case (v, loc) => v + visit(loc)
          }
        })(AbsValue)
      }
    }
    locSet.foldLeft(AbsValue.Bot) {
      case (v, loc) => v + visit(loc)
    }
  }

  def getId(
    locSet: AbsLoc,
    name: String,
    strict: Boolean
  )(st: AbsState): (AbsValue, Set[Exception]) = {
    var visited = AbsLoc.Bot
    val heap = st.heap
    val ctx = st.context
    var excSet = ExcSetEmpty
    def visit(loc: Loc): AbsValue = {
      if (visited.contains(loc)) AbsValue.Bot
      else {
        visited += loc
        val env = ctx.getOrElse(loc, AbsLexEnv.Bot)
        val envRec = env.record
        val exists = envRec.HasBinding(name)(heap)
        exists.map[AbsValue](thenV = {
          val (v, e) = envRec.GetBindingValue(name, strict)(heap)
          excSet ++= e
          v
        }, elseV = {
          if (env.nullOuter.isTop) excSet += ReferenceError
          env.outer.foldLeft(AbsValue.Bot) {
            case (v, loc) => v + visit(loc)
          }
        })(AbsValue)
      }
    }
    val resV = locSet.foldLeft(AbsValue.Bot) {
      case (v, loc) => v + visit(loc)
    }
    (resV, excSet)
  }

  def setId(
    locSet: AbsLoc,
    name: String,
    value: AbsValue,
    strict: Boolean
  )(st: AbsState): (AbsState, Set[Exception]) = {
    var visited = AbsLoc.Bot
    var newH = st.heap
    var newCtx = st.context
    var ctxUpdatePairSet = HashSet[(Loc, AbsLexEnv)]()
    var excSet = ExcSetEmpty
    def visit(loc: Loc): Unit = if (!visited.contains(loc)) {
      visited += loc
      val env = st.context.getOrElse(loc, AbsLexEnv.Bot)
      val envRec = env.record
      val exists = envRec.HasBinding(name)(st.heap)
      exists.map[AbsAbsent](thenV = {
        val (er, h, e) = envRec.SetMutableBinding(name, value, strict)(st.heap)
        val newEnv = env.copyWith(record = er)
        ctxUpdatePairSet += ((loc, newEnv))
        newH = newH.weakUpdate(BuiltinGlobal.loc, h.get(BuiltinGlobal.loc))
        excSet ++= e
        AbsAbsent.Bot
      }, elseV = {
        if (env.nullOuter.isTop) {
          if (strict) excSet += ReferenceError
          else {
            val (_, h, e) = AbsGlobalEnvRec.Top
              .SetMutableBinding(name, value, false)(newH)
            newH = newH.weakUpdate(BuiltinGlobal.loc, h.get(BuiltinGlobal.loc))
            excSet ++= e
          }
        }
        env.outer.foreach(visit(_))
        AbsAbsent.Bot
      })(AbsAbsent)
    }
    locSet.foreach(visit(_))
    ctxUpdatePairSet.size match {
      case 1 => ctxUpdatePairSet.foreach {
        case (loc, newEnv) => newCtx = newCtx.update(loc, newEnv)
      }
      case _ => ctxUpdatePairSet.foreach {
        case (loc, newEnv) => newCtx = newCtx.weakUpdate(loc, newEnv)
      }
    }
    (AbsState(newH, newCtx), excSet)
  }

  def NewDeclarativeEnvironment(outer: AbsLoc): AbsLexEnv =
    Dom(AbsDecEnvRec.Empty, outer, AbsAbsent.Bot)

  def newPureLocal(outer: AbsLoc): AbsLexEnv = {
    val envRec = AbsDecEnvRec(HashMap(
      "@exception" -> (AbsBinding(AbsUndef.Top), AbsAbsent.Top),
      "@exception_all" -> (AbsBinding(AbsUndef.Top), AbsAbsent.Top),
      "@return" -> (AbsBinding(AbsUndef.Top), AbsAbsent.Bot)
    ))
    Dom(envRec, outer, AbsAbsent.Bot)
  }
}
