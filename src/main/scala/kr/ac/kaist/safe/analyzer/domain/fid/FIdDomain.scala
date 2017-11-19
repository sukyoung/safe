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

import kr.ac.kaist.safe.nodes.cfg.FunctionId

// function id abstract domain
trait FIdDomain extends AbsDomain[FId] {
  def apply(fid: FunctionId): Elem
  def apply(fid: Set[FunctionId]): Elem

  // abstract function id element
  type Elem <: ElemTrait

  // abstract function id element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    def contains(fid: FunctionId): Boolean
    def exists(f: FunctionId => Boolean): Boolean
    def filter(f: FunctionId => Boolean): Elem
    def foreach(f: FunctionId => Unit): Unit
    def foldLeft[T](initial: T)(f: (T, FunctionId) => T): T
    def map[T](f: FunctionId => T): Set[T]
    def +(fid: FunctionId): Elem
    def -(fid: FunctionId): Elem
  }
}
