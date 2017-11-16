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

import kr.ac.kaist.safe.phase.HeapBuildConfig
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.errors.error.{
  AbsNumberUtilParseError,
  AbsStringUtilParseError,
  AAddrTypeParseError,
  HeapBuildConfigParseError
}

import spray.json._
import DefaultJsonProtocol._

object ConfigProtocol extends DefaultJsonProtocol {

  implicit object AbsNumberUtilFormat extends RootJsonFormat[AbsNumberUtil] {

    def write(util: AbsNumberUtil): JsValue = util match {
      case DefaultNumber => JsTrue
      case FlatNumber => JsFalse
    }

    def read(value: JsValue): AbsNumberUtil = value match {
      case JsTrue => DefaultNumber
      case JsFalse => FlatNumber
      case _ => throw AbsNumberUtilParseError(value)
    }
  }

  implicit object AbsStringUtilFormat extends RootJsonFormat[AbsStringUtil] {

    def write(util: AbsStringUtil): JsValue = util match {
      case StringSet(n) => JsNumber(n)
    }

    def read(value: JsValue): AbsStringUtil = value match {
      case JsNumber(n) =>
        val k = n.toInt
        AbsString match {
          case StringSet(k) => AbsString
          case _ => StringSet(k)
        }
      case _ => throw AbsStringUtilParseError(value)
    }
  }

  implicit object AAddrTypeFormat extends RootJsonFormat[AAddrType] {

    val map: Map[AAddrType, Int] = Map(
      NormalAAddr -> 0,
      RecencyAAddr -> 1
    )
    val imap: Map[Int, AAddrType] = map.map(_.swap)

    def write(addr: AAddrType): JsValue = JsNumber(map(addr))
    def read(value: JsValue): AAddrType = value match {
      case JsNumber(n) => imap(n.toInt)
      case _ => throw AAddrTypeParseError(value)
    }
  }

  implicit object HeapBuildConfigFormat extends RootJsonFormat[HeapBuildConfig] {

    def write(config: HeapBuildConfig): JsValue = config match {
      case HeapBuildConfig(silent, _, _, _, num, str, call, loop, snapshot, js, addr) => JsArray(
        JsBoolean(silent),
        num.toJson,
        str.toJson,
        JsNumber(call),
        JsNumber(loop),
        snapshot match {
          case Some(snap) => JsString(snap)
          case None => JsNull
        },
        JsBoolean(js),
        addr.toJson
      )
    }

    def read(value: JsValue): HeapBuildConfig = value match {
      case JsArray(Vector(JsBoolean(silent), num, str, JsNumber(call), JsNumber(loop), snap, JsBoolean(js), addr)) =>
        HeapBuildConfig(
          silent,
          AbsNumber = num.convertTo[AbsNumberUtil],
          AbsString = str.convertTo[AbsStringUtil],
          callsiteSensitivity = call.toInt,
          loopSensitivity = loop.toInt,
          snapshot = snap match {
            case JsString(s) => Some(s)
            case JsNull => None
            case _ => throw HeapBuildConfigParseError(value)
          },
          jsModel = js,
          aaddrType = addr.convertTo[AAddrType]
        )
      case _ => throw HeapBuildConfigParseError(value)
    }
  }
}
