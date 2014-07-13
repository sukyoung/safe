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
  override def toString: String = value
}
/*abstract class SymbolicValue {
  var _value: String = null 
  var _type: String = null
  def getValue: String = _value
  def getType: String = _type
}
case class SymbolicUndef() extends SymbolicValue {
  _type = "Undefined"
}
case class SymbolicNull() extends SymbolicValue {
  _type = "Null"
} 
case class SymbolicBool(n: String) extends SymbolicValue {
  _type = "Boolean" 
  _value = n
}
case class SybmolicNumber(n: String) extends SymbolicValue {
  _type = "Number"
  _value = n
}
case class SybmolicString(n: String) extends SymbolicValue {
  _type = "String"
  _value = n
}
case class SybmolicObject(n: String) extends SymbolicValue {
  _type = "Object"
  _value = n
}*/


