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

object BuiltinString extends FuncModel(
  name = "String",
  props = List(
    // TODO fromCharCode
    ("fromCharCode", FuncModel(
      name = "String.fromCharCode",
      code = EmptyCode(argLen = 1)
    ), T, F, T)
  ),
  // TODO @function
  code = EmptyCode(1),
  // TODO @construct
  construct = Some(EmptyCode()),
  protoModel = Some((BuiltinStringProto, F, F, F))
)

object BuiltinStringProto extends ObjModel(
  name = "String.prototype",
  props = List(
    ("@class", PrimModel("String"), F, F, F),

    // TODO toString
    ("toString", FuncModel(
      name = "String.prototype.toString",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO valueOf
    ("valueOf", FuncModel(
      name = "String.prototype.valueOf",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO charAt
    ("charAt", FuncModel(
      name = "String.prototype.charAt",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO charCodeAt
    ("charCodeAt", FuncModel(
      name = "String.prototype.charCodeAt",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO concat
    ("concat", FuncModel(
      name = "String.prototype.concat",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO indexOf
    ("indexOf", FuncModel(
      name = "String.prototype.indexOf",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO lastIndexOf
    ("lastIndexOf", FuncModel(
      name = "String.prototype.lastIndexOf",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO localeCompare
    ("localeCompare", FuncModel(
      name = "String.prototype.localeCompare",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO match
    ("match", FuncModel(
      name = "String.prototype.match",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO replace
    ("replace", FuncModel(
      name = "String.prototype.replace",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO search
    ("search", FuncModel(
      name = "String.prototype.search",
      code = EmptyCode(argLen = 1)
    ), T, F, T),

    // TODO slice
    ("slice", FuncModel(
      name = "String.prototype.slice",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO split
    ("split", FuncModel(
      name = "String.prototype.split",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO substring
    ("substring", FuncModel(
      name = "String.prototype.substring",
      code = EmptyCode(argLen = 2)
    ), T, F, T),

    // TODO toLowerCase
    ("toLowerCase", FuncModel(
      name = "String.prototype.toLowerCase",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toLocaleLowerCase
    ("toLocaleLowerCase", FuncModel(
      name = "String.prototype.toLocaleLowerCase",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toUpperCase
    ("toUpperCase", FuncModel(
      name = "String.prototype.toUpperCase",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO toLocaleUpperCase
    ("toLocaleUpperCase", FuncModel(
      name = "String.prototype.toLocaleUpperCase",
      code = EmptyCode(argLen = 0)
    ), T, F, T),

    // TODO trim
    ("trim", FuncModel(
      name = "String.prototype.trim",
      code = EmptyCode(argLen = 0)
    ), T, F, T)
  )
)
