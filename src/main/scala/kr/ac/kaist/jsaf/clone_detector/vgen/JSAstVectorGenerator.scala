/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.clone_detector.vgen

import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.SpanInfo
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.nodes_util.CharVector
import kr.ac.kaist.jsaf.clone_detector.util.Util
import scala.collection.JavaConversions._

class JSAstVectorGenerator(program: Program, minT: Int) extends Walker {
  def doit = walk(program)
  var bDebugPrint: Boolean = false
  def DebugPrint(string: String) = if(bDebugPrint) System.out.println(string)

  def isRelevant(node:Any):Boolean = Util.isRelevant(node)
  def isSignificant(node:Any):Boolean = Util.isSignificant(node)

  override def walk(node:Any):java.util.Vector[Int] = node match {
    case SAnonymousFnName(info) =>
      DebugPrint("SAnonymousFnName")
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SAnonymousFnName" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector
      
    case SArrayExpr(info, elements) =>
      DebugPrint("SArrayExpr")
      info.getSpan.getCharVector.merge(walk(elements))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SArrayExpr" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SAssignOpApp(info, lhs, op, right) =>
      DebugPrint("SAssignOpApp")
      info.getSpan.getCharVector.merge(walk(lhs))
      walk(op)
      info.getSpan.getCharVector.merge(walk(right))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SAssignOpApp" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SBlock(info, stmts, _) =>
      DebugPrint("SBlock")
      info.getSpan.getCharVector.merge(walk(stmts))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SBlock" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SBool(info, isBool) =>
      DebugPrint("SBool")
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SBool" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SBracket(info, obj, index) =>
      DebugPrint("SBracket")
      info.getSpan.getCharVector.merge(walk(obj))
      info.getSpan.getCharVector.merge(walk(index))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SBracket" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SBreak(info, target) =>
      DebugPrint("SBreak")
      info.getSpan.getCharVector.merge(walk(target))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SBreak" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SCase(info, cond, body) =>
      DebugPrint("SCase")
      info.getSpan.getCharVector.merge(walk(cond))
      walk(body)
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SCase" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SCatch(info, id, body) =>
      DebugPrint("SCatch")
      info.getSpan.getCharVector.merge(walk(id))
      info.getSpan.getCharVector.merge(walk(body))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SCatch" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SCond(info, cond, trueBranch, falseBranch) =>
      DebugPrint("SCond")
      info.getSpan.getCharVector.merge(walk(cond))
      info.getSpan.getCharVector.merge(walk(trueBranch))
      info.getSpan.getCharVector.merge(walk(falseBranch))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SCond" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SContinue(info, target) =>
      DebugPrint("SContinue")
      info.getSpan.getCharVector.merge(walk(target))
      if (isRelevant(node))
        info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SContinue" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SDebugger(info) =>
      DebugPrint("SDebugger")
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SDebugger" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SDoWhile(info, body, cond) =>
      DebugPrint("SDoWhile")
      info.getSpan.getCharVector.merge(walk(body))
      info.getSpan.getCharVector.merge(walk(cond))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SDoWhile" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SDot(info, obj, member) =>
      DebugPrint("SDot")
      info.getSpan.getCharVector.merge(walk(obj))
      info.getSpan.getCharVector.merge(walk(member))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SDot" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SDoubleLiteral(info, text, num) =>
      DebugPrint("SDoubleLiteral")
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SDoubleLiteral" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector
      
    case SEmptyStmt(info) =>
      DebugPrint("SEmptyStmt")
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SEmptyStmt" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SExprList(info, exprs) =>
      DebugPrint("SExprList")
      info.getSpan.getCharVector.merge(walk(exprs))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SExprList" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SExprStmt(info, expr, isInternal) =>
      DebugPrint("SExprStmt")
      info.getSpan.getCharVector.merge(walk(expr))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SExprStmt" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SField(info, prop, expr) =>
      DebugPrint("SField")
      info.getSpan.getCharVector.merge(walk(prop))
      info.getSpan.getCharVector.merge(walk(expr))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SField" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SFor(info, init, cond, action, body) =>
      DebugPrint("SFor")
      info.getSpan.getCharVector.merge(walk(init))
      info.getSpan.getCharVector.merge(walk(cond))
      info.getSpan.getCharVector.merge(walk(action))
      info.getSpan.getCharVector.merge(walk(body))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SFor" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SForIn(info, lhs, expr, body) =>
      DebugPrint("SForIn")
      info.getSpan.getCharVector.merge(walk(lhs))
      info.getSpan.getCharVector.merge(walk(expr))
      info.getSpan.getCharVector.merge(walk(body))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SForIn" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SForVar(info, vars, cond, action, body) =>
      DebugPrint("SForVar")
      info.getSpan.getCharVector.merge(walk(vars))
      info.getSpan.getCharVector.merge(walk(cond))
      info.getSpan.getCharVector.merge(walk(action))
      info.getSpan.getCharVector.merge(walk(body))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SForVar" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SForVarIn(info, varjs, expr, body) =>
      DebugPrint("SForVarIn")
      info.getSpan.getCharVector.merge(walk(varjs))
      info.getSpan.getCharVector.merge(walk(expr))
      info.getSpan.getCharVector.merge(walk(body))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SForVarIn" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SFunApp(info, fun, args) =>
      DebugPrint("SFunApp")
      info.getSpan.getCharVector.merge(walk(fun))
      info.getSpan.getCharVector.merge(walk(args))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SFunApp" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SFunDecl(info, ftn, _) =>
      DebugPrint("SFunDecl")
      info.getSpan.getCharVector.merge(walk(ftn.getName))
      info.getSpan.getCharVector.merge(walk(ftn))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SFunDecl" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SFunExpr(info, ftn) =>
      DebugPrint("SFunExpr")
      info.getSpan.getCharVector.merge(walk(ftn.getName))
      info.getSpan.getCharVector.merge(walk(ftn))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SFunExpr" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SFunctional(vds, fds, pgm, name, params) =>
      DebugPrint("SFunctional")
      var v = new CharVector
      vds.foreach(vd => v.merge(walk(vd)))
      fds.foreach(fd => v.merge(walk(fd)))
      v.merge(walk(pgm))
      params.foreach(p => v.merge(walk(p)))
      if (isRelevant(node)) v.addAt(Util.name2id(node))
      if (isSignificant(node) && v.containsEnoughTokens(minT))
        v.setMergeable
      DebugPrint(v.toString + "SFunctional" + " " + v.getNumOfTokens + " " + v.isMergeable)
      v.getVector

    case SGetProp(info, prop, ftn) =>
      DebugPrint("SGetProp")
      info.getSpan.getCharVector.merge(walk(prop))
      info.getSpan.getCharVector.merge(walk(ftn))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SGetProp" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SId(info, text, uniqueName, _) =>
      DebugPrint("SId \"" + text + "\"")
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SId" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SIf(info, cond, trueBranch, falseBranch) =>
      DebugPrint("SIf")
      info.getSpan.getCharVector.merge(walk(cond))
      info.getSpan.getCharVector.merge(walk(trueBranch))
      info.getSpan.getCharVector.merge(walk(falseBranch))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SIf" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SInfixOpApp(info, left, op, right) =>
      DebugPrint("SInfixOpApp")
      var v = new CharVector
      v.merge(walk(left))
      v.merge(walk(op))
      v.merge(walk(right))
      if (isRelevant(node)) v.addAt(Util.name2id(node))
      if (isSignificant(node) && v.containsEnoughTokens(minT))
        v.setMergeable  
      DebugPrint(v.toString + "SInfixOpApp" + " " + v.getNumOfTokens + " " + v.isMergeable)  
      v.getVector

    case SIntLiteral(info, intVal, radix) =>
      DebugPrint("SIntLiteral")
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SIntLiteral" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SLabel(info, id) =>
      DebugPrint("SLabel")
      info.getSpan.getCharVector.merge(walk(id))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SLabel" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SLabelStmt(info, label, stmt) =>
      DebugPrint("SLabelStmt")
      walk(label)
      info.getSpan.getCharVector.merge(walk(stmt))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SLabelStmt" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SNew(info, lhs) =>
      DebugPrint("SNew")
      walk(lhs)
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SNew" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SNull(info) =>
      DebugPrint("SNull")
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SNull" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SObjectExpr(info, members) =>
      DebugPrint("SObjectExpr")
      info.getSpan.getCharVector.merge(walk(members))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SObjectExpr" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SOp(info, text) =>
      DebugPrint("SOp")
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SOp" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SParenthesized(info, expr) =>
      DebugPrint("SParenthesized")
      info.getSpan.getCharVector.merge(walk(expr))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SParenthesized" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SPrefixOpApp(info, op, right) =>
      DebugPrint("SPrefixOpApp")
      walk(op)
      info.getSpan.getCharVector.merge(walk(right))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SPrefixOpApp" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SProgram(info, program) =>
      DebugPrint("SProgram")
      info.getSpan.getCharVector.merge(walk(program))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SProgram" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SPropId(info, id) =>
      DebugPrint("SPropId")
      info.getSpan.getCharVector.merge(walk(id))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SPropId" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SPropNum(info, num) =>
      DebugPrint("SPropNum")
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SPropNum" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SPropStr(info, str) =>
      DebugPrint("SPropStr")
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SPropStr" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SRegularExpression(info, body, flags) =>
      DebugPrint("SRegularExpression")
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SRegularExpression" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SReturn(info, expr) =>
      DebugPrint("SReturn")
      info.getSpan.getCharVector.merge(walk(expr))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SReturn" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SSetProp(info, prop, ftn) =>
      DebugPrint("SSetProp")
      info.getSpan.getCharVector.merge(walk(prop))
      info.getSpan.getCharVector.merge(walk(ftn))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SSetProp" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case i:SpanInfo =>
      val span = i.getSpan
      DebugPrint("SpanInfo")
      span.getCharVector.merge(walk(span))
      if (isRelevant(node)) span.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && span.getCharVector.containsEnoughTokens(minT))
        span.getCharVector.setMergeable
      DebugPrint(span.getCharVector.toString + "SpanInfo" + " " + span.getCharVector.isMergeable)
      span.getCharVector.getVector
      
    case SSourceElements(info, body, _) =>
      DebugPrint("SSourceElements")
      var v = new CharVector
      body.foreach(s => v.merge(walk(s)))
      v.getVector

    case SStringLiteral(info, quote, txt) =>
      val str = NU.unescapeJava(txt)
      DebugPrint("SStringLiteral \"" + str + "\"")
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SStringLiteral" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SSwitch(info, cond, frontCases, defjs, backCases) =>
      DebugPrint("SSwitch")
      info.getSpan.getCharVector.merge(walk(cond))
      info.getSpan.getCharVector.merge(walk(frontCases))
      info.getSpan.getCharVector.merge(walk(defjs))
      info.getSpan.getCharVector.merge(walk(backCases))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SSwitch" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SThis(info) =>
      DebugPrint("SThis")
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SThis" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SThrow(info, expr) =>
      DebugPrint("SThrow")
      info.getSpan.getCharVector.merge(walk(expr))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SThrow" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case STopLevel(vds, fds, stmts) =>
      DebugPrint("STopLevel")
      var v = new CharVector
      vds.foreach(vd => v.merge(walk(vd)))
      fds.foreach(fd => v.merge(walk(fd)))
      stmts.foreach(s => v.merge(walk(s)))
      DebugPrint(v.toString + "STopLevel" + " " + v.isMergeable)
      v.getVector

    case STry(info, body, catchBlock, fin) =>
      DebugPrint("STry")
      info.getSpan.getCharVector.merge(walk(body))
      info.getSpan.getCharVector.merge(walk(catchBlock))
      info.getSpan.getCharVector.merge(walk(fin))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "STry" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SUnaryAssignOpApp(info, lhs, op) =>
      DebugPrint("SUnaryAssignOpApp")
      info.getSpan.getCharVector.merge(walk(lhs))
      walk(op)
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SUnaryAssignOpApp" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SVarDecl(info, name, expr, _) =>
      DebugPrint("SVarDecl")
      info.getSpan.getCharVector.merge(walk(name))
      info.getSpan.getCharVector.merge(walk(expr))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SVarDecl" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SVarRef(info, id) =>
      DebugPrint("SVarRef")
      info.getSpan.getCharVector.merge(walk(id))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SVarRef" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SVarStmt(info, vds) =>
      DebugPrint("SVarStmt")
      vds.foreach(vd => info.getSpan.getCharVector.merge(walk(vd)))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SVarStmt" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SWhile(info, cond, body) =>
      DebugPrint("SWhile")
      info.getSpan.getCharVector.merge(walk(cond))
      info.getSpan.getCharVector.merge(walk(body))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SWhile" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case SWith(info, expr, stmt) =>
      DebugPrint("SWith")
      info.getSpan.getCharVector.merge(walk(expr))
      info.getSpan.getCharVector.merge(walk(stmt))
      if (isRelevant(node)) info.getSpan.getCharVector.addAt(Util.name2id(node))
      if (isSignificant(node) && info.getSpan.getCharVector.containsEnoughTokens(minT))
        info.getSpan.getCharVector.setMergeable
      DebugPrint(info.getSpan.getCharVector.toString + "SWith" + " " + info.getSpan.getCharVector.getNumOfTokens + " " + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.getVector

    case xs:List[_] =>
      var v = new CharVector
      xs.foreach(x => v.merge(walk(x)))
      v.getVector

    case xs:Option[_] =>
      var v = new CharVector
      xs.foreach(x => v.merge(walk(x)))
      v.getVector

    case _ =>
      DebugPrint("Default")
      val v = new java.util.Vector[Int]
      v
  }
}
