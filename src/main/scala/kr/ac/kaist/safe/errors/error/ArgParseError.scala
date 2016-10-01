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

case object NoInputError extends ArgParseError({
  "Please input a command."
})

case class NoOptError(str: String, cmd: Command) extends ArgParseError({
  s"The option '-$str' is not available for the command '${cmd.name}'."
})

case class NoOptArgError(opt: String, str: String) extends ArgParseError({
  s"The option '-$opt' cannot have the value '$str'."
})
