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
import kr.ac.kaist.safe.nodes.ast.Program
import spray.json._
import DefaultJsonProtocol._

// CFGBuild phase
case object LabelBuild extends PhaseObj[CCFG, LabelBuildConfig, String] {
  val name: String = "labelBuilder"
  val help: String =
    "Builds a labeled program."
  def apply(
    ccfg: CCFG,
    safeConfig: SafeConfig,
    config: LabelBuildConfig
  ): Try[String] = {
    val s: StringBuilder = new StringBuilder
    // Pretty print to file.
    config.outFile.map(out => {
      val (fw, writer) = Useful.fileNameToWriters(out)
      writer.write(s.toString)
      writer.close
      fw.close
      println("Dumped the labeled program to " + out)
    })

    Success(s.toString)
  }

  def defaultConfig: LabelBuildConfig = LabelBuildConfig()
  val options: List[PhaseOption[LabelBuildConfig]] = List(
    ("silent", BoolOption(c => c.silent = true),
      "messages during label building are muted."),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the resulting prorgam will be written to the outfile.")
  )
}

// LabelBuild phase config
case class LabelBuildConfig(
  var silent: Boolean = false,
  var outFile: Option[String] = None
) extends Config
