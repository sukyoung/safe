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

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.util._

// location abstract domain
trait LocDomain extends AbsDomain[Loc] {
  // abstract location element
  type Elem <: ElemTrait

  // abstract location element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    def contains(loc: Loc): Boolean
    def exists(f: Loc => Boolean): Boolean
    def filter(f: Loc => Boolean): Elem
    def foreach(f: Loc => Unit): Unit
    def foldLeft[T](initial: T)(f: (T, Loc) => T): T
    def map[T](f: Loc => T): Set[T]
    def +(loc: Loc): Elem
    def -(loc: Loc): Elem
    /* substitute locR by locO */
    def subsLoc(locR: Recency, locO: Recency): Elem
    /* weakly substitute locR by locO, that is keep locR together */
    def weakSubsLoc(locR: Recency, locO: Recency): Elem
  }
}
