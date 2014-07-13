/*******************************************************************************
    Copyright (c) 2013-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml5

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing.{Semantics, ControlPoint, Helper, PreHelper}
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import scala.Some
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

// Modeled based on HTML Canvas 2D Contex(W3C Candidate Recommendation 17 December 2012)
// www.w3.org/TR/2dcontext2/  
object CanvasRenderingContext2D extends DOM {
  private val name = "CanvasRenderingContext2D"

  /* predefined locatoins */
  val loc_cons = newSystemRecentLoc(name + "Ins")
  val loc_proto = newSystemRecentLoc(name + "Proto")



  /* constructor */
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("length", AbsConstValue(PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, F, F)))),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    // property
    // back-reference to the canvas 'canvas' is initilaized when 'getContext' of HTMLCanavsElement is called
    // compositing
    ("globalAlpha", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), T, T, T)))),
    ("globalCompositeOperation", AbsConstValue(PropValue(ObjectValue(AbsString.alpha("source-over"), T, T, T)))),
    // TODO: transformations 'currentTransform
    // image smoothing
    ("imageSmoothingEnabled", AbsConstValue(PropValue(ObjectValue(Value(BoolTrue), T, T, T)))),
    // TODO: colors and styles 'strokeStyle' and 'fillStyle'
    // shadows
    ("shadowOffsetX", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0), T, T, T)))),
    ("shadowOffsetY", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0), T, T, T)))),
    ("shadowBlur", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0), T, T, T)))),
    ("shadowColor", AbsConstValue(PropValue(ObjectValue(AbsString.alpha("transparent black"), T, T, T)))),
    // CanvasDrawingStyles : CanvasRenderingContext2D implements CanvasDrawingStyles
    // line caps/joins
    ("lineWidth", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1), T, T, T)))),
    ("lineCap", AbsConstValue(PropValue(ObjectValue(AbsString.alpha("butt"), T, T, T)))),
    ("lineJoin", AbsConstValue(PropValue(ObjectValue(AbsString.alpha("miter"), T, T, T)))),
    ("miterLimit", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(10), T, T, T)))),
    // dashed lines
    ("lineDashOffset", AbsConstValue(PropValue(ObjectValue(Value(NumTop), T, T, T)))),
    // text
    ("font", AbsConstValue(PropValue(ObjectValue(AbsString.alpha("10px sans-serif"), T, T, T)))),
    ("textAlign", AbsConstValue(PropValue(ObjectValue(AbsString.alpha("start"), T, T, T)))),
    ("textBaseline", AbsConstValue(PropValue(ObjectValue(AbsString.alpha("alphabetic"), T, T, T))))
  )

  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    // API
    // state
    ("save",   AbsBuiltinFunc("CanvasRenderingContext2D.save", 0)),
    ("restore",   AbsBuiltinFunc("CanvasRenderingContext2D.restore", 0)),
    // transformations
    ("scale",   AbsBuiltinFunc("CanvasRenderingContext2D.scale", 2)),
    ("rotate",   AbsBuiltinFunc("CanvasRenderingContext2D.rotate", 1)),
    ("translate",   AbsBuiltinFunc("CanvasRenderingContext2D.translate", 2)),
    ("transform",   AbsBuiltinFunc("CanvasRenderingContext2D.transform", 6)),
    ("setTransform",   AbsBuiltinFunc("CanvasRenderingContext2D.setTransform", 6)),
    ("resetTransform",   AbsBuiltinFunc("CanvasRenderingContext2D.resetTransform", 0)),
    // colors and styles
    ("createLinearGradient",   AbsBuiltinFunc("CanvasRenderingContext2D.createLinearGradient", 4)),
    ("createRadialGradient",   AbsBuiltinFunc("CanvasRenderingContext2D.createRadialGradient", 6)),
    ("createPattern",   AbsBuiltinFunc("CanvasRenderingContext2D.createPattern", 2)),
    // rects
    ("clearRect",   AbsBuiltinFunc("CanvasRenderingContext2D.clearRect", 4)),
    ("fillRect",   AbsBuiltinFunc("CanvasRenderingContext2D.fillRect", 4)),
    ("strokeRect",   AbsBuiltinFunc("CanvasRenderingContext2D.strokeRect", 4)),
    // path API
    ("beginPath",   AbsBuiltinFunc("CanvasRenderingContext2D.beginPath", 0)),
    ("fill",   AbsBuiltinFunc("CanvasRenderingContext2D.fill", 1)),
    ("stroke",   AbsBuiltinFunc("CanvasRenderingContext2D.stroke", 1)),
    ("drawSystemFocusRing",   AbsBuiltinFunc("CanvasRenderingContext2D.drawSystemFocusRing", 2)),
    ("drawCustomFocusRing",   AbsBuiltinFunc("CanvasRenderingContext2D.drawCustomFocusRing", 2)),
    ("scrollPathIntoView",   AbsBuiltinFunc("CanvasRenderingContext2D.scrollPathIntoView", 1)),
    ("clip",   AbsBuiltinFunc("CanvasRenderingContext2D.clip", 1)),
    ("resetClip",   AbsBuiltinFunc("CanvasRenderingContext2D.resetClip", 0)),
    ("isPointInPath",   AbsBuiltinFunc("CanvasRenderingContext2D.isPointInPath", 1)),
    // text
    ("fillText",   AbsBuiltinFunc("CanvasRenderingContext2D.fillTest", 4)),
    ("strokeText",   AbsBuiltinFunc("CanvasRenderingContext2D.strokeText", 4)),
    ("measureText",   AbsBuiltinFunc("CanvasRenderingContext2D.measureTest", 1)),
    // drawing images
    ("drawImage",   AbsBuiltinFunc("CanvasRenderingContext2D.drawImage", 9)),
    // hit regions
    ("addHitRegion",   AbsBuiltinFunc("CanvasRenderingContext2D.addHitRegion", 1)),
    ("removeHitRegion",   AbsBuiltinFunc("CanvasRenderingContext2D.removeHitRegion", 1)),
    // pixel manipulation
    ("createImageData",   AbsBuiltinFunc("CanvasRenderingContext2D.createImageData", 2)),
    ("createImageDataHD",   AbsBuiltinFunc("CanvasRenderingContext2D.createImageDataHD", 2)),
    ("getImageData",   AbsBuiltinFunc("CanvasRenderingContext2D.getImageData", 4)),
    ("getImageDataHD",   AbsBuiltinFunc("CanvasRenderingContext2D.getImageDataHD", 4)),
    ("putImageData",   AbsBuiltinFunc("CanvasRenderingContext2D.putImageData", 7)),
    ("putImageDataHD",   AbsBuiltinFunc("CanvasRenderingContext2D.putImageDataHD", 7)),
    // CanvasDrawingStyles : CanvasRenderingContext2D implements CanvasDrawingStyles
    // dashed lines
    ("setLineDash",   AbsBuiltinFunc("CanvasRenderingContext2D.setLineDash", 1)),
    ("getLineDash",   AbsBuiltinFunc("CanvasRenderingContext2D.getLineDash", 0)),
    // CanvasPathMethods : CanvasRenderingContext2D implements CanvasPathMethods
    ("closePath",   AbsBuiltinFunc("CanvasRenderingContext2D.closePath", 2)),
    ("moveTo",   AbsBuiltinFunc("CanvasRenderingContext2D.moveTo", 2)),
    ("lineTo",   AbsBuiltinFunc("CanvasRenderingContext2D.lineTo", 2)),
    ("quadraticCurveTo",   AbsBuiltinFunc("CanvasRenderingContext2D.quadraticCurveTo", 4)),
    ("bezierCurveTo",   AbsBuiltinFunc("CanvasRenderingContext2D.bezierCurveTo", 6)),
    ("arcTo",   AbsBuiltinFunc("CanvasRenderingContext2D.arcTo", 7)),
    ("rect",   AbsBuiltinFunc("CanvasRenderingContext2D.rect", 4)),
    ("ellipse",   AbsBuiltinFunc("CanvasRenderingContext2D.ellipse", 8))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //case "CanvasRenderingContext2D.save" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.restore" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.scale" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.rotate" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.translate" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.transform" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.setTransform" => ((h, ctx), (he, ctxe))
      ("CanvasRenderingContext2D.createLinearGradient" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val grad_loc = CanvasGradient.getInstance.get
          ((Helper.ReturnStore(h, Value(grad_loc)), ctx), (he, ctxe))
        })),
      ("CanvasRenderingContext2D.createRadialGradient" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* imprecise semantics : no exception handling */
          val grad_loc = CanvasGradient.getInstance.get
          ((Helper.ReturnStore(h, Value(grad_loc)), ctx), (he, ctxe))
        }))
      //case "CanvasRenderingContext2D.createPattern" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.clearRect" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.fillRect" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.strokeRect" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.beginPath" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.fill" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.stroke" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.drawSystemFocusRing" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.drawCustomFocusRing" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.scrollPathIntoView" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.clip" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.resetClip" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.isPointInPath" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.fillText" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.strokeText" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.measureText" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.drawImage" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.addHitRegion" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.removeHitRegion" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.createImageData" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.createImageDataHD" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.getImageData" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.getImageDataHD" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.putImageData" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.putImageDataHD" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.setLineDash" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.getLineDash" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.closePath" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.moveTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.lineTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.quadraticCurveTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.bezierCurveTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.arcTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.rect" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.ellipse" => ((h, ctx), (he, ctxe))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //TODO: not yet implemented
      //case "CanvasRenderingContext2D.save" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.restore" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.scale" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.rotate" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.translate" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.transform" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.setTransform" => ((h, ctx), (he, ctxe))
      ("CanvasRenderingContext2D.createLinearGradient" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val grad_loc = CanvasGradient.getInstance.get
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(grad_loc)), ctx), (he, ctxe))
        })),
      ("CanvasRenderingContext2D.createRadialGradient" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          /* imprecise semantics : no exception handling */
          val grad_loc = CanvasGradient.getInstance.get
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(grad_loc)), ctx), (he, ctxe))
        }))
      //case "CanvasRenderingContext2D.createPattern" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.clearRect" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.fillRect" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.strokeRect" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.beginPath" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.fill" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.stroke" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.drawSystemFocusRing" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.drawCustomFocusRing" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.scrollPathIntoView" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.clip" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.resetClip" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.isPointInPath" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.fillText" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.strokeText" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.measureText" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.drawImage" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.addHitRegion" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.removeHitRegion" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.createImageData" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.createImageDataHD" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.getImageData" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.getImageDataHD" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.putImageData" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.putImageDataHD" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.setLineDash" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.getLineDash" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.closePath" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.moveTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.lineTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.quadraticCurveTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.bezierCurveTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.arcTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.rect" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.ellipse" => ((h, ctx), (he, ctxe))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      //case "CanvasRenderingContext2D.save" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.restore" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.scale" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.rotate" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.translate" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.transform" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.setTransform" => ((h, ctx), (he, ctxe))
      ("CanvasRenderingContext2D.createLinearGradient" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("CanvasRenderingContext2D.createRadialGradient" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
      //case "CanvasRenderingContext2D.createPattern" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.clearRect" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.fillRect" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.strokeRect" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.beginPath" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.fill" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.stroke" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.drawSystemFocusRing" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.drawCustomFocusRing" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.scrollPathIntoView" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.clip" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.resetClip" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.isPointInPath" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.fillText" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.strokeText" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.measureText" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.drawImage" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.addHitRegion" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.removeHitRegion" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.createImageData" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.createImageDataHD" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.getImageData" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.getImageDataHD" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.putImageData" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.putImageDataHD" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.setLineDash" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.getLineDash" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.closePath" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.moveTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.lineTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.quadraticCurveTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.bezierCurveTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.arcTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.rect" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.ellipse" => ((h, ctx), (he, ctxe))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //case "CanvasRenderingContext2D.save" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.restore" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.scale" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.rotate" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.translate" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.transform" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.setTransform" => ((h, ctx), (he, ctxe))
      ("CanvasRenderingContext2D.createLinearGradient" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("CanvasRenderingContext2D.createRadialGradient" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
      //case "CanvasRenderingContext2D.createPattern" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.clearRect" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.fillRect" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.strokeRect" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.beginPath" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.fill" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.stroke" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.drawSystemFocusRing" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.drawCustomFocusRing" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.scrollPathIntoView" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.clip" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.resetClip" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.isPointInPath" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.fillText" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.strokeText" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.measureText" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.drawImage" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.addHitRegion" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.removeHitRegion" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.createImageData" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.createImageDataHD" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.getImageData" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.getImageDataHD" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.putImageData" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.putImageDataHD" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.setLineDash" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.getLineDash" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.closePath" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.moveTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.lineTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.quadraticCurveTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.bezierCurveTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.arcTo" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.rect" => ((h, ctx), (he, ctxe))
      //case "CanvasRenderingContext2D.ellipse" => ((h, ctx), (he, ctxe))
    )
  }

  /* instance */
  def getInstance(): Option[Loc] = Some (loc_cons)

}
