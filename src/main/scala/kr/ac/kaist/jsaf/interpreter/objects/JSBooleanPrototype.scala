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
      case PVal(SIRBool(_, b)) =>
        I.IS.comp.setReturn(PVal(I.IH.mkIRStr(b.toString)))
      case o: JSBoolean =>
        val b: Val = o._get(IP.pvpn)
        I.IS.comp.setReturn(PVal(I.IH.mkIRStr(I.IH.toBoolean(b).toString)))
      case _ =>
        I.IS.comp.setThrow(IP.typeError, I.IS.span)
    }
  }

  /*
   * 15.6.4.3 Boolean.prototype.valueOf ( )
   */
  def _valueOf(): Unit = {
    I.IS.tb match {
      case b@PVal(SIRBool(_,_)) =>
        I.IS.comp.setReturn(b)
      case o: JSBoolean =>
        val b: Val = o._get(IP.pvpn)
        I.IS.comp.setReturn(b)
      case _ =>
        I.IS.comp.setThrow(IP.typeError, I.IS.span)
    }
  }
}
