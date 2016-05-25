

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.cfg_builder.FunctionId

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

case class Utils(
    absUndef: AbsUndefUtil,
    absNull: AbsNullUtil,
    absBool: AbsBoolUtil,
    absNumber: AbsNumberUtil,
    absString: AbsStringUtil
) {
  val PValueBot: PValue = PValue(absUndef.Bot, absNull.Bot, absBool.Bot, absNumber.Bot, absString.Bot)
  val ValueBot: Value = Value(PValueBot, Set[Loc]())
  val ObjectValueBot: ObjectValue = ObjectValue(ValueBot, absBool.Bot, absBool.Bot, absBool.Bot)
  val PropValueBot: PropValue = PropValue(ObjectValueBot, Set[FunctionId]())

  val ObjBot: Obj = Obj(Obj.ObjMapBot.
    updated(STR_DEFAULT_NUMBER, (PropValueBot, AbsentBot)).
    updated(STR_DEFAULT_OTHER, (PropValueBot, AbsentBot)))
  val ObjEmpty: Obj = Obj(Obj.ObjMapBot.
    updated(STR_DEFAULT_NUMBER, (PropValueBot, AbsentTop)).
    updated(STR_DEFAULT_OTHER, (PropValueBot, AbsentTop)))
}