/******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.shell

import java.io.{BufferedWriter, FileWriter, IOException}
import scala.collection.JavaConversions
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.nodes.IRRoot
import kr.ac.kaist.jsaf.nodes_util.{JSIRUnparser, ASTIO}
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.useful.{Useful, Pair}
import edu.rice.cs.plt.tuple.{Option => JOption}

////////////////////////////////////////////////////////////////////////////////
// Disambiguate and
// Compile
////////////////////////////////////////////////////////////////////////////////
object CompileMain {
  /**
   * Compile a file. If the file compiles ok it will say "Ok".
   * If you want a dump then give -out somefile.
   */
  def compile(): Int = {
    if (Shell.params.FileNames.length == 0) throw new UserError("Need a file to compile.")
    val fileNames = JavaConversions.seqAsJavaList(Shell.params.FileNames)

    val return_code = 0
    val irOpt: JOption[IRRoot] = Shell.fileToIR(fileNames, Shell.toOption(Shell.params.opt_OutFileName))
    if (Shell.opt_DisambiguateOnly) return 0
    if (irOpt.isSome) {
      val ir: IRRoot = irOpt.unwrap
      val ircode = new JSIRUnparser(ir).doit
      if (Shell.params.opt_OutFileName != null) {
        val outfile = Shell.params.opt_OutFileName
        try {
          val pair: Pair[FileWriter, BufferedWriter] = Useful.filenameToBufferedWriter(outfile)
          val (fw, writer) = (pair.first, pair.second)
          ASTIO.writeJavaAst(ir, outfile)
          writer.close
          fw.close
          System.out.println("Dumped IR to " + outfile)
        }
        catch {
          case e: IOException => {
            throw new IOException("IOException " + e + "while writing " + outfile)
          }
        }
      }
      else System.out.println(ircode)
    }
    else return -2

    if (Shell.params.opt_Time) Shell.printTimeTitle = "Compilation"

    return_code
  }
}