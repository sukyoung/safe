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

class JSErrorPrototype(_I: Interpreter, _proto: JSObject)
    extends JSErrorObject(_I, _proto, "Error", true, propTable) {
  def init(): Unit = {
    /*
     * 15.11.4 Properties of the Error Prototype Object
     */
    property.put("constructor", I.IH.objProp(I.IS.ErrorConstructor))
    property.put("name", I.IH.strProp("Error"))
    property.put("message", I.IH.strProp(""))
    property.put("toString", I.IH.objProp(I.IS.ErrorPrototypeToString))
  }

  override def callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    method match {
      case I.IS.ErrorPrototypeToString => _toString()
    }
  }

  def _toString(): Unit = {
    I.IS.tb match {
      case o: JSObject =>
        val name: String = o.get("name") match {
          case name if I.IH.isUndef(name) => "Error"
          case name => I.IH.toString(name)
        }
        val msg: String = o.get("message") match {
          case msg if I.IH.isUndef(msg) => ""
          case msg => I.IH.toString(msg)
        }
        if (name == "") I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR(msg)))
        else if (msg == "") I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR(name)))
        else I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR(name + ": " + msg)))
      case _ => I.IS.comp.setThrow(IP.typeError, I.IS.span)
    }
  }
}
