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

import spray.json._

////////////////////////////////////////////////////////////////////////////////
// concrete string type
////////////////////////////////////////////////////////////////////////////////
case class Str(str: String) extends PValue {
  override def toString: String = s""""$str""""
}

////////////////////////////////////////////////////////////////////////////////
// string abstract domain
////////////////////////////////////////////////////////////////////////////////
trait AbsString extends AbsDomain[Str, AbsString] {
  def ===(that: AbsString): AbsBool
  def <(that: AbsString): AbsBool

  def isNum: AbsBool

  def trim: AbsString
  def concat(that: AbsString): AbsString
  def charAt(pos: AbsNumber): AbsString
  def charCodeAt(pos: AbsNumber): AbsNumber
  def contains(that: AbsString): AbsBool
  def length: AbsNumber
  def toLowerCase: AbsString
  def toUpperCase: AbsString

  def isArrayIndex: AbsBool

  def isRelated(str: String): Boolean
  def isRelated(astr: AbsString): Boolean

  def toAbsNumber: AbsNumber
  def toAbsBoolean: AbsBool

  def json: JsValue
}

trait AbsStringUtil extends AbsDomainUtil[Str, AbsString] {
  // abstraction from all number string
  val Number: AbsString

  // abstraction from all non-number string
  val Other: AbsString

  def fromCharCode(n: AbsNumber): AbsString

  def fromJson(value: JsValue): AbsString
}
