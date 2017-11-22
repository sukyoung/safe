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

import akka.actor.{ ActorSystem, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws.{ Message, TextMessage, UpgradeToWebSocket }
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{ Flow, Sink, Source }
import akka.stream.{ ActorMaterializer, OverflowStrategy }
import kr.ac.kaist.safe.BASE_DIR
import kr.ac.kaist.safe.analyzer.Fixpoint
import kr.ac.kaist.safe.json.JsonImplicits._
import kr.ac.kaist.safe.web.actors.CmdActor
import kr.ac.kaist.safe.web.domain._

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn

object WebServer extends {
  def run(fixpoint: Fixpoint, port: Int = 8080) {
    implicit val system: ActorSystem = ActorSystem("web-debugger")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val SEP = File.separator
    val base = BASE_DIR + SEP
    val assetsPath = Array[String](base + "src", "main", "resources", "assets").mkString(SEP)

    val handlerFlow = {
      val chatActor = system.actorOf(Props(new CmdActor(fixpoint)))

      // Wraps the chatActor in a sink. When the stream to this sink will be completed
      // it sends the `ParticipantLeft` message to the chatActor.
      def chatInSink(uid: String) = Sink.actorRef[Event](chatActor, ParticipantLeft(uid))

      (uid: String) => {
        val in =
          Flow[String]
            .map(x => ReceivedCmd(uid, x))
            .to(chatInSink(uid))

        val out =
          Source.actorRef[Protocol.Message](1, OverflowStrategy.fail)
            .mapMaterializedValue(chatActor ! NewParticipant(uid, _))

        Flow.fromSinkAndSource(in, out)
      }
    }

    def websocketFlow(uid: String): Flow[Message, Message, Any] =
      Flow[Message]
        .collect {
          case TextMessage.Strict(msg) => msg // unpack incoming WS text messages
        }
        .via(handlerFlow(uid)) // route them through the chatFlow
        .map { resp: Protocol.Message => TextMessage.Strict(resp.toJson) } // pack outgoing messages into WS JSON messages

    val route =
      path("") {
        get {
          complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, Templates.getBaseTemplate))
        }
      } ~ pathPrefix("assets") {
        get {
          getFromDirectory(assetsPath)
        }
      } ~ path("ws") {
        parameter('uid) { uid =>
          extractRequest {
            req =>
              {
                req.header[UpgradeToWebSocket] match {
                  case Some(upgrade) => complete(upgrade.handleMessages(websocketFlow(uid)))
                  case None => complete(StatusCodes.BadRequest, HttpEntity("Not a valid websocket request!"))
                }
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
