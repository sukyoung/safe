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

package kr.ac.kaist.safe.util

// address
abstract sealed class Address

object Address {
  implicit def ordering[B <: Address]: Ordering[B] = Ordering.by {
    case addr => addr.toString
  }
}

// program address
case class ProgramAddr(id: Int) extends Address {
  override def toString: String = id.toString
}

// system address
case class SystemAddr(name: String) extends Address {
  override def toString: String = name
}
