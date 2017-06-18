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

class JSErrorConstructor(_I: InterpreterMain, _proto: JSObject)
    extends JSFunction13(_I, _proto, "Function", true,
      propTable, _I.IH.dummyFtn(1), EmptyEnv(), true) {
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
  override def _construct(argsObj: JSObject): JSErrorObject =
    construct(argsObj._get("0"))

  /*
   * 15.11.1 The Error Constructor Called as a Function
   */
  override def _call(tb: Val, argsObj: JSObject): Unit =
    I.IS.comp.setReturn(_construct(argsObj))
}
