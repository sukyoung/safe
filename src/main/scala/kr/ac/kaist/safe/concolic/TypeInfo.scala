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

package kr.ac.kaist.safe.concolic

case class TypeInfo(
    paramType: String,
    var constructorNames: List[String] = Nil,
    var properties: List[String] = Nil
) {
  def addConstructors(x: List[String]): Unit = {
    val distinctConstructorNames = (constructorNames ::: x).distinct
    constructorNames = distinctConstructorNames
  }

  def setProperties(x: List[String]): Unit = {
    //    println(s"In setProperties, $this x = $x")
    val distinctProperties = (properties ::: x).distinct
    properties = distinctProperties
  }

  def getConstructor: String = constructorNames.head
  def getProperties: List[String] = properties

}
