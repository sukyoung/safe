/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter.objects

import kr.ac.kaist.jsaf.nodes.IRNumber
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.interpreter.{InterpreterPredefine => IP, _}

class JSArrayConstructor(_I: Interpreter, _proto: JSObject)
  extends JSFunction13(_I, _proto, "Array", true,
                       propTable, _I.IH.dummyFtn(0), EmptyEnv(), true) {
  def init(): Unit = {
    /*
     * 15.4.3 Properties of the Array Constructor
     */
    // The "length" property initially has the attributes
    // { [[Writable]]:true, [[Enumerable]]:false, [[Configurable]]:false }
    property.put("length", I.IH.mkDataProp(PVal(I.IH.mkIRNum(1)), true, false, false))
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
    var prop: PropTable = propTable
    prop.put("length", I.IH.mkDataProp(PVal(I.IH.mkIRNum(args.length)), true, false, false))
    for (i <- 0 until args.length) {
      prop.put(i.toString, I.IH.mkDataProp(args(i), true, true, true))
    }
    new JSArray(I, I.IS.ArrayPrototype, "Array", true, prop)
  }
  /*
   * 15.4.2.2 new Array(len)
   */
  def construct(len: Val): JSArray = len match {
    case PVal(n:IRNumber) if I.IH.toUint32(len) == n.getNum =>
      // The "length" property initially has the attributes
      // { [[Writable]]:true, [[Enumerable]]:false, [[Configurable]]:false }
      val prop = propTable
      prop.put("length", I.IH.mkDataProp(PVal(n), true, false, false))
      new JSArray(I, I.IS.ArrayPrototype, "Array", true, prop)
    case PVal(n:IRNumber) => throw new RangeErrorException
    case _ =>
      // The "length" property initially has the attributes
      // { [[Writable]]:true, [[Enumerable]]:false, [[Configurable]]:false }
      val prop = propTable
      prop.put("length", I.IH.mkDataProp(PVal(I.IH.mkIRNum(1)), true, false, false))
      prop.put("0", I.IH.mkDataProp(len, true, true, true))
      new JSArray(I, I.IS.ArrayPrototype, "Array", true, prop)
  }

  /*
   * 10.6 Arguments Object
   */
  def apply(len: Val, name: String): JSArray = len match {
    case PVal(n:IRNumber) if I.IH.toUint32(len) == n.getNum =>
      // The "length" property initially has the attributes
      // { [[Writable]]:true, [[Enumerable]]:false, [[Configurable]]:false }
      val prop = propTable
      prop.put("length", I.IH.mkDataProp(PVal(n), true, false, false))
      new JSArray(I, I.IS.ObjectPrototype, name, true, prop)
    case PVal(n:IRNumber) => throw new RangeErrorException
    case _ =>
      // The "length" property initially has the attributes
      // { [[Writable]]:true, [[Enumerable]]:false, [[Configurable]]:false }
      val prop = propTable
      prop.put("length", I.IH.mkDataProp(PVal(I.IH.mkIRNum(1)), true, false, false))
      prop.put("0", I.IH.mkDataProp(len, true, true, true))
      new JSArray(I, I.IS.ObjectPrototype, name, true, prop)
  }

  override def _construct(argsObj: JSObject): JSArray = {
    argsObj._get("length") match {
      case PVal(n:IRNumber) if n.getNum == 0 || n.getNum >= 2 => construct(I.IH.arrayToList(argsObj))
      case PVal(n:IRNumber) if n.getNum == 1 => construct(argsObj._get("0"))
    }
  }

  override def __callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    method match {
      case I.IS.ArrayIsArray => _isArray(argsObj._get("0"))
    }
  }

  override def _call(tb: Val, argsObj: JSObject): Unit = I.IS.comp.setReturn(_construct(argsObj))

  /*
   * 15.4.3.2 Array.isArray(arg)
   */
  def _isArray(arg: Val): Unit = arg match {
    case a:JSArray => I.IS.comp.setReturn(PVal(IP.trueV))
    case _ => I.IS.comp.setReturn(PVal(IP.falseV))
  }
}
