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

class JSString(_I: Interpreter,
               _proto: JSObject,
               _className: String,
               _extensible: Boolean,
               _property: PropTable)
  extends JSObject(_I, _proto, _className, _extensible, _property) {
  /*
   * 15.5.5.2 [[GetOwnProperty]] ( P )
   */
  override def _getOwnProperty(x: PName): ObjectProp = {
    val desc: ObjectProp = super._getOwnProperty(x)
    if (desc != null) desc
    else {
      if (I.IH.toString(PVal(I.IH.mkIRNum(scala.math.abs(I.IH.toInteger(PVal(I.IH.mkIRStr(x))).getNum)))) != x) null
      else {
        val str: String = I.IH.toString(_get(IP.pvpn))
        // TODO:
        val index: Int = I.IH.toInt32(PVal(I.IH.mkIRStr(x)))
        val len: Int = str.length
        if (len <= index) null
        else {
          val resultStr: String = str.substring(index, index + 1)
          I.IH.mkDataProp(PVal(I.IH.mkIRStr(resultStr)), false, true, false)
        }
      }
    }
  }
}
