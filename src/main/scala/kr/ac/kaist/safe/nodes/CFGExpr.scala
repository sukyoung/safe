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

package kr.ac.kaist.safe.nodes

sealed abstract class CFGExpr

// with Info
case class CFGVarRef(info: Info, id: CFGId) extends CFGExpr with CFGInfo {
  override def toString: String = "%s".format(id)
}
case class CFGBin(info: Info, first: CFGExpr, op: IROp, second: CFGExpr) extends CFGExpr with CFGInfo {
  override def toString: String = "%s %s %s".format(first, op.text, second)
}
case class CFGUn(info: Info, op: IROp, expr: CFGExpr) extends CFGExpr with CFGInfo {
  override def toString: String = "%s %s".format(op.text, expr)
}
case class CFGLoad(info: Info, obj: CFGExpr, index: CFGExpr) extends CFGExpr with CFGInfo {
  override def toString: String = "%s[%s]".format(obj, index)
}
case class CFGThis(info: Info) extends CFGExpr with CFGInfo {
  override def toString: String = "this"
}

// without Info
case class CFGNumber(text: String, num: Double) extends CFGExpr {
  override def toString: String = text
}
case class CFGString(str: String) extends CFGExpr {
  override def toString: String = "\"" + str + "\""
}
case class CFGBool(bool: Boolean) extends CFGExpr {
  override def toString: String = if (bool) "true" else "false"
}
case class CFGNull() extends CFGExpr {
  override def toString: String = "null"
}
