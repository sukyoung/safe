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

package kr.ac.kaist.safe.concolic

import _root_.java.util.{ List => JList }

object SymbolicInfoTypes extends Enumeration {

  type Type = Value

  val undefined = Value(0)
  val statement = Value(1)
  val branch = Value(2)
  val endBranch = Value(3)
}

class SymbolicInfo(cond: Boolean, id: Option[SymbolicValue], op: Option[String], lhs: Option[SymbolicValue], rhs: Option[SymbolicValue], branch: Option[Boolean]) {
  val _op = op
  val _id = id
  val _lhs = lhs
  val _rhs = rhs

  // for conditional branch
  val isCond = cond
  val branchTaken = branch match { case Some(b) => b; case None => true }

  private var infoType: SymbolicInfoTypes.Type = SymbolicInfoTypes.undefined
  def setType(t: SymbolicInfoTypes.Type) = infoType = t
  def getType: SymbolicInfoTypes.Type = infoType

  def unpackSymbolicOption(x: Option[SymbolicValue]): String = x match { case Some(xx) => xx.getValue; case None => "" }
  def unpackStringOption(x: Option[String]): String = x match { case Some(xx) => xx; case None => "" }

  override def toString: String = {
    if (infoType == SymbolicInfoTypes.endBranch) return ""
    val x = unpackSymbolicOption(_id)
    val y = unpackSymbolicOption(_lhs) + " " + unpackStringOption(_op) + " " + unpackSymbolicOption(_rhs)
    if (x.isEmpty) y
    else x + " = " + y
  }
  override def equals(another: Any) = this.toString == another.asInstanceOf[SymbolicInfo].toString
}
