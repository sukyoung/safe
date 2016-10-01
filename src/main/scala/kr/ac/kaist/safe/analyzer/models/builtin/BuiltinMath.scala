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

object BuiltinMath extends ObjModel(
  name = "Math",
  props = List(
    InternalProp(IClass, PrimModel("Math")),
    NormalProp("E", PrimModel(2.7182818284590452354), F, F, F),
    NormalProp("LN10", PrimModel(2.302585092994046), F, F, F),
    NormalProp("LN2", PrimModel(0.6931471805599453), F, F, F),
    NormalProp("LOG2E", PrimModel(1.4426950408889634), F, F, F),
    NormalProp("LOG10E", PrimModel(0.4342944819032518), F, F, F),
    NormalProp("PI", PrimModel(3.1415926535897932), F, F, F),
    NormalProp("SQRT1_2", PrimModel(0.7071067811865476), F, F, F),
    NormalProp("SQRT2", PrimModel(1.4142135623730951), F, F, F),

    NormalProp("abs", FuncModel(
      name = "Math.abs",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("0")), h)
        val num = TypeConversionHelper.ToNumber(resV).abs
        AbsValue(num)
      })
    ), T, F, T),

    NormalProp("acos", FuncModel(
      name = "Math.acos",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("0")), h)
        val num = TypeConversionHelper.ToNumber(resV).acos
        AbsValue(num)
      })
    ), T, F, T),

    NormalProp("asin", FuncModel(
      name = "Math.asin",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("0")), h)
        val num = TypeConversionHelper.ToNumber(resV).asin
        AbsValue(num)
      })
    ), T, F, T),

    NormalProp("atan", FuncModel(
      name = "Math.atan",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("0")), h)
        val num = TypeConversionHelper.ToNumber(resV).atan
        AbsValue(num)
      })
    ), T, F, T),

    NormalProp("atan2", FuncModel(
      name = "Math.atan2",
      code = PureCode(argLen = 2, (args, st) => {
        val h = st.heap
        val resVy = Helper.propLoad(args, Set(AbsString("0")), h)
        val resVx = Helper.propLoad(args, Set(AbsString("1")), h)
        val num = TypeConversionHelper.ToNumber(resVy).atan2(TypeConversionHelper.ToNumber(resVx))
        AbsValue(num)
      })
    ), T, F, T),

    NormalProp("ceil", FuncModel(
      name = "Math.ceil",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("0")), h)
        val num = TypeConversionHelper.ToNumber(resV).ceil
        AbsValue(num)
      })
    ), T, F, T),

    NormalProp("cos", FuncModel(
      name = "Math.cos",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("0")), h)
        val num = TypeConversionHelper.ToNumber(resV).cos
        AbsValue(num)
      })
    ), T, F, T),

    NormalProp("exp", FuncModel(
      name = "Math.exp",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("0")), h)
        val num = TypeConversionHelper.ToNumber(resV).exp
        AbsValue(num)
      })
    ), T, F, T),

    NormalProp("floor", FuncModel(
      name = "Math.floor",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("0")), h)
        val num = TypeConversionHelper.ToNumber(resV).floor
        AbsValue(num)
      })
    ), T, F, T),

    NormalProp("log", FuncModel(
      name = "Math.log",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("0")), h)
        val num = TypeConversionHelper.ToNumber(resV).log
        AbsValue(num)
      })
    ), T, F, T),

    NormalProp("max", FuncModel(
      name = "Math.max",
      code = PureCode(argLen = 2, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("length")), h)
        def uintCheck(num: Double): Boolean = {
          val uint = num.toLong
          (num == uint) && (uint > 0 || (num compare 0.0) == 0)
        }
        def nArg(i: Int): AbsNumber = {
          TypeConversionHelper.ToNumber(
            Helper.propLoad(args, Set(AbsString(i.toString)), h)
          )
        }
        resV.pvalue.numval.getSingle match {
          case ConZero() => AbsNumber.Bot
          case ConOne(Num(0)) => AbsNumber.NegInf
          case ConOne(Num(num)) if uintCheck(num) => {
            val len = num.toInt
            val nanN =
              if ((0 until len).exists(AbsNumber.NaN <= nArg(_))) AbsNumber.NaN
              else AbsNumber.Bot
            val maxN = (1 until len).foldLeft(nArg(0)) {
              case (absN, i) => {
                val curN = nArg(i)
                (absN < curN).map[AbsNumber](
                  thenV = curN,
                  elseV = absN
                )(AbsNumber)
              }
            }
            nanN + maxN
          }
          case ConMany() => AbsNumber.Top
        }
      })
    ), T, F, T),

    NormalProp("min", FuncModel(
      name = "Math.min",
      code = PureCode(argLen = 2, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("length")), h)
        def uintCheck(num: Double): Boolean = {
          val uint = num.toLong
          (num == uint) && (uint > 0 || (num compare 0.0) == 0)
        }
        def nArg(i: Int): AbsNumber = {
          TypeConversionHelper.ToNumber(
            Helper.propLoad(args, Set(AbsString(i.toString)), h)
          )
        }
        resV.pvalue.numval.getSingle match {
          case ConZero() => AbsNumber.Bot
          case ConOne(Num(0)) => AbsNumber.PosInf
          case ConOne(Num(num)) if uintCheck(num) => {
            val len = num.toInt
            val nanN =
              if ((0 until len).exists(AbsNumber.NaN <= nArg(_))) AbsNumber.NaN
              else AbsNumber.Bot
            val minN = (1 until len).foldLeft(nArg(0)) {
              case (absN, i) => {
                val curN = nArg(i)
                (absN < curN).map[AbsNumber](
                  thenV = absN,
                  elseV = curN
                )(AbsNumber)
              }
            }
            nanN + minN
          }
          case ConMany() => AbsNumber.Top
        }
      })
    ), T, F, T),

    NormalProp("pow", FuncModel(
      name = "Math.pow",
      code = PureCode(argLen = 2, (args, st) => {
        val h = st.heap
        val resVx = Helper.propLoad(args, Set(AbsString("0")), h)
        val resVy = Helper.propLoad(args, Set(AbsString("1")), h)
        val num = TypeConversionHelper.ToNumber(resVx).pow(TypeConversionHelper.ToNumber(resVy))
        AbsValue(num)
      })
    ), T, F, T),

    NormalProp("random", FuncModel(
      name = "Math.random",
      code = PureCode(argLen = 0, (args, st) => {
        val h = st.heap
        AbsValue(AbsNumber.Top)
      })
    ), T, F, T),

    NormalProp("round", FuncModel(
      name = "Math.round",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("0")), h)
        val num = TypeConversionHelper.ToNumber(resV).round
        AbsValue(num)
      })
    ), T, F, T),

    NormalProp("sin", FuncModel(
      name = "Math.sin",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("0")), h)
        val num = TypeConversionHelper.ToNumber(resV).sin
        AbsValue(num)
      })
    ), T, F, T),

    NormalProp("sqrt", FuncModel(
      name = "Math.sqrt",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("0")), h)
        val num = TypeConversionHelper.ToNumber(resV).sqrt
        AbsValue(num)
      })
    ), T, F, T),

    NormalProp("tan", FuncModel(
      name = "Math.tan",
      code = PureCode(argLen = 1, (args, st) => {
        val h = st.heap
        val resV = Helper.propLoad(args, Set(AbsString("0")), h)
        val num = TypeConversionHelper.ToNumber(resV).tan
        AbsValue(num)
      })
    ), T, F, T)
  )
)
