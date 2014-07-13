/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.concolic

import _root_.edu.rice.cs.plt.tuple.{Pair => JPair}
import _root_.java.lang.{Integer => JInteger}
import _root_.java.util.{List => JList}
import _root_.java.util.{Map => JavaMap}
import _root_.java.util.{HashMap => JavaHashMap}
import _root_.edu.rice.cs.plt.tuple.{Option => JavaOption}
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.EJSOp
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Maps._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.scala_src.useful.Pairs._
import kr.ac.kaist.jsaf.scala_src.useful.Sets._
import scala.collection.mutable.Map

class FunctionInfo() {
  var isTarget: Boolean = false
  var isCandidate: Boolean = false
  var isProcess: Boolean = false
  var params: Map[Int, List[String]] = Map[Int, List[String]]()
  var objects: Map[Int, (String, List[(String, String)])] = Map[Int, (String, List[(String, String)])]() 
  var thisObject: List[(String, String)] = List[(String, String)]() 
  var thisName: String = null
  
  def toJavaObjects: JavaMap[JInteger, JPair[String, JList[JPair[String, String]]]] = {
    var jobjects = new JavaHashMap[JInteger, JPair[String, JList[JPair[String, String]]]]()
    for (key <- objects.keysIterator) {
      jobjects.put(key, JPair.make(objects(key)._1, toJavaList(objects(key)._2.map(toJavaPair(_)))))
    }
    jobjects
  }
  def toJavaThisObject: JList[JPair[String, String]] = toJavaList(thisObject.map(toJavaPair(_)))

  def isNewType(p: Int, t: String) = params.get(p) match { case Some(types) => !types.contains(t); case None => true }

  def storeParameter(p: Int, t: String) = params.put(p, params.get(p) match { case Some(types) => types:+t; case None => List(t)})

  def storeObjectProperties(p: Int, t: (String, List[(String, String)])) = objects.put(p, t)
  def getObjectProperties(obj: Int): Option[List[(String, String)]] = objects.get(obj) match {
    case Some(p) => Some(p._2)
    case None => None
  }
  def getObjectConstruct(obj: Int): String = objects(obj)._1
  
  def storeThisObject(props: List[(String, String)]) = thisObject = props
  def storeThisName(name: String) = thisName = name 
  // only set a function as candidate when it has parameters
  def setCandidate() = isCandidate = true
  
  def targeting = isTarget = true
  def processing = isProcess = true
  def unprocessing = isProcess = false
  def done = {isTarget = false; isCandidate = false; isProcess = false}

  override def toString = "Function Information: target? "+isTarget+" candidate? "+isCandidate+" parameter information? "+params+"\n"
}

