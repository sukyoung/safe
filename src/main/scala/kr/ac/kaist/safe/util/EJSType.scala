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

package kr.ac.kaist.safe.util

trait EJSTypeEnum {
  def isNumber: Boolean = false
  def isString: Boolean = false
  def isObject: Boolean = false
}

// Enumeration of JavaScript Type
object EJSType {
  case object UNDEFINED extends EJSTypeEnum {
    override def toString: String = "Undefined"
  }
  case object NULL extends EJSTypeEnum {
    override def toString: String = "Null"
  }
  case object BOOLEAN extends EJSTypeEnum {
    override def toString: String = "Boolean"
  }
  case object NUMBER extends EJSTypeEnum {
    override def toString: String = "Number"
    override def isNumber: Boolean = true
  }
  case object NUMBER_INT extends EJSTypeEnum {
    override def toString: String = "Number"
    override def isNumber: Boolean = true
  }
  case object NUMBER_DOUBLE extends EJSTypeEnum {
    override def toString: String = "Number"
    override def isNumber: Boolean = true
  }
  case object STRING extends EJSTypeEnum {
    override def toString: String = "String"
    override def isString: Boolean = true
  }
  case object OBJECT extends EJSTypeEnum {
    override def toString: String = "Object"
    override def isObject: Boolean = true
  }
  case object OBJECT_FUNCTION extends EJSTypeEnum {
    override def toString: String = "Object"
    override def isObject: Boolean = true
  }
}
