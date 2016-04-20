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

import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.util.{ NodeUtil => NU, Span }

/* Translates JavaScript AST to IR. */
class Translator(program: Program) extends ASTWalker {
  /* Error handling
   * The signal function collects errors during the AST->IR translation.
   * To collect multiple errors,
   * we should return a default value after signaling an error.
   */
  val excLog: ExcLog = new ExcLog

  ////////////////////////////////////////////////////////////////
  // Helpers
  ////////////////////////////////////////////////////////////////
  val debug = false
  var isLocal = false
  var locals: List[String] = List()
  val plus = NU.makeIROp("+")
  val minus = NU.makeIROp("-")
  val typeof = NU.makeIROp("typeof")
  val equals = NU.makeIROp("==")
  val stricteq = NU.makeIROp("===")
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

  val referenceError = makeTId(NU.defaultSpan("referenceError"), NU.referenceErrorName, true)
  val global = makeTId(NU.defaultSpan("global"), NU.globalName, true)
  def setUID[A <: ASTNode](n: A, uid: Long): A = { n.setUID(uid); n }
  val defaultSpan = NU.defaultSpan("temp")
  def freshId(ast: ASTNode, span: Span, n: String): IRTmpId = {
    val name = NU.freshName(n)
    new IRTmpId(new IRNodeInfo(span, false, ast), name, name, false)
  }
  def freshId(ast: ASTNode): IRTmpId =
    makeTId(ast, NU.freshName("temp"))
  def freshId(ast: ASTNode, n: String): IRTmpId =
    makeTId(ast, NU.freshName(n))
  def freshId(span: Span, n: String): IRTmpId =
    makeTId(span, NU.freshName(n))
  def freshId(span: Span): IRTmpId =
    makeTId(span, NU.freshName("temp"))
  def freshId: IRTmpId =
    makeTId(defaultSpan, NU.freshName("temp"))
  var ignoreId = 0
  def varIgn(ast: ASTNode): IRTmpId = {
    ignoreId += 1
    makeTId(ast, NU.ignoreName + ignoreId)
  }

  val defaultIRInfo = new IRNodeInfo(defaultSpan, false)
  def trueInfo(ast: ASTNode): IRNodeInfo = new IRNodeInfo(ast.info.span, true, ast)
  def trueInfo(span: Span, ast: ASTNode): IRNodeInfo = new IRNodeInfo(span, true, ast)
  def falseInfo(ast: ASTNode): IRNodeInfo = new IRNodeInfo(ast.info.span, false, ast)
  def falseInfo(span: Span, ast: ASTNode): IRNodeInfo = new IRNodeInfo(span, false, ast)
  def falseInfo(span: Span): IRNodeInfo = new IRNodeInfo(span, false)
  def makeSourceInfo(fromSource: Boolean, ast: ASTNode): IRNodeInfo =
    if (fromSource) trueInfo(ast) else falseInfo(ast)

  val zero = new IRString(defaultIRInfo, "0")
  val one = new IRString(defaultIRInfo, "1")
  val two = new IRString(defaultIRInfo, "2")
  val three = new IRString(defaultIRInfo, "3")
  val four = new IRString(defaultIRInfo, "4")
  val five = new IRString(defaultIRInfo, "5")
  val six = new IRString(defaultIRInfo, "6")
  val seven = new IRString(defaultIRInfo, "7")
  val eight = new IRString(defaultIRInfo, "8")
  val nine = new IRString(defaultIRInfo, "9")
  def makeString(str: String, ast: ASTNode): IRString = makeString(false, ast, str)
  def makeString(fromSource: Boolean, ast: ASTNode, str1: String): IRString = {
    if (str1.equals("0")) zero
    else if (str1.equals("1")) one
    else if (str1.equals("2")) two
    else if (str1.equals("3")) three
    else if (str1.equals("4")) four
    else if (str1.equals("5")) five
    else if (str1.equals("6")) six
    else if (str1.equals("7")) seven
    else if (str1.equals("8")) eight
    else if (str1.equals("9")) nine
    else new IRString(makeSourceInfo(fromSource, ast), str1)
  }

  val trueV = new IRBool(falseInfo(NU.defaultAst), true)
  val falseV = new IRBool(falseInfo(NU.defaultAst), false)
  val oneV = new IRNumber(falseInfo(NU.defaultAst), "1", 1)

  // make a user id
  def makeUId(originalName: String, uniqueName: String, isGlobal: Boolean,
    ast: ASTNode, span: Span, isWith: Boolean): IRUserId =
    new IRUserId(trueInfo(span, ast), originalName, uniqueName, isGlobal, isWith)

  def makeUId(originalName: String, uniqueName: String, isGlobal: Boolean,
    ast: ASTNode, isWith: Boolean): IRUserId =
    new IRUserId(trueInfo(ast), originalName, uniqueName, isGlobal, isWith)

  // make a withRewriter-generated id
  def makeWId(originalName: String, uniqueName: String, isGlobal: Boolean,
    ast: ASTNode): IRUserId =
    new IRUserId(trueInfo(ast), originalName, uniqueName, isGlobal, true)

  // make a non-global user id
  def makeNGId(uniqueName: String, ast: ASTNode): IRUserId =
    new IRUserId(trueInfo(ast), uniqueName, uniqueName, false, false)

  // make a global user id
  def makeGId(ast: ASTNode, uniqueName: String): IRUserId =
    new IRUserId(trueInfo(ast), uniqueName, uniqueName, true, false)

  // make a global user id
  def makeGId(ast: ASTNode, originalName: String, uniqueName: String): IRUserId =
    new IRUserId(trueInfo(ast), originalName, uniqueName, true, false)

  // make a non-global temporary id
  def makeTId(span: Span, uniqueName: String): IRTmpId =
    new IRTmpId(falseInfo(span), uniqueName, uniqueName, false)

  def makeTId(span: Span, uniqueName: String, isGlobal: Boolean): IRTmpId =
    new IRTmpId(falseInfo(span), uniqueName, uniqueName, isGlobal)

  def makeTId(ast: ASTNode, uniqueName: String): IRTmpId =
    new IRTmpId(falseInfo(ast), uniqueName, uniqueName, false)

  // make a temporary id
  def makeTId(ast: ASTNode, uniqueName: String, isGlobal: Boolean): IRTmpId =
    new IRTmpId(falseInfo(ast), uniqueName, uniqueName, isGlobal)

  def makeTId(ast: ASTNode, originalName: String, uniqueName: String, isGlobal: Boolean): IRTmpId =
    new IRTmpId(falseInfo(ast), originalName, uniqueName, isGlobal)

  def makeTId(fromSource: Boolean, ast: ASTNode, uniqueName: String): IRTmpId =
    new IRTmpId(makeSourceInfo(fromSource, ast), uniqueName, uniqueName, false)

  def defaultIRId(name: String): IRId =
    new IRTmpId(falseInfo(NU.defaultAst), name, name, false)
  def defaultIRId(id: Id): IRId =
    new IRTmpId(falseInfo(NU.defaultAst), id.text, id.text, false)
  def defaultIRId(label: Label): IRId =
    new IRTmpId(falseInfo(NU.defaultAst), label.id.text, label.id.text, false)

  def defaultIRExpr: IRExpr = defaultIRId("_")

  def defaultIRStmt(ast: ASTNode): IRSeq = makeSeq(ast)

  def defaultIRStmt(ast: ASTNode, msg: String): IRSeq =
    makeSeq(NU.defaultAst, List(makeExprStmt(NU.defaultAst, defaultIRId(msg), defaultIRExpr)))

  def mkExprS(ast: ASTNode, id: IRId, e: IRExpr): IRExprStmt =
    if (containsUserId(e)) makeExprStmt(ast, id, e, true)
    else makeExprStmt(ast, id, e)

  def makeLoadStmt(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, obj: IRId, index: IRExpr): IRExprStmt =
    new IRExprStmt(new IRNodeInfo(span, fromSource, ast), lhs,
      new IRLoad(makeSourceInfo(fromSource, ast), obj, index), false)

  def makeExprStmt(ast: ASTNode, lhs: IRId, right: IRExpr): IRExprStmt =
    new IRExprStmt(falseInfo(ast), lhs, right, false)

  def makeExprStmtIgnore(ast: ASTNode, lhs: IRId, right: IRExpr): IRExprStmt =
    new IRExprStmt(falseInfo(ast), lhs, right, true)

  def makeExprStmt(ast: ASTNode, lhs: IRId, right: IRExpr, isRef: Boolean): IRExprStmt =
    new IRExprStmt(falseInfo(ast), lhs, right, isRef)

  def makeSeq(ast: ASTNode, first: IRStmt, second: IRStmt): IRSeq =
    new IRSeq(falseInfo(ast), List(first, second))

  def makeSeq(ast: ASTNode): IRSeq =
    new IRSeq(falseInfo(ast), Nil)

  def makeSeq(ast: ASTNode, stmt: IRStmt): IRSeq =
    new IRSeq(falseInfo(ast), List(stmt))

  def makeSeq(ast: ASTNode, stmts: List[IRStmt]): IRSeq =
    new IRSeq(falseInfo(ast), stmts)

  def makeSeq(ast: ASTNode, span: Span, stmts: List[IRStmt]): IRSeq =
    new IRSeq(new IRNodeInfo(span, false, ast), stmts)

  def makeSeq(ast: ASTNode, span: Span): IRSeq =
    new IRSeq(new IRNodeInfo(span, false, ast), Nil)

  def makeSeq(ast: ASTNode, info: ASTNodeInfo, ss: List[IRStmt], expr: IRExpr, id: IRId): IRSeq = expr match {
    case irid: IRId if irid.uniqueName.equals(id.uniqueName) => new IRSeq(falseInfo(ast), ss)
    case _ => new IRSeq(falseInfo(ast), ss :+ mkExprS(ast, id, expr))
  }

  def makeStmtUnit(ast: ASTNode, stmts: List[IRStmt]): IRStmtUnit =
    new IRStmtUnit(trueInfo(ast), stmts)

  def makeStmtUnit(ast: ASTNode): IRStmtUnit =
    new IRStmtUnit(trueInfo(ast), Nil)

  def makeStmtUnit(ast: ASTNode, stmt: IRStmt): IRStmtUnit =
    new IRStmtUnit(trueInfo(ast), List(stmt))

  def makeStmtUnit(ast: ASTNode, first: IRStmt, second: IRStmt): IRStmtUnit =
    new IRStmtUnit(trueInfo(ast), List(first, second))

  def makeListIgnore(ast: ASTNode, ss: List[IRStmt], expr: IRExpr): List[IRStmt] = expr match {
    case id: IRId if id.uniqueName.startsWith(NU.ignoreName) => ss
    case _ => ss :+ makeExprStmtIgnore(ast, varIgn(ast), expr)
  }

  def makeList(ast: ASTNode, ss: List[IRStmt], expr: IRExpr, id: IRId): List[IRStmt] = expr match {
    case irid: IRId if irid.uniqueName.equals(id.uniqueName) => ss
    case _ => ss :+ mkExprS(ast, id, expr)
  }

  def toObject(ast: ASTNode, lhs: IRId, arg: IRExpr): IRInternalCall =
    new IRInternalCall(falseInfo(ast), lhs, makeTId(ast, NU.toObjectName, true), arg, None)

  def toNumber(ast: ASTNode, lhs: IRId, id: IRId): IRInternalCall =
    new IRInternalCall(falseInfo(ast), lhs, makeTId(ast, NU.freshGlobalName("toNumber"), true), id, None)

  def getBase(ast: ASTNode, lhs: IRId, f: IRId): IRInternalCall =
    new IRInternalCall(falseInfo(ast), lhs, makeTId(ast, NU.freshGlobalName("getBase"), true), f, None)

  def iteratorInit(ast: ASTNode, iterator: IRId, obj: IRId): IRInternalCall =
    new IRInternalCall(falseInfo(ast), iterator,
      makeTId(ast, NU.freshGlobalName("iteratorInit"), true), obj, None)

  def iteratorHasNext(ast: ASTNode, cond: IRId, obj: IRId, iterator: IRId): IRInternalCall =
    new IRInternalCall(falseInfo(ast), cond,
      makeTId(ast, NU.freshGlobalName("iteratorHasNext"), true),
      obj, Some(iterator))

  def iteratorKey(ast: ASTNode, key: IRId, obj: IRId, iterator: IRId): IRInternalCall =
    new IRInternalCall(falseInfo(ast), key,
      makeTId(ast, NU.freshGlobalName("iteratorNext"), true),
      obj, Some(iterator))

  def isObject(ast: ASTNode, lhs: IRId, id: IRId): IRInternalCall =
    new IRInternalCall(falseInfo(ast), lhs, makeTId(ast, NU.freshGlobalName("isObject"), true), id, None)

  /* Environment for renaming fresh labels and variables
   * created during the AST->IR translation.
   * Only the following identifiers are bound in the environment:
   *     arguments, val, break, testing, and continue.
   */
  type Env = List[(String, IRId)]
  def addE(env: Env, x: String, xid: IRId): Env = (x, xid) :: env
  def getE(env: Env, name: String): IRId = {
    env.find { case (n, _) => n.equals(name) } match {
      case None =>
        val id = defaultIRId(name)
        excLog.signal(IRIdNotBoundError(name, id))
        id
      case Some((_, id)) => id
    }
  }

  def funexprId(span: Span, lhs: Option[String]): Id = {
    val uniq = lhs match {
      case None => NU.funexprName(span)
      case Some(name) => name + NU.funexprName(span)
    }
    new Id(NU.makeASTNodeInfo(span), uniq, Some(uniq), false)
  }

  // Whether a given name is locally declared
  def isLocal(n: String): Boolean = locals.contains(n)

  // Getter and setter names to IRId, which do not check for "arguments"
  def mid2ir(env: Env, id: Id): IRId = id.uniqueName match {
    case None =>
      excLog.signal(NotUniqueIdError(id))
      defaultIRId(id)
    case Some(n) =>
      makeUId(id.text, n, !isLocal(n), id, false)
  }

  // When we don't know whether a give id is a local variable or not
  def id2ir(env: Env, id: Id): IRId = id.uniqueName match {
    case None =>
      excLog.signal(NotUniqueIdError(id))
      defaultIRId(id)
    case Some(n) if id.text.equals(argName) && isLocal =>
      if (debug) println("before getE:id2ir-" + id.text + " " + id.uniqueName)
      env.find { case (n, _) => n.equals(argName) } match {
        case None => makeUId(argName, argName, isLocal, id, false)
        case Some((_, id)) => id
      }
    case Some(n) if id.isWith =>
      makeWId(id.text, n, !isLocal(n), id)
    case Some(n) if NU.isInternal(id.text) =>
      makeTId(id, id.text, n, false)
    case Some(n) =>
      makeUId(id.text, n, !isLocal(n), id, false)
  }

  def label2ir(label: Label): IRId = {
    val id = label.id
    id.uniqueName match {
      case None =>
        excLog.signal(NotUniqueLabelError(label))
        defaultIRId(label)
      case Some(n) => makeUId(id.text, n, false, label, false)
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
    val paramsspan = NU.spanAll(params, NU.getSpan(name))
    var newArg = freshId(name, paramsspan, argName)
    if (debug) println(" arg=" + newArg.uniqueName)
    var newEnv = addE(env, argName, newArg)
    if (debug) {
      println("params.. ")
      params.foreach(p => print(" " + p.text))
    }
    if (params.find(_.text.equals(argName)).isDefined) {
      newArg = freshId(name, paramsspan, argName)
      if (debug) println(" arg=" + newArg.uniqueName)
    }
    val fdNames = fds.map(_.ftn.name.text)
    // nested functions shadow parameters with the same names
    val paramsVds = params.filterNot(p => fdNames contains p.text).
      map(p => new IRVarStmt(falseInfo(p), id2ir(newEnv, p), true))
    // xi = arguments["i"]
    val newParams = params.zipWithIndex.map {
      case (param, index) => makeLoadStmt(false, name, NU.getSpan(param),
        id2ir(newEnv, param),
        newArg,
        makeString(index.toString, param))
    }
    val newFds = fds.map(walkFd(_, newEnv))
    newEnv = newFds.foldLeft(newEnv)((e, fd) => addE(e, fd.ftn.name.uniqueName, fd.ftn.name))
    val newVds = vds.filterNot(_.name.text.equals(argName)).map(walkVd(_, newEnv))
    newEnv = newVds.foldLeft(newEnv)((e, vd) => addE(e, vd.lhs.uniqueName, vd.lhs))
    val newName = fe match { case Some(n) => n case None if isMember => mid2ir(env, name) case None => id2ir(env, name) }
    val newBody = body.body.map(s => walkStmt(s.asInstanceOf[Stmt], newEnv))
    isLocal = oldIsLocal
    locals = oldLocals
    (newName, List(makeTId(name, thisName), newArg),
      // nested functions shadow parameters with the same names
      newParams, /*filterNot (p => fdNames contains p.lhs.originalName),*/
      newFds, paramsVds ++ newVds, newBody)
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
    case _ => List(expr)
  }

  def getName(lhs: LHS): String = lhs match {
    case VarRef(_, id) => id.text
    case Dot(_, front, id) => getName(front) + "." + id.text
    case _: This => "this"
    case _ => ""
  }

  def containsLhs(res: IRExpr, lhs: Expr, env: Env): Boolean = {
    def getLhs(l: Expr): Option[Expr] = l match {
      case Parenthesized(_, expr) => getLhs(expr)
      case vr: VarRef => Some(vr)
      case dot @ Dot(info, obj, member) =>
        getLhs(setUID(Bracket(info, obj,
          new StringLiteral(
            NU.makeASTNodeInfo(NU.getSpan(member)),
            "\"", member.text, false
          )), dot.getUID))
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

  ////////////////////////////////////////////////////////////////
  // Translation
  ////////////////////////////////////////////////////////////////

  /* The main entry function */
  def doit: IRRoot = NU.simplifyIRWalker.walk(walkProgram(program)).asInstanceOf[IRRoot]

  /*
   * AST2IR_P : Program -> IRRoot
   */
  def walkProgram(pgm: Program): IRRoot = pgm match {
    case Program(info, TopLevel(_, fds, vds, sts)) =>
      val env = List()
      new IRRoot(trueInfo(pgm), fds.map(walkFd(_, env)), vds.map(walkVd(_, env)),
        sts.foldLeft(List[Stmt]())((l, s) => l ++ s.body.asInstanceOf[List[Stmt]]).map(s => walkStmt(s, env)))
  }

  /*
   * AST2IR_FD : FunDecl -> Env -> IRFunDecl
   */
  def walkFd(fd: FunDecl, env: Env): IRFunDecl = fd match {
    case FunDecl(info, Functional(_, fds, vds, body, name, params, _), _) =>
      val (newName, newParams, args, newFds, newVds, newBody) =
        functional(name, params, fds, vds, body, env, None, false)
      val info = trueInfo(fd)
      new IRFunDecl(
        info,
        new IRFunctional(info, true, newName, newParams, args,
          newFds, newVds, newBody)
      )
  }

  /*
   * AST2IR_VD : VarDecl -> Env -> IRVarStmt
   */
  def walkVd(vd: VarDecl, env: Env): IRVarStmt = vd match {
    case VarDecl(info, name, expr, _) =>
      expr match {
        case None =>
        case _ =>
          excLog.signal(VarDeclNotHaveInitExprError(vd))
      }
      new IRVarStmt(trueInfo(vd), id2ir(env, name), false)
  }

  var isDoWhile = false

  /*
   * AST2IR_S : Stmt -> Env -> IRStmt
   */
  def walkStmt(s: Stmt, env: Env): IRStmt = s match {
    case ABlock(info, stmts, true) =>
      makeSeq(s, stmts.map(walkStmt(_, env)))

    case ABlock(info, stmts, false) =>
      makeStmtUnit(s, stmts.map(walkStmt(_, env)))

    case StmtUnit(info, stmts) =>
      makeSeq(s, stmts.map(walkStmt(_, env)))

    case EmptyStmt(info) => makeStmtUnit(s)

    case ExprStmt(_, expr @ AssignOpApp(_, _, op, right), isInternal) =>
      val (ss, _) = walkExpr(expr, env, varIgn(right))
      // val ss1 = NU.filterIgnore(ss)
      if (isInternal) makeSeq(expr, ss)
      else makeStmtUnit(expr, ss)

    case ExprStmt(_, expr, isInternal) =>
      val (ss, r) = walkExpr(expr, env, varIgn(expr))
      if (isInternal) makeSeq(expr, makeListIgnore(expr, ss, r))
      else makeStmtUnit(expr, makeListIgnore(expr, ss, r))

    case If(info, InfixOpApp(_, left, op, right), trueB, falseB) if op.text.equals("&&") && allAnds(left) =>
      val args = getArgs(left) :+ right
      val news = args.zipWithIndex.map { case (arg, index) => freshId(arg, NU.getSpan(arg), "new" + index) }
      // list of (ssi, ri)
      val ress = args.zip(news).map { case (ssi, ri) => walkExpr(ssi, env, ri) }
      val lab = freshId(s, "label")
      val trueS = makeSeq(trueB, walkStmt(trueB, env),
        new IRBreak(falseInfo(s), lab))

      val ifStmt = args.zip(ress).foldRight((trueS, Nil): (IRStmt, List[IRStmt])) {
        case ((arg, (ssi, ri)), (stmt, stmts)) => {
          if (stmts.isEmpty)
            (new IRIf(trueInfo(arg), ri, stmt, None), ssi)
          else
            (
              new IRIf(trueInfo(arg), ri,
                makeSeq(left, stmts :+ stmt), None),
              ssi
            )
        }
      } match { case (s, _) => s }
      val body = falseB match {
        case None => ifStmt
        case Some(stmt) => makeSeq(s, List(ifStmt, walkStmt(stmt, env)))
      }
      makeStmtUnit(
        s,
        makeSeq(
          s,
          (ress.head match { case (s, _) => s }) :+ new IRLabelStmt(falseInfo(s), lab, body)
        )
      )

    case If(info, InfixOpApp(_, left, op, right), trueB, falseB) if op.text.equals("||") =>
      val new1 = freshId(left, NU.getSpan(left), "new1")
      val (ss1, r1) = walkExpr(left, env, new1)
      val (ss2, r2) = walkExpr(right, env, freshId(right, NU.getSpan(right), "new2"))
      val lab1 = freshId(s, "label1")
      val lab2 = freshId(s, "label2")
      val ifStmts = ((new IRIf(trueInfo(s), r1,
        new IRBreak(falseInfo(s), lab1), None)) :: ss2) :+
        new IRIf(trueInfo(s), r2, new IRBreak(falseInfo(s), lab1), None)
      val body1 = falseB match {
        case None => makeSeq(s, ifStmts :+ new IRBreak(falseInfo(s), lab2))
        case Some(stmt) =>
          makeSeq(s, ifStmts ++ List(
            walkStmt(stmt, env),
            new IRBreak(falseInfo(s), lab2)
          ))
      }
      val body2 = makeSeq(s, new IRLabelStmt(falseInfo(s), lab1, body1), walkStmt(trueB, env))
      makeStmtUnit(s, makeSeq(s, ss1 :+ new IRLabelStmt(falseInfo(s), lab2, body2)))

    case If(info, Parenthesized(_, expr), trueBranch, falseBranch) =>
      walkStmt(setUID(If(info, expr, trueBranch, falseBranch), s.getUID), env)

    case If(info, cond, trueBranch, falseBranch) =>
      val (ss, r) = walkExpr(cond, env, freshId(cond, NU.getSpan(cond), newName))
      makeStmtUnit(
        s,
        ss :+ new IRIf(trueInfo(s), r, walkStmt(trueBranch, env),
          falseBranch match {
            case None => None
            case Some(stmt) => Some(walkStmt(stmt, env))
          })
      )

    case Switch(info, cond, frontCases, defCase, backCases) =>
      val condVal = freshId(cond, NU.getSpan(cond), valName)
      val breakLabel = freshId(s, switchName)
      val (ss, r) = walkExpr(cond, env, condVal)
      val switchS =
        new IRLabelStmt(falseInfo(s), breakLabel,
          makeSeq(
            s,
            makeSeq(s, info, ss, r, condVal),
            walkCase(s, NU.getSpan(info), backCases.reverse,
              defCase, frontCases.reverse,
              addE(
                addE(env, breakName, breakLabel),
                valName, condVal
              ), List())
          ))
      makeStmtUnit(s, switchS)

    case DoWhile(info, body, cond) =>
      val newone = freshId(cond, NU.getSpan(cond), "new1")
      val labelName = freshId(s, breakName)
      val cont = freshId(s, continueName)
      val newEnv = addE(addE(env, breakName, labelName), continueName, cont)
      val (ss, r) = walkExpr(cond, env, newone)
      isDoWhile = true
      val newBody = makeSeq(
        s,
        List(
          new IRLabelStmt(falseInfo(s), cont, walkStmt(body, newEnv)),
          makeSeq(s, ss)
        )
      )
      isDoWhile = false
      val stmt = makeSeq(s, List(newBody, new IRWhile(trueInfo(s), r, newBody)))
      makeStmtUnit(s, new IRLabelStmt(falseInfo(s), labelName, stmt))

    case While(info, cond, body) =>
      val newone = freshId(cond, NU.getSpan(cond), "new1")
      val labelName = freshId(s, breakName)
      val cont = freshId(s, continueName)
      val newEnv = addE(addE(env, breakName, labelName), continueName, cont)
      val (ss, r) = walkExpr(cond, env, newone)
      val ssList = List(makeSeq(s, ss))
      val newBody = makeSeq(
        s,
        List(new IRLabelStmt(falseInfo(s), cont, walkStmt(body, newEnv))) ++ ssList
      )
      val stmt = makeSeq(s, ssList :+ new IRWhile(trueInfo(s), r, newBody))
      makeStmtUnit(s, new IRLabelStmt(falseInfo(s), labelName, stmt))

    case For(info, init, cond, action, body) =>
      val labelName = freshId(s, breakName)
      val cont = freshId(s, continueName)
      val newEnv = addE(addE(env, breakName, labelName), continueName, cont)
      val front = init match {
        case None => List()
        case Some(iexpr) =>
          val (ss1, r1) = walkExpr(iexpr, env, varIgn(iexpr))
          makeList(s, ss1, r1, varIgn(s))
      }
      val back = action match {
        case None => List()
        case Some(aexpr) =>
          val (ss3, r3) = walkExpr(aexpr, env, varIgn(aexpr))
          makeList(s, ss3, r3, varIgn(s))
      }
      val bodyspan = NU.getSpan(body)
      val nbody = new IRLabelStmt(falseInfo(body), cont, walkStmt(body, newEnv))
      val stmt = cond match {
        case None =>
          makeSeq(s, List(
            makeSeq(s, front),
            new IRWhile(trueInfo(bodyspan, s), trueV,
              makeSeq(s, bodyspan,
                List(nbody, makeSeq(s, bodyspan, back))))
          ))
        case Some(cexpr) =>
          val newtwo = freshId(cexpr, NU.getSpan(cexpr), "new2")
          val (ss2, r2) = walkExpr(cexpr, env, newtwo)
          val newBody = List(nbody, makeSeq(s, bodyspan, back ++ ss2))
          makeSeq(s, List(
            makeSeq(s, front ++ ss2),
            new IRWhile(trueInfo(s), r2, makeSeq(s, newBody))
          ))
      }
      makeStmtUnit(s, new IRLabelStmt(falseInfo(s), labelName, stmt))

    case ForIn(info, lhs, expr, body) =>
      val labelName = freshId(s, breakName)
      val objspan = NU.getSpan(expr)
      val newone = freshId(expr, objspan, "new1")
      val obj = freshId(expr, objspan, "obj")
      val iterator = freshId(expr, objspan, "iterator")
      val condone = freshId(expr, objspan, "cond1")
      val key = freshId(expr, objspan, "key")
      val cont = freshId(expr, objspan, continueName)
      val newEnv = addE(addE(env, breakName, labelName), continueName, cont)
      val iteratorCheck = iteratorHasNext(s, condone, obj, iterator)
      val (ss, r) = walkExpr(expr, env, newone)
      val bodyspan = NU.getSpan(body)
      val newBody = makeSeq(s, bodyspan,
        List(iteratorKey(s, key, obj, iterator)) ++
          (walkLval(lhs, lhs, addE(env, oldName, freshId(lhs, NU.getSpan(lhs), oldName)),
            List(), key, false) match { case (stmts, _) => stmts }) ++
          List(
            new IRLabelStmt(falseInfo(body), cont, walkStmt(body, newEnv)),
            makeSeq(s, bodyspan, List(iteratorCheck))
          ))
      val stmt = makeSeq(
        s,
        List(
          makeSeq(s, ss ++ List(
            mkExprS(expr, obj, r),
            iteratorInit(s, iterator, obj),
            iteratorCheck
          )),
          new IRWhile(trueInfo(bodyspan, s), condone, newBody)
        )
      )
      makeStmtUnit(s, new IRLabelStmt(falseInfo(s), labelName, stmt))

    case _: ForVar =>
      excLog.signal(NotReplacedByHoisterError(s))
      defaultIRStmt(s)

    case _: ForVarIn =>
      excLog.signal(NotReplacedByHoisterError(s))
      defaultIRStmt(s)

    case Continue(info, target) =>
      target match {
        case None =>
          makeStmtUnit(s, new IRBreak(trueInfo(s), getE(env, continueName)))
        case Some(x) =>
          makeStmtUnit(s, new IRBreak(trueInfo(s), label2ir(x)))
      }

    case Break(info, target) =>
      target match {
        case None =>
          makeStmtUnit(s, new IRBreak(trueInfo(s), getE(env, breakName)))
        case Some(tg) =>
          makeStmtUnit(s, new IRBreak(trueInfo(s), label2ir(tg)))
      }

    case r @ Return(info, expr) =>
      expr match {
        case None =>
          makeStmtUnit(s, new IRReturn(trueInfo(s), None))
        case Some(expr) =>
          val new1 = freshId(expr, NU.getSpan(expr), "new1")
          val (ss, r) = walkExpr(expr, env, new1)
          makeStmtUnit(s, ss :+ new IRReturn(trueInfo(s), Some(r)))
      }

    case With(info, expr, stmt) =>
      val objspan = NU.getSpan(expr)
      val new1 = freshId(expr, objspan, "new1")
      val new2 = freshId(expr, objspan, "new2")
      val (ss, r) = walkExpr(expr, env, new1)
      makeStmtUnit(
        s,
        ss ++ List(
          toObject(expr, new2, r),
          new IRWith(trueInfo(s), new2, walkStmt(stmt, env))
        )
      )

    case LabelStmt(info, label, stmt) =>
      makeStmtUnit(s, new IRLabelStmt(trueInfo(s), label2ir(label), walkStmt(stmt, env)))

    case Throw(info, expr) =>
      val new1 = freshId(expr, NU.getSpan(expr), "new1")
      val (ss, r) = walkExpr(expr, env, new1)
      makeStmtUnit(s, ss :+ new IRThrow(trueInfo(s), r))

    case st @ Try(info, body, catchBlock, fin) =>
      val (id, catchBody) = catchBlock match {
        case Some(Catch(_, x @ Id(i, text, Some(name), _), s)) =>
          locals = name +: locals
          val result = (
            Some(makeUId(text, name, false, st, NU.getSpan(i), false)),
            Some(makeStmtUnit(st, s.map(walkStmt(_, env))))
          )
          locals = locals.tail
          result
        case _ => (None, None)
      }
      makeStmtUnit(
        s,
        new IRTry(
          trueInfo(s),
          makeStmtUnit(st, body.map(walkStmt(_, env))),
          id, catchBody,
          fin match {
            case None => None
            case Some(s) =>
              Some(makeStmtUnit(st, s.map(walkStmt(_, env))))
          }
        )
      )

    case Debugger(info) => makeStmtUnit(s)

    case _: VarStmt =>
      excLog.signal(NotReplacedByHoisterError(s))
      defaultIRStmt(s)

    case NoOp(info, desc) =>
      new IRNoOp(falseInfo(s), desc)
  }

  def walkFunExpr(e: Expr, env: Env, res: IRId, lhs: Option[String]): (List[IRFunExpr], IRId) = e match {
    case FunExpr(info, Functional(_, fds, vds, body, name, params, _)) =>
      val id = if (name.text.equals("")) funexprId(NU.getSpan(info), lhs) else name
      val newName = makeUId(id.text, id.uniqueName.get, false,
        e, NU.getSpan(id.info), false)
      val (_, newParams, args, newFds, newVds, newBody) =
        functional(name, params, fds, vds, body, env, Some(newName), false)
      val i = trueInfo(e)
      (
        List(new IRFunExpr(i, res,
          new IRFunctional(i, true,
            newName, newParams, args, newFds, newVds, newBody))),
        res
      )
  }

  /*
   * AST2IR_E : Expr -> Env -> IRId -> List[IRStmt] * IRExpr
   */
  def walkExpr(e: Expr, env: Env, res: IRId): (List[IRStmt], IRExpr) = e match {
    case ExprList(info, Nil) =>
      (Nil, new IRUndef(trueInfo(NU.defaultAst)))

    case ExprList(info, exprs) =>
      val stmts = exprs.dropRight(1).foldLeft(List[IRStmt]())((l, e) => {
        val tmp = freshId
        val (ss, r) = walkExpr(e, env, tmp)
        l ++ ss :+ (mkExprS(e, tmp, r))
      })
      val (ss2, r2) = walkExpr(exprs.last, env, res)
      (stmts ++ ss2, r2)

    case Cond(info, InfixOpApp(_, left, op, right), trueB, falseB) if op.text.equals("&&") =>
      val newa = freshId(left, NU.getSpan(left), "newa")
      val (ssa, ra) = walkExpr(left, env, newa)
      val (ssb, rb) = walkExpr(right, env, freshId(right, NU.getSpan(right), "newb"))
      val (ss2, r2) = walkExpr(trueB, env, res)
      val (ss3, r3) = walkExpr(falseB, env, res)
      val lab = freshId(e, "label")
      val ifStmt = new IRIf(trueInfo(e), ra,
        makeSeq(e, ssb :+
          new IRIf(trueInfo(e), rb,
            makeSeq(e, makeList(trueB, ss2, r2, res) :+
              new IRBreak(falseInfo(e), lab)), None)),
        None)
      val body = makeSeq(e, List(ifStmt) ++ makeList(falseB, ss3, r3, res))
      (ssa :+ new IRLabelStmt(falseInfo(e), lab, body), res)

    case Cond(info, InfixOpApp(_, left, op, right), trueB, falseB) if op.text.equals("||") =>
      val newa = freshId(left, NU.getSpan(left), "newa")
      val (ssa, ra) = walkExpr(left, env, newa)
      val (ssb, rb) = walkExpr(right, env, freshId(right, NU.getSpan(right), "newb"))
      val (ss2, r2) = walkExpr(trueB, env, res)
      val (ss3, r3) = walkExpr(falseB, env, res)
      val lab1 = freshId(e, "label1")
      val lab2 = freshId(e, "label2")
      val irinfo = falseInfo(e)
      val ifStmts = ((new IRIf(trueInfo(e), ra,
        new IRBreak(irinfo, lab1), None)) :: ssb) :+
        new IRIf(trueInfo(e), rb, new IRBreak(irinfo, lab1), None)
      val body1 = makeSeq(e, ifStmts ++ makeList(falseB, ss3, r3, res) :+
        new IRBreak(irinfo, lab2))
      val body2 = makeSeq(e, new IRLabelStmt(falseInfo(e), lab1, body1), makeSeq(trueB, trueB.info, ss2, r2, res))
      (ssa :+ new IRLabelStmt(falseInfo(e), lab2, body2), res)

    case Cond(info, Parenthesized(_, expr), trueBranch, falseBranch) =>
      walkExpr(setUID(Cond(info, expr, trueBranch, falseBranch), e.getUID), env, res)

    case Cond(info, cond, trueBranch, falseBranch) =>
      val new1 = freshId(cond, NU.getSpan(cond), "new1")
      val (ss1, r1) = walkExpr(cond, env, new1)
      val (ss2, r2) = walkExpr(trueBranch, env, res)
      val (ss3, r3) = walkExpr(falseBranch, env, res)
      (ss1 :+ new IRIf(trueInfo(e), r1, makeSeq(trueBranch, trueBranch.info, ss2, r2, res),
        Some(makeSeq(falseBranch, falseBranch.info, ss3, r3, res))), res)

    case AssignOpApp(info, lhs, Op(_, text), right: FunExpr) if text.equals("=") && NU.isName(lhs) =>
      val name = getName(lhs)
      val (ss, r) = walkFunExpr(right, env, res, Some(name))
      if (containsLhs(r, lhs, env))
        walkLval(e, lhs, env, ss, r, false)
      else
        (walkLval(e, lhs, env, ss, r, false) match { case (stmts, _) => stmts }, r)

    case AssignOpApp(info, lhs, op, right) =>
      if (op.text.equals("=")) {
        val (ss, r) = walkExpr(right, env, res)
        if (containsLhs(r, lhs, env))
          walkLval(e, lhs, env, ss, r, false)
        else
          (walkLval(e, lhs, env, ss, r, false) match { case (stmts, _) => stmts }, r)
      } else {
        val y = freshId(right, NU.getSpan(right), "y")
        val oldVal = freshId(lhs, NU.getSpan(lhs), oldName)
        val (ss, r) = walkExpr(right, env, y)
        val bin = new IRBin(trueInfo(e), oldVal, NU.makeIROp(op.text.substring(0, op.text.length - 1)), r)
        (walkLval(e, lhs, addE(env, oldName, oldVal), ss, bin, true) match { case (stmts, _) => stmts }, bin)
      }

    case u @ UnaryAssignOpApp(info, lhs, op) =>
      if (op.text.equals("++") || op.text.equals("--")) {
        val lhsspan = NU.getSpan(lhs)
        val oldVal = freshId(lhs, lhsspan, oldName)
        val newVal = freshId(lhs, lhsspan, "new")
        (
          walkLval(e, lhs, addE(env, oldName, oldVal), List(toNumber(lhs, newVal, oldVal)),
            new IRBin(trueInfo(e), newVal,
              if (op.text.equals("++")) plus else minus,
              oneV), true) match { case (stmts, _) => stmts },
          newVal
        )
      } else {
        excLog.signal(InvalidUnAssignOpError(u))
        (List(), defaultIRExpr)
      }

    case PrefixOpApp(info, op, right) =>
      val rightspan = NU.getSpan(right)
      val opText = op.text
      if (opText.equals("++") || opText.equals("--")) {
        val oldVal = freshId(right, rightspan, oldName)
        val newVal = freshId(right, rightspan, "new")
        val bin = new IRBin(trueInfo(e), newVal,
          if (opText.equals("++")) plus else minus,
          oneV)
        (walkLval(e, right, addE(env, oldName, oldVal),
          List(toNumber(right, newVal, oldVal)),
          bin, true) match { case (stmts, _) => stmts }, bin)
      } else if (opText.equals("delete")) {
        NU.unwrapParen(right) match {
          case VarRef(_, name) =>
            (List(new IRDelete(trueInfo(e), res, id2ir(env, name))), res)
          case dot @ Dot(sinfo, obj, member) =>
            val tmpBracket = setUID(Bracket(sinfo, obj,
              new StringLiteral(
                NU.makeASTNodeInfo(NU.getSpan(member)),
                "\"", member.text, false
              )), dot.getUID)
            val tmpPrefixOpApp = setUID(PrefixOpApp(info, op, tmpBracket), e.getUID)
            walkExpr(tmpPrefixOpApp, env, res)
          case Bracket(_, lhs, e2) =>
            val objspan = NU.getSpan(lhs)
            val obj1 = freshId(lhs, objspan, "obj1")
            val field1 = freshId(e2, NU.getSpan(e2), "field1")
            val obj = freshId(lhs, objspan, "obj")
            val (ss1, r1) = walkExpr(lhs, env, obj1)
            val (ss2, r2) = walkExpr(e2, env, field1)
            ((ss1 :+ toObject(lhs, obj, r1)) ++ ss2 :+
              new IRDeleteProp(trueInfo(e), res, obj, r2), res)
          case _ =>
            val y = freshId(right, NU.getSpan(right), "y")
            val (ss, r) = walkExpr(right, env, y)
            (ss :+ makeExprStmtIgnore(e, varIgn(e), r),
              makeTId(e, NU.varTrue, true))
        }
      } else {
        val y = freshId(right, NU.getSpan(right), "y")
        val (ss, r) = walkExpr(right, env, y)
        (ss, new IRUn(trueInfo(e), NU.makeIROp(opText), r))
      }

    case infix @ InfixOpApp(info, left, op, right) if op.text.equals("&&") =>
      val args = getAndArgs(left) :+ right
      val news = args.zipWithIndex.map { case (arg, index) => freshId(arg, NU.getSpan(arg), "new" + index) }
      // list of (ssi, ri)
      val ress = args.zip(news).map { case (ssi, ri) => walkExpr(ssi, env, ri) }
      val (arg1: Expr, arg2: Expr, argsRest) =
        args.reverse match { case a1 :: a2 :: ar => (a2, a1, ar.reverse) case _ => excLog.signal(InvalidInfixOpAppError(infix)) }
      val ((res11, res12), (res21, res22), ressRest) =
        ress.reverse match { case a1 :: a2 :: ar => (a2, a1, ar.reverse) case _ => excLog.signal(InvalidInfixOpAppError(infix)) }
      val cond = res12.asInstanceOf[IRExpr]
      val body = makeSeq(
        e,
        res11.asInstanceOf[List[IRStmt]] :+
          new IRIf(trueInfo(e), cond,
            makeSeq(e, res21.asInstanceOf[List[IRStmt]] ++
              List(mkExprS(arg2, res,
                res22.asInstanceOf[IRExpr]))),
            Some(new IRIf(
              trueInfo(arg1),
              new IRBin(
                falseInfo(arg1),
                new IRUn(falseInfo(arg1), typeof, cond),
                equals, makeString("boolean", arg1)
              ),
              mkExprS(arg1, res, falseV),
              Some(mkExprS(arg1, res, cond))
            )))
      )
      (
        List(argsRest.asInstanceOf[List[Expr]].
          zip(ressRest.asInstanceOf[List[(List[IRStmt], IRExpr)]]).
          foldRight(body) {
            case ((e, (ss, ie)), r) => {
              val sp = NU.getSpan(e)
              makeSeq(
                e,
                ss :+
                  new IRIf(trueInfo(e), ie, r,
                    Some(new IRIf(
                      trueInfo(e),
                      new IRBin(
                        falseInfo(e),
                        new IRUn(falseInfo(e), typeof, ie),
                        equals, makeString("boolean", e)
                      ),
                      mkExprS(e, res, falseV),
                      Some(mkExprS(e, res, ie))
                    )))
              )
            }
          }),
        res
      )

    case InfixOpApp(info, left, op, right) if op.text.equals("||") =>
      val y = freshId(left, NU.getSpan(left), "y")
      val z = freshId(right, NU.getSpan(right), "z")
      val (ss1, r1) = walkExpr(left, env, y)
      val (ss2, r2) = walkExpr(right, env, z)
      (ss1 :+ new IRIf(trueInfo(e), r1, mkExprS(left, res, r1),
        Some(makeSeq(
          e,
          ss2 :+ mkExprS(right, res, r2)
        ))),
        res)

    case InfixOpApp(info, left, op, right) =>
      val leftspan = NU.getSpan(left)
      val y = freshId(left, leftspan, "y")
      val z = freshId(right, NU.getSpan(right), "z")
      val (ss1, r1) = walkExpr(left, env, y)
      val (ss2, r2) = walkExpr(right, env, z)
      ss2 match {
        case Nil =>
          (ss1, new IRBin(trueInfo(e), r1, NU.makeIROp(op.text), r2))
        case _ =>
          ((ss1 :+ mkExprS(left, y, r1)) ++ ss2, new IRBin(trueInfo(e), y, NU.makeIROp(op.text), r2))
      }

    case VarRef(info, id) => (List(), id2ir(env, id))

    case ArrayNumberExpr(info, elements) =>
      (List(new IRArrayNumber(trueInfo(e), res, elements)), res)

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
      (stmts :+ new IRArray(trueInfo(e), res, newelems.map(elem => elem match {
        case Some((e, _)) => Some(e)
        case _ => None
      })), res)

    case ObjectExpr(info, members) =>
      val newMembers = members.map(walkMember(_, env, freshId))
      val stmts = newMembers.foldLeft(List[IRStmt]()) { case (l, (ss, _)) => l ++ ss }
      (stmts :+ new IRObject(trueInfo(e), res, newMembers.map { case (_, m) => m }, None),
        res)

    case fe: FunExpr => walkFunExpr(e, env, res, None)

    case Parenthesized(_, expr) => walkExpr(expr, env, res)

    case Dot(info, first, member) =>
      val objspan = NU.getSpan(first)
      val obj1 = freshId(first, objspan, "obj1")
      val obj = freshId(first, objspan, "obj")
      val (ss1, r1) = walkExpr(first, env, obj1)
      val str = member.text
      (ss1 :+ toObject(first, obj, r1),
        new IRLoad(trueInfo(e), obj,
          makeString(true, e, str)))

    case Bracket(info, first, StringLiteral(_, _, str, _)) =>
      val objspan = NU.getSpan(first)
      val obj1 = freshId(first, objspan, "obj1")
      val obj = freshId(first, objspan, "obj")
      val (ss1, r1) = walkExpr(first, env, obj1)
      (ss1 :+ toObject(first, obj, r1),
        new IRLoad(trueInfo(e), obj,
          makeString(true, e, str)))

    case Bracket(info, first, index) =>
      val objspan = NU.getSpan(first)
      val obj1 = freshId(first, objspan, "obj1")
      val field1 = freshId(index, NU.getSpan(index), "field1")
      val obj = freshId(first, objspan, "obj")
      val (ss1, r1) = walkExpr(first, env, obj1)
      val (ss2, r2) = walkExpr(index, env, field1)
      ((ss1 :+ toObject(first, obj, r1)) ++ ss2,
        new IRLoad(trueInfo(e), obj, r2))

    case n @ New(info, Parenthesized(_, e)) if e.isInstanceOf[LHS] =>
      walkExpr(setUID(New(info, e.asInstanceOf[LHS]), n.getUID), env, res)

    case n @ New(info, lhs) =>
      val objspan = NU.getSpan(lhs)
      val fun = freshId(lhs, objspan, "fun")
      val fun1 = freshId(lhs, objspan, "fun1")
      val arg = freshId(e, argName)
      val obj = freshId(e, "obj")
      val newObj = freshId(e, "newObj")
      val cond = freshId(e, "cond")
      val proto = freshId(lhs, objspan, "proto")
      val (ftn, args) = lhs match {
        case FunApp(_, f, as) =>
          val newargs = as.map(a => freshId(a))
          val results = as.zipWithIndex.map {
            case (arg, index) => (
              newargs.apply(index),
              walkExpr(arg, env, newargs.apply(index))
            )
          }
          (f, results.foldLeft(List[IRStmt]()) { case (l, (arg, (stmts, expr))) => l ++ stmts :+ (mkExprS(e, arg, expr)) } :+
            new IRArgs(falseInfo(e), arg, newargs.map(p => Some(p))))
        case _ => (lhs, List(new IRArgs(falseInfo(e), arg, Nil)))
      }
      val (ssl, rl) = walkExpr(ftn, env, fun1)
      ((ssl :+ toObject(lhs, fun, rl)) ++ args ++
        List( /*
                  * 15.3.4.5.2
                  proto = fun["prototype"]
                  obj = {[[Prototype]] = proto}
                  newObj = new fun(obj, arg)
                  cond = isObject(newObj)
                  if (cond) then x = newObj else x = obj
                 */
          makeLoadStmt(false, e, NU.getSpan(e), proto, fun,
            makeString("prototype", n)),
          new IRObject(falseInfo(e), obj, Nil, Some(proto)),
          new IRNew(trueInfo(e), newObj, fun, List(obj, arg)),
          isObject(e, cond, newObj),
          new IRIf(falseInfo(e), cond, mkExprS(e, res, newObj),
            Some(mkExprS(e, res, obj)))
        ), res)

    case FunApp(info, fun, List(arg)) if (isToObject(fun)) =>
      val (ss, r) = walkExpr(arg, env, freshId(arg, NU.getSpan(arg), "new1"))
      (ss :+ toObject(fun, res, r), res)

    case FunApp(info, fun, List(arg)) if (NU.isEval(fun)) =>
      val newone = freshId(arg, NU.getSpan(arg), "new1")
      val (ss, r) = walkExpr(arg, env, newone)
      (ss :+ new IREval(trueInfo(e), res, r), res)

    // _<>_print()
    case FunApp(info, fun, List(arg)) if (isPrint(fun)) =>
      val newone = freshId(arg, NU.getSpan(arg), "new1")
      val (ss, r) = walkExpr(arg, env, newone)
      (ss :+ new IRInternalCall(falseInfo(e), res,
        makeGId(e, NU.freshGlobalName("print")), r, None), res)

    // _<>_printIS()
    case FunApp(info, fun, Nil) if (isPrintIS(fun)) =>
      (List(new IRInternalCall(falseInfo(e), res,
        makeGId(e, NU.freshGlobalName("printIS")), res, None)), res)

    // _<>_getTickCount()
    case FunApp(info, fun, Nil) if (isGetTickCount(fun)) =>
      (List(new IRInternalCall(falseInfo(e), res,
        makeGId(e, NU.freshGlobalName("getTickCount")), res, None)), res)

    case FunApp(info, Parenthesized(_, e), args) if e.isInstanceOf[LHS] =>
      walkExpr(setUID(FunApp(info, e.asInstanceOf[LHS], args), e.getUID), env, res)

    case FunApp(info, dot @ Dot(i, obj, member), args) =>
      walkExpr(
        setUID(
          FunApp(
            info,
            setUID(Bracket(i, obj,
              new StringLiteral(
                NU.makeASTNodeInfo(NU.getSpan(member)),
                "\"", member.text, false
              )), dot.getUID),
            args
          ),
          e.getUID
        ),
        env, res
      )

    case FunApp(info, v @ VarRef(_, fid), args) =>
      val fspan = NU.getSpan(v)
      val obj = freshId(v, fspan, "obj")
      val argsspan = NU.spanAll(args, fspan)
      val arg = freshId(e, argsspan, argName)
      val fun = freshId(fid, fspan, "fun")
      val fir = id2ir(env, fid)
      val newargs = args.map(_ => freshId)
      val results = args.zipWithIndex.map {
        case (arg, index) => (
          newargs.apply(index),
          walkExpr(arg, env, newargs.apply(index))
        )
      }
      (List(toObject(v, obj, fir)) ++
        results.foldLeft(List[IRStmt]()) { case (l, (arg, (stmts, expr))) => l ++ stmts :+ (mkExprS(e, arg, expr)) } ++
        List(
          new IRArgs(falseInfo(e), arg, newargs.map(p => Some(p))),
          getBase(v, fun, fir),
          new IRCall(trueInfo(e), res, obj, fun, arg)
        ), res)

    case FunApp(info, b @ Bracket(i, first, index), args) =>
      val firstspan = NU.getSpan(first)
      val objspan = NU.getSpan(i)
      val obj1 = freshId(first, firstspan, "obj1")
      val field1 = freshId(index, NU.getSpan(index), "field1")
      val obj = freshId(e, objspan, "obj")
      val fun = freshId(e, objspan, "fun")
      val argsspan = NU.spanAll(args, NU.getSpan(b))
      val arg = freshId(e, argsspan, argName)
      val (ssl, rl) = walkExpr(first, env, obj1)
      val (ssr, rr) = walkExpr(index, env, field1)
      val newargs = args.map(_ => freshId)
      val results = args.zipWithIndex.map {
        case (arg, index) => (
          newargs.apply(index),
          walkExpr(arg, env, newargs.apply(index))
        )
      }
      (((ssl :+ toObject(first, obj, rl)) ++ ssr) ++
        results.foldLeft(List[IRStmt]()) { case (l, (arg, (stmts, expr))) => l ++ stmts :+ (mkExprS(e, arg, expr)) } ++
        List(
          new IRArgs(falseInfo(e), arg, newargs.map(p => Some(p))),
          toObject(b, fun, new IRLoad(trueInfo(objspan, e), obj, rr)),
          new IRCall(trueInfo(e), res, fun, obj, arg)
        ), res)

    case FunApp(info, fun, args) =>
      val fspan = NU.getSpan(fun)
      val obj1 = freshId(fun, fspan, "obj1")
      val obj = freshId(fun, fspan, "obj")
      val argsspan = NU.spanAll(args, fspan)
      val arg = freshId(e, argsspan, argName)
      val (ss, r) = walkExpr(fun, env, obj1)
      val newargs = args.map(_ => freshId)
      val results = args.zipWithIndex.map {
        case (arg, index) => (
          newargs.apply(index),
          walkExpr(arg, env, newargs.apply(index))
        )
      }
      ((ss :+ toObject(fun, obj, r)) ++
        results.foldLeft(List[IRStmt]()) { case (l, (arg, (stmts, expr))) => l ++ stmts :+ (mkExprS(e, arg, expr)) } ++
        List(
          new IRArgs(falseInfo(e), arg, newargs.map(p => Some(p))),
          new IRCall(trueInfo(e), res, obj, global, arg)
        ), res)

    case t: This => (List(), new IRThis(trueInfo(t)))

    case n: Null => (List(), new IRNull(trueInfo(n)))

    case b @ Bool(info, isBool) =>
      (List(),
        if (isBool) new IRBool(trueInfo(b), true) else new IRBool(trueInfo(b), false))

    case DoubleLiteral(info, text, num) =>
      (List(), new IRNumber(trueInfo(e), text, num))

    case IntLiteral(info, intVal, radix) =>
      (List(), new IRNumber(trueInfo(e), intVal.toString, intVal.doubleValue))

    case StringLiteral(info, _, str, isRE) =>
      (List(),
        if (isRE) makeString(true, e, str)
        else makeString(true, e, NU.unescapeJava(str)))
  }

  def prop2ir(prop: Property): IRId = prop match {
    case PropId(info, id) => makeNGId(id.text, prop)
    case PropStr(info, str) => makeTId(true, prop, str)
    case PropNum(info, DoubleLiteral(_, t, _)) => makeTId(true, prop, t)
    case PropNum(info, IntLiteral(_, i, _)) => makeTId(true, prop, i.toString)
  }
  /*
   * AST2IR_M : Member -> Env -> IRId -> List[IRStmt] * IRMember
   */
  def walkMember(m: Member, env: Env, res: IRId): (List[IRStmt], IRMember) = {
    m match {
      case Field(_, prop, expr) =>
        val (ss, r) = walkExpr(expr, env, res)
        (ss, new IRField(trueInfo(m), prop2ir(prop), r))
      case GetProp(_, prop, Functional(_, fds, vds, body, name, params, _)) =>
        val (newName, newParams, args, newFds, newVds, newBody) =
          functional(NU.prop2Id(prop), params, fds, vds, body, env, None, true)
        val info = trueInfo(m)
        (
          List(),
          new IRGetProp(
            info,
            new IRFunctional(info, true,
              newName, newParams, args, newFds, newVds, newBody)
          )
        )
      case SetProp(_, prop, Functional(_, fds, vds, body, name, params, _)) =>
        val (newName, newParams, args, newFds, newVds, newBody) =
          functional(NU.prop2Id(prop), params, fds, vds, body, env, None, true)
        val info = trueInfo(m)
        (
          List(),
          new IRSetProp(
            info,
            new IRFunctional(info, true,
              newName, newParams, args, newFds, newVds, newBody)
          )
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
        val span = NU.getSpan(info)
        val newLabel = freshId(condExpr, span, "Case2Label")
        makeSeq(
          head,
          new IRLabelStmt(falseInfo(head), newLabel,
            walkCase(ast, switchSpan, tail, defCase, frontCases, env,
            addCE(caseEnv, Some(condExpr), newLabel)).asInstanceOf[IRStmt]),
          makeStmtUnit(head, body.map(walkStmt(_, env)))
        )
      case (Nil, Some(stmt), _) =>
        // span is currently set to the default cases
        val span = if (stmt.isEmpty) switchSpan else NU.getSpan(stmt.head)
        val newLabel = freshId(ast, NU.spanAll(stmt, span), "default")
        makeSeq(
          ast,
          new IRLabelStmt(falseInfo(span, ast), newLabel,
            walkCase(ast, switchSpan, List(), None, frontCases, env,
              addRightCE(caseEnv, newLabel))),
          if (stmt.isEmpty) defaultIRStmt(ast)
          else makeSeq(ast, span, stmt.map(walkStmt(_, env)))
        )
      case (Nil, None, head :: tail) =>
        val Case(info, condExpr, body) = head
        // span is currently set to the head statement of the default case
        val span = NU.getSpan(info)
        val newLabel = freshId(condExpr, NU.getSpan(head), "Case1Label")
        makeSeq(
          head,
          new IRLabelStmt(falseInfo(head), newLabel,
            walkCase(ast, switchSpan, List(), None, tail, env,
              addCE(caseEnv, Some(condExpr), newLabel))),
          makeStmtUnit(head, body.map(walkStmt(_, env)))
        )
      case (Nil, None, Nil) =>
        makeSeq(ast, switchSpan,
          List(
            walkScond(ast, switchSpan, caseEnv, env),
            new IRBreak(falseInfo(switchSpan, ast), getE(env, breakName))
          ))
    }

  /*
   * AST2IR_SC : List[Option[Expr] * IRId] -> Env -> IRStmt
   */
  def walkScond(ast: ASTNode, switchSpan: Span, caseEnv: CaseEnv, env: Env): IRStmt =
    caseEnv match {
      case (Some(expr), label) :: tail =>
        val span = NU.getSpan(expr) // span is a position of the expression
        val cond = freshId(expr, NU.getSpan(expr), condName)
        val (ss, r) = walkExpr(expr, env, cond)
        val comp = new IRBin(falseInfo(span, expr), getE(env, valName), stricteq, r)
        makeSeq(expr, ss :+ new IRIf(trueInfo(expr), comp, new IRBreak(falseInfo(span, expr), label),
          Some(walkScond(ast, switchSpan, tail, env))))
      case List((None, label)) => new IRBreak(falseInfo(switchSpan, ast), label)
      case _ => makeSeq(ast, switchSpan)
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
      walkLval(ast, setUID(Bracket(info, obj,
        new StringLiteral(
          NU.makeASTNodeInfo(NU.getSpan(member)),
          "\"", member.text, false
        )), dot.getUID),
        env, stmts, e, keepOld)
    case Bracket(info, first, index) =>
      val span = NU.getSpan(info)
      val firstspan = NU.getSpan(first)
      val obj1 = freshId(first, firstspan, "obj1")
      val field1 = freshId(index, NU.getSpan(index), "field1")
      val obj = freshId(first, firstspan, "obj")
      val (ss1, r1) = walkExpr(first, env, obj1)
      val (ss2, r2) = walkExpr(index, env, field1)
      val front = (ss1 :+ toObject(first, obj, r1)) ++ ss2
      val back = stmts :+ new IRStore(trueInfo(ast), obj, r2, e)
      if (keepOld)
        ((front :+ makeLoadStmt(true, lhs, span, getE(env, oldName), obj, r2)) ++ back,
          new IRLoad(trueInfo(lhs), obj, r2))
      else (front ++ back, new IRLoad(trueInfo(lhs), obj, r2))
    case _ =>
      /* Instead of signaling an error at compile time,
       * translate an invalid LHS to a constant boolean
       * to result in a runtime error.
       *   ignore = LHS
       *   ignore = RHS
       *   ignore = ReferenceError
      excLog.signal("ReferenceError!", lhs)
       */
      val lhsid = freshId(lhs, "weirdLhs")
      val (ss, r) = walkExpr(lhs, env, lhsid)
      (ss ++ stmts ++ List(
        makeExprStmtIgnore(lhs, varIgn(lhs), r),
        makeExprStmtIgnore(lhs, varIgn(lhs), e),
        makeExprStmtIgnore(lhs, varIgn(lhs), referenceError)
      ),
        defaultIRExpr)
  }
}
