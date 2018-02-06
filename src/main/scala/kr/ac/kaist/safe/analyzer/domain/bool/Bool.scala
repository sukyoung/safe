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

// concrete boolean type
case class Bool(bool: Boolean) extends PValue {
  override def toString: String = bool.toString

  // 11.11 BinaryLogicalOperators
  def &&(that: Bool): Bool = this.bool && that.bool
  def ||(that: Bool): Bool = this.bool || that.bool
}
