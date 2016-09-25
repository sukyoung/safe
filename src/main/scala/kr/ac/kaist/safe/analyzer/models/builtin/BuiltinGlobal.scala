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
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.util.NodeUtil

object BuiltinGlobal extends ObjModel(
  name = "Global",
  props = List(
    NormalProp("NaN", PrimModel(Double.NaN), F, F, F),
    NormalProp("Infinity", PrimModel(Double.PositiveInfinity), F, F, F),
    NormalProp("undefined", PrimModel(Undef), F, F, F),

    NormalProp("eval", FuncModel(
      name = "Global.eval",
      // TODO code
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    NormalProp("parseInt", FuncModel(
      name = "Global.parseInt",
      // TODO code
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    NormalProp("parseFloat", FuncModel(
      name = "Global.parseFloat",
      // TODO code
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    NormalProp("isNaN", FuncModel(
      name = "Global.isNaN",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("0")), h)
        BuiltinHelper.isNaN(resV)
      })
    ), T, F, T),

    NormalProp("isFinite", FuncModel(
      name = "Global.isFinite",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("0")), h)
        val num = TypeConversionHelper.ToNumber(resV)
        num.gamma match {
          case ConFin(set) if set.size <= 3 => set.foldLeft(AbsBool.Bot) {
            case (b, Num(n)) =>
              if (n.isNaN || n.isInfinity) b + AbsBool.False
              else b + AbsBool.True
          }
          case _ => AbsBool.Top
        }
      })
    ), T, F, T),

    NormalProp("decodeURI", FuncModel(
      name = "Global.decodeURI",
      // TODO code
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    NormalProp("decodeURIComponent", FuncModel(
      name = "Global.decodeURIComponent",
      // TODO code
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    NormalProp("encodeURI", FuncModel(
      name = "Global.encodeURI",
      // TODO code
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    NormalProp("encodeURIComponent", FuncModel(
      name = "Global.encodeURIComponent",
      // TODO code
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    NormalProp("Object", BuiltinObject, T, F, T),
    NormalProp("Function", BuiltinFunction, T, F, T),
    NormalProp("Array", BuiltinArray, T, F, T),
    NormalProp("String", BuiltinString, T, F, T),
    NormalProp("Boolean", BuiltinBoolean, T, F, T),
    NormalProp("Number", BuiltinNumber, T, F, T),
    NormalProp("Date", BuiltinDate, T, F, T),
    NormalProp("RegExp", BuiltinRegExp, T, F, T),
    NormalProp("Error", BuiltinError, T, F, T),
    NormalProp("EvalError", BuiltinEvalError, T, F, T),
    NormalProp("RangeError", BuiltinRangeError, T, F, T),
    NormalProp("ReferenceError", BuiltinReferenceError, T, F, T),
    NormalProp("SyntaxError", BuiltinSyntaxError, T, F, T),
    NormalProp("TypeError", BuiltinTypeError, T, F, T),
    NormalProp("URIError", BuiltinURIError, T, F, T),
    NormalProp("Math", BuiltinMath, T, F, T),
    NormalProp("JSON", BuiltinJSON, T, F, T),
    NormalProp(NodeUtil.GLOBAL_NAME, SelfModel, F, F, F),
    NormalProp(NodeUtil.VAR_TRUE, PrimModel(true), F, F, F)
  )
)
