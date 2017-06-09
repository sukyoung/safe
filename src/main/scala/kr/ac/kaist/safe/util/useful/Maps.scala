/*******************************************************************************
    Copyright 2009,2011, Oracle and/or its affiliates.
    All rights reserved.


    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.safe.util.useful

import _root_.java.util.{HashMap => JavaHashMap, Map => JavaMap}

import scala.collection.{JavaConversions, Map => MMap}

object Maps {
  def toMap[S, T](jmap: JavaMap[S, T]): Map[S, T] = Map.empty ++ JavaConversions.mapAsScalaMap(jmap)

  def toJavaMap[S, T](smap: MMap[S, T]): JavaMap[S, T] = {
    var jmap = new JavaHashMap[S, T]()
    for (key <- smap.keysIterator) {
      val value = smap.get(key).get
      jmap.put(key, value)
    }
    jmap
  }
}
