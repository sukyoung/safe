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

package kr.ac.kaist.safe.nodes

import kr.ac.kaist.safe.util.NodeUtil

sealed abstract class CFGId {
  val text: String
  val kind: VarKind
}

case class CFGUserId(info: Info, text: String, kind: VarKind, originalName: String, fromWith: Boolean) extends CFGId with CFGInfo {
  override def toString: String = NodeUtil.pp(text)
}

case class CFGTempId(text: String, kind: VarKind) extends CFGId {
  override def toString: String = NodeUtil.pp(text)
}

sealed abstract class VarKind
case object GlobalVar extends VarKind
case object PureLocalVar extends VarKind
case object CapturedVar extends VarKind
case object CapturedCatchVar extends VarKind
