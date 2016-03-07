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

package kr.ac.kaist.safe

import org.scalatest._
import org.scalatest.Assertions._
import java.io.File
import java.io.FilenameFilter
import kr.ac.kaist.safe.compiler.Parser
import kr.ac.kaist.safe.exceptions.ParserError
import kr.ac.kaist.safe.useful.Useful

class ParserSpec extends FlatSpec {
  val SEP = File.separator
  val dir = Config.basedir + SEP + "tests/parser_tests" + SEP
  val jsFilter = new FilenameFilter() {
    def accept(dir: File, name: String): Boolean = name.endsWith(".js")
  }

  def assertParserSucceeds(file: File): Unit =
    assert(new Parser.Result(Some(Parser.parseFileConvertExn(file)), List()) != null)

  def assertParserFails(file: File): Unit =
    intercept[ParserError] {
      new Parser.Result(Some(Parser.parseFileConvertExn(file)), List())
    }

  // Permute filenames for randomness
  for (filename <- Useful.shuffle(new File(dir).list(jsFilter))) {
    val file = new File(dir + filename)
    registerTest(file.getName) {
      if (file.getName.contains("XXX")) assertParserFails(file)
      else assertParserSucceeds(file)
    }
  }
}
