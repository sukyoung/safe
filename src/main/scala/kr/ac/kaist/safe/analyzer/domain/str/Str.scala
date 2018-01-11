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

// concrete string type
case class Str(str: String) extends PValue {
  // 9.3 ToNumber
  // 9.3.1 ToNumber Applied to the String Type
  override def ToNumber: Num = {
    val exp = "[eE][+-]?[0-9]+"
    val dec1 = s"[0-9]+\\.[0-9]*(?:$exp)?"
    val dec2 = s"\\.[0-9]+(?:$exp)?"
    val dec3 = s"[0-9]+(?:$exp)?"
    val dec = s"(?:[+-]?(?:Infinity|$dec1|$dec2|$dec3))".r
    val hex = "0[xX][0-9a-fA-F]+".r
    str.trim match {
      case "" => Num(+0.0)
      case s @ hex() => Num((s + "p0").toDouble)
      case s @ dec() => Num(s.toDouble)
      case _ => Num.NaN
    }
  }

  // toString
  override def toString: String = s""""$str""""
}
