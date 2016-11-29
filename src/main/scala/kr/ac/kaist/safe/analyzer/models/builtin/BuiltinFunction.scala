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
import kr.ac.kaist.safe.util.Address

import scala.collection.immutable.HashSet

// 15.3 Function Objects
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
      code = BasicCode(argLen = 0, code = (args: AbsValue, st: AbsState) => {
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
  def applyBeforeCall(funcId: CFGId, thisId: CFGId, argsId: CFGId)(args: AbsValue, st: AbsState, addr: Address): (AbsState, AbsState) = {
    val func = st.context.thisBinding.locset
    val thisArg = Helper.propLoad(args, HashSet(AbsString("0")), st.heap)
    val heap = st.heap
    val argArray = Helper.propLoad(args, HashSet(AbsString("1")), heap)

    // 1. If IsCallable(func) is false, then throw a TypeError exception.
    val notAllCallable = func.exists(loc => {
      AbsBool.False <= TypeConversionHelper.IsCallable(loc, heap)
    })
    val excSet1 =
      if (notAllCallable) ExcSetEmpty + TypeError
      else ExcSetEmpty

    val callableFunc = func.locset.filter(loc => {
      AbsBool.True <= TypeConversionHelper.IsCallable(loc, heap)
    })

    // 2. If argArray is null or undefined, then
    val nullOrUndef = (argArray.pvalue.nullval </ AbsNull.Bot) || (argArray.pvalue.undefval </ AbsUndef.Bot)
    val argList1 =
      if (nullOrUndef) AbsObject.newArgObject(AbsNumber(0.0))
      else AbsObject.Bot

    // 3. If Type(argArray) is not Object, then throw a TypeError exception.
    val excSet2 =
      if (argArray.pvalue </ AbsPValue.Bot) ExcSetEmpty + TypeError
      else ExcSetEmpty

    // 4. - 8.
    val argList2 = argArray.locset.foldLeft(AbsObject.Bot)((aobj, loc) => {
      val argObj = heap.get(loc)
      val len = argObj.Get("length", heap)
      val n = TypeConversionHelper.ToUInt32(len)
      n.gamma match {
        case ConInf() =>
          val indexName = AbsString.Number
          val nextArg = argObj.Get(indexName, heap)
          aobj + AbsObject.newArgObject(n).update(indexName, AbsDataProp(nextArg, atrue, atrue, atrue))
        case ConFin(values) =>
          values.foldLeft(aobj)((aobj2, num) => {
            (0 until num.toInt).foldLeft(AbsObject.newArgObject(n))((tmpArg, i) => {
              val indexName = AbsNumber(i).toAbsString
              val nextArg = argObj.Get(indexName, heap)
              tmpArg.update(indexName, AbsDataProp(nextArg, atrue, atrue, atrue))
            }) + aobj2
          })
      }
    })

    // 9. [[Call]]
    val st1 = st.oldify(addr)
    val argsLoc = Loc(addr, Old)
    val h3 = st1.heap.update(argsLoc, argList1 + argList2)
    val newState =
      AbsState(h3, st1.context)
        .varStore(funcId, callableFunc)
        .varStore(thisId, thisArg)
        .varStore(argsId, AbsValue(argsLoc))

    (newState, st1.raiseException(excSet1 ++ excSet2))
  }

  // 15.3.4.4 Function.prototype.call(thisArg [, arg1 [, arg2, ...]])
  def callBeforeCall(funcId: CFGId, thisId: CFGId, argsId: CFGId)(args: AbsValue, st: AbsState, addr: Address): (AbsState, AbsState) = {
    val func = st.context.thisBinding.locset
    val thisArg = Helper.propLoad(args, HashSet(AbsString("0")), st.heap)
    val heap = st.heap
    // 1. If IsCallable(func) is false, then throw a TypeError exception.
    val notAllCallable = func.exists(loc => {
      AbsBool.False <= TypeConversionHelper.IsCallable(loc, heap)
    })
    val excSet1 =
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
    val argList = args.locset.foldLeft(AbsObject.Bot)((aobj, loc) => {
      val argObj = heap.get(loc)
      len.gamma match {
        case ConInf() =>
          val indexName = AbsString.Number
          val nextArg = argObj.Get(indexName, heap)
          AbsObject.newArgObject(len).update(indexName, AbsDataProp(nextArg, atrue, atrue, atrue))
        case ConFin(values) =>
          values.foldLeft(aobj)((aobj2, num) => {
            (0 until num.toInt).foldLeft(AbsObject.newArgObject(len))((tmpArg, i) => {
              val nextArg = argObj.Get(AbsNumber(i + 1).toAbsString, heap)
              tmpArg.update(AbsNumber(i).toAbsString, AbsDataProp(nextArg, atrue, atrue, atrue))
            }) + aobj2
          })
      }
    })

    // 4. [[Call]]
    val st1 = st.oldify(addr)
    val argsLoc = Loc(addr, Old)
    val h3 = st1.heap.update(argsLoc, argList)
    val newState =
      AbsState(h3, st1.context)
        .varStore(funcId, callableFunc)
        .varStore(thisId, thisArg)
        .varStore(argsId, AbsValue(argsLoc))

    (newState, st1.raiseException(excSet1))
  }

  def connectAfterCall(retId: CFGId)(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val (retVal, excSet) = st.lookup(retId)
    (st, st.raiseException(excSet), retVal)
  }
}
