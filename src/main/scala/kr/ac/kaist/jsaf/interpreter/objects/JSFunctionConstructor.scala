/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter.objects

import kr.ac.kaist.jsaf.interpreter._
import kr.ac.kaist.jsaf.interpreter.{InterpreterPredefine => IP, _}

class JSFunctionConstructor(_I: Interpreter, _proto: JSObject)
  extends JSFunction13(_I, _proto, "Function", true, propTable, IP.undefFtn, EmptyEnv(), true) {
  def init(): Unit = {
    // 15.3.2 Properties of the Function Constructor
    property.put("length", I.IH.numProp(1))
    // 15.3.4 Properties of the Function Prototype Object
    property.put("prototype", I.IH.objProp(I.IS.FunctionPrototype))
  }

  override def __callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    // Call the prototype's method
    if (proto == IP.nullObj) I.IS.comp.setThrow(IP.referenceError(method + "from the Function Object"), I.IS.span)
    else proto.__callBuiltinFunction(method, argsObj)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // 15.3 Function Objects
  ////////////////////////////////////////////////////////////////////////////////

  // 15.3.1.1 Function(p1, p2, ... , pn, body)
  override def _call(tb: Val, argsObj: JSObject): Unit = I.IS.comp.setReturn(_construct(argsObj))

  // 15.3.2.1 new Function(p1, p2, ... , pn, body)
  override def _construct(argsObj: JSObject): JSObject = {
    // 1 ~ 10
    // TODO:
    
    // 11. Return a new Function object created as specified in 13.2 passing P as the FormalparameterList(opt) and
    //     body as the FunctionBody. Pass in the Global Environment as the Scope parameter and strict as the Strict flag.
    I.IH.createFunctionObject(IP.undefFtn, EmptyEnv(), I.IS.strict)
  }
}
