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

package kr.ac.kaist.safe

import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.util.NodeUtil._
import scala.collection.immutable.HashMap

package object analyzer {
  // internal API value
  lazy val internalValueMap: Map[String, AbsValue] = HashMap(
    INTERNAL_TOP -> AbsValue.Top,
    INTERNAL_UINT -> AbsNumber.UInt,
    INTERNAL_GLOBAL -> AbsValue(BuiltinGlobal.loc),
    INTERNAL_BOOL_TOP -> AbsBool.Top,
    INTERNAL_NUM_TOP -> AbsNumber.Top,
    INTERNAL_STR_TOP -> AbsString.Top,
    INTERNAL_EVAL_ERR -> AbsValue(BuiltinEvalError.loc),
    INTERNAL_RANGE_ERR -> AbsValue(BuiltinRangeError.loc),
    INTERNAL_REF_ERR -> AbsValue(BuiltinRefError.loc),
    INTERNAL_SYNTAX_ERR -> AbsValue(BuiltinSyntaxError.loc),
    INTERNAL_TYPE_ERR -> AbsValue(BuiltinTypeError.loc),
    INTERNAL_URI_ERR -> AbsValue(BuiltinURIError.loc),
    INTERNAL_EVAL_ERR_PROTO -> AbsValue(BuiltinEvalErrorProto.loc),
    INTERNAL_RANGE_ERR_PROTO -> AbsValue(BuiltinRangeErrorProto.loc),
    INTERNAL_REF_ERR_PROTO -> AbsValue(BuiltinRefErrorProto.loc),
    INTERNAL_SYNTAX_ERR_PROTO -> AbsValue(BuiltinSyntaxErrorProto.loc),
    INTERNAL_TYPE_ERR_PROTO -> AbsValue(BuiltinTypeErrorProto.loc),
    INTERNAL_URI_ERR_PROTO -> AbsValue(BuiltinURIErrorProto.loc),
    INTERNAL_ERR_PROTO -> AbsValue(BuiltinErrorProto.loc),
    INTERNAL_OBJ_CONST -> AbsValue(BuiltinObject.loc),
    INTERNAL_ARRAY_CONST -> AbsValue(BuiltinArray.loc)
  )
}
