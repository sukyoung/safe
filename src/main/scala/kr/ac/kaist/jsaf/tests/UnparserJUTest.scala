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

import kr.ac.kaist.jsaf.ProjectProperties
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.shell._
import kr.ac.kaist.jsaf.useful.TestCaseWrapper
import kr.ac.kaist.jsaf.useful.Useful
import kr.ac.kaist.jsaf.useful.WireTappedPrintStream
import kr.ac.kaist.jsaf.scala_src.useful.Arrays._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._

import junit.framework.Assert
import junit.framework.TestCase
import junit.framework.TestSuite

import java.nio.charset.Charset
import scala.util.control.Breaks._

object UnparserJUTest extends TestCaseWrapper {

  val SEP = File.separator
  val UNPARSER_FAIL_TESTS_DIR = ProjectProperties.BASEDIR + SEP + "tests/unparser_tests" + SEP
  def suite(): TestSuite =
    new UnparserTestSuite("UnparserJUTest", UNPARSER_FAIL_TESTS_DIR)

  class UnparserTestSuite(_name: String,  _failTestDir: String) extends TestSuite(_name) {
    val VERBOSE = true

    // relative to the top directory
    var failTestDir = _failTestDir
    val _ = addUnparserTests(failTestDir)

    def addUnparserTests(_dir: String): Unit = {
      var dir = _dir
      if (!dir.endsWith(SEP)) dir += SEP
      val jsFilter = new FilenameFilter() {
                       def accept(dir: File, name: String) = name.endsWith(".js")
                     }
      //Permute filenames for randomness
      for (filename <- shuffle(new File(dir).list(jsFilter)))
        addTest(new UnparserTestCase(new File(dir + filename)))

      // Navigate subdirectories
      val dirFilter = new FileFilter() {
                      def accept(file: File) =
                          (file.isDirectory && file.getName.charAt(0) != '.')
                      }

      for (subdir <- shuffle(new File(dir).listFiles(dirFilter)))
        addUnparserTests(dir + subdir.getName + SEP)
    }

    class UnparserTestCase(_file: File) extends TestCase(_file.getName) {
      var file = _file

      override def runTest() = {
        // do not print stuff to stdout for JUTests
        val oldOut = System.out
        val oldErr = System.err
        val wt_err = WireTappedPrintStream.make(System.err, true)
        val wt_out = WireTappedPrintStream.make(System.out, true)
        System.setErr(wt_err)
        System.setOut(wt_out)

        assertUnparserSucceeds(file)
        System.setErr(oldErr)
        System.setOut(oldOut)
      }

      def assertUnparserSucceeds(f: File) = {
        val temp_js = "__temp.js"
        val temp2_js = "__temp2.js"
        val temp_tjs = "__temp.tjs"
        val temp2_tjs = "__temp2.tjs"

        val file1 = new File(temp_js)
        val file2 = new File(temp2_js)
        val file3 = new File(temp_tjs)
        val file4 = new File(temp2_tjs)
        try {
          var diff=true
          //first parse
          ParseMain.parse(f.getPath, temp_tjs)
          //first unparse
          UnparseMain.unparse(new String(temp_tjs), temp_js)
          //second parse
          ParseMain.parse(new String(temp_js), temp2_tjs)
          //second unparse
          UnparseMain.unparse(new String(temp2_tjs), temp2_js)
          //compare temp.js, temp2.js
          val is: InputStream = new java.io.FileInputStream(file1)
          val br: BufferedReader = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")))
          val is2: InputStream = new java.io.FileInputStream(file2)
          val br2: BufferedReader = new BufferedReader(new InputStreamReader(is2, Charset.forName("UTF-8")))
          var a,b = -1
          br.readLine
          br2.readLine
          do {
            a = br.read
            b = br2.read
            if (a == -1) {
              if(b == -1) diff = false
            }
          } while (a == b && a != -1)
          br.close
          is.close
          br2.close
          is2.close
          Assert.assertFalse("Unparsing " + f + " is not idemponent.", diff)
        } finally {
          file1.delete
          file2.delete
          file3.delete
          file4.delete
        }
      }
    }
  }
}
