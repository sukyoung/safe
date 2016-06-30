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

  private def showLoc(c: Console, loc: Loc, obj: Obj): String = {
    val am = c.addrManager
    val keyStr = (if (am.isRecentLoc(loc)) "#" else "##") +
      am.locName(loc) + " -> "
    val indentedStr = indentation(obj.toString, keyStr.length)
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
