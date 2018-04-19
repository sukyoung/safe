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

package kr.ac.kaist.safe.errors

import kr.ac.kaist.safe.errors.error.SafeError
import kr.ac.kaist.safe.errors.warning.SafeWarning
import kr.ac.kaist.safe.LINE_SEP

class ExcLog(es: List[SafeError] = Nil, ws: List[SafeWarning] = Nil) {
  private var errs: List[SafeError] = es
  private var warns: List[SafeWarning] = ws
  override def toString: String = {
    (errs.length match {
      case 0 => "No error"
      case l =>
        l + " error" + (if (l > 1) "s" else "") + ":" + LINE_SEP +
          "    " + errs.reverse.mkString(LINE_SEP + "    ")
    }) + LINE_SEP + (warns.length match {
      case 0 => "No warning"
      case l =>
        l + " warning" + (if (l > 1) "s" else "") + ":" + LINE_SEP +
          "    " + warns.reverse.mkString(LINE_SEP + "    ")
    })
  }
  def hasError: Boolean = !errs.isEmpty
  def signal(err: SafeError): Unit = errs ::= err
  def signal(warn: SafeWarning): Unit = warns ::= warn
  def +(other: ExcLog): ExcLog = new ExcLog(other.errs ++ errs, other.warns ++ warns)
}
