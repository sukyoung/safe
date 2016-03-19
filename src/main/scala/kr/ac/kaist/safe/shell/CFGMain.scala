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

package kr.ac.kaist.safe.shell

import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException
import kr.ac.kaist.safe.Config
import kr.ac.kaist.safe.compiler.{ Compiler, DefaultCFGBuilder }
import kr.ac.kaist.safe.errors.{ StaticError, StaticErrors, UserError }
import kr.ac.kaist.safe.nodes.CFG
import kr.ac.kaist.safe.safe_util.{ AddressManager, NodeUtil, Useful }

////////////////////////////////////////////////////////////////////////////////
// CFG Builder
////////////////////////////////////////////////////////////////////////////////
object CFGMain {
  /**
   * Build a controfl flow graph.
   * If you want a dump then give -out=outfile.
   */
  def cfgBuilder(config: Config): Int = {
    if (config.FileNames.length == 0)
      throw new UserError("Need a file to build a control flow graph.")
    val fileNames = config.FileNames
    val fileName: String = config.FileNames(0)
    val addrManager: AddressManager = config.addrManager

    val (ir, rc, _) = Compiler.compile(config)
    val (cfg: CFG, errors: List[StaticError]) = DefaultCFGBuilder.build(ir, config)
    val dump: String = cfg.dump
    var return_code = rc

    if (!errors.isEmpty) {
      StaticErrors.reportErrors(NodeUtil.getFileName(ir), errors)
    } else return_code = -2

    if (config.opt_OutFileName != null) {
      val outFileName = config.opt_OutFileName
      try {
        val (fw, writer): (FileWriter, BufferedWriter) = Useful.filenameToWriters(outFileName)
        writer.write(dump)
        writer.close
        fw.close
        println("Dumped CFG to " + outFileName)
      } catch {
        case e: IOException =>
          throw new IOException("IOException " + e + "while writing to " + outFileName)
      }
    } else println(dump)

    return_code
  }
}
