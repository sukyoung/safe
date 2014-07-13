/*******************************************************************************
    Copyright (c) 2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml5

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.Semantics
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr}
import kr.ac.kaist.jsaf.analysis.typing.{ControlPoint, Helper, PreHelper}
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import scala.Some
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

// Modeled based on MicroSoft MSDN 
// http://msdn.microsoft.com/en-us/library/windows/apps/hh696634.aspx
// JavaScript Console Commands : non-standard
object Console extends DOM {
  private val name = "Console"

  /* predefined locatoins */
  val loc_ins = newSystemRecentLoc(name + "Ins")
  val loc_proto = newSystemRecentLoc(name + "Proto")

  /* constructor */
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("length", AbsConstValue(PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, F, F)))),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )

  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    // API
    ("assert",   AbsBuiltinFunc("Console.assert", 2)),
    ("clear",   AbsBuiltinFunc("Console.clear", 0)),
    ("dir",   AbsBuiltinFunc("Console.dir", 1)),
    ("error",   AbsBuiltinFunc("Console.error", 1)),
    ("info",   AbsBuiltinFunc("Console.info", 1)),
    ("log",   AbsBuiltinFunc("Console.log", 1)),
    ("warn",   AbsBuiltinFunc("Console.warn", 1))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    ("console", AbsConstValue(PropValue(ObjectValue(loc_ins, T, F, T))))
  )
  /* no constructor */
  /* initial property list */
  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_ins, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("Console.assert" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {           
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))        
        }
      )),
      ("Console.clear" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {           
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))        
        }
      )),
      ("Console.dir" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {           
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))        
        }
      )),
      ("Console.error" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {           
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))        
        }
      )),
      ("Console.info" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {           
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))        
        }
      )),
      ("Console.log" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {           
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))        
        }
      )),
      ("Console.warn" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {           
          ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))        
        }
      ))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
    )
  }

  /* instance */
  def getInstance(): Option[Loc] = Some (loc_ins)
}
