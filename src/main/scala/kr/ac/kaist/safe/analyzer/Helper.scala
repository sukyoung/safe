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
import kr.ac.kaist.safe.cfg_builder.{ AddressManager, FunctionId }
import kr.ac.kaist.safe.nodes._

import scala.collection.immutable.HashSet

case class Helper(utils: Utils, addressManager: AddressManager) {
  def canPut(h: Heap, loc: Loc, str: String): AbsBool = canPutHelp(h, loc, str, loc)

  def canPutHelp(h: Heap, loc1: Loc, str: String, loc2: Loc): AbsBool = {
    val domInStr = h.getOrElse(loc1, utils.ObjBot).domIn(str, utils.absBool)
    val b1 =
      if (utils.absBool.True <= domInStr) {
        val obj = h.getOrElse(loc1, utils.ObjBot)
        obj.getOrElse(str, utils.PropValueBot).objval.writable
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
          absB + canPutHelp(h, loc, str, loc2)
        })
      } else utils.absBool.Bot
    b1 + b2
  }

  def canPutVar(h: Heap, x: String): AbsBool = {
    val globalLoc = addressManager.GLOBAL_LOC
    val globalObj = h.getOrElse(globalLoc, utils.ObjBot)
    val domIn = globalObj.domIn(x, utils.absBool)
    val b1 =
      if (utils.absBool.True <= domIn) globalObj.getOrElse(x, utils.PropValueBot).objval.writable
      else utils.absBool.Bot
    val b2 =
      if (utils.absBool.False <= domIn) canPut(h, globalLoc, x)
      else utils.absBool.Bot
    b1 + b2
  }

  def createMutableBinding(h: Heap, id: CFGId, value: Value): Heap = {
    val x = id.text
    id.kind match {
      case PureLocalVar =>
        val localLoc = addressManager.SINGLE_PURE_LOCAL_LOC
        val objV = ObjectValue(value, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.False)
        val propV = PropValue(objV)
        h.update(localLoc, h.getOrElse(localLoc, utils.ObjBot).update(x, propV))
      case CapturedVar =>
        val localLoc = addressManager.SINGLE_PURE_LOCAL_LOC
        val objV = ObjectValue(value, utils.absBool.True, utils.absBool.Bot, utils.absBool.False)
        val propV = PropValue(objV)
        val localObj = h.getOrElse(localLoc, utils.ObjBot)
        localObj.getOrElse("@env", utils.PropValueBot).objval.value.locset.foldLeft(Heap.Bot)((tmpHeap, loc) => {
          tmpHeap + h.update(loc, h.getOrElse(loc, utils.ObjBot).update(x, propV))
        })
      case CapturedCatchVar =>
        val collapsedLoc = addressManager.COLLAPSED_LOC
        val objV = ObjectValue(value, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.False)
        val propV = PropValue(objV)
        h.update(collapsedLoc, h.getOrElse(collapsedLoc, utils.ObjBot).update(x, propV))
      case GlobalVar =>
        val globalLoc = addressManager.GLOBAL_LOC
        val objV = ObjectValue(value, utils.absBool.True, utils.absBool.True, utils.absBool.False)
        val propV = PropValue(objV)
        if (utils.absBool.True == hasProperty(h, globalLoc, utils.absString.alpha(x))) h
        else h.update(globalLoc, h.getOrElse(globalLoc, utils.ObjBot).update(x, propV))
    }
  }

  def delete(h: Heap, loc: Loc, absStr: AbsString): (Heap, AbsBool) = (Heap.Bot, utils.absBool.Bot)

  def raiseException(st: State, excSet: Set[Exception]): State = State.Bot

  def newExceptionLoc(exc: Exception): Loc = 0

  def getThis(h: Heap, value: Value): Set[Loc] = HashSet[Loc]()

  def hasConstruct(h: Heap, loc: Loc): AbsBool = utils.absBool.Bot

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

  def isArray(h: Heap, loc: Loc): AbsBool = utils.absBool.Bot

  def isArrayIndex(absStr: AbsString): AbsBool = utils.absBool.Bot

  def isCallable(h: Heap, loc: Loc): AbsBool = utils.absBool.Bot

  def isCallable(h: Heap, value: Value): AbsBool = utils.absBool.Bot

  def isObject(h: Heap, loc: Loc): AbsBool = utils.absBool.Bot

  def lookup(h: Heap, id: CFGId): (Value, Set[Exception]) = (utils.ValueBot, HashSet[Exception]())

  def lookupG(h: Heap, x: String): (Value, Set[Exception]) = (utils.ValueBot, HashSet[Exception]())

  def lookupL(h: Heap, loc: Loc, x: String): Value = utils.ValueBot

  def lookupBase(h: Heap, id: CFGId): Set[Loc] = HashSet[Loc]()

  def lookupBaseG(h: Heap, x: String): Set[Loc] = HashSet[Loc]()

  def lookupBaseL(h: Heap, loc: Loc, x: String): Set[Loc] = HashSet[Loc]()

  def newBoolean(absB: AbsBool): Obj = utils.ObjBot

  def newNumber(absNum: AbsNumber): Obj = utils.ObjBot

  def newString(absStr: AbsString): Obj = utils.ObjBot

  def newDeclEnvRecord(outerEnv: Value): Obj = {
    val outerEnvObjV = ObjectValue(outerEnv, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
    utils.ObjEmpty.update("@outer", PropValue(outerEnvObjV))
  }

  def newObject(): Obj = utils.ObjBot

  def newObject(loc: Loc): Obj = utils.ObjBot

  def newObject(locSet: Set[Loc]): Obj = utils.ObjBot

  def newArgObject(absLength: AbsNumber): Obj = utils.ObjBot

  def newArrayObject(absLength: AbsNumber): Obj = utils.ObjBot

  def newFunctionObject(fid: FunctionId, env: Value, l: Loc, n: AbsNumber): Obj = utils.ObjBot

  private def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: Value,
    locOpt: Option[Loc], n: AbsNumber): Obj = utils.ObjBot

  private def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: Value,
    locOpt: Option[Loc], writable: AbsBool, enumerable: AbsBool, configurable: AbsBool,
    absLength: AbsNumber): Obj = utils.ObjBot

  def newPureLocal(value: Value, locSet: Set[Loc]): Obj = utils.ObjBot

  def oldify(st: State, addr: Address): State = State.Bot

  def fixOldify(ctx: Context, obj: Obj, mayOld: Set[Address], mustOld: Set[Address]): (Context, Obj) = {
    if (ctx.isBottom) (Context.Bot, utils.ObjBot)
    else {
      mayOld.foldLeft((ctx, obj))((res, a) => {
        val (resCtx, resObj) = res
        val locR = addressManager.addrToLoc(a, Recent)
        val locO = addressManager.addrToLoc(a, Old)
        if (mustOld contains a) {
          val newCtx = resCtx.subsLoc(locR, locO, addressManager)
          val newObj = resObj.subsLoc(locR, locO)
          (newCtx, newObj)
        } else {
          val newCtx = resCtx.weakSubsLoc(locR, locO, addressManager)
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
                Value(utils.PValueBot.copyWith(utils.absUndef.Top), LocSetEmpty)
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

  def protoBase(h: Heap, loc: Loc, absStr: AbsString): Set[Loc] = HashSet[Loc]()

  def varStore(h: Heap, id: CFGId, value: Value): Heap = {
    val pureLocalLoc = addressManager.SINGLE_PURE_LOCAL_LOC
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
        val obj = h.getOrElse(addressManager.COLLAPSED_LOC, utils.ObjBot)
        h.update(addressManager.COLLAPSED_LOC, obj.update(x, propV))
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
    val globalLoc = addressManager.GLOBAL_LOC
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

  def toBoolean(value: Value): AbsBool = utils.absBool.Bot

  def toNumber(pvalue: PValue): AbsNumber = utils.absNumber.Bot

  def toString(pvalue: PValue): AbsString = utils.absString.Bot

  def toStringSet(pvalue: PValue): Set[AbsString] = HashSet[AbsString]()

  def toObject(st: State, value: Value, newAddr: Address): (Value, State, Set[Exception]) =
    (utils.ValueBot, State.Bot, HashSet[Exception]())

  def toPrimitive(value: Value): PValue = utils.PValueBot

  def typTag(h: Heap, value: Value): AbsString = utils.absString.Bot

  def validity(expr: CFGExpr, st: State): Boolean = false

  def validity(expr1: CFGExpr, expr2: CFGExpr, st: State): Boolean = false

  def validity(expr1: CFGExpr, expr2: CFGExpr, expr3: CFGExpr, st: State): Boolean = false
}