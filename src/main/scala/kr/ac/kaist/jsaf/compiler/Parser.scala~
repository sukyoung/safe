/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
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

import java.util.HashMap
import kr.ac.kaist.jsaf.exceptions.JSAFError
import kr.ac.kaist.jsaf.exceptions.MultipleStaticError
import kr.ac.kaist.jsaf.exceptions.ParserError
import kr.ac.kaist.jsaf.exceptions.StaticError
import kr.ac.kaist.jsaf.exceptions.SyntaxError
import kr.ac.kaist.jsaf.exceptions.UserError
import kr.ac.kaist.jsaf.nodes.ASTNodeInfo
import kr.ac.kaist.jsaf.nodes.Comment
import kr.ac.kaist.jsaf.nodes.FunDecl
import kr.ac.kaist.jsaf.nodes.Program
import kr.ac.kaist.jsaf.nodes.SourceElement
import kr.ac.kaist.jsaf.nodes.SpanInfo
import kr.ac.kaist.jsaf.nodes.TopLevel
import kr.ac.kaist.jsaf.nodes.VarDecl
import kr.ac.kaist.jsaf.nodes_util.{NodeFactory => NF}
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU}
import kr.ac.kaist.jsaf.nodes_util.SourceLoc
import kr.ac.kaist.jsaf.nodes_util.SourceLocRats
import kr.ac.kaist.jsaf.nodes_util.Span
import kr.ac.kaist.jsaf.parser.JS
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

  val dummySourceLoc = new SourceLocRats(NU.freshFile("merged"), 0, 0, 0)
  val dummySourceInfo = new SpanInfo(new Span(dummySourceLoc, dummySourceLoc))
  var fileMap = new HashMap[String, String]()
  var fileindex = 1

  def getInfoStmtsComments(program: Program): (ASTNodeInfo, List[SourceElement], List[Comment]) = {
    val info = program.getInfo
    (info, toList(program.getBody.getStmts):+(NF.makeNoOp(info, "EndOfFile")),
     toList(program.getComments))
  }

  def scriptToStmts(script: Triple[String, JInteger, String]): (ASTNodeInfo, List[SourceElement], List[Comment]) =
    scriptToStmts(script, false)

  def scriptToStmts(script: Triple[String, JInteger, String],
                    isCloneDetector: Boolean): (ASTNodeInfo, List[SourceElement], List[Comment]) = {
    val f = script.first
    val file = new File(f)
    fileMap.put(file.getCanonicalPath, "%s::%d".format(f, fileindex))
    fileindex += 1
    getInfoStmtsComments(parseScriptConvertExn(f, script.second, script.third, isCloneDetector))
  }

  def clearFileMap() = fileMap.clear

  def fileToStmts(f: String) = {
    val file = new File(f)
    var path = file.getCanonicalPath
    if(File.separatorChar == '\\') {
      // convert path string to linux style for windows
      path = path.charAt(0).toLower + path.replace('\\', '/').substring(1)
    }
    fileMap.put(path, "%s::%d".format(f, fileindex))
    fileindex += 1
    getInfoStmtsComments(parseFileConvertExn(file))
  }

  def stringToAST(str: Triple[String, JInteger, String]): Program =
    stringToAST(str, false)

  def stringToAST(str: Triple[String, JInteger, String],
                  isCloneDetector: Boolean): Program = {
    val (info, stmts, comments) = scriptToStmts(str, isCloneDetector)
    NF.makeProgram(info, stmts, comments)
  }

  def scriptToAST(ss: JList[Triple[String, JInteger, String]]) = toList(ss) match {
    case List(script) =>
      val (info, stmts, comments) = scriptToStmts(script)
      new Pair[Program, HashMap[String,String]](NF.makeProgram(info, stmts, comments), fileMap)
    case scripts =>
      val (stmts, comments) =
          scripts.foldLeft((List[SourceElement](), List[Comment]()))((l, s) => {
                          val (i, ss, cs) = scriptToStmts(s)
                          (l._1++ss,  l._2++cs)})
      new Pair[Program, HashMap[String,String]](NF.makeProgram(dummySourceInfo, stmts, comments), fileMap)
  }

  def fileToAST(fs: JList[String]) = toList(fs) match {
    case List(file) =>
      val (info, stmts, comments) = fileToStmts(file)
      new Pair[Program, HashMap[String,String]](NF.makeProgram(info, stmts, comments), fileMap)
    case files =>
      val (stmts, comments) =
          files.foldLeft((List[SourceElement](), List[Comment]()))((l, f) => {
                        val (i, ss, cs) = fileToStmts(f)
                        (l._1++ss,  l._2++cs)})
      new Pair[Program, HashMap[String,String]](NF.makeProgram(dummySourceInfo, stmts, comments), fileMap)
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
    val commentLogFile = filename + ".comment"
    try {
      val parser = new JS(in, filename)
      NU.setCloneDetector(isCloneDetector)
      val parseResult = parser.JSmain(0)
      if (parseResult.hasValue) {
        parseResult.asInstanceOf[SemanticValue].value.asInstanceOf[Program]
      } else throw new ParserError(parseResult.asInstanceOf[ParseError], parser, start)
    } finally {
      try {
        Files.rm(syntaxLogFile)
        Files.rm(commentLogFile)
      } catch { case ioe:IOException => }
      try {
        in.close
      } catch { case ioe:IOException => }
    }
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
