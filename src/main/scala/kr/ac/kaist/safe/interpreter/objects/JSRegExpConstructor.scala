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
import kr.ac.kaist.safe.util.regexp._

class JSRegExpConstructor(I: Interpreter, proto: JSObject)
    extends JSFunction13(I, proto, "Array", true, propTable, I.IH.dummyFtn(0), EmptyEnv(), true) {
  def init(): Unit = {
    /*
     * 15.10.5 Properties of the RegExp Constructor
     */
    property.put("length", I.IH.numProp(2))
    // { [[Writable]]: false, [[Enumerable]]: false, [[Configurable]]: false }
    property.put("prototype", I.IH.mkDataProp(I.IS.RegExpPrototype))
  }

  /*
   * 15.10.4 The RegExp Constructor
   * 15.10.4.1 new RegExp(pattern, flags)
   */
  def construct(pattern: Val, flags: Val): JSRegExp = {
    val (p, f) = pattern match {
      case r: JSRegExp =>
        if (I.IH.isUndef(flags)) {
          (r.pattern, r.flags)
        } else {
          // 15.10.4.1. "If *pattern* is an object R whose [[Class]] internal property is 'RegExp'
          // and *flags* is not **undefined**, then throw a **TypeError** exception."
          throw new TypeErrorException
        }
      case pv: PVal =>
        val p = if (I.IH.isUndef(pattern)) "" else I.IH.toString(pattern)
        val f = if (I.IH.isUndef(flags)) "" else I.IH.toString(flags)
        (p, f)
    }

    val s =
      if (p == "") "(?:)"
      else p

    val (matcher, bg, bi, bm, nCapturingParens) =
      try {
        JSRegExpSolver.parse(s, f)
      } catch {
        case e: kr.ac.kaist.safe.util.regexp.SyntaxErrorException =>
          throw new kr.ac.kaist.safe.interpreter.SyntaxErrorException
      }

    val prop = propTable
    prop.put("source", I.IH.strProp(s))
    prop.put("global", I.IH.boolProp(bg))
    prop.put("ignoreCase", I.IH.boolProp(bi))
    prop.put("multiline", I.IH.boolProp(bm))
    prop.put("lastIndex", I.IH.mkDataProp(PVal(I.IH.mkIRNumIR(0)), true, false, false))

    new JSRegExp(I, I.IS.RegExpPrototype, "RegExp", true, prop, matcher, p, f, nCapturingParens)
  }

  /*
   * 15.10.3 The RegExp Constructor Called as a Function
   * 15.10.3.1 RegExp(pattern, flags)
   * TODO
   */
  override def call(tb: Val, argsObj: JSObject): Unit = I.IS.comp.setReturn(construct(argsObj))

  override def construct(argsObj: JSObject): JSRegExp = {
    construct(argsObj.get("0"), argsObj.get("1"))
  }
}
