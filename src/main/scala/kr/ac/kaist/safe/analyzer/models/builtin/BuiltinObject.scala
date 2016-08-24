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

import kr.ac.kaist.safe.analyzer.models._

object BuiltinObject extends FuncModel(
  name = "Object",
  props = List(
    // TODO getPrototypeOf
    ("getPrototypeOf", BuiltinFuncModel(
      name = "Object.getPrototypeOf",
      argLen = 1,
      code = EmptyCode
    ), T, F, T),

    // TODO getOwnPropertyDescriptor
    ("getOwnPropertyDescriptor", BuiltinFuncModel(
      name = "Object.getOwnPropertyDescriptor",
      argLen = 2,
      code = EmptyCode
    ), T, F, T),

    // TODO getOwnPropertyNames
    ("getOwnPropertyNames", BuiltinFuncModel(
      name = "Object.getOwnPropertyNames",
      argLen = 1,
      code = EmptyCode
    ), T, F, T),

    // TODO create
    ("create", BuiltinFuncModel(
      name = "Object.create",
      argLen = 2,
      code = EmptyCode
    ), T, F, T),

    // TODO defineProperty
    ("defineProperty", BuiltinFuncModel(
      name = "Object.defineProperty",
      argLen = 3,
      code = EmptyCode
    ), T, F, T),

    // TODO defineProperties
    ("defineProperties", BuiltinFuncModel(
      name = "Object.defineProperties",
      argLen = 2,
      code = EmptyCode
    ), T, F, T),

    // TODO seal
    ("seal", BuiltinFuncModel(
      name = "Object.seal",
      argLen = 1,
      code = EmptyCode
    ), T, F, T),

    // TODO freeze
    ("freeze", BuiltinFuncModel(
      name = "Object.freeze",
      argLen = 1,
      code = EmptyCode
    ), T, F, T),

    // TODO preventExtensions
    ("preventExtensions", BuiltinFuncModel(
      name = "Object.preventExtensions",
      argLen = 1,
      code = EmptyCode
    ), T, F, T),

    // TODO isSealed
    ("isSealed", BuiltinFuncModel(
      name = "Object.isSealed",
      argLen = 1,
      code = EmptyCode
    ), T, F, T),

    // TODO isFrozen
    ("isFrozen", BuiltinFuncModel(
      name = "Object.isFrozen",
      argLen = 1,
      code = EmptyCode
    ), T, F, T),

    // TODO isExtensible
    ("isExtensible", BuiltinFuncModel(
      name = "Object.isExtensible",
      argLen = 1,
      code = EmptyCode
    ), T, F, T),

    // TODO keys
    ("keys", BuiltinFuncModel(
      name = "Object.keys",
      argLen = 1,
      code = EmptyCode
    ), T, F, T)
  ),
  protoProps = List(
    ("@proto", PrimModel(null), F, F, F),

    // TODO toString
    ("toString", BuiltinFuncModel(
      name = "Object.prototype.toString",
      code = EmptyCode
    ), T, F, T),

    // TODO toLocaleString
    ("toLocaleString", BuiltinFuncModel(
      name = "Object.prototype.toLocaleString",
      code = EmptyCode
    ), T, F, T),

    // TODO valueOf
    ("valueOf", BuiltinFuncModel(
      name = "Object.prototype.valueOf",
      code = EmptyCode
    ), T, F, T),

    // TODO hasOwnProperty
    ("hasOwnProperty", BuiltinFuncModel(
      name = "Object.prototype.hasOwnProperty",
      argLen = 1,
      code = EmptyCode
    ), T, F, T),

    // TODO isPrototypeOf
    ("isPrototypeOf", BuiltinFuncModel(
      name = "Object.prototype.isPrototypeOf",
      argLen = 1,
      code = EmptyCode
    ), T, F, T),

    // TODO propertyIsEnumerable
    ("propertyIsEnumerable", BuiltinFuncModel(
      name = "Object.prototype.propertyIsEnumerable",
      argLen = 1,
      code = EmptyCode
    ), T, F, T)
  ),
  prototypeWritable = F,
  argLen = 1,
  // TODO @function
  code = EmptyCode
) with Builtin
