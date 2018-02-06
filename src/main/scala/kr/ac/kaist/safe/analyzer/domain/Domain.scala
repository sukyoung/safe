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

import spray.json._

// domain
trait Domain {
  // top element
  val Top: Elem

  // bottom element
  val Bot: Elem

  // element
  type Elem <: ElemTrait

  // load from JSON format
  def fromJson(v: JsValue): Elem

  // element traits
  protected trait ElemTrait { this: Elem =>
    // bottom check
    def isBottom: Boolean = this == Bot

    // top check
    def isTop: Boolean = this == Top

    // map/fold utils
    def foldUnit(f: => Unit): Unit = fold(())(_ => f)
    def foldUnit(f: Elem => Unit): Unit = fold(())(f)
    def fold[T](default: T)(f: Elem => T): T = isBottom match {
      case true => default
      case false => f(this)
    }

    // partial order
    def ⊑(that: Elem): Boolean

    // not partial order
    def !⊑(that: Elem): Boolean = !(this ⊑ that)

    // join operator
    def ⊔(that: Elem): Elem

    // meet operator
    def ⊓(that: Elem): Elem

    // to JSON format for dump
    def toJson: JsValue
  }
}
