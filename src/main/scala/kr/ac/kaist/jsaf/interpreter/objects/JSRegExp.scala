/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter.objects

import kr.ac.kaist.jsaf.interpreter._
import kr.ac.kaist.jsaf.utils.regexp._

class JSRegExp(_I: Interpreter,
               _proto: JSObject,
               _className: String,
               _extensible: Boolean,
               _property: PropTable,
               var _match: (String, Int) => MatchResult,
               var pattern: String,
               var flags: String,
               var nCapturingParens: Int)
  extends JSObject(_I, _proto, _className, _extensible, _property)
