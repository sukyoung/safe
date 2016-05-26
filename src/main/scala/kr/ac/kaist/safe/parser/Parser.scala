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
import scala.util.{ Try, Success, Failure }
import xtc.parser.Result
import xtc.parser.SemanticValue
import kr.ac.kaist.safe.errors.error.{ NotJSFileError, AlreadyMergedSourceError }
import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.util.{ NodeUtil => NU, SourceLoc, Span }

object Parser {
  val MERGED_SOURCE_LOC = new SourceLoc(NU.freshFile("Merged"), 0, 0, 0)
  val MERGED_SOURCE_INFO = new ASTNodeInfo(new Span(MERGED_SOURCE_LOC, MERGED_SOURCE_LOC))

  // Used by DynamicRewriter
  def stringToFnE(str: (String, (Int, Int), String)): Try[FunExpr] = {
    val (fileName, (line, offset), code) = str
    val sr = new StringReader(code)
    val in = new BufferedReader(sr)
    val result = Try(NU.AddLinesWalker.addLines(
      new JS(in, fileName).JSFunctionExpr(0).asInstanceOf[SemanticValue].value.asInstanceOf[FunExpr],
      line - 1, offset - 1
    ))
    in.close; sr.close
    result
  }

  // Used by CoreTest
  def stringToAST(str: String): Try[Program] = {
    val sr = new StringReader(str)
    val in = new BufferedReader(sr)
    val result = rewriteDynamic(new JS(in, "stringParse").JSmain(0))
    in.close; sr.close
    result
  }

  // Used by phase/Parse.scala
  def fileToAST(fs: List[String]): Try[Program] = fs match {
    case List(file) =>
      fileToStmts(file).map(s => Program(s.info, List(s)))
    case files =>
      Try(files.foldLeft(List[SourceElements]())((l, f) => l ++ List(fileToStmts(f).get))).
        map(Program(MERGED_SOURCE_INFO, _))
  }

  // Used by ast_rewriter/Hoister.scala
  def scriptToAST(ss: List[(String, (Int, Int), String)]): Try[Program] = ss match {
    case List(script) =>
      scriptToStmts(script).map(s => Program(s.info, List(s)))
    case scripts =>
      Try(scripts.foldLeft(List[SourceElements]())((l, s) => l ++ List(scriptToStmts(s).get))).
        map(Program(MERGED_SOURCE_INFO, _))
  }

  private def fileToStmts(f: String): Try[SourceElements] = {
    var fileName = new File(f).getCanonicalPath
    if (File.separatorChar == '\\') {
      // convert path string to linux style for windows
      fileName = fileName.charAt(0).toLower + fileName.replace('\\', '/').substring(1)
    }
    if (!fileName.endsWith(".js"))
      Failure(NotJSFileError(fileName))
    else {
      val fs = new FileInputStream(new File(f))
      val sr = new InputStreamReader(fs, Charset.forName("UTF-8"))
      val in = new BufferedReader(sr)
      val stmts = parsePgm(in, fileName, 0).flatMap(getInfoStmts(_))
      in.close; sr.close; fs.close
      stmts
    }
  }

  private def parsePgm(in: BufferedReader, fileName: String, start: Int): Try[Program] =
    rewriteDynamic(new JS(in, fileName).JSmain(0))

  private def rewriteDynamic(parseResult: Result): Try[Program] =
    Try(DynamicRewriter(parseResult.asInstanceOf[SemanticValue].value.asInstanceOf[Program]))

  private def getInfoStmts(program: Program): Try[SourceElements] = {
    val info = program.info
    if (program.body.stmts.size == 1) {
      val ses = program.body.stmts.head
      Try(SourceElements(info, (NoOp(info, "StartOfFile")) +: ses.body :+ (NoOp(info, "EndOfFile")), ses.strict))
    } else
      Failure(AlreadyMergedSourceError(info.span))
  }

  private def scriptToStmts(script: (String, (Int, Int), String)): Try[SourceElements] = {
    val (fileName, (line, offset), code) = script
    val is = new ByteArrayInputStream(code.getBytes("UTF-8"))
    val ir = new InputStreamReader(is)
    val in = new BufferedReader(ir)
    val stmts = parsePgm(in, fileName, line).flatMap(p => getInfoStmts(NU.AddLinesProgram.addLines(p, line - 1, offset - 1)))
    in.close; ir.close; is.close
    stmts
  }
}
