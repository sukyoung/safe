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

import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.nodes.cfg.FunctionId

// object abstract domain
trait ObjDomain extends AbsDomain[Obj] {
  val Empty: Elem

  def newObject: Elem
  def newObject(loc: Loc): Elem
  def newObject(locSet: AbsLoc): Elem

  def newArgObject(absLength: AbsNum = AbsNum(0)): Elem

  def newArrayObject(absLength: AbsNum = AbsNum(0)): Elem

  def newFunctionObject(fid: FunctionId, env: AbsValue, l: Loc, n: AbsNum): Elem
  def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: AbsValue,
    locOpt: Option[Loc], n: AbsNum): Elem
  def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: AbsValue,
    locOpt: Option[Loc], writable: AbsBool, enumerable: AbsBool, configurable: AbsBool,
    absLength: AbsNum): Elem

  def newBooleanObj(absB: AbsBool): Elem

  def newNumberObj(absNum: AbsNum): Elem

  def newStringObj(absStr: AbsStr): Elem

  def newErrorObj(errorName: String, protoLoc: Loc): Elem

  def defaultValue(locSet: AbsLoc): AbsPValue
  def defaultValue(locSet: AbsLoc, preferredType: String): AbsPValue
  def defaultValue(locSet: AbsLoc, h: AbsHeap, preferredType: String): AbsPValue

  // 8.10.4 FromPropertyDescriptor ( Desc )
  def FromPropertyDescriptor(h: AbsHeap, desc: AbsDesc): (Elem, Set[Exception])

  // abstract object element
  type Elem <: ElemTrait

  // abstract object element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    /* substitute locR by locO */
    def oldify(loc: Loc): Elem
    def subsLoc(locR: Recency, locO: Recency): Elem
    def weakSubsLoc(locR: Recency, locO: Recency): Elem

    def apply(str: String): AbsDataProp
    def apply(astr: AbsStr): AbsDataProp
    def apply(in: IName): AbsIValue

    def isEmpty: Boolean

    // absent value is set to AbsentBot because it is strong update.
    def update(str: String, dp: AbsDataProp): Elem
    def weakUpdate(astr: AbsStr, dp: AbsDataProp): Elem
    def update(in: IName, iv: AbsIValue): Elem

    def contains(str: String): AbsBool
    def contains(astr: AbsStr): AbsBool
    def contains(in: IName): AbsBool

    // def getOwnPropertyNames: Set[String]
    def abstractKeySet: ConSet[AbsStr]
    def abstractKeySet(filter: (AbsStr, AbsDataProp) => Boolean): ConSet[AbsStr]
    def collectKeySet(prefix: String): ConSet[String]
    def keySetPair(heap: AbsHeap): (List[String], AbsStr)
    def isDefinite(str: AbsStr): Boolean

    ////////////////////////////////////////////////////////////////
    // internal methods of ECMAScript Object
    ///////////////////////////////////////////////////////////////
    // Section 8.12.1 [[GetOwnProperty]](P)
    def GetOwnProperty(P: String): (AbsDesc, AbsUndef) = GetOwnProperty(AbsStr(P))
    def GetOwnProperty(P: AbsStr): (AbsDesc, AbsUndef)

    // Section 8.12.2 [[GetProperty]](P)
    def GetProperty(P: String, h: AbsHeap): (AbsDesc, AbsUndef) = GetProperty(AbsStr(P), h)
    def GetProperty(P: AbsStr, h: AbsHeap): (AbsDesc, AbsUndef)

    // Section 8.12.3 [[Get]](P)
    def Get(str: String, h: AbsHeap): AbsValue = Get(AbsStr(str), h)
    def Get(astr: AbsStr, h: AbsHeap): AbsValue

    // Section 8.12.4 [[CanPut]](P)
    def CanPut(P: String, h: AbsHeap): AbsBool = CanPut(AbsStr(P), h)
    def CanPut(P: AbsStr, h: AbsHeap): AbsBool

    // Section 8.12.5 [[Put]](P, V, Throw)
    def Put(P: AbsStr, V: AbsValue, Throw: Boolean = true, h: AbsHeap): (Elem, Set[Exception])

    // Section 8.12.6 [[HasProperty]](P)
    def HasProperty(P: AbsStr, h: AbsHeap): AbsBool

    // Section 8.12.7 [[Delete]](P, Throw)
    def Delete(str: String): (Elem, AbsBool, Set[Exception]) = Delete(AbsStr(str), false)
    def Delete(str: String, Throw: Boolean): (Elem, AbsBool, Set[Exception]) = Delete(AbsStr(str), Throw)
    def Delete(astr: AbsStr): (Elem, AbsBool, Set[Exception]) = Delete(astr, false)
    def Delete(astr: AbsStr, Throw: Boolean): (Elem, AbsBool, Set[Exception])

    // Section 8.12.8 [[DefaultValue]](hint)
    def DefaultValue(hint: String, h: AbsHeap): AbsPValue
    def DefaultValue(h: AbsHeap): AbsPValue

    //Section 8.12.9 [[DefineOwnProperty]](P, Desc, Throw)
    def DefineOwnProperty(P: AbsStr, Desc: AbsDesc, Throw: Boolean = true, h: AbsHeap): (Elem, AbsBool, Set[Exception])
  }
}
