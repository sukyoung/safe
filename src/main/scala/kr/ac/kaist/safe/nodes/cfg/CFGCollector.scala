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

object CFGCollector extends CFGNodeGeneralWalker[List[CFGNode]] {

  def join(results: List[CFGNode]*): List[CFGNode] =
    results.foldLeft[List[CFGNode]](Nil)(_ ++ _)

  override def walk(node: CFGNode): List[CFGNode] = {
    node :: super.walk(node)
  }

  def collect(node: CFGNode): List[CFGNode] =
    walk(node)

}
