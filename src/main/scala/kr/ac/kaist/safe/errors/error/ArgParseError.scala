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

package kr.ac.kaist.safe.errors.error

import kr.ac.kaist.safe.Command

sealed abstract class ArgParseError(msg: String) extends SafeError(msg)

case class ExtraArgError(name: String) extends ArgParseError({
  s"The option '$name' does not need an argument."
})

case class NoNumArgError(name: String) extends ArgParseError({
  s"The option '$name' needs a number argument."
})

case class NoStrArgError(name: String) extends ArgParseError({
  s"The option '$name' needs a string argument."
})

case class NoListArgError(name: String) extends ArgParseError({
  s"The option '$name' needs at least one string argument."
})

case class NoCmdError(str: String) extends ArgParseError({
  s"Command '$str' does not exist."
})

case class NoPhaseError(str: String) extends ArgParseError({
  s"Phase '$str' does not exist."
})

case object NoInputError extends ArgParseError({
  "Please input a command."
})

case class NoSupportError(str: String) extends ArgParseError({
  s"[NoSupportError]: we do not support '$str' as an option type"
})

case class NoObjError(str: String) extends ArgParseError({
  s"The json '$str' should be an object type."
})

case class NoOptError(str: String, cmd: Command) extends ArgParseError({
  s"The option '-$str' is not available for the command '${cmd.name}'."
})

case class NoOptArgError(opt: String, str: String) extends ArgParseError({
  s"The option '-$opt' cannot have the value '$str'."
})

case class NoFileList(str: String) extends ArgParseError({
  s"'$str' is not a file name list."
})

case class NoFileName(str: String) extends ArgParseError({
  s"'$str' is not a file name."
})

case class NoMode(cmd: String, mode: String) extends ArgParseError({
  s"'$cmd' command has no '$mode' mode."
})
