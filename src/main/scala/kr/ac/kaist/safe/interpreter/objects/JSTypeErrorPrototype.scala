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

class JSTypeErrorPrototype(_I: Interpreter, _proto: JSObject)
    extends JSErrorObject(_I, _proto, "Error", true, propTable) {
  def init(): Unit = {
    /*
     * 15.11.7.7 Properties of the NativeError Prototype Objects
     */
    property.put("constructor", I.IH.objProp(I.IS.TypeErrorConstructor))
    property.put("name", I.IH.strProp("TypeError"))
    property.put("message", I.IH.strProp(""))
  }
}
