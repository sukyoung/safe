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

sealed abstract class CFGId {
  val text: String
  val kind: VarKind

  def pp(str: String): String = {
    str.foldLeft("")((res, c) => c match {
      case '\u0008' => res + "\\b"
      case '\t' => res + "\\t"
      case '\n' => res + "\\n"
      case '\f' => res + "\\f"
      case '\r' => res + "\\r"
      case '\u000b' => res + "\\v"
      case '"' => res + "\\\""
      case '\'' => res + "'"
      case '\\' => res + "\\"
      case c => res + c
    })
  }
}

case class CFGUserId(info: Info, text: String, kind: VarKind, originalName: String, fromWith: Boolean) extends CFGId with CFGInfo {
  override def toString: String = pp(text)
}

case class CFGTempId(text: String, kind: VarKind) extends CFGId {
  override def toString: String = pp(text)
}

sealed abstract class VarKind
case object GlobalVar extends VarKind
case object PureLocalVar extends VarKind
case object CapturedVar extends VarKind
case object CapturedCatchVar extends VarKind
