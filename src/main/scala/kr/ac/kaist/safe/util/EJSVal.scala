/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.util

// Utilities for JavaScript operators.
// Used by NodeUtil to handle operators in EJS nodes.
sealed abstract class EJSVal

//  number literal
case class EJSNumber(
    text: String,
    num: Double
) extends EJSVal {
  override def toString: String = text
}

// string literal
case class EJSString(
    str: String
) extends EJSVal {
  override def toString: String = "\"" + NodeUtil.pp(str) + "\""
}

// true | false
case class EJSBool(
    bool: Boolean
) extends EJSVal {
  override def toString: String = bool.toString
}

// undefined
case object EJSUndef extends EJSVal {
  override def toString: String = "undefined"
}

// null
case object EJSNull extends EJSVal {
  override def toString: String = "null"
}
