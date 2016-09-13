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

////////////////////////////////////////////////////////////////////////////////
// concrete null environment type
////////////////////////////////////////////////////////////////////////////////
abstract class NullEnv extends LexEnv
case object NullEnv extends NullEnv

////////////////////////////////////////////////////////////////////////////////
// null environment abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsNullEnv extends AbsDomain[NullEnv, AbsNullEnv]

trait AbsNullEnvUtil extends AbsDomainUtil[NullEnv, AbsNullEnv]

////////////////////////////////////////////////////////////////////////////////
// default null environment abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultNullEnv extends AbsNullEnvUtil {
  case object Bot extends Dom
  case object Top extends Dom

  def alpha(g: NullEnv): Dom = Top

  abstract class Dom extends AbsNullEnv {
    def gamma: ConSet[NullEnv] = this match {
      case Bot => ConFin()
      case Top => ConFin(NullEnv)
    }

    def getSingle: ConSingle[NullEnv] = this match {
      case Bot => ConZero()
      case Top => ConOne(NullEnv)
    }

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def <=(that: AbsNullEnv): Boolean = (this, check(that)) match {
      case (Top, Bot) => false
      case _ => true
    }

    def +(that: AbsNullEnv): Dom = (this, check(that)) match {
      case (Bot, Bot) => Bot
      case _ => Top
    }

    def <>(that: AbsNullEnv): Dom = (this, check(that)) match {
      case (Top, Top) => Top
      case _ => Bot
    }

    override def toString: String = this match {
      case Bot => "⊥(null environment)"
      case Top => "Top(null environment)"
    }
  }
}
