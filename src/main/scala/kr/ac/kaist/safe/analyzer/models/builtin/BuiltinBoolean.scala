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
import kr.ac.kaist.safe.analyzer.domain.IClass
import kr.ac.kaist.safe.analyzer.domain.IPrimitiveValue
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.util.SystemAddr

object BuiltinBooleanHelper {
  def typeConvert(args: AbsValue, st: State): AbsBool = {
    val h = st.heap
    val argV = Helper.propLoad(args, Set(AbsString("0")), h)
    val argL = Helper.propLoad(args, Set(AbsString("length")), h).pvalue.numval
    val emptyB =
      if (AbsNumber(0) <= argL) AbsBool(false)
      else AbsBool.Bot
    TypeConversionHelper.ToBoolean(argV) + emptyB
  }

  def getValue(thisV: AbsValue, h: Heap): AbsBool = {
    thisV.pvalue.boolval + thisV.locset.foldLeft(AbsBool.Bot)((res, loc) => {
      if ((AbsString("Boolean") <= h.get(loc)(IClass).value.pvalue.strval))
        res + h.get(loc)(IPrimitiveValue).value.pvalue.boolval
      else res
    })
  }

  val constructor = BasicCode(argLen = 1, code = (
    args: AbsValue, st: State
  ) => {
    val bool = typeConvert(args, st)
    val addr = SystemAddr("Boolean<instance>")
    val state = st.oldify(addr)
    val loc = Loc(addr, Recent)
    val heap = state.heap.update(loc, AbsObject.newBooleanObj(bool))

    (State(heap, state.context), State.Bot, AbsValue(loc))
  })

  val typeConversion = PureCode(argLen = 1, code = typeConvert)
}

// 15.6 Boolean Objects
object BuiltinBoolean extends FuncModel(
  name = "Boolean",

  // 15.6.1 The Boolean Constructor Called as a Function: Boolean([value])
  code = BuiltinBooleanHelper.typeConversion,

  // 15.6.2 The Boolean Constructor: new Boolean([value])
  construct = Some(BuiltinBooleanHelper.constructor),

  // 15.6.3.1 Boolean.prototype
  protoModel = Some((BuiltinBooleanProto, F, F, F))
)

// 15.6.4 Boolean.prototype
object BuiltinBooleanProto extends ObjModel(
  name = "Boolean.prototype",
  props = List(
    InternalProp(IPrimitiveValue, PrimModel(false)),
    InternalProp(IClass, PrimModel("Boolean")),

    // 15.6.4.2 Boolean.prototype.toString()
    NormalProp("toString", FuncModel(
      name = "Boolean.prototype.toString",
      code = BasicCode(argLen = 1, (
        args: AbsValue, st: State
      ) => {
        val h = st.heap
        val thisV = st.context.thisBinding
        val excSet = BuiltinHelper.checkExn(h, thisV, "Boolean")
        val b = BuiltinBooleanHelper.getValue(thisV, h)
        val s = TypeConversionHelper.ToString(b)
        (st, st.raiseException(excSet), AbsValue(s))
      })
    ), T, F, T),

    // 15.6.4.3 Boolean.prototype.valueOf()
    NormalProp("valueOf", FuncModel(
      name = "Boolean.prototype.valueOf",
      code = BasicCode(argLen = 1, (
        args: AbsValue, st: State
      ) => {
        val h = st.heap
        val thisV = st.context.thisBinding
        val excSet = BuiltinHelper.checkExn(h, thisV, "Boolean")
        val b = BuiltinBooleanHelper.getValue(thisV, h)
        (st, st.raiseException(excSet), AbsValue(b))
      })
    ), T, F, T)
  )
)
