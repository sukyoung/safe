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

// declarative environment record abstract domain
trait DecEnvRecDomain extends AbsDomain[DecEnvRec] {
  type EnvMap = Map[String, (AbsBinding, AbsAbsent)]

  val Empty: Elem

  def apply(m: EnvMap, upper: Boolean = false): Elem

  // abstract declarative environment record element
  type Elem <: ElemTrait

  // abstract declarative environment record element traits
  trait ElemTrait extends super.ElemTrait { this: Elem =>
    // 10.2.1.1.1 HasBinding(N)
    def HasBinding(name: String): AbsBool

    // 10.2.1.1.2 CreateMutableBinding(N, D)
    def CreateMutableBinding(
      name: String,
      del: Boolean = true
    ): Elem

    // 10.2.1.1.3 SetMutableBinding(N, V, S)
    def SetMutableBinding(
      name: String,
      v: AbsValue,
      strict: Boolean = false
    ): (Elem, Set[Exception])

    // 10.2.1.1.4 GetBindingValue(N, S)
    def GetBindingValue(
      name: String,
      strict: Boolean = false
    ): (AbsValue, Set[Exception])

    // 10.2.1.1.5 DeleteBinding(N)
    def DeleteBinding(
      name: String
    ): (Elem, AbsBool)

    // 10.2.1.1.6 ImplicitThisValue()
    def ImplicitThisValue: AbsValue

    // 10.2.1.1.7 CreateImmutableBinding(N)
    def CreateImmutableBinding(
      name: String
    ): Elem

    // 10.2.1.1.6 InitializeImmutableBinding(N, V)
    def InitializeImmutableBinding(
      name: String,
      v: AbsValue
    ): Elem

    // substitute locR by locO
    def subsLoc(locR: Recency, locO: Recency): Elem

    // weak substitute locR by locO
    def weakSubsLoc(locR: Recency, locO: Recency): Elem
  }
}
