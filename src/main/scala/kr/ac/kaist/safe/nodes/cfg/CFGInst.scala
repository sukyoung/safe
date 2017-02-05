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
    var asite: AllocSite // XXX should be a value but for JS model for a while.
) extends CFGNormalInst(ir, block) {
  override def toString: String = {
    val proto = protoOpt.getOrElse("")
    s"$lhs := alloc($proto) @ #$asite"
  }
}

// x := allocArray(n)
case class CFGAllocArray(
    override val ir: IRNode,
    override val block: NormalBlock,
    lhs: CFGId,
    length: Int,
    var asite: AllocSite // XXX should be a value but for JS model for a while.
) extends CFGNormalInst(ir, block) {
  override def toString: String = s"$lhs := allocArray($length) @ #$asite"
}

// x := allocArg(n)
case class CFGAllocArg(
    override val ir: IRNode,
    override val block: NormalBlock,
    lhs: CFGId,
    length: Int,
    var asite: AllocSite // XXX should be a value but for JS model for a while.
) extends CFGNormalInst(ir, block) {
  override def toString: String = s"$lhs := allocArg($length) @ #$asite"
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
    asite1: AllocSite,
    asite2: AllocSite,
    asite3Opt: Option[AllocSite]
) extends CFGNormalInst(ir, block) {
  override def toString: String = {
    val name = nameOpt.getOrElse("")
    s"$lhs := function $name(${func.id}) @ #$asite1, #$asite2" + (asite3Opt match {
      case Some(asite) => s", #$asite"
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
    name: String,
    arguments: List[CFGExpr],
    var asiteOpt: Option[AllocSite] // XXX should be a value but for JS model for a while.
) extends CFGNormalInst(ir, block) {
  override def toString: String = {
    val arg = arguments.mkString(", ")
    s"$lhs := $name($arg)"
  } + (asiteOpt match {
    case Some(asite) => s" @ #$asite"
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
//   asite1: AllocSite,
//   asite2: AllocSite,
//   asite3: AllocSite
// ) extends CFGNormalInst(ir, block){
//   override def toString: String = s"async($modelType, $callType) @ #$asite1, #$asite2, #$asite3"
// }

/**
 * CFG Call Instruction
 */
sealed abstract class CFGCallInst(
    override val ir: IRNode,
    override val block: Call,
    val fun: CFGExpr,
    val thisArg: CFGExpr,
    val arguments: CFGExpr
) extends CFGInst(ir, block) {
  var asite: AllocSite // XXX should be a value but for JS model for a while.
}

// call(e1, e2, e3)
case class CFGCall(
    override val ir: IRNode,
    override val block: Call,
    override val fun: CFGExpr,
    override val thisArg: CFGExpr,
    override val arguments: CFGExpr,
    override var asite: AllocSite // XXX should be a value but for JS model for a while.
) extends CFGCallInst(ir, block, fun, thisArg, arguments) {
  override def toString: String = s"call($fun, $thisArg, $arguments) @ #$asite"
}

// construct(e1, e2, e3)
case class CFGConstruct(
    override val ir: IRNode,
    override val block: Call,
    override val fun: CFGExpr,
    override val thisArg: CFGExpr,
    override val arguments: CFGExpr,
    override var asite: AllocSite // XXX should be a value but for JS model for a while.
) extends CFGCallInst(ir, block, fun, thisArg, arguments) {
  override def toString: String = s"construct($fun, $thisArg, $arguments) @ #$asite"
}
