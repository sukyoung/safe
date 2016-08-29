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
import kr.ac.kaist.safe.analyzer.models.PredefLoc.{ GLOBAL, SINGLE_PURE_LOCAL }
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.util.{ Useful, Loc, ProgramAddr }

abstract class Command(
    val name: String,
    val info: String = ""
) {
  def run(c: Console, args: List[String]): Option[Target]
  def help: Unit

  protected def showState(c: Console, state: State, all: Boolean = false): String = {
    val heap = state.heap
    val old = state.heap.old
    "** heap **" + LINE_SEP +
      (if (all) heap.toStringAll else heap.toString) + LINE_SEP +
      LINE_SEP +
      "** old address set **" + LINE_SEP +
      old.toString
  }

  protected def grep(key: String, str: String): String = {
    str.split(LINE_SEP)
      .filter(_.contains(key))
      .mkString(LINE_SEP)
  }
}
