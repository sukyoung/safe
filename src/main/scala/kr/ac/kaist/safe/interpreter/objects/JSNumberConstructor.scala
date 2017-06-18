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
import kr.ac.kaist.safe.util._

class JSNumberConstructor(_I: InterpreterMain, _proto: JSObject)
    extends JSFunction13(_I, _proto, "Function", true,
      propTable, _I.IH.dummyFtn(1), EmptyEnv(), true) {
  def init(): Unit = {
    /*
     * 15.7.3 Properties of the Number Constructor
     * { [[Writable]]: false, [[Enumerable]]: false, [[Configurable]]: false }
     */
    property.put("length", I.IH.numProp(1))
    property.put("prototype", I.IH.mkDataProp(I.IS.NumberPrototype))
    property.put("MAX_VALUE", I.IH.numProp(1.7976931348623157e+308))
    property.put("MIN_VALUE", I.IH.numProp(5e-324))
    property.put("NaN", I.IH.mkDataProp(PVal(IRVal(IP.NaN))))
    property.put("NEGATIVE_INFINITY", I.IH.mkDataProp(PVal(IRVal(IP.minusInfinity))))
    property.put("POSITIVE_INFINITY", I.IH.mkDataProp(PVal(IRVal(IP.plusInfinity))))
  }

  /*
   * 15.7.2 The Number Constructor
   * 15.7.2.1 new Number ( [ value ] )
   */
  def construct(value: Option[Val]): JSNumber = {
    new JSNumber(I, I.IS.NumberPrototype, "Number", true,
      I.IH.numPropTable(value match {
        case Some(v) => I.IH.toNumber(v).num
        case None => 0
      }))
  }

  override def _construct(argsObj: JSObject): JSNumber = {
    argsObj._get("length") match {
      case PVal(IRVal(n: EJSNumber)) if n.num == 0 => construct(None)
      case PVal(IRVal(n: EJSNumber)) if n.num >= 1 => construct(Some(argsObj._get("0")))
    }
  }

  override def _call(tb: Val, argsObj: JSObject): Unit = {
    argsObj._get("length") match {
      case PVal(IRVal(n: EJSNumber)) if n.num == 0 => I.IS.comp.setReturn(PVal(IRVal(I.IH.mkIRNum(0))))
      case PVal(IRVal(n: EJSNumber)) if n.num >= 1 => I.IS.comp.setReturn(PVal(IRVal(I.IH.toNumber(argsObj._get("0")))))
    }
  }
}
