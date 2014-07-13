/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.exceptions

/**
 * These are exceptions when the user has asked
 * us to perform an illegal operation in the shell.
 */
class UserError(msg: String) extends Exception(msg) {
  override def toString = msg
}
