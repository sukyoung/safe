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

  def newArgObject(absLength: AbsNumber): AbsObject = {
    Empty
      .update(IClass, InternalValueUtil(AbsString("Arguments")))
      .update(IPrototype, InternalValueUtil(BuiltinObjectProto.loc))
      .update(IExtensible, InternalValueUtil(atrue))
      .update("length", AbsDataProp(absLength, atrue, afalse, atrue))
  }

  def newArrayObject(absLength: AbsNumber): AbsObject = {
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

  def defaultValue(locSet: AbsLoc): AbsPValue = {
    if (locSet.isBottom) AbsPValue.Bot
    else AbsPValue.Top
  }

  def defaultValue(locSet: AbsLoc, preferredType: String): AbsPValue = {
    if (locSet.isBottom) AbsPValue.Bot
    else {
      preferredType match {
        case "Number" => AbsPValue(AbsNumber.Top)
        case "String" => AbsPValue(AbsString.Top)
        case _ => AbsPValue.Top
      }
    }
  }

  def defaultValue(locSet: AbsLoc, h: Heap, preferredType: String): AbsPValue = {
    if (locSet.isBottom) AbsPValue.Bot
    else {
      preferredType match {
        case "Number" =>
          AbsPValue(defaultValueNumber(locSet, h))
        case "String" =>
          AbsPValue(defaultToString(locSet, h))
        case _ => AbsPValue.Top
      }
    }
  }

  private def defaultValueNumber(locSet: AbsLoc, h: Heap): AbsNumber = {
    def getClassStrVal(obj: AbsObject): AbsString = {
      obj(IClass).value.pvalue.strval
    }

    val objSet = locSet.map(l => h.get(l))
    val boolObjSet = objSet.filter(obj => {
      AbsString("Boolean") <= getClassStrVal(obj)
    })
    val numObjSet = objSet.filter(obj => {
      AbsString("Number") <= getClassStrVal(obj)
    })
    val dateObjSet = objSet.filter(obj => {
      AbsString("Date") <= getClassStrVal(obj)
    })
    val strObjSet = objSet.filter(obj => {
      AbsString("String") <= getClassStrVal(obj)
    })
    val regexpObjSet = objSet.filter(obj => {
      AbsString("RegExp") <= getClassStrVal(obj)
    })
    val othersObjSet = objSet.filter(obj => {
      val absClassStr = getClassStrVal(obj)
      absClassStr != AbsString("Boolean") &&
        absClassStr != AbsString("Number") &&
        absClassStr != AbsString("String") &&
        absClassStr != AbsString("RegExp") &&
        absClassStr != AbsString("Date")
    })

    val others = othersObjSet.foldLeft[AbsString](AbsString.Bot)((absStr, obj) => {
      absStr + getClassStrVal(obj)
    })
    val b = boolObjSet.foldLeft[AbsBool](AbsBool.Bot)((absBool, obj) => {
      absBool + obj(IPrimitiveValue).value.pvalue.boolval
    })
    val n = numObjSet.foldLeft[AbsNumber](AbsNumber.Bot)((absNum, obj) => {
      absNum + obj(IPrimitiveValue).value.pvalue.numval
    })
    val n2 = dateObjSet.foldLeft[AbsNumber](AbsNumber.Bot)((absNum, obj) => {
      absNum + obj(IPrimitiveValue).value.pvalue.numval
    })
    val (srcAbsStr, globalAbsB, ignoreCaseAbsB, multilineAbsB) =
      regexpObjSet.foldLeft[(AbsString, AbsBool, AbsBool, AbsBool)](
        (AbsString.Bot, AbsBool.Bot, AbsBool.Bot, AbsBool.Bot)
      )((res, obj) => {
          val (tmpSrc, tmpGlobal, tmpIgnoreCase, tmpMultiline) = res
          (tmpSrc + obj("source").value.pvalue.strval,
            tmpGlobal + obj("global").value.pvalue.boolval,
            tmpIgnoreCase + obj("ignoreCase").value.pvalue.boolval,
            tmpMultiline + obj("multiline").value.pvalue.boolval)
        })

    val absStr1 = strObjSet.foldLeft[AbsString](AbsString.Bot)((absStr, obj) => {
      absStr + obj(IPrimitiveValue).value.pvalue.strval
    })
    val anum2 = TypeConversionHelper.ToNumber(b)
    val anum3 = n
    val anum4 = n2

    val absStr5 = (
      srcAbsStr.getSingle,
      globalAbsB.getSingle,
      ignoreCaseAbsB.getSingle,
      multilineAbsB.getSingle
    ) match {
        case (ConOne(s), ConOne(g), ConOne(i), ConOne(m)) =>
          val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")
          AbsString("/" + s + "/" + flags)
        case (ConZero(), _, _, _)
        | (_, ConZero(), _, _)
        | (_, _, ConZero(), _)
        | (_, _, _, ConZero()) => AbsString.Bot
        case _ => AbsString.Top
      }

    val absStr6 = others.fold(AbsString.Bot)(_ => AbsString.Top)

    absStr1.toAbsNumber + anum2 + anum3 + anum4 + absStr5.toAbsNumber + absStr6.toAbsNumber
  }

  private def defaultToString(locSet: AbsLoc, h: Heap): AbsString = {
    def getClassStrVal(obj: AbsObject): AbsString = {
      obj(IClass).value.pvalue.strval
    }
    val objSet = locSet.map(l => h.get(l))
    val boolObjSet = objSet.filter(obj => AbsString("Boolean") <= getClassStrVal(obj))
    val numObjSet = objSet.filter(obj => AbsString("Number") <= getClassStrVal(obj))
    val strObjSet = objSet.filter(obj => AbsString("String") <= getClassStrVal(obj))
    val regexpObjSet = objSet.filter(obj => AbsString("RegExp") <= getClassStrVal(obj))
    val othersObjSet = objSet.filter(obj => {
      val absClassStr = getClassStrVal(obj)
      absClassStr != AbsString("Boolean") &&
        absClassStr != AbsString("Number") &&
        absClassStr != AbsString("String") &&
        absClassStr != AbsString("RegExp")
    })

    val others = othersObjSet.foldLeft[AbsString](AbsString.Bot)((absStr, obj) => {
      absStr + getClassStrVal(obj)
    })
    val b = boolObjSet.foldLeft[AbsBool](AbsBool.Bot)((absBool, obj) => {
      absBool + obj(IPrimitiveValue).value.pvalue.boolval
    })
    val n = numObjSet.foldLeft[AbsNumber](AbsNumber.Bot)((absNum, obj) => {
      absNum + obj(IPrimitiveValue).value.pvalue.numval
    })
    val (srcAbsStr, globalAbsB, ignoreCaseAbsB, multilineAbsB) =
      regexpObjSet.foldLeft[(AbsString, AbsBool, AbsBool, AbsBool)](
        (AbsString.Bot, AbsBool.Bot, AbsBool.Bot, AbsBool.Bot)
      )((res, obj) => {
          val (tmpSrc, tmpGlobal, tmpIgnoreCase, tmpMultiline) = res
          (tmpSrc + obj("source").value.pvalue.strval,
            tmpGlobal + obj("global").value.pvalue.boolval,
            tmpIgnoreCase + obj("ignoreCase").value.pvalue.boolval,
            tmpMultiline + obj("multiline").value.pvalue.boolval)
        })

    val absStr1 = strObjSet.foldLeft[AbsString](AbsString.Bot)((absStr, obj) => {
      absStr + obj(IPrimitiveValue).value.pvalue.strval
    })
    val absStr2 = b.toAbsString
    val absStr3 = n.toAbsString
    val absStr4 = (
      srcAbsStr.getSingle,
      globalAbsB.getSingle,
      ignoreCaseAbsB.getSingle,
      multilineAbsB.getSingle
    ) match {
        case (ConOne(s), ConOne(g), ConOne(i), ConOne(m)) =>
          val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")
          AbsString("/" + s + "/" + flags)
        case (ConZero(), _, _, _)
        | (_, ConZero(), _, _)
        | (_, _, ConZero(), _)
        | (_, _, _, ConZero()) => AbsString.Bot
        case _ => AbsString.Top
      }

    val absStr5 = others.fold(AbsString.Bot)(_ => {
      AbsString.Top
    })
    absStr1 + absStr2 + absStr3 + absStr4 + absStr5
  }
}
