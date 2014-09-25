/******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
******************************************************************************/

package kr.ac.kaist.jsaf.tests

import java.io._

import kr.ac.kaist.jsaf.ProjectProperties
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.ShellParameters
import kr.ac.kaist.jsaf.nodes.Program
import kr.ac.kaist.jsaf.nodes_util.ASTIO
import kr.ac.kaist.jsaf.nodes_util.JSAstToConcrete
import kr.ac.kaist.jsaf.ts.TSToString
import kr.ac.kaist.jsaf.shell.TSMain
import kr.ac.kaist.jsaf.useful.Files
import kr.ac.kaist.jsaf.useful.TestCaseWrapper
import kr.ac.kaist.jsaf.useful.Useful
import kr.ac.kaist.jsaf.useful.WireTappedPrintStream
import kr.ac.kaist.jsaf.scala_src.useful.Arrays._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._

import junit.framework.Assert
import junit.framework.TestCase
import junit.framework.TestSuite

object TSJUTest extends TestCaseWrapper {

  val SEP = File.separator
  val TYPESCRIPT_FAIL_TESTS_DIR = "tests/ts_tests"

  def suite(): TestSuite =
    new TSTestSuite("TSJUTest", TYPESCRIPT_FAIL_TESTS_DIR)

  class TSTestSuite(_name: String, _failTestDir: String) extends TestSuite(_name) {
    val VERBOSE = true

    // relative to the top directory
    var failTestDir = _failTestDir
    val _ = addTSTests(failTestDir)

    def addTSTests(_dir: String): Unit = {
      var dir = _dir
      if (!dir.endsWith(SEP)) dir += SEP
      val tsFilter = new FilenameFilter() {
        def accept(dir: File, name: String) = name.endsWith(".d.ts")
      }
      //Permute filenames for randomness
      for (filename <- shuffle(new File(dir).list(tsFilter)))
        addTest(new TSTestCase(new File(dir + filename)))

      // Navigate subdirectories
      val dirFilter = new FileFilter() {
        def accept(file: File) =
          (file.isDirectory && file.getName.charAt(0) != '.' && !file.getName.equals("fails"))
      }

      for (subdir <- shuffle(new File(dir).listFiles(dirFilter)))
        addTSTests(dir + subdir.getName + SEP)
    }

    class TSTestCase(_file: File) extends TestCase(_file.getName) {
      var file = _file

      override def runTest() = {
        assertTSSucceeds(file)
      }

      def assertTSSucceeds(file: File) = {
        val fileName = file.getName
        try {
          val orgPgm: Program = TSMain.parseTs(file)

          // test parsing & unparsing
          val orgStr: String = TSToString.doit(orgPgm)
          /* for debugging
          val pair = Useful.filenameToBufferedWriter(fileName+"_parsed.d.ts")
          val writer = pair.second
          writer.write(orgStr)
          writer.close
          pair.first.close
          */
          val parsedPgm: Program = TSMain.parseTs(orgStr, fileName+"_parsed.d.ts")
          val parsedStr: String = TSToString.doit(parsedPgm)
          if (!orgStr.equals(parsedStr))
            System.out.println("Failed testing parse/unparse: " + fileName)

          // test printing & unprinting
          /*
          ASTIO.writeJavaAst(orgPgm, fileName+".tts")
          val result = ASTIO.readJavaAst(fileName+".tts")
          if (result.isSome) {
            val readStr: String = TSToString.doit(result.unwrap)
            if (!orgStr.equals(readStr))
              System.out.println("Failed testing print/unprint: " + fileName)
          } else System.out.println("Error! Reading the " + fileName + ".tts file failed!")
	  */
        } finally {
          try {
            Files.rm(fileName+".log")
            //Files.rm(fileName+"_parsed.d.ts")
            Files.rm(fileName+"_parsed.d.ts.log")
            Files.rm(fileName+".tts")
          } catch { case ioe:IOException => }
        }
      }
    }
  }
}
