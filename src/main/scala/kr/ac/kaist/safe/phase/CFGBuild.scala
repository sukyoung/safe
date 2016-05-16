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
import scala.util.{ Success, Failure }
import kr.ac.kaist.safe.config.{ Config, ConfigOption, OptionKind, BoolOption, NumOption, StrOption }
import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.cfg_builder.{ DefaultCFGBuilder, CFG }
import kr.ac.kaist.safe.nodes.IRRoot
import kr.ac.kaist.safe.util.{ NodeUtil, Useful }

// CFGBuild phase struct.
case class CFGBuild(
    prev: Compile = Compile(),
    cfgBuildConfig: CFGBuildConfig = CFGBuildConfig()
) extends Phase(Some(prev), Some(cfgBuildConfig)) {
  override def apply(config: Config): Unit = cfgBuild(config)
  def cfgBuild(config: Config): Option[CFG] = {
    prev.compile(config) match {
      case Success(ir) => cfgBuild(config, ir)
      case _ => None
    }
  }
  def cfgBuild(config: Config, ir: IRRoot): Option[CFG] = {
    // Build CFG from IR.
    val (cfg: CFG, excLog: ExcLog) = DefaultCFGBuilder.build(ir, config, cfgBuildConfig)

    // Report errors.
    if (excLog.hasError) {
      println(NodeUtil.getFileName(ir) + ":")
      println(excLog)
    }

    // Pretty print to file.
    val dump: String = cfg.dump
    cfgBuildConfig.outFile match {
      case Some(out) => Useful.fileNameToWriters(out) match {
        case Success((fw, writer)) =>
          writer.write(dump)
          writer.close; fw.close
          println("Dumped CFG to " + out)
          Some(cfg)
        case Failure(_) =>
          Some(cfg)
      }
      case None => Some(cfg)
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
