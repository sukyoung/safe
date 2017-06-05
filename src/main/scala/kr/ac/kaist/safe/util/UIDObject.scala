/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.util

// Object with a unique identifier.
class UIDObject {
  var uid: Long = UIDObject.getUId
  override def hashCode: Int = uid.asInstanceOf[Int] ^ (uid >>> 32).asInstanceOf[Int]
}

object UIDObject {
  private var prevUID = 0x7b546b0e12fd2559L
  private def getUId: Long = {
    /* LFSR generating 63-bit residues */
    prevUID *= 2
    prevUID ^= 0xb1463923a7c109cdL
    prevUID
  }
}
