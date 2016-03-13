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
import kr.ac.kaist.safe.compiler.Compiler
import kr.ac.kaist.safe.exceptions.UserError
import kr.ac.kaist.safe.safe_util.JSAstToConcrete
import kr.ac.kaist.safe.useful.Useful
import java.io.BufferedWriter
import java.io.FileWriter
import java.io.IOException

////////////////////////////////////////////////////////////////////////////////
// AST Rewriter
////////////////////////////////////////////////////////////////////////////////
object ASTRewriteMain {
  /**
   * Rewrite files. If they rewrite ok, it will say "Ok".
   * If you want a dump then give -out=outfile.
   */
  def doit(config: Config): Int = {
    if (config.FileNames.length == 0)
      throw new UserError("The astRewrite command needs a file to disambiguate.")
    val (program, return_code, _) = Compiler.astRewrite(config)
    if (config.opt_OutFileName != null) {
      val outFileName = config.opt_OutFileName
      try {
        val (fw, writer): (FileWriter, BufferedWriter) = Useful.filenameToWriters(outFileName)
        writer.write(JSAstToConcrete.doitInternal(program))
        writer.close
        fw.close
        System.out.println("Dumped rewritten AST to " + outFileName)
      } catch {
        case e: IOException =>
          throw new IOException("IOException " + e + "while writing to " + outFileName)
      }
    } else System.out.println(JSAstToConcrete.doit(program))
    return return_code
  }
}
