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

package kr.ac.kaist.safe.nodes.cfg

import kr.ac.kaist.safe.util.NodeUtil

sealed abstract class CFGId(
  val text: String,
  val kind: VarKind
)

case class CFGUserId(
    override val text: String,
    override val kind: VarKind,
    originalName: String,
    fromWith: Boolean
) extends CFGId(text, kind) {
  override def toString: String = NodeUtil.pp(text)
}

case class CFGTempId(
    override val text: String,
    override val kind: VarKind
) extends CFGId(text, kind) {
  override def toString: String = NodeUtil.pp(text)
}

sealed abstract class VarKind
case object GlobalVar extends VarKind
case object PureLocalVar extends VarKind
case object CapturedVar extends VarKind
case object CapturedCatchVar extends VarKind
