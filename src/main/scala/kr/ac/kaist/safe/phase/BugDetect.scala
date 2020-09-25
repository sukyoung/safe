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
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.bug_detector._

// BugDetect phase
case object BugDetect extends PhaseObj[(CFG, Int, TracePartition, Semantics), BugDetectConfig, CFG] {
  val name: String = "bugDetector"
  val help: String = "Detect possible bugs in JavaScript source files."

  val checkers: List[BugDetector] = List(
    CheckNaN,
    CmpFunPrim,
    ConcatUndefStr,
    UndefOffset,
    ShadowProtoProp
  )

  def apply(
    in: (CFG, Int, TracePartition, Semantics),
    safeConfig: SafeConfig,
    config: BugDetectConfig
  ): Try[CFG] = {
    val (cfg, _, _, semantics) = in
    // Bug detection for each checker
    checkers.foreach(checker => {
      cfg.getUserBlocks.foreach(b => {
        val bugs = checker.checkBlock(b, semantics)
        bugs.foreach(println)
      })
    })
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
