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

package kr.ac.kaist.safe.analyzer.console.command

import kr.ac.kaist.safe.analyzer.console._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util.{ Useful, Loc, ProgramAddr }

abstract class Command(
    val name: String,
    val info: String = ""
) {
  def run(c: Console, args: List[String]): Option[Target]
  def help: Unit

  private def showValue(c: Console, v: Value): String = {
    val (pvalue, locset) = (v.pvalue, v.locset)
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

  private def showObjVal(c: Console, ov: ObjectValue): String = {
    val (value, writable, enumerable, configurable) = (ov.value, ov.writable, ov.enumerable, ov.configurable)
    if (ov.isBottom) "⊥ObjectValue"
    else {
      val prefix =
        (writable.gammaSimple, enumerable.gammaSimple, configurable.gammaSimple) match {
          case (ConSimpleBot, ConSimpleBot, ConSimpleBot) => "[Val] "
          case _ => s"[${writable.toString.take(1)}${enumerable.toString.take(1)}${configurable.toString.take(1)}] "
        }
      prefix + showValue(c, value)
    }
  }

  private def showPropValue(c: Console, pv: PropValue): String = {
    val objval = pv.objval
    val funid = pv.funid
    val objValStr =
      if (objval.isBottom) ""
      else showObjVal(c, objval)

    val funidSetStr =
      if (funid.isEmpty) ""
      else s"[FunIds] " + funid.map(id => id.toString).mkString(", ")

    (objval.isBottom, funid.isEmpty) match {
      case (true, true) => "⊥PropValue"
      case (true, false) => funidSetStr
      case (false, true) => objValStr
      case (false, false) => objValStr + LINE_SEP + funidSetStr
    }
  }

  private def showObj(c: Console, obj: Obj): String = {
    val sortedMap = obj.map.toSeq.sortBy {
      case (key, _) => key
    }

    sortedMap.map {
      case (key, (propv, absent)) => key + (absent match {
        case AbsentTop => s" @-> "
        case AbsentBot => s" |-> "
      }) + showPropValue(c, propv)
    }.mkString(LINE_SEP)
  }

  private def showLoc(c: Console, loc: Loc, obj: Obj): String = {
    val keyStr = loc.toString + " -> "
    val indentedStr = Useful.indentation(showObj(c, obj), keyStr.length)
    keyStr + indentedStr
  }

  protected def showLoc(c: Console, heap: Heap, loc: Loc): Option[String] = {
    heap(loc) match {
      case Some(obj) => Some(showLoc(c, loc, obj))
      case None => None
    }
  }

  protected def showHeap(c: Console, heap: Heap, all: Boolean = false): String = {
    if (heap.isBottom) "⊥Heap"
    else {
      val sortedSeq = heap.map.toSeq.filter {
        case (Loc(ProgramAddr(_), _), _) => true
        case _ => all
      }.sortBy { case (loc, _) => loc }

      sortedSeq.map {
        case (loc, obj) => showLoc(c, loc, obj)
      }.mkString(LINE_SEP)
    }
  }

  protected def showContext(c: Console, ctxt: Context, all: Boolean = false): String = "" // TODO

  protected def showState(c: Console, state: State, all: Boolean = false): String = {
    "** heap **" + LINE_SEP +
      showHeap(c, state.heap, all) + LINE_SEP +
      LINE_SEP +
      "** context **" + LINE_SEP +
      showContext(c, state.context, all)
  }

  protected def grep(key: String, str: String): String = {
    str.split(LINE_SEP)
      .filter(_.contains(key))
      .mkString(LINE_SEP)
  }
}
