/**
 * *****************************************************************************
 * Copyright (c) 2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.json

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.json.AbsValueProtocol._
import kr.ac.kaist.safe.errors.error.{
  DefSetParseError,
  AbsDataPropParseError,
  AbsMapParseError,
  INameParseError,
  AbsIValueParseError,
  ObjInternalMapParseError,
  AbsObjectParseError
}

import spray.json._
import DefaultJsonProtocol._

object AbsObjectProtocol extends DefaultJsonProtocol {

  implicit object DefSetJsonFormat extends RootJsonFormat[DefSet] {

    def write(set: DefSet): JsValue = set match {
      case DefSetTop => JsNull
      case DefSetFin(s) => JsArray(s.to[Vector].map(JsString(_)))
    }

    def read(value: JsValue): DefSet = value match {
      case JsNull => DefSetTop
      case JsArray(s) => DefSetFin(
        s.map(_ match {
        case JsString(str) => str
        case _ => throw DefSetParseError(value)
      }).to[Set]
      )
      case _ => throw DefSetParseError(value)
    }
  }

  implicit object AbsDataPropFormat extends RootJsonFormat[AbsDataProp] {

    def write(prop: AbsDataProp): JsValue = prop match {
      case DefaultDataProp.Dom(value, b0, b1, b2) =>
        JsArray(value.toJson, b0.toJson, b1.toJson, b2.toJson)
    }

    def read(value: JsValue): AbsDataProp = value match {
      case JsArray(Vector(value, b0, b1, b2)) =>
        DefaultDataProp.Dom(
          value.convertTo[AbsValue],
          b0.convertTo[AbsBool],
          b1.convertTo[AbsBool],
          b2.convertTo[AbsBool]
        )
      case _ => throw AbsDataPropParseError(value)
    }
  }

  implicit object AbsMapJsonFormat extends RootJsonFormat[AbsMap] {

    def write(map: AbsMap): JsValue = map match {
      case AbsMapBot => JsTrue
      case AbsMapEmpty => JsFalse
      case AbsMapFin(m, s) => JsArray(
        JsArray(m.to[Vector].map { case (str, prop) => JsArray(str.toJson, prop.toJson) }),
        s.toJson
      )
    }

    def read(value: JsValue): AbsMap = value match {
      case JsTrue => AbsMapBot
      case JsFalse => AbsMapEmpty
      case JsArray(Vector(JsArray(m), s)) => AbsMapFin(
        m.map(_ match {
        case JsArray(Vector(str, prop)) =>
          str.convertTo[AbsString] -> prop.convertTo[AbsDataProp]
        case _ => throw AbsMapParseError(value)
      }).toMap,
        s.convertTo[DefSet]
      )
      case _ => throw AbsMapParseError(value)
    }
  }

  implicit object INameJsonFormat extends RootJsonFormat[IName] {
    val map: Map[IName, Int] = Map(
      IPrototype -> 0,
      IClass -> 1,
      IExtensible -> 2,
      IPrimitiveValue -> 3,
      ICall -> 4,
      IConstruct -> 5,
      IScope -> 6,
      IHasInstance -> 7
    )
    val imap: Map[Int, IName] = map.map(_.swap)

    def write(name: IName): JsValue = JsNumber(map(name))
    def read(value: JsValue): IName = value match {
      case JsNumber(n) => imap(n.toInt)
      case _ => throw INameParseError(value)
    }
  }

  implicit object AbsIValueFormat extends RootJsonFormat[AbsIValue] {

    def write(value: AbsIValue): JsValue = value match {
      case AbsIValue(v, s) => JsArray(
        v.toJson,
        JsArray(s.to[Vector].map(JsNumber(_)))
      )
    }

    def read(value: JsValue): AbsIValue = value match {
      case JsArray(Vector(v, JsArray(s))) => AbsIValue(
        v.convertTo[AbsValue],
        s.map(_ match {
        case JsNumber(n) => n.toInt
        case _ => throw AbsIValueParseError(value)
      }).to[Set]
      )
      case _ => throw AbsIValueParseError(value)
    }
  }

  implicit object ObjInternalMapFormat extends RootJsonFormat[ObjInternalMap] {

    def write(map: ObjInternalMap): JsValue =
      JsArray(map.to[Vector].map { case (name, value) => JsArray(name.toJson, value.toJson) })
    def read(value: JsValue): ObjInternalMap = value match {
      case JsArray(v) => v.map(_ match {
        case JsArray(Vector(name, value)) => name.convertTo[IName] -> value.convertTo[AbsIValue]
        case _ => throw ObjInternalMapParseError(value)
      }).toMap
      case _ => throw ObjInternalMapParseError(value)
    }
  }

  implicit object AbsObjectFormat extends RootJsonFormat[AbsObject] {

    def write(obj: AbsObject): JsValue = obj match {
      case DefaultObject.Top => JsNull
      case DefaultObject.ObjMap(amap, imap) => JsArray(amap.toJson, imap.toJson)
    }

    def read(value: JsValue): AbsObject = value match {
      case JsNull => DefaultObject.Top
      case JsArray(Vector(amap, imap)) =>
        DefaultObject.ObjMap(amap.convertTo[AbsMap], imap.convertTo[ObjInternalMap])
      case _ => throw AbsObjectParseError(value)
    }
  }
}
