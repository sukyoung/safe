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

package kr.ac.kaist.safe.errors.error

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.analyzer.domain.AbsValue
import kr.ac.kaist.safe.analyzer.ControlPoint
import kr.ac.kaist.safe.nodes.ir.IRNode
import kr.ac.kaist.safe.util.UserAllocSite

////////////////////////////////////////////////////////////////////////////////
// Analyze Error
////////////////////////////////////////////////////////////////////////////////

sealed abstract class AnalyzeError(msg: String) extends SafeError(msg)

// errors with IRNode
sealed abstract class AnalyzeIRNodeError(msg: String, irNode: IRNode) extends SafeError({
  s"${irNode.ast.info.span}: $msg"
})

case class IPFromExitToNoneError(ir: IRNode) extends AnalyzeIRNodeError({
  "An inter-procedural edge from an Exit node must be connected with an After-Call node."
}, ir)

case class NoAfterCallAfterCatchError(ir: IRNode) extends AnalyzeIRNodeError({
  "CFGConstruct/CFGCall must have corresponding after-call and after-catch"
}, ir)

case class UndefinedFunctionCallError(ir: IRNode) extends AnalyzeIRNodeError({
  "CFGConstruct/CFGCall tried to call undefined function"
}, ir)

case class IRSemanticsNotYetImplementedError(ir: IRNode) extends AnalyzeIRNodeError({
  "Semantics for this node is not implemented yet"
}, ir)

// other errors
case class SemanticsNotYetImplementedError(v: AbsValue, cp: ControlPoint) extends AnalyzeError({
  s"[NotYetImplemented] $v" + LINE_SEP +
    s"* Sensitivity: " + LINE_SEP +
    cp.tracePartition.toStringList.mkString(LINE_SEP)
})

case class ContextAssertionError(funName: String, msg: String) extends AnalyzeError(
  s"[Assert:$funName]: $msg"
)

case class HeapParseError(msg: String) extends AnalyzeError(
  s"[HeapParseError]: $msg"
)

case class ModelParseError(msg: String) extends AnalyzeError(
  s"[ModelParseError]: $msg"
)

case object LocTopGammaError extends AnalyzeError(
  "LocSet.Top.gamma is impossible"
)

case object SymTopGammaError extends AnalyzeError(
  "SymSet.Top.gamma is impossible"
)

case class LocParseError(msg: String) extends AnalyzeError(
  s"[LocParseError] $msg"
)

case class UserAllocSiteError(u: UserAllocSite) extends AnalyzeError({
  s"[UserAllocSiteError]: $u."
})

case object FIdTopGammaError extends AnalyzeError(
  "AbsFId.Top.gamma is impossible"
)

case class NoBlockIdError(fid: Int, bid: Int) extends AnalyzeError({
  s"unknown bid in function[$fid]: $bid"
})

case class NoFuncIdError(fid: Int) extends AnalyzeError({
  s"unknown fid: $fid"
})

case object IllFormedBlockStr extends AnalyzeError("")

case class NotYetDefined(name: String) extends AnalyzeError(
  s"[NotYetDefined] $name"
)

case class StopAnalysis(msg: String) extends AnalyzeError(
  s"[StopAnalysis] $msg"
)

case class Timeout(timeLimit: Int) extends AnalyzeError(
  s"[Timeout] $timeLimit seconds."
)

case class WrongUId(uid: Int) extends AnalyzeError(
  s"[WrongUId] uid $uid does not exist."
)

case class ToJSONFail(target: String) extends AnalyzeError(
  s"[ToJSONFail] fail to convert to JSON for dynamic shortcut."
)
