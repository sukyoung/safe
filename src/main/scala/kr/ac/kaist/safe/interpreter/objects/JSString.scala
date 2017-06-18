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
import kr.ac.kaist.safe.nodes.ir._

class JSString(
                _I: InterpreterMain,
                _proto: JSObject,
                _className: String,
                _extensible: Boolean,
                _property: PropTable
)
    extends JSObject(_I, _proto, _className, _extensible, _property) {
  /*
   * 15.5.5.2 [[GetOwnProperty]] ( P )
   */
  override def _getOwnProperty(x: PName): ObjectProp = {
    val desc: ObjectProp = super._getOwnProperty(x)
    if (desc != null) desc
    else {
      if (I.IH.toString(PVal(IRVal(I.IH.mkIRNum(scala.math.abs(I.IH.toInteger(PVal(I.IH.mkIRStrIR(x))).num))))) != x) null
      else {
        val str: String = I.IH.toString(_get(IP.pvpn))
        // TODO:
        val index: Int = I.IH.toInt32(PVal(I.IH.mkIRStrIR(x)))
        val len: Int = str.length
        if (len <= index) null
        else {
          val resultStr: String = str.substring(index, index + 1)
          I.IH.mkDataProp(PVal(I.IH.mkIRStrIR(resultStr)), false, true, false)
        }
      }
    }
  }
}
