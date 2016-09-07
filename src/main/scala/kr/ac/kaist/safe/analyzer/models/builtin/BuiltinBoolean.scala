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
import kr.ac.kaist.safe.analyzer.TypeConversionHelper
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.util.{ SystemAddr, Loc, Recent }

// 15.6 Boolean Objects
object BuiltinBoolean extends FuncModel(
  name = "Boolean",

  // 15.6.1 The Boolean Constructor Called as a Function: Boolean([value])
  code = SimpleCode(argLen = 1, code = (
    args, h, sem
  ) => {
    val argV = sem.CFGLoadHelper(args, Set(AbsString.alpha("0")), h)

    // Returns a Boolean value (not a Boolean object) computed by ToBoolean(value).
    val boolPV = AbsPValue(TypeConversionHelper.ToBoolean(argV))
    ValueUtil(boolPV)
  }),

  // 15.6.2 The Boolean Constructor: new Boolean([value])
  construct = Some(BasicCode(argLen = 1, code = (
    args, st, sem
  ) => {
    val h = st.heap
    val argV = sem.CFGLoadHelper(args, Set(AbsString.alpha("0")), h)
    val addr = SystemAddr("Boolean<instance>")

    val state = st.oldify(addr)
    val loc = Loc(addr, Recent)
    val obj = AbsObjectUtil.newBooleanObj(TypeConversionHelper.ToBoolean(argV))
    val heap = state.heap.update(loc, obj)

    (State(heap, state.context), State.Bot, ValueUtil(loc))
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
        args, st, sem
      ) => {
        val h = st.heap
        val localObj = st.context.pureLocal
        val thisLocSet = localObj.getOrElse("@this")(LocSetEmpty) { _.value.locset }
        val thisV = ValueUtil(thisLocSet)
        val classV = sem.CFGLoadHelper(thisV, Set(AbsString.alpha("@class")), h)
        val pv = thisV.pvalue
        val excSet = (pv.undefval.gamma, pv.nullval.gamma, pv.numval.gamma, pv.strval.gamma) match {
          case (ConSimpleBot(), ConSimpleBot(), ConSetBot(), ConSetBot()) if (
            (AbsString.alpha("Boolean") <= classV.pvalue.strval
              && classV.pvalue.strval <= AbsString.alpha("Boolean"))
              || classV.pvalue.strval <= AbsString.Bot
          ) => ExceptionSetEmpty
          case _ => HashSet[Exception](TypeError)
        }
        val b = thisV.pvalue.boolval + {
          if ((AbsString.alpha("Boolean") <= classV.pvalue.strval)) {
            val primitiveV = sem.CFGLoadHelper(thisV, Set(AbsString.alpha("@primitive")), h)
            primitiveV.pvalue.boolval
          } else {
            AbsBool.Bot
          }
        }
        (st, st.raiseException(excSet), ValueUtil(b.toAbsString))
      })
    ), T, F, T),

    // 15.6.4.3 Boolean.prototype.valueOf()
    NormalProp("valueOf", FuncModel(
      name = "Boolean.prototype.valueOf",
      code = BasicCode(code = (
        args, st, sem
      ) => {
        val h = st.heap
        val localObj = st.context.pureLocal
        val thisLocSet = localObj.getOrElse("@this")(LocSetEmpty) { _.value.locset }
        val thisV = ValueUtil(thisLocSet)
        val classV = sem.CFGLoadHelper(thisV, Set(AbsString.alpha("@class")), h)
        val pv = thisV.pvalue
        val excSet = (pv.undefval.gamma, pv.nullval.gamma, pv.numval.gamma, pv.strval.gamma) match {
          case (ConSimpleBot(), ConSimpleBot(), ConSetBot(), ConSetBot()) if (
            (AbsString.alpha("Boolean") <= classV.pvalue.strval
              && classV.pvalue.strval <= AbsString.alpha("Boolean"))
              || classV.pvalue.strval <= AbsString.Bot
          ) => ExceptionSetEmpty
          case _ => HashSet[Exception](TypeError)
        }
        val b = thisV.pvalue.boolval + {
          if ((AbsString.alpha("Boolean") <= classV.pvalue.strval)) {
            val primitiveV = sem.CFGLoadHelper(thisV, Set(AbsString.alpha("@primitive")), h)
            primitiveV.pvalue.boolval
          } else {
            AbsBool.Bot
          }
        }
        (st, st.raiseException(excSet), ValueUtil(b))
      })
    ), T, F, T)
  )
)
