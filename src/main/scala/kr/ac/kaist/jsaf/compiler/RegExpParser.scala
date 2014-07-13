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

import xtc.parser.SemanticValue
import xtc.parser.ParseError

import kr.ac.kaist.jsaf.exceptions.JSAFError
import kr.ac.kaist.jsaf.exceptions.ParserError
import kr.ac.kaist.jsaf.exceptions.SyntaxError
import kr.ac.kaist.jsaf.nodes.RegExpPattern
import kr.ac.kaist.jsaf.parser.RegExp
import kr.ac.kaist.jsaf.useful.Files


object RegExpParser {
  class Result(pattern: Option[RegExpPattern], errors: List[SyntaxError])
        extends StaticPhaseResult(errors) {
    var patterns = pattern match {
        case None => Set[RegExpPattern]()
        case Some(p) => Set(p)
      }
  }

  /**
   * Parses a file as a pattern.
   * Converts checked exceptions like IOException and FileNotFoundException
   * to SyntaxError with appropriate error message.
   * Validates the parse by calling
   * parsePattern (see also description of exceptions there).
   */
  def parseFileConvertExn(file: File) =
    try {
      val filename = file.getCanonicalPath
      parsePattern(file, filename)
    } catch {
      case fnfe:FileNotFoundException =>
        throw convertExn(fnfe, file)
      case ioe:IOException =>
        throw convertExn(ioe, file)
    }

  def parsePattern(str: String, filename: String): RegExpPattern = {
    val sr = new StringReader(str)
    val in = new BufferedReader(sr)
    val result = parsePattern(in, filename)
    in.close; sr.close
    result
  }

  def parsePattern(file: File, filename: String): RegExpPattern = {
    val fs = new FileInputStream(file)
    val sr = new InputStreamReader(fs, Charset.forName("UTF-8"))
    val in = new BufferedReader(sr)
    val result = parsePattern(in, filename)
    in.close; sr.close; fs.close
    result
  }

  def parsePattern(in: BufferedReader, filename: String): RegExpPattern = {
    val syntaxLogFile = filename + ".log"
    try {
      val parser = new RegExp(in, filename)
      val parseResult = parser.pPattern(0)
      if (parseResult.hasValue) {
        parseResult.asInstanceOf[SemanticValue].value.asInstanceOf[RegExpPattern]
      } else throw new ParserError(parseResult.asInstanceOf[ParseError], parser, new JInteger(0))
    } finally {
      try {
        Files.rm(syntaxLogFile)
      } catch { case ioe:IOException => }
      try {
        in.close
      } catch { case ioe:IOException => }
    }
  }

  def convertExn(ioe: IOException, f: File) = {
    var desc = "Unable to read file"
    if (ioe.getMessage != null) desc += " (" + ioe.getMessage + ")"
    JSAFError.makeSyntaxError(desc)
  }

  def convertExn(fnfe: FileNotFoundException, f: File) =
    JSAFError.makeSyntaxError("Cannot find file " + f.getAbsolutePath)
}
