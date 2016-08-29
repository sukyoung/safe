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
import kr.ac.kaist.safe.util.Loc
import scala.collection.immutable.HashMap

// 10.2.1.2 Object Environment Records
class ObjEnvRecord(
    val loc: Loc
) extends EnvRecord {
  // TODO 10.2.1.2.1 HasBinding(N)
  def HasBinding(name: String)(boolU: AbsBoolUtil): AbsBool = null

  // TODO 10.2.1.2.2 CreateMutableBinding(N, D)
  def CreateMutableBinding(name: String, del: Boolean): Unit = {}

  // TODO 10.2.1.2.3 SetMutableBinding(N, V, S)
  def SetMutableBinding(
    name: String,
    v: Value,
    strict: Boolean
  ): Set[Exception] = null

  // TODO 10.2.1.2.4 GetBindingValue(N, S)
  def GetBindingValue(name: String, strict: Boolean): Set[Exception] = null

  // TODO 10.2.1.2.5 DeleteBinding(N)
  def DeleteBinding(name: String): AbsBool = null

  // TODO 10.2.1.2.6 ImplicitThisValue()
  def ImplicitThisValue: Value = null
}
