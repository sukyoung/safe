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

package kr.ac.kaist.safe.android_checker

import scala.collection.immutable.HashMap
import kr.ac.kaist.safe.util.Span
import kr.ac.kaist.safe.nodes.ir._

object AndroidUtil {
  var clsName = 0
  var objName = 0

  def init: Map[String, JvClass] = {
    clsName = 0; objName = 0
    new HashMap[String, JvClass]()
  }
  def addClass(name: String, cls: JvClass, jvClasses: Map[String, JvClass]): Map[String, JvClass] =
    jvClasses + (name -> cls)

  // JavaScript type for interoperation
  sealed abstract class AndroidType
  case object Top extends AndroidType
  case class JsObject(
      name: String,
      ftn: Option[IRFunctional] = None,
      brg: Option[String] = None
  ) extends AndroidType {
    var isUnknown: Boolean = false
    def know: Unit = isUnknown = false
    def forget: Unit = isUnknown = true
    override def toString: String =
      (if (isUnknown) "Unknown " else "") +
        s"JsObject($name)" +
        (if (ftn.isDefined) " with JsFunction" else "") +
        (if (brg.isDefined) s" with BrgFunction($brg.get)" else "")
  }
  case class BrgObject(name: String) extends AndroidType

  val jsDoubleObject: JsObject = new JsObject("Double")
  def makeJsDummyObject: JsObject = {
    val newobj = new JsObject(objName.toString)
    objName += 1
    newobj
  }
  def makeJsNamedObject(name: String): JsObject = new JsObject(name)
  def makeJsFunction(ftn: IRFunctional): JsObject = {
    val obj = new JsObject(objName.toString, Some(ftn))
    objName += 1
    obj
  }
  def makeBrgFunction(brg: String): JsObject = {
    val obj = new JsObject(objName.toString, None, Some(brg))
    objName += 1
    obj
  }

  // Android Java type
  sealed abstract class JvType
  case object JvTop extends JvType
  case class JvObject(name: String) extends JvType
  case class JvMethod(val params: List[JvType], val result: JvType) extends JvType
  case object JvPrimitive extends JvType

  def str2jv(name: String): JvType = name match {
    case "@Top" => JvTop
    case "V" => JvPrimitive
    case "String" => JvPrimitive
    case _ => new JvObject(name)
  }

  // Android Java class
  class JvClass(val name: String) {
    var jvMethods: Map[(String, Int), JvMethod] = new HashMap[(String, Int), JvMethod]

    def addJvMethod(name: String, params: List[String], result: String): Unit = {
      val key = (name, params.size)
      jvMethods.get(key) match {
        case Some(mth) =>
          if (!mth.result.equals(str2jv(result)))
            jvMethods += (key -> newJvMethod(params, "@Top"))
        case None => jvMethods += (key -> newJvMethod(params, result))
      }
    }

    def addJvMethod(name: String, num: Int, method: JvMethod): Unit =
      jvMethods += ((name, num) -> method)

    def newJvMethod(params: List[String], result: String): JvMethod =
      new JvMethod(params.map(str2jv), str2jv(result))

    def lookupJvMethod(name: String, num: Int): Option[JvMethod] =
      jvMethods.get((name, num))

    def listJvMethods: Set[String] =
      jvMethods.keys.map { case (name, _) => name }.toSet

    override def toString: String = s"Class '$name'\n\t$jvMethods"
  }

  def makeJvDummyClass(classes: List[JvClass]): JvClass = {
    val dummy = new JvClass(clsName.toString)
    clsName += 1

    val keys = classes.map(_.jvMethods.keySet)
    keys.tail.foldLeft(keys.head)((res, mth) => res intersect mth).foreach {
      case (name, num) => {
        val methods = classes.map(_.lookupJvMethod(name, num).get)
        val mth = methods.head
        val newmth =
          if (!methods.tail.map(_.result).filter(_ != mth.result).isEmpty)
            new JvMethod(mth.params, JvTop)
          else
            mth
        dummy.addJvMethod(name, num, newmth)
      }
    }
    dummy
  }

  class AndroidTypeError(msg: String, span: Span) {
    override def toString: String = s"$msg @ $span"
  }

  class AndroidTypeWarning(msg: String, span: Span) {
    override def toString: String = s"$msg @ $span"
  }
}
