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

package kr.ac.kaist.safe.analyzer.models.builtin

import scala.collection.immutable.HashSet
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.util.PredAllocSite

object BuiltinHelper {
  def checkExn(h: AbsHeap, absValue: AbsValue, clsName: String): HashSet[Exception] = {
    val exist = absValue.locset.foldLeft[AbsBool](AbsBool.Bot)((b, loc) => {
      val clsStr = h.get(loc)(IClass).value.pvalue.strval
      b ⊔ (clsStr StrictEquals AbsStr(clsName))
    })
    val pv = absValue.pvalue
    if (AbsBool.False ⊑ exist)
      HashSet[Exception](TypeError)
    else HashSet[Exception]()
  }

  def isNaN(resV: AbsValue): AbsBool = {
    val num = TypeConversionHelper.ToNumber(resV)
    num.gamma match {
      case ConFin(set) if set.size == 0 => AbsBool.Bot
      case ConFin(set) if set.size == 1 => {
        if (set.head.num.isNaN) AbsBool.True
        else AbsBool.False
      }
      case _ => AbsBool.Top
    }
  }

  def max(left: AbsNum, right: AbsNum): AbsNum =
    if (AbsNum.NaN ⊑ left || AbsNum.NaN ⊑ right) AbsNum.NaN
    else {
      val b = left < right
      val t =
        if (AT ⊑ b) {
          right
        } else AbsNum.Bot
      val f =
        if (AF ⊑ b) {
          left
        } else AbsNum.Bot
      t ⊔ f
    }

  def min(left: AbsNum, right: AbsNum): AbsNum =
    if (AbsNum.NaN ⊑ left || AbsNum.NaN ⊑ right) AbsNum.NaN
    else {
      val b = left < right
      val t =
        if (AT ⊑ b) {
          left
        } else AbsNum.Bot
      val f =
        if (AF ⊑ b) {
          right
        } else AbsNum.Bot
      t ⊔ f
    }
}
