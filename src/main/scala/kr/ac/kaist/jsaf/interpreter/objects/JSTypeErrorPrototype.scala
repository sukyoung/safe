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
