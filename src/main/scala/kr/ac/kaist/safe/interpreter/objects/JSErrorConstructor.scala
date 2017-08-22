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

class JSErrorConstructor(I: Interpreter, proto: JSObject)
    extends JSFunction13(I, proto, "Function", true,
      propTable, I.IH.dummyFtn(1), EmptyEnv(), true) {
  def init(): Unit = {
    /*
     * 15.11.3 Properties of the Error Constructor
     */
    property.put("prototype", I.IH.mkDataProp(I.IS.ErrorPrototype))
  }

  /*
   * 15.11.2.1 new Error (message)
   */
  def construct(message: Val): JSErrorObject =
    new JSErrorObject(I, I.IS.ErrorPrototype, "Error", true,
      if (I.IH.isUndef(message)) propTable
      else I.IH.strPropTable(I.IH.toString(message)))

  /*
   * 15.11.2 The Error Constructor
   */
  override def construct(argsObj: JSObject): JSErrorObject =
    construct(argsObj.get("0"))

  /*
   * 15.11.1 The Error Constructor Called as a Function
   */
  override def call(tb: Val, argsObj: JSObject): Unit =
    I.IS.comp.setReturn(construct(argsObj))
}
