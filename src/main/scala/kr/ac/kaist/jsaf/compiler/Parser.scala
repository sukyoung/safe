/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.compiler

import _root_.java.io._
import _root_.java.lang.{Integer => JInteger}
import _root_.java.nio.charset.Charset
import _root_.java.util.{List => JList}

import xtc.parser.SemanticValue
import xtc.parser.ParseError

import kr.ac.kaist.jsaf.exceptions.JSAFError
import kr.ac.kaist.jsaf.exceptions.MultipleStaticError
import kr.ac.kaist.jsaf.exceptions.ParserError
import kr.ac.kaist.jsaf.exceptions.StaticError
import kr.ac.kaist.jsaf.exceptions.SyntaxError
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.JSONValue
import kr.ac.kaist.jsaf.nodes_util.{NodeFactory => NF}
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU}
import kr.ac.kaist.jsaf.nodes_util.SourceLoc
import kr.ac.kaist.jsaf.nodes_util.SourceLocRats
import kr.ac.kaist.jsaf.nodes_util.Span
import kr.ac.kaist.jsaf.parser.JS
import kr.ac.kaist.jsaf.parser.Json
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.useful.Files
import kr.ac.kaist.jsaf.useful.Triple
import kr.ac.kaist.jsaf.useful.Pair
import kr.ac.kaist.jsaf.useful.Useful


object Parser {
  class Result(pgm: Option[Program], errors: List[SyntaxError])
        extends StaticPhaseResult(errors) {
    var programs = pgm match {
        case None => Set[Program]()
        case Some(p) => Set(p)
      }
  }

  val mergedSourceLoc = new SourceLocRats(NU.freshFile("merged"), 0, 0, 0)
  val mergedSourceInfo = new ASTSpanInfo(new Span(mergedSourceLoc, mergedSourceLoc))
  var fileindex = 1

  def getInfoStmts(program: Program): (ASTSpanInfo, SourceElements) = {
    val info = program.getInfo
    if (program.getBody.getStmts.size == 1) {
      val ses = program.getBody.getStmts.get(0)
      (info, SSourceElements(info, (NF.makeNoOp(info, "StartOfFile"))+:toList(ses.getBody):+(NF.makeNoOp(info, "EndOfFile")), ses.isStrict))
    } else
      throw new UserError("Sources are already merged!")
  }

  def scriptToStmts(script: Triple[String, JInteger, String]): (ASTSpanInfo, SourceElements) =
    scriptToStmts(script, false)

  def scriptToStmts(script: Triple[String, JInteger, String],
                    isCloneDetector: Boolean): (ASTSpanInfo, SourceElements) = {
    val f = script.first
    val file = new File(f)
    fileindex += 1
    getInfoStmts(parseScriptConvertExn(f, script.second, script.third, isCloneDetector))
  }

  def fileToStmts(f: String): (ASTSpanInfo, SourceElements) = {
    val file = new File(f)
    var path = file.getCanonicalPath
    if(File.separatorChar == '\\') {
      // convert path string to linux style for windows
      path = path.charAt(0).toLower + path.replace('\\', '/').substring(1)
    }
    fileindex += 1
    getInfoStmts(parseFileConvertExn(file))
  }

  def stringToFnE(str: Triple[String, JInteger, String]): Option[FunExpr] = {
    val f = str.first
    val start = str.second
    val text = str.third
    val file = new File(f)
    val sr = new StringReader(text)
    val in = new BufferedReader(sr)
    val parser = new JS(in, f)
    val parseResult = parser.pJS$FunctionExpr(0)
    in.close; sr.close
    if (parseResult.hasValue) {
      val fe = parseResult.asInstanceOf[SemanticValue].value.asInstanceOf[FunExpr]
      Some(NU.addLinesWalker.addLines(fe, start-1).asInstanceOf[FunExpr])
    } else None
  }

  def stringToAST(str: Triple[String, JInteger, String]): Program =
    stringToAST(str, false)

  def stringToAST(str: Triple[String, JInteger, String],
                  isCloneDetector: Boolean): Program = {
    val (info, stmts) = scriptToStmts(str, isCloneDetector)
    NF.makeProgram(info, NF.makeTopLevel(info, List(stmts)))
  }

  def scriptToAST(ss: JList[Triple[String, JInteger, String]]): Program = toList(ss) match {
    case List(script) =>
      val (info, stmts) = scriptToStmts(script)
      NF.makeProgram(info, NF.makeTopLevel(info, List(stmts)))
    case scripts =>
      val stmts = scripts.foldLeft(List[SourceElements]())((l, s) => {
        val (_, ss) = scriptToStmts(s)
        l++List(ss)})
      NF.makeProgram(mergedSourceInfo, NF.makeTopLevel(mergedSourceInfo, stmts))
  }

  def fileToAST(fs: JList[String]): Program = toList(fs) match {
    case List(file) =>
      val (info, stmts) = fileToStmts(file)
      NF.makeProgram(info, NF.makeTopLevel(info, List(stmts)))
    case files =>
      val stmts = files.foldLeft(List[SourceElements]())((l, f) => {
        val (_, ss) = fileToStmts(f)
        l++List(ss)})
      NF.makeProgram(mergedSourceInfo, NF.makeTopLevel(mergedSourceInfo, stmts))
  }

  def parseScriptConvertExn(filename: String, start: JInteger, script: String): Program =
    parseScriptConvertExn(filename, start, script, false)

  def parseScriptConvertExn(filename: String, start: JInteger, script: String,
                            isCloneDetector: Boolean): Program =
    try {
      val is = new ByteArrayInputStream(script.getBytes("UTF-8"))
      val ir = new InputStreamReader(is)
      val in = new BufferedReader(ir)
      val program = parsePgm(in, filename, start-1, isCloneDetector)
      in.close; ir.close; is.close
      NU.addLinesWalker.addLines(program, start-1).asInstanceOf[Program]
    } catch {
      case fnfe:FileNotFoundException =>
        throw convertExn(fnfe, filename)
      case ioe:IOException =>
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
    parseFileConvertExn(file, false)

  def parseFileConvertExn(file: File, isCloneDetector: Boolean): Program =
    try {
      val filename = file.getCanonicalPath
      if (!filename.endsWith(".js"))
        throw new UserError("Need a JavaScript file instead of " + filename + ".")
        parsePgm(file, filename, new JInteger(0), isCloneDetector)
    } catch {
      case fnfe:FileNotFoundException =>
        throw convertExn(fnfe, file)
      case ioe:IOException =>
        throw convertExn(ioe)
    }

  def parsePgm(str: String, filename: String): Program = {
    val sr = new StringReader(str)
    val in = new BufferedReader(sr)
    val pgm = parsePgm(in, filename, new JInteger(0), false)
    in.close; sr.close
    pgm
  }

  def parsePgm(in: BufferedReader, filename: String): Program =
    parsePgm(in, filename, new JInteger(0), false)

  def parsePgm(file: File, filename: String, start: JInteger, isCloneDetector: Boolean): Program = {
    val fs = new FileInputStream(file)
    val sr = new InputStreamReader(fs, Charset.forName("UTF-8"))
    val in = new BufferedReader(sr)
    val pgm = parsePgm(in, filename, start, isCloneDetector)
    in.close; sr.close; fs.close
    pgm
  }

  def parsePgm(in: BufferedReader, filename: String, start: JInteger, isCloneDetector: Boolean): Program = {
    val syntaxLogFile = filename + ".log"
    try {
      val parser = new JS(in, filename)
      NU.setCloneDetector(isCloneDetector)
      val parseResult = parser.JSmain(0)
      if (parseResult.hasValue) {
        val result = parseResult.asInstanceOf[SemanticValue].value.asInstanceOf[Program]
        DynamicRewriter.doit(result)
      } else throw new ParserError(parseResult.asInstanceOf[ParseError], parser, start)
    } finally {
      try {
        Files.rm(syntaxLogFile)
      } catch { case ioe:IOException => }
      try {
        in.close
      } catch { case ioe:IOException => }
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

  def parseJSON(str: String): Boolean = {
    val sr = new StringReader(str)
    val in = new BufferedReader(sr)
    val parser = new Json(in, "ParseJSON")
    val parseResult = parser.pJSONText(0)
    in.close; sr.close
    parseResult.hasValue
  }

  def parseJSONApp(file: File): JSONValue = {
    val fs = new FileInputStream(file)
    val sr = new InputStreamReader(fs, Charset.forName("UTF-8"))
    val in = new BufferedReader(sr)
    val parser = new Json(in, "ParseJSONApp")
    val parseResult = parser.pJSONApp(0)
    in.close; sr.close; fs.close
    parseResult.hasValue
    parseResult.asInstanceOf[SemanticValue].value.asInstanceOf[JSONValue]
  }

  def convertExn(ioe: IOException) = {
    var desc = "Unable to read file"
    if (ioe.getMessage != null) desc += " (" + ioe.getMessage + ")"
    JSAFError.makeSyntaxError(desc)
  }

  def convertExn(fnfe: FileNotFoundException, f: File) =
    JSAFError.makeSyntaxError("Cannot find file " + f.getAbsolutePath)

  def convertExn(fnfe: FileNotFoundException, s: String) =
    JSAFError.makeSyntaxError("Cannot find file " + s)
}
