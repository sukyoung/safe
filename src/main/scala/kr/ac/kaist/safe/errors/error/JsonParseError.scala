/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.errors.error

import spray.json._

case object ModelBlockToJsonError extends SafeError(
  s"Try converting ModelBlock to Json."
)

sealed abstract class JsonParseError(msg: String) extends SafeError(msg)

case class NotJsonFileError(name: String) extends JsonParseError({
  s"$name is not a JSON file."
})

case class FunctionNotFoundError(name: String, id: Int) extends JsonParseError({
  s"Cannot find a CFGFunction with fid $id while parsing $name."
})

case class BlockNotFoundError(name: String, fid: Int, bid: Int) extends JsonParseError({
  s"Cannot find a CFGBlock with fid $fid and bid $bid while parsing $name."
})

sealed abstract class JsonFormatError(
  name: String,
  value: JsValue
) extends JsonParseError({
  s"${value.prettyPrint}: Format of json for $name is wrong."
})

case class AllocSiteParseError(value: JsValue) extends JsonFormatError("AllocSite", value)

case class CFGParseError(value: JsValue) extends JsonFormatError("CFG", value)

case class CFGEdgeParseError(value: JsValue) extends JsonFormatError("Edge of CFG", value)

case class CFGFunctionParseError(value: JsValue) extends JsonFormatError("CFGFunction", value)

case class CFGBlockParseError(value: JsValue) extends JsonFormatError("CFGBlock", value)

case class CFGInstParseError(value: JsValue) extends JsonFormatError("CFGInst", value)

case class CFGExprParseError(value: JsValue) extends JsonFormatError("CFGExpr", value)

case class CFGIdParseError(value: JsValue) extends JsonFormatError("CFGId", value)

case class EJSOpParseError(value: JsValue) extends JsonFormatError("EJSOp", value)

case class EJSValParseError(value: JsValue) extends JsonFormatError("EJSVal", value)

case class VarKindParseError(value: JsValue) extends JsonFormatError("VarKind", value)

case class ASTNodeParseError(value: JsValue) extends JsonFormatError("ASTNode", value)

case class SpanParseError(value: JsValue) extends JsonFormatError("Span", value)

case class SourceLocParseError(value: JsValue) extends JsonFormatError("SourceLoc", value)

case class LabelKindParseError(value: JsValue) extends JsonFormatError("LabelKind", value)

case class WorklistParseError(value: JsValue) extends JsonFormatError("Worklist", value)

case class WorkParseError(value: JsValue) extends JsonFormatError("Work", value)

case class ControlPointParseError(value: JsValue) extends JsonFormatError("ControlPoint", value)

case class TracePartitionParseError(value: JsValue) extends JsonFormatError("TracePartition", value)

case class LoopInfoParseError(value: JsValue) extends JsonFormatError("LoopInfo", value)
