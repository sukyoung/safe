/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.DOMCore

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.cfg.CFG
import kr.ac.kaist.jsaf.analysis.typing.models.AbsBuiltinFunc
import kr.ac.kaist.jsaf.analysis.typing.models.AbsConstValue
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._
import kr.ac.kaist.jsaf.Shell

object DOMNamedNodeMap extends DOM {
  private val name = "NamedNodeMap"

  /* predefined locatoins */
  val loc_cons = newSystemRecentLoc(name + "Cons")
  val loc_proto = newSystemRecentLoc(name + "Proto")
  val loc_ins = newSystemRecentLoc(name + "Ins")
  val loc_ins2 = newSystemRecentLoc(name + "AttrIns")

  /* constructor or object*/
  private val prop_cons: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Function")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("@hasinstance", AbsConstValue(PropValueNullTop)),
    ("length", AbsConstValue(PropValue(ObjectValue(Value(AbsNumber.alpha(0)), F, F, F)))),
    ("prototype", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F))))
  )
/* instance */
  private val prop_ins: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("length", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, F, F))))
  )
/* instance */
  private val prop_ins2: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(loc_proto), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("@default_number", AbsConstValue(PropValue(ObjectValue(Value(DOMAttr.loc_ins), F, F, F)))),
    ("@default_other", AbsConstValue(PropValue(ObjectValue(Value(DOMAttr.loc_ins), F, F, F)))),
    ("length", AbsConstValue(PropValue(ObjectValue(Value(UInt), F, F, F))))
  )


  /* prorotype */
  private val prop_proto: List[(String, AbsProperty)] = List(
    ("@class", AbsConstValue(PropValue(AbsString.alpha("Object")))),
    ("@proto", AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(BoolTrue))),
    ("getNamedItem",      AbsBuiltinFunc("DOMNamedNodeMap.getNamedItem", 1)),
    ("setNamedItem",      AbsBuiltinFunc("DOMNamedNodeMap.setNamedItem", 1)),
    ("removeNamedItem",   AbsBuiltinFunc("DOMNamedNodeMap.removeNamedItem", 1)),
    ("getNamedItemNS",    AbsBuiltinFunc("DOMNamedNodeMap.getNamedItemNS", 2)),
    ("setNamedItemNS",    AbsBuiltinFunc("DOMNamedNodeMap.setNamedItemNS", 1)),
    ("removeNamedItemNS", AbsBuiltinFunc("DOMNamedNodeMap.removeNamedItemNS", 2))
  )

  /* global */
  private val prop_global: List[(String, AbsProperty)] = List(
    (name, AbsConstValue(PropValue(ObjectValue(loc_cons, T, F, T))))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = if(Shell.params.opt_Dommodel2) List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global), (loc_ins, prop_ins),  (loc_ins2, prop_ins2)

  ) else List(
    (loc_cons, prop_cons), (loc_proto, prop_proto), (GlobalLoc, prop_global)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //TODO: not yet implemented
      //case "DOMNamedNodeMap.getNamedItem" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.setNamedItem" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.removeNamedItem" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.getNamedItemNS" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.setNamedItemNS" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.removeNamedItemNS" => ((h,ctx),(he,ctxe))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      //TODO: not yet implemented
      //case "DOMNamedNodeMap.getNamedItem" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.setNamedItem" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.removeNamedItem" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.getNamedItemNS" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.setNamedItemNS" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.removeNamedItemNS" => ((h,ctx),(he,ctxe))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      //case "DOMNamedNodeMap.getNamedItem" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.setNamedItem" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.removeNamedItem" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.getNamedItemNS" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.setNamedItemNS" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.removeNamedItemNS" => ((h,ctx),(he,ctxe))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      //TODO: not yet implemented
      //case "DOMNamedNodeMap.getNamedItem" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.setNamedItem" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.removeNamedItem" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.getNamedItemNS" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.setNamedItemNS" => ((h,ctx),(he,ctxe))
      //case "DOMNamedNodeMap.removeNamedItemNS" => ((h,ctx),(he,ctxe))
    )
  }

  /* instance */
  override def getInstance(cfg: CFG): Option[Loc] = Some(newRecentLoc())
  /* list of properties in the instance object */
  def getInsList(length: Int): List[(String, PropValue)] = List(
      ("@class",  PropValue(AbsString.alpha("Object"))),
      ("@proto",  PropValue(ObjectValue(loc_proto, F, F, F))),
      ("@extensible", PropValue(BoolTrue)),
      // DOM Level 1
      ("length",   PropValue(ObjectValue(AbsNumber.alpha(length), F, F, F)))
    )
   
 
}
