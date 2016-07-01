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

package kr.ac.kaist.safe.cfg_builder

import scala.util.Try
import kr.ac.kaist.safe.analyzer.domain._

// Used by cfg_builder/DefaultCFGBuilder.scala
trait AddressManager {
  def addrToLoc(addr: Address, recency: RecencyTag): Loc
  def locToAddr(loc: Loc): Address
  def oldifyLoc(loc: Loc): Loc
  def isRecentLoc(loc: Loc): Boolean
  def isOldLoc(loc: Loc): Boolean
  def compareLoc(a: Loc, b: Loc): Int
  def locName(loc: Loc): String
  def parseLocName(s: String): Option[Loc]
  def registerSystemAddress(addr: Address, name: String): Unit
  def newProgramAddr(): Address
  def newProgramAddr(name: String): Address
  def newRecentLoc(name: String): Loc
  def newRecentLoc(): Loc
  def newSystemRecentLoc(name: String): Loc
  def newSystemLoc(name: String, tag: RecencyTag): Loc

  object PredefLoc {
    val GLOBAL: Loc = newSystemLoc("Global", Recent)
    val SINGLE_PURE_LOCAL: Loc = newSystemLoc("PureLocal", Recent)
    val COLLAPSED: Loc = newSystemLoc("Collapsed", Old)
  }

  object ProtoLoc {
    val OBJ: Loc = newSystemLoc("ObjProto", Recent)
    val FUNCTION: Loc = newSystemLoc("FunctionProto", Recent)

    //TODO: temporal definitions of builtin proto locations
    val ARRAY: Loc = newSystemLoc("BuiltinArrayProto", Recent)
    val BOOLEAN: Loc = newSystemLoc("BuiltinBooleanProto", Recent)
    val NUMBER: Loc = newSystemLoc("BuiltinNumberProto", Recent)
    val STRING: Loc = newSystemLoc("BuiltinStringProto", Recent)

    val ERR: Loc = newSystemLoc("ErrProto", Recent)
    val EVAL_ERR: Loc = newSystemLoc("EvalErrProto", Recent)
    val RANGE_ERR: Loc = newSystemLoc("RangeErrProto", Recent)
    val REF_ERR: Loc = newSystemLoc("RefErrProto", Recent)
    val SYNTAX_ERR: Loc = newSystemLoc("SyntaxErrProto", Recent)
    val TYPE_ERR: Loc = newSystemLoc("TypeErrProto", Recent)
    val URI_ERR: Loc = newSystemLoc("URIErrProto", Recent)
  }

  object ConstructorLoc {
    //TODO: temporal definitions of builtin constructor locations
    val OBJ: Loc = newSystemLoc("ObjectConst", Recent)
    val ARRAY: Loc = newSystemLoc("ArrayProto", Recent)
  }

  object ErrorLoc {
    val ERR: Loc = newSystemLoc("Err", Old)
    val EVAL_ERR: Loc = newSystemLoc("EvalErr", Old)
    val RANGE_ERR: Loc = newSystemLoc("RangeErr", Old)
    val REF_ERR: Loc = newSystemLoc("RefErr", Old)
    val SYNTAX_ERR: Loc = newSystemLoc("SyntaxErr", Old)
    val TYPE_ERR: Loc = newSystemLoc("TypeErr", Old)
    val URI_ERR: Loc = newSystemLoc("URIErr", Old)
  }
}
