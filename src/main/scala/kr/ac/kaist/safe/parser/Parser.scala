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
import xtc.parser.{ Result, ParseError, SemanticValue }
import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.errors.error.{ ParserError, NotJSFileError, AlreadyMergedSourceError }
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.util.{ NodeUtil => NU, SourceLoc, Span }

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

  // Used by DynamicRewriter
  def stringToE(str: (String, (Int, Int), String)): Try[(Expr, ExcLog)] = {
    val (fileName, (line, offset), code) = str
    val sr = new StringReader(code)
    val in = new BufferedReader(sr)
    val expr = resultToAST[Expr](new JS(in, fileName), _.JSExpr(0))
    val result = expr.map {
      case (e, log) =>
        (NU.AddLinesWalker.addLines(e, line - 1, offset - 1), log)
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

  // Used by CoreTest
  def stringToAST(str: String): Try[(Program, ExcLog)] = {
    val sr = new StringReader(str)
    val in = new BufferedReader(sr)
    val pgm = resultToAST[Program](new JS(in, "stringParse"), _.JSmain(0))
    val result = pgm.map { case (e, log) => (DynamicRewriter(e), log) }
    in.close; sr.close
    result
  }

  // Used by phase/Parse.scala
  def fileToAST(fs: List[String], jsModel: Boolean): Try[(Program, ExcLog)] = fs match {
    case List(file) =>
      fileToStmts(file).flatMap {
        case (s, e) =>
          {
            val program = Program(s.info, List(s))
            if (jsModel) addJSModel(program, e)
            else Try(program, e)
          }
      }
    case files =>
      files.foldLeft(Try((List[SourceElements](), new ExcLog))) {
        case (res, f) => fileToStmts(f).flatMap {
          case (ss, ee) => res.flatMap { case (l, ex) => Try((l ++ List(ss), ex + ee)) }
        }
      }.flatMap {
        case (s, e) => {
          val program = Program(NU.MERGED_SOURCE_INFO, s)
          if (jsModel) addJSModel(program, e)
          else Try(program, e)
        }
      }
  }

  // Used by parser/JSFromHTML.scala
  def scriptToAST(ss: List[(String, (Int, Int), String)]): Try[(SourceElements, ExcLog)] = ss match {
    case List(script) =>
      scriptToStmts(script)
    case scripts =>
      scripts.foldLeft(Try((List[SourceElement](), new ExcLog))) {
        case (res, s) => scriptToStmts(s).flatMap {
          case (ss, ee) => res.flatMap { case (l, ex) => Try((l ++ ss.body, ex + ee)) }
        }
      }.map { case (s, e) => (SourceElements(NU.MERGED_SOURCE_INFO, s, false), e) }
  }

  // concatenate ASTs modeled in JavaScript
  private def addJSModel(program: Program, excLog: ExcLog): Try[(Program, ExcLog)] = {
    fileToAST(NU.jsModels, false).map {
      case (p, e) =>
        (p, program) match {
          case (Program(_, TopLevel(_, fds0, vds0, body0)), Program(info, TopLevel(_, fds1, vds1, body1))) =>
            (Program(info, TopLevel(info, fds0 ++ fds1, vds0 ++ vds1, body0 ++ body1)),
              e + excLog)
        }
    }
  }

  // remove ASTs modeled in JavaScript
  def removeJSModel(program: Program): Program = program match {
    case Program(info1, TopLevel(info2, fds, vds, body)) =>
      Program(info1, TopLevel(info2, removeJSModelFds(fds), removeJSModelVds(vds), removeJSModelSes(body)))
  }

  private def removeJSModelFds(list: List[FunDecl]): List[FunDecl] =
    list.foldLeft(List[FunDecl]())((r, fd) => if (NU.isModeled(fd)) r else r ++ List(fd))

  private def removeJSModelVds(list: List[VarDecl]): List[VarDecl] =
    list.foldLeft(List[VarDecl]())((r, vd) => if (NU.isModeled(vd)) r else r ++ List(vd))

  private def removeJSModelSes(list: List[SourceElements]): List[SourceElements] =
    list.foldLeft(List[SourceElements]())((r, se) => if (NU.isModeled(se)) r else r ++ List(se))

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

  private def fileToStmts(f: String): Try[(SourceElements, ExcLog)] = {
    var fileName = new File(f).getCanonicalPath
    if (File.separatorChar == '\\') {
      // convert path string to linux style for windows
      fileName = fileName.charAt(0).toLower + fileName.replace('\\', '/').substring(1)
    }
    if (fileName.endsWith(".js") || fileName.endsWith(".js.err")) {
      val fs = new FileInputStream(new File(f))
      val sr = new InputStreamReader(fs, Charset.forName("UTF-8"))
      val in = new BufferedReader(sr)
      val pair = parsePgm(in, fileName, 0).flatMap { case (p, e) => getInfoStmts(p).map { (_, e) } }
      in.close; sr.close; fs.close
      pair
    } else if (fileName.endsWith(".html") || fileName.endsWith(".xhtml") || fileName.endsWith(".htm")) {
      JSFromHTML.parseScripts(fileName)
    } else {
      Failure(NotJSFileError(fileName))
    }
  }

  private def parsePgm(in: BufferedReader, fileName: String, start: Int): Try[(Program, ExcLog)] = {
    val pgm = resultToAST[Program](new JS(in, fileName), _.JSmain(0))
    pgm.map { case (e, log) => (DynamicRewriter(e), log) }
  }

  private def getInfoStmts(program: Program): Try[SourceElements] = {
    val info = program.info
    if (program.body.stmts.size == 1) {
      val ses = program.body.stmts.head
      Try(SourceElements(info, (NoOp(info, "StartOfFile")) +: ses.body :+ (NoOp(info, "EndOfFile")), ses.strict))
    } else Failure(AlreadyMergedSourceError(info.span))
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
