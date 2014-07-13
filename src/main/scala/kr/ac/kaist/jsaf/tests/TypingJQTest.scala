/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ***************************************************************************** */

package kr.ac.kaist.jsaf.tests

import kr.ac.kaist.jsaf.{ShellParameters, Shell}
import kr.ac.kaist.jsaf.compiler.Predefined
import junit.framework.{TestSuite, Test}

object TypingJQTest {
  val TESTS_DIR = "tests/typing_tests/jquery"
  //Shell.pred = new Predefined(new ShellParameters())
  Shell.params.Set(Array[String]("html", "-context-1-callsite", "-test", "-jq"))

  val EXCLUDE = Set(
    "XXX",
    "NYI"
  )

  def main(args: String*) = junit.textui.TestRunner.run(suite)

  def suite(): Test = {
    val suite = new TestSuite("jQuery Semantics Test")
    val testcases = collectTestcase(TESTS_DIR)
    for (tc <- testcases) {
      //$JUnit-BEGIN$
      suite.addTest(new SemanticsDOMTest(TESTS_DIR, tc, "dense"))
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
