/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing

import kr.ac.kaist.jsaf.analysis.cfg.FunctionId
import scala.collection.immutable.HashSet
import scala.collection.immutable.HashMap
import kr.ac.kaist.jsaf.analysis.cfg.InternalError
import kr.ac.kaist.jsaf.analysis.lib.{HeapTreeMap, ObjTreeMap, LocTreeSet, IntTreeSet}
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

package object domain {
  /* abstract location */
  type Loc = Int
  type Address = Int
  type RecencyTag = Int
  val Recent = 0
  val Old = 1

  val NullTop = AbsNull.NullTop
  val NullBot = AbsNull.NullBot

  val UndefTop = AbsUndef.UndefTop
  val UndefBot = AbsUndef.UndefBot

  val BoolTop = AbsBool.BoolTop
  val BoolBot = AbsBool.BoolBot
  val BoolTrue = AbsBool.BoolTrue
  val BoolFalse = AbsBool.BoolFalse

  val NumTop = AbsNumber.NumTop
  val NumBot = AbsNumber.NumBot
  val Infinity = AbsNumber.Infinity
  val PosInf = AbsNumber.PosInf
  val NegInf = AbsNumber.NegInf
  val NaN = AbsNumber.NaN
  val UInt = AbsNumber.UInt
  val NUInt = AbsNumber.NUInt

  val StrTop = AbsString.StrTop
  val StrBot = AbsString.StrBot
  val NumStr = AbsStringSet.NumStr
  val OtherStr = AbsStringSet.OtherStr

  type StrDomainType = Int
  val StrDomainSet: StrDomainType = 0
  val StrDomainAutomata: StrDomainType = 1
  val StrDomainDefault = StrDomainSet

  // interface between two abstract domains.
  def absUndefToString(au: AbsUndef): AbsString = {
    if (au.isTop) AbsString.alpha("undefined") // AbsString.alpha("undefined")
    else StrBot
  }
  def absNullToString(an: AbsNull): AbsString = {
    if (an.isTop) AbsString.alpha("null") // AbsString.alpha("null")
    else StrBot
  }

  def absBoolToString(ab: AbsBool): AbsString = {
    ab.getPair match {
      case (AbsTop, _) => OtherStr // AbsString.alpha("true") + AbsString.alpha("false")
      case (AbsBot, _) => StrBot
      case (AbsSingle, Some(true)) => AbsString.alpha("true") // AbsString.alpha("true")
      case (AbsSingle, Some(false)) => AbsString.alpha("false") // AbsString.alpha("false")
      case _ => throw new InternalError("AbsBool does not have an abstract value for multiple values.")
    }
  }

  def absNumberToString(an: AbsNumber): AbsString = {
    an.getPair match {
      case (AbsTop, _) => NumStr
      case (AbsBot, _) => StrBot
      case (AbsSingle, _) => an.toAbsString
      case (AbsMulti, _) => NumStr
    }
  }

  // To filter out refined location
  var posMask: Option[Int] = None 
  var negMask: Option[Int] = None 
  
  def setMaskValue(shift: Int) = {
    posMask = Some((1 << shift) - 1)
    negMask = Some((((1 << 31) - 1) | (1 << 31)) - posMask.get)
  }

  /* callsite address for global code */
  val GlobalCallsite = 0

  /* predefined locations */
  val GlobalLoc: Loc        = newSystemLoc("Global", Recent)
  val SinglePureLocalLoc: Loc = newSystemLoc("PureLocal", Recent)
  val CollapsedLoc: Loc     = newSystemLoc("Collapsed", Old)
  val ObjProtoLoc: Loc      = newSystemLoc("ObjProto", Recent)
  val FunctionProtoLoc: Loc = newSystemLoc("FunctionProto", Recent)

  val JSONObjTopLoc: Loc    = newSystemLoc("JSONObjTop", Old)
  val LibModeObjTopLoc: Loc = newSystemLoc("LibModeObjTop", Old)

  /* special location standing for Context */
  val ContextLoc: Loc       = newSystemLoc("Context", Old)

  /* DOM event TimeStamp */
  val DOMEventTimeLoc: Loc = newSystemLoc("DOMEventTime", Old)

  /* HTML lookup table */
  val IdTableLoc: Loc   = newSystemLoc("IdTable", Recent)
  val NameTableLoc: Loc = newSystemLoc("NameTable", Recent)
  val TagTableLoc: Loc  = newSystemLoc("TagTable", Recent)
  val ClassTableLoc: Loc  = newSystemLoc("ClassTable", Recent)

  /* Event table */
  val EventTargetTableLoc: Loc   = newSystemLoc("EventTargetTable", Recent)
  val EventFunctionTableLoc: Loc = newSystemLoc("EventFunctionTable", Recent)
  val EventSelectorTableLoc: Loc = newSystemLoc("EventSelectorTable", Recent)

  /* temp use */
  val TempStyleLoc: Loc  = newSystemLoc("TempStyle", Old)

  /* Tizen Callback table */
  val TizenCallbackTableLoc: Loc = newSystemLoc("TizenCallbackTable", Recent)
  val TizenCallbackArgTableLoc: Loc = newSystemLoc("TizenCallbackArgTable", Recent)

  /* Map type for Heap */
  type HeapMap = HeapTreeMap
  val HeapMapBot: HeapMap = HeapTreeMap.Empty

  /* Map type for Obj */
  type ObjMap = ObjTreeMap

  /* Address set type */
  type AddrSet = IntTreeSet
  val AddrSetBot: AddrSet = IntTreeSet.Empty
  def AddrSet(a: Address): AddrSet = AddrSetBot + a

  /* Location set type */
  type LocSet = LocTreeSet
  val LocSetBot: LocSet = LocTreeSet.Empty
  def LocSet(l: Loc): LocSet = LocSetBot + l
  
  /* Function set type */
  type FunSet = IntTreeSet
  val FunSetBot: FunSet = IntTreeSet.Empty
  def FunSet(fid: FunctionId): FunSet = FunSetBot + fid
  
  /* singleton location sets */
  // val PureLocalSingleton = LocSet(PureLocalLoc)
  val GlobalSingleton = LocSet(GlobalLoc)
  val CollapsedSingleton = LocSet(CollapsedLoc)
  val ObjProtoSingleton = LocSet(ObjProtoLoc)
  
  /* bottom value */
  val PValueBot = PValue(UndefBot, NullBot, BoolBot, NumBot, StrBot)
    
  val ValueBot = Value(PValueBot, LocSetBot)

  val IValueBot = IValue(PValueBot, LocSetBot)

  val ObjectValueBot = ObjectValue(ValueBot, BoolBot, BoolBot, BoolBot)

  // null represents symbolic bottom for intersection domain of must-oldified set.
  // Actual addresses in the bottom (all possible addresses) are not needed in the analysis.
  val ContextBot = Context(LocSetBot, LocSetBot, AddrSetBot, null)
  val ContextEmpty = Context(LocSetBot, LocSetBot, AddrSetBot, AddrSetBot)

  val PropValueBot = PropValue(ObjectValueBot, FunSetBot)
  val PropValueStrTop = PropValue(Value(StrTop))
  val PropValueNumTop = PropValue(Value(NumTop))
  val PropValueBoolTop = PropValue(Value(BoolTop))
  val PropValueNullTop = PropValue(Value(NullTop))
  val PropValueUndefTop = PropValue(Value(UndefTop))

  val HeapBot = Heap(HeapMapBot)

  val Str_default_other = "@default_other"
  val Str_default_number = "@default_number"
  val Prop_class = AbsString.alpha("@class")
  val Prop_primitive = AbsString.alpha("@primitive")
  val Prop_extensible = AbsString.alpha("@extensible")
  val Prop_proto = AbsString.alpha("@proto")
  val Prop_function = AbsString.alpha("@function")
  val Prop_construct = AbsString.alpha("@construct")
  val Prop_hasinstance = AbsString.alpha("@hasinstance")
  val Prop_scope = AbsString.alpha("@scope")
  val Prop_env = AbsString.alpha("@env")
  val Prop_this = AbsString.alpha("@this")
  val Prop_exception = AbsString.alpha("@exception")
  val Prop_exception_all = AbsString.alpha("@exception_all")
  val Prop_return = AbsString.alpha("@return")
  val Prop_bound_args = AbsString.alpha("@bound_args")
  val Prop_bound_this = AbsString.alpha("@bound_this")
  val Prop_target_function = AbsString.alpha("@target_function")
  val Prop_outer = AbsString.alpha("@outer")

  val SProp_length = AbsString.alpha("length")
  val SProp_arguments = AbsString.alpha("Arguments")
  val SProp_prototype = AbsString.alpha("prototype")
  val SProp_constructor = AbsString.alpha("constructor")
  val SProp_callee = AbsString.alpha("callee")
  val SProp_index = AbsString.alpha("index")
  val SProp_source = AbsString.alpha("source")
  val SProp_global = AbsString.alpha("global")
  val SProp_ignoreCase = AbsString.alpha("ignoreCase")
  val SProp_multiline = AbsString.alpha("multiline")
  val SProp_lastIndex = AbsString.alpha("lastIndex")

  val StateBot = State(HeapBot, ContextBot)

  val ExceptionBot = HashSet[Exception]()
  
  /* top value */
  val PValueTop = PValue(UndefTop, NullTop, BoolTop, NumTop, StrTop)

  // Pseudo top value for JSON parsing results.
  val JSONValueTop = Value(PValueTop, LocSet(JSONObjTopLoc))
  val JSONObjectValueTop = ObjectValue(JSONValueTop, BoolTop,BoolTop,BoolTop)
  val JSONObjTop =
    Obj.empty.
      update(Prop_class, PropValueStrTop).
      update(Prop_extensible, PropValueBoolTop).
      update(Prop_proto, PropValueNullTop).
      update(NumStr, PropValue(JSONObjectValueTop)).
      update(OtherStr, PropValue(JSONObjectValueTop))

  // Pseudo top value for unknown values in library mode.
  // Should be used only when Config.libMode is turned on.
  val FIdTop = -2
  val LibModeValueTop = Value(PValueTop, LocSet(LibModeObjTopLoc))
  val LibModeObjectValueTop = ObjectValue(LibModeValueTop, BoolTop,BoolTop,BoolTop)
  val LibModeObjTop =
    Obj.empty.
      update(Prop_class, PropValueStrTop).
      update(Prop_extensible, PropValueBoolTop).
      update(Prop_proto, PropValueNullTop).
      update(Prop_function, PropValue(ObjectValueBot, FunSet(FIdTop))).
      update(Prop_construct, PropValue(ObjectValueBot, FunSet(FIdTop))).
      update(NumStr, PropValue(LibModeObjectValueTop)).
      update(OtherStr, PropValue(LibModeObjectValueTop))

  val LPBot = LPSet(HashMap[Loc,Set[String]]())
  val LBot = LocSetBot
}
