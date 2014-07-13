/*******************************************************************************
    Copyright (c) 2013, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing

import scala.collection.mutable.{HashSet => MHashSet, ListBuffer => MListBuffer}
import kr.ac.kaist.jsaf.analysis.cfg._

class Coverage {
  ////////////////////////////////////////////////////////////////////////////////
  // Variables
  ////////////////////////////////////////////////////////////////////////////////
  type FIDSet = MHashSet[FunctionId]
  private val coveredFIDSet = new FIDSet
  private val notCoveredFIDSet = new FIDSet
  private var typing: TypingInterface = null

  ////////////////////////////////////////////////////////////////////////////////
  // Methods
  ////////////////////////////////////////////////////////////////////////////////
  def getCoveredFIDSet = coveredFIDSet
  def getNotCoveredFIDSet = notCoveredFIDSet

  def coveredFIDSetToString: String = "- Covered Functions\n" + fidSetToString(coveredFIDSet)
  def notCoveredFIDSetToString: String = "- Not Covered Functions\n" + fidSetToString(notCoveredFIDSet)
  private def fidSetToString(fidSet: FIDSet): String = {
    // Filename, BeginLine, BeginColumn, EndLine, EndColumn, FunctionId, FunctionName
    val list = new MListBuffer[(String, Int, Int, Int, Int, FunctionId, String)]

    // Collect functions
    val cfg = typing.cfg
    for(fid <- fidSet) {
      val span = cfg.getFuncInfo(fid).getSpan
      val functionName = cfg.getFuncName(fid) match {
        case functionName: String if !functionName.contains("<>") => functionName
        case _ => "(anonymous function)"
      }
      list.append((span.getFileNameOnly, span.begin.getLine, span.begin.column, span.end.getLine, span.end.column, fid, functionName))
    }

    // Build a string
    val str = new StringBuilder()
    for(e <- list.sorted) str.append("  %s:%d:%d~%d:%d: [%d] %s\n".format(e._1, e._2, e._3, e._4, e._5, e._6, e._7))
    str.toString()
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Clear and Set
  ////////////////////////////////////////////////////////////////////////////////
  def clear(): Unit = {
    coveredFIDSet.clear()
    notCoveredFIDSet.clear()
    typing = null
  }

  def set(typing: TypingInterface): Unit = {
    clear()
    this.typing = typing

    val cfg = typing.cfg
    for (node <- cfg.getNodes.filter(node => cfg.isUserFunction(node._1))) {
      cfg.getCmd(node) match {
        case Entry =>
          // Function level coverage
          typing.readTable(node) match {
            case Some(cstate) => coveredFIDSet.add(node._1)
            case None => notCoveredFIDSet.add(node._1)
          }
        case Block(insts) =>
          // Instruction level coverage
        case _ =>
      }
    }
  }

}
