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

import kr.ac.kaist.safe.config.{ Config, ConfigOption, OptionKind, BoolOption, NumOption, StrOption }
import kr.ac.kaist.safe.errors.{ StaticError, StaticErrors }
import kr.ac.kaist.safe.compiler.DefaultCFGBuilder
import kr.ac.kaist.safe.nodes.{ IRRoot, CFG }
import kr.ac.kaist.safe.util.{ NodeUtil, Useful }

// CFGBuild phase struct.
case class CFGBuild(
    prev: Compile = Compile(),
    cfgBuildConfig: CFGBuildConfig = CFGBuildConfig()
) extends Phase(Some(prev), Some(cfgBuildConfig)) {
  override def apply(config: Config): Unit = cfgBuild(config)
  def cfgBuild(config: Config): Option[CFG] = {
    prev.compile(config) match {
      case Some(ir) => cfgBuild(config, ir)
      case None => None
    }
  }
  def cfgBuild(config: Config, ir: IRRoot): Option[CFG] = {
    // Build CFG from IR.
    val (cfg: CFG, errors: List[StaticError]) = DefaultCFGBuilder.build(ir, config, cfgBuildConfig)

    // Report errors.
    StaticErrors.reportErrors(NodeUtil.getFileName(ir), errors)

    // Pretty print to file.
    val dump: String = cfg.dump
    cfgBuildConfig.outFile match {
      case Some(out) =>
        val (fw, writer): (FileWriter, BufferedWriter) = Useful.filenameToWriters(out)
        writer.write(dump)
        writer.close
        fw.close
        println("Dumped CFG to " + out)
      case None =>
    }

    // Return CFG.
    Some(cfg)
  }
}

// CFGBuild phase helper.
object CFGBuild extends PhaseHelper {
  def create: CFGBuild = CFGBuild()
}

// Config options for CFGBuild phase.
case class CFGBuildConfig(
    var verbose: Boolean = false,
    var outFile: Option[String] = None,
    var unroll: Int = 0
) extends ConfigOption {
  val prefix: String = "cfgBuild:"
  val optMap: Map[String, OptionKind] = Map(
    "verbose" -> BoolOption(() => verbose = true),
    "out" -> StrOption((s: String) => outFile = Some(s)),
    "unroll" -> NumOption((i: Int) => unroll = i)
  )
}
