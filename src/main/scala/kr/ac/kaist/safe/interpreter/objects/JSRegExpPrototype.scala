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
import kr.ac.kaist.safe.util.{ EJSCompletionType => CT }
import kr.ac.kaist.safe.util.regexp._

class JSRegExpPrototype(I: Interpreter, proto: JSObject)
    extends JSRegExp(I, proto, "RegExp", true, propTable, (s: String, i: Int) => None, "", "", 0) {
  def init(): Unit = {
    /*
     * 15.10.6 Properties of the RegExp Prototype Object
     */
    property.put("constructor", I.IH.objProp(I.IS.RegExpConstructor))
    property.put("exec", I.IH.objProp(I.IS.RegExpPrototypeExec))
    property.put("test", I.IH.objProp(I.IS.RegExpPrototypeTest))
    property.put("toString", I.IH.objProp(I.IS.RegExpPrototypeToString))
  }

  override def callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    method match {
      case I.IS.RegExpPrototypeExec => JSExec(argsObj.get("0"))
      case I.IS.RegExpPrototypeTest => JSTest(argsObj.get("0"))
      case I.IS.RegExpPrototypeToString => JSToString()
    }
  }

  def JSExec(string: Val): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case r: JSRegExp =>
        val s: String = I.IH.toString(string) // puppy 1
        val lastIndex: Val = r.get("lastIndex")
        var i: Int = I.IH.toUint32(lastIndex).toInt
        val global: Val = r.get("global")
        if (!I.IH.toBoolean(global))
          i = 0

        val (a, lastIdx, idx, length) = JSRegExpSolver.exec(r.JSMatch, s, i)

        val rtn = a match {
          case None => {
            r.put("lastIndex", PVal(IRVal(I.IH.mkIRNum(0))), true)
            IP.nullV
          }
          case Some(array) => {
            if (I.IH.toBoolean(global))
              r.put("lastIndex", PVal(I.IH.mkIRNumIR(lastIdx)), true)
            val a: JSArray = I.IS.ArrayConstructor.construct(Nil)
            a.defineOwnProperty("index", I.IH.mkDataProp(PVal(IRVal(I.IH.mkIRNum(idx))), true, true, true), true)
            a.defineOwnProperty("input", I.IH.mkDataProp(PVal(IRVal(I.IH.mkIRStr(s))), true, true, true), true)
            a.defineOwnProperty("length", I.IH.mkDataProp(PVal(IRVal(I.IH.mkIRNum(length))), true, true, true), true)
            for (i <- 0 to array.length - 1) {
              val captureI: Val = array(i) match {
                case Some(s) => PVal(I.IH.mkIRStrIR(s))
                case None => IP.undefV
              }
              a.defineOwnProperty(
                i.toString,
                I.IH.mkDataProp(captureI, true, true, true),
                true
              )
            }
            a
          }
        }
        I.IS.comp.setReturn(rtn)
      case _ => I.IS.comp.setThrow(IP.typeError, I.IS.span)
    }
  }

  def JSTest(string: Val): Unit = {
    JSExec(string)
    if (I.IS.comp.Type == CT.RETURN && I.IS.comp.value.isInstanceOf[JSObject]) I.IS.comp.setReturn(IP.truePV)
    else I.IS.comp.setReturn(IP.falsePV)
  }

  def JSToString(): Unit = { // puppy 5
    I.IH.toObject(I.IS.tb) match {
      case r: JSRegExp =>
        val source: String = I.IH.toString(r.get("source"))
        val global: String = if (I.IH.toBoolean(r.get("global"))) "g" else ""
        val ignoreCase: String = if (I.IH.toBoolean(r.get("ignoreCase"))) "i" else ""
        val multiline: String = if (I.IH.toBoolean(r.get("multiline"))) "m" else ""
        val s: String = "/" + source + "/" + global + ignoreCase + multiline
        I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR(s)))
      case _ => I.IS.comp.setThrow(IP.typeError, I.IS.span)
    }
  }
}
