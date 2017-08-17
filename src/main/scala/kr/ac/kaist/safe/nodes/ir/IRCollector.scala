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

package kr.ac.kaist.safe.nodes.ir

object IRCollector extends IRGeneralWalker[Set[IRNode]] {

  def join(result: Set[IRNode]*): Set[IRNode] =
    result.foldLeft(Set[IRNode]())(_ ++ _)

  override def walk(node: IRRoot): Set[IRNode] =
    Set(node) ++ super.walk(node)
  override def walk(node: IRStmt): Set[IRNode] =
    Set(node) ++ super.walk(node)
  override def walk(node: IRExpr): Set[IRNode] =
    Set(node) ++ super.walk(node)
  override def walk(node: IRMember): Set[IRNode] =
    Set(node) ++ super.walk(node)
  override def walk(node: IRFunctional): Set[IRNode] =
    Set(node) ++ super.walk(node)
  override def walk(node: IROp): Set[IRNode] =
    Set(node) ++ super.walk(node)
  override def walk(node: IRFunDecl): Set[IRNode] =
    Set(node) ++ super.walk(node)
  override def walk(node: IRVarStmt): Set[IRNode] =
    Set(node) ++ super.walk(node)
  override def walk(node: IRId): Set[IRNode] =
    Set(node) ++ super.walk(node)

  def collect(node: IRNode): Set[IRNode] = node match {
    case n: IRRoot => walk(n)
    case n: IRStmt => walk(n)
    case n: IRExpr => walk(n)
    case n: IRMember => walk(n)
    case n: IROp => walk(n)
    case n: IRFunctional => walk(n)
  }

}
