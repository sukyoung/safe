/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.domain

import kr.ac.kaist.jsaf.analysis.cfg.InternalError

object AbsDomUtils {
  // If index is greater than or equal to the number of nodes in the list, this returns null.
  def checkIndex(n_index: AbsNumber, n_length: AbsNumber): Value = (n_index < n_length).getPair match {
    case (AbsBot, _) => ValueBot
    case (AbsSingle, Some(b)) if b => ValueBot
    case (AbsTop, _) => Value(NullTop)
    case (AbsSingle, Some(b)) if !b => Value(NullTop)
    case _ => throw new InternalError("AbsBool does not have an abstract value for multiple values.")
  }
}