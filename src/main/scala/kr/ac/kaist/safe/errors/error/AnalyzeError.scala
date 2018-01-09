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

package kr.ac.kaist.safe.errors.error

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

case class SemanticsNotYetImplementedError(ir: IRNode) extends AnalyzeIRNodeError({
  "Semantics for this node is not implemented yet"
}, ir)

// other errors
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
  "AbsLoc.Top.gamma is impossible"
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
  "[NotYetDefined] $name"
)
