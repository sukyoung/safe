/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.compiler

import kr.ac.kaist.jsaf.exceptions.StaticError

class StaticPhaseResult(_errors: List[StaticError]) {
  var errors = _errors
  def isSuccessful = errors.isEmpty
  def setErrors(es: List[StaticError]) = errors ++= es
  def collectErrors(results: List[StaticPhaseResult]) = {
    var allErrors = List[StaticError]()
    for (result <- results)
      allErrors ++= result.errors
    allErrors
  }
}
