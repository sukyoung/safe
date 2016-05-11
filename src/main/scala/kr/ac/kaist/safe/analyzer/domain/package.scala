/**
 * *****************************************************************************
 * Copyright (c) 2012-2014, S-Core, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.nodes.FunctionId

package object domain {
  ////////////////////////////////////////////////////////////////
  // abstract location
  ////////////////////////////////////////////////////////////////
  type Loc = Int
  type Address = Int
  type RecencyTag = Int
  val Recent = 0
  val Old = 1

  ////////////////////////////////////////////////////////////////
  // value constructors
  ////////////////////////////////////////////////////////////////
  def PropValue(objval: ObjectValue, funid: Set[FunctionId]): PropValue =
    DefaultPropValue(objval, funid)

  def ObjectValue(value: Value, writable: AbsBool, enumerable: AbsBool, configurable: AbsBool): ObjectValue =
    DefaultObjectValue(value, writable, enumerable, configurable)

  def Value(pvalue: PValue, locset: Set[Loc]): Value =
    DefaultValue(pvalue, locset)

  def PValue(undefval: AbsUndef, nullval: AbsNull, boolval: AbsBool, numval: AbsNumber, strval: AbsString): PValue =
    DefaultPValue(undefval, nullval, boolval, numval, strval)
}
