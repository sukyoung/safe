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

import kr.ac.kaist.safe.config.Config

object StaticErrors {
  def flattenErrors(ex: Iterable[_ <: StaticError]): List[_ <: StaticError] = {
    var result: List[StaticError] = Nil
    for (err: StaticError <- ex) result ::= err
    return result
  }

  def getReportErrors(file_name: String, errs: Iterable[_ <: StaticError]): Option[String] = {
    val errors = flattenErrors(errs)
    if (!errors.isEmpty) {
      var str: String = ""
      for (error: StaticError <- errors.sortWith(_.compareTo(_) < 0))
        str += error.getMessage + Config.LINE_SEP
      val num_errors = errors.length
      str += s"File $file_name has $num_errors error" + (if (num_errors == 1) "." else "s.")
      Some(str)
    } else {
      None
    }
  }

  def reportErrors(file_name: String, errs: Iterable[_ <: StaticError]): Int = {
    getReportErrors(file_name, errs) match {
      case Some(str) =>
        println(str); -2
      case None => 0
    }
  }
}
