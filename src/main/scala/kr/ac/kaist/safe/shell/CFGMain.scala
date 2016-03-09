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
import kr.ac.kaist.safe.Safe
import kr.ac.kaist.safe.compiler.{ Compiler, BasicCFGBuilder }
import kr.ac.kaist.safe.exceptions.{ StaticError, StaticErrors, UserError }
import kr.ac.kaist.safe.nodes.CFG
import kr.ac.kaist.safe.safe_util.{ AddressManager, NodeUtil }
import kr.ac.kaist.safe.useful.Useful

////////////////////////////////////////////////////////////////////////////////
// CFG Builder
////////////////////////////////////////////////////////////////////////////////
object CFGMain {
  /**
   * Build a controfl flow graph.
   * If you want a dump then give -out=outfile.
   */
  def cfgBuilder: Int = {
    if (Safe.config.FileNames.length == 0)
      throw new UserError("Need a file to build a control flow graph.")
    val fileNames = Safe.config.FileNames
    val fileName: String = Safe.config.FileNames(0)

    // Initialize AddressManager
    AddressManager.reset
    val (ir, rc, _) = Compiler.compile(Safe.config.FileNames)
    val (cfg: CFG, errors: List[StaticError]) = BasicCFGBuilder.build(ir)
    var return_code = rc

    if (!errors.isEmpty) {
      StaticErrors.reportErrors(NodeUtil.getFileName(ir), errors)
    } else return_code = -2

    if (Safe.config.opt_OutFileName != null) {
      val outFileName = Safe.config.opt_OutFileName
      try {
        val (fw, writer): (FileWriter, BufferedWriter) = Useful.filenameToWriters(outFileName)
        // ToDo: cfg.toString
        writer.write(cfg.toString)
        writer.close
        fw.close
        System.out.println("Dumped CFG to " + outFileName)
      } catch {
        case e: IOException =>
          throw new IOException("IOException " + e + "while writing to " + outFileName)
      }
    } else cfg.dump

    return_code
  }
}
