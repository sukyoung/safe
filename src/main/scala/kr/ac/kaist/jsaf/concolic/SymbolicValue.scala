/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.concolic

class SymbolicValue {
  var value: String = null
  def getValue = value
  // Types from analysis part
  var types: List[String] = null
  def getTypes = types
  // Types from execution part
  var instance: String = null
  def setInstance(_instance: String) = instance = _instance
  def getInstance = instance
  var fromConcrete: Boolean = false

  def makeSymbolicValue(_type: String) = types = List(_type)
  def makeSymbolicValue(_types: List[String]) = types = _types 
  def makeSymbolicValue(_value: String, _type: String) = {
    value = _value
    types = List(_type)
  }
  def makeSymbolicValue(_value: String, _types: List[String]) = {
    value = _value
    types = _types 
  }
  def makeSymbolicValueFromConcrete(_type: String) = {
    types = List(_type)
    instance = _type
    fromConcrete = true 
  }
  def makeSymbolicValueFromConcrete(_value: String, _type: String) = {
    value = _value
    types = List(_type)
    instance = _type
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
  override def equals(another: Any) = this.toString == another.asInstanceOf[SymbolicValue].toString
}

