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

// allocation site
abstract sealed class AllocSite

object AllocSite {
  implicit def ordering[B <: AllocSite]: Ordering[B] = Ordering.by {
    case asite => asite.toString
  }
}

// allocation site defined in user code
case class UserAllocSite(id: Int) extends AllocSite {
  override def toString: String = id.toString
}

// predefined allocation site
case class PredAllocSite(name: String) extends AllocSite {
  override def toString: String = name
}
