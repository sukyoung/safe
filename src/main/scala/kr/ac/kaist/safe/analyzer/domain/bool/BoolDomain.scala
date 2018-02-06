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

// boolean abstract domain
trait BoolDomain extends AbsDomain[Bool] {
  // abstraction from true
  val True: Elem

  // abstraction from false
  val False: Elem

  // abstract boolean element
  type Elem <: ElemTrait

  // abstract boolean element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    def StrictEquals(that: Elem): Elem
    def negate: Elem
    def &&(that: Elem): Elem
    def ||(that: Elem): Elem
    def xor(that: Elem): Elem

    def ToNumber: AbsNum
    def ToString: AbsStr
  }
}
