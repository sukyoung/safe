/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.concolic

import _root_.java.util.{List => JList}
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.EJSOp
import kr.ac.kaist.jsaf.nodes_util.{ IRFactory => IF }
import kr.ac.kaist.jsaf.nodes_util.{ NodeUtil => NU }
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.scala_src.useful.Sets._

class SymbolicInfo(cond: Boolean, id: Option[SymbolicValue], op: Option[String], lhs: Option[SymbolicValue], rhs: Option[SymbolicValue], branch: Option[Boolean]) {
  val _op = op
  val _id = id
  val _lhs = lhs
  val _rhs = rhs

  // for conditional branch
  val isCond = cond
  val branchTaken = branch match { case Some(b) => b; case None => true }

  /* Assume that 
   * 1 : Statement information
   * 2 : Branch information
   * 3 : End of branch information
   */
  var infoType: Int = 0 
  def setType(t: Int) = infoType = t
  def getType = infoType

  def unpackSymbolicOption(x: Option[SymbolicValue]): String = x match { case Some(xx) => xx.getValue; case None => "" }
  def unpackStringOption(x: Option[String]): String = x match { case Some(xx) => xx; case None => "" }

  override def toString: String = {
    if (infoType == 3) return ""
    val x = unpackSymbolicOption(_id)
    val y = unpackSymbolicOption(_lhs) + " " + unpackStringOption(_op) + " " + unpackSymbolicOption(_rhs)  
    if (x.isEmpty) y
    else x + " = " + y
  }
  override def equals(another: Any) = this.toString == another.asInstanceOf[SymbolicInfo].toString 
}
