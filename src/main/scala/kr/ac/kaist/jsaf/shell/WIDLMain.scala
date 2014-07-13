/******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.shell

import java.io._
import java.nio.charset.Charset
import java.util.{ArrayList, HashMap}
import java.util.{List => JList}
import kr.ac.kaist.jsaf.analysis.cfg.CFGBuilder
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.models.DOMBuilder
import kr.ac.kaist.jsaf.bug_detector.{StateManager, BugDetector}
import kr.ac.kaist.jsaf.compiler.Parser
import kr.ac.kaist.jsaf.exceptions.{ParserError, UserError}
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util._
import kr.ac.kaist.jsaf.nodes_util.{NodeFactory => NF}
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.useful.{Files, Pair}
import kr.ac.kaist.jsaf.parser.WIDL
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.widl.{WIDLToString, WIDLToDB}
import edu.rice.cs.plt.tuple.{Option => JOption}
import org.cyberneko.html.parsers.DOMParser
import xtc.parser.{ParseError, SemanticValue}
import kr.ac.kaist.jsaf.nodes_util.Coverage

////////////////////////////////////////////////////////////////////////////////
// Web IDL
////////////////////////////////////////////////////////////////////////////////
object WIDLMain {
  ////////////////////////////////////////////////////////////////////////////////
  // Parse
  ////////////////////////////////////////////////////////////////////////////////
  /**
   * Parse a Web IDL file.
   */
  def widlparse: Int = {
    val return_code = 0
    if (Shell.params.FileNames.length == 0)
      throw new UserError("The widlparse command needs a file or a directory to parse.")
    val name = Shell.params.FileNames(0)
    val widl: ArrayList[WDefinition] = if (name.endsWith(".widl")) parseWidl(name)
                                       else parseDir(name)
    if (Shell.params.opt_OutFileName != null) WIDLToDB.storeToDB(Shell.params.opt_OutFileName, widl)
    else System.out.println(WIDLToString.doit(widl))
    return_code
  }

  val SEP = File.separator
  val widlFilter = new FilenameFilter() {
                       def accept(dir: File, name: String) = name.endsWith(".widl")
                   }
  val jsFilter = new FilenameFilter() {
                     def accept(dir: File, name: String) = name.endsWith(".js")
                 }
  def parseDir(_dir: String): ArrayList[WDefinition] = {
    var dir = _dir
    if (!dir.endsWith(SEP)) dir += SEP
    val result = new ArrayList[WDefinition]()
    toList(new File(dir).list(widlFilter).map(f => dir+f).toList).foreach(name => result.addAll(parseWidl(name)))
    result                                                                                         
  }

  def parseWidl(fileName: String): ArrayList[WDefinition] = {
    try {
      val fs = new FileInputStream(new File(fileName))
      val sr = new InputStreamReader(fs, Charset.forName("UTF-8"))
      val in = new BufferedReader(sr)
      val parser = new WIDL(in, fileName)
      val parseResult = parser.pWIDL(0)
      in.close
      sr.close
      fs.close
      if (parseResult.hasValue) {
        parseResult.asInstanceOf[SemanticValue].value.asInstanceOf[ArrayList[WDefinition]]
      }
      else {
        System.out.println("WIDL parsing failed.")
        throw new ParserError(parseResult.asInstanceOf[ParseError], parser, 0)
      }
    }
    catch {
      case f: FileNotFoundException => {
        throw new UserError(fileName + " not found")
      }
    }
  }

  def widlparse(fileName: String): Int = {
    Shell.params.Clear
    Shell.params.FileNames = new Array[String](1)
    Shell.params.FileNames(0) = fileName
    widlparse
  }
}
