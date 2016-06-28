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

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.cfg_builder.AddressManager
import kr.ac.kaist.safe.config.Config
import kr.ac.kaist.safe.nodes.{ CapturedCatchVar, CapturedVar, GlobalVar, PureLocalVar, CFGExpr, CFGId, FunctionId }
import scala.util.Try
import scala.collection.immutable.HashSet

case class Helper(utils: Utils, addrManager: AddressManager, predefLoc: PredefLoc) {

  def allocObject(h: Heap, locSetV: Set[Loc], locR: Loc): Heap = {
    val newObj = newObject(locSetV)
    h.update(locR, newObj)
  }

  def canPut(h: Heap, loc: Loc, absStr: AbsString): AbsBool = canPutHelp(h, loc, absStr, loc)

  def canPutHelp(h: Heap, loc1: Loc, absStr: AbsString, loc2: Loc): AbsBool = {
    val domInStr = (h.getOrElse(loc1, utils.ObjBot) domIn absStr)(utils.absBool)
    val b1 =
      if (utils.absBool.True <= domInStr) {
        val obj = h.getOrElse(loc1, utils.ObjBot)
        obj.getOrElse(absStr, utils.PropValueBot).objval.writable
      } else utils.absBool.Bot
    val b2 =
      if (utils.absBool.False <= domInStr) {
        val protoObj = h.getOrElse(loc1, utils.ObjBot).getOrElse("@proto", utils.PropValueBot)
        val protoLocSet = protoObj.objval.value.locset
        val b3 = protoObj.objval.value.pvalue.nullval.fold(utils.absBool.Bot)(_ => {
          h.getOrElse(loc2, utils.ObjBot)
            .getOrElse("@extensible", utils.PropValueBot).objval.value.pvalue.boolval
        })
        protoLocSet.foldLeft(b3)((absB, loc) => {
          absB + canPutHelp(h, loc, absStr, loc2)
        })
      } else utils.absBool.Bot
    b1 + b2
  }

  def canPutVar(h: Heap, x: String): AbsBool = {
    val globalLoc = predefLoc.GLOBAL_LOC
    val globalObj = h.getOrElse(globalLoc, utils.ObjBot)
    val domIn = (globalObj domIn x)(utils.absBool)
    val b1 =
      if (utils.absBool.True <= domIn) globalObj.getOrElse(x, utils.PropValueBot).objval.writable
      else utils.absBool.Bot
    val b2 =
      if (utils.absBool.False <= domIn) canPut(h, globalLoc, utils.absString.alpha(x))
      else utils.absBool.Bot
    b1 + b2
  }

  def createMutableBinding(h: Heap, id: CFGId, value: Value): Heap = {
    val x = id.text
    id.kind match {
      case PureLocalVar =>
        val localLoc = predefLoc.SINGLE_PURE_LOCAL_LOC
        val objV = ObjectValue(value, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.False)
        val propV = PropValue(objV)
        h.update(localLoc, h.getOrElse(localLoc, utils.ObjBot).update(x, propV))
      case CapturedVar =>
        val localLoc = predefLoc.SINGLE_PURE_LOCAL_LOC
        val objV = ObjectValue(value, utils.absBool.True, utils.absBool.Bot, utils.absBool.False)
        val propV = PropValue(objV)
        val localObj = h.getOrElse(localLoc, utils.ObjBot)
        localObj.getOrElse("@env", utils.PropValueBot).objval.value.locset.foldLeft(Heap.Bot)((tmpHeap, loc) => {
          tmpHeap + h.update(loc, h.getOrElse(loc, utils.ObjBot).update(x, propV))
        })
      case CapturedCatchVar =>
        val collapsedLoc = predefLoc.COLLAPSED_LOC
        val objV = ObjectValue(value, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.False)
        val propV = PropValue(objV)
        h.update(collapsedLoc, h.getOrElse(collapsedLoc, utils.ObjBot).update(x, propV))
      case GlobalVar =>
        val globalLoc = predefLoc.GLOBAL_LOC
        val objV = ObjectValue(value, utils.absBool.True, utils.absBool.True, utils.absBool.False)
        val propV = PropValue(objV)
        if (utils.absBool.True == hasProperty(h, globalLoc, utils.absString.alpha(x))) h
        else h.update(globalLoc, h.getOrElse(globalLoc, utils.ObjBot).update(x, propV))
    }
  }

  def delete(h: Heap, loc: Loc, absStr: AbsString): (Heap, AbsBool) = {
    val test = hasOwnProperty(h, loc, absStr)
    val targetObj = h.getOrElse(loc, utils.ObjBot)
    val isConfigurable = targetObj.getOrElse(absStr, utils.PropValueBot).objval.configurable
    val (h1, b1) =
      if ((utils.absBool.True <= test) && (utils.absBool.False <= isConfigurable))
        (h, utils.absBool.False)
      else
        (Heap.Bot, utils.absBool.Bot)
    val (h2, b2) =
      if (((utils.absBool.True <= test) && (utils.absBool.False != isConfigurable))
        || utils.absBool.False <= test)
        (h.update(loc, targetObj - absStr), utils.absBool.True)
      else
        (Heap.Bot, utils.absBool.Bot)
    (h1 + h2, b1 + b2)
  }

  def raiseException(st: State, excSet: Set[Exception]): State = {
    if (excSet.isEmpty)
      State(Heap.Bot, Context.Bot)
    else {
      val localLoc = predefLoc.SINGLE_PURE_LOCAL_LOC
      val localObj = st.heap.getOrElse(localLoc, utils.ObjBot)
      val oldValue = localObj.getOrElse("@exception_all", utils.PropValueBot).objval.value
      val newExcSet = excSet.foldLeft(LocSetEmpty)((locSet, exc) => locSet + newExceptionLoc(exc))
      val excValue = Value(utils.PValueBot, newExcSet)
      val newExcObjV = utils.ObjectValueBot.copyWith(excValue)
      val newExcSetObjV = utils.ObjectValueBot.copyWith(excValue + oldValue)
      val h1 = st.heap.update(
        localLoc,
        localObj.update("@exception", PropValue(newExcObjV)).
          update("@exception_all", PropValue(newExcSetObjV))
      )
      State(h1, st.context)
    }
  }

  def newExceptionLoc(exc: Exception): Loc = {
    exc match {
      case Error => predefLoc.ERR_LOC
      case EvalError => predefLoc.EVAL_ERR_LOC
      case RangeError => predefLoc.RANGE_ERR_LOC
      case ReferenceError => predefLoc.REF_ERR_LOC
      case SyntaxError => predefLoc.SYNTAX_ERR_LOC
      case TypeError => predefLoc.TYPE_ERR_LOC
      case URIError => predefLoc.URI_ERR_LOC
    }
  }

  def getThis(h: Heap, value: Value): Set[Loc] = {
    val locSet1 = (value.pvalue.nullval.gamma, value.pvalue.undefval.gamma) match {
      case (ConSimpleBot, ConSimpleBot) => LocSetEmpty
      case _ => HashSet(predefLoc.GLOBAL_LOC)
    }

    val foundDeclEnvRecord = value.locset.exists(loc => utils.absBool.False <= isObject(h, loc))

    val locSet2 =
      if (foundDeclEnvRecord) HashSet(predefLoc.GLOBAL_LOC)
      else LocSetEmpty
    val locSet3 = value.locset.foldLeft(LocSetEmpty)((tmpLocSet, loc) => {
      if (utils.absBool.True <= isObject(h, loc)) tmpLocSet + loc
      else tmpLocSet
    })

    locSet1 ++ locSet2 ++ locSet3
  }

  def hasConstruct(h: Heap, loc: Loc): AbsBool = {
    val isDomIn = (h.getOrElse(loc, utils.ObjBot) domIn "@construct")(utils.absBool)
    val b1 =
      if (utils.absBool.True <= isDomIn) utils.absBool.True
      else utils.absBool.Bot
    val b2 =
      if (utils.absBool.False <= isDomIn) utils.absBool.False
      else utils.absBool.Bot
    b1 + b2
  }

  def hasInstance(h: Heap, loc: Loc): AbsBool = {
    val isDomIn = (h.getOrElse(loc, utils.ObjBot) domIn "@hasinstance")(utils.absBool)
    val b1 =
      if (utils.absBool.True <= isDomIn) utils.absBool.True
      else utils.absBool.Bot
    val b2 =
      if (utils.absBool.False <= isDomIn) utils.absBool.False
      else utils.absBool.Bot
    b1 + b2
  }

  def hasProperty(h: Heap, loc: Loc, absStr: AbsString): AbsBool = {
    var visited = LocSetEmpty
    def visit(currentLoc: Loc): AbsBool = {
      if (visited.contains(currentLoc)) utils.absBool.Bot
      else {
        visited += currentLoc
        val test = hasOwnProperty(h, currentLoc, absStr)
        val b1 =
          if (utils.absBool.True <= test) utils.absBool.True
          else utils.absBool.Bot
        val b2 =
          if (utils.absBool.False <= test) {
            val protoV = h.getOrElse(currentLoc, utils.ObjBot).getOrElse("@proto", utils.PropValueBot).objval.value
            val b3 = protoV.pvalue.nullval.fold(utils.absBool.Bot) { _ => utils.absBool.False }
            b3 + protoV.locset.foldLeft[AbsBool](utils.absBool.Bot)((b, protoLoc) => {
              b + visit(protoLoc)
            })
          } else {
            utils.absBool.Bot
          }
        b1 + b2
      }
    }
    visit(loc)
  }

  def hasOwnProperty(h: Heap, loc: Loc, absStr: AbsString): AbsBool = {
    (h.getOrElse(loc, utils.ObjBot) domIn absStr)(utils.absBool)
  }

  def inherit(h: Heap, loc1: Loc, loc2: Loc, bopSEq: (Value, Value) => Value): Value = {
    var visited = LocSetEmpty
    def iter(h: Heap, l1: Loc, l2: Loc): Value = {
      if (visited.contains(l1)) utils.ValueBot
      else {
        visited += l1
        val locVal1 = Value(utils.PValueBot, HashSet(l1))
        val locVal2 = Value(utils.PValueBot, HashSet(l2))
        val eqVal = bopSEq(locVal1, locVal2)
        val boolBotVal = Value(utils.PValueBot.copyWith(utils.absBool.Bot))
        val boolTrueVal = Value(utils.PValueBot.copyWith(utils.absBool.True))
        val boolFalseVal = Value(utils.PValueBot.copyWith(utils.absBool.False))
        val v1 =
          if (utils.absBool.True <= eqVal.pvalue.boolval) boolTrueVal
          else boolBotVal
        val v2 = boolBotVal
        if (utils.absBool.False <= eqVal.pvalue.boolval) {
          val protoVal = h.getOrElse(l1, utils.ObjBot).getOrElse("@proto", utils.PropValueBot).objval.value
          val v1 = protoVal.pvalue.nullval.fold(boolBotVal) { _ => boolFalseVal }
          v1 + protoVal.locset.foldLeft(utils.ValueBot)((tmpVal, protoLoc) => tmpVal + iter(h, protoLoc, l2))
        } else boolBotVal
        v1 + v2
      }
    }

    iter(h, loc1, loc1)
  }

  def isArray(h: Heap, loc: Loc): AbsBool = {
    val classNamePropV = h.getOrElse(loc, utils.ObjBot).getOrElse("@class", utils.PropValueBot)
    val arrayAbsStr = utils.absString.alpha("Array")
    val b1 =
      if (arrayAbsStr <= classNamePropV.objval.value.pvalue.strval)
        utils.absBool.True
      else
        utils.absBool.Bot
    val b2 =
      if (arrayAbsStr != classNamePropV.objval.value.pvalue.strval)
        utils.absBool.False
      else
        utils.absBool.Bot
    b1 + b2
  }

  def isArrayIndex(absStr: AbsString): AbsBool = {
    absStr.gamma match {
      case ConSetBot() => utils.absBool.Bot
      case ConSetTop() => utils.absBool.Top
      case ConSetCon(strSet) =>
        val upper = scala.math.pow(2, 32) - 1
        strSet.foldLeft(utils.absBool.Bot)((res, v) => {
          res + utils.absBool.alpha({
            isNum(v) && {
              val num = v.toDouble
              0 <= num && num < upper
            }
          })
        })
    }
  }

  def isCallable(h: Heap, loc: Loc): AbsBool = {
    val isDomIn = (h.getOrElse(loc, utils.ObjBot) domIn "@function")(utils.absBool)
    val b1 =
      if (utils.absBool.True <= isDomIn) utils.absBool.True
      else utils.absBool.Bot
    val b2 =
      if (utils.absBool.False <= isDomIn) utils.absBool.False
      else utils.absBool.Bot
    b1 + b2
  }

  def isCallable(h: Heap, value: Value): AbsBool = utils.absBool.Bot

  def isObject(h: Heap, loc: Loc): AbsBool = {
    (h.getOrElse(loc, utils.ObjBot) domIn "@class")(utils.absBool)
  }

  def lookup(h: Heap, id: CFGId): (Value, Set[Exception]) = {
    val x = id.text
    val localObj = h.getOrElse(predefLoc.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
    id.kind match {
      case PureLocalVar => (localObj.getOrElse(x, utils.PropValueBot).objval.value, ExceptionSetEmpty)
      case CapturedVar =>
        val envLocSet = localObj.getOrElse("@env", utils.PropValueBot).objval.value.locset
        val value = envLocSet.foldLeft(utils.ValueBot)((tmpVal, envLoc) => {
          tmpVal + lookupL(h, envLoc, x)
        })
        (value, ExceptionSetEmpty)
      case CapturedCatchVar =>
        val collapsedObj = h.getOrElse(predefLoc.COLLAPSED_LOC, utils.ObjBot)
        (collapsedObj.getOrElse(x, utils.PropValueBot).objval.value, ExceptionSetEmpty)
      case GlobalVar => lookupG(h, x)
    }
  }

  def lookupG(h: Heap, x: String): (Value, Set[Exception]) = {
    if (h domIn predefLoc.GLOBAL_LOC) {
      val globalObj = h.getOrElse(predefLoc.GLOBAL_LOC, utils.ObjBot)
      val v1 =
        if (utils.absBool.True <= (globalObj domIn x)(utils.absBool))
          globalObj.getOrElse(x, utils.PropValueBot).objval.value
        else
          utils.ValueBot
      val protoLocSet = globalObj.getOrElse("@proto", utils.PropValueBot).objval.value.locset
      val (v2, excSet) =
        if (utils.absBool.False <= (globalObj domIn x)(utils.absBool)) {
          val excSet = protoLocSet.foldLeft(ExceptionSetEmpty)((excSet, protoLoc) => {
            if (utils.absBool.False <= hasProperty(h, protoLoc, utils.absString.alpha(x))) {
              excSet + ReferenceError
            } else {
              excSet
            }
          })
          val v3 = protoLocSet.foldLeft(utils.ValueBot)((tmpVal, protoLoc) => {
            if (utils.absBool.True <= hasProperty(h, protoLoc, utils.absString.alpha(x))) {
              tmpVal + proto(h, protoLoc, utils.absString.alpha(x))
            } else {
              tmpVal
            }
          })
          (v3, excSet)
        } else {
          (utils.ValueBot, ExceptionSetEmpty)
        }
      (v1 + v2, excSet)
    } else {
      (utils.ValueBot, ExceptionSetEmpty)
    }
  }

  def lookupL(h: Heap, loc: Loc, x: String): Value = {
    var visited = LocSetEmpty
    def visit(l: Loc): Value = {
      if (visited.contains(l)) utils.ValueBot
      else {
        visited += l
        val env = h.getOrElse(l, utils.ObjBot)
        val isDomIn = (env domIn x)(utils.absBool)
        val v1 =
          if (utils.absBool.True <= isDomIn) env.getOrElse(x, utils.PropValueBot).objval.value
          else utils.ValueBot
        val v2 =
          if (utils.absBool.False <= isDomIn) {
            val outerLocSet = env.getOrElse("@outer", utils.PropValueBot).objval.value.locset
            outerLocSet.foldLeft(utils.ValueBot)((tmpVal, outerLoc) => tmpVal + visit(outerLoc))
          } else {
            utils.ValueBot
          }
        v1 + v2
      }
    }
    visit(loc)
  }

  def lookupBase(h: Heap, id: CFGId): Set[Loc] = {
    val x = id.text
    id.kind match {
      case PureLocalVar => HashSet(predefLoc.SINGLE_PURE_LOCAL_LOC)
      case CapturedVar =>
        val localObj = h.getOrElse(predefLoc.SINGLE_PURE_LOCAL_LOC, utils.ObjBot)
        val envLocSet = localObj.getOrElse("@env", utils.PropValueBot).objval.value.locset
        envLocSet.foldLeft(LocSetEmpty)((tmpLocSet, l) => {
          tmpLocSet ++ lookupBaseL(h, l, x)
        })
      case CapturedCatchVar => HashSet(predefLoc.COLLAPSED_LOC)
      case GlobalVar => lookupBaseG(h, x)
    }
  }

  def lookupBaseG(h: Heap, x: String): Set[Loc] = {
    val globalObj = h.getOrElse(predefLoc.GLOBAL_LOC, utils.ObjBot)
    val isDomIn = (globalObj domIn x)(utils.absBool)
    val locSet1 =
      if (utils.absBool.True <= isDomIn)
        HashSet(predefLoc.GLOBAL_LOC)
      else
        LocSetEmpty
    val locSet2 =
      if (utils.absBool.False <= isDomIn) {
        val protoLocSet = globalObj.getOrElse("@proto", utils.PropValueBot).objval.value.locset
        protoLocSet.foldLeft(LocSetEmpty)((res, protoLoc) => {
          res ++ protoBase(h, protoLoc, utils.absString.alpha(x))
        })
      } else {
        LocSetEmpty
      }
    locSet1 ++ locSet2
  }

  def lookupBaseL(h: Heap, loc: Loc, x: String): Set[Loc] = {
    var visited = LocSetEmpty
    def visit(l: Loc): Set[Loc] = {
      if (visited.contains(l)) LocSetEmpty
      else {
        visited += l
        val env = h.getOrElse(l, utils.ObjBot)
        val isDomIn = (env domIn x)(utils.absBool)
        val locSet1 =
          if (utils.absBool.True <= isDomIn) HashSet(l)
          else LocSetEmpty
        val locSet2 =
          if (utils.absBool.False <= isDomIn) {
            val outerLocSet = env.getOrElse("@outer", utils.PropValueBot).objval.value.locset
            outerLocSet.foldLeft(LocSetEmpty)((res, outerLoc) => res ++ visit(outerLoc))
          } else {
            LocSetEmpty
          }
        locSet1 ++ locSet2
      }
    }
    visit(loc)
  }

  def newBoolean(absB: AbsBool): Obj = {
    val newObj = newObject(predefLoc.BOOLEAN_PROTO) //TODO BOOLEAN_PROTO => BuiltinBoolean.ProtoLoc
    newObj.update("@class", PropValue(utils.ObjectValueWith(utils.absString.alpha("Boolean"))))
      .update("@primitive", PropValue(utils.ObjectValueWith(absB)))
  }

  def newNumber(absNum: AbsNumber): Obj = {
    val newObj = newObject(predefLoc.NUMBER_PROTO) //TODO Number_PROTO => BuiltinNumber.ProtoLoc
    newObj.update("@class", PropValue(utils.ObjectValueWith(utils.absString.alpha("Number"))))
      .update("@primitive", PropValue(utils.ObjectValueWith(absNum)))
  }

  def newString(absStr: AbsString): Obj = {
    val newObj = newObject(predefLoc.STRING_PROTO) //TODO STRING_PROTO => BuiltinString.ProtoLoc

    val newObj2 = newObj
      .update("@class", PropValue(utils.ObjectValueWith(utils.absString.alpha("String"))))
      .update("@primitive", PropValue(utils.ObjectValueWith(absStr)))

    val absFalse = utils.absBool.False
    val absTrue = utils.absBool.True
    absStr.gamma match {
      case ConSetCon(strSet) =>
        strSet.foldLeft(utils.ObjBot)((obj, str) => {
          val length = str.length
          val newObj3 = (0 until length).foldLeft(newObj2)((tmpObj, tmpIdx) => {
            val charAbsStr = utils.absString.alpha(str.charAt(tmpIdx).toString)
            val charVal = Value(utils.PValueBot.copyWith(charAbsStr))
            tmpObj.update(tmpIdx.toString, PropValue(ObjectValue(charVal, absFalse, absTrue, absFalse)))
          })
          val lengthVal = Value(utils.PValueBot.copyWith(utils.absNumber.alpha(length)))
          obj + newObj3.update("length", PropValue(ObjectValue(lengthVal, absFalse, absFalse, absFalse)))
        })
      case _ =>
        val strTopVal = Value(utils.PValueBot.copyWith(utils.absString.Top))
        val lengthVal = Value(utils.PValueBot.copyWith(absStr.length(utils.absNumber)))
        newObj2
          .update(utils.absString.NumStr, PropValue(ObjectValue(strTopVal, absFalse, absTrue, absFalse)), utils)
          .update("length", PropValue(ObjectValue(lengthVal, absFalse, absFalse, absFalse)))
    }
  }

  def newDeclEnvRecord(outerEnv: Value): Obj = {
    val outerEnvObjV = ObjectValue(outerEnv, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
    utils.ObjEmpty.update("@outer", PropValue(outerEnvObjV))
  }

  def newObject(): Obj = utils.ObjBot

  def newObject(loc: Loc): Obj = newObject(HashSet(loc))

  def newObject(locSet: Set[Loc]): Obj = {
    val protoVal = Value(utils.PValueBot, locSet)
    val absFalse = utils.absBool.False
    utils.ObjEmpty
      .update("@class", PropValue(utils.ObjectValueWith(utils.absString.alpha("Object"))))
      .update("@proto", PropValue(ObjectValue(protoVal, absFalse, absFalse, absFalse)))
      .update("@extensible", PropValue(utils.ObjectValueWith(utils.absBool.True)))
  }

  def newArgObject(absLength: AbsNumber): Obj = {
    val protoVal = Value(utils.PValueBot, HashSet(predefLoc.OBJ_PROTO_LOC))
    val lengthVal = Value(utils.PValueBot.copyWith(absLength))
    val absFalse = utils.absBool.False
    val absTrue = utils.absBool.True
    utils.ObjEmpty
      .update("@class", PropValue(utils.ObjectValueWith(utils.absString.alpha("Arguments"))))
      .update("@proto", PropValue(ObjectValue(protoVal, absFalse, absFalse, absFalse)))
      .update("@extensible", PropValue(utils.ObjectValueWith(absTrue)))
      .update("length", PropValue(ObjectValue(lengthVal, absTrue, absFalse, absTrue)))
  }

  def newArrayObject(absLength: AbsNumber): Obj = {
    val protoVal = Value(utils.PValueBot, HashSet(predefLoc.ARRAY_PROTO)) //TODO ARRAY_PROTO => BuiltinArray.ProtoLoc
    val lengthVal = Value(utils.PValueBot.copyWith(absLength))
    val absFalse = utils.absBool.False
    utils.ObjEmpty
      .update("@class", PropValue(utils.ObjectValueWith(utils.absString.alpha("Array"))))
      .update("@proto", PropValue(ObjectValue(protoVal, absFalse, absFalse, absFalse)))
      .update("@extensible", PropValue(utils.ObjectValueWith(utils.absBool.True)))
      .update("length", PropValue(ObjectValue(lengthVal, utils.absBool.True, absFalse, absFalse)))
  }

  def newFunctionObject(fid: FunctionId, env: Value, l: Loc, n: AbsNumber): Obj = {
    newFunctionObject(Some(fid), Some(fid), env, Some(l), n)
  }

  private def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: Value,
    locOpt: Option[Loc], n: AbsNumber): Obj = {
    newFunctionObject(fidOpt, constructIdOpt, env,
      locOpt, utils.absBool.True, utils.absBool.False, utils.absBool.False, n)
  }

  private def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: Value,
    locOpt: Option[Loc], writable: AbsBool, enumerable: AbsBool, configurable: AbsBool,
    absLength: AbsNumber): Obj = {
    val protoVal = Value(utils.PValueBot, HashSet(predefLoc.FUNCTION_PROTO_LOC))
    val absFalse = utils.absBool.False
    val lengthVal = Value(utils.PValueBot.copyWith(absLength))
    val obj1 = utils.ObjEmpty
      .update("@class", PropValue(utils.ObjectValueWith(utils.absString.alpha("Function"))))
      .update("@proto", PropValue(ObjectValue(protoVal, absFalse, absFalse, absFalse)))
      .update("@extensible", PropValue(utils.ObjectValueWith(utils.absBool.True)))
      .update("@scope", PropValue(utils.ObjectValueBot.copyWith(env)))
      .update("length", PropValue(ObjectValue(lengthVal, absFalse, absFalse, absFalse)))

    val obj2 = fidOpt match {
      case Some(fid) => obj1.update("@function", PropValue(utils.ObjectValueBot, HashSet(fid)))
      case None => obj1
    }
    val obj3 = constructIdOpt match {
      case Some(cid) => obj2.update("@construct", PropValue(utils.ObjectValueBot, HashSet(cid)))
      case None => obj2
    }
    val obj4 = locOpt match {
      case Some(loc) =>
        val prototypeVal = Value(utils.PValueBot, HashSet(loc))
        obj3.update("@hasinstance", PropValue(utils.ObjectValueWith(utils.absNull.Top)))
          .update("prototype", PropValue(ObjectValue(prototypeVal, writable, enumerable, configurable)))
      case None => obj3
    }
    obj4
  }

  def newPureLocal(envVal: Value, thisLocSet: Set[Loc]): Obj = {
    val thisVal = Value(utils.PValueBot, thisLocSet)
    utils.ObjEmpty
      .update("@env", PropValue(utils.ObjectValueBot.copyWith(envVal)))
      .update("@this", PropValue(utils.ObjectValueBot.copyWith(thisVal))).
      update("@exception", utils.PropValueBot).
      update("@exception_all", utils.PropValueBot).
      update("@return", PropValue(utils.ObjectValueWith(utils.absUndef.Top)))
  }

  def oldify(st: State, addr: Address): State = {
    if (st.context.isBottom) State.Bot
    else {
      val locR = addrManager.addrToLoc(addr, Recent)
      val locO = addrManager.addrToLoc(addr, Old)
      val h1 =
        if ((st.heap domIn locR))
          st.heap.update(locO, st.heap.getOrElse(locR, utils.ObjBot)).remove(locR).subsLoc(locR, locO)
        else
          st.heap.subsLoc(locR, locO)
      val ctx1 = st.context.subsLoc(locR, locO, addrManager)
      State(h1, ctx1)
    }
  }

  def fixOldify(ctx: Context, obj: Obj, mayOld: Set[Address], mustOld: Set[Address]): (Context, Obj) = {
    if (ctx.isBottom) (Context.Bot, utils.ObjBot)
    else {
      mayOld.foldLeft((ctx, obj))((res, a) => {
        val (resCtx, resObj) = res
        val locR = addrManager.addrToLoc(a, Recent)
        val locO = addrManager.addrToLoc(a, Old)
        if (mustOld contains a) {
          val newCtx = resCtx.subsLoc(locR, locO, addrManager)
          val newObj = resObj.subsLoc(locR, locO)
          (newCtx, newObj)
        } else {
          val newCtx = resCtx.weakSubsLoc(locR, locO, addrManager)
          val newObj = resObj.weakSubsLoc(locR, locO)
          (newCtx, newObj)
        }
      })
    }
  }

  def proto(h: Heap, loc: Loc, absStr: AbsString): Value = {
    var visited = LocSetEmpty
    def visit(currentLoc: Loc): Value = {
      if (visited.contains(currentLoc)) utils.ValueBot
      else {
        visited += currentLoc
        val test = (h.getOrElse(currentLoc, utils.ObjBot) domIn absStr)(utils.absBool)
        val v1 =
          if (utils.absBool.True <= test) {
            h.getOrElse(currentLoc, utils.ObjBot).getOrElse(absStr, utils.PropValueBot).objval.value
          } else {
            utils.ValueBot
          }
        val v2 =
          if (utils.absBool.False <= test) {
            val protoV = h.getOrElse(currentLoc, utils.ObjBot).getOrElse("@proto", utils.PropValueBot).objval.value
            val v3 = protoV.pvalue.nullval.fold(utils.ValueBot)(_ => {
              Value(utils.PValueBot.copyWith(utils.absUndef.Top))
            })
            v3 + protoV.locset.foldLeft(utils.ValueBot)((v, protoLoc) => {
              v + visit(protoLoc)
            })
          } else {
            utils.ValueBot
          }
        v1 + v2
      }
    }
    visit(loc)
  }

  def protoBase(h: Heap, loc: Loc, absStr: AbsString): Set[Loc] = {
    var visited = LocSetEmpty

    def visit(l: Loc): Set[Loc] = {
      if (visited.contains(l)) LocSetEmpty
      else {
        visited += l
        val obj = h.getOrElse(l, utils.ObjBot)
        val isDomIn = (obj domIn absStr)(utils.absBool)
        val locSet1 =
          if (utils.absBool.True <= isDomIn) HashSet(l)
          else LocSetEmpty
        val locSet2 =
          if (utils.absBool.False <= isDomIn) {
            val protoLocSet = obj.getOrElse("@proto", utils.PropValueBot).objval.value.locset
            protoLocSet.foldLeft(LocSetEmpty)((res, protoLoc) => {
              res ++ visit(protoLoc)
            })
          } else {
            LocSetEmpty
          }
        locSet1 ++ locSet2
      }
    }
    visit(loc)
  }

  def varStore(h: Heap, id: CFGId, value: Value): Heap = {
    val pureLocalLoc = predefLoc.SINGLE_PURE_LOCAL_LOC
    val x = id.text
    id.kind match {
      case PureLocalVar =>
        val pv = PropValue(ObjectValue(value, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.False))
        val obj = h.getOrElse(pureLocalLoc, utils.ObjBot)
        h.update(pureLocalLoc, obj.update(x, pv))
      case CapturedVar =>
        val propV = h.getOrElse(pureLocalLoc, utils.ObjBot).getOrElse("@env", utils.PropValueBot)
        propV.objval.value.locset.foldLeft(Heap.Bot)((tmpH, loc) => {
          tmpH + varStoreL(h, loc, x, value)
        })
      case CapturedCatchVar =>
        val propV = PropValue(ObjectValue(value, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.False))
        val obj = h.getOrElse(predefLoc.COLLAPSED_LOC, utils.ObjBot)
        h.update(predefLoc.COLLAPSED_LOC, obj.update(x, propV))
      case GlobalVar => {
        val h1 =
          if (utils.absBool.True <= canPutVar(h, x)) varStoreG(h, x, value)
          else Heap.Bot
        val h2 =
          if (utils.absBool.False <= canPutVar(h, x)) h
          else Heap.Bot
        h1 + h2
      }
    }
  }

  def varStoreL(h: Heap, loc: Loc, x: String, value: Value): Heap = {
    val envObj = h.getOrElse(loc, utils.ObjBot)
    val h1 = envObj(x) match {
      case Some(propV) if propV.objval.writable == utils.absBool.True =>
        val newPropV = PropValue(ObjectValue(value, utils.absBool.True, utils.absBool.Bot, utils.absBool.False))
        h.update(loc, envObj.update(x, newPropV))
      case Some(propV) if propV.objval.writable == utils.absBool.False => h
      case _ => Heap.Bot
    }
    val outerLocSet = envObj("@outer") match {
      case Some(propV) => propV.objval.value.locset
      case None => LocSetEmpty
    }
    val h2 =
      if (utils.absBool.False <= (envObj domIn x)(utils.absBool))
        outerLocSet.foldLeft(Heap.Bot)((tmpH, outerLoc) => varStoreL(h, outerLoc, x, value))
      else
        Heap.Bot
    h1 + h2
  }

  def varStoreG(h: Heap, x: String, value: Value): Heap = {
    val globalLoc = predefLoc.GLOBAL_LOC
    val obj = h.getOrElse(globalLoc, utils.ObjBot)
    val h1 =
      if (utils.absBool.False <= (obj domIn x)(utils.absBool))
        propStore(h, globalLoc, utils.absString.alpha(x), value)
      else Heap.Bot
    val h2 =
      if (utils.absBool.True <= (obj domIn x)(utils.absBool)) {
        val oldObjVal = obj.getOrElse(x, utils.PropValueBot).objval
        val newObjVal = oldObjVal.copyWith(value)
        h.update(globalLoc, obj.update(x, PropValue(newObjVal)))
      } else Heap.Bot
    h1 + h2
  }

  def propStore(h: Heap, loc: Loc, absStr: AbsString, value: Value): Heap = {
    val findingObj = h.getOrElse(loc, utils.ObjBot)
    val objDomIn = (findingObj domIn absStr)(utils.absBool)
    objDomIn.gamma match {
      case ConSingleTop() =>
        val oldObjV: ObjectValue = findingObj.getOrElse(absStr, utils.PropValueBot).objval
        val newObjV = ObjectValue(
          value,
          oldObjV.writable + utils.absBool.True,
          oldObjV.enumerable + utils.absBool.True,
          oldObjV.configurable + utils.absBool.True
        )
        h.update(loc, findingObj.update(absStr, PropValue(newObjV), utils))
      case ConSingleBot() => Heap.Bot
      case ConSingleCon(true) =>
        val oldObjV: ObjectValue = findingObj.getOrElse(absStr, utils.PropValueBot).objval
        val newObjV = oldObjV.copyWith(value)
        h.update(loc, findingObj.update(absStr, PropValue(newObjV), utils))
      case ConSingleCon(false) =>
        val oldObjV: ObjectValue = findingObj.getOrElse(absStr, utils.PropValueBot).objval
        val newObjV = ObjectValue(value, utils.absBool.True, utils.absBool.True, utils.absBool.True)
        h.update(loc, findingObj.update(absStr, PropValue(newObjV), utils))
    }
  }

  def propStoreWeak(h: Heap, loc: Loc, absStr: AbsString, value: Value): Heap = Heap.Bot

  def returnStore(h: Heap, value: Value): Heap = Heap.Bot

  def toStringSet(pvalue: PValue): Set[AbsString] = {
    var set = HashSet[AbsString]()

    pvalue.undefval.foldUnit(set += utils.absString.alpha("undefined"))
    pvalue.nullval.foldUnit(set += utils.absString.alpha("null"))

    pvalue.boolval.gamma match {
      case ConSingleBot() => ()
      case ConSingleCon(true) => set += utils.absString.alpha("true")
      case ConSingleCon(false) => set += utils.absString.alpha("false")
      case ConSingleTop() =>
        set += utils.absString.alpha("true")
        set += utils.absString.alpha("false")
    }

    set += pvalue.numval.toAbsString(utils.absString)

    pvalue.strval.foldUnit(set += pvalue.strval)

    // remove redundancies
    set.filter(s => !set.exists(o => s != o && s <= o))
  }

  def toObject(st: State, value: Value, newAddr: Address): (Value, State, Set[Exception]) = {
    val locSet = value.locset
    val pv = value.pvalue

    val excSet = (pv.undefval.gamma, pv.nullval.gamma) match {
      case (ConSimpleBot, ConSimpleBot) => ExceptionSetEmpty
      case _ => HashSet[Exception](TypeError)
    }
    val obj1 = pv.strval.fold(utils.ObjBot) { newString(_) }
    val obj2 = pv.boolval.fold(utils.ObjBot) { newBoolean(_) }
    val obj3 = pv.numval.fold(utils.ObjBot) { newNumber(_) }
    val obj = obj1 + obj2 + obj3

    val recLoc = addrManager.addrToLoc(newAddr, Recent)
    val (locSet1, h2, ctx2) =
      if (!obj.isBottom) {
        val st1 = oldify(st, newAddr)
        (HashSet(recLoc), st1.heap.update(recLoc, obj), st1.context)
      } else {
        (LocSetEmpty, Heap.Bot, Context.Bot)
      }
    val (locSet2, st3) =
      if (!locSet.isEmpty) (locSet, st)
      else (LocSetEmpty, State.Bot)

    (Value(utils.PValueBot, locSet1 ++ locSet2), State(h2, ctx2) + st3, excSet)
  }

  def toPrimitive(value: Value): PValue = utils.PValueBot

  def toPrimitiveBetter(h: Heap, value: Value): PValue = {
    value.pvalue + objToPrimitiveBetter(h, value.locset, "String")
  }

  def objToPrimitive(objs: Set[Loc], hint: String): PValue = {
    if (objs.isEmpty) utils.PValueBot
    else {
      hint match {
        case "Number" => utils.PValueBot.copyWith(utils.absNumber.Top)
        case "String" => utils.PValueBot.copyWith(utils.absString.Top)
        case _ => utils.PValueTop
      }
    }
  }

  def objToPrimitiveBetter(h: Heap, objSet: Set[Loc], hint: String): PValue = {
    if (objSet.isEmpty) utils.PValueBot
    else {
      hint match {
        case "Number" =>
          utils.PValueBot.copyWith(defaultValueNumber(h, objSet).toAbsNumber(utils.absNumber))
        case "String" =>
          utils.PValueBot.copyWith(defaultToString(h, objSet))
      }
    }
  }

  private def defaultValueNumber(h: Heap, objLocSet: Set[Loc]): PValue = {
    def getClassStrVal(obj: Obj): AbsString = {
      obj.getOrElse("@class", utils.PropValueBot).objval.value.pvalue.strval
    }

    val objSet = objLocSet.map(l => h.getOrElse(l, utils.ObjBot))
    val boolObjSet = objSet.filter(obj => {
      utils.absString.alpha("Boolean") <= getClassStrVal(obj)
    })
    val numObjSet = objSet.filter(obj => {
      utils.absString.alpha("Number") <= getClassStrVal(obj)
    })
    val dateObjSet = objSet.filter(obj => {
      utils.absString.alpha("Date") <= getClassStrVal(obj)
    })
    val strObjSet = objSet.filter(obj => {
      utils.absString.alpha("String") <= getClassStrVal(obj)
    })
    val regexpObjSet = objSet.filter(obj => {
      utils.absString.alpha("RegExp") <= getClassStrVal(obj)
    })
    val othersObjSet = objSet.filter(obj => {
      val absClassStr = getClassStrVal(obj)
      absClassStr != utils.absString.alpha("Boolean") &&
        absClassStr != utils.absString.alpha("Number") &&
        absClassStr != utils.absString.alpha("String") &&
        absClassStr != utils.absString.alpha("RegExp") &&
        absClassStr != utils.absString.alpha("Date")
    })

    val others = othersObjSet.foldLeft[AbsString](utils.absString.Bot)((absStr, obj) => {
      absStr + getClassStrVal(obj)
    })
    val b = boolObjSet.foldLeft[AbsBool](utils.absBool.Bot)((absBool, obj) => {
      absBool + obj.getOrElse("@primitive", utils.PropValueBot).objval.value.pvalue.boolval
    })
    val n = numObjSet.foldLeft[AbsNumber](utils.absNumber.Bot)((absNum, obj) => {
      absNum + obj.getOrElse("@primitive", utils.PropValueBot).objval.value.pvalue.numval
    })
    val n2 = dateObjSet.foldLeft[AbsNumber](utils.absNumber.Bot)((absNum, obj) => {
      absNum + obj.getOrElse("@primitive", utils.PropValueBot).objval.value.pvalue.numval
    })
    val (srcAbsStr, globalAbsB, ignoreCaseAbsB, multilineAbsB) =
      regexpObjSet.foldLeft[(AbsString, AbsBool, AbsBool, AbsBool)](
        (utils.absString.Bot, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
      )((res, obj) => {
          val (tmpSrc, tmpGlobal, tmpIgnoreCase, tmpMultiline) = res
          (tmpSrc + obj.getOrElse("source", utils.PropValueBot).objval.value.pvalue.strval,
            tmpGlobal + obj.getOrElse("global", utils.PropValueBot).objval.value.pvalue.boolval,
            tmpIgnoreCase + obj.getOrElse("ignoreCase", utils.PropValueBot).objval.value.pvalue.boolval,
            tmpMultiline + obj.getOrElse("multiline", utils.PropValueBot).objval.value.pvalue.boolval)
        })

    val absStr1 = strObjSet.foldLeft[AbsString](utils.absString.Bot)((absStr, obj) => {
      absStr + obj.getOrElse("@primitive", utils.PropValueBot).objval.value.pvalue.strval
    })
    val pv2 = utils.PValueBot.copyWith(b)
    val pv3 = utils.PValueBot.copyWith(n)
    val pv4 = utils.PValueBot.copyWith(n2)

    val absStr5 = (
      srcAbsStr.gammaSingle,
      globalAbsB.gamma,
      ignoreCaseAbsB.gamma,
      multilineAbsB.gamma
    ) match {
        case (ConSingleCon(s), ConSingleCon(g), ConSingleCon(i), ConSingleCon(m)) =>
          val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")
          utils.absString.alpha("/" + s + "/" + flags)
        case (ConSingleBot(), _, _, _)
        | (_, ConSingleBot(), _, _)
        | (_, _, ConSingleBot(), _)
        | (_, _, _, ConSingleBot()) => utils.absString.Bot
        case _ => utils.absString.Top
      }

    val pv6 = utils.PValueBot.copyWith(
      others.fold(utils.absString.Bot)(_ => {
        utils.absString.Top
      })
    )
    utils.PValueBot.copyWith(absStr1) + pv2 + pv3 + pv4 + utils.PValueBot.copyWith(absStr5) + pv6
  }

  private def defaultToString(h: Heap, objLocSet: Set[Loc]): AbsString = {
    def getClassStrVal(obj: Obj): AbsString = {
      obj.getOrElse("@class", utils.PropValueBot).objval.value.pvalue.strval
    }
    val objSet = objLocSet.map(l => h.getOrElse(l, utils.ObjBot))
    val boolObjSet = objSet.filter(obj => utils.absString.alpha("Boolean") <= getClassStrVal(obj))
    val numObjSet = objSet.filter(obj => utils.absString.alpha("Number") <= getClassStrVal(obj))
    val strObjSet = objSet.filter(obj => utils.absString.alpha("String") <= getClassStrVal(obj))
    val regexpObjSet = objSet.filter(obj => utils.absString.alpha("RegExp") <= getClassStrVal(obj))
    val othersObjSet = objSet.filter(obj => {
      val absClassStr = getClassStrVal(obj)
      absClassStr != utils.absString.alpha("Boolean") &&
        absClassStr != utils.absString.alpha("Number") &&
        absClassStr != utils.absString.alpha("String") &&
        absClassStr != utils.absString.alpha("RegExp")
    })

    val others = othersObjSet.foldLeft[AbsString](utils.absString.Bot)((absStr, obj) => {
      absStr + getClassStrVal(obj)
    })
    val b = boolObjSet.foldLeft[AbsBool](utils.absBool.Bot)((absBool, obj) => {
      absBool + obj.getOrElse("@primitive", utils.PropValueBot).objval.value.pvalue.boolval
    })
    val n = numObjSet.foldLeft[AbsNumber](utils.absNumber.Bot)((absNum, obj) => {
      absNum + obj.getOrElse("@primitive", utils.PropValueBot).objval.value.pvalue.numval
    })
    val (srcAbsStr, globalAbsB, ignoreCaseAbsB, multilineAbsB) =
      regexpObjSet.foldLeft[(AbsString, AbsBool, AbsBool, AbsBool)](
        (utils.absString.Bot, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
      )((res, obj) => {
          val (tmpSrc, tmpGlobal, tmpIgnoreCase, tmpMultiline) = res
          (tmpSrc + obj.getOrElse("source", utils.PropValueBot).objval.value.pvalue.strval,
            tmpGlobal + obj.getOrElse("global", utils.PropValueBot).objval.value.pvalue.boolval,
            tmpIgnoreCase + obj.getOrElse("ignoreCase", utils.PropValueBot).objval.value.pvalue.boolval,
            tmpMultiline + obj.getOrElse("multiline", utils.PropValueBot).objval.value.pvalue.boolval)
        })

    val absStr1 = strObjSet.foldLeft[AbsString](utils.absString.Bot)((absStr, obj) => {
      absStr + obj.getOrElse("@primitive", utils.PropValueBot).objval.value.pvalue.strval
    })
    val absStr2 = b.toAbsString(utils.absString)
    val absStr3 = n.toAbsString(utils.absString)
    val absStr4 = (
      srcAbsStr.gammaSingle,
      globalAbsB.gamma,
      ignoreCaseAbsB.gamma,
      multilineAbsB.gamma
    ) match {
        case (ConSingleCon(s), ConSingleCon(g), ConSingleCon(i), ConSingleCon(m)) =>
          val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")
          utils.absString.alpha("/" + s + "/" + flags)
        case (ConSingleBot(), _, _, _)
        | (_, ConSingleBot(), _, _)
        | (_, _, ConSingleBot(), _)
        | (_, _, _, ConSingleBot()) => utils.absString.Bot
        case _ => utils.absString.Top
      }

    val absStr5 = others.fold(utils.absString.Bot)(_ => {
      utils.absString.Top
    })
    absStr1 + absStr2 + absStr3 + absStr4 + absStr5
  }

  def typeTag(h: Heap, value: Value): AbsString = {
    val s1 = value.pvalue.undefval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("undefined")
    })
    val s2 = value.pvalue.nullval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("object") //TODO: check null type?
    })
    val s3 = value.pvalue.numval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("number")
    })
    val s4 = value.pvalue.boolval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("boolean")
    })
    val s5 = value.pvalue.strval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("string")
    })

    val isCallableLocSet = value.locset.foldLeft(utils.absBool.Bot)((tmpAbsB, l) => tmpAbsB + isCallable(h, l))
    val s6 =
      if (!value.locset.isEmpty && (utils.absBool.False <= isCallableLocSet))
        utils.absString.alpha("object")
      else utils.absString.Bot
    val s7 =
      if (!value.locset.isEmpty && (utils.absBool.True <= isCallableLocSet))
        utils.absString.alpha("function")
      else utils.absString.Bot

    s1 + s2 + s3 + s4 + s5 + s6 + s7
  }

  def validity(expr: CFGExpr, st: State): Boolean = false

  def validity(expr1: CFGExpr, expr2: CFGExpr, st: State): Boolean = false

  def validity(expr1: CFGExpr, expr2: CFGExpr, expr3: CFGExpr, st: State): Boolean = false

  // predef location: string -> Loc map
  def strPredefLocMap: String = (predefLoc.strToLocMap.toSeq.sortBy {
    case (key, _) => -key
  }.map {
    case (loc, name) => s"$loc -> $name"
  }).mkString(Config.LINE_SEP)
}
