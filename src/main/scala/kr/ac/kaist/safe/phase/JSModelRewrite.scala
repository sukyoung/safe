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

package kr.ac.kaist.safe.phase

import scala.io.Source
import scala.util.{ Try, Failure }
import kr.ac.kaist.safe.{ CommandObj, CmdASTRewrite }
import kr.ac.kaist.safe.Safe
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.util._
import spray.json._
import spray.json.DefaultJsonProtocol._
import kr.ac.kaist.safe.analyzer.models._

// Rewrite the misaligned function id jsmodels, and merge them.
case object JSModelRewrite extends PhaseObj[Unit, JSModelRewriteConfig, Unit] {
  val name: String = "JSModelRewriter"
  val help: String = "If the function id in jsmodel is misaligned, fix it."
  def apply(
    unit: Unit,
    safeConfig: SafeConfig,
    config: JSModelRewriteConfig
  ): Try[Unit] = {
    val model = ModelParser.parseFile(NodeUtil.jsModelsBase + safeConfig.fileNames.head).get
    val data = model.toString()
    config.outFile match {
      case Some(v) => {
        val outFileName = v
      }
      case None => {
        val outFileName = safeConfig.fileNames.head
      }
    }
    Try(unit)
  }

  def defaultConfig: JSModelRewriteConfig = JSModelRewriteConfig()
  val options: List[PhaseOption[JSModelRewriteConfig]] = List(
    ("debug", BoolOption(c => c.debug = true),
      "messages during compilation are muted."),
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the results of rewriting will be written to the outfile.jsmodel.")
  )
}

// JSModelRewrite phase config
case class JSModelRewriteConfig(
  var debug: Boolean = false, //
  var outFile: Option[String] = None
) extends Config
