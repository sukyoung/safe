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

import kr.ac.kaist.safe.nodes.ASTNode
import kr.ac.kaist.safe.safe_util.NodeUtil

object ErrorMsgMaker {
  def makeErrorMsg(node: ASTNode): String =
    node.getClass.getSimpleName + " at " + NodeUtil.span(node).begin.at

  def errorMsg(messages: Any*): String = {
    val fullMessage = new StringBuilder
    for (message <- messages) {
      if (message.isInstanceOf[ASTNode]) {
        fullMessage.append(makeErrorMsg(message.asInstanceOf[ASTNode]))
      } else {
        fullMessage.append(message.toString)
      }
    }
    fullMessage.toString
  }
}
