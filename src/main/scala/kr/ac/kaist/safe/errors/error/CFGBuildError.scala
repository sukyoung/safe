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

import kr.ac.kaist.safe.nodes.ir.{ IRNode, IRTry, IRNew }

////////////////////////////////////////////////////////////////////////////////
// CFG Build Error
////////////////////////////////////////////////////////////////////////////////

sealed abstract class CFGBuildError(msg: String, irNode: IRNode) extends SafeError({
  s"${irNode.ast.info.span}: $msg"
})

case class NotHoistedError(ir: IRNode) extends CFGBuildError({
  s"${ir.getClass.getSimpleName} should have been hoisted."
}, ir)
case class WrongTryStmtError(irTry: IRTry) extends CFGBuildError({
  "Wrong IRTryStmt."
}, irTry)
case class NewArgNumError(irNew: IRNew) extends CFGBuildError({
  "IRNew should have two elements in args."
}, irNew)
case class NotSupportedIRError(ir: IRNode) extends CFGBuildError({
  s"${ir.getClass.getSimpleName} is not supported."
}, ir)
