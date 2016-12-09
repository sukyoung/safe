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
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util._
import kr.ac.kaist.safe.util.{ NodeUtil => NU }

/* Translates JavaScript AST to IR. */
class Translator(program: Program) {
  ////////////////////////////////////////////////////////////////
  // results
  ////////////////////////////////////////////////////////////////

  lazy val result: IRRoot = NU.SimplifyIRWalker.walk(walkProgram(program))
  lazy val excLog: ExcLog = new ExcLog

  ////////////////////////////////////////////////////////////////
  // private mutable
  ////////////////////////////////////////////////////////////////

  private var isLocal = false
  private var locals: List[String] = List()
  private var ignoreId = 0
  private var isDoWhile = false

  // default debugging mode
  private val DEBUG = false

  // default operators
  private val PLUS = IROp(NU.TEMP_AST, EJSEtcAdd)
  private val MINUS = IROp(NU.TEMP_AST, EJSEtcSub)
  private val TYPEOF = IROp(NU.TEMP_AST, EJSTypeOf)
  private val EQUALS = IROp(NU.TEMP_AST, EJSEq)
  private val STRICT_EQ = IROp(NU.TEMP_AST, EJSSEq)

  // default strings
  private val THIS_NAME = "this"
  private val ARGS_NAME = "arguments"
  private val VAL_NAME = "val"
  private val NEW_NAME = "new"
  private val OLD_NAME = "old"
  private val COND_NAME = "cond"
  private val BREAK_NAME = "break"
  private val SWITCH_NAME = "switch"
  private val CONTINUE_NAME = "continue"

  // default boolean values
  private val TRUE_BOOL = IRVal(EJSBool(true))
  private val FALSE_BOOL = IRVal(EJSBool(false))

  // default one value
  private val ONE_NUM = IRVal(EJSNumber("1", 1))

  // reference error
  private lazy val REF_ERROR =
    makeTId(Span("referenceError"), NU.REF_ERR_NAME, true)

  // global temporal id
  private lazy val GLOBAL_TMP_ID =
    makeTId(Span("global"), NU.GLOBAL_NAME, true)

  // default span
  private val TEMP_SPAN = Span("temp")

  ////////////////////////////////////////////////////////////////
  // helper function
  ////////////////////////////////////////////////////////////////

  // make fresh id
  private def freshId(ast: ASTNode, span: Span, n: String): IRTmpId = {
    val name = NU.freshName(n)
    IRTmpId(ast, name, name, false)
  }
  private def freshId(ast: ASTNode): IRTmpId =
    makeTId(ast, NU.freshName("temp"))
  private def freshId(ast: ASTNode, n: String): IRTmpId =
    makeTId(ast, NU.freshName(n))
  private def freshId(span: Span, n: String): IRTmpId =
    makeTId(span, NU.freshName(n))
  private def freshId(span: Span): IRTmpId =
    makeTId(span, NU.freshName("temp"))
  private def freshId: IRTmpId =
    makeTId(TEMP_SPAN, NU.freshName("temp"))

  // mkae ignore variable
  private def varIgn(ast: ASTNode): IRTmpId = {
    ignoreId += 1
    makeTId(ast, NU.IGNORE_NAME + ignoreId)
  }

  // make a user id
  private def makeUId(originalName: String, uniqueName: String, isGlobal: Boolean,
    ast: ASTNode, span: Span, isWith: Boolean): IRUserId =
    IRUserId(ast, originalName, uniqueName, isGlobal, isWith)
  private def makeUId(originalName: String, uniqueName: String, isGlobal: Boolean,
    ast: ASTNode, isWith: Boolean): IRUserId =
    IRUserId(ast, originalName, uniqueName, isGlobal, isWith)

  // make a withRewriter-generated id
  private def makeWId(originalName: String, uniqueName: String, isGlobal: Boolean,
    ast: ASTNode): IRUserId =
    IRUserId(ast, originalName, uniqueName, isGlobal, true)

  // make a non-global user id
  private def makeNGId(uniqueName: String, ast: ASTNode): IRUserId =
    IRUserId(ast, uniqueName, uniqueName, false, false)

  // make a global user id
  private def makeGId(ast: ASTNode, uniqueName: String): IRUserId =
    IRUserId(ast, uniqueName, uniqueName, true, false)

  // make a global user id
  private def makeGId(ast: ASTNode, originalName: String, uniqueName: String): IRUserId =
    IRUserId(ast, originalName, uniqueName, true, false)

  // make a non-global temporary id
  private def makeTId(span: Span, uniqueName: String): IRTmpId =
    IRTmpId(null, uniqueName, uniqueName, false) // TODO handle null ASTNode
  private def makeTId(span: Span, uniqueName: String, isGlobal: Boolean): IRTmpId =
    IRTmpId(null, uniqueName, uniqueName, isGlobal) // TODO handle null ASTNode
  private def makeTId(ast: ASTNode, uniqueName: String): IRTmpId =
    IRTmpId(ast, uniqueName, uniqueName, false)

  // make a temporary id
  private def makeTId(ast: ASTNode, uniqueName: String, isGlobal: Boolean): IRTmpId =
    IRTmpId(ast, uniqueName, uniqueName, isGlobal)
  private def makeTId(ast: ASTNode, originalName: String, uniqueName: String, isGlobal: Boolean): IRTmpId =
    IRTmpId(ast, originalName, uniqueName, isGlobal)
  private def makeTId(fromSource: Boolean, ast: ASTNode, uniqueName: String): IRTmpId =
    IRTmpId(ast, uniqueName, uniqueName, false)

  // make default IRId
  private def defaultIRId(name: String): IRId =
    IRTmpId(NU.TEMP_AST, name, name, false)
  private def defaultIRId(id: Id): IRId =
    IRTmpId(NU.TEMP_AST, id.text, id.text, false)
  private def defaultIRId(label: Label): IRId =
    IRTmpId(NU.TEMP_AST, label.id.text, label.id.text, false)

  // make default IRExpr
  private def defaultIRExpr: IRExpr = defaultIRId("_")

  // make expression statement with checking user id
  private def mkExprS(ast: ASTNode, id: IRId, e: IRExpr): IRExprStmt =
    if (containsUserId(e)) IRExprStmt(ast, id, e, true)
    else IRExprStmt(ast, id, e)

  // make load statement
  private def makeLoadStmt(fromSource: Boolean, ast: ASTNode, span: Span, lhs: IRId, obj: IRId, index: IRExpr): IRExprStmt =
    IRExprStmt(ast, lhs,
      IRLoad(ast, obj, index), false)

  private def makeSeq(ast: ASTNode, ss: List[IRStmt], expr: IRExpr, id: IRId): IRSeq = expr match {
    case irid: IRId if irid.uniqueName.equals(id.uniqueName) => IRSeq(ast, ss)
    case _ => IRSeq(ast, ss :+ mkExprS(ast, id, expr))
  }

  private def makeListIgnore(ast: ASTNode, ss: List[IRStmt], expr: IRExpr): List[IRStmt] = expr match {
    case id: IRId if id.uniqueName.startsWith(NU.IGNORE_NAME) => ss
    case _ => ss :+ IRExprStmt(ast, varIgn(ast), expr, false)
  }

  private def makeList(ast: ASTNode, ss: List[IRStmt], expr: IRExpr, id: IRId): List[IRStmt] = expr match {
    case irid: IRId if irid.uniqueName.equals(id.uniqueName) => ss
    case _ => ss :+ mkExprS(ast, id, expr)
  }

  private def toObject(ast: ASTNode, lhs: IRId, arg: IRExpr): IRInternalCall =
    IRInternalCall(ast, lhs, makeTId(ast, NU.INTERNAL_TO_OBJ, true), arg, None)

  private def toNumber(ast: ASTNode, lhs: IRId, id: IRId): IRInternalCall =
    IRInternalCall(ast, lhs, makeTId(ast, NU.INTERNAL_TO_NUM, true), id, None)

  private def getBase(ast: ASTNode, lhs: IRId, f: IRId): IRInternalCall =
    IRInternalCall(ast, lhs, makeTId(ast, NU.INTERNAL_GET_BASE, true), f, None)

  private def iteratorInit(ast: ASTNode, iterator: IRId, obj: IRId): IRInternalCall =
    IRInternalCall(ast, iterator, makeTId(ast, NU.INTERNAL_ITER_INIT, true), obj, None)

  private def iteratorHasNext(ast: ASTNode, cond: IRId, obj: IRId, iterator: IRId): IRInternalCall =
    IRInternalCall(ast, cond, makeTId(ast, NU.INTERNAL_HAS_NEXT, true), obj, Some(iterator))

  private def iteratorKey(ast: ASTNode, key: IRId, obj: IRId, iterator: IRId): IRInternalCall =
    IRInternalCall(ast, key, makeTId(ast, NU.INTERNAL_ITER_NEXT, true), obj, Some(iterator))

  private def isObject(ast: ASTNode, lhs: IRId, id: IRId): IRInternalCall =
    IRInternalCall(ast, lhs, makeTId(ast, NU.INTERNAL_IS_OBJ, true), id, None)

  private def unescapeJava(s: String, e: StringLiteral): String =
    if (-1 == s.indexOf('\\')) s
    else {
      val length = s.length
      val buf = new StringBuilder(length)
      var i = 0
      while (i < length) {
        var c = s.charAt(i)
        if ('\\' != c) {
          buf.append(c)
          i += 1
        } else {
          i += 1
          if (i >= length) {
            excLog.signal(InvalidStringError(e))
          }
          c = s.charAt(i)
          c match {
            case '"' => buf.append('"')
            case '\'' => buf.append('\'')
            case '\\' => buf.append('\\')
            case 'b' => buf.append('\b')
            case 'f' => buf.append('\f')
            case 'n' => buf.append('\n')
            case 'r' => buf.append('\r')
            case 't' => buf.append('\t')
            case 'v' => buf.append('\u000b')
            case 'x' =>
              i += 2
              if (i >= length) {
                excLog.signal(InvalidStringError(e))
              }
              val n = Integer.parseInt(s.substring(i - 1, i + 1), 16)
              buf.append(n.asInstanceOf[Char])
            case 'u' =>
              i += 4
              if (i >= length) {
                excLog.signal(InvalidStringError(e))
              }
              val n = Integer.parseInt(s.substring(i - 3, i + 1), 16)
              buf.append(n.asInstanceOf[Char])
            case c if NU.lineTerminating(c) =>
            case _ => buf.append(c)
          }
          i += 1
        }
      }
      buf.toString
    }

  /* Environment for renaming fresh labels and variables
   * created during the AST->IR translation.
   * Only the following identifiers are bound in the environment:
   *     arguments, val, break, testing, and continue.
   */
  type Env = List[(String, IRId)]
  private def addE(env: Env, x: String, xid: IRId): Env = (x, xid) :: env
  private def getE(env: Env, name: String): IRId = {
    env.find { case (n, _) => n.equals(name) } match {
      case None =>
        val id = defaultIRId(name)
        excLog.signal(IRIdNotBoundError(name, id.ast))
        id
      case Some((_, id)) => id
    }
  }
  private def getContLabel(env: Env, label: Label): IRId = {
    val id = label2ir(label)
    val name = id.uniqueName
    env.foldLeft[(Option[IRId], Boolean)]((None, false)) {
      case ((_, false), (CONTINUE_NAME, id)) => (Some(id), false)
      case ((Some(cont), false), (str, _)) if str == name => (Some(cont), true)
      case (pair, _) => pair
    } match {
      case (Some(cont), true) => cont
      case _ => id
    }
  }

  private def funexprId(span: Span, lhs: Option[String]): Id = {
    val uniq = lhs match {
      case None => NU.funexprName(span)
      case Some(name) => name + NU.funexprName(span)
    }
    Id(NU.makeASTNodeInfo(span), uniq, Some(uniq), false)
  }

  // Whether a given name is locally declared
  private def isLocal(n: String): Boolean = locals.contains(n)

  // Getter and setter names to IRId, which do not check for "arguments"
  private def mid2ir(env: Env, id: Id): IRId = id.uniqueName match {
    case None =>
      excLog.signal(NotUniqueIdError(id))
      defaultIRId(id)
    case Some(n) =>
      makeUId(id.text, n, !isLocal(n), id, false)
  }

  // When we don't know whether a give id is a local variable or not
  private def id2ir(env: Env, id: Id): IRId = id.uniqueName match {
    case None =>
      excLog.signal(NotUniqueIdError(id))
      defaultIRId(id)
    case Some(n) if id.text.equals(ARGS_NAME) && isLocal =>
      if (DEBUG) println("before getE:id2ir-" + id.text + " " + id.uniqueName)
      env.find { case (n, _) => n.equals(ARGS_NAME) } match {
        case None => makeUId(ARGS_NAME, ARGS_NAME, isLocal, id, false)
        case Some((_, id)) => id
      }
    case Some(n) if id.isWith =>
      makeWId(id.text, n, !isLocal(n), id)
    case Some(n) if NU.isInternal(id.text) =>
      makeTId(id, id.text, n, false)
    case Some(n) =>
      makeUId(id.text, n, !isLocal(n), id, false)
  }

  private def label2ir(label: Label): IRId = {
    val id = label.id
    id.uniqueName match {
      case None =>
        excLog.signal(NotUniqueLabelError(label))
        defaultIRId(label)
      case Some(n) => makeUId(id.text, n, false, label, false)
    }
  }

  private def functional(name: Id, params: List[Id], fds: List[FunDecl],
    vds: List[VarDecl], body: SourceElements, env: Env,
    fe: Option[IRId], isMember: Boolean): (IRId, List[IRId], List[IRStmt], List[IRFunDecl], List[IRVarStmt], List[IRStmt]) = {
    val oldIsLocal = isLocal
    val oldLocals = locals
    locals = oldLocals ++ (fe match { case Some(n) => List(n.uniqueName) case None => Nil }) ++
      params.map(_.uniqueName.get) ++
      fds.map(_.ftn.name.uniqueName.get) ++
      vds.map(_.name.uniqueName.get)
    isLocal = true
    val paramsspan = Span.merge(params, name.span)
    var newArg = freshId(name, paramsspan, ARGS_NAME)
    if (DEBUG) println(" arg=" + newArg.uniqueName)
    var newEnv = addE(env, ARGS_NAME, newArg)
    if (DEBUG) {
      println("params.. ")
      params.foreach(p => print(" " + p.text))
    }
    if (params.find(_.text.equals(ARGS_NAME)).isDefined) {
      newArg = freshId(name, paramsspan, ARGS_NAME)
      if (DEBUG) println(" arg=" + newArg.uniqueName)
    }
    val fdNames = fds.map(_.ftn.name.text)
    // nested functions shadow parameters with the same names
    val paramsVds = params.filterNot(p => fdNames contains p.text).
      map(p => IRVarStmt(p, id2ir(newEnv, p), true))
    // xi = arguments["i"]
    val newParams = params.zipWithIndex.map {
      case (param, index) => makeLoadStmt(false, name, param.span,
        id2ir(newEnv, param),
        newArg,
        IRVal(index.toString))
    }
    val newFds = fds.map(walkFd(_, newEnv))
    newEnv = newFds.foldLeft(newEnv)((e, fd) => addE(e, fd.ftn.name.uniqueName, fd.ftn.name))
    val newVds = vds.filterNot(_.name.text.equals(ARGS_NAME)).map(walkVd(_, newEnv))
    newEnv = newVds.foldLeft(newEnv)((e, vd) => addE(e, vd.lhs.uniqueName, vd.lhs))
    val NEW_NAME = fe match { case Some(n) => n case None if isMember => mid2ir(env, name) case None => id2ir(env, name) }
    val newBody = body.body.map(s => walkStmt(s.asInstanceOf[Stmt], newEnv))
    isLocal = oldIsLocal
    locals = oldLocals
    (NEW_NAME, List(makeTId(name, THIS_NAME), newArg),
      // nested functions shadow parameters with the same names
      newParams, /*filterNot (p => fdNames contains p.lhs.originalName),*/
      newFds, paramsVds ++ newVds, newBody)
  }

  private def containsUserId(e: IRExpr): Boolean = e match {
    case IRBin(_, first, _, second) => containsUserId(first) || containsUserId(second)
    case IRUn(_, _, expr) => containsUserId(expr)
    case IRLoad(_, _: IRUserId, _) => true
    case IRLoad(_, _, index) => containsUserId(index)
    case _: IRUserId => true
    case _ => false
  }

  private def allAnds(expr: Expr): Boolean = expr match {
    case Parenthesized(_, e) => allAnds(e)
    case InfixOpApp(_, l, op, r) => op.text.equals("&&") && allAnds(l) && allAnds(r)
    case _: Expr => true
    case _ => false
  }

  private def getArgs(expr: Expr): List[Expr] = expr match {
    case Parenthesized(_, e) => getArgs(e)
    case InfixOpApp(_, l, _, r) => getArgs(l) ++ getArgs(r)
    case _: Expr => List(expr)
    case _ => Nil
  }

  private def getAndArgs(expr: Expr): List[Expr] = expr match {
    case Parenthesized(_, e) => getAndArgs(e)
    case InfixOpApp(_, l, op, r) if op.text.equals("&&") => getAndArgs(l) ++ getAndArgs(r)
    case _ => List(expr)
  }

  private def getName(lhs: LHS): String = lhs match {
    case VarRef(_, id) => id.text
    case Dot(_, front, id) => getName(front) + "." + id.text
    case _: This => "this"
    case _ => ""
  }

  private def containsLhs(res: IRExpr, lhs: Expr, env: Env): Boolean = {
    def getLhs(l: Expr): Option[Expr] = l match {
      case Parenthesized(_, expr) => getLhs(expr)
      case vr: VarRef => Some(vr)
      case dot @ Dot(info, obj, member) =>
        getLhs(Bracket(info, obj,
          StringLiteral(
            NU.makeASTNodeInfo(member.span),
            "\"", member.text, false
          )))
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

  /*
   * AST2IR_P : Program -> IRRoot
   */
  private def walkProgram(pgm: Program): IRRoot = pgm match {
    case Program(_, TopLevel(_, fds, vds, sts)) =>
      val env = List()
      IRRoot(pgm, fds.map(walkFd(_, env)), vds.map(walkVd(_, env)),
        sts.foldLeft(List[Stmt]())((l, s) => l ++ s.body.asInstanceOf[List[Stmt]]).map(s => walkStmt(s, env)))
  }

  /*
   * AST2IR_FD : FunDecl -> Env -> IRFunDecl
   */
  private def walkFd(fd: FunDecl, env: Env): IRFunDecl = fd match {
    case FunDecl(_, f @ Functional(_, fds, vds, body, name, params, _), _) =>
      val (newName, newParams, args, newFds, newVds, newBody) =
        functional(name, params, fds, vds, body, env, None, false)
      IRFunDecl(
        fd,
        IRFunctional(f, true, newName, newParams, args,
          newFds, newVds, newBody)
      )
  }

  /*
   * AST2IR_VD : VarDecl -> Env -> IRVarStmt
   */
  private def walkVd(vd: VarDecl, env: Env): IRVarStmt = vd match {
    case VarDecl(_, name, expr, _) =>
      expr match {
        case None =>
        case _ =>
          excLog.signal(VarDeclNotHaveInitExprError(vd))
      }
      IRVarStmt(vd, id2ir(env, name), false)
  }

  /*
   * AST2IR_S : Stmt -> Env -> IRStmt
   */
  private def walkStmt(s: Stmt, env: Env): IRStmt = s match {
    case ABlock(_, stmts, true) =>
      IRSeq(s, stmts.map(walkStmt(_, env)))

    case ABlock(_, stmts, false) =>
      IRStmtUnit(s, stmts.map(walkStmt(_, env)))

    case StmtUnit(_, stmts) =>
      IRSeq(s, stmts.map(walkStmt(_, env)))

    case EmptyStmt(_) => IRStmtUnit(s)

    case ExprStmt(_, expr @ AssignOpApp(_, _, op, right), isInternal) =>
      val (ss, _) = walkExpr(expr, env, varIgn(right))
      // val ss1 = NU.filterIgnore(ss)
      if (isInternal) IRSeq(expr, ss)
      else IRStmtUnit(expr, ss)

    case ExprStmt(_, expr, isInternal) =>
      val (ss, r) = walkExpr(expr, env, varIgn(expr))
      if (isInternal) IRSeq(expr, makeListIgnore(expr, ss, r))
      else IRStmtUnit(expr, makeListIgnore(expr, ss, r))

    case If(_, InfixOpApp(_, left, op, right), trueB, falseB) if op.text.equals("&&") && allAnds(left) =>
      val args = getArgs(left) :+ right
      val news = args.zipWithIndex.map { case (arg, index) => freshId(arg, arg.span, "new" + index) }
      // list of (ssi, ri)
      val ress = args.zip(news).map { case (ei, newi) => walkExpr(ei, env, newi) }
      val lab = freshId(s, "label")
      val trueS = IRSeq(
        trueB,
        walkStmt(trueB, env),
        IRBreak(s, lab)
      )

      val ifStmt = args.zip(ress).foldRight((trueS, Nil): (IRStmt, List[IRStmt])) {
        case ((arg, (ssi, ri)), (stmt, stmts)) => {
          if (stmts.isEmpty)
            (IRIf(arg, ri, stmt, None), ssi)
          else
            (
              IRIf(arg, ri,
                IRSeq(left, stmts :+ stmt), None),
                ssi
            )
        }
      } match { case (s, _) => s }
      val body = falseB match {
        case None => ifStmt
        case Some(stmt) => IRSeq(s, ifStmt, walkStmt(stmt, env))
      }
      IRStmtUnit(
        s,
        List(IRSeq(
          s,
          (ress.head match { case (s, _) => s }) :+ IRLabelStmt(s, lab, body)
        ))
      )

    case If(_, InfixOpApp(_, left, op, right), trueB, falseB) if op.text.equals("||") =>
      val new1 = freshId(left, left.span, "new1")
      val (ss1, r1) = walkExpr(left, env, new1)
      val (ss2, r2) = walkExpr(right, env, freshId(right, right.span, "new2"))
      val lab1 = freshId(s, "label1")
      val lab2 = freshId(s, "label2")
      val ifStmts = ((IRIf(s, r1,
        IRBreak(s, lab1), None)) :: ss2) :+
        IRIf(s, r2, IRBreak(s, lab1), None)
      val body1 = falseB match {
        case None => IRSeq(s, ifStmts :+ IRBreak(s, lab2))
        case Some(stmt) =>
          IRSeq(s, ifStmts ++ List(
            walkStmt(stmt, env),
            IRBreak(s, lab2)
          ))
      }
      val body2 = IRSeq(s, IRLabelStmt(s, lab1, body1), walkStmt(trueB, env))
      IRStmtUnit(s, IRSeq(s, ss1 :+ IRLabelStmt(s, lab2, body2)))

    case If(info, Parenthesized(_, expr), trueBranch, falseBranch) =>
      walkStmt(If(info, expr, trueBranch, falseBranch), env)

    case If(_, cond, trueBranch, falseBranch) =>
      val (ss, r) = walkExpr(cond, env, freshId(cond, cond.span, NEW_NAME))
      IRStmtUnit(
        s,
        ss :+ IRIf(s, r, walkStmt(trueBranch, env),
          falseBranch match {
            case None => None
            case Some(stmt) => Some(walkStmt(stmt, env))
          })
      )

    case Switch(info, cond, frontCases, defCase, backCases) =>
      val condVal = freshId(cond, cond.span, VAL_NAME)
      val breakLabel = freshId(s, SWITCH_NAME)
      val (ss, r) = walkExpr(cond, env, condVal)
      val switchS =
        IRLabelStmt(s, breakLabel,
          IRSeq(
            s,
            makeSeq(s, ss, r, condVal),
            walkCase(s, info.span, backCases.reverse,
              defCase, frontCases.reverse,
              addE(
                addE(env, BREAK_NAME, breakLabel),
                VAL_NAME, condVal
              ), List())
          ))
      IRStmtUnit(s, switchS)

    case DoWhile(_, body, cond) =>
      val newone = freshId(cond, cond.span, "new1")
      val labelName = freshId(s, BREAK_NAME)
      val cont = freshId(s, CONTINUE_NAME)
      val newEnv = addE(addE(env, BREAK_NAME, labelName), CONTINUE_NAME, cont)
      val (ss, r) = walkExpr(cond, env, newone)
      isDoWhile = true
      val newBody = IRSeq(
        s,
        IRLabelStmt(s, cont, walkStmt(body, newEnv)),
        IRSeq(s, ss)
      )
      isDoWhile = false
      val stmt = IRSeq(s, newBody, IRWhile(s, r, newBody))
      IRStmtUnit(s, IRLabelStmt(s, labelName, stmt))

    case While(_, cond, body) =>
      val newone = freshId(cond, cond.span, "new1")
      val labelName = freshId(s, BREAK_NAME)
      val cont = freshId(s, CONTINUE_NAME)
      val newEnv = addE(addE(env, BREAK_NAME, labelName), CONTINUE_NAME, cont)
      val (ss, r) = walkExpr(cond, env, newone)
      val ssList = List(IRSeq(s, ss))
      val newBody = IRSeq(
        s,
        IRLabelStmt(s, cont, walkStmt(body, newEnv)) :: ssList
      )
      val stmt = IRSeq(s, ssList :+ IRWhile(s, r, newBody))
      IRStmtUnit(s, IRLabelStmt(s, labelName, stmt))

    case For(_, init, cond, action, body) =>
      val labelName = freshId(s, BREAK_NAME)
      val cont = freshId(s, CONTINUE_NAME)
      val newEnv = addE(addE(env, BREAK_NAME, labelName), CONTINUE_NAME, cont)
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
      val bodyspan = body.span
      val nbody = IRLabelStmt(body, cont, walkStmt(body, newEnv))
      val stmt = cond match {
        case None =>
          IRSeq(
            s,
            IRSeq(s, front),
            IRWhile(s, TRUE_BOOL,
              IRSeq(
                s,
                nbody, IRSeq(s, back)
              ))
          )
        case Some(cexpr) =>
          val newtwo = freshId(cexpr, cexpr.span, "new2")
          val (ss2, r2) = walkExpr(cexpr, env, newtwo)
          val newBody = List(nbody, IRSeq(s, back ++ ss2))
          IRSeq(
            s,
            IRSeq(s, front ++ ss2),
            IRWhile(s, r2, IRSeq(s, newBody))
          )
      }
      IRStmtUnit(s, IRLabelStmt(s, labelName, stmt))

    case ForIn(_, lhs, expr, body) =>
      val labelName = freshId(s, BREAK_NAME)
      val objspan = expr.span
      val newone = freshId(expr, objspan, "new1")
      val obj = freshId(expr, objspan, "obj")
      val iterator = freshId(expr, objspan, "iterator")
      val condone = freshId(expr, objspan, "cond1")
      val key = freshId(expr, objspan, "key")
      val cont = freshId(expr, objspan, CONTINUE_NAME)
      val newEnv = addE(addE(env, BREAK_NAME, labelName), CONTINUE_NAME, cont)
      val iteratorCheck = iteratorHasNext(s, condone, obj, iterator)
      val (ss, r) = walkExpr(expr, env, newone)
      val bodyspan = body.span
      val newBody = IRSeq(
        s,
        iteratorKey(s, key, obj, iterator) ::
          (walkLval(lhs, lhs, addE(env, OLD_NAME, freshId(lhs, lhs.span, OLD_NAME)),
            List(), key, false) match { case (stmts, _) => stmts }) ++
          List(
            IRLabelStmt(body, cont, walkStmt(body, newEnv)),
            IRSeq(s, iteratorCheck)
          )
      )
      val stmt = IRSeq(
        s,
        IRSeq(s, ss ++ List(
          mkExprS(expr, obj, r),
          iteratorInit(s, iterator, obj),
          iteratorCheck
        )),
        IRWhile(s, condone, newBody)
      )
      IRStmtUnit(s, IRLabelStmt(s, labelName, stmt))

    case _: ForVar =>
      excLog.signal(NotReplacedByHoisterError(s))
      IRSeq(s)

    case _: ForVarIn =>
      excLog.signal(NotReplacedByHoisterError(s))
      IRSeq(s)

    case Continue(_, target) =>
      target match {
        case None =>
          IRStmtUnit(s, IRBreak(s, getE(env, CONTINUE_NAME)))
        case Some(x) =>
          IRStmtUnit(s, IRBreak(s, getContLabel(env, x)))
      }

    case Break(_, target) =>
      target match {
        case None =>
          IRStmtUnit(s, IRBreak(s, getE(env, BREAK_NAME)))
        case Some(tg) =>
          IRStmtUnit(s, IRBreak(s, label2ir(tg)))
      }

    case r @ Return(_, expr) =>
      expr match {
        case None =>
          IRStmtUnit(s, IRReturn(s, None))
        case Some(expr) =>
          val new1 = freshId(expr, expr.span, "new1")
          val (ss, r) = walkExpr(expr, env, new1)
          IRStmtUnit(s, ss :+ IRReturn(s, Some(r)))
      }

    case With(_, expr, stmt) =>
      val objspan = expr.span
      val new1 = freshId(expr, objspan, "new1")
      val new2 = freshId(expr, objspan, "new2")
      val (ss, r) = walkExpr(expr, env, new1)
      IRStmtUnit(
        s,
        ss ++ List(
          toObject(expr, new2, r),
          IRWith(s, new2, walkStmt(stmt, env))
        )
      )

    case LabelStmt(_, label, stmt) =>
      val id = label2ir(label)
      val name = id.uniqueName
      val newEnv = addE(env, name, id)
      IRStmtUnit(s, IRLabelStmt(s, id, walkStmt(stmt, newEnv)))

    case Throw(_, expr) =>
      val new1 = freshId(expr, expr.span, "new1")
      val (ss, r) = walkExpr(expr, env, new1)
      IRStmtUnit(s, ss :+ IRThrow(s, r))

    case st @ Try(_, body, catchBlock, fin) =>
      val (id, catchBody) = catchBlock match {
        case Some(Catch(_, x @ Id(i, text, Some(name), _), s)) =>
          locals = name +: locals
          val result = (
            Some(makeUId(text, name, false, st, i.span, false)),
            Some(IRStmtUnit(st, s.map(walkStmt(_, env))))
          )
          locals = locals.tail
          result
        case _ => (None, None)
      }
      IRStmtUnit(
        s,
        IRTry(
          s,
          IRStmtUnit(st, body.map(walkStmt(_, env))),
          id, catchBody,
          fin match {
            case None => None
            case Some(s) =>
              Some(IRStmtUnit(st, s.map(walkStmt(_, env))))
          }
        )
      )

    case Debugger(_) => IRStmtUnit(s)
    case _: VarStmt =>
      excLog.signal(NotReplacedByHoisterError(s))
      IRSeq(s)

    case NoOp(_, desc) =>
      IRNoOp(s, desc)
  }

  private def walkFunExpr(e: Expr, env: Env, res: IRId, lhs: Option[String]): (List[IRFunExpr], IRId) = e match {
    case FunExpr(info, f @ Functional(_, fds, vds, body, name, params, _)) =>
      val id = if (name.text.equals("")) funexprId(info.span, lhs) else name
      val NEW_NAME = makeUId(id.text, id.uniqueName.get, false,
        e, id.info.span, false)
      val (_, newParams, args, newFds, newVds, newBody) =
        functional(name, params, fds, vds, body, env, Some(NEW_NAME), false)
      val i = e
      (
        List(IRFunExpr(i, res,
          IRFunctional(f, true,
            NEW_NAME, newParams, args, newFds, newVds, newBody))),
        res
      )
  }

  /*
   * AST2IR_E : Expr -> Env -> IRId -> List[IRStmt] * IRExpr
   */
  private def walkExpr(e: Expr, env: Env, res: IRId): (List[IRStmt], IRExpr) = e match {
    case ExprList(_, Nil) =>
      (Nil, IRVal(EJSUndef))

    case ExprList(_, exprs) =>
      val stmts = exprs.dropRight(1).foldLeft(List[IRStmt]())((l, e) => {
        val tmp = freshId
        val (ss, r) = walkExpr(e, env, tmp)
        l ++ ss :+ (mkExprS(e, tmp, r))
      })
      val (ss2, r2) = walkExpr(exprs.last, env, res)
      (stmts ++ ss2, r2)

    case Cond(_, InfixOpApp(_, left, op, right), trueB, falseB) if op.text.equals("&&") =>
      val newa = freshId(left, left.span, "newa")
      val (ssa, ra) = walkExpr(left, env, newa)
      val (ssb, rb) = walkExpr(right, env, freshId(right, right.span, "newb"))
      val (ss2, r2) = walkExpr(trueB, env, res)
      val (ss3, r3) = walkExpr(falseB, env, res)
      val lab = freshId(e, "label")
      val ifStmt = IRIf(e, ra,
        IRSeq(e, ssb :+
          IRIf(e, rb,
            IRSeq(e, makeList(trueB, ss2, r2, res) :+
              IRBreak(e, lab)), None)),
        None)
      val body = IRSeq(e, List(ifStmt) ++ makeList(falseB, ss3, r3, res))
      (ssa :+ IRLabelStmt(e, lab, body), res)

    case Cond(_, InfixOpApp(_, left, op, right), trueB, falseB) if op.text.equals("||") =>
      val newa = freshId(left, left.span, "newa")
      val (ssa, ra) = walkExpr(left, env, newa)
      val (ssb, rb) = walkExpr(right, env, freshId(right, right.span, "newb"))
      val (ss2, r2) = walkExpr(trueB, env, res)
      val (ss3, r3) = walkExpr(falseB, env, res)
      val lab1 = freshId(e, "label1")
      val lab2 = freshId(e, "label2")
      val ifStmts = ((IRIf(e, ra,
        IRBreak(e, lab1), None)) :: ssb) :+
        IRIf(e, rb, IRBreak(e, lab1), None)
      val body1 = IRSeq(e, ifStmts ++ makeList(falseB, ss3, r3, res) :+
        IRBreak(e, lab2))
      val body2 = IRSeq(e, IRLabelStmt(e, lab1, body1), makeSeq(trueB, ss2, r2, res))
      (ssa :+ IRLabelStmt(e, lab2, body2), res)

    case Cond(info, Parenthesized(_, expr), trueBranch, falseBranch) =>
      walkExpr(Cond(info, expr, trueBranch, falseBranch), env, res)

    case Cond(_, cond, trueBranch, falseBranch) =>
      val new1 = freshId(cond, cond.span, "new1")
      val (ss1, r1) = walkExpr(cond, env, new1)
      val (ss2, r2) = walkExpr(trueBranch, env, res)
      val (ss3, r3) = walkExpr(falseBranch, env, res)
      (ss1 :+ IRIf(e, r1, makeSeq(trueBranch, ss2, r2, res),
        Some(makeSeq(falseBranch, ss3, r3, res))), res)

    case AssignOpApp(_, lhs, Op(_, text), right: FunExpr) if text.equals("=") && lhs.isName =>
      val name = getName(lhs)
      val (ss, r) = walkFunExpr(right, env, res, Some(name))
      if (containsLhs(r, lhs, env))
        walkLval(e, lhs, env, ss, r, false)
      else
        (walkLval(e, lhs, env, ss, r, false) match { case (stmts, _) => stmts }, r)

    case AssignOpApp(_, lhs, op, right) =>
      if (op.text.equals("=")) {
        val (ss, r) = walkExpr(right, env, res)
        if (containsLhs(r, lhs, env))
          walkLval(e, lhs, env, ss, r, false)
        else
          (walkLval(e, lhs, env, ss, r, false) match { case (stmts, _) => stmts }, r)
      } else {
        val y = freshId(right, right.span, "y")
        val oldVal = freshId(lhs, lhs.span, OLD_NAME)
        val (ss, r) = walkExpr(right, env, y)
        val bin = IRBin(e, oldVal, IROp(NU.TEMP_AST, EJSOp(op.text.substring(0, op.text.length - 1))), r)
        (walkLval(e, lhs, addE(env, OLD_NAME, oldVal), ss, bin, true) match { case (stmts, _) => stmts }, bin)
      }

    case u @ UnaryAssignOpApp(_, lhs, op) =>
      if (op.text.equals("++") || op.text.equals("--")) {
        val lhsspan = lhs.span
        val oldVal = freshId(lhs, lhsspan, OLD_NAME)
        val newVal = freshId(lhs, lhsspan, "new")
        (
          walkLval(e, lhs, addE(env, OLD_NAME, oldVal), List(toNumber(lhs, newVal, oldVal)),
            IRBin(e, newVal,
              if (op.text.equals("++")) PLUS else MINUS,
              ONE_NUM), true) match { case (stmts, _) => stmts },
          newVal
        )
      } else {
        excLog.signal(InvalidUnAssignOpError(u))
        (List(), defaultIRExpr)
      }

    case PrefixOpApp(info, op, right) =>
      val rightspan = right.span
      val opText = op.text
      if (opText.equals("++") || opText.equals("--")) {
        val oldVal = freshId(right, rightspan, OLD_NAME)
        val newVal = freshId(right, rightspan, "new")
        val bin = IRBin(e, newVal,
          if (opText.equals("++")) PLUS else MINUS,
          ONE_NUM)
        (walkLval(e, right, addE(env, OLD_NAME, oldVal),
          List(toNumber(right, newVal, oldVal)),
          bin, true) match { case (stmts, _) => stmts }, bin)
      } else if (opText.equals("delete")) {
        right.unwrapParen match {
          case VarRef(_, name) =>
            (List(IRDelete(e, res, id2ir(env, name))), res)
          case dot @ Dot(sinfo, obj, member) =>
            val tmpBracket = Bracket(sinfo, obj,
              StringLiteral(
                NU.makeASTNodeInfo(member.span),
                "\"", member.text, false
              ))
            val tmpPrefixOpApp = PrefixOpApp(info, op, tmpBracket)
            walkExpr(tmpPrefixOpApp, env, res)
          case Bracket(_, lhs, e2) =>
            val objspan = lhs.span
            val obj1 = freshId(lhs, objspan, "obj1")
            val field1 = freshId(e2, e2.span, "field1")
            val obj = freshId(lhs, objspan, "obj")
            val (ss1, r1) = walkExpr(lhs, env, obj1)
            val (ss2, r2) = walkExpr(e2, env, field1)
            ((ss1 :+ toObject(lhs, obj, r1)) ++ ss2 :+
              IRDeleteProp(e, res, obj, r2), res)
          case _ =>
            val y = freshId(right, right.span, "y")
            val (ss, r) = walkExpr(right, env, y)
            (ss :+ IRExprStmt(e, varIgn(e), r, false),
              makeTId(e, NU.VAR_TRUE, true))
        }
      } else {
        val y = freshId(right, right.span, "y")
        val (ss, r) = walkExpr(right, env, y)
        (ss, IRUn(e, IROp(NU.TEMP_AST, EJSOp(opText)), r))
      }

    case infix @ InfixOpApp(_, left, op, right) if op.text.equals("&&") =>
      val args = getAndArgs(left) :+ right
      val news = args.zipWithIndex.map { case (arg, index) => freshId(arg, arg.span, "new" + index) }
      // list of (ssi, ri)
      val ress = args.zip(news).map { case (ssi, ri) => walkExpr(ssi, env, ri) }
      val (arg1: Expr, arg2: Expr, argsRest) =
        args.reverse match { case a1 :: a2 :: ar => (a2, a1, ar.reverse) case _ => excLog.signal(InvalidInfixOpAppError(infix)) }
      val ((res11, cond: IRExpr), (res21, res22: IRExpr), ressRest) =
        ress.reverse match { case a1 :: a2 :: ar => (a2, a1, ar.reverse) case _ => excLog.signal(InvalidInfixOpAppError(infix)) }
      val body = IRSeq(
        e,
        res11.asInstanceOf[List[IRStmt]] :+
          IRIf(e, cond,
            IRSeq(e, res21.asInstanceOf[List[IRStmt]] ++
              List(mkExprS(arg2, res,
                res22))),
            Some(IRIf(
              arg1,
              IRBin(
                arg1,
                IRUn(arg1, TYPEOF, cond),
                EQUALS, IRVal("boolean")
              ),
              mkExprS(arg1, res, FALSE_BOOL),
              Some(mkExprS(arg1, res, cond))
            )))
      )
      (
        List(argsRest.asInstanceOf[List[Expr]].
          zip(ressRest.asInstanceOf[List[(List[IRStmt], IRExpr)]]).
          foldRight(body) {
            case ((e, (ss, ie)), r) => {
              val sp = e.span
              IRSeq(
                e,
                ss :+
                  IRIf(e, ie, r,
                    Some(IRIf(
                      e,
                      IRBin(
                        e,
                        IRUn(e, TYPEOF, ie),
                        EQUALS, IRVal("boolean")
                      ),
                      mkExprS(e, res, FALSE_BOOL),
                      Some(mkExprS(e, res, ie))
                    )))
              )
            }
          }),
        res
      )

    case InfixOpApp(_, left, op, right) if op.text.equals("||") =>
      val y = freshId(left, left.span, "y")
      val z = freshId(right, right.span, "z")
      val (ss1, r1) = walkExpr(left, env, y)
      val (ss2, r2) = walkExpr(right, env, z)
      (ss1 :+ IRIf(e, r1, mkExprS(left, res, r1),
        Some(IRSeq(
          e,
          ss2 :+ mkExprS(right, res, r2)
        ))),
        res)

    case InfixOpApp(_, left, op, right) =>
      val leftspan = left.span
      val y = freshId(left, leftspan, "y")
      val z = freshId(right, right.span, "z")
      val (ss1, r1) = walkExpr(left, env, y)
      val (ss2, r2) = walkExpr(right, env, z)
      ss2 match {
        case Nil =>
          (ss1, IRBin(e, r1, IROp(NU.TEMP_AST, EJSOp(op.text)), r2))
        case _ =>
          ((ss1 :+ mkExprS(left, y, r1)) ++ ss2, IRBin(e, y, IROp(NU.TEMP_AST, EJSOp(op.text)), r2))
      }

    case VarRef(_, id @ Id(_, name, _, _)) if NU.isInternalValue(name) =>
      (List(), IRInternalValue(id, name))
    case VarRef(_, id) => (List(), id2ir(env, id))

    case ArrayNumberExpr(_, elements) =>
      (List(IRArrayNumber(e, res, elements)), res)

    case ArrayExpr(_, elements) =>
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
      (stmts :+ IRArray(e, res, newelems.map(elem => elem match {
        case Some((e, _)) => Some(e)
        case _ => None
      })), res)

    case ObjectExpr(_, members) =>
      val newMembers = members.map(walkMember(_, env, freshId))
      val stmts = newMembers.foldLeft(List[IRStmt]()) { case (l, (ss, _)) => l ++ ss }
      (stmts :+ IRObject(e, res, newMembers.map { case (_, m) => m }, None),
        res)

    case fe: FunExpr => walkFunExpr(e, env, res, None)

    case Parenthesized(_, expr) => walkExpr(expr, env, res)

    case Dot(_, first, member) =>
      val objspan = first.span
      val obj1 = freshId(first, objspan, "obj1")
      val obj = freshId(first, objspan, "obj")
      val (ss1, r1) = walkExpr(first, env, obj1)
      val str = member.text
      (ss1 :+ toObject(first, obj, r1),
        IRLoad(e, obj,
          IRVal(str)))

    case Bracket(_, first, StringLiteral(_, _, str, _)) =>
      val objspan = first.span
      val obj1 = freshId(first, objspan, "obj1")
      val obj = freshId(first, objspan, "obj")
      val (ss1, r1) = walkExpr(first, env, obj1)
      (ss1 :+ toObject(first, obj, r1),
        IRLoad(e, obj,
          IRVal(str)))

    case Bracket(_, first, index) =>
      val objspan = first.span
      val obj1 = freshId(first, objspan, "obj1")
      val field1 = freshId(index, index.span, "field1")
      val obj = freshId(first, objspan, "obj")
      val (ss1, r1) = walkExpr(first, env, obj1)
      val (ss2, r2) = walkExpr(index, env, field1)
      ((ss1 :+ toObject(first, obj, r1)) ++ ss2,
        IRLoad(e, obj, r2))

    case n @ New(info, Parenthesized(_, e)) if e.isInstanceOf[LHS] =>
      walkExpr(New(info, e.asInstanceOf[LHS]), env, res)

    case n @ New(_, lhs) =>
      val objspan = lhs.span
      val fun = freshId(lhs, objspan, "fun")
      val fun1 = freshId(lhs, objspan, "fun1")
      val arg = freshId(e, ARGS_NAME)
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
            IRArgs(e, arg, newargs.map(p => Some(p))))
        case _ => (lhs, List(IRArgs(e, arg, Nil)))
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
          makeLoadStmt(false, e, e.span, proto, fun,
            IRVal("prototype")),
          IRObject(e, obj, Nil, Some(proto)),
          IRNew(e, newObj, fun, List(obj, arg)),
          isObject(e, cond, newObj),
          IRIf(e, cond, mkExprS(e, res, newObj),
            Some(mkExprS(e, res, obj)))
        ), res)

    case FunApp(_, VarRef(_, Id(_, fun, _, _)), args) if (NU.isInternalCall(fun)) => args match {
      case Nil =>
        (List(IRInternalCall(e, res,
          makeTId(e, fun, true), res, None)), res)
      case _ =>
        val last = args.last
        val front = args.take(args.length - 1)
        val newArgs = front.zipWithIndex.map { case (arg, index) => freshId(arg, arg.span, "new" + index) }
        val results = front.zip(newArgs).map { case (arg, newArg) => (newArg, walkExpr(arg, env, newArg)) }
        val ss1 = results.foldLeft(List[IRStmt]()) { case (l, (newArg, (stmts, expr))) => l ++ stmts :+ (mkExprS(e, newArg, expr)) }
        val (ss2, r) = walkExpr(last, env, freshId(last, last.span, "new" + args.length))
        (ss1 ++ ss2 :+ IRInternalCall(e, res,
          makeTId(e, fun, true), r, None), res)
    }

    case FunApp(_, fun, List(arg)) if (fun.isEval) =>
      val newone = freshId(arg, arg.span, "new1")
      val (ss, r) = walkExpr(arg, env, newone)
      (ss :+ IREval(e, res, r), res)

    case FunApp(info, Parenthesized(_, e), args) if e.isInstanceOf[LHS] =>
      walkExpr(FunApp(info, e.asInstanceOf[LHS], args), env, res)

    case FunApp(info, dot @ Dot(i, obj, member), args) =>
      walkExpr(
        FunApp(
          info,
          Bracket(i, obj,
            StringLiteral(
              NU.makeASTNodeInfo(member.span),
              "\"", member.text, false
            )),
          args
        ),
        env, res
      )

    case FunApp(_, v @ VarRef(_, fid), args) =>
      val fspan = v.span
      val obj = freshId(v, fspan, "obj")
      val argsspan = Span.merge(args, fspan)
      val arg = freshId(e, argsspan, ARGS_NAME)
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
          IRArgs(e, arg, newargs.map(p => Some(p))),
          getBase(v, fun, fir),
          IRCall(e, res, obj, fun, arg)
        ), res)

    case FunApp(_, b @ Bracket(i, first, index), args) =>
      val firstspan = first.span
      val objspan = i.span
      val obj1 = freshId(first, firstspan, "obj1")
      val field1 = freshId(index, index.span, "field1")
      val obj = freshId(e, objspan, "obj")
      val fun = freshId(e, objspan, "fun")
      val argsspan = Span.merge(args, b.span)
      val arg = freshId(e, argsspan, ARGS_NAME)
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
          IRArgs(e, arg, newargs.map(p => Some(p))),
          toObject(b, fun, IRLoad(e, obj, rr)),
          IRCall(e, res, fun, obj, arg)
        ), res)

    case FunApp(_, fun, args) =>
      val fspan = fun.span
      val obj1 = freshId(fun, fspan, "obj1")
      val obj = freshId(fun, fspan, "obj")
      val argsspan = Span.merge(args, fspan)
      val arg = freshId(e, argsspan, ARGS_NAME)
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
          IRArgs(e, arg, newargs.map(p => Some(p))),
          IRCall(e, res, obj, GLOBAL_TMP_ID, arg)
        ), res)

    case (t: This) => (List(), IRThis(t))

    case (n: Null) => (List(), IRVal(EJSNull))

    case b @ Bool(_, isBool) =>
      (List(),
        if (isBool) IRVal(true) else IRVal(false))

    case DoubleLiteral(_, text, num) =>
      (List(), IRVal(text, num))

    case IntLiteral(_, intVal, radix) =>
      (List(), IRVal(intVal.toString, intVal.doubleValue))

    case s @ StringLiteral(_, _, str, isRE) =>
      (List(), IRVal(if (isRE) str else unescapeJava(str, s)))
  }

  private def prop2ir(prop: Property): IRId = prop match {
    case PropId(_, id) => makeNGId(id.text, prop)
    case PropStr(_, str) => makeTId(true, prop, str)
    case PropNum(_, DoubleLiteral(_, t, _)) => makeTId(true, prop, t)
    case PropNum(_, IntLiteral(_, i, _)) => makeTId(true, prop, i.toString)
  }
  /*
   * AST2IR_M : Member -> Env -> IRId -> List[IRStmt] * IRMember
   */
  private def walkMember(m: Member, env: Env, res: IRId): (List[IRStmt], IRMember) = {
    m match {
      case Field(_, prop, expr) =>
        val (ss, r) = walkExpr(expr, env, res)
        (ss, IRField(m, prop2ir(prop), r))
      case GetProp(_, prop, f @ Functional(_, fds, vds, body, name, params, _)) =>
        val (newName, newParams, args, newFds, newVds, newBody) =
          functional(prop.toId, params, fds, vds, body, env, None, true)
        (
          List(),
          IRGetProp(
            m,
            IRFunctional(f, true,
              newName, newParams, args, newFds, newVds, newBody)
          )
        )
      case SetProp(_, prop, f @ Functional(_, fds, vds, body, name, params, _)) =>
        val (newName, newParams, args, newFds, newVds, newBody) =
          functional(prop.toId, params, fds, vds, body, env, None, true)
        (
          List(),
          IRSetProp(
            m,
            IRFunctional(f, true,
              newName, newParams, args, newFds, newVds, newBody)
          )
        )
    }
  }

  type CaseEnv = List[(Option[Expr], IRId)]
  private def addCE(env: CaseEnv, x: Option[Expr], xid: IRId): CaseEnv = (x, xid) :: env
  private def addRightCE(env: CaseEnv, xid: IRId): CaseEnv = env ++ List((None, xid))
  /*
   * AST2IR_CC : List[Case] * Option[List[Stmt]] * List[Case] -> Env -> List[Option[Expr] * IRId] -> IRStmt
   */
  private def walkCase(ast: ASTNode, switchSpan: Span, backCases: List[Case], defCase: Option[List[Stmt]],
    frontCases: List[Case], env: Env, caseEnv: CaseEnv): IRStmt =
    (backCases, defCase, frontCases) match {
      case (head :: tail, _, _) =>
        val Case(info, condExpr, body) = head
        // span is currently set to the head statement of the default case
        val span = info.span
        val newLabel = freshId(condExpr, span, "Case2Label")
        IRSeq(
          head,
          IRLabelStmt(head, newLabel,
            walkCase(ast, switchSpan, tail, defCase, frontCases, env,
              addCE(caseEnv, Some(condExpr), newLabel))),
          IRStmtUnit(head, body.map(walkStmt(_, env)))
        )
      case (Nil, Some(stmt), _) =>
        // span is currently set to the default cases
        val span = if (stmt.isEmpty) switchSpan else stmt.head.span
        val newLabel = freshId(ast, Span.merge(stmt, span), "default")
        IRSeq(
          ast,
          IRLabelStmt(ast, newLabel,
            walkCase(ast, switchSpan, List(), None, frontCases, env,
              addRightCE(caseEnv, newLabel))),
          if (stmt.isEmpty) IRSeq(ast)
          else IRSeq(ast, stmt.map(walkStmt(_, env)))
        )
      case (Nil, None, head :: tail) =>
        val Case(info, condExpr, body) = head
        // span is currently set to the head statement of the default case
        val span = info.span
        val newLabel = freshId(condExpr, head.span, "Case1Label")
        IRSeq(
          head,
          IRLabelStmt(head, newLabel,
            walkCase(ast, switchSpan, List(), None, tail, env,
              addCE(caseEnv, Some(condExpr), newLabel))),
          IRStmtUnit(head, body.map(walkStmt(_, env)))
        )
      case (Nil, None, Nil) =>
        IRSeq(
          ast,
          walkScond(ast, switchSpan, caseEnv, env),
          IRBreak(ast, getE(env, BREAK_NAME))
        )
    }

  /*
   * AST2IR_SC : List[Option[Expr] * IRId] -> Env -> IRStmt
   */
  private def walkScond(ast: ASTNode, switchSpan: Span, caseEnv: CaseEnv, env: Env): IRStmt =
    caseEnv match {
      case (Some(expr), label) :: tail =>
        val span = expr.span // span is a position of the expression
        val cond = freshId(expr, expr.span, COND_NAME)
        val (ss, r) = walkExpr(expr, env, cond)
        val comp = IRBin(expr, getE(env, VAL_NAME), STRICT_EQ, r)
        IRSeq(expr, ss :+ IRIf(expr, comp, IRBreak(expr, label),
          Some(walkScond(ast, switchSpan, tail, env))))
      case List((None, label)) => IRBreak(ast, label)
      case _ => IRSeq(ast)
    }

  /*
   * AST2IR_LVAL : Expr -> Env -> List[IRStmt] -> IRExpr -> boolean -> List[IRStmt] * IRExpr
   */
  private def walkLval(ast: ASTNode, lhs: Expr, env: Env, stmts: List[IRStmt], e: IRExpr,
    keepOld: Boolean): (List[IRStmt], IRExpr) = lhs match {
    case Parenthesized(_, expr) =>
      walkLval(ast, expr, env, stmts, e, keepOld)
    case VarRef(_, id) =>
      if (DEBUG) println("  id=" + id.text + " " + id.uniqueName)
      val irid = id2ir(env, id)
      if (DEBUG) println("VarRef: irid=" + irid.uniqueName)
      if (keepOld)
        (List(mkExprS(ast, getE(env, OLD_NAME), irid)) ++ stmts :+ mkExprS(ast, irid, e), irid)
      else
        (stmts :+ mkExprS(ast, irid, e), irid)
    case dot @ Dot(info, obj, member) =>
      walkLval(ast, Bracket(info, obj,
        StringLiteral(
          NU.makeASTNodeInfo(member.span),
          "\"", member.text, false
        )),
        env, stmts, e, keepOld)
    case Bracket(info, first, index) =>
      val span = info.span
      val firstspan = first.span
      val obj1 = freshId(first, firstspan, "obj1")
      val field1 = freshId(index, index.span, "field1")
      val obj = freshId(first, firstspan, "obj")
      val (ss1, r1) = walkExpr(first, env, obj1)
      val (ss2, r2) = walkExpr(index, env, field1)
      val front = (ss1 :+ toObject(first, obj, r1)) ++ ss2
      val back = stmts :+ IRStore(ast, obj, r2, e)
      if (keepOld)
        ((front :+ makeLoadStmt(true, lhs, span, getE(env, OLD_NAME), obj, r2)) ++ back,
          IRLoad(lhs, obj, r2))
      else (front ++ back, IRLoad(lhs, obj, r2))
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
        IRExprStmt(lhs, varIgn(lhs), r, false),
        IRExprStmt(lhs, varIgn(lhs), e, false),
        IRExprStmt(lhs, varIgn(lhs), REF_ERROR, false)
      ),
        defaultIRExpr)
  }

  ////////////////////////////////////////////////////////////////
  // calculate results
  ////////////////////////////////////////////////////////////////

  (result, excLog)
}
