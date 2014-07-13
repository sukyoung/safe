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
import kr.ac.kaist.jsaf.analysis.typing.Config

// class definition for eclipse JUnit runner
class TypingTizenTest

object TypingTizenTest {
  val TESTS_DIR = "tests/tizen_tests"
  Config.tizenMode = true
  Shell.pred = new Predefined(new ShellParameters())

  def main(args: String*) = junit.textui.TestRunner.run(suite)

  def suite(): Test = {
    val suite = new TestSuite("Typing Tizen Semantics Test")
    val testcases = collectTestcase(TESTS_DIR)
    for (tc <- testcases) {
      //$JUnit-BEGIN$
      suite.addTest(new SemanticsTest(TESTS_DIR, tc, "dense"))
      //$JUnit-END$
    }
    suite
  }

  private def collectTestcase(dirname: String) = {
    val dir = FileTests.directoryAsFile(dirname)
    val filtered = dir.list.toSeq.filter(fname =>
      fname.endsWith(".js"))
    filtered.sorted
  }
}