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

import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util.AllocSite

trait CFGNodeGeneralWalker[Result] {

  private var cfgBlocksVisited: Set[CFGBlock] = Set()
  var cfgNodesVisited: Set[CFGNode] = Set()

  protected def visitCfgBlock(cfgBlock: CFGBlock): Unit =
    cfgBlocksVisited += cfgBlock

  protected def visitCfgNode(cfgNode: CFGNode): Unit =
    cfgNodesVisited += cfgNode

  protected def isCfgBlockVisited(cfgBlock: CFGBlock): Boolean =
    cfgBlocksVisited.contains(cfgBlock)

  protected def isCfgNodeVisited(cfgNode: CFGNode): Boolean =
    cfgNodesVisited.contains(cfgNode)

  def join(args: Result*): Result

  def walkOpt(opt: Option[Any]): List[Result] =
    opt.map[List[Result]]((n: Any) => List(n match {
      case n: AllocSite =>
        walk(n)
      case n: CFGExpr =>
        walk(n)
      case n: CFGId =>
        walk(n)
      case n: CFGNode =>
        walk(n)
    })).getOrElse(Nil)

  def walk(irNode: IRNode): Result = join()

  def walk(block: NormalBlock): Result = join()

  def walk(allocSite: AllocSite): Result = join()

  def walk(id: CFGId): Result = join()

  def walk(block: CFGBlock): Result =
    if (isCfgBlockVisited(block)) {
      join()
    } else {
      visitCfgBlock(block)
      block match {
        case b: Call =>
          val x = walk(b.callInst)
          join(walk(b.func) :: x :: b.getInsts.map(walk): _*)
        case b: NormalBlock =>
          join(b.getInsts.map(walk): _*)
        case _ =>
          join()
      }
    }

  def walk(node: CFGNode): Result = {
    if (isCfgNodeVisited(node)) {
      join()
    } else {
      visitCfgNode(node)
      node match {
        case CFGAlloc(ir, block, lhs, protoOpt, asite) =>
          join(walk(ir) :: walk(block) :: walk(lhs) :: walk(asite) :: walkOpt(protoOpt): _*)
        case CFGAllocArg(ir, block, lhs, length, asite) =>
          join(walk(ir), walk(block), walk(lhs), walk(asite))
        case CFGAllocArray(ir, block, lhs, length, asite) =>
          join(walk(ir), walk(block), walk(lhs), walk(asite))
        case CFGAssert(ir, block, expr, flag) =>
          join(walk(ir), walk(block), walk(expr))
        case CFGBin(ir, first, op, second) =>
          join(walk(ir), walk(first), walk(second))
        case CFGCall(ir, block, fun, thisArg, args, asite) =>
          join(walk(ir), walk(block), walk(fun), walk(thisArg), walk(args), walk(asite))
        case CFGCatch(ir, block, name) =>
          join(walk(ir), walk(block), walk(name))
        case CFGConstruct(ir, block, fun, thisArg, args, asite) =>
          join(walk(ir), walk(block), walk(fun), walk(thisArg), walk(args), walk(asite))
        case CFGDelete(ir, block, lhs, expr) =>
          join(walk(ir), walk(block), walk(lhs), walk(expr))
        case CFGEnterCode(ir, block, lhs, thisExpr) =>
          join(walk(ir), walk(block), walk(lhs), walk(thisExpr))
        case CFGExprStmt(ir, block, lhs, right) =>
          join(walk(ir), walk(block), walk(lhs), walk(right))
        case n @ CFGFunction(ir, argsName, argVars, localVars, name, isUser) =>
          val l: List[Result] = n.getAllBlocks.map(walk)
          join(walk(ir) :: argVars.map(walk) ++ localVars.map(walk) ++ l: _*)
        case CFGFunExpr(ir, block, lhs, nameOpt, func, asite1, asite2, asite3Opt) =>
          join(((walk(ir) :: walk(block) :: walk(lhs) :: walkOpt(nameOpt)) :+ walk(func)) ++
            (walk(asite1) :: walk(asite2) :: walkOpt(asite3Opt)) :+ walk(asite1): _*)
        case CFGInternalCall(ir, block, lhs, name, args, asiteOpt) =>
          join(walk(ir) :: walk(block) :: walk(lhs) :: args.map(walk) ++ walkOpt(asiteOpt): _*)
        case CFGInternalValue(ir, name) =>
          walk(ir)
        case CFGLoad(ir, obj, index) =>
          join(walk(ir), walk(obj), walk(index))
        case CFGNoOp(ir, block, desc) =>
          join(walk(ir), walk(block))
        case CFGReturn(ir, block, exprOpt) =>
          join(walk(ir) :: walk(block) :: walkOpt(exprOpt): _*)
        case CFGStore(ir, block, obj, index, rhs) =>
          join(walk(ir), walk(block), walk(obj), walk(index), walk(rhs))
        case CFGStoreStringIdx(ir, block, obj, index, rhs) =>
          join(walk(ir), walk(block), walk(obj), walk(rhs))
        case CFGThis(ir) =>
          walk(ir)
        case CFGThrow(ir, block, expr) =>
          join(walk(ir), walk(block), walk(expr))
        case CFGUn(ir, op, expr) =>
          join(walk(ir), walk(expr))
        case CFGVal(value) =>
          join()
        case CFGVarRef(ir, id) =>
          join(walk(ir), walk(id))
        case c: CFG =>
          val l: List[Result] = c.getAllBlocks.flatMap(_.getInsts).map(walk)
          val ll: List[Result] = c.getAllFuncs.map(walk)
          join(walk(c.ir) :: walk(c.globalFunc) :: ll ++ l: _*)
        case _ => join()
      }
    }
  }

}
