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

    // 15.2.3.5 Object.create(O [, Properties])
    NormalProp("create", FuncModel(
      name = "Object.create",
      code = BasicCode(argLen = 2, BuiltinObjectHelper.create)
    ), T, F, T),

    // 15.2.3.6 Object.defineProperty(O, P, Attributes)
    NormalProp("defineProperty", FuncModel(
      name = "Object.defineProperty",
      code = BasicCode(argLen = 3, BuiltinObjectHelper.defineProperty)
    ), T, F, T),

    // 15.2.3.7 Object.defineProperties(O, Properties)
    NormalProp("defineProperties", FuncModel(
      name = "Object.defineProperties",
      code = BasicCode(argLen = 2, BuiltinObjectHelper.defineProperties)
    ), T, F, T),

    // 15.2.3.8 Object.seal(O)
    NormalProp("seal", FuncModel(
      name = "Object.seal",
      code = BasicCode(argLen = 1, BuiltinObjectHelper.seal)
    ), T, F, T),

    // 15.2.3.9 Object.freeze(O)
    NormalProp("freeze", FuncModel(
      name = "Object.freeze",
      code = BasicCode(argLen = 1, BuiltinObjectHelper.freeze)
    ), T, F, T),

    // 15.2.3.10 Object.preventExtensions(O)
    NormalProp("preventExtensions", FuncModel(
      name = "Object.preventExtensions",
      code = BasicCode(argLen = 1, BuiltinObjectHelper.preventExtensions)
    ), T, F, T),

    // 15.2.3.11 Object.isSealed(O)
    NormalProp("isSealed", FuncModel(
      name = "Object.isSealed",
      code = BasicCode(argLen = 1, BuiltinObjectHelper.isSealed)
    ), T, F, T),

    // 15.2.3.12 Object.isFrozen(O)
    NormalProp("isFrozen", FuncModel(
      name = "Object.isFrozen",
      code = BasicCode(argLen = 1, BuiltinObjectHelper.isFrozen)
    ), T, F, T),

    // 15.2.3.13 Object.isExtensible(O)
    NormalProp("isExtensible", FuncModel(
      name = "Object.isExtensible",
      code = BasicCode(argLen = 1, BuiltinObjectHelper.isExtensible)
    ), T, F, T),

    // 15.2.3.14 Object.keys(O)
    NormalProp("keys", FuncModel(
      name = "Object.keys",
      code = BasicCode(argLen = 1, BuiltinObjectHelper.keys)
    ), T, F, T)
  )
)

// 15.2.4 Properties of the Object Prototype Object
object BuiltinObjectProto extends ObjModel(
  name = "Object.prototype",
  props = List(
    InternalProp(IPrototype, PrimModel(Null)),

    // 15.2.4.2 Object.prototype.toString()
    NormalProp("toString", FuncModel(
      name = "Object.prototype.toString",
      code = PureCode(argLen = 0, BuiltinObjectHelper.toString)
    ), T, F, T),

    // 15.2.4.3 Object.prototype.toLocaleString()
    NormalProp("toLocaleString", FuncModel(
      name = "Object.prototype.toLocaleString",
      // TODO unsound: not use locale function.
      // we should fix this unsound manner by using CallCode.
      code = PureCode(argLen = 0, BuiltinObjectHelper.toString)
    ), T, F, T),

    // 15.2.4.4 Object.prototype.valueOf()
    NormalProp("valueOf", FuncModel(
      name = "Object.prototype.valueOf",
      code = PureCode(argLen = 0, BuiltinObjectHelper.valueOf)
    ), T, F, T),

    // 15.2.4.5 Object.prototype.hasOwnProperty(V)
    NormalProp("hasOwnProperty", FuncModel(
      name = "Object.prototype.hasOwnProperty",
      code = PureCode(argLen = 1, BuiltinObjectHelper.hasOwnProperty)
    ), T, F, T),

    // 15.2.4.6 Object.prototype.isPrototypeOf(V)
    NormalProp("isPrototypeOf", FuncModel(
      name = "Object.prototype.isPrototypeOf",
      code = PureCode(argLen = 1, BuiltinObjectHelper.isPrototypeOf)
    ), T, F, T),

    // 15.2.4.7 Object.prototype.propertyIsEnumerable(V)
    NormalProp("propertyIsEnumerable", FuncModel(
      name = "Object.prototype.propertyIsEnumerable",
      code = PureCode(argLen = 1, BuiltinObjectHelper.propertyIsEnumerable)
    ), T, F, T)
  )
)

object BuiltinObjectHelper {
  ////////////////////////////////////////////////////////////////
  // Object
  ////////////////////////////////////////////////////////////////
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
    val (retSt, retV, excSet2) = if (!desc.isBottom) {
      val (descObj, excSet) = AbsObjectUtil.FromPropertyDescriptor(desc)
      val descAddr = SystemAddr("Object.getOwnPropertyDescriptor<descriptor>")
      val state = st.oldify(descAddr)
      val descLoc = Loc(descAddr, Recent)
      val retH = state.heap.update(descLoc, descObj)
      val retV = AbsValue(undef, AbsLoc(descLoc))
      (State(retH, state.context), retV, excSet)
    } else (st, AbsValue(undef), ExcSetEmpty)

    val excSt = st.raiseException(excSet1 ++ excSet2)

    (retSt, excSt, retV)
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
    val maxOpt =
      if (lenSet.isEmpty) None
      else Some(lenSet.max)

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
    val (retObj, retExcSet) = maxOpt match {
      case Some(max) => (0 until max.toInt).foldLeft((array, excSet)) {
        case ((obj, e), n) => {
          val prop = AbsString(n.toString)
          // b. Call the [[DefineOwnProperty]] internal method of array with arguments
          //    ToString(n), the PropertyDescriptor {[[Value]]: name, [[Writable]]:
          //    true, [[Enumerable]]: true, [[Configurable]]:true}, and false.
          val (newObj, _, excSet) = obj.DefineOwnProperty(prop, desc, false)
          (obj + newObj, e ++ excSet)
        }
      }
      case None => (AbsObjectUtil.Bot, excSet)
    }

    // 5. Return array.
    retObj.isBottom match {
      case true => (State.Bot, st.raiseException(retExcSet), AbsValue.Bot)
      case false => {
        val state = st.oldify(arrAddr)
        val arrLoc = Loc(arrAddr, Recent)
        val retHeap = state.heap.update(arrLoc, retObj)
        val excSt = state.raiseException(retExcSet)

        (State(retHeap, state.context), excSt, AbsValue(arrLoc))
      }
    }
  }

  def create(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsString("0")), h)
    val propsV = Helper.propLoad(args, Set(AbsString("1")), h)

    // 1. If Type(O) is not Object or Null throw a TypeError exception.
    val excSet =
      if (objV.pvalue.copyWith(nullval = AbsNull.Bot).isBottom) ExcSetEmpty
      else HashSet(TypeError)
    // 2. Let obj be the result of creating a new object.
    val obj = AbsObjectUtil.newObject
    // 3. Set the [[Prototype]] internal property of obj to O.
    val protoV = AbsValue(objV.locset) + objV.pvalue.nullval
    val newObj = obj.update(IPrototype, InternalValueUtil(protoV))
    // 4. If the argument Properties is present and not undefined, add own properties to obj as if by calling the
    //    standard built-in function Object.defineProperties with arguments obj and Properties.
    val addr = SystemAddr("Object.create<object>")
    val state = st.oldify(addr)
    val loc = Loc(addr, Recent)
    val newH = h.update(loc, newObj)
    val retV = AbsLoc(loc)
    val (retSt, e) =
      if (propsV <= AbsUndef.Top) (State(newH, state.context), ExcSetEmpty)
      else defProps(retV, propsV, State(newH, state.context))
    // 5. Return obj.
    val excSt = state.raiseException(excSet ++ e)

    (retSt, excSt, retV)
  }

  def defineProperty(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsString("0")), h)
    val propV = Helper.propLoad(args, Set(AbsString("1")), h)
    val attrV = Helper.propLoad(args, Set(AbsString("2")), h)

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)
    // 2. Let name be ToString(P).
    val name = TypeConversionHelper.ToString(propV)
    // 3. Let desc be the result of calling ToPropertyDescriptor with Attributes as the argument.
    val attr = h.get(attrV.locset)
    val desc = AbsDesc.ToPropertyDescriptor(attr, h)
    val (retH, retExcSet) = objV.locset.foldLeft((h, excSet)) {
      case ((heap, e), loc) => {
        // 4. Call the [[DefineOwnProperty]] internal method of O with arguments name, desc, and true.
        val obj = heap.get(loc)
        val (retObj, _, newExcSet) = obj.DefineOwnProperty(name, desc, true)
        // 5. Return O.
        val retH = heap.update(loc, retObj)
        (retH, e ++ newExcSet)
      }
    }

    val excSt = st.raiseException(retExcSet)

    (State(retH, st.context), excSt, objV.locset)
  }

  def defineProperties(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsString("0")), h)
    val propsV = Helper.propLoad(args, Set(AbsString("1")), h)

    val (retSt, excSet) = defProps(objV, propsV, st)
    val excSt = st.raiseException(excSet)

    (retSt, excSt, objV.locset)
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

  def keys(args: AbsValue, st: State): (State, State, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsString("0")), h)
    val arrAddr = SystemAddr("Object.keys<array>")
    val obj = h.get(objV.locset)
    val keyStr = obj.amap.abstractKeySet((key, dp) => {
      AbsBool.True <= dp.enumerable
    }).foldLeft(AbsString.Bot)(_ + _)

    // 1. If the Type(O) is not Object, throw a TypeError exception.
    val excSet = objCheck(objV)

    val AT = (AbsBool.True, AbsAbsent.Bot)
    val name = AbsValue(AbsPValue(strval = keyStr))
    val desc = AbsDesc((name, AbsAbsent.Bot), AT, AT, AT)
    val (retObj, retExcSet) = keyStr.gamma match {
      case ConFin(set) if obj.amap.isDefinite(keyStr) => {
        // 2. Let n be the number of own enumerable properties of O
        val n = set.size
        // 3. Let array be the result of creating a new Object as if by the ex pression new Array(n).
        val array = AbsObjectUtil.newArrayObject(AbsNumber(n))
        // 4. For each own enumerable property of O whose name String is P (wiht index 0 until n)
        (0 until n).foldLeft((array, ExcSetEmpty)) {
          case ((arr, e), index) => {
            // a. Call the [[DefineOwnProperty]] internal method of array with arguments ToString(index),
            //    the PropertyDescriptor {[[Value]]: P, [[Writable]]: true, [[Enumerable]]: true, [[Configurable]]: true}, and false.
            val (newArr, _, excSet) = arr.DefineOwnProperty(AbsString(index.toString), desc, false)
            (newArr, e ++ excSet)
          }
        }
      }
      case _ => {
        // 2. Let n be the number of own enumerable properties of O
        val n = AbsNumber.Top
        // 3. Let array be the result of creating a new Object as if by the ex pression new Array(n).
        val array = AbsObjectUtil.newArrayObject(n)
        // 4. For each own enumerable property of O whose name String is P (wiht index 0 until n)
        //   a. Call the [[DefineOwnProperty]] internal method of array with arguments ToString(index),
        //      the PropertyDescriptor {[[Value]]: P, [[Writable]]: true, [[Enumerable]]: true, [[Configurable]]: true}, and false.
        val (newArr, _, excSet) = array.DefineOwnProperty(AbsString.Number, desc, false)
        (newArr, excSet)
      }
    }
    // 6. Return array.
    val state = st.oldify(arrAddr)
    val arrLoc = Loc(arrAddr, Recent)
    val retHeap = state.heap.update(arrLoc, retObj)
    val excSt = st.raiseException(retExcSet)

    (State(retHeap, state.context), excSt, AbsValue(arrLoc))
  }

  ////////////////////////////////////////////////////////////////
  // Object.prototype
  ////////////////////////////////////////////////////////////////
  def toString(args: AbsValue, st: State): AbsValue = {
    val h = st.heap
    val thisLoc = st.context.thisBinding.locset
    // XXX: 1. If the this value is undefined, return "[object Undefined]".
    // XXX: 2. If the this value is null, return "[object Null]".
    // XXX: 3. Let O be the result of calling ToObject passing the this value as the argument.
    // TODO current "this" value only have location. we should change!
    // 4. Let class be the value of the [[Class]] internal property of O.
    val obj = h.get(thisLoc)
    val className = obj(IClass).value.pvalue.strval
    // 5. Return the String value that is the result of concatenating the three Strings "[object ", class, and "]".
    AbsString("[object ") concat className concat AbsString("]")
  }

  def valueOf(args: AbsValue, st: State): AbsValue = {
    val h = st.heap
    val thisLoc = st.context.thisBinding.locset
    // XXX: 1. Let O be the result of calling ToObject passing the this value as the argument.
    // TODO current "this" value only have location. we should change!
    // 2. Return O.
    thisLoc
  }

  def hasOwnProperty(args: AbsValue, st: State): AbsValue = {
    val h = st.heap
    val value = Helper.propLoad(args, Set(AbsString("0")), h)
    val thisLoc = st.context.thisBinding.locset
    // 1. Let P be ToString(V).
    val prop = TypeConversionHelper.ToString(value)
    // XXX: 2. Let O be the result of calling ToObject passing the this value as the argument.
    // TODO current "this" value only have location. we should change!
    // 3. Let desc be the result of calling the [[GetOwnProperty]] internal method of O passing P as the argument.
    val obj = h.get(thisLoc)
    val (desc, undef) = obj.GetOwnProperty(prop)
    // 4. If desc is undefined, return false.
    val falseV = undef.fold(AbsBool.Bot)(_ => AbsBool.False)
    // 5. Return true.
    val trueV = desc.fold(AbsBool.Bot)(_ => AbsBool.True)
    falseV + trueV
  }

  def isPrototypeOf(args: AbsValue, st: State): AbsValue = {
    val h = st.heap
    val value = Helper.propLoad(args, Set(AbsString("0")), h)
    val thisLoc = st.context.thisBinding.locset
    // 1. If V is not an object, return false.
    val v1 = value.pvalue.fold(AbsBool.Bot)(_ => AbsBool.False)
    // XXX: 2. Let O be the result of calling ToObject passing the this value as the argument.
    // TODO current "this" value only have location. we should change!
    // 3. Repeat
    var visited: Set[Loc] = HashSet()
    def repeat(loc: Loc): AbsBool = {
      if (visited contains loc) AbsBool.Bot
      else {
        visited += loc
        // a. Let V be the value of the [[Prototype]] internal property of V.
        val obj = h.get(loc)
        val value = obj(IPrototype).value
        // b. if V is null, return false
        val falseV = value.pvalue.nullval.fold(AbsBool.Bot)(_ => AbsBool.False)
        // c. If O and V refer to the same object, return true.
        val trueV =
          if (AbsLoc(loc) <= thisLoc) AbsBool.True
          else AbsBool.Bot
        value.locset.foldLeft(falseV + trueV)(_ + repeat(_))
      }
    }
    value.locset.foldLeft(v1)(_ + repeat(_))
  }

  def propertyIsEnumerable(args: AbsValue, st: State): AbsValue = {
    val h = st.heap
    val value = Helper.propLoad(args, Set(AbsString("0")), h)
    val thisLoc = st.context.thisBinding.locset
    // 1. Let P be ToString(V).
    val prop = TypeConversionHelper.ToString(value)
    // XXX: 2. Let O be the result of calling ToObject passing the this value as the argument.
    // TODO current "this" value only have location. we should change!
    // 3. Let desc be the result of calling the [[GetOwnProperty]] internal method of O passing P as the argument.
    val obj = h.get(thisLoc)
    val (desc, undef) = obj.GetOwnProperty(prop)
    // 4. If desc is undefined, return false.
    val undefV = undef.fold(AbsBool.Bot)(_ => AbsBool.False)
    // 5. Return the value of desc.[[Enumerable]].
    val (enum, _) = desc.enumerable
    undefV + enum
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

  private def defProps(objV: AbsValue, propsV: AbsValue, st: State): (State, Set[Exception]) = {
    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)
    // 2. Let props be ToObject(Properties).
    val addr = SystemAddr("Object.defineProperties<props>")
    val (v1, st1, toExcSet) = TypeConversionHelper.ToObject(propsV, st, addr)
    val h1 = st1.heap
    val ctx1 = st1.context
    val props = h1.get(v1.locset)
    // 4. For each enumerable property of props whose name String is P
    val keyStrSet = props.amap.abstractKeySet((key, dp) => {
      AbsBool.True <= dp.enumerable
    })
    val (retH, retExcSet) = objV.locset.foldLeft((h1, excSet ++ toExcSet)) {
      case ((heap, e), loc) => {
        val obj = h1.get(loc)
        val (retObj, excSet) = keyStrSet.foldLeft((obj, e)) {
          case ((obj, e), astr) => {
            // a. Let descObj be the result of calling the [[Get]] internal method of props with P as the argument.
            val descObjLoc = props.Get(astr, h1).locset
            if (!descObjLoc.isBottom) {
              val descObj = h1.get(descObjLoc)
              // b. Let desc be the result of calling ToPropertyDescriptor with descObj as the argument.
              val desc = AbsDesc.ToPropertyDescriptor(descObj, h1)
              // c. Call the [[DefineOwnProperty]] internal method of O with arguments P, desc, and true.
              val (retObj, _, excSet) = obj.DefineOwnProperty(astr, desc, true)
              (retObj, e ++ excSet)
            } else (obj, e)
          }
        }
        // 5. Return O.
        val retH = heap.update(loc, retObj)
        (retH, excSet)
      }
    }
    (State(retH, ctx1), retExcSet)
  }
}
