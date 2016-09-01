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
import kr.ac.kaist.safe.analyzer.models._

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
      code = SimpleCode(argLen = 1, (args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = sem.typeHelper.ToNumber(resV).abs
        utils.value(num)
      })
    ), T, F, T),

    NormalProp("acos", FuncModel(
      name = "Math.acos",
      code = SimpleCode(argLen = 1, (args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = sem.typeHelper.ToNumber(resV).acos
        utils.value(num)
      })
    ), T, F, T),

    NormalProp("asin", FuncModel(
      name = "Math.asin",
      code = SimpleCode(argLen = 1, (args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = sem.typeHelper.ToNumber(resV).asin
        utils.value(num)
      })
    ), T, F, T),

    NormalProp("atan", FuncModel(
      name = "Math.atan",
      code = SimpleCode(argLen = 1, (args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = sem.typeHelper.ToNumber(resV).atan
        utils.value(num)
      })
    ), T, F, T),

    NormalProp("atan2", FuncModel(
      name = "Math.atan2",
      code = SimpleCode(argLen = 2, (args, h, sem, utils) => {
        val resVy = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val resVx = sem.CFGLoadHelper(args, Set(utils.absString.alpha("1")), h)
        val num = sem.typeHelper.ToNumber(resVy).atan2(sem.typeHelper.ToNumber(resVx))
        utils.value(num)
      })
    ), T, F, T),

    NormalProp("ceil", FuncModel(
      name = "Math.ceil",
      code = SimpleCode(argLen = 1, (args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = sem.typeHelper.ToNumber(resV).ceil
        utils.value(num)
      })
    ), T, F, T),

    NormalProp("cos", FuncModel(
      name = "Math.cos",
      code = SimpleCode(argLen = 1, (args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = sem.typeHelper.ToNumber(resV).cos
        utils.value(num)
      })
    ), T, F, T),

    NormalProp("exp", FuncModel(
      name = "Math.exp",
      code = SimpleCode(argLen = 1, (args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = sem.typeHelper.ToNumber(resV).exp
        utils.value(num)
      })
    ), T, F, T),

    NormalProp("floor", FuncModel(
      name = "Math.floor",
      code = SimpleCode(argLen = 1, (args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = sem.typeHelper.ToNumber(resV).floor
        utils.value(num)
      })
    ), T, F, T),

    NormalProp("log", FuncModel(
      name = "Math.log",
      code = SimpleCode(argLen = 1, (args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = sem.typeHelper.ToNumber(resV).log
        utils.value(num)
      })
    ), T, F, T),

    //TODO max
    NormalProp("max", FuncModel(
      name = "Math.max",
      code = SimpleCode(argLen = 2, (args, h, sem, utils) => {
        utils.value(utils.absNumber.Top)
      })
    ), T, F, T),

    //TODO min
    NormalProp("min", FuncModel(
      name = "Math.min",
      code = SimpleCode(argLen = 2, (args, h, sem, utils) => {
        utils.value(utils.absNumber.Top)
      })
    ), T, F, T),

    NormalProp("pow", FuncModel(
      name = "Math.pow",
      code = SimpleCode(argLen = 2, (args, h, sem, utils) => {
        val resVx = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val resVy = sem.CFGLoadHelper(args, Set(utils.absString.alpha("1")), h)
        val num = sem.typeHelper.ToNumber(resVx).pow(sem.typeHelper.ToNumber(resVy))
        utils.value(num)
      })
    ), T, F, T),

    NormalProp("random", FuncModel(
      name = "Math.random",
      code = SimpleCode(argLen = 0, (args, h, sem, utils) => {
        utils.value(utils.absNumber.Top)
      })
    ), T, F, T),

    NormalProp("round", FuncModel(
      name = "Math.round",
      code = SimpleCode(argLen = 1, (args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = sem.typeHelper.ToNumber(resV).round
        utils.value(num)
      })
    ), T, F, T),

    NormalProp("sin", FuncModel(
      name = "Math.sin",
      code = SimpleCode(argLen = 1, (args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = sem.typeHelper.ToNumber(resV).sin
        utils.value(num)
      })
    ), T, F, T),

    NormalProp("sqrt", FuncModel(
      name = "Math.sqrt",
      code = SimpleCode(argLen = 1, (args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = sem.typeHelper.ToNumber(resV).sqrt
        utils.value(num)
      })
    ), T, F, T),

    NormalProp("tan", FuncModel(
      name = "Math.tan",
      code = SimpleCode(argLen = 1, (args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = sem.typeHelper.ToNumber(resV).tan
        utils.value(num)
      })
    ), T, F, T)
  )
)
