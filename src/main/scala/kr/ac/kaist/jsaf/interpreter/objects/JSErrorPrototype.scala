/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter.objects

import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.interpreter.{InterpreterPredefine => IP, _}

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

  override def __callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    method match {
      case I.IS.ErrorPrototypeToString => _toString()
    }
  }

  def _toString(): Unit = {
    I.IS.tb match {
      case o: JSObject =>
        val name: String = o._get("name") match {
          case name if I.IH.isUndef(name) => "Error"
          case name => I.IH.toString(name)
        }
        val msg: String = o._get("message") match {
          case msg if I.IH.isUndef(msg) => ""
          case msg => I.IH.toString(msg)
        }
        if (name == "") I.IS.comp.setReturn(PVal(I.IH.mkIRStr(msg)))
        else if (msg == "") I.IS.comp.setReturn(PVal(I.IH.mkIRStr(name)))
        else I.IS.comp.setReturn(PVal(I.IH.mkIRStr(name + ": " + msg)))
      case _ => I.IS.comp.setThrow(IP.typeError, I.IS.span)
    }
  }
}
