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

import kr.ac.kaist.safe.analyzer.{ Semantics, Helper }
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.analyzer.TypeConversionHelper
import kr.ac.kaist.safe.util.{ SystemAddr, Loc, Recent }
import scala.collection.immutable.HashSet

// 15.2 Object Objects
object BuiltinObject extends FuncModel(
  name = "Object",

  // 15.2.1 The Object Constructor Called as a Function: Object([value])
  code = BasicCode(argLen = 1, (
    args: Value, st: State, sem: Semantics
  ) => {
    val h = st.heap
    val argV = sem.CFGLoadHelper(args, Set(AbsString.alpha("0")), h)
    val addr = SystemAddr("Object<instance>")

    // 1. If value is null, undefined or not supplied, create and return
    //    a new Object object exactly as if the standard built-in Object
    //    constructor had been called with the same arguments.
    val pv = argV.pvalue
    val (v1, st1) = if (pv.undefval.gamma != ConSimpleBot ||
      pv.nullval.gamma != ConSimpleBot ||
      argV.isBottom) {
      val state = st.oldify(addr)
      val loc = Loc(addr, Recent)
      val obj = AbsObjectUtil.newObject
      val heap = state.heap.update(loc, obj)
      (ValueUtil(loc), State(heap, st.context))
    } else {
      (ValueUtil.Bot, State.Bot)
    }

    // 2. Return ToObject(value)
    val (v2, st2, _) = TypeConversionHelper.ToObject(argV, st, addr)

    (st1 + st2, State.Bot, v1 + v2)
  }),

  // 15.2.2 The Object Constructor: new Object([value])
  construct = Some(BasicCode(argLen = 1, (
    args: Value, st: State, sem: Semantics
  ) => {
    val h = st.heap
    val argV = sem.CFGLoadHelper(args, Set(AbsString.alpha("0")), h)
    val addr = SystemAddr("Object<instance>")

    // 1. If value is supplied, then
    //    a. If Type(value) is Object, then
    //       i. If the value is a native ECMAScript object, do not create a new object
    //          but simply return value.
    //       ii. If the value is a host object, then actions are taken and a result is
    //           returned in an implementation-dependent manner that may depend on
    //           the host object.
    //           (We do not consider an implementation-dependent actions for a host object)
    //    b. If Type(value) is String, return ToObject(value).
    //    c. If Type(value) is Boolean, return ToObject(value).
    //    d. If Type(value) is Number, return ToObject(value).
    val (v2, st2, _) = TypeConversionHelper.ToObject(argV, st, addr)

    // 2. Assert: The argument value was not supplied or its type was Null or Undefined.
    // 3. Let obj be a newly created native ECMAScript object.
    val pv = argV.pvalue
    val (v1, st1) = if (pv.undefval.gamma != ConSimpleBot ||
      pv.nullval.gamma != ConSimpleBot ||
      argV.isBottom) {
      val state = st.oldify(addr)
      val loc = Loc(addr, Recent)
      val obj = AbsObjectUtil.newObject
      val heap = state.heap.update(loc, obj)
      (ValueUtil(loc), State(heap, st.context))
    } else {
      (ValueUtil.Bot, State.Bot)
    }

    (st1 + st2, State.Bot, v1 + v2)
  })),

  // 15.2.3.1 Object.prototype
  protoModel = Some((BuiltinObjectProto, F, F, F)),

  props = List(
    // 15.2.3.2 Object.getPrototypeOf(O)
    NormalProp("getPrototypeOf", FuncModel(
      name = "Object.getPrototypeOf",
      code = BasicCode(argLen = 1, (
        args: Value, st: State, sem: Semantics
      ) => {
        val h = st.heap
        val argV = sem.CFGLoadHelper(args, Set(AbsString.alpha("0")), h)
        val tmpAddr = SystemAddr("<temp>")

        val (retV, retSt, excSet) = TypeConversionHelper.ToObject(argV, st, tmpAddr)

        // 1. If Type(O) is not Object throw a TypeError exception.
        val excSt = st.raiseException(excSet)

        // 2. Return the value of [[Prototype]] internal property of O.
        val protoV = retV.locset.foldLeft(ValueUtil.Bot)((v, loc) => {
          v + retSt.heap(loc).getOrElse(AbsObjectUtil.Bot).get(IPrototype).value
        })

        (st, excSt, protoV)
      })
    ), T, F, T),

    // 15.2.3.3 getOwnPropertyDescriptor(O, P)
    NormalProp("getOwnPropertyDescriptor", FuncModel(
      name = "Object.getOwnPropertyDescriptor",
      code = BasicCode(argLen = 2, (
        args: Value, st: State, sem: Semantics
      ) => {
        val h = st.heap
        val objV = sem.CFGLoadHelper(args, Set(AbsString.alpha("0")), h)
        val strV = sem.CFGLoadHelper(args, Set(AbsString.alpha("1")), h)
        val tmpAddr = SystemAddr("<temp>")
        val descAddr = SystemAddr("Object.getOwnPropertyDescriptor<descriptor>")
        val AT = AbsBool.alpha(true)
        val AF = AbsBool.alpha(false)
        val (locV, retSt, excSet) = TypeConversionHelper.ToObject(objV, st, tmpAddr)

        // 1. If Type(O) is not Object throw a TypeError exception.
        val excSt = st.raiseException(excSet)

        // 2. Let name be ToString(P).
        val name = TypeConversionHelper.ToString(strV)

        // 3. Let desc be the result of calling the [[GetOwnProperty]]
        //    internal method of O with argument name.
        // 4. Return the result of calling FromPropertyDescriptor(desc)
        val obj = locV.locset.foldLeft(AbsObjectUtil.Bot)((obj, loc) => {
          obj + retSt.heap.getOrElse(loc, AbsObjectUtil.Bot)
        })
        val isDomIn = (obj domIn name)
        val v1 =
          if (AF <= isDomIn) ValueUtil.alpha()
          else ValueUtil.Bot
        val (state, v2) =
          if (AT <= isDomIn) {
            val objval = obj(name).getOrElse(PropValue.Bot).objval
            val valueV = objval.value
            val writableV = ValueUtil(objval.writable)
            val enumerableV = ValueUtil(objval.enumerable)
            val configurableV = ValueUtil(objval.configurable)
            val descObj = AbsObjectUtil.newObject
              .update("value", PropValue(DataProperty(valueV, AT, AF, AT)))
              .update("writable", PropValue(DataProperty(writableV, AT, AF, AT)))
              .update("enumerable", PropValue(DataProperty(enumerableV, AT, AF, AT)))
              .update("configurable", PropValue(DataProperty(configurableV, AT, AF, AT)))
            val state = st.oldify(descAddr)
            val descLoc = Loc(descAddr, Recent)
            val retHeap = state.heap.update(descLoc, descObj)
            (State(retHeap, st.context), ValueUtil(descLoc))
          } else (st, ValueUtil.Bot)

        (state, excSt, v1 + v2)
      })
    ), T, F, T),

    // 15.2.3.4 Object.getOwnPropertyNames(O)
    NormalProp("getOwnPropertyNames", FuncModel(
      name = "Object.getOwnPropertyNames",
      code = BasicCode(argLen = 1, (
        args: Value, st: State, sem: Semantics
      ) => {
        val h = st.heap
        val objV = sem.CFGLoadHelper(args, Set(AbsString.alpha("0")), h)
        val tmpAddr = SystemAddr("<temp>")
        val arrAddr = SystemAddr("Object.getOwnPropertyNames<array>")
        val (locV, retSt, excSet) = TypeConversionHelper.ToObject(objV, st, tmpAddr)
        val (keyStr, lenSet) = locV.locset.foldLeft(
          (AbsString.Bot, Set[Double]())
        ) {
            case ((str, lenSet), loc) => {
              val obj = h.getOrElse(loc, AbsObjectUtil.Bot)
              val keys = obj.map.keySet.filter(!_.startsWith("@"))
              val keyStr = AbsString.alpha(keys)
              (str + keyStr, lenSet + keys.size)
            }
          }
        val len = lenSet.max
        val AT = AbsBool.True

        // 1. If Type(O) is not Object throw a TypeError exception.
        val excSt = st.raiseException(excSet)

        // 2. Let array be the result of creating a new object
        //    as if by the expression new Array() where Array is the
        //    standard built-in constructor with that name.
        val arrObj = AbsObjectUtil.newArrayObject(AbsNumber.alpha(lenSet))

        // 3. Let n be 0.
        // 4. For each named own property P of O
        //    a. Let name be the String value that is the name of P.
        //    b. Call the [[DefineOwnProperty]] internal method of
        //       array with arguments ToString(n), the PropertyDescriptor
        //       {[[Value]]: name, [[Writable]]: true, [[Enumerable]]: true,
        //       [[Configurable]]: true}, and false.
        //    c. Increment n by 1.
        val v = ValueUtil(PValue(
          AbsUndef.Top,
          AbsNull.Bot,
          AbsBool.Bot,
          AbsNumber.Bot,
          keyStr
        ))
        val retObj = (0 until len.toInt).foldLeft(arrObj)((obj, idx) => {
          obj.update(idx.toString, PropValue(DataProperty(v, AT, AT, AT)))
        })
        val state = st.oldify(arrAddr)
        val arrLoc = Loc(arrAddr, Recent)
        val retHeap = state.heap.update(arrLoc, retObj)

        // 5. Return array.
        (State(retHeap, st.context), excSt, ValueUtil(arrLoc))
      })
    ), T, F, T),

    // TODO create
    NormalProp("create", FuncModel(
      name = "Object.create",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO defineProperty
    NormalProp("defineProperty", FuncModel(
      name = "Object.defineProperty",
      code = EmptyCode(argLen = 3)
    ), T, F, T),

    // TODO defineProperties
    NormalProp("defineProperties", FuncModel(
      name = "Object.defineProperties",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO seal
    NormalProp("seal", FuncModel(
      name = "Object.seal",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO freeze
    NormalProp("freeze", FuncModel(
      name = "Object.freeze",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO preventExtensions
    NormalProp("preventExtensions", FuncModel(
      name = "Object.preventExtensions",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO isSealed
    NormalProp("isSealed", FuncModel(
      name = "Object.isSealed",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO isFrozen
    NormalProp("isFrozen", FuncModel(
      name = "Object.isFrozen",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO isExtensible
    NormalProp("isExtensible", FuncModel(
      name = "Object.isExtensible",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO keys
    NormalProp("keys", FuncModel(
      name = "Object.keys",
      code = EmptyCode(argLen = 1)
    ), T, F, T)
  )
)

object BuiltinObjectProto extends ObjModel(
  name = "Object.prototype",
  props = List(
    InternalProp(IPrototype, PrimModel(null)),

    // TODO toString
    NormalProp("toString", FuncModel(
      name = "Object.prototype.toString",
      code = EmptyCode()
    ), T, F, T),

    // TODO toLocaleString
    NormalProp("toLocaleString", FuncModel(
      name = "Object.prototype.toLocaleString",
      code = EmptyCode()
    ), T, F, T),

    // TODO valueOf
    NormalProp("valueOf", FuncModel(
      name = "Object.prototype.valueOf",
      code = EmptyCode()
    ), T, F, T),

    // TODO hasOwnProperty
    NormalProp("hasOwnProperty", FuncModel(
      name = "Object.prototype.hasOwnProperty",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO isPrototypeOf
    NormalProp("isPrototypeOf", FuncModel(
      name = "Object.prototype.isPrototypeOf",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO propertyIsEnumerable
    NormalProp("propertyIsEnumerable", FuncModel(
      name = "Object.prototype.propertyIsEnumerable",
      code = EmptyCode(argLen = 1)
    ), T, F, T)
  )
)
