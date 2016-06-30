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

package kr.ac.kaist.safe.analyzer.console.command

import kr.ac.kaist.safe.analyzer.console._

// TODO home
case object CmdHome extends Command("home", "Reset the current position.") {
  def help: Unit = println("usage: " + name)
  def run(c: Console, args: List[String]): Option[Target] = None
  // args match {
  //   case Array() => c.goHome
  //   case _ => help
  // }
  // None
}
