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

package kr.ac.kaist.safe.errors.error

import spray.json._

sealed abstract class JsonParseError(name: String, value: JsValue) extends SafeError({
  s"${value.prettyPrint}: Format of json for $name is wrong."
})

case class AllocSiteParseError(value: JsValue) extends JsonParseError("AllocSite", value)

case class CFGParseError(value: JsValue) extends JsonParseError("CFG", value)

case class CFGFunctionParseError(value: JsValue) extends JsonParseError("CFGFunction", value)

case class CFGBlockParseError(value: JsValue) extends JsonParseError("CFGBlock", value)

case object ModelBlockToJsonError extends SafeError(
  s"Try converting ModelBlock to Json."
)

case class CFGInstParseError(value: JsValue) extends JsonParseError("CFGInst", value)

case class CFGExprParseError(value: JsValue) extends JsonParseError("CFGExpr", value)

case class CFGIdParseError(value: JsValue) extends JsonParseError("CFGId", value)

case class EJSOpParseError(value: JsValue) extends JsonParseError("EJSOp", value)

case class EJSValParseError(value: JsValue) extends JsonParseError("EJSVal", value)

case class VarKindParseError(value: JsValue) extends JsonParseError("VarKind", value)

case class ASTNodeParseError(value: JsValue) extends JsonParseError("ASTNode", value)

case class SpanParseError(value: JsValue) extends JsonParseError("Span", value)

case class SourceLocParseError(value: JsValue) extends JsonParseError("SourceLoc", value)

case class LabelKindParseError(value: JsValue) extends JsonParseError("LabelKind", value)
