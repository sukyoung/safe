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
    ("@class", PrimModel("Math"), F, F, F),
    ("E", PrimModel(2.7182818284590452354), F, F, F),
    ("LN10", PrimModel(2.302585092994046), F, F, F),
    ("LN2", PrimModel(0.6931471805599453), F, F, F),
    ("LOG2E", PrimModel(1.4426950408889634), F, F, F),
    ("LOG10E", PrimModel(0.4342944819032518), F, F, F),
    ("PI", PrimModel(3.1415926535897932), F, F, F),
    ("SQRT1_2", PrimModel(0.7071067811865476), F, F, F),
    ("SQRT2", PrimModel(1.4142135623730951), F, F, F),

    ("abs", BuiltinFuncModel(
      name = "Math.abs",
      argLen = 1,
      code = SimpleCode((args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = resV.toAbsNumber(utils.absNumber).abs
        Value(PValue(num)(utils))
      })
    ), T, F, T),

    ("acos", BuiltinFuncModel(
      name = "Math.acos",
      argLen = 1,
      code = SimpleCode((args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = resV.toAbsNumber(utils.absNumber).acos
        Value(PValue(num)(utils))
      })
    ), T, F, T),

    ("asin", BuiltinFuncModel(
      name = "Math.asin",
      argLen = 1,
      code = SimpleCode((args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = resV.toAbsNumber(utils.absNumber).asin
        Value(PValue(num)(utils))
      })
    ), T, F, T),

    ("atan", BuiltinFuncModel(
      name = "Math.atan",
      argLen = 1,
      code = SimpleCode((args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = resV.toAbsNumber(utils.absNumber).atan
        Value(PValue(num)(utils))
      })
    ), T, F, T),

    ("atan2", BuiltinFuncModel(
      name = "Math.atan2",
      argLen = 2,
      code = SimpleCode((args, h, sem, utils) => {
        val resVy = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val resVx = sem.CFGLoadHelper(args, Set(utils.absString.alpha("1")), h)
        val num = resVy.toAbsNumber(utils.absNumber).atan2(resVx.toAbsNumber(utils.absNumber))
        Value(PValue(num)(utils))
      })
    ), T, F, T),

    ("ceil", BuiltinFuncModel(
      name = "Math.ceil",
      argLen = 1,
      code = SimpleCode((args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = resV.toAbsNumber(utils.absNumber).ceil
        Value(PValue(num)(utils))
      })
    ), T, F, T),

    ("cos", BuiltinFuncModel(
      name = "Math.cos",
      argLen = 1,
      code = SimpleCode((args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = resV.toAbsNumber(utils.absNumber).cos
        Value(PValue(num)(utils))
      })
    ), T, F, T),

    ("exp", BuiltinFuncModel(
      name = "Math.exp",
      argLen = 1,
      code = SimpleCode((args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = resV.toAbsNumber(utils.absNumber).exp
        Value(PValue(num)(utils))
      })
    ), T, F, T),

    ("floor", BuiltinFuncModel(
      name = "Math.floor",
      argLen = 1,
      code = SimpleCode((args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = resV.toAbsNumber(utils.absNumber).floor
        Value(PValue(num)(utils))
      })
    ), T, F, T),

    ("log", BuiltinFuncModel(
      name = "Math.log",
      argLen = 1,
      code = SimpleCode((args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = resV.toAbsNumber(utils.absNumber).log
        Value(PValue(num)(utils))
      })
    ), T, F, T),

    //TODO max
    ("max", BuiltinFuncModel(
      name = "Math.max",
      argLen = 2,
      code = SimpleCode((args, h, sem, utils) => {
        Value(PValue(utils.absNumber.Top)(utils))
      })
    ), T, F, T),

    //TODO min
    ("min", BuiltinFuncModel(
      name = "Math.min",
      argLen = 2,
      code = SimpleCode((args, h, sem, utils) => {
        Value(PValue(utils.absNumber.Top)(utils))
      })
    ), T, F, T),

    ("pow", BuiltinFuncModel(
      name = "Math.pow",
      argLen = 2,
      code = SimpleCode((args, h, sem, utils) => {
        val resVx = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val resVy = sem.CFGLoadHelper(args, Set(utils.absString.alpha("1")), h)
        val num = resVx.toAbsNumber(utils.absNumber).pow(resVy.toAbsNumber(utils.absNumber))
        Value(PValue(num)(utils))
      })
    ), T, F, T),

    ("random", BuiltinFuncModel(
      name = "Math.random",
      code = SimpleCode((args, h, sem, utils) => {
        Value(PValue(utils.absNumber.Top)(utils))
      })
    ), T, F, T),

    ("round", BuiltinFuncModel(
      name = "Math.round",
      argLen = 1,
      code = SimpleCode((args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = resV.toAbsNumber(utils.absNumber).round
        Value(PValue(num)(utils))
      })
    ), T, F, T),

    ("sin", BuiltinFuncModel(
      name = "Math.sin",
      argLen = 1,
      code = SimpleCode((args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = resV.toAbsNumber(utils.absNumber).sin
        Value(PValue(num)(utils))
      })
    ), T, F, T),

    ("sqrt", BuiltinFuncModel(
      name = "Math.sqrt",
      argLen = 1,
      code = SimpleCode((args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = resV.toAbsNumber(utils.absNumber).sqrt
        Value(PValue(num)(utils))
      })
    ), T, F, T),

    ("tan", BuiltinFuncModel(
      name = "Math.tan",
      argLen = 1,
      code = SimpleCode((args, h, sem, utils) => {
        val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
        val num = resV.toAbsNumber(utils.absNumber).tan
        Value(PValue(num)(utils))
      })
    ), T, F, T)
  )
) with Builtin
