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

package kr.ac.kaist.safe.cfg_builder

import scala.collection.immutable.HashSet
import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.errors.warning._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.SafeConfig
import kr.ac.kaist.safe.phase.CFGBuildConfig

// Collects captured variables in a given IRNode
// Used by compiler/DefaultCFGBuilder.scala
class CapturedVariableCollector(
    irRoot: IRRoot,
    safeConfig: SafeConfig,
    config: CFGBuildConfig
) {
  ////////////////////////////////////////////////////////////////
  // results
  ////////////////////////////////////////////////////////////////

  lazy val result: CapturedNames = CollectCaptVarWalker.walk(irRoot)
  lazy val excLog: ExcLog = new ExcLog

  ////////////////////////////////////////////////////////////////
  // private mutable
  ////////////////////////////////////////////////////////////////

  private type LocalNames = Set[String]
  private type CapturedNames = Set[String]
  private val EMPTY: Set[String] = HashSet()

  ////////////////////////////////////////////////////////////////
  // helper function
  ////////////////////////////////////////////////////////////////

  private object CollectCaptVarWalker extends IRGeneralWalker[CapturedNames] {
    // join definition
    def join(args: CapturedNames*): CapturedNames = args.foldLeft(EMPTY)(_ ++ _)

    // IRRoot
    override def walk(ir: IRRoot): CapturedNames = ir match {
      case IRRoot(_, fds, _, stmts) =>
        walk(fds, walk(_: IRFunDecl)) ++ walk(stmts, walk(_: IRStmt, EMPTY))
    }

    // IRFunctional
    override def walk(func: IRFunctional): CapturedNames = func match {
      case IRFunctional(_, _, name, params, args, fds, vds, body) =>
        // flatten IRSeq in IRStmt list
        def flatten(stmts: List[IRStmt]): List[IRStmt] = {
          stmts.foldRight(List[IRStmt]()) {
            case (seq: IRSeq, l) => seq.stmts ++ l
            case (s, l) => s :: l
          }
        }

        // get names of arguments
        def namesOfArgs(loads: List[IRStmt]): LocalNames = {
          flatten(loads).foldLeft(EMPTY) {
            //case (set, IRExprStmt(_, id, _, _)) => id match {
            case (set, IRExprStmt(_, id, _, _)) => id match {
              case IRUserId(_, _, name, _, _) => set + name
              case IRTmpId(_, orig, name, _) if orig.startsWith("<>arguments") =>
                set + name
              case _ => set
            }
            case (set, _) => set
          }
        }

        // get names of function declaration
        def namesOfFunDecls(fds: List[IRFunDecl]): LocalNames =
          fds.map(_.ftn.name.uniqueName).toSet

        // get names of variables
        def namesOfVars(vds: List[IRVarStmt]): LocalNames =
          vds.map(_.lhs.uniqueName).toSet

        val locals = namesOfArgs(args) ++ namesOfFunDecls(fds) ++ namesOfVars(vds)
        walk(fds, walk(_: IRFunDecl)) ++ walk(body, walk(_: IRStmt, locals))
    }

    // IRStmt
    def walk(stmt: IRStmt, locals: LocalNames): CapturedNames = stmt match {
      case IRNoOp(_, _) => EMPTY
      case IRStmtUnit(_, stmts) => walk(stmts, walk(_: IRStmt, locals))
      case IRSeq(_, stmts) => walk(stmts, walk(_: IRStmt, locals))
      case (vd: IRVarStmt) =>
        excLog.signal(NotHoistedError(vd)); EMPTY
      case (fd: IRFunDecl) =>
        excLog.signal(NotHoistedError(fd)); EMPTY
      case IRFunExpr(_, lhs, func) => walk(lhs, locals) ++ walk(func)
      case IRObject(_, lhs, members, proto) =>
        walk(lhs, locals) ++
          walk(members, walk(_: IRMember, locals)) ++
          walk(proto, walk(_: IRId, locals))
      case irTry @ IRTry(_, body, name, catchB, finallyB) =>
        walk(body, locals) ++
          ((name, catchB) match {
            case (Some(x), Some(stmt)) => walk(stmt, locals + x.uniqueName)
            case (None, None) => EMPTY
            case _ => excLog.signal(WrongTryStmtError(irTry)); EMPTY
          }) ++
          walk(finallyB, walk(_: IRStmt, locals))
      case IRArgs(_, lhs, elements) =>
        walk(lhs, locals) ++
          walk(elements, walk(_: Option[IRExpr], walk(_: IRExpr, locals)))
      case IRArray(_, lhs, elements) =>
        walk(lhs, locals) ++
          walk(elements, walk(_: Option[IRExpr], walk(_: IRExpr, locals)))
      case IRArrayNumber(_, lhs, _) => walk(lhs, locals)
      case IRBreak(_, label) => EMPTY
      case IRInternalCall(_, lhs, fun, arg1, arg2) =>
        walk(lhs, locals) ++
          walk(fun, locals) ++
          walk(arg1, locals) ++
          walk(arg2, walk(_: IRId, locals))
      case IRCall(_, lhs, fun, thisB, args) =>
        walk(lhs, locals) ++
          walk(fun, locals) ++
          walk(thisB, locals) ++
          walk(args, locals)
      case IRNew(_, lhs, cons, args) if (args.length == 2) =>
        walk(lhs, locals) ++
          walk(cons, locals) ++
          walk(args(0), locals) ++
          walk(args(1), locals)
      case c @ IRNew(_, lhs, fun, args) =>
        excLog.signal(NewArgNumError(c)); EMPTY
      case IRDelete(_, lhs, id) =>
        walk(lhs, locals) ++ walk(id, locals)
      case IRDeleteProp(_, lhs, obj, index) =>
        walk(lhs, locals) ++
          walk(obj, locals) ++
          walk(index, locals)
      case IRExprStmt(_, lhs, expr, _) =>
        walk(lhs, locals) ++ walk(expr, locals)
      case IRIf(_, cond, trueblock, falseblock) =>
        walk(cond, locals) ++
          walk(trueblock, locals) ++
          walk(falseblock, walk(_: IRStmt, locals))
      case IRLabelStmt(_, label, stmt) => walk(stmt, locals)
      case IRReturn(_, expr) => walk(expr, walk(_: IRExpr, locals))
      case IRStore(_, obj, index, rhs) =>
        walk(obj, locals) ++
          walk(index, locals) ++
          walk(rhs, locals)
      case IRThrow(_, expr) => walk(expr, locals)
      case IRWhile(_, cond, body) =>
        walk(cond, locals) ++ walk(body, locals)
      case _ =>
        if (safeConfig.verbose || config.verbose) excLog.signal(IRIgnored(stmt))
        EMPTY
    }

    // IRMember
    def walk(mem: IRMember, locals: LocalNames): CapturedNames = mem match {
      case IRField(_, prop, expr) => walk(expr, locals)
      case getOrSet => excLog.signal(NotSupportedIRError(getOrSet)); EMPTY
    }

    // IRExpr
    def walk(expr: IRExpr, locals: LocalNames): CapturedNames = expr match {
      case IRLoad(_, obj, index) =>
        walk(obj, locals) ++ walk(index, locals)
      case IRBin(_, first, _, second) =>
        walk(first, locals) ++ walk(second, locals)
      case IRUn(_, _, expr) => walk(expr, locals)
      case (id: IRId) => walk(id, locals)
      case _ => EMPTY
    }

    // IRId
    def walk(id: IRId, locals: LocalNames): CapturedNames = id match {
      case IRUserId(_, _, name, false, _) if (!locals.contains(name)) =>
        HashSet(name)
      case _ => EMPTY
    }

    // generic list type
    def walk[T](list: List[T], walk: T => CapturedNames): CapturedNames =
      list.foldLeft(EMPTY) { _ ++ walk(_) }

    // generic option type
    def walk[T](opt: Option[T], walk: T => CapturedNames): CapturedNames =
      opt.fold(EMPTY) { walk(_) }
  }

  ////////////////////////////////////////////////////////////////
  // calculate results
  ////////////////////////////////////////////////////////////////

  (result, excLog)
}
