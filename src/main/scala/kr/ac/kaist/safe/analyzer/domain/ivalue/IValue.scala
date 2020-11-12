/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import Num._

import spray.json._

// concrete internal value type
abstract class IValue {
  def toJSON: JsValue = this match {
    case Undef => JsObject("undefined" -> JsNull)
    case Null => JsNull
    case Bool(b) => JsBoolean(b)
    case n: Num => JsObject("number" -> (n match {
      case NaN => JsString("NaN")
      case PosInf => JsString("+Infinity")
      case NegInf => JsString("-Infinity")
      case NegZero => JsString("-0")
      case Num(n) => JsNumber(n)
    }))
    case Str(str) => JsString(str)
    case l: Loc => JsObject("location" -> JsString(l.toString))
    case FId(id) => JsObject("fid" -> JsNumber(id))
  }
}
