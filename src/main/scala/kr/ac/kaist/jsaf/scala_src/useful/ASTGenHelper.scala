/*******************************************************************************
    Copyright 2009,2011, Oracle and/or its affiliates.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.scala_src.useful

import _root_.java.util.{HashMap => JHashMap}
import _root_.java.util.{HashSet => JHashSet}
import _root_.java.util.{List => JList}
import _root_.java.util.{ArrayList => JArrayList}
import _root_.java.util.{Map => JMap}
import _root_.java.util.{Set => JSet}
import _root_.java.util.{Collections => JCollections}
import _root_.java.lang.{Integer => JInteger}
import edu.rice.cs.plt.tuple.{Option => JOption}
import kr.ac.kaist.jsaf.nodes
import scala.collection.JavaConversions

object ASTGenHelper {
  def needsScalafication(o: Any): Boolean = o match {
    case o: JOption[_]  => true
    case o: JList[_]    => true
    case o: JMap[_, _]  => true
    case o: JSet[_]     => true
    case o: JInteger    => true
    case _ => false
  }

  def scalaify(typ: Any): Any = typ match {
    case o: JOption[_] => {
      if (o.isSome)
        Some(scalaify(o.unwrap))
      else
        None
    }

    case l: JList[_] => {
      if (l.isEmpty) {
        Nil
      } else {
        val r = l.toArray.toList
        if (needsScalafication(r.head)) {
          r.map(scalaify)
        } else {
          r
        }
      }
    }

    case m: JMap[_, _] => {
      var accum = Map[Any, Any]()
      for (k <- (Map.empty ++ JavaConversions.mapAsScalaMap(m)).keySet) {
        accum += ((scalaify(k), scalaify(m.get(k))))
      }
      accum
    }

    case s: JSet[_] => {
      var accum = Set[Any]()
      for (e <- Set.empty ++ JavaConversions.asScalaSet(s)) {
        accum = accum + scalaify(e)
      }
      accum
    }

    case i: JInteger => i.intValue

    case _ => typ
  }

  def needsJavafication(o: Any): Boolean = o match {
    case o: Option[_] => true
    case o: List[_]   => true
    case o: Map[_,_]  => true
    case o: Set[_]    => true
    case o: Int       => true
    case _ => false
  }

  def javaify(typ: Any): Object = typ match {
    case Some(t) => JOption.some(javaify(t))
    case None => JOption.none

    case l: List[_] => {
      val m = l match {
        case head::_ if needsJavafication(l.head) => l.map(javaify)
        case _ => l
      }
      Lists.toJavaList(m)
    }

    case m: Map[_, _] => {
      val accum = new JHashMap[Object, Object]()
      val keyset = m.keysIterator
      for (k <- keyset) {
        accum.put(javaify(k), javaify(m.apply(k)))
      }
      accum
    }

    case s: Set[_] => {
      val accum = new JHashSet[Object]()
      for (e <- s) {
        accum.add(javaify(e))
      }
      accum
    }

    case i: Int => JInteger.valueOf(i)

    case _ => typ.asInstanceOf[Object]
  }
}
