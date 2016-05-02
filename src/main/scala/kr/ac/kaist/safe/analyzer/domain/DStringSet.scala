/**
 * *****************************************************************************
 * Copyright (c) 2012-2013, S-Core, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.domain

import scala.collection.immutable.{ HashSet => IHashSet }

class DStrSetUtil(maxSetSize: Int) extends AbsStringUtil {
  val Top: AbsString = DStrTop
  val Bot: AbsString = DStrBot
  val NumStr: AbsString = DStrNum
  val OtherStr: AbsString = DStrOther

  /* regexp, number string */
  private val hex = "(0[xX][0-9a-fA-F]+)".r.pattern
  private val exp = "[eE][+-]?[0-9]+"
  private val dec1 = "[0-9]+\\.[0-9]*(" + exp + ")?"
  private val dec2 = "\\.[0-9]+(" + exp + ")?"
  private val dec3 = "[0-9]+(" + exp + ")?"
  private val dec = "([+-]?(Infinity|(" + dec1 + ")|(" + dec2 + ")|(" + dec3 + ")))"
  private val num_regexp = ("NaN|(" + hex + ")|(" + dec + ")").r.pattern

  // an abstract value which stands for natural numbers(0, 1, 2, ...)
  //  val NumTop = NumStr

  def isHex(str: String): Boolean =
    hex.matcher(str).matches()

  def isNum(str: String): Boolean =
    num_regexp.matcher(str).matches()

  def alpha(str: String): AbsString =
    if (str == null) DStrBot
    else DStrSet(IHashSet(str))

  def alpha(values: IHashSet[String]): AbsString =
    if (values.isEmpty)
      DStrBot
    else if (maxSetSize == 0 | values.size <= maxSetSize)
      DStrSet(values)
    else if (hasNum(values)) {
      if (hasOther(values)) DStrTop
      else DStrNum
    } else if (hasOther(values)) OtherStr
    else DStrTop

  def hasNum(values: IHashSet[String]): Boolean =
    values.foldLeft(false)((b: Boolean, v: String) => b | isNum(v))
  def hasOther(values: IHashSet[String]): Boolean =
    values.foldLeft(false)((b: Boolean, v: String) => b | !isNum(v))

  def fromCharCode(n: AbsNumber, absNumber: AbsNumberUtil): AbsString = {
    if (n </ absNumber.Bot) {
      n.gammaOpt match {
        case Some(vs) =>
          vs.foldLeft[AbsString](DStrBot)((r, v) => {
            r + alpha("%c".format(v.toInt))
          })
        case None => DStrTop
      }
    } else {
      DStrBot
    }
  }

  sealed abstract class DStringSet(maxSetSize: Int = 0) extends AbsString {

    def getAbsCase: AbsCase =
      this match {

        case DStrTop => AbsTop
        case DStrBot => AbsBot
        case DStrNum => AbsMulti
        case DStrOther => AbsMulti
        case DStrSet(v) => if (v.size == 1) AbsSingle else AbsMulti
      }

    def getSingle: Option[String] =
      this match {
        case DStrTop => None
        case DStrBot => None
        case DStrNum => None
        case DStrOther => None
        case DStrSet(v) => if (v.size == 1) Some(v.head) else None
      }

    def gammaOpt: Option[Set[String]] =
      this match {
        case DStrTop => None
        case DStrBot => None
        case DStrNum => None
        case DStrOther => None
        case DStrSet(v) => Some(v)
      }

    /* partial order */
    def <=(that: AbsString): Boolean =
      (this, that) match {
        case (DStrBot, _) => true
        case (_, DStrTop) => true
        //      case (DStrSet(v1), DStrSet(v2)) => (!a.hasNum || b.hasNum) && (!a.hasOther || b.hasOther) && a.values.subsetOf(b.values)
        case (DStrSet(v1), DStrSet(v2)) if v1.subsetOf(v2) => true
        case (DStrSet(v1), DStrSet(v2)) => false
        case (DStrSet(v), DStrNum) => hasNum(v) && !hasOther(v)
        case (DStrSet(v), DStrOther) => !hasNum(v) && hasOther(v)
        case (DStrNum, DStrNum) => true
        case (DStrOther, DStrOther) => true
        case _ => false
      }

    /* join */
    def +(that: AbsString): AbsString =
      (this, that) match {
        case (DStrSet(v1), DStrSet(v2)) if v1 == v2 => this
        case (a: DStrSet, b: DStrSet) => alpha(a.values ++ b.values)
        case _ =>
          (this <= that, that <= this) match {
            case (true, _) => that
            case (_, true) => this
            case _ => DStrTop
          }
      }

    /* meet */
    def <>(that: AbsString): AbsString =
      (this, that) match {
        case (DStrSet(v1), DStrSet(v2)) => alpha(v1 intersect v2)

        case (DStrSet(v), DStrNum) if hasNum(v) =>
          alpha(v.filter((s: String) => isNum(s)))
        case (DStrNum, DStrSet(v)) if hasNum(v) =>
          alpha(v.filter((s: String) => isNum(s)))
        case (DStrSet(_), DStrNum)
          | (DStrNum, DStrSet(_)) => DStrBot

        case (DStrSet(v), DStrOther) if hasOther(v) =>
          alpha(v.filter((s: String) => !isNum(s)))
        case (DStrOther, DStrSet(v)) if hasOther(v) =>
          alpha(v.filter((s: String) => !isNum(s)))
        case (DStrSet(_), DStrOther)
          | (DStrOther, DStrSet(_)) => DStrBot

        case (DStrNum, DStrNum) => DStrNum
        case (DStrNum, DStrOther)
          | (DStrOther, DStrNum) => DStrBot
        case (DStrOther, DStrOther) => DStrOther

        case _ =>
          (this <= that, that <= this) match {
            case (true, _) => this
            case (_, true) => that
            case _ => DStrBot
          }
      }

    /* abstract operator 'equal to' */
    def ===(that: AbsString, absBool: AbsBoolUtil): AbsBool =
      (this.getSingle, that.getSingle) match {
        case (Some(s1), Some(s2)) =>
          absBool.alpha(s1 == s2)
        case _ => {
          if (this <= DStrBot || that <= DStrBot) absBool.Bot
          else {
            (this <= that, that <= this) match {
              case (false, false) => absBool.False
              case _ => absBool.Top
            }
          }
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
        case DStrSet(vs) =>
          vs.foldLeft[AbsString](DStrBot)((r: AbsString, s: String) => r + alpha(s.trim))
        case DStrNum => DStrNum
        case DStrOther => DStrOther + DStrNum
        case DStrBot => DStrBot
        case _ => DStrTop
      }

    def concat(that: AbsString): AbsString =
      (this, that) match {
        case (DStrSet(v1), DStrSet(v2)) if v1.size * v2.size <= maxSetSize => {
          val set = v1.foldLeft(IHashSet[String]())((hs1: IHashSet[String], s1: String) =>
            v2.foldLeft(hs1)((hs2: IHashSet[String], s2: String) => hs2 + (s1 + s2)))
          alpha(set)
        }
        case (DStrSet(v1), DStrSet(v2)) =>
          (hasNum(v1) | hasNum(v2), hasOther(v1) | hasOther(v2)) match {
            case (true, true) => DStrTop
            case (true, false) => DStrNum
            case (false, true) => DStrOther
            case (false, false) => DStrBot
          }
        case (DStrBot, _) => DStrBot
        case (_, DStrBot) => DStrBot
        case _ => DStrTop
      }

    def charAt(pos: AbsNumber): AbsString = {
      this.gammaOpt match {
        case Some(vs) => {
          pos.getUIntSingle match {
            case Some(d) => {
              vs.foldLeft[AbsString](DStrBot)((r, s) => {
                if (d >= s.length || d < 0)
                  r + alpha("")
                else {
                  val i = d.toInt
                  r + alpha(s.substring(i, i + 1))
                }
              })
            }
            case _ => DStrTop
          }
        }
        case _ => DStrTop
      }
    }

    def charCodeAt(pos: AbsNumber, absNumber: AbsNumberUtil): AbsNumber = {
      if (this.gammaOpt.isDefined) {
        val vs: Set[String] = this.gammaOpt.get
        pos.getUIntSingle match {
          case Some(d) => {
            vs.foldLeft[AbsNumber](absNumber.Bot)((r, s) => {
              if (d >= s.length || d < 0)
                r + absNumber.NaN
              else {
                val i = d.toInt
                r + absNumber.alpha(s.substring(i, i + 1).head.toInt)
              }
            })
          }
          case _ => absNumber.UInt
        }
      } else absNumber.UInt
    }

    def contains(that: AbsString, absBool: AbsBoolUtil): AbsBool =
      this match {
        case DStrNum => absBool.Top
        case DStrOther => absBool.Top
        case DStrSet(vs) =>
          that.getSingle match {
            case Some(_s) => vs.foldLeft[AbsBool](absBool.Bot)((result, v) => result + absBool.alpha(v.contains(_s)))
            case None =>
              if (that </ DStrBot)
                absBool.Top
              else
                absBool.Bot
          }
        case DStrTop => absBool.Top
        case DStrBot => absBool.Bot
      }

    def length(absNumber: AbsNumberUtil): AbsNumber =
      this match {
        case DStrNum => absNumber.UInt
        case DStrOther => absNumber.UInt
        case DStrSet(vs) => vs.foldLeft[AbsNumber](absNumber.Bot)((result, v) => result + absNumber.alpha(v.length))
        case DStrTop => absNumber.UInt
        case DStrBot => absNumber.Bot
      }

    def toLowerCase: AbsString =
      this match {
        case DStrNum => DStrTop
        case DStrOther => DStrOther
        case DStrSet(vs) => vs.foldLeft[AbsString](DStrBot)((result, v) => result + alpha(v.toLowerCase))
        case DStrTop => DStrTop
        case DStrBot => DStrBot
      }

    def toUpperCase: AbsString =
      this match {
        case DStrNum => DStrTop
        case DStrOther => DStrOther
        case DStrSet(vs) => vs.foldLeft[AbsString](DStrBot)((result, v) => result + alpha(v.toUpperCase))
        case DStrTop => DStrTop
        case DStrBot => DStrBot
      }

    override def toString: String =
      this match {
        case DStrNum => "NumStr"
        case DStrOther => "OtherStr"
        case DStrSet(vs) => vs.foldLeft("")((result, v) => if (result.length == 0) "\"" + v + "\"" else result + ", \"" + v + "\"")
        case DStrTop => "String"
        case DStrBot => "Bot"
      }

    def isTop: Boolean = this == DStrTop
    def isBottom: Boolean = this == DStrBot

    def isConcrete: Boolean =
      this match {
        case DStrSet(_) => true
        case _ => false
      }

    def toAbsString(absString: AbsStringUtil): AbsString =
      this match {
        case DStrSet(_) => this
        case _ => DStrBot
      }

    def isAllNums: Boolean =
      this match {
        case DStrNum => true
        case DStrSet(v) => hasNum(v) && !hasOther(v)
        case _ => false
      }

    def isAllOthers: Boolean =
      this match {
        case DStrOther => true
        case DStrSet(v) => !hasNum(v) && hasOther(v)
        case _ => false
      }
  }
  case object DStrTop extends DStringSet
  case object DStrBot extends DStringSet
  case object DStrNum extends DStringSet
  case object DStrOther extends DStringSet
  case class DStrSet(values: IHashSet[String]) extends DStringSet
}
