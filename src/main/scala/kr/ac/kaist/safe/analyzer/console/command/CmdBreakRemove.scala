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

package kr.ac.kaist.safe.analyzer.console.command

import kr.ac.kaist.safe.analyzer.console._

// remove break
case object CmdBreakRemove extends Command("break-rm", "Remove a break point.") {
  override val help: String = "usage: " + name + " {break-order}"

  def run(c: Interactive, args: List[String]): Option[Target] = {
    val orderPattern = "(\\d+)".r
    args match {
      case orderPattern(orderStr) :: Nil => {
        val order = orderStr.toInt
        val breakList = c.getBreakList
        val len = breakList.length
        if (breakList.isDefinedAt(order)) {
          val block = breakList(order)
          val fid = block.func.id
          c.removeBreak(block)
          printResult(s"* break-point[$order] removed.")
          printResult(s"[$order] function[$fid] $block")
        } else {
          printResult(s"* given order is out of bound: $len")
          CmdBreakList.run(c, Nil)
        }
      }
      case _ => printResult(help)
    }
    None
  }
}
