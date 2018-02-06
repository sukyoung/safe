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
import kr.ac.kaist.safe.analyzer.console._
import kr.ac.kaist.safe.analyzer.domain.AbsState
import kr.ac.kaist.safe.analyzer.html_debugger.HTMLWriter
import kr.ac.kaist.safe.analyzer.{ ControlPoint, Fixpoint, TracePartition }
import kr.ac.kaist.safe.json.JsonUtil
import kr.ac.kaist.safe.nodes.cfg.{ BlockId, CFGCallInst, CFGNormalInst, FunctionId }
import kr.ac.kaist.safe.web.domain.Actions
import kr.ac.kaist.safe.web.domain.Protocol._
import kr.ac.kaist.safe.web.{ NewParticipant, ParticipantLeft, ReceivedCmd, UpdateFixpoint }
import scala.collection.mutable
import scala.util.control.Breaks._

case class State(subscriber: ActorRef, var fixpoint: Fixpoint = null)

class CmdActor() extends Actor {
  var states = mutable.Map.empty[String, State]

  def dispatch(uid: String, msg: Message): Unit = {
    states(uid).subscriber ! msg
  }

  def receive: Receive = {
    case UpdateFixpoint(uid, fixpoint) =>
      states(uid).fixpoint = fixpoint
      dispatch(uid, getStatus(fixpoint))
    case NewParticipant(uid, subscriber) => // New Participant has joined
      println("Participant Joined " + uid)
      if (states.contains(uid)) {
        // Error already existing uid
      } else {
        context.watch(subscriber)
        states.put(uid, State(subscriber))
        dispatch(uid, InitialState())
      }
    case ReceivedCmd(uid: String, msg: String) => // Someone send command
      val fixpoint = states(uid).fixpoint
      if (msg == "heartbeat") {
        // Because of idle-timeout, if client doesn't send anything for more than 2 hours,
        // connection will be closed and fixpoint state will be cleared from server.
        // Client will send heartbeat for every 1 hour if web page has not been closed. (See ws.js)
        //
        // If you want to change idle-timeout, you can find it at application.conf file.
      } else if (fixpoint == null) {
        dispatch(uid, InitialState())
      } else {
        val base = JsonUtil.fromJson[Base](msg)
        base.action match {
          case Actions.CMD => dispatch(uid, processCmd(msg, fixpoint))
          case Actions.getBlockState => dispatch(uid, getBlockState(msg, fixpoint))
          case Actions.runInst => dispatch(uid, runInst(msg, fixpoint))
          case x: String => println("Unmatched command : " + x)
        }
      }
    case ParticipantLeft(uid: String) => // Participant has left
      println("Participant Left " + uid)
      val entry = states(uid)
      if (entry != null) {
        entry.subscriber ! Status.Success(Unit)
        states -= uid
      }
    case Terminated(sub) ⇒ // clean up dead subscribers, but should have been removed when `ParticipantLeft`
      states = states.filterNot(_._2.subscriber == sub)
  }

  def processCmd(cmd: String, fixpoint: Fixpoint): Result = {
    val console: Interactive = fixpoint.consoleOpt.get
    val req = JsonUtil.fromJson[Run](cmd)

    console.runCmd(req.cmd) match {
      case CmdResultContinue(output) =>
        val state = HTMLWriter.renderGraphStates(console.cfg, console.sem, Some(console.worklist), simplified = true)
        Result(Actions.CMD, req.cmd, console.getPrompt, console.getIter, output, state, fixpoint.worklist.isEmpty)
      case CmdResultBreak(output) =>
        if (req.cmd == "run") {
          fixpoint.compute()
        } else {
          fixpoint.computeOneStep()
        }
        val state = HTMLWriter.renderGraphStates(console.cfg, console.sem, Some(console.worklist), simplified = true)
        Result(Actions.CMD, req.cmd, console.getPrompt, console.getIter, output, state, fixpoint.worklist.isEmpty)
      case CmdResultRestart =>
        val state = HTMLWriter.renderGraphStates(console.cfg, console.sem, Some(console.worklist), simplified = true)
        Result(Actions.CMD, req.cmd, console.getPrompt, console.getIter, "", state, fixpoint.worklist.isEmpty)
    }
  }

  def getBlockState(req: String, fixpoint: Fixpoint): BlockState = {
    val console: Interactive = fixpoint.consoleOpt.get
    val fetchBlockStateReq = JsonUtil.fromJson[FetchBlockState](req)
    val states = HTMLWriter.getBlockStates(console.cfg, console.sem, fetchBlockStateReq.bid)
    BlockState(Actions.getBlockState, fetchBlockStateReq.bid, states.insts, states.state)
  }

  def runInst(req: String, fixpoint: Fixpoint): InstState = {
    val runInstReq = JsonUtil.fromJson[RunInst](req)
    val tmp = runInstReq.bid.split(':').map(x => x.toInt)
    val fid: FunctionId = tmp(0)
    val bid: BlockId = tmp(1)
    val instId = runInstReq.iid

    // Move CP
    val c = fixpoint.consoleOpt.get
    val sem = c.sem
    val bl = c.cfg.getBlock(fid, bid).get

    val tpList: List[TracePartition] = sem.getState(bl).toList.map {
      case (tp, _) => tp
    }
    if (tpList.isEmpty) {
      throw new IllegalArgumentException("tpList is empty")
    }
    val cp = ControlPoint(bl, tpList.head)

    // Run Insts
    val block = cp.block
    val insts = block.getInsts.reverse

    var st = c.sem.getState(cp)
    var excSt = AbsState.Bot

    breakable {
      for (inst <- insts) {
        val (s, e) = inst match {
          case i: CFGNormalInst => c.sem.I(i, st, excSt)
          case i: CFGCallInst => c.sem.CI(cp, i, st, excSt)
        }
        st = s; excSt = e
        if (inst.id == instId.toInt) break
      }
    }

    InstState(Actions.runInst, runInstReq.bid, instId, "{" + HTMLWriter.addSingleState(runInstReq.bid + ':' + instId, st) + "}")
  }

  def getStatus(fixpoint: Fixpoint): Message = {
    if (fixpoint == null) {
      InitialState()
    } else {
      val console = fixpoint.consoleOpt.get
      val state = HTMLWriter.renderGraphStates(
        console.cfg,
        console.sem,
        Some(console.worklist),
        simplified = true
      )
      Result(Actions.CMD, "", console.getPrompt, console.getIter, "", state, fixpoint.worklist.isEmpty)
    }
  }
}

class NoFileSelectedException extends Exception
