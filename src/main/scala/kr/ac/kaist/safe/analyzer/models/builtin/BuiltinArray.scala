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
      code = BasicCode(argLen = 0, BuiltinArrayHelper.toString)
    ), T, F, T),

    // 15.4.4.3 Array.prototype.toLocaleString()
    NormalProp("toLocaleString", FuncModel(
      name = "Array.prototype.toLocaleString",
      // TODO unsound!!: not following ECMAScript spec
      code = BasicCode(argLen = 0, BuiltinArrayHelper.toString)
    ), T, F, T),

    // 15.4.4.4 Array.prototype.concat([item1[, item2[, ... ]]])
    NormalProp("concat", FuncModel(
      name = "Array.prototype.concat",
      code = BasicCode(argLen = 1, BuiltinArrayHelper.concat)
    ), T, F, T),

    // 15.4.4.5 Array.prototype.join(separator)
    NormalProp("join", FuncModel(
      name = "Array.prototype.join",
      code = BasicCode(argLen = 1, BuiltinArrayHelper.join)
    ), T, F, T),

    // 15.4.4.6 Array.prototype.pop()
    NormalProp("pop", FuncModel(
      name = "Array.prototype.pop",
      code = BasicCode(argLen = 0, BuiltinArrayHelper.pop)
    ), T, F, T),

    // 15.4.4.7 Array.prototype.push([item1[, item2[, ... ]]])
    NormalProp("push", FuncModel(
      name = "Array.prototype.push",
      code = BasicCode(argLen = 1, BuiltinArrayHelper.push)
    ), T, F, T),

    // 15.4.4.8 Array.prototype.reverse()
    NormalProp("reverse", FuncModel(
      name = "Array.prototype.reverse",
      code = BasicCode(argLen = 0, BuiltinArrayHelper.reverse)
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

    // 15.4.4.11 Array.prototype.sort(comparefn)
    // Modeled in src/main/resources/jsModels/__builtin__.js
    /*
    NormalProp("sort", FuncModel(
      name = "Array.prototype.sort",
      code = EmptyCode(argLen = 1)
    ), T, F, T),
    */

    // 15.4.4.12 Array.prototype.splice(start, deleteCount[, item1[, item2[, ... ]]])
    NormalProp("splice", FuncModel(
      name = "Array.prototype.splice",
      code = BasicCode(argLen = 2, BuiltinArrayHelper.splice)
    ), T, F, T),

    // 15.4.4.13 Array.prototype.unshift([item1[, item2[, ... ]]])
    NormalProp("unshift", FuncModel(
      name = "Array.prototype.unshift",
      code = BasicCode(argLen = 1, BuiltinArrayHelper.unshift)
    ), T, F, T),

    // 15.4.4.14 Array.prototype.indexOf(searchElement[, fromIndex])
    NormalProp("indexOf", FuncModel(
      name = "Array.prototype.indexOf",
      code = BasicCode(argLen = 1, BuiltinArrayHelper.indexOf)
    ), T, F, T),

    // 15.4.4.15 Array.prototype.lastIndexOf(searchElement[, fromIndex ])
    NormalProp("lastIndexOf", FuncModel(
      name = "Array.prototype.lastIndexOf",
      code = BasicCode(argLen = 1, BuiltinArrayHelper.lastIndexOf)
    ), T, F, T),

    // TODO 15.4.4.16 Array.prototype.every(callbackfn [, thisArg ])
    NormalProp("every", FuncModel(
      name = "Array.prototype.every",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO 15.4.4.17 Array.prototype.some(callbackfn [, thisArg ])
    NormalProp("some", FuncModel(
      name = "Array.prototype.some",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO 15.4.4.18 Array.prototype.forEach(callbackfn [, thisArg ])
    NormalProp("forEach", FuncModel(
      name = "Array.prototype.forEach",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO 15.4.4.19 Array.prototype.map(callbackfn [, thisArg ])
    NormalProp("map", FuncModel(
      name = "Array.prototype.map",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO 15.4.4.20 Array.prototype.filter(callbackfn [, thisArg ])
    NormalProp("filter", FuncModel(
      name = "Array.prototype.filter",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO 15.4.4.21 Array.prototype.reduce(callbackfn [, initialValue ])
    NormalProp("reduce", FuncModel(
      name = "Array.prototype.reduce",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO 15.4.4.22 Array.prototype.reduceRight(callbackfn [, initialValue ])
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
  def construct(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val length = Helper.propLoad(args, Set(AbsString("length")), h).pvalue.numval
    val first = Helper.propLoad(args, Set(AbsString("0")), h)
    val argObj = h.get(args.locset)
    val AT = AbsBool.True
    val (retObj: AbsObject, retExcSet: Set[Exception]) = length.getSingle match {
      case ConZero() => (AbsObject.Bot, ExcSetEmpty)
      case ConOne(Num(1)) => {
        // 15.4.2.2 new Array(len)
        val firstN = first.pvalue.numval
        val (lenObj: AbsObject, excSet: Set[Exception]) = if (!firstN.isBottom) {
          // If the argument len is a Number and ToUint32(len) is equal to len,
          // then the length property of the newly constructed object is set to ToUint32(len).
          val equal = (firstN === firstN.toUInt32)
          val trueV = if (AbsBool.True <= equal) {
            AbsObject.newArrayObject(firstN)
          } else AbsObject.Bot
          // If the argument len is a Number and ToUint32(len) is not equal to len,
          // a RangeError exception is thrown.
          val falseV =
            if (AbsBool.False <= equal) HashSet(RangeError)
            else ExcSetEmpty
          (trueV, falseV)
        } else (AbsObject.Bot, ExcSetEmpty)

        val otherObj = if (!first.pvalue.copyWith(numval = AbsNumber.Bot).isBottom || !first.locset.isBottom) {
          // If the argument len is not a Number, then the length property of the newly constructed object
          // is set to 1 and the 0 property of the newly constructed object is set to len with attributes
          // {[[Writable]]: true, [[Enumerable]]: true, [[Configurable]]: true}.
          val arr = AbsObject.newArrayObject(AbsNumber(1))
          val dp = AbsDataProp(first, AT, AT, AT)
          arr.initializeUpdate("0", dp)
        } else AbsObject.Bot

        (lenObj + otherObj, excSet)
      }
      case ConOne(Num(n)) => {
        // 15.4.2.1 new Array([item0[, item1[, ... ]]])
        val length = n.toInt
        val arr = AbsObject.newArrayObject(AbsNumber(length))
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
        val arr = AbsObject.newArrayObject(len)
        val aKeySet = argObj.abstractKeySet((aKey, _) => aKey <= AbsString.Number)
        val arrObj = aKeySet match {
          case ConInf() => AbsObject.Top
          case ConFin(set) => set.foldLeft(arr)((arr, aKey) => {
            val value = argObj(aKey).value
            val dp = AbsDataProp(value, AT, AT, AT)
            arr.update(aKey, dp)
          })
        }
        (arrObj, HashSet(RangeError))
      }
    }
    val arrAddr = SystemAddr("Array<instance>")
    val state = st.oldify(arrAddr)
    val arrLoc = Loc(arrAddr, Recent)
    val retH = state.heap.update(arrLoc, retObj.oldify(arrAddr))
    val excSt = state.raiseException(retExcSet)
    (AbsState(retH, state.context), excSt, AbsLoc(arrLoc))
  }

  def isArray(args: AbsValue, st: AbsState): AbsValue = {
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
  def toString(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    // 1. Let array be the result of calling ToObject on the this value.
    val addr = SystemAddr("Array.prototype.toString<object>")
    val thisBinding = st.context.thisBinding
    val (thisLoc, state, excSet) = TypeConversionHelper.ToObject(thisBinding, st, addr)
    val h = state.heap

    val array = h.get(thisLoc)
    // TODO: it is unsound: we should call locale "join" function instead of Array.prototype.join.
    // 2. Let func be the result of calling the [[Get]] internal method of array with argument "join".
    // 3. If IsCallable(func) is false, then let func be the standard built-in method Object.prototype.toString (15.2.4.2).
    // 4. Return the result of calling the [[Call]] internal method of func providing array as the this value and an
    //    empty arguments list.
    val tempArr = SystemAddr("<temp>")
    val tempLoc = Loc(tempArr, Recent)
    val newArgs = AbsObject.newArgObject()
    val tempH = h.update(tempLoc, newArgs)
    val tempSt = AbsState(tempH, state.context)
    val (joinSt, joinExcSt, joinV) = join(AbsLoc(tempLoc), tempSt)
    val excSt = st.raiseException(excSet)
    (joinSt, excSt + joinExcSt, joinV)
  }

  def concat(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val argObj = h.get(args.locset)
    val length = Helper.propLoad(args, Set(AbsString("length")), h).pvalue.numval
    val thisLoc = st.context.thisBinding.locset
    val thisObj = h.get(thisLoc)
    val AT = (AbsBool.True, AbsAbsent.Bot)
    val Bot = AbsObject.Bot
    val Top = AbsObject
      .newArrayObject(AbsNumber.Top)
      .update(AbsString.Number, AbsDataProp.Top)
    val retObj: AbsObject = length.getSingle match {
      case ConZero() => Bot
      case ConOne(Num(n)) => {
        val argLen = n.toInt
        val isArray = AbsString("Array") === thisObj(IClass).value.pvalue.strval
        val (arrList, arrIsBot): (Option[List[AbsValue]], Boolean) =
          if (AbsBool.True <= isArray) {
            val thisLength = thisObj.Get("length", h).pvalue.numval
            thisLength.getSingle match {
              case ConZero() => (None, true)
              case ConOne(Num(n)) => {
                val thisLen = n.toInt
                val initList = (0 until thisLen).foldLeft[List[AbsValue]](Nil)((lst, k) => {
                  thisObj.Get(k.toString, h) :: lst
                })
                (Some(initList), false)
              }
              case ConMany() => (None, false)
            }
          } else { (None, true) }
        val (objList, objIsBot): (Option[List[AbsValue]], Boolean) =
          if (AbsBool.False <= isArray) {
            (Some(List(AbsValue(thisLoc))), false)
          } else { (None, true) }
        if (!arrIsBot || !objIsBot) {
          val initList: Option[List[AbsValue]] =
            if (arrIsBot) objList
            else if (objIsBot) arrList
            else None
          val vlOpt = (0 until argLen).foldLeft[Option[List[AbsValue]]](initList) {
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
              val arr = AbsObject.newArrayObject(AbsNumber(finalLen))
              valueList.reverse.zipWithIndex.foldLeft(arr) {
                case (arr, (value, idx)) => {
                  val desc = AbsDesc((value, AbsAbsent.Bot), AT, AT, AT)
                  val (newArr, _, _) = arr.DefineOwnProperty(AbsString(idx.toString), desc, false)
                  newArr
                }
              }
            }
          }
        } else Bot
      }
      case ConMany() => Top
    }
    val arrAddr = SystemAddr("Array.prototype.concat<array>")
    val state = st.oldify(arrAddr)
    val arrLoc = Loc(arrAddr, Recent)
    val retH = state.heap.update(arrLoc, retObj.oldify(arrAddr))
    (AbsState(retH, state.context), AbsState.Bot, AbsLoc(arrLoc))
  }

  def join(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val separator = Helper.propLoad(args, Set(AbsString("0")), st.heap)
    // 1. Let O be the result of calling ToObject passing the this value as the argument.
    val addr = SystemAddr("Array.prototype.join<object>")
    val thisBinding = st.context.thisBinding
    val (thisLoc, state, excSet) = TypeConversionHelper.ToObject(thisBinding, st, addr)
    val h = state.heap
    val result = thisLoc.foldLeft(AbsString.Bot)((str, loc) => {
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
    val excSt = st.raiseException(excSet)
    (state, excSt, result)
  }

  def pop(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    // 1. Let O be the result of calling ToObject passing the this value as the argument.
    val addr = SystemAddr("Array.prototype.pop<object>")
    val thisBinding = st.context.thisBinding
    val (thisLoc, state, es) = TypeConversionHelper.ToObject(thisBinding, st, addr)
    val h = state.heap
    val (retH, retV, excSet) = thisLoc.foldLeft((h, AbsValue.Bot, es)) {
      case ((h, value, excSet), loc) => {
        val arr = h.get(loc)
        // 2. Let lenVal be the result of calling the [[Get]] internal method of O with argument "length".
        val lenVal = arr.Get("length", h)
        // 3. Let len be ToUint32(lenVal).
        val len = TypeConversionHelper.ToUInt32(lenVal)
        val (retObj: AbsObject, retV: AbsValue, retExcSet: Set[Exception]) = len.getSingle match {
          case ConZero() => (AbsObject.Bot, AbsValue.Bot, ExcSetEmpty)
          // 4. If len is zero,
          case ConOne(Num(0)) => {
            // a. Call the [[Put]] internal method of O with arguments "length", 0, and true.
            val (retArr, excSet) = arr.Put(AbsString("length"), AbsNumber(0), true, h)
            // b. Return undefined.
            (retArr, AbsValue(AbsUndef.Top), excSet)
          }
          // 5. Else, len > 0
          case ConOne(Num(n)) => {
            val len = n.toLong
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
          case ConMany() => (arr.update(AbsString.Number, AbsDataProp.Top).update(AbsString("length"), AbsDataProp.Top), AbsValue.Top, HashSet(TypeError))
        }
        val retH = h.update(loc, retObj)
        (retH, value + retV, excSet ++ retExcSet)
      }
    }
    val excSt = st.raiseException(excSet)
    (AbsState(retH, state.context), excSt, retV)
  }

  def push(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val argObj = st.heap.get(args.locset)
    val argLen = Helper.propLoad(args, Set(AbsString("length")), st.heap).pvalue.numval
    // 1. Let O be the result of calling ToObject passing the this value as the argument.
    val addr = SystemAddr("Array.prototype.push<object>")
    val thisBinding = st.context.thisBinding
    val (thisLoc, state, es) = TypeConversionHelper.ToObject(thisBinding, st, addr)
    val h = state.heap
    val (retH, retV, excSet) = thisLoc.foldLeft((h, AbsValue.Bot, es)) {
      case ((h, value, excSet), loc) => {
        val arr = h.get(loc)
        // 2. Let lenVal be the result of calling the [[Get]] internal method of O with argument "length".
        val lenVal = arr.Get("length", h)
        // 3. Let n be ToUint32(lenVal).
        val n = TypeConversionHelper.ToUInt32(lenVal)
        // 4. Let items be an internal List whose elements are, in left to right order, the arguments that were passed to this
        //    function invocation.
        val (retObj: AbsObject, retV: AbsValue, retExcSet: Set[Exception]) = (argLen.getSingle, n.getSingle) match {
          case (ConZero(), _) | (_, ConZero()) => (AbsObject.Bot, AbsValue.Bot, ExcSetEmpty)
          case (ConOne(Num(al)), ConOne(Num(tl))) => {
            val argLen = al.toInt
            val thisLen = tl.toInt
            // 5. Repeat, while items is not empty
            val (retObj, retExcSet) = (0 until argLen).foldLeft((arr, ExcSetEmpty)) {
              case ((arr, excSet), k) => {
                val kValue = argObj.Get(k.toString, h)
                //   a. Remove the first element from items and let E be the value of the element.
                //   b. Call the [[Put]] internal method of O with arguments ToString(n), E, and true.
                val (retObj, retExcSet) = arr.Put(AbsString((thisLen + k).toString), kValue, true, h)
                (retObj, retExcSet)
                //   c. Increase n by 1.
              }
            }
            // 6. Call the [[Put]] internal method of O with arguments "length", n, and true.
            val n = AbsNumber(argLen + thisLen)
            val (putObj, putExcSet) = retObj.Put(AbsString("length"), n, true, h)
            // 7. Return n.
            (putObj, AbsValue(n), putExcSet ++ retExcSet)
          }
          case _ => (arr.update(AbsString.Number, AbsDataProp.Top), AbsValue(AbsNumber.Top), HashSet(TypeError))
        }
        val retH = h.update(loc, retObj)
        (retH, value + retV, excSet ++ retExcSet)
      }
    }
    val excSt = st.raiseException(excSet)
    (AbsState(retH, state.context), excSt, retV)
  }

  def reverse(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    // 1. Let O be the result of calling ToObject passing the this value as the argument.
    val addr = SystemAddr("Array.prototype.reverse<object>")
    val thisBinding = st.context.thisBinding
    val (thisLoc, state, es) = TypeConversionHelper.ToObject(thisBinding, st, addr)
    val h = state.heap
    val (retH, excSet) = thisLoc.foldLeft((h, es)) {
      case ((h, excSet), loc) => {
        val arr = h.get(loc)
        // 2. Let lenVal be the result of calling the [[Get]] internal method of O with argument "length".
        val lenVal = arr.Get("length", h)
        // 3. Let len be ToUint32(lenVal).
        val len = TypeConversionHelper.ToUInt32(lenVal)
        val (retObj: AbsObject, retExcSet: Set[Exception]) = len.getSingle match {
          case ConZero() => (AbsObject.Bot, ExcSetEmpty)
          case ConOne(Num(n)) => {
            val length = n.toInt
            val pairList = (0 until length).foldLeft[List[(AbsValue, AbsBool)]](Nil) {
              case (lst, k) => {
                val absK = AbsString(k.toString)
                val kValue = arr.Get(absK, h)
                val kHas = arr.HasProperty(absK, h)
                (kValue, kHas) :: lst
              }
            }
            pairList.zipWithIndex.foldLeft((arr, ExcSetEmpty)) {
              case ((arr, excSet), ((value, has), idx)) => {
                val absIdx = AbsString(idx.toString)
                val delObj =
                  if (AbsBool.False <= has) {
                    val (delObj, _) = arr.Delete(absIdx)
                    delObj
                  } else AbsObject.Bot
                val (putObj, putExcSet) =
                  if (AbsBool.True <= has) {
                    arr.Put(absIdx, value, true, h)
                  } else (AbsObject.Bot, ExcSetEmpty)
                (delObj + putObj, excSet ++ putExcSet)
              }
            }
          }
          case ConMany() => (arr.update(AbsString.Number, AbsDataProp.Top), HashSet(TypeError))
        }
        val retH = h.update(loc, retObj)
        (retH, excSet ++ retExcSet)
      }
    }
    val excSt = st.raiseException(excSet)
    (AbsState(retH, state.context), excSt, thisLoc)
  }

  def shift(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    // 1. Let O be the result of calling ToObject passing the this value as the argument.
    val addr = SystemAddr("Array.prototype.shift<object>")
    val thisBinding = st.context.thisBinding
    val (thisLoc, state, es) = TypeConversionHelper.ToObject(thisBinding, st, addr)
    val h = state.heap
    val (retH, retV, excSet) = thisLoc.foldLeft((h, AbsValue.Bot, es)) {
      case ((h, value, excSet), loc) => {
        // 2. Let lenVal be the result of calling the [[Get]] internal method of O with argument "length".
        val obj = h.get(loc)
        val lenVal = obj.Get("length", h)
        // 3. Let len be ToUint32(lenVal).
        val len = TypeConversionHelper.ToUInt32(lenVal)
        val (retObj: AbsObject, retV: AbsValue, retExcSet: Set[Exception]) = len.getSingle match {
          case ConZero() => (AbsObject.Bot, AbsValue.Bot, ExcSetEmpty)
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
              } else AbsObject.Bot
              val falseV = if (AbsBool.False <= fromPresent) {
                // e. Else, fromPresent is false
                // i. Call the [[Delete]] internal method of O with arguments to and true.
                val (retObj, _) = obj.Delete(to) //XXX: missing second argument Throw = true.
                // f. Increase k by 1.
                retObj
              } else AbsObject.Bot
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
    (AbsState(retH, state.context), excSt, retV)
  }

  def slice(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val start = Helper.propLoad(args, Set(AbsString("0")), st.heap)
    val end = Helper.propLoad(args, Set(AbsString("1")), st.heap)

    // 1. Let O be the result of calling ToObject passing the this value as the argument.
    val addr = SystemAddr("Array.prototype.slice<object>")
    val thisBinding = st.context.thisBinding
    val (thisLoc, state, es) = TypeConversionHelper.ToObject(thisBinding, st, addr)
    val h = state.heap
    val obj = h.get(thisLoc)
    // 2. Let A be a new array created as if by the expression new Array().
    val arr = AbsObject.newArrayObject()
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
      case (ConZero(), _, _) | (_, ConZero(), _) | (_, _, ConZero()) => (AbsObject.Bot, ExcSetEmpty)
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
            } else (AbsObject.Bot, ExcSetEmpty)
            val falseObj = if (AbsBool.False <= kPresent) obj else AbsObject.Bot
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
    val st1 = state.oldify(arrAddr)
    val arrLoc = Loc(arrAddr, Recent)
    val retH = st1.heap.update(arrLoc, retObj.oldify(arrAddr))
    val excSt = st1.raiseException(retExcSet)
    (AbsState(retH, st1.context), excSt, AbsLoc(arrLoc))
  }

  def splice(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val start = Helper.propLoad(args, Set(AbsString("0")), h)
    val deleteCount = Helper.propLoad(args, Set(AbsString("1")), h)

    val argLoc = args.locset
    val argObj = h.get(args.locset)
    val argLen = argObj.Get("length", h).pvalue.numval

    val relativeStart = TypeConversionHelper.ToInteger(start)
    val relativeDeleteCount = TypeConversionHelper.ToInteger(deleteCount)

    val AT = (AbsBool.True, AbsAbsent.Bot)
    val thisLoc = st.context.thisBinding.locset
    val Top = AbsObject
      .newArrayObject(AbsNumber.Top)
      .update(AbsString.Number, AbsDataProp.Top)
    val (retH: AbsHeap, retArr: AbsObject, retExcSet: Set[Exception]) = thisLoc.foldLeft((h, AbsObject.Bot, ExcSetEmpty)) {
      case ((h, arr, excSet), loc) => {
        val thisObj = h.get(loc)
        val thisLen = TypeConversionHelper.ToUInt32(thisObj.Get("length", h))
        val (retObj: AbsObject, retArr: AbsObject, retExcSet: Set[Exception]) = (
          thisLen.getSingle,
          argLen.getSingle,
          relativeStart.getSingle,
          relativeDeleteCount.getSingle
        ) match {
            case (ConZero(), _, _, _)
            | (_, ConZero(), _, _)
            | (_, _, ConZero(), _)
            | (_, _, _, ConZero()) => (AbsObject.Bot, AbsObject.Bot, ExcSetEmpty)
            case (ConOne(Num(tl)), ConOne(Num(al)), ConOne(Num(rs)), ConOne(Num(rd))) => {
              val thisLen = tl.toInt
              val argLen = al.toInt
              val relativeStart = rs.toInt
              val relativeDeleteCount = rd.toInt
              val actualStart =
                if (relativeStart < 0) Math.max((thisLen + relativeStart), 0)
                else Math.min(relativeStart, thisLen)
              val actualDeleteCount = Math.min(Math.max(relativeDeleteCount, 0), thisLen - actualStart)
              val arr = AbsObject.newArrayObject(AbsNumber(actualDeleteCount))
              val retArr: AbsObject = (0 until actualDeleteCount).foldLeft(arr)((arr, k) => {
                val kValue = thisObj.Get((actualStart + k).toString, h)
                val desc = AbsDesc((kValue, AbsAbsent.Bot), AT, AT, AT)
                val (newArr, _, _) = arr.DefineOwnProperty(AbsString(k.toString), desc, false)
                newArr
              })
              val newLen = Math.max(argLen - 2, 0)
              val remainFrom = actualStart + actualDeleteCount
              val remainTo = actualStart + newLen
              val remainLen = thisLen - (actualStart + actualDeleteCount)
              val (remainObj: AbsObject, remainExcSet: Set[Exception]) = (0 until remainLen).foldLeft((thisObj, ExcSetEmpty)) {
                case ((obj, excSet), k) => {
                  val kValue = thisObj.Get((remainFrom + k).toString, h)
                  val (newObj, newExcSet) = obj.Put(AbsString((remainTo + k).toString), kValue, true, h)
                  (newObj, excSet ++ newExcSet)
                }
              }
              val (newObj: AbsObject, newExcSet: Set[Exception]) = (0 until newLen).foldLeft((remainObj, remainExcSet)) {
                case ((obj, excSet), k) => {
                  val kValue = argObj.Get((k + 2).toString, h)
                  val (newObj, newExcSet) = obj.Put(AbsString((actualStart + k).toString), kValue, true, h)
                  (newObj, excSet ++ newExcSet)
                }
              }
              val length = remainTo + remainLen
              val delObj: AbsObject =
                if (length < thisLen) (length until thisLen).foldLeft(newObj) {
                  case (obj, k) => {
                    val (delArr, _) = obj.Delete(k.toString) // XXX: missing second argument Throw = true.
                    delArr
                  }
                }
                else newObj
              val (lenObj, _) = delObj.Put(AbsString("length"), AbsNumber(length), false, h)
              (lenObj, retArr, newExcSet)
            }
            case _ => (Top, Top, HashSet(TypeError))
          }
        val retH = h.update(loc, retObj)
        (retH, arr + retArr, excSet ++ retExcSet)
      }
    }
    val arrAddr = SystemAddr("Array.prototype.splice<array>")
    val newSt = AbsState(retH, st.context)
    val state = newSt.oldify(arrAddr)
    val arrLoc = Loc(arrAddr, Recent)
    val finalH = state.heap.update(arrLoc, retArr.oldify(arrAddr))
    val excSt = state.raiseException(retExcSet)
    (AbsState(finalH, state.context), excSt, AbsLoc(arrLoc))
  }

  def unshift(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val argLoc = args.locset
    val argObj = h.get(args.locset)
    val argLen = argObj.Get("length", h).pvalue.numval

    val AT = (AbsBool.True, AbsAbsent.Bot)
    val thisLoc = st.context.thisBinding.locset
    val Top = AbsObject
      .newArrayObject(AbsNumber.Top)
      .update(AbsString.Number, AbsDataProp.Top)
    val (retH: AbsHeap, retV: AbsValue, retExcSet: Set[Exception]) = thisLoc.foldLeft((h, AbsValue.Bot, ExcSetEmpty)) {
      case ((h, value, excSet), loc) => {
        val thisObj = h.get(loc)
        val thisLen = TypeConversionHelper.ToUInt32(thisObj.Get("length", h))
        val (retObj: AbsObject, retV: AbsValue, retExcSet: Set[Exception]) = (thisLen.getSingle, argLen.getSingle) match {
          case (ConZero(), _) | (_, ConZero()) => (AbsObject.Bot, AbsValue.Bot, ExcSetEmpty)
          case (ConOne(Num(tl)), ConOne(Num(al))) => {
            val thisLen = tl.toInt
            val argLen = al.toInt

            val newLen = argLen + thisLen
            val (pushObj: AbsObject, pushExcSet: Set[Exception]) = (thisLen - 1 to 0 by -1).foldLeft((thisObj, ExcSetEmpty)) {
              case ((obj, excSet), k) => {
                val kValue = thisObj.Get(k.toString, h)
                val (newObj, newExcSet) = obj.Put(AbsString((argLen + k).toString), kValue, true, h)
                (newObj, excSet ++ newExcSet)
              }
            }
            val (newObj: AbsObject, newExcSet: Set[Exception]) = (0 until argLen).foldLeft((pushObj, pushExcSet)) {
              case ((obj, excSet), k) => {
                val kValue = argObj.Get(k.toString, h)
                val (newObj, newExcSet) = obj.Put(AbsString(k.toString), kValue, true, h)
                (newObj, excSet ++ newExcSet)
              }
            }
            val newAbsLen = AbsNumber(newLen)
            val (lenObj, _) = newObj.Put(AbsString("length"), newAbsLen, false, h)
            (lenObj, AbsValue(newAbsLen), newExcSet)
          }
          case _ => (Top, AbsValue.Top, HashSet(TypeError))
        }
        val retH = h.update(loc, retObj)
        (retH, value + retV, excSet ++ retExcSet)
      }
    }
    val excSt = st.raiseException(retExcSet)
    (AbsState(retH, st.context), excSt, retV)
  }

  def indexOf(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    // 1. Let O be the result of calling ToObject passing the this value as the argument.
    val addr = SystemAddr("Array.prototype.indexOf<object>")
    val thisBinding = st.context.thisBinding
    val (thisLoc, state, excSet) = TypeConversionHelper.ToObject(thisBinding, st, addr)
    val h = state.heap
    val searchElement = Helper.propLoad(args, Set(AbsString("0")), h)
    val fromIndex = Helper.propLoad(args, Set(AbsString("1")), h)
    val result = thisLoc.foldLeft[AbsNumber](AbsNumber.Bot)((num, loc) => {
      val thisObj = h.get(loc)
      // 2. Let lenValue be the result of calling the [[Get]] internal method of O with the argument "length".
      val lenValue = thisObj.Get("length", h)
      // 3. Let len be ToUint32(lenValue).
      val len = TypeConversionHelper.ToUInt32(lenValue)
      val retN: AbsNumber = len.getSingle match {
        case ConZero() => AbsNumber.Bot
        // 4. If len is 0, return -1.
        case ConOne(Num(0)) => AbsNumber(-1)
        case ConOne(Num(l)) => {
          val len = l.toInt
          // 5. If argument fromIndex was passed let n be ToInteger(fromIndex); else let n be 0.
          val undefN =
            if (fromIndex.pvalue.undefval.isBottom) AbsNumber.Bot
            else AbsNumber(0)
          val otherN =
            if (fromIndex.pvalue.copyWith(undefval = AbsUndef.Bot).isBottom && fromIndex.locset.isBottom) AbsNumber.Bot
            else TypeConversionHelper.ToInteger(fromIndex)
          val n = undefN + otherN
          n.getSingle match {
            case ConZero() => AbsNumber.Bot
            case ConOne(Num(num)) => {
              val n = num.toInt
              // 6. If n ≥ len, return -1.
              if (n >= len) AbsNumber(-1)
              else {
                val k =
                  // 7. If n ≥ 0, then
                  //   a. Let k be n.
                  if (n >= 0) n
                  // 8. Else, n < 0
                  else {
                    //   a. Let k be len - abs(n).
                    val k = len - Math.abs(n)
                    //   b. If k is less than 0, then let k be 0.
                    if (k < 0) 0 else k
                  }
                // 9. Repeat, while k < len
                val (retN, retB) = (k until len).foldLeft[(AbsNumber, AbsBool)]((AbsNumber.Bot, AbsBool.False)) {
                  case ((num, b), k) => {
                    // a. Let kPresent be the result of calling the [[HasProperty]] internal method of O with argument ToString(k).
                    val kPresent = thisObj.HasProperty(AbsString(k.toString), h)
                    // b. If kPresent is true, then
                    // i. Let elementK be the result of calling the [[Get]] internal method of O with the argument
                    //    ToString(k).
                    val elementK = thisObj.Get(k.toString, h)
                    // ii. Let same be the result of applying the Strict Equality Comparison Algorithm to
                    //     searchElement and elementK.
                    // XXX: unsound!: only check between primtive values becuase we do not have any strict equality for (Loc/Obj)
                    val same = elementK.pvalue === searchElement.pvalue
                    // iii. If same is true, return k.
                    val retN = if (AbsBool.False <= b && AbsBool.True <= kPresent && AbsBool.True <= same) {
                      AbsNumber(k)
                    } else AbsNumber.Bot
                    // c. Increase k by 1.
                    (num + retN, b || same)
                  }
                }
                // 10. Return -1.
                val notFound =
                  if (AbsBool.False <= retB) AbsNumber(-1)
                  else AbsNumber.Bot
                retN + notFound
              }
            }
            case ConMany() => AbsNumber.Top
          }
        }
        case ConMany() => AbsNumber.Top
      }
      num + retN
    })
    val excSt = st.raiseException(excSet)
    (state, excSt, result)
  }

  def lastIndexOf(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    // 1. Let O be the result of calling ToObject passing the this value as the argument.
    val addr = SystemAddr("Array.prototype.lastIndexOf<object>")
    val thisBinding = st.context.thisBinding
    val (thisLoc, state, excSet) = TypeConversionHelper.ToObject(thisBinding, st, addr)
    val h = state.heap
    val searchElement = Helper.propLoad(args, Set(AbsString("0")), h)
    val fromIndex = Helper.propLoad(args, Set(AbsString("1")), h)
    val result = thisLoc.foldLeft[AbsNumber](AbsNumber.Bot)((num, loc) => {
      val thisObj = h.get(loc)
      // 2. Let lenValue be the result of calling the [[Get]] internal method of O with the argument "length".
      val lenValue = thisObj.Get("length", h)
      // 3. Let len be ToUint32(lenValue).
      val len = TypeConversionHelper.ToUInt32(lenValue)
      val retN: AbsNumber = len.getSingle match {
        case ConZero() => AbsNumber.Bot
        // 4. If len is 0, return -1.
        case ConOne(Num(0)) => AbsNumber(-1)
        case ConOne(Num(l)) => {
          val len = l.toInt
          // 5. If argument fromIndex was passed let n be ToInteger(fromIndex); else let n be 0.
          val undefN =
            if (fromIndex.pvalue.undefval.isBottom) AbsNumber.Bot
            else AbsNumber(len - 1)
          val otherN =
            if (fromIndex.pvalue.copyWith(undefval = AbsUndef.Bot).isBottom && fromIndex.locset.isBottom) AbsNumber.Bot
            else TypeConversionHelper.ToInteger(fromIndex)
          val n = undefN + otherN
          n.getSingle match {
            case ConZero() => AbsNumber.Bot
            case ConOne(Num(num)) => {
              val n = num.toInt
              val k =
                // 6. If n ≥ 0,then let k be min(n, len–1).
                if (n >= 0) Math.min(n, len - 1)
                // 7. Else, n<0
                //   a. Let k be len - abs(n).
                else len - Math.abs(n)
              // 8. Repeat, while k ≥ 0
              val (retN, retB) = (k to 0 by -1).foldLeft[(AbsNumber, AbsBool)]((AbsNumber.Bot, AbsBool.False)) {
                case ((num, b), k) => {
                  // a. Let kPresent be the result of calling the [[HasProperty]] internal method of O with argument ToString(k).
                  val kPresent = thisObj.HasProperty(AbsString(k.toString), h)
                  // b. If kPresent is true, then
                  // i. Let elementK be the result of calling the [[Get]] internal method of O with the argument
                  //    ToString(k).
                  val elementK = thisObj.Get(k.toString, h)
                  // ii. Let same be the result of applying the Strict Equality Comparison Algorithm to
                  //     searchElement and elementK.
                  // XXX: unsound!: only check between primtive values becuase we do not have any strict equality for (Loc/Obj)
                  val same = elementK.pvalue === searchElement.pvalue
                  // iii. If same is true, return k.
                  val retN = if (AbsBool.False <= b && AbsBool.True <= kPresent && AbsBool.True <= same) {
                    AbsNumber(k)
                  } else AbsNumber.Bot
                  // c. Decrease k by 1.
                  (num + retN, b || same)
                }
              }
              // 9. Return -1.
              val notFound =
                if (AbsBool.False <= retB) AbsNumber(-1)
                else AbsNumber.Bot
              retN + notFound
            }
            case ConMany() => AbsNumber.Top
          }
        }
        case ConMany() => AbsNumber.Top
      }
      num + retN
    })
    val excSt = st.raiseException(excSet)
    (state, excSt, result)
  }
}
