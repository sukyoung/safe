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
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.json.NodeProtocol._
import kr.ac.kaist.safe.errors.error.{
  CFGExprParseError,
  CFGIdParseError,
  EJSOpParseError,
  EJSValParseError,
  VarKindParseError
}

import spray.json._
import DefaultJsonProtocol._

object CFGExprProtocol extends DefaultJsonProtocol {

  implicit object EJSOpJsonFormat extends RootJsonFormat[EJSOp] {

    def write(op: EJSOp): JsValue = JsString(op.name)

    def read(value: JsValue): EJSOp = value match {
      case JsString(name) => EJSOp(name)
      case _ => throw EJSOpParseError(value)
    }
  }

  implicit object EJSValJsonFormat extends RootJsonFormat[EJSVal] {

    def write(value: EJSVal): JsValue = value match {
      case EJSNumber(text, num) => JsArray(JsString(text), JsNumber(num))
      case EJSString(str) => JsString(str)
      case EJSBool(bool) => JsBoolean(bool)
      case EJSUndef => JsObject()
      case EJSNull => JsNull
    }

    def read(value: JsValue): EJSVal = value match {
      case JsArray(Vector(JsString(text), JsNumber(num))) => EJSNumber(text, num.toDouble)
      case JsString(str) => EJSString(str)
      case JsBoolean(bool) => EJSBool(bool)
      case JsObject(f) => EJSUndef
      case JsNull => EJSNull
      case _ => throw EJSValParseError(value)
    }
  }

  implicit object VarKindJsonFormat extends RootJsonFormat[VarKind] {
    val map: Map[String, VarKind] = Map(
      "g" -> GlobalVar,
      "p" -> PureLocalVar,
      "c" -> CapturedVar,
      "C" -> CapturedCatchVar
    )
    val imap: Map[VarKind, String] = map.map(_.swap)

    def write(kind: VarKind): JsValue = JsString(imap(kind))

    def read(value: JsValue): VarKind = value match {
      case JsString(str) => map(str)
      case _ => throw VarKindParseError(value)
    }
  }

  implicit object CFGIdJsonFormat extends RootJsonFormat[CFGId] {

    def write(id: CFGId): JsValue = id match {
      case CFGUserId(text, kind, name, from) =>
        JsArray(JsString(text), kind.toJson, JsString(name), JsBoolean(from))
      case CFGTempId(text, kind) => JsArray(JsString(text), kind.toJson)
    }

    def read(value: JsValue): CFGId = value match {
      case JsArray(Vector(JsString(text), kind, JsString(name), JsBoolean(from))) =>
        CFGUserId(text, kind.convertTo[VarKind], name, from)
      case JsArray(Vector(JsString(text), kind)) => CFGTempId(text, kind.convertTo[VarKind])
      case _ => throw CFGIdParseError(value)
    }
  }

  implicit object CFGExprJsonFormat extends RootJsonFormat[CFGExpr] {

    def write(expr: CFGExpr): JsValue = expr match {
      case CFGVarRef(ir, id) => JsArray(ir.toJson, id.toJson)
      case CFGLoad(ir, obj, index) => JsArray(ir.toJson, obj.toJson, index.toJson)
      case CFGThis(ir) => JsArray(ir.toJson)
      case CFGBin(ir, first, op, second) =>
        JsArray(ir.toJson, first.toJson, op.toJson, second.toJson)
      case CFGUn(ir, op, expr) => JsArray(JsNull, ir.toJson, op.toJson, expr.toJson)
      case CFGInternalValue(ir, name) => JsArray(ir.toJson, JsString(name))
      case CFGVal(value) => JsArray(JsNull, value.toJson)
    }

    def read(value: JsValue): CFGExpr = value match {
      case JsArray(Vector(ir)) => CFGThis(ir.convertTo[IRNode])
      case JsArray(Vector(JsNull, value)) => CFGVal(value.convertTo[EJSVal])
      case JsArray(Vector(ir, JsString(name))) => CFGInternalValue(ir.convertTo[IRNode], name)
      case JsArray(Vector(ir, id)) => CFGVarRef(ir.convertTo[IRNode], id.convertTo[CFGId])
      case JsArray(Vector(ir, obj, index)) =>
        CFGLoad(ir.convertTo[IRNode], obj.convertTo[CFGExpr], index.convertTo[CFGExpr])
      case JsArray(Vector(JsNull, ir, op, expr)) =>
        CFGUn(ir.convertTo[IRNode], op.convertTo[EJSOp], expr.convertTo[CFGExpr])
      case JsArray(Vector(ir, first, op, second)) =>
        CFGBin(ir.convertTo[IRNode], first.convertTo[CFGExpr], op.convertTo[EJSOp],
          second.convertTo[CFGExpr])
      case _ => throw CFGExprParseError(value)
    }
  }
}
