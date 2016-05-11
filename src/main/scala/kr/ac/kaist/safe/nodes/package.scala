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

package object nodes {
  ////////////////////////////////////////////////////////////////
  // CFG information
  ////////////////////////////////////////////////////////////////
  // location information from IR
  type Info = IRNodeInfo

  // function id
  type FunctionId = Int

  // block id
  type BlockId = Int

  // inst id
  type InstId = Int

  trait CFGInfo {
    val info: Info
  }

  type ControlPoint = CFGNode
}
