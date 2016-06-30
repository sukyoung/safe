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

// TODO break
case object CmdBreak extends Command("break") {
  def help: Unit = {}
  def run(c: Console, args: List[String]): Option[Target] = None
  // val arg = arguments.head
  // val p1 = new Regex("""([^:]+):(\\d+)""", "filename", "line")

  // try {
  //   val p1(filename, line) = arg
  //   System.out.println(line+" @"+filename)
  // } catch {
  //   case e => e.printStackTrace()
  // }
}
