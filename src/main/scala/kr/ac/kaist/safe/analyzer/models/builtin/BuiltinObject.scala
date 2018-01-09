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

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.util._
import scala.collection.immutable.HashSet

// 15.2 Object Objects
object BuiltinObject extends FuncModel(
  name = "Object",

  // 15.2.1 The Object Constructor Called as a Function: Object([value])
  code = BasicCode(
    argLen = 1,
    asiteSet = HashSet(BuiltinObjectHelper.instanceASite),
    code = BuiltinObjectHelper.construct
  ),

  // 15.2.2 The Object Constructor: new Object([value])
  construct = Some(BasicCode(
    argLen = 1,
    asiteSet = HashSet(BuiltinObjectHelper.instanceASite),
    code = BuiltinObjectHelper.construct
  )),

  // 15.2.3.1 Object.prototype
  protoModel = Some((BuiltinObjectProto, F, F, F)),

  props = List(
    // 15.2.3.2 Object.getPrototypeOf(O)
    NormalProp("getPrototypeOf", FuncModel(
      name = "Object.getPrototypeOf",
      code = BasicCode(argLen = 1, code = BuiltinObjectHelper.getPrototypeOf)
    ), T, F, T),

    // 15.2.3.3 getOwnPropertyDescriptor(O, P)
    NormalProp("getOwnPropertyDescriptor", FuncModel(
      name = "Object.getOwnPropertyDescriptor",
      code = BasicCode(
        argLen = 2,
        asiteSet = HashSet(BuiltinObjectHelper.getOPDDescASite),
        code = BuiltinObjectHelper.getOwnPropertyDescriptor
      )
    ), T, F, T),

    // 15.2.3.4 Object.getOwnPropertyNames(O)
    NormalProp("getOwnPropertyNames", FuncModel(
      name = "Object.getOwnPropertyNames",
      code = BasicCode(
        argLen = 1,
        asiteSet = HashSet(BuiltinObjectHelper.getOPNArrASite),
        code = BuiltinObjectHelper.getOwnPropertyNames
      )
    ), T, F, T),

    // 15.2.3.5 Object.create(O [, Properties])
    NormalProp("create", FuncModel(
      name = "Object.create",
      code = BasicCode(
        argLen = 2,
        asiteSet = HashSet(
          BuiltinObjectHelper.createObjASite,
          BuiltinObjectHelper.definePropsObjASite
        ),
        code = BuiltinObjectHelper.create
      )
    ), T, F, T),

    // 15.2.3.6 Object.defineProperty(O, P, Attributes)
    NormalProp("defineProperty", FuncModel(
      name = "Object.defineProperty",
      code = BasicCode(argLen = 3, code = BuiltinObjectHelper.defineProperty)
    ), T, F, T),

    // 15.2.3.7 Object.defineProperties(O, Properties)
    NormalProp("defineProperties", FuncModel(
      name = "Object.defineProperties",
      code = BasicCode(
        argLen = 2,
        asiteSet = HashSet(BuiltinObjectHelper.definePropsObjASite),
        code = BuiltinObjectHelper.defineProperties
      )
    ), T, F, T),

    // 15.2.3.8 Object.seal(O)
    NormalProp("seal", FuncModel(
      name = "Object.seal",
      code = BasicCode(argLen = 1, code = BuiltinObjectHelper.seal)
    ), T, F, T),

    // 15.2.3.9 Object.freeze(O)
    NormalProp("freeze", FuncModel(
      name = "Object.freeze",
      code = BasicCode(argLen = 1, code = BuiltinObjectHelper.freeze)
    ), T, F, T),

    // 15.2.3.10 Object.preventExtensions(O)
    NormalProp("preventExtensions", FuncModel(
      name = "Object.preventExtensions",
      code = BasicCode(argLen = 1, code = BuiltinObjectHelper.preventExtensions)
    ), T, F, T),

    // 15.2.3.11 Object.isSealed(O)
    NormalProp("isSealed", FuncModel(
      name = "Object.isSealed",
      code = BasicCode(argLen = 1, code = BuiltinObjectHelper.isSealed)
    ), T, F, T),

    // 15.2.3.12 Object.isFrozen(O)
    NormalProp("isFrozen", FuncModel(
      name = "Object.isFrozen",
      code = BasicCode(argLen = 1, code = BuiltinObjectHelper.isFrozen)
    ), T, F, T),

    // 15.2.3.13 Object.isExtensible(O)
    NormalProp("isExtensible", FuncModel(
      name = "Object.isExtensible",
      code = BasicCode(argLen = 1, code = BuiltinObjectHelper.isExtensible)
    ), T, F, T),

    // 15.2.3.14 Object.keys(O)
    NormalProp("keys", FuncModel(
      name = "Object.keys",
      code = BasicCode(
        argLen = 1,
        asiteSet = HashSet(BuiltinObjectHelper.keysArrASite),
        code = BuiltinObjectHelper.keys
      )
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
      code = BasicCode(
        argLen = 0,
        asiteSet = HashSet(BuiltinObjectHelper.toStringObjASite),
        code = BuiltinObjectHelper.toString
      )
    ), T, F, T),

    // 15.2.4.3 Object.prototype.toLocaleString()
    NormalProp("toLocaleString", FuncModel(
      name = "Object.prototype.toLocaleString",
      // TODO unsound: not use locale function.
      // we should fix this unsound manner by using CallCode.
      code = BasicCode(
        argLen = 0,
        asiteSet = HashSet(BuiltinObjectHelper.toStringObjASite),
        code = BuiltinObjectHelper.toString
      )
    ), T, F, T),

    // 15.2.4.4 Object.prototype.valueOf()
    NormalProp("valueOf", FuncModel(
      name = "Object.prototype.valueOf",
      code = BasicCode(
        argLen = 0,
        asiteSet = HashSet(BuiltinObjectHelper.valueOfObjASite),
        code = BuiltinObjectHelper.valueOf
      )
    ), T, F, T),

    // 15.2.4.5 Object.prototype.hasOwnProperty(V)
    NormalProp("hasOwnProperty", FuncModel(
      name = "Object.prototype.hasOwnProperty",
      code = BasicCode(
        argLen = 1,
        asiteSet = HashSet(BuiltinObjectHelper.hasOPObjASite),
        code = BuiltinObjectHelper.hasOwnProperty
      )
    ), T, F, T),

    // 15.2.4.6 Object.prototype.isPrototypeOf(V)
    NormalProp("isPrototypeOf", FuncModel(
      name = "Object.prototype.isPrototypeOf",
      code = BasicCode(
        argLen = 1,
        asiteSet = HashSet(BuiltinObjectHelper.isPObjASite),
        code = BuiltinObjectHelper.isPrototypeOf
      )
    ), T, F, T),

    // 15.2.4.7 Object.prototype.propertyIsEnumerable(V)
    NormalProp("propertyIsEnumerable", FuncModel(
      name = "Object.prototype.propertyIsEnumerable",
      code = BasicCode(
        argLen = 1,
        asiteSet = HashSet(BuiltinObjectHelper.propIsEObjASite),
        code = BuiltinObjectHelper.propertyIsEnumerable
      )
    ), T, F, T)
  )
)

object BuiltinObjectHelper {
  ////////////////////////////////////////////////////////////////
  // Predefined Allocation Site
  ////////////////////////////////////////////////////////////////
  val instanceASite = PredAllocSite("Object<instance>")
  val getOPDDescASite = PredAllocSite("Object.getOwnPropertyDescriptor<descriptor>")
  val getOPNArrASite = PredAllocSite("Object.getOwnPropertyNames<array>")
  val createObjASite = PredAllocSite("Object.create<object>")
  val keysArrASite = PredAllocSite("Object.keys<array>")
  val toStringObjASite = PredAllocSite("Object.prototype.toString<object>")
  val valueOfObjASite = PredAllocSite("Object.prototype.valueOf<object>")
  val hasOPObjASite = PredAllocSite("Object.prototype.hasOwnProperty<object>")
  val isPObjASite = PredAllocSite("Object.prototype.isPrototypeOf<object>")
  val propIsEObjASite = PredAllocSite("Object.prototype.propertyIsEnumerable<object>")
  val definePropsObjASite = PredAllocSite("Object.defineProperties<object>")

  ////////////////////////////////////////////////////////////////
  // Object
  ////////////////////////////////////////////////////////////////
  def construct(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val argV = Helper.propLoad(args, Set(AbsStr("0")), h)
    val asite = instanceASite

    // 1. If value is supplied and it is not null or undefined,
    //    then, return ToObject(value)
    //    XXX: We do not consider an implementation-dependent actions
    //         for a host objects)
    val (v1, st1) =
      if (argV.pvalue.copy(undefval = AbsUndef.Bot, nullval = AbsNull.Bot).isBottom && argV.locset.isBottom) (AbsValue.Bot, AbsState.Bot)
      else {
        val (loc, state, _) = TypeConversionHelper.ToObject(argV, st, asite)
        (AbsValue(loc), state)
      }

    // 2. Else, return a newly created native ECMAScript object.
    val pv = argV.pvalue
    val (v2, st2) =
      if (pv.undefval.isBottom && pv.nullval.isBottom) (AbsValue.Bot, AbsState.Bot)
      else newObjSt(st, asite)

    (st1 ⊔ st2, AbsState.Bot, v1 ⊔ v2)
  }

  def getPrototypeOf(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val argV = Helper.propLoad(args, Set(AbsStr("0")), h)

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(argV)

    // 2. Return the value of [[Prototype]] internal property of O.
    val protoV = argV.locset.foldLeft(AbsValue.Bot)((v, loc) => {
      v ⊔ h.get(loc)(IPrototype).value
    })

    val excSt = st.raiseException(excSet)

    (st, excSt, protoV)
  }

  def getOwnPropertyDescriptor(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsStr("0")), h)
    val propV = Helper.propLoad(args, Set(AbsStr("1")), h)

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
      val (descObj, excSet) = AbsObj.FromPropertyDescriptor(h, desc)
      val descLoc = Loc(getOPDDescASite)
      val state = st.oldify(descLoc)
      val retH = state.heap.update(descLoc, descObj.oldify(descLoc))
      val retV = AbsValue(undef, AbsLoc(descLoc))
      (AbsState(retH, state.context), retV, excSet)
    } else (st, AbsValue(undef), ExcSetEmpty)

    val excSt = st.raiseException(excSet1 ++ excSet2)

    (retSt, excSt, retV)
  }

  def getOwnPropertyNames(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsStr("0")), h)
    val (keyStr, lenSet) = objV.locset.foldLeft((AbsStr.Bot, Set[Option[Int]]())) {
      case ((str, lenSet), loc) => {
        val obj = h.get(loc)
        val (keys, size) = obj.collectKeySet("") match {
          case ConInf => (AbsStr.Top, None)
          case ConFin(set) => (AbsStr(set), Some(set.size))
        }
        (str ⊔ keys, lenSet + size)
      }
    }
    val (maxOpt, len) =
      if (lenSet.isEmpty) (None, AbsNum.Bot)
      else {
        val (opt, num) = lenSet.foldLeft[(Option[Int], AbsNum)]((Some(0), AbsNum.Bot)) {
          case ((None, _), _) | (_, None) => (None, AbsNum.Top)
          case ((Some(k), num), Some(t)) => (Some(math.max(k, t)), num ⊔ AbsNum(t))
        }
        (Some(opt), num)
      }

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)
    // 2. Let array be the result of creating a new Array object.
    // (XXX: we assign the length of the Array object as the number of properties)
    val array = AbsObj.newArrayObject(len)
    // 3. For each named own property P of O (with index n started from 0)
    //   a. Let name be the String value that is the name of P.
    val AT = (AbsBool.True, AbsAbsent.Bot)
    val name = AbsValue(AbsPValue(strval = keyStr))
    val desc = AbsDesc((name, AbsAbsent.Bot), AT, AT, AT)
    val (retObj, retExcSet) = maxOpt match {
      case Some(Some(max)) => (0 until max.toInt).foldLeft((array, excSet)) {
        case ((obj, e), n) => {
          val prop = AbsStr(n.toString)
          // b. Call the [[DefineOwnProperty]] internal method of array with arguments
          //    ToString(n), the PropertyDescriptor {[[Value]]: name, [[Writable]]:
          //    true, [[Enumerable]]: true, [[Configurable]]:true}, and false.
          val (newObj, _, excSet) = obj.DefineOwnProperty(prop, desc, false, h)
          (obj ⊔ newObj, e ++ excSet)
        }
      }
      case Some(None) => (AbsObj.Top, excSet + TypeError + RangeError)
      case None => (AbsObj.Bot, excSet)
    }

    // 5. Return array.
    retObj.isBottom match {
      case true => (AbsState.Bot, st.raiseException(retExcSet), AbsValue.Bot)
      case false => {
        val arrLoc = Loc(getOPNArrASite)
        val state = st.oldify(arrLoc)
        val retHeap = state.heap.update(arrLoc, retObj.oldify(arrLoc))
        val excSt = state.raiseException(retExcSet)

        (AbsState(retHeap, state.context), excSt, AbsValue(arrLoc))
      }
    }
  }

  def create(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsStr("0")), h)
    val propsV = Helper.propLoad(args, Set(AbsStr("1")), h)

    // 1. If Type(O) is not Object or Null throw a TypeError exception.
    val excSet =
      if (objV.pvalue.copy(nullval = AbsNull.Bot).isBottom) ExcSetEmpty
      else HashSet(TypeError)
    // 2. Let obj be the result of creating a new object.
    val obj = AbsObj.newObject
    // 3. Set the [[Prototype]] internal property of obj to O.
    val protoV = AbsValue(objV.locset) ⊔ objV.pvalue.nullval
    val newObj = obj.update(IPrototype, AbsIValue(protoV))
    // 4. If the argument Properties is present and not undefined, add own properties to obj as if by calling the
    //    standard built-in function Object.defineProperties with arguments obj and Properties.
    val loc = Loc(createObjASite)
    val state = st.oldify(loc)
    val newH = state.heap.update(loc, newObj.oldify(loc))
    val retV = AbsLoc(loc)
    val (retSt, e) =
      if (propsV ⊑ AbsUndef.Top) (AbsState(newH, state.context), ExcSetEmpty)
      else defProps(retV, propsV, AbsState(newH, state.context))
    // 5. Return obj.
    val excSt = state.raiseException(excSet ++ e)

    (retSt, excSt, retV)
  }

  def defineProperty(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsStr("0")), h)
    val propV = Helper.propLoad(args, Set(AbsStr("1")), h)
    val attrV = Helper.propLoad(args, Set(AbsStr("2")), h)

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)
    // 2. Let name be ToString(P).
    val name = TypeConversionHelper.ToString(propV)
    // 3. Let desc be the result of calling ToPropertyDescriptor with Attributes as the argument.
    val attr = h.get(attrV.locset)
    val notObjExcSet =
      if (!attrV.pvalue.isBottom) HashSet(TypeError)
      else ExcSetEmpty
    val desc = AbsDesc.ToPropertyDescriptor(attr, h)
    val (retH, retExcSet) = objV.locset.foldLeft((h, excSet ++ notObjExcSet)) {
      case ((heap, e), loc) => {
        // 4. Call the [[DefineOwnProperty]] internal method of O with arguments name, desc, and true.
        val obj = heap.get(loc)
        val (retObj, _, newExcSet) = obj.DefineOwnProperty(name, desc, true, h)
        // 5. Return O.
        val retH = heap.update(loc, retObj)
        (retH, e ++ newExcSet)
      }
    }

    val excSt = st.raiseException(retExcSet)

    (AbsState(retH, st.context), excSt, objV.locset)
  }

  def defineProperties(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsStr("0")), h)
    val propsV = Helper.propLoad(args, Set(AbsStr("1")), h)

    val (retSt, excSet) = defProps(objV, propsV, st)
    val excSt = st.raiseException(excSet) ⊔ retSt.raiseException(excSet)

    (retSt, excSt, objV.locset)
  }

  def seal(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsStr("0")), h)

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)

    val (retH, retExcSet) = objV.locset.foldLeft((h, excSet)) {
      case ((heap, e), loc) => {
        val obj = h.get(loc)
        // 2. For each named own property name P of O,
        //   a. Let desc be the result of calling the [[GetOwnProperty]] internal method of O with P.
        //   b. If desc.[[Configurable]] is true, set desc.[[Configurable]] to false.
        //   c. Call the [[DefineOwnProperty]] internal method of O with P, desc, and true as arguments.
        val (newObj, excSet) = changeProps(h, obj, desc => {
          val (c, ca) = desc.configurable
          val newConfig = c.fold(AbsBool.Bot)(_ => AbsBool.False)
          desc.copy(configurable = (newConfig, ca))
        })
        // 3. Set the [[Extensible]] internal property of O to false.
        val retObj = newObj.update(IExtensible, AbsIValue(AbsBool.False))
        // 4. Return O.
        val retH = heap.update(loc, retObj)
        (retH, e ++ excSet)
      }
    }

    val excSt = st.raiseException(retExcSet)

    (AbsState(retH, st.context), excSt, objV.locset)
  }

  def freeze(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsStr("0")), h)

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
        val (newObj, excSet) = changeProps(h, obj, desc => {
          val (w, wa) = desc.writable
          val (c, ca) = desc.configurable
          val newWriteable = w.fold(AbsBool.Bot)(_ => AbsBool.False)
          val newConfig = c.fold(AbsBool.Bot)(_ => AbsBool.False)
          desc.copy(
            writable = (newWriteable, wa),
            configurable = (newConfig, ca)
          )
        })
        // 3. Set the [[Extensible]] internal property of O to false.
        val retObj = newObj.update(IExtensible, AbsIValue(AbsBool.False))
        // 4. Return O.
        val retH = heap.update(loc, retObj)
        (retH, e ++ excSet)
      }
    }

    val excSt = st.raiseException(retExcSet)

    (AbsState(retH, st.context), excSt, objV.locset)
  }

  def preventExtensions(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsStr("0")), h)

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)
    val retH = objV.locset.foldLeft(h) {
      case (heap, loc) => {
        val obj = h.get(loc)
        // 2. Set the [[Extensible]] internal property of O to false.
        val retObj = obj.update(IExtensible, AbsIValue(AbsBool.False))
        // 3. Return O.
        heap.update(loc, retObj)
      }
    }

    val excSt = st.raiseException(excSet)

    (AbsState(retH, st.context), excSt, objV.locset)
  }

  def isSealed(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsStr("0")), h)

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)
    // 2. For each named own property name P of O,
    //   a. Let desc be the result of calling the [[GetOwnProperty]] internal method of O with P.
    //   b. If desc.[[Configurable]] is true, then return false.
    val obj = h.get(objV.locset)
    val cCheck = forall(obj, (desc, undef) => {
      val (c, ca) = desc.configurable
      c.negate ⊔ ca.fold(AbsBool.Bot)(_ => AT) ⊔ undef.fold(AbsBool.Bot)(_ => AT)
    })
    // 3. If the [[Extensible]] internal property of O is false, then return true.
    val eCheck = obj(IExtensible).value.pvalue.boolval.negate
    // 4. Otherwise, return false.
    val retB = cCheck && eCheck
    val excSt = st.raiseException(excSet)
    (st, excSt, retB)
  }

  def isFrozen(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsStr("0")), h)

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)
    // 2. For each named own property name P of O,
    //   a. Let desc be the result of calling the [[GetOwnProperty]] internal method of O with P.
    //   b. If desc.[[Writable]] is true, return false.
    //   c. If desc.[[Configurable]] is true, then return false.
    val obj = h.get(objV.locset)
    val cCheck = forall(obj, (desc, undef) => {
      val (w, wa) = desc.writable
      val (c, ca) = desc.configurable
      val undefB = undef.fold(AbsBool.Bot)(_ => AT)
      val otherB =
        (w.negate ⊔ wa.fold(AbsBool.Bot)(_ => AT)) && (c.negate ⊔ ca.fold(AbsBool.Bot)(_ => AT))
      undefB ⊔ otherB
    })
    // 3. If the [[Extensible]] internal property of O is false, then return true.
    val eCheck = obj(IExtensible).value.pvalue.boolval.negate
    // 4. Otherwise, return false.
    val retB = cCheck && eCheck
    val excSt = st.raiseException(excSet)
    (st, excSt, retB)
  }

  def isExtensible(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsStr("0")), h)

    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)
    // 2. Return the Boolean value of the [[Extensible]] internal property of O.
    val obj = h.get(objV.locset)
    val retB = obj(IExtensible).value.pvalue.boolval
    val excSt = st.raiseException(excSet)
    (st, excSt, retB)
  }

  def keys(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val objV = Helper.propLoad(args, Set(AbsStr("0")), h)
    val obj = h.get(objV.locset)
    val keyStr = obj.abstractKeySet((key, dp) => {
      AbsBool.True ⊑ dp.enumerable
    }) match {
      case ConInf => AbsStr.Top
      case ConFin(set) => set.foldLeft(AbsStr.Bot)(_ ⊔ _)
    }

    // 1. If the Type(O) is not Object, throw a TypeError exception.
    val objExcSet = objCheck(objV)

    val AT = (AbsBool.True, AbsAbsent.Bot)
    val name = AbsValue(AbsPValue(strval = keyStr))
    val desc = AbsDesc((name, AbsAbsent.Bot), AT, AT, AT)
    val (retObj, retExcSet) = keyStr.gamma match {
      case ConFin(set) if obj.isDefinite(keyStr) => {
        // 2. Let n be the number of own enumerable properties of O
        val n = set.size
        // 3. Let array be the result of creating a new Object as if by the ex pression new Array(n).
        val array = AbsObj.newArrayObject(AbsNum(n))
        // 4. For each own enumerable property of O whose name String is P (wiht index 0 until n)
        (0 until n).foldLeft((array, objExcSet)) {
          case ((arr, e), index) => {
            // a. Call the [[DefineOwnProperty]] internal method of array with arguments ToString(index),
            //    the PropertyDescriptor {[[Value]]: P, [[Writable]]: true, [[Enumerable]]: true, [[Configurable]]: true}, and false.
            val (newArr, _, excSet) = arr.DefineOwnProperty(AbsStr(index.toString), desc, false, h)
            (newArr, e ++ excSet)
          }
        }
      }
      case _ => {
        // 2. Let n be the number of own enumerable properties of O
        val n = AbsNum.Top
        // 3. Let array be the result of creating a new Object as if by the ex pression new Array(n).
        val array = AbsObj.newArrayObject(n)
        // 4. For each own enumerable property of O whose name String is P (wiht index 0 until n)
        //   a. Call the [[DefineOwnProperty]] internal method of array with arguments ToString(index),
        //      the PropertyDescriptor {[[Value]]: P, [[Writable]]: true, [[Enumerable]]: true, [[Configurable]]: true}, and false.
        val (newArr, _, excSet) = array.DefineOwnProperty(AbsStr.Number, desc, false, h)
        (newArr, objExcSet ++ excSet)
      }
    }
    // 6. Return array.
    val arrLoc = Loc(keysArrASite)
    val state = st.oldify(arrLoc)
    val retHeap = state.heap.update(arrLoc, retObj.oldify(arrLoc))
    val excSt = st.raiseException(retExcSet)

    (AbsState(retHeap, state.context), excSt, AbsValue(arrLoc))
  }

  ////////////////////////////////////////////////////////////////
  // Object.prototype
  ////////////////////////////////////////////////////////////////
  def toString(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val thisBinding = st.context.thisBinding
    val thisLoc = thisBinding.locset
    // 1. If the this value is undefined, return "[object Undefined]".
    val (checkU, undef) = thisBinding.pvalue.undefval.fold((false, AbsStr.Bot))(_ => (true, AbsStr("[object Undefined]")))
    // 2. If the this value is null, return "[object Null]".
    val (checkN, nu) = thisBinding.pvalue.nullval.fold((false, AbsStr.Bot))(_ => (true, AbsStr("[object Null]")))
    // 3. Let O be the result of calling ToObject passing the this value as the argument.
    val asite = toStringObjASite
    val (loc1, st1, _) = TypeConversionHelper.ToObject(thisBinding, st, asite)
    val obj = st1.heap.get(loc1)
    // 4. Let class be the value of the [[Class]] internal property of O.
    val className = obj(IClass).value.pvalue.strval
    // 5. Return the String value that is the result of concatenating the three Strings "[object ", class, and "]".
    val result = undef ⊔ nu ⊔ (AbsStr("[object ") concat className concat AbsStr("]"))
    val finalSt =
      if (checkU || checkN) st ⊔ st1
      else st1
    (finalSt, AbsState.Bot, result)
  }

  def valueOf(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val thisBinding = st.context.thisBinding
    // 1. Let O be the result of calling ToObject passing the this value as the argument.
    val asite = valueOfObjASite
    val (loc, state, excSet) = TypeConversionHelper.ToObject(thisBinding, st, asite)
    val excSt = st.raiseException(excSet)

    // 2. Return O.
    (state, excSt, AbsValue(loc))
  }

  def hasOwnProperty(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val h = st.heap
    val value = Helper.propLoad(args, Set(AbsStr("0")), h)
    val thisBinding = st.context.thisBinding
    // 1. Let P be ToString(V).
    val prop = TypeConversionHelper.ToString(value)
    // 2. Let O be the result of calling ToObject passing the this value as the argument.
    val asite = hasOPObjASite
    val (thisLoc, state, excSet) = TypeConversionHelper.ToObject(thisBinding, st, asite)
    // 3. Let desc be the result of calling the [[GetOwnProperty]] internal method of O passing P as the argument.
    val obj = state.heap.get(thisLoc)
    val (desc, undef) = obj.GetOwnProperty(prop)
    // 4. If desc is undefined, return false.
    val falseV = undef.fold(AbsBool.Bot)(_ => AbsBool.False)
    // 5. Return true.
    val trueV = desc.fold(AbsBool.Bot)(_ => AbsBool.True)
    val excSt = st.raiseException(excSet)

    (state, excSt, falseV ⊔ trueV)
  }

  def isPrototypeOf(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val value = Helper.propLoad(args, Set(AbsStr("0")), st.heap)
    val thisBinding = st.context.thisBinding
    // 1. If V is not an object, return false.
    val v1 = value.pvalue.fold(AbsBool.Bot)(_ => AbsBool.False)
    // 2. Let O be the result of calling ToObject passing the this value as the argument.
    val asite = isPObjASite
    val (thisLoc, state, excSet) = TypeConversionHelper.ToObject(thisBinding, st, asite)
    val h = state.heap
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
          if (AbsLoc(loc) ⊑ thisLoc) AbsBool.True
          else AbsBool.Bot
        value.locset.foldLeft(falseV ⊔ trueV)(_ ⊔ repeat(_))
      }
    }
    val result = value.locset.foldLeft(v1)(_ ⊔ repeat(_))
    val excSt = st.raiseException(excSet)
    (state, excSt, result)
  }

  def propertyIsEnumerable(args: AbsValue, st: AbsState): (AbsState, AbsState, AbsValue) = {
    val value = Helper.propLoad(args, Set(AbsStr("0")), st.heap)
    val thisBinding = st.context.thisBinding
    // 1. Let P be ToString(V).
    val prop = TypeConversionHelper.ToString(value)
    // 2. Let O be the result of calling ToObject passing the this value as the argument.
    val asite = propIsEObjASite
    val (thisLoc, state, excSet) = TypeConversionHelper.ToObject(thisBinding, st, asite)
    val h = state.heap
    // 3. Let desc be the result of calling the [[GetOwnProperty]] internal method of O passing P as the argument.
    val obj = h.get(thisLoc)
    val (desc, undef) = obj.GetOwnProperty(prop)
    // 4. If desc is undefined, return false.
    val undefV = undef.fold(AbsBool.Bot)(_ => AbsBool.False)
    // 5. Return the value of desc.[[Enumerable]].
    val (enum, _) = desc.enumerable
    val result = undefV ⊔ enum
    val excSt = st.raiseException(excSet)
    (state, excSt, result)
  }

  ////////////////////////////////////////////////////////////////
  // private helper functions
  ////////////////////////////////////////////////////////////////
  private def objCheck(value: AbsValue): Set[Exception] = {
    if (value.pvalue.isBottom) ExcSetEmpty
    else HashSet(TypeError)
  }

  private def newObjSt(st: AbsState, asite: PredAllocSite): (AbsValue, AbsState) = {
    val loc = Loc(asite)
    val state = st.oldify(loc)
    val obj = AbsObj.newObject
    val heap = state.heap.update(loc, obj)
    (AbsValue(loc), AbsState(heap, state.context))
  }

  private def changeProps(h: AbsHeap, obj: AbsObj, f: AbsDesc => AbsDesc): (AbsObj, Set[Exception]) = {
    // For each named own property name P of O,
    obj.abstractKeySet match {
      case ConInf => (AbsObj.Top, HashSet(TypeError, RangeError))
      case ConFin(set) => set.foldLeft(obj, ExcSetEmpty) {
        case ((o, e), key) => {
          // Let desc be the result of calling the [[GetOwnProperty]] internal method of O with P.
          val (desc, _) = obj.GetOwnProperty(key)
          if (!desc.isBottom) {
            // create new PropertyDescriptor by using f.
            val newDesc = f(desc)
            // Call the [[DefineOwnProperty]] internal method of O with P, desc, and true as arguments.
            val (retObj, _, excSet) = o.DefineOwnProperty(key, newDesc, true, h)
            (retObj, e ++ excSet)
          } else (o, e)
        }
      }
    }
  }

  private def forall(obj: AbsObj, f: (AbsDesc, AbsUndef) => AbsBool): AbsBool = {
    if (obj.isBottom) AbsBool.Bot
    else {
      // For each named own property name P of O,
      obj.abstractKeySet match {
        case ConInf => AbsBool.Top
        case ConFin(set) => set.foldLeft(AbsBool.True) {
          case (b, key) => {
            // Let desc be the result of calling the [[GetOwnProperty]] internal method of O with P.
            val (desc, undef) = obj.GetOwnProperty(key)
            // Check by using f.
            b && f(desc, undef)
          }
        }
      }
    }
  }

  private def defProps(objV: AbsValue, propsV: AbsValue, st: AbsState): (AbsState, Set[Exception]) = {
    // 1. If Type(O) is not Object throw a TypeError exception.
    val excSet = objCheck(objV)
    // 2. Let props be ToObject(Properties).
    val asite = definePropsObjASite
    val (loc1, st1, toExcSet) = TypeConversionHelper.ToObject(propsV, st, asite)
    val h1 = st1.heap
    val ctx1 = st1.context
    val props = h1.get(loc1)
    // 4. For each enumerable property of props whose name String is P
    val keyStrSet = props.abstractKeySet((key, dp) => {
      AbsBool.True ⊑ dp.enumerable
    })
    val (retH, retExcSet) = objV.locset.foldLeft((h1, excSet ++ toExcSet)) {
      case ((heap, e), loc) => {
        val obj = h1.get(loc)
        val (retObj: AbsObj, excSet: Set[Exception]) = keyStrSet match {
          case ConInf => (AbsObj.Top, HashSet(TypeError, RangeError))
          case ConFin(set) => set.foldLeft((obj, e)) {
            case ((obj, e), astr) => {
              // a. Let descObj be the result of calling the [[Get]] internal method of props with P as the argument.
              val desc = props.Get(astr, h1)
              val descObjLoc = desc.locset
              val (obj1, excSet1) =
                if (!descObjLoc.isBottom) {
                  val descObj = h1.get(descObjLoc)
                  // b. Let desc be the result of calling ToPropertyDescriptor with descObj as the argument.
                  val desc = AbsDesc.ToPropertyDescriptor(descObj, h1)
                  // c. Call the [[DefineOwnProperty]] internal method of O with arguments P, desc, and true.
                  val (retObj, _, excSet) = obj.DefineOwnProperty(astr, desc, true, h1)
                  (retObj, e ++ excSet)
                } else (obj, e)
              val (obj2, excSet2) =
                if (!desc.pvalue.isBottom) {
                  (AbsObj.Bot, e + TypeError)
                } else (obj, e)
              (obj1 ⊔ obj2, excSet1 ++ excSet2)
            }
          }
        }
        // 5. Return O.
        val retH = heap.update(loc, retObj)
        (retH, excSet)
      }
    }
    (AbsState(retH, ctx1), retExcSet)
  }
}
