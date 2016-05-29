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

import java.io.{ BufferedWriter, FileWriter, IOException }
import scala.util.{ Try, Success, Failure }
import kr.ac.kaist.safe.config.{ Config, ConfigOption, OptionKind, BoolOption, NumOption, StrOption }
import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.cfg_builder.DefaultCFGBuilder
import kr.ac.kaist.safe.nodes.{ IRRoot, CFG }
import kr.ac.kaist.safe.util.{ NodeUtil, Useful }

// CFGBuild phase struct.
case class CFGBuild(
    prev: Compile = Compile(),
    cfgBuildConfig: CFGBuildConfig = CFGBuildConfig()
) extends Phase(Some(prev), Some(cfgBuildConfig)) {
  override def apply(config: Config): Unit = cfgBuild(config) recover {
    //case ex => Console.err.print(ex.toString)
    case ex => Console.err.print(ex.getStackTrace.mkString("\n"))
  }
  def cfgBuild(config: Config): Try[CFG] =
    prev.compile(config).flatMap(cfgBuild(config, _))
  def cfgBuild(config: Config, ir: IRRoot): Try[CFG] = {
    // Build CFG from IR.
    val cbResult = new DefaultCFGBuilder(ir, config, cfgBuildConfig)
    val cfg: CFG = cbResult.cfg
    val excLog: ExcLog = cbResult.excLog

    // Report errors.
    if (excLog.hasError) {
      println(ir.fileName + ":")
      println(excLog)
    }

    // Pretty print to file.
    cfgBuildConfig.outFile match {
      case Some(out) => Useful.fileNameToWriters(out).map { pair =>
        {
          val ((fw, writer)) = pair
          writer.write(cfg.dump)
          writer.close; fw.close
          println("Dumped CFG to " + out)
          cfg
        }
      }
      case None => Try(cfg)
    }
  }
}

// CFGBuild phase helper.
object CFGBuild extends PhaseHelper {
  def create: CFGBuild = CFGBuild()
}

// Config options for CFGBuild phase.
case class CFGBuildConfig(
    var verbose: Boolean = false,
    var outFile: Option[String] = None
// TODO add option for cfg builder
) extends ConfigOption {
  val prefix: String = "cfgBuild:"
  val optMap: Map[String, OptionKind] = Map(
    "verbose" -> BoolOption(() => verbose = true),
    "out" -> StrOption((s: String) => outFile = Some(s))
  )
}
