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

// Created by 13.2 Creating Function Objects
class JSFunction13(
  I: Interpreter,
  proto: JSObject,
  className: String,
  extensible: Boolean,
  property: PropTable,
  code: IRFunctional,
  scope: Env,
  const: Boolean = false,
  builtin: JSObject = null
)
    extends JSFunction(I, proto, className, extensible, property, code, scope, const, builtin) {
  ////////////////////////////////////////////////////////////////////////////////
  // 8. Types Table 9
  // 13.2 Creating Function Objects
  // 15.3.5 Properties of Function Instances
  ////////////////////////////////////////////////////////////////////////////////

  // 13.2.1 [[Call]]
  override def call(tb: Val, argsObj: JSObject): Unit = {
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
  override def construct(argsObj: JSObject): JSObject = throw new InterpreterError("JSFunction13::_construct()", I.IS.span)

  // 15.3.5.3 [[HasInstance]](V)
  override def hasInstance(v: Val): Unit = I.IS.comp.setThrow(IP.nyiError, I.IS.span)
}
