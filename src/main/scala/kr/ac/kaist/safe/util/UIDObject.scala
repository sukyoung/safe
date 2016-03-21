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
import kr.ac.kaist.safe.errors.ErrorMsgMaker

class UIDObject {
  private var uid: Long = next
  override def hashCode: Int = uid.asInstanceOf[Int] ^ (uid >>> 32).asInstanceOf[Int]

  override def toString: String =
    if (this.isInstanceOf[ASTNode])
      ErrorMsgMaker.makeErrorMsg(this.asInstanceOf[ASTNode])
    else super.toString

  def at: String =
    if (this.isInstanceOf[ASTNode])
      NodeUtil.span(this.asInstanceOf[ASTNode]).toString
    else throw new Error("Class " + this.getClass.toString + " needs to a case in UIDObject.at")

  private val seedUID = 0x7b546b0e12fd2559L
  private var prevUID = seedUID

  /* LFSR generating 63-bit residues */
  private def next: Long = {
    this.synchronized {
      var x: Long = prevUID
      x = x + x
      if (x < 0)
        x = x ^ 0xb1463923a7c109cdL
      prevUID = x
      x
    }
  }

  def getUID: Long = uid

  def setUID(uid: Long): Unit = {
    this.uid = uid
  }
}
