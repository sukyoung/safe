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

import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.nodes.cfg.FunctionId
import scala.collection.immutable.{ HashMap, HashSet }

package object domain {
  ////////////////////////////////////////////////////////////////
  // value constructors
  ////////////////////////////////////////////////////////////////
  val ExcSetEmpty: Set[Exception] = HashSet[Exception]()
  val ExcSetTop: Set[Exception] = null // TODO refactoring

  val FidSetEmpty: Set[FunctionId] = HashSet[FunctionId]()

  type ObjInternalMap = Map[InternalName, InternalValue]
  val ObjEmptyMap: Map[String, (PropValue, AbsAbsent)] = HashMap()
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

  def isNumber(str: String): Boolean =
    numRegexp.matcher(str).matches()

  ////////////////////////////////////////////////////////////////
  // implicit conversion for domains
  ////////////////////////////////////////////////////////////////
  // Boolean <-> Bool
  implicit def bool2boolean(b: Bool): Boolean = b.b
  implicit def boolean2bool(b: Boolean): Bool = Bool(b)
  implicit def booleanSet2bool(set: Set[Boolean]): Set[Bool] = set.map(boolean2bool)

  // Double <-> Num
  implicit def num2double(num: Num): Double = num.num
  implicit def double2num(num: Double): Num = Num(num)
  implicit def doubleSet2num(set: Set[Double]): Set[Num] = set.map(double2num)

  // String <-> Str
  implicit def str2string(str: Str): String = str.str
  implicit def string2str(str: String): Str = Str(str)
  implicit def stringSet2str(set: Set[String]): Set[Str] = set.map(string2str)

  // primitive abstract domains -> AbsPValue
  implicit def undef2pv[T <% AbsUndef](undef: T): AbsPValue = AbsPValue(undef)
  implicit def null2pv[T <% AbsNull](x: T): AbsPValue = AbsPValue(x)
  implicit def bool2pv[T <% AbsBool](b: T): AbsPValue = AbsPValue(b)
  implicit def num2pv[T <% AbsNumber](num: T): AbsPValue = AbsPValue(num)
  implicit def str2pv[T <% AbsString](str: T): AbsPValue = AbsPValue(str)

  // AbsPValue -> AbsValue
  implicit def pv2v[T <% AbsPValue](pv: T): AbsValue = AbsValue(pv)

  // AbsLoc -> AbsValue
  implicit def loc2v[T <% AbsLoc](loc: T): AbsValue = AbsValue(loc)
}
