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
import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.analyzer.TypeConversionHelper
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._
import scala.collection.immutable.HashSet

/* 8.6 The Object Type */

////////////////////////////////////////////////////////////////////////////////
// concrete object type
////////////////////////////////////////////////////////////////////////////////
case class Object(amap: Map[String, DataProp], imap: Map[IName, IValue])

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

////////////////////////////////////////////////////////////////////////////////
// default object abstract domain
////////////////////////////////////////////////////////////////////////////////
object DefaultObject extends AbsObjectUtil {
  private lazy val atrue = AbsBool.True
  private lazy val afalse = AbsBool.False

  case object Top extends Dom
  case class ObjMap(
    val amap: AbsMap,
    val imap: ObjInternalMap = ObjEmptyIMap
  ) extends Dom
  lazy val Bot: AbsObject = ObjMap(AbsMapBot)
  lazy val Empty: AbsObject = ObjMap(AbsMapEmpty)

  def alpha(obj: Object): Dom = {
    val amap: AbsMap = obj.amap.foldLeft[AbsMap](AbsMapEmpty) {
      case (map, (key, dp)) => map.update(key, AbsDataProp(dp))
    }
    val imap: ObjInternalMap = obj.imap.foldLeft[ObjInternalMap](ObjEmptyIMap) {
      case (map, (iname, ivalue)) =>
        map + (iname -> (ivalue match {
          case FId(fid) => AbsIValue(AbsValue.Bot, HashSet(fid))
          case v: Value => AbsIValue(AbsValue(v), HashSet())
        }))
    }
    ObjMap(amap, imap)
  }

  sealed abstract class Dom extends AbsObject {
    def gamma: ConSet[Object] = ConInf() // TODO more precise

    def getSingle: ConSingle[Object] = ConMany() // TODO more precise

    def isBottom: Boolean = this == Bot
    def isTop: Boolean = this == Top

    def <=(that: AbsObject): Boolean = (this, check(that)) match {
      case (_, Top) => true
      case (Top, _) => false
      case (left: ObjMap, right: ObjMap) =>
        if (left.isEmpty) true
        else if (right.isEmpty) false
        else if (!(left.imap.keySet subsetOf right.imap.keySet)) false
        else (left.amap <= right.amap) &&
          right.imap.forall {
            case (key, rightIV) => {
              left.imap.get(key) match {
                case None => false
                case Some(leftIV) => leftIV <= rightIV
              }
            }
          }
    }

    def +(that: AbsObject): AbsObject = (this, check(that)) match {
      case (Top, _) | (_, Top) => Top
      case (left: ObjMap, right: ObjMap) =>
        val newAMap = left.amap + right.amap

        val ikeys = left.imap.keySet ++ right.imap.keySet
        val newIMap = ikeys.foldLeft(ObjEmptyIMap)((im, key) => {
          val leftIVal = left.imap.get(key)
          val rightIVal = right.imap.get(key)
          (leftIVal, rightIVal) match {
            case (None, None) => im
            case (None, Some(iv)) => im + (key -> iv)
            case (Some(iv), None) => im + (key -> iv)
            case (Some(iv1), Some(iv2)) => im + (key -> (iv1 + iv2))
          }
        })

        ObjMap(newAMap, newIMap)
    }

    def <>(that: AbsObject): AbsObject = (this, check(that)) match {
      case (Top, right) => right
      case (left, Top) => left
      case (left: ObjMap, right: ObjMap) =>
        if (left.amap eq right.amap) this
        else ObjMap(AbsMapEmpty, ObjEmptyIMap) // TODO is really sound?
    }

    override def toString: String = this match {
      case Top => "Top"
      case ObjMap(amap, imap) =>
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

    ////////////////////////////////////////////////////////////////
    // Additional untility functions
    ///////////////////////////////////////////////////////////////
    def isEmpty: Boolean = this == Empty

    def oldify(loc: Loc): AbsObject = loc match {
      case locR @ Recency(subLoc, Recent) => subsLoc(locR, Recency(subLoc, Old))
      case _ => this
    }

    /* substitute locR by locO */
    def subsLoc(locR: Recency, locO: Recency): AbsObject = this match {
      case Top => Top
      case obj @ ObjMap(amap, imap) =>
        if (obj.isEmpty) obj
        else {
          val newAMap = amap.mapValue { dp => dp.copyWith(dp.value.subsLoc(locR, locO)) }
          val newIMap = imap.foldLeft(ObjEmptyIMap)((im, kv) => {
            val (key, iv) = kv
            val newV = iv.value.subsLoc(locR, locO)
            im + (key -> AbsIValue(newV, iv.fidset))
          })
          ObjMap(newAMap, newIMap)
        }
    }

    def weakSubsLoc(locR: Recency, locO: Recency): AbsObject = this match {
      case Top => Top
      case obj @ ObjMap(amap, imap) =>
        if (obj.isEmpty) obj
        else {
          val newAMap = amap.mapValue { dp => dp.copyWith(dp.value.weakSubsLoc(locR, locO)) }
          val newIMap = imap.foldLeft(ObjEmptyIMap)((im, kv) => {
            val (key, iv) = kv
            val newV = iv.value.weakSubsLoc(locR, locO)
            im + (key -> AbsIValue(newV, iv.fidset))
          })
          ObjMap(newAMap, newIMap)
        }
    }

    def apply(str: String): AbsDataProp = this match {
      case Top => AbsDataProp.Top
      case ObjMap(amap, imap) =>
        val (dpset, success) = amap.lookup(str)
        if (dpset.nonEmpty) dpset.reduce(_ + _)
        else AbsDataProp.Bot
    }

    def apply(astr: AbsString): AbsDataProp = this match {
      case Top => AbsDataProp.Top
      case ObjMap(amap, imap) =>
        val (dpset, success) = amap.lookup(astr)
        if (dpset.nonEmpty) dpset.reduce(_ + _)
        else AbsDataProp.Bot
    }

    def apply(in: IName): AbsIValue = this match {
      case Top => AbsIValueUtil.Top
      case ObjMap(amap, imap) => imap.get(in) match {
        case Some(iv) => iv
        case None => AbsIValueUtil.Bot
      }
    }

    def initializeUpdate(str: String, dp: AbsDataProp): AbsObject = this match {
      case Top => Top
      case ObjMap(amap, imap) => ObjMap(amap.initializeUpdate(str, dp), imap)
    }

    // absent value is set to AbsentBot because it is strong update.
    def update(str: String, dp: AbsDataProp, weak: Boolean = false): AbsObject = this match {
      case Top => Top
      case obj @ ObjMap(amap, imap) =>
        if (obj.isBottom) obj
        else ObjMap(amap.update(str, dp, weak), imap)
    }

    def update(astr: AbsString, dp: AbsDataProp): AbsObject = this match {
      case Top => Top
      case ObjMap(amap, imap) =>
        ObjMap(amap.update(astr, dp), imap)
    }

    def update(in: IName, iv: AbsIValue): AbsObject = this match {
      case Top => Top
      case ObjMap(amap, imap) =>
        // val newIv = iv + this(in)
        val newIv = iv
        ObjMap(amap, imap + (in -> newIv))
    }

    def contains(str: String): AbsBool = this match {
      case Top => AbsBool.Top
      case ObjMap(amap, imap) => amap contains str
    }

    def contains(strSet: Set[String]): AbsBool = this match {
      case Top => AbsBool.Top
      case obj @ ObjMap(amap, imap) =>
        strSet.foldLeft(AbsBool.Bot)((absB, str) => absB + (obj contains str))
    }

    def contains(astr: AbsString): AbsBool = this match {
      case Top => AbsBool.Top
      case ObjMap(amap, imap) => amap contains astr
    }

    def contains(in: IName): AbsBool = this match {
      case Top => AbsBool.Top
      case ObjMap(amap, imap) =>
        imap.get(in) match {
          case None => AbsBool.False
          case Some(_) => AbsBool.Top
        }
    }

    def abstractKeySet: ConSet[AbsString] = this match {
      case Top => ConInf()
      case ObjMap(amap, _) => ConFin(amap.abstractKeySet)
    }
    def abstractKeySet(filter: (AbsString, AbsDataProp) => Boolean): ConSet[AbsString] = this match {
      case Top => ConInf()
      case ObjMap(amap, _) => ConFin(amap.abstractKeySet(filter))
    }
    def collectKeySet(prefix: String): ConSet[String] = this match {
      case Top => ConInf()
      case ObjMap(amap, _) => amap.collectKeySet(prefix)
    }
    def isDefinite(str: AbsString): Boolean = this match {
      case Top => false
      case ObjMap(amap, _) => amap.isDefinite(str)
    }

    ////////////////////////////////////////////////////////////////
    // internal methods of ECMAScript Object
    ///////////////////////////////////////////////////////////////
    // Section 8.12.1 [[GetOwnProperty]](P)
    def GetOwnProperty(P: String): (AbsDesc, AbsUndef) =
      GetOwnProperty(AbsString(P))

    def GetOwnProperty(P: AbsString): (AbsDesc, AbsUndef) = this match {
      case Top => (AbsDesc.Top, AbsUndef.Top)
      case ObjMap(amap, imap) =>
        val (dpset, definite) = amap.lookup(P)
        val dp = dpset.foldLeft(AbsDataProp.Bot)(_ + _)
        val undef =
          if (definite) AbsUndef.Bot else AbsUndef.Top
        val desc = AbsDesc(
          (dp.value, AbsAbsent.Bot),
          (dp.writable, AbsAbsent.Bot),
          (dp.enumerable, AbsAbsent.Bot),
          (dp.configurable, AbsAbsent.Bot)
        )
        (desc, undef)
    }

    // Section 8.12.2 [[GetProperty]](P)
    def GetProperty(P: String, h: AbsHeap): (AbsDesc, AbsUndef) =
      GetProperty(AbsString(P), h)

    def GetProperty(P: AbsString, h: AbsHeap): (AbsDesc, AbsUndef) = this match {
      case Top => (AbsDesc.Top, AbsUndef.Top)
      case obj @ ObjMap(amap, imap) =>
        var visited = HashSet[Loc]()
        def visit(currObj: AbsObject): (AbsDesc, AbsUndef) = {
          val (desc, undef) = currObj.GetOwnProperty(P)
          val (parentDesc, parentUndef) =
            if (undef.isBottom) (AbsDesc.Bot, AbsUndef.Bot)
            else {
              val proto = currObj(IPrototype)
              val undef =
                if (proto.value.pvalue.nullval.isBottom) AbsUndef.Top
                else AbsUndef.Bot
              proto.value.locset.foldLeft((AbsDesc.Bot, AbsUndef.Bot)) {
                case ((desc, undef), loc) => {
                  if (visited contains loc) (desc, undef)
                  else {
                    visited += loc
                    val (newDesc, newUndef) = visit(h.get(loc))
                    (desc + newDesc, undef + newUndef)
                  }
                }
              }
            }
          (desc + parentDesc, undef + parentUndef)
        }
        visit(obj)
    }

    // Section 8.12.3 [[Get]](P)
    def Get(str: String, h: AbsHeap): AbsValue = this match {
      case Top => AbsValue.Top
      case obj @ ObjMap(amap, imap) =>
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
        visit(obj)
    }

    def Get(astr: AbsString, h: AbsHeap): AbsValue = this match {
      case Top => AbsValue.Top
      case obj @ ObjMap(amap, imap) =>
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
        visit(obj)
    }

    // Section 8.12.4 [[CanPut]](P)
    def CanPut(P: String, h: AbsHeap): AbsBool = CanPut(AbsString(P), h)

    def CanPut(P: AbsString, h: AbsHeap): AbsBool = this match {
      case Top => AbsBool.Top
      case obj @ ObjMap(amap, imap) =>
        val (desc, undef) = GetOwnProperty(P)
        val (b, _) = desc.writable
        val newB = {
          if (undef.isBottom) AbsBool.Bot
          else {
            val proto = obj(IPrototype).value
            val extensible = obj(IExtensible).value.pvalue.boolval
            val nullProtoCase =
              if (proto.pvalue.nullval.isBottom) AbsBool.Bot
              else extensible
            val (inheritDesc, inheritUndef) = proto.locset.foldLeft((AbsDesc.Bot, AbsUndef.Bot)) {
              case ((desc, undef), loc) => {
                val (newDesc, newUndef) = GetProperty(P, h)
                (desc + newDesc, undef + newUndef)
              }
            }
            val undefInheritCase =
              if (inheritUndef.isBottom) AbsBool.Bot
              else extensible
            val inheritCase = extensible.map[AbsBool](
              elseV = AbsBool.False,
              thenV = { val (b, _) = inheritDesc.writable; b }
            )(AbsBool)
            nullProtoCase + undefInheritCase + inheritCase
          }
        }
        b + newB
    }

    // Section 8.12.5 [[Put]](P, V, Throw)
    def Put(P: AbsString, V: AbsValue, Throw: Boolean = true, h: AbsHeap): (AbsObject, Set[Exception]) = this match {
      case Top => (Top, HashSet(TypeError, RangeError))
      case obj @ ObjMap(amap, imap) =>
        val canPut = CanPut(P, h)
        val (falseObj: AbsObject, falseExcSet: Set[Exception]) =
          if (AbsBool.False <= canPut) {
            if (Throw) (Bot, HashSet(TypeError))
            else (obj, ExcSetEmpty)
          } else (Bot, ExcSetEmpty)
        val (trueObj, trueExcSet) =
          if (AbsBool.True <= canPut) {
            val (ownDesc, ownUndef) = GetOwnProperty(P)
            val (ownObj, ownExcSet) =
              if (ownDesc.isBottom) (Bot, ExcSetEmpty)
              else {
                val valueDesc = AbsDesc((V, AbsAbsent.Bot))
                val (o, _, e) = DefineOwnProperty(h, P, valueDesc, Throw)
                (o, e)
              }
            val (undefObj, undefExcSet) =
              if (ownUndef.isBottom) (Bot, ExcSetEmpty)
              else {
                val desc = GetProperty(P, h)
                val newDesc = AbsDesc(
                  (V, AbsAbsent.Bot),
                  (AbsBool.True, AbsAbsent.Bot),
                  (AbsBool.True, AbsAbsent.Bot),
                  (AbsBool.True, AbsAbsent.Bot)
                )
                val (o, _, e) = DefineOwnProperty(h, P, newDesc, Throw)
                (o, e)
              }
            (ownObj + undefObj, ownExcSet ++ undefExcSet)
          } else (Bot, ExcSetEmpty)
        (falseObj + trueObj, falseExcSet ++ trueExcSet)
    }

    // Section 8.12.6 [[HasProperty]](P)
    def HasProperty(P: AbsString, h: AbsHeap): AbsBool = this match {
      case Top => AbsBool.Top
      case obj @ ObjMap(amap, imap) =>
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
        visit(obj)
    }

    // Section 8.12.7 [[Delete]](P, Throw)
    def Delete(str: String): (AbsObject, AbsBool) = this match {
      case Top => (Top, AbsBool.Top)
      case obj @ ObjMap(amap, imap) =>
        if (obj.isBottom) (obj, AbsBool.Bot)
        else {
          val (newAMap, asuccess) = amap.delete(str)
          (ObjMap(newAMap, imap), asuccess)
        }
    }

    def Delete(astr: AbsString): (AbsObject, AbsBool) = this match {
      case Top => (Top, AbsBool.Top)
      case obj @ ObjMap(amap, imap) =>
        if (obj.isBottom) (obj, AbsBool.Bot)
        else {
          val (newAMap, asuccess) = amap.delete(astr)
          (ObjMap(newAMap, imap), asuccess)
        }
    }

    // Section 8.12.8 [[DefaultValue]](hint)
    def DefaultValue(hint: String, h: AbsHeap): AbsPValue = {
      hint match {
        case "String" => DefaultValueAsString(h)
        case "Number" => DefaultValueAsNumber(h)
        case _ => DefaultValue(h)
      }
    }

    def DefaultValue(h: AbsHeap): AbsPValue = this match {
      case Top => AbsPValue(numval = AbsNumber.Top, strval = AbsString.Top)
      case obj @ ObjMap(amap, imap) =>
        val className = obj(IClass)
        val isDateClass = className.value.pvalue.strval === AbsString("Date")
        isDateClass.map(DefaultValueAsString(h), DefaultValueAsNumber(h))(AbsPValue)
    }

    private def DefaultValueAsString(h: AbsHeap): AbsPValue = this match {
      case Top => AbsString.Top
      case ObjMap(_, _) =>
        val toString = Get("toString", h)
        val isCallable = TypeConversionHelper.IsCallable(toString, h)
        val str =
          if (AbsBool.True <= isCallable) AbsPValue(strval = AbsString.Top)
          else AbsPValue.Bot
        if (AbsBool.False <= isCallable) {
          val valueOf = Get("valueOf", h)
          val value =
            if (AbsBool.True <= TypeConversionHelper.IsCallable(valueOf, h)) AbsPValue.Top
            else AbsPValue.Bot
          str + value
        } else str
    }

    private def DefaultValueAsNumber(h: AbsHeap): AbsPValue = this match {
      case Top => AbsNumber.Top
      case ObjMap(_, _) =>
        val valueOf = Get("valueOf", h)
        val isCallable = TypeConversionHelper.IsCallable(valueOf, h)
        val value =
          if (AbsBool.True <= isCallable) AbsPValue.Top
          else AbsPValue.Bot
        if (AbsBool.False <= isCallable) {
          val toString = Get("toString", h)
          val str =
            if (AbsBool.True <= TypeConversionHelper.IsCallable(toString, h)) AbsPValue(strval = AbsString.Top)
            else AbsPValue.Bot
          value + str
        } else value
    }

    //Section 8.12.9 [[DefineOwnProperty]](P, Desc, Throw)
    def DefineOwnProperty(h: AbsHeap, P: AbsString, Desc: AbsDesc, Throw: Boolean = true): (AbsObject, AbsBool, Set[Exception]) = this match {
      case Top => (Top, AbsBool.Top, HashSet(TypeError, RangeError))
      case obj @ ObjMap(_, _) =>
        val Reject =
          if (Throw) (obj, AbsBool.Bot, HashSet(TypeError))
          else (obj, AbsBool.False, ExcSetEmpty)
        val BotTriple = (Bot, AbsBool.Bot, ExcSetEmpty)
        val (current, curUndef) = GetOwnProperty(P)
        val extensible = obj(IExtensible).value.pvalue.boolval

        val (obj1, b1, excSet1) = if (curUndef.isTop) {
          val (obj1, b1, excSet1) =
            if (AbsBool.True <= extensible) {
              val changedObj = update(P, AbsDataProp(Desc))
              (changedObj, AbsBool.True, ExcSetEmpty)
            } else BotTriple
          val (obj2: AbsObject, b2: AbsBool, excSet2: Set[Exception]) =
            if (AbsBool.False <= extensible) Reject
            else BotTriple
          (obj1 + obj2, b1 + b2, excSet1 ++ excSet2)
        } else BotTriple

        val (cv, cva) = current.value
        val (cw, cwa) = current.writable
        val (ce, cea) = current.enumerable
        val (cc, cca) = current.configurable
        val (dv, dva) = Desc.value
        val (dw, dwa) = Desc.writable
        val (de, dea) = Desc.enumerable
        val (dc, dca) = Desc.configurable

        // 5. Return true, if every field in Desc is absent.
        val (obj5, b5) =
          if (dva.isTop && dwa.isTop && dea.isTop && dca.isTop) (obj, AbsBool.True)
          else (Bot, AbsBool.Bot)
        // 6. Return true, if every field in Desc also occurs in current and same
        val (obj6, b6) =
          if ((dva.isTop || (!dv.isBottom && AbsBool.True <= (TypeConversionHelper.SameValue(h, dv, cv)))) &&
            (dwa.isTop || (!dw.isBottom && AbsBool.True <= (dw === cw))) &&
            (dea.isTop || (!de.isBottom && AbsBool.True <= (de === ce))) &&
            (dca.isTop || (!dc.isBottom && AbsBool.True <= (dc === cc)))) (obj, AbsBool.True)
          else (Bot, AbsBool.Bot)

        val (obj2: AbsObject, b2: AbsBool, excSet2: Set[Exception]) =
          if (AbsBool.False <= cc && AbsBool.False <= cw) Reject
          else BotTriple

        val (obj3, b3, excSet3) =
          if (AbsBool.True <= cc || AbsBool.True <= cw) {
            var newDP = obj(P)
            if (!dv.isBottom) newDP = newDP.copyWith(value = dv)
            if (!dw.isBottom) newDP = newDP.copyWith(writable = dw)
            if (!de.isBottom) newDP = newDP.copyWith(enumerable = de)
            if (!dc.isBottom) newDP = newDP.copyWith(configurable = dc)
            val changedObj = update(P, newDP)
            (changedObj, AbsBool.True, ExcSetEmpty)
          } else BotTriple

        val excSet4 =
          if (AbsString("Array") <= obj(IClass).value.pvalue.strval &&
            AbsString("length") <= P &&
            AbsBool.False <= (TypeConversionHelper.ToNumber(dv) === TypeConversionHelper.ToUInt32(dv))) HashSet(RangeError)
          else ExcSetEmpty
        (obj1 + obj2 + obj3 + obj5 + obj6, b1 + b2 + b3 + b5 + b6, excSet1 ++ excSet2 ++ excSet3 ++ excSet4)
    }
  }

  ////////////////////////////////////////////////////////////////
  // new Object constructors
  ////////////////////////////////////////////////////////////////
  def newObject: AbsObject = newObject(BuiltinObjectProto.loc)

  def newObject(loc: Loc): AbsObject = newObject(AbsLoc(loc))

  def newObject(locSet: AbsLoc): AbsObject = {
    Empty
      .update(IClass, AbsIValueUtil(AbsString("Object")))
      .update(IPrototype, AbsIValueUtil(locSet))
      .update(IExtensible, AbsIValueUtil(atrue))
  }

  def newArgObject(absLength: AbsNumber = AbsNumber(0)): AbsObject = {
    Empty
      .update(IClass, AbsIValueUtil(AbsString("Arguments")))
      .update(IPrototype, AbsIValueUtil(BuiltinObjectProto.loc))
      .update(IExtensible, AbsIValueUtil(atrue))
      .update("length", AbsDataProp(absLength, atrue, afalse, atrue))
  }

  def newArrayObject(absLength: AbsNumber = AbsNumber(0)): AbsObject = {
    Empty
      .update(IClass, AbsIValueUtil(AbsString("Array")))
      .update(IPrototype, AbsIValueUtil(BuiltinArrayProto.loc))
      .update(IExtensible, AbsIValueUtil(atrue))
      .update("length", AbsDataProp(absLength, atrue, afalse, afalse))
  }

  def newFunctionObject(fid: FunctionId, env: AbsValue, l: Loc, n: AbsNumber): AbsObject = {
    newFunctionObject(Some(fid), Some(fid), env, Some(l), n)
  }

  def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: AbsValue,
    locOpt: Option[Loc], n: AbsNumber): AbsObject = {
    newFunctionObject(fidOpt, constructIdOpt, env,
      locOpt, atrue, afalse, afalse, n)
  }

  def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: AbsValue,
    locOpt: Option[Loc], writable: AbsBool, enumerable: AbsBool, configurable: AbsBool,
    absLength: AbsNumber): AbsObject = {
    val obj1 =
      Empty
        .update(IClass, AbsIValueUtil(AbsString("Function")))
        .update(IPrototype, AbsIValueUtil(BuiltinFunctionProto.loc))
        .update(IExtensible, AbsIValueUtil(atrue))
        .update(IScope, AbsIValueUtil(env))
        .update("length", AbsDataProp(absLength, afalse, afalse, afalse))

    val obj2 = fidOpt match {
      case Some(fid) => obj1.update(ICall, AbsIValueUtil(fid))
      case None => obj1
    }
    val obj3 = constructIdOpt match {
      case Some(cid) => obj2.update(IConstruct, AbsIValueUtil(cid))
      case None => obj2
    }
    val obj4 = locOpt match {
      case Some(loc) =>
        val prototypeVal = AbsValue(loc)
        obj3.update(IHasInstance, AbsIValueUtil(AbsNull.Top))
          .update("prototype", AbsDataProp(prototypeVal, writable, enumerable, configurable))
      case None => obj3
    }
    obj4
  }

  def newBooleanObj(absB: AbsBool): AbsObject = {
    val newObj = newObject(BuiltinBooleanProto.loc)
    newObj.update(IClass, AbsIValueUtil(AbsString("Boolean")))
      .update(IPrimitiveValue, AbsIValueUtil(absB))
  }

  def newNumberObj(absNum: AbsNumber): AbsObject = {
    val newObj = newObject(BuiltinNumberProto.loc)
    newObj.update(IClass, AbsIValueUtil(AbsString("Number")))
      .update(IPrimitiveValue, AbsIValueUtil(absNum))
  }

  def newStringObj(absStr: AbsString): AbsObject = {
    val newObj = newObject(BuiltinStringProto.loc)

    val newObj2 = newObj
      .update(IClass, AbsIValueUtil(AbsString("String")))
      .update(IPrimitiveValue, AbsIValueUtil(absStr))

    absStr.gamma match {
      case ConFin(strSet) =>
        strSet.foldLeft(Bot)((obj, str) => {
          val length = str.length
          val newObj3 = (0 until length).foldLeft(newObj2)((tmpObj, tmpIdx) => {
            val charAbsStr = AbsString(str.charAt(tmpIdx).toString)
            val charVal = AbsValue(charAbsStr)
            tmpObj.update(tmpIdx.toString, AbsDataProp(charVal, afalse, atrue, afalse))
          })
          val lengthVal = AbsValue(length)
          obj + newObj3.update("length", AbsDataProp(lengthVal, afalse, afalse, afalse))
        })
      case _ =>
        newObj2
          .update(AbsString.Number, AbsDataProp(AbsValue(AbsString.Top), afalse, atrue, afalse))
          .update("length", AbsDataProp(absStr.length, afalse, afalse, afalse))
    }
  }

  def newErrorObj(errorName: String, protoLoc: Loc): AbsObject = {
    Empty
      .update(IClass, AbsIValueUtil(AbsString(errorName)))
      .update(IPrototype, AbsIValueUtil(protoLoc))
      .update(IExtensible, AbsIValueUtil(AbsBool.True))
  }

  def defaultValue(locSet: AbsLoc): AbsPValue = {
    if (locSet.isBottom) AbsPValue.Bot
    else AbsPValue.Top
  }

  def defaultValue(locSet: AbsLoc, preferredType: String): AbsPValue = {
    if (locSet.isBottom) AbsPValue.Bot
    else {
      preferredType match {
        case "Number" => AbsPValue(numval = AbsNumber.Top)
        case "String" => AbsPValue(strval = AbsString.Top)
        case _ => AbsPValue.Top
      }
    }
  }

  def defaultValue(locSet: AbsLoc, h: AbsHeap, preferredType: String): AbsPValue = {
    if (locSet.isBottom) AbsPValue.Bot
    else locSet.foldLeft(AbsPValue.Bot)((pv, loc) => h.get(loc).DefaultValue(preferredType, h))
  }

  // 8.10.4 FromPropertyDescriptor ( Desc )
  def FromPropertyDescriptor(h: AbsHeap, desc: AbsDesc): (AbsObject, Set[Exception]) = {
    def put(
      obj: AbsObject,
      name: String,
      pair: (AbsValue, AbsAbsent)
    ): (AbsObject, AbsBool, Set[Exception]) = {
      val T = (AbsBool.True, AbsAbsent.Bot)
      obj.DefineOwnProperty(
        h,
        AbsString(name),
        AbsDesc(pair, T, T, T),
        false
      )
    }
    def toValue(pair: (AbsBool, AbsAbsent)): (AbsValue, AbsAbsent) = {
      val (b, a) = pair
      (AbsValue(b), a)
    }
    val (obj1, _, excSet1) = put(newObject, "value", desc.value)
    val (obj2, _, excSet2) = put(obj1, "writable", toValue(desc.writable))
    val (obj3, _, excSet3) = put(obj2, "enumerable", toValue(desc.enumerable))
    val (obj4, _, excSet4) = put(obj3, "configurable", toValue(desc.configurable))
    (obj4, excSet1 ++ excSet2 ++ excSet3 ++ excSet4)
  }
}
