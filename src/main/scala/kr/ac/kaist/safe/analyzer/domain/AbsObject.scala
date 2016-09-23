/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.analyzer.TypeConversionHelper

import scala.collection.immutable.HashSet

class AbsObject(
    val amap: AbsMap,
    val imap: ObjInternalMap
) {
  override def toString: String = {
    val sortedIMap = imap.toSeq.sortBy {
      case (key, _) => key.toString()
    }

    val s = new StringBuilder
    sortedIMap.map {
      case (key, iv) => {
        s.append(key.toString)
          .append(s" : ")
          .append(iv.toString)
          .append(LINE_SEP)
      }
    }
    s.append(amap.toString)

    s.toString
  }

  /* partial order */
  def <=(that: AbsObject): Boolean = {
    if (this.isEmpty) true
    else if (that.isEmpty) false
    else if (!(this.imap.keySet subsetOf that.imap.keySet)) false
    else (this.amap <= that.amap) &&
      that.imap.forall {
        case (key, thatIV) => {
          this.imap.get(key) match {
            case None => false
            case Some(thisIV) => thisIV <= thatIV
          }
        }
      }
  }

  /* not a partial order */
  def </(that: AbsObject): Boolean = !(this <= that)

  /* join */
  def +(that: AbsObject): AbsObject = {
    val newAMap = this.amap + that.amap

    val ikeys = this.imap.keySet ++ that.imap.keySet
    val newIMap = ikeys.foldLeft(ObjEmptyIMap)((im, key) => {
      val thisIVal = this.imap.get(key)
      val thatIVal = that.imap.get(key)
      (thisIVal, thatIVal) match {
        case (None, None) => im
        case (None, Some(iv)) => im + (key -> iv)
        case (Some(iv), None) => im + (key -> iv)
        case (Some(iv1), Some(iv2)) => im + (key -> (iv1 + iv2))
      }
    })

    new AbsObject(newAMap, newIMap)
  }

  /* meet */
  def <>(that: AbsObject): AbsObject = {
    if (this.amap eq that.amap) this
    else new AbsObject(AbsMapEmpty, ObjEmptyIMap)
  }

  def isBottom: Boolean = {
    if (this.isEmpty) true
    else
      this.amap.isBottom &&
        this.imap.foldLeft(true)((b, kv) => {
          val (_, iv) = kv
          b && iv.isBottom
        })
  }

  ////////////////////////////////////////////////////////////////
  // Additional untility functions
  ///////////////////////////////////////////////////////////////
  def isEmpty: Boolean = this.amap.isEmpty && this.imap.isEmpty

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): AbsObject = {
    if (this.isEmpty) this
    else {
      val newAMap = this.amap.mapValue { dp => dp.copyWith(dp.value.subsLoc(locR, locO)) }
      val newIMap = this.imap.foldLeft(ObjEmptyIMap)((im, kv) => {
        val (key, iv) = kv
        val newV = iv.value.subsLoc(locR, locO)
        im + (key -> InternalValue(newV, iv.fidset))
      })
      new AbsObject(newAMap, newIMap)
    }
  }

  def weakSubsLoc(locR: Loc, locO: Loc): AbsObject = {
    if (this.isEmpty) this
    else {
      val newAMap = this.amap.mapValue { dp => dp.copyWith(dp.value.weakSubsLoc(locR, locO)) }
      val newIMap = this.imap.foldLeft(ObjEmptyIMap)((im, kv) => {
        val (key, iv) = kv
        val newV = iv.value.weakSubsLoc(locR, locO)
        im + (key -> InternalValue(newV, iv.fidset))
      })
      new AbsObject(newAMap, newIMap)
    }
  }

  def apply(str: String): AbsDataProp = {
    val (dpset, success) = amap.lookup(str)
    if (dpset.nonEmpty) dpset.reduce(_ + _)
    else AbsDataProp.Bot
  }

  def apply(astr: AbsString): AbsDataProp = {
    val (dpset, success) = amap.lookup(astr)
    if (dpset.nonEmpty) dpset.reduce(_ + _)
    else AbsDataProp.Bot
  }

  def apply(in: InternalName): InternalValue =
    this.imap.get(in) match {
      case Some(iv) => iv
      case None => InternalValueUtil.Bot
    }

  def initializeUpdate(str: String, dp: AbsDataProp): AbsObject = {
    new AbsObject(amap.initializeUpdate(str, dp), imap)
  }

  // absent value is set to AbsentBot because it is strong update.
  def update(str: String, dp: AbsDataProp, exist: Boolean = false): AbsObject = {
    if (this.isBottom) this
    else new AbsObject(amap.update(str, dp), imap)
  }

  def update(astr: AbsString, dp: AbsDataProp): AbsObject =
    new AbsObject(amap.update(astr, dp), imap)

  def update(in: InternalName, iv: InternalValue): AbsObject = {
    val newIv = iv + this(in)
    new AbsObject(amap, imap + (in -> newIv))
  }

  def contains(str: String): AbsBool = this.amap contains str

  def contains(strSet: Set[String]): AbsBool =
    strSet.foldLeft(AbsBool.Bot)((absB, str) => absB + (this contains str))

  def contains(astr: AbsString): AbsBool = this.amap contains astr

  def contains(in: InternalName): AbsBool = {
    imap.get(in) match {
      case None => AbsBool.False
      case Some(_) => AbsBool.Top
    }
  }

  def getOwnPropertyNames: Set[String] = this.amap.concreteKeySet
  def collectKeySet(prefix: String): Set[String] = this.amap.collectKeySet(prefix)

  ////////////////////////////////////////////////////////////////
  // internal methods of ECMAScript Object
  ///////////////////////////////////////////////////////////////
  // Section 8.12.1 [[GetOwnProperty]](P)
  def GetOwnProperty(P: String): AbsDataProp = {
    val astr = AbsString(P)
    GetOwnProperty(astr)
  }

  def GetOwnProperty(P: AbsString): AbsDataProp = {
    val (dpset, definite) = amap.lookup(P)
    val dp =
      if (dpset.nonEmpty) dpset.reduce(_ + _)
      else AbsDataProp.Bot

    if (definite) dp
    else dp.copyWith(dp.value + AbsValue(AbsUndef.Top))
  }

  // Section 8.12.2 [[GetProperty]](P)
  def GetProperty(P: String, h: Heap): AbsDataProp = {
    val astr = AbsString(P)
    GetProperty(astr, h)
  }

  def GetProperty(P: AbsString, h: Heap): AbsDataProp = {
    var visited = HashSet[Loc]()
    def visit(currObj: AbsObject): AbsDataProp = {
      val prop = currObj.GetOwnProperty(P)
      if (prop.value.pvalue.undefval </ AbsUndef.Bot) {
        val proto = currObj(IPrototype)
        if (proto.value.pvalue.nullval </ AbsNull.Bot || proto.value.locset.isBottom)
          prop.copyWith(prop.value + AbsValue(AbsUndef.Top))
        else
          proto.value.locset.foldLeft(prop)((dp, loc) =>
            if (visited contains loc) dp
            else {
              visited += loc
              dp + visit(h.get(loc))
            })
      } else prop
    }
    visit(this)
  }

  // Section 8.12.3 [[Get]](P)
  def Get(str: String, h: Heap): AbsValue = {
    var visited = HashSet[Loc]()
    val valueBot = AbsValue.Bot
    def visit(currentObj: AbsObject): AbsValue = {
      val test = currentObj contains str
      val v1 =
        if (AbsBool.True <= test) currentObj(str).value
        else valueBot
      val v2 =
        if (AbsBool.False <= test) {
          val protoV = currentObj(IPrototype).value
          val v3 = protoV.pvalue.nullval.fold(valueBot)(_ => {
            AbsUndef.Top
          })
          v3 + protoV.locset.foldLeft(valueBot)((v, protoLoc) => {
            if (visited contains protoLoc) v
            else {
              visited += protoLoc
              v + visit(h.get(protoLoc))
            }
          })
        } else valueBot
      v1 + v2
    }
    visit(this)
  }

  def Get(astr: AbsString, h: Heap): AbsValue = {
    var visited = HashSet[Loc]()
    val valueBot = AbsValue.Bot
    def visit(currentObj: AbsObject): AbsValue = {
      val test = currentObj contains astr
      val v1 =
        if (AbsBool.True <= test) currentObj(astr).value
        else valueBot
      val v2 =
        if (AbsBool.False <= test) {
          val protoV = currentObj(IPrototype).value
          val v3 = protoV.pvalue.nullval.fold(valueBot)({ _ =>
            AbsUndef.Top
          })
          v3 + protoV.locset.foldLeft(valueBot)((v, protoLoc) => {
            if (visited contains protoLoc) v
            else {
              visited += protoLoc
              v + visit(h.get(protoLoc))
            }
          })
        } else valueBot
      v1 + v2
    }
    visit(this)
  }

  // Section 8.12.4 [[CanPut]](P)
  def CanPut(P: String, h: Heap): AbsBool = this.CanPut(AbsString(P), h)

  def CanPut(P: AbsString, h: Heap): AbsBool = {
    var visited = HashSet[Loc]()
    val desc = this.GetOwnProperty(P)
    if (AbsUndef.Top </ desc.value.pvalue.undefval) desc.writable
    else {
      val proto = this(IPrototype).value
      val extensible = this(IExtensible).value.pvalue.boolval
      val nullProtoCase =
        if (AbsNull.Top <= proto.pvalue.nullval) extensible
        else AbsBool.Bot
      val inherited = proto.locset.foldLeft(AbsDataProp.Bot)((b, loc) =>
        if (visited contains loc) b
        else {
          visited += loc
          b + this.GetProperty(P, h)
        })
      val undefInheritCase =
        if (AbsUndef.Top <= inherited.value.pvalue.undefval) extensible
        else extensible && inherited.writable
      nullProtoCase + undefInheritCase
    }
  }

  // Section 8.12.5 [[Put]](P, V, Throw)
  def Put(P: AbsString, V: AbsValue, Throw: Boolean = true, h: Heap): (AbsObject, Set[Exception]) = {
    def IsDataDescriptor(dataProp: AbsDataProp): AbsBool = {
      if (AbsUndef.Top <= dataProp.value) {
        if (dataProp.value <= AbsUndef.Top) AbsBool.False
        else AbsBool.Top
      } else AbsBool.True
    }

    val canPut = this.CanPut(P, h)
    val excSet1 =
      if (AbsBool.False <= canPut) ExcSetEmpty + TypeError
      else ExcSetEmpty
    if (AbsBool.True <= canPut) {
      val ownDesc = this.GetOwnProperty(P)
      val (obj2, b2, excSet2) =
        if (AbsBool.True <= IsDataDescriptor(ownDesc)) {
          val valueDesc = AbsDataProp(V, AbsBool.Bot, AbsBool.Bot, AbsBool.Bot)
          this.DefineOwnProperty(P, valueDesc, Throw)
        } else (AbsObjectUtil.Bot, AbsBool.Bot, ExcSetEmpty)
      val (obj3, b3, excSet3) =
        if (AbsBool.False <= IsDataDescriptor(ownDesc)) {
          val desc = this.GetProperty(P, h)
          val newDesc = AbsDataProp(V, AbsBool.True, AbsBool.True, AbsBool.True)
          this.DefineOwnProperty(P, newDesc)
        } else (AbsObjectUtil.Bot, AbsBool.Bot, ExcSetEmpty)
      (obj2 + obj3, excSet1 ++ excSet2 ++ excSet3)
    } else
      (this, excSet1)
  }

  // Section 8.12.6 [[HasProperty]](P)
  def HasProperty(P: AbsString, h: Heap): AbsBool = {
    var visited = AbsLoc.Bot
    def visit(currObj: AbsObject): AbsBool = {
      val test = currObj contains P
      val b1 =
        if (AbsBool.True <= test) AbsBool.True
        else AbsBool.Bot
      val b2 =
        if (AbsBool.False <= test) {
          val protoV = currObj(IPrototype).value
          val b3 = protoV.pvalue.nullval.fold(AbsBool.Bot) { _ => AbsBool.False }
          b3 + protoV.locset.foldLeft[AbsBool](AbsBool.Bot)((b, protoLoc) =>
            if (visited contains protoLoc) b
            else {
              visited += protoLoc
              b + visit(h.get(protoLoc))
            })
        } else AbsBool.Bot
      b1 + b2
    }
    visit(this)
  }

  // Section 8.12.7 [[Delete]](P, Throw)
  def Delete(str: String): (AbsObject, AbsBool) = {
    if (this.isBottom) (this, AbsBool.Bot)
    else {
      val (newAMap, asuccess) = this.amap.delete(str)
      (new AbsObject(newAMap, this.imap), asuccess)
    }
  }

  def Delete(astr: AbsString): (AbsObject, AbsBool) = {
    if (this.isBottom) (this, AbsBool.Bot)
    else {
      val (newAMap, asuccess) = this.amap.delete(astr)
      (new AbsObject(newAMap, this.imap), asuccess)
    }
  }

  // Section 8.12.8 [[DefaultValue]](hint)
  def DefaultValue(hint: String, h: Heap): AbsPValue = {
    hint match {
      case "String" => DefaultValueAsString(h)
      case "Number" => DefaultValueAsNumber(h)
      case _ => DefaultValue(h)
    }
  }

  def DefaultValue(h: Heap): AbsPValue = {
    val className = this(IClass)
    val isDateClass = className.value.pvalue.strval === AbsString("Date")
    isDateClass.map(this.DefaultValueAsString(h), this.DefaultValueAsNumber(h))(AbsPValue)
  }

  private def DefaultValueAsString(h: Heap): AbsPValue = {
    val toString = this.Get("toString", h)
    val isCallable = TypeConversionHelper.IsCallable(toString, h)
    val str =
      if (AbsBool.True <= isCallable) AbsPValue(AbsString.Top)
      else AbsPValue.Bot
    if (AbsBool.False <= isCallable) {
      val valueOf = this.Get("valueOf", h)
      val value =
        if (AbsBool.True <= TypeConversionHelper.IsCallable(valueOf, h)) AbsPValue.Top
        else AbsPValue.Bot
      str + value
    } else str
  }

  private def DefaultValueAsNumber(h: Heap): AbsPValue = {
    val valueOf = this.Get("valueOf", h)
    val isCallable = TypeConversionHelper.IsCallable(valueOf, h)
    val value =
      if (AbsBool.True <= isCallable) AbsPValue.Top
      else AbsPValue.Bot
    if (AbsBool.False <= isCallable) {
      val toString = this.Get("toString", h)
      val str =
        if (AbsBool.True <= TypeConversionHelper.IsCallable(toString, h)) AbsPValue(AbsString.Top)
        else AbsPValue.Bot
      value + str
    } else value
  }

  //Section 8.12.9 [[DefineOwnProperty]](P, Desc, Throw)
  def DefineOwnProperty(P: AbsString, Desc: AbsDataProp, Throw: Boolean = true): (AbsObject, AbsBool, Set[Exception]) = {
    def Reject: (AbsBool, Set[Exception]) =
      if (Throw) (AbsBool.Bot, ExcSetEmpty + TypeError)
      else (AbsBool.False, ExcSetEmpty)

    val current = this.GetOwnProperty(P)
    val extensible = this(IExtensible).value.pvalue.boolval

    val (b, excSet) =
      if (AbsUndef.Top <= current.value && AbsBool.False <= extensible) Reject
      else (AbsBool.Bot, ExcSetEmpty)

    // below are estimation of DefineOwnProperty
    val objDomIn = this contains P
    if (objDomIn == AbsBool.Top) {
      val oldObjV = this(P)
      val newObjV = AbsDataProp(
        Desc.value,
        oldObjV.writable + Desc.writable,
        oldObjV.enumerable + Desc.enumerable,
        oldObjV.configurable + Desc.configurable
      )
      (this.update(P, newObjV), AbsBool.True + b, excSet)
    } else if (objDomIn == AbsBool.True) {
      val oldObjV: AbsDataProp = this(P)
      val newObjV = oldObjV.copyWith(Desc.value)
      (this.update(P, newObjV), AbsBool.True + b, excSet)
    } else if (objDomIn == AbsBool.False) {
      val newObjV = AbsDataProp(Desc.value, AbsBool.True, AbsBool.True, AbsBool.True)
      (this.update(P, newObjV), AbsBool.True + b, excSet)
    } else {
      (AbsObjectUtil.Bot, b, excSet)
    }
  }
}
