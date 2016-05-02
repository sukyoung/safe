package kr.ac.kaist.safe.analyzer.domain

trait AbsUndefUtil {
  val Top: AbsUndef
  val Bot: AbsUndef
  def alpha: AbsUndef
}

trait AbsNullUtil {
  val Top: AbsNull
  val Bot: AbsNull
  def alpha: AbsNull
}

trait AbsStringUtil {
  val Top: AbsString
  val Bot: AbsString
  val NumStr: AbsString
  val OtherStr: AbsString

  def alpha(str: String): AbsString

  def isHex(str: String): Boolean
  def isNum(str: String): Boolean
  def fromCharCode(n: AbsNumber, absNumber: AbsNumberUtil): AbsString
}

trait AbsNumberUtil {
  val Top: AbsNumber
  val Bot: AbsNumber
  val Infinity: AbsNumber
  val PosInf: AbsNumber
  val NegInf: AbsNumber
  val NaN: AbsNumber
  val UInt: AbsNumber
  val NUInt: AbsNumber

  val naturalNumbers: AbsNumber

  def alpha(d: Double): AbsNumber
}

trait AbsBoolUtil {
  val Top: AbsBool
  val Bot: AbsBool
  val True: AbsBool
  val False: AbsBool

  def alpha(b: Boolean): AbsBool
}
