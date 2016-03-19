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

import scala.collection.mutable.{ Set => MSet }
import scala.collection.mutable.{ HashSet => MHashSet }
import scala.collection.immutable.HashSet
import kr.ac.kaist.safe.errors.StaticError
import kr.ac.kaist.safe.errors.ErrorLog
import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.Config

class CapturedVariableCollector(ir: IRRoot, config: Config) {
  /* Error handling
   * The signal function collects errors during the disambiguation phase.
   * To collect multiple errors,
   * we should return a dummy value after signaling an error.
   */
  val errors: ErrorLog = new ErrorLog
  def signal(msg: String, node: Node): Unit = errors.signal(msg, node)
  def signal(node: Node, msg: String): Unit = errors.signal(msg, node)
  def signal(error: StaticError): Unit = errors.signal(error)
  def getErrors(): List[StaticError] = errors.errors

  val captured: MSet[String] = MHashSet()

  def collect: Set[String] = {
    errors.errors = Nil
    captured.clear
    ir match {
      case IRRoot(info, fds, vds, stmts) =>
        fds.foreach(checkFunDecl)
        checkStmts(stmts, HashSet[String]())
    }
    captured.toSet
  }

  private def checkId(id: IRId, locals: Set[String]): Unit = id match {
    case IRUserId(_, _, uniqueName, false, _) =>
      if (!locals.contains(uniqueName)) {
        captured.add(uniqueName)
      }
    case IRUserId(_, _, _, true, _) => ()
    case IRTmpId(_, _, _, _) => ()
  }

  private def checkFunDecl(fd: IRFunDecl): Unit = fd match {
    case IRFunDecl(_, func) => checkFunctional(func)
  }

  private def checkFunctional(func: IRFunctional): Unit = func match {
    case IRFunctional(_, _, name, params, args, fds, vds, body) =>
      val locals = namesOfArgs(args) ++ namesOfFunDecls(fds) ++ namesOfVars(vds)
      fds.foreach(checkFunDecl)
      checkStmts(body, locals)
  }

  private def namesOfFunDecls(fds: List[IRFunDecl]): Set[String] = {
    fds.foldLeft(HashSet[String]())((set, fd) => set + fd.ftn.name.uniqueName)
  }

  // flatten IRSeq
  private def flatten(stmts: List[IRStmt]): List[IRStmt] =
    stmts.foldRight(List[IRStmt]())((s, l) =>
      if (s.isInstanceOf[IRSeq])
        s.asInstanceOf[IRSeq].stmts ++ l
      else List(s) ++ l)

  private def namesOfArgs(loads: List[IRStmt]): Set[String] = {
    // When arguments may not be a list of IRExprStmts
    // because of using compiler.IRSimplifier
    // to move IRBin, IRUn, and IRLoad out of IRExpr
    /*
    loads.asInstanceOf[List[IRExprStmt]].foldLeft(HashSet[String]())((set, load) => 
      set + load.lhs.uniqueName)
     */
    flatten(loads).foldLeft(HashSet[String]())((set, load) =>
      if (load.isInstanceOf[IRExprStmt]) {
        val name = load.asInstanceOf[IRExprStmt].lhs
        if (name.isInstanceOf[IRUserId] ||
          name.originalName.startsWith("<>arguments"))
          set + load.asInstanceOf[IRExprStmt].lhs.uniqueName
        else set
      } else set)
  }

  private def namesOfVars(vds: List[IRVarStmt]): Set[String] = {
    vds.foldLeft(HashSet[String]())((set, vd) => set + vd.lhs.uniqueName)
  }

  private def checkStmts(stmts: List[IRStmt], locals: Set[String]): Unit = {
    stmts.foreach(stmt => checkStmt(stmt, locals))
  }

  private def checkStmt(stmt: IRStmt, locals: Set[String]): Unit = stmt match {
    case IRNoOp(irinfo, desc) => ()
    case IRStmtUnit(irinfo, stmts) => checkStmts(stmts, locals)
    case IRSeq(irinfo, stmts) => checkStmts(stmts, locals)

    case vd: IRVarStmt =>
      signal("IRVarStmt should have been hoisted.", vd)

    case fd: IRFunDecl =>
      signal("IRFunDecl should have been hoisted.", fd)

    case IRFunExpr(irinfo, lhs, func) =>
      checkId(lhs, locals)
      checkFunctional(func)

    case IRObject(irinfo, lhs, members, proto) =>
      checkId(lhs, locals)
      members.foreach((m) => checkMember(m, locals))
      proto match {
        case Some(p) => checkId(p, locals)
        case None => ()
      }

    case IRTry(irinfo, body, name, catchB, finallyB) =>
      checkStmt(body, locals)
      (name, catchB) match {
        case (Some(x), Some(stmt)) => checkStmt(stmt, locals + x.uniqueName)
        case (None, None) => ()
        case _ => signal("Wrong IRTryStmt.", stmt)
      }
      finallyB match {
        case Some(stmt) => checkStmt(stmt, locals)
        case None => ()
      }

    case IRArgs(irinfo, lhs, elements) =>
      checkId(lhs, locals)
      checkExprOptList(elements, locals)

    case IRArray(irinfo, lhs, elements) =>
      checkId(lhs, locals)
      checkExprOptList(elements, locals)

    case IRArrayNumber(irinfo, lhs, elements) => checkId(lhs, locals)
    case IRBreak(irinfo, label) => ()

    case IRInternalCall(irinfo, lhs, fun, arg1, arg2) =>
      checkId(lhs, locals)
      checkId(fun, locals)
      checkExpr(arg1, locals)
      checkExprOpt(arg2, locals)

    case IRCall(irinfo, lhs, fun, thisB, args) =>
      checkId(lhs, locals)
      checkId(fun, locals)
      checkId(thisB, locals)
      checkId(args, locals)

    case IRNew(irinfo, lhs, cons, args) if (args.length == 2) =>
      checkId(lhs, locals)
      checkId(cons, locals)
      checkId(args(0), locals)
      checkId(args(1), locals)

    case c @ IRNew(irinfo, lhs, fun, args) =>
      signal("IRNew should have two elements in args.", c)

    case IRDelete(irinfo, lhs, id) =>
      checkId(lhs, locals)
      checkId(id, locals)

    case IRDeleteProp(irinfo, lhs, obj, index) =>
      checkId(lhs, locals)
      checkId(obj, locals)
      checkExpr(index, locals)

    case IRExprStmt(irinfo, lhs, expr, _) =>
      checkId(lhs, locals)
      checkExpr(expr, locals)

    case IRIf(irinfo, cond, trueblock, falseblock) =>
      checkExpr(cond, locals)
      checkStmt(trueblock, locals)
      falseblock match {
        case Some(block) => checkStmt(block, locals)
        case None => ()
      }

    case IRLabelStmt(irinfo, label, stmt) => checkStmt(stmt, locals)
    case IRReturn(irinfo, expr) => checkExprOpt(expr, locals)

    case IRStore(irinfo, obj, index, rhs) =>
      checkId(obj, locals)
      checkExpr(index, locals)
      checkExpr(rhs, locals)

    case IRThrow(irinfo, expr) => checkExpr(expr, locals)

    case IRWhile(irinfo, cond, body) =>
      checkExpr(cond, locals)
      checkStmt(body, locals)

    case _ => {
      if (config.opt_Verbose) Console.err.println("* Warning: following IR statement is ignored: " + stmt)
    }
  }

  private def checkMember(mem: IRMember, locals: Set[String]): Unit = {
    mem match {
      case IRField(irinfo, prop, expr) => checkExpr(expr, locals)
      case getOrSet =>
        signal("IRGetProp, IRSetProp is not supported.", getOrSet)
        Unit
    }
  }

  private def checkExprOptList(list: List[Option[IRExpr]], locals: Set[String]): Unit = {
    list.foreach(exprOpt => checkExprOpt(exprOpt, locals))
  }

  private def checkExprOpt(exprOpt: Option[IRExpr], locals: Set[String]): Unit = exprOpt match {
    case Some(expr) => checkExpr(expr, locals)
    case None => ()
  }

  private def checkExpr(expr: IRExpr, locals: Set[String]): Unit = expr match {
    case IRLoad(_, obj, index) =>
      checkId(obj, locals)
      checkExpr(index, locals)

    case IRBin(_, first, op, second) =>
      checkExpr(first, locals)
      checkExpr(second, locals)

    case IRUn(_, op, expr) => checkExpr(expr, locals)
    case id: IRId => checkId(id, locals)
    case _: IRThis => ()
    case _: IRNumber => ()
    case _: IRString => ()
    case _: IRBool => ()
    case _: IRNull => ()
  }
}
