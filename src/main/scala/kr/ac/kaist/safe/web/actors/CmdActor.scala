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

import scala.util.control.Breaks._
import akka.actor.{Actor, ActorRef, Status, Terminated}
import kr.ac.kaist.safe.analyzer.{ControlPoint, Fixpoint, TracePartition}
import kr.ac.kaist.safe.analyzer.console._
import kr.ac.kaist.safe.analyzer.domain.AbsState
import kr.ac.kaist.safe.analyzer.domain.Utils.AbsState
import kr.ac.kaist.safe.analyzer.html_debugger.HTMLWriter
import kr.ac.kaist.safe.json.JsonUtil
import kr.ac.kaist.safe.nodes.cfg.{BlockId, CFGCallInst, CFGNormalInst, FunctionId}
import kr.ac.kaist.safe.web.domain.Protocol._
import kr.ac.kaist.safe.web.domain.Actions
import kr.ac.kaist.safe.web.{NewParticipant, ParticipantLeft, ReceivedCmd}

class CmdActor(fixpoint: Fixpoint) extends Actor {
  var subscribers = Set.empty[(String, ActorRef)]

  lazy val console: Interactive = fixpoint.consoleOpt.get

  def dispatch(msg: Message): Unit = subscribers.foreach(_._2 ! msg)

  def finish(uid: String, msg: Message): Unit = {
    val (_, subscriber) = subscribers.find(p => p._1 == uid).orNull
    subscriber ! msg
  }

  def receive: Receive = {
    case NewParticipant(uid, subscriber) => // New Participant has joined
      context.watch(subscriber)
      subscribers += (uid -> subscriber)
      subscriber ! getStatus(fixpoint)

    case ReceivedCmd(uid: String, msg: String) => // Someone send command
      val base = JsonUtil.fromJson[Base](msg)
      base.action match {
        case Actions.CMD => dispatch(processCmd(msg))
        case Actions.getBlockState => finish(uid, getBlockState(msg))
        case Actions.runInst => finish(uid, runInst(msg))
        case x: String =>
          println("Unmatched command : " + x)
      }
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

  def processCmd(cmd: String): Result = {
    val req = JsonUtil.fromJson[Run](cmd)
    console.runCmd(req.cmd) match {
      case CmdResultContinue(output) =>
        val state = HTMLWriter.renderGraphStates(console.cfg, console.sem, Some(console.worklist), simplified = true)
        Result(Actions.CMD, req.cmd, console.getPrompt, console.getIter, output, state, fixpoint.worklist.isEmpty)
      case CmdResultBreak(output) =>
        fixpoint.computeOneStep()
        val state = HTMLWriter.renderGraphStates(console.cfg, console.sem, Some(console.worklist), simplified = true)
        Result(Actions.CMD, req.cmd, console.getPrompt, console.getIter, output, state, fixpoint.worklist.isEmpty)
      case CmdResultRestart =>
        val state = HTMLWriter.renderGraphStates(console.cfg, console.sem, Some(console.worklist), simplified = true)
        Result(Actions.CMD, req.cmd, console.getPrompt, console.getIter, "", state, fixpoint.worklist.isEmpty)
    }
  }

  def getBlockState(req: String): BlockState = {
    val fetchBlockStateReq = JsonUtil.fromJson[FetchBlockState](req)
    val states = HTMLWriter.getBlockStates(console.cfg, console.sem, fetchBlockStateReq.bid)
    BlockState(Actions.getBlockState, fetchBlockStateReq.bid, states.insts, states.state)
  }

  def runInst(req: String): InstState = {
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

  def getStatus(fixpoint: Fixpoint): Result = {
    val console = fixpoint.consoleOpt.get
    val state = HTMLWriter.renderGraphStates(
      console.cfg,
      console.sem,
      Some(console.worklist),
      simplified = true,
    )
    Result(Actions.CMD, "", console.getPrompt, console.getIter, "", state, fixpoint.worklist.isEmpty)
  }
}
