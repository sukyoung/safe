/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
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
import kr.ac.kaist.jsaf.analysis.typing.AddressManager

// class definition for eclipse JUnit runner 
class TypingSemanticsJUTest

object TypingSemanticsJUTest {
  val TESTS_DIR = "tests/typing_tests/semantics"
  Shell.pred = new Predefined(new ShellParameters())

  val EXCLUDE = Set(
    "XXX",
    "NYI"
  ) 

  def main(args: String*) = junit.textui.TestRunner.run(suite)

  def suite(): Test = {
    // Initialize AddressManager
    AddressManager.reset()

    val suite = new TestSuite("Typing Semantics Test")
    val testcases = collectTestcase(TESTS_DIR)
    for (tc <- testcases) {
      //$JUnit-BEGIN$
      suite.addTest(new SemanticsTest(TESTS_DIR, tc, "sparse"))
      //$JUnit-END$
    }
    suite
  }
  
  private def collectTestcase(dirname: String) = {
    val dir = FileTests.directoryAsFile(dirname)
    val filtered = dir.list.toSeq.filter(fname =>
      fname.endsWith(".js") &&
      !EXCLUDE.exists(prefix => fname.startsWith(prefix)))
    filtered.sorted
  }
}
