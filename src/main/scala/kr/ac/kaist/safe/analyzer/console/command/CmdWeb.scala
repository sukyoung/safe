/*
 * ****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ***************************************************************************
 */

package kr.ac.kaist.safe.analyzer.console.command

import kr.ac.kaist.safe.analyzer.console._
import kr.ac.kaist.safe.web.WebServer

// web
case object CmdWeb extends Command("web", "Run interactive debugging web server") {
  def help: Unit = println(
    s"""usage: $name [port]
      |
      |Parameters
      | port [optional] default port is 8080
    """.stripMargin
  )
  def run(c: Console, args: List[String]): Option[Target] = {
    args match {
      case str :: Nil =>
        str match {
          case _ if str.matches("[+-]?\\d+") =>
            WebServer.run(c, str.toInt); None
          case _ => help; None
        }
      case _ => WebServer.run(c); None
    }
  }
}
