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

  // 15.4.1.1 Array([item1[, item2[, ... ]]])
  code = BasicCode(argLen = 1, BuiltinArrayHelper.construct),

  // 15.4.2.1 new Array([item0[, item1[, ... ]]])
  // 15.4.2.2 new Array(len)
  construct = Some(BasicCode(argLen = 1, BuiltinArrayHelper.construct)),

  // 15.4.3.1 Array.prototype
  protoModel = Some((BuiltinArrayProto, F, F, F)),

  props = List(
    // 15.4.3.2 Array.isArray(arg)
    NormalProp("isArray", FuncModel(
      name = "Array.isArray",
      code = PureCode(argLen = 1, BuiltinArrayHelper.isArray)
    ), T, F, T)
  )
)

object BuiltinArrayProto extends ObjModel(
  name = "Array.prototype",
  props = List(
    InternalProp(IClass, PrimModel("Array")),
    NormalProp("length", PrimModel(0.0), T, F, T),

    // 15.4.4.2 Array.prototype.toString()
    NormalProp("toString", FuncModel(
      name = "Array.prototype.toString",
      code = PureCode(argLen = 0, BuiltinArrayHelper.toString)
    ), T, F, T),

    // 15.4.4.3 Array.prototype.toLocaleString()
    NormalProp("toLocaleString", FuncModel(
      name = "Array.prototype.toLocaleString",
      // TODO unsound!!: not following ECMAScript spec
      code = PureCode(argLen = 0, BuiltinArrayHelper.toString)
    ), T, F, T),

    // 15.4.4.4 Array.prototype.concat([item1[, item2[, ... ]]])
    NormalProp("concat", FuncModel(
      name = "Array.prototype.concat",
      code = BasicCode(argLen = 1, BuiltinArrayHelper.concat)
    ), T, F, T),

    // 15.4.4.5 Array.prototype.join(separator)
    NormalProp("join", FuncModel(
      name = "Array.prototype.join",
      code = PureCode(argLen = 1, BuiltinArrayHelper.join)
    ), T, F, T),

    // 15.4.4.6 Array.prototype.pop()
    NormalProp("pop", FuncModel(
      name = "Array.prototype.pop",
      code = BasicCode(argLen = 0, BuiltinArrayHelper.pop)
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

    // 15.4.4.10 Array.prototype.slice(start, end)
    NormalProp("slice", FuncModel(
      name = "Array.prototype.slice",
      code = BasicCode(argLen = 2, BuiltinArrayHelper.slice)
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
  def construct(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val length = Helper.propLoad(args, Set(AbsString("length")), h).pvalue.numval
    val first = Helper.propLoad(args, Set(AbsString("0")), h)
    val argObj = h.get(args.locset)
    val AT = AbsBool.True
    val (retObj: AbsObject, retExcSet: Set[Exception]) = length.getSingle match {
      case ConZero() => (AbsObjectUtil.Bot, ExcSetEmpty)
      case ConOne(Num(1)) => {
        // 15.4.2.2 new Array(len)
        val firstN = first.pvalue.numval
        val (lenObj: AbsObject, excSet: Set[Exception]) = if (!firstN.isBottom) {
          // If the argument len is a Number and ToUint32(len) is equal to len,
          // then the length property of the newly constructed object is set to ToUint32(len).
          val equal = (firstN === firstN.toUInt32)
          val trueV = if (AbsBool.True <= equal) {
            AbsObjectUtil.newArrayObject(firstN)
          } else AbsObjectUtil.Bot
          // If the argument len is a Number and ToUint32(len) is not equal to len,
          // a RangeError exception is thrown.
          val falseV =
            if (AbsBool.False <= equal) HashSet(RangeError)
            else ExcSetEmpty
          (trueV, falseV)
        } else (AbsObjectUtil.Bot, ExcSetEmpty)

        val otherObj = if (!first.pvalue.copyWith(numval = AbsNumber.Bot).isBottom || !first.locset.isBottom) {
          // If the argument len is not a Number, then the length property of the newly constructed object
          // is set to 1 and the 0 property of the newly constructed object is set to len with attributes
          // {[[Writable]]: true, [[Enumerable]]: true, [[Configurable]]: true}.
          val arr = AbsObjectUtil.newArrayObject(AbsNumber(1))
          val dp = AbsDataProp(first, AT, AT, AT)
          arr.initializeUpdate("0", dp)
        } else AbsObjectUtil.Bot

        (lenObj + otherObj, excSet)
      }
      case ConOne(Num(n)) => {
        // 15.4.2.1 new Array([item0[, item1[, ... ]]])
        val length = n.toInt
        val arr = AbsObjectUtil.newArrayObject(AbsNumber(length))
        val obj = (0 until length).foldLeft(arr)((arr, k) => {
          val kStr = k.toString
          val kValue = argObj(kStr).value
          val dp = AbsDataProp(kValue, AT, AT, AT)
          arr.initializeUpdate(kStr, dp)
        })
        (obj, ExcSetEmpty)
      }
      case ConMany() => {
        val len = first.pvalue.numval + length
        val arr = AbsObjectUtil.newArrayObject(len)
        val aKeySet = argObj.amap.abstractKeySet((aKey, _) => aKey <= AbsString.Number)
        val arrObj = aKeySet.foldLeft(arr)((arr, aKey) => {
          val value = argObj(aKey).value
          val dp = AbsDataProp(value, AT, AT, AT)
          arr.update(aKey, dp)
        })
        (arrObj, HashSet(RangeError))
      }
    }
    val arrAddr = SystemAddr("Array<instance>")
    val state = st.oldify(arrAddr)
    val arrLoc = Loc(arrAddr, Recent)
    val retH = state.heap.update(arrLoc, retObj)
    val excSt = state.raiseException(retExcSet)
    (State(retH, state.context), excSt, AbsLoc(arrLoc))
  }

  def isArray(args: AbsValue, st: State): AbsValue = {
    val h = st.heap
    val arg = Helper.propLoad(args, Set(AbsString("0")), h)
    // 1. If Type(arg) is not Object, return false.
    val noObjB =
      if (arg.pvalue.isBottom) AbsBool.Bot
      else AbsBool.False
    val obj = h.get(arg.locset)
    // 2. If the value of the [[Class]] internal property of arg is "Array", then return true.
    // 3. Return false.
    val arrB = obj(IClass).value.pvalue.strval === AbsString("Array")
    noObjB + arrB
  }

  ////////////////////////////////////////////////////////////////
  // Array.prototype
  ////////////////////////////////////////////////////////////////
  def toString(args: AbsValue, st: State): AbsValue = {
    val h = st.heap
    // XXX: 1. Let array be the result of calling ToObject on the this value.
    // TODO current "this" value only have location. we should change!
    val thisLoc = st.context.thisBinding
    val array = h.get(thisLoc)
    // TODO: it is unsound: we should call locale "join" function instead of Array.prototype.join.
    // 2. Let func be the result of calling the [[Get]] internal method of array with argument "join".
    // 3. If IsCallable(func) is false, then let func be the standard built-in method Object.prototype.toString (15.2.4.2).
    // 4. Return the result of calling the [[Call]] internal method of func providing array as the this value and an
    //    empty arguments list.
    val tempArr = SystemAddr("<temp>")
    val tempLoc = Loc(tempArr, Recent)
    val newArgs = AbsObjectUtil.newArgObject()
    val tempH = h.update(tempLoc, newArgs)
    val tempSt = State(tempH, st.context)
    join(AbsLoc(tempLoc), tempSt)
  }

  def concat(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val argObj = h.get(args.locset)
    val length = Helper.propLoad(args, Set(AbsString("length")), h).pvalue.numval
    val thisLoc = st.context.thisBinding
    val thisObj = h.get(thisLoc)
    val AT = (AbsBool.True, AbsAbsent.Bot)
    val Bot = AbsObjectUtil.Bot
    val Top = AbsObjectUtil
      .newArrayObject(AbsNumber.Top)
      .update(AbsString.Number, AbsDataProp.Top)
    val retObj: AbsObject = length.getSingle match {
      case ConZero() => Bot
      case ConOne(Num(n)) => {
        val argLen = n.toInt
        val thisLength = thisObj.Get("length", h).pvalue.numval
        thisLength.getSingle match {
          case ConZero() => Bot
          case ConOne(Num(n)) => {
            val thisLen = n.toInt
            val initList = (0 until thisLen).foldLeft[List[AbsValue]](Nil)((lst, k) => {
              thisObj.Get(k.toString, h) :: lst
            })
            val vlOpt = (0 until argLen).foldLeft[Option[List[AbsValue]]](Some(initList)) {
              case (None, _) => None
              case (Some(lst), k) => {
                val kValue = argObj.Get(k.toString, h)
                val (normalLoc, arrLoc) = kValue.locset.foldLeft((kValue.locset, AbsLoc.Bot)) {
                  case ((normal, array), loc) => {
                    val obj = h.get(loc)
                    val clsName = obj(IClass).value.pvalue.strval
                    val isArr = clsName === AbsString("Array")
                    if (AbsBool.True <= isArr) (normal - loc, array + loc)
                    else (normal, array)
                  }
                }
                val arrObj = h.get(arrLoc)
                val subLen = arrObj.Get("length", h).pvalue.numval
                subLen.getSingle match {
                  case ConZero() => Some(AbsValue(kValue.pvalue, normalLoc) :: lst)
                  case ConOne(Num(n)) => if (normalLoc.isBottom && kValue.pvalue.isBottom) {
                    val subLen = n.toInt
                    Some((0 until subLen).foldLeft(lst)((lst, k) => {
                      arrObj.Get(k.toString, h) :: lst
                    }))
                  } else None
                  case ConMany() => None
                }
              }
            }
            vlOpt match {
              case None => Top
              case Some(valueList) => {
                val finalLen = valueList.length
                val arr = AbsObjectUtil.newArrayObject(AbsNumber(finalLen))
                valueList.reverse.zipWithIndex.foldLeft(arr) {
                  case (arr, (value, idx)) => {
                    val desc = AbsDesc((value, AbsAbsent.Bot), AT, AT, AT)
                    val (newArr, _, _) = arr.DefineOwnProperty(AbsString(idx.toString), desc, false)
                    newArr
                  }
                }
              }
            }
          }
          case ConMany() => Top
        }
      }
      case ConMany() => Top
    }
    val arrAddr = SystemAddr("Array.prototype.concat<array>")
    val state = st.oldify(arrAddr)
    val arrLoc = Loc(arrAddr, Recent)
    val retH = state.heap.update(arrLoc, retObj)
    (State(retH, state.context), State.Bot, AbsLoc(arrLoc))
  }

  def join(args: AbsValue, st: State): AbsValue = {
    val h = st.heap
    val thisLoc = st.context.thisBinding
    val separator = Helper.propLoad(args, Set(AbsString("0")), h)
    thisLoc.foldLeft(AbsString.Bot)((str, loc) => {
      // XXX: 1. Let O be the result of calling ToObject passing the this value as the argument.
      // TODO current "this" value only have location. we should change!
      val obj = h.get(loc)
      // 2. Let lenVal be the result of calling the [[Get]] internal method of O with argument "length".
      val lenVal = obj.Get("length", h)
      // 3. Let len be ToUint32(lenVal).
      val len = TypeConversionHelper.ToUInt32(lenVal)
      // 4. If separator is undefined, let separator be the single-character String ",".
      val noUndef = AbsValue(separator.pvalue.copyWith(undefval = AbsUndef.Bot), separator.locset)
      val undefV: AbsValue =
        if (separator.pvalue.undefval.isTop) AbsString(",")
        else AbsValue.Bot
      val newSep = noUndef + undefV
      // 5. Let sep be ToString(separator).
      val sep = TypeConversionHelper.ToString(newSep)
      len.getSingle match {
        case ConZero() => AbsString.Bot
        // 6. If len is zero, return the empty String.
        case ConOne(Num(0)) => AbsString("")
        case ConOne(Num(l)) => {
          val len = l.toInt
          def noUndefNull(value: AbsValue): AbsString = {
            val empty =
              if (value.pvalue.undefval.isBottom && value.pvalue.nullval.isBottom) AbsString.Bot
              else AbsString("")
            val noUNPV = value.pvalue.copyWith(undefval = AbsUndef.Bot, nullval = AbsNull.Bot)
            val other = TypeConversionHelper.ToString(AbsValue(noUNPV, value.locset))
            empty + other
          }
          // 7. Let element0 be the result of calling the [[Get]] internal method of O with argument "0".
          val element0 = obj.Get("0", h)
          // 8. If element0 is undefined or null, let R be the empty String; otherwise, Let R be ToString(element0).
          val R = noUndefNull(element0)
          // 9. Let k be1.
          // 10. Repeat, while k < len
          (1 until len).foldLeft(R)((R, k) => {
            // a. Let S be the String value produced by concatenating R and sep.
            val S = R concat sep
            // b. Let element be the result of calling the [[Get]] internal method of O with argument ToString(k).
            val element = obj.Get(k.toString, h)
            // c. If element is undefined or null, Let next be the empty String; otherwise, let next be
            // ToString(element).
            val next = noUndefNull(element)
            // d. Let R be a String value produced by concatenating S and next.
            S concat next
            // e. Increase k by 1.
          })
          // 11. Return R.
        }
        case ConMany() => AbsString.Top
      }
    })
  }

  def pop(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val thisLoc = st.context.thisBinding
    val (retH, retV, excSet) = thisLoc.foldLeft((h, AbsValue.Bot, ExcSetEmpty)) {
      case ((h, value, excSet), loc) => {
        // XXX: 1. Let O be the result of calling ToObject passing the this value as the argument.
        // TODO current "this" value only have location. we should change!
        val arr = h.get(loc)
        // 2. Let lenVal be the result of calling the [[Get]] internal method of O with argument "length".
        val lenVal = arr.Get("length", h)
        // 3. Let len be ToUint32(lenVal).
        val len = TypeConversionHelper.ToUInt32(lenVal)
        val (retObj: AbsObject, retV: AbsValue, retExcSet: Set[Exception]) = len.getSingle match {
          case ConZero() => (AbsObjectUtil.Bot, AbsValue.Bot, ExcSetEmpty)
          // 4. If len is zero,
          case ConOne(Num(0)) => {
            // a. Call the [[Put]] internal method of O with arguments "length", 0, and true.
            val (retArr, excSet) = arr.Put(AbsString("length"), AbsNumber(0), true, h)
            // b. Return undefined.
            (retArr, AbsValue(AbsUndef.Top), excSet)
          }
          // 5. Else, len > 0
          case ConOne(Num(n)) => {
            val len = n.toInt
            // a. Let indx be ToString(len–1).
            val indx = (len - 1).toString
            // b. Let element be the result of calling the [[Get]] internal method of O with argument indx.
            val element = arr.Get(indx, h)
            // c. Call the [[Delete]] internal method of O with arguments indx and true.
            val (delArr, _) = arr.Delete(indx) // XXX: missing second argument Throw = true.
            // d. Call the [[Put]] internal method of O with arguments "length", indx, and true.
            val (putArr, excSet) = delArr.Put(AbsString("length"), AbsNumber(len - 1), true, h)
            // e. Return element.
            (putArr, element, excSet)
          }
          // XXX: very imprecise ConMany case
          case ConMany() => (arr.update(AbsString.Number, AbsDataProp.Top), AbsValue.Top, HashSet(TypeError))
        }
        val retH = h.update(loc, retObj)
        (retH, value + retV, excSet ++ retExcSet)
      }
    }
    val excSt = st.raiseException(excSet)
    (State(retH, st.context), excSt, retV)
  }

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
          case ConMany() => (obj.update(AbsString.Number, AbsDataProp.Top), AbsValue.Top, HashSet(TypeError))
        }
        val retH = h.update(loc, retObj)
        (retH, value + retV, excSet ++ retExcSet)
      }
    }
    val excSt = st.raiseException(excSet)
    (State(retH, st.context), excSt, retV)
  }

  def slice(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val thisLoc = st.context.thisBinding
    val start = Helper.propLoad(args, Set(AbsString("0")), h)
    val end = Helper.propLoad(args, Set(AbsString("1")), h)

    // XXX: 1. Let O be the result of calling ToObject passing the this value as the argument.
    // TODO current "this" value only have location. we should change!
    val obj = h.get(thisLoc)
    // 2. Let A be a new array created as if by the expression new Array().
    val arr = AbsObjectUtil.newArrayObject()
    // 3. Let lenVal be the result of calling the [[Get]] internal method of O with argument "length".
    val lenVal = obj.Get("length", h)
    // 4. Let len be ToUint32(lenVal).
    val len = TypeConversionHelper.ToUInt32(lenVal)
    // 5. Let relativeStart be ToInteger(start).
    val relativeStart = TypeConversionHelper.ToInteger(start)
    // 6. If end is undefined, let relativeEnd be len; else let relativeEnd be ToInteger(end).
    val undefLen =
      if (end.pvalue.undefval.isBottom) AbsNumber.Bot
      else len
    val numLen =
      if (end.pvalue.copyWith(undefval = AbsUndef.Bot).isBottom && end.locset.isBottom) AbsNumber.Bot
      else TypeConversionHelper.ToInteger(end)
    val relativeEnd = undefLen + numLen
    val (retObj: AbsObject, retExcSet: Set[Exception]) = (len.getSingle, relativeStart.getSingle, relativeEnd.getSingle) match {
      case (ConZero(), _, _) | (_, ConZero(), _) | (_, _, ConZero()) => (AbsObjectUtil.Bot, ExcSetEmpty)
      case (ConOne(Num(l)), ConOne(Num(from)), ConOne(Num(to))) => {
        val len = l.toInt
        val relativeStart = from.toInt
        val relativeEnd = to.toInt
        def toU(num: Int): Int =
          if (num < 0) Math.max((len + num), 0)
          else Math.min(num, len)
        // 7. If relativeStart is negative, let k be max((len + relativeStart),0); else let k be min(relativeStart, len).
        val k = toU(relativeStart)
        // 8. If relativeEnd is negative, let final be max((len + relativeEnd),0); else let final be min(relativeEnd, len).
        val finalN = toU(relativeEnd)
        // 9. Let n be 0.
        // 10. Repeat, while k < final
        val start = k
        // XXX: It is not in the spec: but it is needed because we did not modeling the aliasing of 'length' for Array obects.
        val length =
          if (start > finalN) 0
          else finalN - start
        val (initArr, _) = arr.Put(AbsString("length"), AbsNumber(length), false, h)
        (start until finalN).foldLeft((initArr, ExcSetEmpty)) {
          case ((arr, excSet), k) => {
            val n = k - start
            // a. Let Pk be ToString(k).
            val Pk = AbsString(k.toString)
            // b. Let kPresent be the result of calling the [[HasProperty]] internal method of O with argument Pk.
            val kPresent = obj.HasProperty(Pk, h)
            // c. If kPresent is true, then
            val (retObj, retExcSet) = if (AbsBool.True <= kPresent) {
              // i. Let kValue be the result of calling the [[Get]] internal method of O with argument Pk.
              val kValue = obj.Get(Pk, h)
              // ii. Call the [[DefineOwnProperty]] internal method of A with arguments ToString(n), Property Descriptor
              //     {[[Value]]: kValue, [[Writable]]: true, [[Enumerable]]: true, [[Configurable]]: true}, and false.
              val AT = (AbsBool.True, AbsAbsent.Bot)
              val desc = AbsDesc((kValue, AbsAbsent.Bot), AT, AT, AT)
              val (retObj, _, excSet) = arr.DefineOwnProperty(AbsString(n.toString), desc, false)
              (retObj, excSet)
            } else (AbsObjectUtil.Bot, ExcSetEmpty)
            val falseObj = if (AbsBool.False <= kPresent) obj else AbsObjectUtil.Bot
            // d. Increase k by 1.
            // e. Increase n by 1.
            (retObj + falseObj, excSet ++ retExcSet)
          }
        }
      }
      case _ => (arr.update(AbsString.Top, AbsDataProp.Top), HashSet(TypeError))
    }
    // 11. Return A.
    val arrAddr = SystemAddr("Array.prototype.slice<array>")
    val state = st.oldify(arrAddr)
    val arrLoc = Loc(arrAddr, Recent)
    val retH = state.heap.update(arrLoc, retObj)
    val excSt = state.raiseException(retExcSet)
    (State(retH, state.context), excSt, AbsLoc(arrLoc))
  }
}
