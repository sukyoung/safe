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

package kr.ac.kaist.safe.analyzer.domain

import scala.collection.immutable.HashSet
import scala.util.Try

class DefaultStrSetUtil(maxSetSize: Int) extends AbsStringUtil {
  val Top: AbsString = DefaultStrTop
  val Bot: AbsString = DefaultStrBot
  val NumStr: AbsString = DefaultStrNum
  val OtherStr: AbsString = DefaultStrOther

  def alpha(str: String): AbsString =
    if (str == null) DefaultStrBot
    else DefaultStrSet(HashSet(str))

  def alpha(values: Set[String]): AbsString =
    if (values.isEmpty)
      DefaultStrBot
    else if (maxSetSize == 0 | values.size <= maxSetSize)
      DefaultStrSet(values)
    else if (hasNum(values)) {
      if (hasOther(values)) DefaultStrTop
      else DefaultStrNum
    } else if (hasOther(values)) OtherStr
    else DefaultStrTop

  def hasNum(values: Set[String]): Boolean =
    values.foldLeft(false)((b: Boolean, v: String) => b | isNum(v))
  def hasOther(values: Set[String]): Boolean =
    values.foldLeft(false)((b: Boolean, v: String) => b | !isNum(v))

  def fromCharCode(n: AbsNumber)(absNumber: AbsNumberUtil): AbsString = {
    n.gamma match {
      case ConSetTop() => DefaultStrTop
      case ConSetBot() => DefaultStrBot
      case ConSetCon(vs) =>
        vs.foldLeft[AbsString](DefaultStrBot)((r, v) => {
          r + alpha("%c".format(v.toInt))
        })
    }
  }

  sealed abstract class DefaultStringSet(maxSetSize: Int = 0) extends AbsString {
    /* AbsDomain Interface */
    def gamma: ConSet[String]
    def gammaSingle: ConSingle[String]
    def gammaSimple: ConSimple = ConSimpleTop
    def gammaIsAllNums: ConSingle[Boolean]
    override def toString: String
    def toAbsString(absString: AbsStringUtil): AbsString
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber

    /* AbsNumber Interface */
    def <=(that: AbsString): Boolean =
      (this, that) match {
        case (DefaultStrBot, _) => true
        case (_, DefaultStrTop) => true
        //      case (DefaultStrSet(v1), DefaultStrSet(v2)) => (!a.hasNum || b.hasNum) && (!a.hasOther || b.hasOther) && a.values.subsetOf(b.values)
        case (DefaultStrSet(v1), DefaultStrSet(v2)) if v1.subsetOf(v2) => true
        case (DefaultStrSet(v1), DefaultStrSet(v2)) => false
        case (DefaultStrSet(v), DefaultStrNum) => hasNum(v) && !hasOther(v)
        case (DefaultStrSet(v), DefaultStrOther) => !hasNum(v) && hasOther(v)
        case (DefaultStrNum, DefaultStrNum) => true
        case (DefaultStrOther, DefaultStrOther) => true
        case _ => false
      }

    def +(that: AbsString): AbsString =
      (this, that) match {
        case (DefaultStrSet(v1), DefaultStrSet(v2)) if v1 == v2 => this
        case (a: DefaultStrSet, b: DefaultStrSet) => alpha(a.values ++ b.values)
        case _ =>
          (this <= that, that <= this) match {
            case (true, _) => that
            case (_, true) => this
            case _ => DefaultStrTop
          }
      }

    def <>(that: AbsString): AbsString =
      (this, that) match {
        case (DefaultStrSet(v1), DefaultStrSet(v2)) => alpha(v1 intersect v2)

        case (DefaultStrSet(v), DefaultStrNum) if hasNum(v) =>
          alpha(v.filter((s: String) => isNum(s)))
        case (DefaultStrNum, DefaultStrSet(v)) if hasNum(v) =>
          alpha(v.filter((s: String) => isNum(s)))
        case (DefaultStrSet(_), DefaultStrNum)
          | (DefaultStrNum, DefaultStrSet(_)) => DefaultStrBot

        case (DefaultStrSet(v), DefaultStrOther) if hasOther(v) =>
          alpha(v.filter((s: String) => !isNum(s)))
        case (DefaultStrOther, DefaultStrSet(v)) if hasOther(v) =>
          alpha(v.filter((s: String) => !isNum(s)))
        case (DefaultStrSet(_), DefaultStrOther)
          | (DefaultStrOther, DefaultStrSet(_)) => DefaultStrBot

        case (DefaultStrNum, DefaultStrNum) => DefaultStrNum
        case (DefaultStrNum, DefaultStrOther)
          | (DefaultStrOther, DefaultStrNum) => DefaultStrBot
        case (DefaultStrOther, DefaultStrOther) => DefaultStrOther

        case _ =>
          (this <= that, that <= this) match {
            case (true, _) => this
            case (_, true) => that
            case _ => DefaultStrBot
          }
      }

    def ===(that: AbsString)(absBool: AbsBoolUtil): AbsBool =
      (this.gammaSingle, that.gammaSingle) match {
        case (ConSingleCon(s1), ConSingleCon(s2)) => absBool.alpha(s1 == s2)
        case (ConSingleBot(), _) | (_, ConSingleBot()) => absBool.Bot
        case _ => (this <= that, that <= this) match {
          case (false, false) => absBool.False
          case _ => absBool.Top
        }
      }

    def <(that: AbsString)(absBool: AbsBoolUtil): AbsBool = {
      (this, that) match {
        case (DefaultStrBot, _) | (_, DefaultStrBot) => absBool.Bot
        case (DefaultStrSet(leftStrSet), DefaultStrSet(rightStrSet)) =>
          leftStrSet.foldLeft(absBool.Bot)((r1, x) => {
            r1 + rightStrSet.foldLeft(absBool.Bot)((r2, y) => r2 + absBool.alpha(x < y))
          })
        case _ => absBool.Top
      }
    }

    val whitespace = " \u0009\u000B\u000C\u0020\u00A0\uFEFF"
    def isWhitespace(c: Char): Boolean = {
      if (whitespace.indexOf(c) < 0)
        Character.getType(c) == Character.SPACE_SEPARATOR
      else
        true
    }
    val lineterminator = "\u000A\u000D\u2028\u2029"
    def isLineTerminator(c: Char): Boolean = {
      lineterminator.indexOf(c) >= 0
    }
    def isWhitespaceOrLineterminator(c: Char): Boolean = isWhitespace(c) || isLineTerminator(c)

    def trim: AbsString =
      this match {
        case DefaultStrSet(vs) =>
          vs.foldLeft[AbsString](DefaultStrBot)((r: AbsString, s: String) => r + alpha(s.trim))
        case DefaultStrNum => DefaultStrNum
        case DefaultStrOther => DefaultStrOther + DefaultStrNum
        case DefaultStrBot => DefaultStrBot
        case _ => DefaultStrTop
      }

    def concat(that: AbsString): AbsString =
      (this, that) match {
        case (DefaultStrSet(v1), DefaultStrSet(v2)) if v1.size * v2.size <= maxSetSize => {
          val set = v1.foldLeft(HashSet[String]())((hs1, s1) =>
            v2.foldLeft(hs1)((hs2, s2) => hs2 + (s1 + s2)))
          alpha(set)
        }
        case (DefaultStrSet(v1), DefaultStrSet(v2)) =>
          (hasNum(v1) | hasNum(v2), hasOther(v1) | hasOther(v2)) match {
            case (true, true) => DefaultStrTop
            case (true, false) => DefaultStrNum
            case (false, true) => DefaultStrOther
            case (false, false) => DefaultStrBot
          }
        case (DefaultStrBot, _) => DefaultStrBot
        case (_, DefaultStrBot) => DefaultStrBot
        case _ => DefaultStrTop
      }

    def charAt(pos: AbsNumber): AbsString = gamma match {
      case ConSetTop() | ConSetBot() => DefaultStrBot
      case ConSetCon(vs) =>
        pos.gammaSingle match {
          case ConSingleBot() => DefaultStrTop
          case ConSingleCon(d) =>
            vs.foldLeft[AbsString](DefaultStrBot)((r, s) => {
              if (d >= s.length || d < 0)
                r + alpha("")
              else {
                val i = d.toInt
                r + alpha(s.substring(i, i + 1))
              }
            })
          case ConSingleTop() => DefaultStrTop
        }
    }

    def charCodeAt(pos: AbsNumber)(absNumber: AbsNumberUtil): AbsNumber = {
      gamma match {
        case ConSetTop() => absNumber.UInt
        case ConSetBot() => absNumber.Bot
        case ConSetCon(vs) =>
          pos.gammaSingle match {
            case ConSingleTop() | ConSingleBot() => absNumber.UInt
            case ConSingleCon(d) =>
              vs.foldLeft[AbsNumber](absNumber.Bot)((r, s) => {
                if (d >= s.length || d < 0)
                  r + absNumber.NaN
                else {
                  val i = d.toInt
                  r + absNumber.alpha(s.substring(i, i + 1).head.toInt)
                }
              })
          }
      }
    }

    def contains(that: AbsString)(absBool: AbsBoolUtil): AbsBool =
      this match {
        case DefaultStrNum => absBool.Top
        case DefaultStrOther => absBool.Top
        case DefaultStrSet(vs) => that.gammaSingle match {
          case ConSingleTop() => absBool.Top
          case ConSingleBot() => absBool.Bot
          case ConSingleCon(s) => vs.foldLeft[AbsBool](absBool.Bot)((result, v) => {
            result + absBool.alpha(v.contains(s))
          })
        }
        case DefaultStrTop => absBool.Top
        case DefaultStrBot => absBool.Bot
      }

    def length(absNumber: AbsNumberUtil): AbsNumber =
      this match {
        case DefaultStrNum => absNumber.UInt
        case DefaultStrOther => absNumber.UInt
        case DefaultStrSet(vs) => vs.foldLeft[AbsNumber](absNumber.Bot)((result, v) => result + absNumber.alpha(v.length))
        case DefaultStrTop => absNumber.UInt
        case DefaultStrBot => absNumber.Bot
      }

    def toLowerCase: AbsString =
      this match {
        case DefaultStrNum => DefaultStrTop
        case DefaultStrOther => DefaultStrOther
        case DefaultStrSet(vs) => vs.foldLeft[AbsString](DefaultStrBot)((result, v) => result + alpha(v.toLowerCase))
        case DefaultStrTop => DefaultStrTop
        case DefaultStrBot => DefaultStrBot
      }

    def toUpperCase: AbsString =
      this match {
        case DefaultStrNum => DefaultStrTop
        case DefaultStrOther => DefaultStrOther
        case DefaultStrSet(vs) => vs.foldLeft[AbsString](DefaultStrBot)((result, v) => result + alpha(v.toUpperCase))
        case DefaultStrTop => DefaultStrTop
        case DefaultStrBot => DefaultStrBot
      }

    def isAllNums: Boolean =
      this match {
        case DefaultStrNum => true
        case DefaultStrSet(v) => hasNum(v) && !hasOther(v)
        case _ => false
      }

    def isAllOthers: Boolean =
      this match {
        case DefaultStrOther => true
        case DefaultStrSet(v) => !hasNum(v) && hasOther(v)
        case _ => false
      }
  }

  case object DefaultStrTop extends DefaultStringSet {
    val gamma: ConSet[String] = ConSetTop()
    val gammaSingle: ConSingle[String] = ConSingleTop()
    val gammaIsAllNums: ConSingle[Boolean] = ConSingleTop()
    override val toString: String = "String"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.Top
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.Top
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.Top
    def isArrayIndex(absBool: AbsBoolUtil): AbsBool = absBool.Top
  }

  case object DefaultStrBot extends DefaultStringSet {
    val gamma: ConSet[String] = ConSetBot()
    val gammaSingle: ConSingle[String] = ConSingleBot()
    val gammaIsAllNums: ConSingle[Boolean] = ConSingleBot()
    override val gammaSimple: ConSimple = ConSimpleBot
    override val toString: String = "Bot"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.Bot
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.Bot
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.Bot
    def isArrayIndex(absBool: AbsBoolUtil): AbsBool = absBool.Bot
  }

  case object DefaultStrNum extends DefaultStringSet {
    val gamma: ConSet[String] = ConSetTop()
    val gammaSingle: ConSingle[String] = ConSingleTop()
    val gammaIsAllNums: ConSingle[Boolean] = ConSingleCon(true)
    override val toString: String = "NumStr"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.NumStr
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.True
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.Top
    def isArrayIndex(absBool: AbsBoolUtil): AbsBool = absBool.Top
  }

  case object DefaultStrOther extends DefaultStringSet {
    val gamma: ConSet[String] = ConSetTop()
    val gammaSingle: ConSingle[String] = ConSingleTop()
    val gammaIsAllNums: ConSingle[Boolean] = ConSingleCon(false)
    override val toString: String = "OtherStr"
    def toAbsString(absString: AbsStringUtil): AbsString = absString.OtherStr
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = absBool.Top
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = absNumber.NaN
    def isArrayIndex(absBool: AbsBoolUtil): AbsBool = absBool.False
  }

  case class DefaultStrSet(values: Set[String]) extends DefaultStringSet {
    def gamma: ConSet[String] = values.size match {
      case 0 => ConSetBot()
      case _ => ConSetCon(values)
    }
    def gammaSingle: ConSingle[String] = values.size match {
      case 0 => ConSingleBot()
      case 1 => ConSingleCon(values.head)
      case _ => ConSingleTop()
    }
    def gammaIsAllNums: ConSingle[Boolean] = values.size match {
      case 0 => ConSingleBot()
      case _ if !hasOther(values) => ConSingleCon(true)
      case _ if !hasNum(values) => ConSingleCon(false)
      case _ => ConSingleTop()
    }
    override def toString: String = values.map("\"" + _ + "\"").mkString(", ")
    def toAbsString(absString: AbsStringUtil): AbsString = absString.alpha(values)
    def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = values contains "" match {
      case true if values.size == 1 => absBool.False
      case true => absBool.Top
      case false if values.size == 0 => absBool.Bot
      case false => absBool.True
    }
    def toAbsNumber(absNumber: AbsNumberUtil): AbsNumber = values.foldLeft(absNumber.Bot)((tmpAbsNum, str) => {
      val absNum = str.trim match {
        case "" => absNumber.alpha(0)
        case s if isHex(s) => absNumber.alpha((s + "p0").toDouble)
        case s => Try(absNumber.alpha(s.toDouble)).getOrElse(absNumber.NaN)
      }
      absNum + tmpAbsNum
    })

    def isArrayIndex(absBool: AbsBoolUtil): AbsBool = {
      val upper = scala.math.pow(2, 32) - 1
      values.foldLeft(absBool.Bot)((res, v) => {
        res + absBool.alpha({
          isNum(v) && {
            val num = v.toDouble
            0 <= num && num < upper
          }
        })
      })
    }
  }
}
