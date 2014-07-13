/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.models.jquery

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper=>AH}

object JQueryProperty extends ModelData {

  // TODO: browser, fx object ??
  val FxLoc = newSystemLoc("jQueryFx", Recent)
  val StepLoc = newSystemLoc("jQueryFxStep", Recent)
  val SpeedsLoc = newSystemLoc("jQueryFxSpeeds", Recent)
  val TweenLoc = newSystemLoc("jQueryTween", Recent)
  val SupportLoc = newSystemLoc("jQuerySupport", Recent)

  private val prop_const: List[(String, AbsProperty)] = List(
    ("browser",  AbsConstValue(PropValue(ObjectValue(NullTop, T, T, T)))),
    ("fx",       AbsConstValue(PropValue(ObjectValue(Value(FxLoc), T, T, T)))),
    ("support",  AbsConstValue(PropValue(ObjectValue(Value(SupportLoc), T, T, T))))
  )

  private val prop_proto: List[(String, AbsProperty)] = List(
    ("jquery", AbsConstValue(PropValue(ObjectValue(AbsString.alpha("1.8.2"), F, F, F))))
  )
  
  private val prop_fx: List[(String, AbsProperty)] = List(
    ("@class",               AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto",               AbsConstValue(PropValue(ObjectValue(Value(FunctionProtoLoc), F, F, F)))),
    ("@extensible",          AbsConstValue(PropValue(T))),
    ("@scope",               AbsConstValue(PropValueNullTop)),
    ("@function",            AbsInternalFunc("jQuery.fx")),
    ("@construct",           AbsInternalFunc("jQuery.fx.constructor")),
    ("@hasinstance",         AbsConstValue(PropValueNullTop)),
    ("length",               AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(6), F, F, F)))),
    ("interval",               AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(13), T, T, T)))),
    ("name",               AbsConstValue(PropValue(ObjectValue(AbsString.alpha(""), T, T, T)))),
    ("step",               AbsConstValue(PropValue(ObjectValue(Value(StepLoc), T, T, T)))),
    ("speeds",               AbsConstValue(PropValue(ObjectValue(Value(SpeedsLoc), T, T, T)))),
    ("prototype",               AbsConstValue(PropValue(ObjectValue(Value(TweenLoc), T, T, T)))),
    ("start",      AbsBuiltinFunc("jQuery.fx.start", 0)),
    ("stop",      AbsBuiltinFunc("jQuery.fx.stop", 0)),
    ("tick",      AbsBuiltinFunc("jQuery.fx.tick", 0)),
    ("timer",      AbsBuiltinFunc("jQuery.fx.timer", 1))
  )
  
  private val prop_step: List[(String, AbsProperty)] = List(
    ("@class",      AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",      AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue)))
  )
  
  private val prop_speeds: List[(String, AbsProperty)] = List(
    ("@class",      AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",      AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    // default values
    ("_default",    AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(400), T, T, T)))),
    ("fast",        AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(200), T, T, T)))),
    ("slow",        AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(600), T, T, T))))
  )
  
  private val prop_tween: List[(String, AbsProperty)] = List(
    ("@class",      AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",      AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("constructor",      AbsBuiltinFunc("Twin", 5)),
    ("cur",         AbsBuiltinFunc("Twin.cur", 0)),
    ("init",         AbsBuiltinFunc("Twin.init", 6)),
    ("run",         AbsBuiltinFunc("Twin.run", 1))
  )
  
  private val prop_support: List[(String, AbsProperty)] = List(
    ("@class",      AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto",      AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("ajax",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("appendChecked",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("changeBubbles",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("checkClone",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("checkOn",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("clearCloneStyle",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("cors",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("cssFloat",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("deleteExpando",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("enctype",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("focusinBubbles",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("getSetAttribute",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("hrefNormalized",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("html5Clone",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("htmlSerialize",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("inlineBlockNeedsLayout",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("input",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("leadingWhitespace",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("noCloneChecked",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("noCloneEvent",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("opacity",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("optDisabled",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("optSelected",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("ownLast",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("radioValue",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("style",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("submitBubbles",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("tbody",        AbsConstValue(PropValue(ObjectValue(Value(BoolTop), T, T, T)))),
    ("boxSizing",         AbsBuiltinFunc("jQuery.support.boxSizing", 0)),
    ("boxSizingReliable",         AbsBuiltinFunc("jQuery.support.boxSizingReliable", 0)),
    ("pixelPosition",         AbsBuiltinFunc("jQuery.support.pixelPosition", 0)),
    ("reliableHiddenOffsets",         AbsBuiltinFunc("jQuery.support.reliableHiddenOffsets", 0)),
    ("shrinkWrapBlocks",         AbsBuiltinFunc("jQuery.support.shrinkWrapBlocks", 0))
  )


  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (JQuery.ConstLoc, prop_const), (JQuery.ProtoLoc, prop_proto), 
    (FxLoc, prop_fx), (StepLoc, prop_step), (SpeedsLoc, prop_speeds),
    (TweenLoc, prop_tween), (SupportLoc, prop_support)
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
}
