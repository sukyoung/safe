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

import kr.ac.kaist.safe.nodes.IRNode

////////////////////////////////////////////////////////////////////////////////
// Analyze Error
////////////////////////////////////////////////////////////////////////////////

sealed abstract class AnalyzeError(msg: String, irNode: IRNode) extends SafeError({
  s"${irNode.ast.info.span}: $msg"
})

case class IPFromExitToNoneError(ir: IRNode) extends AnalyzeError({
  "An inter-procedural edge from an Exit node must be connected with an After-Call node."
}, ir)
