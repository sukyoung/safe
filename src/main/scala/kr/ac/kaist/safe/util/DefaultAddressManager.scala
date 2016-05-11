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

import scala.collection.mutable.{ Map => MMap, HashMap => MHashMap }
import scala.util.matching.Regex
import kr.ac.kaist.safe.nodes.{ FunctionId, InstId }
import kr.ac.kaist.safe.analyzer.domain.{ Address, Loc, RecencyTag, Recent, Old }

// Set by config/Config.scala
class DefaultAddressManager extends AddressManager {
  private object AMObj {
    // Address 0 is pre-allocated for Global Callsite.
    /* address counter for user loation */
    var programAddrCount = 1

    /* location name */
    val addrTable: MMap[Address, String] = MHashMap()
    // String name to predefined address table to ensure same address between analysis runs.
    // This approach assumes that all predefined names are distinct.
    val reverseAddrTable: MMap[String, Address] = MHashMap()

    val allocTable: MMap[((FunctionId, Address), InstId, Int), Address] = MHashMap()

    // locclone
    val allocTableLocClone: MMap[(InstId, Int), Address] = MHashMap()

    def registerAddress(addr: Address, name: String): Unit = {
      reverseAddrTable(name) = addr
      addrTable(addr) = name
    }

    def newProgramAddr(): Address = {
      val addr = programAddrCount
      programAddrCount += 1
      addr
    }

    def getAPIAddress(k1: (FunctionId, Address), k2: InstId, k3: Int): Address = {
      val key = (k1, k2, k3)
      allocTable.get(key) match {
        case Some(addr) => addr
        case None => {
          val addr = newProgramAddr()
          allocTable(key) = addr
          addr
        }
      }
    }

    def getAPIAddress(k2: InstId, k3: Int): Address = {
      val key = (k2, k3)
      allocTableLocClone.get(key) match {
        case Some(addr) => addr
        case None => {
          val addr = newProgramAddr()
          allocTableLocClone(key) = addr
          addr
        }
      }
    }

  }

  /* start address for PureLocal/builtin function addresses */
  var systemStartAddr = -1
  val systemAddrTable: MMap[Address, String] = MHashMap()
  val revSystemAddrTable: MMap[String, Address] = MHashMap()

  private def newSystemAddr(): Address = {
    val addr = systemStartAddr
    systemStartAddr -= 1
    addr
  }

  def addrToLoc(addr: Address, recency: RecencyTag): Loc = (addr << 1) | recency

  def locToAddr(loc: Loc): Address = loc >> 1

  def oldifyLoc(loc: Loc): Loc = loc | 1

  def isRecentLoc(loc: Loc): Boolean = (loc & 1) == Recent

  def isOldLoc(loc: Loc): Boolean = (loc & 1) == Old

  // Note that location range is -2^30 ~ (2^30 - 1)
  def compareLoc(a: Loc, b: Loc): Int = a - b

  def locName(loc: Loc): String = {
    val addr = locToAddr(loc)
    val tbl =
      if (addr < 0)
        systemAddrTable
      else
        AMObj.addrTable
    tbl.get(addr) match {
      case Some(name) => name
      case None => addr.toString
    }
  }

  def parseLocName(s: String): Option[Loc] = {
    val pattern = new Regex("""(#|##)([0-9a-zA-Z.]+)""", "prefix", "locname")
    def find(addrName: String): Option[Address] = {
      val f = AMObj.reverseAddrTable.get(addrName)
      f match {
        case Some(_) => f
        case None => revSystemAddrTable.get(addrName)
      }
    }

    try {
      val pattern(prefix, locname) = s
      val r = prefix match {
        case "#" => Recent
        case "##" => Old
      }
      val address = find(locname) match {
        case Some(addr) => addr
        case None => locname.toInt
      }
      Some(addrToLoc(address, r))
    } catch {
      case e: MatchError => {
        None
      }
      case e: NumberFormatException => {
        None
      }
    }
  }

  def registerSystemAddress(addr: Address, name: String): Unit = {
    revSystemAddrTable(name) = addr
    systemAddrTable(addr) = name
  }

  /* newProgramAddr : Unit -> Address */
  def newProgramAddr(): Address = AMObj.newProgramAddr()
  def newProgramAddr(name: String): Address = {
    val addr = AMObj.newProgramAddr()
    AMObj.registerAddress(addr, name)
    addr
  }
  /* newProgramLoc : String -> Loc */
  def newRecentLoc(name: String): Loc = {
    val addr = newProgramAddr(name)
    addrToLoc(addr, Recent)
  }
  def newRecentLoc(): Loc = {
    val addr = AMObj.newProgramAddr()
    addrToLoc(addr, Recent)
  }

  def newSystemRecentLoc(name: String): Loc = {
    revSystemAddrTable.get(name) match {
      case Some(addr) => addrToLoc(addr, Recent)
      case None => {
        val addr = newSystemAddr()
        registerSystemAddress(addr, name)
        addrToLoc(addr, Recent)
      }
    }
  }

  def newSystemLoc(name: String, tag: Int): Loc = {
    revSystemAddrTable.get(name) match {
      case Some(addr) => addrToLoc(addr, tag)
      case None => {
        val addr = newSystemAddr()
        registerSystemAddress(addr, name)
        addrToLoc(addr, tag)
      }
    }
  }
}
