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

import kr.ac.kaist.safe.analyzer.domain.IPrototype
import kr.ac.kaist.safe.analyzer.models._

// TODO Function
object BuiltinFunction extends FuncModel(
  name = "Function",
  // TODO @function
  // argLen: 15.3.3.2 Function.length
  code = EmptyCode(argLen = 1),
  // TODO @construct
  construct = Some(EmptyCode()),
  // 15.3.3.1 Function.prototype
  protoModel = Some((BuiltinFunctionProto, F, F, F))
)

object BuiltinFunctionProto extends FuncModel(
  name = "Function.prototype",
  // 15.3.4
  code = EmptyCode(argLen = 0),
  props = List(
    InternalProp(IPrototype, BuiltinObjectProto),

    // TODO toString
    NormalProp("toString", FuncModel(
      name = "Function.prototype.toString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO apply
    NormalProp("apply", FuncModel(
      name = "Function.prototype.apply",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO call
    NormalProp("call", FuncModel(
      name = "Function.prototype.call",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO bind
    NormalProp("bind", FuncModel(
      name = "Function.prototype.bind",
      code = EmptyCode(argLen = 1)
    ), T, F, T)
  )
)
