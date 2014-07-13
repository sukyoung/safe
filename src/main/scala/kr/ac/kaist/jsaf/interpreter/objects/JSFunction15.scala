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

// Created by 15.3.4.5 Function.prototype.bind(thisArg [, arg1 [, arg2, ...]])
class JSFunction15(__I: Interpreter,
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

  // 15.3.4.5.1 [[Call]]
  override def _call(tb: Val, argsObj: JSObject): Unit = I.IS.comp.setThrow(IP.nyiError, I.IS.span)

  // 15.3.4.5.2 [[Construct]]
  override def _construct(argsObj: JSObject): JSObject = throw new InterpreterError("JSFunction15::_construct()", I.IS.span)

  // 15.3.4.5.3 [[HasInstance]](V)
  override def _hasInstance(v: Val): Unit = I.IS.comp.setThrow(IP.nyiError, I.IS.span)
}
