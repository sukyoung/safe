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
  val typeConversion = PureCode(argLen = 1, code = (
    args, h
  ) => {
    val resV = Helper.propLoad(args, Set(AbsString("length")), h)
    resV.pvalue.numval.getSingle match {
      // If value is not supplied, +0 is returned.
      case ConOne(Num(num)) if num == 0 =>
        AbsValue(+0)
      // Returns a Number value computed by ToNumber(value).
      case ConOne(Num(num)) =>
        val res = Helper.propLoad(args, Set(AbsString("0")), h)
        AbsValue(TypeConversionHelper.ToNumber(res))
      case _ => AbsNumber.Bot
    }
  })

  def checkExn(h: Heap, absValue: AbsValue, clsName: String): HashSet[Exception] = {
    val exist = absValue.locset.exists(loc => {
      val v = h.get(loc)(IClass).value.pvalue.strval
      (AbsString(clsName) <= v && v <= AbsString(clsName)) || v <= AbsString.Bot
    })
    if (exist)
      HashSet[Exception](TypeError)
    else
      HashSet[Exception]()
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
  construct = Some(BasicCode(argLen = 1, code = (
    args: AbsValue, st: State
  ) => {
    val h = st.heap
    val argV = Helper.propLoad(args, Set(AbsString("0")), h)
    val argL = Helper.propLoad(args, Set(AbsString("length")), h).pvalue.numval
    val addr = SystemAddr("Number<instance>")

    val state = st.oldify(addr)
    val loc = Loc(addr, Recent)
    val num = if (argL <= AbsNumber(0)) AbsNumber(0)
    else TypeConversionHelper.ToNumber(argV)
    val heap = state.heap.update(loc, AbsObjectUtil.newNumberObj(num))

    (State(heap, state.context), State.Bot, AbsValue(loc))
  })),

  // 15.7.3.1 Number.prototype
  protoModel = Some((BuiltinNumberProto, F, F, F)),

  props = List(
    InternalProp(IPrototype, BuiltinFunctionProto),

    NormalProp("length", PrimModel(1), F, F, F),

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
  // 15.7.4
  name = "Number.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Number")),

    InternalProp(IPrimitiveValue, PrimModel(0)),

    InternalProp(IPrototype, BuiltinObjectProto),

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
        val n = thisV.pvalue.numval +
          thisV.locset.foldLeft(AbsNumber.Bot)((res, loc) => {
            if ((AbsString("Number") <= h.get(loc)(IClass).value.pvalue.strval))
              h.get(loc)(IPrimitiveValue).value.pvalue.numval
            else res
          })
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
