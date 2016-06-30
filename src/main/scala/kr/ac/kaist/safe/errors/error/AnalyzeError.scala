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

package kr.ac.kaist.safe.errors.error

import kr.ac.kaist.safe.nodes.ir.IRNode

////////////////////////////////////////////////////////////////////////////////
// Analyze Error
////////////////////////////////////////////////////////////////////////////////

sealed abstract class AnalyzeError(msg: String, irNode: IRNode) extends SafeError({
  s"${irNode.ast.info.span}: $msg"
})

case class IPFromExitToNoneError(ir: IRNode) extends AnalyzeError({
  "An inter-procedural edge from an Exit node must be connected with an After-Call node."
}, ir)

case class NoAfterCallAfterCatchError(ir: IRNode) extends AnalyzeError({
  "CFGConstruct/CFGCall must have corresponding after-call and after-catch"
}, ir)

case class UndefinedFunctionCallError(ir: IRNode) extends AnalyzeError({
  "CFGConstruct/CFGCall tried to call undefined function"
}, ir)

case class SemanticsNotYetImplementedError(ir: IRNode) extends AnalyzeError({
  "Semantics for this node is not implemented yet"
}, ir)
