/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.cfg

abstract class CFGId extends CFGNode {
  def getText: String
  def getVarKind: VarKind
  override def toString : String
}

case class CFGUserId(info: Info, text: String, kind: VarKind,
                     originalName: String, fromWith: Boolean) extends CFGId {
  def getInfo: Info = info
  def getText: String = text
  def getVarKind: VarKind = kind
  def getOriginalName: String = originalName
  def isWith: Boolean = fromWith
  override def toString: String = getText
}

case class CFGTempId(text: String, kind: VarKind) extends CFGId {
  def getText: String = text
  def getVarKind: VarKind = kind
  override def toString: String = getText
}

sealed abstract class VarKind
case object GlobalVar extends VarKind
case object PureLocalVar extends VarKind
case object CapturedVar extends VarKind
case object CapturedCatchVar extends VarKind
