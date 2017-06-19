/**
  * *****************************************************************************
  * Copyright (c) 2016, KAIST.
  * All rights reserved.
  *
  * Use is subject to license terms.
  *
  * This distribution may include materials developed by third parties.
  * ****************************************************************************
  */

package kr.ac.kaist.safe

import _root_.edu.rice.cs.plt.tuple.{ Option => JavaOption }
import junit.framework.TestCase
import kr.ac.kaist.safe.util.useful.Options

class OptionsJUTest() extends TestCase {
  def testEmptyToJavaOption() = {
    val none = JavaOption.none
    assert(
      Options.toOption(none) equals None,
      "Java nones are not mapped to Scala nones"
    )
  }

  def testNonEmptyToJavaOption() = {
    val some = JavaOption.some(1)
    assert(
      Options.toOption(some) equals Some(1),
      "Java somes are not mapped to Scala somes"
    )
  }
}
