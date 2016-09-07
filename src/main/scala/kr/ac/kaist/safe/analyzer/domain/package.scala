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

import kr.ac.kaist.safe.nodes.cfg.FunctionId

import scala.collection.immutable.{ HashMap, HashSet }
import kr.ac.kaist.safe.util.Loc

package object domain {
  ////////////////////////////////////////////////////////////////
  // value constructors
  ////////////////////////////////////////////////////////////////
  val LocSetEmpty: Set[Loc] = HashSet[Loc]()
  val ExceptionSetEmpty: Set[Exception] = HashSet[Exception]()
  val ExceptionSetTop: Set[Exception] = null // TODO refactoring

  val FidSetEmpty: Set[FunctionId] = HashSet[FunctionId]()

  type ObjInternalMap = Map[InternalName, InternalValue]
  val ObjEmptyMap: Map[String, (PropValue, Absent)] = HashMap[String, (PropValue, Absent)]()
  val ObjEmptyIMap: ObjInternalMap = HashMap[InternalName, InternalValue]()

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
  private val numRegexp = ("NaN|(" + hex + ")|(" + dec + ")").r.pattern

  def isHex(str: String): Boolean =
    hex.matcher(str).matches()

  def isNum(str: String): Boolean =
    numRegexp.matcher(str).matches()
}
