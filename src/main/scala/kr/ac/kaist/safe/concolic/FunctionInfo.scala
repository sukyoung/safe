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

import scala.collection.mutable.Map

class FunctionInfo() {
  /* STORE FUNCTION INFORMATION */
  var params = Map[Int, List[TypeInfo]]()
  def storeParameter(n: Int, t: TypeInfo) = {
    val temp = params.get(n) match {
      case Some(ts) =>
        if (ts.map(_.paramType).contains(t.paramType)) {
          if (t.paramType == "Object") {
            val temp = ts.filter(_.paramType == "Object").head
            temp.addConstructors(t.constructorNames)

            ts.filterNot(_.paramType == "Object") :+ temp
          } else
            ts
        } else
          ts :+ t
      case None => List(t)
    }
    params(n) = temp
  }

  var thisObject: TypeInfo = null
  def getThisObject = thisObject
  def setThisObject(x: TypeInfo) = thisObject = x

  /* USE FUNCTION INFORMATION */
  def getType(n: Int): List[String] = params(n).map(_.paramType)
  def getObjectInformation(n: Int): Option[TypeInfo] = {
    val obj = params(n).filter(_.paramType == "Object")
    if (obj.nonEmpty) Some(obj.head)
    else None
  }
  def getObjectConstructors(n: Int): List[String] = getObjectInformation(n) match {
    case Some(info) => info.constructorNames
    case None => List[String]()
  }
  def getObjectProperties(n: Int): Option[List[String]] = getObjectInformation(n) match {
    case Some(info) => Some(info.properties)
    case None => None
  }

  def getThisConstructors: List[String] = thisObject.constructorNames
  def getThisProperties: List[String] = if (thisObject != null) thisObject.properties else List()
  def hasThisObject: Boolean = thisObject != null

  /* CHECK FOR CONCOLIC TESTING */
  var depth: Int = 0
  def initDepth(): Unit = depth = 0
  def checkRecursive(limit: Int) = {
    val res = if (depth < limit) true else false
    depth += 1
    res
  }

  def isRecursive: Boolean = depth > 1
  var isTarget: Boolean = false
  var isCandidate: Boolean = false
  var isProcess: Boolean = false
  // only set a function as candidate when it has parameters
  def setCandidate(): Unit = isCandidate = true
  def targeting(): Unit = isTarget = true
  def done(): Unit = { isTarget = false; isCandidate = false; isProcess = false }
}
