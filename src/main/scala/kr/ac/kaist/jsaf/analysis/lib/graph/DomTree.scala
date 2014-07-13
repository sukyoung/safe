/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.lib.graph

import scala.collection.immutable.HashMap
import scala.collection.immutable.HashSet
import kr.ac.kaist.jsaf.analysis.cfg._
import kr.ac.kaist.jsaf.analysis.typing.domain.{Loc, LocSet, LocSetBot, DomainPrinter}
import scala.collection.mutable.{ Stack }
import kr.ac.kaist.jsaf.analysis.lib.SSA

class DomTree[Node](var idom: HashMap[Node, Node], var dom_tree: HashMap[Node, Set[Node]]) {
  def hasParent(n: Node): Boolean = idom.contains(n)
  def getParent(n: Node): Node = idom(n)
  def getChildren(n: Node): Set[Node] = {
    dom_tree.get(n) match {
      case Some(s) => s
      case None => Set()
    }
  }

  private def NormalEdgeStyle:String = "fontname=\"Consolas\" style=solid"

  def toDot_String(getLabel:(Node => String)): String = {
    var str = ""
    idom.foreach{ case (src, dst) => {
      str += (getLabel(src)+ "->"+getLabel(dst)+"["+NormalEdgeStyle+"]; \n")
    }}
    str
  }

}
