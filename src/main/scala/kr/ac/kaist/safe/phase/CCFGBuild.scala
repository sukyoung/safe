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

// CFGBuild phase
case object CCFGBuild extends PhaseObj[CFG, CCFGBuildConfig, CCFG] {
  val name: String = "ccfgBuilder"
  val help: String =
    "Builds a control flow graph with JavaScript codes."
  def apply(
    cfg: CFG,
    safeConfig: SafeConfig,
    config: CCFGBuildConfig
  ): Try[CCFG] = {
    val ccfg = cfg.toCode
    // Pretty print to file.
    config.outFile.map(out => {
      val (fw, writer) = Useful.fileNameToWriters(out)
      writer.write(cfg.toCode.toString)
      //writer.write(ccfg.toJson.prettyPrint)
      writer.close
      fw.close
      println("Dumped CCFG to " + out)
    })

    Success(ccfg)
  }

  def defaultConfig: CCFGBuildConfig = CCFGBuildConfig()
  val options: List[PhaseOption[CCFGBuildConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "messages during CCFG building are muted."),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the resulting CCFG will be written to the outfile.")
  )
}

// CCFGBuild phase config
case class CCFGBuildConfig(
  var silent: Boolean = false,
  var outFile: Option[String] = None
) extends Config
