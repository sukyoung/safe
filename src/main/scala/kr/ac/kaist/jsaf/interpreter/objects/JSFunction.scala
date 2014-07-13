/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter.objects

import kr.ac.kaist.jsaf.nodes.IRStmt
import kr.ac.kaist.jsaf.nodes.IRFunctional
import kr.ac.kaist.jsaf.interpreter._
import kr.ac.kaist.jsaf.interpreter.{InterpreterPredefine => IP, _}
import kr.ac.kaist.jsaf.scala_src.useful.Lists._

abstract class JSFunction(_I: Interpreter,
                          _proto: JSObject,
                          _className: String,
                          _extensible: Boolean,
                          _property: PropTable,
                          var code: IRFunctional,
                          var scope: Env,
                          var const: Boolean,
                          var builtin: JSObject)
  extends JSObject(_I, _proto, _className, _extensible, _property) {

  // Make a IR list in advance
  val codeIRs: List[IRStmt] = toList(code.getFds) ++ toList(code.getVds) ++ toList(code.getArgs) ++ toList(code.getBody)

  ////////////////////////////////////////////////////////////////////////////////
  // 8. Types Table 9
  // 13.2 Creating Function Objects
  // 15.3.5 Properties of Function Instances
  ////////////////////////////////////////////////////////////////////////////////

  // [[Call]]
  def _call(tb: Val, argsObj: JSObject): Unit

  // [[Construct]]
  def _construct(argsObj: JSObject): JSObject

  // [[HasInstance]](V)
  def _hasInstance(v: Val): Unit

  // 15.3.5.4 [[Get]](P)
  def _get(p: Val): Unit = I.IS.comp.setThrow(IP.nyiError, I.IS.span)
}
