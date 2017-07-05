/**
 * *****************************************************************************
 * Copyright (c) 2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.analyzer.console.command

import java.io._

import kr.ac.kaist.safe.analyzer.console.{ Console => Con, Target }
import kr.ac.kaist.safe.util.Useful
import kr.ac.kaist.safe.json.CFGProtocol._
import kr.ac.kaist.safe.json.WorklistProtocol._
import kr.ac.kaist.safe.json.ConfigProtocol._

import spray.json._

// dump 
case object CmdDump extends Command("dump", "Dump current analysis data.") {
  def help: Unit = println("usage: " + name + " {name}")
  def run(c: Con, args: List[String]): Option[Target] = {
    args match {
      case name :: Nil =>
        val (fw, writer) = Useful.fileNameToWriters(s"$name.json")
        writer.write(JsArray(
          c.cfg.toJson,
          c.worklist.toJson,
          c.sem.toJson,
          c.config.toJson,
          JsNumber(c.getIter - 1)
        ).prettyPrint)
        writer.close
        fw.close
        None
      case _ => help; None
    }
  }
}
