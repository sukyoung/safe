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

package kr.ac.kaist.safe.phase

import scala.io.Source
import scala.util.{ Try, Success }
import kr.ac.kaist.safe.{ CommandObj, CmdASTRewrite }
import kr.ac.kaist.safe.Safe
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.util._
import spray.json._
import spray.json.DefaultJsonProtocol._
import kr.ac.kaist.safe.analyzer.models._
import kr.ac.kaist.safe.analyzer.domain._
import java.io._

// Rewrite the misaligned function id jsmodels, and merge them.
case object JSModelRewrite extends PhaseObj[Unit, JSModelRewriteConfig, Unit] {
  val name: String = "JSModelRewriter"
  val help: String = "If the function id in jsmodel is misaligned, fix it."
  def apply(
    unit: Unit,
    safeConfig: SafeConfig,
    config: JSModelRewriteConfig
  ): Try[Unit] = {
    Utils.register(aaddrType = NormalAAddr)
    val model = ModelParser.parseFile(safeConfig.fileNames.head).get
    val data = model.toString
    val outFileName = config.outFile match {
      case Some(v) => {
        v
      }
      case None => {
        safeConfig.fileNames.head
      }
    }
    val pw = new PrintWriter(new File(outFileName))
    pw.write(data)
    pw.close
    Success(unit)
  }

  def defaultConfig: JSModelRewriteConfig = JSModelRewriteConfig()
  val options: List[PhaseOption[JSModelRewriteConfig]] = List(
    ("out", StrOption((c, s) => c.outFile = Some(s)),
      "the results of rewriting will be written to the outfile.jsmodel.")
  )
}

// JSModelRewrite phase config
case class JSModelRewriteConfig(
  var outFile: Option[String] = None
) extends Config
