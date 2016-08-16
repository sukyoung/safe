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
      Value(PValue(num)(utils))
    })), T, F, T) ::
    ("acos", BuiltinFuncModel("Math.acos", SimpleCode((args, h, sem, utils) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).acos
      Value(PValue(num)(utils))
    })), T, F, T) ::
    ("asin", BuiltinFuncModel("Math.asin", SimpleCode((args, h, sem, utils) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).asin
      Value(PValue(num)(utils))
    })), T, F, T) ::
    ("atan", BuiltinFuncModel("Math.atan", SimpleCode((args, h, sem, utils) => {
      val resV = sem.CFGLoadHelper(args, Set(utils.absString.alpha("0")), h)
      val num = resV.toAbsNumber(utils.absNumber).atan
      Value(PValue(num)(utils))
    })), T, F, T) :: Nil
}) with Builtin
