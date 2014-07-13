/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.tests

import junit.framework.TestSuite
import java.io.File

object WIDLParserJUTest {
  val SEP = File.separator
  val WIDLPARSER_FAIL_TESTS_DIR = "tests/widlparser_tests"

  def main(args: String*) = junit.textui.TestRunner.run(suite)

  def suite() = {
    val suite = new TestSuite("Test all .widl files in 'tests/widl_tests.")
    val failsOnly = true // false if we want to print out the test results
    //$JUnit-BEGIN$
    suite.addTest(FileTests.compilerSuite(WIDLPARSER_FAIL_TESTS_DIR, failsOnly, false))
    //$JUnit-END$
    suite
  }
}
