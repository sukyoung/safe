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

// string abstract domain
trait StrDomain extends AbsDomain[Str] {
  // abstraction from all number string
  val Number: Elem

  // abstraction from all non-number string
  val Other: Elem

  def fromCharCode(n: AbsNum): Elem

  // abstract string element
  type Elem <: ElemTrait

  // abstract string element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    def StrictEquals(that: Elem): AbsBool
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

    def ToNumber: AbsNum
    def ToBoolean: AbsBool
  }
}
