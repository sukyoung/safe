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

import kr.ac.kaist.safe.nodes.NodeInfo

object SAFEError extends RuntimeException {
  def np[T](): T = throw new Error("Not Possible")
  def error[T](msg: String): T = throw new Error(msg)
  def makeStaticError(description: String, info: NodeInfo): StaticError =
    new StaticError(description, Some(info))
  def makeSyntaxError(description: String, info: NodeInfo): SyntaxError =
    new SyntaxError(description, Some(info))
  def makeSyntaxError(description: String): SyntaxError =
    new SyntaxError(description, None)
}
