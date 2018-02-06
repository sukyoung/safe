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

case object NotDumpFormatError extends JsonParseError({
  s"Dump file has a wrong format."
})

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

case class AbsOptParseError(value: JsValue) extends JsonFormatError("AbsOpt", value)

case class AbsMapParseError(value: JsValue) extends JsonFormatError("AbsMap", value)

case class StringParseError(value: JsValue) extends JsonFormatError("String", value)

case class IntParseError(value: JsValue) extends JsonFormatError("Int", value)

case class MapParseError(value: JsValue) extends JsonFormatError("Map", value)

case class SetParseError(value: JsValue) extends JsonFormatError("Set", value)

case class PairParseError(value: JsValue) extends JsonFormatError("Pair", value)

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

case class AbsUndefParseError(value: JsValue) extends JsonFormatError("AbsUndef", value)

case class AbsNullParseError(value: JsValue) extends JsonFormatError("AbsNull", value)

case class AbsBoolParseError(value: JsValue) extends JsonFormatError("AbsBool", value)

case class AbsNumParseError(value: JsValue) extends JsonFormatError("AbsNum", value)

case class AbsStrParseError(value: JsValue) extends JsonFormatError("AbsStr", value)

case class RecencyParseError(value: JsValue) extends JsonFormatError("Recency", value)

case class RecencyTagParseError(value: JsValue) extends JsonFormatError("RecencyTag", value)

case class AbsLocParseError(value: JsValue) extends JsonFormatError("AbsLoc", value)

case class AbsPValueParseError(value: JsValue) extends JsonFormatError("AbsPValue", value)

case class AbsValueParseError(value: JsValue) extends JsonFormatError("AbsValue", value)

case class DefSetParseError(value: JsValue) extends JsonFormatError("DefSet", value)

case class AbsDataPropParseError(value: JsValue) extends JsonFormatError("AbsDataProp", value)

case class APropMapParseError(value: JsValue) extends JsonFormatError("APropMap", value)

case class INameParseError(value: JsValue) extends JsonFormatError("IName", value)

case class AbsFIdParseError(value: JsValue) extends JsonFormatError("AbsFId", value)

case class AbsIValueParseError(value: JsValue) extends JsonFormatError("AbsIValue", value)

case class AbsObjParseError(value: JsValue) extends JsonFormatError("AbsObj", value)

case class AbsAbsentParseError(value: JsValue) extends JsonFormatError("AbsAbsent", value)

case class AbsBindingParseError(value: JsValue) extends JsonFormatError("AbsBinding", value)

case class EnvMapParseError(value: JsValue) extends JsonFormatError("EnvMap", value)

case class AbsDecEnvRecParseError(value: JsValue) extends JsonFormatError("AbsDecEnvRec", value)

case class AbsGlobalEnvRecParseError(value: JsValue) extends JsonFormatError("AbsGlobalEnvRec", value)

case class AbsEnvRecParseError(value: JsValue) extends JsonFormatError("AbsEnvRec", value)

case class AbsLexEnvParseError(value: JsValue) extends JsonFormatError("AbsLexEnv", value)

case class AbsDescParseError(value: JsValue) extends JsonFormatError("AbsDesc", value)

case class OldASiteSetParseError(value: JsValue) extends JsonFormatError("OldASiteSet", value)

case class AbsContextParseError(value: JsValue) extends JsonFormatError("AbsContext", value)

case class AbsHeapParseError(value: JsValue) extends JsonFormatError("AbsHeap", value)

case class AbsStateParseError(value: JsValue) extends JsonFormatError("AbsState", value)

case class EdgeDataParseError(value: JsValue) extends JsonFormatError("EdgeData", value)

case class SemanticsParseError(value: JsValue) extends JsonFormatError("Semantics", value)

case class NumDomainParseError(value: JsValue) extends JsonFormatError("NumDomain", value)

case class StrDomainParseError(value: JsValue) extends JsonFormatError("StrDomain", value)

case class AAddrTypeParseError(value: JsValue) extends JsonFormatError("AAddrType", value)

case class HeapBuildConfigParseError(value: JsValue) extends JsonFormatError("HeapBuildConfig", value)
