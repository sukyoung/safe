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
import kr.ac.kaist.safe.cfg_builder.{ AddressManager }
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
    val domInStr = h.getOrElse(loc1, utils.ObjBot).domIn(absStr, utils.absBool)
    val b1 =
      if (utils.absBool.True <= domInStr) {
        val obj = h.getOrElse(loc1, utils.ObjBot)
        obj.getOrElse(absStr, utils.PropValueBot).objval.writable
      } else utils.absBool.Bot
    val b2 =
      if (utils.absBool.False <= domInStr) {
        val protoObj = h.getOrElse(loc1, utils.ObjBot).getOrElse("@proto", utils.PropValueBot)
        val protoLocSet = protoObj.objval.value.locset
        val b3 =
          if (!protoObj.objval.value.pvalue.nullval.isBottom)
            h.getOrElse(loc2, utils.ObjBot).getOrElse("@extensible", utils.PropValueBot).objval.value.pvalue.boolval
          else utils.absBool.Bot
        protoLocSet.foldLeft(b3)((absB, loc) => {
          absB + canPutHelp(h, loc, absStr, loc2)
        })
      } else utils.absBool.Bot
    b1 + b2
  }

  def canPutVar(h: Heap, x: String): AbsBool = {
    val globalLoc = predefLoc.GLOBAL_LOC
    val globalObj = h.getOrElse(globalLoc, utils.ObjBot)
    val domIn = globalObj.domIn(x, utils.absBool)
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
    val locSet1 =
      if (value.pvalue.nullval.isTop || value.pvalue.undefval.isTop) HashSet(predefLoc.GLOBAL_LOC)
      else LocSetEmpty

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
    val isDomIn = h.getOrElse(loc, utils.ObjBot).domIn("@construct", utils.absBool)
    val b1 =
      if (utils.absBool.True <= isDomIn) utils.absBool.True
      else utils.absBool.Bot
    val b2 =
      if (utils.absBool.False <= isDomIn) utils.absBool.False
      else utils.absBool.Bot
    b1 + b2
  }

  def hasInstance(h: Heap, loc: Loc): AbsBool = utils.absBool.Bot

  def hasProperty(h: Heap, loc: Loc, absStr: AbsString): AbsBool = {
    var visited = LocSetEmpty
    def visit(currentLoc: Loc): AbsBool = {
      if (visited(currentLoc)) utils.absBool.Bot
      else {
        visited += currentLoc
        val test = hasOwnProperty(h, currentLoc, absStr)
        val b1 =
          if (utils.absBool.True <= test) utils.absBool.True
          else utils.absBool.Bot
        val b2 =
          if (utils.absBool.False <= test) {
            val protoV = h.getOrElse(currentLoc, utils.ObjBot).getOrElse("@proto", utils.PropValueBot).objval.value
            val b3 =
              if (!protoV.pvalue.nullval.isBottom) utils.absBool.False
              else utils.absBool.Bot
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
    h.getOrElse(loc, utils.ObjBot).domIn(absStr, utils.absBool)
  }

  def inherit(h: Heap, loc1: Loc, loc2: Loc): Value = utils.ValueBot

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
    absStr.gammaOpt match {
      case None if absStr.isBottom => utils.absBool.Bot
      case None if absStr.isTop => utils.absBool.Top
      case Some(strSet) if absStr.isAllNums =>
        strSet.foldLeft[AbsBool](utils.absBool.Bot)((res, v) => {
          val num = v.toDouble
          res + utils.absBool.alpha(0 <= num && num < scala.math.pow(2, 32) - 1)
        })
      case Some(_) if absStr.isAllOthers => utils.absBool.False
      case Some(_) => utils.absBool.Bot
      case None => utils.absBool.Top
    }
  }

  def isCallable(h: Heap, loc: Loc): AbsBool = {
    val isDomIn = h.getOrElse(loc, utils.ObjBot).domIn("@function", utils.absBool)
    val b1 =
      if (utils.absBool.True <= isDomIn) utils.absBool.True
      else utils.absBool.Bot
    val b2 =
      if (utils.absBool.False <= isDomIn) utils.absBool.False
      else utils.absBool.Bot
    b1 + b2
  }

  def isCallable(h: Heap, value: Value): AbsBool = utils.absBool.Bot

  def isObject(h: Heap, loc: Loc): AbsBool = utils.absBool.Bot

  def lookup(h: Heap, id: CFGId): (Value, Set[Exception]) = (utils.ValueBot, HashSet[Exception]())

  def lookupG(h: Heap, x: String): (Value, Set[Exception]) = (utils.ValueBot, HashSet[Exception]())

  def lookupL(h: Heap, loc: Loc, x: String): Value = utils.ValueBot

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
    val isDomIn = globalObj.domIn(x, utils.absBool)
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
        val isDomIn = env.domIn(x, utils.absBool)
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
    absStr.gammaOpt match {
      case Some(strSet) =>
        strSet.foldLeft(utils.ObjBot)((obj, str) => {
          val length = str.length
          val newObj3 = (0 until length).foldLeft(newObj2)((_o, _i) => {
            val charAbsStr = utils.absString.alpha(str.charAt(_i).toString)
            val charVal = Value(utils.PValueBot.copyWith(charAbsStr))
            _o.update(_i.toString, PropValue(ObjectValue(charVal, absFalse, absTrue, absFalse)))
          })
          val lengthVal = Value(utils.PValueBot.copyWith(utils.absNumber.alpha(length)))
          obj + newObj3.update("length", PropValue(ObjectValue(lengthVal, absFalse, absFalse, absFalse)))
        })
      case None =>
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
        if (st.heap.domIn(locR))
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
        val test = h.getOrElse(currentLoc, utils.ObjBot).domIn(absStr, utils.absBool)
        val v1 =
          if (utils.absBool.True <= test) {
            h.getOrElse(currentLoc, utils.ObjBot).getOrElse(absStr, utils.PropValueBot).objval.value
          } else {
            utils.ValueBot
          }
        val v2 =
          if (utils.absBool.False <= test) {
            val protoV = h.getOrElse(currentLoc, utils.ObjBot).getOrElse("@proto", utils.PropValueBot).objval.value
            val v3 =
              if (!protoV.pvalue.nullval.isBottom) {
                Value(utils.PValueBot.copyWith(utils.absUndef.Top))
              } else {
                utils.ValueBot
              }
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
      if (visited(l)) LocSetEmpty
      else {
        visited += l
        val obj = h.getOrElse(l, utils.ObjBot)
        val isDomIn = obj.domIn(absStr, utils.absBool)
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
      if (utils.absBool.False <= envObj.domIn(x, utils.absBool))
        outerLocSet.foldLeft(Heap.Bot)((tmpH, outerLoc) => varStoreL(h, outerLoc, x, value))
      else
        Heap.Bot
    h1 + h2
  }

  def varStoreG(h: Heap, x: String, value: Value): Heap = {
    val globalLoc = predefLoc.GLOBAL_LOC
    val obj = h.getOrElse(globalLoc, utils.ObjBot)
    val h1 =
      if (utils.absBool.False <= obj.domIn(x, utils.absBool))
        propStore(h, globalLoc, utils.absString.alpha(x), value)
      else Heap.Bot
    val h2 =
      if (utils.absBool.True <= obj.domIn(x, utils.absBool)) {
        val oldObjVal = obj.getOrElse(x, utils.PropValueBot).objval
        val newObjVal = oldObjVal.copyWith(value)
        h.update(globalLoc, obj.update(x, PropValue(newObjVal)))
      } else Heap.Bot
    h1 + h2
  }

  def propStore(h: Heap, loc: Loc, absStr: AbsString, value: Value): Heap = {
    val findingObj = h.getOrElse(loc, utils.ObjBot)
    val objDomIn = findingObj.domIn(absStr, utils.absBool)
    objDomIn.getSingle match {
      case Some(true) =>
        val oldObjV: ObjectValue = findingObj.getOrElse(absStr, utils.PropValueBot).objval
        val newObjV = oldObjV.copyWith(value)
        h.update(loc, findingObj.update(absStr, PropValue(newObjV), utils))
      case Some(false) =>
        val oldObjV: ObjectValue = findingObj.getOrElse(absStr, utils.PropValueBot).objval
        val newObjV = ObjectValue(value, utils.absBool.True, utils.absBool.True, utils.absBool.True)
        h.update(loc, findingObj.update(absStr, PropValue(newObjV), utils))
      case None if objDomIn.isTop =>
        val oldObjV: ObjectValue = findingObj.getOrElse(absStr, utils.PropValueBot).objval
        val newObjV = ObjectValue(
          value,
          oldObjV.writable + utils.absBool.True,
          oldObjV.enumerable + utils.absBool.True,
          oldObjV.configurable + utils.absBool.True
        )
        h.update(loc, findingObj.update(absStr, PropValue(newObjV), utils))
      case None => Heap.Bot
    }
  }

  def propStoreWeak(h: Heap, loc: Loc, absStr: AbsString, value: Value): Heap = Heap.Bot

  def returnStore(h: Heap, value: Value): Heap = Heap.Bot

  def toBoolean(value: Value): AbsBool = {
    val bot = utils.absBool.Bot
    val top = utils.absBool.Top
    val absTrue = utils.absBool.True
    val absFalse = utils.absBool.False

    val b1 = if (value.pvalue.undefval.isTop) absFalse else bot
    val b2 = if (value.pvalue.nullval.isTop) absFalse else bot
    val b3 = value.pvalue.boolval
    val b4 = value.pvalue.numval.getAbsCase match {
      case AbsTop => top
      case AbsBot => bot
      case AbsSingle => value.pvalue.numval.toBoolean(utils.absBool)
      case AbsMulti if value.pvalue.numval.isInfinity => absTrue
      case AbsMulti => top
    }
    val strval = value.pvalue.strval
    val b5 =
      if (strval.isAllNums) absTrue
      else strval.gammaOpt match {
        case _ if strval.isTop => top
        case _ if strval.isBottom => bot
        case Some(vs) => vs.foldLeft[AbsBool](bot)((r, v) => {
          r + utils.absBool.alpha(v != "")
        })
        case None => top
      }
    val b6 = if (value.locset.isEmpty) bot else absTrue

    b1 + b2 + b3 + b4 + b5 + b6
  }

  def toNumber(pvalue: PValue): AbsNumber = {
    val absNum = utils.absNumber

    val pv1 = if (pvalue.undefval.isTop) absNum.NaN else absNum.Bot

    val pv2 = if (pvalue.nullval.isTop) absNum.alpha(+0) else absNum.Bot

    val pv3 = pvalue.boolval.getPair match {
      case (AbsBot, _) => absNum.Bot
      case (AbsSingle, Some(true)) => absNum.alpha(1)
      case (AbsSingle, Some(false)) => absNum.alpha(+0)
      case _ => absNum.UInt
    }

    val pv4 = pvalue.numval

    val pv5 = pvalue.strval.gammaOpt match {
      case Some(strSet) =>
        strSet.foldLeft(absNum.Bot)((absN, str) => {
          val strN = str.trim match {
            case "" => absNum.alpha(0)
            case s if isHex(s) => absNum.alpha((s + "p0").toDouble)
            case s => Try(absNum.alpha(s.toDouble)).getOrElse(absNum.NaN)
          }
          absN + strN
        })
      case None if pvalue.strval.isBottom => absNum.Bot
      case None => absNum.Top
    }

    pv1 + pv2 + pv3 + pv4 + pv5
  }

  def toString(pvalue: PValue): AbsString = utils.absString.Bot

  def toStringSet(pvalue: PValue): Set[AbsString] = {
    var set = HashSet[AbsString]()

    if (pvalue.undefval.isTop) set += utils.absString.alpha("undefined")

    if (pvalue.nullval.isTop) set += utils.absString.alpha("null")

    pvalue.boolval.getPair match {
      case (AbsBot, _) => ()
      case (AbsSingle, Some(true)) => set += utils.absString.alpha("true")
      case (AbsSingle, Some(false)) => set += utils.absString.alpha("false")
      case _ =>
        set += utils.absString.alpha("true")
        set += utils.absString.alpha("false")
    }

    pvalue.numval.getAbsCase match {
      case AbsBot => ()
      case AbsMulti if pvalue.numval.isInfinity =>
        set += utils.absString.alpha("Infinity")
        set += utils.absString.alpha("-Infinity")
      case _ => pvalue.numval.toAbsString(utils.absString)
    }

    pvalue.strval.getAbsCase match {
      case AbsBot => ()
      case _ => set += pvalue.strval
    }

    // remove redundancies
    set.filter(s => !set.exists(o => s != o && s <= o))
  }

  def toObject(st: State, value: Value, newAddr: Address): (Value, State, Set[Exception]) = {
    val locSet = value.locset
    val pv = value.pvalue

    val excSet =
      if (!pv.undefval.isBottom || !pv.nullval.isBottom) HashSet[Exception](TypeError)
      else ExceptionSetEmpty

    val obj1 =
      if (!pv.strval.isBottom) newString(pv.strval)
      else utils.ObjBot
    val obj2 =
      if (!pv.boolval.isBottom) newBoolean(pv.boolval)
      else utils.ObjBot
    val obj3 =
      if (!pv.numval.isBottom) newNumber(pv.numval)
      else utils.ObjBot
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

  private def objToPrimitiveBetter(h: Heap, objSet: Set[Loc], hint: String): PValue = {
    if (objSet.isEmpty) utils.PValueBot
    else {
      hint match {
        case "Number" =>
          utils.PValueBot.copyWith(toNumber(defaultValueNumber(h, objSet)))
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

    val isNotBottom = !srcAbsStr.isBottom && !globalAbsB.isBottom && !ignoreCaseAbsB.isBottom && !multilineAbsB.isBottom
    val absStr5 =
      (srcAbsStr.getSingle, globalAbsB.getSingle, ignoreCaseAbsB.getSingle, multilineAbsB.getSingle) match {
        case (Some(s), Some(g), Some(i), Some(m)) => {
          val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")
          utils.absString.alpha("/" + s + "/" + flags)
        }
        case _ if isNotBottom => utils.absString.Top
        case _ => utils.absString.Bot
      }

    val pv6 = others.getSingle match {
      case None if others.isBottom => utils.PValueBot.copyWith(utils.absString.Bot)
      case Some(_) | None => utils.PValueBot.copyWith(utils.absString.Top)
    }
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

    val isNotBottom = !srcAbsStr.isBottom && !globalAbsB.isBottom && !ignoreCaseAbsB.isBottom && !multilineAbsB.isBottom
    val absStr4 =
      (srcAbsStr.getSingle, globalAbsB.getSingle, ignoreCaseAbsB.getSingle, multilineAbsB.getSingle) match {
        case (Some(s), Some(g), Some(i), Some(m)) => {
          val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")
          utils.absString.alpha("/" + s + "/" + flags)
        }
        case _ if isNotBottom => utils.absString.Top
        case _ => utils.absString.Bot
      }

    val absStr5 = others.getSingle match {
      case Some(s) => utils.absString.Top
      case None if others.isBottom => utils.absString.Bot
      case None => utils.absString.Top
    }
    absStr1 + absStr2 + absStr3 + absStr4 + absStr5
  }

  def typTag(h: Heap, value: Value): AbsString = utils.absString.Bot

  def validity(expr: CFGExpr, st: State): Boolean = false

  def validity(expr1: CFGExpr, expr2: CFGExpr, st: State): Boolean = false

  def validity(expr1: CFGExpr, expr2: CFGExpr, expr3: CFGExpr, st: State): Boolean = false
}
