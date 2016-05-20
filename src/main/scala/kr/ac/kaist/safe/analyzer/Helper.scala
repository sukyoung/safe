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
import kr.ac.kaist.safe.cfg_builder.FunctionId
import kr.ac.kaist.safe.nodes.{ CFGExpr, CFGId }

import scala.collection.immutable.HashSet

case class Helper(utils: Utils) {
  def canPut(h: Heap, loc: Loc, str: String): Boolean = false

  def canPutHelp(h: Heap, loc1: Loc, str: String, loc2: Loc): Boolean = false

  def canPutVar(h: Heap, x: String): Boolean = false

  def createMutableBinding(h: Heap, id: CFGId, value: Value): Heap = Heap.Bot

  def delete(h: Heap, loc: Loc, absStr: AbsString): (Heap, AbsBool) = (Heap.Bot, utils.absBool.Bot)

  def raiseException(st: State, excSet: Set[Exception]): State = State.Bot

  def newExceptionLoc(exc: Exception): Loc = 0

  def getThis(h: Heap, value: Value): Set[Loc] = HashSet[Loc]()

  def hasConstruct(h: Heap, loc: Loc): AbsBool = utils.absBool.Bot

  def hasInstance(h: Heap, loc: Loc): AbsBool = utils.absBool.Bot

  def hasProperty(h: Heap, loc: Loc, absStr: AbsString): AbsBool = utils.absBool.Bot

  def HasOwnProperty(h: Heap, loc: Loc, absStr: AbsString): AbsBool = utils.absBool.Bot

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

  def newDeclEnvRecord(outerEnv: Value): Obj = utils.ObjBot

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

  def fixOldify(ctx: Context, obj: Obj, mayOld: Set[Address], mustOld: Set[Address]): (Context, Obj) =
    (Context.Bot, utils.ObjBot)

  def proto(h: Heap, loc: Loc, absStr: AbsString): Value = utils.ValueBot

  def protoBase(h: Heap, loc: Loc, absStr: AbsString): Set[Loc] = HashSet[Loc]()

  def varStore(h: Heap, id: CFGId, value: Value): Heap = Heap.Bot

  def varStoreL(h: Heap, loc: Loc, x: String, value: Value): Heap = Heap.Bot

  def varStoreG(h: Heap, x: String, value: Value): Heap = Heap.Bot

  def propStore(h: Heap, loc: Loc, absStr: AbsString, value: Value): Heap = Heap.Bot

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