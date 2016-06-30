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

// TODO move
case object CmdMove extends Command("move", "Change a current position.") {
  def help: Unit = {
    println("usage: move {CFGBlock}")
    println("example: move entry0")
    println("         move block23")
    println("         move exitexc2")
  }
  def run(c: Console, args: List[String]): Option[Target] = None
  // try {
  //   if (args.length > 0) {
  //     // parse first argument(cp)
  //     // TODO need to support a syntax for control point.
  //     val arg0 = args(0).toLowerCase
  //     parseNode(c, arg0) match {
  //       case Some(node) => {
  //         c.getTable.get(node) match {
  //           case Some(cs) => {
  //             val contexts = cs.keySet.toArray
  //             if (contexts.length == 1) {
  //               c.current = (node, contexts.head)
  //             } else {
  //               System.out.println("* Contexts")
  //               (0 to contexts.length - 1).foreach(i => System.out.println("[" + i + "]: " + contexts(i).toString))
  //               val line = c.reader.readLine("[0 to " + (contexts.length - 1) + "]? ")
  //               val i = line.toInt
  //               c.current = (node, contexts(i))
  //             }
  //           }
  //           case None => {
  //             System.out.println(node + " doesn't have a state")
  //           }
  //         }
  //       }
  //       case None => {
  //         System.out.println("Cannot parse: " + arg0)
  //       }
  //     }
  //   }
  // } catch {
  //   case _ =>
  //     if (args.length > 0)
  //       System.out.println("Cannot parse command : " + args(0))
  //     else
  //       System.out.println("Cannot parse command : ")
  // }
}
