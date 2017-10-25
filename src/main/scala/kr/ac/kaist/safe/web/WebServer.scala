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

import java.io.File

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws.{ BinaryMessage, Message, TextMessage, UpgradeToWebSocket }
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{ Flow, Sink }
import kr.ac.kaist.safe.BASE_DIR
import kr.ac.kaist.safe.analyzer.Fixpoint
import kr.ac.kaist.safe.analyzer.html_debugger.HTMLWriter

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object WebServer {
  def run(fixpoint: Fixpoint, port: Int = 8080) {
    implicit val system: ActorSystem = ActorSystem("web-debugger")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val SEP = File.separator
    val base = BASE_DIR + SEP
    val assetsPath = Array[String](base + "src", "main", "resources", "assets").mkString(SEP)

    val handleWebsocket =
      Flow[Message]
        .mapConcat {
          case TextMessage.Strict(tm) => WebsocketHandler.handleTextMessage(tm, fixpoint) :: Nil
          // ignore binary messages but drain content to avoid the stream being clogged
          case bm: BinaryMessage => bm.dataStream.runWith(Sink.ignore); Nil
        }

    val route =
      path("") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, Templates.getBaseTemplate))
        }
      } ~ path("result") {
        get {
          val interactive = fixpoint.consoleOpt.get
          complete(HttpEntity(
            ContentTypes.`text/html(UTF-8)`,
            HTMLWriter.drawGraph(interactive.cfg, interactive.sem, Some(interactive.worklist))
          ))
        }
      } ~ pathPrefix("assets") {
        get {
          getFromDirectory(assetsPath)
        }
      } ~ path("ws") {
        extractRequest {
          req =>
            {
              req.header[UpgradeToWebSocket] match {
                case Some(upgrade) => complete(upgrade.handleMessages(handleWebsocket))
                case None => complete(StatusCodes.BadRequest, HttpEntity("Not a valid websocket request!"))
              }
            }
        }
      }

    val bindingFuture = Http().bindAndHandle(route, "localhost", port)

    println(s"Open browser and navigate http://localhost:$port/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
