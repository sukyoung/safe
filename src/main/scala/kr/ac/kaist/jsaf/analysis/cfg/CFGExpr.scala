/*******************************************************************************
    Copyright (c) 2012-2013, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.analysis.cfg

import kr.ac.kaist.jsaf.nodes.IROp
import scala.runtime.RichLong
import scala.runtime.RichDouble

abstract class CFGExpr extends CFGNode {
    def getInfo: Option[Info] = None
}

case class CFGVarRef(info: Info, id: CFGId) extends CFGExpr {
  override def getInfo = Some(info)
  override def toString = id.toString
}
case class CFGBin(info: Info, first: CFGExpr, op: IROp, second: CFGExpr) extends CFGExpr {
  override def getInfo = Some(info)
  override def toString = first.toString+" "+op.getText+" "+second.toString
}
case class CFGUn(info: Info, op: IROp, expr: CFGExpr) extends CFGExpr {
  override def getInfo = Some(info)
  override def toString = op.getText+" "+expr.toString
}
case class CFGLoad(info: Info, obj: CFGExpr, index: CFGExpr) extends CFGExpr {
  override def getInfo = Some(info)
  override def toString = obj.toString+"["+index.toString+"]"
}
case class CFGNumber(text: String, num: Double) extends CFGExpr {
  override def toString = text
  def toNumber = num
}
case class CFGString(str: String) extends CFGExpr {
  override def toString = "\""+str+"\""
}
case class CFGBool(bool: Boolean) extends CFGExpr {
  override def toString = if (bool) "true" else "false"
}
case class CFGNull() extends CFGExpr {
  override def toString = "null"
}
case class CFGThis(info: Info) extends CFGExpr {
  override def getInfo = Some(info)
  override def toString = "this"
}
