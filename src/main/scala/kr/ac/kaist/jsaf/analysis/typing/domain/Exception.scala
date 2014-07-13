/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.domain

sealed abstract class Exception {
  override def toString(): String = {
    this match {
      case Error => "Err"
      case EvalError => "EvalErr"
      case RangeError => "RangeErr"
      case ReferenceError => "RefErr"
      case SyntaxError => "SyntaxErr"
      case TypeError => "TypeErr"
      case URIError => "URIErr"
    }
  }
}

case object Error extends Exception
case object EvalError extends Exception
case object RangeError extends Exception
case object ReferenceError extends Exception
case object SyntaxError extends Exception
case object TypeError extends Exception
case object URIError extends Exception
