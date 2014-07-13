/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.domain

import java.util.{HashMap => JHashMap}

object AbsString {
  abstract class AbsStringCase
  case object StrTopCase extends AbsStringCase
  case object StrBotCase extends AbsStringCase

  val StrTop: AbsString = new AbsString(StrTopCase)
  val StrBot: AbsString = new AbsString(StrBotCase)

  private var alpha_cache: JHashMap[(StrDomainType, String), AbsString] = null
  /* regexp, number string */
  private val hex = "(0[xX][0-9a-fA-F]+)".r.pattern
  private val exp = "[eE][+-]?[0-9]+"
  private val dec1 = "[0-9]+\\.[0-9]*(" +exp+ ")?"
  private val dec2 = "\\.[0-9]+(" +exp+ ")?"
  private val dec3 = "[0-9]+(" +exp+ ")?"
  private val dec = "([+-]?(Infinity|(" +dec1+ ")|(" +dec2 + ")|(" +dec3 + ")))"
  private val num_regexp = ("NaN|(" +hex+ ")|(" +dec+ ")").r.pattern

  // an abstract value which stands for natural numbers(0, 1, 2, ...)
  val NumTop = AbsStringSet.NumTop

  def initCache(): Unit = {
    clearCache()
    alpha_cache = new JHashMap()
  }

  def clearCache(): Unit = {
   if (alpha_cache != null) {
      alpha_cache.synchronized {
        alpha_cache.clear()
      }
    }
    alpha_cache = null
  }

  def isHex(str: String): Boolean =
    hex.matcher(str).matches()

  def isNum(str: String): Boolean =
    num_regexp.matcher(str).matches()

  def alpha(str: String, strDomainType: StrDomainType = StrDomainDefault): AbsString = {
    if(str == null) return StrBot

    // check cached result
    if (alpha_cache != null) {
      alpha_cache.synchronized {
        val cached = alpha_cache.get((strDomainType, str))
        if (cached != null) return cached
      }
    }

    // compute result if not cached
    val result = strDomainType match {
      case StrDomainSet => AbsStringSet.alpha(str)
      case StrDomainAutomata => AbsStringAutomata.alpha(str)
    }

    // cache computed result
    if (alpha_cache != null) {
      alpha_cache.synchronized {
        alpha_cache.put((strDomainType, str), result)
      }
    }

    // return result
    result
  }

  def fromCharCode(n: AbsNumber): AbsString = {
    if (n </ NumBot) {
      n.gamma match {
        case Some(vs) =>
          vs.foldLeft[AbsString](StrBot)((r, v) => {
            r + AbsString.alpha("%c".format(v.toInt))
          })
        case None => StrTop
      }
    } else {
      StrBot
    }
  }
}

class AbsString(_kind: AbsString.AbsStringCase) extends AbsBase[String] {
  val kind: AbsString.AbsStringCase = _kind

  override def getAbsCase: AbsCase =
    this.kind match {
      case AbsString.StrTopCase => AbsTop
      case AbsString.StrBotCase => AbsBot
    }

  override def getSingle: Option[String] = None

  override def gamma: Option[Set[String]] = None

  /* partial order */
  def <= (that: AbsString): Boolean =
    (this.kind, that.kind) match {
      case (AbsString.StrBotCase, _) => true
      case (_, AbsString.StrTopCase) => true
      case _ => false
    }

  /* not a partial order */
  def </ (that: AbsString) = !(this <= that)

  /* join */
  def + (that: AbsString): AbsString =
    (this <= that, that <= this) match {
      case (true, _) => that
      case (_, true) => this
      case _ => StrTop
    }

  /* meet */
  def <> (that: AbsString): AbsString =
    (this <= that, that <= this) match {
      case (true, _) => this
      case (_, true) => that
      case _ => StrBot
    }

  /* abstract operator 'equal to' */
  def === (that: AbsString): AbsBool = {
    if (this <= StrBot || that <= StrBot)
      BoolBot
    else {
      (this <= that, that <= this) match {
        case (false, false) => BoolFalse
        case _ => BoolTop
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
    this.kind match {
      case AbsString.StrBotCase => StrBot
      case _ => StrTop
    }

  def concat(that: AbsString): AbsString =
    (this.kind, that.kind) match {
      case (AbsString.StrBotCase, _) => StrBot
      case (_, AbsString.StrBotCase) => StrBot
      case _ => StrTop
    }

  def charAt(pos: AbsNumber): AbsString = {
    this.gamma match {
      case Some(vs) => {
        AbsNumber.getUIntSingle(pos) match {
          case Some(d) => {
            vs.foldLeft[AbsString](StrBot)((r, s) => {
              if (d >= s.length || d < 0)
                r + AbsString.alpha("")
              else {
                val i = d.toInt
                r + AbsString.alpha(s.substring(i, i + 1))
              }
            })
          }
          case _ => StrTop
        }
      }
      case _ => StrTop
    }
  }

  def charCodeAt(pos: AbsNumber): AbsNumber = {
    this.gamma match {
      case Some(vs) => {
        AbsNumber.getUIntSingle(pos) match {
          case Some(d) => {
            vs.foldLeft[AbsNumber](NumBot)((r, s) => {
              if (d >= s.length || d < 0)
                r + NaN
              else {
                val i = d.toInt
                r + AbsNumber.alpha(s.substring(i, i+1).head.toInt)
              }
            })
          }
          case _ => UInt
        }
      }
      case _ => UInt
    }
  }

  def contains(s: AbsString): AbsBool =
    this.kind match {
      case AbsString.StrTopCase => BoolTop
      case AbsString.StrBotCase => BoolBot
    }

  def length: AbsNumber =
    this.kind match {
      case AbsString.StrTopCase => UInt
      case AbsString.StrBotCase => NumBot
    }

  def toLowerCase: AbsString =
    this.kind match {
      case AbsString.StrTopCase => StrTop
      case AbsString.StrBotCase => StrBot
    }

  def toUpperCase: AbsString =
    this.kind match {
      case AbsString.StrTopCase => StrTop
      case AbsString.StrBotCase => StrBot
    }

  override def toString: String =
    this.kind match {
      case AbsString.StrTopCase => "String"
      case AbsString.StrBotCase => "Bot"
    }

  override def isTop: Boolean = this == StrTop

  override def isBottom: Boolean = this == StrBot

  override def isConcrete: Boolean = false

  override def toAbsString: AbsString = StrBot

  def isAllNums: Boolean = false
  def isAllOthers: Boolean = false
}
