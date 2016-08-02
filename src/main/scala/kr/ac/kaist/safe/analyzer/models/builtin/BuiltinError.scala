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

object BuiltinError extends FuncModel(
  "Error",
  EmptyCode,
  Nil,
  ("name", PrimModel("Error"), T, F, T) ::
    ("message", PrimModel(""), T, F, T) :: Nil
) with Builtin

object BuiltinEvalError extends FuncModel(
  "EvalError",
  EmptyCode,
  ("@proto", BuiltinError, F, F, F) ::
    ("name", PrimModel("EvalError"), T, F, T) :: Nil,
  ("@proto", BuiltinError.protoModel, F, F, F) ::
    ("name", PrimModel("EvalError"), T, F, T) ::
    ("message", PrimModel(""), T, F, T) :: Nil
) with Builtin

object BuiltinRangeError extends FuncModel(
  "RangeError",
  EmptyCode,
  ("@proto", BuiltinError, F, F, F) ::
    ("name", PrimModel("RangeError"), T, F, T) :: Nil,
  ("@proto", BuiltinError.protoModel, F, F, F) ::
    ("name", PrimModel("RangeError"), T, F, T) ::
    ("message", PrimModel(""), T, F, T) :: Nil
) with Builtin

object BuiltinReferenceError extends FuncModel(
  "ReferenceError",
  EmptyCode,
  ("@proto", BuiltinError, F, F, F) ::
    ("name", PrimModel("ReferenceError"), T, F, T) :: Nil,
  ("@proto", BuiltinError.protoModel, F, F, F) ::
    ("name", PrimModel("ReferenceError"), T, F, T) ::
    ("message", PrimModel(""), T, F, T) :: Nil
) with Builtin

object BuiltinSyntaxError extends FuncModel(
  "SyntaxError",
  EmptyCode,
  ("@proto", BuiltinError, F, F, F) ::
    ("name", PrimModel("SyntaxError"), T, F, T) :: Nil,
  ("@proto", BuiltinError.protoModel, F, F, F) ::
    ("name", PrimModel("SyntaxError"), T, F, T) ::
    ("message", PrimModel(""), T, F, T) :: Nil
) with Builtin

object BuiltinTypeError extends FuncModel(
  "TypeError",
  EmptyCode,
  ("@proto", BuiltinError, F, F, F) ::
    ("name", PrimModel("TypeError"), T, F, T) :: Nil,
  ("@proto", BuiltinError.protoModel, F, F, F) ::
    ("name", PrimModel("TypeError"), T, F, T) ::
    ("message", PrimModel(""), T, F, T) :: Nil
) with Builtin

object BuiltinURIError extends FuncModel(
  "URIError",
  EmptyCode,
  ("@proto", BuiltinError, F, F, F) ::
    ("name", PrimModel("URIError"), T, F, T) :: Nil,
  ("@proto", BuiltinError.protoModel, F, F, F) ::
    ("name", PrimModel("URIError"), T, F, T) ::
    ("message", PrimModel(""), T, F, T) :: Nil
) with Builtin
