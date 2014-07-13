/*******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing

import scala.collection.mutable.{Map => MMap}
import scala.collection.mutable.{HashMap => MHashMap}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import scala.util.matching.Regex
import kr.ac.kaist.jsaf.analysis.cfg.{InstId, FunctionId}

object AddressManager {
  private class AddressManager {
    // Address 0 is pre-allocated for Global Callsite.
    /* address counter for user loation */
    var programAddrCount = 1

    /* location name */
    val addrTable: MMap[Address, String] = MHashMap()
    // String name to predefined address table to ensure same address between analysis runs.
    // This approach assumes that all predefined names are distinct.
    val reverseAddrTable: MMap[String, Address] = MHashMap()

    val allocTable: MMap[((FunctionId, Address), InstId, Int), Address] = MHashMap()

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
  }

  private var manager: AddressManager = null

  /* start address for PureLocal/builtin function addresses */
  var systemStartAddr = -1
  val systemAddrTable: MMap[Address, String] = MHashMap()
  val revSystemAddrTable: MMap[String, Address] = MHashMap()

  private def newSystemAddr(): Address = {
    val addr = systemStartAddr
    systemStartAddr -= 1
    addr
  }

  def reset() = {
    manager = new AddressManager()
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
        manager.addrTable
    tbl.get(addr) match {
      case Some(name) => name
      case None => addr.toString
    }
  }

  def parseLocName(s: String): Option[Loc] = {
    val pattern = new Regex("""(#|##)([0-9a-zA-Z.]+)""", "prefix", "locname")
    def find(addrName: String): Option[Address] = {
      val f = manager.reverseAddrTable.get(addrName)
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
  def newProgramAddr(): Address = manager.newProgramAddr()
  def newProgramAddr(name: String): Address = {
    val addr = manager.newProgramAddr()
    manager.registerAddress(addr, name)
    addr
  }
  /* newProgramLoc : String -> Loc */
  def newRecentLoc(name: String): Loc = {
    val addr = newProgramAddr(name)
    addrToLoc(addr, Recent)
  }
  def newRecentLoc(): Loc = {
    val addr = manager.newProgramAddr()
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
  
  def getAPIAddress(k1: (FunctionId, Address), k2: InstId, k3: Int): Address = manager.getAPIAddress(k1, k2, k3)

  def getRegisteredRecentLoc(name: String): Option[Loc] = {
    manager.reverseAddrTable.get(name) match {
      case Some(addr) => Some(addrToLoc(addr, Recent))
      case None => None
    }
  }
}
