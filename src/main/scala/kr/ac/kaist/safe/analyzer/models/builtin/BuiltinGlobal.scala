/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.models.builtin

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.util.NodeUtil

object BuiltinGlobal extends ObjModel(
  name = "Global",
  props = List(
    // 15.1.1.1 NaN
    NormalProp("NaN", PrimModel(Double.NaN), F, F, F),
    // 15.1.1.2 Infinity
    NormalProp("Infinity", PrimModel(Double.PositiveInfinity), F, F, F),
    // 15.1.1.3 undefined
    NormalProp("undefined", PrimModel(Undef), F, F, F),

    // TODO 15.1.2.1 eval(x)
    NormalProp("eval", FuncModel(
      name = "Global.eval",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO 15.1.2.2 parseInt(string, radix)
    NormalProp("parseInt", FuncModel(
      name = "Global.parseInt",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO 15.1.2.3 parseFloat(string)
    NormalProp("parseFloat", FuncModel(
      name = "Global.parseFloat",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // 15.1.2.4 isNaN(number)
    NormalProp("isNaN", FuncModel(
      name = "Global.isNaN",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsStr("0")), h)
        BuiltinHelper.isNaN(resV)
      })
    ), T, F, T),

    // 15.1.2.5 isFinite(number)
    NormalProp("isFinite", FuncModel(
      name = "Global.isFinite",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsStr("0")), h)
        val num = TypeConversionHelper.ToNumber(resV)
        num.gamma match {
          case ConFin(set) if set.size <= 3 => set.foldLeft(AbsBool.Bot) {
            case (b, Num(n)) =>
              if (n.isNaN || n.isInfinity) b ⊔ AbsBool.False
              else b ⊔ AbsBool.True
          }
          case _ => AbsBool.Top
        }
      })
    ), T, F, T),

    // TODO 15.1.3.1 decodeURI(encodedURI)
    NormalProp("decodeURI", FuncModel(
      name = "Global.decodeURI",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO 15.1.3.2 decodeURIComponent(encodedURIComponent)
    NormalProp("decodeURIComponent", FuncModel(
      name = "Global.decodeURIComponent",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO 15.1.3.3 encodeURI(uri)
    NormalProp("encodeURI", FuncModel(
      name = "Global.encodeURI",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO 15.1.3.4 encodeURIComponent(uriComponent)
    NormalProp("encodeURIComponent", FuncModel(
      name = "Global.encodeURIComponent",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // 15.1.4.1 Object(...)
    NormalProp("Object", BuiltinObject, T, F, T),
    // 15.1.4.2 Function(...)
    NormalProp("Function", BuiltinFunction, T, F, T),
    // 15.1.4.3 Array(...)
    NormalProp("Array", BuiltinArray, T, F, T),
    // 15.1.4.4 String(...)
    NormalProp("String", BuiltinString, T, F, T),
    // 15.1.4.5 Boolean(...)
    NormalProp("Boolean", BuiltinBoolean, T, F, T),
    // 15.1.4.6 Number(...)
    NormalProp("Number", BuiltinNumber, T, F, T),
    // 15.1.4.7 Date(...)
    NormalProp("Date", BuiltinDate, T, F, T),
    // 15.1.4.8 RegExp(...)
    NormalProp("RegExp", BuiltinRegExp, T, F, T),
    // 15.1.4.9 Error(...)
    NormalProp("Error", BuiltinError, T, F, T),
    // 15.1.4.10 EvalError(...)
    NormalProp("EvalError", BuiltinEvalError, T, F, T),
    // 15.1.4.11 RangeError(...)
    NormalProp("RangeError", BuiltinRangeError, T, F, T),
    // 15.1.4.12 ReferenceError(...)
    NormalProp("ReferenceError", BuiltinRefError, T, F, T),
    // 15.1.4.13 SyntaxError(...)
    NormalProp("SyntaxError", BuiltinSyntaxError, T, F, T),
    // 15.1.4.14 TypeError(...)
    NormalProp("TypeError", BuiltinTypeError, T, F, T),
    // 15.1.4.15 URIError(...)
    NormalProp("URIError", BuiltinURIError, T, F, T),
    // 15.1.5.1 Math(...)
    NormalProp("Math", BuiltinMath, T, F, T),
    // 15.1.5.2 JSON(...)
    NormalProp("JSON", BuiltinJSON, T, F, T),

    NormalProp(NodeUtil.GLOBAL_NAME, SelfModel, F, F, F),
    NormalProp(NodeUtil.VAR_TRUE, PrimModel(true), F, F, F)
  )
)
