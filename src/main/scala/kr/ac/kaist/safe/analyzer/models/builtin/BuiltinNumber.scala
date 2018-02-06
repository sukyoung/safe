/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.models.builtin

import scala.collection.immutable.HashSet
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.util._

object BuiltinNumberHelper {
  val instanceASite = PredAllocSite("Number<instance>")

  def typeConvert(args: AbsValue, st: AbsState): AbsNum = {
    val h = st.heap
    val argV = Helper.propLoad(args, Set(AbsStr("0")), h)
    val argL = Helper.propLoad(args, Set(AbsStr("length")), h).pvalue.numval
    val emptyN =
      if (AbsNum(0) ⊑ argL) AbsNum(0)
      else AbsNum.Bot
    TypeConversionHelper.ToNumber(argV) ⊔ emptyN
  }

  def getValue(thisV: AbsValue, h: AbsHeap): AbsNum = {
    thisV.pvalue.numval ⊔ thisV.locset.foldLeft(AbsNum.Bot)((res, loc) => {
      if ((AbsStr("Number") ⊑ h.get(loc)(IClass).value.pvalue.strval))
        res ⊔ h.get(loc)(IPrimitiveValue).value.pvalue.numval
      else res
    })
  }

  val constructor = BasicCode(
    argLen = 1,
    asiteSet = HashSet(instanceASite),
    code = (args: AbsValue, st: AbsState) => {
      val num = typeConvert(args, st)
      val loc = Loc(instanceASite)
      val state = st.oldify(loc)
      val heap = state.heap.update(loc, AbsObj.newNumberObj(num))
      (AbsState(heap, state.context), AbsState.Bot, AbsValue(loc))
    }
  )

  val typeConversion = PureCode(argLen = 1, code = typeConvert)
}

// 15.7 Number Objects
object BuiltinNumber extends FuncModel(
  name = "Number",

  // 15.7.1 The Number Constructor Called as a Function
  // 15.7.1.1 Number( [value] )
  code = BuiltinNumberHelper.typeConversion,

  // 15.7.2 The Number Constructor
  // 15.7.2.1 new Number ( [ value ] )
  construct = Some(BuiltinNumberHelper.constructor),

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
      code = BuiltinNumberHelper.constructor
    ), T, F, T),

    // 15.7.4.2 Number.prototype.toString ( [ radix ] )
    NormalProp("toString", FuncModel(
      name = "Number.prototype.toString",
      code = BasicCode(argLen = 1, code = (
        args: AbsValue, st: AbsState
      ) => {
        val h = st.heap
        val thisV = st.context.thisBinding
        var excSet = BuiltinHelper.checkExn(h, thisV, "Number")

        // The optional radix should be an integer value in the inclusive range 2 to 36.
        val argV = Helper.propLoad(args, Set(AbsStr("0")), h)
        val argL = Helper.propLoad(args, Set(AbsStr("length")), h).pvalue.numval
        // If radix not present or is undefined the Number 10 is used as the value of radix.
        val emptyN =
          if (AbsNum(0) ⊑ argL || AbsUndef.Top ⊑ argV) AbsNum(10)
          else AbsNum.Bot
        val radix = TypeConversionHelper.ToInteger(argV) ⊔ emptyN

        // If ToInteger(radix) is not an integer between 2 and 36 inclusive
        // throw a RangeError exception.
        if (AbsBool.True ⊑ Helper.bopGreater(radix, AbsNum(36)) ||
          AbsBool.True ⊑ Helper.bopLess(radix, AbsNum(2))) {
          excSet += RangeError
        }

        // If ToInteger(radix) is the Number 10
        // then this Number value is given as an argument to the ToString abstract operation;
        // the resulting String value is returned.
        val n = BuiltinNumberHelper.getValue(thisV, h)
        val s = {
          val b = TypeConversionHelper.SameValue(h, AbsNum(10), radix)
          val t =
            if (AT ⊑ b) {
              TypeConversionHelper.ToString(n)
            } else AbsStr.Bot
          val f =
            if (AF ⊑ b) {
              // If ToInteger(radix) is an integer from 2 to 36, but not 10,
              // XXX: give up the precision! (Room for the analysis precision improvement!)
              AbsStr.Top
            } else AbsStr.Bot
          t ⊔ f
        }
        (st, st.raiseException(excSet), AbsValue(s))
      })
    ), T, F, T),

    // 15.7.4.3 Number.prototype.toLocaleString()
    NormalProp("toLocaleString", FuncModel(
      name = "Number.prototype.toLocaleString",
      code = BasicCode(argLen = 0, code = (
        args: AbsValue, st: AbsState
      ) => {
        val h = st.heap
        // Produces a String value that represents this Number value formatted
        // according to the conventions of the host-dependent, and it is permissible,
        // but not encouraged, for it to return the same thing as toString.
        val thisV = st.context.thisBinding
        val excSet = BuiltinHelper.checkExn(h, thisV, "Number")
        val s = TypeConversionHelper.ToString(BuiltinNumberHelper.getValue(thisV, h))
        (st, st.raiseException(excSet), AbsValue(s))
      })
    ), T, F, T),

    // 15.7.4.4 Number.prototype.valueOf()
    NormalProp("valueOf", FuncModel(
      name = "Number.prototype.valueOf",
      code = BasicCode(argLen = 0, code = (
        args: AbsValue, st: AbsState
      ) => {
        val h = st.heap
        // If the "this" value is not a Number or a Number object,
        // throws a TypeError exception.
        val thisV = st.context.thisBinding
        val excSet = BuiltinHelper.checkExn(h, thisV, "Number")
        // Otherwise, returns the Number value.
        val n = BuiltinNumberHelper.getValue(thisV, h)
        (st, st.raiseException(excSet), AbsValue(n))
      })
    ), T, F, T),

    // 15.7.4.5 Number.prototype.toFixed(fractionDigits)
    NormalProp("toFixed", FuncModel(
      name = "Number.prototype.toFixed",
      code = BasicCode(argLen = 1, code = (
        args: AbsValue, st: AbsState
      ) => {
        val h = st.heap
        val thisV = st.context.thisBinding
        var excSet = BuiltinHelper.checkExn(h, thisV, "Number")
        // 1. Let f be ToInteger(fractionDigits).
        // (If fractionDigits is undefined, this step produces the value 0).
        val frac = Helper.propLoad(args, Set(AbsStr("0")), h)
        val undefV = (if (AbsUndef.Top ⊑ frac) AbsNum(0) else AbsNum.Bot)
        val f = TypeConversionHelper.ToInteger(frac) ⊔ undefV
        // 2. If f < 0 or f > 20, throw a RangeError exception.
        if (AbsBool.True ⊑ Helper.bopGreater(f, AbsNum(20)) ||
          AbsBool.True ⊑ Helper.bopLess(f, AbsNum(0))) {
          excSet += RangeError
        }
        // XXX: give up the precision! (Room for the analysis precision improvement!)
        (st, st.raiseException(excSet), AbsValue(AbsStr.Top))
      })
    ), T, F, T),

    // 15.7.4.6 Number.prototype.toExponential (fractionDigits)
    NormalProp("toExponential", FuncModel(
      name = "Number.prototype.toExponential",
      code = BasicCode(argLen = 1, code = (
        args: AbsValue, st: AbsState
      ) => {
        val h = st.heap
        val thisV = st.context.thisBinding
        var excSet = BuiltinHelper.checkExn(h, thisV, "Number")
        // 1. Let x be this Number value.
        val x = BuiltinNumberHelper.getValue(thisV, h)
        // 2. Let f be ToInteger(fractionDigits).
        val argV = Helper.propLoad(args, Set(AbsStr("0")), h)
        val f = TypeConversionHelper.ToInteger(argV)
        // 7. If fractionDigits is not undefined and (f < 0 or f > 20), throw a RangeError exception.
        if ((!argV.locset.isBottom || !argV.pvalue.copy(undefval = AbsUndef.Bot).isBottom) &&
          (AbsBool.True ⊑ (
            Helper.bopLess(f, AbsNum(0)).pvalue.boolval ||
            Helper.bopGreater(f, AbsNum(20)).pvalue.boolval
          )))
          excSet += RangeError

        // XXX: give up the precision! (Room for the analysis precision improvement!)
        (st, st.raiseException(excSet), AbsValue(AbsStr.Top))
      })
    ), T, F, T),

    // 15.7.4.7 Number.prototype.toPrecision(precision)
    NormalProp("toPrecision", FuncModel(
      name = "Number.prototype.toPrecision",
      code = BasicCode(argLen = 1, code = (
        args: AbsValue, st: AbsState
      ) => {
        val h = st.heap
        val thisV = st.context.thisBinding
        var excSet = BuiltinHelper.checkExn(h, thisV, "Number")
        val argV = Helper.propLoad(args, Set(AbsStr("0")), h)
        // 3. Let p be ToInteger(precision).
        val p = TypeConversionHelper.ToInteger(argV)
        // 8. If p < 1 or p > 21, throw a RangeError exception.
        if (AbsBool.True ⊑ Helper.bopGreater(p, AbsNum(21)) ||
          AbsBool.True ⊑ Helper.bopLess(p, AbsNum(1))) {
          excSet += RangeError
        }
        // XXX: give up the precision! (Room for the analysis precision improvement!)
        (st, st.raiseException(excSet), AbsValue(AbsStr.Top))
      })
    ), T, F, T)
  )
)
