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

// import jline.console.ConsoleReader
// import scala.util.matching.Regex
// import kr.ac.kaist.safe.analyzer.{ ControlPoint, Worklist }
// import kr.ac.kaist.safe.cfg_builder.DotWriter
// import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.analyzer.console._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.config.Config

abstract class Command(
    val name: String,
    val info: String = ""
) {
  def run(c: Console, args: List[String]): Option[Target]
  def help: Unit

  private def indentation(objStr: String, indent: Int): String = {
    val arr = objStr.split(Config.LINE_SEP)
    if (arr.length < 2) objStr
    else {
      val space = " " * indent
      val tailStr = arr.tail.map(s => space + s).mkString(Config.LINE_SEP)
      arr.head + Config.LINE_SEP + tailStr
    }
  }

  private def showValue(c: Console, v: Value): String = {
    val (pvalue, locset) = (v.pvalue, v.locset)
    val pvalStr =
      if (pvalue.isBottom) ""
      else pvalue.toString

    val locSetStr =
      if (locset.isEmpty) ""
      else locset.map(loc => locName(c, loc)).mkString(", ")

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
      case (false, false) => objValStr + Config.LINE_SEP + funidSetStr
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
    }.mkString(Config.LINE_SEP)
  }

  private def locName(c: Console, loc: Loc): String = {
    val am = c.addrManager
    (if (am.isRecentLoc(loc)) "#" else "##") + am.locName(loc)
  }

  private def showLoc(c: Console, loc: Loc, obj: Obj): String = {
    val am = c.addrManager
    val keyStr = locName(c, loc) + " -> "
    val indentedStr = indentation(showObj(c, obj), keyStr.length)
    keyStr + indentedStr
  }

  protected def showLoc(c: Console, heap: Heap, loc: Loc): Option[String] = {
    heap(loc) match {
      case Some(obj) => Some(showLoc(c, loc, obj))
      case None => None
    }
  }

  protected def showHeap(c: Console, heap: Heap, all: Boolean = false): String = {
    val am = c.addrManager
    def isUserLoc(loc: Loc): Boolean = {
      val pred = am.PredefLoc
      am.locToAddr(loc) >= am.locToAddr(pred.COLLAPSED)
    }
    if (heap.isBottom) "⊥Heap"
    else {
      val sortedSeq = heap.map.toSeq.filter {
        case (loc, _) => all || isUserLoc(loc)
      }.sortBy { case (loc, _) => loc }

      sortedSeq.map {
        case (loc, obj) => showLoc(c, loc, obj)
      }.mkString(Config.LINE_SEP)
    }
  }

  protected def showContext(c: Console, ctxt: Context, all: Boolean = false): String = "" // TODO

  protected def showState(c: Console, state: State, all: Boolean = false): String = {
    "** heap **" + Config.LINE_SEP +
      showHeap(c, state.heap, all) + Config.LINE_SEP +
      Config.LINE_SEP +
      "** context **" + Config.LINE_SEP +
      showContext(c, state.context, all)
  }

  protected def parseLocName(c: Console, str: String): Option[Loc] =
    c.addrManager.parseLocName(str)

  protected def grep(key: String, str: String): String = {
    str.split(Config.LINE_SEP)
      .filter(_.contains(key))
      .mkString(Config.LINE_SEP)
  }
}
