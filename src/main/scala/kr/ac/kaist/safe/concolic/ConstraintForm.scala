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

object ConstraintForm {

  def makeConstraint(
    id: Option[SymbolicValue],
    left: Option[SymbolicValue],
    operator: Option[String],
    right: Option[SymbolicValue]
  ): ConstraintForm = {
    val newConstraintForm = new ConstraintForm
    id match {
      case Some(id) =>
        newConstraintForm.lhs = id
        newConstraintForm.op = Some("=")
        val tmp = ConstraintForm.makeConstraint(None, left, operator, right)
        newConstraintForm.rhs = Some(tmp)
      // Conditional information
      case None => left match {
        case Some(lhs) =>
          newConstraintForm.lhs = lhs
          newConstraintForm.op = operator
          newConstraintForm.rhs = right match {
            case Some(right) =>
              val tmp = ConstraintForm.makeConstraint(None, Some(right), None, None)
              Some(tmp)
            case None => None
          }
        case None =>
      }
    }
    newConstraintForm
  }
}

class ConstraintForm() {
  //var lhs: String = null
  var lhs: SymbolicValue = null
  var op: Option[String] = None
  var rhs: Option[ConstraintForm] = None

  def getLhs: SymbolicValue = lhs
  def getOp: Option[String] = op
  def getRhs: Option[ConstraintForm] = rhs

  def objectRelated: Boolean = {
    if (lhs == null) return false
    val left = lhs.isObject || lhs.isNull
    val right = rhs.exists(_.objectRelated)
    left || right
  }

  private var branchConstraint: Boolean = false
  def setBranchConstraint(): Unit = branchConstraint = true
  def isBranchConstraint: Boolean = branchConstraint

  def getSymbolicValues: List[SymbolicValue] = {
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

