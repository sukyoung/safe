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

import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.errors.error.{
  AbsUndefParseError,
  AbsNullParseError,
  AbsNumberParseError,
  AbsStringParseError,
  AbsBoolParseError,
  AllocSiteParseError,
  RecencyTagParseError,
  AbsLocParseError,
  AbsPValueParseError,
  AbsValueParseError
}

import spray.json._
import DefaultJsonProtocol._

object AbsValueProtocol extends DefaultJsonProtocol {

  implicit object AbsUndefJsonFormat extends RootJsonFormat[AbsUndef] {
    val map: Map[AbsUndef, Int] = Map(DefaultUndef.Top -> 0, DefaultUndef.Bot -> 1)
    val imap: Map[Int, AbsUndef] = map.map(_.swap)

    def write(undef: AbsUndef): JsValue = JsNumber(map(undef))
    def read(value: JsValue): AbsUndef = value match {
      case JsNumber(n) => imap(n.toInt)
      case _ => throw AbsUndefParseError(value)
    }
  }

  implicit object AbsNullJsonFormat extends RootJsonFormat[AbsNull] {
    val map: Map[AbsNull, Int] = Map(DefaultNull.Top -> 0, DefaultNull.Bot -> 1)
    val imap: Map[Int, AbsNull] = map.map(_.swap)

    def write(nl: AbsNull): JsValue = JsNumber(map(nl))
    def read(value: JsValue): AbsNull = value match {
      case JsNumber(n) => imap(n.toInt)
      case _ => throw AbsNullParseError(value)
    }
  }

  implicit object AbsBoolJsonFormat extends RootJsonFormat[AbsBool] {
    val map: Map[AbsBool, Int] = Map(
      DefaultBool.Top -> 0,
      DefaultBool.Bot -> 1,
      DefaultBool.True -> 2,
      DefaultBool.False -> 3
    )
    val imap: Map[Int, AbsBool] = map.map(_.swap)

    def write(bool: AbsBool): JsValue = JsNumber(map(bool))
    def read(value: JsValue): AbsBool = value match {
      case JsNumber(n) => imap(n.toInt)
      case _ => throw AbsBoolParseError(value)
    }
  }

  implicit object AbsNumberJsonFormat extends RootJsonFormat[AbsNumber] {
    val map: Map[AbsNumber, Int] = Map(
      DefaultNumber.Top -> 0,
      DefaultNumber.Bot -> 1,
      DefaultNumber.Inf -> 2,
      DefaultNumber.PosInf -> 3,
      DefaultNumber.NegInf -> 4,
      DefaultNumber.NaN -> 5,
      DefaultNumber.UInt -> 6,
      DefaultNumber.NUInt -> 7,
      FlatNumber.Top -> 8,
      FlatNumber.Bot -> 9
    )
    val imap: Map[Int, AbsNumber] = map.map(_.swap)

    def write(num: AbsNumber): JsValue = num match {
      case DefaultNumber.UIntConst(v) => JsArray(JsNumber(10), JsNumber(v))
      case DefaultNumber.NUIntConst(v) => JsArray(JsNumber(11), JsNumber(v))
      case FlatNumber.Const(v) => JsArray(JsNumber(12), JsNumber(v))
      case _ => JsNumber(map(num))
    }
    def read(value: JsValue): AbsNumber = value match {
      case JsArray(Vector(JsNumber(x), JsNumber(v))) => x.toInt match {
        case 10 => DefaultNumber.UIntConst(v.toLong)
        case 11 => DefaultNumber.NUIntConst(v.toLong)
        case 12 => FlatNumber.Const(v.toDouble)
      }
      case JsNumber(n) => imap(n.toInt)
      case _ => throw AbsNumberParseError(value)
    }
  }

  // TODO
  implicit object AbsStringJsonFormat extends RootJsonFormat[AbsString] {
    /*
    val map: Map[AbsString, Int] = Map(
      StringSet.Top -> 0,
      StringSet.Number -> 1,
      StringSet.Other -> 2
    )
    val imap: Map[Int, AbsString] = map.map(_.swap)

    def write(str: AbsString): JsValue = str match {
      case StringSet.StrSet(values) => JsArray(values.to[Vector].map(JsString(_)))
      case _ => JsNumber(map(str))
    }
    def read(value: JsValue): AbsString = value match {
      case JsArray(values) => StringSet.StrSet(
        values.map(_ match {
        case JsString(s) => s
        case _ => throw AbsStringParseError(value)
      }).to[Set]
      )
      case JsNumber(n) => imap(n.toInt)
      case _ => throw AbsStringParseError(value)
    }
  */
    def write(str: AbsString): JsValue = JsNull
    def read(value: JsValue): AbsString = throw AbsStringParseError(value)
  }

  implicit object AllocSiteJsonFormat extends RootJsonFormat[AllocSite] {

    def write(aSite: AllocSite): JsValue = aSite match {
      case UserAllocSite(id) => JsNumber(id)
      case PredAllocSite(name) => JsString(name)
    }

    def read(value: JsValue): AllocSite = value match {
      case JsNumber(id) => UserAllocSite(id.toInt)
      case JsString(name) => PredAllocSite(name)
      case _ => throw AllocSiteParseError(value)
    }
  }

  implicit object RecencyTagJsonFormat extends RootJsonFormat[RecencyTag] {
    val map: Map[RecencyTag, Int] = Map(Recent -> 0, Old -> 1)
    val imap: Map[Int, RecencyTag] = map.map(_.swap)

    def write(tag: RecencyTag): JsValue = JsNumber(map(tag))
    def read(value: JsValue): RecencyTag = value match {
      case JsNumber(n) => imap(n.toInt)
      case _ => throw RecencyTagParseError(value)
    }
  }

  implicit object LocJsonFormat extends RootJsonFormat[Loc] {

    def write(loc: Loc): JsValue = loc match {
      case Recency(loc, tag) => JsArray(loc.toJson, tag.toJson)
      case Concrete(loc) => JsArray(loc.toJson)
      case _ => loc.asInstanceOf[AllocSite].toJson
    }

    def read(value: JsValue): Loc = value match {
      case JsArray(Vector(loc, tag)) => Recency(loc.convertTo[Loc], tag.convertTo[RecencyTag])
      case JsArray(Vector(loc)) => Concrete(loc.convertTo[Loc])
      case _ => value.convertTo[AllocSite]
    }
  }

  implicit object AbsLocJsonFormat extends RootJsonFormat[AbsLoc] {

    def write(loc: AbsLoc): JsValue = loc match {
      case DefaultLoc.Top => JsNull
      case DefaultLoc.LocSet(set) => JsArray(set.to[Vector].map(_.toJson))
    }

    def read(value: JsValue): AbsLoc = value match {
      case JsNull => DefaultLoc.Top
      case JsArray(set) => DefaultLoc.LocSet(set.map(_.convertTo[Loc]).to[Set])
      case _ => throw AbsLocParseError(value)
    }
  }

  implicit object AbsPValueFormat extends RootJsonFormat[AbsPValue] {

    def write(value: AbsPValue): JsValue = value match {
      case DefaultPValue.Dom(ud, nl, bl, num, str) =>
        JsArray(ud.toJson, nl.toJson, bl.toJson, num.toJson, str.toJson)
    }

    def read(value: JsValue): AbsPValue = value match {
      case JsArray(Vector(ud, nl, bl, num, str)) =>
        DefaultPValue.Dom(
          ud.convertTo[AbsUndef],
          nl.convertTo[AbsNull],
          bl.convertTo[AbsBool],
          num.convertTo[AbsNumber],
          str.convertTo[AbsString]
        )
      case _ => throw AbsPValueParseError(value)
    }
  }

  implicit object AbsValue extends RootJsonFormat[AbsValue] {

    def write(value: AbsValue): JsValue = value match {
      case DefaultValue.Dom(pvalue, locset) => JsArray(pvalue.toJson, locset.toJson)
    }

    def read(value: JsValue): AbsValue = value match {
      case JsArray(Vector(pvalue, locset)) =>
        DefaultValue.Dom(pvalue.convertTo[AbsPValue], locset.convertTo[AbsLoc])
      case _ => throw AbsValueParseError(value)
    }
  }
}
