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

import junit.framework.TestCase
import kr.ac.kaist.safe.util.useful.Lists

class ListsJUTest() extends TestCase {
  def testEmptyToJavaList() = {
    val xs = List[Int]()
    assert(
      Lists.toJavaList(xs).isEmpty,
      "Empty Scala lists are not mapped to empty Java lists"
    )
  }
}