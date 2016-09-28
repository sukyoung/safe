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

package kr.ac.kaist.safe.analyzer.models.builtin

import kr.ac.kaist.safe.analyzer.{ Helper, TypeConversionHelper }
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util.{ Address, SystemAddr }

import scala.collection.immutable.HashSet

// TODO Function
object BuiltinFunction extends FuncModel(
  name = "Function",
  // TODO: 15.3.1 Function(...)
  // ICall -> ...
  // argLen: 15.3.3.2 Function.length
  code = EmptyCode(argLen = 1),
  // TODO: 15.3.2.1 new Function(...)
  // IConstruct -> ...
  construct = Some(EmptyCode()),
  // 15.3.3.1 Function.prototype
  protoModel = Some((BuiltinFunctionProto, F, F, F))
)

object BuiltinFunctionProto extends FuncModel(
  name = "Function.prototype",
  // 15.3.4
  code = EmptyCode(argLen = 0),
  props = List(
    // 15.3.4 "The value of the [[Prototype]] internal property of the Function prototype object is
    // the standard built-in Object prototype object."
    InternalProp(IPrototype, BuiltinObjectProto),

    // 15.3.4.2 Function.prototype.toString()
    NormalProp("toString", FuncModel(
      name = "Function.prototype.toString",
      code = BasicCode(argLen = 0, (args: AbsValue, st: State) => {
        val thisBinding = st.context.thisBinding.locset
        val functionClass = AbsString("Function")
        val notAllFunctionClass = thisBinding.exists(loc => {
          val thisClass = st.heap.get(loc)(IClass).value.pvalue.strval
          AbsBool.True </ (thisClass === functionClass)
        })
        val excSet =
          if (notAllFunctionClass) ExcSetEmpty + TypeError
          else ExcSetEmpty

        // return value is implementation dependent
        val result = AbsString.Top
        (st, st.raiseException(excSet), result)
      })
    ), T, F, T),

    // 15.3.4.3 Fucntion.prototype.apply(thisArg, argArray)
    NormalProp("apply", FuncModel(
      name = "Function.prototype.apply",
      code = {
      val funcId = CFGTempId("<>Model<>Function.prototype.apply<>func<>", PureLocalVar)
      val thisId = CFGTempId("<>Model<>Function.prototype.apply<>this<>", PureLocalVar)
      val argsId = CFGTempId("<>Model<>Function.prototype.apply<>arguments<>", PureLocalVar)
      val retId = CFGTempId("<>Model<>Function.prototype.apply<>return<>", PureLocalVar)
      CallCode(
        argLen = 2,
        funcId, thisId, argsId, retId,
        BuiltinFunctionProtoHelper.applyBeforeCall(funcId, thisId, argsId),
        BuiltinFunctionProtoHelper.connectAfterCall(retId)
      )
    }
    ), T, F, T),

    // 15.3.4.4 Function.prototype.call(thisArg [, arg1 [, arg2, ...]])
    NormalProp("call", FuncModel(
      name = "Function.prototype.call",
      code = {
      val funcId = CFGTempId("<>Model<>Function.prototype.call<>func<>", PureLocalVar)
      val thisId = CFGTempId("<>Model<>Function.prototype.call<>this<>", PureLocalVar)
      val argsId = CFGTempId("<>Model<>Function.prototype.call<>arguments<>", PureLocalVar)
      val retId = CFGTempId("<>Model<>Function.prototype.call<>return<>", PureLocalVar)
      CallCode(
        argLen = 1,
        funcId, thisId, argsId, retId,
        BuiltinFunctionProtoHelper.callBeforeCall(funcId, thisId, argsId),
        BuiltinFunctionProtoHelper.connectAfterCall(retId)
      )
    }
    ), T, F, T),

    // TODO 15.3.4.5 Function.prototype.bind(thisArg [, arg1 [, arg2, ...]])
    NormalProp("bind", FuncModel(
      name = "Function.prototype.bind",
      code = EmptyCode(argLen = 1)
    ), T, F, T)
  )
)

private object BuiltinFunctionProtoHelper {
  val atrue = AbsBool.True

  // 15.3.4.3 Fucntion.prototype.apply(thisArg, argArray)
  def applyBeforeCall(funcId: CFGId, thisId: CFGId, argsId: CFGId)(args: AbsValue, st: State, addr: Address): (State, State) = {
    val func = st.context.thisBinding.locset
    val (thisArg, st1, excSet1) = {
      val origThis = Helper.propLoad(args, HashSet(AbsString("0")), st.heap)

      // "NOTE: Edition 3, where a undefined or null thisArg is replaced with the global object
      // and ToObject is applied to all other values and that result is passed as the this value."
      // However, most of the browsers returns the object as Edition 3.
      // Below modeling for thisArg follows the Edition 3.
      val nullOrUndef = (origThis.pvalue.nullval </ AbsNull.Bot) || (origThis.pvalue.undefval </ AbsUndef.Bot)
      val thisLoc1 =
        if (nullOrUndef) AbsValue(BuiltinGlobal.loc)
        else AbsValue.Bot

      val notNullNorUndef = (origThis </ AbsNull.Top) && (origThis </ AbsUndef.Top)
      val (thisLoc2, st1, excSet1) =
        if (notNullNorUndef) {
          val addr = SystemAddr("Function.prototype.apply<this>")
          TypeConversionHelper.ToObject(origThis, st, addr)
        } else (AbsValue.Bot, st, ExcSetEmpty)

      (thisLoc1 + thisLoc2, st1, excSet1)
    }
    val heap = st1.heap
    val argArray = Helper.propLoad(args, HashSet(AbsString("1")), heap)

    // 1. If IsCallable(func) is false, then throw a TypeError exception.
    val notAllCallable = func.exists(loc => {
      AbsBool.False <= TypeConversionHelper.IsCallable(loc, heap)
    })
    val excSet2 =
      if (notAllCallable) ExcSetEmpty + TypeError
      else ExcSetEmpty

    val callableFunc = func.locset.filter(loc => {
      AbsBool.True <= TypeConversionHelper.IsCallable(loc, heap)
    })

    // 2. If argArray is null or undefined, then
    val nullOrUndef = (argArray.pvalue.nullval </ AbsNull.Bot) || (argArray.pvalue.undefval </ AbsUndef.Bot)
    val argList1 =
      if (nullOrUndef) AbsObjectUtil.newArgObject(AbsNumber(0.0))
      else AbsObjectUtil.Bot

    // 3. If Type(argArray) is not Object, then throw a TypeError exception.
    val excSet3 =
      if (argArray.pvalue </ AbsPValue.Bot) ExcSetEmpty + TypeError
      else ExcSetEmpty

    // 4. - 8.
    val argList2 = argArray.locset.foldLeft(AbsObjectUtil.Bot)((aobj, loc) => {
      val argObj = heap.get(loc)
      val len = argObj.Get("length", heap)
      val n = TypeConversionHelper.ToUInt32(len)
      n.gamma match {
        case ConInf() =>
          val indexName = AbsString.Number
          val nextArg = argObj.Get(indexName, heap)
          aobj + AbsObjectUtil.newArgObject(n).update(indexName, AbsDataProp(nextArg, atrue, atrue, atrue))
        case ConFin(values) =>
          values.foldLeft(aobj)((aobj2, num) => {
            (0 until num.toInt).foldLeft(AbsObjectUtil.newArgObject(n))((tmpArg, i) => {
              val indexName = AbsNumber(i).toAbsString
              val nextArg = argObj.Get(indexName, heap)
              tmpArg.update(indexName, AbsDataProp(nextArg, atrue, atrue, atrue))
            }) + aobj2
          })
      }
    })

    // 9. [[Call]]
    val st2 = st1.oldify(addr)
    val argsLoc = Loc(addr, Old)
    val h3 = st2.heap.update(argsLoc, argList1 + argList2)
    val newState =
      State(h3, st2.context)
        .varStore(funcId, callableFunc)
        .varStore(thisId, thisArg)
        .varStore(argsId, AbsValue(argsLoc))

    (newState, st2.raiseException(excSet1 ++ excSet2 ++ excSet3))
  }

  // 15.3.4.4 Function.prototype.call(thisArg [, arg1 [, arg2, ...]])
  def callBeforeCall(funcId: CFGId, thisId: CFGId, argsId: CFGId)(args: AbsValue, st: State, addr: Address): (State, State) = {
    val func = st.context.thisBinding.locset
    val (thisArg, st1, excSet1) = {
      val origThis = Helper.propLoad(args, HashSet(AbsString("0")), st.heap)

      // "NOTE: Edition 3, where a undefined or null thisArg is replaced with the global object
      // and ToObject is applied to all other values and that result is passed as the this value."
      // However, most of the browsers returns the object as Edition 3.
      // Below modeling for thisArg follows the Edition 3.
      val nullOrUndef = (origThis.pvalue.nullval </ AbsNull.Bot) || (origThis.pvalue.undefval </ AbsUndef.Bot)
      val thisLoc1 =
        if (nullOrUndef) AbsValue(BuiltinGlobal.loc)
        else AbsValue.Bot

      val notNullNorUndef = (origThis </ AbsNull.Top) && (origThis </ AbsUndef.Top)
      val (thisLoc2, st1, excSet1) =
        if (notNullNorUndef) {
          val addr = SystemAddr("Function.prototype.apply<this>")
          TypeConversionHelper.ToObject(origThis, st, addr)
        } else (AbsValue.Bot, st, ExcSetEmpty)

      (thisLoc1 + thisLoc2, st1, excSet1)
    }

    val heap = st1.heap
    // 1. If IsCallable(func) is false, then throw a TypeError exception.
    val notAllCallable = func.exists(loc => {
      AbsBool.False <= TypeConversionHelper.IsCallable(loc, heap)
    })
    val excSet2 =
      if (notAllCallable) ExcSetEmpty + TypeError
      else ExcSetEmpty

    val callableFunc = func.locset.filter(loc => {
      AbsBool.True <= TypeConversionHelper.IsCallable(loc, heap)
    })

    // 2. - 3.
    val len = {
      val origLength = Helper.propLoad(args, HashSet(AbsString("length")), heap)
      Helper.bopMinus(origLength, AbsNumber(1.0)).pvalue.numval
    }
    val argList = args.locset.foldLeft(AbsObjectUtil.Bot)((aobj, loc) => {
      val argObj = heap.get(loc)
      len.gamma match {
        case ConInf() =>
          val indexName = AbsString.Number
          val nextArg = argObj.Get(indexName, heap)
          AbsObjectUtil.newArgObject(len).update(indexName, AbsDataProp(nextArg, atrue, atrue, atrue))
        case ConFin(values) =>
          values.foldLeft(aobj)((aobj2, num) => {
            (0 until num.toInt).foldLeft(AbsObjectUtil.newArgObject(len))((tmpArg, i) => {
              val nextArg = argObj.Get(AbsNumber(i + 1).toAbsString, heap)
              tmpArg.update(AbsNumber(i).toAbsString, AbsDataProp(nextArg, atrue, atrue, atrue))
            }) + aobj2
          })
      }
    })

    // 4. [[Call]]
    val st2 = st1.oldify(addr)
    val argsLoc = Loc(addr, Old)
    val h3 = st2.heap.update(argsLoc, argList)
    val newState =
      State(h3, st2.context)
        .varStore(funcId, callableFunc)
        .varStore(thisId, thisArg)
        .varStore(argsId, AbsValue(argsLoc))

    (newState, st2.raiseException(excSet1 ++ excSet2))
  }

  def connectAfterCall(retId: CFGId)(args: AbsValue, st: State): (State, State, AbsValue) = {
    val (retVal, excSet) = st.lookup(retId)
    (st, st.raiseException(excSet), retVal)
  }
}
