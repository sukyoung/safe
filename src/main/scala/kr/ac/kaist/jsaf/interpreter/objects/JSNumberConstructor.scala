/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter.objects

import kr.ac.kaist.jsaf.nodes.IRNumber
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.interpreter.{InterpreterPredefine => IP, _}

class JSNumberConstructor(_I: Interpreter, _proto: JSObject)
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
    property.put("NaN", I.IH.mkDataProp(PVal(IP.NaN)))
    property.put("NEGATIVE_INFINITY", I.IH.mkDataProp(PVal(IP.minusInfinity)))
    property.put("POSITIVE_INFINITY", I.IH.mkDataProp(PVal(IP.plusInfinity)))
  }

  /*
   * 15.7.2 The Number Constructor
   * 15.7.2.1 new Number ( [ value ] )
   */
  def construct(value: Option[Val]): JSNumber = {
    new JSNumber(I, I.IS.NumberPrototype, "Number", true,
                 I.IH.numPropTable(value match {
                                   case Some(v) => I.IH.toNumber(v).getNum
                                   case None => 0
                                 }))
  }

  override def _construct(argsObj: JSObject): JSNumber = {
    argsObj._get("length") match {
      case PVal(n:IRNumber) if n.getNum == 0 => construct(None)
      case PVal(n:IRNumber) if n.getNum >= 1 => construct(Some(argsObj._get("0")))
    }
  }

  override def _call(tb: Val, argsObj: JSObject): Unit = {
    argsObj._get("length") match {
      case PVal(n:IRNumber) if n.getNum == 0 => I.IS.comp.setReturn(PVal(I.IH.mkIRNum(0)))
      case PVal(n:IRNumber) if n.getNum >= 1 => I.IS.comp.setReturn(PVal(I.IH.toNumber(argsObj._get("0"))))
    }
  }
}
