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

import kr.ac.kaist.safe.analyzer.domain.Address
import kr.ac.kaist.safe.cfg_builder.{ FunctionId, CFGFunction, CFGBlock, CFGNormalBlock, Call }
import kr.ac.kaist.safe.util.NodeUtil

sealed abstract class CFGNode(override val info: CFGNodeInfo)
    extends Node(info: NodeInfo) {
  override def toString(indent: Int): String = " " * indent + this
}

////////////////////////////////////////////////////////////////////////////////
// CFG Instruction
////////////////////////////////////////////////////////////////////////////////

sealed abstract class CFGInst(
    override val info: CFGNodeInfo,
    block: CFGBlock
) extends CFGNode(info) {
  val id: InstId = CFGInst.getId
}
object CFGInst {
  private var counter: Int = 0
  private def getId: Int = { counter += 1; counter - 1 }
  def resetId: Unit = counter = 0
}

/**
 * CFG Normal Instruction
 */
sealed abstract class CFGNormalInst(
  override val info: CFGNodeInfo,
  val block: CFGNormalBlock
) extends CFGInst(info, block)

// x := alloc(e^?)
case class CFGAlloc(
    override val info: CFGNodeInfo,
    override val block: CFGNormalBlock,
    lhs: CFGId,
    protoOpt: Option[CFGExpr],
    addr: Address
) extends CFGNormalInst(info, block) {
  override def toString: String = {
    val proto = protoOpt.getOrElse("")
    s"$lhs := alloc($proto) @ #$addr"
  }
}

// x := allocArray(n)
case class CFGAllocArray(
    override val info: CFGNodeInfo,
    override val block: CFGNormalBlock,
    lhs: CFGId,
    length: Int,
    addr: Address
) extends CFGNormalInst(info, block) {
  override def toString: String = s"$lhs := allocArray($length) @ #$addr"
}

// x := allocArg(n)
case class CFGAllocArg(
    override val info: CFGNodeInfo,
    override val block: CFGNormalBlock,
    lhs: CFGId,
    length: Int,
    addr: Address
) extends CFGNormalInst(info, block) {
  override def toString: String = s"$lhs := allocArg($length) @ #$addr"
}

// x := e
case class CFGExprStmt(
    override val info: CFGNodeInfo,
    override val block: CFGNormalBlock,
    lhs: CFGId, right: CFGExpr
) extends CFGNormalInst(info, block) {
  override def toString: String = s"$lhs := $right"
}

// x := delete(e)
case class CFGDelete(
    override val info: CFGNodeInfo,
    override val block: CFGNormalBlock,
    lhs: CFGId,
    expr: CFGExpr
) extends CFGNormalInst(info, block) {
  override def toString: String = s"$lhs := delete($expr)"
}

// x := delete(e1, e2)
case class CFGDeleteProp(
    override val info: CFGNodeInfo,
    override val block: CFGNormalBlock,
    lhs: CFGId,
    obj: CFGExpr,
    index: CFGExpr
) extends CFGNormalInst(info, block) {
  override def toString: String = s"$lhs := delete($obj, $index)"
}

// e1[e2] := e3
case class CFGStore(
    override val info: CFGNodeInfo,
    override val block: CFGNormalBlock,
    obj: CFGExpr,
    index: CFGExpr,
    rhs: CFGExpr
) extends CFGNormalInst(info, block) {
  override def toString: String = s"$obj[$index] := $rhs"
}

// x1 := function x_2^?(f)
case class CFGFunExpr(
    override val info: CFGNodeInfo,
    override val block: CFGNormalBlock,
    lhs: CFGId,
    nameOpt: Option[CFGId],
    func: CFGFunction,
    addr1: Address,
    addr2: Address,
    addr3Opt: Option[Address]
) extends CFGNormalInst(info, block) {
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
    override val info: CFGNodeInfo,
    override val block: CFGNormalBlock,
    expr: CFGExpr,
    flag: Boolean
) extends CFGNormalInst(info, block) {
  override def toString: String = s"assert($expr)"
}

// cond(x)
case class CFGCond(
    override val info: CFGNodeInfo,
    override val block: CFGNormalBlock,
    expr: CFGExpr,
    isEvent: Boolean = false
) extends CFGNormalInst(info, block) {
  override def toString: String = s"cond($expr)"
}

// catch(x)
case class CFGCatch(
    override val info: CFGNodeInfo,
    override val block: CFGNormalBlock,
    name: CFGId
) extends CFGNormalInst(info, block) {
  override def toString: String = s"catch($name)"
}

// return(e^?)
case class CFGReturn(
    override val info: CFGNodeInfo,
    override val block: CFGNormalBlock,
    exprOpt: Option[CFGExpr]
) extends CFGNormalInst(info, block) {
  override def toString: String = {
    val expr = exprOpt.getOrElse("")
    s"return($expr)"
  }
}

// throw(e)
case class CFGThrow(
    override val info: CFGNodeInfo,
    override val block: CFGNormalBlock,
    expr: CFGExpr
) extends CFGNormalInst(info, block) {
  override def toString: String = s"throw($expr)"
}

// noop
case class CFGNoOp(
    override val info: CFGNodeInfo,
    override val block: CFGNormalBlock,
    desc: String
) extends CFGNormalInst(info, block) {
  override def toString: String = s"noop($desc)"
}

// x := <>x(x^*)
case class CFGInternalCall(
    override val info: CFGNodeInfo,
    override val block: CFGNormalBlock,
    lhs: CFGId,
    fun: CFGId,
    arguments: List[CFGExpr],
    addrOpt: Option[Address]
) extends CFGNormalInst(info, block) {
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
//   override val info: CFGNodeInfo,
//   override val block: CFGNormalBlock,
//   model: String,
//   fun: String,
//   arguments: CFGExpr
// ) extends CFGNormalInst(info, block){
//   override def toString: String = s"[]$model.$fun($arguments)"
// }
// 
// case class CFGAsyncCall(
//   override val info: CFGNodeInfo,
//   override val block: CFGNormalBlock,
//   modelType: String,
//   callType: String,
//   addr1: Address,
//   addr2: Address,
//   addr3: Address
// ) extends CFGNormalInst(info, block){
//   override def toString: String = s"async($modelType, $callType) @ #$addr1, #$addr2, #$addr3"
// }

/**
 * CFG Call Instruction
 */
sealed abstract class CFGCallInst(
  override val info: CFGNodeInfo,
  val block: Call
) extends CFGInst(info, block)

// call(e1, e2, e3)
case class CFGCall(
    override val info: CFGNodeInfo,
    override val block: Call,
    fun: CFGExpr,
    thisArg: CFGExpr,
    arguments: CFGExpr,
    addr1: Address,
    addr2: Address
) extends CFGCallInst(info, block) {
  override def toString: String = s"call($fun, $thisArg, $arguments) @ #$addr1"
}

// construct(e1, e2, e3)
case class CFGConstruct(
    override val info: CFGNodeInfo,
    override val block: Call,
    cons: CFGExpr,
    thisArg: CFGExpr,
    arguments: CFGExpr,
    addr1: Address,
    addr2: Address
) extends CFGCallInst(info, block) {
  override def toString: String = s"construct($cons, $thisArg, $arguments) @ #$addr1, #$addr2"
}

////////////////////////////////////////////////////////////////////////////////
// CFG Expression
////////////////////////////////////////////////////////////////////////////////

sealed abstract class CFGExpr(override val info: CFGNodeInfo)
  extends CFGNode(info)

// variable reference
case class CFGVarRef(
    override val info: CFGNodeInfo,
    id: CFGId
) extends CFGExpr(info) {
  override def toString: String = s"$id"
}

// binary operation
case class CFGBin(
    override val info: CFGNodeInfo,
    first: CFGExpr,
    op: IROp,
    second: CFGExpr
) extends CFGExpr(info) {
  override def toString: String = { val text = op.text; s"$first $text $second" }
}

// unary operation
case class CFGUn(
    override val info: CFGNodeInfo,
    op: IROp,
    expr: CFGExpr
) extends CFGExpr(info) {
  override def toString: String = { val text = op.text; s"$text $expr" }
}

// load
case class CFGLoad(
    override val info: CFGNodeInfo,
    obj: CFGExpr,
    index: CFGExpr
) extends CFGExpr(info) {
  override def toString: String = s"$obj[$index]"
}

// this
case class CFGThis(
    override val info: CFGNodeInfo
) extends CFGExpr(info) {
  override def toString: String = "this"
}

// number
case class CFGNumber(
    override val info: CFGNodeInfo,
    text: String,
    num: Double
) extends CFGExpr(info) {
  override def toString: String = text
}

// string
case class CFGString(
    override val info: CFGNodeInfo,
    str: String
) extends CFGExpr(info) {
  override def toString: String = "\"" + NodeUtil.pp(str) + "\""
}

// boolean
case class CFGBool(
    override val info: CFGNodeInfo,
    bool: Boolean
) extends CFGExpr(info) {
  override def toString: String = if (bool) "true" else "false"
}

// null
case class CFGNull(
    override val info: CFGNodeInfo
) extends CFGExpr(info) {
  override def toString: String = "null"
}

////////////////////////////////////////////////////////////////////////////////
// CFG Expression
////////////////////////////////////////////////////////////////////////////////

sealed abstract class CFGId(
  override val info: CFGNodeInfo,
  val text: String,
  val kind: VarKind
) extends CFGNode(info)

case class CFGUserId(
    override val info: CFGNodeInfo,
    override val text: String,
    override val kind: VarKind,
    originalName: String,
    fromWith: Boolean
) extends CFGId(info, text, kind) {
  override def toString: String = NodeUtil.pp(text)
}

case class CFGTempId(
    override val info: CFGNodeInfo,
    override val text: String,
    override val kind: VarKind
) extends CFGId(info, text, kind) {
  override def toString: String = NodeUtil.pp(text)
}

sealed abstract class VarKind
case object GlobalVar extends VarKind
case object PureLocalVar extends VarKind
case object CapturedVar extends VarKind
case object CapturedCatchVar extends VarKind
