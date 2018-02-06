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
import kr.ac.kaist.safe.errors.error._
import scala.collection.immutable.{ HashMap, HashSet }
import spray.json._

package object domain {
  ////////////////////////////////////////////////////////////////
  // value alias
  ////////////////////////////////////////////////////////////////
  lazy val AT = AbsBool.True
  lazy val AF = AbsBool.False
  val T = Bool(true)
  val F = Bool(false)

  ////////////////////////////////////////////////////////////////
  // value constructors
  ////////////////////////////////////////////////////////////////
  val ExcSetEmpty: Set[Exception] = HashSet[Exception]()
  val ExcSetTop: Set[Exception] = null // TODO refactoring

  val FidSetEmpty: Set[FunctionId] = HashSet[FunctionId]()

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
  implicit def bool2boolean(b: Bool): Boolean = b.bool
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
  implicit def num2pv(num: AbsNum): AbsPValue = AbsPValue(numval = num)
  implicit def str2pv(str: AbsStr): AbsPValue = AbsPValue(strval = str)

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
    absUndef: UndefDomain = DefaultUndef,
    absNull: NullDomain = DefaultNull,
    absBool: BoolDomain = DefaultBool,
    absNum: NumDomain = DefaultNumber,
    absStr: StrDomain = StringSet(0),
    absLoc: LocDomain = DefaultLoc,
    aaddrType: AAddrType = RecencyAAddr
  ): Unit = {
    this.absUndef = Some(absUndef)
    this.absNull = Some(absNull)
    this.absBool = Some(absBool)
    this.absNum = Some(absNum)
    this.absStr = Some(absStr)
    this.absLoc = Some(absLoc)
    this.aaddrType = Some(aaddrType)
  }

  // primitive values
  private var absUndef: Option[UndefDomain] = _
  lazy val AbsUndef: UndefDomain = get("UndefDomain", absUndef)
  type AbsUndef = AbsUndef.Elem

  private var absNull: Option[NullDomain] = _
  lazy val AbsNull: NullDomain = get("NullDomain", absNull)
  type AbsNull = AbsNull.Elem

  private var absBool: Option[BoolDomain] = _
  lazy val AbsBool: BoolDomain = get("BoolDomain", absBool)
  type AbsBool = AbsBool.Elem

  private var absNum: Option[NumDomain] = _
  lazy val AbsNum: NumDomain = get("NumDomain", absNum)
  type AbsNum = AbsNum.Elem

  private var absStr: Option[StrDomain] = _
  lazy val AbsStr: StrDomain = get("StrDomain", absStr)
  type AbsStr = AbsStr.Elem

  val AbsPValue: DefaultPValue.type = DefaultPValue
  type AbsPValue = DefaultPValue.Elem

  // abstract address type
  private var aaddrType: Option[AAddrType] = _
  lazy val AAddrType: AAddrType = get("AAddrType", aaddrType)

  // location
  private var absLoc: Option[LocDomain] = _
  lazy val AbsLoc: LocDomain = get("LocDomain", absLoc)
  type AbsLoc = AbsLoc.Elem

  // value
  val AbsValue: DefaultValue.type = DefaultValue
  type AbsValue = DefaultValue.Elem

  // function id
  val AbsFId: DefaultFId.type = DefaultFId
  type AbsFId = DefaultFId.Elem

  // internal value
  val AbsIValue: DefaultIValue.type = DefaultIValue
  type AbsIValue = DefaultIValue.Elem

  // data property
  val AbsDataProp: DefaultDataProp.type = DefaultDataProp
  type AbsDataProp = DefaultDataProp.Elem

  // descriptor
  val AbsDesc: DefaultDesc.type = DefaultDesc
  type AbsDesc = DefaultDesc.Elem

  // absent value for parital map
  val AbsAbsent: DefaultAbsent.type = DefaultAbsent
  type AbsAbsent = DefaultAbsent.Elem

  // execution context
  val AbsBinding: DefaultBinding.type = DefaultBinding
  type AbsBinding = DefaultBinding.Elem

  val AbsDecEnvRec: DefaultDecEnvRec.type = DefaultDecEnvRec
  type AbsDecEnvRec = DefaultDecEnvRec.Elem

  val AbsGlobalEnvRec: DefaultGlobalEnvRec.type = DefaultGlobalEnvRec
  type AbsGlobalEnvRec = DefaultGlobalEnvRec.Elem

  val AbsEnvRec: DefaultEnvRec.type = DefaultEnvRec
  type AbsEnvRec = DefaultEnvRec.Elem

  val AbsLexEnv: DefaultLexEnv.type = DefaultLexEnv
  type AbsLexEnv = DefaultLexEnv.Elem

  val AbsContext: DefaultContext.type = DefaultContext
  type AbsContext = DefaultContext.Elem

  // object
  val AbsObj: CKeyObject.type = CKeyObject
  type AbsObj = CKeyObject.Elem

  // heap
  val AbsHeap: DefaultHeap.type = DefaultHeap
  type AbsHeap = DefaultHeap.Elem

  // state
  val AbsState: DefaultState.type = DefaultState
  type AbsState = DefaultState.Elem

  private def get[T](name: String, opt: Option[T]): T = opt match {
    case Some(choice) => choice
    case None => throw NotYetDefined(name)
  }

  ////////////////////////////////////////////////////////////////
  // load from JSON for general structures
  ////////////////////////////////////////////////////////////////
  // for string
  def json2str(v: JsValue): String = v match {
    case JsString(str) => str
    case _ => throw StringParseError(v)
  }

  // for integer
  def json2int(v: JsValue): Int = v match {
    case JsNumber(n) => n.toInt
    case _ => throw IntParseError(v)
  }

  // for map structure
  def json2map[K, V](
    v: JsValue,
    kFromJson: JsValue => K,
    vFromJson: JsValue => V
  ): Map[K, V] = v match {
    case JsArray(vec) => vec.foldLeft[Map[K, V]](HashMap()) {
      case (m, JsArray(Vector(k, v))) => m + (kFromJson(k) -> vFromJson(v))
      case _ => throw MapParseError(v)
    }
    case _ => throw MapParseError(v)
  }

  // for set structure
  def json2set[X](
    v: JsValue,
    fromJson: JsValue => X
  ): Set[X] = v match {
    case JsArray(vec) => vec.foldLeft[Set[X]](HashSet()) {
      case (set, x) => set + fromJson(x)
    }
    case _ => throw SetParseError(v)
  }

  // for pair structure
  def json2pair[L, R](
    v: JsValue,
    lFromJson: JsValue => L,
    rFromJson: JsValue => R
  ): (L, R) = v match {
    case JsArray(Vector(l, r)) => (lFromJson(l), rFromJson(r))
    case _ => throw PairParseError(v)
  }
}
