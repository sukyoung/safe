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

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.analyzer.models.FuncModel
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

  def getModel: FuncModel = {
    this match {
      case Error => BuiltinError
      case EvalError => BuiltinEvalError
      case RangeError => BuiltinRangeError
      case ReferenceError => BuiltinRefError
      case SyntaxError => BuiltinSyntaxError
      case TypeError => BuiltinTypeError
      case URIError => BuiltinURIError
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
