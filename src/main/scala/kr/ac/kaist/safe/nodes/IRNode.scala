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

import java.lang.Double
import java.math.BigInteger
import kr.ac.kaist.safe.util.{ NodeUtil => NU, Span }

abstract class IRNode(override val info: IRNodeInfo)
    extends Node(info: NodeInfo) {

  // Pretty printing IR nodes
  val width = 50

  def getIndent(indent: Int): String = {
    val s: StringBuilder = new StringBuilder
    for (i <- 0 to indent - 1) s.append(" ")
    s.toString
  }

  def pp(s: StringBuilder, str: String): Unit = {
    for (c <- str) c match {
      case '\u0008' => s.append("\\b")
      case '\t' => s.append("\\t")
      case '\n' => s.append("\\n")
      case '\f' => s.append("\\f")
      case '\r' => s.append("\\r")
      case '\u000b' => s.append("\\v")
      case '"' => s.append("\\\"")
      case '\'' => s.append("'")
      case '\\' => s.append("\\")
      case c => s.append(c + "")
    }
  }

  def join(indent: Int, all: List[IRNode], sep: String, result: StringBuilder): StringBuilder = all match {
    case Nil => result
    case _ => result.length match {
      case 0 => {
        join(indent, all.tail, sep, result.append(all.head.toString(indent)))
      }
      case _ =>
        if (result.length > width && sep.equals(", "))
          join(indent, all.tail, sep, result.append(", \n" + getIndent(indent)).append(all.head.toString(indent)))
        else
          join(indent, all.tail, sep, result.append(sep).append(all.head.toString(indent)))
    }
  }

  def inlineIndent(stmt: IRStmt, s: StringBuilder, indent: Int): Unit = {
    stmt match {
      case IRStmtUnit(_, stmts) if stmts.length != 1 =>
        s.append(getIndent(indent)).append(stmt.toString(indent))
      case IRSeq(_, _) =>
        s.append(getIndent(indent)).append(stmt.toString(indent))
      case _ =>
        s.append(getIndent(indent + 1)).append(stmt.toString(indent + 1))
    }
  }

  def toString(indent: Int): String
}

/**
 * IRRoot ::= Statement*
 */
case class IRRoot(override val info: IRNodeInfo, val fds: List[IRFunDecl], val vds: List[IRVarStmt], val irs: List[IRStmt])
    extends IRNode(info: IRNodeInfo) {
  override def toString(indent: Int): String = {
    NU.initIRPrint
    val s: StringBuilder = new StringBuilder
    val indentString = getIndent(indent)
    s.append(indentString).append(join(indent, fds, "\n" + indentString, new StringBuilder("")))
    s.append("\n")
    s.append(indentString).append(join(indent, vds, "\n" + indentString, new StringBuilder("")))
    s.append("\n")
    s.append(indentString).append(join(indent, irs, "\n" + indentString, new StringBuilder("")))
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
    s.append(lhs.toString(indent)).append(" = {\n")
    s.append(getIndent(indent + 1)).append(join(indent, members, ",\n" + getIndent(indent + 1), new StringBuilder("")))
    if (proto.isDefined) {
      s.append("[[Prototype]]=").append(proto.get.toString(indent + 1))
    }
    s.append("\n").append(getIndent(indent)).append("}")
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
    s.append(join(indent, args, ", ", new StringBuilder("")))
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
      s.append("{\n")
      s.append(getIndent(indent + 1)).append(join(indent + 1, stmts, "\n" + getIndent(indent + 1), new StringBuilder("")))
      s.append("\n").append(getIndent(indent)).append("}")
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
    s.append(id.toString(indent)).append(")\n")
    s.append(getIndent(indent)).append(stmt.toString(indent))
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
    s.append("{\n")
    s.append(getIndent(indent + 1)).append(join(indent + 1, stmts, "\n" + getIndent(indent + 1), new StringBuilder("")))
    s.append("\n").append(getIndent(indent)).append("}")
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
    s.append("if(").append(expr.toString(indent)).append(")\n")
    inlineIndent(trueB, s, indent)
    falseB match {
      case Some(f) =>
        s.append("\n").append(getIndent(indent)).append("else\n")
        inlineIndent(f, s, indent)
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
    s.append(cond.toString(indent)).append(")\n")
    s.append(getIndent(indent)).append(body.toString(indent))
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
    s.append("try\n")
    inlineIndent(body, s, indent)
    catchB match {
      case Some(cb) =>
        s.append("\n").append(getIndent(indent))
        s.append("catch(").append(name.get.toString(indent)).append(")\n")
        inlineIndent(cb, s, indent)
      case None =>
    }
    finallyB match {
      case Some(f) =>
        s.append("\n").append(getIndent(indent)).append("finally\n")
        inlineIndent(f, s, indent)
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
    s.append(prop.toString(indent)).append(" : ").append(expr.toString(indent))
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
    else if (!NU.isGlobalName(uniqueName)) uniqueName.dropRight(size) + NU.getE(uniqueName.takeRight(size))
    else uniqueName
    val s: StringBuilder = new StringBuilder
    pp(s, str)
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
    pp(s, str.replaceAll("\\\\", "\\\\\\\\")) // TODO need to be checked.
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
    s.append(join(indent, params, ", ", new StringBuilder("")))
    s.append(") \n").append(getIndent(indent)).append("{\n")
    s.append(getIndent(indent + 1))
    s.append(join(indent + 1, fds ++ vds ++ args ++ body, "\n" + getIndent(indent + 1), new StringBuilder("")))
    s.append("\n").append(getIndent(indent)).append("}")
    s.toString
  }
}

trait IRWalker {
  def walk(node: Any): Any = {
    node match {
      case IRArgs(info, lhs, elements) =>
        IRArgs(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(elements).asInstanceOf[List[Option[IRExpr]]])
      case IRArray(info, lhs, elements) =>
        IRArray(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(elements).asInstanceOf[List[Option[IRExpr]]])
      case IRArrayNumber(info, lhs, elements) =>
        IRArrayNumber(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(elements).asInstanceOf[List[_root_.java.lang.Double]])
      case IRBin(info, first, op, second) =>
        IRBin(walk(info).asInstanceOf[IRNodeInfo], walk(first).asInstanceOf[IRExpr], walk(op).asInstanceOf[IROp], walk(second).asInstanceOf[IRExpr])
      case IRBool(info, isBool) =>
        IRBool(walk(info).asInstanceOf[IRNodeInfo], walk(isBool).asInstanceOf[Boolean])
      case IRBreak(info, label) =>
        IRBreak(walk(info).asInstanceOf[IRNodeInfo], walk(label).asInstanceOf[IRId])
      case IRCall(info, lhs, fun, thisB, args) =>
        IRCall(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(fun).asInstanceOf[IRId], walk(thisB).asInstanceOf[IRId], walk(args).asInstanceOf[IRId])
      case IRDelete(info, lhs, id) =>
        IRDelete(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(id).asInstanceOf[IRId])
      case IRDeleteProp(info, lhs, obj, index) =>
        IRDeleteProp(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(obj).asInstanceOf[IRId], walk(index).asInstanceOf[IRExpr])
      case IREval(info, lhs, arg) =>
        IREval(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(arg).asInstanceOf[IRExpr])
      case IRExprStmt(info, lhs, right, isRef) =>
        IRExprStmt(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(right).asInstanceOf[IRExpr], walk(isRef).asInstanceOf[Boolean])
      case IRField(info, prop, expr) =>
        IRField(walk(info).asInstanceOf[IRNodeInfo], walk(prop).asInstanceOf[IRId], walk(expr).asInstanceOf[IRExpr])
      case IRFunDecl(info, ftn) =>
        IRFunDecl(walk(info).asInstanceOf[IRNodeInfo], walk(ftn).asInstanceOf[IRFunctional])
      case IRFunExpr(info, lhs, ftn) =>
        IRFunExpr(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(ftn).asInstanceOf[IRFunctional])
      case IRFunctional(info, isFromSource, name, params, args, fds, vds, body) =>
        IRFunctional(walk(info).asInstanceOf[IRNodeInfo], walk(isFromSource).asInstanceOf[Boolean], walk(name).asInstanceOf[IRId], walk(params).asInstanceOf[List[IRId]], walk(args).asInstanceOf[List[IRStmt]], walk(fds).asInstanceOf[List[IRFunDecl]], walk(vds).asInstanceOf[List[IRVarStmt]], walk(body).asInstanceOf[List[IRStmt]])
      case IRGetProp(info, ftn) =>
        IRGetProp(walk(info).asInstanceOf[IRNodeInfo], walk(ftn).asInstanceOf[IRFunctional])
      case IRIf(info, expr, trueB, falseB) =>
        IRIf(walk(info).asInstanceOf[IRNodeInfo], walk(expr).asInstanceOf[IRExpr], walk(trueB).asInstanceOf[IRStmt], walk(falseB).asInstanceOf[Option[IRStmt]])
      case IRInternalCall(info, lhs, fun, first, second) =>
        IRInternalCall(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(fun).asInstanceOf[IRId], walk(first).asInstanceOf[IRExpr], walk(second).asInstanceOf[Option[IRId]])
      case IRLabelStmt(info, label, stmt) =>
        IRLabelStmt(walk(info).asInstanceOf[IRNodeInfo], walk(label).asInstanceOf[IRId], walk(stmt).asInstanceOf[IRStmt])
      case IRLoad(info, obj, index) =>
        IRLoad(walk(info).asInstanceOf[IRNodeInfo], walk(obj).asInstanceOf[IRId], walk(index).asInstanceOf[IRExpr])
      case IRNew(info, lhs, fun, args) =>
        IRNew(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(fun).asInstanceOf[IRId], walk(args).asInstanceOf[List[IRId]])
      case IRNoOp(info, desc) =>
        IRNoOp(walk(info).asInstanceOf[IRNodeInfo], walk(desc).asInstanceOf[String])
      case IRNull(info) =>
        IRNull(walk(info).asInstanceOf[IRNodeInfo])
      case IRNumber(info, text, num) =>
        IRNumber(walk(info).asInstanceOf[IRNodeInfo], walk(text).asInstanceOf[String], walk(num).asInstanceOf[_root_.java.lang.Double])
      case IRObject(info, lhs, members, proto) =>
        IRObject(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(members).asInstanceOf[List[IRMember]], walk(proto).asInstanceOf[Option[IRId]])
      case IROp(info, text, kind) =>
        IROp(walk(info).asInstanceOf[IRNodeInfo], walk(text).asInstanceOf[String], walk(kind).asInstanceOf[Int])
      case IRReturn(info, expr) =>
        IRReturn(walk(info).asInstanceOf[IRNodeInfo], walk(expr).asInstanceOf[Option[IRExpr]])
      case IRRoot(info, fds, vds, irs) =>
        IRRoot(walk(info).asInstanceOf[IRNodeInfo], walk(fds).asInstanceOf[List[IRFunDecl]], walk(vds).asInstanceOf[List[IRVarStmt]], walk(irs).asInstanceOf[List[IRStmt]])
      case IRSeq(info, stmts) =>
        IRSeq(walk(info).asInstanceOf[IRNodeInfo], walk(stmts).asInstanceOf[List[IRStmt]])
      case IRSetProp(info, ftn) =>
        IRSetProp(walk(info).asInstanceOf[IRNodeInfo], walk(ftn).asInstanceOf[IRFunctional])
      case IRNodeInfo(span, fromSource, ast) =>
        IRNodeInfo(walk(span).asInstanceOf[Span], walk(fromSource).asInstanceOf[Boolean], ast)
      case IRStmtUnit(info, stmts) =>
        IRStmtUnit(walk(info).asInstanceOf[IRNodeInfo], walk(stmts).asInstanceOf[List[IRStmt]])
      case IRStore(info, obj, index, rhs) =>
        IRStore(walk(info).asInstanceOf[IRNodeInfo], walk(obj).asInstanceOf[IRId], walk(index).asInstanceOf[IRExpr], walk(rhs).asInstanceOf[IRExpr])
      case IRString(info, str) =>
        IRString(walk(info).asInstanceOf[IRNodeInfo], walk(str).asInstanceOf[String])
      case IRThis(info) =>
        IRThis(walk(info).asInstanceOf[IRNodeInfo])
      case IRThrow(info, expr) =>
        IRThrow(walk(info).asInstanceOf[IRNodeInfo], walk(expr).asInstanceOf[IRExpr])
      case IRTmpId(info, originalName, uniqueName, isGlobal) =>
        IRTmpId(walk(info).asInstanceOf[IRNodeInfo], walk(originalName).asInstanceOf[String], walk(uniqueName).asInstanceOf[String], walk(isGlobal).asInstanceOf[Boolean])
      case IRTry(info, body, name, catchB, finallyB) =>
        IRTry(walk(info).asInstanceOf[IRNodeInfo], walk(body).asInstanceOf[IRStmt], walk(name).asInstanceOf[Option[IRId]], walk(catchB).asInstanceOf[Option[IRStmt]], walk(finallyB).asInstanceOf[Option[IRStmt]])
      case IRUn(info, op, expr) =>
        IRUn(walk(info).asInstanceOf[IRNodeInfo], walk(op).asInstanceOf[IROp], walk(expr).asInstanceOf[IRExpr])
      case IRUndef(info) =>
        IRUndef(walk(info).asInstanceOf[IRNodeInfo])
      case IRUserId(info, originalName, uniqueName, isGlobal, isWith) =>
        IRUserId(walk(info).asInstanceOf[IRNodeInfo], walk(originalName).asInstanceOf[String], walk(uniqueName).asInstanceOf[String], walk(isGlobal).asInstanceOf[Boolean], walk(isWith).asInstanceOf[Boolean])
      case IRVarStmt(info, lhs, isFromParam) =>
        IRVarStmt(walk(info).asInstanceOf[IRNodeInfo], walk(lhs).asInstanceOf[IRId], walk(isFromParam).asInstanceOf[Boolean])
      case IRWhile(info, cond, body) =>
        IRWhile(walk(info).asInstanceOf[IRNodeInfo], walk(cond).asInstanceOf[IRExpr], walk(body).asInstanceOf[IRStmt])
      case IRWith(info, id, stmt) =>
        IRWith(walk(info).asInstanceOf[IRNodeInfo], walk(id).asInstanceOf[IRId], walk(stmt).asInstanceOf[IRStmt])
      case xs: List[_] => xs.map(walk _)
      case xs: Option[_] => xs.map(walk _)
      case _ => node
    }
  }
  def walkUnit(node: Any): Unit = {
    node match {
      case IRArgs(info, lhs, elements) =>
        walkUnit(info); walkUnit(lhs); walkUnit(elements)
      case IRArray(info, lhs, elements) =>
        walkUnit(info); walkUnit(lhs); walkUnit(elements)
      case IRArrayNumber(info, lhs, elements) =>
        walkUnit(info); walkUnit(lhs); walkUnit(elements)
      case IRBin(info, first, op, second) =>
        walkUnit(info); walkUnit(first); walkUnit(op); walkUnit(second)
      case IRBool(info, isBool) =>
        walkUnit(info); walkUnit(isBool)
      case IRBreak(info, label) =>
        walkUnit(info); walkUnit(label)
      case IRCall(info, lhs, fun, thisB, args) =>
        walkUnit(info); walkUnit(lhs); walkUnit(fun); walkUnit(thisB); walkUnit(args)
      case IRDelete(info, lhs, id) =>
        walkUnit(info); walkUnit(lhs); walkUnit(id)
      case IRDeleteProp(info, lhs, obj, index) =>
        walkUnit(info); walkUnit(lhs); walkUnit(obj); walkUnit(index)
      case IREval(info, lhs, arg) =>
        walkUnit(info); walkUnit(lhs); walkUnit(arg)
      case IRExprStmt(info, lhs, right, isRef) =>
        walkUnit(info); walkUnit(lhs); walkUnit(right); walkUnit(isRef)
      case IRField(info, prop, expr) =>
        walkUnit(info); walkUnit(prop); walkUnit(expr)
      case IRFunDecl(info, ftn) =>
        walkUnit(info); walkUnit(ftn)
      case IRFunExpr(info, lhs, ftn) =>
        walkUnit(info); walkUnit(lhs); walkUnit(ftn)
      case IRFunctional(info, isFromSource, name, params, args, fds, vds, body) =>
        walkUnit(info); walkUnit(isFromSource); walkUnit(name); walkUnit(params); walkUnit(args); walkUnit(fds); walkUnit(vds); walkUnit(body)
      case IRGetProp(info, ftn) =>
        walkUnit(info); walkUnit(ftn)
      case IRIf(info, expr, trueB, falseB) =>
        walkUnit(info); walkUnit(expr); walkUnit(trueB); walkUnit(falseB)
      case IRInternalCall(info, lhs, fun, first, second) =>
        walkUnit(info); walkUnit(lhs); walkUnit(fun); walkUnit(first); walkUnit(second)
      case IRLabelStmt(info, label, stmt) =>
        walkUnit(info); walkUnit(label); walkUnit(stmt)
      case IRLoad(info, obj, index) =>
        walkUnit(info); walkUnit(obj); walkUnit(index)
      case IRNew(info, lhs, fun, args) =>
        walkUnit(info); walkUnit(lhs); walkUnit(fun); walkUnit(args)
      case IRNoOp(info, desc) =>
        walkUnit(info); walkUnit(desc)
      case IRNull(info) =>
        walkUnit(info)
      case IRNumber(info, text, num) =>
        walkUnit(info); walkUnit(text); walkUnit(num)
      case IRObject(info, lhs, members, proto) =>
        walkUnit(info); walkUnit(lhs); walkUnit(members); walkUnit(proto)
      case IROp(info, text, kind) =>
        walkUnit(info); walkUnit(text); walkUnit(kind)
      case IRReturn(info, expr) =>
        walkUnit(info); walkUnit(expr)
      case IRRoot(info, fds, vds, irs) =>
        walkUnit(info); walkUnit(fds); walkUnit(vds); walkUnit(irs)
      case IRSeq(info, stmts) =>
        walkUnit(info); walkUnit(stmts)
      case IRSetProp(info, ftn) =>
        walkUnit(info); walkUnit(ftn)
      case IRNodeInfo(span, fromSource, ast) =>
        walkUnit(span); walkUnit(fromSource)
      case IRStmtUnit(info, stmts) =>
        walkUnit(info); walkUnit(stmts)
      case IRStore(info, obj, index, rhs) =>
        walkUnit(info); walkUnit(obj); walkUnit(index); walkUnit(rhs)
      case IRString(info, str) =>
        walkUnit(info); walkUnit(str)
      case IRThis(info) =>
        walkUnit(info)
      case IRThrow(info, expr) =>
        walkUnit(info); walkUnit(expr)
      case IRTmpId(info, originalName, uniqueName, isGlobal) =>
        walkUnit(info); walkUnit(originalName); walkUnit(uniqueName); walkUnit(isGlobal)
      case IRTry(info, body, name, catchB, finallyB) =>
        walkUnit(info); walkUnit(body); walkUnit(name); walkUnit(catchB); walkUnit(finallyB)
      case IRUn(info, op, expr) =>
        walkUnit(info); walkUnit(op); walkUnit(expr)
      case IRUndef(info) =>
        walkUnit(info)
      case IRUserId(info, originalName, uniqueName, isGlobal, isWith) =>
        walkUnit(info); walkUnit(originalName); walkUnit(uniqueName); walkUnit(isGlobal); walkUnit(isWith)
      case IRVarStmt(info, lhs, isFromParam) =>
        walkUnit(info); walkUnit(lhs); walkUnit(isFromParam)
      case IRWhile(info, cond, body) =>
        walkUnit(info); walkUnit(cond); walkUnit(body)
      case IRWith(info, id, stmt) =>
        walkUnit(info); walkUnit(id); walkUnit(stmt)
      case xs: List[_] => xs.foreach(walkUnit _)
      case xs: Option[_] => xs.foreach(walkUnit _)
      case _: Span =>
      case _ =>
    }
  }
}
