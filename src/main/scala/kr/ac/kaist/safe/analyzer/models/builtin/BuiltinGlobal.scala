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

import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.analyzer.Helper
import kr.ac.kaist.safe.analyzer.domain.Value
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.util.NodeUtil

object BuiltinGlobal extends ObjModel("Global", {
  ("NaN", PrimModel(Double.NaN), F, F, F) ::
    ("Infinity", PrimModel(Double.PositiveInfinity), F, F, F) ::
    ("undefined", PrimModel(), F, F, F) ::
    // TODO eval
    ("eval", BuiltinFuncModel("Global.eval", EmptyCode), T, F, T) ::
    ("Object", BuiltinObject, T, F, T) ::
    ("Array", BuiltinArray, T, F, T) ::
    ("Function", BuiltinFunction, T, F, T) ::
    ("Boolean", BuiltinBoolean, T, F, T) ::
    ("Number", BuiltinNumber, T, F, T) ::
    ("String", BuiltinString, T, F, T) ::
    ("Math", BuiltinMath, T, F, T) ::
    ("Error", BuiltinError, T, F, T) ::
    ("EvalError", BuiltinEvalError, T, F, T) ::
    ("RangeError", BuiltinRangeError, T, F, T) ::
    ("ReferenceError", BuiltinReferenceError, T, F, T) ::
    ("SyntaxError", BuiltinSyntaxError, T, F, T) ::
    ("TypeError", BuiltinTypeError, T, F, T) ::
    ("URIError", BuiltinURIError, T, F, T) ::
    (NodeUtil.GLOBAL_NAME, SelfModel, F, F, F) ::
    (NodeUtil.VAR_TRUE, PrimModel(true), F, F, F) :: Nil
}) with Builtin
