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

package kr.ac.kaist.safe.compiler

import java.io._
import java.nio.charset.Charset
import java.util.{ List => JList }
import xtc.parser.SemanticValue
import xtc.parser.ParseError
import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.safe_util.{ NodeFactory => NF, NodeUtil => NU, SourceLoc, Span }
import kr.ac.kaist.safe.errors.SAFEError
import kr.ac.kaist.safe.errors.SyntaxError
import kr.ac.kaist.safe.errors.ParserError
import kr.ac.kaist.safe.errors.UserError
import kr.ac.kaist.safe.parser.JS
import kr.ac.kaist.safe.scala_useful.Lists._

object Parser {
  class Result(pgm: Option[Program], errors: List[SyntaxError])
      extends StaticPhaseResult(errors) {
    var programs = pgm match {
      case None => Set[Program]()
      case Some(p) => Set(p)
    }
  }

  val mergedSourceLoc = new SourceLoc(NU.freshFile("Merged"), 0, 0, 0)
  val mergedSourceInfo = new ASTNodeInfo(new Span(mergedSourceLoc, mergedSourceLoc))
  var fileindex = 1

  def getInfoStmts(program: Program): (ASTNodeInfo, SourceElements) = {
    val info = program.info
    if (program.body.stmts.size == 1) {
      val ses = program.body.stmts.head
      (info, SourceElements(info, (NF.makeNoOp(info, "StartOfFile")) +: ses.body :+ (NF.makeNoOp(info, "EndOfFile")), ses.strict))
    } else
      throw new UserError("Sources are already merged!")
  }

  def scriptToStmts(script: (String, (Int, Int), String)): (ASTNodeInfo, SourceElements) = {
    val (fileName, (line, offset), code) = script
    val file = new File(fileName)
    fileindex += 1
    getInfoStmts(parseScriptConvertExn(fileName, (line, offset), code))
  }

  def fileToStmts(f: String): (ASTNodeInfo, SourceElements) = {
    val file = new File(f)
    var path = file.getCanonicalPath
    if (File.separatorChar == '\\') {
      // convert path string to linux style for windows
      path = path.charAt(0).toLower + path.replace('\\', '/').substring(1)
    }
    fileindex += 1
    getInfoStmts(parseFileConvertExn(file))
  }

  def stringToFnE(str: (String, (Int, Int), String)): Option[FunExpr] = {
    val (fileName, (line, offset), code) = str
    val file = new File(fileName)
    val sr = new StringReader(code)
    val in = new BufferedReader(sr)
    val parser = new JS(in, fileName)
    val parseResult = parser.pJS$FunctionExpr(0)
    in.close; sr.close
    if (parseResult.hasValue) {
      val fe = parseResult.asInstanceOf[SemanticValue].value.asInstanceOf[FunExpr]
      Some(NU.addLinesWalker.addLines(fe, line - 1, offset - 1).asInstanceOf[FunExpr])
    } else None
  }

  def scriptToAST(ss: List[(String, (Int, Int), String)]): Program = ss match {
    case List(script) =>
      val (info, stmts) = scriptToStmts(script)
      NF.makeProgram(info, NF.makeTopLevel(info, List(stmts)))
    case scripts =>
      val stmts = scripts.foldLeft(List[SourceElements]())((l, s) => {
        val (_, ss) = scriptToStmts(s)
        l ++ List(ss)
      })
      NF.makeProgram(mergedSourceInfo, NF.makeTopLevel(mergedSourceInfo, stmts))
  }

  def fileToAST(fs: List[String]): Program = fs match {
    case List(file) =>
      val (info, stmts) = fileToStmts(file)
      NF.makeProgram(info, NF.makeTopLevel(info, List(stmts)))
    case files =>
      val stmts = files.foldLeft(List[SourceElements]())((l, f) => {
        val (_, ss) = fileToStmts(f)
        l ++ List(ss)
      })
      NF.makeProgram(mergedSourceInfo, NF.makeTopLevel(mergedSourceInfo, stmts))
  }

  def parseScriptConvertExn(fileName: String, start: (Int, Int), script: String): Program =
    try {
      val (line, offset) = start
      val is = new ByteArrayInputStream(script.getBytes("UTF-8"))
      val ir = new InputStreamReader(is)
      val in = new BufferedReader(ir)
      val program = parsePgm(in, fileName, line)
      in.close; ir.close; is.close
      NU.addLinesProgram.addLines(program, line - 1, offset - 1).asInstanceOf[Program]
    } catch {
      case fnfe: FileNotFoundException =>
        throw convertExn(fnfe, fileName)
      case ioe: IOException =>
        throw convertExn(ioe)
    }

  /**
   * Parses a file as a program.
   * Converts checked exceptions like IOException and FileNotFoundException
   * to SyntaxError with appropriate error message.
   * Validates the parse by calling
   * parsePgm (see also description of exceptions there).
   */
  def parseFileConvertExn(file: File): Program =
    try {
      val filename = file.getCanonicalPath
      if (!filename.endsWith(".js"))
        throw new UserError("Need a JavaScript file instead of " + filename + ".")
      parsePgm(file, filename, 0)
    } catch {
      case fnfe: FileNotFoundException => throw convertExn(fnfe, file)
      case ioe: IOException => throw convertExn(ioe)
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
      } else throw new ParserError(parseResult.asInstanceOf[ParseError], parser, start)
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

  def convertExn(ioe: IOException): SyntaxError = {
    var desc = "Unable to read file"
    if (ioe.getMessage != null) desc += " (" + ioe.getMessage + ")"
    SAFEError.makeSyntaxError(desc)
  }

  def convertExn(fnfe: FileNotFoundException, f: File): SyntaxError =
    SAFEError.makeSyntaxError("Cannot find file " + f.getAbsolutePath)

  def convertExn(fnfe: FileNotFoundException, s: String): SyntaxError =
    SAFEError.makeSyntaxError("Cannot find file " + s)
}
