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

object BuiltinMath extends ObjModel("Math", {
  ("@class", PrimModel("Math"), F, F, F) ::
    ("E", PrimModel(2.7182818284590452354), F, F, F) ::
    ("LN10", PrimModel(2.302585092994046), F, F, F) ::
    ("LN2", PrimModel(0.6931471805599453), F, F, F) ::
    ("LOG2E", PrimModel(1.4426950408889634), F, F, F) ::
    ("LOG10E", PrimModel(0.4342944819032518), F, F, F) ::
    ("PI", PrimModel(3.1415926535897932), F, F, F) ::
    ("SQRT1_2", PrimModel(0.7071067811865476), F, F, F) ::
    ("SQRT2", PrimModel(1.4142135623730951), F, F, F) ::
    ("abs", BuiltinFuncModel("Math.abs", SimpleCode((args, h, sem, utils) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).abs
      (h, Value(PValue(num)(utils)))
    })), T, F, T) ::
    ("acos", BuiltinFuncModel("Math.acos", SimpleCode((args, h, sem, utils) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).acos
      (h, Value(PValue(num)(utils)))
    })), T, F, T) ::
    ("asin", BuiltinFuncModel("Math.asin", SimpleCode((args, h, sem, utils) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).asin
      (h, Value(PValue(num)(utils)))
    })), T, F, T) ::
    ("atan", BuiltinFuncModel("Math.atan", SimpleCode((args, h, sem, utils) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).atan
      (h, Value(PValue(num)(utils)))
    })), T, F, T) ::
    ("atan2", BuiltinFuncModel("Math.atan2", SimpleCode((args, h, sem, utils) => {
      val resVy = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val resVx = sem.CFGLoadHelper(args, Set(utils.absString.alpha("1")), h)
      val num = resVy.toAbsNumber(utils.absNumber).atan2(resVx.toAbsNumber(utils.absNumber))
      (h, Value(PValue(num)(utils)))
    })), T, F, T) ::
    ("ceil", BuiltinFuncModel("Math.ceil", SimpleCode((args, h, sem, utils) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).ceil
      (h, Value(PValue(num)(utils)))
    })), T, F, T) ::
    ("cos", BuiltinFuncModel("Math.cos", SimpleCode((args, h, sem, utils) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).cos
      (h, Value(PValue(num)(utils)))
    })), T, F, T) ::
    ("exp", BuiltinFuncModel("Math.exp", SimpleCode((args, h, sem, utils) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).exp
      (h, Value(PValue(num)(utils)))
    })), T, F, T) ::
    ("floor", BuiltinFuncModel("Math.floor", SimpleCode((args, h, sem, utils) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).floor
      (h, Value(PValue(num)(utils)))
    })), T, F, T) ::
    ("log", BuiltinFuncModel("Math.log", SimpleCode((args, h, sem, utils) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).log
      (h, Value(PValue(num)(utils)))
    })), T, F, T) ::
    //TODO max
    ("max", BuiltinFuncModel("Math.max", SimpleCode((args, h, sem, utils) => {
      (h, Value(PValue(utils.absNumber.Top)(utils)))
    })), T, F, T) ::
    //TODO min
    ("min", BuiltinFuncModel("Math.min", SimpleCode((args, h, sem, utils) => {
      (h, Value(PValue(utils.absNumber.Top)(utils)))
    })), T, F, T) ::
    ("pow", BuiltinFuncModel("Math.pow", SimpleCode((args, h, sem, utils) => {
      val resVx = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val resVy = sem.CFGLoadHelper(args, Set(utils.absString.alpha("1")), h)
      val num = resVx.toAbsNumber(utils.absNumber).pow(resVy.toAbsNumber(utils.absNumber))
      (h, Value(PValue(num)(utils)))
    })), T, F, T) ::
    ("random", BuiltinFuncModel("Math.random", SimpleCode((args, h, sem, utils) => {
      (h, Value(PValue(utils.absNumber.Top)(utils)))
    })), T, F, T) ::
    ("round", BuiltinFuncModel("Math.round", SimpleCode((args, h, sem, utils) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).round
      (h, Value(PValue(num)(utils)))
    })), T, F, T) ::
    ("sin", BuiltinFuncModel("Math.sin", SimpleCode((args, h, sem, utils) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).sin
      (h, Value(PValue(num)(utils)))
    })), T, F, T) ::
    ("sqrt", BuiltinFuncModel("Math.sqrt", SimpleCode((args, h, sem, utils) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).sqrt
      (h, Value(PValue(num)(utils)))
    })), T, F, T) ::
    ("tan", BuiltinFuncModel("Math.tan", SimpleCode((args, h, sem, utils) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).tan
      (h, Value(PValue(num)(utils)))
    })), T, F, T) :: Nil
}) with Builtin
