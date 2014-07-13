/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util

// Enumeration of JavaScript Type
object EJSType {
  def isNumber(t: Int): Boolean = t / 10 == 4 // 4x
  def isString(t: Int): Boolean = t / 10 == 5 // 5x
  def isObject(t: Int): Boolean = t / 10 == 6 // 6x
  def toString(t: Int): String = t match {
    case UNDEFINED => "Undefined"
    case NULL => "Null"
    case BOOLEAN => "Boolean"
    case NUMBER => "Number"
    case STRING => "String"
    case OBJECT => "Object"
    case OBJECT_FUNCTION => "Object"
  }

  val UNDEFINED = 10
  val NULL = 20
  val BOOLEAN = 30
  val NUMBER = 40
  val NUMBER_INT = 41
  val NUMBER_DOUBLE = 42
  val STRING = 50
  val OBJECT = 60
  val OBJECT_FUNCTION = 61
}
