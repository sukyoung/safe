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

package kr.ac.kaist.safe.analyzer.models

import kr.ac.kaist.safe.util.{ Loc, SystemLoc, Recent, Old }

object ProtoLoc extends ModelLoc {
  val OBJ: Loc = SystemLoc("ObjProto", Recent)
  val FUNCTION: Loc = SystemLoc("FunctionProto", Recent)

  //TODO: temporal definitions of builtin proto locations
  val ARRAY: Loc = SystemLoc("BuiltinArrayProto", Recent)
  val BOOLEAN: Loc = SystemLoc("BuiltinBooleanProto", Recent)
  val NUMBER: Loc = SystemLoc("BuiltinNumberProto", Recent)
  val STRING: Loc = SystemLoc("BuiltinStringProto", Recent)

  val ERR: Loc = SystemLoc("ErrProto", Recent)
  val EVAL_ERR: Loc = SystemLoc("EvalErrProto", Recent)
  val RANGE_ERR: Loc = SystemLoc("RangeErrProto", Recent)
  val REF_ERR: Loc = SystemLoc("RefErrProto", Recent)
  val SYNTAX_ERR: Loc = SystemLoc("SyntaxErrProto", Recent)
  val TYPE_ERR: Loc = SystemLoc("TypeErrProto", Recent)
  val URI_ERR: Loc = SystemLoc("URIErrProto", Recent)
}
