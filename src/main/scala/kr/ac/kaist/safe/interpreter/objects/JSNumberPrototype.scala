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
import kr.ac.kaist.safe.interpreter.{ InterpreterPredefine => IP }
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util._

class JSNumberPrototype(I: Interpreter, proto: JSObject)
    extends JSNumber(I, proto, "Number", true, propTable) {
  def init(): Unit = {
    /*
     * 15.7.4 Properties of the Number Prototype Object
     */
    property.put(IP.pvpn, I.IH.numProp(0))
    property.put("constructor", I.IH.objProp(I.IS.NumberConstructor))
    property.put("toString", I.IH.objProp(I.IS.NumberPrototypeToString))
    // 15.7.4.3
    property.put("valueOf", I.IH.objProp(I.IS.NumberPrototypeValueOf))
    // 15.7.4.5
    // 15.7.4.6
    // 15.7.4.7
  }

  override def callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    method match {
      case I.IS.NumberPrototypeToString => argsObj.get("length") match {
        case PVal(IRVal(n: EJSNumber)) if n.num == 0 => JSToString(None)
        case PVal(IRVal(n: EJSNumber)) if n.num >= 1 => JSToString(Some(argsObj.get("0")))
      }
      // 15.7.4.3
      case I.IS.NumberPrototypeValueOf => JSValueOf()
      // 15.7.4.5
      // 15.7.4.6
      // 15.7.4.7
    }
  }

  def JSToString(radix: Option[Val]): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSNumber =>
        val r: Int = radix match {
          // TODO: ToInteger vs. ToInt32
          case Some(v) => I.IH.toInt32(v)
          case None => 10
        }
        r match {
          case r if !(2 <= r && r <= 36) => I.IS.comp.setThrow(IP.rangeError, I.IS.span)
          case 10 => I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR(I.IH.toString(o.get(IP.pvpn)))))
          case r =>
            var s: String = ""
            // TODO: ToInteger vs. ToInt32
            val n: Int = I.IH.toInt32(o.get(IP.pvpn))
            if (n == 0) I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR("0")))
            else {
              var m: Int = n.abs
              while (m > 0) {
                val k: Int = m % r
                val ch: Char = if (k < 10) ('0' + k).toChar else ('a' + k - 10).toChar
                s += ch
                m /= r
              }
              if (n < 0) s += '-'
              I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR(s.reverse)))
            }
        }
      case o: JSObject => I.IS.comp.setThrow(IP.typeError, I.IS.span)
      case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def JSValueOf(): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSNumber => I.IS.comp.setReturn(o.get(IP.pvpn))
      case o: JSObject => I.IS.comp.setThrow(IP.typeError, I.IS.span)
      case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }
}
