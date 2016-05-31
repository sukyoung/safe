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

trait AbsDomain {
  def getAbsCase: AbsCase

  def isTop: Boolean
  def isBottom: Boolean
  def isConcrete: Boolean
  def toAbsString(absString: AbsStringUtil): AbsString
  def getConcreteValueAsString(defaultString: String = ""): String = {
    if (isConcrete) toString else defaultString
  }
}

trait AbsUndef extends AbsDomain {
  def <=(that: AbsUndef): Boolean
  def </(that: AbsUndef): Boolean = !(this <= that)
  def +(that: AbsUndef): AbsUndef
  def <>(that: AbsUndef): AbsUndef
  def ===(that: AbsUndef, absBool: AbsBoolUtil): AbsBool
}

trait AbsNull extends AbsDomain {
  def <=(that: AbsNull): Boolean
  def </(that: AbsNull): Boolean = !(this <= that)
  def +(that: AbsNull): AbsNull
  def <>(that: AbsNull): AbsNull
  def ===(that: AbsNull, absBool: AbsBoolUtil): AbsBool
}

trait AbsBool extends AbsDomain {
  def getPair: (AbsCase, Option[Boolean]) = (getAbsCase, getSingle)
  def getSingle: Option[Boolean]
  def gammaOpt: Option[Set[Boolean]]

  def unary_!(): AbsBool

  def <=(that: AbsBool): Boolean
  def </(that: AbsBool): Boolean
  def +(that: AbsBool): AbsBool
  def <>(that: AbsBool): AbsBool
  def ===(that: AbsBool, absBool: AbsBoolUtil): AbsBool
}

trait AbsNumber extends AbsDomain {
  def getPair: (AbsCase, Option[Double]) = (getAbsCase, getSingle)
  def getSingle: Option[Double]
  def gammaOpt: Option[Set[Double]]

  def <=(that: AbsNumber): Boolean
  def </(that: AbsNumber): Boolean = !(this <= that)
  def +(that: AbsNumber): AbsNumber
  def <>(that: AbsNumber): AbsNumber
  def isEqualTo(that: AbsNumber, absBool: AbsBoolUtil): AbsBool
  def isSmallerThan(that: AbsNumber, absBool: AbsBoolUtil): AbsBool

  def toBoolean(absBool: AbsBoolUtil): AbsBool
  def isNum(v: Double): Boolean
  def isNum: Boolean
  def isInfinity: Boolean
  def isPosInf: Boolean
  def isNegInf: Boolean
  def getUIntSingle: Option[Double]
  def isUIntSingle: Boolean
  def isNUIntSingle: Boolean
  def isUIntAll: Boolean
  def isUInt: Boolean
  def isUIntOrBot: Boolean
  def isNaN: Boolean
}

trait AbsString extends AbsDomain {
  def getPair: (AbsCase, Option[String]) = (getAbsCase, getSingle)
  def getSingle: Option[String]
  def gammaOpt: Option[Set[String]]

  def <=(that: AbsString): Boolean
  def </(that: AbsString): Boolean = !(this <= that)
  def +(that: AbsString): AbsString
  def <>(that: AbsString): AbsString
  def ===(that: AbsString, absBool: AbsBoolUtil): AbsBool

  def trim: AbsString
  def concat(that: AbsString): AbsString
  def charAt(pos: AbsNumber): AbsString
  def charCodeAt(pos: AbsNumber, absNumber: AbsNumberUtil): AbsNumber
  def contains(that: AbsString, absBool: AbsBoolUtil): AbsBool
  def length(absNumber: AbsNumberUtil): AbsNumber
  def toLowerCase: AbsString
  def toUpperCase: AbsString

  def isAllNums: Boolean
  def isAllOthers: Boolean
}

sealed abstract class AbsCase
case object AbsTop extends AbsCase
case object AbsBot extends AbsCase
case object AbsSingle extends AbsCase
case object AbsMulti extends AbsCase
