/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.exceptions

import _root_.java.io.PrintWriter
import _root_.java.io.StringWriter

class WrappedException(throwable: Throwable)
      extends StaticError(throwable.getMessage, None) {
  override def getMessage = throwable.getMessage
  override def stringName = throwable.getMessage
  override def toString = throwable.getMessage
  override def at = "no line information"
  override def description = ""
  override def getCause = throwable
}
