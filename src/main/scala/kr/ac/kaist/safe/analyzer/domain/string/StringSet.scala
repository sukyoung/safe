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

  def alpha(str: Str): AbsString = StrSet(str)

  override def alpha(values: Set[Str]): AbsString = {
    val strSet = values.map(_.str)
    if (maxSetSize == 0 | strSet.size <= maxSetSize)
      StrSet(strSet)
    else if (hasNum(strSet)) {
      if (hasNotNumber(strSet)) Top
      else Number
    } else if (hasNotNumber(strSet)) NotNumber
    else Top
  }

  sealed abstract class AbsDom(maxSetSize: Int = 0) extends AbsString {
    def gamma: ConSet[Str] = this match {
      case StrSet(set) => ConFin(set)
      case Top | Number | NotNumber => ConInf()
    }

    def isBottom: Boolean = this == Bot

    def getSingle: ConSingle[Str] = this match {
      case StrSet(set) if set.size == 0 => ConZero()
      case StrSet(set) if set.size == 1 => ConOne(set.head)
      case _ => ConMany()
    }

    def isNum: AbsBool = this match {
      case StrSet(set) if set.size == 0 => AbsBool.Bot
      case StrSet(set) if !hasNotNumber(set) => AbsBool.True
      case Number => AbsBool.True
      case StrSet(set) if !hasNum(set) => AbsBool.False
      case NotNumber => AbsBool.False
      case _ => AbsBool.Top
    }

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
          case "" => AbsNumber(0)
          case s if isHex(s) => AbsNumber((s + "p0").toDouble)
          case s => Try(AbsNumber(s.toDouble)).getOrElse(AbsNumber.NaN)
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
        alpha(v.filter((s: String) => isNumber(s)))
      case (Number, StrSet(v)) if hasNum(v) =>
        alpha(v.filter((s: String) => isNumber(s)))
      case (StrSet(_), Number)
        | (Number, StrSet(_)) => Bot

      case (StrSet(v), NotNumber) if hasNotNumber(v) =>
        alpha(v.filter((s: String) => !isNumber(s)))
      case (NotNumber, StrSet(v)) if hasNotNumber(v) =>
        alpha(v.filter((s: String) => !isNumber(s)))
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
      (this.getSingle, that.getSingle) match {
        case (ConOne(s1), ConOne(s2)) => AbsBool(s1 == s2)
        case (ConZero(), _) | (_, ConZero()) => AbsBool.Bot
        case _ => (this <= that, that <= this) match {
          case (false, false) => AbsBool.False
          case _ => AbsBool.Top
        }
      }

    def <(that: AbsString): AbsBool = (this, check(that)) match {
      case (Bot, _) | (_, Bot) => AbsBool.Bot
      case (StrSet(leftStrSet), StrSet(rightStrSet)) =>
        leftStrSet.foldLeft(AbsBool.Bot)((r1, x) => {
          r1 + rightStrSet.foldLeft(AbsBool.Bot)((r2, y) => r2 + AbsBool(x < y))
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

    def charAt(pos: AbsNumber): AbsString = (gamma, pos.gamma) match {
      case (ConFin(vs), ConFin(ds)) => {
        ds.foldLeft[AbsString](Bot) {
          case (res, d) => vs.foldLeft(res) {
            case (res, str) =>
              if (d >= str.length || d < 0)
                res + alpha("")
              else {
                val i = d.toInt
                res + alpha(str.substring(i, i + 1))
              }
          }
        }
      }
      case _ => Top
    }

    def charCodeAt(pos: AbsNumber): AbsNumber = gamma match {
      case ConInf() => AbsNumber.UInt
      case ConFin(vs) => pos.getSingle match {
        case ConOne(d) =>
          vs.foldLeft[AbsNumber](AbsNumber.Bot)((r, s) => {
            if (d >= s.length || d < 0)
              r + AbsNumber.NaN
            else {
              val i = d.toInt
              r + AbsNumber(s.substring(i, i + 1).head.toInt)
            }
          })
        case _ => AbsNumber.UInt
      }
    }

    def contains(that: AbsString): AbsBool =
      this match {
        case Number => AbsBool.Top
        case NotNumber => AbsBool.Top
        case StrSet(vs) => that.getSingle match {
          case ConMany() => AbsBool.Top
          case ConZero() => AbsBool.Bot
          case ConOne(s) => vs.foldLeft[AbsBool](AbsBool.Bot)((result, v) => {
            result + AbsBool(v.contains(s))
          })
        }
        case Top => AbsBool.Top
        case Bot => AbsBool.Bot
      }

    def length: AbsNumber =
      this match {
        case Number => AbsNumber.UInt
        case NotNumber => AbsNumber.UInt
        case StrSet(vs) => vs.foldLeft[AbsNumber](AbsNumber.Bot)((result, v) => result + AbsNumber(v.length))
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
          res + AbsBool({
            isNumber(v) && {
              val num = v.toDouble
              0 <= num && num < upper
            }
          })
        })
      }
    }
  }

  def hasNum(values: Set[String]): Boolean =
    values.foldLeft(false)((b: Boolean, v: String) => b | isNumber(v))
  def hasNotNumber(values: Set[String]): Boolean =
    values.foldLeft(false)((b: Boolean, v: String) => b | !isNumber(v))

  def fromCharCode(n: AbsNumber): AbsString = {
    n.gamma match {
      case ConInf() => Top
      case ConFin(vs) =>
        vs.foldLeft[AbsString](Bot)((r, v) => {
          r + alpha("%c".format(v.toInt))
        })
    }
  }

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

  private def isHex(str: String): Boolean =
    hex.matcher(str).matches()

  private def isNumber(str: String): Boolean =
    numRegexp.matcher(str).matches()
}
