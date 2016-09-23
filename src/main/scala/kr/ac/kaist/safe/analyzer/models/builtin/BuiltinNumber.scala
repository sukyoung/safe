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
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.analyzer._

// 15.7 Number
object BuiltinNumber extends FuncModel(
  name = "Number",
  // 15.7.1 The Number Constructor Called as a Function
  // 15.7.1.1 Number( [value] )
  code = PureCode(argLen = 1, (args, h) => {
    val resV = Helper.propLoad(args, Set(AbsString("length")), h)
    resV.pvalue.numval.getSingle match {
      // If value is not supplied, +0 is returned.
      case ConOne(Num(num)) if num == 0 =>
        AbsValue(+0)
      // Returns a Number value computed by ToNumber(value).
      case ConOne(Num(num)) =>
        val resV = Helper.propLoad(args, Set(AbsString("0")), h)
        AbsValue(TypeConversionHelper.ToNumber(resV))
      case _ => AbsNumber.Bot
    }
  }),
  props = List(
    // 15.7.3.2 Number.MAX_VALUE
    // the largest positive finite value of the Number type, which is approximately 1.7976931348623157*10^308
    NormalProp("MAX_VALUE", PrimModel(Double.MaxValue), F, F, F),
    // 15.7.3.3 Number.MIN_VALUE
    // the smallest positive value of the Number type, which is approximately 5*10^-324
    NormalProp("MIN_VALUE", PrimModel(Double.MinPositiveValue), F, F, F),
    // 15.7.3.4 Number.NaN
    NormalProp("NaN", PrimModel(Double.NaN), F, F, F),
    // 15.7.3.5 Number.NEGATIVE_INFINITY
    NormalProp("NEGATIVE_INFINITY", PrimModel(Double.NegativeInfinity), F, F, F),
    // 15.7.3.6 Number.POSITIVE_INFINITY
    NormalProp("POSITIVE_INFINITY", PrimModel(Double.PositiveInfinity), F, F, F)
  ),
  // TODO @construct
  construct = Some(EmptyCode()),

  // 15.7.3.1 Number.prototype
  protoModel = Some((BuiltinNumberProto, F, F, F))
)

object BuiltinNumberProto extends ObjModel(
  // 15.7.4
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
