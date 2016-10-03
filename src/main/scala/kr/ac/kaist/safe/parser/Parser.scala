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
import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.errors.error.{ NotJSFileError, AlreadyMergedSourceError }
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.util.{ NodeUtil => NU, SourceLoc, Span }

object Parser {

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

  // Used by DynamicRewriter
  def stringToE(str: (String, (Int, Int), String)): Try[Expr] = {
    val (fileName, (line, offset), code) = str
    val sr = new StringReader(code)
    val in = new BufferedReader(sr)
    val ast = new JS(in, fileName).JSExpr(0).asInstanceOf[SemanticValue].value.asInstanceOf[Expr]
    val result = Try(NU.AddLinesWalker.addLines(
      ast,
      line - 1, offset - 1
    ))
    in.close; sr.close
    result
  }

  // Used by CoreTest
  def stringToAST(str: String): Try[(Program, ExcLog)] = {
    val sr = new StringReader(str)
    val in = new BufferedReader(sr)
    val parser = new JS(in, "stringParse")
    val result = rewriteDynamic(parser.JSmain(0)).map((_, parser.excLog))
    in.close; sr.close
    result
  }

  // Used by phase/Parse.scala
  def fileToAST(fs: List[String]): Try[(Program, ExcLog)] = fs match {
    case List(file) =>
      fileToStmts(file).map { case (s, e) => (Program(s.info, List(s)), e) }
    case files =>
      files.foldLeft(Try((List[SourceElements](), new ExcLog))) {
        case (res, f) => fileToStmts(f).flatMap {
          case (ss, ee) => res.flatMap { case (l, ex) => Try((l ++ List(ss), ex + ee)) }
        }
      }.map { case (s, e) => (Program(NU.MERGED_SOURCE_INFO, s), e) }
  }

  // Used by ast_rewriter/Hoister.scala
  def scriptToAST(ss: List[(String, (Int, Int), String)]): Try[(Program, ExcLog)] = ss match {
    case List(script) =>
      scriptToStmts(script).map { case (s, e) => (Program(s.info, List(s)), e) }
    case scripts =>
      scripts.foldLeft(Try((List[SourceElements](), new ExcLog))) {
        case (res, s) => scriptToStmts(s).flatMap {
          case (ss, ee) => res.flatMap { case (l, ex) => Try((l ++ List(ss), ex + ee)) }
        }
      }.map { case (s, e) => (Program(NU.MERGED_SOURCE_INFO, s), e) }
  }

  private def fileToStmts(f: String): Try[(SourceElements, ExcLog)] = {
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
      val pair = parsePgm(in, fileName, 0).flatMap { case (p, e) => getInfoStmts(p).map { (_, e) } }
      in.close; sr.close; fs.close
      pair
    }
  }

  private def parsePgm(in: BufferedReader, fileName: String, start: Int): Try[(Program, ExcLog)] = {
    val parser = new JS(in, fileName)
    rewriteDynamic(parser.JSmain(0)).map((_, parser.excLog))
  }

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

  private def scriptToStmts(script: (String, (Int, Int), String)): Try[(SourceElements, ExcLog)] = {
    val (fileName, (line, offset), code) = script
    val is = new ByteArrayInputStream(code.getBytes("UTF-8"))
    val ir = new InputStreamReader(is)
    val in = new BufferedReader(ir)
    val pair = parsePgm(in, fileName, line).flatMap { case (p, e) => getInfoStmts(NU.AddLinesProgram.addLines(p, line - 1, offset - 1)).map { (_, e) } }
    in.close; ir.close; is.close
    pair
  }
}
