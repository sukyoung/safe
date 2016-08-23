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
  // TODO @function
  code = EmptyCode,
  props =
  ("length", PrimModel(1), F, F, F) ::
    // TODO getPrototypeOf
    ("getPrototypeOf", BuiltinFuncModel("Object.getPrototypeOf", EmptyCode), T, F, T) ::
    // TODO getOwnPropertyDescriptor
    ("getOwnPropertyDescriptor", BuiltinFuncModel("Object.getOwnPropertyDescriptor", EmptyCode), T, F, T) ::
    // TODO getOwnPropertyNames
    ("getOwnPropertyNames", BuiltinFuncModel("Object.getOwnPropertyNames", EmptyCode), T, F, T) ::
    // TODO create
    ("create", BuiltinFuncModel("Object.create", EmptyCode), T, F, T) ::
    // TODO defineProperty
    ("defineProperty", BuiltinFuncModel("Object.create", EmptyCode), T, F, T) ::
    // TODO defineProperties
    ("defineProperties", BuiltinFuncModel("Object.defineProperties", EmptyCode), T, F, T) ::
    // TODO seal
    ("seal", BuiltinFuncModel("Object.seal", EmptyCode), T, F, T) ::
    // TODO freeze
    ("freeze", BuiltinFuncModel("Object.freeze", EmptyCode), T, F, T) ::
    // TODO preventExtensions
    ("preventExtensions", BuiltinFuncModel("Object.preventExtensions", EmptyCode), T, F, T) ::
    // TODO isSealed
    ("isSealed", BuiltinFuncModel("Object.isSealed", EmptyCode), T, F, T) ::
    // TODO isFrozen
    ("isFrozen", BuiltinFuncModel("Object.isFrozen", EmptyCode), T, F, T) ::
    // TODO isExtensible
    ("isExtensible", BuiltinFuncModel("Object.isExtensible", EmptyCode), T, F, T) ::
    // TODO keys
    ("keys", BuiltinFuncModel("Object.keys", EmptyCode), T, F, T) :: Nil,
  protoProps =
  ("@proto", PrimModel(null), F, F, F) ::
    // TODO toString
    ("toString", BuiltinFuncModel("Object.prototype.toString", EmptyCode), T, F, T) ::
    // TODO toLocaleString
    ("toLocaleString", BuiltinFuncModel("Object.prototype.toLocaleString", EmptyCode), T, F, T) ::
    // TODO valueOf
    ("valueOf", BuiltinFuncModel("Object.prototype.valueOf", EmptyCode), T, F, T) ::
    // TODO hasOwnProperty
    ("hasOwnProperty", BuiltinFuncModel("Object.prototype.hasOwnProperty", EmptyCode), T, F, T) ::
    // TODO isPrototypeOf
    ("isPrototypeOf", BuiltinFuncModel("Object.prototype.isPrototypeOf", EmptyCode), T, F, T) ::
    // TODO propertyIsEnumerable
    ("propertyIsEnumerable", BuiltinFuncModel("Object.prototype.propertyIsEnumerable", EmptyCode), T, F, T) :: Nil,
  writable = F
) with Builtin
