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
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.util.SystemAddr
import scala.collection.immutable.HashSet

object BuiltinArray extends FuncModel(
  name = "Array",
  props = List(
    // TODO isArray
    NormalProp("isArray", FuncModel(
      name = "Array.isArray",
      code = EmptyCode(argLen = 1)
    ), T, F, T)
  ),
  // TODO @function
  code = EmptyCode(argLen = 1),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinArrayProto, F, F, F))
)

object BuiltinArrayProto extends ObjModel(
  name = "Array.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Array")),
    NormalProp("length", PrimModel(0.0), T, F, T),

    // TODO toString
    NormalProp("toString", FuncModel(
      name = "Array.prototype.toString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toLocaleString
    NormalProp("toLocaleString", FuncModel(
      name = "Array.prototype.toLocaleString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO concat
    NormalProp("concat", FuncModel(
      name = "Array.prototype.concat",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO join
    NormalProp("join", FuncModel(
      name = "Array.prototype.join",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO pop
    NormalProp("pop", FuncModel(
      name = "Array.prototype.pop",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO push
    NormalProp("push", FuncModel(
      name = "Array.prototype.push",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO reverse
    NormalProp("reverse", FuncModel(
      name = "Array.prototype.reverse",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // 15.4.4.9 Array.prototype.shift()
    NormalProp("shift", FuncModel(
      name = "Array.prototype.shift",
      code = BasicCode(argLen = 0, BuiltinArrayHelper.shift)
    ), T, F, T),

    // TODO slice
    NormalProp("slice", FuncModel(
      name = "Array.prototype.slice",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO sort
    NormalProp("sort", FuncModel(
      name = "Array.prototype.sort",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO splice
    NormalProp("splice", FuncModel(
      name = "Array.prototype.splice",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO unshift
    NormalProp("unshift", FuncModel(
      name = "Array.prototype.unshift",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO indexOf
    NormalProp("indexOf", FuncModel(
      name = "Array.prototype.indexOf",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO lastIndexOf
    NormalProp("lastIndexOf", FuncModel(
      name = "Array.prototype.lastIndexOf",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO every
    NormalProp("every", FuncModel(
      name = "Array.prototype.every",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO some
    NormalProp("some", FuncModel(
      name = "Array.prototype.some",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO forEach
    NormalProp("forEach", FuncModel(
      name = "Array.prototype.forEach",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO map
    NormalProp("map", FuncModel(
      name = "Array.prototype.map",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO filter
    NormalProp("filter", FuncModel(
      name = "Array.prototype.filter",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO reduce
    NormalProp("reduce", FuncModel(
      name = "Array.prototype.reduce",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO reduceRight
    NormalProp("reduceRight", FuncModel(
      name = "Array.prototype.reduceRight",
      code = EmptyCode(argLen = 1)
    ), T, F, T)
  )
)

object BuiltinArrayHelper {
  ////////////////////////////////////////////////////////////////
  // Array
  ////////////////////////////////////////////////////////////////

  ////////////////////////////////////////////////////////////////
  // Array.prototype
  ////////////////////////////////////////////////////////////////
  def shift(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val thisLoc = st.context.thisBinding
    val (retH, retV, excSet) = thisLoc.foldLeft((h, AbsValue.Bot, ExcSetEmpty)) {
      case ((h, value, excSet), loc) => {
        // XXX: 1. Let O be the result of calling ToObject passing the this value as the argument.
        // TODO current "this" value only have location. we should change!
        // 2. Let lenVal be the result of calling the [[Get]] internal method of O with argument "length".
        val obj = h.get(loc)
        val lenVal = obj.Get("length", h)
        // 3. Let len be ToUint32(lenVal).
        val len = TypeConversionHelper.ToUInt32(lenVal)
        val (retObj: AbsObject, retV: AbsValue, retExcSet: Set[Exception]) = len.getSingle match {
          case ConZero() => (AbsObjectUtil.Bot, AbsValue.Bot, ExcSetEmpty)
          // 4. If len is zero, then
          case ConOne(Num(0)) => {
            // a. Call the [[Put]] internal method of O with arguments "length", 0, and true.
            val (retObj, retExcSet) = obj.Put(AbsString("length"), AbsValue(0), true, h)
            // b. Return undefined.
            (retObj, AbsValue(AbsUndef.Top), retExcSet)
          }
          case ConOne(Num(n)) => {
            val len = n.toInt
            // 5. Let first be the result of calling the [[Get]] internal method of O with argument "0".
            val first = obj.Get("0", h)
            // 6. Let k be 1.
            // 7. Repeat, while k < len
            var excSet = ExcSetEmpty
            val retObj = (1 until len).foldLeft(obj)((obj, k) => {
              // a. Let from be ToString(k).
              val from = AbsString(k.toString)
              // b. Let to be ToString(k–1).
              val to = AbsString((k - 1).toString)
              // c. Let fromPresent be the result of calling the [[HasProperty]] internal method of O with argument from.
              val fromPresent = obj.HasProperty(from, h)
              val trueV = if (AbsBool.True <= fromPresent) {
                // d. If fromPresent is true, then
                // i. Let fromVal be the result of calling the [[Get]] internal method of O with argument from.
                val fromVal = obj.Get(from, h)
                // ii. Call the [[Put]] internal method of O with arguments to, fromVal, and true.
                val (retObj, retExcSet) = obj.Put(to, fromVal, true, h)
                excSet ++= retExcSet
                retObj
              } else AbsObjectUtil.Bot
              val falseV = if (AbsBool.False <= fromPresent) {
                // e. Else, fromPresent is false
                // i. Call the [[Delete]] internal method of O with arguments to and true.
                val (retObj, _) = obj.Delete(to) //XXX: missing second argument Throw = true.
                // f. Increase k by 1.
                retObj
              } else AbsObjectUtil.Bot
              trueV + falseV
            })
            // 8. Call the [[Delete]] internal method of O with arguments ToString(len–1) and true.
            val (delObj, _) = retObj.Delete(AbsString((len - 1).toString)) //XXX: missing second argument Throw = true.
            // 9. Call the [[Put]] internal method of O with arguments "length", (len–1) , and true.
            val (putObj, putExcSet) = delObj.Put(AbsString("length"), AbsNumber(len - 1), true, h)
            // 10. Return first.
            val retExcSet = excSet ++ putExcSet
            (putObj, first, retExcSet)
          }
          // XXX: very imprecise ConMany case
          case ConMany() => (obj.update(AbsString.Top, AbsDataProp.Top), AbsValue.Top, HashSet(TypeError))
        }
        val retH = h.update(loc, retObj)
        (retH, value + retV, excSet ++ retExcSet)
      }
    }
    val excSt = st.raiseException(excSet)
    (State(retH, st.context), excSt, retV)
  }
}
