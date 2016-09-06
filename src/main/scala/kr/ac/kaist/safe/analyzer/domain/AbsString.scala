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

// string abstract domain
trait AbsString extends AbsDomain[String, AbsString] with Primitive {
  def gamma: ConSet[String]
  def gammaSingle: ConSingle[String]
  def gammaSimple: ConSimple[String]
  def gammaIsAllNums: ConSingle[Boolean]

  def ===(that: AbsString): AbsBool
  def <(that: AbsString): AbsBool

  def trim: AbsString
  def concat(that: AbsString): AbsString
  def charAt(pos: AbsNumber): AbsString
  def charCodeAt(pos: AbsNumber): AbsNumber
  def contains(that: AbsString): AbsBool
  def length: AbsNumber
  def toLowerCase: AbsString
  def toUpperCase: AbsString

  def isAllNums: Boolean
  def isAllNotNumbers: Boolean
  def isArrayIndex: AbsBool
}

trait AbsStringUtil extends AbsDomainUtil[String, AbsString] {
  // abstraction from all number string
  val Number: AbsString

  // abstraction from all non-number string
  val NotNumber: AbsString
}
