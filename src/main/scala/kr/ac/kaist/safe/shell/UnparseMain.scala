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

import java.io.{ IOException, BufferedWriter, FileWriter }
import scala.Predef.String
import kr.ac.kaist.safe.Safe
import kr.ac.kaist.safe.exceptions.UserError
import kr.ac.kaist.safe.safe_util.JSAstToConcrete
import kr.ac.kaist.safe.nodes_util.ASTIO
import kr.ac.kaist.safe.useful.Useful
import kr.ac.kaist.safe.Config

////////////////////////////////////////////////////////////////////////////////
// UnParse
////////////////////////////////////////////////////////////////////////////////
object UnparseMain {
  /**
   * UnParse a file.
   * If you want a dump then give -out=outfile.
   */
  def unparse(): Int = {
    if (Safe.config.FileNames.length == 0)
      throw new UserError("The unparse command needs a file to unparse.")
    val fileName = Safe.config.FileNames(0)

    val result = ASTIO.readJavaAst(fileName)
    if (result.isSome) {
      val code = JSAstToConcrete.doit(result.unwrap)
      if (Safe.config.opt_OutFileName != null) {
        try {
          val (fw, writer) = Useful.filenameToWriters(Safe.config.opt_OutFileName)
          writer.write(code)
          writer.close
          fw.close
        } catch {
          case e: IOException => {
            throw new IOException("IOException " + e + "while writing " + Safe.config.opt_OutFileName)
          }
        }
      } else System.out.println(code)
    } else System.out.println("Error! Reading the " + fileName + " file failed!")

    0
  }

  def unparse(fileName: String, outFileName: String): Int = {
    Safe.config = Config(List("usage", "-out=" + outFileName, fileName))
    unparse
  }
}
