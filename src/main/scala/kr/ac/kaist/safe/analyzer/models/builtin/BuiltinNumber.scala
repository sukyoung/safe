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

import kr.ac.kaist.safe.analyzer.domain.IClass
import kr.ac.kaist.safe.analyzer.models._

// TODO Number
object BuiltinNumber extends FuncModel(
  name = "Number",
  // TODO @function
  code = EmptyCode(argLen = 1),
  props = List(
    // the largest positive finite value of the Number type, which is approximately 1.7976931348623157*10^308
    NormalProp("MAX_VALUE", PrimModel(Double.MaxValue), F, F, F),
    // the smallest positive value of the Number type, which is approximately 5*10^-324
    NormalProp("MIN_VALUE", PrimModel(Double.MinPositiveValue), F, F, F),
    NormalProp("NaN", PrimModel(Double.NaN), F, F, F),
    NormalProp("NEGATIVE_INFINITY", PrimModel(Double.NegativeInfinity), F, F, F),
    NormalProp("POSITIVE_INFINITY", PrimModel(Double.PositiveInfinity), F, F, F)
  ),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinNumberProto, F, F, F))
)

object BuiltinNumberProto extends ObjModel(
  name = "Number.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Number")),

    // TODO toString
    NormalProp("toString", FuncModel(
      name = "Number.prototype.toString",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO toLocaleString
    NormalProp("toLocaleString", FuncModel(
      name = "Number.prototype.toLocaleString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO valueOf
    NormalProp("valueOf", FuncModel(
      name = "Number.prototype.valueOf",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toFixed
    NormalProp("toFixed", FuncModel(
      name = "Number.prototype.toFixed",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO toExponential
    NormalProp("toExponential", FuncModel(
      name = "Number.prototype.toExponential",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO toPrecision
    NormalProp("toPrecision", FuncModel(
      name = "Number.prototype.toPrecision",
      code = EmptyCode(argLen = 1)
    ), T, F, T)
  )
)
