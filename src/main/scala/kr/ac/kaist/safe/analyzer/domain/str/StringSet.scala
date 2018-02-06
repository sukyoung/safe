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

package kr.ac.kaist.safe.analyzer.domain

import kr.ac.kaist.safe.errors.error.{ AbsStrParseError }
import scala.collection.immutable.HashSet
import scala.util.Try
import spray.json._

// string set domain with max set size
case class StringSet(maxSetSize: Int) extends StrDomain {
  case object Top extends Elem
  case object Number extends Elem
  case object Other extends Elem
  case class StrSet(values: Set[String]) extends Elem
  object StrSet {
    def apply(seq: String*): StrSet = StrSet(seq.toSet)
  }
  lazy val Bot: Elem = StrSet()

  def alpha(str: Str): Elem = StrSet(str)

  override def alpha(values: Set[Str]): Elem = {
    val strSet = values.map(_.str)
    if (maxSetSize == 0 | strSet.size <= maxSetSize)
      StrSet(strSet)
    else if (hasNum(strSet)) {
      if (hasOther(strSet)) Top
      else Number
    } else if (hasOther(strSet)) Other
    else Top
  }

  def fromJson(v: JsValue): Elem = v match {
    case JsString("⊤") => Top
    case JsString("number") => Number
    case JsString("other") => Other
    case _ => StrSet(json2set(v, json2str))
  }

  sealed abstract class Elem extends ElemTrait {
    def gamma: ConSet[Str] = this match {
      case StrSet(set) => ConFin(set.map(Str(_)))
      case Top | Number | Other => ConInf
    }

    def getSingle: ConSingle[Str] = this match {
      case StrSet(set) if set.size == 0 => ConZero()
      case StrSet(set) if set.size == 1 => ConOne(set.head)
      case _ => ConMany()
    }

    def isNum: AbsBool = this match {
      case StrSet(set) if set.size == 0 => AbsBool.Bot
      case StrSet(set) if !hasOther(set) => AbsBool.True
      case Number => AbsBool.True
      case StrSet(set) if !hasNum(set) => AbsBool.False
      case Other => AbsBool.False
      case _ => AbsBool.Top
    }

    override def toString: String = this match {
      case StrSet(set) if set.size == 0 => "⊥(string)"
      case Top => "Top(string)"
      case Number => "Number"
      case Other => "Other"
      case StrSet(set) => set.map("\"" + _ + "\"").mkString(", ")
    }

    def ToBoolean: AbsBool = this match {
      case StrSet(set) => set contains "" match {
        case true if set.size == 1 => AbsBool.False
        case true => AbsBool.Top
        case false if set.size == 0 => AbsBool.Bot
        case false => AbsBool.True
      }
      case Number => AbsBool.True
      case Top | Other => AbsBool.Top
    }

    def ToNumber: AbsNum = this match {
      case Top => AbsNum.Top
      case Number => AbsNum.Top
      case Other => AbsNum.NaN
      case StrSet(set) => set.foldLeft(AbsNum.Bot)((tmpAbsNum, str) => {
        val absNum = str.trim match {
          case "" => AbsNum(0)
          case s if isHex(s) => AbsNum((s + "p0").toDouble)
          case s => Try(AbsNum(s.toDouble)).getOrElse(AbsNum.NaN)
        }
        absNum ⊔ tmpAbsNum
      })
    }

    def ⊑(that: Elem): Boolean = (this, that) match {
      case (Bot, _) => true
      case (_, Top) => true
      //      case (StrSet(v1), StrSet(v2)) => (!a.hasNum || b.hasNum) && (!a.hasOther || b.hasOther) && a.values.subsetOf(b.values)
      case (StrSet(v1), StrSet(v2)) if v1.subsetOf(v2) => true
      case (StrSet(v1), StrSet(v2)) => false
      case (StrSet(v), Number) => hasNum(v) && !hasOther(v)
      case (StrSet(v), Other) => !hasNum(v) && hasOther(v)
      case (Number, Number) => true
      case (Other, Other) => true
      case _ => false
    }

    def ⊔(that: Elem): Elem = (this, that) match {
      case (StrSet(v1), StrSet(v2)) if v1 == v2 => this
      case (a: StrSet, b: StrSet) => alpha(a.values ++ b.values)
      case _ =>
        (this ⊑ that, that ⊑ this) match {
          case (true, _) => that
          case (_, true) => this
          case _ => Top
        }
    }

    def ⊓(that: Elem): Elem = (this, that) match {
      case (StrSet(v1), StrSet(v2)) => alpha(v1 intersect v2)

      case (StrSet(v), Number) if hasNum(v) =>
        alpha(v.filter((s: String) => isNumber(s)))
      case (Number, StrSet(v)) if hasNum(v) =>
        alpha(v.filter((s: String) => isNumber(s)))
      case (StrSet(_), Number)
        | (Number, StrSet(_)) => Bot

      case (StrSet(v), Other) if hasOther(v) =>
        alpha(v.filter((s: String) => !isNumber(s)))
      case (Other, StrSet(v)) if hasOther(v) =>
        alpha(v.filter((s: String) => !isNumber(s)))
      case (StrSet(_), Other)
        | (Other, StrSet(_)) => Bot

      case (Number, Number) => Number
      case (Number, Other)
        | (Other, Number) => Bot
      case (Other, Other) => Other

      case _ =>
        (this ⊑ that, that ⊑ this) match {
          case (true, _) => this
          case (_, true) => that
          case _ => Bot
        }
    }

    def StrictEquals(that: Elem): AbsBool =
      (this.getSingle, that.getSingle) match {
        case (ConOne(s1), ConOne(s2)) => AbsBool(s1 == s2)
        case (ConZero(), _) | (_, ConZero()) => AbsBool.Bot
        case _ => (this ⊑ that, that ⊑ this) match {
          case (false, false) => AbsBool.False
          case _ => AbsBool.Top
        }
      }

    def <(that: Elem): AbsBool = (this, that) match {
      case (Bot, _) | (_, Bot) => AbsBool.Bot
      case (StrSet(leftStrSet), StrSet(rightStrSet)) =>
        leftStrSet.foldLeft(AbsBool.Bot)((r1, x) => {
          r1 ⊔ rightStrSet.foldLeft(AbsBool.Bot)((r2, y) => r2 ⊔ AbsBool(x < y))
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

    def trim: Elem =
      this match {
        case StrSet(vs) =>
          vs.foldLeft[Elem](Bot)((r: Elem, s: String) => r ⊔ alpha(s.trim))
        case Number => Number
        case Other => Other ⊔ Number
        case Bot => Bot
        case _ => Top
      }

    def concat(that: Elem): Elem = (this, that) match {
      case (StrSet(v1), StrSet(v2)) if (maxSetSize == 0 || v1.size * v2.size <= maxSetSize) => {
        val set = v1.foldLeft(HashSet[String]())((hs1, s1) =>
          v2.foldLeft(hs1)((hs2, s2) => hs2 + (s1 + s2)))
        alpha(set)
      }
      case (StrSet(v1), StrSet(v2)) =>
        (hasNum(v1) | hasNum(v2), hasOther(v1) | hasOther(v2)) match {
          case (true, true) => Top
          case (true, false) => Number
          case (false, true) => Other
          case (false, false) => Bot
        }
      case (Bot, _) => Bot
      case (_, Bot) => Bot
      case _ => Top
    }

    def charAt(pos: AbsNum): Elem = (gamma, pos.gamma) match {
      case (ConFin(vs), ConFin(ds)) => {
        ds.foldLeft[Elem](Bot) {
          case (res, Num(d)) => vs.foldLeft(res) {
            case (res, Str(str)) =>
              if (d >= str.length || d < 0)
                res ⊔ alpha("")
              else {
                val i = d.toInt
                res ⊔ alpha(str.substring(i, i + 1))
              }
          }
        }
      }
      case _ => Top
    }

    def charCodeAt(pos: AbsNum): AbsNum = gamma match {
      case ConInf => AbsNum.UInt
      case ConFin(vs) => pos.getSingle match {
        case ConOne(d) =>
          vs.foldLeft[AbsNum](AbsNum.Bot)((r, s) => {
            if (d >= s.length || d < 0)
              r ⊔ AbsNum.NaN
            else {
              val i = d.toInt
              r ⊔ AbsNum(s.substring(i, i + 1).head.toInt)
            }
          })
        case _ => AbsNum.UInt
      }
    }

    def contains(that: Elem): AbsBool =
      this match {
        case Number => AbsBool.Top
        case Other => AbsBool.Top
        case StrSet(vs) => that.getSingle match {
          case ConMany() => AbsBool.Top
          case ConZero() => AbsBool.Bot
          case ConOne(s) => vs.foldLeft[AbsBool](AbsBool.Bot)((result, v) => {
            result ⊔ AbsBool(v.contains(s))
          })
        }
        case Top => AbsBool.Top
        case Bot => AbsBool.Bot
      }

    def length: AbsNum =
      this match {
        case Number => AbsNum.UInt
        case Other => AbsNum.UInt
        case StrSet(vs) => vs.foldLeft[AbsNum](AbsNum.Bot)((result, v) => result ⊔ AbsNum(v.length))
        case Top => AbsNum.UInt
        case Bot => AbsNum.Bot
      }

    def toLowerCase: Elem =
      this match {
        case Number => Top
        case Other => Other
        case StrSet(vs) => vs.foldLeft[Elem](Bot)((result, v) => result ⊔ alpha(v.toLowerCase))
        case Top => Top
        case Bot => Bot
      }

    def toUpperCase: Elem =
      this match {
        case Number => Top
        case Other => Other
        case StrSet(vs) => vs.foldLeft[Elem](Bot)((result, v) => result ⊔ alpha(v.toUpperCase))
        case Top => Top
        case Bot => Bot
      }

    def isArrayIndex: AbsBool = this match {
      case Top | Number => AbsBool.Top
      case Other => AbsBool.False
      case StrSet(set) => {
        val upper = scala.math.pow(2, 32) - 1
        set.foldLeft(AbsBool.Bot)((res, v) => {
          res ⊔ AbsBool({
            isNumber(v) && {
              val num = v.toDouble
              num % 1 == 0 && 0 <= num && num < upper
            }
          })
        })
      }
    }

    // gamma(this) contains str
    def isRelated(str: String): Boolean = this match {
      case Top => true
      case Bot => false
      case Number => isNumber(str)
      case Other => !isNumber(str)
      case StrSet(v) => v contains str
    }

    def toJson: JsValue = this match {
      case Top => JsString("⊤")
      case Number => JsString("number")
      case Other => JsString("other")
      case StrSet(set) => JsArray(set.toSeq.map(JsString(_)): _*)
    }
  }

  def hasNum(values: Set[String]): Boolean =
    values.foldLeft(false)((b: Boolean, v: String) => b | isNumber(v))
  def hasOther(values: Set[String]): Boolean =
    values.foldLeft(false)((b: Boolean, v: String) => b | !isNumber(v))

  def fromCharCode(n: AbsNum): Elem = {
    n.gamma match {
      case ConInf => Top
      case ConFin(vs) =>
        vs.foldLeft[Elem](Bot)((r, v) => {
          r ⊔ alpha("%c".format(v.toInt))
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
