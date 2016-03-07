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

package kr.ac.kaist.safe.safe_util

import _root_.java.util.ArrayList
import _root_.java.util.{ List => JList }
import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.safe_util.{ NodeUtil => NU }
import kr.ac.kaist.safe.scala_useful.Lists._
import kr.ac.kaist.safe.scala_useful.Options._
import edu.rice.cs.plt.iter.IterUtil
import edu.rice.cs.plt.tuple.Option

object JSAstToConcrete extends Walker {

  val width = 50
  var internal = false
  var testWith = false
  var typescript = false
  val significantBits = NU.significantBits

  def doit(node: ASTNode): String = walk(node)
  def doitInternal(node: ASTNode): String = {
    internal = true
    walk(node)
  }
  def doitTestWith(node: ASTNode): String = {
    testWith = true
    walk(node)
  }
  def setTypeScript: Unit = {
    typescript = true
  }

  /* indentation utilities *************************************************/
  var indent = 0
  val tab: StringBuilder = new StringBuilder("  ")
  def increaseIndent: Unit = indent += 1
  def decreaseIndent: Unit = indent -= 1
  def getIndent: String = {
    val s: StringBuilder = new StringBuilder
    for (i <- 0 to indent - 1) s.append(tab)
    s.toString
  }
  def isOneline(node: Any): Boolean = node match {
    case Block => false
    case Some(in) => isOneline(in)
    case _ => !(node.isInstanceOf[Block])
  }

  /* utility methods ********************************************************/

  /*  make sure it is parenthesized */
  def inParentheses(str: String): String = {
    val charArr = str.toCharArray
    var parenthesized = true
    var depth = 0
    for (
      c <- charArr if parenthesized
    ) {
      if (c == '(') depth += 1
      else if (c == ')') depth -= 1
      else if (depth == 0) parenthesized = false
    }
    if (parenthesized) str
    else new StringBuilder("(").append(str).append(")").toString
  }

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

  var uniq_id = 0
  def fresh(): String = { uniq_id += 1; uniq_id.toString }
  type Env = List[(String, String)]
  var env = Nil.asInstanceOf[Env]
  def addE(uniq: String, new_uniq: String): Unit = env = (uniq, new_uniq) :: env
  def getE(uniq: String): String = env.find(p => p._1.equals(uniq)) match {
    case None =>
      val new_uniq = fresh
      addE(uniq, new_uniq)
      new_uniq
    case Some((_, new_uniq)) => new_uniq
  }

  def pp(s: StringBuilder, str: String): Unit = {
    for (c <- str) c match {
      case '\u0008' => s.append("\\b")
      case '\t' => s.append("\\t")
      case '\n' => s.append("\\n")
      case '\f' => s.append("\\f")
      case '\r' => s.append("\\r")
      case '\u000b' => s.append("\\v")
      case '"' => s.append("\"")
      case '\'' => s.append("'")
      case '\\' => s.append("\\")
      case c => s.append(c + "")
    }
  }

  def prBody(body: JList[SourceElement]): String =
    join(toList(body), "\n", new StringBuilder("")).toString

  def prFtn(s: StringBuilder, fds: List[FunDecl], vds: List[VarDecl],
    body: List[SourceElement]): Unit = {
    fds match {
      case Nil =>
      case _ =>
        increaseIndent
        s.append(getIndent).append(join(fds, "\n" + getIndent, new StringBuilder("")))
        decreaseIndent
        s.append("\n").append(getIndent)
    }
    vds match {
      case Nil =>
      case _ =>
        increaseIndent
        s.append(getIndent)
        vds.foreach(vd => vd match {
          case VarDecl(_, n, _, _) =>
            s.append("var " + n.text + ";\n" + getIndent)
        })
        decreaseIndent
        s.append("\n").append(getIndent)
    }
    increaseIndent
    s.append(getIndent).append(join(body, "\n" + getIndent, new StringBuilder("")))
    decreaseIndent
  }

  def prUseStrictDirective(s: StringBuilder, fds: List[FunDecl], vds: List[VarDecl], body: SourceElements): Unit =
    prUseStrictDirective(s, fds, vds, List(body))

  def prUseStrictDirective(s: StringBuilder, fds: List[FunDecl], vds: List[VarDecl], stmts: List[SourceElements]): Unit =
    fds.find(fd => fd.strict) match {
      case Some(_) => s.append(getIndent).append("\"use strict\";\n")
      case None => vds.find(vd => vd.strict) match {
        case Some(_) => s.append(getIndent).append("\"use strict\";\n")
        case None => stmts.find(stmts => stmts.strict) match {
          case Some(_) => s.append(getIndent).append("\"use strict\";\n")
          case None =>
        }
      }
    }

  /* The rule of separators(indentation, semicolon and newline) in unparsing pattern matchings.
   * This rule is applied recursively
   * Principle: All case already has indentation at the front and newline at the end.
   *
   * Root case: See [case Program].
   *    program's type is List<SourceElement>.
   *    Each <SourceElement> has indentation at the front
   *    and newline at the end to keep the principle.
   *
   * Branch case(Stmt or ListofStmt): Block, FunDecl, VarStmt, ExprStmt...
   *    Add indentation and newline to keep the principle in inner cases.
   *    When its type is [Stmt], add ";" at the end.
   *
   * Leaf case(not Stmt, may have inner case): ExprList, ArrayExpr, ...
   *    Don't add indentation, newline and ";".
   *    They are already added.
   *    But other separators(like ", " or " ") may be added.
   *
   */
  override def walk(node: Any): String = node match {
    case ArrayExpr(info, elements) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("[")
      elements.foreach(e => s.append(walk(e)).append(", "))
      s.append("]")
      s.toString
    case ArrayNumberExpr(info, elements) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("[")
      s.append("A LOT!!! " + elements.size + " elements are not printed here.")
      s.append("]")
      s.toString
    case AssignOpApp(info, lhs, op, right) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append(walk(lhs)).append(" ")
      s.append(walk(op)).append(" ")
      s.append(walk(right))
      s.toString
    case Block(info, stmts, _) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("{\n")
      increaseIndent
      s.append(getIndent).append(join(stmts, "\n" + getIndent, new StringBuilder("")))
      decreaseIndent
      s.append("\n").append(getIndent).append("}")
      s.toString
    case StmtUnit(info, stmts) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("{\n")
      increaseIndent
      s.append(getIndent).append(join(stmts, "\n" + getIndent, new StringBuilder("")))
      decreaseIndent
      s.append("\n").append(getIndent).append("}")
      s.toString
    case Bool(info, isBool) =>
      walk(info) + (if (isBool) "true" else "false")
    case Bracket(info, obj, index) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append(walk(obj)).append("[").append(walk(index)).append("]")
      s.toString
    case Break(info, target) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("break")
      if (target.isSome) s.append(" ").append(walk(target))
      s.append(";")
      s.toString
    case Case(info, cond, body) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("case ").append(walk(cond))
      s.append(":\n")
      increaseIndent
      s.append(getIndent).append(join(body, "\n" + getIndent, new StringBuilder("")))
      decreaseIndent
      s.append("\n")
      s.toString
    case Catch(info, id, body) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      var oneline: Boolean = isOneline(body)
      s.append("catch (").append(walk(id)).append(")\n")
      increaseIndent
      s.append("{")
      s.append(getIndent).append(join(body, "\n" + getIndent, new StringBuilder("")))
      s.append("}\n")
      decreaseIndent
      s.toString
    case Cond(info, cond, trueBranch, falseBranch) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append(walk(cond)).append(" ? ").append(walk(trueBranch)).append(" : ").append(walk(falseBranch))
      s.toString
    case Continue(info, target) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("continue")
      if (target.isSome) s.append(" ").append(walk(target))
      s.append(";")
      s.toString
    case Debugger(info) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("debugger;")
      s.toString
    case DoWhile(info, body, cond) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      var oneline: Boolean = isOneline(body)
      s.append("do\n")
      if (oneline) increaseIndent
      s.append(getIndent).append(walk(body))
      if (oneline) decreaseIndent
      s.append("while (")
      s.append(walk(cond)).append(");")
      s.toString
    case Dot(info, obj, member) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append(walk(obj)).append(".").append(walk(member))
      s.toString
    case EmptyStmt(info) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append(";")
      s.toString
    case ExprList(info, exprs) =>
      join(exprs, ", ", new StringBuilder(walk(info))).toString
    case ExprStmt(info, expr, _) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append(walk(expr) + ";")
      s.toString
    case Field(info, prop, expr) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append(walk(prop)).append(" : ").append(walk(expr))
      s.toString
    case DoubleLiteral(info, text, num) =>
      walk(info) + text
    case For(info, init, cond, action, body) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      var oneline: Boolean = isOneline(body)
      s.append("for (")
      if (init.isSome) s.append(walk(init))
      s.append(";")
      if (cond.isSome) s.append(walk(cond))
      s.append(";")
      if (action.isSome) s.append(walk(action))
      s.append(")\n")
      if (oneline) increaseIndent
      s.append(getIndent).append(walk(body))
      if (oneline) decreaseIndent
      s.toString
    case ForIn(info, lhs, expr, body) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      var oneline: Boolean = isOneline(body)
      s.append("for (")
      s.append(walk(lhs)).append(" in ").append(walk(expr)).append(")\n")
      if (oneline) increaseIndent
      s.append(getIndent).append(walk(body))
      if (oneline) decreaseIndent
      s.toString
    case ForVar(info, vars, cond, action, body) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      var oneline: Boolean = isOneline(body)
      s.append("for(var ")
      s.append(join(vars, ", ", new StringBuilder("")))
      s.append(";")
      if (cond.isSome) s.append(walk(cond))
      s.append(";")
      if (action.isSome) s.append(walk(action))
      s.append(")\n")
      if (oneline) increaseIndent
      s.append(getIndent).append(walk(body))
      if (oneline) decreaseIndent
      s.toString
    case ForVarIn(info, varjs, expr, body) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      var oneline: Boolean = isOneline(body)
      s.append("for(var ")
      s.append(walk(varjs)).append(" in ").append(walk(expr)).append(")\n")
      if (oneline) increaseIndent
      s.append(getIndent).append(walk(body))
      if (oneline) decreaseIndent
      s.toString
    case FunApp(info, fun, args) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append(walk(fun)).append("(")
      s.append(join(args, ", ", new StringBuilder("")))
      s.append(")")
      s.toString
    case FunDecl(info, Functional(_, fds, vds, body, name, params, _), _) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("function ").append(walk(name)).append("(")
      s.append(join(params, ", ", new StringBuilder("")))
      s.append(") \n").append(getIndent).append("{\n")
      prUseStrictDirective(s, fds, vds, body)
      prFtn(s, fds, vds, body.body)
      s.append("\n").append(getIndent).append("}")
      s.toString
    case FunExpr(info, Functional(_, fds, vds, body, name, params, _)) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("(function ")
      if (!NU.isFunExprName(name.text)) s.append(walk(name))
      s.append("(")
      s.append(join(params, ", ", new StringBuilder("")))
      s.append(") \n").append(getIndent).append("{\n")
      prUseStrictDirective(s, fds, vds, body)
      prFtn(s, fds, vds, body.body)
      s.append("\n").append(getIndent).append("})")
      s.toString
    case GetProp(info, prop, Functional(_, fds, vds, body, _, _, _)) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("get ").append(walk(prop)).append("()\n").append(getIndent).append("{\n")
      prUseStrictDirective(s, fds, vds, body)
      prFtn(s, fds, vds, body.body)
      s.append("\n").append(getIndent).append("}")
      s.toString
    case Id(info, text, Some(uniq), _) =>
      walk(info) + (if (internal && NU.isInternal(uniq))
        uniq.dropRight(significantBits) + getE(uniq.takeRight(significantBits))
      else text)
    case Id(info, text, None, _) =>
      walk(info) + text
    case If(info, cond, trueBranch, falseBranch) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      var oneline: Boolean = isOneline(trueBranch)
      s.append("if (").append(walk(cond)).append(")\n")
      if (oneline) increaseIndent
      s.append(getIndent).append(walk(trueBranch))
      if (oneline) decreaseIndent
      if (falseBranch.isSome) {
        oneline = isOneline(falseBranch)
        s.append("\n").append(getIndent).append("else\n")
        if (oneline) increaseIndent
        s.append(getIndent).append(walk(falseBranch))
        if (oneline) decreaseIndent
      }
      s.toString
    case InfixOpApp(info, left, op, right) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append(walk(left)).append(" ")
      s.append(walk(op)).append(" ")
      s.append(walk(right))
      s.toString
    case IntLiteral(info, intVal, radix) =>
      val str = if (radix == 8) "0" + intVal.toString(8)
      else if (radix == 16) "0x" + intVal.toString(16)
      else intVal.toString
      walk(info) + str
    case Label(info, id) =>
      walk(info) + walk(id)
    case LabelStmt(info, label, stmt) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append(walk(label)).append(" : ").append(walk(stmt))
      s.toString
    case New(info, lhs) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("new ").append(walk(lhs))
      s.toString
    case Null(info) =>
      walk(info) + "null"
    case ObjectExpr(info, members) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("{\n")
      increaseIndent
      s.append(getIndent).append(join(members, ",\n" + getIndent, new StringBuilder("")))
      decreaseIndent
      s.append("\n").append(getIndent).append("}")
      s.toString
    case Op(info, text) =>
      walk(info) + text
    case Parenthesized(info, expr) =>
      walk(info) + inParentheses(walk(expr))
    case PrefixOpApp(info, op, right) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append(walk(op)).append(" ").append(walk(right))
      s.toString
    case Program(info, TopLevel(_, fds, vds, program)) =>
      val s: StringBuilder = new StringBuilder
      prUseStrictDirective(s, fds, vds, program)
      prFtn(s, fds, vds, NU.toStmts(program))
      s.append(walk(info))
      s.toString
    case PropId(info, id) =>
      walk(info) + walk(id)
    case PropNum(info, num) => walk(info) + walk(num)
    case PropStr(info, str) =>
      walk(info) + (if (str.equals("\"")) "'\"'" else "\"" + str + "\"")
    case RegularExpression(info, body, flags) =>
      walk(info) + (if (testWith) "/" + body + "/" + flags
      else "/" + NU.unescapeJava(body) + "/" + NU.unescapeJava(flags))
    case Return(info, expr) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("return")
      if (expr.isSome) s.append(" ").append(walk(expr))
      s.append(";")
      s.toString
    case SetProp(info, prop, Functional(_, fds, vds, body, _, List(id), _)) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("set ").append(walk(prop)).append("(")
      s.append(walk(id)).append(") \n").append(getIndent).append("{\n")
      prUseStrictDirective(s, fds, vds, body)
      prFtn(s, fds, vds, body.body)
      s.append("\n").append(getIndent).append("}")
      s.toString
    case StringLiteral(info, quote, txt) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append(quote)
      if (typescript) pp(s, txt)
      else pp(s, NU.unescapeJava(txt))
      s.append(quote)
      s.toString
    case Switch(info, cond, frontCases, defjs, backCases) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("switch (").append(walk(cond)).append("){\n")
      increaseIndent
      s.append(getIndent).append(join(frontCases, "\n" + getIndent, new StringBuilder("")))
      if (defjs.isSome) {
        s.append("\n").append(getIndent).append("default:")
        increaseIndent
        s.append("\n").append(getIndent).append(join(defjs.unwrap, "\n" + getIndent, new StringBuilder("")))
        decreaseIndent
      }
      s.append("\n").append(getIndent).append(join(backCases, "\n" + getIndent, new StringBuilder("")))
      decreaseIndent
      s.append("\n").append(getIndent).append("}")
      s.toString
    case This(info) =>
      walk(info) + "this"
    case Throw(info, expr) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("throw ").append(walk(expr)).append(";")
      s.toString
    case Try(info, body, catchBlock, fin) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append("try\n{")
      increaseIndent
      s.append(getIndent).append(join(body, "\n" + getIndent, new StringBuilder("")))
      s.append("}")
      decreaseIndent
      if (catchBlock.isSome) s.append("\n").append(getIndent).append(walk(catchBlock))
      if (fin.isSome) {
        var oneline: Boolean = isOneline(fin)
        s.append("\n").append(getIndent).append("finally\n{")
        increaseIndent
        s.append(getIndent).append(join(fin.get, "\n" + getIndent, new StringBuilder("")))
        s.append("}\n")
        decreaseIndent
      }
      s.toString
    case UnaryAssignOpApp(info, lhs, op) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append(walk(lhs)).append(walk(op))
      s.toString
    case VarDecl(info, name, expr, _) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      s.append(walk(name))
      if (expr.isSome) s.append(" = ").append(walk(expr))
      s.toString
    case VarRef(info, id) =>
      walk(info) + walk(id)
    case VarStmt(info, vds) => vds match {
      case Nil => walk(info)
      case _ =>
        val s: StringBuilder = new StringBuilder
        s.append(walk(info))
        s.append("var ")
        s.append(join(vds, ", ", new StringBuilder(""))).append(";")
        s.toString
    }
    case While(info, cond, body) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      var oneline: Boolean = isOneline(body)
      s.append("while (")
      s.append(walk(cond)).append(")\n")
      if (oneline) increaseIndent
      s.append(getIndent).append(walk(body))
      if (oneline) decreaseIndent
      s.toString
    case With(info, expr, stmt) =>
      val s: StringBuilder = new StringBuilder
      s.append(walk(info))
      var oneline: Boolean = isOneline(stmt)
      s.append("with (")
      s.append(walk(expr)).append(")\n")
      if (oneline) increaseIndent
      s.append(getIndent).append(walk(stmt))
      if (oneline) decreaseIndent
      s.toString
    case Comment(info, comment) =>
      comment + "\n"
    case ASTNodeInfo(_, comment) => walk(comment)
    case _: NoOp => ""
    case Some(in) =>
      walk(in)
    case None => ""
    case _ =>
      "#@#" + node.getClass.toString
  }
}
