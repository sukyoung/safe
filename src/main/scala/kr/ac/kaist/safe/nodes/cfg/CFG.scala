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

package kr.ac.kaist.safe.nodes.cfg

import scala.collection.immutable.HashSet
import scala.collection.mutable.{ HashMap => MHashMap, Map => MMap }
import kr.ac.kaist.safe.analyzer.models.PredefLoc._
import kr.ac.kaist.safe.LINE_SEP
import kr.ac.kaist.safe.nodes.ir.IRNode
import kr.ac.kaist.safe.util._

class CFG(
    override val ir: IRNode,
    globalVars: List[CFGId]
) extends CFGNode(ir) {
  // cfg id
  val id: Int = CFG.getId

  // get functions / blocks in this cfg
  private def getBlocks(funcs: List[CFGFunction]): List[CFGBlock] =
    funcs.foldRight(List[CFGBlock]())(_.getAllBlocks ++ _)

  private var funcs: List[CFGFunction] = Nil
  def getAllFuncs: List[CFGFunction] = funcs
  def getAllBlocks: List[CFGBlock] = getBlocks(funcs)

  private var userFuncs: List[CFGFunction] = Nil
  def getUserFuncs: List[CFGFunction] = userFuncs
  def getUserBlocks: List[CFGBlock] = getBlocks(userFuncs)

  // function / block map from id
  private val funMap: MMap[FunctionId, CFGFunction] = MHashMap()
  def getFunc(fid: FunctionId): Option[CFGFunction] = funMap.get(fid)
  def getBlock(fid: FunctionId, bid: BlockId): Option[CFGBlock] =
    funMap.get(fid).fold[Option[CFGBlock]](None) { _.getBlock(bid) }

  private var fidCount: FunctionId = 0
  def getFId: FunctionId = fidCount

  // global function
  lazy val globalFunc: CFGFunction =
    createFunction("", Nil, globalVars, "top-level", ir, true)

  // create function
  def createFunction(
    argumentsName: String,
    argVars: List[CFGId],
    localVars: List[CFGId],
    name: String,
    ir: IRNode,
    isUser: Boolean
  ): CFGFunction = {
    val func: CFGFunction =
      new CFGFunction(ir, this, argumentsName, argVars, localVars, name, isUser)
    fidCount += 1
    funcs ::= func
    if (isUser) userFuncs ::= func
    funMap(func.id) = func
    func
  }

  // add edge
  def addEdge(
    fromList: List[CFGBlock],
    toList: List[CFGBlock],
    etype: CFGEdgeType = CFGEdgeNormal
  ): Unit = {
    fromList.foreach((from) => toList.foreach((to) => {
      from.addSucc(etype, to)
      to.addPred(etype, from)
    }))
  }

  // toString
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    funcs.reverseIterator.foreach {
      case func => s.append(func.toString(indent)).append(LINE_SEP)
    }
    s.toString
  }

  // program address size
  private var pgmAddrSize: Int = 0
  def getProgramAddrSize: Int = pgmAddrSize
  def newProgramAddr: ProgramAddr = {
    pgmAddrSize += 1
    val addr = ProgramAddr(pgmAddrSize)
    addr
  }

  // system address set
  private var systemAddrSet: Set[Address] = HashSet(
    GLOBAL_ENV.address,
    PURE_LOCAL.address,
    COLLAPSED.address
  )
  def getSystemAddrSet: Set[Address] = systemAddrSet
  def registerSystemAddr(addr: Address): Unit = systemAddrSet += addr

  // get all locations
  // TODO it does not have all system address(addresses in modeling function)
  def getAllAddrSet: Set[Address] =
    (1 to pgmAddrSize).foldLeft(systemAddrSet)(_ + ProgramAddr(_))
}

object CFG {
  private var idCount: Int = 0
  private def getId: Int = idCount
}
