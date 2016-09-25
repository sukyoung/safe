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
        val thisBinding = st.context.thisBinding
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

    // TODO 15.3.4.3 Fucntion.prototype.apply(thisArg, argArray)
    NormalProp("apply", FuncModel(
      name = "Function.prototype.apply",
      code = BasicCode(argLen = 2, BuiltinFunctionProtoHelper.apply)
    ), T, F, T),

    // TODO 15.3.4.4 Function.prototype.call(thisArg [, arg1 [, arg2, ...]])
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
        BuiltinFunctionProtoHelper.callAfterCall(retId)
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

  // TODO 15.3.4.3 Fucntion.prototype.apply(thisArg, argArray)
  def apply(args: AbsValue, st: State): (State, State, AbsValue) = {
    val func = st.context.thisBinding
    val thisArg = Helper.propLoad(args, HashSet(AbsString("0")), st.heap)
    val argArray = Helper.propLoad(args, HashSet(AbsString("1")), st.heap)

    // 1. If IsCallable(func) is false, then throw a TypeError exception.
    val notAllCallable = func.exists(loc => {
      AbsBool.False <= TypeConversionHelper.IsCallable(loc, st.heap)
    })
    val excSet1 =
      if (notAllCallable) ExcSetEmpty + TypeError
      else ExcSetEmpty

    val callableFunc = func.locset.filter(loc => {
      AbsBool.True <= TypeConversionHelper.IsCallable(loc, st.heap)
    })

    // 2. If argArray is null or undefined, then
    val nullOrUndef = (argArray.pvalue.nullval </ AbsNull.Bot) || (argArray.pvalue.undefval </ AbsUndef.Bot)
    val argList1 =
      if (nullOrUndef) AbsObjectUtil.newArgObject(AbsNumber(0.0))
      else AbsObjectUtil.Bot

    // 3. If Type(argArray) is not Object, then throw a TypeError exception.
    val excSet2 =
      if (argArray.pvalue </ AbsPValue.Bot) ExcSetEmpty + TypeError
      else ExcSetEmpty

    // 4. - 8.
    val argList2 = argArray.locset.foldLeft(AbsObjectUtil.Bot)((aobj, loc) => {
      val argObj = st.heap.get(loc)
      val len = argObj.Get("length", st.heap)
      val n = TypeConversionHelper.ToUInt32(len)
      n.gamma match {
        case ConInf() =>
          val indexName = AbsString.Number
          val nextArg = argObj.Get(indexName, st.heap)
          aobj + AbsObjectUtil.newArgObject(n).update(indexName, AbsDataProp(nextArg, atrue, atrue, atrue))
        case ConFin(values) =>
          values.foldLeft(aobj)((aobj2, num) => {
            (0 until num.toInt).foldLeft(AbsObjectUtil.newArgObject(n))((tmpArg, i) => {
              val indexName = AbsNumber(i).toAbsString
              val nextArg = argObj.Get(indexName, st.heap)
              tmpArg.update(indexName, AbsDataProp(nextArg, atrue, atrue, atrue))
            }) + aobj2
          })
      }
    })

    // TODO: 9. [[Call]]
    // val result = callableFunc.foldLeft[AbsValue](AbsPValue.Bot)((avalue, loc) => {
    //   st.heap.get(loc).[[Call]](thisArg, argList1 + argList2)
    // })
    val result = AbsUndef.Top

    (st, st.raiseException(excSet1 ++ excSet2), result)
  }

  // TODO 15.3.4.4 Function.prototype.call(thisArg [, arg1 [, arg2, ...]])
  def callBeforeCall(funcId: CFGId, thisId: CFGId, argsId: CFGId)(args: AbsValue, st: State, addr: Address): (State, State) = {
    val func = st.context.thisBinding
    val thisArg = Helper.propLoad(args, HashSet(AbsString("0")), st.heap)

    // 1. If IsCallable(func) is false, then throw a TypeError exception.
    val notAllCallable = func.exists(loc => {
      AbsBool.False <= TypeConversionHelper.IsCallable(loc, st.heap)
    })
    val excSet =
      if (notAllCallable) ExcSetEmpty + TypeError
      else ExcSetEmpty

    val callableFunc = func.locset.filter(loc => {
      AbsBool.True <= TypeConversionHelper.IsCallable(loc, st.heap)
    })

    // 2. - 3.
    val len = {
      val origLength = Helper.propLoad(args, HashSet(AbsString("length")), st.heap)
      Helper.bopMinus(origLength, AbsNumber(1.0)).pvalue.numval
    }
    val argList = args.locset.foldLeft(AbsObjectUtil.Bot)((aobj, loc) => {
      val argObj = st.heap.get(loc)
      len.gamma match {
        case ConInf() =>
          val indexName = AbsString.Number
          val nextArg = argObj.Get(indexName, st.heap)
          AbsObjectUtil.newArgObject(len).update(indexName, AbsDataProp(nextArg, atrue, atrue, atrue))
        case ConFin(values) =>
          values.foldLeft(aobj)((aobj2, num) => {
            (0 until num.toInt).foldLeft(AbsObjectUtil.newArgObject(len))((tmpArg, i) => {
              val nextArg = argObj.Get(AbsNumber(i + 1).toAbsString, st.heap)
              tmpArg.update(AbsNumber(i).toAbsString, AbsDataProp(nextArg, atrue, atrue, atrue))
            }) + aobj2
          })
      }
    })

    // TODO: 4. [[Call]]
    // val result = callableFunc.foldLeft[AbsValue](AbsPValue.Bot)((avalue, loc) => {
    //   st.heap.get(loc).[[Call]](thisArg, argList)
    // })

    val argsLoc = Loc(addr, Old)
    val h1 = st.heap.update(argsLoc, argList)
    val newState =
      State(h1, st.context)
        .varStore(funcId, callableFunc)
        .varStore(thisId, thisArg)
        .varStore(argsId, AbsValue(argsLoc))

    (newState, st.raiseException(excSet))
  }

  def callAfterCall(retId: CFGId)(args: AbsValue, st: State): (State, State, AbsValue) = {
    val (retVal, excSet) = st.lookup(retId)
    (st, st.raiseException(excSet), retVal)
  }
}
