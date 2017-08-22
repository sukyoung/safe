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

class JSBooleanPrototype(I: Interpreter, proto: JSObject)
    extends JSBoolean(I, proto, "Boolean", true, propTable) {
  def init(): Unit = {
    /*
     * 15.6.3 Properties of the Boolean Constructor
     * { [[Writable]]: false, [[Enumerable]]: false, [[Configurable]]: false }
     */
    property.put(IP.pvpn, I.IH.boolProp(false))
    property.put("constructor", I.IH.objProp(I.IS.BooleanConstructor))
    property.put("toString", I.IH.objProp(I.IS.BooleanPrototypeToString))
    property.put("valueOf", I.IH.objProp(I.IS.BooleanPrototypeValueOf))
  }

  override def callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    method match {
      case I.IS.BooleanPrototypeToString => JSToString()
      case I.IS.BooleanPrototypeValueOf => JSValueOf()
    }
  }

  /*
   * 15.6.4.2 Boolean.prototype.toString ( )
   */
  def JSToString(): Unit = {
    I.IS.tb match {
      case PVal(IRVal(EJSBool(b))) =>
        I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR(b.toString)))
      case o: JSBoolean =>
        val b: Val = o.get(IP.pvpn)
        I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR(I.IH.toBoolean(b).toString)))
      case _ =>
        I.IS.comp.setThrow(IP.typeError, I.IS.span)
    }
  }

  /*
   * 15.6.4.3 Boolean.prototype.valueOf ( )
   */
  def JSValueOf(): Unit = {
    I.IS.tb match {
      case b @ PVal(IRVal(EJSBool(_))) =>
        I.IS.comp.setReturn(b)
      case o: JSBoolean =>
        val b: Val = o.get(IP.pvpn)
        I.IS.comp.setReturn(b)
      case _ =>
        I.IS.comp.setThrow(IP.typeError, I.IS.span)
    }
  }
}
