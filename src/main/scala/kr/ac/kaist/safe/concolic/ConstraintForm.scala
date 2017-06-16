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
import _root_.edu.rice.cs.plt.tuple.{ Option => JavaOption }
import _root_.kr.ac.kaist.safe.util.useful.Options._
// TODO REM import kr.ac.kaist.jsaf.nodes_util.{IRFactory => IF, NodeUtil => NU}

class ConstraintForm() {
  //var lhs: String = null
  var lhs: SymbolicValue = null
  var op: Option[String] = None
  var rhs: Option[ConstraintForm] = None

  def makeConstraint(id: Option[SymbolicValue], left: Option[SymbolicValue], operator: Option[String], right: Option[SymbolicValue]): Unit = id match {
    case Some(_id) =>
      lhs = _id
      op = Some("=")
      var tmp = new ConstraintForm
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
  def getJavaOp: JavaOption[String] = toJavaOption(op)
  def getRhs: Option[ConstraintForm] = rhs
  def getJavaRhs: JavaOption[ConstraintForm] = toJavaOption(rhs)

  def objectRelated: Boolean = {
    if (lhs == null) return false
    var left = lhs.isObject || lhs.isNull
    var right = false
    if (rhs.isSome) {
      right = rhs.unwrap.objectRelated
    }
    left || right
  }

  var branchConstraint: Boolean = false
  def setBranchConstraint() = branchConstraint = true
  def isBranchConstraint() = branchConstraint

  def getSymbolicValues(): List[SymbolicValue] = {
    var result = List(getLhs)
    getRhs match {
      case Some(x) => result ::: x.getSymbolicValues
      case None => result
    }
  }

  override def toString: String = {
    var operator = op match {
      case Some(x) => " " + x + " "
      case None => ""
    }
    var right = rhs match {
      case Some(x) => x.toString
      case None => ""
    }
    return lhs.toString + operator + right
  }
}

