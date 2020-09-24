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

package kr.ac.kaist.safe.phase

import scala.util.{ Failure, Success, Try }
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.bug_detector._

// BugDetect phase
case object BugDetect extends PhaseObj[(CFG, Int, TracePartition, Semantics), BugDetectConfig, CFG] {
  val name: String = "bugDetector"
  val help: String = "Detect possible bugs in JavaScript source files."

  val checkers = List(CheckNaN, CmpFunPrim, ConcatUndefStr)
  // Generators of bug detector messages

  // Move to CFGBlock?  Copied from HTMLWriter.
  private def isReachableUserCode(sem: Semantics, block: CFGBlock): Boolean =
    !sem.getState(block).isEmpty && !NodeUtil.isModeled(block)

  // Collect CFG expressions from CFG instructions
  // Check block/instruction-level rules: ConditionalBranch
  private def checkBlock(block: CFGBlock, semantics: Semantics, checkers: List[BugDetector]): List[String] =
    if (isReachableUserCode(semantics, block)) {
      semantics.getState(block).foldLeft(List[String]()) {
        case (bugs, (tp, st)) => {
          val cp = ControlPoint(block, tp)
          val (res, _) = block.getInsts.foldRight(bugs, st)((inst, r) => {
            inst match {
              case inst: CFGNormalInst =>
                val (bs, state) = r
                val newAlarms = checkers.foldLeft(List[String]())((acc, checker) => {
                  checker.getAlarmsFromInst(inst, state, semantics) ::: acc
                })
                val (res, _) = semantics.I(cp, inst, state, AbsState.Bot)
                (newAlarms ::: bs, res)
              case _ => r
            }
          })
          val blockAlarms = checkers.foldLeft(List[String]())((acc, checker) => {
            checker.getAlarmsFromBlock(block, st, semantics) ::: acc
          })
          blockAlarms ::: res
        }
      }
    } else List[String]()

  def apply(
    in: (CFG, Int, TracePartition, Semantics),
    safeConfig: SafeConfig,
    config: BugDetectConfig
  ): Try[CFG] = {
    val (cfg, _, _, semantics) = in
    // Bug detection
    val result = cfg.getUserBlocks.foldRight(List[String]())((b, r) => checkBlock(b, semantics, checkers) ++ r)
    result.foreach(println)
    Success(cfg)
  }

  def defaultConfig: BugDetectConfig = BugDetectConfig()
  val options: List[PhaseOption[BugDetectConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "messages during bug detection are muted.")
  )
}

// BugDetect phase config
case class BugDetectConfig(
  var silent: Boolean = false
) extends Config
