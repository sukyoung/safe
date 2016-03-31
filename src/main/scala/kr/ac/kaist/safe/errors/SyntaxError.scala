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
import kr.ac.kaist.safe.nodes.NodeInfo
import java.util.regex.Pattern

class SyntaxError(description: String, location: Option[NodeInfo])
    extends StaticError(description, location) {
  override def getMessage: String = toString
  override def toString: String = String.format("%s:$s    %s", at, Config.LINE_SEP, description)

  override def at: String = location match {
    case Some(info) => info.span.toString
    case _ => ""
  }

  override def errorMsg(messages: Any*): String = ErrorMsgMaker.errorMsg(messages)

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

  override def error[T](msg: String): T = throw new SyntaxError(msg, None)
}
