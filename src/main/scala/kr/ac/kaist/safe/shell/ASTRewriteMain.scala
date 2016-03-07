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
import kr.ac.kaist.safe.Safe
import kr.ac.kaist.safe.compiler.Parser
import kr.ac.kaist.safe.compiler.Hoister
import kr.ac.kaist.safe.compiler.Disambiguator
import kr.ac.kaist.safe.compiler.WithRewriter
import kr.ac.kaist.safe.exceptions.StaticError
import kr.ac.kaist.safe.exceptions.StaticErrors
import kr.ac.kaist.safe.exceptions.UserError
import kr.ac.kaist.safe.nodes.Program
import kr.ac.kaist.safe.safe_util.{ JSAstToConcrete, NodeUtil }
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
  def doit(): Int = {
    if (Safe.config.FileNames.length == 0)
      throw new UserError("The astRewrite command needs a file to disambiguate.")
    val fileName = Safe.config.FileNames(0)
    var return_code = 0
    var program: Program = Parser.fileToAST(Safe.config.FileNames)
    program = (new Hoister(program).doit).asInstanceOf[Program]
    val disambiguator = new Disambiguator(program)
    program = (disambiguator.doit).asInstanceOf[Program]
    var errors: List[StaticError] = disambiguator.getErrors
    val withRewriter: WithRewriter = new WithRewriter(program, false)
    program = (withRewriter.doit).asInstanceOf[Program]
    errors :::= (withRewriter.getErrors)
    return_code = StaticErrors.reportErrors(NodeUtil.getFileName(program), errors)
    if (Safe.config.opt_OutFileName != null) {
      val outFileName = Safe.config.opt_OutFileName
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

  def disambiguate(fileName: String, outFileName: String): Int = {
    Safe.config = Config(List("usage", "-out=" + outFileName, fileName))
    doit
  }
}
