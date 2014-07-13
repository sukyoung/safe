/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.exceptions

import _root_.java.util.regex.Pattern
import _root_.java.util.regex.Matcher

import kr.ac.kaist.jsaf.nodes_util.ErrorMsgMaker
import kr.ac.kaist.jsaf.useful.HasAt

class SyntaxError(description: String, location: Option[HasAt])
      extends StaticError(description, location) {
  override def stringName = toString
  override def getMessage = toString
  override def toString = String.format("%s:\n    %s", at, description)

  override def at = location match {
    case Some(loc) => loc.at
    case _ => ""
  }

  override def errorMsg(messages: Any*)  = ErrorMsgMaker.errorMsg(messages)

  def compareTo(that: SyntaxError): Int = {
    val numMatcher = Pattern.compile("(.+\\.js:)(\\d+)(?::(\\d+)(?:-(\\d+))?)?");
    val m1 = numMatcher.matcher(this.toString)
    val m2 = numMatcher.matcher(that.toString)

    // If either does not have the span, just do string comparison.
    if (m1.lookingAt && m2.lookingAt) {
      // Check that they both have the same prefix before numbers.
      val cmp = m1.group(1).compareTo(m2.group(1))
      if (cmp != 0) cmp
      else {
        // Compare each number.
        for (i <- 2 to 4) {
          val num1 = Integer.parseInt(m1.group(i))
          val num2 = Integer.parseInt(m2.group(i))
          num1.compareTo(num2)
        }
        this.toString.compareTo(that.toString)
      }
    } else this.toString.compareTo(that.toString)
  }

  override def error(msg: String) = throw new SyntaxError(msg, None)
}
