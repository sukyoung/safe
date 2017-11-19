/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
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
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.util._

object BuiltinBooleanHelper {
  val instanceASite = PredAllocSite("Boolean<instance>")

  def typeConvert(args: AbsValue, st: AbsState): AbsBool = {
    val h = st.heap
    val argV = Helper.propLoad(args, Set(AbsStr("0")), h)
    val argL = Helper.propLoad(args, Set(AbsStr("length")), h).pvalue.numval
    val emptyB =
      if (AbsNum(0) ⊑ argL) AbsBool(false)
      else AbsBool.Bot
    TypeConversionHelper.ToBoolean(argV) ⊔ emptyB
  }

  def getValue(thisV: AbsValue, h: AbsHeap): AbsBool = {
    thisV.pvalue.boolval ⊔ thisV.locset.foldLeft(AbsBool.Bot)((res, loc) => {
      if ((AbsStr("Boolean") ⊑ h.get(loc)(IClass).value.pvalue.strval))
        res ⊔ h.get(loc)(IPrimitiveValue).value.pvalue.boolval
      else res
    })
  }

  val constructor = BasicCode(
    argLen = 1,
    asiteSet = HashSet(instanceASite),
    code = (args: AbsValue, st: AbsState) => {
      val bool = typeConvert(args, st)
      val loc = Loc(instanceASite)
      val state = st.oldify(loc)
      val heap = state.heap.update(loc, AbsObj.newBooleanObj(bool))

      (AbsState(heap, state.context), AbsState.Bot, AbsValue(loc))
    }
  )

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
      code = BasicCode(argLen = 1, code = (
        args: AbsValue, st: AbsState
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
      code = BasicCode(argLen = 1, code = (
        args: AbsValue, st: AbsState
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
