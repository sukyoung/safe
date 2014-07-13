/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter.objects

import kr.ac.kaist.jsaf.interpreter.{InterpreterPredefine => IP, _}

class JSObjectPrototype(_I: Interpreter, _proto: JSObject)
  extends JSObject(_I, _proto, "Object", true, propTable) {
  def init(): Unit = {
    // 15.2.4 Properties of the Object Prototype Object
    property.put("constructor", I.IH.objProp(I.IS.ObjectConstructor))
    property.put("toString", I.IH.objProp(I.IS.ObjectPrototypeToString))
    /*
    property.put("toLocaleString", IH.objProp(IS.ObjectPrototypeToLocaleString))
    property.put("valueOf", IH.objProp(IS.ObjectPrototypeValueOf))
    */
    property.put("hasOwnProperty", I.IH.objProp(I.IS.ObjectPrototypeHasOwnProperty))
    /*
    property.put("isPrototypeOf", IH.objProp(IS.ObjectPrototypeIsPrototypeOf))
    property.put("propertyIsEnumerable", IH.objProp(IS.ObjectPrototypePropertyIsEnumerable))
    */
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Basic
  ////////////////////////////////////////////////////////////////////////////////

  override def __callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    val args: Array[Val] = I.IH.argsObjectToArray(argsObj, 1)
    method match {
      case I.IS.ObjectPrototypeToString => _toString()
      /*
      case IS.ObjectPrototypeToLocaleString => toLocaleString()
      case IS.ObjectPrototypeValueOf => valueOf()
      */
      case I.IS.ObjectPrototypeHasOwnProperty => _hasOwnProperty(args(0))
      /*
      case IS.ObjectPrototypeIsPrototypeOf => isPrototypeOf(args(0))
      case IS.ObjectPrototypePropertyIsEnumerable => propertyIsEnumerable(args(0))
      */
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // 15.2.4 Properties of the Object Prototype Object
  ////////////////////////////////////////////////////////////////////////////////

  // 15.2.4.2 toString()
  def _toString(): Unit = I.IS.tb match {
    case tb if I.IH.isUndef(tb) => I.IS.comp.setReturn(PVal(I.IH.mkIRStr("[object Undefined]")))
    case tb if I.IH.isNull(tb) => I.IS.comp.setReturn(PVal(I.IH.mkIRStr("[object Null]")))
    case tb =>
      val o = I.IH.toObject(tb).asInstanceOf[JSObject]
      I.IS.comp.setReturn(PVal(I.IH.mkIRStr("[object "+o.className+"]")))
  }

  /*
  // 15.2.4.3 toLocaleString()
  // TODO:
  def toLocaleString(): Unit = throwErr(nyiError, IS.Span)

  // 15.2.4.4 valueOf()
  // TODO:
  def valueOf(): Unit = throwErr(nyiError, IS.Span)
  */

  // 15.2.4.5 hasOwnProperty(v)
  def _hasOwnProperty(v: Val): Unit = {
    val p: String = I.IH.toString(v)
    I.IH.toObject(I.IS.tb) match {
      case o: JSObject =>
        I.IS.comp.setReturn(PVal(I.IH.mkIRBool(o._getOwnProperty(p) != null)))
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  /*
  // 15.2.4.6 isPrototypeOf(v)
  // TODO:
  def isPrototypeOf(v: Val): Unit = throwErr(nyiError, IS.Span)

  // 15.2.4.7 propertyIsEnumerable(v)
  // TODO:
  def propertyIsEnumerable(v: Val): Unit = throwErr(nyiError, IS.Span)
  */
}
