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
import scala.collection.immutable.HashSet

/* 10.2 Lexical Environments */

////////////////////////////////////////////////////////////////////////////////
// concrete normal lexical environment type
////////////////////////////////////////////////////////////////////////////////
case class NormalEnv(record: EnvRec, outer: Loc) extends LexEnv

////////////////////////////////////////////////////////////////////////////////
// normal lexical environment abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsNormalEnv extends AbsDomain[NormalEnv, AbsNormalEnv] {
  val record: AbsEnvRec
  val outer: AbsLoc

  // substitute locR by locO
  def subsLoc(locR: Loc, locO: Loc): AbsNormalEnv

  // weak substitute locR by locO
  def weakSubsLoc(locR: Loc, locO: Loc): AbsNormalEnv
}

trait AbsNormalEnvUtil extends AbsDomainUtil[NormalEnv, AbsNormalEnv] {
  def apply(
    record: AbsEnvRec,
    outer: AbsLoc = AbsLoc.Bot // TODO delete default input
  ): AbsNormalEnv
}

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

    def subsLoc(locR: Loc, locO: Loc): AbsNormalEnv =
      Dom(record.subsLoc(locR, locO), outer.subsLoc(locR, locO))

    def weakSubsLoc(locR: Loc, locO: Loc): AbsNormalEnv =
      Dom(record.weakSubsLoc(locR, locO), outer.subsLoc(locR, locO))
  }
}
