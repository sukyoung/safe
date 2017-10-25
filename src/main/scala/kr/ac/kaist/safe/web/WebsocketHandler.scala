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

package kr.ac.kaist.safe.web

import akka.http.scaladsl.model.ws.TextMessage
import kr.ac.kaist.safe.analyzer.console.{ CmdResult, Interactive }

object WebsocketHandler {
  private var iter: Int = 0

  def handleTextMessage(tm: String, c: Interactive): TextMessage = {
    iter += 1

    c.runCmd(tm) match {
      case result: CmdResult => TextMessage(result.toString)
    }
  }
}
