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

case class Utils()

object Utils {
  def register(
    absUndef: AbsUndefUtil,
    absNull: AbsNullUtil,
    absBool: AbsBoolUtil,
    absNumber: AbsNumberUtil,
    absString: AbsStringUtil
  ): Unit = {
    AbsUndef = absUndef
    AbsNull = absNull
    AbsBool = absBool
    AbsNumber = absNumber
    AbsString = absString
    AbsPValue = new DefaultPValue(absUndef, absNull, absBool, absNumber, absString)
  }
  var AbsUndef: AbsUndefUtil = null
  var AbsNull: AbsNullUtil = null
  var AbsBool: AbsBoolUtil = null
  var AbsNumber: AbsNumberUtil = null
  var AbsString: AbsStringUtil = null
  var AbsPValue: AbsPValueUtil = null
}
