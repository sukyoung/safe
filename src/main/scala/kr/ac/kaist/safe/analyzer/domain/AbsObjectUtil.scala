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

import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util.Loc

import scala.collection.immutable.{ HashSet, HashMap }

case class AbsObjectUtil(utils: Utils) {
  private val pvalueU = utils.pvalue
  private val valueU = utils.value
  private val ivalueU = utils.ivalue
  private val absBoolU = utils.absBool
  private val atrue = absBoolU.True
  private val afalse = absBoolU.False
  private val absNumberU = utils.absNumber
  private val absStringU = utils.absString

  def apply(m: Map[String, (PropValue, Absent)]): Obj = new Obj(m, ObjEmptyIMap)
  def apply(m: Map[String, (PropValue, Absent)], im: ObjInternalMap): Obj = new Obj(m, im)

  val Bot: Obj = {
    val map = ObjEmptyMap +
      (STR_DEFAULT_NUMBER -> (PropValue.Bot(utils), AbsentBot)) +
      (STR_DEFAULT_OTHER -> (PropValue.Bot(utils), AbsentBot))
    apply(map)
  }

  val Empty: Obj = {
    val map = ObjEmptyMap +
      (STR_DEFAULT_NUMBER -> (PropValue.Bot(utils), AbsentTop)) +
      (STR_DEFAULT_OTHER -> (PropValue.Bot(utils), AbsentTop))
    apply(map)
  }

  ////////////////////////////////////////////////////////////////
  // new Object constructos
  ////////////////////////////////////////////////////////////////
  def newObject: Obj = newObject(BuiltinObjectProto.loc)

  def newObject(loc: Loc): Obj = newObject(HashSet(loc))

  def newObject(locSet: Set[Loc]): Obj = {
    Empty
      .update(IClass, ivalueU(absStringU.alpha("Object")))
      .update(IPrototype, ivalueU(locSet))
      .update(IExtensible, ivalueU(atrue))
  }

  def newArgObject(absLength: AbsNumber): Obj = {
    Empty
      .update(IClass, ivalueU(absStringU.alpha("Arguments")))
      .update(IPrototype, ivalueU(BuiltinObjectProto.loc))
      .update(IExtensible, ivalueU(atrue))
      .update("length", PropValue(utils.dataProp(absLength)(atrue, afalse, atrue)))
  }

  def newArrayObject(absLength: AbsNumber): Obj = {
    Empty
      .update(IClass, ivalueU(absStringU.alpha("Array")))
      .update(IPrototype, ivalueU(BuiltinArrayProto.loc))
      .update(IExtensible, ivalueU(atrue))
      .update("length", PropValue(utils.dataProp(absLength)(atrue, afalse, afalse)))
  }

  def newFunctionObject(fid: FunctionId, env: Value, l: Loc, n: AbsNumber): Obj = {
    newFunctionObject(Some(fid), Some(fid), env, Some(l), n)
  }

  def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: Value,
    locOpt: Option[Loc], n: AbsNumber): Obj = {
    newFunctionObject(fidOpt, constructIdOpt, env,
      locOpt, atrue, afalse, afalse, n)
  }

  def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: Value,
    locOpt: Option[Loc], writable: AbsBool, enumerable: AbsBool, configurable: AbsBool,
    absLength: AbsNumber): Obj = {
    val obj1 =
      Empty
        .update(IClass, ivalueU(absStringU.alpha("Function")))
        .update(IPrototype, ivalueU(BuiltinFunctionProto.loc))
        .update(IExtensible, ivalueU(atrue))
        .update(IScope, ivalueU(env))
        .update("length", PropValue(utils.dataProp(absLength)(afalse, afalse, afalse)))

    val obj2 = fidOpt match {
      case Some(fid) => obj1.update(ICall, ivalueU(fid))
      case None => obj1
    }
    val obj3 = constructIdOpt match {
      case Some(cid) => obj2.update(IConstruct, ivalueU(cid))
      case None => obj2
    }
    val obj4 = locOpt match {
      case Some(loc) =>
        val prototypeVal = valueU(HashSet(loc))
        obj3.update(IHasInstance, ivalueU(utils.absNull.Top))
          .update("prototype", PropValue(utils.dataProp(prototypeVal)(writable, enumerable, configurable)))
      case None => obj3
    }
    obj4
  }

  def newBooleanObj(absB: AbsBool): Obj = {
    val newObj = newObject(BuiltinBooleanProto.loc)
    newObj.update(IClass, ivalueU(absStringU.alpha("Boolean")))
      .update(IPrimitiveValue, ivalueU(absB))
  }

  def newNumberObj(absNum: AbsNumber): Obj = {
    val newObj = newObject(BuiltinNumberProto.loc)
    newObj.update(IClass, ivalueU(absStringU.alpha("Number")))
      .update(IPrimitiveValue, ivalueU(absNum))
  }

  def newStringObj(absStr: AbsString): Obj = {
    val newObj = newObject(BuiltinStringProto.loc)

    val newObj2 = newObj
      .update(IClass, ivalueU(absStringU.alpha("String")))
      .update(IPrimitiveValue, ivalueU(absStr))

    absStr.gamma match {
      case ConSetCon(strSet) =>
        strSet.foldLeft(Bot)((obj, str) => {
          val length = str.length
          val newObj3 = (0 until length).foldLeft(newObj2)((tmpObj, tmpIdx) => {
            val charAbsStr = absStringU.alpha(str.charAt(tmpIdx).toString)
            val charVal = valueU(charAbsStr)
            tmpObj.update(tmpIdx.toString, PropValue(utils.dataProp(charVal)(afalse, atrue, afalse)))
          })
          val lengthVal = valueU.alpha(length)
          obj + newObj3.update("length", PropValue(utils.dataProp(lengthVal)(afalse, afalse, afalse)))
        })
      case _ =>
        newObj2
          .update(absStringU.NumStr, PropValue(utils.dataProp(valueU(absStringU.Top))(afalse, atrue, afalse)), utils)
          .update("length", PropValue(utils.dataProp(absStr.length(absNumberU))(afalse, afalse, afalse)))
    }
  }

  def defaultValue(locSet: Set[Loc]): PValue = {
    if (locSet.isEmpty) pvalueU.Bot
    else pvalueU.Top
  }

  def defaultValue(locSet: Set[Loc], preferredType: String): PValue = {
    if (locSet.isEmpty) pvalueU.Bot
    else {
      preferredType match {
        case "Number" => pvalueU(absNumberU.Top)
        case "String" => pvalueU(absStringU.Top)
        case _ => pvalueU.Top
      }
    }
  }

  def defaultValue(locSet: Set[Loc], h: Heap, preferredType: String): PValue = {
    if (locSet.isEmpty) pvalueU.Bot
    else {
      preferredType match {
        case "Number" =>
          pvalueU(defaultValueNumber(locSet, h))
        case "String" =>
          pvalueU(defaultToString(locSet, h))
        case _ => pvalueU.Top
      }
    }
  }

  private def defaultValueNumber(locSet: Set[Loc], h: Heap): AbsNumber = {
    def getClassStrVal(obj: Obj): AbsString = {
      obj.getOrElse(IClass)(absStringU.Bot) { _.value.pvalue.strval }
    }

    val objSet = locSet.map(l => h.getOrElse(l, Bot))
    val boolObjSet = objSet.filter(obj => {
      absStringU.alpha("Boolean") <= getClassStrVal(obj)
    })
    val numObjSet = objSet.filter(obj => {
      absStringU.alpha("Number") <= getClassStrVal(obj)
    })
    val dateObjSet = objSet.filter(obj => {
      absStringU.alpha("Date") <= getClassStrVal(obj)
    })
    val strObjSet = objSet.filter(obj => {
      absStringU.alpha("String") <= getClassStrVal(obj)
    })
    val regexpObjSet = objSet.filter(obj => {
      absStringU.alpha("RegExp") <= getClassStrVal(obj)
    })
    val othersObjSet = objSet.filter(obj => {
      val absClassStr = getClassStrVal(obj)
      absClassStr != absStringU.alpha("Boolean") &&
        absClassStr != absStringU.alpha("Number") &&
        absClassStr != absStringU.alpha("String") &&
        absClassStr != absStringU.alpha("RegExp") &&
        absClassStr != absStringU.alpha("Date")
    })

    val others = othersObjSet.foldLeft[AbsString](absStringU.Bot)((absStr, obj) => {
      absStr + getClassStrVal(obj)
    })
    val b = boolObjSet.foldLeft[AbsBool](utils.absBool.Bot)((absBool, obj) => {
      absBool + obj.getOrElse(IPrimitiveValue)(utils.absBool.Bot) { _.value.pvalue.boolval }
    })
    val n = numObjSet.foldLeft[AbsNumber](absNumberU.Bot)((absNum, obj) => {
      absNum + obj.getOrElse(IPrimitiveValue)(absNumberU.Bot) { _.value.pvalue.numval }
    })
    val n2 = dateObjSet.foldLeft[AbsNumber](absNumberU.Bot)((absNum, obj) => {
      absNum + obj.getOrElse(IPrimitiveValue)(absNumberU.Bot) { _.value.pvalue.numval }
    })
    val (srcAbsStr, globalAbsB, ignoreCaseAbsB, multilineAbsB) =
      regexpObjSet.foldLeft[(AbsString, AbsBool, AbsBool, AbsBool)](
        (absStringU.Bot, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
      )((res, obj) => {
          val (tmpSrc, tmpGlobal, tmpIgnoreCase, tmpMultiline) = res
          (tmpSrc + obj.getOrElse("source")(absStringU.Bot) { _.objval.value.pvalue.strval },
            tmpGlobal + obj.getOrElse("global")(utils.absBool.Bot) { _.objval.value.pvalue.boolval },
            tmpIgnoreCase + obj.getOrElse("ignoreCase")(utils.absBool.Bot) { _.objval.value.pvalue.boolval },
            tmpMultiline + obj.getOrElse("multiline")(utils.absBool.Bot) { _.objval.value.pvalue.boolval })
        })

    val absStr1 = strObjSet.foldLeft[AbsString](absStringU.Bot)((absStr, obj) => {
      absStr + obj.getOrElse(IPrimitiveValue)(absStringU.Bot) { _.value.pvalue.strval }
    })
    val anum2 = b.toAbsNumber(absNumberU)
    val anum3 = n.toAbsNumber(absNumberU)
    val anum4 = n2.toAbsNumber(absNumberU)

    val absStr5 = (
      srcAbsStr.gammaSingle,
      globalAbsB.gamma,
      ignoreCaseAbsB.gamma,
      multilineAbsB.gamma
    ) match {
        case (ConSingleCon(s), ConSingleCon(g), ConSingleCon(i), ConSingleCon(m)) =>
          val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")
          absStringU.alpha("/" + s + "/" + flags)
        case (ConSingleBot(), _, _, _)
        | (_, ConSingleBot(), _, _)
        | (_, _, ConSingleBot(), _)
        | (_, _, _, ConSingleBot()) => absStringU.Bot
        case _ => absStringU.Top
      }

    val absStr6 = others.fold(absStringU.Bot)(_ => absStringU.Top)

    absStr1.toAbsNumber(absNumberU) + anum2 + anum3 + anum4 + absStr5.toAbsNumber(absNumberU) + absStr6.toAbsNumber(absNumberU)
  }

  private def defaultToString(locSet: Set[Loc], h: Heap): AbsString = {
    def getClassStrVal(obj: Obj): AbsString = {
      obj.getOrElse(IClass)(absStringU.Bot) { _.value.pvalue.strval }
    }
    val objSet = locSet.map(l => h.getOrElse(l, Bot))
    val boolObjSet = objSet.filter(obj => absStringU.alpha("Boolean") <= getClassStrVal(obj))
    val numObjSet = objSet.filter(obj => absStringU.alpha("Number") <= getClassStrVal(obj))
    val strObjSet = objSet.filter(obj => absStringU.alpha("String") <= getClassStrVal(obj))
    val regexpObjSet = objSet.filter(obj => absStringU.alpha("RegExp") <= getClassStrVal(obj))
    val othersObjSet = objSet.filter(obj => {
      val absClassStr = getClassStrVal(obj)
      absClassStr != absStringU.alpha("Boolean") &&
        absClassStr != absStringU.alpha("Number") &&
        absClassStr != absStringU.alpha("String") &&
        absClassStr != absStringU.alpha("RegExp")
    })

    val others = othersObjSet.foldLeft[AbsString](absStringU.Bot)((absStr, obj) => {
      absStr + getClassStrVal(obj)
    })
    val b = boolObjSet.foldLeft[AbsBool](utils.absBool.Bot)((absBool, obj) => {
      absBool + obj.getOrElse(IPrimitiveValue)(utils.absBool.Bot) { _.value.pvalue.boolval }
    })
    val n = numObjSet.foldLeft[AbsNumber](absNumberU.Bot)((absNum, obj) => {
      absNum + obj.getOrElse(IPrimitiveValue)(absNumberU.Bot) { _.value.pvalue.numval }
    })
    val (srcAbsStr, globalAbsB, ignoreCaseAbsB, multilineAbsB) =
      regexpObjSet.foldLeft[(AbsString, AbsBool, AbsBool, AbsBool)](
        (absStringU.Bot, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
      )((res, obj) => {
          val (tmpSrc, tmpGlobal, tmpIgnoreCase, tmpMultiline) = res
          (tmpSrc + obj.getOrElse("source")(absStringU.Bot) { _.objval.value.pvalue.strval },
            tmpGlobal + obj.getOrElse("global")(utils.absBool.Bot) { _.objval.value.pvalue.boolval },
            tmpIgnoreCase + obj.getOrElse("ignoreCase")(utils.absBool.Bot) { _.objval.value.pvalue.boolval },
            tmpMultiline + obj.getOrElse("multiline")(utils.absBool.Bot) { _.objval.value.pvalue.boolval })
        })

    val absStr1 = strObjSet.foldLeft[AbsString](absStringU.Bot)((absStr, obj) => {
      absStr + obj.getOrElse(IPrimitiveValue)(absStringU.Bot) { _.value.pvalue.strval }
    })
    val absStr2 = b.toAbsString(absStringU)
    val absStr3 = n.toAbsString(absStringU)
    val absStr4 = (
      srcAbsStr.gammaSingle,
      globalAbsB.gamma,
      ignoreCaseAbsB.gamma,
      multilineAbsB.gamma
    ) match {
        case (ConSingleCon(s), ConSingleCon(g), ConSingleCon(i), ConSingleCon(m)) =>
          val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")
          absStringU.alpha("/" + s + "/" + flags)
        case (ConSingleBot(), _, _, _)
        | (_, ConSingleBot(), _, _)
        | (_, _, ConSingleBot(), _)
        | (_, _, _, ConSingleBot()) => absStringU.Bot
        case _ => absStringU.Top
      }

    val absStr5 = others.fold(absStringU.Bot)(_ => {
      absStringU.Top
    })
    absStr1 + absStr2 + absStr3 + absStr4 + absStr5
  }
}