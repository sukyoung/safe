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

object WebsocketHandler {
  private var iter: Int = 0

  def handleTextMessage(tm: String): TextMessage = {
    iter += 1
    TextMessage("input[" + iter + "]: " ++ tm)
  }
}
