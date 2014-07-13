/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.exceptions

import kr.ac.kaist.jsaf.scala_src.useful.Lists
import edu.rice.cs.plt.iter.IterUtil

class MultipleStaticError(_errors: List[StaticError])
      extends StaticError(toString, None) {
  override def at = ""
  override def description = ""
  override def toString = IterUtil.toString(IterUtil.sort(toJList), "", "\n", "")
  def toJList = Lists.toJavaList(_errors)
}
