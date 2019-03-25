/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.nodes.ast

trait ASTWalker {
  def walk(info: ASTNodeInfo): ASTNodeInfo = info match {
    case ASTNodeInfo(span, comment) =>
      ASTNodeInfo(span, comment.map(walk))
  }

  def walk(node: ASTNode): ASTNode = node match {
    case p: Program => walk(p)
    case s: Stmt => walk(s)
    case s: Stmts => walk(s)
    case v: VarDecl => walk(v)
    case c: Case => walk(c)
    case c: Catch => walk(c)
    case e: Expr => walk(e)
    case p: Property => walk(p)
    case m: Member => walk(m)
    case i: Id => walk(i)
    case o: Op => walk(o)
    case a: AnonymousFnName => walk(a)
    case l: Label => walk(l)
    case c: Comment => walk(c)
    case t: TopLevel => walk(t)
    case f: Functional => walk(f)
  }

  def walk(node: Program): Program = node match {
    case Program(info, body) =>
      Program(walk(info), walk(body))
  }

  def walk(node: Stmt): Stmt = node match {
    case NoOp(info, desc) =>
      NoOp(walk(info), desc)
    case StmtUnit(info, stmts) =>
      StmtUnit(walk(info), stmts.map(walk))
    case fd: FunDecl =>
      walk(fd)
    case ABlock(info, stmts, isInternal) =>
      ABlock(walk(info), stmts.map(walk), isInternal)
    case VarStmt(info, vds) =>
      VarStmt(walk(info), vds.map(walk))
    case EmptyStmt(info) =>
      EmptyStmt(walk(info))
    case ExprStmt(info, expr, isInternal) =>
      ExprStmt(walk(info), walk(expr), isInternal)
    case If(info, cond, trueB, falseB) =>
      If(walk(info), walk(cond), walk(trueB), falseB.map(walk))
    case DoWhile(info, body, cond) =>
      DoWhile(walk(info), walk(body), walk(cond))
    case While(info, cond, body) =>
      While(walk(info), walk(cond), walk(body))
    case For(info, init, cond, action, body) =>
      For(walk(info), init.map(walk), cond.map(walk), action.map(walk), walk(body))
    case ForIn(info, lhs, expr, body) =>
      ForIn(walk(info), walk(lhs), walk(expr), walk(body))
    case ForVar(info, vars, cond, action, body) =>
      ForVar(walk(info), vars.map(walk), cond.map(walk), action.map(walk), walk(body))
    case ForVarIn(info, vari, expr, body) =>
      ForVarIn(walk(info), walk(vari), walk(expr), walk(body))
    case Continue(info, target) =>
      Continue(walk(info), target.map(walk))
    case Break(info, target) =>
      Break(walk(info), target.map(walk))
    case Return(info, expr) =>
      Return(walk(info), expr.map(walk))
    case With(info, expr, stmt) =>
      With(walk(info), walk(expr), walk(stmt))
    case Switch(info, cond, frontCases, defi, backCases) =>
      Switch(walk(info), walk(cond), frontCases.map(walk), defi.map(_.map(walk)), backCases.map(walk))
    case LabelStmt(info, label, stmt) =>
      LabelStmt(walk(info), walk(label), walk(stmt))
    case Throw(info, expr) =>
      Throw(walk(info), walk(expr))
    case Try(info, body, catchBlock, fin) =>
      Try(walk(info), body.map(walk), catchBlock.map(walk), fin.map(_.map(walk)))
    case Debugger(info) =>
      Debugger(walk(info))
  }

  def walk(node: Expr): Expr = node match {
    case ExprList(info, exprs) =>
      ExprList(walk(info), exprs.map(walk))
    case Cond(info, cond, trueB, falseB) =>
      Cond(walk(info), walk(cond), walk(trueB), walk(falseB))
    case InfixOpApp(info, left, op, right) =>
      InfixOpApp(walk(info), walk(left), walk(op), walk(right))
    case PrefixOpApp(info, op, right) =>
      PrefixOpApp(walk(info), walk(op), walk(right))
    case UnaryAssignOpApp(info, lhs, op) =>
      UnaryAssignOpApp(walk(info), walk(lhs), walk(op))
    case AssignOpApp(info, lhs, op, right) =>
      AssignOpApp(walk(info), walk(lhs), walk(op), walk(right))
    case l: LHS =>
      walk(l)
  }

  def walk(node: LHS): LHS = node match {
    case This(info) =>
      This(walk(info))
    case Null(info) =>
      Null(walk(info))
    case Bool(info, isBool) =>
      Bool(walk(info), isBool)
    case n: NumberLiteral =>
      walk(n)
    case StringLiteral(info, quote, escaped, isRE) =>
      StringLiteral(walk(info), quote, escaped, isRE)
    case RegularExpression(info, body, flag) =>
      RegularExpression(walk(info), body, flag)
    case VarRef(info, id) =>
      VarRef(walk(info), walk(id))
    case ArrayExpr(info, elements) =>
      ArrayExpr(walk(info), elements.map(_.map(walk)))
    case ArrayNumberExpr(info, elements) =>
      ArrayNumberExpr(walk(info), elements)
    case ObjectExpr(info, members) =>
      ObjectExpr(walk(info), members.map(walk))
    case Parenthesized(info, expr) =>
      Parenthesized(walk(info), walk(expr))
    case FunExpr(info, ftn) =>
      FunExpr(walk(info), walk(ftn))
    case Bracket(info, obj, index) =>
      Bracket(walk(info), walk(obj), walk(index))
    case Dot(info, obj, member) =>
      Dot(walk(info), walk(obj), walk(member))
    case New(info, lhs) =>
      New(walk(info), walk(lhs))
    case FunApp(info, fun, args) =>
      FunApp(walk(info), walk(fun), args.map(walk))
  }

  def walk(node: NumberLiteral): NumberLiteral = node match {
    case DoubleLiteral(info, text, num) =>
      DoubleLiteral(walk(info), text, num)
    case IntLiteral(info, intVal, radix) =>
      IntLiteral(walk(info), intVal, radix)
  }

  def walk(node: Stmts): Stmts = node match {
    case Stmts(info, body, isStrict) =>
      Stmts(walk(info), body.map(walk), isStrict)
  }

  def walk(node: FunDecl): FunDecl = node match {
    case FunDecl(info, ftn, isStrict) =>
      FunDecl(walk(info), walk(ftn), isStrict)
  }

  def walk(node: VarDecl): VarDecl = node match {
    case VarDecl(info, name, expr, isStrict) =>
      VarDecl(walk(info), walk(name), expr.map(walk), isStrict)
  }

  def walk(node: Case): Case = node match {
    case Case(info, cond, body) =>
      Case(walk(info), walk(cond), body.map(walk))
  }

  def walk(node: Catch): Catch = node match {
    case Catch(info, id, body) =>
      Catch(walk(info), walk(id), body.map(walk))
  }

  def walk(node: Property): Property = node match {
    case PropId(info, id) =>
      PropId(walk(info), walk(id))
    case PropStr(info, str) =>
      PropStr(walk(info), str)
    case PropNum(info, num) =>
      PropNum(walk(info), walk(num))
  }

  def walk(node: Member): Member = node match {
    case Field(info, prop, expr) =>
      Field(walk(info), walk(prop), walk(expr))
    case GetProp(info, prop, ftn) =>
      GetProp(walk(info), walk(prop), walk(ftn))
    case SetProp(info, prop, ftn) =>
      SetProp(walk(info), walk(prop), walk(ftn))
  }

  def walk(node: Id): Id = node match {
    case Id(info, text, uniqueName, isWith) =>
      Id(walk(info), text, uniqueName, isWith)
  }

  def walk(node: Op): Op = node match {
    case Op(info, text) =>
      Op(walk(info), text)
  }

  def walk(node: AnonymousFnName): AnonymousFnName = node match {
    case AnonymousFnName(info, text) =>
      AnonymousFnName(walk(info), text)
  }

  def walk(node: Label): Label = node match {
    case Label(info, id) =>
      Label(walk(info), walk(id))
  }

  def walk(node: Comment): Comment = node match {
    case Comment(info, comment) =>
      Comment(walk(info), comment)
  }

  def walk(node: TopLevel): TopLevel = node match {
    case TopLevel(info, fds, vds, stmts) =>
      TopLevel(walk(info), fds.map(walk), vds.map(walk), stmts.map(walk))
  }

  def walk(node: Functional): Functional = node match {
    case Functional(info, fds, vds, stmts, name, params, body) =>
      Functional(walk(info), fds.map(walk), vds.map(walk), walk(stmts), walk(name),
        params.map(walk), body)
  }
}
