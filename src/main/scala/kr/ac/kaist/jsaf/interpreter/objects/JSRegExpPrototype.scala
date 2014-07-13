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
import kr.ac.kaist.jsaf.nodes_util.{EJSCompletionType => CT}
import kr.ac.kaist.jsaf.interpreter.{InterpreterPredefine => IP, _}
import kr.ac.kaist.jsaf.utils.regexp._

class JSRegExpPrototype(_I: Interpreter, _proto: JSObject)
  extends JSRegExp(_I, _proto, "RegExp", true, propTable, (s: String, i: Int) => None, "", "", 0) {
  def init(): Unit = {
    /*
     * 15.10.6 Properties of the RegExp Prototype Object
     */
    property.put("constructor", I.IH.objProp(I.IS.RegExpConstructor))
    property.put("exec", I.IH.objProp(I.IS.RegExpPrototypeExec))
    property.put("test", I.IH.objProp(I.IS.RegExpPrototypeTest))
    property.put("toString", I.IH.objProp(I.IS.RegExpPrototypeToString))
  }

  override def __callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    method match {
      case I.IS.RegExpPrototypeExec => _exec(argsObj._get("0"))
      case I.IS.RegExpPrototypeTest => _test(argsObj._get("0"))
      case I.IS.RegExpPrototypeToString => _toString()
    }
  }

  def _exec(string: Val): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case r:JSRegExp =>
        val s: String = I.IH.toString(string) // puppy 1
        val lastIndex: Val = r._get("lastIndex")
        var i: Int = I.IH.toUint32(lastIndex).toInt
        val global: Val = r._get("global")
        if (!I.IH.toBoolean(global))
          i = 0

        val (a, lastIdx, idx, length) = JSRegExpSolver.exec(r._match, s, i)

        val rtn = a match {
          case None => {
            r._put("lastIndex", PVal(I.IH.mkIRNum(0)), true)
            IP.nullV
          }
          case Some(array) => {
            if (I.IH.toBoolean(global))
              r._put("lastIndex", PVal(I.IH.mkIRNum(lastIdx)), true)
            val a: JSArray = I.IS.ArrayConstructor.construct(Nil)
            a._defineOwnProperty("index", I.IH.mkDataProp(PVal(I.IH.mkIRNum(idx)), true, true, true), true)
            a._defineOwnProperty("input", I.IH.mkDataProp(PVal(I.IH.mkIRStr(s)), true, true, true), true)
            a._defineOwnProperty("length", I.IH.mkDataProp(PVal(I.IH.mkIRNum(length)), true, true, true), true)
            for (i <- 0 to array.length-1) {
              val captureI: Val = array(i) match {
                 case Some(s) => PVal(I.IH.mkIRStr(s))
                 case None => IP.undefV
              }
              a._defineOwnProperty(i.toString,
                I.IH.mkDataProp(captureI, true, true, true),
                true)
            }
            a
          }
        }
        I.IS.comp.setReturn(rtn)
      case _ => I.IS.comp.setThrow(IP.typeError, I.IS.span)
    }
  }

  def _test(string: Val): Unit = {
    _exec(string)
    if(I.IS.comp.Type == CT.RETURN && I.IS.comp.value.isInstanceOf[JSObject]) I.IS.comp.setReturn(IP.truePV)
    else I.IS.comp.setReturn(IP.falsePV)
  }

  def _toString(): Unit = { // puppy 5
    I.IH.toObject(I.IS.tb) match {
      case r: JSRegExp =>
        val source: String = I.IH.toString(r._get("source"))
        val global: String = if (I.IH.toBoolean(r._get("global"))) "g" else ""
        val ignoreCase: String = if (I.IH.toBoolean(r._get("ignoreCase"))) "i" else ""
        val multiline: String = if (I.IH.toBoolean(r._get("multiline"))) "m" else ""
        val s: String = "/"+source+"/"+global+ignoreCase+multiline
        I.IS.comp.setReturn(PVal(I.IH.mkIRStr(s)))
      case _ => I.IS.comp.setThrow(IP.typeError, I.IS.span)
    }
  }
}
