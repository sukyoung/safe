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

package kr.ac.kaist.safe.interpreter.objects

import kr.ac.kaist.safe.interpreter._
import kr.ac.kaist.safe.interpreter.{ InterpreterPredefine => IP }
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util._

class JSArrayConstructor(I: Interpreter, proto: JSObject)
    extends JSFunction13(I, proto, "Array", true,
                         propTable, I.IH.dummyFtn(0), EmptyEnv(), true) {
  def init(): Unit = {
    /*
     * 15.4.3 Properties of the Array Constructor
     */
    // The "length" property initially has the attributes
    // { [[Writable]]:true, [[Enumerable]]:false, [[Configurable]]:false }
    property.put("length", I.IH.mkDataProp(PVal(IRVal(I.IH.mkIRNum(1))), true, false, false))
    // [[Writable]]: false, [[Enumerable]]: false, [[Configurable]]: false
    property.put("prototype", I.IH.mkDataProp(I.IS.ArrayPrototype))
    property.put("isArray", I.IH.objProp(I.IS.ArrayIsArray))
  }

  /*
   * 15.4.2 The Array Constructor
   * 15.4.2.1 new Array([item0[, item1[, ...]]])
   * [[Writable]]: true, [[Enumerable]]: true, [[Configurable]]: true
   */
  def construct(args: List[Val]): JSArray = {
    // The "length" property initially has the attributes
    // { [[Writable]]:true, [[Enumerable]]:false, [[Configurable]]:false }
    val prop: PropTable = propTable
    prop.put("length", I.IH.mkDataProp(PVal(IRVal(I.IH.mkIRNum(args.length))), true, false, false))
    for (i <- args.indices) {
      prop.put(i.toString, I.IH.mkDataProp(args(i), true, true, true))
    }
    new JSArray(I, I.IS.ArrayPrototype, "Array", true, prop)
  }
  /*
   * 15.4.2.2 new Array(len)
   */
  def construct(len: Val): JSArray = len match {
    case PVal(IRVal(n: EJSNumber)) if I.IH.toUint32(len) == n.num =>
      // The "length" property initially has the attributes
      // { [[Writable]]:true, [[Enumerable]]:false, [[Configurable]]:false }
      val prop = propTable
      prop.put("length", I.IH.mkDataProp(PVal(IRVal(n)), true, false, false))
      new JSArray(I, I.IS.ArrayPrototype, "Array", true, prop)
    case PVal(IRVal(n: EJSNumber)) => throw new RangeErrorException
    case _ =>
      // The "length" property initially has the attributes
      // { [[Writable]]:true, [[Enumerable]]:false, [[Configurable]]:false }
      val prop = propTable
      prop.put("length", I.IH.mkDataProp(PVal(IRVal(I.IH.mkIRNum(1))), true, false, false))
      prop.put("0", I.IH.mkDataProp(len, true, true, true))
      new JSArray(I, I.IS.ArrayPrototype, "Array", true, prop)
  }

  /*
   * 10.6 Arguments Object
   */
  def apply(len: Val, name: String): JSArray = len match {
    case PVal(IRVal(n: EJSNumber)) if I.IH.toUint32(len) == n.num =>
      // The "length" property initially has the attributes
      // { [[Writable]]:true, [[Enumerable]]:false, [[Configurable]]:false }
      val prop = propTable
      prop.put("length", I.IH.mkDataProp(PVal(IRVal(n)), true, false, false))
      new JSArray(I, I.IS.ObjectPrototype, name, true, prop)
    case PVal(IRVal(n: EJSNumber)) =>
      throw new RangeErrorException
    case _ =>
      // The "length" property initially has the attributes
      // { [[Writable]]:true, [[Enumerable]]:false, [[Configurable]]:false }
      val prop = propTable
      prop.put("length", I.IH.mkDataProp(PVal(IRVal(I.IH.mkIRNum(1))), true, false, false))
      prop.put("0", I.IH.mkDataProp(len, true, true, true))
      new JSArray(I, I.IS.ObjectPrototype, name, true, prop)
  }

  override def construct(argsObj: JSObject): JSArray = {
    argsObj.get("length") match {
      case PVal(IRVal(n: EJSNumber)) if n.num == 0 || n.num >= 2 =>
        construct(I.IH.arrayToList(argsObj))
      case PVal(IRVal(n: EJSNumber)) if n.num == 1 =>
        construct(argsObj.get("0"))
    }
  }

  override def callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    method match {
      case I.IS.ArrayIsArray =>
        JSIsArray(argsObj.get("0"))
    }
  }

  override def call(tb: Val, argsObj: JSObject): Unit = I.IS.comp.setReturn(construct(argsObj))

  /*
   * 15.4.3.2 Array.isArray(arg)
   */
  def JSIsArray(arg: Val): Unit = arg match {
    case a: JSArray => I.IS.comp.setReturn(PVal(IP.trueV))
    case _ => I.IS.comp.setReturn(PVal(IP.falseV))
  }
}
