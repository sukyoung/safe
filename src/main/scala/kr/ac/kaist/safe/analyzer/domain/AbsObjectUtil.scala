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

import kr.ac.kaist.safe.analyzer.TypeConversionHelper
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.nodes.cfg._

import scala.collection.immutable.{ HashSet, HashMap }

object AbsObjectUtil {
  private val atrue = AbsBool.True
  private val afalse = AbsBool.False

  def apply(m: AbsMap): AbsObject = new AbsObject(m, ObjEmptyIMap)
  def apply(m: AbsMap, im: ObjInternalMap): AbsObject = new AbsObject(m, im)

  val Bot: AbsObject = apply(AbsMapBot)
  val Empty: AbsObject = apply(AbsMapEmpty)

  ////////////////////////////////////////////////////////////////
  // new Object constructos
  ////////////////////////////////////////////////////////////////
  def newObject: AbsObject = newObject(BuiltinObjectProto.loc)

  def newObject(loc: Loc): AbsObject = newObject(AbsLoc(loc))

  def newObject(locSet: AbsLoc): AbsObject = {
    Empty
      .update(IClass, InternalValueUtil(AbsString("Object")))
      .update(IPrototype, InternalValueUtil(locSet))
      .update(IExtensible, InternalValueUtil(atrue))
  }

  def newArgObject(absLength: AbsNumber = AbsNumber(0)): AbsObject = {
    Empty
      .update(IClass, InternalValueUtil(AbsString("Arguments")))
      .update(IPrototype, InternalValueUtil(BuiltinObjectProto.loc))
      .update(IExtensible, InternalValueUtil(atrue))
      .update("length", AbsDataProp(absLength, atrue, afalse, atrue))
  }

  def newArrayObject(absLength: AbsNumber = AbsNumber(0)): AbsObject = {
    Empty
      .update(IClass, InternalValueUtil(AbsString("Array")))
      .update(IPrototype, InternalValueUtil(BuiltinArrayProto.loc))
      .update(IExtensible, InternalValueUtil(atrue))
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
        .update(IClass, InternalValueUtil(AbsString("Function")))
        .update(IPrototype, InternalValueUtil(BuiltinFunctionProto.loc))
        .update(IExtensible, InternalValueUtil(atrue))
        .update(IScope, InternalValueUtil(env))
        .update("length", AbsDataProp(absLength, afalse, afalse, afalse))

    val obj2 = fidOpt match {
      case Some(fid) => obj1.update(ICall, InternalValueUtil(fid))
      case None => obj1
    }
    val obj3 = constructIdOpt match {
      case Some(cid) => obj2.update(IConstruct, InternalValueUtil(cid))
      case None => obj2
    }
    val obj4 = locOpt match {
      case Some(loc) =>
        val prototypeVal = AbsValue(loc)
        obj3.update(IHasInstance, InternalValueUtil(AbsNull.Top))
          .update("prototype", AbsDataProp(prototypeVal, writable, enumerable, configurable))
      case None => obj3
    }
    obj4
  }

  def newBooleanObj(absB: AbsBool): AbsObject = {
    val newObj = newObject(BuiltinBooleanProto.loc)
    newObj.update(IClass, InternalValueUtil(AbsString("Boolean")))
      .update(IPrimitiveValue, InternalValueUtil(absB))
  }

  def newNumberObj(absNum: AbsNumber): AbsObject = {
    val newObj = newObject(BuiltinNumberProto.loc)
    newObj.update(IClass, InternalValueUtil(AbsString("Number")))
      .update(IPrimitiveValue, InternalValueUtil(absNum))
  }

  def newStringObj(absStr: AbsString): AbsObject = {
    val newObj = newObject(BuiltinStringProto.loc)

    val newObj2 = newObj
      .update(IClass, InternalValueUtil(AbsString("String")))
      .update(IPrimitiveValue, InternalValueUtil(absStr))

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
      .update(IClass, InternalValueUtil(AbsString(errorName)))
      .update(IPrototype, InternalValueUtil(protoLoc))
      .update(IExtensible, InternalValueUtil(AbsBool.True))
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

  def defaultValue(locSet: AbsLoc, h: Heap, preferredType: String): AbsPValue = {
    if (locSet.isBottom) AbsPValue.Bot
    else locSet.foldLeft(AbsPValue.Bot)((pv, loc) => h.get(loc).DefaultValue(preferredType, h))
  }

  // 8.10.4 FromPropertyDescriptor ( Desc )
  def FromPropertyDescriptor(desc: AbsDesc): (AbsObject, Set[Exception]) = {
    def put(
      obj: AbsObject,
      name: String,
      pair: (AbsValue, AbsAbsent)
    ): (AbsObject, AbsBool, Set[Exception]) = {
      val T = (AbsBool.True, AbsAbsent.Bot)
      obj.DefineOwnProperty(
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
