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

package kr.ac.kaist.safe.errors.warning

import kr.ac.kaist.safe.nodes.ir.{ IRNode, IRStmt }

////////////////////////////////////////////////////////////////////////////////
// CFG Build Warning
////////////////////////////////////////////////////////////////////////////////

sealed abstract class CFGBuildWarning(msg: String, irNode: IRNode) extends SafeWarning({
  s"${irNode.ast.info.span}: $msg"
})

case class IRIgnored(ir: IRStmt) extends CFGBuildWarning({
  s"The IR statement ${ir.getClass.getSimpleName} is ignored."
}, ir)
