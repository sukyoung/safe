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

package kr.ac.kaist.safe.errors.error

sealed abstract class InterpreterError(msg: String) extends SafeError(msg)

case class ECMASpecTestFailedError(msg: String) extends InterpreterError(msg)
