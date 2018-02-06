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

// concrete value type
abstract class Value extends IValue

// helper values for modeling
abstract class TypeValue(name: String) extends Value {
  override def toString: String = name
}
case object StringT extends TypeValue("string")
case object NumberT extends TypeValue("number")
case object BoolT extends TypeValue("bool")
