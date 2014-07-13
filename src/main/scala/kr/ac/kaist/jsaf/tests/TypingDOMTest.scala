/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.jsaf.tests

import junit.framework.Test
import junit.framework.TestSuite
import kr.ac.kaist.jsaf.compiler.Predefined
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.ShellParameters

class TypingDOMTest {

}

object TypingDOMTest {
  val TESTS_DIR = "tests/html_tests/domtest"
  Shell.pred = new Predefined(new ShellParameters())

  val EXCLUDE = Set(
    "XXX",
    "NYI"
  )

  def main(args: String*) = junit.textui.TestRunner.run(suite)

  def suite(): Test = {
    val suite = new TestSuite("DOM Semantics Test")
    val testcases = collectTestcase(TESTS_DIR)
    for (tc <- testcases) {
      //$JUnit-BEGIN$
      suite.addTest(new SemanticsDOMTest(TESTS_DIR, tc, "sparse"))
      //$JUnit-END$
    }
    suite
  }

  private def collectTestcase(dirname: String) = {
    val dir = FileTests.directoryAsFile(dirname)
    val filtered = dir.list.toSeq.filter(fname =>
      fname.endsWith(".html") &&
        !EXCLUDE.exists(prefix => fname.startsWith(prefix)))
    filtered.sorted
  }
}