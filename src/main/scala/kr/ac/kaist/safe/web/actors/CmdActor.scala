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

package kr.ac.kaist.safe.web.actors

import akka.actor.{ Actor, ActorRef, Status, Terminated }
import kr.ac.kaist.safe.analyzer.Fixpoint
import kr.ac.kaist.safe.analyzer.console._
import kr.ac.kaist.safe.analyzer.html_debugger.HTMLWriter
import kr.ac.kaist.safe.json.JsonUtil
import kr.ac.kaist.safe.web.{ NewParticipant, ParticipantLeft, ReceivedCmd }
import kr.ac.kaist.safe.web.Protocol.{ Result, Run, Message }

class CmdActor(fixpoint: Fixpoint) extends Actor {
  var subscribers = Set.empty[(String, ActorRef)]

  def dispatch(msg: Message): Unit = subscribers.foreach(_._2 ! msg)

  def receive: Receive = {
    case NewParticipant(uid, subscriber) => // New Participant has joined
      context.watch(subscriber)
      subscribers += (uid -> subscriber)
      dispatch(getStatus(fixpoint))

    case ReceivedCmd(cmd: String) => // Someone send command
      dispatch(processCmd(cmd, fixpoint))
    case ParticipantLeft(uid: String) => // Participant has left
      val entry = subscribers.find(p => p._1 == uid).orNull
      if (entry != null) {
        val ref = entry._2
        ref ! Status.Success(Unit)
        subscribers -= entry
      }
    case Terminated(sub) ⇒ // clean up dead subscribers, but should have been removed when `ParticipantLeft`
      subscribers = subscribers.filterNot(_._2 == sub)
  }

  def processCmd(cmd: String, fixpoint: Fixpoint): Result = {
    val req = JsonUtil.fromJson[Run](cmd)
    val console = fixpoint.consoleOpt.get
    console.runCmd(req.cmd) match {
      case CmdResultContinue(output) =>
        val state = HTMLWriter.renderGraphStates(console.cfg, console.sem, Some(console.worklist))
        Result(req.cmd, console.getPrompt, console.getIter, output, state, fixpoint.worklist.isEmpty)
      case CmdResultBreak(output) =>
        fixpoint.computeOneStep()
        val state = HTMLWriter.renderGraphStates(console.cfg, console.sem, Some(console.worklist))
        Result(req.cmd, console.getPrompt, console.getIter, output, state, fixpoint.worklist.isEmpty)
      case CmdResultRestart =>
        val state = HTMLWriter.renderGraphStates(console.cfg, console.sem, Some(console.worklist))
        Result(req.cmd, console.getPrompt, console.getIter, "", state, fixpoint.worklist.isEmpty)
    }
  }

  def getStatus(fixpoint: Fixpoint): Result = {
    val console = fixpoint.consoleOpt.get
    val state = HTMLWriter.renderGraphStates(
      console.cfg,
      console.sem,
      Some(console.worklist)
    )
    Result("", console.getPrompt, console.getIter, "", state, fixpoint.worklist.isEmpty)
  }
}
