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
case class Heap(map: Map[Loc, Object]) {
  def +(other: Heap): Heap = {
    val emptyObj = Object(HashMap[String, DataProp](), HashMap[IName, IValue]())
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
trait AbsHeap extends AbsDomain[Heap, AbsHeap] {
  // lookup
  def get(loc: Loc): AbsObject
  def get(locSet: AbsLoc): AbsObject

  // heap update
  def weakUpdate(loc: Loc, obj: AbsObject): AbsHeap
  def update(loc: Loc, obj: AbsObject): AbsHeap

  // remove location
  def remove(loc: Loc): AbsHeap

  // substitute locR by locO
  def subsLoc(locR: Recency, locO: Recency): AbsHeap
  def oldify(loc: Loc): AbsHeap
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
  def protoBase(loc: Loc, absStr: AbsString): AbsLoc

  // store
  def propStore(loc: Loc, absStr: AbsString, value: AbsValue): AbsHeap

  // update location
  def delete(loc: Loc, absStr: AbsString): (AbsHeap, AbsBool)

  // get all map
  def getMap: Option[Map[Loc, AbsObject]]

  // location concrete check
  def isConcrete(loc: Loc): Boolean
}

trait AbsHeapUtil extends AbsDomainUtil[Heap, AbsHeap] {
  def apply(map: Map[Loc, AbsObject]): AbsHeap
}
