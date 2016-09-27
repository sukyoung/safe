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
import kr.ac.kaist.safe.analyzer.domain.{ IClass, IPrimitiveValue, IPrototype }
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.util.SystemAddr

object BuiltinDateHelper {
  val constructor = BasicCode(argLen = 1, code = (
    args: AbsValue, st: State
  ) => {
    val h = st.heap
    val addr = SystemAddr("Date<instance>")
    val state = st.oldify(addr)
    val loc = Loc(addr, Recent)
    val newObj = AbsObjectUtil.newObject(BuiltinDateProto.loc)
    val argL = Helper.propLoad(args, Set(AbsString("length")), h).pvalue.numval
    val absNum = argL.getSingle match {
      // 15.9.3.2 new Date(value)
      case ConOne(Num(n)) if n == 1 =>
        // 1. Let v be ToPrimitive(value).
        val v = TypeConversionHelper.ToPrimitive(Helper.propLoad(args, Set(AbsString("0")), h))
        // 2. If Type(v) is String, then
        AbsBool(AbsString("string") <= TypeConversionHelper.typeTag(v, h)).map[AbsNumber](
          //   a. Parse v as a date, in exactly the same manner as for the parse method (15.9.4.2);
          //      let V be the time value for this date.
          // XXX: give up the precision! (Room for the analysis precision improvement!)
          thenV = AbsNumber.Top,
          // 3. Else, let V be ToNumber(v).
          elseV = TypeConversionHelper.ToNumber(v)
        // 4. Set the [[PrimitiveValue]] internal property of the newly constructed object
        // to TimeClip(V) and return.
        )(AbsNumber)
      // 15.9.3.1 new Date( year, month [, date [, hours [, minutes [, seconds [, ms ]]]]] )
      // 15.9.3.3 new Date()
      // XXX: give up the precision! (Room for the analysis precision improvement!)
      case _ => AbsNumber.Top
    }
    val newObj2 = newObj
      .update(IClass, InternalValueUtil(AbsString("Date")))
      .update(IPrimitiveValue, InternalValueUtil(absNum))
    val heap = state.heap.update(loc, newObj2)
    (State(heap, state.context), State.Bot, AbsValue(loc))
  })
}

// 15.9 Date Objects
object BuiltinDate extends FuncModel(
  name = "Date",

  // 15.9.2 The Date Constructor Called as a Function
  // 15.9.2.1 Date ([year [, month [, date [, hours [, minutes [, seconds [, ms]]]]]]])
  code = PureCode(argLen = 7, code = (args: AbsValue, st: State) => AbsString.Top),

  // 15.9.2 The Date Constructor
  construct = Some(BuiltinDateHelper.constructor),

  // 15.9.4.1 Date.prototype
  protoModel = Some((BuiltinDateProto, F, F, F)),

  props = List(
    // TODO parse
    NormalProp("parse", FuncModel(
      name = "Date.parse",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO UTC
    NormalProp("UTC", FuncModel(
      name = "Date.UTC",
      code = EmptyCode(argLen = 7)
    ), T, F, T),

    // 15.9.4.4 Date.now()
    NormalProp("now", FuncModel(
      name = "Date.now",
      code = PureCode(
        argLen = 0,
        code = (args: AbsValue, st: State) => AbsNumber.Top
      )
    ), T, F, T)
  )
)

object BuiltinDateProto extends ObjModel(
  name = "Date.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Date")),
    InternalProp(IPrototype, PrimModel(Double.NaN)),

    // TODO toString
    NormalProp("toString", FuncModel(
      name = "Date.prototype.toString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toDateString
    NormalProp("toDateString", FuncModel(
      name = "Date.prototype.toDateString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toTimeString
    NormalProp("toTimeString", FuncModel(
      name = "Date.prototype.toTimeString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toLocaleString
    NormalProp("toLocaleString", FuncModel(
      name = "Date.prototype.toLocaleString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toLocaleDateString
    NormalProp("toLocaleDateString", FuncModel(
      name = "Date.prototype.toLocaleDateString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toLocaleTimeString
    NormalProp("toLocaleTimeString", FuncModel(
      name = "Date.prototype.toLocaleTimeString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO valueOf
    NormalProp("valueOf", FuncModel(
      name = "Date.prototype.valueOf",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getTime
    NormalProp("getTime", FuncModel(
      name = "Date.prototype.getTime",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getFullYear
    NormalProp("getFullYear", FuncModel(
      name = "Date.prototype.getFullYear",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getUTCFullYear
    NormalProp("getUTCFullYear", FuncModel(
      name = "Date.prototype.getUTCFullYear",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getMonth
    NormalProp("getMonth", FuncModel(
      name = "Date.prototype.getMonth",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getUTCMonth
    NormalProp("getUTCMonth", FuncModel(
      name = "Date.prototype.getUTCMonth",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getDate
    NormalProp("getDate", FuncModel(
      name = "Date.prototype.getDate",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getUTCDate
    NormalProp("getUTCDate", FuncModel(
      name = "Date.prototype.getUTCDate",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getDay
    NormalProp("getDay", FuncModel(
      name = "Date.prototype.getDay",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getUTCDay
    NormalProp("getUTCDay", FuncModel(
      name = "Date.prototype.getUTCDay",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getHours
    NormalProp("getHours", FuncModel(
      name = "Date.prototype.getUTCDay",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getUTCHours
    NormalProp("getUTCHours", FuncModel(
      name = "Date.prototype.getUTCHours",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getMinutes
    NormalProp("getMinutes", FuncModel(
      name = "Date.prototype.getMinutes",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getUTCMinutes
    NormalProp("getUTCMinutes", FuncModel(
      name = "Date.prototype.getUTCMinutes",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getSeconds
    NormalProp("getSeconds", FuncModel(
      name = "Date.prototype.getSeconds",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getUTCSeconds
    NormalProp("getUTCSeconds", FuncModel(
      name = "Date.prototype.getUTCSeconds",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getMilliseconds
    NormalProp("getMilliseconds", FuncModel(
      name = "Date.prototype.getMilliseconds",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getUTCMilliseconds
    NormalProp("getUTCMilliseconds", FuncModel(
      name = "Date.prototype.getUTCMilliseconds",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO getTimezoneOffset
    NormalProp("getTimezoneOffset", FuncModel(
      name = "Date.prototype.getTimezoneOffset",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO setTime
    NormalProp("setTime", FuncModel(
      name = "Date.prototype.setTime",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO setMilliseconds
    NormalProp("setMilliseconds", FuncModel(
      name = "Date.prototype.setMilliseconds",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO setUTCMilliseconds
    NormalProp("setUTCMilliseconds", FuncModel(
      name = "Date.prototype.setUTCMilliseconds",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO setSeconds
    NormalProp("setSeconds", FuncModel(
      name = "Date.prototype.setSeconds",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO setUTCSeconds
    NormalProp("setUTCSeconds", FuncModel(
      name = "Date.prototype.setUTCSeconds",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO setMinutes
    NormalProp("setMinutes", FuncModel(
      name = "Date.prototype.setMinutes",
      code = EmptyCode(argLen = 3)
    ), T, F, T),

    // TODO setUTCMinutes
    NormalProp("setUTCMinutes", FuncModel(
      name = "Date.prototype.setUTCMinutes",
      code = EmptyCode(argLen = 3)
    ), T, F, T),

    // TODO setHours
    NormalProp("setHours", FuncModel(
      name = "Date.prototype.setHours",
      code = EmptyCode(argLen = 4)
    ), T, F, T),

    // TODO setUTCHours
    NormalProp("setUTCHours", FuncModel(
      name = "Date.prototype.setUTCHours",
      code = EmptyCode(argLen = 4)
    ), T, F, T),

    // TODO setDate
    NormalProp("setDate", FuncModel(
      name = "Date.prototype.setDate",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO setUTCDate
    NormalProp("setUTCDate", FuncModel(
      name = "Date.prototype.setUTCDate",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO setMonth
    NormalProp("setMonth", FuncModel(
      name = "Date.prototype.setMonth",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO setUTCMonth
    NormalProp("setUTCMonth", FuncModel(
      name = "Date.prototype.setUTCMonth",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO setFullYear
    NormalProp("setFullYear", FuncModel(
      name = "Date.prototype.setFullYear",
      code = EmptyCode(argLen = 3)
    ), T, F, T),

    // TODO setUTCFullYear
    NormalProp("setUTCFullYear", FuncModel(
      name = "Date.prototype.setUTCFullYear",
      code = EmptyCode(argLen = 3)
    ), T, F, T),

    // TODO toUTCString
    NormalProp("toUTCString", FuncModel(
      name = "Date.prototype.toUTCString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toISOString
    NormalProp("toISOString", FuncModel(
      name = "Date.prototype.toISOString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toJSON
    NormalProp("toJSON", FuncModel(
      name = "Date.prototype.toJSON",
      code = EmptyCode(argLen = 1)
    ), T, F, T)
  )
)
