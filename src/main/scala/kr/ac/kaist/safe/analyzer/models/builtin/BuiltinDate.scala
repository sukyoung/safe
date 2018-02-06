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

object BuiltinDateHelper {
  val instanceASite = PredAllocSite("Date<instance>")

  def getValue(thisV: AbsValue, h: AbsHeap): AbsNum = {
    thisV.pvalue.numval ⊔ thisV.locset.foldLeft[AbsNum](AbsNum.Bot)((res, loc) => {
      if ((AbsStr("Date") ⊑ h.get(loc)(IClass).value.pvalue.strval)) {
        res ⊔ h.get(loc)(IPrimitiveValue).value.pvalue.numval
      } else res
    })
  }

  val valueOf = BasicCode(argLen = 0, code = (
    args: AbsValue, st: AbsState
  ) => {
    val h = st.heap
    val thisV = st.context.thisBinding
    var excSet = BuiltinHelper.checkExn(h, thisV, "Date")
    val s = BuiltinDateHelper.getValue(thisV, h)
    (st, st.raiseException(excSet), AbsValue(s))
  })

  def timeClip(num: AbsNum): AbsNum = num.getSingle match {
    case ConZero() => AbsNum.Bot
    case ConOne(Num(n)) => n match {
      case n if n.isInfinity || n.isNaN => AbsNum.NaN
      case n if math.abs(n) > (8.64 * math.pow(10, 15)) => AbsNum.NaN
      // case -0.0 => AbsNum(0) XXX: implementation-dependent
      case _ => TypeConversionHelper.ToInteger(num)
    }
    case ConMany() => AbsNum.Top
  }

  val constructor = BasicCode(
    argLen = 1,
    asiteSet = HashSet(instanceASite),
    code = (args: AbsValue, st: AbsState) => {
      val h = st.heap
      val loc = Loc(instanceASite)
      val state = st.oldify(loc)
      val newObj = AbsObj.newObject(BuiltinDateProto.loc)
      val argL = Helper.propLoad(args, Set(AbsStr("length")), h).pvalue.numval
      val absNum = argL.getSingle match {
        // 15.9.3.2 new Date(value)
        case ConOne(Num(n)) if n == 1 =>
          // 1. Let v be ToPrimitive(value).
          val v = TypeConversionHelper.ToPrimitive(Helper.propLoad(args, Set(AbsStr("0")), h))
          // 2. If Type(v) is String, then
          val b = AbsBool(AbsStr("string") ⊑ TypeConversionHelper.typeTag(v, h))
          val t =
            if (AT ⊑ b) {
              //   a. Parse v as a date, in exactly the same manner as for the parse method (15.9.4.2);
              //      let V be the time value for this date.
              // XXX: give up the precision! (Room for the analysis precision improvement!)
              AbsNum.Top
            } else AbsNum.Bot
          val f =
            if (AF ⊑ b) {
              // 3. Else, let V be ToNumber(v).
              timeClip(TypeConversionHelper.ToNumber(v))
            } else AbsNum.Bot
          // 4. Set the [[PrimitiveValue]] internal property of the newly constructed object
          // to TimeClip(V) and return.
          t ⊔ f
        // 15.9.3.1 new Date( year, month [, date [, hours [, minutes [, seconds [, ms ]]]]] )
        // 15.9.3.3 new Date()
        // XXX: give up the precision! (Room for the analysis precision improvement!)
        case _ => AbsNum.Top
      }
      val newObj2 = newObj
        .update(IClass, AbsIValue(AbsStr("Date")))
        .update(IPrimitiveValue, AbsIValue(absNum))
      val heap = state.heap.update(loc, newObj2)
      (AbsState(heap, state.context), AbsState.Bot, AbsValue(loc))
    }
  )

  val getNumber = BasicCode(argLen = 0, code = (
    args: AbsValue, st: AbsState
  ) => {
    val h = st.heap
    val thisV = st.context.thisBinding
    var excSet = BuiltinHelper.checkExn(h, thisV, "Date")
    // 1. Let t be this time value.
    val time = getValue(thisV, h)
    // 2. If t is NaN, return NaN.
    // 3. Return ...
    // XXX: give up the precision! (Room for the analysis precision improvement!)
    val b = BuiltinHelper.isNaN(time)
    val t =
      if (AT ⊑ b) {
        AbsNum.NaN
      } else AbsNum.Bot
    val f =
      if (AF ⊑ b) {
        AbsNum.Top
      } else AbsNum.Bot
    val res = t ⊔ f
    (st, st.raiseException(excSet), AbsValue(res))
  })

  def setNumber(n: Int): BasicCode = BasicCode(
    argLen = n,
    asiteSet = HashSet(instanceASite),
    code = (args: AbsValue, st: AbsState) => {
      val h = st.heap
      val loc = Loc(instanceASite)
      val state = st.oldify(loc)
      val thisV = state.context.thisBinding
      var excSet = BuiltinHelper.checkExn(h, thisV, "Date")
      // XXX: give up the precision! (Room for the analysis precision improvement!)
      // Set the [[PrimitiveValue]] internal property of this Date object to v.
      val v = AbsNum.Top
      val retH = h.update(loc, h.get(loc).update(IPrimitiveValue, AbsIValue(v)))
      (AbsState(retH, state.context), state.raiseException(excSet), AbsValue(v))
    }
  )
}

// 15.9 Date Objects
object BuiltinDate extends FuncModel(
  name = "Date",

  // 15.9.2 The Date Constructor Called as a Function
  // 15.9.2.1 Date ([year [, month [, date [, hours [, minutes [, seconds [, ms]]]]]]])
  code = PureCode(argLen = 7, code = (args: AbsValue, st: AbsState) => AbsStr.Top),

  // 15.9.2 The Date Constructor
  construct = Some(BuiltinDateHelper.constructor),

  // 15.9.4.1 Date.prototype
  protoModel = Some((BuiltinDateProto, F, F, F)),

  props = List(
    // 15.9.4.2 Date.parse(string)
    // XXX: give up the precision! (Room for the analysis precision improvement!)
    NormalProp("parse", FuncModel(
      name = "Date.parse",
      code = PureCode(
        argLen = 1,
        code = (args: AbsValue, st: AbsState) => AbsNum.Top
      )
    ), T, F, T),

    // 15.9.4.3 Date.UTC(year, month [, date [, hours [, minutes [, seconds [, ms ]]]]])
    // XXX: give up the precision! (Room for the analysis precision improvement!)
    NormalProp("UTC", FuncModel(
      name = "Date.UTC",
      code = PureCode(
        argLen = 7,
        code = (args: AbsValue, st: AbsState) => AbsNum.Top
      )
    ), T, F, T),

    // 15.9.4.4 Date.now()
    // XXX: give up the precision! (Room for the analysis precision improvement!)
    NormalProp("now", FuncModel(
      name = "Date.now",
      code = PureCode(
        argLen = 0,
        code = (args: AbsValue, st: AbsState) => AbsNum.Top
      )
    ), T, F, T)
  )
)

object BuiltinDateProto extends ObjModel(
  name = "Date.prototype",

  // 15.9.5 Properties of the Date Prototype Object
  props = List(
    InternalProp(IClass, PrimModel("Date")),

    InternalProp(IPrimitiveValue, PrimModel(Double.NaN)),

    // 15.9.5.1 Date.prototype.constructor
    NormalProp("constructor", FuncModel(
      name = "Date.prototype.constructor",
      code = BuiltinDateHelper.constructor
    ), T, F, T),

    // 15.9.5.2 Date.prototype.toString()
    NormalProp("toString", FuncModel(
      name = "Date.prototype.toString",
      code = PureCode(
        argLen = 0,
        code = (args: AbsValue, st: AbsState) => AbsStr.Top
      )
    ), T, F, T),

    // 15.9.5.3 Date.prototype.toDateString()
    NormalProp("toDateString", FuncModel(
      name = "Date.prototype.toDateString",
      code = PureCode(
        argLen = 0,
        code = (args: AbsValue, st: AbsState) => AbsStr.Top
      )
    ), T, F, T),

    // 15.9.5.4 Date.prototype.toTimeString()
    NormalProp("toTimeString", FuncModel(
      name = "Date.prototype.toTimeString",
      code = PureCode(
        argLen = 0,
        code = (args: AbsValue, st: AbsState) => AbsStr.Top
      )
    ), T, F, T),

    // 15.9.5.5 Date.prototype.toLocaleString()
    NormalProp("toLocaleString", FuncModel(
      name = "Date.prototype.toLocaleString",
      code = PureCode(
        argLen = 0,
        code = (args: AbsValue, st: AbsState) => AbsStr.Top
      )
    ), T, F, T),

    // 15.9.5.6 Date.prototype.toLocaleDateString()
    NormalProp("toLocaleDateString", FuncModel(
      name = "Date.prototype.toLocaleDateString",
      code = PureCode(
        argLen = 0,
        code = (args: AbsValue, st: AbsState) => AbsStr.Top
      )
    ), T, F, T),

    // 15.9.5.7 Date.prototype.toLocaleTimeString()
    NormalProp("toLocaleTimeString", FuncModel(
      name = "Date.prototype.toLocaleTimeString",
      code = PureCode(
        argLen = 0,
        code = (args: AbsValue, st: AbsState) => AbsStr.Top
      )
    ), T, F, T),

    // 15.9.5.8 Date.prototype.valueOf()
    NormalProp("valueOf", FuncModel(
      name = "Date.prototype.valueOf",
      code = BuiltinDateHelper.valueOf
    ), T, F, T),

    // 15.9.5.9 Date.prototype.getTime()
    NormalProp("getTime", FuncModel(
      name = "Date.prototype.getTime",
      code = BuiltinDateHelper.valueOf
    ), T, F, T),

    // 15.9.5.10 Date.prototype.getFullYear()
    NormalProp("getFullYear", FuncModel(
      name = "Date.prototype.getFullYear",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.11 Date.prototype.getUTCFullYear()
    NormalProp("getUTCFullYear", FuncModel(
      name = "Date.prototype.getUTCFullYear",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.12 Date.prototype.getMonth()
    NormalProp("getMonth", FuncModel(
      name = "Date.prototype.getMonth",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.13 Date.prototype.getUTCMonth()
    NormalProp("getUTCMonth", FuncModel(
      name = "Date.prototype.getUTCMonth",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.14 Date.prototype.getDate()
    NormalProp("getDate", FuncModel(
      name = "Date.prototype.getDate",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.15 Date.prototype.getUTCDate()
    NormalProp("getUTCDate", FuncModel(
      name = "Date.prototype.getUTCDate",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.16 Date.prototype.getDay()
    NormalProp("getDay", FuncModel(
      name = "Date.prototype.getDay",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.17 Date.prototype.getUTCDay()
    NormalProp("getUTCDay", FuncModel(
      name = "Date.prototype.getUTCDay",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.18 Date.prototype.getHours()
    NormalProp("getHours", FuncModel(
      name = "Date.prototype.getHours",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.19 Date.prototype.getUTCHours()
    NormalProp("getUTCHours", FuncModel(
      name = "Date.prototype.getUTCHours",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.20 Date.prototype.getMinutes()
    NormalProp("getMinutes", FuncModel(
      name = "Date.prototype.getMinutes",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.21 Date.prototype.getUTCMinutes()
    NormalProp("getUTCMinutes", FuncModel(
      name = "Date.prototype.getUTCMinutes",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.22 Date.prototype.getSeconds()
    NormalProp("getSeconds", FuncModel(
      name = "Date.prototype.getSeconds",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.23 Date.prototype.getUTCSeconds()
    NormalProp("getUTCSeconds", FuncModel(
      name = "Date.prototype.getUTCSeconds",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.24 Date.prototype.getMilliseconds()
    NormalProp("getMilliseconds", FuncModel(
      name = "Date.prototype.getMilliseconds",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.25 Date.prototype.getUTCMilliseconds()
    NormalProp("getUTCMilliseconds", FuncModel(
      name = "Date.prototype.getUTCMilliseconds",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.26 Date.prototype.getTimezoneOffset()
    NormalProp("getTimezoneOffset", FuncModel(
      name = "Date.prototype.getTimezoneOffset",
      code = BuiltinDateHelper.getNumber
    ), T, F, T),

    // 15.9.5.27 Date.prototype.setTime(time)
    NormalProp("setTime", FuncModel(
      name = "Date.prototype.setTime",
      code = BuiltinDateHelper.setNumber(1)
    ), T, F, T),

    // 15.9.5.28 Date.prototype.setMilliseconds(ms)
    NormalProp("setMilliseconds", FuncModel(
      name = "Date.prototype.setMilliseconds",
      code = BuiltinDateHelper.setNumber(1)
    ), T, F, T),

    // 15.9.5.29 Date.prototype.setUTCMilliseconds(ms)
    NormalProp("setUTCMilliseconds", FuncModel(
      name = "Date.prototype.setUTCMilliseconds",
      code = BuiltinDateHelper.setNumber(1)
    ), T, F, T),

    // 15.9.5.30 Date.prototype.setSeconds(sec [, ms ])
    NormalProp("setSeconds", FuncModel(
      name = "Date.prototype.setSeconds",
      code = BuiltinDateHelper.setNumber(2)
    ), T, F, T),

    // 15.9.5.31 Date.prototype.setUTCSeconds(sec [, ms ])
    NormalProp("setUTCSeconds", FuncModel(
      name = "Date.prototype.setUTCSeconds",
      code = BuiltinDateHelper.setNumber(2)
    ), T, F, T),

    // 15.9.5.32 Date.prototype.setMinutes(min [, sec [, ms ]])
    NormalProp("setMinutes", FuncModel(
      name = "Date.prototype.setMinutes",
      code = BuiltinDateHelper.setNumber(3)
    ), T, F, T),

    // 15.9.5.33 Date.prototype.setUTCMinutes(min [, sec [, ms ]])
    NormalProp("setUTCMinutes", FuncModel(
      name = "Date.prototype.setUTCMinutes",
      code = BuiltinDateHelper.setNumber(3)
    ), T, F, T),

    // 15.9.5.34 Date.prototype.setHours(hour [, min [, sec [, ms ]]])
    NormalProp("setHours", FuncModel(
      name = "Date.prototype.setHours",
      code = BuiltinDateHelper.setNumber(4)
    ), T, F, T),

    // 15.9.5.35 Date.prototype.setUTCHours(hour [, min [, sec [, ms ]]])
    NormalProp("setUTCHours", FuncModel(
      name = "Date.prototype.setUTCHours",
      code = BuiltinDateHelper.setNumber(4)
    ), T, F, T),

    // 15.9.5.36 Date.prototype.setDate(date)
    NormalProp("setDate", FuncModel(
      name = "Date.prototype.setDate",
      code = BuiltinDateHelper.setNumber(1)
    ), T, F, T),

    // 15.9.5.37 Date.prototype.setUTCDate(date)
    NormalProp("setUTCDate", FuncModel(
      name = "Date.prototype.setUTCDate",
      code = BuiltinDateHelper.setNumber(1)
    ), T, F, T),

    // 15.9.5.38 Date.prototype.setMonth(month [, date ])
    NormalProp("setMonth", FuncModel(
      name = "Date.prototype.setMonth",
      code = BuiltinDateHelper.setNumber(2)
    ), T, F, T),

    // 15.9.5.39 Date.prototype.setUTCMonth(month [, date ])
    NormalProp("setUTCMonth", FuncModel(
      name = "Date.prototype.setUTCMonth",
      code = BuiltinDateHelper.setNumber(2)
    ), T, F, T),

    // 15.9.5.40 Date.prototype.setFullYear(year [, month [, date ]])
    NormalProp("setFullYear", FuncModel(
      name = "Date.prototype.setFullYear",
      code = BuiltinDateHelper.setNumber(3)
    ), T, F, T),

    // 15.9.5.41 Date.prototype.setUTCFullYear(year [, month [, date ]])
    NormalProp("setUTCFullYear", FuncModel(
      name = "Date.prototype.setUTCFullYear",
      code = BuiltinDateHelper.setNumber(3)
    ), T, F, T),

    // 15.9.5.42 Date.prototype.toUTCString()
    NormalProp("toUTCString", FuncModel(
      name = "Date.prototype.toUTCString",
      code = PureCode(
        argLen = 0,
        code = (args: AbsValue, st: AbsState) => AbsStr.Top
      )
    ), T, F, T),

    // 15.9.5.43 Date.prototype.toISOString()
    NormalProp("toISOString", FuncModel(
      name = "Date.prototype.toISOString",
      code = BasicCode(argLen = 0, code = (
        args: AbsValue, st: AbsState
      ) => {
        val h = st.heap
        val thisV = st.context.thisBinding
        var excSet = BuiltinHelper.checkExn(h, thisV, "Date")
        val v = BuiltinDateHelper.getValue(thisV, h)
        // If the time value of this object is not a finite Number a RangeError exception is thrown.
        v.gamma match {
          case ConInf => excSet += RangeError
          case _ =>
        }
        if (!thisV.pvalue.isBottom) excSet += TypeError
        (st, st.raiseException(excSet), AbsValue(AbsStr.Top))
      })
    ), T, F, T),

    // TODO toJSON
    NormalProp("toJSON", FuncModel(
      name = "Date.prototype.toJSON",
      code = EmptyCode(argLen = 1)
    ), T, F, T)
  )
)
