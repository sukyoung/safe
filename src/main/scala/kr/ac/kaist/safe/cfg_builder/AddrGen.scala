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

package kr.ac.kaist.safe.cfg_builder

import kr.ac.kaist.safe.util.ProgramAddr

// program address generator
class AddrGen {
  private var count: Int = 1
  def getAddrCount: Int = count
  def apply(): ProgramAddr = {
    val addr = ProgramAddr(count)
    count += 1
    addr
  }
}

// TODO revert after API modeling
// val allocTable: MMap[((FunctionId, Address), InstId, Int), Address] = MHashMap()
//
// // locclone
// val allocTableLocClone: MMap[(InstId, Int), Address] = MHashMap()
//
// def getAPIAddress(k1: (FunctionId, Address), k2: InstId, k3: Int): Address = {
//   val key = (k1, k2, k3)
//   allocTable.get(key) match {
//     case Some(addr) => addr
//     case None => {
//       val addr = newProgramAddr()
//       allocTable(key) = addr
//       addr
//     }
//   }
// }
// 
// def getAPIAddress(k2: InstId, k3: Int): Address = {
//   val key = (k2, k3)
//   allocTableLocClone.get(key) match {
//     case Some(addr) => addr
//     case None => {
//       val addr = newProgramAddr()
//       allocTableLocClone(key) = addr
//       addr
//     }
//   }
// }
