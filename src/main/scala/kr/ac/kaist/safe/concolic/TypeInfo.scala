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
    constructorNames: List[String] = Nil,
    properties: List[String] = Nil
) {
  def addConstructors(x: List[String]): TypeInfo = {
    val distinctConstructorNames = (constructorNames ::: x).distinct
    this.copy(constructorNames = distinctConstructorNames)
  }

  def setProperties(x: List[String]): TypeInfo = {
    val distinctProperties = (properties ::: x).distinct
    this.copy(properties = distinctProperties)
  }

}
