/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.typing.domain

import scala.collection.immutable.{HashSet => IHashSet}
import kr.ac.kaist.jsaf.Shell

object AbsStringSet {
  case object NumStrCase extends AbsString.AbsStringCase
  case object OtherStrCase extends AbsString.AbsStringCase
  case class StrSetCase(values: IHashSet[String]) extends AbsString.AbsStringCase {
    var hasNum = false
    var hasOther = false

    // Constructor
    {
      def setFlags(): Unit = {
        for(v <- values) {
          if(AbsString.isNum(v)) hasNum = true else hasOther = true
          if(hasNum && hasOther) return
        }
      }
      setFlags()
    }
  }

  val NumStr: AbsString = new AbsStringSet(NumStrCase)
  val OtherStr: AbsString = new AbsStringSet(OtherStrCase)
  private def StrSet(value: String): AbsString = new AbsStringSet(StrSetCase(IHashSet(value)))
  private def StrSet(values: IHashSet[String]): AbsString = new AbsStringSet(StrSetCase(values))

  // an abstract value which stands for natural numbers(0, 1, 2, ...)
  val NumTop = NumStr

  def alpha(str: String): AbsString = StrSet(str)
}

class AbsStringSet(_kind: AbsString.AbsStringCase) extends AbsString(_kind) {
  override def getAbsCase: AbsCase =
    this.kind match {
      case AbsStringSet.NumStrCase => AbsMulti
      case AbsStringSet.OtherStrCase => AbsMulti
      case AbsStringSet.StrSetCase(v) => if(v.size == 1) AbsSingle else AbsMulti
      case _ => super.getAbsCase
    }

  /**
   * Concretize the given abstract string to a single value.
   * Returns None if the value cannot be concretized to a single value.
   *
   * @param str a given abstract string to be concretized.
   * @return a concretized string.
   */
  override def getSingle: Option[String] =
    this.kind match {
      case AbsStringSet.StrSetCase(v) => if(v.size == 1) Some(v.head) else None
      case AbsStringSet.NumStrCase => None
      case AbsStringSet.OtherStrCase => None
      case _ => super.getSingle
    }

  override def gamma: Option[Set[String]] =
    this.kind match {
      case AbsStringSet.StrSetCase(v) => Some(v)
      case AbsStringSet.NumStrCase => None
      case AbsStringSet.OtherStrCase => None
      case _ => super.gamma
    }

  /* partial order */
  override def <= (that: AbsString) =
    (this.kind, that.kind) match {
      case (a: AbsStringSet.StrSetCase, b: AbsStringSet.StrSetCase) => (!a.hasNum || b.hasNum) && (!a.hasOther || b.hasOther) && a.values.subsetOf(b.values)
      case (a: AbsStringSet.StrSetCase, AbsStringSet.NumStrCase) => a.hasNum && !a.hasOther
      case (a: AbsStringSet.StrSetCase, AbsStringSet.OtherStrCase) => !a.hasNum && a.hasOther
      case (AbsStringSet.NumStrCase, AbsStringSet.NumStrCase) => true
      case (AbsStringSet.OtherStrCase, AbsStringSet.OtherStrCase) => true
      case _ => super.<=(that)
    }

  /* join */
  override def + (that: AbsString) =
    (this.kind, that.kind) match {
      case (a: AbsStringSet.StrSetCase, b: AbsStringSet.StrSetCase) =>
        if (a.values == b.values) this
        else {
          val union = a.values ++ b.values
          if(Shell.params.opt_MaxStrSetSize == 0 || union.size <= Shell.params.opt_MaxStrSetSize)
            AbsStringSet.StrSet(union)
          else {
            if(a.hasNum && a.hasOther || b.hasNum && b.hasOther) StrTop
            else if(a.hasNum && !a.hasOther) {
              if(b.hasNum && !b.hasOther) NumStr
              else StrTop
            }
            else {
              if(b.hasNum && !b.hasOther) StrTop
              else OtherStr
            }
          }
        }
      case _ =>
        super.+(that)
    }

  /* meet */
  override def <> (that: AbsString) = (this, that) match {
    case (a: AbsStringSet.StrSetCase, b: AbsStringSet.StrSetCase) =>
      val intersected = a.values.intersect(b.values)
      if(intersected.size == 0) StrBot
      else AbsStringSet.StrSet(intersected)
    case (a: AbsStringSet.StrSetCase, NumStr) =>
      if(a.hasNum) AbsStringSet.StrSet(a.values.filter(s => AbsString.isNum(s)))
      else StrBot
    case (a: AbsStringSet.StrSetCase, OtherStr) =>
      if(a.hasOther) AbsStringSet.StrSet(a.values.filter(s => !AbsString.isNum(s)))
      else StrBot
    case (NumStr, b: AbsStringSet.StrSetCase) =>
      if(b.hasNum) AbsStringSet.StrSet(b.values.filter(s => AbsString.isNum(s)))
      else StrBot
    case (OtherStr, b: AbsStringSet.StrSetCase) =>
      if(b.hasOther) AbsStringSet.StrSet(b.values.filter(s => !AbsString.isNum(s)))
      else StrBot
    case (NumStr, NumStr) => NumStr
    case (NumStr, OtherStr) => StrBot
    case (OtherStr, NumStr) => StrBot
    case (OtherStr, OtherStr) => OtherStr
    case _ => super.<>(that)
  }

  /* abstract operator 'equal to' */
  override def === (that: AbsString): AbsBool =
    (this.getSingle, that.getSingle) match {
      case (Some(s1), Some(s2)) =>
        AbsBool.alpha(s1 == s2)
      case _ =>
        super.===(that)
    }

  override def trim: AbsString =
    this.kind match {
      case AbsStringSet.StrSetCase(vs) =>
        vs.foldLeft[AbsString](StrBot)((r, s) => {
          var iS = -1
          var b = true
          (0 to s.length - 1).foreach(i => {
            if (b && isWhitespaceOrLineterminator(s.charAt(i))) {
              iS = i + 1
            } else {
              b = false
            }
          })
          val s_2 = if (iS > 0) s.substring(iS) else s
          var iE = s_2.length
          b = true
          (0 to s_2.length - 1).foreach(j => {
            val i = s_2.length - 1 - j
            if (b && isWhitespaceOrLineterminator(s_2.charAt(i))) {
              iE = i
            } else {
              b = false
            }
          })
          r + AbsString.alpha(s_2.substring(0, iE))
        })
      case AbsStringSet.NumStrCase => NumStr
      case AbsStringSet.OtherStrCase => OtherStr + NumStr
    }

  override def concat(that: AbsString) = {
    (this.kind, that.kind) match {
      case (a: AbsStringSet.StrSetCase, b: AbsStringSet.StrSetCase) =>
        if(a.values.size * b.values.size <= Shell.params.opt_MaxStrSetSize) {
          var result = IHashSet[String]()
          for(x <- a.values) for(y <- b.values) result+= x + y
          AbsStringSet.StrSet(result)
        }
        else {
          ((a.hasNum || b.hasNum), (a.hasOther || b.hasOther)) match {
            case (true, true) => StrTop
            case (true, false) => NumStr
            case (false, true) => OtherStr
          }
        }
      case _ => super.concat(that)
    }
  }

  override def contains(s: AbsString): AbsBool =
    this.kind match {
      case AbsStringSet.NumStrCase => BoolTop
      case AbsStringSet.OtherStrCase => BoolTop
      case AbsStringSet.StrSetCase(vs) =>
        s.getSingle match {
          case Some(_s) => vs.foldLeft[AbsBool](BoolBot)((result, v) => result + AbsBool.alpha(v.contains(_s)))
          case None =>
            if (s </ StrBot)
              BoolTop
            else
              BoolBot
        }
      case _ => super.contains(s)
    }

  override def length: AbsNumber =
    this.kind match {
      case AbsStringSet.NumStrCase => UInt
      case AbsStringSet.OtherStrCase => UInt
      case AbsStringSet.StrSetCase(vs) => vs.foldLeft[AbsNumber](NumBot)((result, v) => result + AbsNumber.alpha(v.length))
      case _ => super.length
    }

  override def toLowerCase: AbsString =
    this.kind match {
      case AbsStringSet.NumStrCase => StrTop
      case AbsStringSet.OtherStrCase => OtherStr
      case AbsStringSet.StrSetCase(vs) => vs.foldLeft[AbsString](StrBot)((result, v) => result + AbsString.alpha(v.toLowerCase))
      case _ => super.toLowerCase
    }

  override def toUpperCase: AbsString =
    this.kind match {
      case AbsStringSet.NumStrCase => StrTop
      case AbsStringSet.OtherStrCase => OtherStr
      case AbsStringSet.StrSetCase(vs) => vs.foldLeft[AbsString](StrBot)((result, v) => result + AbsString.alpha(v.toUpperCase))
      case _ => super.toUpperCase
    }

  override def toString: String =
    this.kind match {
      case AbsStringSet.NumStrCase => "NumStr"
      case AbsStringSet.OtherStrCase => "OtherStr"
      case AbsStringSet.StrSetCase(vs) => vs.foldLeft("")((result, v) => if(result.length == 0) "\"" + v + "\"" else result + ", \"" + v + "\"")
    }

  override def isConcrete: Boolean =
    this.kind match {
      case _: AbsStringSet.StrSetCase => true
      case _ => super.isConcrete
    }

  override def toAbsString: AbsString =
    this.kind match {
      case _: AbsStringSet.StrSetCase => this
      case _ => super.toAbsString
    }

  override def isAllNums: Boolean =
    this.kind match {
      case AbsStringSet.NumStrCase => true
      case a: AbsStringSet.StrSetCase => a.hasNum && !a.hasOther
      case _ => super.isAllNums
    }

  override def isAllOthers: Boolean =
    this.kind match {
      case AbsStringSet.OtherStrCase => true
      case a: AbsStringSet.StrSetCase => !a.hasNum && a.hasOther
      case _ => super.isAllOthers
    }

  override def equals(other: Any) =
    other match {
      case that: AbsStringSet =>
        if (this.kind != that.kind) false
        else (this.kind, that.kind) match {
          case (AbsStringSet.StrSetCase(vs1), AbsStringSet.StrSetCase(vs2)) => vs1.toString == vs2.toString
          case _ => true
        }
      case _ => false
    }
}
