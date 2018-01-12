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

// primitive value abstract domain
trait PValueDomain extends AbsDomain[PValue] {
  def apply(
    undefval: AbsUndef = AbsUndef.Bot,
    nullval: AbsNull = AbsNull.Bot,
    boolval: AbsBool = AbsBool.Bot,
    numval: AbsNum = AbsNum.Bot,
    strval: AbsStr = AbsStr.Bot
  ): Elem

  // abstract primitive value element
  type Elem <: ElemTrait

  // abstract primitive value element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    val undefval: AbsUndef
    val nullval: AbsNull
    val boolval: AbsBool
    val numval: AbsNum
    val strval: AbsStr

    def StrictEquals(that: Elem): AbsBool
    def typeCount: Int
    def toStringSet: Set[AbsStr]
    def copy(
      undefval: AbsUndef = this.undefval,
      nullval: AbsNull = this.nullval,
      boolval: AbsBool = this.boolval,
      numval: AbsNum = this.numval,
      strval: AbsStr = this.strval
    ): Elem
  }
}
