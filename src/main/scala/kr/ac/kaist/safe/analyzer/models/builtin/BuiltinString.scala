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

object BuiltinStringHelper {
  def typeConvert(args: AbsValue, h: Heap): AbsString = {
    val argV = Helper.propLoad(args, Set(AbsString("0")), h)
    val argL = Helper.propLoad(args, Set(AbsString("length")), h).pvalue.numval
    val emptyS =
      if (AbsNumber(0) <= argL) AbsString("")
      else AbsString.Bot
    TypeConversionHelper.ToString(argV) + emptyS
  }

  val constructor = BasicCode(argLen = 1, code = (
    args: AbsValue, st: State
  ) => {
    val h = st.heap
    val num = typeConvert(args, h)
    val addr = SystemAddr("String<instance>")
    val state = st.oldify(addr)
    val loc = Loc(addr, Recent)
    val heap = state.heap.update(loc, AbsObjectUtil.newStringObj(num))
    (State(heap, state.context), State.Bot, AbsValue(loc))
  })

  val typeConversion = PureCode(argLen = 1, code = typeConvert)
}

// 15.5 String Objects
object BuiltinString extends FuncModel(
  name = "String",

  // 15.5.1 The String Constructor Called as a Function
  // 15.5.1.1 String( [value] )
  code = BuiltinStringHelper.typeConversion,

  // 15.5.2 The String Constructor
  // 15.5.2.1 new String ( [ value ] )
  construct = Some(BuiltinStringHelper.constructor),

  // 15.5.3.1 String.prototype
  protoModel = Some((BuiltinStringProto, F, F, F)),

  props = List(
    // 15.5.3.2 String.fromCharCode ([char0 [, char1 [, ...]]])
    NormalProp("fromCharCode", FuncModel(
      name = "String.fromCharCode",
      code = BasicCode(argLen = 0, (
        args: AbsValue, st: State
      ) => {
        val h = st.heap
        val argL = Helper.propLoad(args, Set(AbsString("length")), h).pvalue.numval
        // If no arguments are supplied, the result is the empty String.
        val emptyS = if (AbsNumber(0) <= argL) AbsString("") else AbsString.Bot
        // An argument is converted to a character by applying the operation ToUint16 (9.7)
        // and regarding the resulting 16-bit integer as the code unit value of a character.
        val s = emptyS + (argL.getSingle match {
          case ConOne(Num(n)) =>
            (0 until n.toInt).foldLeft(AbsString(""))((str, i) => {
              val argV = Helper.propLoad(args, Set(AbsString(i.toString)), h)
              str.concat(AbsString.fromCharCode(TypeConversionHelper.ToUInt16(argV)))
            })
          case _ => AbsString.Top
        })
        (st, State.Bot, AbsValue(s))
      })
    ), T, F, T)
  )
)

object BuiltinStringProto extends ObjModel(
  name = "String.prototype",

  // 15.5.4 Properties of the String Prototype Object
  props = List(
    InternalProp(IClass, PrimModel("String")),

    InternalProp(IPrimitiveValue, PrimModel("")),

    // 15.5.4.1 String.prototype.constructor
    NormalProp("constructor", FuncModel(
      name = "String.prototype.constructor",
      code = BuiltinStringHelper.constructor
    ), T, F, T),

    // TODO toString
    NormalProp("toString", FuncModel(
      name = "String.prototype.toString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO valueOf
    NormalProp("valueOf", FuncModel(
      name = "String.prototype.valueOf",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO charAt
    NormalProp("charAt", FuncModel(
      name = "String.prototype.charAt",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO charCodeAt
    NormalProp("charCodeAt", FuncModel(
      name = "String.prototype.charCodeAt",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO concat
    NormalProp("concat", FuncModel(
      name = "String.prototype.concat",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO indexOf
    NormalProp("indexOf", FuncModel(
      name = "String.prototype.indexOf",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO lastIndexOf
    NormalProp("lastIndexOf", FuncModel(
      name = "String.prototype.lastIndexOf",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO localeCompare
    NormalProp("localeCompare", FuncModel(
      name = "String.prototype.localeCompare",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO match
    NormalProp("match", FuncModel(
      name = "String.prototype.match",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO replace
    NormalProp("replace", FuncModel(
      name = "String.prototype.replace",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO search
    NormalProp("search", FuncModel(
      name = "String.prototype.search",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO slice
    NormalProp("slice", FuncModel(
      name = "String.prototype.slice",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO split
    NormalProp("split", FuncModel(
      name = "String.prototype.split",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO substring
    NormalProp("substring", FuncModel(
      name = "String.prototype.substring",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO toLowerCase
    NormalProp("toLowerCase", FuncModel(
      name = "String.prototype.toLowerCase",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toLocaleLowerCase
    NormalProp("toLocaleLowerCase", FuncModel(
      name = "String.prototype.toLocaleLowerCase",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toUpperCase
    NormalProp("toUpperCase", FuncModel(
      name = "String.prototype.toUpperCase",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toLocaleUpperCase
    NormalProp("toLocaleUpperCase", FuncModel(
      name = "String.prototype.toLocaleUpperCase",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO trim
    NormalProp("trim", FuncModel(
      name = "String.prototype.trim",
      code = EmptyCode(argLen = 0)
    ), T, F, T)
  )
)
