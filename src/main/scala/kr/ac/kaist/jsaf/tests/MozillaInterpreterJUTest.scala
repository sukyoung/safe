/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.tests

import junit.framework.TestSuite
import _root_.java.io.File
import _root_.java.io.FileFilter
import kr.ac.kaist.jsaf.ProjectProperties
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.scala_src.useful.Arrays._

object MozillaInterpreterJUTest {
  val SEP = File.separator
  val MOZILLA_INTERPRETER_FAIL_TESTS_DIR = "tests/interpreter_mozilla_tests" + SEP

  def main(args: String*) = junit.textui.TestRunner.run(suite)

  def suite() = {
    val suite = new TestSuite("Test all .js files in 'tests/interpreter_mozilla_tests.")
    val failsOnly = true // false if we want to print out the test results
    def addMozillaInterpreterTests(_dir: String): Unit = {
      var dir = _dir
      if (!dir.endsWith(SEP)) dir += SEP
      suite.addTest(FileTests.compilerSuite(dir, failsOnly, false))
      // Navigate subdirectories
      val dirFilter = new FileFilter() {
                        def accept(file: File) =
                            (file.isDirectory && !file.getName.charAt(0).equals(".") && !file.getName.equals("NPY"))
                        }
      for (subdir <- shuffle(new File(dir).listFiles(dirFilter)))
        addMozillaInterpreterTests(dir + subdir.getName + SEP)
    }
    val _ = addMozillaInterpreterTests(MOZILLA_INTERPRETER_FAIL_TESTS_DIR)
    suite
  }
}
