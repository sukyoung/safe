/*******************************************************************************
    Copyright (c) 2013, S-Core.
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

object JQueryCallback extends ModelData {
  //val CallbackLoc = newPreDefLoc("jQueryCallback", Recent)

  private val prop_const: List[(String, AbsProperty)] = List(
    ("Callbacks", AbsBuiltinFunc("jQuery.Callbacks", 1))
  )

  // TODO: callback object
  /*
  private val prop_callback: List[(String, AbsProperty)] = List(
    ("add",   AbsBuiltinFunc("jQuery.prototype.ajaxComplete", 1)),
    ("disable",      AbsBuiltinFunc("jQuery.prototype.ajaxError", 1)),
    ("disabled",       AbsBuiltinFunc("jQuery.prototype.ajaxSend", 1)),
    ("empty",      AbsBuiltinFunc("jQuery.prototype.ajaxStart", 1)),
    ("fire",       AbsBuiltinFunc("jQuery.prototype.ajaxStop", 1)),
    ("fireWith",    AbsBuiltinFunc("jQuery.prototype.ajaxSuccess", 1)),
    ("has",           AbsBuiltinFunc("jQuery.prototype.load", 3)),
    ("lock",      AbsBuiltinFunc("jQuery.prototype.serialize", 0)),
    ("locked", AbsBuiltinFunc("jQuery.prototype.serializeArray", 0)),
    ("remove", AbsBuiltinFunc("jQuery.prototype.serializeArray", 0))
  )
  */

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (JQuery.ConstLoc, prop_const)
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
