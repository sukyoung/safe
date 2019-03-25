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

trait ASTGeneralWalker[Result] {
  def join(args: Result*): Result

  def walkOptList(opt: Option[List[ASTNode]]): List[Result] = opt match {
    case Some(l) => l.map(walk)
    case _ => List[Result]()
  }

  def walkOpt(opt: Option[ASTNode]): List[Result] =
    opt.fold(List[Result]()) { n: ASTNode =>
      List(n match {
        case s: Stmt => walk(s)
        case e: Expr => walk(e)
        case c: Catch => walk(c)
        case l: Label => walk(l)
      })
    }

  def walk(info: ASTNodeInfo): Result = join()

  def walk(node: ASTNode): Result = node match {
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

  def walk(node: Program): Result = node match {
    case Program(info, body) =>
      join(walk(info), walk(body))
  }

  def walk(node: Stmt): Result = node match {
    case NoOp(info, desc) =>
      walk(info)
    case StmtUnit(info, stmts) =>
      join(walk(info) :: stmts.map(walk): _*)
    case fd: FunDecl =>
      walk(fd)
    case ABlock(info, stmts, isInternal) =>
      join(walk(info) :: stmts.map(walk): _*)
    case VarStmt(info, vds) =>
      join(walk(info) :: vds.map(walk): _*)
    case EmptyStmt(info) =>
      walk(info)
    case ExprStmt(info, expr, isInternal) =>
      join(walk(info), walk(expr))
    case If(info, cond, trueB, falseB) =>
      join(walk(info) :: walk(cond) :: walk(trueB) :: walkOpt(falseB): _*)
    case DoWhile(info, body, cond) =>
      join(walk(info), walk(body), walk(cond))
    case While(info, cond, body) =>
      join(walk(info), walk(cond), walk(body))
    case For(info, init, cond, action, body) =>
      join(walk(info) :: walkOpt(init) ++ walkOpt(cond) ++ walkOpt(action) ++ List(walk(body)): _*)
    case ForIn(info, lhs, expr, body) =>
      join(walk(info), walk(lhs), walk(expr), walk(body))
    case ForVar(info, vars, cond, action, body) =>
      join(walk(info) :: vars.map(walk) ++ walkOpt(cond) ++ walkOpt(action) ++ List(walk(body)): _*)
    case ForVarIn(info, vari, expr, body) =>
      join(walk(info), walk(vari), walk(expr), walk(body))
    case Continue(info, target) =>
      join(walk(info) :: walkOpt(target): _*)
    case Break(info, target) =>
      join(walk(info) :: walkOpt(target): _*)
    case Return(info, expr) =>
      join(walk(info) :: walkOpt(expr): _*)
    case With(info, expr, stmt) =>
      join(walk(info), walk(expr), walk(stmt))
    case Switch(info, cond, frontCases, defi, backCases) =>
      join(walk(info) :: walk(cond) :: frontCases.map(walk) ++ walkOptList(defi) ++ backCases.map(walk): _*)
    case LabelStmt(info, label, stmt) =>
      join(walk(info), walk(label), walk(stmt))
    case Throw(info, expr) =>
      join(walk(info), walk(expr))
    case Try(info, body, catchBlock, fin) =>
      join(walk(info) :: body.map(walk) ++ walkOpt(catchBlock) ++ walkOptList(fin): _*)
    case Debugger(info) =>
      walk(info)
  }

  def walk(node: Expr): Result = node match {
    case ExprList(info, exprs) =>
      join(walk(info) :: exprs.map(walk): _*)
    case Cond(info, cond, trueB, falseB) =>
      join(walk(info), walk(cond), walk(trueB), walk(falseB))
    case InfixOpApp(info, left, op, right) =>
      join(walk(info), walk(left), walk(op), walk(right))
    case PrefixOpApp(info, op, right) =>
      join(walk(info), walk(op), walk(right))
    case UnaryAssignOpApp(info, lhs, op) =>
      join(walk(info), walk(lhs), walk(op))
    case AssignOpApp(info, lhs, op, right) =>
      join(walk(info), walk(lhs), walk(op), walk(right))
    case l: LHS =>
      walk(l)
  }

  def walk(node: LHS): Result = node match {
    case This(info) =>
      walk(info)
    case Null(info) =>
      walk(info)
    case Bool(info, isBool) =>
      walk(info)
    case n: NumberLiteral =>
      walk(n)
    case StringLiteral(info, quote, escaped, isRE) =>
      walk(info)
    case RegularExpression(info, body, flag) =>
      walk(info)
    case VarRef(info, id) =>
      join(walk(info), walk(id))
    case ArrayExpr(info, elements) =>
      join(walk(info) :: elements.flatMap(walkOpt): _*)
    case ArrayNumberExpr(info, elements) =>
      walk(info)
    case ObjectExpr(info, members) =>
      join(walk(info) :: members.map(walk): _*)
    case Parenthesized(info, expr) =>
      join(walk(info), walk(expr))
    case FunExpr(info, ftn) =>
      join(walk(info), walk(ftn))
    case Bracket(info, obj, index) =>
      join(walk(info), walk(obj), walk(index))
    case Dot(info, obj, member) =>
      join(walk(info), walk(obj), walk(member))
    case New(info, lhs) =>
      join(walk(info), walk(lhs))
    case FunApp(info, fun, args) =>
      join(walk(info) :: walk(fun) :: args.map(walk): _*)
  }

  def walk(node: NumberLiteral): Result = node match {
    case DoubleLiteral(info, text, num) =>
      walk(info)
    case IntLiteral(info, intVal, radix) =>
      walk(info)
  }

  def walk(node: Stmts): Result = node match {
    case Stmts(info, body, isStrict) =>
      join(walk(info) :: body.map(walk): _*)
  }

  def walk(node: FunDecl): Result = node match {
    case FunDecl(info, ftn, isStrict) =>
      join(walk(info), walk(ftn))
  }

  def walk(node: VarDecl): Result = node match {
    case VarDecl(info, name, expr, isStrict) =>
      join(walk(info) :: walk(name) :: walkOpt(expr): _*)
  }

  def walk(node: Case): Result = node match {
    case Case(info, cond, body) =>
      join(walk(info) :: walk(cond) :: body.map(walk): _*)
  }

  def walk(node: Catch): Result = node match {
    case Catch(info, id, body) =>
      join(walk(info) :: walk(id) :: body.map(walk): _*)
  }

  def walk(node: Property): Result = node match {
    case PropId(info, id) =>
      join(walk(info), walk(id))
    case PropStr(info, str) =>
      walk(info)
    case PropNum(info, num) =>
      join(walk(info), walk(num))
  }

  def walk(node: Member): Result = node match {
    case Field(info, prop, expr) =>
      join(walk(info), walk(prop), walk(expr))
    case GetProp(info, prop, ftn) =>
      join(walk(info), walk(prop), walk(ftn))
    case SetProp(info, prop, ftn) =>
      join(walk(info), walk(prop), walk(ftn))
  }

  def walk(node: Id): Result = node match {
    case Id(info, text, uniqueName, isWith) =>
      walk(info)
  }

  def walk(node: Op): Result = node match {
    case Op(info, text) =>
      walk(info)
  }

  def walk(node: AnonymousFnName): Result = node match {
    case AnonymousFnName(info, text) =>
      walk(info)
  }

  def walk(node: Label): Result = node match {
    case Label(info, id) =>
      join(walk(info), walk(id))
  }

  def walk(node: Comment): Result = node match {
    case Comment(info, comment) =>
      walk(info)
  }

  def walk(node: TopLevel): Result = node match {
    case TopLevel(info, fds, vds, stmts) =>
      join(walk(info) :: fds.map(walk) ++ vds.map(walk) ++ stmts.map(walk): _*)
  }

  def walk(node: Functional): Result = node match {
    case Functional(info, fds, vds, stmts, name, params, body) =>
      join(walk(info) :: fds.map(walk) ++ vds.map(walk) ++ List(walk(stmts), walk(name)) ++ params.map(walk): _*)
  }
}
