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

package kr.ac.kaist.safe.concolic

import kr.ac.kaist.safe.interpreter.{ InterpreterPredefine => IP }
import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.errors.error.SafeError
import kr.ac.kaist.safe.nodes.{ NodeFactory => NF }
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.nodes.ir.{ IRFactory => IF }
import kr.ac.kaist.safe.util.{ NodeUtil => NU, NodeRelation => NR, Span }
import kr.ac.kaist.safe.util.useful.Lists

object IRGenerator {
  val errors: ExcLog = new ExcLog
  def signal(error: SafeError) = errors.signal(error)

  val dummySpan = NU.dummySpan("forConcolic")
  def freshId(ast: ASTNode): IRTmpId = freshId(ast, "temp")
  def freshId(ast: ASTNode, n: String): IRTmpId =
    IF.makeTId(ast, NU.freshName(n), false)
  // TODO MV No longer supported: can't create a TId from just a Span
  def freshId(span: Span, n: String): IRTmpId =
    IF.makeTId(NU.makeASTNodeInfo(span), NU.freshName(n))
  // TODO MV No longer supported: can't create a TId from just a Span
  def freshId(): IRTmpId = freshId(dummySpan, "temp")

  val globalName = NU.freshGlobalName("global")
  val global = IF.makeTId(NU.makeASTNodeInfo(NU.dummySpan("global")), globalName, true)
  var ignoreId = 0
  def varIgn(ast: ASTNode, span: Span) = {
    ignoreId += 1
    IF.makeTId(ast, NU.ignoreName + ignoreId)
  }
  def getSpan(n: ASTNode) = n.info.span
  def getSpan(n: ASTNodeInfo) = n.span

  def setUID[A <: ASTNode](n: A, uid: Long): A = { n.setUID(uid); n }

  type Env = List[(String, IRId)]

  val argName = "arguments"

  def isObject(ast: ASTNode, lhs: IRId, id: IRId) =
    IF.makeInternalCall(ast, lhs, IF.makeTId(ast, NU.freshGlobalName("isObject"), true), id)

  def toObject(ast: ASTNode, lhs: IRId, arg: IRExpr) =
    IF.makeInternalCall(ast, lhs, IF.makeTId(ast, NU.toObjectName, true), arg)

  def getBase(ast: ASTNode, lhs: IRId, f: IRId) =
    IF.makeInternalCall(ast, lhs, IF.makeTId(ast, NU.freshGlobalName("getBase"), true), f)

  def funexprId(span: Span, lhs: Option[String]) = {
    val uniq = lhs match {
      case None => NU.funexprName(span)
      case Some(name) => name + NU.funexprName(span)
    }
    NF.makeId(span, uniq, Some(uniq))
  }

  def containsUserId(e: IRExpr): Boolean = e match {
    case IRBin(_, first, _, second) => containsUserId(first) || containsUserId(second)
    case IRUn(_, _, expr) => containsUserId(expr)
    case IRLoad(_, _: IRUserId, _) => true
    case IRLoad(_, _, index) => containsUserId(index)
    case _: IRUserId => true
    case _ => false
  }

  def mkExprS(ast: ASTNode, id: IRId, e: IRExpr): IRExprStmt =
    mkExprS(ast, ast.info.span, id, e)
  def mkExprS(ast: ASTNode, span: Span, id: IRId, e: IRExpr) =
    if (containsUserId(e)) IF.makeExprStmt(ast, id, e, true)
    else IF.makeExprStmt(ast, id, e)

  def expr2ir(e: Expr, env: Env, res: IRId): (List[IRStmt], IRExpr) = e match {
    case n: Null => (List(), IF.makeNull(n))

    case b @ Bool(info, isBool) =>
      (List(), if (isBool) IF.makeBoolIR(true) else IF.makeBoolIR(false))

    case DoubleLiteral(info, text, num) =>
      (List(), IF.makeNumberIR(text, num))

    case IntLiteral(info, intVal, radix) =>
      (List(), IF.makeNumberIR(intVal.toString, intVal.doubleValue))

    case StringLiteral(info, _, str, _) =>
      (List(), IF.makeStringIR(NU.unescapeJava(str)))

    case ObjectExpr(info, members) =>
      val new_members = members.map(member2ir(_, env, freshId))
      val stmts = new_members.foldLeft(List[IRStmt]())((l, p) => l ++ p._1)
      (stmts :+ IF.makeObject(true, e, res, new_members.map(p => p._2)), res)
    case Parenthesized(_, expr) => expr2ir(expr, env, res)
    case n @ New(info, lhs) =>
      val span = getSpan(info)
      val objspan = getSpan(lhs)
      val fun = freshId(lhs, "fun")
      val fun1 = freshId(lhs, "fun1")
      val arg = freshId(e, argName)
      val obj = freshId(e, "obj")
      val newObj = freshId(e, "newObj")
      val cond = freshId(e, "cond")
      val proto = freshId(lhs, "proto")
      val (ftn, args) = lhs match {
        case FunApp(_, f, as) =>
          val newargs = as.map(a => freshId(a))
          val results = as.zipWithIndex.map(a => (
            newargs.apply(a._2),
            expr2ir(a._1, env, newargs.apply(a._2))
          ))
          (f, results.foldLeft(List[IRStmt]())((l, tp) => l ++ tp._2._1 :+ (mkExprS(e, tp._1, tp._2._2))) :+
            IF.makeArray(false, e, arg, newargs.map(p => Some(p))))
      }
      val (ssl, rl) = expr2ir(ftn, env, fun1)
      ((ssl :+ toObject(lhs, fun, rl)) ++ args ++
        List(
          IF.makeLoadStmt(false, e, proto, fun, IF.makeStringIR("prototype")),
          IF.makeObject(false, e, obj, Nil, Some(proto)),
          IF.makeNew(true, e, newObj, fun, List(obj, arg)),
          isObject(e, cond, newObj),
          IF.makeIf(false, e, cond, mkExprS(e, res, newObj), Some(mkExprS(e, res, obj)))
        ), res)

    case AssignOpApp(info, lhs, Op(_, text), right: FunExpr) if text.equals("=") && lhs.isName =>
      val name = NU.getName(lhs)
      val (ss, r) = funexpr2ir(right, env, res, Some(name), null)
      (lval2ir(e, lhs, env, ss, r)._1, r)

    case AssignOpApp(info, lhs, op, right) if op.text.equals("=") =>
      val span = getSpan(info)
      val (ss, r) = expr2ir(right, env, res)
      lhs match {
        case bracket: Bracket => lval2ir(e, lhs, env, ss, r)
        case dot @ Dot(info, obj, member) =>
          lval2ir(e, setUID(Bracket(info, obj, NF.makeStringLiteral(getSpan(member), member.text, "\"")), dot.getUID),
            env, ss, r)
        case vr: VarRef =>
          (lval2ir(e, lhs, env, ss, r)._1, r)
      }
    case VarRef(info, id) => (List(), IF.makeUId(id.text, id.uniqueName.get, true, id, false))
  }

  def lval2ir(ast: ASTNode, lhs: Expr, env: Env, stmts: List[IRStmt], e: IRExpr): (List[IRStmt], IRExpr) = lhs match {
    case Bracket(info, first, index) =>
      val span = getSpan(info)
      val firstspan = getSpan(first)
      val obj1 = freshId(first, "obj1")
      val field1 = freshId(index, "field1")
      val obj = freshId(first, "obj")
      val (ss1, r1) = expr2ir(first, env, obj1)
      val (ss2, r2) = expr2ir(index, env, field1)
      val front = (ss1 :+ toObject(first, obj, r1)) ++ ss2
      val back = stmts :+ IF.makeStore(true, ast, obj, r2, e)
      (front ++ back, IF.makeLoad(true, lhs, obj, r2))

    case VarRef(info, id) =>
      val irid = IF.makeUId(id.text, id.uniqueName.get, true, id, false)
      (stmts :+ mkExprS(ast, irid, e), irid)
  }

  def member2ir(m: Member, env: Env, res: IRId) = {
    val span = getSpan(m.info)
    m match {
      case Field(_, prop, expr) =>
        val (ss, r) = expr2ir(expr, env, res)
        (ss, IF.makeField(true, m, prop2ir(prop), r))
    }
  }

  def prop2ir(prop: Property) = prop match {
    case PropId(info, id) => IF.makeNGId(id.text, prop)
  }

  def funexpr2ir(e: Expr, env: Env, res: IRId, lhs: Option[String], target: String): (List[IRStmt], IRExpr) = e match {
    case FunExpr(info, f @ Functional(_, fds, vds, body, name, params, _)) =>
      if (name.text.equals("")) {
        // TODO MV basically inlined the call to dummyFtn here, and removed the matching
        //args, fds, vds = Nil
        // don't care about fromSource = false
        // name = IP.defId
        // params = List(IP.thisTId, IP.argumentsTId).asInstanceOf[List[IRId]])
        // body = toJavaList(for (i <- 1 to length) yield IF.dummyIRStmt(IF.dummyAst, IP.defSpan).asInstanceOf[IRStmt])
        // length in definition of body is 0, so expression yields an empty 'list' -> body = Nil
        val args: List[IRStmt] = Nil
        val fds: List[IRFunDecl] = Nil
        val vds: List[IRVarStmt] = Nil
        val name = IP.defId
        val params: List[IRId] = List(IP.thisTId, IP.argumentsTId)
        val body: List[IRStmt] = Nil
        return (List(IF.makeFunExpr(true, f, res, name, params, args, fds, vds, body)), res)
      } else {
        for (k <- NF.irSet) {
          k match {
            case IRFunctional(info, _, name, params, args, fds, vds, body) =>
              if (target == name.uniqueName)
                return (List(IF.makeFunExpr(true, f, res, name, params, args, fds, vds, body)), res)
            case _ =>
          }
        }

        return (List(IF.dummyIRStmt(e)), res)
      }
  }

  def funapp2ir(e: FunApp, env: Env, res: IRId, target: String): IRStmt = e match {
    case FunApp(info, v @ VarRef(_, fid), args) =>
      val fspan = getSpan(v)
      val obj = freshId(v, "obj")
      val arg = freshId(e, argName)
      val fun = freshId(fid, "fun")
      // TODO: A name of function can be locally declared?
      val fir = IF.makeUId(fid.text, fid.uniqueName.get, true, fid, false)
      val newargs = args.map(_ => freshId)
      val results = args.zipWithIndex.map(a => (
        newargs.apply(a._2),
        expr2ir(a._1, env, newargs.apply(a._2))
      ))
      new IRStmtUnit(NF.makeDummyAST("forConcolic"), List(toObject(v, obj, fir)) ++
        results.foldLeft(List[IRStmt]())((l, tp) => l ++ tp._2._1 :+ (mkExprS(e, tp._1, tp._2._2))) ++
        List(
          IF.makeArgs(e, arg, newargs.map(p => Some(p))),
          getBase(v, fun, fir),
          IF.makeCall(true, e, res, obj, fun, arg)
        ))

    case FunApp(info, dot @ Dot(i, obj, member), args) =>
      funapp2ir(setUID(FunApp(info, setUID(
        Bracket(i, obj, NF.makeStringLiteral(getSpan(member), member.text, "\"")),
        dot.getUID
      ), args), e.getUID), env, res, target)

    case FunApp(info, b @ Bracket(_, first, index), args) =>
      val firstspan = getSpan(first)
      val obj1 = freshId(first, "obj1")
      val field1 = freshId(index, "field1")
      val obj = freshId(e, "obj")
      val fun = freshId(e, "fun")
      val arg = freshId(e, argName)
      val (ssl, rl) = expr2ir(first, env, obj1)
      val (ssr, rr) = expr2ir(index, env, field1)
      val newargs = args.map(_ => freshId)
      val results = args.zipWithIndex.map(a => (
        newargs.apply(a._2),
        expr2ir(a._1, env, newargs.apply(a._2))
      ))
      new IRStmtUnit(NF.makeDummyAST("forConcolic"), ((ssl :+ toObject(first, obj, rl)) ++ ssr) ++
        results.foldLeft(List[IRStmt]())((l, tp) => l ++ tp._2._1 :+ (mkExprS(e, tp._1, tp._2._2))) ++
        List(
          IF.makeArgs(e, arg, newargs.map(p => Some(p))),
          toObject(b, fun, IF.makeLoad(true, e, obj, rr)),
          IF.makeCall(true, e, res, fun, obj, arg)
        ))

    case FunApp(info, fun, args) =>
      val fspan = getSpan(fun)
      val obj1 = freshId(fun, "obj1")
      val obj = freshId(fun, "obj")
      val arg = freshId(e, argName)
      val (ss, r) = funexpr2ir(fun, env, obj1, None, target)
      val newargs = args.map(_ => freshId)
      val results = args.zipWithIndex.map(a => (
        newargs.apply(a._2),
        expr2ir(a._1, env, newargs.apply(a._2))
      ))

      new IRStmtUnit(NF.makeDummyAST("forConcolic"), (ss :+ toObject(fun, obj, r)) ++
        results.foldLeft(List[IRStmt]())((l, tp) => l ++ tp._2._1 :+ (mkExprS(e, tp._1, tp._2._2))) ++
        List(
          IF.makeArgs(e, arg, newargs.map(p => Some(p))),
          IF.makeCall(true, e, res, obj, global, arg)
        ))
  }

  def additional2ir(stmt: Stmt, env: Env): IRStmt = stmt match {
    case ExprStmt(info, expr @ AssignOpApp(_, _, op, _), isInternal) if op.text.equals("=") =>
      val (ss, _) = expr2ir(expr, env, varIgn(expr, expr.info.span))
      IF.makeStmtUnit(stmt, ss)
  }
}
