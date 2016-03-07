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

import java.util.regex.Pattern
import kr.ac.kaist.safe.nodes.NodeInfo
import kr.ac.kaist.safe.safe_util.ErrorMsgMaker

class StaticError(val description: String, val location: Option[NodeInfo])
    extends RuntimeException with Ordered[StaticError] {
  override def getMessage: String = toString
  override def toString: String = String.format("%s:\n    %s", at, description)

  def at: String = location match {
    case Some(info) => info.span.toString
    case _ => ""
  }

  def errorMsg(messages: Any*): String = ErrorMsgMaker.errorMsg(messages)

  override def compare(that: StaticError): Int = this.compareTo(that)

  override def compareTo(that: StaticError): Int = {
    val numMatcher = Pattern.compile("(.+\\.js:)(\\d+)(?::(\\d+)(?:-(\\d+))?)?")
    val m1 = numMatcher.matcher(this.toString)
    val m2 = numMatcher.matcher(that.toString)

    // If either does not have the span, just do string comparison.
    if (m1.lookingAt && m2.lookingAt) {
      // Check that they both have the same prefix before numbers.
      val cmp = m1.group(1).compareTo(m2.group(1))
      if (cmp != 0) cmp
      else {
        // Compare each number.
        var continue = true
        var result = 0
        for (i <- 2 to 4; if continue) {
          var num1: Option[Int] = None
          var num2: Option[Int] = None
          try { num1 = Some(Integer.parseInt(m1.group(i))) }
          catch { case e: Throwable => }
          try { num2 = Some(Integer.parseInt(m2.group(i))) }
          catch { case e: Throwable => }
          (num1, num2) match {
            case (None, None) =>
            case (None, _) =>
              result = -1; continue = false
            case (Some(n1), None) =>
              result = 1; continue = false
            case (Some(n1), Some(n2)) =>
              if (!n1.equals(n2)) { result = n1.compareTo(n2); continue = false }
          }
        }
        result
      }
    } else this.toString.compareTo(that.toString)
  }

  def error[T](msg: String): T = throw new StaticError(msg, None)
}
