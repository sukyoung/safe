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

////////////////////////////////////////////////////////////////////////////////
// concrete boolean type
////////////////////////////////////////////////////////////////////////////////
case class Bool(b: Boolean) extends PValue {
  override def toString: String = b.toString
}

////////////////////////////////////////////////////////////////////////////////
// boolean abstract domain
////////////////////////////////////////////////////////////////////////////////
trait BoolDomain extends AbsDomain[Bool] { domain: BoolDomain =>
  // abstraction from true
  val True: Elem

  // abstraction from false
  val False: Elem

  // abstract boolean element
  type Elem <: ElemTrait

  trait ElemTrait extends super.ElemTrait { this: Elem =>
    def ===(that: Elem): Elem
    def negate: Elem
    def &&(that: Elem): Elem
    def ||(that: Elem): Elem
    def xor(that: Elem): Elem

    def toAbsNum: AbsNum
    def toAbsStr: AbsStr
  }
}
