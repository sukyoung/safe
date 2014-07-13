/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.tests

import _root_.java.util.{List => JList}
import _root_.java.io.BufferedReader
import _root_.java.io.InputStreamReader
import _root_.java.io.InputStream
import _root_.java.io.File
import _root_.java.io.FileFilter
import _root_.java.io.FilenameFilter
import _root_.java.io.IOException
import _root_.java.io.PrintStream
import _root_.java.util.Arrays
import _root_.java.util.Collections
import _root_.java.util.Random

import kr.ac.kaist.jsaf.ProjectProperties
import kr.ac.kaist.jsaf.compiler.Parser
import kr.ac.kaist.jsaf.exceptions.SyntaxError
import kr.ac.kaist.jsaf.scala_src.useful.Arrays._
import kr.ac.kaist.jsaf.useful.Files
import kr.ac.kaist.jsaf.useful.TestCaseWrapper
import kr.ac.kaist.jsaf.useful.WireTappedPrintStream

import junit.framework.Assert
import junit.framework.TestCase
import junit.framework.TestSuite

object ParserJUTest extends TestCaseWrapper {

  val SEP = File.separator
  val PARSER_FAIL_TESTS_DIR = ProjectProperties.BASEDIR + SEP + "tests/parser_tests" + SEP

  def suite(): TestSuite =
    new ParserTestSuite("ParserJUTest", PARSER_FAIL_TESTS_DIR)

  class ParserTestSuite(_name: String,  _failTestDir: String) extends TestSuite(_name) {
    val VERBOSE = true

    // relative to the top directory
    var failTestDir = _failTestDir
    val _ = addParserTests(failTestDir)

    def addParserTests(_dir: String): Unit = {
      var dir = _dir
      if (!dir.endsWith(SEP)) dir += SEP
      val jsFilter = new FilenameFilter() {
                       def accept(dir: File, name: String) = name.endsWith(".js")
                     }
      // Permute filenames for randomness
      for (filename <- shuffle(new File(dir).list(jsFilter)))
        addTest(new ParserTestCase(new File(dir + filename)))

      // Navigate subdirectories
      val dirFilter = new FileFilter() {
                      def accept(file: File) =
                          (file.isDirectory && !file.getName.charAt(0).equals("."))
                      }

      // To save the testing time during development,
      // we skip parsing tests for the following directories.
      // They will be tested nightly.
      val skip = Set("ecma", "ecma_2", "ecma_3", "ecma_3_1", "ecma_5",
                     "js1_1", "js1_2", "js1_3", "js1_4", "js1_5", "js1_6",
                     "src", "sunspider_v0.9.1")
      for (subdir <- shuffle(new File(dir).listFiles(dirFilter));
           if (dir.endsWith("tests/parser_tests/js/src/tests/") &&
               ! skip.contains(subdir.getName)) ) {
        addParserTests(dir + subdir.getName + SEP)
      }
    }

    class ParserTestCase(_file: File) extends TestCase(_file.getName) {
      var file = _file

      override def runTest() = {
        // do not print stuff to stdout for JUTests
        val oldOut = System.out
        val oldErr = System.err
        val wt_err = WireTappedPrintStream.make(System.err, true)
        val wt_out = WireTappedPrintStream.make(System.out, true)
        System.setErr(wt_err)
        System.setOut(wt_out)

        if (file.getName.contains("XXX")) assertParserFails(file)
        else assertParserSucceeds(file)
        System.setErr(oldErr)
        System.setOut(oldOut)
      }

      def assertParserSucceeds(f: File) = {
        val result = parseFile(f)
        Assert.assertFalse("Source " + f + " was compiled with parser errors",
                           !result.isSuccessful)
      }

      def assertParserFails(f: File) =
        try {
          val result = parseFile(f)
          Assert.assertFalse("Source " + f + " was compiled with parser errors",
                             result.isSuccessful)
        } catch {
          case e:Throwable =>
        }

      def parseFile(f: File) =
        try {
          new Parser.Result(Some(Parser.parseFileConvertExn(f)), List())
        } catch {
          case se:SyntaxError => new Parser.Result(None, List(se))
        }
    }
  }
}
