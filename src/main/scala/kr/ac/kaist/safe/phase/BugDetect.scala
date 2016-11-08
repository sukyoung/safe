/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
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
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util._

// BugDetect phase
case object BugDetect extends PhaseObj[(CFG, Int, CallContext, Semantics), BugDetectConfig, CFG] {
  val name: String = "bugDetector"
  val help: String = "Detect possible bugs in JavaScript source files."

  // Move to CFGBlock?  Copied from HTMLWriter.
  private def isReachable(block: CFGBlock): Boolean =
    !block.getState.isEmpty

  private def check(block: CFGBlock, semantics: Semantics): List[String] =
    if (isReachable(block) && !block.getInsts.isEmpty) {
      val (_, st) = block.getState.head // TODO it is working only when for each CFGBlock has only one control point.
      val (bugs, _) =
        block.getInsts.foldRight(List[String](), st)((inst, r) => {
          val (bs, state) = r
          inst match {
            case CFGAssert(_, _, cond: CFGBin, true) =>
              val (v, _) = semantics.V(cond, state)
              val bv = TypeConversionHelper.ToBoolean(v)
              if (!bv.isBottom && ((bv === AbsBool.True) <= AbsBool.True))
                (List(cond.ir.span.toString + ": [Warning] Conditional expression is always true.") ++ bs, state)
              else if (!bv.isBottom && ((bv === AbsBool.False) <= AbsBool.True))
                (List(cond.ir.span.toString + ": [Warning] Conditional expression is always false.") ++ bs, state)
              else (bs, state)
            case i: CFGNormalInst =>
              val (res, _) = semantics.I(i, state, AbsState.Bot)
              (bs, res)
            case _ => r
          }
        })
      bugs
    } else List[String]()

  def apply(
    in: (CFG, Int, CallContext, Semantics),
    safeConfig: SafeConfig,
    config: BugDetectConfig
  ): Try[CFG] = {
    val (cfg, _, _, semantics) = in
    // Bug detection
    val result = cfg.getUserBlocks.foldRight(List[String]())((b, r) => check(b, semantics) ++ r)
    result.foreach(println)
    Success(cfg)
  }

  def defaultConfig: BugDetectConfig = BugDetectConfig()
  val options: List[PhaseOption[BugDetectConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "messages during analysis are muted.")
  )
}

// BugDetect phase config
case class BugDetectConfig(
  var silent: Boolean = false
) extends Config
