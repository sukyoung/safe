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
import kr.ac.kaist.safe.analyzer.Fixpoint
import kr.ac.kaist.safe.analyzer.console.{ CmdResultBreak, CmdResultContinue }
import kr.ac.kaist.safe.web.ReqRespJsonFormat._
import spray.json._

object WebsocketHandler {
  def handleTextMessage(tm: String, fixpoint: Fixpoint): TextMessage = {
    val req = tm.parseJson.convertTo[Request]

    val console = fixpoint.consoleOpt.get
    console.runCmd(req.cmd) match {
      case CmdResultContinue(output) =>
        val resp = Response(console.getPrompt, output, fixpoint.worklist.isEmpty)
        TextMessage(resp.toJson.compactPrint)
      case CmdResultBreak(output) =>
        fixpoint.computeOneStep()
        val resp = Response(console.getPrompt, output, fixpoint.worklist.isEmpty)
        TextMessage(resp.toJson.compactPrint)
    }
  }
}
