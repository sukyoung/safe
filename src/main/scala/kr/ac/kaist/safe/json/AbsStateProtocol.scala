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

import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.json.AbsValueProtocol._
import kr.ac.kaist.safe.json.AbsObjectProtocol._
import kr.ac.kaist.safe.json.AbsLexEnvProtocol._
import kr.ac.kaist.safe.errors.error.{
  OldASiteSetParseError,
  AbsContextParseError,
  AbsHeapParseError,
  AbsStateParseError
}

import spray.json._
import DefaultJsonProtocol._

object AbsStateProtocol extends DefaultJsonProtocol {

  implicit object OldASiteSetJsonFormat extends RootJsonFormat[OldASiteSet] {

    def write(set: OldASiteSet): JsValue = set match {
      case OldASiteSet(may, must) => JsArray(
        JsArray(may.to[Vector].map(_.toJson)),
        JsArray(must.to[Vector].map(_.toJson))
      )
    }

    def read(value: JsValue): OldASiteSet = value match {
      case JsArray(Vector(JsArray(may), JsArray(must))) =>
        OldASiteSet(
          may.map(_.convertTo[Loc]).to[Set],
          must.map(_.convertTo[Loc]).to[Set]
        )
      case _ => throw OldASiteSetParseError(value)
    }
  }

  implicit object AbsContextJsonFormat extends RootJsonFormat[AbsContext] {

    def write(ctx: AbsContext): JsValue = ctx match {
      case DefaultContext.Bot => JsTrue
      case DefaultContext.Top => JsFalse
      case DefaultContext.CtxMap(map, set, old, value) => JsArray(
        JsArray(map.to[Vector].map { case (loc, env) => JsArray(loc.toJson, env.toJson) }),
        JsArray(set.to[Vector].map(_.asInstanceOf[Loc].toJson)),
        old.toJson,
        value.toJson
      )
    }

    def read(value: JsValue): AbsContext = value match {
      case JsTrue => DefaultContext.Bot
      case JsFalse => DefaultContext.Top
      case JsArray(Vector(JsArray(map), JsArray(set), old, value)) => DefaultContext.CtxMap(
        map.map(_ match {
        case JsArray(Vector(loc, env)) => loc.convertTo[Loc] -> env.convertTo[AbsLexEnv]
        case _ => throw AbsContextParseError(value)
      }).toMap,
        set.map(_.convertTo[Loc].asInstanceOf[Concrete]).to[Set],
        old.convertTo[OldASiteSet],
        value.convertTo[AbsValue]
      )
      case _ => throw AbsContextParseError(value)
    }
  }

  implicit object AbsHeapJsonFormat extends RootJsonFormat[AbsHeap] {

    def write(heap: AbsHeap): JsValue = heap match {
      case DefaultHeap.Top => JsNull
      case DefaultHeap.HeapMap(map, set) => JsArray(
        JsArray(map.to[Vector].map { case (loc, obj) => JsArray(loc.toJson, obj.toJson) }),
        JsArray(set.to[Vector].map(_.asInstanceOf[Loc].toJson))
      )
    }

    def read(value: JsValue): AbsHeap = value match {
      case JsNull => DefaultHeap.Top
      case JsArray(Vector(JsArray(map), JsArray(set))) => DefaultHeap.HeapMap(
        map.map(_ match {
        case JsArray(Vector(loc, obj)) => loc.convertTo[Loc] -> obj.convertTo[AbsObject]
        case _ => throw AbsHeapParseError(value)
      }).toMap,
        set.map(_.convertTo[Loc].asInstanceOf[Concrete]).to[Set]
      )
      case _ => throw AbsHeapParseError(value)
    }
  }

  implicit object AbsStateJsonFormat extends RootJsonFormat[AbsState] {

    def write(state: AbsState): JsValue = state match {
      case DefaultState.Dom(heap, ctx) => JsArray(heap.toJson, ctx.toJson)
    }

    def read(value: JsValue): AbsState = value match {
      case JsArray(Vector(heap, ctx)) => DefaultState.Dom(
        heap.convertTo[AbsHeap],
        ctx.convertTo[AbsContext]
      )
      case _ => throw AbsStateParseError(value)
    }
  }
}
