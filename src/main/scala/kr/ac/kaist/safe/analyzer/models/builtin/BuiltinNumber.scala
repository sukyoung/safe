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

import scala.collection.immutable.HashSet
import kr.ac.kaist.safe.analyzer.domain.IClass
import kr.ac.kaist.safe.analyzer.domain.IPrimitiveValue
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.util.SystemAddr

object BuiltinNumberUtil {
  val constructor = BasicCode(argLen = 1, code = (
    args: AbsValue, st: State
  ) => {
    val h = st.heap
    val argV = Helper.propLoad(args, Set(AbsString("0")), h)
    val argL = Helper.propLoad(args, Set(AbsString("length")), h).pvalue.numval
    val addr = SystemAddr("Number<instance>")

    val state = st.oldify(addr)
    val loc = Loc(addr, Recent)
    val emptyN =
      if (AbsNumber(0) <= argL) AbsNumber(0)
      else AbsNumber.Bot
    val num = TypeConversionHelper.ToNumber(argV) + emptyN
    val heap = state.heap.update(loc, AbsObjectUtil.newNumberObj(num))

    (State(heap, state.context), State.Bot, AbsValue(loc))
  })

  val typeConversion = PureCode(argLen = 1, code = (
    args: AbsValue, h: Heap
  ) => {
    val argV = Helper.propLoad(args, Set(AbsString("0")), h)
    val argL = Helper.propLoad(args, Set(AbsString("length")), h).pvalue.numval
    val emptyN =
      if (AbsNumber(0) <= argL) AbsNumber(0)
      else AbsNumber.Bot
    TypeConversionHelper.ToNumber(argV) + emptyN
  })

  def checkExn(h: Heap, absValue: AbsValue, clsName: String): HashSet[Exception] = {
    val exist = absValue.locset.foldLeft(AbsBool.Bot)((b, loc) => {
      b + (h.get(loc)(IClass).value.pvalue.strval === AbsString(clsName))
    })
    if (AbsBool.False <= exist) HashSet[Exception](TypeError)
    else HashSet[Exception]()
  }

  def getValue(thisV: AbsValue, h: Heap): AbsNumber = {
    thisV.pvalue.numval + thisV.locset.foldLeft(AbsNumber.Bot)((res, loc) => {
      if ((AbsString("Number") <= h.get(loc)(IClass).value.pvalue.strval))
        res + h.get(loc)(IPrimitiveValue).value.pvalue.numval
      else res
    })
  }
}

// 15.7 Number Objects
object BuiltinNumber extends FuncModel(
  name = "Number",

  // 15.7.1 The Number Constructor Called as a Function
  // 15.7.1.1 Number( [value] )
  code = BuiltinNumberUtil.typeConversion,

  // 15.7.2 The Number Constructor
  // 15.7.2.1 new Number ( [ value ] )
  construct = Some(BuiltinNumberUtil.constructor),

  // 15.7.3.1 Number.prototype
  protoModel = Some((BuiltinNumberProto, F, F, F)),

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
  )
)

object BuiltinNumberProto extends ObjModel(
  name = "Number.prototype",

  // 15.7.4 Properties of the Number Prototype Object
  props = List(
    InternalProp(IClass, PrimModel("Number")),

    InternalProp(IPrimitiveValue, PrimModel(0)),

    // 15.7.4.1 Number.prototype.constructor
    NormalProp("constructor", FuncModel(
      name = "Number.prototype.constructor",
      code = BuiltinNumberUtil.constructor
    ), T, F, T),

    // 15.7.4.2 Number.prototype.toString ( [ radix ] )
    NormalProp("toString", FuncModel(
      name = "Number.prototype.toString",
      code = BasicCode(argLen = 1, (
        args: AbsValue, st: State
      ) => {
        val h = st.heap
        val thisV = AbsValue(st.context.thisBinding)
        var excSet = BuiltinNumberUtil.checkExn(h, thisV, "Number")

        // The optional radix should be an integer value in the inclusive range 2 to 36.
        val argV = Helper.propLoad(args, Set(AbsString("0")), h)
        val argL = Helper.propLoad(args, Set(AbsString("length")), h).pvalue.numval
        // If radix not present or is undefined the Number 10 is used as the value of radix.
        val radix = if (argL <= AbsNumber(0) || argV <= AbsUndef.Top) AbsNumber(10)
        else TypeConversionHelper.ToInteger(argV)

        // If ToInteger(radix) is not an integer between 2 and 36 inclusive
        // throw a RangeError exception.
        if (AbsBool.True <= Helper.bopGreater(radix, AbsNumber(36)) ||
          AbsBool.True <= Helper.bopLess(radix, AbsNumber(2))) {
          excSet += RangeError
        }

        // If ToInteger(radix) is the Number 10
        // then this Number value is given as an argument to the ToString abstract operation;
        // the resulting String value is returned.
        val n = BuiltinNumberUtil.getValue(thisV, h)
        val s =
          if (AbsNumber(10) <= radix) TypeConversionHelper.ToString(n)
          // If ToInteger(radix) is an integer from 2 to 36, but not 10,
          // XXX: give up the precision! (Room for the analysis precision improvement!)
          else AbsString.Top

        (st, st.raiseException(excSet), AbsValue(s))
      })
    ), T, F, T),

    // 15.7.4.3 Number.prototype.toLocaleString()
    NormalProp("toLocaleString", FuncModel(
      name = "Number.prototype.toLocaleString",
      code = BasicCode(argLen = 1, (
        args: AbsValue, st: State
      ) => {
        val h = st.heap
        // Produces a String value that represents this Number value formatted
        // according to the conventions of the host-dependent, and it is permissible,
        // but not encouraged, for it to return the same thing as toString.
        val thisV = AbsValue(st.context.thisBinding)
        val excSet = BuiltinNumberUtil.checkExn(h, thisV, "Number")
        val s = TypeConversionHelper.ToString(BuiltinNumberUtil.getValue(thisV, h))

        (st, st.raiseException(excSet), AbsValue(s))
      })
    ), T, F, T),

    // 15.7.4.4 Number.prototype.valueOf()
    NormalProp("valueOf", FuncModel(
      name = "Number.prototype.valueOf",
      code = BasicCode(argLen = 0, (
        args: AbsValue, st: State
      ) => {
        val h = st.heap
        // If the "this" value is not a Number or a Number object,
        // throws a TypeError exception.
        val thisV = AbsValue(st.context.thisBinding)
        val excSet = BuiltinNumberUtil.checkExn(h, thisV, "Number")
        // Otherwise, returns the Number value.
        val n = BuiltinNumberUtil.getValue(thisV, h)
        (st, st.raiseException(excSet), AbsValue(n))
      })
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
