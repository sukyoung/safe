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
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.errors.error.{
  ASTNodeParseError,
  SpanParseError,
  SourceLocParseError
}

import spray.json._
import DefaultJsonProtocol._

object NodeProtocol extends DefaultJsonProtocol {

  implicit object SourceLocJsonFormat extends RootJsonFormat[SourceLoc] {

    def write(loc: SourceLoc): JsValue = loc match {
      case SourceLoc(line, col, off) => JsArray(JsNumber(line), JsNumber(col), JsNumber(off))
    }

    def read(value: JsValue): SourceLoc = value match {
      case JsArray(Vector(JsNumber(line), JsNumber(col), JsNumber(off))) =>
        SourceLoc(line.toInt, col.toInt, off.toInt)
      case _ => throw SourceLocParseError(value)
    }
  }

  implicit object SpanJsonFormat extends RootJsonFormat[Span] {

    def write(span: Span): JsValue = span match {
      case Span(name, begin, end) => JsArray(JsString(name), begin.toJson, end.toJson)
    }

    def read(value: JsValue): Span = value match {
      case JsArray(Vector(JsString(name), begin, end)) =>
        Span(name, begin.convertTo[SourceLoc], end.convertTo[SourceLoc])
      case _ => throw SpanParseError(value)
    }
  }

  def spanCommentToJs(span: Span, comment: Option[Comment]): JsValue = JsArray(
    span.toJson,
    comment match {
      case Some(Comment(info, txt)) => JsString(txt)
      case None => JsNull
    }
  )

  implicit object ASTNodeJsonFormat extends RootJsonFormat[ASTNode] {

    def write(ast: ASTNode): JsValue = spanCommentToJs(ast.span, ast.comment)

    def read(value: JsValue): ASTNode = value match {
      case JsArray(Vector(sp, JsString(txt))) => {
        val span: Span = sp.convertTo[Span]
        val comment: Option[Comment] = Some(Comment(ASTNodeInfo(span), txt))
        Comment(ASTNodeInfo(span, comment), "")
      }
      case JsArray(Vector(sp, JsNull)) => Comment(ASTNodeInfo(sp.convertTo[Span]), "")
      case _ => throw ASTNodeParseError(value)
    }
  }

  implicit object IRNodeJsonFormat extends RootJsonFormat[IRNode] {

    def write(ir: IRNode): JsValue = spanCommentToJs(ir.span, ir.comment)

    def read(value: JsValue): IRNode = IRRoot(value.convertTo[ASTNode], Nil, Nil, Nil)
  }
}
