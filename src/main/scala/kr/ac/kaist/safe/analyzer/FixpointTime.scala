/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.console.Interactive
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util.Useful
import kr.ac.kaist.safe.LINE_SEP

class FixpointTime(
    semantics: Semantics,
    override val consoleOpt: Option[Interactive],
    blockLogFile: String,
    funcLogFile: String
) extends Fixpoint(semantics, consoleOpt) {
  case class TimeInfo(count: Int, time: Double)
  private var totalTime = 0.0
  private var blockTime: Map[CFGBlock, TimeInfo] = Map()
  private var funcTime: Map[CFGFunction, TimeInfo] = Map()

  override def compute(initIters: Int = 0): (Int, Double) = {
    var iters = initIters
    while (!worklist.isEmpty) {
      iters += 1
      computeOneStep
    }
    consoleOpt.foreach(_.runFinished)

    // write duration times into log files
    Useful.writeWithFileName(blockLogFile)(writer => {
      writer.write("fid, fname, fspan, block, bspan, count, time, average" + LINE_SEP)
      blockTime.foreach {
        case (block, TimeInfo(count, time)) =>
          val func = block.func
          val fid = func.id
          val fname = func.name
          val fspan = func.span
          val bspan = block.span
          val average = time / count
          writer.write(f"$fid, $fname, $fspan, $block, $bspan, $count, $time%.9f, $average%.9f$LINE_SEP")
      }
    })

    Useful.writeWithFileName(funcLogFile)(writer => {
      writer.write("fid, fname, fspan, count, time, average" + LINE_SEP)
      funcTime.foreach {
        case (func, TimeInfo(count, time)) =>
          val fid = func.id
          val fname = func.name
          val fspan = func.span
          val average = time / count
          writer.write(f"$fid, $fname, $fspan, $count, $time%.9f, $average%.9f$LINE_SEP")
      }
    })

    (iters, totalTime)
  }

  override def computeOneStep: Unit = {
    // get current control point
    val cp = worklist.head
    val block = cp.block
    val func = cp.block.func

    // set the start time.
    val startTime = System.nanoTime

    // compute one step
    super.computeOneStep

    // calculate duration
    val duration = (System.nanoTime - startTime) / 1e9

    // log for block
    blockTime += block -> (blockTime.get(block) match {
      case Some(TimeInfo(count, time)) => TimeInfo(count + 1, time + duration)
      case None => TimeInfo(1, duration)
    })

    // log for function
    val entryCount = cp.block match {
      case Entry(_) => 1
      case _ => 0
    }
    funcTime += func -> (funcTime.get(func) match {
      case Some(TimeInfo(count, time)) => TimeInfo(count + entryCount, time + duration)
      case None => TimeInfo(entryCount, duration)
    })
    totalTime += duration
  }
}
