/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.domain

abstract class AbsBase[A] extends AbsDomain {
  // if the concretization produces a singleton set
  // Some(elt) if the concretization produces a singleton set {elt}
  // None      otherwise
  def getSingle: Option[A]

  def getPair: (AbsCase, Option[A]) = (getAbsCase, getSingle)

  // concretization
  // Some(set) if the concretization produces a finite set
  // None if the concretization produces an infinite set
  def gamma: Option[Set[A]]

  def exists(b: A): Boolean = this.gamma match {
    case Some(set) => set.contains(b)
    case _ => false
  }

  def forall(f: A => Boolean): Boolean = this.gamma match {
    case Some(set) => set.forall(f)
    case _ => false
  }
}
