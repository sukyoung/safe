/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.phase

import scala.util.{ Try, Success }
import kr.ac.kaist.safe.{ LINE_SEP, SafeConfig }
import kr.ac.kaist.safe.cfg_builder.DefaultCFGBuilder
import kr.ac.kaist.safe.nodes.ir.IRRoot
import kr.ac.kaist.safe.nodes.cfg._
import kr.ac.kaist.safe.util._
import spray.json._
import DefaultJsonProtocol._
import kr.ac.kaist.safe.analyzer._

// CFGSpanInfo phase
case object CFGSpanInfo extends PhaseObj[(CFG, Semantics, TracePartition, HeapBuildConfig, Int), CFGSpanInfoConfig, CFGSpanList] {
  val name: String = "cfgSpanInfo"
  val help: String =
    "Builds a CFGSpanList."
  def apply(
    in: (CFG, Semantics, TracePartition, HeapBuildConfig, Int),
    safeConfig: SafeConfig,
    config: CFGSpanInfoConfig
  ): Try[CFGSpanList] = {
    val (cfg, sem, initTP, heapConfig, iter) = in
    val spanList = getCFGSpanList(cfg)
    // Pretty print to file.
    config.outFile.map(out => {
      val (fw, writer) = Useful.fileNameToWriters(out)
      writer.write(spanList.toJson.prettyPrint)
      writer.close
      fw.close
      println("Dumped CFGSpanList to " + out)
    })
    Success(spanList)
  }

  def getCFGSpanList(cfg: CFG): CFGSpanList = {
    val userBlocks = cfg.getUserBlocks
    val userSpanList = userBlocks.foldLeft[CFGSpanList](List())((acc, b) => {
      b.getInsts.foldLeft(acc)((acc, i) => {
        i match {
          case CFGAlloc(ir, block, lhs, protoOpt, asite) =>
            List("T", ir.span.toString, asite.toString) :: acc
          case CFGAllocArray(ir, block, lhs, length, asite) =>
            List("T", ir.span.toString, asite.toString) :: acc
          case CFGCall(ir, block, fun, thisArg, arguments, asite) =>
            List("FM", ir.span.toString, asite.toString, 0.toString, block.func.id + ":" + block.id) :: acc
          case CFGConstruct(ir, block, fun, thisArg, arguments, asite) =>
            List("FM", ir.span.toString, asite.toString, 1.toString, block.func.id + ":" + block.id) :: acc
          case _ => acc
        }
      })
    })
    val blocks = cfg.getAllBlocks
    val spanList = blocks.foldLeft[CFGSpanList](List())((acc, b) => {
      if (b.func.id >= 0) {
        acc
      } else {
        b.getInsts.foldLeft(acc)((acc, i) => {
          i match {
            case CFGAlloc(ir, block, lhs, protoOpt, asite) =>
              block.func.ast match {
                case Some(ast) =>
                  List("T", ir.span.toString, asite.toString, ast.toString(0)) :: acc
                case _ => acc
              }
            case CFGAllocArray(ir, block, lhs, length, asite) =>
              block.func.ast match {
                case Some(ast) =>
                  List("T", ir.span.toString, asite.toString, ast.toString(0)) :: acc
                case _ => acc
              }
            case _ => acc
          }
        })
      }
    })
    spanList.reverse
  }

  def defaultConfig: CFGSpanInfoConfig = CFGSpanInfoConfig()
  val options: List[PhaseOption[CFGSpanInfoConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "messages during CFGSpanInfo are muted."),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the resulting CFGSpanList will be written to the outfile.")
  )
}

// CFGSpanInfo phase config
case class CFGSpanInfoConfig(
  var silent: Boolean = false,
  var outFile: Option[String] = None
) extends Config
