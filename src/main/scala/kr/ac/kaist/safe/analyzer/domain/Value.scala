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

import kr.ac.kaist.safe.analyzer.models.PredefLoc

import scala.collection.immutable.HashSet
import kr.ac.kaist.safe.util.Loc

object Value {
  def Bot: Utils => Value = utils => Value(PValue.Bot(utils), LocSetEmpty)

  def apply(pvalue: PValue): Value = Value(pvalue, LocSetEmpty)

  def apply(loc: Loc): Utils => Value = utils => Value(PValue.Bot(utils), HashSet(loc))
  def apply(locSet: Set[Loc]): Utils => Value = utils => Value(PValue.Bot(utils), locSet)
}

case class Value(pvalue: PValue, locset: Set[Loc]) {
  override def toString: String = {
    val pvalStr =
      if (pvalue.isBottom) ""
      else pvalue.toString

    val locSetStr =
      if (locset.isEmpty) ""
      else locset.mkString(", ")

    (pvalue.isBottom, locset.isEmpty) match {
      case (true, true) => "⊥Value"
      case (true, false) => locSetStr
      case (false, true) => pvalStr
      case (false, false) => s"$pvalStr, $locSetStr"
    }
  }

  /* partial order */
  def <=(that: Value): Boolean = {
    if (this eq that) true
    else {
      this.pvalue <= that.pvalue &&
        this.locset.subsetOf(that.locset)
    }
  }

  /* not a partial order */
  def </(that: Value): Boolean = {
    if (this eq that) false
    else {
      !(this.pvalue <= that.pvalue) ||
        !(this.locset.subsetOf(that.locset))
    }
  }

  /* join */
  def +(that: Value): Value =
    (this, that) match {
      case (a, b) if a eq b => this
      case (a, _) if a.isBottom => that
      case (_, b) if b.isBottom => this
      case (_, _) => Value(
        this.pvalue + that.pvalue,
        this.locset ++ that.locset
      )
    }

  /* meet */
  def <>(that: Value): Value = {
    if (this eq that) this
    else {
      Value(
        this.pvalue <> that.pvalue,
        this.locset.intersect(that.locset)
      )
    }
  }

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): Value = {
    if (this.locset(locR)) Value(this.pvalue, (this.locset - locR) + locO)
    else this
  }

  /* weakly substitute locR by locO, that is keep locR together */
  def weakSubsLoc(locR: Loc, locO: Loc): Value = {
    if (this.locset(locR)) Value(this.pvalue, this.locset + locO)
    else this
  }

  def typeCount: Int = {
    if (this.locset.isEmpty)
      pvalue.typeCount
    else
      pvalue.typeCount + 1
  }

  def typeKinds: String = {
    val sb = new StringBuilder()
    sb.append(pvalue.typeKinds)
    if (!this.locset.isEmpty) sb.append((if (sb.length > 0) ", " else "") + "Object")
    sb.toString
  }

  def isBottom: Boolean =
    this.pvalue.isBottom && this.locset.isEmpty

  def toAbsBoolean(absBool: AbsBoolUtil): AbsBool = {
    pvalue.toAbsBoolean(absBool) +
      (if (locset.isEmpty) absBool.Bot else absBool.True)
  }

  def toPrimitive: PValue = this.pvalue

  def toPrimitiveBetter(h: Heap)(utils: Utils): PValue = {
    this.pvalue + objToPrimitiveBetter(h, "String")(utils)
  }

  def objToPrimitive(hint: String)(utils: Utils): PValue = {
    val pvalue: (Utils => PValue) =
      if (this.locset.isEmpty) PValue.Bot
      else {
        hint match {
          case "Number" => PValue(utils.absNumber.Top)
          case "String" => PValue(utils.absString.Top)
          case _ => PValue.Top
        }
      }
    pvalue(utils)
  }

  def objToPrimitiveBetter(h: Heap, hint: String)(utils: Utils): PValue = {
    val pvalue: (Utils => PValue) =
      if (this.locset.isEmpty) PValue.Bot
      else {
        hint match {
          case "Number" =>
            PValue(defaultValueNumber(h)(utils).toAbsNumber(utils.absNumber))
          case "String" =>
            PValue(defaultToString(h)(utils))
        }
      }
    pvalue(utils)
  }

  private def defaultValueNumber(h: Heap)(utils: Utils): PValue = {
    def getClassStrVal(obj: Obj): AbsString = {
      obj.getOrElse("@class")(utils.absString.Bot) { _.objval.value.pvalue.strval }
    }

    val objSet = this.locset.map(l => h.getOrElse(l, Obj.Bot(utils)))
    val boolObjSet = objSet.filter(obj => {
      utils.absString.alpha("Boolean") <= getClassStrVal(obj)
    })
    val numObjSet = objSet.filter(obj => {
      utils.absString.alpha("Number") <= getClassStrVal(obj)
    })
    val dateObjSet = objSet.filter(obj => {
      utils.absString.alpha("Date") <= getClassStrVal(obj)
    })
    val strObjSet = objSet.filter(obj => {
      utils.absString.alpha("String") <= getClassStrVal(obj)
    })
    val regexpObjSet = objSet.filter(obj => {
      utils.absString.alpha("RegExp") <= getClassStrVal(obj)
    })
    val othersObjSet = objSet.filter(obj => {
      val absClassStr = getClassStrVal(obj)
      absClassStr != utils.absString.alpha("Boolean") &&
        absClassStr != utils.absString.alpha("Number") &&
        absClassStr != utils.absString.alpha("String") &&
        absClassStr != utils.absString.alpha("RegExp") &&
        absClassStr != utils.absString.alpha("Date")
    })

    val others = othersObjSet.foldLeft[AbsString](utils.absString.Bot)((absStr, obj) => {
      absStr + getClassStrVal(obj)
    })
    val b = boolObjSet.foldLeft[AbsBool](utils.absBool.Bot)((absBool, obj) => {
      absBool + obj.getOrElse("@primitive")(utils.absBool.Bot) { _.objval.value.pvalue.boolval }
    })
    val n = numObjSet.foldLeft[AbsNumber](utils.absNumber.Bot)((absNum, obj) => {
      absNum + obj.getOrElse("@primitive")(utils.absNumber.Bot) { _.objval.value.pvalue.numval }
    })
    val n2 = dateObjSet.foldLeft[AbsNumber](utils.absNumber.Bot)((absNum, obj) => {
      absNum + obj.getOrElse("@primitive")(utils.absNumber.Bot) { _.objval.value.pvalue.numval }
    })
    val (srcAbsStr, globalAbsB, ignoreCaseAbsB, multilineAbsB) =
      regexpObjSet.foldLeft[(AbsString, AbsBool, AbsBool, AbsBool)](
        (utils.absString.Bot, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
      )((res, obj) => {
          val (tmpSrc, tmpGlobal, tmpIgnoreCase, tmpMultiline) = res
          (tmpSrc + obj.getOrElse("source")(utils.absString.Bot) { _.objval.value.pvalue.strval },
            tmpGlobal + obj.getOrElse("global")(utils.absBool.Bot) { _.objval.value.pvalue.boolval },
            tmpIgnoreCase + obj.getOrElse("ignoreCase")(utils.absBool.Bot) { _.objval.value.pvalue.boolval },
            tmpMultiline + obj.getOrElse("multiline")(utils.absBool.Bot) { _.objval.value.pvalue.boolval })
        })

    val absStr1 = strObjSet.foldLeft[AbsString](utils.absString.Bot)((absStr, obj) => {
      absStr + obj.getOrElse("@primitive")(utils.absString.Bot) { _.objval.value.pvalue.strval }
    })
    val pv2 = PValue(b)(utils)
    val pv3 = PValue(n)(utils)
    val pv4 = PValue(n2)(utils)

    val absStr5 = (
      srcAbsStr.gammaSingle,
      globalAbsB.gamma,
      ignoreCaseAbsB.gamma,
      multilineAbsB.gamma
    ) match {
        case (ConSingleCon(s), ConSingleCon(g), ConSingleCon(i), ConSingleCon(m)) =>
          val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")
          utils.absString.alpha("/" + s + "/" + flags)
        case (ConSingleBot(), _, _, _)
        | (_, ConSingleBot(), _, _)
        | (_, _, ConSingleBot(), _)
        | (_, _, _, ConSingleBot()) => utils.absString.Bot
        case _ => utils.absString.Top
      }

    val pv6 = PValue(
      others.fold(utils.absString.Bot)(_ => {
        utils.absString.Top
      })
    )(utils)
    PValue(absStr1)(utils) + pv2 + pv3 + pv4 + PValue(absStr5)(utils) + pv6
  }

  private def defaultToString(h: Heap)(utils: Utils): AbsString = {
    def getClassStrVal(obj: Obj): AbsString = {
      obj.getOrElse("@class")(utils.absString.Bot) { _.objval.value.pvalue.strval }
    }
    val objSet = this.locset.map(l => h.getOrElse(l, Obj.Bot(utils)))
    val boolObjSet = objSet.filter(obj => utils.absString.alpha("Boolean") <= getClassStrVal(obj))
    val numObjSet = objSet.filter(obj => utils.absString.alpha("Number") <= getClassStrVal(obj))
    val strObjSet = objSet.filter(obj => utils.absString.alpha("String") <= getClassStrVal(obj))
    val regexpObjSet = objSet.filter(obj => utils.absString.alpha("RegExp") <= getClassStrVal(obj))
    val othersObjSet = objSet.filter(obj => {
      val absClassStr = getClassStrVal(obj)
      absClassStr != utils.absString.alpha("Boolean") &&
        absClassStr != utils.absString.alpha("Number") &&
        absClassStr != utils.absString.alpha("String") &&
        absClassStr != utils.absString.alpha("RegExp")
    })

    val others = othersObjSet.foldLeft[AbsString](utils.absString.Bot)((absStr, obj) => {
      absStr + getClassStrVal(obj)
    })
    val b = boolObjSet.foldLeft[AbsBool](utils.absBool.Bot)((absBool, obj) => {
      absBool + obj.getOrElse("@primitive")(utils.absBool.Bot) { _.objval.value.pvalue.boolval }
    })
    val n = numObjSet.foldLeft[AbsNumber](utils.absNumber.Bot)((absNum, obj) => {
      absNum + obj.getOrElse("@primitive")(utils.absNumber.Bot) { _.objval.value.pvalue.numval }
    })
    val (srcAbsStr, globalAbsB, ignoreCaseAbsB, multilineAbsB) =
      regexpObjSet.foldLeft[(AbsString, AbsBool, AbsBool, AbsBool)](
        (utils.absString.Bot, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
      )((res, obj) => {
          val (tmpSrc, tmpGlobal, tmpIgnoreCase, tmpMultiline) = res
          (tmpSrc + obj.getOrElse("source")(utils.absString.Bot) { _.objval.value.pvalue.strval },
            tmpGlobal + obj.getOrElse("global")(utils.absBool.Bot) { _.objval.value.pvalue.boolval },
            tmpIgnoreCase + obj.getOrElse("ignoreCase")(utils.absBool.Bot) { _.objval.value.pvalue.boolval },
            tmpMultiline + obj.getOrElse("multiline")(utils.absBool.Bot) { _.objval.value.pvalue.boolval })
        })

    val absStr1 = strObjSet.foldLeft[AbsString](utils.absString.Bot)((absStr, obj) => {
      absStr + obj.getOrElse("@primitive")(utils.absString.Bot) { _.objval.value.pvalue.strval }
    })
    val absStr2 = b.toAbsString(utils.absString)
    val absStr3 = n.toAbsString(utils.absString)
    val absStr4 = (
      srcAbsStr.gammaSingle,
      globalAbsB.gamma,
      ignoreCaseAbsB.gamma,
      multilineAbsB.gamma
    ) match {
        case (ConSingleCon(s), ConSingleCon(g), ConSingleCon(i), ConSingleCon(m)) =>
          val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")
          utils.absString.alpha("/" + s + "/" + flags)
        case (ConSingleBot(), _, _, _)
        | (_, ConSingleBot(), _, _)
        | (_, _, ConSingleBot(), _)
        | (_, _, _, ConSingleBot()) => utils.absString.Bot
        case _ => utils.absString.Top
      }

    val absStr5 = others.fold(utils.absString.Bot)(_ => {
      utils.absString.Top
    })
    absStr1 + absStr2 + absStr3 + absStr4 + absStr5
  }

  def typeTag(h: Heap)(utils: Utils): AbsString = {
    val s1 = pvalue.undefval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("undefined")
    })
    val s2 = pvalue.nullval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("object") //TODO: check null type?
    })
    val s3 = pvalue.numval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("number")
    })
    val s4 = pvalue.boolval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("boolean")
    })
    val s5 = pvalue.strval.fold(utils.absString.Bot)(_ => {
      utils.absString.alpha("string")
    })

    val isCallableLocSet = locset.foldLeft(utils.absBool.Bot)((tmpAbsB, l) => tmpAbsB + h.isCallable(l)(utils))
    val s6 =
      if (this.locset.nonEmpty && (utils.absBool.False <= isCallableLocSet))
        utils.absString.alpha("object")
      else utils.absString.Bot
    val s7 =
      if (this.locset.nonEmpty && (utils.absBool.True <= isCallableLocSet))
        utils.absString.alpha("function")
      else utils.absString.Bot

    s1 + s2 + s3 + s4 + s5 + s6 + s7
  }

  def getThis(h: Heap)(utils: Utils): Set[Loc] = {
    val locSet1 = (pvalue.nullval.gamma, pvalue.undefval.gamma) match {
      case (ConSimpleBot, ConSimpleBot) => LocSetEmpty
      case _ => HashSet(PredefLoc.GLOBAL)
    }

    val foundDeclEnvRecord = locset.exists(loc => utils.absBool.False <= h.isObject(loc)(utils))

    val locSet2 =
      if (foundDeclEnvRecord) HashSet(PredefLoc.GLOBAL)
      else LocSetEmpty
    val locSet3 = locset.foldLeft(LocSetEmpty)((tmpLocSet, loc) => {
      if (utils.absBool.True <= h.isObject(loc)(utils)) tmpLocSet + loc
      else tmpLocSet
    })

    locSet1 ++ locSet2 ++ locSet3
  }
}
