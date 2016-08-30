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

import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.analyzer.models.builtin._
import kr.ac.kaist.safe.nodes.cfg.FunctionId
import kr.ac.kaist.safe.util.Loc

import scala.collection.immutable.{ HashMap, HashSet }

case class AbsObjectUtil(utils: Utils) {
  private val pvalueU = utils.pvalue
  private val absNumberU = utils.absNumber
  private val absStringU = utils.absString

  def defaultValue(locSet: Set[Loc]): PValue = {
    if (locSet.isEmpty) pvalueU.Bot
    else pvalueU.Top
  }

  def defaultValue(locSet: Set[Loc], preferredType: String): PValue = {
    if (locSet.isEmpty) pvalueU.Bot
    else {
      preferredType match {
        case "Number" => pvalueU(absNumberU.Top)
        case "String" => pvalueU(absStringU.Top)
        case _ => pvalueU.Top
      }
    }
  }

  def defaultValue(locSet: Set[Loc], h: Heap, preferredType: String): PValue = {
    if (locSet.isEmpty) pvalueU.Bot
    else {
      preferredType match {
        case "Number" =>
          pvalueU(defaultValueNumber(locSet, h))
        case "String" =>
          pvalueU(defaultToString(locSet, h))
        case _ => pvalueU.Top
      }
    }
  }

  private def defaultValueNumber(locSet: Set[Loc], h: Heap): AbsNumber = {
    def getClassStrVal(obj: Obj): AbsString = {
      obj.getOrElse("@class")(absStringU.Bot) { _.objval.value.pvalue.strval }
    }

    val objSet = locSet.map(l => h.getOrElse(l, Obj.Bot(utils)))
    val boolObjSet = objSet.filter(obj => {
      absStringU.alpha("Boolean") <= getClassStrVal(obj)
    })
    val numObjSet = objSet.filter(obj => {
      absStringU.alpha("Number") <= getClassStrVal(obj)
    })
    val dateObjSet = objSet.filter(obj => {
      absStringU.alpha("Date") <= getClassStrVal(obj)
    })
    val strObjSet = objSet.filter(obj => {
      absStringU.alpha("String") <= getClassStrVal(obj)
    })
    val regexpObjSet = objSet.filter(obj => {
      absStringU.alpha("RegExp") <= getClassStrVal(obj)
    })
    val othersObjSet = objSet.filter(obj => {
      val absClassStr = getClassStrVal(obj)
      absClassStr != absStringU.alpha("Boolean") &&
        absClassStr != absStringU.alpha("Number") &&
        absClassStr != absStringU.alpha("String") &&
        absClassStr != absStringU.alpha("RegExp") &&
        absClassStr != absStringU.alpha("Date")
    })

    val others = othersObjSet.foldLeft[AbsString](absStringU.Bot)((absStr, obj) => {
      absStr + getClassStrVal(obj)
    })
    val b = boolObjSet.foldLeft[AbsBool](utils.absBool.Bot)((absBool, obj) => {
      absBool + obj.getOrElse("@primitive")(utils.absBool.Bot) { _.objval.value.pvalue.boolval }
    })
    val n = numObjSet.foldLeft[AbsNumber](absNumberU.Bot)((absNum, obj) => {
      absNum + obj.getOrElse("@primitive")(absNumberU.Bot) { _.objval.value.pvalue.numval }
    })
    val n2 = dateObjSet.foldLeft[AbsNumber](absNumberU.Bot)((absNum, obj) => {
      absNum + obj.getOrElse("@primitive")(absNumberU.Bot) { _.objval.value.pvalue.numval }
    })
    val (srcAbsStr, globalAbsB, ignoreCaseAbsB, multilineAbsB) =
      regexpObjSet.foldLeft[(AbsString, AbsBool, AbsBool, AbsBool)](
        (absStringU.Bot, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
      )((res, obj) => {
          val (tmpSrc, tmpGlobal, tmpIgnoreCase, tmpMultiline) = res
          (tmpSrc + obj.getOrElse("source")(absStringU.Bot) { _.objval.value.pvalue.strval },
            tmpGlobal + obj.getOrElse("global")(utils.absBool.Bot) { _.objval.value.pvalue.boolval },
            tmpIgnoreCase + obj.getOrElse("ignoreCase")(utils.absBool.Bot) { _.objval.value.pvalue.boolval },
            tmpMultiline + obj.getOrElse("multiline")(utils.absBool.Bot) { _.objval.value.pvalue.boolval })
        })

    val absStr1 = strObjSet.foldLeft[AbsString](absStringU.Bot)((absStr, obj) => {
      absStr + obj.getOrElse("@primitive")(absStringU.Bot) { _.objval.value.pvalue.strval }
    })
    val anum2 = b.toAbsNumber(absNumberU)
    val anum3 = n.toAbsNumber(absNumberU)
    val anum4 = n2.toAbsNumber(absNumberU)

    val absStr5 = (
      srcAbsStr.gammaSingle,
      globalAbsB.gamma,
      ignoreCaseAbsB.gamma,
      multilineAbsB.gamma
    ) match {
        case (ConSingleCon(s), ConSingleCon(g), ConSingleCon(i), ConSingleCon(m)) =>
          val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")
          absStringU.alpha("/" + s + "/" + flags)
        case (ConSingleBot(), _, _, _)
        | (_, ConSingleBot(), _, _)
        | (_, _, ConSingleBot(), _)
        | (_, _, _, ConSingleBot()) => absStringU.Bot
        case _ => absStringU.Top
      }

    val absStr6 = others.fold(absStringU.Bot)(_ => absStringU.Top)

    absStr1.toAbsNumber(absNumberU) + anum2 + anum3 + anum4 + absStr5.toAbsNumber(absNumberU) + absStr6.toAbsNumber(absNumberU)
  }

  private def defaultToString(locSet: Set[Loc], h: Heap): AbsString = {
    def getClassStrVal(obj: Obj): AbsString = {
      obj.getOrElse("@class")(absStringU.Bot) { _.objval.value.pvalue.strval }
    }
    val objSet = locSet.map(l => h.getOrElse(l, Obj.Bot(utils)))
    val boolObjSet = objSet.filter(obj => absStringU.alpha("Boolean") <= getClassStrVal(obj))
    val numObjSet = objSet.filter(obj => absStringU.alpha("Number") <= getClassStrVal(obj))
    val strObjSet = objSet.filter(obj => absStringU.alpha("String") <= getClassStrVal(obj))
    val regexpObjSet = objSet.filter(obj => absStringU.alpha("RegExp") <= getClassStrVal(obj))
    val othersObjSet = objSet.filter(obj => {
      val absClassStr = getClassStrVal(obj)
      absClassStr != absStringU.alpha("Boolean") &&
        absClassStr != absStringU.alpha("Number") &&
        absClassStr != absStringU.alpha("String") &&
        absClassStr != absStringU.alpha("RegExp")
    })

    val others = othersObjSet.foldLeft[AbsString](absStringU.Bot)((absStr, obj) => {
      absStr + getClassStrVal(obj)
    })
    val b = boolObjSet.foldLeft[AbsBool](utils.absBool.Bot)((absBool, obj) => {
      absBool + obj.getOrElse("@primitive")(utils.absBool.Bot) { _.objval.value.pvalue.boolval }
    })
    val n = numObjSet.foldLeft[AbsNumber](absNumberU.Bot)((absNum, obj) => {
      absNum + obj.getOrElse("@primitive")(absNumberU.Bot) { _.objval.value.pvalue.numval }
    })
    val (srcAbsStr, globalAbsB, ignoreCaseAbsB, multilineAbsB) =
      regexpObjSet.foldLeft[(AbsString, AbsBool, AbsBool, AbsBool)](
        (absStringU.Bot, utils.absBool.Bot, utils.absBool.Bot, utils.absBool.Bot)
      )((res, obj) => {
          val (tmpSrc, tmpGlobal, tmpIgnoreCase, tmpMultiline) = res
          (tmpSrc + obj.getOrElse("source")(absStringU.Bot) { _.objval.value.pvalue.strval },
            tmpGlobal + obj.getOrElse("global")(utils.absBool.Bot) { _.objval.value.pvalue.boolval },
            tmpIgnoreCase + obj.getOrElse("ignoreCase")(utils.absBool.Bot) { _.objval.value.pvalue.boolval },
            tmpMultiline + obj.getOrElse("multiline")(utils.absBool.Bot) { _.objval.value.pvalue.boolval })
        })

    val absStr1 = strObjSet.foldLeft[AbsString](absStringU.Bot)((absStr, obj) => {
      absStr + obj.getOrElse("@primitive")(absStringU.Bot) { _.objval.value.pvalue.strval }
    })
    val absStr2 = b.toAbsString(absStringU)
    val absStr3 = n.toAbsString(absStringU)
    val absStr4 = (
      srcAbsStr.gammaSingle,
      globalAbsB.gamma,
      ignoreCaseAbsB.gamma,
      multilineAbsB.gamma
    ) match {
        case (ConSingleCon(s), ConSingleCon(g), ConSingleCon(i), ConSingleCon(m)) =>
          val flags = (if (g) "g" else "") + (if (i) "i" else "") + (if (m) "m" else "")
          absStringU.alpha("/" + s + "/" + flags)
        case (ConSingleBot(), _, _, _)
        | (_, ConSingleBot(), _, _)
        | (_, _, ConSingleBot(), _)
        | (_, _, _, ConSingleBot()) => absStringU.Bot
        case _ => absStringU.Top
      }

    val absStr5 = others.fold(absStringU.Bot)(_ => {
      absStringU.Top
    })
    absStr1 + absStr2 + absStr3 + absStr4 + absStr5
  }
}

//TODO: Merge ObjMap implementation
//TODO: Handle default values, key values with "@"
class Obj(val map: Map[String, (PropValue, Absent)]) {
  override def toString: String = {
    val sortedMap = map.toSeq.sortBy {
      case (key, _) => key
    }

    val s = new StringBuilder
    sortedMap.map {
      case (key, (propv, absent)) => {
        s.append(key).append(absent match {
          case AbsentTop => s" @-> "
          case AbsentBot => s" |-> "
        }).append(propv.toString).append(LINE_SEP)
      }
    }

    s.toString
  }

  /* partial order */
  def <=(that: Obj): Boolean = {
    if (this.map.isEmpty) true
    else if (that.map.isEmpty) false
    else if (!(this.map.keySet subsetOf that.map.keySet)) false
    else that.map.forall(kv => {
      val (key, thatPVA) = kv
      this.map.get(key) match {
        case None => false
        case Some(thisPVA) =>
          val (thisPV, thisAbsent) = thisPVA
          val (thatPV, thatAbsent) = thatPVA
          thisPV <= thatPV && thisAbsent <= thatAbsent
      }
    })
  }

  /* not a partial order */
  def </(that: Obj): Boolean = !(this <= that)

  /* join */
  def +(that: Obj): Obj = {
    val keys = this.map.keySet ++ that.map.keySet
    val newMap = keys.foldLeft(Obj.ObjMapBot)((m, key) => {
      val thisVal = this.map.get(key)
      val thatVal = that.map.get(key)
      (thisVal, thatVal) match {
        case (None, None) => m
        case (None, Some(v)) =>
          val (prop, _) = v
          m + (key -> (prop, AbsentTop))
        case (Some(v), None) =>
          val (prop, _) = v
          m + (key -> (prop, AbsentTop))
        case (Some(v1), Some(v2)) =>
          val (propV1, absent1) = v1
          val (propV2, absent2) = v2
          m + (key -> (propV1 + propV2, absent1 + absent2))
      }
    })
    new Obj(newMap)
  }

  /* lookup */
  private def lookup(x: String): (Option[PropValue], Absent) = {
    this.map.get(x) match {
      case Some(pva) =>
        val (propV, absent) = pva
        (Some(propV), absent)
      case None if x.take(1) == "@" => (None, AbsentBot)
      case None if isNum(x) =>
        val (propV, absent) = map(STR_DEFAULT_NUMBER)
        (Some(propV), absent)
      case None if !isNum(x) =>
        val (propV, absent) = map(STR_DEFAULT_OTHER)
        (Some(propV), absent)
    }
  }

  /* meet */
  def <>(that: Obj): Obj = {
    if (this.map eq that.map) this
    else {
      val map1 = that.map.foldLeft(this.map)((m, kv) => {
        val (key, thatPVA) = kv
        val (thatPV, thatAbsent) = thatPVA
        val (thisPVOpt, thisAbsent) = this.lookup(key)
        thisPVOpt match {
          case Some(thisPV) if m.contains(key) => m + (key -> (thisPV <> thatPV, thisAbsent <> thatAbsent))
          case _ => m - key
        }
      })
      val map2 = this.map.foldLeft(map1)((m, kv) => {
        val (key, thisPVA) = kv
        if (that.map.contains(key)) m
        else m - key
      })
      new Obj(map2)
    }
  }

  def isBottom: Boolean = {
    if (this.map.isEmpty) true
    else if ((this.map.keySet diff DEFAULT_KEYSET).nonEmpty) false
    else
      this.map.foldLeft(true)((b, kv) => {
        val (_, pva) = kv
        val (propV, absent) = pva
        b && propV.isBottom && absent.isBottom
      })
  }

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): Obj = {
    if (this.map.isEmpty) this
    else {
      val newMap = this.map.foldLeft(Obj.ObjMapBot)((m, kv) => {
        val (key, pva) = kv
        val (propV, absent) = pva
        val newV = propV.objval.value.subsLoc(locR, locO)
        val newOV = DataProperty(newV, propV.objval.writable, propV.objval.enumerable, propV.objval.configurable)
        val newPropV = PropValue(newOV, propV.funid)
        m + (key -> (newPropV, absent))
      })
      new Obj(newMap)
    }
  }

  def weakSubsLoc(locR: Loc, locO: Loc): Obj = {
    if (this.map.isEmpty) this
    else {
      val newMap = this.map.foldLeft(Obj.ObjMapBot)((m, kv) => {
        val (key, pva) = kv
        val (propV, absent) = pva
        val newV = propV.objval.value.weakSubsLoc(locR, locO)
        val newOV = DataProperty(newV, propV.objval.writable, propV.objval.enumerable, propV.objval.configurable)
        val newPropV = PropValue(newOV, propV.funid)
        m + (key -> (newPropV, absent))
      })
      new Obj(newMap)
    }
  }

  def apply(s: String): Option[PropValue] = {
    this.map.get(s) match {
      case Some(pva) =>
        val (propV, _) = pva
        Some(propV)
      case None if s.take(1) == "@" => None
      case None if DEFAULT_KEYSET contains s => None
      case None if isNum(s) => this(STR_DEFAULT_NUMBER)
      case None if !isNum(s) => this(STR_DEFAULT_OTHER)
    }
  }

  def apply(absStr: AbsString): Option[PropValue] = {
    def addPropOpt(opt1: Option[PropValue], opt2: Option[PropValue]): Option[PropValue] =
      (opt1, opt2) match {
        case (Some(propV1), Some(propV2)) => Some(propV1 + propV2)
        case (Some(_), None) => opt1
        case (None, Some(_)) => opt2
        case (None, None) => None
      }

    absStr.gamma match {
      case ConSetCon(strSet) if strSet.size == 1 => apply(strSet.head)
      case ConSetCon(strSet) if strSet.size > 1 => strSet.map(apply(_)).reduce(addPropOpt(_, _))
      case ConSetBot() => None
      case ConSetTop() =>
        val opt1 = absStr.gammaIsAllNums match {
          case ConSingleBot() | ConSingleCon(false) => None
          case ConSingleCon(true) | ConSingleTop() =>
            val pset = map.keySet.filter(x => !(x.take(1) == "@") && isNum(x))
            val optSet = pset.map((x) => apply(x))
            val opt1 =
              if (optSet.size > 1) optSet.reduce(addPropOpt(_, _))
              else if (optSet.size == 1) optSet.head
              else None
            this.map.get(STR_DEFAULT_NUMBER) match {
              case Some((propv, _)) => addPropOpt(Some(propv), opt1)
              case None => opt1
            }
        }
        val opt2 = absStr.gammaIsAllNums match {
          case ConSingleBot() | ConSingleCon(true) => None
          case ConSingleCon(false) | ConSingleTop() =>
            val pset = map.keySet.filter(x => !(x.take(1) == "@") && !isNum(x))
            val optSet = pset.map((x) => apply(x))
            val opt1 =
              if (optSet.size > 1) optSet.reduce(addPropOpt(_, _))
              else if (optSet.size == 1) optSet.head
              else None
            this.map.get(STR_DEFAULT_OTHER) match {
              case Some((propv, _)) => addPropOpt(Some(propv), opt1)
              case None => opt1
            }
        }
        addPropOpt(opt1, opt2)
    }
  }

  def getOrElse[T](s: String)(default: T)(f: PropValue => T): T = {
    this(s) match {
      case Some(propV) => f(propV)
      case None => default
    }
  }

  def getOrElse[T](absStr: AbsString)(default: T)(f: PropValue => T): T = {
    this(absStr) match {
      case Some(propV) => f(propV)
      case None => default
    }
  }

  def get(s: String)(utils: Utils): PropValue = {
    this(s) match {
      case Some(propV) => propV
      case None => PropValue.Bot(utils)
    }
  }

  def -(s: String): Obj = {
    if (this.isBottom) this
    else Obj(this.map - s)
  }

  def -(absStr: AbsString)(utils: Utils): Obj = {
    val (defaultNumber, _) = this.map(STR_DEFAULT_NUMBER)
    val (defaultOther, _) = this.map(STR_DEFAULT_OTHER)
    absStr.gamma match {
      case _ if this.isBottom => this
      case ConSetBot() => Obj.Bot(utils)
      case ConSetTop() =>
        val properties = this.map.keySet.filter(x => {
          val isInternalProp = x.take(1) == "@"
          val (prop, _) = this.map(x)
          val configurable = utils.absBool.True <= prop.objval.configurable
          (!isInternalProp) && configurable
        })
        val newMap = properties.foldLeft(this.map)((tmpMap, x) => {
          val (prop, _) = tmpMap(x)
          if (isNum(x) && prop <= defaultNumber) tmpMap - x
          else if (!isNum(x) && prop <= defaultOther) tmpMap - x
          else tmpMap.updated(x, (prop, AbsentTop))
        })
        Obj(newMap)
      case ConSetCon(strSet) if strSet.size == 1 => this - strSet.head
      case ConSetCon(strSet) =>
        val newMap = strSet.foldLeft(this.map)((tmpMap, x) => {
          tmpMap.get(x) match {
            case None => tmpMap
            case Some(pva) =>
              val (prop, _) = pva
              if (isNum(x) && prop <= defaultNumber) tmpMap - x
              else if (!isNum(x) && prop <= defaultOther) tmpMap - x
              else tmpMap.updated(x, (prop, AbsentTop))
          }
        })
        Obj(newMap)
    }
  }

  // absent value is set to AbsentBot because it is strong update.
  def update(x: String, propv: PropValue, exist: Boolean = false): Obj = {
    if (this.isBottom)
      this
    else if (x.startsWith("@default"))
      Obj(map.updated(x, (propv, AbsentTop)))
    else
      Obj(map.updated(x, (propv, AbsentBot)))
  }

  def update(absStr: AbsString, propV: PropValue, utils: Utils): Obj = {
    absStr.gamma match {
      case ConSetCon(strSet) if strSet.size == 1 => // strong update
        Obj(map.updated(strSet.head, (propV, AbsentBot)))
      case ConSetCon(strSet) =>
        strSet.foldLeft(this)((r, x) => r + update(x, propV))
      case ConSetBot() => Obj.Bot(utils)
      case ConSetTop() => absStr.gammaIsAllNums match {
        case ConSingleBot() => Obj.Bot(utils)
        case ConSingleCon(true) =>
          val newDefaultNum = this.map.get(STR_DEFAULT_NUMBER) match {
            case Some((numPropV, _)) => numPropV + propV
            case None => propV
          }
          val pset = map.keySet.filter(x => map.get(x) match {
            case Some((xPropV, _)) => !(x.take(1) == "@") && isNum(x) && utils.absBool.True <= xPropV.objval.writable
            case None => false
          })
          val weakUpdatedMap = pset.foldLeft(this.map)((m, x) => {
            val absX = utils.absString.alpha(x)
            val (xPropV, xAbsent) = m.get(x) match {
              case Some((xPropV, xAbsent)) => (propV + xPropV, xAbsent)
              case None => (propV, AbsentBot)
            }
            if (AbsentTop <= xAbsent && absX.isAllNums) m - x
            else m + (x -> (xPropV, xAbsent))
          })
          Obj(weakUpdatedMap + (STR_DEFAULT_NUMBER -> (newDefaultNum, AbsentTop)))
        case ConSingleCon(false) =>
          val newDefaultOther = this.map.get(STR_DEFAULT_OTHER) match {
            case Some((otherPropV, _)) => otherPropV + propV
            case None => propV
          }
          val pset = map.keySet.filter(x => map.get(x) match {
            case Some((xPropV, _)) => !(x.take(1) == "@") && !isNum(x) && utils.absBool.True <= xPropV.objval.writable
            case None => false
          })
          val weakUpdatedMap = pset.foldLeft(this.map)((m, x) => {
            val absX = utils.absString.alpha(x)
            val (xPropV, xAbsent) = m.get(x) match {
              case Some((xPropV, xAbsent)) => (propV + xPropV, xAbsent)
              case None => (propV, AbsentBot)
            }
            if (AbsentTop <= xAbsent && absX.isAllOthers) m - x
            else m + (x -> (xPropV, xAbsent))
          })
          Obj(weakUpdatedMap + (STR_DEFAULT_OTHER -> (newDefaultOther, AbsentTop)))
        case ConSingleTop() =>
          val newDefaultNum = this.map.get(STR_DEFAULT_NUMBER) match {
            case Some((numPropV, _)) => numPropV + propV
            case None => propV
          }
          val newDefaultOther = this.map.get(STR_DEFAULT_OTHER) match {
            case Some((otherPropV, _)) => otherPropV + propV
            case None => propV
          }
          val pset = map.keySet.filter(x => map.get(x) match {
            case Some((xPropV, _)) => !(x.take(1) == "@") && utils.absBool.True <= xPropV.objval.writable
            case None => false
          })
          val weakUpdatedMap = pset.foldLeft(this.map)((m, x) => {
            val absX = utils.absString.alpha(x)
            val (xPropV, xAbsent) = m.get(x) match {
              case Some((xPropV, xAbsent)) => (propV + xPropV, xAbsent)
              case None => (propV, AbsentBot)
            }
            if (AbsentTop <= xAbsent && absX.isAllNums && xPropV <= newDefaultNum) m - x
            else if (AbsentTop <= xAbsent && absX.isAllOthers && xPropV <= newDefaultOther) m - x
            else m + (x -> (xPropV, xAbsent))
          })
          Obj(weakUpdatedMap +
            (STR_DEFAULT_NUMBER -> (newDefaultNum, AbsentTop),
              STR_DEFAULT_OTHER -> (newDefaultOther, AbsentTop)))
      }
    }
  }

  def domIn(x: String)(absBool: AbsBoolUtil): AbsBool = {
    def defaultDomIn(default: String): AbsBool = {
      this.map.get(default) match {
        case Some((defaultPropV, _)) if !defaultPropV.objval.value.isBottom => absBool.Top
        case _ => absBool.False
      }
    }

    this.map.get(x) match {
      case Some((propV, AbsentBot)) if !propV.isBottom => absBool.True
      case Some((propV, AbsentTop)) if !propV.isBottom & x.take(1) == "@" => absBool.True
      case Some((propV, AbsentTop)) if !propV.isBottom => absBool.Top
      case Some((propV, _)) if x.take(1) == "@" => absBool.False
      case Some((propV, _)) if propV.isBottom & isNum(x) => defaultDomIn(STR_DEFAULT_NUMBER)
      case Some((propV, _)) if propV.isBottom & !isNum(x) => defaultDomIn(STR_DEFAULT_OTHER)
      case None if x.take(1) == "@" => absBool.False
      case None if isNum(x) => defaultDomIn(STR_DEFAULT_NUMBER)
      case None if !isNum(x) => defaultDomIn(STR_DEFAULT_OTHER)
    }
  }

  def domIn(strSet: Set[String])(absBool: AbsBoolUtil): AbsBool =
    strSet.foldLeft(absBool.Bot)((absB, str) => absB + (this domIn str)(absBool))

  def domIn(absStr: AbsString)(absBool: AbsBoolUtil): AbsBool = {
    absStr.gamma match {
      case ConSetCon(strSet) => (this domIn strSet)(absBool)
      case ConSetBot() => absBool.Bot
      case ConSetTop() => absStr.gammaIsAllNums match {
        case ConSingleBot() => absBool.Bot
        case ConSingleCon(true) =>
          this.map.get(STR_DEFAULT_NUMBER) match {
            case Some((numPropV, _)) if !numPropV.objval.value.isBottom => absBool.Top
            case _ if map.keySet.exists(x => !(x.take(1) == "@") && isNum(x)) => absBool.Top
            case _ => absBool.False
          }
        case ConSingleCon(false) =>
          this.map.get(STR_DEFAULT_OTHER) match {
            case Some((otherPropV, _)) if !otherPropV.objval.value.isBottom => absBool.Top
            case _ if map.keySet.exists(x => !(x.take(1) == "@") && !isNum(x)) => absBool.Top
            case _ => absBool.False
          }
        case ConSingleTop() =>
          (this.map.get(STR_DEFAULT_NUMBER), this.map.get(STR_DEFAULT_OTHER)) match {
            case (Some((numPropV, _)), _) if !numPropV.objval.value.isBottom => absBool.Top
            case (_, Some((otherPropV, _))) if !otherPropV.objval.value.isBottom => absBool.Top
            case _ if this.map.keySet.exists(x => !(x.take(1) == "@")) => absBool.Top
            case _ => absBool.False
          }
      }
    }
  }

  def collectKeysStartWith(prefix: String): Set[String] = {
    this.map.keySet.filter(s => s.startsWith(prefix))
  }
}

object Obj {
  val ObjMapBot: Map[String, (PropValue, Absent)] = HashMap[String, (PropValue, Absent)]()

  def apply(m: Map[String, (PropValue, Absent)]): Obj = new Obj(m)

  def Bot: Utils => Obj = utils => {
    val map = ObjMapBot +
      (STR_DEFAULT_NUMBER -> (PropValue.Bot(utils), AbsentBot)) +
      (STR_DEFAULT_OTHER -> (PropValue.Bot(utils), AbsentBot))

    new Obj(map)
  }

  def Empty: Utils => Obj = utils => {
    val map = ObjMapBot +
      (STR_DEFAULT_NUMBER -> (PropValue.Bot(utils), AbsentTop)) +
      (STR_DEFAULT_OTHER -> (PropValue.Bot(utils), AbsentTop))

    new Obj(map)
  }

  ////////////////////////////////////////////////////////////////
  // new Object constructos
  ////////////////////////////////////////////////////////////////
  def newObject(utils: Utils): Obj = newObject(BuiltinObjectProto.loc)(utils)

  def newObject(loc: Loc)(utils: Utils): Obj = newObject(HashSet(loc))(utils)

  def newObject(locSet: Set[Loc])(utils: Utils): Obj = {
    val absFalse = utils.absBool.False
    Empty(utils)
      .update("@class", PropValue(utils.absString.alpha("Object"))(utils))
      .update("@proto", PropValue(utils.dataProp(locSet)(absFalse, absFalse, absFalse)))
      .update("@extensible", PropValue(utils.absBool.True)(utils))
  }

  def newArgObject(absLength: AbsNumber)(utils: Utils): Obj = {
    val protoVal = utils.value(BuiltinObjectProto.loc)
    val lengthVal = utils.value(absLength)
    val absFalse = utils.absBool.False
    val absTrue = utils.absBool.True
    Empty(utils)
      .update("@class", PropValue(utils.absString.alpha("Arguments"))(utils))
      .update("@proto", PropValue(utils.dataProp(protoVal)(absFalse, absFalse, absFalse)))
      .update("@extensible", PropValue(absTrue)(utils))
      .update("length", PropValue(utils.dataProp(lengthVal)(absTrue, absFalse, absTrue)))
  }

  def newArrayObject(absLength: AbsNumber)(utils: Utils): Obj = {
    val protoVal = utils.value(BuiltinArrayProto.loc)
    val lengthVal = utils.value(absLength)
    val absFalse = utils.absBool.False
    Empty(utils)
      .update("@class", PropValue(utils.absString.alpha("Array"))(utils))
      .update("@proto", PropValue(utils.dataProp(protoVal)(absFalse, absFalse, absFalse)))
      .update("@extensible", PropValue(utils.absBool.True)(utils))
      .update("length", PropValue(utils.dataProp(lengthVal)(utils.absBool.True, absFalse, absFalse)))
  }

  def newFunctionObject(fid: FunctionId, env: Value, l: Loc, n: AbsNumber)(utils: Utils): Obj = {
    newFunctionObject(Some(fid), Some(fid), env, Some(l), n)(utils)
  }

  def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: Value,
    locOpt: Option[Loc], n: AbsNumber)(utils: Utils): Obj = {
    newFunctionObject(fidOpt, constructIdOpt, env,
      locOpt, utils.absBool.True, utils.absBool.False, utils.absBool.False, n)(utils)
  }

  def newFunctionObject(fidOpt: Option[FunctionId], constructIdOpt: Option[FunctionId], env: Value,
    locOpt: Option[Loc], writable: AbsBool, enumerable: AbsBool, configurable: AbsBool,
    absLength: AbsNumber)(utils: Utils): Obj = {
    val protoVal = utils.value(BuiltinFunctionProto.loc)
    val absFalse = utils.absBool.False
    val lengthVal = utils.value(absLength)
    val obj1 = Empty(utils)
      .update("@class", PropValue(utils.absString.alpha("Function"))(utils))
      .update("@proto", PropValue(utils.dataProp(protoVal)(absFalse, absFalse, absFalse)))
      .update("@extensible", PropValue(utils.absBool.True)(utils))
      .update("@scope", PropValue(utils.dataProp(env)))
      .update("length", PropValue(utils.dataProp(lengthVal)(absFalse, absFalse, absFalse)))

    val obj2 = fidOpt match {
      case Some(fid) => obj1.update("@function", PropValue(HashSet(fid))(utils))
      case None => obj1
    }
    val obj3 = constructIdOpt match {
      case Some(cid) => obj2.update("@construct", PropValue(HashSet(cid))(utils))
      case None => obj2
    }
    val obj4 = locOpt match {
      case Some(loc) =>
        val prototypeVal = utils.value(HashSet(loc))
        obj3.update("@hasinstance", PropValue(utils.absNull.Top)(utils))
          .update("prototype", PropValue(utils.dataProp(prototypeVal)(writable, enumerable, configurable)))
      case None => obj3
    }
    obj4
  }

  def newBooleanObj(absB: AbsBool)(utils: Utils): Obj = {
    val newObj = newObject(BuiltinBooleanProto.loc)(utils)
    newObj.update("@class", PropValue(utils.absString.alpha("Boolean"))(utils))
      .update("@primitive", PropValue(absB)(utils))
  }

  def newNumberObj(absNum: AbsNumber)(utils: Utils): Obj = {
    val newObj = newObject(BuiltinNumberProto.loc)(utils)
    newObj.update("@class", PropValue(utils.absString.alpha("Number"))(utils))
      .update("@primitive", PropValue(absNum)(utils))
  }

  def newStringObj(absStr: AbsString)(utils: Utils): Obj = {
    val newObj = newObject(BuiltinStringProto.loc)(utils)

    val newObj2 = newObj
      .update("@class", PropValue(utils.absString.alpha("String"))(utils))
      .update("@primitive", PropValue(absStr)(utils))

    val absFalse = utils.absBool.False
    val absTrue = utils.absBool.True
    absStr.gamma match {
      case ConSetCon(strSet) =>
        strSet.foldLeft(Bot(utils))((obj, str) => {
          val length = str.length
          val newObj3 = (0 until length).foldLeft(newObj2)((tmpObj, tmpIdx) => {
            val charAbsStr = utils.absString.alpha(str.charAt(tmpIdx).toString)
            val charVal = utils.value(charAbsStr)
            tmpObj.update(tmpIdx.toString, PropValue(utils.dataProp(charVal)(absFalse, absTrue, absFalse)))
          })
          val lengthVal = utils.value.alpha(length)
          obj + newObj3.update("length", PropValue(utils.dataProp(lengthVal)(absFalse, absFalse, absFalse)))
        })
      case _ =>
        val strTopVal = utils.value(utils.absString.Top)
        val lengthVal = utils.value(absStr.length(utils.absNumber))
        newObj2
          .update(utils.absString.NumStr, PropValue(utils.dataProp(strTopVal)(absFalse, absTrue, absFalse)), utils)
          .update("length", PropValue(utils.dataProp(lengthVal)(absFalse, absFalse, absFalse)))
    }
  }
}
