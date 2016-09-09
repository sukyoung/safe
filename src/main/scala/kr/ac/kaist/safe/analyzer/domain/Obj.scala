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
import kr.ac.kaist.safe.LINE_SEP

import scala.collection.immutable.HashMap

class Obj(
    val map: Map[String, (PropValue, AbsAbsent)],
    val imap: ObjInternalMap
) {
  override def toString: String = {
    val sortedMap = map.toSeq.sortBy {
      case (key, _) => key
    }
    val sortedIMap = imap.toSeq.sortBy {
      case (key, _) => key.toString()
    }

    val s = new StringBuilder
    sortedMap.map {
      case (key, (propv, absent)) => {
        s.append(key).append(absent.isBottom match {
          case true => s" |-> "
          case false => s" @-> "
        }).append(propv.toString).append(LINE_SEP)
      }
    }
    sortedIMap.map {
      case (key, iv) => {
        s.append(key.toString)
          .append(s" : ")
          .append(iv.toString)
          .append(LINE_SEP)
      }
    }

    s.toString
  }

  /* partial order */
  def <=(that: Obj): Boolean = {
    if (this.isEmpty) true
    else if (that.isEmpty) false
    else if (!(this.map.keySet subsetOf that.map.keySet)) false
    else if (!(this.imap.keySet subsetOf that.imap.keySet)) false
    else that.map.forall {
      case (key, thatPVA) => {
        this.map.get(key) match {
          case None => false
          case Some(thisPVA) =>
            val (thisPV, thisAbsent) = thisPVA
            val (thatPV, thatAbsent) = thatPVA
            thisPV <= thatPV && thisAbsent <= thatAbsent
        }
      }
    } && that.imap.forall {
      case (key, thatIV) => {
        this.imap.get(key) match {
          case None => false
          case Some(thisIV) => thisIV <= thatIV
        }
      }
    }
  }

  /* not a partial order */
  def </(that: Obj): Boolean = !(this <= that)

  /* join */
  def +(that: Obj): Obj = {
    val keys = this.map.keySet ++ that.map.keySet
    val newMap = keys.foldLeft(ObjEmptyMap)((m, key) => {
      val thisVal = this.map.get(key)
      val thatVal = that.map.get(key)
      (thisVal, thatVal) match {
        case (None, None) => m
        case (None, Some(v)) =>
          val (prop, _) = v
          m + (key -> (prop, AbsAbsent.Top))
        case (Some(v), None) =>
          val (prop, _) = v
          m + (key -> (prop, AbsAbsent.Top))
        case (Some(v1), Some(v2)) =>
          val (propV1, absent1) = v1
          val (propV2, absent2) = v2
          m + (key -> (propV1 + propV2, absent1 + absent2))
      }
    })

    val ikeys = this.imap.keySet ++ that.imap.keySet
    val newIMap = ikeys.foldLeft(ObjEmptyIMap)((im, key) => {
      val thisIVal = this.imap.get(key)
      val thatIVal = that.imap.get(key)
      (thisIVal, thatIVal) match {
        case (None, None) => im
        case (None, Some(iv)) => im + (key -> iv)
        case (Some(iv), None) => im + (key -> iv)
        case (Some(iv1), Some(iv2)) => im + (key -> (iv1 + iv2))
      }
    })

    new Obj(newMap, newIMap)
  }

  /* lookup */
  private def lookup(x: String): (Option[PropValue], AbsAbsent) = {
    this.map.get(x) match {
      case Some(pva) =>
        val (propV, absent) = pva
        (Some(propV), absent)
      //      case None if x.take(1) == "@" => (None, AbsAbsent.Bot)
      case None if isNumber(x) =>
        val (propV, absent) = map(STR_DEFAULT_NUMBER)
        (Some(propV), absent)
      case None if !isNumber(x) =>
        val (propV, absent) = map(STR_DEFAULT_OTHER)
        (Some(propV), absent)
    }
  }

  /* meet */
  def <>(that: Obj): Obj = {
    if (this.map eq that.map) this
    else {
      //      val map1 = that.map.foldLeft(this.map)((m, kv) => {
      //        val (key, thatPVA) = kv
      //        val (thatPV, thatAbsent) = thatPVA
      //        val (thisPVOpt, thisAbsent) = this.lookup(key)
      //        thisPVOpt match {
      //          case Some(thisPV) if m.contains(key) => m + (key -> (thisPV <> thatPV, thisAbsent <> thatAbsent))
      //          case _ => m - key
      //        }
      //      })
      //      val map2 = this.map.foldLeft(map1)((m, kv) => {
      //        val (key, thisPVA) = kv
      //        if (that.map.contains(key)) m
      //        else m - key
      //      })
      //      new Obj(map2)
      new Obj(ObjEmptyMap, ObjEmptyIMap)
    }
  }

  def isBottom: Boolean = {
    if (this.isEmpty) true
    else if ((this.map.keySet diff DEFAULT_KEYSET).nonEmpty) false
    else
      this.map.foldLeft(true)((b, kv) => {
        val (_, pva) = kv
        val (propV, absent) = pva
        b && propV.isBottom && absent.isBottom
      }) && this.imap.foldLeft(true)((b, kv) => {
        val (_, iv) = kv
        b && iv.isBottom
      })
  }

  /* substitute locR by locO */
  def subsLoc(locR: Loc, locO: Loc): Obj = {
    if (this.isEmpty) this
    else {
      val newMap = this.map.foldLeft(ObjEmptyMap)((m, kv) => {
        val (key, pva) = kv
        val (propV, absent) = pva
        val newV = propV.objval.value.subsLoc(locR, locO)
        val newOV = AbsDataProp(newV, propV.objval.writable, propV.objval.enumerable, propV.objval.configurable)
        val newPropV = PropValue(newOV, propV.funid)
        m + (key -> (newPropV, absent))
      })
      val newIMap = this.imap.foldLeft(ObjEmptyIMap)((im, kv) => {
        val (key, iv) = kv
        val newV = iv.value.subsLoc(locR, locO)
        im + (key -> InternalValue(newV, iv.fidset))
      })
      new Obj(newMap, newIMap)
    }
  }

  def weakSubsLoc(locR: Loc, locO: Loc): Obj = {
    if (this.map.isEmpty) this
    else {
      val newMap = this.map.foldLeft(ObjEmptyMap)((m, kv) => {
        val (key, pva) = kv
        val (propV, absent) = pva
        val newV = propV.objval.value.weakSubsLoc(locR, locO)
        val newOV = AbsDataProp(newV, propV.objval.writable, propV.objval.enumerable, propV.objval.configurable)
        val newPropV = PropValue(newOV, propV.funid)
        m + (key -> (newPropV, absent))
      })
      val newIMap = this.imap.foldLeft(ObjEmptyIMap)((im, kv) => {
        val (key, iv) = kv
        val newV = iv.value.weakSubsLoc(locR, locO)
        im + (key -> InternalValue(newV, iv.fidset))
      })
      new Obj(newMap, newIMap)
    }
  }

  def apply(s: String): Option[PropValue] = {
    this.map.get(s) match {
      case Some(pva) =>
        val (propV, _) = pva
        Some(propV)
      case None if s.take(1) == "@" => None
      case None if DEFAULT_KEYSET contains s => None
      case None if isNumber(s) => this(STR_DEFAULT_NUMBER)
      case None if !isNumber(s) => this(STR_DEFAULT_OTHER)
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
      case ConFin(strSet) => strSet.map(apply(_)).reduce(addPropOpt(_, _))
      case ConInf() =>
        val opt1 = if (AbsBool.True <= absStr.isNum) {
          val pset = map.keySet.filter(x => !(x.take(1) == "@") && isNumber(x))
          val optSet = pset.map((x) => apply(x))
          val opt1 =
            if (optSet.size > 1) optSet.reduce(addPropOpt(_, _))
            else if (optSet.size == 1) optSet.head
            else None
          this.map.get(STR_DEFAULT_NUMBER) match {
            case Some((propv, _)) => addPropOpt(Some(propv), opt1)
            case None => opt1
          }
        } else { None }
        val opt2 = if (AbsBool.False <= absStr.isNum) {
          val pset = map.keySet.filter(x => !(x.take(1) == "@") && !isNumber(x))
          val optSet = pset.map((x) => apply(x))
          val opt1 =
            if (optSet.size > 1) optSet.reduce(addPropOpt(_, _))
            else if (optSet.size == 1) optSet.head
            else None
          this.map.get(STR_DEFAULT_OTHER) match {
            case Some((propv, _)) => addPropOpt(Some(propv), opt1)
            case None => opt1
          }
        } else { None }
        addPropOpt(opt1, opt2)
    }
  }

  def apply(in: InternalName): Option[InternalValue] = imap.get(in)

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

  def get(s: String): PropValue = {
    this(s) match {
      case Some(propV) => propV
      case None => PropValue.Bot
    }
  }

  def getOrElse[T](in: InternalName)(default: T)(f: InternalValue => T): T = {
    this(in) match {
      case Some(iv) => f(iv)
      case None => default
    }
  }

  def get(in: InternalName): InternalValue = {
    this(in) match {
      case Some(iv) => iv
      case None => InternalValueUtil.Bot
    }
  }

  def -(s: String): Obj = {
    if (this.isBottom) this
    else new Obj(this.map - s, this.imap)
  }

  def -(absStr: AbsString): Obj = {
    val (defaultNumber, _) = this.map(STR_DEFAULT_NUMBER)
    val (defaultOther, _) = this.map(STR_DEFAULT_OTHER)
    absStr.gamma match {
      case _ if this.isBottom => this
      case ConInf() =>
        val properties = this.map.keySet.filter(x => {
          val isInternalProp = x.take(1) == "@"
          val (prop, _) = this.map(x)
          val configurable = AbsBool.True <= prop.objval.configurable
          (!isInternalProp) && configurable
        })
        val newMap = properties.foldLeft(this.map)((tmpMap, x) => {
          val (prop, _) = tmpMap(x)
          if (isNumber(x) && prop <= defaultNumber) tmpMap - x
          else if (!isNumber(x) && prop <= defaultOther) tmpMap - x
          else tmpMap.updated(x, (prop, AbsAbsent.Top))
        })
        new Obj(newMap, this.imap)
      case ConFin(strSet) if strSet.size == 1 => this - strSet.head
      case ConFin(strSet) =>
        val newMap = strSet.foldLeft(this.map)((tmpMap, x) => {
          tmpMap.get(x) match {
            case None => tmpMap
            case Some(pva) =>
              val (prop, _) = pva
              if (isNumber(x) && prop <= defaultNumber) tmpMap - x
              else if (!isNumber(x) && prop <= defaultOther) tmpMap - x
              else tmpMap.updated(x, (prop, AbsAbsent.Top))
          }
        })
        new Obj(newMap, this.imap)
    }
  }

  // absent value is set to AbsAbsent.Bot because it is strong update.
  def update(x: String, propv: PropValue, exist: Boolean = false): Obj = {
    if (this.isBottom)
      this
    else if (x.startsWith("@default"))
      new Obj(map.updated(x, (propv, AbsAbsent.Top)), imap)
    else
      new Obj(map.updated(x, (propv, AbsAbsent.Bot)), imap)
  }

  def update(absStr: AbsString, propV: PropValue): Obj = {
    absStr.gamma match {
      case ConFin(strSet) if strSet.size == 1 => // strong update
        new Obj(map.updated(strSet.head, (propV, AbsAbsent.Bot)), imap)
      case ConFin(strSet) =>
        strSet.foldLeft(this)((r, x) => r + update(x, propV))
      case ConInf() => absStr.isNum.getSingle match {
        case ConZero() => AbsObjectUtil.Bot
        case ConOne(Bool(true)) =>
          val newDefaultNum = this.map.get(STR_DEFAULT_NUMBER) match {
            case Some((numPropV, _)) => numPropV + propV
            case None => propV
          }
          val pset = map.keySet.filter(x => map.get(x) match {
            case Some((xPropV, _)) => !(x.take(1) == "@") && isNumber(x) && AbsBool.True <= xPropV.objval.writable
            case None => false
          })
          val weakUpdatedMap = pset.foldLeft(this.map)((m, x) => {
            val absX = AbsString(x)
            val (xPropV, xAbsent) = m.get(x) match {
              case Some((xPropV, xAbsent)) => (propV + xPropV, xAbsent)
              case None => (propV, AbsAbsent.Bot)
            }
            if (AbsAbsent.Top <= xAbsent && absX.isAllNums) m - x
            else m + (x -> (xPropV, xAbsent))
          })
          new Obj(weakUpdatedMap + (STR_DEFAULT_NUMBER -> (newDefaultNum, AbsAbsent.Top)), imap)
        case ConOne(Bool(false)) =>
          val newDefaultOther = this.map.get(STR_DEFAULT_OTHER) match {
            case Some((otherPropV, _)) => otherPropV + propV
            case None => propV
          }
          val pset = map.keySet.filter(x => map.get(x) match {
            case Some((xPropV, _)) => !(x.take(1) == "@") && !isNumber(x) && AbsBool.True <= xPropV.objval.writable
            case None => false
          })
          val weakUpdatedMap = pset.foldLeft(this.map)((m, x) => {
            val absX = AbsString(x)
            val (xPropV, xAbsent) = m.get(x) match {
              case Some((xPropV, xAbsent)) => (propV + xPropV, xAbsent)
              case None => (propV, AbsAbsent.Bot)
            }
            if (AbsAbsent.Top <= xAbsent && absX.isAllOthers) m - x
            else m + (x -> (xPropV, xAbsent))
          })
          new Obj(weakUpdatedMap + (STR_DEFAULT_OTHER -> (newDefaultOther, AbsAbsent.Top)), imap)
        case ConMany() =>
          val newDefaultNum = this.map.get(STR_DEFAULT_NUMBER) match {
            case Some((numPropV, _)) => numPropV + propV
            case None => propV
          }
          val newDefaultOther = this.map.get(STR_DEFAULT_OTHER) match {
            case Some((otherPropV, _)) => otherPropV + propV
            case None => propV
          }
          val pset = map.keySet.filter(x => map.get(x) match {
            case Some((xPropV, _)) => !(x.take(1) == "@") && AbsBool.True <= xPropV.objval.writable
            case None => false
          })
          val weakUpdatedMap = pset.foldLeft(this.map)((m, x) => {
            val absX = AbsString(x)
            val (xPropV, xAbsent) = m.get(x) match {
              case Some((xPropV, xAbsent)) => (propV + xPropV, xAbsent)
              case None => (propV, AbsAbsent.Bot)
            }
            if (AbsAbsent.Top <= xAbsent && absX.isAllNums && xPropV <= newDefaultNum) m - x
            else if (AbsAbsent.Top <= xAbsent && absX.isAllOthers && xPropV <= newDefaultOther) m - x
            else m + (x -> (xPropV, xAbsent))
          })
          new Obj(
            weakUpdatedMap +
              (STR_DEFAULT_NUMBER -> (newDefaultNum, AbsAbsent.Top),
                STR_DEFAULT_OTHER -> (newDefaultOther, AbsAbsent.Top)),
            imap
          )
      }
    }
  }

  def update(in: InternalName, iv: InternalValue): Obj = {
    val newIv =
      this(in) match {
        case Some(oldIv) => iv + oldIv
        case None => iv
      }
    new Obj(map, imap + (in -> newIv))
  }

  def domIn(x: String): AbsBool = {
    def defaultDomIn(default: String): AbsBool = {
      this.map.get(default) match {
        case Some((defaultPropV, _)) if !defaultPropV.objval.value.isBottom => AbsBool.Top
        case _ => AbsBool.False
      }
    }

    this.map.get(x) match {
      case Some((propV, abs)) if !propV.isBottom & abs.isBottom => AbsBool.True
      case Some((propV, abs)) if !propV.isBottom & x.take(1) == "@" & !abs.isBottom => AbsBool.True
      case Some((propV, abs)) if !propV.isBottom & !abs.isBottom => AbsBool.Top
      case Some((propV, _)) if x.take(1) == "@" => AbsBool.False
      case Some((propV, _)) if propV.isBottom & isNumber(x) => defaultDomIn(STR_DEFAULT_NUMBER)
      case Some((propV, _)) if propV.isBottom & !isNumber(x) => defaultDomIn(STR_DEFAULT_OTHER)
      case None if x.take(1) == "@" => AbsBool.False
      case None if isNumber(x) => defaultDomIn(STR_DEFAULT_NUMBER)
      case None if !isNumber(x) => defaultDomIn(STR_DEFAULT_OTHER)
    }
  }

  def domIn(strSet: Set[String]): AbsBool =
    strSet.foldLeft(AbsBool.Bot)((absB, str) => absB + (this domIn str))

  def domIn(absStr: AbsString): AbsBool = {
    absStr.gamma match {
      case ConFin(strSet) => (this domIn strSet.map(_.str))
      case ConInf() => absStr.isNum.getSingle match {
        case ConZero() => AbsBool.Bot
        case ConOne(Bool(true)) =>
          this.map.get(STR_DEFAULT_NUMBER) match {
            case Some((numPropV, _)) if !numPropV.objval.value.isBottom => AbsBool.Top
            case _ if map.keySet.exists(x => !(x.take(1) == "@") && isNumber(x)) => AbsBool.Top
            case _ => AbsBool.False
          }
        case ConOne(Bool(false)) =>
          this.map.get(STR_DEFAULT_OTHER) match {
            case Some((otherPropV, _)) if !otherPropV.objval.value.isBottom => AbsBool.Top
            case _ if map.keySet.exists(x => !(x.take(1) == "@") && !isNumber(x)) => AbsBool.Top
            case _ => AbsBool.False
          }
        case ConMany() =>
          (this.map.get(STR_DEFAULT_NUMBER), this.map.get(STR_DEFAULT_OTHER)) match {
            case (Some((numPropV, _)), _) if !numPropV.objval.value.isBottom => AbsBool.Top
            case (_, Some((otherPropV, _))) if !otherPropV.objval.value.isBottom => AbsBool.Top
            case _ if this.map.keySet.exists(x => !(x.take(1) == "@")) => AbsBool.Top
            case _ => AbsBool.False
          }
      }
    }
  }

  def domIn(in: InternalName): AbsBool = {
    imap.get(in) match {
      case None => AbsBool.False
      case Some(_) => AbsBool.Top
    }
  }

  def collectKeysStartWith(prefix: String): Set[String] = {
    this.map.keySet.filter(s => s.startsWith(prefix))
  }

  def isEmpty: Boolean = this.map.isEmpty && this.imap.isEmpty
}
