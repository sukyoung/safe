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

import kr.ac.kaist.safe.analyzer.models.builtin._

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

  def getLoc: Loc = {
    this match {
      case Error => BuiltinError.loc
      case EvalError => BuiltinEvalError.loc
      case RangeError => BuiltinRangeError.loc
      case ReferenceError => BuiltinReferenceError.loc
      case SyntaxError => BuiltinSyntaxError.loc
      case TypeError => BuiltinTypeError.loc
      case URIError => BuiltinURIError.loc
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
