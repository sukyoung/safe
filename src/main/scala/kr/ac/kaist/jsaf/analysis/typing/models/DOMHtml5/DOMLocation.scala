/*******************************************************************************
    Copyright (c) 2013-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMHtml5

import scala.collection.mutable.{Map=>MMap, HashMap=>MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.cfg.{CFG, CFGExpr}
import kr.ac.kaist.jsaf.analysis.typing.{ControlPoint, Helper, PreHelper, Semantics}
import kr.ac.kaist.jsaf.analysis.typing.domain.Heap
import kr.ac.kaist.jsaf.analysis.typing.domain.Context
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import scala.Some
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

// Modeled based on WHATWG HTML Living Standard 
// Section 6.5.3 The Location Interface
object DOMLocation extends DOM {
  private val name = "Location"

  /* predefined locatoins */
  val loc_ins = newSystemRecentLoc(name + "Ins")
  val loc_proto = newSystemRecentLoc(name + "Proto")

  /* instance */
  private val prop_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    // Location implements URLUtils
    // property
    ("href", AbsConstValue(PropValue(ObjectValue(AbsString.alpha(DOMHelper.getDocumentURI), T, T, T)))),
    ("origin", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
    ("protocol", AbsConstValue(PropValue(ObjectValue(AbsString.alpha(DOMHelper.getProtocol), T, T, T)))),
    //("protocol", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
    ("username", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
    ("password", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
    ("host", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
    ("hostname", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
    ("port", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
    ("pathname", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T)))),
    ("search", AbsConstValue(PropValue(ObjectValue(AbsString.alpha(""), T, T, T)))),
    ("hash", AbsConstValue(PropValue(ObjectValue(StrTop, T, T, T))))
    // TODO: "query"
  )

  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    // API
    ("assign",    AbsBuiltinFunc("Location.assign", 1)),
    ("replace",   AbsBuiltinFunc("Location.replace", 1)),
    ("reload",   AbsBuiltinFunc("Location.reload", 0)),
    // non-standard
    ("toString", AbsBuiltinFunc("Location.toString", 0))
  )

  /* initial property list */
  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_ins, prop_ins), (loc_proto, prop_proto)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("Location.toString" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(StrTop)), ctx), (he, ctxe))
        })),
      //case "Location.assign"
      ("Location.replace" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          /* argument */
          val url = Helper.toString(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, "0")))
          if(url </ StrBot)
            // unsound semantics : we do not navate the current browsing context to 'url'
            ((Helper.ReturnStore(h, Value(UndefTop)), ctx), (he, ctxe))
          else
            ((HeapBot, ContextBot), (he, ctxe))
        }))
      //case "Location.reload"
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //case "Location.assign"
      //case "Location.replace"
      //case "Location.reload"
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      //case "Location.assign"
      //case "Location.replace"
      //case "Location.reload"
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //case "Location.assign"
      //case "Location.replace"
      //case "Location.reload"
    )
  }


  /* instance */
  def getInstance(): Option[Loc] = Some (loc_ins)


}
