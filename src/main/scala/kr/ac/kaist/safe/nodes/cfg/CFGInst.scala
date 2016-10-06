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

sealed abstract class CFGInst(
    override val ir: IRNode,
    val block: CFGBlock
) extends CFGNode(ir) {
  val id: InstId = block.getIId

  // equals
  override def equals(other: Any): Boolean = other match {
    case (inst: CFGInst) =>
      inst.block == block &&
        inst.id == id
    case _ => false
  }
}

/**
 * CFG Normal Instruction
 */
sealed abstract class CFGNormalInst(
  override val ir: IRNode,
  override val block: NormalBlock
) extends CFGInst(ir, block)

// x := alloc(e^?)
case class CFGAlloc(
    override val ir: IRNode,
    override val block: NormalBlock,
    lhs: CFGId,
    protoOpt: Option[CFGExpr],
    addr: Address
) extends CFGNormalInst(ir, block) {
  override def toString: String = {
    val proto = protoOpt.getOrElse("")
    s"$lhs := alloc($proto) @ #$addr"
  }
}

// x := allocArray(n)
case class CFGAllocArray(
    override val ir: IRNode,
    override val block: NormalBlock,
    lhs: CFGId,
    length: Int,
    addr: Address
) extends CFGNormalInst(ir, block) {
  override def toString: String = s"$lhs := allocArray($length) @ #$addr"
}

// x := allocArg(n)
case class CFGAllocArg(
    override val ir: IRNode,
    override val block: NormalBlock,
    lhs: CFGId,
    length: Int,
    addr: Address
) extends CFGNormalInst(ir, block) {
  override def toString: String = s"$lhs := allocArg($length) @ #$addr"
}

// this := enterCode(e)
case class CFGEnterCode(
    override val ir: IRNode,
    override val block: NormalBlock,
    lhs: CFGId,
    thisExpr: CFGExpr
) extends CFGNormalInst(ir, block) {
  override def toString: String = s"$lhs := enterCode($thisExpr)"
}

// x := e
case class CFGExprStmt(
    override val ir: IRNode,
    override val block: NormalBlock,
    lhs: CFGId, right: CFGExpr
) extends CFGNormalInst(ir, block) {
  override def toString: String = s"$lhs := $right"
}

// x := delete(e)
case class CFGDelete(
    override val ir: IRNode,
    override val block: NormalBlock,
    lhs: CFGId,
    expr: CFGExpr
) extends CFGNormalInst(ir, block) {
  override def toString: String = s"$lhs := delete($expr)"
}

// x := delete(e1, e2)
case class CFGDeleteProp(
    override val ir: IRNode,
    override val block: NormalBlock,
    lhs: CFGId,
    obj: CFGExpr,
    index: CFGExpr
) extends CFGNormalInst(ir, block) {
  override def toString: String = s"$lhs := delete($obj, $index)"
}

// e1[e2] := e3
case class CFGStore(
    override val ir: IRNode,
    override val block: NormalBlock,
    obj: CFGExpr,
    index: CFGExpr,
    rhs: CFGExpr
) extends CFGNormalInst(ir, block) {
  override def toString: String = s"$obj[$index] := $rhs"
}

// e1[e2] := s
case class CFGStoreStringIdx(
    override val ir: IRNode,
    override val block: NormalBlock,
    obj: CFGExpr,
    index: EJSString,
    rhs: CFGExpr
) extends CFGNormalInst(ir, block) {
  override def toString: String = s"$obj[$index] := $rhs"
}

// x1 := function x_2^?(f)
case class CFGFunExpr(
    override val ir: IRNode,
    override val block: NormalBlock,
    lhs: CFGId,
    nameOpt: Option[CFGId],
    func: CFGFunction,
    addr1: Address,
    addr2: Address,
    addr3Opt: Option[Address]
) extends CFGNormalInst(ir, block) {
  override def toString: String = {
    val name = nameOpt.getOrElse("")
    s"$lhs := function $name(${func.id}) @ #$addr1, #$addr2" + (addr3Opt match {
      case Some(addr) => s", #$addr"
      case None => ""
    })
  }
}

// assert(e1 x e2)
case class CFGAssert(
    override val ir: IRNode,
    override val block: NormalBlock,
    expr: CFGExpr,
    flag: Boolean
) extends CFGNormalInst(ir, block) {
  override def toString: String = s"assert($expr)"
}

// cond(x)
case class CFGCond(
    override val ir: IRNode,
    override val block: NormalBlock,
    expr: CFGExpr,
    isEvent: Boolean = false
) extends CFGNormalInst(ir, block) {
  override def toString: String = s"cond($expr)"
}

// catch(x)
case class CFGCatch(
    override val ir: IRNode,
    override val block: NormalBlock,
    name: CFGId
) extends CFGNormalInst(ir, block) {
  override def toString: String = s"catch($name)"
}

// return(e^?)
case class CFGReturn(
    override val ir: IRNode,
    override val block: NormalBlock,
    exprOpt: Option[CFGExpr]
) extends CFGNormalInst(ir, block) {
  override def toString: String = {
    val expr = exprOpt.getOrElse("")
    s"return($expr)"
  }
}

// throw(e)
case class CFGThrow(
    override val ir: IRNode,
    override val block: NormalBlock,
    expr: CFGExpr
) extends CFGNormalInst(ir, block) {
  override def toString: String = s"throw($expr)"
}

// noop
case class CFGNoOp(
    override val ir: IRNode,
    override val block: NormalBlock,
    desc: String
) extends CFGNormalInst(ir, block) {
  override def toString: String = s"noop($desc)"
}

// x := <>x(x^*)
case class CFGInternalCall(
    override val ir: IRNode,
    override val block: NormalBlock,
    lhs: CFGId,
    fun: CFGId,
    arguments: List[CFGExpr],
    addrOpt: Option[Address]
) extends CFGNormalInst(ir, block) {
  override def toString: String = {
    val arg = arguments.mkString(", ")
    s"$lhs := $fun($arg)"
  } + (addrOpt match {
    case Some(addr) => s" @ #$addr"
    case None => ""
  })
}

// TODO revert after modeling
// case class CFGAPICall(
//   override val ir: IRNode,
//   override val block: NormalBlock,
//   model: String,
//   fun: String,
//   arguments: CFGExpr
// ) extends CFGNormalInst(ir, block){
//   override def toString: String = s"[]$model.$fun($arguments)"
// }
// 
// case class CFGAsyncCall(
//   override val ir: IRNode,
//   override val block: NormalBlock,
//   modelType: String,
//   callType: String,
//   addr1: Address,
//   addr2: Address,
//   addr3: Address
// ) extends CFGNormalInst(ir, block){
//   override def toString: String = s"async($modelType, $callType) @ #$addr1, #$addr2, #$addr3"
// }

/**
 * CFG Call Instruction
 */
sealed abstract class CFGCallInst(
  override val ir: IRNode,
  override val block: Call,
  val fun: CFGExpr,
  val thisArg: CFGExpr,
  val arguments: CFGExpr,
  val addr1: Address,
  val addr2: Address
) extends CFGInst(ir, block)

// call(e1, e2, e3)
case class CFGCall(
    override val ir: IRNode,
    override val block: Call,
    override val fun: CFGExpr,
    override val thisArg: CFGExpr,
    override val arguments: CFGExpr,
    override val addr1: Address,
    override val addr2: Address
) extends CFGCallInst(ir, block, fun, thisArg, arguments, addr1, addr2) {
  override def toString: String = s"call($fun, $thisArg, $arguments) @ #$addr1"
}

// construct(e1, e2, e3)
case class CFGConstruct(
    override val ir: IRNode,
    override val block: Call,
    override val fun: CFGExpr,
    override val thisArg: CFGExpr,
    override val arguments: CFGExpr,
    override val addr1: Address,
    override val addr2: Address
) extends CFGCallInst(ir, block, fun, thisArg, arguments, addr1, addr2) {
  override def toString: String = s"construct($fun, $thisArg, $arguments) @ #$addr1, #$addr2"
}
