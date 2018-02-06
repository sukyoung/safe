/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
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

// BugDetect phase
case object BugDetect extends PhaseObj[(CFG, Int, TracePartition, Semantics), BugDetectConfig, CFG] {
  val name: String = "bugDetector"
  val help: String = "Detect possible bugs in JavaScript source files."

  // Generators of bug detector messages
  def always(expr: CFGExpr, cond: Boolean): String =
    expr.ir.span.toString + ":\n    [Warning] The conditional expression \"" + expr.ir.ast.toString(0) + "\" is always " + cond + "."
  def absentProp(expr: CFGExpr, name: AbsStr, obj: CFGExpr): String =
    expr.ir.span.toString + ":\n    [Warning] The property " + name + " of the object \"" + obj.ir.ast.toString(0) + "\" is absent."

  // Move to CFGBlock?  Copied from HTMLWriter.
  private def isReachableUserCode(sem: Semantics, block: CFGBlock): Boolean =
    !sem.getState(block).isEmpty && !NodeUtil.isModeled(block)

  // Collect CFG expressions from CFG instructions
  private def collectExprs(i: CFGNormalInst): List[CFGExpr] = i match {
    case CFGAlloc(_, _, _, Some(e), _) => List(e)
    case CFGEnterCode(_, _, _, e) => List(e)
    case CFGExprStmt(_, _, _, e) => List(e)
    case CFGDelete(_, _, _, e) => List(e)
    case CFGDeleteProp(_, _, _, e1, e2) => List(e1, e2)
    case CFGStore(_, _, e1, e2, e3) => List(e1, e2, e3)
    case CFGStoreStringIdx(_, _, e1, _, e2) => List(e1, e2)
    case CFGAssert(_, _, e, _) => List(e)
    case CFGReturn(_, _, Some(e)) => List(e)
    case CFGThrow(_, _, e) => List(e)
    case CFGInternalCall(_, _, _, _, es, _) => es
    case _ => Nil
  }

  // Check expression-level rules: AbsentPropertyRead
  private def checkExpr(expr: CFGExpr, state: AbsState,
    semantics: Semantics): List[String] = expr match {
    // Don't check if this instruction is "LHS = <>fun<>["prototype"]".
    case CFGLoad(_, CFGVarRef(_, CFGTempId(name, _)),
      CFGVal(EJSString("prototype"))) if name.startsWith("<>fun<>") =>
      List[String]()
    case CFGLoad(_, obj, index) =>
      val (objV, _) = semantics.V(obj, state)
      val (propV, _) = semantics.V(index, state)
      // Check for each object location
      objV.locset.foldLeft(List[String]())((bugs, objLoc) => {
        if (!propV.isBottom && !propV.pvalue.strval.isBottom) {
          val propStr = propV.pvalue.strval
          val heap = state.heap
          val propExist = heap.get(objLoc).HasProperty(propStr, heap)
          if (!propExist.isBottom && propExist ⊑ AbsBool.False)
            absentProp(expr, propStr, obj) :: bugs
          else bugs
        } else bugs
      })
    case _ => List[String]()
  }

  // Check block/instruction-level rules: ConditionalBranch
  private def checkBlock(block: CFGBlock, semantics: Semantics): List[String] =
    if (isReachableUserCode(semantics, block) && !block.getInsts.isEmpty) {
      // TODO it is working only when for each CFGBlock has only one control point.
      val (_, st) = semantics.getState(block).head
      val (bugs, _) =
        block.getInsts.foldRight(List[String](), st)((inst, r) => {
          val (bs, state) = r
          inst match {
            case i @ CFGAssert(_, _, cond, true) =>
              val exprBugs = checkExpr(cond, state, semantics)
              val (v, _) = semantics.V(cond, state)
              val bv = TypeConversionHelper.ToBoolean(v)
              val (res, _) = semantics.I(i, state, AbsState.Bot)
              if (!bv.isBottom && ((bv StrictEquals AbsBool.True) ⊑ AbsBool.True))
                (always(cond, true) :: exprBugs ++ bs, res)
              else if (!bv.isBottom && ((bv StrictEquals AbsBool.False) ⊑ AbsBool.True))
                (always(cond, false) :: exprBugs ++ bs, res)
              else (exprBugs ++ bs, res)

            case i: CFGNormalInst =>
              val exprsBugs = collectExprs(i).foldRight(bs)((e, r) =>
                checkExpr(e, state, semantics) ++ r)
              val (res, _) = semantics.I(i, state, AbsState.Bot)
              (exprsBugs, res)

            case _ => r
          }
        })
      bugs
    } else List[String]()

  def apply(
    in: (CFG, Int, TracePartition, Semantics),
    safeConfig: SafeConfig,
    config: BugDetectConfig
  ): Try[CFG] = {
    val (cfg, _, _, semantics) = in
    // Bug detection
    val result = cfg.getUserBlocks.foldRight(List[String]())((b, r) => checkBlock(b, semantics) ++ r)
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
