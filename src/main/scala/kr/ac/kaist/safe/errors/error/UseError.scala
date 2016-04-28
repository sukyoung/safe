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

package kr.ac.kaist.safe.errors.error

import kr.ac.kaist.safe.config.Command

sealed abstract class UseError(msg: String) extends SafeError(msg)

case class NoFileError(cmd: Command) extends UseError(s"Need a file to $cmd.")
case class NotJSFileError(fileName: String) extends UseError({
  s"Need a JavaScript file instead of $fileName."
})
