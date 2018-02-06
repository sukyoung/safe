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

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.ws.{ Message, TextMessage, UpgradeToWebSocket }
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.ExceptionHandler
import akka.stream.scaladsl.{ Flow, Sink, Source }
import akka.stream.{ ActorMaterializer, OverflowStrategy }
import kr.ac.kaist.safe._
import kr.ac.kaist.safe.analyzer.Fixpoint
import kr.ac.kaist.safe.analyzer.console.{ Interactive, WebConsole }
import kr.ac.kaist.safe.cfg_builder.DefaultCFGBuilder
import kr.ac.kaist.safe.compiler.Translator
import kr.ac.kaist.safe.json.JsonImplicits._
import kr.ac.kaist.safe.json.JsonUtil
import kr.ac.kaist.safe.parser.Parser
import kr.ac.kaist.safe.phase._
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.web.actors.{ CmdActor, NoFileSelectedException }
import kr.ac.kaist.safe.web.domain.Protocol.FileUploadResp
import kr.ac.kaist.safe.web.domain._

import scala.concurrent.ExecutionContextExecutor
import scala.io.StdIn
import scala.util.{ Failure, Success }

object WebServer extends {
  var cmdActor: ActorRef = _

  def run(port: Int) {
    implicit val system: ActorSystem = ActorSystem("web-debugger")
    implicit val materializer: ActorMaterializer = ActorMaterializer()
    // needed for the future flatMap/onComplete in the end
    implicit val executionContext: ExecutionContextExecutor = system.dispatcher

    val assetsPath = Useful.path("src", "main", "resources", "assets")
    cmdActor = system.actorOf(Props(new CmdActor()))

    val handlerFlow = {
      // Wraps the chatActor in a sink. When the stream to this sink will be completed
      // it sends the `ParticipantLeft` message to the chatActor.
      def chatInSink(uid: String) = Sink.actorRef[Event](cmdActor, ParticipantLeft(uid))

      (uid: String) => {
        val in =
          Flow[String]
            .map(x => ReceivedCmd(uid, x))
            .to(chatInSink(uid))

        val out =
          Source.actorRef[Protocol.Message](1, OverflowStrategy.fail)
            .mapMaterializedValue(cmdActor ! NewParticipant(uid, _))

        Flow.fromSinkAndSource(in, out)
      }
    }

    implicit def myExceptionHandler: ExceptionHandler =
      ExceptionHandler {
        case _: NoFileSelectedException =>
          extractUri { uri =>
            println(s"Request to $uri could not be handled normally")
            complete(StatusCodes.BadRequest, "File is not selected")
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
      } ~ uploadedFile("upload") {
        case (metadata, file) =>
          parameter('uid) { uid =>
            // Read Content
            val content = io.Source.fromFile(file).getLines.mkString("\n")

            // Parse
            Parser.stringToAST(content) match {
              case Failure(e) => complete(StatusCodes.BadRequest, JsonUtil.toJson(FileUploadResp("error", "parse failed")))
              case Success((pgm, _)) =>

                val testSafeConfig: SafeConfig = SafeConfig(CmdBase, Nil)
                val parser = new ArgParser(CmdBase, testSafeConfig)
                val heapBuildConfig = HeapBuild.defaultConfig
                val testJSON = Useful.path("config.json")
                parser.addRule(heapBuildConfig, HeapBuild.name, HeapBuild.options)
                parser(List(s"-json=$testJSON"))

                // AST
                val (ast, _) = ASTRewrite.rewrite(pgm)
                complete(ast.toString)

                // Translate AST -> IR.
                val translator = new Translator(ast)
                val ir = translator.result

                // Build CFG from IR.
                val cbResult = new DefaultCFGBuilder(ir, null, null)
                val cfg = cbResult.cfg

                // HeapBuild
                HeapBuild(cfg, null, heapBuildConfig) match {
                  case Failure(e) => complete(StatusCodes.BadRequest, JsonUtil.toJson(FileUploadResp("error", "heap build failed")))
                  case Success(some) =>
                    val (cfg, sem, initTP, heapConfig, iter) = some

                    // set the start time.
                    val startTime = System.currentTimeMillis
                    var iters: Int = 0

                    var interOpt: Option[Interactive] = None
                    interOpt = Some(new WebConsole(cfg, sem, heapConfig, iter))

                    cmdActor ! UpdateFixpoint(uid, new Fixpoint(sem, interOpt))

                    complete(JsonUtil.toJson(FileUploadResp("complete")))
                }
            }
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

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", port)

    println(s"Open browser and navigate http://localhost:$port/\nPress RETURN to stop...")
    StdIn.readLine() // let it run until user presses return
    bindingFuture
      .flatMap(_.unbind()) // trigger unbinding from the port
      .onComplete(_ => system.terminate()) // and shutdown when done
  }
}
