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

// break list
case object CmdBreakList extends Command("break-list", "Show the list of break points.") {
  def run(c: Interactive, args: List[String]): Option[Target] = {
    args match {
      case Nil => c.getBreakList match {
        case Nil => printResult("* no break point.")
        case breakList => {
          val len = breakList.length
          printResult(s"* $len break point(s).")
          breakList.zipWithIndex.foreach {
            case (block, order) => {
              val fid = block.func.id
              printResult(s"  [$order] function[$fid] $block")
            }
          }
        }
      }
      case _ => printResult(help)
    }
    None
  }
}
