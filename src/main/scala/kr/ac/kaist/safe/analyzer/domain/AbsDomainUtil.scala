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

import kr.ac.kaist.safe.nodes.cfg.FunctionId

import scala.collection.immutable.HashSet

trait AbsUndefUtil {
  val Top: AbsUndef
  val Bot: AbsUndef
  def alpha: AbsUndef
}

trait AbsNullUtil {
  val Top: AbsNull
  val Bot: AbsNull
  def alpha: AbsNull
}

trait AbsStringUtil {
  val Top: AbsString
  val Bot: AbsString
  val NumStr: AbsString
  val OtherStr: AbsString

  def alpha(str: String): AbsString
  def alpha(str: Set[String]): AbsString

  def fromCharCode(n: AbsNumber)(absNumber: AbsNumberUtil): AbsString
}

trait AbsNumberUtil {
  val Top: AbsNumber
  val Bot: AbsNumber
  val Infinity: AbsNumber
  val PosInf: AbsNumber
  val NegInf: AbsNumber
  val NaN: AbsNumber
  val UInt: AbsNumber
  val NUInt: AbsNumber
  val NaturalNumbers: AbsNumber

  def alpha(d: Double): AbsNumber
  def alpha(l: Long): AbsNumber
}

trait AbsBoolUtil {
  val Top: AbsBool
  val Bot: AbsBool
  val True: AbsBool
  val False: AbsBool

  def alpha(b: Boolean): AbsBool
}

case class Utils(
  absUndef: AbsUndefUtil,
  absNull: AbsNullUtil,
  absBool: AbsBoolUtil,
  absNumber: AbsNumberUtil,
  absString: AbsStringUtil
)