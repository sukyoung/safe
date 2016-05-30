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

package kr.ac.kaist.safe.util

import kr.ac.kaist.safe.nodes.ASTNode

// Object with a unique identifier.
// Every Span, AST node, IR node, and CFG node extends UIDObject.
class UIDObject {
  /* LFSR generating 63-bit residues */
  var uid: Long = {
    var x = UIDObject.prevUID
    x *= 2
    if (x < 0) x ^= 0xb1463923a7c109cdL
    UIDObject.prevUID = x
    x
  }
  override def hashCode: Int = uid.asInstanceOf[Int] ^ (uid >>> 32).asInstanceOf[Int]
}

object UIDObject {
  private val seedUID = 0x7b546b0e12fd2559L
  private var prevUID = seedUID
}
