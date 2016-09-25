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
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.util.SystemAddr
import scala.collection.immutable.HashSet

// 15.2 Object Objects
object BuiltinObject extends FuncModel(
  name = "Object",

  // 15.2.1 The Object Constructor Called as a Function: Object([value])
  code = BasicCode(argLen = 1, BuiltinObjectHelper.construct),

  // 15.2.2 The Object Constructor: new Object([value])
  construct = Some(BasicCode(argLen = 1, BuiltinObjectHelper.construct)),

  // 15.2.3.1 Object.prototype
  protoModel = Some((BuiltinObjectProto, F, F, F)),

  props = List(
    // 15.2.3.2 Object.getPrototypeOf(O)
    NormalProp("getPrototypeOf", FuncModel(
      name = "Object.getPrototypeOf",
      code = BasicCode(argLen = 1, BuiltinObjectHelper.getPrototypeOf)
    ), T, F, T),

    // 15.2.3.3 getOwnPropertyDescriptor(O, P)
    NormalProp("getOwnPropertyDescriptor", FuncModel(
      name = "Object.getOwnPropertyDescriptor",
      code = BasicCode(argLen = 2, BuiltinObjectHelper.getOwnPropertyDescriptor)
    ), T, F, T),

    // 15.2.3.4 Object.getOwnPropertyNames(O)
    NormalProp("getOwnPropertyNames", FuncModel(
      name = "Object.getOwnPropertyNames",
      code = BasicCode(argLen = 1, BuiltinObjectHelper.getOwnPropertyNames)
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

    NormalProp("seal", FuncModel(
      name = "Object.seal",
      code = BasicCode(argLen = 1, BuiltinObjectHelper.seal)
    ), T, F, T),

    NormalProp("freeze", FuncModel(
      name = "Object.freeze",
      code = BasicCode(argLen = 1, BuiltinObjectHelper.freeze)
    ), T, F, T),

    NormalProp("preventExtensions", FuncModel(
      name = "Object.preventExtensions",
      code = BasicCode(argLen = 1, BuiltinObjectHelper.preventExtensions)
    ), T, F, T),

    NormalProp("isSealed", FuncModel(
      name = "Object.isSealed",
      code = BasicCode(argLen = 1, BuiltinObjectHelper.isSealed)
    ), T, F, T),

    NormalProp("isFrozen", FuncModel(
      name = "Object.isFrozen",
      code = BasicCode(argLen = 1, BuiltinObjectHelper.isFrozen)
    ), T, F, T),

    NormalProp("isExtensible", FuncModel(
      name = "Object.isExtensible",
      code = BasicCode(argLen = 1, BuiltinObjectHelper.isExtensible)
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
    InternalProp(IPrototype, PrimModel(Null)),

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

object BuiltinObjectHelper {
  def construct(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val argV = Helper.propLoad(args, Set(AbsString("0")), h)
    val addr = SystemAddr("Object<instance>")

    // 1. If value is supplied and it is not null or undefined,
    //    then, return ToObject(value)
    //    XXX: We do not consider an implementation-dependent actions
    //         for a host objects)
    val (v2, st2, _) = TypeConversionHelper.ToObject(argV, st, addr)

    // 2. Else, return a newly created native ECMAScript object.
    val pv = argV.pvalue
    val (v1, st1) =
      if (pv.undefval.isBottom && pv.nullval.isBottom) (AbsValue.Bot, State.Bot)
      else newObjSt(st, addr)

    (st1 + st2, State.Bot, v1 + v2)
  }

  def getPrototypeOf(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val argV = Helper.propLoad(args, Set(AbsString("0")), h)

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(argV)

    // 2. Return the value of [[Prototype]] internal property of O.
    val protoV = argV.locset.foldLeft(AbsValue.Bot)((v, loc) => {
      v + h.get(loc)(IPrototype).value
    })

    val excSt = st.raiseException(excSet)

    (st, excSt, protoV)
  }

  def getOwnPropertyDescriptor(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsString("0")), h)
    val propV = Helper.propLoad(args, Set(AbsString("1")), h)

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet1 = objCheck(objV)
    // 2. Let name be ToString(P).
    val name = TypeConversionHelper.ToString(propV)
    // 3. Let desc be the result of calling the [[GetOwnProperty]]
    //    internal method of O with argument name.
    val obj = h.get(objV.locset)
    val (desc, undef) = obj.GetOwnProperty(name)
    // 4. Return the result of calling FromPropertyDescriptor(desc) (8.10.4).
    val (retH, retV, excSet2) = if (!desc.isBottom) {
      val (descObj, excSet) = AbsObjectUtil.FromPropertyDescriptor(desc)
      val descAddr = SystemAddr("Object.getOwnPropertyDescriptor<descriptor>")
      val descLoc = Loc(descAddr, Recent)
      val retH = h.update(descLoc, descObj)
      val retV = AbsValue(undef, AbsLoc(descLoc))
      (retH, retV, excSet)
    } else (h, AbsValue(undef), ExcSetEmpty)

    val excSt = st.raiseException(excSet1 ++ excSet2)

    (State(retH, st.context), excSt, retV)
  }

  def getOwnPropertyNames(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsString("0")), h)
    val arrAddr = SystemAddr("Object.getOwnPropertyNames<array>")
    val (keyStr, lenSet) = objV.locset.foldLeft((AbsString.Bot, Set[Double]())) {
      case ((str, lenSet), loc) => {
        val obj = h.get(loc)
        val keys = obj.collectKeySet("")
        (str + AbsString(keys), lenSet + keys.size)
      }
    }
    val max = lenSet.max

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)
    // 2. Let array be the result of creating a new Array object.
    // (XXX: we assign the length of the Array object as the number of properties)
    val array = AbsObjectUtil.newArrayObject(AbsNumber(lenSet))
    // 3. For each named own property P of O (with index n started from 0)
    //   a. Let name be the String value that is the name of P.
    val AT = (AbsBool.True, AbsAbsent.Bot)
    val name = AbsValue(AbsPValue(strval = keyStr))
    val desc = AbsDesc((name, AbsAbsent.Bot), AT, AT, AT)
    val (retObj, retExcSet) = (0 until max.toInt).foldLeft((array, excSet)) {
      case ((obj, e), n) => {
        val prop = AbsString(n.toString)
        // b. Call the [[DefineOwnProperty]] internal method of array with arguments
        //    ToString(n), the PropertyDescriptor {[[Value]]: name, [[Writable]]:
        //    true, [[Enumerable]]: true, [[Configurable]]:true}, and false.
        val (newObj, _, excSet) = obj.DefineOwnProperty(prop, desc, false)
        (obj + newObj, e ++ excSet)
      }
    }

    // 5. Return array.
    val state = st.oldify(arrAddr)
    val arrLoc = Loc(arrAddr, Recent)
    val retHeap = state.heap.update(arrLoc, retObj)
    val excSt = st.raiseException(retExcSet)

    (State(retHeap, st.context), excSt, AbsValue(arrLoc))
  }

  def seal(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsString("0")), h)

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)

    val (retH, retExcSet) = objV.locset.foldLeft((h, excSet)) {
      case ((heap, e), loc) => {
        val obj = h.get(loc)
        // 2. For each named own property name P of O,
        //   a. Let desc be the result of calling the [[GetOwnProperty]] internal method of O with P.
        //   b. If desc.[[Configurable]] is true, set desc.[[Configurable]] to false.
        //   c. Call the [[DefineOwnProperty]] internal method of O with P, desc, and true as arguments.
        val (newObj, excSet) = changeProps(obj, desc => {
          val (c, ca) = desc.configurable
          val newConfig = c.fold(AbsBool.Bot)(_ => AbsBool.False)
          desc.copyWith(configurable = (newConfig, ca))
        })
        // 3. Set the [[Extensible]] internal property of O to false.
        val retObj = newObj.update(IExtensible, InternalValueUtil(AbsBool.False))
        // 4. Return O.
        val retH = heap.update(loc, retObj)
        (retH, e ++ excSet)
      }
    }

    val excSt = st.raiseException(retExcSet)

    (State(retH, st.context), excSt, objV.locset)
  }

  def freeze(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsString("0")), h)

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)
    val (retH, retExcSet) = objV.locset.foldLeft((h, excSet)) {
      case ((heap, e), loc) => {
        val obj = h.get(loc)
        // 2. For each named own property name P of O,
        //   a. Let desc be the result of calling the [[GetOwnProperty]] internal method of O with P.
        //   b. If desc.[[Writable]] is true, set desc.[[Writable]] to false.
        //   c. If desc.[[Configurable]] is true, set desc.[[Configurable]] to false.
        //   d. Call the [[DefineOwnProperty]] internal method of O with P, desc, and true as arguments.
        val (newObj, excSet) = changeProps(obj, desc => {
          val (w, wa) = desc.writable
          val (c, ca) = desc.configurable
          val newWriteable = w.fold(AbsBool.Bot)(_ => AbsBool.False)
          val newConfig = c.fold(AbsBool.Bot)(_ => AbsBool.False)
          desc.copyWith(
            writable = (newWriteable, wa),
            configurable = (newConfig, ca)
          )
        })
        // 3. Set the [[Extensible]] internal property of O to false.
        val retObj = newObj.update(IExtensible, InternalValueUtil(AbsBool.False))
        // 4. Return O.
        val retH = heap.update(loc, retObj)
        (retH, e ++ excSet)
      }
    }

    val excSt = st.raiseException(retExcSet)

    (State(retH, st.context), excSt, objV.locset)
  }

  def preventExtensions(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsString("0")), h)

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)
    val retH = objV.locset.foldLeft(h) {
      case (heap, loc) => {
        val obj = h.get(loc)
        // 2. Set the [[Extensible]] internal property of O to false.
        val retObj = obj.update(IExtensible, InternalValueUtil(AbsBool.False))
        // 3. Return O.
        heap.update(loc, retObj)
      }
    }

    val excSt = st.raiseException(excSet)

    (State(retH, st.context), excSt, objV.locset)
  }

  def isSealed(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsString("0")), h)

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)
    // 2. For each named own property name P of O,
    //   a. Let desc be the result of calling the [[GetOwnProperty]] internal method of O with P.
    //   b. If desc.[[Configurable]] is true, then return false.
    val obj = h.get(objV.locset)
    val cCheck = forall(obj, desc => {
      val (c, ca) = desc.configurable
      c.negate || AbsBool(ca.isTop)
    })
    // 3. If the [[Extensible]] internal property of O is false, then return true.
    val eCheck = obj(IExtensible).value.pvalue.boolval.negate
    // 4. Otherwise, return false.
    val retB = cCheck && eCheck
    val excSt = st.raiseException(excSet)
    (st, excSt, retB)
  }

  def isFrozen(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsString("0")), h)

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)
    // 2. For each named own property name P of O,
    //   a. Let desc be the result of calling the [[GetOwnProperty]] internal method of O with P.
    //   b. If desc.[[Writable]] is true, return false.
    //   c. If desc.[[Configurable]] is true, then return false.
    val obj = h.get(objV.locset)
    val cCheck = forall(obj, desc => {
      val (w, wa) = desc.writable
      val (c, ca) = desc.configurable
      (w.negate || AbsBool(wa.isTop)) && (c.negate || AbsBool(ca.isTop))
    })
    // 3. If the [[Extensible]] internal property of O is false, then return true.
    val eCheck = obj(IExtensible).value.pvalue.boolval.negate
    // 4. Otherwise, return false.
    val retB = cCheck && eCheck
    val excSt = st.raiseException(excSet)
    (st, excSt, retB)
  }

  def isExtensible(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsString("0")), h)

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)
    // 2. Return the Boolean value of the [[Extensible]] internal property of O.
    val obj = h.get(objV.locset)
    val retB = obj(IExtensible).value.pvalue.boolval
    val excSt = st.raiseException(excSet)
    (st, excSt, retB)
  }

  ////////////////////////////////////////////////////////////////
  // private helper functions
  ////////////////////////////////////////////////////////////////
  private def objCheck(value: AbsValue): Set[Exception] = {
    if (value.pvalue.isBottom) ExcSetEmpty
    else HashSet(TypeError)
  }

  private def newObjSt(st: State, addr: SystemAddr): (AbsValue, State) = {
    val state = st.oldify(addr)
    val loc = Loc(addr, Recent)
    val obj = AbsObjectUtil.newObject
    val heap = state.heap.update(loc, obj)
    (AbsValue(loc), State(heap, state.context))
  }

  private def changeProps(obj: AbsObject, f: AbsDesc => AbsDesc): (AbsObject, Set[Exception]) = {
    val aKeySet = obj.abstractKeySet
    // For each named own property name P of O,
    aKeySet.foldLeft(obj, ExcSetEmpty) {
      case ((o, e), key) => {
        // Let desc be the result of calling the [[GetOwnProperty]] internal method of O with P.
        val (desc, _) = obj.GetOwnProperty(key)
        // create new PropertyDescriptor by using f.
        val newDesc = f(desc)
        // Call the [[DefineOwnProperty]] internal method of O with P, desc, and true as arguments.
        val (retObj, _, excSet) = o.DefineOwnProperty(key, newDesc, true)
        (retObj, e ++ excSet)
      }
    }
  }

  private def forall(obj: AbsObject, f: AbsDesc => AbsBool): AbsBool = {
    val aKeySet = obj.abstractKeySet
    if (obj.isBottom) AbsBool.Bot
    else {
      // For each named own property name P of O,
      aKeySet.foldLeft(AbsBool.True) {
        case (b, key) => {
          // Let desc be the result of calling the [[GetOwnProperty]] internal method of O with P.
          val (desc, _) = obj.GetOwnProperty(key)
          // Check by using f.
          b && f(desc)
        }
      }
    }
  }
}
