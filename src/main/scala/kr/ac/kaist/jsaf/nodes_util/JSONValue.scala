/*******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util

import _root_.java.util.{List => JList}
import kr.ac.kaist.jsaf.useful.Pair
import kr.ac.kaist.jsaf.scala_src.useful.Lists._

object JSONValue {
  sealed abstract class JSONCase
  case object JSONNull extends JSONCase
  case object JSONTrue extends JSONCase
  case object JSONFalse extends JSONCase
  case class JSONObject(e: List[(String, JSONValue)]) extends JSONCase
  case class JSONArray(e: List[JSONValue]) extends JSONCase
  case class JSONString(s: String) extends JSONCase
  case class JSONNumber(n: String) extends JSONCase

  val nullJV = new JSONValue(JSONNull)
  val trueJV = new JSONValue(JSONTrue)
  val falseJV = new JSONValue(JSONFalse)
  def nullV = nullJV
  def trueV = trueJV
  def falseV = falseJV
  def objF(e: JList[Pair[String, JSONValue]]) =
    new JSONValue(JSONObject(toList(e).map(p => (p.getA, p.getB))))
  def arrF(e: JList[JSONValue]) = new JSONValue(JSONArray(toList(e)))
  def strF(s: String) = new JSONValue(JSONString(s))
  def numF(n: String) = new JSONValue(JSONNumber(n))
}

class JSONValue(_kind: JSONValue.JSONCase) {
  val kind: JSONValue.JSONCase = _kind

  def get(names: List[String]): Option[String] = names match {
    case Nil => None
    case name::rest =>
      kind match {
        case JSONValue.JSONObject(es) => es.find(e => e._1.equals(name)) match {
          case Some((_, v)) => rest match {
            case Nil => Some(v.toString)
            case _ => v.get(rest)
          }
          case _ => None
        }
        case _ => None
      }
  }

  def join(all: List[Any], sep: String, result: StringBuilder): StringBuilder = all match {
    case Nil => result
    case _ => result.length match {
      case 0 => {
        join(all.tail, sep, result.append(toStr(all.head)))
      }
      case _ =>
        join(all.tail, sep, result.append(sep).append(toStr(all.head)))
    }
  }

  def toStr(a: Any) = a match {
    case pair: (_, _) => "\""+pair._1.asInstanceOf[String]+"\":"+pair._2.asInstanceOf[JSONValue].toString
    case _ => a.toString
  }

  override def toString = kind match {
    case JSONValue.JSONNull => "null"
    case JSONValue.JSONTrue => "true"
    case JSONValue.JSONFalse => "false"
    case JSONValue.JSONObject(es) =>
      val s: StringBuilder = new StringBuilder
      s.append("{")
      s.append(join(es, ",\n", new StringBuilder))
      s.append("}")
      s.toString
    case JSONValue.JSONArray(es) =>
      val s: StringBuilder = new StringBuilder
      s.append("[")
      s.append(join(es, ",\n", new StringBuilder))
      s.append("]")
      s.toString
    case JSONValue.JSONString(s) => "\""+s+"\""
    case JSONValue.JSONNumber(n) => n
  }
}
