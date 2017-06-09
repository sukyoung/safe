/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.safe.interpreter.objects

class JSRegExpConstructor(_I: Interpreter, _proto: JSObject)
  extends JSFunction13(_I, _proto, "Array", true, propTable(), _I.IH.dummyFtn(0), EmptyEnv(), true) {
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
      case pv:PVal =>
        val p = if (I.IH.isUndef(pattern)) "" else I.IH.toString(pattern)
        val f = if (I.IH.isUndef(flags)) "" else I.IH.toString(flags)
        (p, f)
    }

    val s =
      if (p == "") "(?:)"
      else p

    val (matcher, b_g, b_i, b_m, nCapturingParens) =
      try {
        JSRegExpSolver.parse(s, f)
      } catch {
        case e: kr.ac.kaist.jsaf.utils.regexp.SyntaxErrorException =>
          throw new SyntaxErrorException
      }


    val prop = propTable()
    prop.put("source", I.IH.strProp(s))
    prop.put("global", I.IH.boolProp(b_g))
    prop.put("ignoreCase", I.IH.boolProp(b_i))
    prop.put("multiline", I.IH.boolProp(b_m))
    prop.put("lastIndex", I.IH.mkDataProp(PVal(I.IH.mkIRNum(0)), true, false, false))

    new JSRegExp(I, I.IS.RegExpPrototype, "RegExp", true, prop, matcher, p, f, nCapturingParens)
  }

  /*
   * 15.10.3 The RegExp Constructor Called as a Function
   * 15.10.3.1 RegExp(pattern, flags)
   * TODO
   */
  override def _call(tb: Val, argsObj: JSObject): Unit = I.IS.comp.setReturn(_construct(argsObj))

  override def _construct(argsObj: JSObject): JSRegExp = {
    construct(argsObj._get("0"), argsObj._get("1"))
  }
}
