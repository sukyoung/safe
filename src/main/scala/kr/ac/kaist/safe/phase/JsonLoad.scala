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

package kr.ac.kaist.safe.phase

import scala.io.Source
import scala.util.{ Try, Success, Failure }
import kr.ac.kaist.safe.{ LINE_SEP, SafeConfig }
import kr.ac.kaist.safe.cfg_builder.DotWriter
import kr.ac.kaist.safe.nodes.cfg.CFG
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.analyzer._
import kr.ac.kaist.safe.analyzer.domain._
import kr.ac.kaist.safe.json.CFGProtocol
import kr.ac.kaist.safe.json.CFGProtocol._
import kr.ac.kaist.safe.json.WorklistProtocol
import kr.ac.kaist.safe.json.WorklistProtocol._
import kr.ac.kaist.safe.json.ConfigProtocol._
import kr.ac.kaist.safe.errors.error.{
  NotJsonFileError,
  NotDumpFormatError,
  JsonParseError,
  NoFileError
}

import spray.json._
import DefaultJsonProtocol._

// JsonLoad phase
case object JsonLoad extends PhaseObj[Unit, JsonLoadConfig, (CFG, Semantics, TracePartition, HeapBuildConfig, Int)] {
  val name: String = "jsonLoader"
  val help: String =
    "Loads a control flow graph, work list, heap state, and heap build options from a given JSON file."

  def apply(
    unit: Unit,
    safeConfig: SafeConfig,
    config: JsonLoadConfig
  ): Try[(CFG, Semantics, TracePartition, HeapBuildConfig, Int)] = safeConfig.fileNames match {
    case Nil => Failure(NoFileError("cfg load"))
    case _ => {
      val fileName = safeConfig.fileNames(0)
      FileKind(fileName) match {
        case JSONFile =>
          try {
            val source: Source = Source.fromFile(fileName, "utf-8")
            source.mkString.parseJson match {
              case JsArray(Vector(cfgJson, worklistJson, semJson, heapConfigJson, JsNumber(iter))) => {
                val heapConfig = heapConfigJson.convertTo[HeapBuildConfig]

                // initialization
                Utils.register(
                  heapConfig.AbsUndef,
                  heapConfig.AbsNull,
                  heapConfig.AbsBool,
                  heapConfig.AbsNumber,
                  heapConfig.AbsString,
                  DefaultLoc,
                  heapConfig.aaddrType
                )

                CFGProtocol.jsModel = heapConfig.jsModel
                val cfg = cfgJson.convertTo[CFG]
                WorklistProtocol.cfg = cfg
                val worklist = worklistJson.convertTo[Worklist]
                WorklistProtocol.worklist = worklist
                val sem = semJson.convertTo[Semantics]
                source.close

                // Pretty print to file.
                config.outFile.map(out => {
                  val (fw, writer) = Useful.fileNameToWriters(out)
                  writer.write(cfg.toString(0))
                  writer.close
                  fw.close
                  println("Dumped CFG to " + out)
                })

                // print dot file: {dotName}.gv, {dotName}.pdf
                config.dotName.map(name => {
                  DotWriter.spawnDot(cfg, None, None, None, s"$name.gv", s"$name.pdf")
                })

                val sens =
                  CallSiteSensitivity(heapConfig.callsiteSensitivity) *
                    LoopSensitivity(heapConfig.loopSensitivity)
                Success((cfg, sem, sens.initTP, heapConfig, iter.toInt))
              }
              case _ => {
                source.close
                Failure(NotDumpFormatError)
              }
            }
          } catch {
            case e: JsonParseError => Failure(e)
          }
        case _ => Failure(NotJsonFileError(fileName))
      }
    }
  }

  def defaultConfig: JsonLoadConfig = JsonLoadConfig()
  val options: List[PhaseOption[JsonLoadConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "messages during JSON loading are muted."),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the loaded CFG will be written to the outfile."),
    ("dot", StrOption((c, s) => c.dotName = Some(s)),
      "the loaded CFG will be drawn to the {name}.gv and {name}.pdf")
  )
}

// JsonLoad phase config
case class JsonLoadConfig(
  var silent: Boolean = false,
  var outFile: Option[String] = None,
  var dotName: Option[String] = None
) extends Config
