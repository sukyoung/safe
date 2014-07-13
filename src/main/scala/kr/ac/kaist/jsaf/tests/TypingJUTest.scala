/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
package kr.ac.kaist.jsaf.tests

import junit.framework.Test
import junit.framework.TestSuite

// class definition for eclipse JUnit runner 
class TypingJUTest

object TypingJUTest {
  def main(args: String*) = junit.textui.TestRunner.run(suite)

  def suite(): Test = {
    val suite = new TestSuite("Typing Test")
    //$JUnit-BEGIN$
    suite.addTest(TypingSemanticsJUTest.suite)
    suite.addTest(TypingOperatorJUTest.suite)
    suite.addTest(TypingTAJSMicroJUTest.suite)
    //$JUnit-END$
    suite
  }
}
