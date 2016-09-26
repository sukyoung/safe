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

package kr.ac.kaist.safe.analyzer.models.builtin

import scala.collection.immutable.HashSet
import kr.ac.kaist.safe.analyzer.domain.IClass
import kr.ac.kaist.safe.analyzer.domain.IPrimitiveValue
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.analyzer.domain.Utils._
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.util.SystemAddr

object BuiltinHelper {
  def checkExn(h: Heap, absValue: AbsValue, clsName: String): HashSet[Exception] = {
    val exist = absValue.locset.foldLeft(AbsBool.Bot)((b, loc) => {
      val clsStr = h.get(loc)(IClass).value.pvalue.strval
      b + (clsStr === AbsString(clsName))
    })
    val pv = absValue.pvalue
    if (AbsBool.False <= exist)
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

  def max(left: AbsNumber, right: AbsNumber): AbsNumber =
    AbsBool(AbsNumber.NaN <= left || AbsNumber.NaN <= right).map[AbsNumber](
      thenV = AbsNumber.NaN,
      elseV = (left < right).map[AbsNumber](thenV = right, elseV = left)(AbsNumber)
    )(AbsNumber)

  def min(left: AbsNumber, right: AbsNumber): AbsNumber =
    AbsBool(AbsNumber.NaN <= left || AbsNumber.NaN <= right).map[AbsNumber](
      thenV = AbsNumber.NaN,
      elseV = (left < right).map[AbsNumber](thenV = left, elseV = right)(AbsNumber)
    )(AbsNumber)
}
