/******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.shell

import java.io.{FileNotFoundException, IOException, BufferedWriter, FileWriter}
import java.util.HashMap
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.compiler.Parser
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.nodes.Program
import kr.ac.kaist.jsaf.nodes_util.{JSAstToConcrete, ASTIO, NodeFactory}
import kr.ac.kaist.jsaf.useful.Pair
import kr.ac.kaist.jsaf.useful.Useful
import scala.collection.JavaConversions

////////////////////////////////////////////////////////////////////////////////
// Parse
////////////////////////////////////////////////////////////////////////////////
object ParseMain {
  val return_code = 0
  /**
   * Parse a file. If the file parses ok it will say "Ok".
   * If you want a dump then give -out somefile.
   */
  def parse(): Int = {
    if (Shell.params.FileNames.length == 0) throw new UserError("Need a file to parse")
    val fileNames = JavaConversions.seqAsJavaList(Shell.params.FileNames)
    try {
      NodeFactory.initIr2ast
      val pgm: Program = Parser.fileToAST(fileNames)
      System.out.println("Ok")
      if (Shell.params.opt_OutFileName != null) {
        try {
          ASTIO.writeJavaAst(pgm, Shell.params.opt_OutFileName)
          System.out.println("Dumped parse tree to " + Shell.params.opt_OutFileName)
        }
        catch {
          case e: IOException => {
            throw new IOException("IOException " + e + "while writing " + Shell.params.opt_OutFileName)
          }
        }
      }
    }
    catch {
      case f: FileNotFoundException => {
        throw new UserError(f + " not found")
      }
    }

    if (Shell.params.opt_Time) Shell.printTimeTitle = "Parsing"

    return_code
  }

  def prettyparse(): Int = {
    // file name check
    if (Shell.params.FileNames.length == 0) throw new UserError("Need a file to parse")
    val fileNames = JavaConversions.seqAsJavaList(Shell.params.FileNames)
    // parse -> unparse -> result
    try {
      NodeFactory.initIr2ast
      // parsing ...
      val pgm: Program = Parser.fileToAST(fileNames)
      System.out.println("AST parsing finshed.")
      // unparsing ...
      val code = JSAstToConcrete.doit(pgm)
      System.out.println("Code unparsing finshed.")
      // dump result 
      if (Shell.params.opt_Pretty && Shell.params.opt_PrettyFileName != null) {
        try {
          val pair: Pair[FileWriter, BufferedWriter] = Useful.filenameToBufferedWriter(Shell.params.opt_PrettyFileName)
          val (fw, writer) = (pair.first, pair.second)
          writer.write(code)
          writer.close
          fw.close
          System.out.println("Dumped pretty parsed code to " + Shell.params.opt_PrettyFileName)
        }
        catch {
          case e: IOException => {
            throw new IOException("IOException " + e + "while writing " + Shell.params.opt_PrettyFileName)
          }
        }
      }
      else System.out.println(code)
    }
    catch {
      case f: FileNotFoundException => {
        throw new UserError(f + " not found")
      }
    }

    return_code
  }

  def parse(fileName: String, outFileName: String): Int = {
    Shell.params.Clear
    Shell.params.opt_OutFileName = outFileName
    Shell.params.FileNames = new Array[String](1)
    Shell.params.FileNames(0) = fileName
    parse()
  }
  def prettyparse(fileName: String, prettyFileName: String): Int = {
    Shell.params.Clear
    Shell.params.opt_PrettyFileName = prettyFileName
    Shell.params.FileNames = new Array[String](1)
    Shell.params.FileNames(0) = fileName
    prettyparse()
  }
}
