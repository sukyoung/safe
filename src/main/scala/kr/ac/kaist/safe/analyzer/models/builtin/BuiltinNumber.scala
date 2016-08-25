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

import kr.ac.kaist.safe.analyzer.models._

// TODO Number
object BuiltinNumber extends FuncModel(
  name = "Number",
  // TODO @function
  code = EmptyCode(argLen = 1),
  props = List(
    ("MAX_VALUE", PrimModel(Double.MaxValue), F, F, F),
    ("MIN_VALUE", PrimModel(Double.MinPositiveValue), F, F, F),
    ("NaN", PrimModel(Double.NaN), F, F, F),
    ("NEGATIVE_INFINITY", PrimModel(Double.NegativeInfinity), F, F, F),
    ("POSITIVE_INFINITY", PrimModel(Double.PositiveInfinity), F, F, F)
  ),
  hasConstruct = T,
  protoModel = Some((BuiltinNumberProto, F, F, F))
)

object BuiltinNumberProto extends ObjModel(
  name = "Number.prototype",
  props = List(
    ("@class", PrimModel("Number"), F, F, F),

    // TODO toString
    ("toString", FuncModel(
      name = "Number.prototype.toString",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO toLocaleString
    ("toLocaleString", FuncModel(
      name = "Number.prototype.toLocaleString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO valueOf
    ("valueOf", FuncModel(
      name = "Number.prototype.valueOf",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toFixed
    ("toFixed", FuncModel(
      name = "Number.prototype.toFixed",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO toExponential
    ("toExponential", FuncModel(
      name = "Number.prototype.toExponential",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO toPrecision
    ("toPrecision", FuncModel(
      name = "Number.prototype.toPrecision",
      code = EmptyCode(argLen = 1)
    ), T, F, T)
  )
)
