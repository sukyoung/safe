/**
 * *****************************************************************************
 * Copyright (c) 2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.clone_detector.vgen

import java.util

import kr.ac.kaist.safe.clone_detector.util.Util
import kr.ac.kaist.safe.nodes.ast._

class JSAstVectorGenerator(program: Program, minT: Int) extends ASTWalker {
  def doit: util.Vector[Int] = walkAST(program)
  var bDebugPrint: Boolean = false
  def DebugPrint(string: String): Unit = if (bDebugPrint) System.out.println(string)

  def isRelevant(node: Any): Boolean = Util.isRelevant(node)
  def isSignificant(node: Any): Boolean = Util.isSignificant(node)

  def walkAST(node: Any): java.util.Vector[Int] = node match {
    case AnonymousFnName(info, text) =>
      DebugPrint("SAnonymousFnName")
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SAnonymousFnName" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case ArrayExpr(info, elements) =>
      DebugPrint("SArrayExpr")
      info.span.cvec.merge(walkAST(elements))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SArrayExpr" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case AssignOpApp(info, lhs, op, right) =>
      DebugPrint("SAssignOpApp")
      info.span.cvec.merge(walkAST(lhs))
      walkAST(op)
      info.span.cvec.merge(walkAST(right))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SAssignOpApp" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case ABlock(info, stmts, _) =>
      DebugPrint("SBlock")
      info.span.cvec.merge(walkAST(stmts))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SBlock" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Bool(info, isBool) =>
      DebugPrint("SBool")
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SBool" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Bracket(info, obj, index) =>
      DebugPrint("SBracket")
      info.span.cvec.merge(walkAST(obj))
      info.span.cvec.merge(walkAST(index))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SBracket" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Break(info, target) =>
      DebugPrint("SBreak")
      info.span.cvec.merge(walkAST(target))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SBreak" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Case(info, cond, body) =>
      DebugPrint("SCase")
      info.span.cvec.merge(walkAST(cond))
      walkAST(body)
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SCase" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Catch(info, id, body) =>
      DebugPrint("SCatch")
      info.span.cvec.merge(walkAST(id))
      info.span.cvec.merge(walkAST(body))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SCatch" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Cond(info, cond, trueBranch, falseBranch) =>
      DebugPrint("SCond")
      info.span.cvec.merge(walkAST(cond))
      info.span.cvec.merge(walkAST(trueBranch))
      info.span.cvec.merge(walkAST(falseBranch))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SCond" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Continue(info, target) =>
      DebugPrint("SContinue")
      info.span.cvec.merge(walkAST(target))
      if (isRelevant(node))
        info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SContinue" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Debugger(info) =>
      DebugPrint("SDebugger")
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SDebugger" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case DoWhile(info, body, cond) =>
      DebugPrint("SDoWhile")
      info.span.cvec.merge(walkAST(body))
      info.span.cvec.merge(walkAST(cond))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SDoWhile" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Dot(info, obj, member) =>
      DebugPrint("SDot")
      info.span.cvec.merge(walkAST(obj))
      info.span.cvec.merge(walkAST(member))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SDot" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case DoubleLiteral(info, text, num) =>
      DebugPrint("SDoubleLiteral")
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SDoubleLiteral" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case EmptyStmt(info) =>
      DebugPrint("SEmptyStmt")
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SEmptyStmt" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case ExprList(info, exprs) =>
      DebugPrint("SExprList")
      info.span.cvec.merge(walkAST(exprs))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SExprList" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case ExprStmt(info, expr, isInternal) =>
      DebugPrint("SExprStmt")
      info.span.cvec.merge(walkAST(expr))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SExprStmt" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Field(info, prop, expr) =>
      DebugPrint("SField")
      info.span.cvec.merge(walkAST(prop))
      info.span.cvec.merge(walkAST(expr))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SField" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case For(info, init, cond, action, body) =>
      DebugPrint("SFor")
      info.span.cvec.merge(walkAST(init))
      info.span.cvec.merge(walkAST(cond))
      info.span.cvec.merge(walkAST(action))
      info.span.cvec.merge(walkAST(body))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SFor" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case ForIn(info, lhs, expr, body) =>
      DebugPrint("SForIn")
      info.span.cvec.merge(walkAST(lhs))
      info.span.cvec.merge(walkAST(expr))
      info.span.cvec.merge(walkAST(body))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SForIn" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case ForVar(info, vars, cond, action, body) =>
      DebugPrint("SForVar")
      info.span.cvec.merge(walkAST(vars))
      info.span.cvec.merge(walkAST(cond))
      info.span.cvec.merge(walkAST(action))
      info.span.cvec.merge(walkAST(body))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SForVar" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case ForVarIn(info, varjs, expr, body) =>
      DebugPrint("SForVarIn")
      info.span.cvec.merge(walkAST(varjs))
      info.span.cvec.merge(walkAST(expr))
      info.span.cvec.merge(walkAST(body))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SForVarIn" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case FunApp(info, fun, args) =>
      DebugPrint("SFunApp")
      info.span.cvec.merge(walkAST(fun))
      info.span.cvec.merge(walkAST(args))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SFunApp" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case FunDecl(info, ftn, _) =>
      DebugPrint("SFunDecl")
      info.span.cvec.merge(walkAST(ftn.name))
      info.span.cvec.merge(walkAST(ftn))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SFunDecl" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case FunExpr(info, ftn) =>
      DebugPrint("SFunExpr")
      info.span.cvec.merge(walkAST(ftn.name))
      info.span.cvec.merge(walkAST(ftn))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SFunExpr" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Functional(info, fds, vds, stmts, name, params, body) =>
      DebugPrint("SFunctional")
      var v = new CharVector
      fds.foreach(fd => v.merge(walkAST(fd)))
      vds.foreach(vd => v.merge(walkAST(vd)))
      v.merge(walkAST(stmts))
      params.foreach(p => v.merge(walkAST(p)))
      if (isRelevant(node)) v.addAt(Util.name2id(node))
      if (isSignificant(node) && v.containsEnoughTokens(minT))
        v.setMergeable()
      DebugPrint(v.toString + "SFunctional" + " " + v.getNumOfTokens + " " + v.isMergeable)
      v.getVector

    case GetProp(info, prop, ftn) =>
      DebugPrint("SGetProp")
      info.span.cvec.merge(walkAST(prop))
      info.span.cvec.merge(walkAST(ftn))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SGetProp" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Id(info, text, uniqueName, _) =>
      DebugPrint("SId \"" + text + "\"")
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SId" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case If(info, cond, trueBranch, falseBranch) =>
      DebugPrint("SIf")
      info.span.cvec.merge(walkAST(cond))
      info.span.cvec.merge(walkAST(trueBranch))
      info.span.cvec.merge(walkAST(falseBranch))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SIf" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case InfixOpApp(info, left, op, right) =>
      DebugPrint("SInfixOpApp")
      var v = new CharVector
      v.merge(walkAST(left))
      v.merge(walkAST(op))
      v.merge(walkAST(right))
      if (isRelevant(node)) v.addAt(Util.name2id(node))
      if (isSignificant(node) && v.containsEnoughTokens(minT))
        v.setMergeable()
      DebugPrint(v.toString + "SInfixOpApp" + " " + v.getNumOfTokens + " " + v.isMergeable)
      v.getVector

    case IntLiteral(info, intVal, radix) =>
      DebugPrint("SIntLiteral")
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SIntLiteral" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Label(info, id) =>
      DebugPrint("SLabel")
      info.span.cvec.merge(walkAST(id))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SLabel" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case LabelStmt(info, label, stmt) =>
      DebugPrint("SLabelStmt")
      walkAST(label)
      info.span.cvec.merge(walkAST(stmt))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SLabelStmt" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case New(info, lhs) =>
      DebugPrint("SNew")
      walkAST(lhs)
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SNew" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Null(info) =>
      DebugPrint("SNull")
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SNull" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case ObjectExpr(info, members) =>
      DebugPrint("SObjectExpr")
      info.span.cvec.merge(walkAST(members))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SObjectExpr" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Op(info, text) =>
      DebugPrint("SOp")
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SOp" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Parenthesized(info, expr) =>
      DebugPrint("SParenthesized")
      info.span.cvec.merge(walkAST(expr))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SParenthesized" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case PrefixOpApp(info, op, right) =>
      DebugPrint("SPrefixOpApp")
      walkAST(op)
      info.span.cvec.merge(walkAST(right))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SPrefixOpApp" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Program(info, body) =>
      DebugPrint("SProgram")
      info.span.cvec.merge(walkAST(body))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SProgram" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case PropId(info, id) =>
      DebugPrint("SPropId")
      info.span.cvec.merge(walkAST(id))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SPropId" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case PropNum(info, num) =>
      DebugPrint("SPropNum")
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SPropNum" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case PropStr(info, str) =>
      DebugPrint("SPropStr")
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SPropStr" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case RegularExpression(info, body, flags) =>
      DebugPrint("SRegularExpression")
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SRegularExpression" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Return(info, expr) =>
      DebugPrint("SReturn")
      info.span.cvec.merge(walkAST(expr))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SReturn" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case SetProp(info, prop, ftn) =>
      DebugPrint("SSetProp")
      info.span.cvec.merge(walkAST(prop))
      info.span.cvec.merge(walkAST(ftn))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SSetProp" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case i: ASTNodeInfo =>
      val span = i.span
      DebugPrint("SpanInfo")
      span.cvec.merge(walkAST(span))
      if (isRelevant(node)) span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && span.cvec.containsEnoughTokens(minT))
        span.cvec.setMergeable()
      DebugPrint(span.cvec.toString + "SpanInfo" + " " + span.cvec.isMergeable)
      span.cvec.getVector

    case SourceElements(info, body, _) =>
      DebugPrint("SSourceElements")
      var v = new CharVector
      body.foreach(s => v.merge(walkAST(s)))
      v.getVector

    case StringLiteral(info, quote, txt, isRE) =>
      DebugPrint("SStringLiteral \"" + txt + "\"")
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SStringLiteral" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Switch(info, cond, frontCases, defjs, backCases) =>
      DebugPrint("SSwitch")
      info.span.cvec.merge(walkAST(cond))
      info.span.cvec.merge(walkAST(frontCases))
      info.span.cvec.merge(walkAST(defjs))
      info.span.cvec.merge(walkAST(backCases))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SSwitch" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case This(info) =>
      DebugPrint("SThis")
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SThis" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case Throw(info, expr) =>
      DebugPrint("SThrow")
      info.span.cvec.merge(walkAST(expr))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SThrow" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case TopLevel(info, vds, fds, stmts) =>
      DebugPrint("STopLevel")
      var v = new CharVector
      vds.foreach(vd => v.merge(walkAST(vd)))
      fds.foreach(fd => v.merge(walkAST(fd)))
      stmts.foreach(s => v.merge(walkAST(s)))
      DebugPrint(v.toString + "STopLevel" + " " + v.isMergeable)
      v.getVector

    case Try(info, body, catchBlock, fin) =>
      DebugPrint("STry")
      info.span.cvec.merge(walkAST(body))
      info.span.cvec.merge(walkAST(catchBlock))
      info.span.cvec.merge(walkAST(fin))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "STry" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case UnaryAssignOpApp(info, lhs, op) =>
      DebugPrint("SUnaryAssignOpApp")
      info.span.cvec.merge(walkAST(lhs))
      walkAST(op)
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SUnaryAssignOpApp" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case VarDecl(info, name, expr, _) =>
      DebugPrint("SVarDecl")
      info.span.cvec.merge(walkAST(name))
      info.span.cvec.merge(walkAST(expr))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SVarDecl" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case VarRef(info, id) =>
      DebugPrint("SVarRef")
      info.span.cvec.merge(walkAST(id))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SVarRef" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case VarStmt(info, vds) =>
      DebugPrint("SVarStmt")
      vds.foreach(vd => info.span.cvec.merge(walkAST(vd)))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SVarStmt" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case While(info, cond, body) =>
      DebugPrint("SWhile")
      info.span.cvec.merge(walkAST(cond))
      info.span.cvec.merge(walkAST(body))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SWhile" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case With(info, expr, stmt) =>
      DebugPrint("SWith")
      info.span.cvec.merge(walkAST(expr))
      info.span.cvec.merge(walkAST(stmt))
      if (isRelevant(node)) info.span.cvec.addAt(Util.name2id(node))
      if (isSignificant(node) && info.span.cvec.containsEnoughTokens(minT))
        info.span.cvec.setMergeable()
      DebugPrint(info.span.cvec.toString + "SWith" + " " + info.span.cvec.getNumOfTokens + " " + info.span.cvec.isMergeable)
      info.span.cvec.getVector

    case xs: List[_] =>
      var v = new CharVector
      xs.foreach(x => v.merge(walkAST(x)))
      v.getVector

    case xs: Option[_] =>
      var v = new CharVector
      xs.foreach(x => v.merge(walkAST(x)))
      v.getVector

    case _ =>
      DebugPrint("Default")
      val v = new java.util.Vector[Int]
      v
  }
}
