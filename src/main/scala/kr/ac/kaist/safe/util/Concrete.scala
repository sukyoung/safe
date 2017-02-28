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

package kr.ac.kaist.safe.util

import kr.ac.kaist.safe.analyzer.domain.Loc

////////////////////////////////////////////////////////////////////////////////
// concrete abstraction
////////////////////////////////////////////////////////////////////////////////
case class Concrete(loc: Loc) extends Loc {
  override def toString: String = s"C$loc"
}
