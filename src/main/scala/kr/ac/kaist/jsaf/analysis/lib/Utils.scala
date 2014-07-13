/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.lib

import scala.collection.immutable.HashMap
import scala.collection.immutable.HashSet

object Utils {
  /**
   * Number the functions like this.
   * (It's for a graph but the below example is a tree for your easy understanding.
   *  If a function is already numbered while traversing the graph, just skip the function.)
   *        0
   *       / \
   *      /   \
   *     4     1
   *    / \   / \
   *   6   5 3   2
   */
  def get_reverse_postorder[T](entry: T, size: Int, succs: T => Set[T]): Map[T,Int] = {
    var map = Map[T,Int]()
    var visited = HashSet[T]()
    var i = size - 1

    def dfs(n: T): Unit = {
      visited += (n)
      val children = try succs(n) catch { case e => Set() }

      children.foreach((c) => {
        if (!visited.contains(c))
          dfs(c)
      })
      map += (n -> i)
      i -= 1
    }

    dfs(entry)
    map
  }
}
