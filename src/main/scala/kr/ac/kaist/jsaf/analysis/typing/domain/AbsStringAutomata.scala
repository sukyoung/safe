/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.domain

object AbsStringAutomata {
  def alpha(str: String): AbsString = StrTop
}

class AbsStringAutomata(_kind: AbsString.AbsStringCase) extends AbsString(_kind) {
}
