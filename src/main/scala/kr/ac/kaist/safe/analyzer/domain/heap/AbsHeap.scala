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

import scala.collection.immutable.HashMap
import scala.io.Source
import kr.ac.kaist.safe.analyzer.HeapParser._
import kr.ac.kaist.safe.util._
import spray.json._

////////////////////////////////////////////////////////////////////////////////
// concrete heap type
////////////////////////////////////////////////////////////////////////////////
case class Heap(map: Map[Loc, Obj]) {
  def +(other: Heap): Heap = {
    val emptyObj = Obj(HashMap[String, DataProp](), HashMap[IName, IValue]())
    val newHeapMap = other.map.foldLeft(this.map) {
      case (map, (loc, obj)) => {
        val newObj = map.getOrElse(loc, emptyObj) + obj
        map + (loc -> newObj)
      }
    }
    Heap(newHeapMap)
  }
}
object Heap {
  def parse(fileName: String): Heap =
    Source.fromFile(fileName)("UTF-8").mkString.parseJson.convertTo
}

////////////////////////////////////////////////////////////////////////////////
// heap abstract domain
////////////////////////////////////////////////////////////////////////////////
trait HeapDomain extends AbsDomain[Heap] { domain: HeapDomain =>
  def apply(map: Map[Loc, AbsObj]): Elem

  // abstract boolean element
  type Elem <: ElemTrait

  trait ElemTrait extends super.ElemTrait { this: Elem =>
    // lookup
    def get(loc: Loc): AbsObj
    def get(locSet: AbsLoc): AbsObj

    // heap update
    def weakUpdate(loc: Loc, obj: AbsObj): Elem
    def update(loc: Loc, obj: AbsObj): Elem

    // remove location
    def remove(loc: Loc): Elem

    // substitute locR by locO
    def subsLoc(locR: Recency, locO: Recency): Elem
    def oldify(loc: Loc): Elem
    def domIn(loc: Loc): Boolean

    // toString
    def toStringAll: String
    def toStringLoc(loc: Loc): Option[String]

    // predicates
    def hasConstruct(loc: Loc): AbsBool
    def hasInstance(loc: Loc): AbsBool
    def isArray(loc: Loc): AbsBool
    def isObject(loc: Loc): Boolean
    def canPutVar(x: String): AbsBool

    // proto
    def protoBase(loc: Loc, absStr: AbsStr): AbsLoc

    // store
    def propStore(loc: Loc, absStr: AbsStr, value: AbsValue): Elem

    // update location
    def delete(loc: Loc, absStr: AbsStr): (Elem, AbsBool)

    // get all map
    def getMap: Option[Map[Loc, AbsObj]]

    // location concrete check
    def isConcrete(loc: Loc): Boolean
  }
}
