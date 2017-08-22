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

abstract class JSFunction(
  I: Interpreter,
  proto: JSObject,
  className: String,
  extensible: Boolean,
  property: PropTable,
  var code: IRFunctional,
  var scope: Env,
  var const: Boolean,
  var builtin: JSObject
)
    extends JSObject(I, proto, className, extensible, property) {

  // Make a IR list in advance
  val codeIRs: List[IRStmt] = code.fds ++ code.vds ++ code.args ++ code.body

  ////////////////////////////////////////////////////////////////////////////////
  // 8. Types Table 9
  // 13.2 Creating Function Objects
  // 15.3.5 Properties of Function Instances
  ////////////////////////////////////////////////////////////////////////////////

  // [[Call]]
  def call(tb: Val, argsObj: JSObject): Unit

  // [[Construct]]
  def construct(argsObj: JSObject): JSObject

  // [[HasInstance]](V)
  def hasInstance(v: Val): Unit

  // 15.3.5.4 [[Get]](P)
  def get(p: Val): Unit = I.IS.comp.setThrow(IP.nyiError, I.IS.span)
}
