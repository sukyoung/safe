/*******************************************************************************
    Copyright (c) 2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.tests

import junit.framework.TestSuite
import _root_.java.io.File
import kr.ac.kaist.jsaf.ProjectProperties
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.ShellParameters
import kr.ac.kaist.jsaf.compiler.Predefined

object BugDetectorJUTest {
  val SEP = File.separator
  val BUGDETECTOR_FAIL_TESTS_DIR = "tests/bug_detector_tests"
  Shell.pred = new Predefined(new ShellParameters())

  def main(args: String*) = junit.textui.TestRunner.run(suite)

  def suite() = {
    val suite = new TestSuite("Test all .js files in 'tests/bug_detector_tests.")
    val failsOnly = true // false if we want to print out the test results
    //$JUnit-BEGIN$
    suite.addTest(FileTests.compilerSuite(BUGDETECTOR_FAIL_TESTS_DIR, failsOnly, false))
    //$JUnit-END$
    suite
  }
}
