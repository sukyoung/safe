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

import kr.ac.kaist.safe.Config
import kr.ac.kaist.safe.compiler.Parser
import kr.ac.kaist.safe.compiler.Hoister
import kr.ac.kaist.safe.compiler.Disambiguator
import kr.ac.kaist.safe.compiler.WithRewriter
import kr.ac.kaist.safe.compiler.Compiler
import kr.ac.kaist.safe.errors.StaticError
import kr.ac.kaist.safe.errors.StaticErrors
import kr.ac.kaist.safe.errors.UserError
import kr.ac.kaist.safe.nodes.Program
import kr.ac.kaist.safe.safe_util.{ JSIRUnparser, NodeUtil, Useful }
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException

////////////////////////////////////////////////////////////////////////////////
// Compiler
////////////////////////////////////////////////////////////////////////////////
object CompileMain {
  /**
   * Compile files. If they compile ok, it will say "Ok".
   * If you want a dump then give -out=outfile.
   */
  def doit(config: Config): Int = {
    if (config.FileNames.length == 0)
      throw new UserError("The astRewrite command needs a file to disambiguate.")
    val (ir, return_code, _) = Compiler.compile(config)
    val ircode = new JSIRUnparser(ir).doit
    if (config.opt_OutFileName != null) {
      val outFileName = config.opt_OutFileName
      try {
        val (fw, writer): (FileWriter, BufferedWriter) = Useful.filenameToWriters(outFileName)
        writer.write(ircode)
        writer.close
        fw.close
        println("Dumped IR to " + outFileName)
      } catch {
        case e: IOException =>
          throw new IOException("IOException " + e + "while writing to " + outFileName)
      }
    } else println(ircode)
    return_code
  }
}
