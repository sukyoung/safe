/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.cfg

import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.Config
import kr.ac.kaist.jsaf.nodes.IRStmt

abstract class CFGInst extends CFGNode {
  def getInstId: InstId
  def getInfo: Option[Info] = None
}

case class CFGAlloc(iid: InstId, info: Info,
                    lhs: CFGId, proto: Option[CFGExpr],
                    addr: Address) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString =
    proto match {
      case None => lhs.toString+" := alloc() @ #"+addr.toString
      case Some(p) => lhs.toString+" := alloc("+p.toString+") @ #"+addr.toString
    }
}

case class CFGAllocArray(iid: InstId, info: Info,
                         lhs: CFGId, length: Int,
                         addr: Address) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString = lhs.toString+" := allocArray("+length.toString+") @ #"+addr.toString
}

case class CFGAllocArg(iid: InstId, info: Info,
                       lhs: CFGId, length: Int,
                       addr: Address) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString = lhs.toString+" := allocArg("+length.toString+") @ #"+addr.toString
}

case class CFGExprStmt(iid: InstId, info: Info,
                       lhs: CFGId, right: CFGExpr) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString = lhs.toString+" := "+right.toString
}

case class CFGDelete(iid: InstId, info: Info,
                     lhs: CFGId, expr: CFGExpr) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString = lhs.toString+" := delete("+expr.toString+")"
}

case class CFGDeleteProp(iid: InstId, info: Info,
                         lhs: CFGId, obj: CFGExpr, index: CFGExpr) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString = lhs.toString+" := delete("+obj.toString+", "+index.toString+")"
}

case class CFGStore(iid: InstId, info: Info,
                    obj: CFGExpr, index: CFGExpr, rhs: CFGExpr) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString = obj.toString+"["+index.toString+"] := " + rhs.toString
}

case class CFGFunExpr(iid: InstId, info: Info,
                      lhs: CFGId, name: Option[CFGId], fid: FunctionId,
                      addr1: Address, addr2: Address, addr3: Option[Address]) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString = {
    val name_str = name match {
      case Some(x) => x.toString
      case None => ""
    }
    val addr3_str = addr3 match {
      case Some(x) => ", #" + x.toString
      case None => ""
    }
    lhs.toString+" := function "+name_str+"("+fid.toString+") @ #"+
                      addr1.toString+", #"+addr2.toString+addr3_str
  }
}

case class CFGConstruct(iid: InstId, info: Info,
                        cons: CFGExpr, thisArg: CFGExpr, arguments: CFGExpr,
                        addr1: Address, addr2: Address) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString =
//    "construct("+cons.toString+", "+thisArg.toString+", "+arguments.toString+") @ #"+addr.toString
    "construct("+cons.toString+", "+thisArg.toString+", "+arguments.toString+") @ #"+addr1.toString + ", #"+addr2.toString
}

case class CFGCall(iid: InstId, info: Info,
                   fun: CFGExpr, thisArg: CFGExpr, arguments: CFGExpr,
                   addr1: Address, addr2: Address) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString =
    if(Config.loopSensitive)
    "call("+fun.toString+", "+thisArg.toString+", "+arguments.toString+") @ #"+addr1.toString + ", #"+addr2.toString  
    else
    "call("+fun.toString+", "+thisArg.toString+", "+arguments.toString+") @ #"+addr1.toString 
}

case class CFGInternalCall(iid: InstId, info: Info,
                           lhs: CFGId, fun: CFGId, arguments: List[CFGExpr],
                           addr: Option[Address]) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString = {
    val sb = new StringBuilder()
    sb.append(lhs.toString)
    sb.append(" := ")
    sb.append(fun.toString)
    sb.append("(")
    arguments match {
      case Nil => ()
      case arg1::args =>
        sb.append(arg1.toString)
        args.foreach(arg => {
          sb.append(", ")
          sb.append(arg.toString)
        })
    }
    sb.append(")")
    addr match {
      case Some(l) =>
        sb.append(" @ #")
        sb.append(l.toString)
      case None => ()
    }
    sb.toString
  }
}

case class CFGAPICall(iid: InstId,
                      model: String, fun: String, arguments: CFGExpr) extends CFGInst {
  def getInstId = iid
  override def toString =
    "[]"+model+"."+fun+"("+arguments.toString+")"
}

case class CFGAssert(iid: InstId, info: Info,
                     expr: CFGExpr, flag: Boolean) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString = "assert("+expr.toString+")"
}


case class CFGCond(iid: InstId, info: Info, expr: CFGExpr, isEvent: Boolean=false) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString = "cond("+expr.toString+")"
}


case class CFGCatch(iid: InstId, info: Info,
                    name: CFGId) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString = "catch("+name.toString+")"
}

case class CFGReturn(iid: InstId, info: Info,
                     expr: Option[CFGExpr]) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString =
    expr match {
      case None => "return()"
      case Some(e) => "return("+e.toString+")"
    }
}

case class CFGThrow(iid: InstId, info: Info,
                    expr: CFGExpr) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString = "throw("+expr.toString+")";
}

case class CFGNoOp(iid: InstId, info: Info,
                   desc: String) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString = "noop("+desc+")"
}

case class CFGAsyncCall(iid: InstId, info: Info, modelType: String, callType: String,
                        addr1: Address, addr2: Address, addr3: Address) extends CFGInst {
  def getInstId = iid
  override def getInfo = Some(info)
  override def toString = "async("+modelType+", "+ callType+") @ #"+addr1.toString+", #"+addr2.toString+", #"+addr3.toString
}
