package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.cfg_builder.AddressManager

case class PredefLoc(addrManager: AddressManager) {
  val GLOBAL_LOC: Loc = addrManager.newSystemLoc("Global", Recent)
  val SINGLE_PURE_LOCAL_LOC: Loc = addrManager.newSystemLoc("PureLocal", Recent)
  val COLLAPSED_LOC: Loc = addrManager.newSystemLoc("Collapsed", Old)

  val OBJ_PROTO_LOC: Loc = addrManager.newSystemLoc("ObjProto", Recent)
  val FUNCTION_PROTO_LOC: Loc = addrManager.newSystemLoc("FunctionProto", Recent)

  //TODO: temporal definitions of builtin proto locations
  val ARRAY_PROTO: Loc = addrManager.newSystemLoc("BuiltinArrayProto", Recent)
  val BOOLEAN_PROTO: Loc = addrManager.newSystemLoc("BuiltinBooleanProto", Recent)
  val NUMBER_PROTO: Loc = addrManager.newSystemLoc("BuiltinNumberProto", Recent)
  val STRING_PROTO: Loc = addrManager.newSystemLoc("BuiltinStringProto", Recent)

  val ERR_LOC: Loc = addrManager.newSystemLoc("Err", Old)
  val EVAL_ERR_LOC: Loc = addrManager.newSystemLoc("EvalErr", Old)
  val RANGE_ERR_LOC: Loc = addrManager.newSystemLoc("RangeErr", Old)
  val REF_ERR_LOC: Loc = addrManager.newSystemLoc("RefErr", Old)
  val SYNTAX_ERR_LOC: Loc = addrManager.newSystemLoc("SyntaxErr", Old)
  val TYPE_ERR_LOC: Loc = addrManager.newSystemLoc("TypeErr", Old)
  val URI_ERR_LOC: Loc = addrManager.newSystemLoc("URIErr", Old)
}