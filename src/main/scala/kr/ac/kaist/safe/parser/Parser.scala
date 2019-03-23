/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
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
import scala.util.{ Random, Try, Success, Failure }
import xtc.parser.{ Result, ParseError, SemanticValue }
import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.errors.error.{ ParserError, NotJSFileError, AlreadyMergedSourceError }
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.util.{ NodeUtil => NU, _ }

object Parser {
  // Used by DynamicRewriter
  def stringToFnE(str: (String, (Int, Int), String)): Try[(FunExpr, ExcLog)] = {
    val (fileName, (line, offset), code) = str
    val sr = new StringReader(code)
    val in = new BufferedReader(sr)
    val funE = resultToAST[FunExpr](new JS(in, fileName), _.JSFunctionExpr(0))
    val result = funE.map {
      case (e, log) =>
        (NU.AddLinesWalker.addLines(e, line - 1, offset - 1), log)
    }
    in.close; sr.close
    result
  }

  def normalized(s: String): String = s.replaceAll("\\s+", "").replaceAll("\\n+", "")
  // Used by DynamicRewriter
  def stringToE(str: (String, (Int, Int), String)): Try[(Expr, ExcLog)] = {
    val (fileName, (line, offset), code) = str
    val sr = new StringReader(code)
    val in = new BufferedReader(sr)
    val expr = resultToAST[Expr](new JS(in, fileName), _.JSExpr(0))
    val result = expr match {
      case Success((e, log)) =>
        if (normalized(e.toString(0)) == normalized(code))
          Try((NU.AddLinesWalker.addLines(e, line - 1, offset - 1), log))
        else
          Failure(ParserError("eval with an unsupported argument", e.info.span))
      case fail => fail
    }
    in.close; sr.close
    result
  }

  // Used by DynamicRewriter
  def stringToLHS(str: (String, (Int, Int), String)): Try[(LHS, ExcLog)] = {
    val (fileName, (line, offset), code) = str
    val sr = new StringReader(code)
    val in = new BufferedReader(sr)
    val lhs = resultToAST[LHS](new JS(in, fileName), _.JSLHS(0))
    val result = lhs.map {
      case (e, log) =>
        (NU.AddLinesWalker.addLines(e, line - 1, offset - 1), log)
    }
    in.close; sr.close
    result
  }

  // Used by tests
  def stringToAST(str: String): Try[(Program, ExcLog)] = {
    val sr = new StringReader(str)
    val in = new BufferedReader(sr)
    val rand = Random.alphanumeric.take(16).mkString
    val pgm = resultToAST[Program](new JS(in, s"stringParse_$rand"), _.JSmain(0))
    val result = pgm.map { case (e, log) => (DynamicRewriter(e), log) }
    in.close; sr.close
    result
  }

  // Used by phase/Parse.scala
  def fileToAST(fs: List[String]): Try[(Program, ExcLog)] = fs match {
    case List(file) =>
      fileToStmts(file).flatMap {
        case (s, e) =>
          {
            val program = Program(s.info, List(s))
            Try(program, e)
          }
      }
    case files =>
      files.foldLeft(Try((List[Stmts](), new ExcLog))) {
        case (res, f) => fileToStmts(f).flatMap {
          case (ss, ee) => res.flatMap { case (l, ex) => Try((l ++ List(ss), ex + ee)) }
        }
      }.flatMap {
        case (s, e) => {
          val program = Program(NU.MERGED_SOURCE_INFO, s)
          Try(program, e)
        }
      }
  }

  // Used by parser/JSFromHTML.scala
  def scriptToAST(ss: List[(String, (Int, Int), String)]): Try[(Stmts, ExcLog)] = ss match {
    case List(script) =>
      scriptToStmts(script)
    case scripts =>
      scripts.foldLeft(Try((List[Stmt](), new ExcLog))) {
        case (res, s) => scriptToStmts(s).flatMap {
          case (ss, ee) => res.flatMap { case (l, ex) => Try((l ++ ss.body, ex + ee)) }
        }
      }.map { case (s, e) => (Stmts(NU.MERGED_SOURCE_INFO, s, false), e) }
  }

  private def resultToAST[T <: ASTNode](
    parser: JS,
    doit: JS => Result
  ): Try[(T, ExcLog)] = {
    doit(parser) match {
      case (result: ParseError) =>
        val span = decodeSpan(parser.format(result))
        Failure(ParserError(result.msg, span))
      case (semV: SemanticValue) => Try((semV.value.asInstanceOf[T], parser.excLog))
      case _ => Failure(new Error()) // TODO more exact error type
    }
  }

  // xtc.parser.ParserBase
  // public final String format(ParseError error) throws IOException
  private def decodeSpan(formatted: String): Span = {
    val array = formatted.split(":")
    val (file, line, column) = (array(0), array(1).toInt, array(2).toInt)
    val loc = new SourceLoc(line, column, 0)
    new Span(file, loc, loc)
  }

  private def fileToStmts(f: String): Try[(Stmts, ExcLog)] = {
    var fileName = new File(f).getCanonicalPath
    if (File.separatorChar == '\\') {
      // convert path string to linux style for windows
      fileName = fileName.charAt(0).toLower + fileName.replace('\\', '/').substring(1)
    }
    FileKind(fileName) match {
      case JSFile | JSErrFile => {
        val fs = new FileInputStream(new File(f))
        val sr = new InputStreamReader(fs, Charset.forName("UTF-8"))
        val in = new BufferedReader(sr)
        val pair = parsePgm(in, fileName, 0).flatMap {
          case (p, e) => getInfoStmts(p).map((_, e))
        }
        in.close; sr.close; fs.close
        pair
      }
      case HTMLFile => JSFromHTML.parseScripts(fileName)
      case JSTodoFile | NormalFile => Failure(NotJSFileError(fileName))
    }
  }

  private def parsePgm(in: BufferedReader, fileName: String, start: Int): Try[(Program, ExcLog)] = {
    val pgm = resultToAST[Program](new JS(in, fileName), _.JSmain(0))
    pgm.map { case (e, log) => (DynamicRewriter(e), log) }
  }

  private def getInfoStmts(program: Program): Try[Stmts] = {
    val info = program.info
    if (program.body.stmts.size == 1) {
      val ses = program.body.stmts.head
      Try(Stmts(info, (NoOp(info, "StartOfFile")) +: ses.body :+ (NoOp(info, "EndOfFile")), ses.strict))
    } else Failure(AlreadyMergedSourceError(info.span))
  }

  private def scriptToStmts(script: (String, (Int, Int), String)): Try[(Stmts, ExcLog)] = {
    val (fileName, (line, offset), code) = script
    val is = new ByteArrayInputStream(code.getBytes("UTF-8"))
    val ir = new InputStreamReader(is)
    val in = new BufferedReader(ir)
    val pair = parsePgm(in, fileName, line).flatMap { case (p, e) => getInfoStmts(NU.AddLinesProgram.addLines(p, line - 1, offset - 1)).map { (_, e) } }
    in.close; ir.close; is.close
    pair
  }
}
