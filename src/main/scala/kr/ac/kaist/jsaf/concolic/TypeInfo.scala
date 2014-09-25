/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.concolic

import _root_.java.util.{List => JList}
import kr.ac.kaist.jsaf.scala_src.useful.Lists._

class TypeInfo(t: String) {
  val paramType = t
  def getType() = paramType

  var constructorNames = List[String]()
  def getConstructors() = constructorNames
  def addConstructors(x: List[String]) = {
    var temp = constructorNames:::x
    temp.distinct
    constructorNames = temp
  }

  var properties = List[String]()
  def getProperties() = properties
  def setProperties(x: List[String]) = {
    var temp = properties:::x
    temp.distinct
    properties = temp
  }

  def getJavaConstructor(): String = constructorNames(0)
  def getJavaProperties(): JList[String] = toJavaList(properties) 

}
