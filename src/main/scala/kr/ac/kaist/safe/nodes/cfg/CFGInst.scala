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

sealed trait CFGInst extends CFGNode {
  val block: CFGBlock
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
sealed trait CFGNormalInst extends CFGInst {
  val block: NormalBlock
}

// x := alloc(e^?)
case class CFGAlloc(
    ir: IRNode,
    block: NormalBlock,
    lhs: CFGId,
    protoOpt: Option[CFGExpr],
    var asite: AllocSite // XXX should be a value but for JS model for a while.
) extends CFGNormalInst {
  override def toString: String = {
    val proto = protoOpt.getOrElse("")
    s"$lhs := alloc($proto) @ $asite"
  }
}

// x := allocArray(n)
case class CFGAllocArray(
    ir: IRNode,
    block: NormalBlock,
    lhs: CFGId,
    length: Int,
    var asite: AllocSite // XXX should be a value but for JS model for a while.
) extends CFGNormalInst {
  override def toString: String = s"$lhs := allocArray($length) @ $asite"
}

// x := allocArg(n)
case class CFGAllocArg(
    ir: IRNode,
    block: NormalBlock,
    lhs: CFGId,
    length: Int,
    var asite: AllocSite // XXX should be a value but for JS model for a while.
) extends CFGNormalInst {
  override def toString: String = s"$lhs := allocArg($length) @ $asite"
}

// this := enterCode(e)
case class CFGEnterCode(
    ir: IRNode,
    block: NormalBlock,
    lhs: CFGId,
    thisExpr: CFGExpr
) extends CFGNormalInst {
  override def toString: String = s"$lhs := enterCode($thisExpr)"
}

// x := e
case class CFGExprStmt(
    ir: IRNode,
    block: NormalBlock,
    lhs: CFGId, right: CFGExpr
) extends CFGNormalInst {
  override def toString: String = s"$lhs := $right"
}

// x := delete(e)
case class CFGDelete(
    ir: IRNode,
    block: NormalBlock,
    lhs: CFGId,
    expr: CFGExpr
) extends CFGNormalInst {
  override def toString: String = s"$lhs := delete($expr)"
}

// x := delete(e1, e2)
case class CFGDeleteProp(
    ir: IRNode,
    block: NormalBlock,
    lhs: CFGId,
    obj: CFGExpr,
    index: CFGExpr
) extends CFGNormalInst {
  override def toString: String = s"$lhs := delete($obj, $index)"
}

// e1[e2] := e3
case class CFGStore(
    ir: IRNode,
    block: NormalBlock,
    obj: CFGExpr,
    index: CFGExpr,
    rhs: CFGExpr
) extends CFGNormalInst {
  override def toString: String = s"$obj[$index] := $rhs"
}

// e1[e2] := s
case class CFGStoreStringIdx(
    ir: IRNode,
    block: NormalBlock,
    obj: CFGExpr,
    index: EJSString,
    rhs: CFGExpr
) extends CFGNormalInst {
  override def toString: String = s"$obj[$index] := $rhs"
}

// x1 := function x_2^?(f)
case class CFGFunExpr(
    ir: IRNode,
    block: NormalBlock,
    lhs: CFGId,
    nameOpt: Option[CFGId],
    func: CFGFunction,
    asite1: AllocSite,
    asite2: AllocSite,
    asite3Opt: Option[AllocSite]
) extends CFGNormalInst {
  override def toString: String = {
    val name = nameOpt.getOrElse("")
    s"$lhs := function $name(${func.id}) @ $asite1, $asite2" + (asite3Opt match {
      case Some(asite) => s", $asite"
      case None => ""
    })
  }
}

// assert(e1 x e2)
case class CFGAssert(
    ir: IRNode,
    block: NormalBlock,
    expr: CFGExpr,
    flag: Boolean
) extends CFGNormalInst {
  override def toString: String = s"assert($expr)"
}

// catch(x)
case class CFGCatch(
    ir: IRNode,
    block: NormalBlock,
    name: CFGId
) extends CFGNormalInst {
  override def toString: String = s"catch($name)"
}

// return(e^?)
case class CFGReturn(
    ir: IRNode,
    block: NormalBlock,
    exprOpt: Option[CFGExpr]
) extends CFGNormalInst {
  override def toString: String = {
    val expr = exprOpt.getOrElse("")
    s"return($expr)"
  }
}

// throw(e)
case class CFGThrow(
    ir: IRNode,
    block: NormalBlock,
    expr: CFGExpr
) extends CFGNormalInst {
  override def toString: String = s"throw($expr)"
}

// noop
case class CFGNoOp(
    ir: IRNode,
    block: NormalBlock,
    desc: String
) extends CFGNormalInst {
  override def toString: String = s"noop($desc)"
}

// x := <>x(x^*)
case class CFGInternalCall(
    ir: IRNode,
    block: NormalBlock,
    lhs: CFGId,
    name: String,
    arguments: List[CFGExpr],
    var asiteOpt: Option[AllocSite] // XXX should be a value but for JS model for a while.
) extends CFGNormalInst {
  override def toString: String = {
    val arg = arguments.mkString(", ")
    s"$lhs := $name($arg)"
  } + (asiteOpt match {
    case Some(asite) => s" @ $asite"
    case None => ""
  })
}

// TODO revert after modeling
// case class CFGAPICall(
//   ir: IRNode,
//   block: NormalBlock,
//   model: String,
//   fun: String,
//   arguments: CFGExpr
// ) extends CFGNormalInst{
//   override def toString: String = s"[]$model.$fun($arguments)"
// }
// 
// case class CFGAsyncCall(
//   ir: IRNode,
//   block: NormalBlock,
//   modelType: String,
//   callType: String,
//   asite1: AllocSite,
//   asite2: AllocSite,
//   asite3: AllocSite
// ) extends CFGNormalInst{
//   override def toString: String = s"async($modelType, $callType) @ $asite1, $asite2, $asite3"
// }

/**
 * CFG Call Instruction
 */
sealed trait CFGCallInst extends CFGInst {
  val block: Call
  val fun: CFGExpr
  val thisArg: CFGExpr
  val arguments: CFGExpr
  var asite: AllocSite // XXX should be a value but for JS model for a while.
}

// call(e1, e2, e3)
case class CFGCall(
    ir: IRNode,
    block: Call,
    fun: CFGExpr,
    thisArg: CFGExpr,
    arguments: CFGExpr,
    var asite: AllocSite // XXX should be a value but for JS model for a while.
) extends CFGCallInst {
  override def toString: String = s"call($fun, $thisArg, $arguments) @ $asite"
}

// construct(e1, e2, e3)
case class CFGConstruct(
    ir: IRNode,
    block: Call,
    fun: CFGExpr,
    thisArg: CFGExpr,
    arguments: CFGExpr,
    override var asite: AllocSite // XXX should be a value but for JS model for a while.
) extends CFGCallInst {
  override def toString: String = s"construct($fun, $thisArg, $arguments) @ $asite"
}
