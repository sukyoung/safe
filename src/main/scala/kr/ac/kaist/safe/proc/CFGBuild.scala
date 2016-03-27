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

package kr.ac.kaist.safe.proc

import java.io.{ BufferedWriter, FileWriter, IOException }

import kr.ac.kaist.safe.Config
import kr.ac.kaist.safe.errors.{ StaticError, StaticErrors }
import kr.ac.kaist.safe.compiler.DefaultCFGBuilder
import kr.ac.kaist.safe.nodes.{ IRRoot, CFG }
import kr.ac.kaist.safe.util.{ NodeUtil, Useful }

// CFGBuild procedure struct.
case class CFGBuild(
    prev: Compile = Compile(),
    cfgBuildConfig: CFGBuildConfig = CFGBuildConfig()
) extends Procedure(Some(prev), Some(cfgBuildConfig)) {
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
        // TODO delete try-catch.
        try {
          val (fw, writer): (FileWriter, BufferedWriter) = Useful.filenameToWriters(out)
          writer.write(dump)
          writer.close
          fw.close
          println("Dumped CFG to " + out)
        } catch {
          case e: IOException =>
            throw new IOException("IOException " + e + "while writing to " + out)
        }
      case None =>
    }

    // Return CFG.
    Some(cfg)
  }
}

// CFGBuild procedure helper.
object CFGBuild extends ProcedureHelper {
  def create: CFGBuild = CFGBuild()
}

// Config options for CFGBuild procedure.
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
