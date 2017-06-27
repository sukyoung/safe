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

package kr.ac.kaist.safe.json

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.json.AbsValueProtocol._
import kr.ac.kaist.safe.errors.error.{
  AbsAbsentParseError,
  AbsBindingParseError,
  EnvMapParseError,
  AbsDecEnvRecParseError,
  AbsGlobalEnvRecParseError,
  AbsEnvRecParseError,
  AbsLexEnvParseError
}

import spray.json._
import DefaultJsonProtocol._

object AbsLexEnvProtocol extends DefaultJsonProtocol {

  implicit object AbsAbsentJsonFormat extends RootJsonFormat[AbsAbsent] {
    val map: Map[AbsAbsent, Int] = Map(DefaultAbsent.Bot -> 0, DefaultAbsent.Top -> 1)
    val imap: Map[Int, AbsAbsent] = map.map(_.swap)

    def write(ab: AbsAbsent): JsValue = JsNumber(map(ab))
    def read(value: JsValue): AbsAbsent = value match {
      case JsNumber(n) => imap(n.toInt)
      case _ => throw AbsAbsentParseError(value)
    }
  }

  implicit object AbsBindingJsonFormat extends RootJsonFormat[AbsBinding] {

    def write(bind: AbsBinding): JsValue = bind match {
      case DefaultBinding.Dom(vl, ab, bl) => JsArray(vl.toJson, ab.toJson, bl.toJson)
    }

    def read(value: JsValue): AbsBinding = value match {
      case JsArray(Vector(vl, ab, bl)) =>
        DefaultBinding.Dom(
          vl.convertTo[AbsValue],
          ab.convertTo[AbsAbsent],
          bl.convertTo[AbsBool]
        )
      case _ => throw AbsBindingParseError(value)
    }
  }

  def envMapToJson(map: Map[String, (AbsBinding, AbsAbsent)]): JsValue = JsArray(
    map.to[Vector].map {
      case (str, (bind, ab)) => JsArray(JsString(str), bind.toJson, ab.toJson)
    }
  )

  def jsonToEnvMap(value: JsValue): Map[String, (AbsBinding, AbsAbsent)] = value match {
    case JsArray(v) => v.map(_ match {
      case JsArray(Vector(JsString(str), bind, ab)) =>
        str -> (bind.convertTo[AbsBinding], ab.convertTo[AbsAbsent])
      case _ => throw EnvMapParseError(value)
    }).toMap
    case _ => throw EnvMapParseError(value)
  }

  implicit object AbsDecEnvRecJsonFormat extends RootJsonFormat[AbsDecEnvRec] {

    def write(rec: AbsDecEnvRec): JsValue = rec match {
      case DefaultDecEnvRec.Bot => JsNull
      case DefaultDecEnvRec.LBindMap(map) => JsArray(JsString("L"), envMapToJson(map))
      case DefaultDecEnvRec.UBindMap(map) => JsArray(JsString("U"), envMapToJson(map))
    }

    def read(value: JsValue): AbsDecEnvRec = value match {
      case JsNull => DefaultDecEnvRec.Bot
      case JsArray(Vector(JsString("L"), map)) => DefaultDecEnvRec.LBindMap(jsonToEnvMap(map))
      case JsArray(Vector(JsString("U"), map)) => DefaultDecEnvRec.UBindMap(jsonToEnvMap(map))
      case _ => throw AbsDecEnvRecParseError(value)
    }
  }

  implicit object AbsGlobalEnvRecJsonFormat extends RootJsonFormat[AbsGlobalEnvRec] {
    val map: Map[AbsGlobalEnvRec, Int] = Map(
      DefaultGlobalEnvRec.Bot -> 0,
      DefaultGlobalEnvRec.Top -> 1
    )
    val imap: Map[Int, AbsGlobalEnvRec] = map.map(_.swap)

    def write(rec: AbsGlobalEnvRec): JsValue = JsNumber(map(rec))
    def read(value: JsValue): AbsGlobalEnvRec = value match {
      case JsNumber(n) => imap(n.toInt)
      case _ => throw AbsGlobalEnvRecParseError(value)
    }
  }

  implicit object AbsEnvRecJsonFormat extends RootJsonFormat[AbsEnvRec] {

    def write(rec: AbsEnvRec): JsValue = rec match {
      case DefaultEnvRec.Dom(d, g) => JsArray(d.toJson, g.toJson)
    }

    def read(value: JsValue): AbsEnvRec = value match {
      case JsArray(Vector(d, g)) =>
        DefaultEnvRec.Dom(d.convertTo[AbsDecEnvRec], g.convertTo[AbsGlobalEnvRec])
      case _ => throw AbsEnvRecParseError(value)
    }
  }

  implicit object AbsLexEnvJsonFormat extends RootJsonFormat[AbsLexEnv] {

    def write(env: AbsLexEnv): JsValue = env match {
      case DefaultLexEnv.Dom(rec, loc, ab) => JsArray(rec.toJson, loc.toJson, ab.toJson)
    }

    def read(value: JsValue): AbsLexEnv = value match {
      case JsArray(Vector(rec, loc, ab)) =>
        DefaultLexEnv.Dom(rec.convertTo[AbsEnvRec], loc.convertTo[AbsLoc], ab.convertTo[AbsAbsent])
      case _ => throw AbsLexEnvParseError(value)
    }
  }
}
