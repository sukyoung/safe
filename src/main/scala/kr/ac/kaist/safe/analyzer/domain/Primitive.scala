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

trait Primitive extends {
  // Abstraction of Table 11 in section 9.2, ECMAScript 5.1
  def toAbsBoolean: AbsBool
  // Abstraction of Table 12 in section 9.3, ECMAScript 5.1
  def toAbsNumber: AbsNumber
  // Abstraction of Table 13 in section 9.8, ECMAScript 5.1
  def toAbsString: AbsString
}
