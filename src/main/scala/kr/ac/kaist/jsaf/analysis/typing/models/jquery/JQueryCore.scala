/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.models.jquery

import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG, InternalError}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper=>AH}
import kr.ac.kaist.jsaf.analysis.typing._

object JQueryCore extends ModelData {
  private val prop_const: List[(String, AbsProperty)] = List(
    ("holdReady",  AbsBuiltinFunc("jQuery.holdReady", 1)),
    ("noConflict", AbsBuiltinFunc("jQuery.noConflict", 1)),
    ("sub",        AbsBuiltinFunc("jQuery.sub", 0)),
    ("when",       AbsBuiltinFunc("jQuery.when", 1))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (JQuery.ConstLoc, prop_const)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      "jQuery.noConflict" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => { 
          /* arguments :  deep */
          val v_deep = Helper.toBoolean(getArgValue(h, ctx, args, "0"))

          val v_$ = h(GlobalLoc)("$")._1._1._1
          val v_jQuery = h(GlobalLoc)("jQuery")._1._1._1
          
          val oldv_$ = h(JQuery.EnvLoc)("_$")._1
          val oldv_jQuery = h(JQuery.EnvLoc)("_jQuery")._1
          val v_j = Value(JQuery.ConstLoc)
          // if( window.$ === jQuery)
          val h_1 = if(v_$ <= v_jQuery && v_jQuery <= v_$) { 
                      val new_obj = h(GlobalLoc).update("$", oldv_$)
                      h.update(GlobalLoc, new_obj)
                    }
                    else if(v_$ <= v_jQuery || v_jQuery <= v_$){
                      val new_obj = h(GlobalLoc).update("$", oldv_$ + PropValue(ObjectValue(v_$, T, T, T)))
                      h.update(GlobalLoc, new_obj)
                    }
                    else h
          val h_2 = if(BoolTrue <= v_deep) {
                      if(BoolTop <= v_deep) {
                        // window.jQuery === jQuery
                        if(v_jQuery <= v_j || v_j <= v_jQuery) {
                          val new_obj = h_1(GlobalLoc).update("jQuery", oldv_jQuery + PropValue(ObjectValue(v_jQuery, T, T, T)))
                          h_1.update(GlobalLoc, new_obj)
                        }
                        else
                          h_1
                      }
                      // BoolTrue
                      else {
                        // window.jQuery === jQuery
                        if(v_j <= v_jQuery && v_jQuery <= v_j) {
                          val new_obj = h_1(GlobalLoc).update("jQuery", oldv_jQuery)
                          h_1.update(GlobalLoc, new_obj)
                        }
                        else if(v_jQuery <= v_j || v_j <= v_jQuery) {
                          val new_obj = h_1(GlobalLoc).update("jQuery", oldv_jQuery + PropValue(ObjectValue(v_jQuery, T, T, T)))
                          h_1.update(GlobalLoc, new_obj)
                        }
                        else
                          h_1
                      }
                    }
                    else h_1

          ((Helper.ReturnStore(h_2, v_j), ctx), (he, ctxe))
        })

    )
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
