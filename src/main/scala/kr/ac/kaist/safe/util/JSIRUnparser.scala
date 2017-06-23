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

import kr.ac.kaist.safe.SIGNIFICANT_BITS
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util.{ NodeUtil => NU, _ }

/* Converts an IR to a string which is the concrete version of that IR
 */
class JSIRUnparser(program: IRNode) {

  def doit(): String = walk(program)
  val width: Int = 50

  /* indentation utilities *************************************************/
  var indent = 0
  val tab: StringBuilder = new StringBuilder("  ")
  def increaseIndent(): Unit = indent += 1
  def decreaseIndent(): Unit = indent -= 1
  def getIndent = {
    val s: StringBuilder = new StringBuilder
    for (i <- 0 to indent - 1) s.append(tab)
    s.toString
  }
  def isOneline(node: Any): Boolean = node match {
    case _: ABlock => false
    case Some(in) => isOneline(in)
    case _ => !node.isInstanceOf[ABlock]
  }

  /* utility methods ********************************************************/

  /*  make sure it is parenthesized */
  def inParentheses(str: String) =
    if (str.startsWith("(") && str.endsWith(")")) str
    else new StringBuilder("(").append(str).append(")").toString

  def join(all: List[Any], sep: String, result: StringBuilder): StringBuilder = all match {
    case Nil => result
    case _ => result.length match {
      case 0 => {
        join(all.tail, sep, result.append(walk(all.head)))
      }
      case _ =>
        if (result.length > width && sep.equals(", "))
          join(all.tail, sep, result.append(", \n" + getIndent).append(walk(all.head)))
        else
          join(all.tail, sep, result.append(sep).append(walk(all.head)))
    }
  }

  var uniq_id: Int = 0
  def fresh: String = {
    uniq_id += 1
    uniq_id.toString
  }
  type Env = List[(String, String)]
  var env = Nil.asInstanceOf[Env]
  /*
   * The following line is to get unsimplified name.
   * Check interpret method in Shell.java to print IR program before Interpreter.
   */
  // def getE(uniq: String): String = uniq
  def getE(uniq: String): String = env.find(p => p._1.equals(uniq)) match {
    case None =>
      val new_uniq = fresh
      env = (uniq, new_uniq) :: env
      new_uniq
    case Some((_, new_uniq)) => new_uniq
  }

  def pp(s: StringBuilder, str: String) = {
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

  def printFun(s: StringBuilder, header: String, name: IRId, params: List[IRId],
    args: List[IRStmt], fds: List[IRFunDecl], vds: List[IRVarStmt],
    body: List[IRStmt]) = {
    s.append(header).append(walk(name)).append("(")
    s.append(join(params, ", ", new StringBuilder("")))
    s.append(") \n").append(getIndent).append("{\n")
    increaseIndent
    s.append(getIndent).append(join(fds ++ vds ++ args ++ body, "\n" + getIndent, new StringBuilder("")))
    decreaseIndent
    s.append("\n").append(getIndent).append("}")
    s.toString
  }

  def id2str(n: String): String = {
    val size = SIGNIFICANT_BITS
    if (!NU.isInternal(n)) n
    else if (!NU.isGlobalName(n)) n.dropRight(size) + getE(n.takeRight(size))
    else n
  }

  /* The rule of separators(indentation, semicolon and newline) in unparsing pattern matchings.
   * This rule is applied recursively
   * Principle: All case already has indentation at the front and newline at the end.
   */
  def walk(node: Any): String = node match {
    case IRArray(_, lhs, elements) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(lhs)).append(" = ")
      s.append("[")
      elements.foreach(e => s.append(walk(e)).append(", "))
      s.append("]")
      s.toString
    case IRArrayNumber(_, lhs, elements) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(lhs)).append(" = ")
      s.append("[")
      s.append("A LOT!!! " + elements.size + " elements are not printed here.")
      s.append("]")
      s.toString
    case IRArgs(_, lhs, elements) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(lhs)).append(" = ")
      s.append("[")
      elements.foreach(e => s.append(walk(e)).append(", "))
      s.append("]")
      s.toString
    case IRBin(_, left, op, right) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(left)).append(" ")
      s.append(walk(op)).append(" ")
      s.append(walk(right))
      s.toString
    case IRVal(EJSBool(b)) => if (b) "true" else "false"
    case IRBreak(_, label) =>
      val s: StringBuilder = new StringBuilder
      s.append("break ") append (walk(label))
      s.toString
    case IRInternalCall(_, lhs, fun, first :: rest) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(lhs)).append(" = ")
      s.append(walk(fun)).append("(")
      s.append(walk(first))
      rest.foreach((r) => s.append(", ").append(walk(rest.head)))
      s.append(")")
      s.toString
    case IRCall(_, lhs, fun, thisB, args) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(lhs)).append(" = ")
      s.append(walk(fun)).append("(")
      s.append(walk(thisB))
      s.append(", ").append(walk(args))
      s.append(")")
      s.toString
    case IRNew(_, lhs, fun, args) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(lhs)).append(" = new ")
      s.append(walk(fun)).append("(")
      s.append(join(args, ", ", new StringBuilder("")))
      s.append(")")
      s.toString
    case IREval(_, lhs, arg) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(lhs)).append(" = eval(").append(walk(arg)).append(")")
      s.toString
    case IRExprStmt(_, lhs, right, _) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(lhs)).append(" = ").append(walk(right))
      s.toString
    case IRField(_, prop, expr) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(prop)).append(" : ").append(walk(expr))
      s.toString
    case IRVal(EJSNumber(text, num)) => text
    case IRFunDecl(_, IRFunctional(_, _, name, params, args, fds, vds, body)) =>
      val s: StringBuilder = new StringBuilder
      printFun(s, "function ", name, params, args, fds, vds, body)
      s.toString
    case IRFunExpr(_, lhs, IRFunctional(_, _, name, params, args, fds, vds, body)) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(lhs)).append(" = ")
      printFun(s, "function ", name, params, args, fds, vds, body)
      s.toString
    case IRGetProp(_, IRFunctional(_, _, name, params, args, fds, vds, body)) =>
      val s: StringBuilder = new StringBuilder
      printFun(s, "get ", name, params, args, fds, vds, body)
      s.toString
    case id: IRId => id2str(id.uniqueName)
    case IRIf(_, expr, trueBranch, falseBranch) =>
      val s: StringBuilder = new StringBuilder
      var oneline: Boolean = isOneline(trueBranch)
      s.append("if(").append(walk(expr)).append(")\n")
      if (oneline) increaseIndent
      s.append(getIndent).append(walk(trueBranch))
      if (oneline) decreaseIndent
      if (falseBranch.isDefined) {
        oneline = isOneline(falseBranch)
        s.append("\n").append(getIndent).append("else\n")
        if (oneline) increaseIndent
        s.append(getIndent).append(walk(falseBranch))
        if (oneline) decreaseIndent
      }
      s.toString
    case IRLabelStmt(_, label, stmt) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(label)).append(" : ").append(walk(stmt))
      s.toString
    case IRLoad(_, obj, index) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(obj)).append("[").append(walk(index)).append("]")
      s.toString
    case IRVal(EJSNull) => "null"
    case IRObject(_, lhs, members, proto) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(lhs)).append(" = {\n")
      increaseIndent
      s.append(getIndent).append(join(members, ",\n" + getIndent, new StringBuilder("")))
      if (proto.isDefined) {
        s.append("[[Prototype]]=").append(walk(proto.get))
      }
      decreaseIndent
      s.append("\n").append(getIndent).append("}")
      s.toString
    case op @ IROp(ast, _) => op.name
    case IRReturn(_, expr) =>
      val s: StringBuilder = new StringBuilder
      s.append("return")
      if (expr.isDefined) s.append(" ").append(walk(expr))
      s.toString
    case IRRoot(_, fds, vds, irs) =>
      val s: StringBuilder = new StringBuilder
      s.append(getIndent).append(join(fds, "\n" + getIndent, new StringBuilder("")))
      s.append("\n")
      s.append(getIndent).append(join(vds, "\n" + getIndent, new StringBuilder("")))
      s.append("\n")
      s.append(getIndent).append(join(irs, "\n" + getIndent, new StringBuilder("")))
      s.toString
    case IRSeq(_, stmts) =>
      val s: StringBuilder = new StringBuilder
      s.append("{\n")
      increaseIndent
      s.append(getIndent).append(join(stmts, "\n" + getIndent, new StringBuilder("")))
      decreaseIndent
      s.append("\n").append(getIndent).append("}")
      s.toString
    case IRSetProp(_, IRFunctional(_, _, name, params, args, fds, vds, body)) =>
      val s: StringBuilder = new StringBuilder
      printFun(s, "set ", name, params, args, fds, vds, body)
      s.toString
    case IRStore(_, obj, index, rhs) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(obj)).append("[").append(walk(index))
      s.append("] = ").append(walk(rhs))
      s.toString
    case IRVal(EJSString(str)) =>
      val s: StringBuilder = new StringBuilder
      s.append("\"")
      pp(s, str.replaceAll("\\\\", "\\\\\\\\")) // TODO need to be checked.
      s.append("\"")
      s.toString
    case IRThrow(_, expr) =>
      val s: StringBuilder = new StringBuilder
      s.append("throw ").append(walk(expr))
      s.toString
    case IRTry(_, body, name, catchBlock, fin) =>
      val s: StringBuilder = new StringBuilder
      s.append("try\n").append(getIndent).append(walk(body))
      if (catchBlock.isDefined) {
        s.append("\n").append(getIndent)
        var oneline: Boolean = isOneline(body)
        s.append("catch(").append(walk(name.get)).append(")\n")
        if (oneline) increaseIndent
        s.append(getIndent).append(walk(catchBlock.get))
        if (oneline) decreaseIndent
      }
      if (fin.isDefined) {
        var oneline: Boolean = isOneline(fin)
        s.append("\n").append(getIndent).append("finally\n")
        if (oneline) increaseIndent
        s.append(getIndent).append(walk(fin))
        if (oneline) decreaseIndent
      }
      s.toString
    case IRUn(_, op, expr) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(op)).append(" ").append(walk(expr))
      s.toString
    case IRDelete(_, lhs, expr) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(lhs)).append(" = delete ").append(walk(expr))
      s.toString
    case IRDeleteProp(_, lhs, obj, index) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(lhs)).append(" = delete ")
      s.append(walk(obj)).append("[").append(walk(index)).append("]")
      s.toString
    case IRVal(EJSUndef) => "undefined"
    case _: IRThis => "this"
    case IRVarStmt(_, lhs, _) =>
      val s: StringBuilder = new StringBuilder
      s.append("var ").append(walk(lhs))
      s.toString
    case IRWhile(_, cond, body, breakLabel, contLabel) =>
      val s: StringBuilder = new StringBuilder
      var oneline: Boolean = isOneline(body)
      s.append("while(")
      s.append(walk(cond)).append(")\n")
      if (oneline) increaseIndent
      s.append(getIndent).append(walk(body))
      if (oneline) decreaseIndent
      s.toString
    case IRWith(_, expr, stmt) =>
      val s: StringBuilder = new StringBuilder
      var oneline: Boolean = isOneline(stmt)
      s.append("with(")
      s.append(walk(expr)).append(")\n")
      if (oneline) increaseIndent
      s.append(getIndent).append(walk(stmt))
      if (oneline) decreaseIndent
      s.toString
    case Some(in) => walk(in)
    case xs: List[_] =>
      val s: StringBuilder = new StringBuilder
      s.append(join(xs, ", ", new StringBuilder("")))
      s.toString
    case IRStmtUnit(_, List(stmt)) => walk(stmt)
    case IRStmtUnit(_, stmts) =>
      val s: StringBuilder = new StringBuilder
      s.append("{\n")
      increaseIndent
      s.append(getIndent).append(join(stmts, "\n" + getIndent, new StringBuilder("")))
      decreaseIndent
      s.append("\n").append(getIndent).append("}")
      s.toString
    case _: IRNoOp => ""
    case None => ""
    case _ => "#@#" + node.getClass.toString
  }
}
