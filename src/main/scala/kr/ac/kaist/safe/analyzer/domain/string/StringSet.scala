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

import kr.ac.kaist.safe.analyzer.domain.Utils._
import scala.collection.immutable.HashSet
import scala.util.Try

// string set domain with max set size
case class StringSet(maxSetSize: Int) extends AbsStringUtil {
  case object Top extends AbsDom
  case object Number extends AbsDom
  case object NotNumber extends AbsDom
  case class StrSet(values: Set[String]) extends AbsDom
  object StrSet {
    def apply(seq: String*): StrSet = StrSet(seq.toSet)
  }
  val Bot: AbsDom = StrSet()

  def alpha(str: String): AbsString = StrSet(str)

  override def alpha(values: Set[String]): AbsString =
    if (maxSetSize == 0 | values.size <= maxSetSize)
      StrSet(values)
    else if (hasNum(values)) {
      if (hasNotNumber(values)) Top
      else Number
    } else if (hasNotNumber(values)) NotNumber
    else Top

  sealed abstract class AbsDom(maxSetSize: Int = 0) extends AbsString {
    def gamma: ConSet[String] = this match {
      case StrSet(set) if set.size == 0 => ConSetBot()
      case StrSet(set) => ConSetCon(set)
      case Top | Number | NotNumber => ConSetTop()
    }

    def gammaSingle: ConSingle[String] = this match {
      case StrSet(set) if set.size == 0 => ConSingleBot()
      case StrSet(set) if set.size == 1 => ConSingleCon(set.head)
      case _ => ConSingleTop()
    }

    def gammaSimple: ConSimple[String] = this match {
      case StrSet(set) if set.size == 0 => ConSimpleBot()
      case _ => ConSimpleTop()
    }

    def gammaIsAllNums: ConSingle[Boolean] = this match {
      case StrSet(set) if set.size == 0 => ConSingleBot()
      case StrSet(set) if !hasNotNumber(set) => ConSingleCon(true)
      case Number => ConSingleCon(true)
      case StrSet(set) if !hasNum(set) => ConSingleCon(false)
      case NotNumber => ConSingleCon(false)
      case _ => ConSingleTop()
    }

    def isBottom: Boolean = this == Bot

    override def toString: String = this match {
      case StrSet(set) if set.size == 0 => "Bot"
      case Top => "String"
      case Number => "Number"
      case NotNumber => "NotNumber"
      case StrSet(set) => set.map("\"" + _ + "\"").mkString(", ")
    }

    def toAbsBoolean: AbsBool = this match {
      case StrSet(set) => set contains "" match {
        case true if set.size == 1 => AbsBool.False
        case true => AbsBool.Top
        case false if set.size == 0 => AbsBool.Bot
        case false => AbsBool.True
      }
      case Number => AbsBool.True
      case Top | NotNumber => AbsBool.Top
    }

    def toAbsNumber: AbsNumber = this match {
      case Top => AbsNumber.Top
      case Number => AbsNumber.Top
      case NotNumber => AbsNumber.NaN
      case StrSet(set) => set.foldLeft(AbsNumber.Bot)((tmpAbsNum, str) => {
        val absNum = str.trim match {
          case "" => AbsNumber.alpha(0)
          case s if isHex(s) => AbsNumber.alpha((s + "p0").toDouble)
          case s => Try(AbsNumber.alpha(s.toDouble)).getOrElse(AbsNumber.NaN)
        }
        absNum + tmpAbsNum
      })
    }

    def <=(that: AbsString): Boolean = (this, check(that)) match {
      case (Bot, _) => true
      case (_, Top) => true
      //      case (StrSet(v1), StrSet(v2)) => (!a.hasNum || b.hasNum) && (!a.hasNotNumber || b.hasNotNumber) && a.values.subsetOf(b.values)
      case (StrSet(v1), StrSet(v2)) if v1.subsetOf(v2) => true
      case (StrSet(v1), StrSet(v2)) => false
      case (StrSet(v), Number) => hasNum(v) && !hasNotNumber(v)
      case (StrSet(v), NotNumber) => !hasNum(v) && hasNotNumber(v)
      case (Number, Number) => true
      case (NotNumber, NotNumber) => true
      case _ => false
    }

    def +(that: AbsString): AbsString = (this, check(that)) match {
      case (StrSet(v1), StrSet(v2)) if v1 == v2 => this
      case (a: StrSet, b: StrSet) => alpha(a.values ++ b.values)
      case _ =>
        (this <= that, that <= this) match {
          case (true, _) => that
          case (_, true) => this
          case _ => Top
        }
    }

    def <>(that: AbsString): AbsString = (this, check(that)) match {
      case (StrSet(v1), StrSet(v2)) => alpha(v1 intersect v2)

      case (StrSet(v), Number) if hasNum(v) =>
        alpha(v.filter((s: String) => isNum(s)))
      case (Number, StrSet(v)) if hasNum(v) =>
        alpha(v.filter((s: String) => isNum(s)))
      case (StrSet(_), Number)
        | (Number, StrSet(_)) => Bot

      case (StrSet(v), NotNumber) if hasNotNumber(v) =>
        alpha(v.filter((s: String) => !isNum(s)))
      case (NotNumber, StrSet(v)) if hasNotNumber(v) =>
        alpha(v.filter((s: String) => !isNum(s)))
      case (StrSet(_), NotNumber)
        | (NotNumber, StrSet(_)) => Bot

      case (Number, Number) => Number
      case (Number, NotNumber)
        | (NotNumber, Number) => Bot
      case (NotNumber, NotNumber) => NotNumber

      case _ =>
        (this <= that, that <= this) match {
          case (true, _) => this
          case (_, true) => that
          case _ => Bot
        }
    }

    def ===(that: AbsString): AbsBool =
      (this.gammaSingle, that.gammaSingle) match {
        case (ConSingleCon(s1), ConSingleCon(s2)) => AbsBool.alpha(s1 == s2)
        case (ConSingleBot(), _) | (_, ConSingleBot()) => AbsBool.Bot
        case _ => (this <= that, that <= this) match {
          case (false, false) => AbsBool.False
          case _ => AbsBool.Top
        }
      }

    def <(that: AbsString): AbsBool = (this, check(that)) match {
      case (Bot, _) | (_, Bot) => AbsBool.Bot
      case (StrSet(leftStrSet), StrSet(rightStrSet)) =>
        leftStrSet.foldLeft(AbsBool.Bot)((r1, x) => {
          r1 + rightStrSet.foldLeft(AbsBool.Bot)((r2, y) => r2 + AbsBool.alpha(x < y))
        })
      case _ => AbsBool.Top
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
        case StrSet(vs) =>
          vs.foldLeft[AbsString](Bot)((r: AbsString, s: String) => r + alpha(s.trim))
        case Number => Number
        case NotNumber => NotNumber + Number
        case Bot => Bot
        case _ => Top
      }

    def concat(that: AbsString): AbsString = (this, check(that)) match {
      case (StrSet(v1), StrSet(v2)) if v1.size * v2.size <= maxSetSize => {
        val set = v1.foldLeft(HashSet[String]())((hs1, s1) =>
          v2.foldLeft(hs1)((hs2, s2) => hs2 + (s1 + s2)))
        alpha(set)
      }
      case (StrSet(v1), StrSet(v2)) =>
        (hasNum(v1) | hasNum(v2), hasNotNumber(v1) | hasNotNumber(v2)) match {
          case (true, true) => Top
          case (true, false) => Number
          case (false, true) => NotNumber
          case (false, false) => Bot
        }
      case (Bot, _) => Bot
      case (_, Bot) => Bot
      case _ => Top
    }

    def charAt(pos: AbsNumber): AbsString = gamma match {
      case ConSetTop() | ConSetBot() => Bot
      case ConSetCon(vs) =>
        pos.gammaSingle match {
          case ConSingleBot() => Top
          case ConSingleCon(d) =>
            vs.foldLeft[AbsString](Bot)((r, s) => {
              if (d >= s.length || d < 0)
                r + alpha("")
              else {
                val i = d.toInt
                r + alpha(s.substring(i, i + 1))
              }
            })
          case ConSingleTop() => Top
        }
    }

    def charCodeAt(pos: AbsNumber): AbsNumber = {
      gamma match {
        case ConSetTop() => AbsNumber.UInt
        case ConSetBot() => AbsNumber.Bot
        case ConSetCon(vs) =>
          pos.gammaSingle match {
            case ConSingleTop() | ConSingleBot() => AbsNumber.UInt
            case ConSingleCon(d) =>
              vs.foldLeft[AbsNumber](AbsNumber.Bot)((r, s) => {
                if (d >= s.length || d < 0)
                  r + AbsNumber.NaN
                else {
                  val i = d.toInt
                  r + AbsNumber.alpha(s.substring(i, i + 1).head.toInt)
                }
              })
          }
      }
    }

    def contains(that: AbsString): AbsBool =
      this match {
        case Number => AbsBool.Top
        case NotNumber => AbsBool.Top
        case StrSet(vs) => that.gammaSingle match {
          case ConSingleTop() => AbsBool.Top
          case ConSingleBot() => AbsBool.Bot
          case ConSingleCon(s) => vs.foldLeft[AbsBool](AbsBool.Bot)((result, v) => {
            result + AbsBool.alpha(v.contains(s))
          })
        }
        case Top => AbsBool.Top
        case Bot => AbsBool.Bot
      }

    def length: AbsNumber =
      this match {
        case Number => AbsNumber.UInt
        case NotNumber => AbsNumber.UInt
        case StrSet(vs) => vs.foldLeft[AbsNumber](AbsNumber.Bot)((result, v) => result + AbsNumber.alpha(v.length))
        case Top => AbsNumber.UInt
        case Bot => AbsNumber.Bot
      }

    def toLowerCase: AbsString =
      this match {
        case Number => Top
        case NotNumber => NotNumber
        case StrSet(vs) => vs.foldLeft[AbsString](Bot)((result, v) => result + alpha(v.toLowerCase))
        case Top => Top
        case Bot => Bot
      }

    def toUpperCase: AbsString =
      this match {
        case Number => Top
        case NotNumber => NotNumber
        case StrSet(vs) => vs.foldLeft[AbsString](Bot)((result, v) => result + alpha(v.toUpperCase))
        case Top => Top
        case Bot => Bot
      }

    def isAllNums: Boolean =
      this match {
        case Number => true
        case StrSet(v) => hasNum(v) && !hasNotNumber(v)
        case _ => false
      }

    def isAllNotNumbers: Boolean =
      this match {
        case NotNumber => true
        case StrSet(v) => !hasNum(v) && hasNotNumber(v)
        case _ => false
      }

    def isArrayIndex: AbsBool = this match {
      case Top | Number => AbsBool.Top
      case NotNumber => AbsBool.False
      case StrSet(set) => {
        val upper = scala.math.pow(2, 32) - 1
        set.foldLeft(AbsBool.Bot)((res, v) => {
          res + AbsBool.alpha({
            isNum(v) && {
              val num = v.toDouble
              0 <= num && num < upper
            }
          })
        })
      }
    }
  }

  def hasNum(values: Set[String]): Boolean =
    values.foldLeft(false)((b: Boolean, v: String) => b | isNum(v))
  def hasNotNumber(values: Set[String]): Boolean =
    values.foldLeft(false)((b: Boolean, v: String) => b | !isNum(v))

  def fromCharCode(n: AbsNumber): AbsString = {
    n.gamma match {
      case ConSetTop() => Top
      case ConSetBot() => Bot
      case ConSetCon(vs) =>
        vs.foldLeft[AbsString](Bot)((r, v) => {
          r + alpha("%c".format(v.toInt))
        })
    }
  }
}
