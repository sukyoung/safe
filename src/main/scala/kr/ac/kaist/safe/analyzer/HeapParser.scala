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

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.errors.error.HeapParseError
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.util._
import scala.collection.immutable.HashMap
import spray.json._

object HeapParser extends DefaultJsonProtocol {
  implicit object testFormat extends RootJsonFormat[Heap] {
    def read(value: JsValue): Heap = {
      value match {
        case JsObject(objs) => {
          val map = objs.foldLeft[Map[Loc, Obj]](HashMap()) {
            case (map, (loc, JsObject(props))) => {
              type NMap = Map[String, DataProp]
              type IMap = Map[IName, Value]
              val (nmap, imap) = props.foldLeft[(NMap, IMap)]((HashMap(), HashMap())) {
                case ((am, im), (key, value)) => key match {
                  case "[[Prototype]]" => (am, im + (IPrototype -> readValue(value)))
                  case "[[Class]]" => (am, im + (IClass -> readValue(value)))
                  case "[[Extensible]]" => (am, im + (IExtensible -> readValue(value)))
                  case "[[PrimitiveValue]]" => (am, im + (IPrimitiveValue -> readValue(value)))
                  case "[[Call]]" => (am, im + (ICall -> readValue(value)))
                  case "[[Construct]]" => (am, im + (IConstruct -> readValue(value)))
                  case "[[Scope]]" => (am, im + (IScope -> readValue(value)))
                  case "[[HasInstance]]" => (am, im + (IHasInstance -> readValue(value)))
                  case _ => (am + (key -> readProp(value)), im)
                }
              }
              map + (readLoc(loc) -> Obj(nmap, imap))
            }
            case _ => throw HeapParseError("an object should be represented as an object in JSON.")
          }
          Heap(map)
        }
        case _ => throw HeapParseError("a heap should be represented as an object in JSON.")
      }
    }

    def write(heap: Heap): JsValue = JsObject() // TODO

    private def readValue(jsValue: JsValue): Value = jsValue match {
      // number
      case JsNumber(n) => n.toDouble
      case JsString("@NaN") => Double.NaN
      case JsString("@PosInf") => Double.PositiveInfinity
      case JsString("@NegInf") => Double.NegativeInfinity

      // boolean
      case JsBoolean(b) => b

      // null
      case JsNull => Null

      // undefined
      case JsString("@undef") => Undef

      // location
      case JsString(str) if str.startsWith("#") =>
        Loc(str.substring(1))

      // string
      case JsString(str) => str

      // error
      case _ => throw HeapParseError("wrong format for values")
    }

    private def readBool(jsValue: JsValue): Bool = jsValue match {
      case JsBoolean(b) => b
      case _ => throw HeapParseError("wrong format for boolean values")
    }

    private def readLoc(str: String): Loc = str.startsWith("#") match {
      case true => Loc(str.substring(1))
      case false => throw HeapParseError("wrong format for locations")
    }

    private def readProp(jsValue: JsValue): DataProp = jsValue match {
      case JsObject(fields) => {
        def get(key: String): JsValue = fields.get(key) match {
          case None => throw HeapParseError("wrong format for data properties")
          case Some(v) => v
        }
        val value = readValue(get("value"))
        val writable = readBool(get("writable"))
        val enumerable = readBool(get("enumerable"))
        val configurable = readBool(get("configurable"))

        DataProp(value, writable, enumerable, configurable)
      }
      case _ => throw HeapParseError("wrong format for data properties")
    }
  }
}
