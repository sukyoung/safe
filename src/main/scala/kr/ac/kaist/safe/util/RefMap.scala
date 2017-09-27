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

package kr.ac.kaist.safe.util

import scala.collection.immutable.{ HashMap, HashSet }

class RefMap[Key, Data] private (
    private val helper: RefMapHelper[Key],
    private val internalMap: Map[Key, Data],
    val id: Long
) extends Map[Key, Data] {
  // add data
  def +[Value >: Data](kv: (Key, Value)): RefMap[Key, Value] = {
    val (key, value) = kv
    create(internalMap + kv, HashSet(key))
  }

  // delete key
  def -(key: Key): RefMap[Key, Data] = this -- HashSet(key)
  def --(keys: Set[Key]): RefMap[Key, Data] = create(internalMap -- keys, keys)

  // get data
  def get(key: Key): Option[Data] = internalMap get key

  // iterator
  def iterator: Iterator[(Key, Data)] = internalMap.iterator

  // create another map
  private def create[Value >: Data](map: Map[Key, Value], keys: Set[Key]): RefMap[Key, Value] = {
    val newId = helper.getId
    helper.add(newId, id, keys)
    new RefMap(helper, map, newId)
  }

  // find different keys
  def findDiffKeys(that: RefMap[Key, Data]): Option[Set[Key]] = {
    this.helper eq that.helper match {
      // both map have same helper
      case true => Some(helper.findDiffKeys(this.id, that.id))

      // otherwise
      case false => None
    }
  }
}

object RefMap {
  def apply[Key, Data](internalMap: Map[Key, Data] = HashMap[Key, Data]()): RefMap[Key, Data] = {
    val helper = new RefMapHelper[Key]
    val id = helper.getId
    new RefMap(helper, internalMap, id)
  }
}

class RefMapHelper[Key] {
  // counter for id
  private var idCount: Long = 0
  def getId: Long = {
    val id = idCount
    idCount += 1
    id
  }

  // tree structure for parents
  private var parent: Map[Long, Long] = HashMap(idCount -> idCount)

  // depth of tree structure
  private var depth: Map[Long, Long] = HashMap(idCount -> 0)

  // diff key sets
  private var diffKeys: Map[Long, Set[Key]] = HashMap(idCount -> HashSet())

  // add a map
  def add(id: Long, p: Long, keys: Set[Key]): Unit = {
    parent += (id -> p)
    depth += (id -> (depth(p) + 1))
    diffKeys += (id -> keys)
  }

  // find different keys
  def findDiffKeys(left: Long, right: Long): Set[Key] = {
    var ldepth = depth(left)
    var rdepth = depth(right)
    def find(left: Long, right: Long, keys: Set[Key]): Set[Key] = {
      if (ldepth > rdepth) {
        ldepth -= 1
        find(parent(left), right, keys ++ diffKeys(left))
      } else if (ldepth < rdepth) {
        rdepth -= 1
        find(left, parent(right), keys ++ diffKeys(right))
      } else if (left != right) {
        ldepth -= 1
        rdepth -= 1
        find(parent(left), parent(right), keys ++ diffKeys(left) ++ diffKeys(right))
      } else {
        keys
      }
    }
    find(left, right, (HashSet()))
  }
}
