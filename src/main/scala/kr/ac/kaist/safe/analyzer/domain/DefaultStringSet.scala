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

class DefaultStrSetUtil(maxSetSize: Int) extends AbsStringUtil {
  val Top: AbsString = DefaultStrTop
  val Bot: AbsString = DefaultStrBot
  val NumStr: AbsString = DefaultStrNum
  val OtherStr: AbsString = DefaultStrOther

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
    if (str == null) DefaultStrBot
    else DefaultStrSet(IHashSet(str))

  def alpha(values: IHashSet[String]): AbsString =
    if (values.isEmpty)
      DefaultStrBot
    else if (maxSetSize == 0 | values.size <= maxSetSize)
      DefaultStrSet(values)
    else if (hasNum(values)) {
      if (hasOther(values)) DefaultStrTop
      else DefaultStrNum
    } else if (hasOther(values)) OtherStr
    else DefaultStrTop

  def hasNum(values: IHashSet[String]): Boolean =
    values.foldLeft(false)((b: Boolean, v: String) => b | isNum(v))
  def hasOther(values: IHashSet[String]): Boolean =
    values.foldLeft(false)((b: Boolean, v: String) => b | !isNum(v))

  def fromCharCode(n: AbsNumber, absNumber: AbsNumberUtil): AbsString = {
    if (n </ absNumber.Bot) {
      n.gammaOpt match {
        case Some(vs) =>
          vs.foldLeft[AbsString](DefaultStrBot)((r, v) => {
            r + alpha("%c".format(v.toInt))
          })
        case None => DefaultStrTop
      }
    } else {
      DefaultStrBot
    }
  }

  sealed abstract class DefaultStringSet(maxSetSize: Int = 0) extends AbsString {

    def getAbsCase: AbsCase =
      this match {

        case DefaultStrTop => AbsTop
        case DefaultStrBot => AbsBot
        case DefaultStrNum => AbsMulti
        case DefaultStrOther => AbsMulti
        case DefaultStrSet(v) => if (v.size == 1) AbsSingle else AbsMulti
      }

    def getSingle: Option[String] =
      this match {
        case DefaultStrTop => None
        case DefaultStrBot => None
        case DefaultStrNum => None
        case DefaultStrOther => None
        case DefaultStrSet(v) => if (v.size == 1) Some(v.head) else None
      }

    def gammaOpt: Option[Set[String]] =
      this match {
        case DefaultStrTop => None
        case DefaultStrBot => None
        case DefaultStrNum => None
        case DefaultStrOther => None
        case DefaultStrSet(v) => Some(v)
      }

    /* partial order */
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

    /* join */
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

    /* meet */
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

    /* abstract operator 'equal to' */
    def ===(that: AbsString, absBool: AbsBoolUtil): AbsBool =
      (this.getSingle, that.getSingle) match {
        case (Some(s1), Some(s2)) =>
          absBool.alpha(s1 == s2)
        case _ => {
          if (this <= DefaultStrBot || that <= DefaultStrBot) absBool.Bot
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
          val set = v1.foldLeft(IHashSet[String]())((hs1: IHashSet[String], s1: String) =>
            v2.foldLeft(hs1)((hs2: IHashSet[String], s2: String) => hs2 + (s1 + s2)))
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

    def charAt(pos: AbsNumber): AbsString = {
      this.gammaOpt match {
        case Some(vs) => {
          pos.getUIntSingle match {
            case Some(d) => {
              vs.foldLeft[AbsString](DefaultStrBot)((r, s) => {
                if (d >= s.length || d < 0)
                  r + alpha("")
                else {
                  val i = d.toInt
                  r + alpha(s.substring(i, i + 1))
                }
              })
            }
            case _ => DefaultStrTop
          }
        }
        case _ => DefaultStrTop
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
        case DefaultStrNum => absBool.Top
        case DefaultStrOther => absBool.Top
        case DefaultStrSet(vs) =>
          that.getSingle match {
            case Some(_s) => vs.foldLeft[AbsBool](absBool.Bot)((result, v) => result + absBool.alpha(v.contains(_s)))
            case None =>
              if (that </ DefaultStrBot)
                absBool.Top
              else
                absBool.Bot
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

    override def toString: String =
      this match {
        case DefaultStrNum => "NumStr"
        case DefaultStrOther => "OtherStr"
        case DefaultStrSet(vs) => vs.foldLeft("")((result, v) => if (result.length == 0) "\"" + v + "\"" else result + ", \"" + v + "\"")
        case DefaultStrTop => "String"
        case DefaultStrBot => "Bot"
      }

    def isTop: Boolean = this == DefaultStrTop
    def isBottom: Boolean = this == DefaultStrBot

    def isConcrete: Boolean =
      this match {
        case DefaultStrSet(_) => true
        case _ => false
      }

    def toAbsString(absString: AbsStringUtil): AbsString =
      this match {
        case DefaultStrSet(_) => this
        case _ => DefaultStrBot
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
  case object DefaultStrTop extends DefaultStringSet
  case object DefaultStrBot extends DefaultStringSet
  case object DefaultStrNum extends DefaultStringSet
  case object DefaultStrOther extends DefaultStringSet
  case class DefaultStrSet(values: IHashSet[String]) extends DefaultStringSet
}
