/*******************************************************************************
    Copyright 2009,2011, Oracle and/or its affiliates.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.scala_src.useful

import kr.ac.kaist.jsaf.useful.HasAt
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.exceptions.JSAFError
import kr.ac.kaist.jsaf.exceptions.StaticError

class ErrorLog() {
  var errors = List[StaticError]()

  def signal(msg: String, hasAt: HasAt): Unit =
    signal(JSAFError.makeStaticError(msg, hasAt))

  def signal(error: StaticError) = {
    errors = error :: errors
  }

  def syntaxError(msg: String, hasAt: HasAt): Unit =
    signal(JSAFError.makeSyntaxError(msg, hasAt))

  def asList() = {Errors.removeDuplicates(errors)}

  def asJavaList() = {toJavaList(asList())}
}

object Errors {
  def removeDuplicates(errors: List[StaticError]): List[StaticError] = {
    errors match {
      case Nil => errors
      case fst :: rst =>
        if (rst contains fst) {removeDuplicates(rst)}
        else {fst :: removeDuplicates(rst)}
    }
  }
}

/**
 * Stores the error and then throws it as an exception. Error messages should be
 * printed with nested spacing so that any errors from the tryCheck that are
 * actually reported will be nested inside an outer error from the type checker.
 */
class TryErrorLog extends ErrorLog {
  override def signal(error: StaticError) = {
    super.signal(error)
    throw error
  }
}

/** Does not maintain any errors; no throwing, no storing. */
object DummyErrorLog extends ErrorLog {
  override def signal(error: StaticError) = ()
}
