/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer

import kr.ac.kaist.safe.nodes.cfg.{ CFG, FunctionId }
import kr.ac.kaist.safe.util.IName
import scala.collection.immutable.{ HashMap, HashSet }

package object domain {
  ////////////////////////////////////////////////////////////////
  // value constructors
  ////////////////////////////////////////////////////////////////
  val ExcSetEmpty: Set[Exception] = HashSet[Exception]()
  val ExcSetTop: Set[Exception] = null // TODO refactoring

  val FidSetEmpty: Set[FunctionId] = HashSet[FunctionId]()

  type ObjInternalMap = Map[IName, AbsIValue]
  val ObjEmptyIMap: ObjInternalMap = HashMap[IName, AbsIValue]()

  ////////////////////////////////////////////////////////////////
  // constant values
  ////////////////////////////////////////////////////////////////
  val STR_DEFAULT_OTHER = "@default_other"
  val STR_DEFAULT_NUMBER = "@default_number"
  val DEFAULT_KEYSET = Set(STR_DEFAULT_NUMBER, STR_DEFAULT_OTHER)

  ////////////////////////////////////////////////////////////////
  // string value helper functions
  ////////////////////////////////////////////////////////////////
  /* regexp, number string */
  private val hex = "(0[xX][0-9a-fA-F]+)".r.pattern
  private val exp = "[eE][+-]?[0-9]+"
  private val dec1 = "[0-9]+\\.[0-9]*(" + exp + ")?"
  private val dec2 = "\\.[0-9]+(" + exp + ")?"
  private val dec3 = "[0-9]+(" + exp + ")?"
  private val dec = "([+-]?(Infinity|(" + dec1 + ")|(" + dec2 + ")|(" + dec3 + ")))"
  private val numRegexp = ("NaN|(" + hex + ")|(" + dec + ")").r.pattern

  def isHex(str: String): Boolean =
    hex.matcher(str).matches()

  def isNumber(str: String): Boolean =
    numRegexp.matcher(str).matches()

  ////////////////////////////////////////////////////////////////
  // implicit conversion for concrete types of primitive values
  ////////////////////////////////////////////////////////////////
  // Boolean <-> Bool
  implicit def bool2boolean(b: Bool): Boolean = b.b
  implicit def boolean2bool(b: Boolean): Bool = Bool(b)
  implicit def booleanSet2bool(set: Set[Boolean]): Set[Bool] = set.map(boolean2bool)

  // Double <-> Num
  implicit def num2double(num: Num): Double = num.num
  implicit def double2num(num: Double): Num = Num(num)
  implicit def doubleSet2num(set: Set[Double]): Set[Num] = set.map(double2num)

  // String <-> Str
  implicit def str2string(str: Str): String = str.str
  implicit def string2str(str: String): Str = Str(str)
  implicit def stringSet2str(set: Set[String]): Set[Str] = set.map(string2str)

  ////////////////////////////////////////////////////////////////
  // implicit conversion for abstract domains
  ////////////////////////////////////////////////////////////////
  // primitive abstract domains -> AbsPValue
  implicit def undef2pv(undef: AbsUndef): AbsPValue = AbsPValue(undefval = undef)
  implicit def null2pv(x: AbsNull): AbsPValue = AbsPValue(nullval = x)
  implicit def bool2pv(b: AbsBool): AbsPValue = AbsPValue(boolval = b)
  implicit def num2pv(num: AbsNumber): AbsPValue = AbsPValue(numval = num)
  implicit def str2pv(str: AbsString): AbsPValue = AbsPValue(strval = str)

  // AbsPValue -> AbsValue
  implicit def pv2v[T <% AbsPValue](pv: T): AbsValue = AbsValue(pv)

  // AbsLoc -> AbsValue
  implicit def loc2v[T <% AbsLoc](loc: T): AbsValue = AbsValue(loc)

  // AbsValue -> AbsIValue
  implicit def v2iv[T <% AbsValue](v: T): AbsIValue = AbsIValue(v)

  // AbsFId -> AbsIValue
  implicit def fid2iv[T <% AbsFId](fid: T): AbsIValue = AbsIValue(fid)

  // AbsDecEnvRec, AbsGlobalEnvRec -> AbsEnvRec
  implicit def denv2env(dEnv: AbsDecEnvRec): AbsEnvRec = AbsEnvRec(dEnv)
  implicit def genv2env(gEnv: AbsGlobalEnvRec): AbsEnvRec = AbsEnvRec(gEnv)

  ////////////////////////////////////////////////////////////////
  // abstract domains
  ////////////////////////////////////////////////////////////////
  def register(
    absUndef: AbsUndefUtil = DefaultUndef,
    absNull: AbsNullUtil = DefaultNull,
    absBool: AbsBoolUtil = DefaultBool,
    absNumber: AbsNumberUtil = DefaultNumber,
    absString: AbsStringUtil = StringSet(0),
    absLoc: AbsLocUtil = DefaultLoc,
    aaddrType: AAddrType = RecencyAAddr
  ): Unit = {
    AbsUndef = absUndef
    AbsNull = absNull
    AbsBool = absBool
    AbsNumber = absNumber
    AbsString = absString
    AbsLoc = absLoc
    AAddrType = aaddrType
  }

  // primitive values
  var AbsUndef: AbsUndefUtil = _
  var AbsNull: AbsNullUtil = _
  var AbsBool: AbsBoolUtil = _
  var AbsNumber: AbsNumberUtil = _
  var AbsString: AbsStringUtil = _
  var AbsPValue: AbsPValueUtil = DefaultPValue

  // abstract address type
  var AAddrType: AAddrType = _

  // location
  var AbsLoc: AbsLocUtil = _

  // value
  var AbsValue: AbsValueUtil = DefaultValue

  // function id
  var AbsFId: AbsFIdUtil = DefaultFId

  // internal value
  var AbsIValue: AbsIValueUtil = DefaultIValue

  // data property
  var AbsDataProp: AbsDataPropUtil = DefaultDataProp

  // descriptor
  var AbsDesc: AbsDescUtil = DefaultDesc

  // absent value for parital map
  var AbsAbsent: AbsAbsentUtil = DefaultAbsent

  // execution context
  var AbsBinding: AbsBindingUtil = DefaultBinding
  var AbsDecEnvRec: AbsDecEnvRecUtil = DefaultDecEnvRec
  var AbsGlobalEnvRec: AbsGlobalEnvRecUtil = DefaultGlobalEnvRec
  var AbsEnvRec: AbsEnvRecUtil = DefaultEnvRec
  var AbsLexEnv: AbsLexEnvUtil = DefaultLexEnv
  var AbsContext: AbsContextUtil = DefaultContext

  // object
  var AbsObject: AbsObjectUtil = AKeyObject

  // heap
  var AbsHeap: AbsHeapUtil = DefaultHeap

  // state
  var AbsState: AbsStateUtil = DefaultState

  // concrete domains
  def ConSingle[T]: ConSingleUtil[T] = ConSingleUtil[T]
  def ConSet[T]: ConSetUtil[T] = ConSetUtil[T]
}
