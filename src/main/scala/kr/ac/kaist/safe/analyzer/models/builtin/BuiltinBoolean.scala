/**
 * *****************************************************************************
 * Copyright (c) 2016, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.models.builtin

import scala.collection.immutable.HashSet
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.util.SystemAddr

// 15.6 Boolean Objects
object BuiltinBoolean extends FuncModel(
  name = "Boolean",

  // 15.6.1 The Boolean Constructor Called as a Function: Boolean([value])
  code = SimpleCode(argLen = 1, code = (
    args, h
  ) => {
    val argV = Helper.propLoad(args, Set(AbsString("0")), h)

    // Returns a Boolean value (not a Boolean object) computed by ToBoolean(value).
    val boolPV = AbsPValue(TypeConversionHelper.ToBoolean(argV))
    AbsValue(boolPV)
  }),

  // 15.6.2 The Boolean Constructor: new Boolean([value])
  construct = Some(BasicCode(argLen = 1, code = (
    args, st
  ) => {
    val h = st.heap
    val argV = Helper.propLoad(args, Set(AbsString("0")), h)
    val addr = SystemAddr("Boolean<instance>")

    val state = st.oldify(addr)
    val loc = Loc(addr, Recent)
    val obj = AbsObjectUtil.newBooleanObj(TypeConversionHelper.ToBoolean(argV))
    val heap = state.heap.update(loc, obj)

    (State(heap, state.context), State.Bot, AbsValue(loc))
  })),

  // 15.6.3.1 Boolean.prototype
  protoModel = Some((BuiltinBooleanProto, F, F, F))
)

// 15.6.4 Boolean.prototype
object BuiltinBooleanProto extends ObjModel(
  name = "Boolean.prototype",
  props = List(
    InternalProp(IPrototype, BuiltinObjectProto),

    // 15.6.4.2 Boolean.prototype.toString()
    NormalProp("toString", FuncModel(
      name = "Boolean.prototype.toString",
      code = BasicCode(code = (
        args, st
      ) => {
        val h = st.heap
        val localObj = st.context.pureLocal.record.decEnvRec
        val (thisV, _) = localObj.GetBindingValue("@this")
        val classV = Helper.propLoad(thisV, Set(AbsString("@class")), h)
        val pv = thisV.pvalue
        val excSet = (pv.undefval.isBottom, pv.nullval.isBottom, pv.numval.isBottom, pv.strval.isBottom) match {
          case (true, true, true, true) if (
            (AbsString("Boolean") <= classV.pvalue.strval
              && classV.pvalue.strval <= AbsString("Boolean"))
              || classV.pvalue.strval <= AbsString.Bot
          ) => ExcSetEmpty
          case _ => HashSet[Exception](TypeError)
        }
        val b = thisV.pvalue.boolval + {
          if ((AbsString("Boolean") <= classV.pvalue.strval)) {
            val primitiveV = Helper.propLoad(thisV, Set(AbsString("@primitive")), h)
            primitiveV.pvalue.boolval
          } else {
            AbsBool.Bot
          }
        }
        (st, st.raiseException(excSet), AbsValue(b.toAbsString))
      })
    ), T, F, T),

    // 15.6.4.3 Boolean.prototype.valueOf()
    NormalProp("valueOf", FuncModel(
      name = "Boolean.prototype.valueOf",
      code = BasicCode(code = (
        args, st
      ) => {
        val h = st.heap
        val localObj = st.context.pureLocal.record.decEnvRec
        val (thisV, _) = localObj.GetBindingValue("@this")
        val classV = Helper.propLoad(thisV, Set(AbsString("@class")), h)
        val pv = thisV.pvalue
        val excSet = (pv.undefval.isBottom, pv.nullval.isBottom, pv.numval.isBottom, pv.strval.isBottom) match {
          case (true, true, true, true) if (
            (AbsString("Boolean") <= classV.pvalue.strval
              && classV.pvalue.strval <= AbsString("Boolean"))
              || classV.pvalue.strval <= AbsString.Bot
          ) => ExcSetEmpty
          case _ => HashSet[Exception](TypeError)
        }
        val b = thisV.pvalue.boolval + {
          if ((AbsString("Boolean") <= classV.pvalue.strval)) {
            val primitiveV = Helper.propLoad(thisV, Set(AbsString("@primitive")), h)
            primitiveV.pvalue.boolval
          } else {
            AbsBool.Bot
          }
        }
        (st, st.raiseException(excSet), AbsValue(b))
      })
    ), T, F, T)
  )
)
