/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.jsaf.nodes_util

import scala.collection.mutable.{HashMap => MHashMap}
import kr.ac.kaist.jsaf.nodes._

////////////////////////////////////////////////////////////////////////////////
// Custom HashMap for Node
////////////////////////////////////////////////////////////////////////////////
class NodeHashMap[KeyType, ValueType] extends MHashMap[KeyType, ValueType] {
  // Custom hash function uses the uid as a hash value
  override def elemHashCode(key: KeyType): Int = {
    key match {
      case ast: AbstractNode => ast.getUID.toInt
      case ast: ScopeBody => ast.getUID.toInt
      case ir: IRAbstractNode => ir.getUID.toInt
      case ir: IRExpr => ir.getUID.toInt
      case ir: IROp => ir.getUID.toInt
      case ir: IRInfoNode => ir.getUID.toInt
      case _ => key.##
    }
  }

  // Key comparison
  override def elemEquals(key1: KeyType, key2: KeyType): Boolean = {
    val key1Value = key1 match {
      case ast: AbstractNode => ast.getUID
      case ast: ScopeBody => ast.getUID
      case ir: IRAbstractNode => ir.getUID
      case ir: IRExpr => ir.getUID
      case ir: IROp => ir.getUID
      case ir: IRInfoNode => ir.getUID
      case _ => return key1 == key2
    }
    val key2Value = key2 match {
      case ast: AbstractNode => ast.getUID
      case ast: ScopeBody => ast.getUID
      case ir: IRAbstractNode => ir.getUID
      case ir: IRExpr => ir.getUID
      case ir: IROp => ir.getUID
      case ir: IRInfoNode => ir.getUID
      case _ => return key1 == key2
    }
    key1Value == key2Value
  }

  // Clone
  override def clone = {
    val clonedMap = new NodeHashMap[KeyType, ValueType]
    for(keyValue <- this) clonedMap.put(keyValue._1, keyValue._2)
    clonedMap
  }
}
