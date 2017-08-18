/**
 * *****************************************************************************
 * Copyright (c) 2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.clone_detector.vgen

import kr.ac.kaist.safe.clone_detector.util.Util
import kr.ac.kaist.safe.nodes.ast._

class JSAstSerializer(program: Program, st: java.util.Vector[ASTNode], minT: Int) extends ASTWalker {
  def doit(): Any = walkAST(program)
  var bDebugPrint: Boolean = false
  def DebugPrint(string: String): Unit = if (bDebugPrint) System.out.println(string)

  def walkAST(node: Any): Any = node match {
    case AnonymousFnName(info, text) =>
      DebugPrint("SAnonymousFnName")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case ArrayExpr(info, elements) =>
      DebugPrint("SArrayExpr")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(elements)
    case AssignOpApp(info, lhs, op, right) =>
      DebugPrint("SAssignOpApp")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(lhs)
      walkAST(op)
      walkAST(right)
    case ABlock(info, stmts, _) =>
      DebugPrint("SBlock")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(stmts)
    case Bool(info, isBool) =>
      DebugPrint("SBool")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case Bracket(info, obj, index) =>
      DebugPrint("SBracket")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(obj)
      walkAST(index)
    case Break(info, target) =>
      DebugPrint("SBreak")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(target)
    case Case(info, cond, body) =>
      DebugPrint("SCase")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(cond)
      walkAST(body)
    case Catch(info, id, body) =>
      DebugPrint("SCatch")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(id)
      walkAST(body)
    case Cond(info, cond, trueBranch, falseBranch) =>
      DebugPrint("SCond")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(cond)
      walkAST(trueBranch)
      walkAST(falseBranch)
    case Continue(info, target) =>
      DebugPrint("SContinue")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(target)
    case Debugger(info) =>
      DebugPrint("SDebugger")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case DoWhile(info, body, cond) =>
      DebugPrint("SDoWhile")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(body)
      walkAST(cond)
    case Dot(info, obj, member) =>
      DebugPrint("SDot")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(obj)
      walkAST(member)
    case DoubleLiteral(info, text, num) =>
      DebugPrint("SDoubleLiteral")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(text)
      walkAST(num)
    case EmptyStmt(info) =>
      DebugPrint("SEmptyStmt")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case ExprList(info, exprs) =>
      DebugPrint("SExprList")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(exprs)
    case ExprStmt(info, expr, isInternal) =>
      DebugPrint("SExprStmt")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(expr)
    case Field(info, prop, expr) =>
      DebugPrint("SField")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(prop)
      walkAST(expr)
    case For(info, init, cond, action, body) =>
      DebugPrint("SFor")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(init)
      walkAST(cond)
      walkAST(action)
      walkAST(body)
    case ForIn(info, lhs, expr, body) =>
      DebugPrint("SForIn")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(lhs)
      walkAST(expr)
      walkAST(body)
    case ForVar(info, vars, cond, action, body) =>
      DebugPrint("SForVar")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(vars)
      walkAST(cond)
      walkAST(action)
      walkAST(body)
    case ForVarIn(info, varjs, expr, body) =>
      DebugPrint("SForVarIn")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(varjs)
      walkAST(expr)
      walkAST(body)
    case FunApp(info, fun, args) =>
      DebugPrint("SFunApp")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(fun)
      walkAST(args)
    case FunDecl(info, ftn, _) =>
      DebugPrint("SFunDecl")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(ftn.name)
      walkAST(ftn)
    case FunExpr(info, ftn) =>
      DebugPrint("SFunExpr")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(ftn.name)
      walkAST(ftn)
    case Functional(info, fds, vds, stmts, name, params, body) =>
      DebugPrint("SFunctional")
      fds.foreach(walkAST)
      vds.foreach(walkAST)
      walkAST(stmts)
      params.foreach(walkAST)
    case GetProp(info, prop, ftn) =>
      DebugPrint("SGetProp")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(prop)
      walkAST(ftn)
    case Id(info, text, _, _) =>
      DebugPrint("SId \"" + text + "\"")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case If(info, cond, trueBranch, falseBranch) =>
      DebugPrint("SIf")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(cond)
      walkAST(trueBranch)
      walkAST(falseBranch)
    case InfixOpApp(info, left, op, right) =>
      DebugPrint("SInfixOpApp")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(left)
      walkAST(op)
      walkAST(right)
    case IntLiteral(info, intVal, radix) =>
      DebugPrint("SIntLiteral")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case Label(info, id) =>
      DebugPrint("SLabel")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(id)
    case LabelStmt(info, label, stmt) =>
      DebugPrint("SLabelStmt")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(label)
      walkAST(stmt)
    case New(info, lhs) =>
      DebugPrint("SNew")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(lhs)
    case Null(info) =>
      DebugPrint("SNull")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case ObjectExpr(info, members) =>
      DebugPrint("SObjectExpr")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(members)
    case Op(info, text) =>
      DebugPrint("SOp")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case Parenthesized(info, expr) =>
      DebugPrint("SParenthesized")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(expr)
    case PrefixOpApp(info, op, right) =>
      DebugPrint("SPrefixOpApp")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(op)
      walkAST(right)
    case Program(info, program) =>
      DebugPrint("SProgram")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(program)
    case PropId(info, id) =>
      DebugPrint("SPropId")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(id)
    case PropNum(info, num) =>
      DebugPrint("SPropNum")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(num)
    case PropStr(info, str) =>
      DebugPrint("SPropStr")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case RegularExpression(info, body, flags) =>
      DebugPrint("SRegularExpression")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case Return(info, expr) =>
      DebugPrint("SReturn")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(expr)
    case SetProp(info, prop, ftn) =>
      DebugPrint("SSetProp")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(prop)
      walkAST(ftn)
    case i: ASTNodeInfo =>
      val span = i.span
      DebugPrint("SpanInfo")
      DebugPrint(span.cvec.toString + span.cvec.isMergeable)
      span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case SourceElements(info, body, _) =>
      DebugPrint("SSourceElements")
      body.foreach(walkAST)
    case StringLiteral(info, _, txt, isRE) =>
      DebugPrint("SStringLiteral \"" + txt + "\"")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case Switch(info, cond, frontCases, defjs, backCases) =>
      DebugPrint("SSwitch")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(cond)
      walkAST(frontCases)
      walkAST(defjs)
      walkAST(backCases)
    case This(info) =>
      DebugPrint("SThis")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case Throw(info, expr) =>
      DebugPrint("SThrow")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(expr)
    case TopLevel(info, vds, fds, stmts) =>
      DebugPrint("STopLevel")
      vds.foreach(walkAST)
      fds.foreach(walkAST)
      stmts.foreach(walkAST)
    case Try(info, body, catchBlock, fin) =>
      DebugPrint("STry")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(body)
      walkAST(catchBlock)
      walkAST(fin)
    case UnaryAssignOpApp(info, lhs, op) =>
      DebugPrint("SUnaryAssignOpApp")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(lhs)
      walkAST(op)
    case VarDecl(info, name, expr, _) =>
      DebugPrint("SVarDecl")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(name)
      walkAST(expr)
    case VarRef(info, id) =>
      DebugPrint("SVarRef")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(id)
    case VarStmt(info, vds) =>
      DebugPrint("SVarStmt")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      vds.foreach(walkAST)
    case While(info, cond, body) =>
      DebugPrint("SWhile")
      DebugPrint(info.span.cvec.toString + info.span.cvec.isMergeable)
      info.span.cvec.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walkAST(cond)
      walkAST(body)
    case With =>
      DebugPrint("SWith")
      st.add(node.asInstanceOf[ASTNode])
    case xs: List[_] =>
      xs.foreach(walkAST)
    case xs: Option[_] =>
      xs.foreach(walkAST)
    case _ =>
      DebugPrint("Default")
  }
}