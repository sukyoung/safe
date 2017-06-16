/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
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

class JSNumber(
  _I: Interpreter,
  _proto: JSObject,
  _className: String,
  _extensible: Boolean,
  _property: PropTable
)
    extends JSObject(_I, _proto, _className, _extensible, _property)
