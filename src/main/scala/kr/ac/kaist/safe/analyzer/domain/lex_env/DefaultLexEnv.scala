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

import kr.ac.kaist.safe.analyzer.model.GLOBAL_LOC
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.LINE_SEP

import spray.json._
import kr.ac.kaist.safe.nodes.cfg.CFG

// default lexical environment abstract domain
object DefaultLexEnv extends LexEnvDomain {
  lazy val Bot = Elem(AbsEnvRec.Bot, LocSet.Bot, AbsAbsent.Bot)
  lazy val Top = Elem(AbsEnvRec.Top, LocSet.Top, AbsAbsent.Top)

  def alpha(env: LexEnv): Elem = env.outer match {
    case None => Elem(AbsEnvRec(env.record), LocSet.Bot, AbsAbsent.Top)
    case Some(loc) => Elem(AbsEnvRec(env.record), LocSet(loc), AbsAbsent.Bot)
  }

  def apply(
    record: AbsEnvRec,
    outer: LocSet,
    nullOuter: AbsAbsent
  ): Elem = Elem(record, outer, nullOuter)

  case class Elem(
      record: AbsEnvRec,
      outer: LocSet,
      nullOuter: AbsAbsent
  ) extends ElemTrait {
    def gamma: ConSet[LexEnv] = ConInf // TODO more precise

    def getSingle: ConSingle[LexEnv] = ConMany // TODO more precise

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

    def toJSON(implicit uomap: UIdObjMap): JsValue = JsObject(
      "record" -> record.toJSON,
      "outer" -> ((outer.getSingle, nullOuter.isBottom) match {
        case (ConOne(loc), true) => loc.toJSON
        case (ConZero, false) => JsNull
        case _ => fail
      })
    )

    def copy(
      record: AbsEnvRec = this.record,
      outer: LocSet = this.outer,
      nullOuter: AbsAbsent = this.nullOuter
    ): Elem = Elem(record, outer, nullOuter)

    def subsLoc(from: Loc, to: Loc): Elem =
      Elem(record.subsLoc(from, to), outer.subsLoc(from, to), nullOuter)

    def weakSubsLoc(from: Loc, to: Loc): Elem =
      Elem(record.weakSubsLoc(from, to), outer.subsLoc(from, to), nullOuter)

    def remove(locs: Set[Loc]): Elem =
      Elem(record.remove(locs), outer.remove(locs), nullOuter)
  }

  def getIdBase(
    locSet: LocSet,
    name: String,
    strict: Boolean
  )(st: AbsState): AbsValue = {
    var visited = LocSet.Bot
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
            LocSet(loc)
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
    locSet: LocSet,
    name: String,
    strict: Boolean
  )(st: AbsState): (AbsValue, Set[Exception]) = {
    var visited = LocSet.Bot
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
    locSet: LocSet,
    name: String,
    value: AbsValue,
    strict: Boolean
  )(st: AbsState): (AbsState, Set[Exception]) = {
    var visited = LocSet.Bot
    var newH = st.heap
    var newCtx = st.context
    var ctxUpdatePairSet = Set[(Loc, Elem)]()
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
          newH = newH.weakUpdate(GLOBAL_LOC, h.get(GLOBAL_LOC))
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
              newH = newH.weakUpdate(GLOBAL_LOC, h.get(GLOBAL_LOC))
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
    (AbsState(newH, newCtx, st.allocs), excSet)
  }

  def NewDeclarativeEnvironment(outer: LocSet): Elem =
    if (outer.isBottom) Elem(AbsDecEnvRec.Empty, LocSet.Bot, AbsAbsent.Top)
    else Elem(AbsDecEnvRec.Empty, outer, AbsAbsent.Bot)

  def newPureLocal(outer: LocSet): Elem = {
    val envRec = AbsDecEnvRec(Map(
      "@exception" -> (AbsBinding(AbsUndef.Top), AbsAbsent.Bot),
      "@exception_all" -> (AbsBinding(AbsUndef.Top), AbsAbsent.Bot),
      "@return" -> (AbsBinding(AbsUndef.Top), AbsAbsent.Bot)
    ))
    Elem(envRec, outer, AbsAbsent.Bot)
  }

  def fromJSON(json: JsValue, cfg: CFG)(implicit uomap: UIdObjMap): Elem = {
    val fields = json.asJsObject().fields
    Elem(
      AbsEnvRec.fromJSON(fields("record"), cfg),
      LocSet.fromJSON(fields("outer"), cfg),
      AbsAbsent.fromJSON(fields("nullOuter"))
    )
  }
}
