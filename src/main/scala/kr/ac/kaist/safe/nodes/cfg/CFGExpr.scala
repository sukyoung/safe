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

package kr.ac.kaist.safe.nodes.cfg

import kr.ac.kaist.safe.nodes.ir.IRNode
import kr.ac.kaist.safe.util._

sealed abstract class CFGExpr(
  override val ir: IRNode
) extends CFGNode(ir)

// variable reference
case class CFGVarRef(
    override val ir: IRNode,
    id: CFGId
) extends CFGExpr(ir) {
  override def toString: String = s"$id"
}

// load
case class CFGLoad(
    override val ir: IRNode,
    obj: CFGExpr,
    index: CFGExpr
) extends CFGExpr(ir) {
  override def toString: String = s"$obj[$index]"
}

// this
case class CFGThis(
    override val ir: IRNode
) extends CFGExpr(ir) {
  override def toString: String = "this"
}

// binary operation
case class CFGBin(
    override val ir: IRNode,
    first: CFGExpr,
    op: EJSOp,
    second: CFGExpr
) extends CFGExpr(ir) {
  override def toString: String = s"$first $op $second"
}

// unary operation
case class CFGUn(
    override val ir: IRNode,
    op: EJSOp,
    expr: CFGExpr
) extends CFGExpr(ir) {
  override def toString: String = s"$op $expr"
}

case class CFGInternalValue(
    override val ir: IRNode,
    name: String
) extends CFGExpr(ir) {
  override def toString: String = s"<>$name<>"
}

case class CFGVal(
    value: EJSVal
) extends CFGExpr(NodeUtil.TEMP_IR) {
  override def toString: String = value.toString
}
object CFGVal {
  def apply(text: String, num: Double): CFGVal = CFGVal(EJSNumber(text, num))
  def apply(str: String): CFGVal = CFGVal(EJSString(str))
  def apply(bool: Boolean): CFGVal = CFGVal(EJSBool(bool))
}
