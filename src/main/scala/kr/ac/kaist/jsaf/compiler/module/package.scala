/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.compiler

import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU, _}
import kr.ac.kaist.jsaf.scala_src.nodes._
import java.util.HashMap
import java.util.HashSet

package object module {
  /*
   * Domains
   */
  type Identifier = String
  type Path = List[Identifier]

  abstract class QualName(var p: Path, var x: Identifier)
  case class QualIntName(_p: Path, _x: Identifier) extends QualName(_p, _x) {
    override def toString(): String = {
      var s: String = ""
      for (t <- p.reverse) s += t+"."
      s + "("+x+")"
    }
  }
  case class QualExtName(_p: Path, _x: Identifier) extends QualName(_p, _x) {
    override def toString(): String = {
      var s: String = ""
      for (t <- p.reverse) s += t+"."
      s + x
    }
  }

  /*
  class ExpQualName(p: Path) {
    override def toString(): String = {
      var s: String = ""
      for (t <- p.reverse) s += t+"."
      s + "*"
    }
  }
  */

  abstract class Type
  case object Var extends Type {
    override def toString(): String = "var"
  }
  case object Module extends Type {
    override def toString(): String = "module"
  }
  case object GetSet extends Type {
    override def toString(): String = "get/set"
  }
  case object Local extends Type {
    override def toString(): String = "local"
  }

  class Env(var env: HashMap[QualName, (Type, QualName)], var dir: HashMap[Path, HashSet[QualName]]) {
    def this() = this(new HashMap[QualName, (Type, QualName)](), new HashMap[Path, HashSet[QualName]]())

    override def clone(): Env = {
      val newEnv = env.clone.asInstanceOf[HashMap[QualName, (Type, QualName)]]
      val newDir = new HashMap[Path, HashSet[QualName]]()
      var it = newEnv.keySet.iterator
      while (it.hasNext) {
        val value = it.next
        if (newDir.get(value.p) == null) newDir.put(value.p, new HashSet[QualName]())
        newDir.get(value.p).add(value)
      }
      new Env(newEnv, newDir)
    }

    def get(k: QualName): (Type, QualName) = {
      env.get(k)
    }
    def put(k: QualName, v: (Type, QualName)) {
      env.put(k, v)
      if (dir.get(k.p) == null) dir.put(k.p, new HashSet[QualName]())
      dir.get(k.p).add(k)
    }
    def namesIn(p: Path): List[QualName] = {
      var l: List[QualName] = Nil
      if (dir.get(p) == null) dir.put(p, new HashSet[QualName]())
      var it = dir.get(p).iterator
      while (it.hasNext)
        l = it.next :: l
      l
    }

    def print() {
      var it = env.entrySet.iterator
      while (it.hasNext) {
        val pair = it.next
        System.out.println(pair.getKey + " -> " + pair.getValue)
      }
      var iit = dir.keySet.iterator
      while (iit.hasNext) {
        val key = iit.next
        System.out.println(key + " => " + namesIn(key))
      }
    }
  }
}
