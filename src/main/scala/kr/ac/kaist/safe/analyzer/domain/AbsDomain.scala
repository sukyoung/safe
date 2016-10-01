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

package kr.ac.kaist.safe.analyzer.domain

// abstract domain
trait AbsDomain[C, Self <: AbsDomain[C, _]] extends Domain[Self] {
  // concretization
  def gamma: ConSet[C]

  // get concrete value if |gamma(this)| = 1
  def getSingle: ConSingle[C]
}

// utility class for given abstract domain
trait AbsDomainUtil[C, ABS <: AbsDomain[C, ABS]] extends DomainUtil[ABS] {
  /** gamma(Bot) is always empty set in our analyzer **/

  // abstraction
  def alpha(value: C): ABS
  def alpha(set: Set[C]): ABS = set.foldLeft(Bot) {
    case (abs, value) => abs + alpha(value)
  }
  def alpha(seq: C*): ABS = alpha(seq.toSet)
  def apply(set: Set[C]): ABS = alpha(set)
  def apply(seq: C*): ABS = alpha(seq.toSet)
}
