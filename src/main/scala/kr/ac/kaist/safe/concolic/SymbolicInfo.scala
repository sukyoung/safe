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

package kr.ac.kaist.safe.concolic

object SymbolicInfoTypes extends Enumeration {

  type Type = Value

  val undefined = Value(0)
  val statement = Value(1)
  val branch = Value(2)
  val endBranch = Value(3)
}

class SymbolicInfo(
    var cond: Boolean,
    var id: Option[SymbolicValue],
    var op: Option[String],
    var lhs: Option[SymbolicValue],
    var rhs: Option[SymbolicValue],
    var branch: Option[Boolean]
) {

  // for conditional branch
  val isCond = cond
  val branchTaken = branch match { case Some(b) => b; case None => true }

  private var infoType: SymbolicInfoTypes.Type = SymbolicInfoTypes.undefined
  def setType(t: SymbolicInfoTypes.Type): Unit = infoType = t
  def getType: SymbolicInfoTypes.Type = infoType

  def unpackSymbolicOption(x: Option[SymbolicValue]): String = x match {
    case Some(xx) => xx.getValue
    case None => ""
  }
  def unpackStringOption(x: Option[String]): String = x match {
    case Some(xx) => xx
    case None => ""
  }

  override def toString: String = {
    if (infoType == SymbolicInfoTypes.endBranch) {
      return ""
    }
    val x = unpackSymbolicOption(id)
    val y = unpackSymbolicOption(lhs) + " " + unpackStringOption(op) + " " + unpackSymbolicOption(rhs)
    if (x.isEmpty) y
    else x + " = " + y
  }
  override def equals(another: Any): Boolean = this.toString == another.asInstanceOf[SymbolicInfo].toString
}
