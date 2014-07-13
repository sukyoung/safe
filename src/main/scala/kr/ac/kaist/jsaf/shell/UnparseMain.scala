/******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.shell

import java.io.{IOException, BufferedWriter, FileWriter}
import scala.Predef.String
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.nodes_util.{JSAstToConcrete, ASTIO}
import kr.ac.kaist.jsaf.useful.Pair
import kr.ac.kaist.jsaf.useful.Useful

////////////////////////////////////////////////////////////////////////////////
// UnParse
////////////////////////////////////////////////////////////////////////////////
object UnparseMain {
  /**
   * UnParse a file.
   * If you want a dump then give -out somefile.
   */
  def unparse(): Int = {
    if (Shell.params.FileNames.length == 0) throw new UserError("The unparse command needs a file to unparse.")
    val fileName = Shell.params.FileNames(0)

    val result = ASTIO.readJavaAst(fileName)
    if (result.isSome) {
      val code = JSAstToConcrete.doit(result.unwrap)
      if (Shell.params.opt_OutFileName != null) {
        try {
          val pair: Pair[FileWriter, BufferedWriter] = Useful.filenameToBufferedWriter(Shell.params.opt_OutFileName)
          val (fw, writer) = (pair.first, pair.second)
          writer.write(code)
          writer.close
          fw.close
        }
        catch {
          case e: IOException => {
            throw new IOException("IOException " + e + "while writing " + Shell.params.opt_OutFileName)
          }
        }
      }
      else System.out.println(code)
    }
    else System.out.println("Error! Reading the " + fileName + " file failed!")

    0
  }

  def unparse(fileName: String, outFileName: String): Int = {
    Shell.params.Clear
    Shell.params.opt_OutFileName = outFileName
    Shell.params.FileNames = new Array[String](1)
    Shell.params.FileNames(0) = fileName
    unparse()
  }
}
