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

import kr.ac.kaist.safe.analyzer.models.builtin.BuiltinError
import kr.ac.kaist.safe.util.Loc

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
      case Error => BuiltinError.ERR_LOC
      case EvalError => BuiltinError.EVAL_ERR_LOC
      case RangeError => BuiltinError.RANGE_ERR_LOC
      case ReferenceError => BuiltinError.REF_ERR_LOC
      case SyntaxError => BuiltinError.SYNTAX_ERR_LOC
      case TypeError => BuiltinError.TYPE_ERR_LOC
      case URIError => BuiltinError.URI_ERR_LOC
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
