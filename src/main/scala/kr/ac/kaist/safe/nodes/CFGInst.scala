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

trait CFGNormalInst extends CFGInst

case class CFGAlloc(node: CFGNode, info: Info, lhs: CFGId, protoOpt: Option[CFGExpr], addr: Address) extends CFGNormalInst {
  override def toString: String = { val proto = protoOpt.getOrElse(""); s"$lhs := alloc($proto) @ #$addr" }
}

case class CFGAllocArray(node: CFGNode, info: Info, lhs: CFGId, length: Int, addr: Address) extends CFGNormalInst {
  override def toString: String = s"$lhs := allocArray($length) @ #$addr"
}

case class CFGAllocArg(node: CFGNode, info: Info, lhs: CFGId, length: Int, addr: Address) extends CFGNormalInst {
  override def toString: String = s"$lhs := allocArg($length) @ #$addr"
}

case class CFGExprStmt(node: CFGNode, info: Info, lhs: CFGId, right: CFGExpr) extends CFGNormalInst {
  override def toString: String = s"$lhs := $right"
}

case class CFGDelete(node: CFGNode, info: Info, lhs: CFGId, expr: CFGExpr) extends CFGNormalInst {
  override def toString: String = s"$lhs := delete($expr)"
}

case class CFGDeleteProp(node: CFGNode, info: Info, lhs: CFGId, obj: CFGExpr, index: CFGExpr) extends CFGNormalInst {
  override def toString: String = s"$lhs := delete($obj, $index)"
}

case class CFGStore(node: CFGNode, info: Info, obj: CFGExpr, index: CFGExpr, rhs: CFGExpr) extends CFGNormalInst {
  override def toString: String = s"$obj[$index] := $rhs"
}

case class CFGFunExpr(node: CFGNode, info: Info, lhs: CFGId, nameOpt: Option[CFGId], fid: FunctionId, addr1: Address, addr2: Address, addr3Opt: Option[Address]) extends CFGNormalInst {
  override def toString: String = {
    val name = nameOpt.getOrElse("")
    s"$lhs := function $name($fid) @ #$addr1, #$addr2" + (addr3Opt match {
      case Some(addr) => s", #$addr"
      case None => ""
    })
  }
}

case class CFGAssert(node: CFGNode, info: Info, expr: CFGExpr, flag: Boolean) extends CFGNormalInst {
  override def toString: String = s"assert($expr)"
}

case class CFGCond(node: CFGNode, info: Info, expr: CFGExpr, isEvent: Boolean = false) extends CFGNormalInst {
  override def toString: String = s"cond($expr)"
}

case class CFGCatch(node: CFGNode, info: Info, name: CFGId) extends CFGNormalInst {
  override def toString: String = s"catch($name)"
}

case class CFGReturn(node: CFGNode, info: Info, exprOpt: Option[CFGExpr]) extends CFGNormalInst {
  override def toString: String = { val expr = exprOpt.getOrElse(""); s"return($expr)" }
}

case class CFGThrow(node: CFGNode, info: Info, expr: CFGExpr) extends CFGNormalInst {
  override def toString: String = s"throw($expr)"
}

case class CFGNoOp(node: CFGNode, info: Info, desc: String) extends CFGNormalInst {
  override def toString: String = s"noop($desc)"
}

case class CFGInternalCall(node: CFGNode, info: Info, lhs: CFGId, fun: CFGId, arguments: List[CFGExpr], addrOpt: Option[Address]) extends CFGNormalInst {
  override def toString: String = { val arg = arguments.mkString(", "); s"$lhs := $fun($arg)" } + (addrOpt match {
    case Some(addr) => s" @ #$addr"
    case None => ""
  })
}

// TODO revert after modeling
// case class CFGAPICall(node: CFGNode, info: Info, model: String, fun: String, arguments: CFGExpr) extends CFGNormalInst {
//   override def toString: String = s"[]$model.$fun($arguments)"
// }
// 
// case class CFGAsyncCall(node: CFGNode, info: Info, modelType: String, callType: String, addr1: Address, addr2: Address, addr3: Address) extends CFGNormalInst {
//   override def toString: String = s"async($modelType, $callType) @ #$addr1, #$addr2, #$addr3"
// }

// call type inst
trait CFGCallInst extends CFGInst

case class CFGCall(node: CFGNode, info: Info, fun: CFGExpr, thisArg: CFGExpr, arguments: CFGExpr, addr1: Address, addr2: Address) extends CFGCallInst {
  override def toString: String = s"call($fun, $thisArg, $arguments) @ #$addr1"
}

case class CFGConstruct(node: CFGNode, info: Info, cons: CFGExpr, thisArg: CFGExpr, arguments: CFGExpr, addr1: Address, addr2: Address) extends CFGCallInst {
  override def toString: String = s"construct($cons, $thisArg, $arguments) @ #$addr1, #$addr2"
}
