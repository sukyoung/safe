/******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.shell

import java.io.{IOException, BufferedWriter, FileWriter}
import java.util.HashMap
import scala.collection.JavaConversions
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.nodes_util.JSAstToConcrete
import kr.ac.kaist.jsaf.compiler.Parser
import kr.ac.kaist.jsaf.compiler.module.ModuleRewriter
import kr.ac.kaist.jsaf.nodes.Program
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.useful.{Useful, Pair}

////////////////////////////////////////////////////////////////////////////////
// Module Rewriter
////////////////////////////////////////////////////////////////////////////////
object ModuleMain {
  /**
   * Rewrite a JavaScript source code using the module syntax
   * to another one without using the module syntax.
   * If you want to dump the rewritten code,
   * then give -out somefile.
   * Not yet fully implemented.
   */
  def module: Int = {
    if (Shell.params.FileNames.length == 0) throw new UserError("The module command needs a file to rewrite.")
    val fileNames = JavaConversions.seqAsJavaList(Shell.params.FileNames)

    Shell.params.opt_Module = true
    Shell.params.opt_IgnoreErrorOnAST = true
    val program: Program = new ModuleRewriter(Parser.fileToAST(fileNames)).doit
    val rewritten: String = JSAstToConcrete.doit(program)
    if (Shell.params.opt_OutFileName != null) {
      try {
        val ppair: Pair[FileWriter, BufferedWriter] = Useful.filenameToBufferedWriter(Shell.params.opt_OutFileName)
        val (fw, writer) = (ppair.first, ppair.second)
        writer.write(rewritten)
        writer.close
        fw.close
      }
      catch {
        case e: IOException => {
          throw new IOException("IOException " + e + "while writing " + Shell.params.opt_OutFileName)
        }
      }
    }
    else  System.out.println(rewritten)

    0
  }
}
