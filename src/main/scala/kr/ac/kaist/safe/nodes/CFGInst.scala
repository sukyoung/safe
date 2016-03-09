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

sealed abstract class CFGInst extends CFGInfo {
  val id: InstId = CFGInst.getId
  val node: CFGNode
}

object CFGInst {
  private var counter: Int = 0
  private def getId: Int = { counter += 1; counter - 1 }
  def resetId: Unit = counter = 0
}

case class CFGAlloc(node: CFGNode, info: Info, lhs: CFGId, protoOpt: Option[CFGExpr], addr: Address) extends CFGInst {
  override def toString: String = "%s := alloc(%s) @ #%s".format(lhs, protoOpt.getOrElse(""), addr)
}

case class CFGAllocArray(node: CFGNode, info: Info, lhs: CFGId, length: Int, addr: Address) extends CFGInst {
  override def toString: String = "%s := allocArray(%s) @ #%s".format(lhs, length, addr)
}

case class CFGAllocArg(node: CFGNode, info: Info, lhs: CFGId, length: Int, addr: Address) extends CFGInst {
  override def toString: String = "%s := allocArg(%s) @ #%s".format(lhs, length, addr)
}

case class CFGExprStmt(node: CFGNode, info: Info, lhs: CFGId, right: CFGExpr) extends CFGInst {
  override def toString: String = "%s := %s".format(lhs, right)
}

case class CFGDelete(node: CFGNode, info: Info, lhs: CFGId, expr: CFGExpr) extends CFGInst {
  override def toString: String = "%s := delete(%s)".format(lhs, expr)
}

case class CFGDeleteProp(node: CFGNode, info: Info, lhs: CFGId, obj: CFGExpr, index: CFGExpr) extends CFGInst {
  override def toString: String = "%s := delete(%s, %s)".format(lhs, obj, index)
}

case class CFGStore(node: CFGNode, info: Info, obj: CFGExpr, index: CFGExpr, rhs: CFGExpr) extends CFGInst {
  override def toString: String = "%s[%s] := %s".format(obj, index, rhs)
}

case class CFGFunExpr(node: CFGNode, info: Info, lhs: CFGId, nameOpt: Option[CFGId], fid: FunctionId, addr1: Address, addr2: Address, addr3Opt: Option[Address]) extends CFGInst {
  override def toString: String = {
    "%s := function %s(%s) @ #%s, #%s".format(lhs, nameOpt.getOrElse(""), fid, addr1, addr2) + (addr3Opt match {
      case Some(addr) => ", #%s".format(addr)
      case None => ""
    })
  }
}

case class CFGAssert(node: CFGNode, info: Info, expr: CFGExpr, flag: Boolean) extends CFGInst {
  override def toString: String = "assert(%s)".format(expr)
}

case class CFGCond(node: CFGNode, info: Info, expr: CFGExpr, isEvent: Boolean = false) extends CFGInst {
  override def toString: String = "cond(%s)".format(expr)
}

case class CFGCatch(node: CFGNode, info: Info, name: CFGId) extends CFGInst {
  override def toString: String = "catch(%s)".format(name)
}

case class CFGReturn(node: CFGNode, info: Info, exprOpt: Option[CFGExpr]) extends CFGInst {
  override def toString: String = "return(%s)".format(exprOpt.getOrElse(""))
}

case class CFGThrow(node: CFGNode, info: Info, expr: CFGExpr) extends CFGInst {
  override def toString: String = "throw(%s)".format(expr)
}

case class CFGNoOp(node: CFGNode, info: Info, desc: String) extends CFGInst {
  override def toString: String = "noop(%s)".format(desc)
}

// call type inst
trait CFGCallInst extends CFGInst

case class CFGCall(node: CFGNode, info: Info, fun: CFGExpr, thisArg: CFGExpr, arguments: CFGExpr, addr1: Address, addr2: Address) extends CFGCallInst {
  override def toString: String = "call(%s, %s, %s) @ #%s".format(fun, thisArg, arguments, addr1)
}

case class CFGInternalCall(node: CFGNode, info: Info, lhs: CFGId, fun: CFGId, arguments: List[CFGExpr], addrOpt: Option[Address]) extends CFGCallInst {
  override def toString: String = "%s := %s(%s)".format(lhs, fun, arguments.mkString(", ")) + (addrOpt match {
    case Some(addr) => " @ #%s".format(addr)
    case None => ""
  })
}

case class CFGConstruct(node: CFGNode, info: Info, cons: CFGExpr, thisArg: CFGExpr, arguments: CFGExpr, addr1: Address, addr2: Address) extends CFGCallInst {
  override def toString: String = "construct(%s, %s, %s) @ #%s, #%s".format(cons, thisArg, arguments, addr1, addr2)
}

case class CFGAPICall(node: CFGNode, info: Info, model: String, fun: String, arguments: CFGExpr) extends CFGInst {
  override def toString: String = "[]%s.%s(%s)".format(model, fun, arguments)
}

case class CFGAsyncCall(node: CFGNode, info: Info, modelType: String, callType: String, addr1: Address, addr2: Address, addr3: Address) extends CFGCallInst {
  override def toString: String = "async(%s, %s) @ #%s, #%s, #%s".format(modelType, callType, addr1, addr2, addr3)
}
