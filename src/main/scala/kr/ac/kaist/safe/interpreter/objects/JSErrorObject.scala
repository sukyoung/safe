/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.safe.interpreter.objects

import kr.ac.kaist.safe.interpreter._
import kr.ac.kaist.safe.interpreter.{InterpreterPredefine => IP}

class JSErrorObject(_I: Interpreter,
                    _proto: JSObject,
                    _className: String,
                    _extensible: Boolean,
                    _property: PropTable)
  extends JSObject(_I, _proto, _className, _extensible, _property)
