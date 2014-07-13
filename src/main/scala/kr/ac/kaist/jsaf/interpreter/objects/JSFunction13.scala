/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter.objects

import kr.ac.kaist.jsaf.nodes.IRFunctional
import kr.ac.kaist.jsaf.interpreter._
import kr.ac.kaist.jsaf.interpreter.{InterpreterPredefine => IP, _}
import kr.ac.kaist.jsaf.interpreter.{InterpreterHelper => IH, _}

// Created by 13.2 Creating Function Objects
class JSFunction13(__I: Interpreter,
                   __proto: JSObject,
                   __className: String,
                   __extensible: Boolean,
                   __property: PropTable,
                   __code: IRFunctional,
                   __scope: Env,
                   __const: Boolean = false,
                   __builtin: JSObject = null)
  extends JSFunction(__I, __proto, __className, __extensible, __property, __code, __scope, __const, __builtin) {
  ////////////////////////////////////////////////////////////////////////////////
  // 8. Types Table 9
  // 13.2 Creating Function Objects
  // 15.3.5 Properties of Function Instances
  ////////////////////////////////////////////////////////////////////////////////

  // 13.2.1 [[Call]]
  override def _call(tb: Val, argsObj: JSObject): Unit = {
    val oldEnv = I.IS.env
    val oldTb = I.IS.tb
    try {
      I.IH.call(I.IS.info, tb, argsObj, this)
    } finally {
      I.IS.env = oldEnv
      I.IS.tb = oldTb
    }
  }

  // 13.2.2 [[Construct]]
  override def _construct(argsObj: JSObject): JSObject = throw new InterpreterError("JSFunction13::_construct()", I.IS.span)

  // 15.3.5.3 [[HasInstance]](V)
  override def _hasInstance(v: Val): Unit = I.IS.comp.setThrow(IP.nyiError, I.IS.span)
}
