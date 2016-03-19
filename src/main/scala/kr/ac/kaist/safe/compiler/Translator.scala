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

package kr.ac.kaist.safe.compiler

import kr.ac.kaist.safe.errors.ErrorLog
import kr.ac.kaist.safe.errors.SAFEError.error
import kr.ac.kaist.safe.errors.StaticError
import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.util.{ NodeFactory => NF, NodeUtil => NU, IRFactory => IF, Span }

/* Translates JavaScript AST to IR. */
class Translator(program: Program) extends ASTWalker {
  val debug = false
  var isLocal = false
  var locals: List[String] = List()
  val plus = IF.makeOp("+")
  val minus = IF.makeOp("-")
  val typeof = IF.makeOp("typeof")
  val equals = IF.makeOp("==")
  val stricteq = IF.makeOp("===")

  /* Error handling
   * The signal function collects errors during the AST->IR translation.
   * To collect multiple errors,
   * we should return a dummy value after signaling an error.
   */
  val errors: ErrorLog = new ErrorLog
  def signal(msg: String, node: Node): Unit = errors.signal(msg, node)
  def signal(node: Node, msg: String): Unit = errors.signal(msg, node)
  def signal(error: StaticError): Unit = errors.signal(error)
  def getErrors: List[StaticError] = errors.errors

  def makeStmtUnit(ast: ASTNode, span: Span): IRStmtUnit =
    IF.makeStmtUnit(ast, span)

  def makeStmtUnit(ast: ASTNode, span: Span, stmt: IRStmt): IRStmtUnit =
    IF.makeStmtUnit(ast, span, stmt)

  def makeStmtUnit(ast: ASTNode, span: Span, first: IRStmt, second: IRStmt): IRStmtUnit =
    IF.makeStmtUnit(ast, span, first, second)

  def makeStmtUnit(ast: ASTNode, span: Span, stmts: List[IRStmt]): IRStmtUnit =
    IF.makeStmtUnit(ast, span, stmts)

  val dummySpan = IF.dummySpan("temp")
  def freshId(ast: ASTNode, span: Span): IRTmpId = freshId(ast, span, "temp")
  def freshId(ast: ASTNode, span: Span, n: String): IRTmpId =
    IF.makeTId(ast, span, NU.freshName(n), false)
  def freshId(span: Span, n: String): IRTmpId =
    IF.makeTId(span, NU.freshName(n))
  def freshId(span: Span): IRTmpId = freshId(span, "temp")
  def freshId: IRTmpId = freshId(dummySpan, "temp")

  val referenceErrorName = NU.freshGlobalName("referenceError")
  val referenceError = IF.makeTId(IF.dummySpan("referenceError"), referenceErrorName, true)
  val globalName = NU.freshGlobalName("global")
  val global = IF.makeTId(IF.dummySpan("global"), globalName, true)
  val globalSpan = IF.dummySpan(globalName)
  var ignoreId = 0
  def varIgn(ast: ASTNode, span: Span): IRTmpId = {
    ignoreId += 1
    IF.makeTId(ast, span, NU.ignoreName + ignoreId)
  }
  def getSpan(n: IRNode): Span = n.info.span
  def getSpan(n: IRNodeInfo): Span = n.span
  def getSpan(n: ASTNode): Span = n match {
    case Parenthesized(_, e) => getSpan(e)
    case _ => n.info.span
  }
  def getSpan(n: ASTNodeInfo): Span = n.span

  def setUID[A <: ASTNode](n: A, uid: Long): A = { n.setUID(uid); n }

  /* Environment for renaming fresh labels and variables
   * created during the AST->IR translation.
   * Only the following identifiers are bound in the environment:
   *     arguments, val, break, testing, and continue.
   */
  type Env = List[(String, IRId)]
  def addE(env: Env, x: String, xid: IRId): Env = (x, xid) :: env
  def getE(env: Env, name: String): IRId = env.find(p => p._1.equals(name)) match {
    case None =>
      val id = IF.dummyIRId(name)
      signal("Identifier " + name + " is not bound.", id)
      id
    case Some((_, id)) => id
  }

  val thisName = "this"
  val argName = "arguments"
  val valName = "val"
  val newName = "new"
  val oldName = "old"
  val condName = "cond"
  val breakName = "break"
  val switchName = "switch"
  val testingName = "testing"
  val continueName = "continue"

  def funexprId(span: Span, lhs: Option[String]): Id = {
    val uniq = lhs match {
      case None => NU.funexprName(span)
      case Some(name) => name + NU.funexprName(span)
    }
    NF.makeId(span, uniq, Some(uniq))
  }

  // Whether a given name is locally declared
  def isLocal(n: String): Boolean = locals.contains(n)

  // Getter and setter names to IRId, which do not check for "arguments"
  def mid2ir(env: Env, id: Id): IRId = id.uniqueName match {
    case None =>
      signal("Identifiers should have a unique name after the disambiguation phase:" + id.text, id)
      IF.dummyIRId(id)
    case Some(n) =>
      IF.makeUId(id.text, n, !isLocal(n), id, getSpan(id), false)
  }

  // When we don't know whether a give id is a local variable or not
  def id2ir(env: Env, id: Id): IRId = id.uniqueName match {
    case None =>
      signal("Identifiers should have a unique name after the disambiguation phase:" + id.text, id)
      IF.dummyIRId(id)
    case Some(n) if id.text.equals(argName) && isLocal =>
      if (debug) println("before getE:id2ir-" + id.text + " " + id.uniqueName)
      env.find(p => p._1.equals(argName)) match {
        case None => IF.makeUId(argName, argName, isLocal, id, getSpan(id), false)
        case Some((_, id)) => id
      }
    case Some(n) if id.isWith =>
      IF.makeWId(id.text, n, !isLocal(n), id, getSpan(id))
    case Some(n) if NU.isInternal(id.text) =>
      IF.makeTId(id, getSpan(id), id.text, n, false)
    case Some(n) =>
      IF.makeUId(id.text, n, !isLocal(n), id, getSpan(id), false)
  }
  def label2ir(label: Label): IRId = {
    val id = label.id
    id.uniqueName match {
      case None =>
        signal("Labels should have a unique name after the disambiguation phase:" + id.text, label)
        IF.dummyIRId(label)
      case Some(n) => IF.makeUId(id.text, n, false, label, getSpan(id), false)
    }
  }

  def functional(name: Id, params: List[Id], fds: List[FunDecl],
    vds: List[VarDecl], body: SourceElements, env: Env,
    fe: Option[IRId], isMember: Boolean): (IRId, List[IRId], List[IRStmt], List[IRFunDecl], List[IRVarStmt], List[IRStmt]) = {
    val oldIsLocal = isLocal
    val oldLocals = locals
    locals = oldLocals ++ (fe match { case Some(n) => List(n.uniqueName) case None => Nil }) ++
      params.map(_.uniqueName.get) ++
      fds.map(_.ftn.name.uniqueName.get) ++
      vds.map(_.name.uniqueName.get)
    isLocal = true
    val paramsspan = NU.spanAll(params, getSpan(name))
    var new_arg = freshId(name, paramsspan, argName)
    if (debug) println(" arg=" + new_arg.uniqueName)
    var new_env = addE(env, argName, new_arg)
    if (debug) {
      println("params.. ")
      params.foreach(p => print(" " + p.text))
    }
    if (params.find(_.text.equals(argName)).isDefined) {
      new_arg = freshId(name, paramsspan, argName)
      if (debug) println(" arg=" + new_arg.uniqueName)
    }
    val fd_names = fds.map(_.ftn.name.text)
    // nested functions shadow parameters with the same names
    val params_vds = params.filterNot(p => fd_names contains p.text).
      map(p => IF.makeVarStmt(false, p, getSpan(p), id2ir(new_env, p), true))
    // x_i = arguments["i"]
    val new_params = params.zipWithIndex.map(p => IF.makeLoadStmt(false, name, getSpan(p._1),
      id2ir(new_env, p._1),
      new_arg,
      IF.makeString(p._2.toString, p._1)))
    val new_fds = fds.map(walkFd(_, new_env))
    new_env = new_fds.foldLeft(new_env)((e, fd) => addE(e, fd.ftn.name.uniqueName, fd.ftn.name))
    val new_vds = vds.filterNot(_.name.text.equals(argName)).map(walkVd(_, new_env))
    new_env = new_vds.foldLeft(new_env)((e, vd) => addE(e, vd.lhs.uniqueName, vd.lhs))
    val new_name = fe match { case Some(n) => n case None if isMember => mid2ir(env, name) case None => id2ir(env, name) }
    val new_body = body.body.map(s => walkStmt(s.asInstanceOf[Stmt], new_env))
    isLocal = oldIsLocal
    locals = oldLocals
    (new_name, List(IF.makeTId(name, paramsspan, thisName), new_arg),
      // nested functions shadow parameters with the same names
      new_params, /*filterNot (p => fd_names contains p.lhs.originalName),*/
      new_fds, params_vds ++ new_vds, new_body)
  }

  def containsUserId(e: IRExpr): Boolean = e match {
    case IRBin(_, first, _, second) => containsUserId(first) || containsUserId(second)
    case IRUn(_, _, expr) => containsUserId(expr)
    case IRLoad(_, _: IRUserId, _) => true
    case IRLoad(_, _, index) => containsUserId(index)
    case _: IRUserId => true
    case _ => false
  }

  def isIgnore(id: IRId): Boolean = id.uniqueName.startsWith(NU.ignoreName)
  def mkExprS(ast: ASTNode, id: IRId, e: IRExpr): IRExprStmt = id match {
    case _: IRUserId => mkExprS(ast, getSpan(ast.info), id, e)
    case _ => mkExprS(ast, getSpan(e.info), id, e)
  }
  def mkExprS(ast: ASTNode, span: Span, id: IRId, e: IRExpr): IRExprStmt =
    if (containsUserId(e)) IF.makeExprStmt(ast, span, id, e, true)
    else IF.makeExprStmt(ast, span, id, e)

  def makeListIgnore(ast: ASTNode, ss: List[IRStmt], expr: IRExpr): List[IRStmt] = expr match {
    case id: IRId if id.uniqueName.startsWith(NU.ignoreName) => ss
    case _ => ss :+ IF.makeExprStmtIgnore(ast, ast.info.span, varIgn(ast, ast.info.span), expr)
  }

  def makeList(ast: ASTNode, ss: List[IRStmt], expr: IRExpr, id: IRId): List[IRStmt] = expr match {
    case irid: IRId if irid.uniqueName.equals(id.uniqueName) => ss
    case _ => ss :+ mkExprS(ast, id, expr)
  }

  def makeSeq(ast: ASTNode, info: ASTNodeInfo, ss: List[IRStmt], expr: IRExpr, id: IRId): IRSeq = expr match {
    case irid: IRId if irid.uniqueName.equals(id.uniqueName) => IF.makeSeq(ast, getSpan(info), ss)
    case _ => IF.makeSeq(ast, getSpan(info), ss :+ mkExprS(ast, id, expr))
  }

  def toObject(ast: ASTNode, span: Span, lhs: IRId, arg: IRExpr): IRInternalCall =
    IF.makeInternalCall(ast, span, lhs, IF.makeTId(ast, span, NU.toObjectName, true), arg)

  def toNumber(ast: ASTNode, span: Span, lhs: IRId, id: IRId): IRInternalCall =
    IF.makeInternalCall(ast, span, lhs, IF.makeTId(ast, span, NU.freshGlobalName("toNumber"), true), id)

  def getBase(ast: ASTNode, span: Span, lhs: IRId, f: IRId): IRInternalCall =
    IF.makeInternalCall(ast, span, lhs, IF.makeTId(ast, span, NU.freshGlobalName("getBase"), true), f)

  def iteratorInit(ast: ASTNode, span: Span, iterator: IRId, obj: IRId): IRInternalCall =
    IF.makeInternalCall(ast, span, iterator,
      IF.makeTId(ast, span, NU.freshGlobalName("iteratorInit"), true), obj)
  def iteratorHasNext(ast: ASTNode, span: Span, cond: IRId, obj: IRId, iterator: IRId): IRInternalCall =
    IF.makeInternalCall(ast, span, cond,
      IF.makeTId(ast, span, NU.freshGlobalName("iteratorHasNext"), true),
      obj, iterator)
  def iteratorKey(ast: ASTNode, span: Span, key: IRId, obj: IRId, iterator: IRId): IRInternalCall =
    IF.makeInternalCall(ast, span, key,
      IF.makeTId(ast, span, NU.freshGlobalName("iteratorNext"), true),
      obj, iterator)

  def isObject(ast: ASTNode, span: Span, lhs: IRId, id: IRId): IRInternalCall =
    IF.makeInternalCall(ast, span, lhs, IF.makeTId(ast, span, NU.freshGlobalName("isObject"), true), id)

  def isPrint(n: Expr): Boolean = n match {
    case VarRef(info, Id(_, id, _, _)) => id.equals(NU.internalPrint)
    case _ => false
  }
  def isPrintIS(n: Expr): Boolean = n match {
    case VarRef(info, Id(_, id, _, _)) => id.equals(NU.internalPrintIS)
    case _ => false
  }
  def isGetTickCount(n: Expr): Boolean = n match {
    case VarRef(info, Id(_, id, _, _)) => id.equals(NU.internalGetTickCount)
    case _ => false
  }
  def isToObject(n: Expr): Boolean = n match {
    case VarRef(info, Id(_, id, _, _)) => id.equals(NU.toObjectName)
    case _ => false
  }

  def allAnds(expr: Expr): Boolean = expr match {
    case Parenthesized(_, e) => allAnds(e)
    case InfixOpApp(_, l, op, r) => op.text.equals("&&") && allAnds(l) && allAnds(r)
    case _: Expr => true
    case _ => false
  }

  def getArgs(expr: Expr): List[Expr] = expr match {
    case Parenthesized(_, e) => getArgs(e)
    case InfixOpApp(_, l, _, r) => getArgs(l) ++ getArgs(r)
    case _: Expr => List(expr)
    case _ => Nil
  }

  def getAndArgs(expr: Expr): List[Expr] = expr match {
    case Parenthesized(_, e) => getAndArgs(e)
    case InfixOpApp(_, l, op, r) if op.text.equals("&&") => getAndArgs(l) ++ getAndArgs(r)
    case _: Expr => List(expr)
    case _ => Nil
  }

  def containsLhs(res: IRExpr, lhs: Expr, env: Env): Boolean = {
    def getLhs(l: Expr): Option[Expr] = l match {
      case Parenthesized(_, expr) => getLhs(expr)
      case vr: VarRef => Some(vr)
      case dot @ Dot(info, obj, member) =>
        getLhs(setUID(Bracket(info, obj, NF.makeStringLiteral(getSpan(member), member.text, "\"")), dot.getUID))
      case br: Bracket => Some(br)
      case _ => None
    }
    getLhs(lhs) match {
      case Some(VarRef(_, id)) =>
        val irid = id2ir(env, id)
        res match {
          case b: IRBin => containsLhs(b.first, lhs, env) || containsLhs(b.second, lhs, env)
          case u: IRUn => containsLhs(u.expr, lhs, env)
          case l: IRLoad => l.obj.uniqueName.equals(irid.uniqueName) || containsLhs(l.index, lhs, env)
          case id: IRId => id.uniqueName.equals(irid.uniqueName)
          case _ => false
        }
      case Some(_: Bracket) => true
      case _ => false
    }
  }

  /* The main entry function */
  def doit: IRRoot = NU.simplifyIRWalker.walk(walkProgram(program)).asInstanceOf[IRRoot]

  /*
   * AST2IR_P : Program -> IRRoot
   */
  def walkProgram(pgm: Program): IRRoot = pgm match {
    case Program(info, TopLevel(_, fds, vds, sts)) =>
      val env = List()
      IF.makeRoot(true, pgm, getSpan(info), fds.map(walkFd(_, env)), vds.map(walkVd(_, env)),
        NU.toStmts(sts).map(s => walkStmt(s, env)))
  }

  /*
   * AST2IR_FD : FunDecl -> Env -> IRFunDecl
   */
  def walkFd(fd: FunDecl, env: Env): IRFunDecl = fd match {
    case FunDecl(info, Functional(_, fds, vds, body, name, params, _), _) =>
      val (new_name, new_params, args, new_fds, new_vds, new_body) =
        functional(name, params, fds, vds, body, env, None, false)
      IF.makeFunDecl(true, fd, getSpan(info), new_name, new_params, args,
        new_fds, new_vds, new_body)
  }

  /*
   * AST2IR_VD : VarDecl -> Env -> IRVarStmt
   */
  def walkVd(vd: VarDecl, env: Env): IRVarStmt = vd match {
    case VarDecl(info, name, expr, _) =>
      expr match {
        case None =>
        case _ =>
          signal("Variable declarations should not have any initialization expressions after the disambiguation phase.", vd)
      }
      IF.makeVarStmt(true, vd, getSpan(info), id2ir(env, name), false)
  }

  var isDoWhile = false

  /*
   * AST2IR_S : Stmt -> Env -> IRStmt
   */
  def walkStmt(s: Stmt, env: Env): IRStmt = s match {
    case ABlock(info, stmts, true) =>
      IF.makeSeq(s, info.span, stmts.map(walkStmt(_, env)))

    case ABlock(info, stmts, false) =>
      makeStmtUnit(s, info.span, stmts.map(walkStmt(_, env)))

    case StmtUnit(info, stmts) =>
      IF.makeSeq(s, info.span, stmts.map(walkStmt(_, env)))

    case EmptyStmt(info) => makeStmtUnit(s, info.span)

    case ExprStmt(_, expr @ AssignOpApp(_, _, op, right), isInternal) =>
      val (ss, _) = walkExpr(expr, env, varIgn(right, getSpan(right)))
      // val ss1 = NU.filterIgnore(ss)
      if (isInternal) IF.makeSeq(expr, getSpan(expr), ss)
      else makeStmtUnit(expr, getSpan(expr), ss)

    case ExprStmt(_, expr, isInternal) =>
      val (ss, r) = walkExpr(expr, env, varIgn(expr, getSpan(expr)))
      if (isInternal) IF.makeSeq(expr, getSpan(expr), makeListIgnore(expr, ss, r))
      else makeStmtUnit(expr, getSpan(expr), makeListIgnore(expr, ss, r))

    case If(info, InfixOpApp(_, left, op, right), trueB, falseB) if op.text.equals("&&") && allAnds(left) =>
      val span = getSpan(info)
      val args = getArgs(left) :+ right
      val news = args.zipWithIndex.map(a => freshId(a._1, getSpan(a._1), "new" + a._2))
      // list of (ss_i, r_i)
      val ress = args.zip(news).map(p => walkExpr(p._1, env, p._2))
      val lab = freshId(s, span, "label")
      val trueS = IF.makeSeq(trueB, getSpan(trueB), walkStmt(trueB, env),
        IF.makeBreak(false, s, span, lab))

      val ifStmt = args.zip(ress).foldRight((trueS, Nil): (IRStmt, List[IRStmt]))((p, r) => {
        if (r._2.isEmpty)
          (IF.makeIf(true, p._1, getSpan(p._1), p._2._2, r._1, None), p._2._1)
        else
          (
            IF.makeIf(true, p._1, getSpan(p._1), p._2._2,
              IF.makeSeq(left, getSpan(left), r._2 :+ r._1), None),
              p._2._1
          )
      })._1
      val body = falseB match {
        case None => ifStmt
        case Some(stmt) => IF.makeSeq(s, span, List(ifStmt, walkStmt(stmt, env)))
      }
      makeStmtUnit(s, span,
        IF.makeSeq(s, span,
          ress.head._1 :+ IF.makeLabelStmt(false, s, span, lab, body)))

    case If(info, InfixOpApp(_, left, op, right), trueB, falseB) if op.text.equals("||") =>
      val span = getSpan(info)
      val leftspan = getSpan(left)
      val new1 = freshId(left, leftspan, "new1")
      val (ss1, r1) = walkExpr(left, env, new1)
      val (ss2, r2) = walkExpr(right, env, freshId(right, getSpan(right), "new2"))
      val lab1 = freshId(s, span, "label1")
      val lab2 = freshId(s, span, "label2")
      val ifStmts = ((IF.makeIf(true, s, span, r1,
        IF.makeBreak(false, s, span, lab1), None)) :: ss2) :+
        IF.makeIf(true, s, span, r2, IF.makeBreak(false, s, span, lab1), None)
      val body1 = falseB match {
        case None => IF.makeSeq(s, span, ifStmts :+ IF.makeBreak(false, s, span, lab2))
        case Some(stmt) =>
          IF.makeSeq(s, span, ifStmts ++ List(
            walkStmt(stmt, env),
            IF.makeBreak(false, s, span, lab2)
          ))
      }
      val body2 = IF.makeSeq(s, span, IF.makeLabelStmt(false, s, span, lab1, body1), walkStmt(trueB, env))
      makeStmtUnit(s, span, IF.makeSeq(s, span, ss1 :+ IF.makeLabelStmt(false, s, span, lab2, body2)))

    case If(info, Parenthesized(_, expr), trueBranch, falseBranch) =>
      walkStmt(setUID(If(info, expr, trueBranch, falseBranch), s.getUID), env)

    case If(info, cond, trueBranch, falseBranch) =>
      val span = getSpan(info)
      val (ss, r) = walkExpr(cond, env, freshId(cond, getSpan(cond), newName))
      makeStmtUnit(s, info.span,
        ss :+ IF.makeIf(true, s, span, r, walkStmt(trueBranch, env),
          falseBranch match {
            case None => None
            case Some(stmt) => Some(walkStmt(stmt, env))
          }))

    case Switch(info, cond, frontCases, defCase, backCases) =>
      val span = getSpan(info)
      val condVal = freshId(cond, getSpan(cond), valName)
      val breakLabel = freshId(s, span, switchName)
      val (ss, r) = walkExpr(cond, env, condVal)
      val switchS =
        IF.makeLabelStmt(false, s, span, breakLabel,
          IF.makeSeq(s, span,
            makeSeq(s, info, ss, r, condVal),
            walkCase(s, span, backCases.reverse,
              defCase, frontCases.reverse,
              addE(
                addE(env, breakName, breakLabel),
                valName, condVal
              ), List())))
      makeStmtUnit(s, span, switchS)

    case DoWhile(info, body, cond) =>
      val span = getSpan(info)
      val newone = freshId(cond, getSpan(cond), "new1")
      val labelName = freshId(s, span, breakName)
      val cont = freshId(s, span, continueName)
      val new_env = addE(addE(env, breakName, labelName), continueName, cont)
      val (ss, r) = walkExpr(cond, env, newone)
      isDoWhile = true
      val new_body = IF.makeSeq(s, span,
        List(
          IF.makeLabelStmt(false, s, span, cont, walkStmt(body, new_env)),
          IF.makeSeq(s, span, ss)
        ))
      isDoWhile = false
      val stmt = IF.makeSeq(s, span, List(new_body, IF.makeWhile(true, s, span, r, new_body)))
      makeStmtUnit(s, span, IF.makeLabelStmt(false, s, span, labelName, stmt))

    case While(info, cond, body) =>
      val span = getSpan(info)
      val newone = freshId(cond, getSpan(cond), "new1")
      val labelName = freshId(s, span, breakName)
      val cont = freshId(s, span, continueName)
      val new_env = addE(addE(env, breakName, labelName), continueName, cont)
      val (ss, r) = walkExpr(cond, env, newone)
      val ssList = List(IF.makeSeq(s, span, ss))
      val new_body = IF.makeSeq(s, span,
        List(IF.makeLabelStmt(false, s, span, cont, walkStmt(body, new_env))) ++ ssList)
      val stmt = IF.makeSeq(s, span, ssList :+ IF.makeWhile(true, s, span, r, new_body))
      makeStmtUnit(s, span, IF.makeLabelStmt(false, s, span, labelName, stmt))

    case For(info, init, cond, action, body) =>
      val span = getSpan(info)
      val labelName = freshId(s, span, breakName)
      val cont = freshId(s, span, continueName)
      val new_env = addE(addE(env, breakName, labelName), continueName, cont)
      val front = init match {
        case None => List()
        case Some(iexpr) =>
          val (ss1, r1) = walkExpr(iexpr, env, varIgn(iexpr, getSpan(iexpr)))
          makeList(s, ss1, r1, varIgn(s, info.span))
      }
      val back = action match {
        case None => List()
        case Some(aexpr) =>
          val (ss3, r3) = walkExpr(aexpr, env, varIgn(aexpr, getSpan(aexpr)))
          makeList(s, ss3, r3, varIgn(s, info.span))
      }
      val bodyspan = getSpan(body)
      val nbody = IF.makeLabelStmt(false, s, bodyspan, cont, walkStmt(body, new_env))
      val stmt = cond match {
        case None =>
          IF.makeSeq(s, span, List(
            IF.makeSeq(s, span, front),
            IF.makeWhile(true, s, bodyspan, IF.trueV,
              IF.makeSeq(s, bodyspan,
                List(nbody, IF.makeSeq(s, bodyspan, back))))
          ))
        case Some(cexpr) =>
          val newtwo = freshId(cexpr, getSpan(cexpr), "new2")
          val (ss2, r2) = walkExpr(cexpr, env, newtwo)
          val new_body = List(nbody, IF.makeSeq(s, bodyspan, back ++ ss2))
          IF.makeSeq(s, span, List(
            IF.makeSeq(s, span, front ++ ss2),
            IF.makeWhile(true, s, span, r2, IF.makeSeq(s, span, new_body))
          ))
      }
      makeStmtUnit(s, span, IF.makeLabelStmt(false, s, span, labelName, stmt))

    case ForIn(info, lhs, expr, body) =>
      val span = getSpan(info)
      val labelName = freshId(s, span, breakName)
      val objspan = getSpan(expr)
      val newone = freshId(expr, objspan, "new1")
      val obj = freshId(expr, objspan, "obj")
      val iterator = freshId(expr, objspan, "iterator")
      val condone = freshId(expr, objspan, "cond1")
      val key = freshId(expr, objspan, "key")
      val cont = freshId(expr, objspan, continueName)
      val new_env = addE(addE(env, breakName, labelName), continueName, cont)
      val iteratorCheck = iteratorHasNext(s, span, condone, obj, iterator)
      val (ss, r) = walkExpr(expr, env, newone)
      val bodyspan = getSpan(body)
      val new_body = IF.makeSeq(s, bodyspan,
        List(iteratorKey(s, bodyspan, key, obj, iterator)) ++
          walkLval(lhs, lhs, addE(env, oldName, freshId(lhs, getSpan(lhs), oldName)),
            List(), key, false)._1 ++
            List(
              IF.makeLabelStmt(false, s, bodyspan, cont, walkStmt(body, new_env)),
              IF.makeSeq(s, bodyspan, iteratorCheck)
            ))
      val stmt = IF.makeSeq(s, span,
        List(
          IF.makeSeq(s, span, ss ++ List(
            mkExprS(expr, obj, r),
            iteratorInit(s, span, iterator, obj),
            iteratorCheck
          )),
          IF.makeWhile(true, s, bodyspan, condone, new_body)
        ))
      makeStmtUnit(s, span, IF.makeLabelStmt(false, s, span, labelName, stmt))

    case _: ForVar =>
      signal("ForVar should be replaced by Hoister.", s)
      IF.dummyIRStmt(s, getSpan(s))

    case _: ForVarIn =>
      signal("ForVarIn should be replaced by Hoister.", s)
      IF.dummyIRStmt(s, getSpan(s))

    case Continue(info, target) =>
      val span = getSpan(info)
      target match {
        case None =>
          makeStmtUnit(s, span, IF.makeBreak(true, s, span, getE(env, continueName)))
        case Some(x) =>
          makeStmtUnit(s, span, IF.makeBreak(true, s, span, label2ir(x)))
      }

    case Break(info, target) =>
      val span = getSpan(info)
      target match {
        case None =>
          makeStmtUnit(s, span, IF.makeBreak(true, s, span, getE(env, breakName)))
        case Some(tg) =>
          makeStmtUnit(s, span, IF.makeBreak(true, s, span, label2ir(tg)))
      }

    case r @ Return(info, expr) =>
      val span = getSpan(info)
      expr match {
        case None =>
          makeStmtUnit(s, span, IF.makeReturn(true, s, span, None))
        case Some(expr) =>
          val new1 = freshId(expr, getSpan(expr), "new1")
          val (ss, r) = walkExpr(expr, env, new1)
          makeStmtUnit(s, span, ss :+ IF.makeReturn(true, s, span, Some(r)))
      }

    case With(info, expr, stmt) =>
      val span = getSpan(info)
      val objspan = getSpan(expr)
      val new1 = freshId(expr, objspan, "new1")
      val new2 = freshId(expr, objspan, "new2")
      val (ss, r) = walkExpr(expr, env, new1)
      makeStmtUnit(s, span,
        ss ++ List(
          toObject(expr, objspan, new2, r),
          IF.makeWith(true, s, span, new2, walkStmt(stmt, env))
        ))

    case LabelStmt(info, label, stmt) =>
      val span = getSpan(info)
      makeStmtUnit(s, span, IF.makeLabelStmt(true, s, span, label2ir(label), walkStmt(stmt, env)))

    case Throw(info, expr) =>
      val span = getSpan(info)
      val new1 = freshId(expr, getSpan(expr), "new1")
      val (ss, r) = walkExpr(expr, env, new1)
      makeStmtUnit(s, span, ss :+ IF.makeThrow(true, s, span, r))

    case st @ Try(info, body, catchBlock, fin) =>
      val span = getSpan(info)
      val (id, catchBody) = catchBlock match {
        case Some(Catch(_, x @ Id(i, text, Some(name), _), s)) =>
          locals = name +: locals
          val result = (
            Some(IF.makeUId(text, name, false, st, getSpan(i), false)),
            Some(makeStmtUnit(st, span, s.map(walkStmt(_, env))))
          )
          locals = locals.tail
          result
        case _ => (None, None)
      }
      makeStmtUnit(s, span,
        IF.makeTry(true, s, span,
          makeStmtUnit(st, span, body.map(walkStmt(_, env))),
          id, catchBody,
          fin match {
            case None => None
            case Some(s) =>
              Some(makeStmtUnit(st, span, s.map(walkStmt(_, env))))
          }))

    case Debugger(info) => IF.makeStmtUnit(s, getSpan(info))

    case _: VarStmt =>
      signal("VarStmt should be replaced by the hoister.", s)
      IF.dummyIRStmt(s, getSpan(s))

    case NoOp(info, desc) =>
      IF.makeNoOp(s, getSpan(info), desc)
  }

  def walkFunExpr(e: Expr, env: Env, res: IRId, lhs: Option[String]): (List[IRFunExpr], IRId) = e match {
    case FunExpr(info, Functional(_, fds, vds, body, name, params, _)) =>
      val span = getSpan(info)
      val id = if (name.text.equals("")) funexprId(span, lhs) else name
      val new_name = IF.makeUId(id.text, id.uniqueName.get, false,
        e, getSpan(id.info), false)
      val (_, new_params, args, new_fds, new_vds, new_body) =
        functional(name, params, fds, vds, body, env, Some(new_name), false)
      (List(IF.makeFunExpr(true, e, span, res, new_name, new_params, args,
        new_fds, new_vds, new_body)), res)
  }

  /*
   * AST2IR_E : Expr -> Env -> IRId -> List[IRStmt] * IRExpr
   */
  def walkExpr(e: Expr, env: Env, res: IRId): (List[IRStmt], IRExpr) = e match {
    case ExprList(info, Nil) =>
      (Nil, IF.makeUndef(IF.dummyAst))

    case ExprList(info, exprs) =>
      val stmts = exprs.dropRight(1).foldLeft(List[IRStmt]())((l, e) => {
        val tmp = freshId
        val (ss, r) = walkExpr(e, env, tmp)
        l ++ ss :+ (mkExprS(e, tmp, r))
      })
      val (ss2, r2) = walkExpr(exprs.last, env, res)
      (stmts ++ ss2, r2)

    case Cond(info, InfixOpApp(_, left, op, right), trueB, falseB) if op.text.equals("&&") =>
      val span = getSpan(info)
      val newa = freshId(left, getSpan(left), "newa")
      val (ssa, ra) = walkExpr(left, env, newa)
      val (ssb, rb) = walkExpr(right, env, freshId(right, getSpan(right), "newb"))
      val (ss2, r2) = walkExpr(trueB, env, res)
      val (ss3, r3) = walkExpr(falseB, env, res)
      val lab = freshId(e, span, "label")
      val ifStmt = IF.makeIf(true, e, span, ra,
        IF.makeSeq(e, span, ssb :+
          IF.makeIf(true, e, span, rb,
            IF.makeSeq(e, span, makeList(trueB, ss2, r2, res) :+
              IF.makeBreak(false, e, span, lab)), None)),
        None)
      val body = IF.makeSeq(e, span, List(ifStmt) ++ makeList(falseB, ss3, r3, res))
      (ssa :+ IF.makeLabelStmt(false, e, span, lab, body), res)

    case Cond(info, InfixOpApp(_, left, op, right), trueB, falseB) if op.text.equals("||") =>
      val span = getSpan(info)
      val newa = freshId(left, getSpan(left), "newa")
      val (ssa, ra) = walkExpr(left, env, newa)
      val (ssb, rb) = walkExpr(right, env, freshId(right, getSpan(right), "newb"))
      val (ss2, r2) = walkExpr(trueB, env, res)
      val (ss3, r3) = walkExpr(falseB, env, res)
      val lab1 = freshId(e, span, "label1")
      val lab2 = freshId(e, span, "label2")
      val ifStmts = ((IF.makeIf(true, e, span, ra,
        IF.makeBreak(false, e, span, lab1), None)) :: ssb) :+
        IF.makeIf(true, e, span, rb, IF.makeBreak(false, e, span, lab1), None)
      val body1 = IF.makeSeq(e, span, ifStmts ++ makeList(falseB, ss3, r3, res) :+ IF.makeBreak(false, e, span, lab2))
      val body2 = IF.makeSeq(e, span, IF.makeLabelStmt(false, e, span, lab1, body1), makeSeq(trueB, trueB.info, ss2, r2, res))
      (ssa :+ IF.makeLabelStmt(false, e, span, lab2, body2), res)

    case Cond(info, Parenthesized(_, expr), trueBranch, falseBranch) =>
      walkExpr(setUID(Cond(info, expr, trueBranch, falseBranch), e.getUID), env, res)

    case Cond(info, cond, trueBranch, falseBranch) =>
      val span = getSpan(info)
      val new1 = freshId(cond, getSpan(cond), "new1")
      val (ss1, r1) = walkExpr(cond, env, new1)
      val (ss2, r2) = walkExpr(trueBranch, env, res)
      val (ss3, r3) = walkExpr(falseBranch, env, res)
      (ss1 :+ IF.makeIf(true, e, span, r1, makeSeq(trueBranch, trueBranch.info, ss2, r2, res),
        Some(makeSeq(falseBranch, falseBranch.info, ss3, r3, res))), res)

    case AssignOpApp(info, lhs, Op(_, text), right: FunExpr) if text.equals("=") && NU.isName(lhs) =>
      val name = NU.getName(lhs)
      val (ss, r) = walkFunExpr(right, env, res, Some(name))
      if (containsLhs(r, lhs, env))
        walkLval(e, lhs, env, ss, r, false)
      else
        (walkLval(e, lhs, env, ss, r, false)._1, r)

    case AssignOpApp(info, lhs, op, right) =>
      val span = getSpan(info)
      if (op.text.equals("=")) {
        val (ss, r) = walkExpr(right, env, res)
        if (containsLhs(r, lhs, env))
          walkLval(e, lhs, env, ss, r, false)
        else
          (walkLval(e, lhs, env, ss, r, false)._1, r)
      } else {
        val y = freshId(right, getSpan(right), "y")
        val oldVal = freshId(lhs, getSpan(lhs), oldName)
        val (ss, r) = walkExpr(right, env, y)
        val bin = IF.makeBin(true, e, span, oldVal, IF.makeOp(op.text.substring(0, op.text.length - 1)), r)
        (walkLval(e, lhs, addE(env, oldName, oldVal), ss, bin, true)._1, bin)
      }

    case UnaryAssignOpApp(info, lhs, op) =>
      if (op.text.equals("++") || op.text.equals("--")) {
        val lhsspan = getSpan(lhs)
        val oldVal = freshId(lhs, lhsspan, oldName)
        val newVal = freshId(lhs, lhsspan, "new")
        (
          walkLval(e, lhs, addE(env, oldName, oldVal), List(toNumber(lhs, lhsspan, newVal, oldVal)),
            IF.makeBin(true, e, getSpan(info), newVal,
              if (op.text.equals("++")) plus else minus,
              IF.oneV), true)._1,
            newVal
        )
      } else {
        signal("Invalid UnaryAssignOpApp operator: " + op.text, e)
        (List(), IF.dummyIRExpr)
      }

    case PrefixOpApp(info, op, right) =>
      val span = getSpan(info)
      val rightspan = getSpan(right)
      val opText = op.text
      if (opText.equals("++") || opText.equals("--")) {
        val oldVal = freshId(right, rightspan, oldName)
        val newVal = freshId(right, rightspan, "new")
        val bin = IF.makeBin(true, e, span, newVal,
          if (opText.equals("++")) plus else minus,
          IF.oneV)
        (walkLval(e, right, addE(env, oldName, oldVal),
          List(toNumber(right, rightspan, newVal, oldVal)),
          bin, true)._1, bin)
      } else if (opText.equals("delete")) {
        NU.unwrapParen(right) match {
          case VarRef(_, name) =>
            (List(IF.makeDelete(true, e, span, res, id2ir(env, name))), res)
          case dot @ Dot(sinfo, obj, member) =>
            val tmpBracket = setUID(Bracket(sinfo, obj, NF.makeStringLiteral(getSpan(member), member.text, "\"")), dot.getUID)
            val tmpPrefixOpApp = setUID(PrefixOpApp(info, op, tmpBracket), e.getUID)
            walkExpr(tmpPrefixOpApp, env, res)
          case Bracket(_, lhs, e2) =>
            val objspan = getSpan(lhs)
            val obj1 = freshId(lhs, objspan, "obj1")
            val field1 = freshId(e2, getSpan(e2), "field1")
            val obj = freshId(lhs, objspan, "obj")
            val (ss1, r1) = walkExpr(lhs, env, obj1)
            val (ss2, r2) = walkExpr(e2, env, field1)
            ((ss1 :+ toObject(lhs, objspan, obj, r1)) ++ ss2 :+
              IF.makeDeleteProp(true, e, span, res, obj, r2), res)
          case _ =>
            val y = freshId(right, getSpan(right), "y")
            val (ss, r) = walkExpr(right, env, y)
            (ss :+ IF.makeExprStmtIgnore(e, span, varIgn(e, span), r),
              IF.makeTId(e, span, NU.varTrue, true))
        }
      } else {
        val y = freshId(right, getSpan(right), "y")
        val (ss, r) = walkExpr(right, env, y)
        (ss, IF.makeUn(true, e, span, IF.makeOp(opText), r))
      }

    case InfixOpApp(info, left, op, right) if op.text.equals("&&") =>
      val span = getSpan(info)
      val args = getAndArgs(left) :+ right
      val news = args.zipWithIndex.map(a => freshId(a._1, getSpan(a._1), "new" + a._2))
      // list of (ss_i, r_i)
      val ress = args.zip(news).map(p => walkExpr(p._1, env, p._2))
      val (arg1: Expr, arg2: Expr, argsRest) =
        args.reverse match { case a1 :: a2 :: ar => (a2, a1, ar.reverse) case _ => signal("Internal error", e) }
      val (res1: (_, _), res2: (_, _), ressRest) =
        ress.reverse match { case a1 :: a2 :: ar => (a2, a1, ar.reverse) case _ => signal("Internal error", e) }
      val cond = res1._2.asInstanceOf[IRExpr]
      val body = IF.makeSeq(e, span,
        res1._1.asInstanceOf[List[IRStmt]] :+
          IF.makeIf(true, e, span, cond,
            IF.makeSeq(e, span, res2._1.asInstanceOf[List[IRStmt]] ++
              List(mkExprS(arg2, res,
                res2._2.asInstanceOf[IRExpr]))),
            Some(IF.makeIf(true, arg1, span,
              IF.makeBin(false, arg1, span,
                IF.makeUn(false, arg1, span, typeof, cond),
                equals, IF.makeString("boolean", arg1)),
              mkExprS(arg1, res, IF.falseV),
              Some(mkExprS(arg1, res, cond))))))
      (
        List(argsRest.asInstanceOf[List[Expr]].
          zip(ressRest.asInstanceOf[List[(List[IRStmt], IRExpr)]]).
          foldRight(body)((p, r) => {
            val sp = getSpan(p._1)
            IF.makeSeq(p._1, sp,
              p._2._1 :+
                IF.makeIf(true, p._1, sp, p._2._2, r,
                  Some(IF.makeIf(true, p._1, sp,
                    IF.makeBin(false, p._1, sp,
                      IF.makeUn(false, p._1, sp, typeof, p._2._2),
                      equals, IF.makeString("boolean", p._1)),
                    mkExprS(p._1, res, IF.falseV),
                    Some(mkExprS(p._1, res, p._2._2))))))
          })),
        res
      )

    case InfixOpApp(info, left, op, right) if op.text.equals("||") =>
      val span = getSpan(info)
      val y = freshId(left, getSpan(left), "y")
      val z = freshId(right, getSpan(right), "z")
      val (ss1, r1) = walkExpr(left, env, y)
      val (ss2, r2) = walkExpr(right, env, z)
      (ss1 :+ IF.makeIf(true, e, span, r1, mkExprS(left, res, r1),
        Some(IF.makeSeq(e, span,
          ss2 :+ mkExprS(right, res, r2)))),
        res)

    case InfixOpApp(info, left, op, right) =>
      val span = getSpan(info)
      val leftspan = getSpan(left)
      val y = freshId(left, leftspan, "y")
      val z = freshId(right, getSpan(right), "z")
      val (ss1, r1) = walkExpr(left, env, y)
      val (ss2, r2) = walkExpr(right, env, z)
      ss2 match {
        case Nil =>
          (ss1, IF.makeBin(true, e, span, r1, IF.makeOp(op.text), r2))
        case _ =>
          ((ss1 :+ mkExprS(left, leftspan, y, r1)) ++ ss2, IF.makeBin(true, e, span, y, IF.makeOp(op.text), r2))
      }

    case VarRef(info, id) => (List(), id2ir(env, id))

    case ArrayNumberExpr(info, elements) =>
      (List(IF.makeArrayNumber(true, e, getSpan(info), res, elements)), res)

    case ArrayExpr(info, elements) =>
      val newelems = elements.map(elem => elem match {
        case Some(e) =>
          val tmp = freshId
          Some((tmp, walkExpr(e, env, tmp)))
        case _ => None
      })
      val stmts = newelems.foldLeft(List[IRStmt]())((l, p) => p match {
        case None => l
        case Some((t, (ss, r))) => l ++ ss :+ (mkExprS(e, t, r))
      })
      (stmts :+ IF.makeArray(true, e, getSpan(info), res, newelems.map(elem => elem match {
        case Some(e) => Some(e._1)
        case _ => None
      })), res)

    case ObjectExpr(info, members) =>
      val new_members = members.map(walkMember(_, env, freshId))
      val stmts = new_members.foldLeft(List[IRStmt]())((l, p) => l ++ p._1)
      (stmts :+ IF.makeObject(true, e, getSpan(info), res, new_members.map(p => p._2)), res)

    case fe: FunExpr => walkFunExpr(e, env, res, None)

    case Parenthesized(_, expr) => walkExpr(expr, env, res)

    case Dot(info, first, member) =>
      val objspan = getSpan(first)
      val obj1 = freshId(first, objspan, "obj1")
      val obj = freshId(first, objspan, "obj")
      val (ss1, r1) = walkExpr(first, env, obj1)
      val str = member.text
      (ss1 :+ toObject(first, objspan, obj, r1),
        IF.makeLoad(true, e, getSpan(info), obj,
          IF.makeString(true, e, NU.unescapeJava(str))))

    case Bracket(info, first, StringLiteral(_, _, str)) =>
      val objspan = getSpan(first)
      val obj1 = freshId(first, objspan, "obj1")
      val obj = freshId(first, objspan, "obj")
      val (ss1, r1) = walkExpr(first, env, obj1)
      (ss1 :+ toObject(first, objspan, obj, r1),
        IF.makeLoad(true, e, getSpan(info), obj,
          IF.makeString(true, e, NU.unescapeJava(str))))

    case Bracket(info, first, index) =>
      val objspan = getSpan(first)
      val obj1 = freshId(first, objspan, "obj1")
      val field1 = freshId(index, getSpan(index), "field1")
      val obj = freshId(first, objspan, "obj")
      val (ss1, r1) = walkExpr(first, env, obj1)
      val (ss2, r2) = walkExpr(index, env, field1)
      ((ss1 :+ toObject(first, objspan, obj, r1)) ++ ss2,
        IF.makeLoad(true, e, getSpan(info), obj, r2))

    case n @ New(info, Parenthesized(_, e)) if e.isInstanceOf[LHS] =>
      walkExpr(setUID(New(info, e.asInstanceOf[LHS]), n.getUID), env, res)

    case n @ New(info, lhs) =>
      val span = getSpan(info)
      val objspan = getSpan(lhs)
      val fun = freshId(lhs, objspan, "fun")
      val fun1 = freshId(lhs, objspan, "fun1")
      val arg = freshId(e, span, argName)
      val obj = freshId(e, span, "obj")
      val newObj = freshId(e, span, "newObj")
      val cond = freshId(e, span, "cond")
      val proto = freshId(lhs, objspan, "proto")
      val (ftn, args) = lhs match {
        case FunApp(_, f, as) =>
          val newargs = as.map(a => freshId(a, getSpan(a)))
          val results = as.zipWithIndex.map(a => (
            newargs.apply(a._2),
            walkExpr(a._1, env, newargs.apply(a._2))
          ))
          (f, results.foldLeft(List[IRStmt]())((l, tp) => l ++ tp._2._1 :+ (mkExprS(e, tp._1, tp._2._2))) :+
            IF.makeArgs(e, span, arg, newargs.map(p => Some(p))))
        case _ => (lhs, List(IF.makeArgs(e, span, arg, Nil)))
      }
      val (ssl, rl) = walkExpr(ftn, env, fun1)
      ((ssl :+ toObject(lhs, objspan, fun, rl)) ++ args ++
        List( /*
                  * 15.3.4.5.2
                  proto = fun["prototype"]
                  obj = {[[Prototype]] = proto}
                  newObj = new fun(obj, arg)
                  cond = isObject(newObj)
                  if (cond) then x = newObj else x = obj
                 */
          IF.makeLoadStmt(false, e, span, proto, fun,
            IF.makeString("prototype", n)),
          IF.makeObject(false, e, span, obj, Nil, Some(proto)),
          IF.makeNew(true, e, span, newObj, fun, List(obj, arg)),
          isObject(e, span, cond, newObj),
          IF.makeIf(false, e, span, cond, mkExprS(e, res, newObj),
            Some(mkExprS(e, res, obj)))
        ), res)

    case FunApp(info, fun, List(arg)) if (isToObject(fun)) =>
      val (ss, r) = walkExpr(arg, env, freshId(arg, getSpan(arg), "new1"))
      (ss :+ toObject(fun, getSpan(fun), res, r), res)

    case FunApp(info, fun, List(arg)) if (NU.isEval(fun)) =>
      val newone = freshId(arg, getSpan(arg), "new1")
      val (ss, r) = walkExpr(arg, env, newone)
      (ss :+ IF.makeEval(true, e, getSpan(info), res, r), res)

    // _<>_print()
    case FunApp(info, fun, List(arg)) if (isPrint(fun)) =>
      val newone = freshId(arg, getSpan(arg), "new1")
      val (ss, r) = walkExpr(arg, env, newone)
      (ss :+ IF.makeInternalCall(e, getSpan(info), res,
        IF.makeGId(e, NU.freshGlobalName("print")), r), res)

    // _<>_printIS()
    case FunApp(info, fun, Nil) if (isPrintIS(fun)) =>
      (List(IF.makeInternalCall(e, getSpan(info), res,
        IF.makeGId(e, NU.freshGlobalName("printIS")), res)), res)

    // _<>_getTickCount()
    case FunApp(info, fun, Nil) if (isGetTickCount(fun)) =>
      (List(IF.makeInternalCall(e, getSpan(info), res,
        IF.makeGId(e, NU.freshGlobalName("getTickCount")), res)), res)

    case FunApp(info, Parenthesized(_, e), args) if e.isInstanceOf[LHS] =>
      walkExpr(setUID(FunApp(info, e.asInstanceOf[LHS], args), e.getUID), env, res)

    case FunApp(info, dot @ Dot(i, obj, member), args) =>
      walkExpr(
        setUID(
          FunApp(
            info,
            setUID(Bracket(i, obj, NF.makeStringLiteral(getSpan(member), member.text, "\"")), dot.getUID),
            args
          ),
          e.getUID
        ),
        env, res
      )

    case FunApp(info, v @ VarRef(_, fid), args) =>
      val fspan = getSpan(v)
      val obj = freshId(v, fspan, "obj")
      val argsspan = NU.spanAll(args, fspan)
      val arg = freshId(e, argsspan, argName)
      val fun = freshId(fid, fspan, "fun")
      val fir = id2ir(env, fid)
      val newargs = args.map(_ => freshId)
      val results = args.zipWithIndex.map(a => (
        newargs.apply(a._2),
        walkExpr(a._1, env, newargs.apply(a._2))
      ))
      (List(toObject(v, fspan, obj, fir)) ++
        results.foldLeft(List[IRStmt]())((l, tp) => l ++ tp._2._1 :+ (mkExprS(e, tp._1, tp._2._2))) ++
        List(
          IF.makeArgs(e, getSpan(info), arg, newargs.map(p => Some(p))),
          getBase(v, fspan, fun, fir),
          IF.makeCall(true, e, getSpan(info), res, obj, fun, arg)
        ), res)

    case FunApp(info, b @ Bracket(i, first, index), args) =>
      val firstspan = getSpan(first)
      val objspan = getSpan(i)
      val obj1 = freshId(first, firstspan, "obj1")
      val field1 = freshId(index, getSpan(index), "field1")
      val obj = freshId(e, objspan, "obj")
      val fun = freshId(e, objspan, "fun")
      val argsspan = NU.spanAll(args, getSpan(b))
      val arg = freshId(e, argsspan, argName)
      val (ssl, rl) = walkExpr(first, env, obj1)
      val (ssr, rr) = walkExpr(index, env, field1)
      val newargs = args.map(_ => freshId)
      val results = args.zipWithIndex.map(a => (
        newargs.apply(a._2),
        walkExpr(a._1, env, newargs.apply(a._2))
      ))
      (((ssl :+ toObject(first, firstspan, obj, rl)) ++ ssr) ++
        results.foldLeft(List[IRStmt]())((l, tp) => l ++ tp._2._1 :+ (mkExprS(e, tp._1, tp._2._2))) ++
        List(
          IF.makeArgs(e, getSpan(info), arg, newargs.map(p => Some(p))),
          toObject(b, b.info.span, fun, IF.makeLoad(true, e, objspan, obj, rr)),
          IF.makeCall(true, e, getSpan(info), res, fun, obj, arg)
        ), res)

    case FunApp(info, fun, args) =>
      val fspan = getSpan(fun)
      val obj1 = freshId(fun, fspan, "obj1")
      val obj = freshId(fun, fspan, "obj")
      val argsspan = NU.spanAll(args, fspan)
      val arg = freshId(e, argsspan, argName)
      val (ss, r) = walkExpr(fun, env, obj1)
      val newargs = args.map(_ => freshId)
      val results = args.zipWithIndex.map(a => (
        newargs.apply(a._2),
        walkExpr(a._1, env, newargs.apply(a._2))
      ))
      ((ss :+ toObject(fun, fspan, obj, r)) ++
        results.foldLeft(List[IRStmt]())((l, tp) => l ++ tp._2._1 :+ (mkExprS(e, tp._1, tp._2._2))) ++
        List(
          IF.makeArgs(e, getSpan(info), arg, newargs.map(p => Some(p))),
          IF.makeCall(true, e, getSpan(info), res, obj, global, arg)
        ), res)

    case t: This => (List(), IF.makeThis(t, getSpan(t)))

    case n: Null => (List(), IF.makeNull(n))

    case b @ Bool(info, isBool) =>
      (List(), if (isBool) IF.makeBool(true, b, true) else IF.makeBool(true, b, false))

    case DoubleLiteral(info, text, num) =>
      (List(), IF.makeNumber(true, e, text, num))

    case IntLiteral(info, intVal, radix) =>
      (List(), IF.makeNumber(true, e, intVal.toString, intVal.doubleValue))

    case StringLiteral(info, _, str) =>
      (List(), IF.makeString(true, e, NU.unescapeJava(str)))
  }

  def prop2ir(prop: Property): IRId = prop match {
    case PropId(info, id) => IF.makeNGId(id.text, prop, getSpan(info))
    case PropStr(info, str) => IF.makeTId(true, prop, getSpan(info), NU.unescapeJava(str))
    case PropNum(info, DoubleLiteral(_, t, _)) => IF.makeTId(true, prop, getSpan(info), t)
    case PropNum(info, IntLiteral(_, i, _)) => IF.makeTId(true, prop, getSpan(info), i.toString)
  }
  /*
   * AST2IR_M : Member -> Env -> IRId -> List[IRStmt] * IRMember
   */
  def walkMember(m: Member, env: Env, res: IRId): (List[IRStmt], IRMember) = {
    val span = getSpan(m.info)
    m match {
      case Field(_, prop, expr) =>
        val (ss, r) = walkExpr(expr, env, res)
        (ss, IF.makeField(true, m, span, prop2ir(prop), r))
      case GetProp(_, prop, Functional(_, fds, vds, body, name, params, _)) =>
        val (new_name, new_params, args, new_fds, new_vds, new_body) =
          functional(NU.prop2Id(prop), params, fds, vds, body, env, None, true)
        (
          List(),
          IF.makeGetProp(true, m, span, new_name, new_params, args, new_fds, new_vds, new_body)
        )
      case SetProp(_, prop, Functional(_, fds, vds, body, name, params, _)) =>
        val (new_name, new_params, args, new_fds, new_vds, new_body) =
          functional(NU.prop2Id(prop), params, fds, vds, body, env, None, true)
        (
          List(),
          IF.makeSetProp(true, m, span, new_name, new_params, args, new_fds, new_vds, new_body)
        )
    }
  }

  type CaseEnv = List[(Option[Expr], IRId)]
  def addCE(env: CaseEnv, x: Option[Expr], xid: IRId): CaseEnv = (x, xid) :: env
  def addRightCE(env: CaseEnv, xid: IRId): CaseEnv = env ++ List((None, xid)).asInstanceOf[CaseEnv]
  /*
   * AST2IR_CC : List[Case] * Option[List[Stmt]] * List[Case] -> Env -> List[Option[Expr] * IRId] -> IRStmt
   */
  def walkCase(ast: ASTNode, switchSpan: Span, backCases: List[Case], defCase: Option[List[Stmt]],
    frontCases: List[Case], env: Env, caseEnv: CaseEnv): IRStmt =
    (backCases, defCase, frontCases) match {
      case (head :: tail, _, _) =>
        val Case(info, condExpr, body) = head
        // span is currently set to the head statement of the default case
        val span = getSpan(info)
        val newLabel = freshId(condExpr, span, "Case2Label")
        IF.makeSeq(head, span,
          IF.makeLabelStmt(false, head, span, newLabel,
            walkCase(ast, switchSpan, tail, defCase, frontCases, env,
            addCE(caseEnv, Some(condExpr), newLabel)).asInstanceOf[IRStmt]),
          makeStmtUnit(head, span, body.map(walkStmt(_, env))))
      case (Nil, Some(stmt), _) =>
        // span is currently set to the default cases
        val span = if (stmt.isEmpty) switchSpan else getSpan(stmt.head)
        val newLabel = freshId(ast, NU.spanAll(stmt, span), "default")
        IF.makeSeq(ast, span,
          IF.makeLabelStmt(false, ast, span, newLabel,
            walkCase(ast, switchSpan, List(), None, frontCases, env,
              addRightCE(caseEnv, newLabel))),
          if (stmt.isEmpty) IF.dummyIRStmt(ast, span)
          else IF.makeSeq(ast, span, stmt.map(walkStmt(_, env))))
      case (Nil, None, head :: tail) =>
        val Case(info, condExpr, body) = head
        // span is currently set to the head statement of the default case
        val span = getSpan(info)
        val newLabel = freshId(condExpr, getSpan(head), "Case1Label")
        IF.makeSeq(head, span,
          IF.makeLabelStmt(false, head, span, newLabel,
            walkCase(ast, switchSpan, List(), None, tail, env,
              addCE(caseEnv, Some(condExpr), newLabel))),
          makeStmtUnit(head, span, body.map(walkStmt(_, env))))
      case (Nil, None, Nil) =>
        IF.makeSeq(ast, switchSpan,
          walkScond(ast, switchSpan, caseEnv, env),
          IF.makeBreak(false, ast, switchSpan, getE(env, breakName)))
    }

  /*
   * AST2IR_SC : List[Option[Expr] * IRId] -> Env -> IRStmt
   */
  def walkScond(ast: ASTNode, switchSpan: Span, caseEnv: CaseEnv, env: Env): IRStmt =
    caseEnv match {
      case (Some(expr), label) :: tail =>
        val span = getSpan(expr) // span is a position of the expression
        val cond = freshId(expr, getSpan(expr), condName)
        val (ss, r) = walkExpr(expr, env, cond)
        val comp = IF.makeBin(false, expr, span, getE(env, valName), stricteq, r)
        IF.makeSeq(expr, span, ss :+ IF.makeIf(true, expr, span, comp, IF.makeBreak(false, expr, span, label),
          Some(walkScond(ast, switchSpan, tail, env))))
      case List((None, label)) => IF.makeBreak(false, ast, switchSpan, label)
      case _ => IF.makeSeq(ast, switchSpan)
    }

  /*
   * AST2IR_LVAL : Expr -> Env -> List[IRStmt] -> IRExpr -> boolean -> List[IRStmt] * IRExpr
   */
  def walkLval(ast: ASTNode, lhs: Expr, env: Env, stmts: List[IRStmt], e: IRExpr,
    keepOld: Boolean): (List[IRStmt], IRExpr) = lhs match {
    case Parenthesized(_, expr) =>
      walkLval(ast, expr, env, stmts, e, keepOld)
    case VarRef(info, id) =>
      if (debug) println("  id=" + id.text + " " + id.uniqueName)
      val irid = id2ir(env, id)
      if (debug) println("VarRef: irid=" + irid.uniqueName)
      if (keepOld)
        (List(mkExprS(ast, getE(env, oldName), irid)) ++ stmts :+ mkExprS(ast, irid, e), irid)
      else
        (stmts :+ mkExprS(ast, irid, e), irid)
    case dot @ Dot(info, obj, member) =>
      walkLval(ast, setUID(Bracket(info, obj, NF.makeStringLiteral(getSpan(member), member.text, "\"")), dot.getUID),
        env, stmts, e, keepOld)
    case Bracket(info, first, index) =>
      val span = getSpan(info)
      val firstspan = getSpan(first)
      val obj1 = freshId(first, firstspan, "obj1")
      val field1 = freshId(index, getSpan(index), "field1")
      val obj = freshId(first, firstspan, "obj")
      val (ss1, r1) = walkExpr(first, env, obj1)
      val (ss2, r2) = walkExpr(index, env, field1)
      val front = (ss1 :+ toObject(first, firstspan, obj, r1)) ++ ss2
      val back = stmts :+ IF.makeStore(true, ast, span, obj, r2, e)
      if (keepOld)
        ((front :+ IF.makeLoadStmt(true, lhs, span, getE(env, oldName), obj, r2)) ++ back,
          IF.makeLoad(true, lhs, span, obj, r2))
      else (front ++ back, IF.makeLoad(true, lhs, span, obj, r2))
    case _ =>
      /* Instead of signaling an error at compile time,
       * translate an invalid LHS to a constant boolean
       * to result in a runtime error.
       *   ignore = LHS
       *   ignore = RHS
       *   ignore = ReferenceError
      signal("ReferenceError!", lhs)
       */
      val span = getSpan(lhs.info)
      val lhsid = freshId(lhs, span, "weird_lhs")
      val (ss, r) = walkExpr(lhs, env, lhsid)
      (ss ++ stmts ++ List(
        IF.makeExprStmtIgnore(lhs, span, varIgn(lhs, span), r),
        IF.makeExprStmtIgnore(lhs, span, varIgn(lhs, span), e),
        IF.makeExprStmtIgnore(lhs, span, varIgn(lhs, span), referenceError)
      ),
        IF.dummyIRExpr)
  }
}
