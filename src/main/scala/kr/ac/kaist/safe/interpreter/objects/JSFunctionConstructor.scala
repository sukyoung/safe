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

class JSFunctionConstructor(_I: Interpreter, _proto: JSObject)
    extends JSFunction13(_I, _proto, "Function", true, propTable, IP.undefFtn, EmptyEnv(), true) {
  def init(): Unit = {
    // 15.3.2 Properties of the Function Constructor
    property.put("length", I.IH.numProp(1))
    // 15.3.4 Properties of the Function Prototype Object
    property.put("prototype", I.IH.objProp(I.IS.FunctionPrototype))
  }

  override def callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    // Call the prototype's method
    if (proto == IP.nullObj) I.IS.comp.setThrow(IP.referenceError(method + "from the Function Object"), I.IS.span)
    else proto.callBuiltinFunction(method, argsObj)
  }

  ////////////////////////////////////////////////////////////////////////////////
  // 15.3 Function Objects
  ////////////////////////////////////////////////////////////////////////////////

  // 15.3.1.1 Function(p1, p2, ... , pn, body)
  override def call(tb: Val, argsObj: JSObject): Unit = I.IS.comp.setReturn(construct(argsObj))

  // 15.3.2.1 new Function(p1, p2, ... , pn, body)
  override def construct(argsObj: JSObject): JSObject = {
    // 1 ~ 10
    // TODO:

    // 11. Return a new Function object created as specified in 13.2 passing P as the FormalparameterList(opt) and
    //     body as the FunctionBody. Pass in the Global Environment as the Scope parameter and strict as the Strict flag.
    I.IH.createFunctionObject(IP.undefFtn, EmptyEnv(), I.IS.strict)
  }
}
