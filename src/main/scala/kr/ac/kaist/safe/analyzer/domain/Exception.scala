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

sealed trait Exception
case object Error extends Exception
case object EvalError extends Exception
case object RangeError extends Exception
case object ReferenceError extends Exception
case object SyntaxError extends Exception
case object TypeError extends Exception
case object URIError extends Exception
