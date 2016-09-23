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

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models._

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

    // TODO 15.3.4.2 Function.prototype.toString()
    NormalProp("toString", FuncModel(
      name = "Function.prototype.toString",
      code = BasicCode(argLen = 1, (args: AbsValue, st: State) => {
        val thisBinding = st.context.thisBinding
        val functionClass = AbsString("Function")
        val notAllFunctionClass = thisBinding.exists(loc => {
          val thisClass = st.heap.get(loc)(IClass).value.pvalue.strval
          AbsBool.True </ (thisClass === functionClass)
        })
        val excSet =
          if (notAllFunctionClass) ExcSetEmpty + TypeError
          else ExcSetEmpty

        (st, st.raiseException(excSet), AbsString.Top)
      })
    ), T, F, T),

    // TODO 15.3.4.3 Fucntion.prototype.apply(thisArg, argArray)
    NormalProp("apply", FuncModel(
      name = "Function.prototype.apply",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO 15.3.4.4 Function.prototype.call(thisArg [, arg1 [, arg2, ...]])
    NormalProp("call", FuncModel(
      name = "Function.prototype.call",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO 15.3.4.5 Function.prototype.bind(thisArg [, arg1 [, arg2, ...]])
    NormalProp("bind", FuncModel(
      name = "Function.prototype.bind",
      code = EmptyCode(argLen = 1)
    ), T, F, T)
  )
)
