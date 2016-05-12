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

package kr.ac.kaist.safe.parser

import java.io._
import java.nio.charset.Charset
import xtc.parser.SemanticValue
import xtc.parser.ParseError
import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.util.{ NodeUtil => NU, SourceLoc, Span }
import kr.ac.kaist.safe.errors.error._

object Parser {
  val mergedSourceLoc = new SourceLoc(NU.freshFile("Merged"), 0, 0, 0)
  val mergedSourceInfo = new ASTNodeInfo(new Span(mergedSourceLoc, mergedSourceLoc))
  var fileindex = 1

  def getInfoStmts(program: Program): (ASTNodeInfo, SourceElements) = {
    val info = program.info
    if (program.body.stmts.size == 1) {
      val ses = program.body.stmts.head
      (info, SourceElements(info, (NU.makeNoOp(info, "StartOfFile")) +: ses.body :+ (NU.makeNoOp(info, "EndOfFile")), ses.strict))
    } else
      throw AlreadyMergedSourceError(info.span)
  }

  def scriptToStmts(script: (String, (Int, Int), String)): (ASTNodeInfo, SourceElements) = {
    val (fileName, (line, offset), code) = script
    val file = new File(fileName)
    fileindex += 1
    getInfoStmts(parseScript(fileName, (line, offset), code))
  }

  def fileToStmts(f: String): (ASTNodeInfo, SourceElements) = {
    val file = new File(f)
    var path = file.getCanonicalPath
    if (File.separatorChar == '\\') {
      // convert path string to linux style for windows
      path = path.charAt(0).toLower + path.replace('\\', '/').substring(1)
    }
    fileindex += 1
    getInfoStmts(parseFile(file))
  }

  def stringToAST(str: String): Option[Program] = {
    val sr = new StringReader(str)
    val in = new BufferedReader(sr)
    val parser = new JS(in, "stringParse")
    val parseResult = parser.JSmain(0)
    in.close; sr.close
    if (parseResult.hasValue) {
      val result = parseResult.asInstanceOf[SemanticValue].value.asInstanceOf[Program]
      Some(DynamicRewriter.doit(result))
    } else None
  }

  def stringToFnE(str: (String, (Int, Int), String)): Option[FunExpr] = {
    val (fileName, (line, offset), code) = str
    val sr = new StringReader(code)
    val in = new BufferedReader(sr)
    val parser = new JS(in, fileName)
    val parseResult = parser.pJS$FunctionExpr(0)
    in.close; sr.close
    if (parseResult.hasValue) {
      val fe = parseResult.asInstanceOf[SemanticValue].value.asInstanceOf[FunExpr]
      Some(NU.addLinesWalker.addLines(fe, line - 1, offset - 1))
    } else None
  }

  def scriptToAST(ss: List[(String, (Int, Int), String)]): Program = ss match {
    case List(script) =>
      val (info, stmts) = scriptToStmts(script)
      NU.makeProgram(info, List(stmts))
    case scripts =>
      val stmts = scripts.foldLeft(List[SourceElements]())((l, s) => {
        val (_, ss) = scriptToStmts(s)
        l ++ List(ss)
      })
      NU.makeProgram(mergedSourceInfo, stmts)
  }

  def fileToAST(fs: List[String]): Program = fs match {
    case List(file) =>
      val (info, stmts) = fileToStmts(file)
      NU.makeProgram(info, List(stmts))
    case files =>
      val stmts = files.foldLeft(List[SourceElements]())((l, f) => {
        val (_, ss) = fileToStmts(f)
        l ++ List(ss)
      })
      NU.makeProgram(mergedSourceInfo, stmts)
  }

  def parseScript(fileName: String, start: (Int, Int), script: String): Program = {
    val (line, offset) = start
    val is = new ByteArrayInputStream(script.getBytes("UTF-8"))
    val ir = new InputStreamReader(is)
    val in = new BufferedReader(ir)
    val program = parsePgm(in, fileName, line)
    in.close; ir.close; is.close
    NU.addLinesProgram.addLines(program, line - 1, offset - 1)
  }

  /**
   * Parses a file as a program.
   * Converts checked exceptions like IOException and FileNotFoundException
   * to SyntaxError with appropriate error message.
   * Validates the parse by calling
   * parsePgm (see also description of exceptions there).
   */
  def parseFile(file: File): Program = {
    val filename = file.getCanonicalPath
    if (!filename.endsWith(".js"))
      throw NotJSFileError(filename)
    parsePgm(file, filename, 0)
  }

  def parsePgm(str: String, filename: String): Program = {
    val sr = new StringReader(str)
    val in = new BufferedReader(sr)
    val pgm = parsePgm(in, filename, 0)
    in.close; sr.close
    pgm
  }

  def parsePgm(file: File, filename: String, start: Int): Program = {
    val fs = new FileInputStream(file)
    val sr = new InputStreamReader(fs, Charset.forName("UTF-8"))
    val in = new BufferedReader(sr)
    val pgm = parsePgm(in, filename, start)
    in.close; sr.close; fs.close
    pgm
  }

  def parsePgm(in: BufferedReader, filename: String, start: Int): Program = {
    val syntaxLogFile = filename + ".log"
    try {
      val parser = new JS(in, filename)
      val parseResult = parser.JSmain(0)
      if (parseResult.hasValue) {
        val result = parseResult.asInstanceOf[SemanticValue].value.asInstanceOf[Program]
        DynamicRewriter.doit(result)
      } else {
        val error = parseResult.asInstanceOf[ParseError]
        throw ParserError(error.msg, NU.makeSpan(parser.location(error.index)))
      }
    } finally {
      try {
        val file = new File(syntaxLogFile)
        if (file.exists && !file.delete) throw new IOException
      } catch { case ioe: IOException => }
      try {
        in.close
      } catch { case ioe: IOException => }
    }
  }

  def parseFunctionBody(str: String): Boolean = {
    val sr = new StringReader(str)
    val in = new BufferedReader(sr)
    val parser = new JS(in, "ParseFunctionBody")
    val parseResult = parser.pJS$FunctionBody(0)
    in.close; sr.close
    parseResult.hasValue
  }

  def parseFunctionParams(str: String): Boolean = {
    val sr = new StringReader(str)
    val in = new BufferedReader(sr)
    val parser = new JS(in, "ParseFunctionParams")
    val parseResult = parser.pJS$Params(0)
    in.close; sr.close
    parseResult.hasValue
  }
}
