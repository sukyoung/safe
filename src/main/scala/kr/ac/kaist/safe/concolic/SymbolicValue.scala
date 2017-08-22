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

package kr.ac.kaist.safe.concolic

/**
 *
 * @param optValue
 * @param types Types from analysis part
 * @param optInstance Types from execution part
 */
class SymbolicValue(
    private var optValue: Option[String],
    private var types: List[String],
    private var optInstance: Option[String]
) {

  def getValue: Option[String] = optValue
  def getTypes: List[String] = types
  def setInstance(newInstance: String): Unit = optInstance = Some(newInstance)
  def getInstance: Option[String] = optInstance
  def fromConcrete: Boolean = optInstance.isDefined

  def this(value: String, typ: String) = {
    this(Some(value), List(typ), None)
  }

  def this(value: String, types: List[String]) = {
    this(Some(value), types, None)
  }

  def isObject: Boolean = optInstance match {
    case Some("Object") =>
      true
    case _ =>
      false
  }
  def isNull: Boolean = optInstance match {
    case Some("Null") =>
      true
    case _ =>
      false
  }

  def isInput: Boolean = optValue match {
    case Some(value) =>
      value.contains("i") || value.contains("this")
    case None =>
      false
  }

  override def toString: String = optValue match {
    case Some(value) =>
      value
    case None =>
      "null"
  }
  override def equals(another: Any): Boolean = this.toString == another.asInstanceOf[SymbolicValue].toString
}

object SymbolicValue {

  def makeSymbolicValueFromConcrete(typ: String): SymbolicValue = {
    new SymbolicValue(None, List(typ), Some(typ))
  }

  def makeSymbolicValueFromConcrete(value: String, typ: String): SymbolicValue = {
    new SymbolicValue(Some(value), List(typ), Some(typ))
  }
}

