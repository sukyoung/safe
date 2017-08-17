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

class JSBooleanConstructor(I: Interpreter, proto: JSObject)
    extends JSFunction13(I, proto, "Function", true,
      propTable, I.IH.dummyFtn(1), EmptyEnv(), true) {
  def init(): Unit = {
    /*
     * 15.6.3 Properties of the Boolean Constructor
     * { [[Writable]]: false, [[Enumerable]]: false, [[Configurable]]: false }
     */
    property.put("length", I.IH.numProp(1))
    property.put("prototype", I.IH.mkDataProp(I.IS.BooleanPrototype))
  }

  /*
   * 15.6.2 The Boolean Constructor
   * 15.6.2.1 new Boolean (value)
   */
  def construct(value: Val): JSBoolean = {
    new JSBoolean(I, I.IS.BooleanPrototype, "Boolean", true,
      I.IH.boolPropTable(I.IH.toBoolean(value)))
  }

  override def construct(argsObj: JSObject): JSBoolean = construct(argsObj.get("0"))

  /*
   * 15.6.1 The Boolean Constructor Called as a Function
   * 15.6.1.1 Boolean (value)
   */
  override def call(tb: Val, argsObj: JSObject): Unit = {
    I.IS.comp.setReturn(PVal(IRVal(I.IH.mkIRBool(I.IH.toBoolean(argsObj.get("0"))))))
  }
}
