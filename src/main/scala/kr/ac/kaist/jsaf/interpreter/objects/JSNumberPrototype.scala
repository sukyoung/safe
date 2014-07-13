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
import kr.ac.kaist.jsaf.interpreter.{InterpreterHelper => IH,
                                     InterpreterPredefine => IP, _}

class JSNumberPrototype(_I: Interpreter, _proto: JSObject)
  extends JSNumber(_I, _proto, "Number", true, propTable) {
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

  override def __callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    method match {
      case I.IS.NumberPrototypeToString => argsObj._get("length") match {
        case PVal(n:IRNumber) if n.getNum == 0 => _toString(None)
        case PVal(n:IRNumber) if n.getNum >= 1 => _toString(Some(argsObj._get("0")))
      }
      // 15.7.4.3
      case I.IS.NumberPrototypeValueOf => _valueOf()
      // 15.7.4.5
      // 15.7.4.6
      // 15.7.4.7
    }
  }

  def _toString(radix: Option[Val]): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSNumber =>
        val r: Int = radix match {
          // TODO: ToInteger vs. ToInt32
          case Some(v) => I.IH.toInt32(v)
          case None => 10
        }
        r match {
          case r if !(2 <= r && r <= 36) => I.IS.comp.setThrow(IP.rangeError, I.IS.span)
          case 10 => I.IS.comp.setReturn(PVal(I.IH.mkIRStr(I.IH.toString(o._get(IP.pvpn)))))
          case r =>
            var s: String = ""
            // TODO: ToInteger vs. ToInt32
            val n: Int = I.IH.toInt32(o._get(IP.pvpn))
            if (n == 0) I.IS.comp.setReturn(PVal(I.IH.mkIRStr("0")))
            else {
              var m: Int = n.abs
              while (m > 0) {
                val k: Int = m % r
                val ch: Char = if (k < 10) ('0'+k).toChar else ('a'+k-10).toChar
                s += ch
                m /= r
              }
              if (n < 0) s += '-'
              I.IS.comp.setReturn(PVal(I.IH.mkIRStr(s.reverse)))
            }
        }
      case o: JSObject => I.IS.comp.setThrow(IP.typeError, I.IS.span)
      case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def _valueOf(): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSNumber => I.IS.comp.setReturn(o._get(IP.pvpn))
      case o: JSObject => I.IS.comp.setThrow(IP.typeError, I.IS.span)
      case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }
}
