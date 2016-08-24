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

// TODO Object
object BuiltinObject extends FuncModel(
  name = "Object",
  props = List(
    // TODO getPrototypeOf
    ("getPrototypeOf", FuncModel(
      name = "Object.getPrototypeOf",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO getOwnPropertyDescriptor
    ("getOwnPropertyDescriptor", FuncModel(
      name = "Object.getOwnPropertyDescriptor",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO getOwnPropertyNames
    ("getOwnPropertyNames", FuncModel(
      name = "Object.getOwnPropertyNames",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO create
    ("create", FuncModel(
      name = "Object.create",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO defineProperty
    ("defineProperty", FuncModel(
      name = "Object.defineProperty",
      code = EmptyCode(argLen = 3)
    ), T, F, T),

    // TODO defineProperties
    ("defineProperties", FuncModel(
      name = "Object.defineProperties",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO seal
    ("seal", FuncModel(
      name = "Object.seal",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO freeze
    ("freeze", FuncModel(
      name = "Object.freeze",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO preventExtensions
    ("preventExtensions", FuncModel(
      name = "Object.preventExtensions",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO isSealed
    ("isSealed", FuncModel(
      name = "Object.isSealed",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO isFrozen
    ("isFrozen", FuncModel(
      name = "Object.isFrozen",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO isExtensible
    ("isExtensible", FuncModel(
      name = "Object.isExtensible",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO keys
    ("keys", FuncModel(
      name = "Object.keys",
      code = EmptyCode(argLen = 1)
    ), T, F, T)
  ),
  // TODO @function
  code = EmptyCode(1),
  hasConstruct = T,
  protoModel = Some((BuiltinObjectProto, F, F, F))
)

object BuiltinObjectProto extends ObjModel(
  name = "Object.prototype",
  props = List(
    ("@proto", PrimModel(null), F, F, F),

    // TODO toString
    ("toString", FuncModel(
      name = "Object.prototype.toString",
      code = EmptyCode()
    ), T, F, T),

    // TODO toLocaleString
    ("toLocaleString", FuncModel(
      name = "Object.prototype.toLocaleString",
      code = EmptyCode()
    ), T, F, T),

    // TODO valueOf
    ("valueOf", FuncModel(
      name = "Object.prototype.valueOf",
      code = EmptyCode()
    ), T, F, T),

    // TODO hasOwnProperty
    ("hasOwnProperty", FuncModel(
      name = "Object.prototype.hasOwnProperty",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO isPrototypeOf
    ("isPrototypeOf", FuncModel(
      name = "Object.prototype.isPrototypeOf",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO propertyIsEnumerable
    ("propertyIsEnumerable", FuncModel(
      name = "Object.prototype.propertyIsEnumerable",
      code = EmptyCode(argLen = 1)
    ), T, F, T)
  )
)
