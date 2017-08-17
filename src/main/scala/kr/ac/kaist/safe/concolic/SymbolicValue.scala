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

class SymbolicValue {
  var value: String = null
  def getValue: String = value
  // Types from analysis part
  var types: List[String] = null
  def getTypes: List[String] = types
  // Types from execution part
  var instance: String = null
  def setInstance(newInstance: String): Unit = instance = newInstance
  def getInstance: String = instance
  var fromConcrete: Boolean = false

  def makeSymbolicValue(newType: String): Unit = types = List(newType)
  def makeSymbolicValue(newTypes: List[String]): Unit = types = newTypes
  def makeSymbolicValue(newValue: String, newType: String): Unit = {
    value = newValue
    types = List(newType)
  }
  def makeSymbolicValue(newValue: String, newTypes: List[String]): Unit = {
    value = newValue
    types = newTypes
  }
  def makeSymbolicValueFromConcrete(newType: String): Unit = {
    types = List(newType)
    instance = newType
    fromConcrete = true
  }
  def makeSymbolicValueFromConcrete(newValue: String, newType: String): Unit = {
    value = newValue
    types = List(newType)
    instance = newType
    fromConcrete = true
  }

  def isObject: Boolean = {
    if (instance == null)
      false
    else
      instance.equals("Object")
  }
  def isNull: Boolean = {
    if (instance == null)
      false
    else
      instance.equals("Null")
  }

  def isInput(): Boolean = {
    if (value == null)
      return false
    value.contains("i") || value.contains("this")
  }

  override def toString: String = value
  override def equals(another: Any): Boolean = this.toString == another.asInstanceOf[SymbolicValue].toString
}

