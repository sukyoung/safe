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

import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinGlobal
import kr.ac.kaist.safe.errors.error.AbsLexEnvParseError
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.LINE_SEP
import scala.collection.immutable.{ HashSet, HashMap }
import spray.json._

// default lexical environment abstract domain
object DefaultLexEnv extends LexEnvDomain {
  lazy val Bot = Elem(AbsEnvRec.Bot, AbsLoc.Bot, AbsAbsent.Bot)
  lazy val Top = Elem(AbsEnvRec.Top, AbsLoc.Top, AbsAbsent.Top)

  def alpha(env: LexEnv): Elem = env.outer match {
    case None => Elem(AbsEnvRec(env.record), AbsLoc.Bot, AbsAbsent.Top)
    case Some(loc) => Elem(AbsEnvRec(env.record), AbsLoc(loc), AbsAbsent.Bot)
  }

  def apply(
    record: AbsEnvRec,
    outer: AbsLoc,
    nullOuter: AbsAbsent
  ): Elem = Elem(record, outer, nullOuter)

  def fromJson(v: JsValue): Elem = v match {
    case JsObject(m) => (
      m.get("record").map(AbsEnvRec.fromJson _),
      m.get("outer").map(AbsLoc.fromJson _),
      m.get("nullOuter").map(AbsAbsent.fromJson _)
    ) match {
        case (Some(r), Some(o), Some(n)) => Elem(r, o, n)
        case _ => throw AbsLexEnvParseError(v)
      }
    case _ => throw AbsLexEnvParseError(v)
  }

  case class Elem(
      record: AbsEnvRec,
      outer: AbsLoc,
      nullOuter: AbsAbsent
  ) extends ElemTrait {
    def gamma: ConSet[LexEnv] = ConInf // TODO more precise

    def getSingle: ConSingle[LexEnv] = ConMany() // TODO more precise

    def ⊑(that: Elem): Boolean = {
      val right = that
      this.record ⊑ right.record &&
        this.outer ⊑ right.outer &&
        this.nullOuter ⊑ right.nullOuter
    }

    def ⊔(that: Elem): Elem = {
      val right = that
      Elem(
        this.record ⊔ right.record,
        this.outer ⊔ right.outer,
        this.nullOuter ⊔ right.nullOuter
      )
    }

    def ⊓(that: Elem): Elem = {
      val right = that
      Elem(
        this.record ⊓ right.record,
        this.outer ⊓ right.outer,
        this.nullOuter ⊓ right.nullOuter
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

    def copy(
      record: AbsEnvRec = this.record,
      outer: AbsLoc = this.outer,
      nullOuter: AbsAbsent = this.nullOuter
    ): Elem = Elem(record, outer, nullOuter)

    def subsLoc(locR: Recency, locO: Recency): Elem =
      Elem(record.subsLoc(locR, locO), outer.subsLoc(locR, locO), nullOuter)

    def weakSubsLoc(locR: Recency, locO: Recency): Elem =
      Elem(record.weakSubsLoc(locR, locO), outer.subsLoc(locR, locO), nullOuter)

    def toJson: JsValue = JsObject(
      ("record", record.toJson),
      ("outer", outer.toJson),
      ("nullOuter", nullOuter.toJson)
    )
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
        val env = ctx.getOrElse(loc, Bot)
        val envRec = env.record
        val exists = envRec.HasBinding(name)(heap)
        val b = exists
        val t: AbsValue =
          if (AT ⊑ b) {
            AbsLoc(loc)
          } else AbsValue.Bot
        val f =
          if (AF ⊑ b) {
            var initV: AbsValue =
              if (env.nullOuter.isTop) AbsUndef.Top
              else AbsValue.Bot
            env.outer.foldLeft(initV) {
              case (v, loc) => v ⊔ visit(loc)
            }
          } else AbsValue.Bot
        t ⊔ f
      }
    }
    locSet.foldLeft(AbsValue.Bot) {
      case (v, loc) => v ⊔ visit(loc)
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
        val env = ctx.getOrElse(loc, Bot)
        val envRec = env.record
        val exists = envRec.HasBinding(name)(heap)
        val b = exists
        val t =
          if (AT ⊑ b) {
            val (v, e) = envRec.GetBindingValue(name, strict)(heap)
            excSet ++= e
            v
          } else AbsValue.Bot
        val f =
          if (AF ⊑ b) {
            if (env.nullOuter.isTop) excSet += ReferenceError
            env.outer.foldLeft(AbsValue.Bot) {
              case (v, loc) => v ⊔ visit(loc)
            }
          } else AbsValue.Bot
        t ⊔ f
      }
    }
    val resV = locSet.foldLeft(AbsValue.Bot) {
      case (v, loc) => v ⊔ visit(loc)
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
    var ctxUpdatePairSet = HashSet[(Loc, Elem)]()
    var excSet = ExcSetEmpty
    def visit(loc: Loc): Unit = if (!visited.contains(loc)) {
      visited += loc
      val env = st.context.getOrElse(loc, Bot)
      val envRec = env.record
      val exists = envRec.HasBinding(name)(st.heap)
      val b = exists
      val t =
        if (AT ⊑ b) {
          val (er, h, e) = envRec.SetMutableBinding(name, value, strict)(st.heap)
          val newEnv = env.copy(record = er)
          ctxUpdatePairSet += ((loc, newEnv))
          newH = newH.weakUpdate(BuiltinGlobal.loc, h.get(BuiltinGlobal.loc))
          excSet ++= e
          AbsAbsent.Bot
        } else AbsAbsent.Bot
      val f =
        if (AF ⊑ b) {
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
        } else AbsAbsent.Bot
      t ⊔ f
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

  def NewDeclarativeEnvironment(outer: AbsLoc): Elem =
    Elem(AbsDecEnvRec.Empty, outer, AbsAbsent.Bot)

  def newPureLocal(outer: AbsLoc): Elem = {
    val envRec = AbsDecEnvRec(HashMap(
      "@exception" -> (AbsBinding(AbsUndef.Top), AbsAbsent.Top),
      "@exception_all" -> (AbsBinding(AbsUndef.Top), AbsAbsent.Top),
      "@return" -> (AbsBinding(AbsUndef.Top), AbsAbsent.Bot)
    ))
    Elem(envRec, outer, AbsAbsent.Bot)
  }
}
