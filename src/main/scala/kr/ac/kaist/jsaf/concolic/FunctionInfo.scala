/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.concolic

import _root_.java.lang.{Integer => JInteger}
import _root_.java.util.{List => JList}
import _root_.java.util.{Map => JavaMap}
import _root_.java.util.{HashMap => JavaHashMap}
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import scala.collection.mutable.Map


class FunctionInfo() {
  /* STORE FUNCTION INFORMATION */
  var params = Map[Int, List[TypeInfo]]() 
  def storeParameter(n: Int, t: TypeInfo) = {
    val temp = params.get(n) match {
      case Some(ts) => 
        if (ts.map(_.getType).contains(t.getType)) {
          if (t.getType == "Object") {
            var temp = ts.filter(_.getType == "Object")(0)
            temp.addConstructors(t.getConstructors)  

            ts.filterNot(_.getType == "Object") :+ temp
          }
          else
            ts
        }
        else 
          ts:+t
      case None => List(t) 
    }
    params(n) = temp
  }

  var thisObject: TypeInfo = null 
  def getThisObject = thisObject
  def setThisObject(x: TypeInfo) = thisObject = x

  /* USE FUNCTION INFORMATION */
  def getType(n: Int): List[String] = params(n).map(_.getType)
  def getObjectInformation(n: Int): Option[TypeInfo] = {
    val obj = params(n).filter(_.getType == "Object")
    if (obj.nonEmpty) Some(obj(0)) 
    else None
  }
  def getObjectConstructors(n: Int): List[String] = getObjectInformation(n) match {
    case Some(info) => info.getConstructors
    case None => List[String]()
  }
  def getObjectProperties(n: Int): Option[List[String]] = getObjectInformation(n) match {
    case Some(info) => Some(info.getProperties)
    case None => None
  }

  def getThisConstructors() = thisObject.getConstructors
  def getThisProperties(): List[String] = if (thisObject != null) thisObject.getProperties else List()
  def hasThisObject() = thisObject != null

  /* CHECK FOR CONCOLIC TESTING */
  var depth = 0
  def initDepth = depth = 0
  def checkRecursive(limit: Int) = {
    val res = if (depth < limit) true else false
    depth += 1 
    res
  }

  def isRecursive = depth > 1
  var isTarget: Boolean = false
  var isCandidate: Boolean = false
  var isProcess: Boolean = false
  // only set a function as candidate when it has parameters
  def setCandidate = isCandidate = true
  def targeting = isTarget = true
  def done = {isTarget = false; isCandidate = false; isProcess = false}

  /* HELPER FUNCTIONS */
  def getJavaObjects(): JavaMap[JInteger, TypeInfo] = {
    var result = new JavaHashMap[JInteger, TypeInfo]
    for (key <- params.keysIterator) {
      getObjectInformation(key) match {
        case Some(info) => result.put(key, info)
        case None => 
      }
    }
    result
  }
  def getJavaThisProperties(): JList[String] = toJavaList(getThisProperties) 
}
