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
    var err_string: String = null
    if (!errs.isEmpty) {
      for (error: StaticError <- errors.sortWith(_.compareTo(_) < 0))
        System.out.println(error.getMessage)
      var err_string: String = null
      val num_errors = errors.length
      err_string = "File " + file_name + " has " + num_errors + " error" + (if (num_errors == 1) "." else "s.");
      return_code = -2;
    } else
      err_string = ""
    System.out.println(err_string)
    return return_code
  }
}
