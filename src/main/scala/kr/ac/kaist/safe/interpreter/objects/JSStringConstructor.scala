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
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util._

class JSStringConstructor(_I: Interpreter, _proto: JSObject)
    extends JSFunction13(_I, _proto, "String", true, propTable, _I.IH.dummyFtn(1), EmptyEnv(), true) {
  def init(): Unit = {
    /*
     * 15.5.3 Properties of the String Constructor
     */
    property.put("prototype", I.IH.mkDataProp(I.IS.StringPrototype))
    property.put("fromCharCode", I.IH.objProp(I.IS.StringFromCharCode))
  }

  /*
   * 15.5.2 The String Constructor
   * 15.5.2.1 new String ( [ value ] )
   */
  def construct(value: Option[Val]): JSString = {
    val s: String = value match {
      case Some(v) => I.IH.toString(v)
      case None => ""
    }
    val prop: PropTable = I.IH.strPropTable(s)
    prop.put("length", I.IH.numProp(s.length))
    new JSString(I, I.IS.StringPrototype, "String", true, prop)
  }

  override def construct(argsObj: JSObject): JSString = {
    argsObj.get("length") match {
      case PVal(IRVal(n: EJSNumber)) if n.num == 0 => construct(None)
      case PVal(IRVal(n: EJSNumber)) if n.num >= 1 => construct(Some(argsObj.get("0")))
    }
  }

  override def call(tb: Val, argsObj: JSObject): Unit = {
    argsObj.get("length") match {
      case PVal(IRVal(n: EJSNumber)) if n.num == 0 => I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR("")))
      case PVal(IRVal(n: EJSNumber)) if n.num >= 1 => I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR(I.IH.toString(argsObj.get("0")))))
    }
  }

  override def callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    method match {
      case I.IS.StringFromCharCode => _fromCharCode(I.IH.arrayToList(argsObj))
    }
  }

  def _fromCharCode(chars: List[Val]): Unit = {
    val str = chars.map((char: Val) => I.IH.toUint16(char).toChar).mkString
    I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR(str)))
  }
}
