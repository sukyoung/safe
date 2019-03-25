/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

// concrete object type
case class Obj(nmap: Map[String, DataProp], imap: Map[IName, IValue]) {
  def +(other: Obj): Obj = {
    val newnmap = this.nmap.foldLeft(other.nmap) {
      case (map, (str, prop)) => {
        map.get(str) match {
          case Some(p) => map + (str -> (prop + p))
          case None => map + (str -> prop)
        }
      }
    }
    val newimap = this.imap.foldLeft(other.imap) {
      case (map, (name, value)) => {
        map.get(name) match {
          case Some(v) => map + (name -> v)
          case None => map + (name -> value)
        }
      }
    }
    Obj(newnmap, newimap)
  }
}

// internal property names
sealed abstract class IName(name: String) {
  override def toString: String = s"[[$name]]"
}
object IName {
  val all: List[IName] = List(
    IPrototype,
    IClass,
    IExtensible,
    IPrimitiveValue,
    ICall,
    IConstruct,
    IScope,
    IHasInstance,
    ITargetFunction,
    IBoundThis,
    IBoundArgs
  )
  def makeMap[V](value: V): Map[IName, V] = all.foldLeft(Map[IName, V]()) {
    case (map, iname) => map + (iname -> value)
  }
}
case object IPrototype extends IName("Prototype")
case object IClass extends IName("Class")
case object IExtensible extends IName("Extensible")
case object IPrimitiveValue extends IName("PrimitiveValue")
case object ICall extends IName("Call")
case object IConstruct extends IName("Construct")
case object IScope extends IName("Scope")
case object IHasInstance extends IName("HasInstance") //TODO
case object ITargetFunction extends IName("TargetFunction")
case object IBoundThis extends IName("BoundThis")
case object IBoundArgs extends IName("BoundArgs")
