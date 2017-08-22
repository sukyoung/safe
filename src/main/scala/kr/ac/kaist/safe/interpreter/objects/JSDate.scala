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

class JSDate(
  I: Interpreter,
  proto: JSObject,
  className: String,
  extensible: Boolean,
  property: PropTable
)
    extends JSObject(I, proto, className, extensible, property)
