/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis

package object cfg {
  // location information from IR
  type Info = kr.ac.kaist.jsaf.nodes.IRSpanInfo
  
  // function id
  type FunctionId = Int
  
  // basic block id
  type BlockId = Int
  
  // instruction id
  type InstId = Int
  
  // disambiguated name for "arguments" of function
  type ArgumentsName = String
  
  // argument variables of function
  type ArgVars = List[CFGId]
  
  // local variables of function
  type LocalVars = List[CFGId]
  
  // Node type represents unique key of a CFG node.
  // Actual data must be retrieved through methods in CFG class.
  type Node = (FunctionId, Label)
  
  // Basic block node type.
  // only this type of nodes can have Instruction.
  type BlockNode = (FunctionId, LBlock)

  implicit def comp2func(x: FunctionId) = new Ordered[FunctionId] { 
    def compare(y: FunctionId) = { 
      x compareTo y
    }
  }

  implicit def comp2ord(x: Label) = new Ordered[Label] {
    // Assumes that a number 'n' in LBlock is 0 or positive integer(CFG.scala: blockCount)
    def ltov(x: Label) : Int = x match {
      case LEntry => -3
      case LExit => -2
      case LExitExc => -1
      case LBlock(n) => n
    }

    def compare(y: Label) = {
      val l : Int = ltov(x)
      val r : Int = ltov(y)
      r - l
    }
  }
}
