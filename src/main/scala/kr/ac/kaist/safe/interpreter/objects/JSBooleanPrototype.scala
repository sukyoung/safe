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

package kr.ac.kaist.safe.interpreter.objects

import kr.ac.kaist.safe.interpreter._
import kr.ac.kaist.safe.interpreter.{ InterpreterPredefine => IP }
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util._

class JSBooleanPrototype(_I: Interpreter, _proto: JSObject)
    extends JSBoolean(_I, _proto, "Boolean", true, propTable) {
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

  override def __callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    method match {
      case I.IS.BooleanPrototypeToString => _toString()
      case I.IS.BooleanPrototypeValueOf => _valueOf()
    }
  }

  /*
   * 15.6.4.2 Boolean.prototype.toString ( )
   */
  def _toString(): Unit = {
    I.IS.tb match {
      case PVal(IRVal(EJSBool(b))) =>
        I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR(b.toString)))
      case o: JSBoolean =>
        val b: Val = o._get(IP.pvpn)
        I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR(I.IH.toBoolean(b).toString)))
      case _ =>
        I.IS.comp.setThrow(IP.typeError, I.IS.span)
    }
  }

  /*
   * 15.6.4.3 Boolean.prototype.valueOf ( )
   */
  def _valueOf(): Unit = {
    I.IS.tb match {
      case b @ PVal(IRVal(EJSBool(_))) =>
        I.IS.comp.setReturn(b)
      case o: JSBoolean =>
        val b: Val = o._get(IP.pvpn)
        I.IS.comp.setReturn(b)
      case _ =>
        I.IS.comp.setThrow(IP.typeError, I.IS.span)
    }
  }
}
