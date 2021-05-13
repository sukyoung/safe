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
import kr.ac.kaist.safe.util.{ Useful, BoolOption }
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

    val cfgSpanInfoJson = "cfgSpanInfo.json"
    Useful.dump(spanList.toJson.prettyPrint, cfgSpanInfoJson)
    println("Dumped CFGSpanList to " + cfgSpanInfoJson)

    val fidToNameJson = "fidToName.json"
    var fidToNameMap: Map[String, Map[String, JsValue]] = Map()
    fidToName.foreach {
      case (k, FidNameCase(isFunc, name)) =>
        val subName = if (isFunc) "call" else "construct"
        val subMap = fidToNameMap.getOrElse(name, Map())
        fidToNameMap += name -> (subMap + (subName -> JsNumber(k)))
    }
    val json = JsObject(fidToNameMap.map { case (k, v) => k -> JsObject(v) })
    Useful.dump(json.prettyPrint, fidToNameJson)
    println("Dumped fidToName to " + fidToNameJson)

    Success(spanList)
  }

  def getCFGSpanList(cfg: CFG): CFGSpanList = {
    val userBlocks = cfg.getUserBlocks
    val funcSpanList = cfg.getUserFuncs.collect({
      case func @ CFGFunction(_, _, argVars, localVars, _, _, Some(ast)) =>
        val (ty, locals) = if (func.id == 0) {
          ("Se", "")
        } else {
          ("Fe", (localVars ::: argVars).filter((id) => id.kind match {
            case CapturedVar | CapturedCatchVar => true
            case _ => false
          }).map((id) => id.toString).mkString(","))
        }
        List(ast.span.begin.line.toString, ast.span.begin.column.toString, ast.span.end.line.toString, ast.span.end.column.toString, ty, func.id.toString, locals)
    }).reverse
    val userSpanList = userBlocks.foldLeft[CFGSpanList](List())((acc, b) => b match {
      case lh @ LoopHead(func, cond, span) =>
        List(span.begin.line.toString, span.begin.column.toString, span.end.line.toString, span.end.column.toString, "LE", func.id + ":" + lh.id) :: acc
      case _ =>
        b.getInsts.foldLeft(acc)((acc, i) => {
          i match {
            case CFGFunExpr(ir, block, lhs, _, func, aNew1, aNew2, aNew3) =>
              val li = List(ir.span.begin.line.toString, ir.span.begin.column.toString, ir.span.end.line.toString, ir.span.end.column.toString, "T", aNew1.toString, func.id.toString, aNew2.toString)
              val li2 = aNew3 match {
                case Some(aNew) => li ::: List(aNew.toString)
                case None => li
              }
              li2 :: acc
            case CFGAlloc(ir, block, lhs, protoOpt, asite) =>
              List(ir.span.begin.line.toString, ir.span.begin.column.toString, ir.span.end.line.toString, ir.span.end.column.toString, "T", asite.toString) :: acc
            case CFGAllocArray(ir, block, lhs, length, asite) =>
              List(ir.span.begin.line.toString, ir.span.begin.column.toString, ir.span.end.line.toString, ir.span.end.column.toString, "T", asite.toString) :: acc
            case CFGAllocArg(ir, block, lhs, length, asite) =>
              List(ir.span.begin.line.toString, ir.span.begin.column.toString, ir.span.end.line.toString, ir.span.end.column.toString, "A", asite.toString) :: acc
            case CFGCall(ir, block, fun, thisArg, arguments, asite) =>
              List(ir.span.begin.line.toString, ir.span.begin.column.toString, ir.span.end.line.toString, ir.span.end.column.toString, "F,M", asite.toString, 0.toString, block.func.id + ":" + block.id) :: acc
            case CFGConstruct(ir, block, fun, thisArg, arguments, asite) =>
              List(ir.span.begin.line.toString, ir.span.begin.column.toString, ir.span.end.line.toString, ir.span.end.column.toString, "F,M", asite.toString, 1.toString, block.func.id + ":" + block.id) :: acc
            case _ => acc
          }
        })
    })
    funcSpanList ::: userSpanList.reverse
  }

  def defaultConfig: CFGSpanInfoConfig = CFGSpanInfoConfig()
  val options: List[PhaseOption[CFGSpanInfoConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "messages during CFGSpanInfo are muted.")
  )
}

// CFGSpanInfo phase config
case class CFGSpanInfoConfig(
  var silent: Boolean = false
) extends Config
