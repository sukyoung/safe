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

class JSSyntaxErrorConstructor(I: Interpreter, proto: JSObject)
    extends JSFunction13(I, proto, "Function", true,
                         propTable, I.IH.dummyFtn(1), EmptyEnv(), true) {
  def init(): Unit = {
    /*
     * 15.11.7.5 Properties of the NativeError Constructors
     */
    property.put("prototype", I.IH.mkDataProp(I.IS.SyntaxErrorPrototype))
  }

  /*
   * 15.11.7.4 new NativeError (message)
   */
  def construct(message: Val): JSErrorObject =
    new JSErrorObject(I, I.IS.SyntaxErrorPrototype, "Error", true,
      if (I.IH.isUndef(message)) propTable
      else I.IH.strPropTable(I.IH.toString(message)))

  /*
   * 15.11.7.3 The NativeError Constructors
   */
  override def construct(argsObj: JSObject): JSErrorObject =
    construct(argsObj.get("0"))

  /*
   * 15.11.7.1 NativeError Constructors Called as Functions
   */
  override def call(tb: Val, argsObj: JSObject): Unit =
    I.IS.comp.setReturn(construct(argsObj))
}
