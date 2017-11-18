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

////////////////////////////////////////////////////////////////////////////////
// concrete string type
////////////////////////////////////////////////////////////////////////////////
case class Str(str: String) extends PValue {
  override def toString: String = s""""$str""""
}

////////////////////////////////////////////////////////////////////////////////
// string abstract domain
////////////////////////////////////////////////////////////////////////////////
trait StrDomain extends AbsDomain[Str] { domain: StrDomain =>
  // abstraction from all number string
  val Number: Elem

  // abstraction from all non-number string
  val Other: Elem

  def fromCharCode(n: AbsNum): Elem

  // abstract boolean element
  type Elem <: ElemTrait

  trait ElemTrait extends super.ElemTrait { this: Elem =>
    def ===(that: Elem): AbsBool
    def <(that: Elem): AbsBool

    def isNum: AbsBool

    def trim: Elem
    def concat(that: Elem): Elem
    def charAt(pos: AbsNum): Elem
    def charCodeAt(pos: AbsNum): AbsNum
    def contains(that: Elem): AbsBool
    def length: AbsNum
    def toLowerCase: Elem
    def toUpperCase: Elem

    def isArrayIndex: AbsBool

    def isRelated(str: String): Boolean
    def isRelated(astr: Elem): Boolean

    def toAbsNum: AbsNum
    def toAbsBoolean: AbsBool

    def json: JsValue
  }
}
