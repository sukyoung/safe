/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.exceptions

import kr.ac.kaist.jsaf.useful.HasAt

object JSAFError extends RuntimeException {
  def makeStaticError(description: String, loc: HasAt) =
    new StaticError(description, Some(loc))

  def makeSyntaxError(description: String, loc: HasAt): SyntaxError =
    new SyntaxError(description, Some(loc))

  def makeSyntaxError(description: String): SyntaxError =
    new SyntaxError(description, None)

  def error(msg: String) = throw new StaticError(msg, None)
}
