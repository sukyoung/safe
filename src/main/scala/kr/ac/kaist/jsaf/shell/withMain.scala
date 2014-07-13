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
import kr.ac.kaist.jsaf.compiler.{WithRewriter, Disambiguator, Hoister, Parser}
import kr.ac.kaist.jsaf.nodes.Program
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.useful.{Useful, Pair}

////////////////////////////////////////////////////////////////////////////////
// with Rewriter
////////////////////////////////////////////////////////////////////////////////
object WithMain {
  /**
   * Rewrite a JavaScript source code using the with statement
   * to another one without using the with statement.
   * If you want to dump the rewritten code,
   * then give -out somefile.
   * Not yet fully implemented.
   */
  def withRewriter: Int = {
    if (Shell.params.FileNames.length == 0) throw new UserError("The with command needs a file to rewrite.")
    val fileNames = JavaConversions.seqAsJavaList(Shell.params.FileNames)

    var program: Program = Parser.fileToAST(fileNames)
    program = new Hoister(program).doit.asInstanceOf[Program]
    program = new Disambiguator(program, Shell.opt_DisambiguateOnly).doit.asInstanceOf[Program]
    program = new WithRewriter(program, false).doit.asInstanceOf[Program]
    val rewritten = JSAstToConcrete.doit(program)
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
    else System.out.println(rewritten)

    0
  }
}
