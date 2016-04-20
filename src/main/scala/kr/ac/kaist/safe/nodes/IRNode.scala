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

/**
 * *************************
 * JavaScript IR
 * ECMAScript 5
 * *************************
 */

package kr.ac.kaist.safe.nodes

import kr.ac.kaist.safe.config.Config
import java.lang.Double
import java.math.BigInteger
import kr.ac.kaist.safe.util.{ NodeUtil => NU, Span }

abstract class IRNode(override val info: IRNodeInfo)
  extends Node(info: NodeInfo)

/**
 * IRRoot ::= Statement*
 */
case class IRRoot(override val info: IRNodeInfo, val fds: List[IRFunDecl], val vds: List[IRVarStmt], val irs: List[IRStmt])
    extends IRNode(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    NU.initNodesPrint
    val s: StringBuilder = new StringBuilder
    val indentString = NU.getIndent(indent)
    s.append(indentString).append(NU.join(indent, fds, Config.LINE_SEP + indentString, new StringBuilder("")))
    s.append(Config.LINE_SEP)
    s.append(indentString).append(NU.join(indent, vds, Config.LINE_SEP + indentString, new StringBuilder("")))
    s.append(Config.LINE_SEP)
    s.append(indentString).append(NU.join(indent, irs, Config.LINE_SEP + indentString, new StringBuilder("")))
    s.toString
  }
}
/**
 * Statement
 */
abstract class IRStmt(override val info: IRNodeInfo)
  extends IRNode(info: IRNodeInfo)
abstract class IRAssign(override val info: IRNodeInfo, val lhs: IRId)
  extends IRStmt(info: IRNodeInfo)

/**
 * Expression
 * Stmt ::= x = e
 */
case class IRExprStmt(override val info: IRNodeInfo, override val lhs: IRId, val right: IRExpr, val ref: Boolean = false)
    extends IRAssign(info: IRNodeInfo, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ").append(right.toString(indent))
    s.toString
  }
}

/**
 * Delete expression
 * Stmt ::= x = delete y
 */
case class IRDelete(override val info: IRNodeInfo, override val lhs: IRId, val id: IRId)
    extends IRAssign(info: IRNodeInfo, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = delete ").append(id.toString(indent))
    s.toString
  }
}

/**
 * Delete property expression
 * Stmt ::= x = delete y[e]
 */
case class IRDeleteProp(override val info: IRNodeInfo, override val lhs: IRId, val obj: IRId, val index: IRExpr)
    extends IRAssign(info: IRNodeInfo, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = delete ")
    s.append(obj.toString(indent)).append("[").append(index.toString(indent)).append("]")
    s.toString
  }
}

/**
 * Object literal
 * Stmt ::= x = { member, ... }
 */
case class IRObject(override val info: IRNodeInfo, override val lhs: IRId, val members: List[IRMember], val proto: Option[IRId])
    extends IRAssign(info: IRNodeInfo, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = {").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent + 1)).append(NU.join(indent, members, "," + Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
    if (proto.isDefined) {
      s.append("[[Prototype]]=").append(proto.get.toString(indent + 1))
    }
    s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("}")
    s.toString
  }
}

/**
 * Array literal
 * Stmt ::= x = [ e, ... ]
 */
case class IRArray(override val info: IRNodeInfo, override val lhs: IRId, val elements: List[Option[IRExpr]])
    extends IRAssign(info: IRNodeInfo, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ")
    s.append("[")
    elements.foreach(e => s.append(if (e.isDefined) e.get.toString(indent) else "").append(", "))
    s.append("]")
    s.toString
  }
}

/**
 * Array literal with numbers
 * Stmt ::= x = [ n, ... ]
 */
case class IRArrayNumber(override val info: IRNodeInfo, override val lhs: IRId, val elements: List[Double])
    extends IRAssign(info: IRNodeInfo, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ")
    s.append("[")
    s.append("A LOT!!! " + elements.size + " elements are not printed here.")
    s.append("]")
    s.toString
  }
}

/**
 * Arguments
 * Stmt ::= x = [ e, ... ]
 */
case class IRArgs(override val info: IRNodeInfo, override val lhs: IRId, val elements: List[Option[IRExpr]])
    extends IRAssign(info: IRNodeInfo, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ")
    s.append("[")
    elements.foreach(e => s.append(if (e.isDefined) e.get.toString(indent) else "").append(", "))
    s.append("]")
    s.toString
  }
}

/**
 * Call
 * Stmt ::= x = f(this, arguments)
 */
case class IRCall(override val info: IRNodeInfo, override val lhs: IRId, val fun: IRId, val thisB: IRId, val args: IRId)
    extends IRAssign(info: IRNodeInfo, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ")
    s.append(fun.toString(indent)).append("(")
    s.append(thisB.toString(indent))
    s.append(", ").append(args.toString(indent))
    s.append(")")
    s.toString
  }
}

/**
 * Internal function call
 * toObject, toString, toNubmer, isObject, getBase,
 * iteratorInit, iteratorHasNext, iteratorKey
 */
case class IRInternalCall(override val info: IRNodeInfo, override val lhs: IRId, val fun: IRId, val first: IRExpr, val second: Option[IRId])
    extends IRAssign(info: IRNodeInfo, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ")
    s.append(fun.toString(indent)).append("(")
    s.append(first.toString(indent))
    if (second.isDefined) s.append(", ").append(second.get.toString(indent))
    s.append(")")
    s.toString
  }
}

/**
 * New
 * Stmt ::= x = new f(x, ...)
 */
case class IRNew(override val info: IRNodeInfo, override val lhs: IRId, val fun: IRId, val args: List[IRId])
    extends IRAssign(info: IRNodeInfo, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = new ")
    s.append(fun.toString(indent)).append("(")
    s.append(NU.join(indent, args, ", ", new StringBuilder("")))
    s.append(")")
    s.toString
  }
}

/**
 * Function expression
 * Stmt ::= x = function f (this, arguments) { s }
 */
case class IRFunExpr(override val info: IRNodeInfo, override val lhs: IRId, val fun: IRFunctional)
    extends IRAssign(info: IRNodeInfo, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = ").append("function ")
    s.append(fun.toString(indent))
    s.toString
  }
}

/**
 * Eval
 * Stmt ::= x = eval(e)
 */
case class IREval(override val info: IRNodeInfo, override val lhs: IRId, val arg: IRExpr)
    extends IRAssign(info: IRNodeInfo, lhs: IRId) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(lhs.toString(indent)).append(" = eval(").append(arg.toString(indent)).append(")")
    s.toString
  }
}

/**
 * AST statement unit
 * Stmt ::= s
 */
case class IRStmtUnit(override val info: IRNodeInfo, val stmts: List[IRStmt])
    extends IRStmt(info: IRNodeInfo) {
  override def toString(indent: Int): String = stmts match {
    case List(stmt) => stmt.toString(indent)
    case _ =>
      val s: StringBuilder = new StringBuilder
      s.append("{").append(Config.LINE_SEP)
      s.append(NU.getIndent(indent + 1)).append(NU.join(indent + 1, stmts, Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
      s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("}")
      s.toString
  }
}

/**
 * Store
 * Stmt ::= x[e] = e
 */
case class IRStore(override val info: IRNodeInfo, val obj: IRId, val index: IRExpr, val rhs: IRExpr)
    extends IRStmt(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(obj.toString(indent)).append("[").append(index.toString(indent))
    s.append("] = ").append(rhs.toString(indent))
    s.toString
  }
}

/**
 * Function declaration
 * Stmt ::= function f (this, arguments) { s }
 */
case class IRFunDecl(override val info: IRNodeInfo, val ftn: IRFunctional)
    extends IRStmt(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("function ")
    s.append(ftn.toString(indent))
    s.toString
  }
}

/**
 * Break
 * Stmt ::= break label
 */
case class IRBreak(override val info: IRNodeInfo, val label: IRId)
    extends IRStmt(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("break ") append (label.toString(indent))
    s.toString
  }
}

/**
 * Return
 * Stmt ::= return e?
 */
case class IRReturn(override val info: IRNodeInfo, val expr: Option[IRExpr])
    extends IRStmt(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("return")
    if (expr.isDefined) s.append(" ").append(expr.get.toString(indent))
    s.toString
  }
}

/**
 * With
 * Stmt ::= with ( x ) s
 */
case class IRWith(override val info: IRNodeInfo, val id: IRId, val stmt: IRStmt)
    extends IRStmt(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("with(")
    s.append(id.toString(indent)).append(")").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent)).append(stmt.toString(indent))
    s.toString
  }
}

/**
 * Label
 * Stmt ::= l : { s }
 */
case class IRLabelStmt(override val info: IRNodeInfo, val label: IRId, val stmt: IRStmt)
    extends IRStmt(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(label.toString(indent)).append(" : ").append(stmt.toString(indent))
    s.toString
  }
}

/**
 * Var
 * Stmt ::= var x
 */
case class IRVarStmt(override val info: IRNodeInfo, val lhs: IRId, val fromParam: Boolean)
    extends IRStmt(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("var ").append(lhs.toString(indent))
    s.toString
  }
}

/**
 * Throw
 * Stmt ::= throw e
 */
case class IRThrow(override val info: IRNodeInfo, val expr: IRExpr)
    extends IRStmt(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("throw ").append(expr.toString(indent))
    s.toString
  }
}

/**
 * Sequence
 * Stmt ::= s; ...
 */
case class IRSeq(override val info: IRNodeInfo, val stmts: List[IRStmt])
    extends IRStmt(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("{").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent + 1)).append(NU.join(indent + 1, stmts, Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
    s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("}")
    s.toString
  }
}

/**
 * If
 * Stmt ::= if (e) then s (else s)?
 */
case class IRIf(override val info: IRNodeInfo, val expr: IRExpr, val trueB: IRStmt, val falseB: Option[IRStmt])
    extends IRStmt(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("if(").append(expr.toString(indent)).append(")").append(Config.LINE_SEP)
    NU.inlineIndent(trueB, s, indent)
    falseB match {
      case Some(f) =>
        s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("else").append(Config.LINE_SEP)
        NU.inlineIndent(f, s, indent)
      case None =>
    }
    s.toString
  }
}

/**
 * While
 * Stmt ::= while (e) s
 */
case class IRWhile(override val info: IRNodeInfo, val cond: IRExpr, val body: IRStmt)
    extends IRStmt(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("while(")
    s.append(cond.toString(indent)).append(")").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent)).append(body.toString(indent))
    s.toString
  }
}

/**
 * Try
 * Stmt ::= try { s } (catch (x) { s })? (finally { s })?
 */
case class IRTry(override val info: IRNodeInfo, val body: IRStmt, val name: Option[IRId], val catchB: Option[IRStmt], finallyB: Option[IRStmt])
    extends IRStmt(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("try").append(Config.LINE_SEP)
    NU.inlineIndent(body, s, indent)
    catchB match {
      case Some(cb) =>
        s.append(Config.LINE_SEP).append(NU.getIndent(indent))
        s.append("catch(").append(name.get.toString(indent)).append(")").append(Config.LINE_SEP)
        NU.inlineIndent(cb, s, indent)
      case None =>
    }
    finallyB match {
      case Some(f) =>
        s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("finally").append(Config.LINE_SEP)
        NU.inlineIndent(f, s, indent)
      case None =>
    }
    s.toString
  }
}

/**
 * No operation
 */
case class IRNoOp(override val info: IRNodeInfo, val desc: String)
    extends IRStmt(info: IRNodeInfo) {
  override def toString(indent: Int): String = ""
}

/**
 * Member
 */
abstract class IRMember(override val info: IRNodeInfo)
  extends IRNode(info: IRNodeInfo)
/**
 * Member ::= x : e
 */
case class IRField(override val info: IRNodeInfo, val prop: IRId, val expr: IRExpr)
    extends IRMember(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(if (prop.info.ast.isInstanceOf[PropStr]) prop.toPropName(indent) else prop.toString(indent))
    s.append(" : ").append(expr.toString(indent))
    s.toString
  }
}

/**
 * Member ::= get x () { s }
 */
case class IRGetProp(override val info: IRNodeInfo, val ftn: IRFunctional)
    extends IRMember(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("get ").append(ftn)
    s.toString
  }
}

/**
 * Member ::= set x ( y ) { s }
 */
case class IRSetProp(override val info: IRNodeInfo, val ftn: IRFunctional)
    extends IRMember(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("set ").append(ftn)
    s.toString
  }
}

/**
 * Expression
 */
abstract class IRExpr(override val info: IRNodeInfo)
  extends IRNode(info: IRNodeInfo)
/**
 * Side-effect free expressions
 */
abstract class IROpApp(override val info: IRNodeInfo)
  extends IRExpr(info: IRNodeInfo)
/**
 * Binary expression
 * Expr ::= e binop e
 */
case class IRBin(override val info: IRNodeInfo, val first: IRExpr, val op: IROp, val second: IRExpr)
    extends IROpApp(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(first.toString(indent)).append(" ")
    s.append(op.toString(indent)).append(" ")
    s.append(second.toString(indent))
    s.toString
  }
}

/**
 * Unary expression
 * Expr ::= unop e
 */
case class IRUn(override val info: IRNodeInfo, val op: IROp, val expr: IRExpr)
    extends IROpApp(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(op.toString(indent)).append(" ").append(expr.toString(indent))
    s.toString
  }
}

/**
 * Load
 * Expr ::= x[e]
 */
case class IRLoad(override val info: IRNodeInfo, val obj: IRId, val index: IRExpr)
    extends IROpApp(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(obj.toString(indent)).append("[").append(index.toString(indent)).append("]")
    s.toString
  }
}

/**
 * Variable
 */
abstract class IRId(override val info: IRNodeInfo, val originalName: String, val uniqueName: String, val global: Boolean)
    extends IRExpr(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val size = NU.significantBits
    val str = if (!NU.isInternal(uniqueName)) uniqueName
    else if (!NU.isGlobalName(uniqueName)) uniqueName.dropRight(size) + NU.getNodesE(uniqueName.takeRight(size))
    else uniqueName
    val s: StringBuilder = new StringBuilder
    s.append(NU.pp(str))
    s.toString
  }

  // When this IRId is a property string name, use toPropName instead of toString
  def toPropName(indent: Int): String = {
    val size = NU.significantBits
    val str = if (!NU.isInternal(uniqueName)) uniqueName
    else if (!NU.isGlobalName(uniqueName)) uniqueName.dropRight(size) + NU.getNodesE(uniqueName.takeRight(size))
    else uniqueName
    val s: StringBuilder = new StringBuilder
    s.append("\"")
    s.append(NU.pp(str.replaceAll("\\\\", "\\\\\\\\")))
    s.append("\"")
    s.toString
  }
}

/**
 * Variable
 * Expr ::= x
 */
case class IRUserId(override val info: IRNodeInfo, override val originalName: String, override val uniqueName: String, override val global: Boolean, val isWith: Boolean)
  extends IRId(info: IRNodeInfo, originalName, uniqueName, global)

/**
 * Internally generated identifiers by Translator
 * Do not appear in the JavaScript source text.
 */
case class IRTmpId(override val info: IRNodeInfo, override val originalName: String, override val uniqueName: String, override val global: Boolean)
  extends IRId(info: IRNodeInfo, originalName, uniqueName, global)

/**
 * this
 */
case class IRThis(override val info: IRNodeInfo)
    extends IRExpr(info: IRNodeInfo) {
  override def toString(indent: Int): String = "this"
}

/**
 * Value
 */
abstract class IRVal(override val info: IRNodeInfo)
  extends IRExpr(info: IRNodeInfo)

/**
 * Primitive value
 */
abstract class IRPVal(override val info: IRNodeInfo)
  extends IRExpr(info: IRNodeInfo)

/**
 * PVal ::= number literal
 */
case class IRNumber(override val info: IRNodeInfo, val text: String, val num: Double)
    extends IRVal(info: IRNodeInfo) {
  override def toString(indent: Int): String = text
}

/**
 * PVal ::= String
 */
case class IRString(override val info: IRNodeInfo, val str: String)
    extends IRVal(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append("\"")
    s.append(NU.pp(str.replaceAll("\\\\", "\\\\\\\\")))
    s.append("\"")
    s.toString
  }
}

/**
 * PVal ::= true | false
 */
case class IRBool(override val info: IRNodeInfo, val bool: Boolean)
    extends IRVal(info: IRNodeInfo) {
  override def toString(indent: Int): String = if (bool) "true" else "false"
}

/**
 * PVal ::= undefined
 */
case class IRUndef(override val info: IRNodeInfo)
    extends IRVal(info: IRNodeInfo) {
  override def toString(indent: Int): String = "undefined"
}

/**
 * PVal ::= null
 */
case class IRNull(override val info: IRNodeInfo)
    extends IRVal(info: IRNodeInfo) {
  override def toString(indent: Int): String = "null"
}

/**
 * Operator
 */
case class IROp(override val info: IRNodeInfo, val text: String, val kind: Int)
    extends IRNode(info: IRNodeInfo) {
  override def toString(indent: Int): String = text
}

/**
 * Common shape for functions
 */
case class IRFunctional(override val info: IRNodeInfo, val fromSource: Boolean,
  val name: IRId, val params: List[IRId], val args: List[IRStmt],
  val fds: List[IRFunDecl], val vds: List[IRVarStmt], val body: List[IRStmt])
    extends IRNode(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    s.append(name.toString(indent)).append("(")
    s.append(NU.join(indent, params, ", ", new StringBuilder("")))
    s.append(") ").append(Config.LINE_SEP).append(NU.getIndent(indent)).append("{").append(Config.LINE_SEP)
    s.append(NU.getIndent(indent + 1))
    s.append(NU.join(indent + 1, fds ++ vds ++ args ++ body, Config.LINE_SEP + NU.getIndent(indent + 1), new StringBuilder("")))
    s.append(Config.LINE_SEP).append(NU.getIndent(indent)).append("}")
    s.toString
  }
}

trait IRWalker {
  def walk(info: IRNodeInfo): IRNodeInfo = info

  def walk(node: IRRoot): IRRoot = node match {
    case IRRoot(info, fds, vds, irs) =>
      IRRoot(walk(info), fds.map(walk), vds.map(walk), irs.map(walk))
  }

  def walk(node: IRStmt): IRStmt = node match {
    case IRExprStmt(info, lhs, right, isRef) =>
      IRExprStmt(walk(info), walk(lhs), walk(right), isRef)
    case IRDelete(info, lhs, id) =>
      IRDelete(walk(info), walk(lhs), walk(id))
    case IRDeleteProp(info, lhs, obj, index) =>
      IRDeleteProp(walk(info), walk(lhs), walk(obj), walk(index))
    case IRObject(info, lhs, members, proto) =>
      IRObject(walk(info), walk(lhs), members.map(walk), proto.map(walk))
    case IRArray(info, lhs, elements) =>
      IRArray(walk(info), walk(lhs), elements.map(_.map(walk)))
    case IRArrayNumber(info, lhs, elements) =>
      IRArrayNumber(walk(info), walk(lhs), elements)
    case IRArgs(info, lhs, elements) =>
      IRArgs(walk(info), walk(lhs), elements.map(_.map(walk)))
    case IRCall(info, lhs, fun, thisB, args) =>
      IRCall(walk(info), walk(lhs), walk(fun), walk(thisB), walk(args))
    case IRInternalCall(info, lhs, fun, first, second) =>
      IRInternalCall(walk(info), walk(lhs), walk(fun), walk(first), second.map(walk))
    case IRNew(info, lhs, fun, args) =>
      IRNew(walk(info), walk(lhs), walk(fun), args.map(walk))
    case IRFunExpr(info, lhs, ftn) =>
      IRFunExpr(walk(info), walk(lhs), walk(ftn))
    case IREval(info, lhs, arg) =>
      IREval(walk(info), walk(lhs), walk(arg))
    case IRStmtUnit(info, stmts) =>
      IRStmtUnit(walk(info), stmts.map(walk))
    case IRStore(info, obj, index, rhs) =>
      IRStore(walk(info), walk(obj), walk(index), walk(rhs))
    case fd: IRFunDecl =>
      walk(fd)
    case IRBreak(info, label) =>
      IRBreak(walk(info), walk(label))
    case IRReturn(info, expr) =>
      IRReturn(walk(info), expr.map(walk))
    case IRWith(info, id, stmt) =>
      IRWith(walk(info), walk(id), walk(stmt))
    case IRLabelStmt(info, label, stmt) =>
      IRLabelStmt(walk(info), walk(label), walk(stmt))
    case vs: IRVarStmt =>
      walk(vs)
    case IRThrow(info, expr) =>
      IRThrow(walk(info), walk(expr))
    case IRSeq(info, stmts) =>
      IRSeq(walk(info), stmts.map(walk))
    case IRIf(info, expr, trueB, falseB) =>
      IRIf(walk(info), walk(expr), walk(trueB), falseB.map(walk))
    case IRWhile(info, cond, body) =>
      IRWhile(walk(info), walk(cond), walk(body))
    case IRTry(info, body, name, catchB, finallyB) =>
      IRTry(walk(info), walk(body), name.map(walk), catchB.map(walk), finallyB.map(walk))
    case IRNoOp(info, desc) =>
      IRNoOp(walk(info), desc)
  }

  def walk(node: IRExpr): IRExpr = node match {
    case IRBin(info, first, op, second) =>
      IRBin(walk(info), walk(first), walk(op), walk(second))
    case IRUn(info, op, expr) =>
      IRUn(walk(info), walk(op), walk(expr))
    case IRLoad(info, obj, index) =>
      IRLoad(walk(info), walk(obj), walk(index))
    case id: IRUserId =>
      walk(id)
    case id: IRTmpId =>
      walk(id)
    case IRThis(info) =>
      IRThis(walk(info))
    case IRNumber(info, text, num) =>
      IRNumber(walk(info), text, num)
    case IRString(info, str) =>
      IRString(walk(info), str)
    case IRBool(info, isBool) =>
      IRBool(walk(info), isBool)
    case IRUndef(info) =>
      IRUndef(walk(info))
    case IRNull(info) =>
      IRNull(walk(info))
  }

  def walk(node: IRMember): IRMember = node match {
    case IRField(info, prop, expr) =>
      IRField(walk(info), walk(prop), walk(expr))
    case IRGetProp(info, ftn) =>
      IRGetProp(walk(info), walk(ftn))
    case IRSetProp(info, ftn) =>
      IRSetProp(walk(info), walk(ftn))
  }

  def walk(node: IRFunctional): IRFunctional = node match {
    case IRFunctional(info, isFromSource, name, params, args, fds, vds, body) =>
      IRFunctional(walk(info), isFromSource, walk(name), params.map(walk),
        args.map(walk), fds.map(walk), vds.map(walk), body.map(walk))
  }

  def walk(node: IROp): IROp = node match {
    case IROp(info, text, kind) =>
      IROp(walk(info), text, kind)
  }

  def walk(node: IRFunDecl): IRFunDecl = node match {
    case IRFunDecl(info, ftn) =>
      IRFunDecl(walk(info), walk(ftn))
  }

  def walk(node: IRVarStmt): IRVarStmt = node match {
    case IRVarStmt(info, lhs, isFromParam) =>
      IRVarStmt(walk(info), walk(lhs), isFromParam)
  }

  def walk(node: IRId): IRId = node match {
    case IRUserId(info, originalName, uniqueName, isGlobal, isWith) =>
      IRUserId(walk(info), originalName, uniqueName, isGlobal, isWith)
    case IRTmpId(info, originalName, uniqueName, isGlobal) =>
      IRTmpId(walk(info), originalName, uniqueName, isGlobal)
  }
}

trait IRGeneralWalker[Result] {
  def join(args: Result*): Result

  def walkOpt(opt: Option[IRNode]): List[Result] =
    opt.fold(List[Result]()) { n: IRNode =>
      List(n match {
        case s: IRStmt => walk(s)
        case i: IRId => walk(i)
        case e: IRExpr => walk(e)
      })
    }

  def walk(info: IRNodeInfo): Result = join()

  def walk(node: IRRoot): Result = node match {
    case IRRoot(info, fds, vds, irs) =>
      join(walk(info) :: fds.map(walk) ++ vds.map(walk) ++ irs.map(walk): _*)
  }

  def walk(node: IRStmt): Result = node match {
    case IRExprStmt(info, lhs, right, isRef) =>
      join(walk(info), walk(lhs), walk(right))
    case IRDelete(info, lhs, id) =>
      join(walk(info), walk(lhs), walk(id))
    case IRDeleteProp(info, lhs, obj, index) =>
      join(walk(info), walk(lhs), walk(obj), walk(index))
    case IRObject(info, lhs, members, proto) =>
      join(walk(info) :: walk(lhs) :: members.map(walk) ++ walkOpt(proto): _*)
    case IRArray(info, lhs, elements) =>
      join(walk(info) :: walk(lhs) :: elements.flatMap(walkOpt): _*)
    case IRArrayNumber(info, lhs, elements) =>
      join(walk(info), walk(lhs))
    case IRArgs(info, lhs, elements) =>
      join(walk(info) :: walk(lhs) :: elements.flatMap(walkOpt): _*)
    case IRCall(info, lhs, fun, thisB, args) =>
      join(walk(info), walk(lhs), walk(fun), walk(thisB), walk(args))
    case IRInternalCall(info, lhs, fun, first, second) =>
      join(walk(info) :: walk(lhs) :: walk(fun) :: walk(first) :: walkOpt(second): _*)
    case IRNew(info, lhs, fun, args) =>
      join(walk(info) :: walk(lhs) :: walk(fun) :: args.map(walk): _*)
    case IRFunExpr(info, lhs, ftn) =>
      join(walk(info), walk(lhs), walk(ftn))
    case IREval(info, lhs, arg) =>
      join(walk(info), walk(lhs), walk(arg))
    case IRStmtUnit(info, stmts) =>
      join(walk(info) :: stmts.map(walk): _*)
    case IRStore(info, obj, index, rhs) =>
      join(walk(info), walk(obj), walk(index), walk(rhs))
    case fd: IRFunDecl =>
      walk(fd)
    case IRBreak(info, label) =>
      join(walk(info), walk(label))
    case IRReturn(info, expr) =>
      join(walk(info) :: walkOpt(expr): _*)
    case IRWith(info, id, stmt) =>
      join(walk(info), walk(id), walk(stmt))
    case IRLabelStmt(info, label, stmt) =>
      join(walk(info), walk(label), walk(stmt))
    case vs: IRVarStmt =>
      walk(vs)
    case IRThrow(info, expr) =>
      join(walk(info), walk(expr))
    case IRSeq(info, stmts) =>
      join(walk(info) :: stmts.map(walk): _*)
    case IRIf(info, expr, trueB, falseB) =>
      join(walk(info) :: walk(expr) :: walk(trueB) :: walkOpt(falseB): _*)
    case IRWhile(info, cond, body) =>
      join(walk(info), walk(cond), walk(body))
    case IRTry(info, body, name, catchB, finallyB) =>
      join(walk(info) :: walk(body) :: walkOpt(name) ++ walkOpt(catchB) ++ walkOpt(finallyB): _*)
    case IRNoOp(info, desc) =>
      walk(info)
  }

  def walk(node: IRExpr): Result = node match {
    case IRBin(info, first, op, second) =>
      join(walk(info), walk(first), walk(op), walk(second))
    case IRUn(info, op, expr) =>
      join(walk(info), walk(op), walk(expr))
    case IRLoad(info, obj, index) =>
      join(walk(info), walk(obj), walk(index))
    case id: IRUserId =>
      walk(id)
    case id: IRTmpId =>
      walk(id)
    case IRThis(info) =>
      walk(info)
    case IRNumber(info, text, num) =>
      walk(info)
    case IRString(info, str) =>
      walk(info)
    case IRBool(info, isBool) =>
      walk(info)
    case IRUndef(info) =>
      walk(info)
    case IRNull(info) =>
      walk(info)
  }

  def walk(node: IRMember): Result = node match {
    case IRField(info, prop, expr) =>
      join(walk(info), walk(prop), walk(expr))
    case IRGetProp(info, ftn) =>
      join(walk(info), walk(ftn))
    case IRSetProp(info, ftn) =>
      join(walk(info), walk(ftn))
  }

  def walk(node: IRFunctional): Result = node match {
    case IRFunctional(info, isFromSource, name, params, args, fds, vds, body) =>
      join(walk(info) :: walk(name) ::
        (params.map(walk) ++ args.map(walk) ++ fds.map(walk) ++ vds.map(walk) ++ body.map(walk)): _*)
  }

  def walk(node: IROp): Result = node match {
    case IROp(info, text, kind) =>
      walk(info)
  }

  def walk(node: IRFunDecl): Result = node match {
    case IRFunDecl(info, ftn) =>
      join(walk(info), walk(ftn))
  }

  def walk(node: IRVarStmt): Result = node match {
    case IRVarStmt(info, lhs, isFromParam) =>
      join(walk(info), walk(lhs))
  }

  def walk(node: IRId): Result = node match {
    case IRUserId(info, originalName, uniqueName, isGlobal, isWith) =>
      walk(info)
    case IRTmpId(info, originalName, uniqueName, isGlobal) =>
      walk(info)
  }
}
