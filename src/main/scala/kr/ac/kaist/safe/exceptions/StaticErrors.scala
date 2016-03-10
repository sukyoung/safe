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

package kr.ac.kaist.safe.exceptions

object StaticErrors {
  def flattenErrors(ex: Iterable[_ <: StaticError]): List[_ <: StaticError] = {
    var result: List[StaticError] = Nil
    for (err: StaticError <- ex) result ::= err
    return result
  }

  def reportErrors(file_name: String, errs: Iterable[_ <: StaticError]): Int = {
    val errors = flattenErrors(errs)
    var return_code: Int = 0
    if (!errs.isEmpty) {
      for (error: StaticError <- errors.sortWith(_.compareTo(_) < 0))
        System.out.println(error.getMessage)
      val num_errors = errors.length
      System.out.println(s"File $file_name has $num_errors error" + (if (num_errors == 1) "." else "s."))
      return_code = -2
    }
    return return_code
  }
}
