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

object NightlyInterpreterJUTest {
  //val SEP = File.separator
  //val INTERPRETER_FAIL_TESTS_DIR = "tests/interpreter_tests"
  val INTERPRETER_NIGHTLY_FAIL_TESTS_DIR = "tests/interpreter_nightly_tests"

  def main(args: String*) = junit.textui.TestRunner.run(suite)

  def suite() = {
    val suite = new TestSuite("Test all .js files in 'tests/interpreter_nightly_tests.")
    val failsOnly = true // false if we want to print out the test results

    def addTestDir(_dir: String): Unit = {
      suite.addTest(FileTests.compilerSuite(_dir, failsOnly, false))

      var dir = _dir + '/'

      // Navigate subdirectories
      val dirFilter = new FileFilter() {
                      def accept(file: File) =
                          (file.isDirectory && file.getName.charAt(0) != '.')
                      }

      for (subdir <- new File(dir).listFiles(dirFilter))
        addTestDir(dir + subdir.getName)
    }

    //$JUnit-BEGIN$
    //suite.addTest(FileTests.compilerSuite(INTERPRETER_FAIL_TESTS_DIR, failsOnly, false))
    addTestDir(INTERPRETER_NIGHTLY_FAIL_TESTS_DIR)
    //$JUnit-END$
    suite
  }
}
