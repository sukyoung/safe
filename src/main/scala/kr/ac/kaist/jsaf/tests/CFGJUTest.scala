/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.tests

import junit.framework.Test
import junit.framework.TestSuite
import _root_.java.io.File
import kr.ac.kaist.jsaf.ProjectProperties
import kr.ac.kaist.jsaf.Shell

// class definition for eclipse JUnit runner 
class CFGJUTest

object CFGJUTest {
  val SEP = File.separator
  val CFG_FAIL_TESTS_DIR = "tests/cfg_tests"

  def main(args: String*) = junit.textui.TestRunner.run(suite)

  def suite(): Test = {
    val suite = new TestSuite("Test all .js files in 'tests/cfg_tests.")
    val failsOnly = true // false if we want to print out the test results
    //$JUnit-BEGIN$
    suite.addTest(FileTests.compilerSuite(CFG_FAIL_TESTS_DIR, failsOnly, false))
    //$JUnit-END$
    suite
  }
}
