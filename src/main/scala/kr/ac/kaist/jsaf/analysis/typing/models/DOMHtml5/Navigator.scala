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
// Section 7.5.1 The Navigator object.
object Navigator extends DOM {
  private val name = "Navigator"

  /* predefined locatoins */
  val loc_ins = newSystemRecentLoc(name + "Ins")
  val loc_proto = newSystemRecentLoc(name + "Proto")

  /* instant object */
  private val prop_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("length", AbsConstValue(PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, F, F)))),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    // property
    // Navigator implements NavigatorID (Section 7.5.1.1)
    ("appCodeName", AbsConstValue(PropValue(ObjectValue(AbsString.alpha("Mozilla"), F, T, T)))),
    ("appName", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
    ("appVersion", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
    ("platform", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
    ("product", AbsConstValue(PropValue(ObjectValue(AbsString.alpha("Gecko"), F, T, T)))),
    ("userAgent", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
    //("userAgent", AbsConstValue(PropValue(ObjectValue(AbsString.alpha("Gecko"), F, T, T)))),
    // Navigator implements NavigatorLanguage (Section 7.5.1.2)
    ("language", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T)))),
    // Navigator implements NavigatorOnLine (Section 6.7.10)
    ("onLine", AbsConstValue(PropValue(ObjectValue(BoolTop, F, T, T)))),
    // Navigator implements NavigatorStorageUtils (Section 7.5.1.4)
    ("cookieEnabled", AbsConstValue(PropValue(ObjectValue(BoolTop, F, T, T)))),
    // Navigator implements NavigatorPlugins (Section 7.5.1.5)
    ("plugins", AbsConstValue(PropValue(ObjectValue(PluginArray.loc_ins, F, T, T)))),
    ("mimeTypes", AbsConstValue(PropValue(ObjectValue(MimeTypeArray.loc_ins, F, T, T)))),
    // Non-standard
    ("vendor", AbsConstValue(PropValue(ObjectValue(StrTop, F, T, T))))
  )

  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(ObjProtoLoc, F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    // API
    // Navigator implements NavigatorContentUtils (Section 7.5.1.3)
    ("registerProtocolHandler",   AbsBuiltinFunc("Navigator.registerProtocolHandler", 3)),
    ("registerContentHandler",   AbsBuiltinFunc("Navigator.registerContentHandler", 3)),
    ("isProtocolHandlerRegistered",   AbsBuiltinFunc("Navigator.isProtocolHandlerRegistered", 2)),
    ("isContentHandlerRegistered",   AbsBuiltinFunc("Navigator.isContentHandlerRegistered", 2)),
    ("unregisterProtocolHandler",   AbsBuiltinFunc("Navigator.unregisterProtocolHandler", 2)),
    ("unregisterContentHandler",   AbsBuiltinFunc("Navigator.unregisterContentHandler", 2)),
    // Navigator implements NavigatorStorageUtils (Section 7.5.1.4)
    ("yieldForStorageUpdates",   AbsBuiltinFunc("Navigator.yieldForStorageUpdates", 0)),
    // Navigator implements NavigatorPlugins (Section 7.5.1.5)
    // Browsers implement 'javaEnabled' as a function, though the standard specifies it as a field
    ("javaEnabled",   AbsBuiltinFunc("Navigator.javaEnabled", 0))
  )

  /* no constructor */
  /* initial property list */
  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (loc_ins, prop_ins), (loc_proto, prop_proto)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(    
      ("Navigator.javaEnabled" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(BoolTop)), ctx), (he, ctxe))
        }))

      //case "Navigator.registerProtocolHandler"
      //case "Navigator.registerContentHandler"
      //case "Navigator.isProtocolHandlerRegistered"
      //case "Navigator.isContentHandlerRegistered"
      //case "Navigator.unregisterProtocolHandler"
      //case "Navigator.unregisterContentHandler"
      //case "Navigator.yieldForStorageUpdates"
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //case "Navigator.registerProtocolHandler"
      //case "Navigator.registerContentHandler"
      //case "Navigator.isProtocolHandlerRegistered"
      //case "Navigator.isContentHandlerRegistered"
      //case "Navigator.unregisterProtocolHandler"
      //case "Navigator.unregisterContentHandler"
      //case "Navigator.yieldForStorageUpdates"
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      //case "Navigator.registerProtocolHandler"
      //case "Navigator.registerContentHandler"
      //case "Navigator.isProtocolHandlerRegistered"
      //case "Navigator.isContentHandlerRegistered"
      //case "Navigator.unregisterProtocolHandler"
      //case "Navigator.unregisterContentHandler"
      //case "Navigator.yieldForStorageUpdates"
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //case "Navigator.registerProtocolHandler"
      //case "Navigator.registerContentHandler"
      //case "Navigator.isProtocolHandlerRegistered"
      //case "Navigator.isContentHandlerRegistered"
      //case "Navigator.unregisterProtocolHandler"
      //case "Navigator.unregisterContentHandler"
      //case "Navigator.yieldForStorageUpdates"
    )
  }

  /* instance */
  def getInstance(): Option[Loc] = Some (loc_ins)
}
