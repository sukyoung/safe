/*******************************************************************************
    Copyright (c) 2012-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.cfg

import _root_.java.util.{List => JList}
import scala.collection.mutable.{Set => MSet}
import scala.collection.mutable.{HashSet => MHashSet}
import scala.collection.immutable.HashSet
import kr.ac.kaist.jsaf.useful.HasAt
import kr.ac.kaist.jsaf.exceptions.StaticError
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.ErrorLog
import kr.ac.kaist.jsaf.scala_src.useful.Lists._

class CapturedVariableCollector {
  /* Error handling
   * The signal function collects errors during the disambiguation phase.
   * To collect multiple errors,
   * we should return a dummy value after signaling an error.
   */
  val errors: ErrorLog = new ErrorLog
  def signal(msg:String, hasAt:HasAt) = errors.signal(msg, hasAt)
  def signal(hasAt:HasAt, msg:String) = errors.signal(msg, hasAt)
  def signal(error: StaticError) = errors.signal(error)
  def getErrors(): JList[StaticError] = toJavaList(errors.errors)

  val captured: MSet[String] = MHashSet()
  
  def collect(ir: IRRoot): MSet[String] = {
    ir match {
      case SIRRoot(info, fds, vds, stmts) => 
        fds.foreach(checkFunDecl)
        checkStmts(stmts, HashSet[String]())
    }
    captured
  }
    
  private def checkId(id: IRId, locals: Set[String]) = id match {
    case SIRUserId(_, _, uniqueName, false, _) =>
      if (!locals.contains(uniqueName)) {
        captured.add(uniqueName) 
      }
    case SIRUserId(_, _, _, true, _) => ()
    case SIRTmpId(_, _, _, _) => ()
  }
  
  private def checkFunDecl(fd: IRFunDecl): Unit = fd match {
    case SIRFunDecl(_, func) => checkFunctional(func)
  }

  private def checkFunctional(func: IRFunctional): Unit = func match {
    case SIRFunctional(_,name,params,args,fds,vds,body) =>
      val locals = namesOfArgs(args) ++ namesOfFunDecls(fds) ++ namesOfVars(vds)
      fds.foreach(checkFunDecl)
      checkStmts(body, locals)
  }
  
  private def namesOfFunDecls(fds: List[IRFunDecl]): Set[String] = {
    fds.foldLeft(HashSet[String]())((set, fd) => set + fd.getFtn.getName.getUniqueName)
  }
  
  // flatten IRSeq
  private def flatten(stmts: List[IRStmt]): List[IRStmt] =
    stmts.foldRight(List[IRStmt]())((s, l) =>
                                    if (s.isInstanceOf[IRSeq])
                                      toList(s.asInstanceOf[IRSeq].getStmts) ++ l
                                    else List(s) ++ l)

  private def namesOfArgs(loads: List[IRStmt]): Set[String] = {
    // for Concolic testing
    // arguments may not be a list of IRExprStmts
    // because concolic testing uses compiler.IRSimplifier
    // to move IRBin, IRUn, and IRLoad out of IRExpr
    /*
    loads.asInstanceOf[List[IRExprStmt]].foldLeft(HashSet[String]())((set, load) => 
      set + load.getLhs.getUniqueName)
     */
    flatten(loads).foldLeft(HashSet[String]())((set, load) =>
                                               if (load.isInstanceOf[IRExprStmt]) {
                                                 val name = load.asInstanceOf[IRExprStmt].getLhs
                                                 if (name.isInstanceOf[IRUserId] ||
                                                     name.getOriginalName.startsWith("<>arguments"))
                                                   set + load.asInstanceOf[IRExprStmt].getLhs.getUniqueName
                                                 else set
                                               } else set)
  }

  private def namesOfVars(vds: List[IRVarStmt]): Set[String] = {
    vds.foldLeft(HashSet[String]())((set, vd) => set + vd.getLhs.getUniqueName)
  }
  
  private def checkStmts(stmts: List[IRStmt], locals: Set[String]): Unit = { 
    stmts.foreach(stmt => checkStmt(stmt, locals))
  }
  
  private def checkStmt(stmt: IRStmt, locals: Set[String]): Unit = stmt match {
    case SIRNoOp(irinfo, desc) => ()
    case SIRStmtUnit(irinfo, stmts) => checkStmts(stmts, locals)
    case SIRSeq(irinfo, stmts) => checkStmts(stmts, locals)

    case vd:IRVarStmt =>
      signal("IRVarStmt should have been hoisted.", vd)
    
    case fd:IRFunDecl =>
      signal("IRFunDecl should have been hoisted.", fd)

    case SIRFunExpr(irinfo, lhs, func) => 
      checkId(lhs, locals)
      checkFunctional(func)

    case SIRObject(irinfo, lhs, members, proto) =>
      checkId(lhs, locals)
      members.foreach((m) => checkMember(m, locals))
      proto match {
        case Some(p) => checkId(p, locals)
        case None => ()
      }

    case SIRTry(irinfo, body, name, catchB, finallyB) =>
      checkStmt(body, locals)
      (name, catchB) match {
        case (Some(x), Some(stmt)) => checkStmt(stmt, locals + x.getUniqueName)
        case (None, None) => ()
        case _ => signal("Wrong IRTryStmt.", stmt)
      }
      finallyB match {
        case Some(stmt) => checkStmt(stmt, locals)
        case None => ()
      }

    case SIRArgs(irinfo, lhs, elements) =>
      checkId(lhs, locals)
      checkExprOptList(elements, locals)

    case SIRArray(irinfo, lhs, elements) =>
      checkId(lhs, locals)
      checkExprOptList(elements, locals)
      
    case SIRArrayNumber(irinfo, lhs, elements) => checkId(lhs, locals)
    case SIRBreak(irinfo, label) => ()

    case SIRInternalCall(irinfo, lhs, fun, arg1, arg2) =>
      checkId(lhs, locals)
      checkId(fun, locals)
      checkExpr(arg1, locals)
      checkExprOpt(arg2, locals)

    case SIRCall(irinfo, lhs, fun, thisB, args) =>
      checkId(lhs, locals)
      checkId(fun, locals)
      checkId(thisB, locals)
      checkId(args, locals)

    case SIRNew(irinfo, lhs, cons, args) if (args.length == 2) =>
      checkId(lhs, locals)
      checkId(cons, locals)
      checkId(args(0), locals)
      checkId(args(1), locals)

    case c@SIRNew(irinfo, lhs, fun, args) =>
      signal("IRNew should have two elements in args.", c)

    case SIRDelete(irinfo, lhs, id) =>
      checkId(lhs, locals)
      checkId(id, locals)

    case SIRDeleteProp(irinfo, lhs, obj, index) =>
      checkId(lhs, locals)
      checkId(obj, locals)
      checkExpr(index, locals)

    case SIRExprStmt(irinfo, lhs, expr, _) =>
      checkId(lhs, locals)
      checkExpr(expr, locals)

    case SIRIf(irinfo, cond, trueblock, falseblock) =>
      checkExpr(cond, locals)
      checkStmt(trueblock, locals)
      falseblock match {
        case Some(block) => checkStmt(block, locals)
        case None => ()
      }

    case SIRLabelStmt(irinfo, label, stmt) => checkStmt(stmt, locals)
    case SIRReturn(irinfo, expr) => checkExprOpt(expr, locals)

    case SIRStore(irinfo, obj, index, rhs) =>
      checkId(obj, locals)
      checkExpr(index, locals)
      checkExpr(rhs, locals)

    case SIRThrow(irinfo, expr) => checkExpr(expr, locals)

    case SIRWhile(irinfo, cond, body) =>
      checkExpr(cond, locals)
      checkStmt(body, locals)

    case _ => {
      System.err.println("* Warning: following IR statement is ignored: "+ stmt)
    }
  }
    
  private def checkMember(mem: IRMember, locals: Set[String]): Unit = {
    mem match {
      case SIRField(irinfo, prop, expr) => checkExpr(expr, locals)
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
    case SIRLoad(_, obj, index) =>
      checkId(obj, locals)
      checkExpr(index, locals)

    case SIRBin(_, first, op, second) =>
      checkExpr(first, locals)
      checkExpr(second, locals)

    case SIRUn(_, op, expr) => checkExpr(expr, locals)
    case id:IRId => checkId(id, locals)
    case _:IRThis => ()
    case _:IRNumber => ()
    case _:IRString => ()
    case _:IRBool => ()
    case _:IRNull => ()
  }
}