/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.clone_detector.vgen

import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.SpanInfo
import kr.ac.kaist.jsaf.nodes_util.CharVector
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.clone_detector.util.Util
import scala.collection.JavaConversions._

class JSAstSerializer (program: Program, st: java.util.Vector[ASTNode], minT: Int) extends Walker {
  def doit() = walk(program)
  var bDebugPrint: Boolean = false
  def DebugPrint(string: String) = if(bDebugPrint) System.out.println(string)

  override def walk(node:Any) = node match {
    case SAnonymousFnName(info) =>
      DebugPrint("SAnonymousFnName")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case SArrayExpr(info, elements) =>
      DebugPrint("SArrayExpr")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(elements)
    case SAssignOpApp(info, lhs, op, right) =>
      DebugPrint("SAssignOpApp")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(lhs)
      walk(op)
      walk(right)
    case SBlock(info, stmts, _) =>
      DebugPrint("SBlock")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(stmts)
    case SBool(info, isBool) =>
      DebugPrint("SBool")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case SBracket(info, obj, index) =>
      DebugPrint("SBracket")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(obj)
      walk(index)
    case SBreak(info, target) =>
      DebugPrint("SBreak")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(target)
    case SCase(info, cond, body) =>
      DebugPrint("SCase")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(cond)
      walk(body)
    case SCatch(info, id, body) =>
      DebugPrint("SCatch")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(id)
      walk(body)
    case SCond(info, cond, trueBranch, falseBranch) =>
      DebugPrint("SCond")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(cond)
      walk(trueBranch)
      walk(falseBranch)
    case SContinue(info, target) =>
      DebugPrint("SContinue")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(target)
    case SDebugger(info) =>
      DebugPrint("SDebugger")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case SDoWhile(info, body, cond) =>
      DebugPrint("SDoWhile")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(body)
      walk(cond)
    case SDot(info, obj, member) =>
      DebugPrint("SDot")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(obj)
      walk(member)
    case SDoubleLiteral(info, text, num) =>
      DebugPrint("SDoubleLiteral")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(text)
      walk(num)  
    case SEmptyStmt(info) =>
      DebugPrint("SEmptyStmt")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case SExprList(info, exprs) =>
      DebugPrint("SExprList")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(exprs)
    case SExprStmt(info, expr, isInternal) =>
      DebugPrint("SExprStmt")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(expr)
    case SField(info, prop, expr) =>
      DebugPrint("SField")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(prop)
      walk(expr)
    case SFor(info, init, cond, action, body) =>
      DebugPrint("SFor")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(init)
      walk(cond)
      walk(action)
      walk(body)
    case SForIn(info, lhs, expr, body) =>
      DebugPrint("SForIn")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(lhs)
      walk(expr)
      walk(body)
    case SForVar(info, vars, cond, action, body) =>
      DebugPrint("SForVar")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(vars)
      walk(cond)
      walk(action)
      walk(body)
    case SForVarIn(info, varjs, expr, body) =>
      DebugPrint("SForVarIn")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(varjs)
      walk(expr)
      walk(body)
    case SFunApp(info, fun, args) =>
      DebugPrint("SFunApp")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(fun)
      walk(args)
    case SFunDecl(info, ftn, _) =>
      DebugPrint("SFunDecl")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(ftn.getName)
      walk(ftn)
    case SFunExpr(info, ftn) =>
      DebugPrint("SFunExpr")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(ftn.getName)
      walk(ftn)
    case SFunctional(vds, fds, pgm, name, params) =>
      DebugPrint("SFunctional")
      vds.foreach(walk)
      fds.foreach(walk)
      walk(pgm)
      params.foreach(walk)
    case SGetProp(info, prop, ftn) =>
      DebugPrint("SGetProp")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(prop)
      walk(ftn)
    case SId(info, text, _, _) =>
      DebugPrint("SId \"" + text + "\"")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case SIf(info, cond, trueBranch, falseBranch) =>
      DebugPrint("SIf")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(cond)
      walk(trueBranch)
      walk(falseBranch)
    case SInfixOpApp(info, left, op, right) =>
      DebugPrint("SInfixOpApp")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(left)
      walk(op)
      walk(right)
    case SIntLiteral(info, intVal, radix) =>
      DebugPrint("SIntLiteral")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case SLabel(info, id) =>
      DebugPrint("SLabel")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(id)
    case SLabelStmt(info, label, stmt) =>
      DebugPrint("SLabelStmt")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(label)
      walk(stmt)
    case SNew(info, lhs) =>
      DebugPrint("SNew")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(lhs)
    case SNull(info) =>
      DebugPrint("SNull")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case SObjectExpr(info, members) =>
      DebugPrint("SObjectExpr")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(members)
    case SOp(info, text) =>
      DebugPrint("SOp")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case SParenthesized(info, expr) =>
      DebugPrint("SParenthesized")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(expr)
    case SPrefixOpApp(info, op, right) =>
      DebugPrint("SPrefixOpApp")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(op)
      walk(right)
    case SProgram(info, program) =>
      DebugPrint("SProgram")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(program)
    case SPropId(info, id) =>
      DebugPrint("SPropId")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(id)
    case SPropNum(info, num) =>
      DebugPrint("SPropNum")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(num)
    case SPropStr(info, str) =>
      DebugPrint("SPropStr")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case SRegularExpression(info, body, flags) =>
      DebugPrint("SRegularExpression")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case SReturn(info, expr) =>
      DebugPrint("SReturn")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(expr)
    case SSetProp(info, prop, ftn) =>
      DebugPrint("SSetProp")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(prop)
      walk(ftn)
    case i:SpanInfo =>
      val span = i.getSpan
      DebugPrint("SpanInfo")
      DebugPrint(span.getCharVector.toString + span.getCharVector.isMergeable)
      span.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case SSourceElements(info, body, _) =>
      DebugPrint("SSourceElements")
      body.foreach(walk)
    case SStringLiteral(info, _, txt) =>
      val str = NU.unescapeJava(txt)
      DebugPrint("SStringLiteral \"" + str + "\"")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case SSwitch(info, cond, frontCases, defjs, backCases) =>
      DebugPrint("SSwitch")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(cond)
      walk(frontCases)
      walk(defjs)
      walk(backCases)
    case SThis(info) =>
      DebugPrint("SThis")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
    case SThrow(info, expr) =>
      DebugPrint("SThrow")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(expr)
    case STopLevel(vds, fds, stmts) =>
      DebugPrint("STopLevel")
      vds.foreach(walk)
      fds.foreach(walk)
      stmts.foreach(walk)
    case STry(info, body, catchBlock, fin) =>
      DebugPrint("STry")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(body)
      walk(catchBlock)
      walk(fin)
    case SUnaryAssignOpApp(info, lhs, op) =>
      DebugPrint("SUnaryAssignOpApp")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(lhs)
      walk(op)
    case SVarDecl(info, name, expr, _) =>
      DebugPrint("SVarDecl")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(name)
      walk(expr)
    case SVarRef(info, id) =>
      DebugPrint("SVarRef")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(id)
    case SVarStmt(info, vds) =>
      DebugPrint("SVarStmt")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      vds.foreach(walk _)
    case SWhile(info, cond, body) =>
      DebugPrint("SWhile")
      DebugPrint(info.getSpan.getCharVector.toString + info.getSpan.getCharVector.isMergeable)
      info.getSpan.getCharVector.setNodeKind(Util.name2id(node))
      st.add(node.asInstanceOf[ASTNode])
      walk(cond)
      walk(body)
    case SWith =>
      DebugPrint("SWith")
      st.add(node.asInstanceOf[ASTNode])
    case xs:List[_] =>
      xs.foreach(walk)
    case xs:Option[_] =>
      xs.foreach(walk)
    case _ =>
      DebugPrint("Default")
  }
}