/**
 * *****************************************************************************
 * Copyright (c) 2018, KAIST.
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
import kr.ac.kaist.safe.errors.error._

import spray.json._
import DefaultJsonProtocol._

object ConfigProtocol extends DefaultJsonProtocol {

  implicit object NumDomainFormat extends RootJsonFormat[NumDomain] {

    def write(util: NumDomain): JsValue = util match {
      case DefaultNumber => JsTrue
      // TODO case FlatNumber => JsFalse
    }

    def read(value: JsValue): NumDomain = value match {
      case JsTrue => DefaultNumber
      // TODO case JsFalse => FlatNumber
      case _ => throw NumDomainParseError(value)
    }
  }

  implicit object StrDomainFormat extends RootJsonFormat[StrDomain] {

    def write(util: StrDomain): JsValue = util match {
      case StringSet(n) => JsNumber(n)
    }

    def read(value: JsValue): StrDomain = value match {
      case JsNumber(n) => StringSet(n.toInt)
      case _ => throw StrDomainParseError(value)
    }
  }

  implicit object HeapBuildConfigFormat extends RootJsonFormat[HeapBuildConfig] {

    def write(config: HeapBuildConfig): JsValue = config match {
      case HeapBuildConfig(silent, _, _, _, num, str, call, loop, snapshot, js, _, _) => JsArray()
    }

    def read(value: JsValue): HeapBuildConfig = value match {
      case JsArray(Vector(JsBoolean(silent), num, str, JsNumber(call), JsNumber(loop), snap, JsBoolean(js), addr)) =>
        HeapBuildConfig()
      case _ => throw HeapBuildConfigParseError(value)
    }
  }
}
