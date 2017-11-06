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
import kr.ac.kaist.safe.analyzer.console.{CmdResultBreak, CmdResultContinue}
import kr.ac.kaist.safe.analyzer.html_debugger.HTMLWriter
import kr.ac.kaist.safe.web.utils.JsonUtil
import kr.ac.kaist.safe.web.utils.JsonImplicits._

object WebsocketHandler {
  def handleTextMessage(tm: String, fixpoint: Fixpoint): TextMessage = {
    val req = JsonUtil.fromJson[Request](tm)
    val console = fixpoint.consoleOpt.get
    val state = HTMLWriter.renderGraphStates(
      console.cfg,
      console.sem,
      Some(console.worklist),
    )
    if (req.cmd == "status") {
      val resp = Response(console.getPrompt, console.getIter, "", state, fixpoint.worklist.isEmpty)
      TextMessage(resp.toJson)
    } else {
      console.runCmd(req.cmd) match {
        case CmdResultContinue(output) =>
          val resp = Response(console.getPrompt, console.getIter, output, state, fixpoint.worklist.isEmpty)
          TextMessage(resp.toJson)
        case CmdResultBreak(output) =>
          fixpoint.computeOneStep()
          val resp = Response(console.getPrompt, console.getIter, output, state, fixpoint.worklist.isEmpty)
          TextMessage(resp.toJson)
      }
    }
  }
}
