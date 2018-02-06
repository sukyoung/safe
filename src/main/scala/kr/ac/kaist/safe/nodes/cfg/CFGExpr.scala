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

package kr.ac.kaist.safe.nodes.cfg

import kr.ac.kaist.safe.nodes.ir.IRNode
import kr.ac.kaist.safe.util._

sealed trait CFGExpr extends CFGNode {
  val uniqueId: Long = CFGExpr.getId
}
object CFGExpr {
  private var idCount: Long = 0
  private def getId: Long = { val id = idCount; idCount += 1; id }
}

// variable reference
case class CFGVarRef(
    ir: IRNode,
    id: CFGId
) extends CFGExpr {
  override def toString: String = s"$id"
}

// load
case class CFGLoad(
    ir: IRNode,
    obj: CFGExpr,
    index: CFGExpr
) extends CFGExpr {
  override def toString: String = s"$obj[$index]"
}

// this
case class CFGThis(
    ir: IRNode
) extends CFGExpr {
  override def toString: String = "this"
}

// binary operation
case class CFGBin(
    ir: IRNode,
    first: CFGExpr,
    op: EJSOp,
    second: CFGExpr
) extends CFGExpr {
  override def toString: String = s"$first $op $second"
}

// unary operation
case class CFGUn(
    ir: IRNode,
    op: EJSOp,
    expr: CFGExpr
) extends CFGExpr {
  override def toString: String = s"$op $expr"
}

case class CFGInternalValue(
    ir: IRNode,
    name: String
) extends CFGExpr {
  override def toString: String = s"<>$name<>"
}

case class CFGVal(
    value: EJSVal
) extends CFGExpr {
  val ir: IRNode = NodeUtil.TEMP_IR
  override def toString: String = value.toString
}
object CFGVal {
  def apply(text: String, num: Double): CFGVal = CFGVal(EJSNumber(text, num))
  def apply(str: String): CFGVal = CFGVal(EJSString(str))
  def apply(bool: Boolean): CFGVal = CFGVal(EJSBool(bool))
}
