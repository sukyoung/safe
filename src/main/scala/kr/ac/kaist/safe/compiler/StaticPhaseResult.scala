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

package kr.ac.kaist.safe.compiler

import kr.ac.kaist.safe.errors.StaticError

class StaticPhaseResult(_errors: List[StaticError]) {
  var errors = _errors
  def isSuccessful: Boolean = errors.isEmpty
  def setErrors(es: List[StaticError]): Unit = errors ++= es
  def collectErrors(results: List[StaticPhaseResult]): List[StaticError] = {
    var allErrors = List[StaticError]()
    for (result <- results)
      allErrors ++= result.errors
    allErrors
  }
}
