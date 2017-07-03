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

class ConstraintForm() {
  //var lhs: String = null
  var lhs: SymbolicValue = null
  var op: Option[String] = None
  var rhs: Option[ConstraintForm] = None

  def makeConstraint(id: Option[SymbolicValue], left: Option[SymbolicValue], operator: Option[String], right: Option[SymbolicValue]): Unit = id match {
    case Some(_id) =>
      lhs = _id
      op = Some("=")
      val tmp = new ConstraintForm
      tmp.makeConstraint(None, left, operator, right)
      rhs = Some(tmp)
    // Conditional information
    case None => left match {
      case Some(_lhs) =>
        lhs = _lhs
        op = operator
        rhs = right match {
          case Some(_right) =>
            var tmp = new ConstraintForm
            tmp.makeConstraint(None, Some(_right), None, None)
            Some(tmp)
          case None => None
        }
      case None =>
    }
  }

  def getLhs: SymbolicValue = lhs
  def getOp: Option[String] = op
  def getRhs: Option[ConstraintForm] = rhs

  def objectRelated: Boolean = {
    if (lhs == null) return false
    val left = lhs.isObject || lhs.isNull
    val right = rhs.map(_.objectRelated).getOrElse(false)
    left || right
  }

  var branchConstraint: Boolean = false
  def setBranchConstraint(): Unit = branchConstraint = true
  def isBranchConstraint: Boolean = branchConstraint

  def getSymbolicValues(): List[SymbolicValue] = {
    val result = List(getLhs)
    getRhs match {
      case Some(x) => result ::: x.getSymbolicValues
      case None => result
    }
  }

  override def toString: String = {
    val operator = op match {
      case Some(x) => " " + x + " "
      case None => ""
    }
    val right = rhs match {
      case Some(x) => x.toString
      case None => ""
    }
    lhs.toString + operator + right
  }
}

