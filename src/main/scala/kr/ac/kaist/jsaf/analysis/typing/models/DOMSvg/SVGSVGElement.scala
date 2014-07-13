/*******************************************************************************
    Copyright (c) 2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMSvg

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr}
import kr.ac.kaist.jsaf.analysis.typing.{Semantics, ControlPoint, Helper, PreHelper}
import scala.Some
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

// Modeled based on W3C Scalable Vector Graphics (SVG) 1.1 (Second Edition)
// Section 5.11.2 Interface SVGSVGElement
object SVGSVGElement extends DOM {
  private val name = "SVGSVGElement"

  /* predefined locations */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")

  /* constructor */
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("length", AbsConstValue(PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, F, F)))),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(SVGElement.loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("suspendRedraw", AbsBuiltinFunc("SVGSVGElement.suspendRedraw", 1)),
    ("unsuspendRedraw", AbsBuiltinFunc("SVGSVGElement.unsuspendRedraw", 1)),
    ("unsuspendRedrawAll", AbsBuiltinFunc("SVGSVGElement.unsuspendRedrawAll", 0)),
    ("forceRedraw", AbsBuiltinFunc("SVGSVGElement.forceRedraw", 0)),
    ("pauseAnimations", AbsBuiltinFunc("SVGSVGElement.pauseAnimations", 0)),
    ("unpauseAnimations", AbsBuiltinFunc("SVGSVGElement.unpauseAnimations", 0)),
    ("animationsPaused", AbsBuiltinFunc("SVGSVGElement.animationsPaused", 0)),
    ("getCurrentTime", AbsBuiltinFunc("SVGSVGElement.getCurrentTime", 0)),
    ("setCurrentTime", AbsBuiltinFunc("SVGSVGElement.setCurrentTime", 0)),
    ("getIntersectionList", AbsBuiltinFunc("SVGSVGElement.getIntersectionList", 2)),
    ("getEnclosureList", AbsBuiltinFunc("SVGSVGElement.getEnclosureList", 2)),
    ("checkIntersection", AbsBuiltinFunc("SVGSVGElement.checkIntersection", 2)),
    ("checkEnclosure", AbsBuiltinFunc("SVGSVGElement.checkEnclosure", 2)),
    ("deselectAll", AbsBuiltinFunc("SVGSVGElement.deselectAll", 0)),
    ("createSVGNumber", AbsBuiltinFunc("SVGSVGElement.createSVGNumber", 0)),
    ("createSVGLength", AbsBuiltinFunc("SVGSVGElement.createSVGLength", 0)),
    ("createSVGAngle", AbsBuiltinFunc("SVGSVGElement.createSVGAngle", 0)),
    ("createSVGPoint", AbsBuiltinFunc("SVGSVGElement.createSVGPoint", 0)),
    ("createSVGMatix", AbsBuiltinFunc("SVGSVGElement.createSVGMatrix", 0)),
    ("createSVGRect", AbsBuiltinFunc("SVGSVGElement.createSVGRect", 0)),
    ("createSVGTransform", AbsBuiltinFunc("SVGSVGElement.createSVGTransform", 0)),
    ("createSVGTransformFromMatrix", AbsBuiltinFunc("SVGSVGElement.createSVGTransformFromMatrix", 1)),
    ("getElementById", AbsBuiltinFunc("SVGSVGElement.getElementById", 1))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_proto, prop_proto)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map()
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map()
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map()
  }

  /* instance */
  override def getInstance(cfg: CFG): Option[Loc] = Some(newRecentLoc())
  /* list of properties in the instance object */

  def getInsList(contentScriptType: PropValue, contentStyleType: PropValue, pixelUnitToMillimeterX: PropValue, pixelUnitToMillimeterY: PropValue,
                 screenPixelToMillimeterX: PropValue, screenPixelToMillimeterY: PropValue, useCurrentView: PropValue, currentScale: PropValue): List[(String, PropValue)] = List(
    ("@class",    PropValue(AbsString.alpha("Object"))),
    ("@proto",    PropValue(ObjectValue(loc_proto, F, F, F))),
    ("@extensible", PropValue(BoolTrue)),
    ("contentScriptType", contentScriptType),
    ("contentStyleType",  contentStyleType),
    ("pixelUnitToMillimeterX",   pixelUnitToMillimeterX),
    ("pixelUnitToMillimeterY", pixelUnitToMillimeterY),
    ("screenPixelToMillimeterX",   screenPixelToMillimeterX),
    ("screenPixelToMillimeterY",   screenPixelToMillimeterY),
    ("useCurrentView",  useCurrentView),
    ("currentScale",  currentScale)
    // TODO: 'x', 'y', 'width', 'height', 'viewport', 'currentView', 'currentTranslate'
  )

  override def default_getInsList(): List[(String, PropValue)] = {
    val contentScriptType = PropValue(ObjectValue(OtherStr, T, T, T))
    val contentStyleType = PropValue(ObjectValue(OtherStr, T, T, T))
    val pixelUnitToMillimeterX = PropValue(ObjectValue(NumTop, F, T, T))
    val pixelUnitToMillimeterY = PropValue(ObjectValue(NumTop, F, T, T))
    val screenPixelToMillimeterX = PropValue(ObjectValue(NumTop, F, T, T))
    val screenPixelToMillimeterY = PropValue(ObjectValue(NumTop, F, T, T))
    val useCurrentView = PropValue(ObjectValue(BoolFalse, F, T, T))
    val currentScale = PropValue(ObjectValue(AbsNumber.alpha(1), T, T, T))
    // This object has all properties of the SVGElement object 
    SVGElement.default_getInsList :::
      getInsList(contentScriptType, contentStyleType, pixelUnitToMillimeterX, pixelUnitToMillimeterY, screenPixelToMillimeterX, 
                 screenPixelToMillimeterY, useCurrentView, currentScale)
  }

}
