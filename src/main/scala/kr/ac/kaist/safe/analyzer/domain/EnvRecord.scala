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

import kr.ac.kaist.safe.LINE_SEP
import scala.collection.immutable.HashMap

// 10.2.1 Environment Records
abstract class EnvRecord {
  // HasBinding(N)
  def HasBinding(name: String): AbsBool

  // CreateMutableBinding(N, D)
  def CreateMutableBinding(
    name: String,
    del: Boolean
  ): EnvRecord

  // SetMutableBinding(N, V, S)
  def SetMutableBinding(
    name: String,
    v: AbsValue,
    strict: Boolean
  ): (EnvRecord, Set[Exception])

  // GetBindingValue(N, S)
  def GetBindingValue(
    name: String,
    strict: Boolean
  ): (AbsValue, Set[Exception])

  // DeleteBinding(N)
  def DeleteBinding(name: String): (EnvRecord, AbsBool)

  // ImplicitThisValue()
  def ImplicitThisValue: AbsValue
}
