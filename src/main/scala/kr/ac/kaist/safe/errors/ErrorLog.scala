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

package kr.ac.kaist.safe.errors

import kr.ac.kaist.safe.nodes.Node
import kr.ac.kaist.safe.scala_useful.Lists._

class ErrorLog() {
  var errors = List[StaticError]()

  def signal(msg: String, node: Node): Unit =
    signal(SAFEError.makeStaticError(msg, node.info))

  def signal(error: StaticError): Unit = {
    errors = error :: errors
  }

  def syntaxError(msg: String, node: Node): Unit =
    signal(SAFEError.makeSyntaxError(msg, node.info))

  def asList(): List[StaticError] = { Errors.removeDuplicates(errors) }
}

object Errors {
  def removeDuplicates(errors: List[StaticError]): List[StaticError] = {
    errors match {
      case Nil => errors
      case fst :: rst =>
        if (rst contains fst) { removeDuplicates(rst) }
        else { fst :: removeDuplicates(rst) }
    }
  }
}

/**
 * Stores the error and then throws it as an exception. Error messages should be
 * printed with nested spacing so that any errors from the tryCheck that are
 * actually reported will be nested inside an outer error from the type checker.
 */
class TryErrorLog extends ErrorLog {
  override def signal(error: StaticError): Unit = {
    super.signal(error)
    throw error
  }
}

/** Does not maintain any errors; no throwing, no storing. */
object DummyErrorLog extends ErrorLog {
  override def signal(error: StaticError): Unit = ()
}
