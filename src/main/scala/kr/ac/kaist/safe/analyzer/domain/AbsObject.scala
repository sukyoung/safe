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

import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._

/* 8.6 The Object Type */

////////////////////////////////////////////////////////////////////////////////
// concrete object type
////////////////////////////////////////////////////////////////////////////////
case class Object(amap: Map[String, DataProp], imap: Map[IName, IValue]) {
  def +(other: Object): Object = {
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
          case Some(v) => map + (name -> (value + v))
          case None => map + (name -> value)
        }
      }
    }
    Object(newamap, newimap)
  }
}
////////////////////////////////////////////////////////////////////////////////
// object abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsObject extends AbsDomain[Object, AbsObject] {
  /* substitute locR by locO */
  def oldify(loc: Loc): AbsObject
  def subsLoc(locR: Recency, locO: Recency): AbsObject
  def weakSubsLoc(locR: Recency, locO: Recency): AbsObject

  def apply(str: String): AbsDataProp
  def apply(astr: AbsString): AbsDataProp
  def apply(in: IName): AbsIValue

  def initializeUpdate(str: String, dp: AbsDataProp): AbsObject

  def isEmpty: Boolean

  // absent value is set to AbsentBot because it is strong update.
  def update(str: String, dp: AbsDataProp, weak: Boolean = false): AbsObject
  def update(astr: AbsString, dp: AbsDataProp): AbsObject
  def update(in: IName, iv: AbsIValue): AbsObject

  def contains(str: String): AbsBool
  def contains(strSet: Set[String]): AbsBool
  def contains(astr: AbsString): AbsBool
  def contains(in: IName): AbsBool

  // def getOwnPropertyNames: Set[String]
  def abstractKeySet: ConSet[AbsString]
  def abstractKeySet(filter: (AbsString, AbsDataProp) => Boolean): ConSet[AbsString]
  def collectKeySet(prefix: String): ConSet[String]
  def keySetPair: (List[String], AbsString)
  def isDefinite(str: AbsString): Boolean

  ////////////////////////////////////////////////////////////////
  // internal methods of ECMAScript Object
  ///////////////////////////////////////////////////////////////
  // Section 8.12.1 [[GetOwnProperty]](P)
  def GetOwnProperty(P: String): (AbsDesc, AbsUndef)
  def GetOwnProperty(P: AbsString): (AbsDesc, AbsUndef)

  // Section 8.12.2 [[GetProperty]](P)
  def GetProperty(P: String, h: AbsHeap): (AbsDesc, AbsUndef)
  def GetProperty(P: AbsString, h: AbsHeap): (AbsDesc, AbsUndef)

  // Section 8.12.3 [[Get]](P)
  def Get(str: String, h: AbsHeap): AbsValue
  def Get(astr: AbsString, h: AbsHeap): AbsValue

  // Section 8.12.4 [[CanPut]](P)
  def CanPut(P: String, h: AbsHeap): AbsBool
  def CanPut(P: AbsString, h: AbsHeap): AbsBool

  // Section 8.12.5 [[Put]](P, V, Throw)
  def Put(P: AbsString, V: AbsValue, Throw: Boolean = true, h: AbsHeap): (AbsObject, Set[Exception])

  // Section 8.12.6 [[HasProperty]](P)
  def HasProperty(P: AbsString, h: AbsHeap): AbsBool

  // Section 8.12.7 [[Delete]](P, Throw)
  def Delete(str: String): (AbsObject, AbsBool)
  def Delete(astr: AbsString): (AbsObject, AbsBool)

  // Section 8.12.8 [[DefaultValue]](hint)
  def DefaultValue(hint: String, h: AbsHeap): AbsPValue
  def DefaultValue(h: AbsHeap): AbsPValue

  //Section 8.12.9 [[DefineOwnProperty]](P, Desc, Throw)
  def DefineOwnProperty(h: AbsHeap, P: AbsString, Desc: AbsDesc, Throw: Boolean = true): (AbsObject, AbsBool, Set[Exception])
}

trait AbsObjectUtil extends AbsDomainUtil[Object, AbsObject] {
  val Empty: AbsObject

  def newObject: AbsObject
  def newObject(loc: Loc): AbsObject
  def newObject(locSet: AbsLoc): AbsObject

  def newArgObject(absLength: AbsNumber = AbsNumber(0)): AbsObject

  def newArrayObject(absLength: AbsNumber = AbsNumber(0)): AbsObject

  def newFunctionObject(fid: FunctionId, env: AbsValue, l: Loc, n: AbsNumber): AbsObject
  def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: AbsValue,
    locOpt: Option[Loc], n: AbsNumber): AbsObject
  def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: AbsValue,
    locOpt: Option[Loc], writable: AbsBool, enumerable: AbsBool, configurable: AbsBool,
    absLength: AbsNumber): AbsObject

  def newBooleanObj(absB: AbsBool): AbsObject

  def newNumberObj(absNum: AbsNumber): AbsObject

  def newStringObj(absStr: AbsString): AbsObject

  def newErrorObj(errorName: String, protoLoc: Loc): AbsObject

  def defaultValue(locSet: AbsLoc): AbsPValue
  def defaultValue(locSet: AbsLoc, preferredType: String): AbsPValue
  def defaultValue(locSet: AbsLoc, h: AbsHeap, preferredType: String): AbsPValue

  // 8.10.4 FromPropertyDescriptor ( Desc )
  def FromPropertyDescriptor(h: AbsHeap, desc: AbsDesc): (AbsObject, Set[Exception])
}
