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

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.cfg_builder.FunctionId

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

  ////////////////////////////////////////////////////////////////
  // constant values
  ////////////////////////////////////////////////////////////////
  val STR_DEFAULT_OTHER = "@default_other"
  val STR_DEFAULT_NUMBER = "@default_number"
  val DEFAULT_KEYSET = Set(STR_DEFAULT_NUMBER, STR_DEFAULT_OTHER)

  ////////////////////////////////////////////////////////////////
  // string value helper functions
  ////////////////////////////////////////////////////////////////
  /* regexp, number string */
  private val hex = "(0[xX][0-9a-fA-F]+)".r.pattern
  private val exp = "[eE][+-]?[0-9]+"
  private val dec1 = "[0-9]+\\.[0-9]*(" + exp + ")?"
  private val dec2 = "\\.[0-9]+(" + exp + ")?"
  private val dec3 = "[0-9]+(" + exp + ")?"
  private val dec = "([+-]?(Infinity|(" + dec1 + ")|(" + dec2 + ")|(" + dec3 + ")))"
  private val num_regexp = ("NaN|(" + hex + ")|(" + dec + ")").r.pattern

  def isHex(str: String): Boolean =
    hex.matcher(str).matches()

  def isNum(str: String): Boolean =
    num_regexp.matcher(str).matches()
}
