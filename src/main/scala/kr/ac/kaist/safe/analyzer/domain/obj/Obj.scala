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

package kr.ac.kaist.safe.analyzer.domain

import spray.json._

// concrete object type
case class Obj(amap: Map[String, DataProp], imap: Map[IName, IValue]) {
  def +(other: Obj): Obj = {
    val newamap = this.amap.foldLeft(other.amap) {
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
    Obj(newamap, newimap)
  }
}

// internal property names
sealed abstract class IName(name: String) {
  override def toString: String = s"[[$name]]"
  def toJson: JsValue = JsString(name)
}
object IName {
  def fromJson(v: JsValue): Option[IName] = v match {
    case JsString("Prototype") => Some(IPrototype)
    case JsString("Class") => Some(IClass)
    case JsString("Extensible") => Some(IExtensible)
    case JsString("PrimitiveValue") => Some(IPrimitiveValue)
    case JsString("Call") => Some(ICall)
    case JsString("Construct") => Some(IConstruct)
    case JsString("Scope") => Some(IScope)
    case JsString("HasInstance") => Some(IHasInstance)
    case JsString("TargetFunction") => Some(ITargetFunction)
    case JsString("BoundThis") => Some(IBoundThis)
    case JsString("BoundArgs") => Some(IBoundArgs)
    case _ => None
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
