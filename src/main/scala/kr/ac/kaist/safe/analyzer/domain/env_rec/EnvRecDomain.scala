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

// environment record abstract domain
trait EnvRecDomain extends AbsDomain[EnvRec] {
  def apply(envRec: AbsDecEnvRec): Elem
  def apply(envRec: AbsGlobalEnvRec): Elem

  // abstract environment record element
  type Elem <: ElemTrait

  // abstract environment record element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    val decEnvRec: AbsDecEnvRec
    val globalEnvRec: AbsGlobalEnvRec

    // 10.2.1.2.1 HasBinding(N)
    def HasBinding(name: String)(heap: AbsHeap): AbsBool

    // 10.2.1.2.2 CreateMutableBinding(N, D)
    def CreateMutableBinding(
      name: String,
      del: Boolean
    )(heap: AbsHeap): (Elem, AbsHeap, Set[Exception])

    // 10.2.1.2.3 SetMutableBinding(N, V, S)
    def SetMutableBinding(
      name: String,
      v: AbsValue,
      strict: Boolean
    )(heap: AbsHeap): (Elem, AbsHeap, Set[Exception])

    // 10.2.1.2.4 GetBindingValue(N, S)
    def GetBindingValue(
      name: String,
      strict: Boolean
    )(heap: AbsHeap): (AbsValue, Set[Exception])

    // 10.2.1.2.5 DeleteBinding(N)
    def DeleteBinding(
      name: String
    )(heap: AbsHeap): (Elem, AbsHeap, AbsBool)

    // 10.2.1.2.6 ImplicitThisValue()
    def ImplicitThisValue(heap: AbsHeap): AbsValue

    // substitute locR by locO
    def subsLoc(locR: Recency, locO: Recency): Elem

    // weak substitute locR by locO
    def weakSubsLoc(locR: Recency, locO: Recency): Elem
  }
}
