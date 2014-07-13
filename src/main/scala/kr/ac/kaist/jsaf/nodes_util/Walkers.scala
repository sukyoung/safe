/*******************************************************************************
    Copyright (c) 2013-2014, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util

import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.analysis.cfg._

trait Walkers {
  // AST nodes
  def walkAST(parent: Any, node: Any): Unit = node match {
    case SProgram(info, body) => walkAST(node, body)
    case SModDecl(info, name, body) => walkAST(node, name); walkAST(node, body)
    case SModExpVarStmt(info, vds) => walkAST(node, vds)
    case SModExpFunDecl(info, fd) => walkAST(node, fd)
    case SModExpGetter(info, fd) => walkAST(node, fd)
    case SModExpSetter(info, fd) => walkAST(node, fd)
    case SModExpSpecifiers(info, names) => walkAST(node, names)
    case SModImpDecl(info, imports) => walkAST(node, imports)
    case SNoOp(info, desc) =>
    case SStmtUnit(info, stmts) => walkAST(node, stmts)
    case SFunDecl(info, ftn, strict) => walkAST(node, ftn)
    case SBlock(info, stmts, internal) => walkAST(node, stmts)
    case SVarStmt(info, vds) => walkAST(node, vds)
    case SEmptyStmt(info) =>
    case SExprStmt(info, expr, internal) => walkAST(node, expr)
    case SIf(info, cond, trueBranch, falseBranch) => walkAST(node, cond); walkAST(node, trueBranch); walkAST(node, falseBranch)
    case SDoWhile(info, body, cond) => walkAST(node, body); walkAST(node, cond)
    case SWhile(info, cond, body) => walkAST(node, cond); walkAST(node, body)
    case SFor(info, init, cond, action, body) => walkAST(node, init); walkAST(node, cond); walkAST(node, action); walkAST(node, body)
    case SForIn(info, lhs, expr, body) => walkAST(node, lhs); walkAST(node, expr); walkAST(node, body)
    case SForVar(info, vars, cond, action, body) => walkAST(node, vars); walkAST(node, cond); walkAST(node, action); walkAST(node, body)
    case SForVarIn(info, _var, expr, body) => walkAST(node, _var); walkAST(node, expr); walkAST(node, body)
    case SContinue(info, target) => walkAST(node, target)
    case SBreak(info, target) => walkAST(node, target)
    case SReturn(info, expr) => walkAST(node, expr)
    case SWith(info, expr, stmt) => walkAST(node, expr); walkAST(node, stmt)
    case SSwitch(info, cond, frontCases, _def, backCases) => walkAST(node, cond); walkAST(node, frontCases); walkAST(node, _def); walkAST(node, backCases)
    case SLabelStmt(info, label, stmt) => walkAST(node, label); walkAST(node, stmt)
    case SThrow(info, expr) => walkAST(node, expr)
    case STry(info, body, catchBlock, fin) => walkAST(node, body); walkAST(node, catchBlock); walkAST(node, fin)
    case SDebugger(info) =>
    case SSourceElements(info, body, strict) => walkAST(node, body)
    case SVarDecl(info, id, expr, strict) => walkAST(node, id); walkAST(node, expr)
    case SCase(info, cond, body) => walkAST(node, cond); walkAST(node, body)
    case SCatch(info, id, body) => walkAST(node, id); walkAST(node, body)
    case SModImpSpecifierSet(info, imports, module) => walkAST(node, imports); walkAST(node, module)
    case SModImpAliasClause(info, name, alias) => walkAST(node, name); walkAST(node, alias)
    case SExprList(info, exprs) => walkAST(node, exprs)
    case SCond(info, cond, trueBranch, falseBranch) => walkAST(node, cond); walkAST(node, trueBranch); walkAST(node, falseBranch)
    case SInfixOpApp(info, left, op, right) => walkAST(node, left); walkAST(node, op); walkAST(node, right)
    case SPrefixOpApp(info, op, right) => walkAST(node, op); walkAST(node, right)
    case SUnaryAssignOpApp(info, lhs, op) => walkAST(node, lhs); walkAST(node, op)
    case SAssignOpApp(info, lhs, op, right) => walkAST(node, lhs); walkAST(node, op); walkAST(node, right)
    case SThis(info) =>
    case SNull(info) =>
    case SBool(info, bool) =>
    case SDoubleLiteral(info, text, num) =>
    case SIntLiteral(info, intVal, radix) =>
    case SStringLiteral(info, quote, escaped) =>
    case SRegularExpression(info, body, flag) =>
    case SVarRef(info, id) => walkAST(node, id)
    case SArrayExpr(info, elements) => walkAST(node, elements)
    case SArrayNumberExpr(info, elements) => walkAST(node, elements)
    case SObjectExpr(info, members) => walkAST(node, members)
    case SParenthesized(info, expr) => walkAST(node, expr)
    case SFunExpr(info, ftn) => walkAST(node, ftn)
    case SBracket(info, obj, index) => walkAST(node, obj); walkAST(node, index)
    case SDot(info, obj, member) => walkAST(node, obj); walkAST(node, member)
    case SNew(info, lhs) => walkAST(node, lhs)
    case SFunApp(info, fun, args) => walkAST(node, fun); walkAST(node, args)
    case SPropId(info, id) => walkAST(node, id)
    case SPropStr(info, str) =>
    case SPropNum(info, num) =>
    case SField(info, prop, expr) => walkAST(node, prop); walkAST(node, expr)
    case SGetProp(info, prop, ftn) => walkAST(node, prop); walkAST(node, ftn)
    case SSetProp(info, prop, ftn) => walkAST(node, prop); walkAST(node, ftn)
    case SId(info, text, uniqueName, _with) =>
    case SOp(info, text) =>
    case SAnonymousFnName(info) =>
    case SPath(info, names) => walkAST(node, names)
    case SModExpStarFromPath(info, modules) => walkAST(node, modules)
    case SModExpStar(info) =>
    case SModExpAlias(info, name, alias) => walkAST(node, name); walkAST(node, alias)
    case SModExpName(info, name) => walkAST(node, name)
    case SModImpAlias(info, name, alias) => walkAST(node, name); walkAST(node, alias)
    case SModImpName(info, name) => walkAST(node, name)
    case SLabel(info, id) => walkAST(node, id)
    case SComment(info, comment) =>
    case STopLevel(fds, vds, stmts) => walkAST(node, fds); walkAST(node, vds); walkAST(node, stmts)
    case SFunctional(fds, vds, stmts, id, params) => walkAST(node, fds); walkAST(node, vds); walkAST(node, stmts); walkAST(node, id); walkAST(node, params)
    case list: List[_] => for(node <- list) walkAST(parent, node)
    case Some(node) => walkAST(parent, node)
    case None =>
  }

  // IR nodes
  def walkIR(parent: Any, node: Any): Unit = node match {
    case SIRRoot(info, fds, vds, irs) => walkIR(node, fds); walkIR(node, vds); walkIR(node, irs)
    case SIRExprStmt(info, lhs, right, isRef) => walkIR(node, lhs); walkIR(node, right)
    case SIRDelete(info, lhs, id) => walkIR(node, lhs); walkIR(node, id)
    case SIRDeleteProp(info, lhs, obj, index) => walkIR(node, lhs); walkIR(node, obj); walkIR(node, index)
    case SIRObject(info, lhs, members, proto) => walkIR(node, lhs); walkIR(node, members); walkIR(node, proto)
    case SIRArray(info, lhs, elements) => walkIR(node, lhs); walkIR(node, elements)
    case SIRArrayNumber(info, lhs, elements) => walkIR(node, lhs); walkIR(node, elements)
    case SIRArgs(info, lhs, elements) => walkIR(node, lhs); walkIR(node, elements)
    case SIRCall(info, lhs, fun, thisB, args) => walkIR(node, lhs); walkIR(node, fun); walkIR(node, thisB); walkIR(node, args)
    case SIRInternalCall(info, lhs, fun, first, second) => walkIR(node, lhs); walkIR(node, fun); walkIR(node, first); walkIR(node, second)
    case SIRNew(info, lhs, fun, args) => walkIR(node, lhs); walkIR(node, fun); walkIR(node, args)
    case SIRFunExpr(info, lhs, ftn) => walkIR(node, lhs); walkIR(node, ftn)
    case SIREval(info, lhs, arg) => walkIR(node, lhs); walkIR(node, arg)
    case SIRStmtUnit(info, stmts) => walkIR(node, stmts)
    case SIRStore(info, obj, index, rhs) => walkIR(node, obj); walkIR(node, index); walkIR(node, rhs)
    case SIRFunDecl(info, ftn) => walkIR(node, ftn)
    case SIRBreak(info, label) => walkIR(node, label)
    case SIRReturn(info, expr) => walkIR(node, expr)
    case SIRWith(info, id, stmt) => walkIR(node, id); walkIR(node, stmt)
    case SIRLabelStmt(info, label, stmt) => walkIR(node, label); walkIR(node, stmt)
    case SIRVarStmt(info, lhs, fromParam) => walkIR(node, lhs)
    case SIRThrow(info, expr) => walkIR(node, expr)
    case SIRSeq(info, stmts) => walkIR(node, stmts)
    case SIRIf(info, expr, trueB, falseB) => walkIR(node, expr); walkIR(node, trueB); walkIR(node, falseB)
    case SIRWhile(info, cond, body) => walkIR(node, cond); walkIR(node, body)
    case SIRTry(info, body, name, catchB, finallyB) => walkIR(node, body); walkIR(node, name); walkIR(node, catchB); walkIR(node, finallyB)
    case SIRNoOp(info, desc) =>
    case SIRField(info, prop, expr) => walkIR(node, prop); walkIR(node, expr)
    case SIRGetProp(info, ftn) => walkIR(node, ftn)
    case SIRSetProp(info, ftn) => walkIR(node, ftn)
    case SIRBin(info, first, op, second) => walkIR(node, first); walkIR(node, op); walkIR(node, second)
    case SIRUn(info, op, expr) => walkIR(node, op); walkIR(node, expr)
    case SIRLoad(info, obj, index) => walkIR(node, obj); walkIR(node, index)
    case SIRUserId(info, originalName, uniqueName, global, _with) =>
    case SIRTmpId(info, originalName, uniqueName, global) =>
    case SIRThis(info) =>
    case SIRNumber(info, text, num) =>
    case SIRString(info, str) =>
    case SIRBool(info, bool) =>
    case SIRUndef(info) =>
    case SIRNull(info) =>
    case SIROp(text, kind) =>
    case SIRFunctional(fromSource, name, params, args, fds, vds, body) => walkIR(node, name); walkIR(node, params); walkIR(node, args); walkIR(node, fds); walkIR(node, vds); walkIR(node, body)
    case SIRSpanInfo(fromSource, span) =>
    case list: List[_] => for(node <- list) walkIR(parent, node)
    case Some(node) => walkIR(parent, node)
    case None =>
  }

  // CFG nodes
  def walkCFG(parent: Any, cfg: Any): Unit = cfg match {
    case CFGAlloc(iid, info, lhs, proto, addr) => walkCFG(cfg, lhs); walkCFG(cfg, proto)
    case CFGAllocArray(iid, info, lhs, length, addr) => walkCFG(cfg, lhs)
    case CFGAllocArg(iid, info, lhs, length, addr) => walkCFG(cfg, lhs)
    case CFGExprStmt(iid, info, lhs, expr) => walkCFG(cfg, lhs); walkCFG(cfg, expr)
    case CFGDelete(iid, info, lhs, expr) => walkCFG(cfg, lhs); walkCFG(cfg, expr)
    case CFGDeleteProp(iid, info, lhs, obj, index) => walkCFG(cfg, lhs); walkCFG(cfg, obj); walkCFG(cfg, index)
    case CFGStore(iid, info, obj, index, rhs) => walkCFG(cfg, obj); walkCFG(cfg, index); walkCFG(cfg, rhs)
    case CFGFunExpr(iid, info, lhs, name, fid, addr1, addr2, addr3) => walkCFG(cfg, lhs); walkCFG(cfg, name)
    case CFGConstruct(iid, info, cons, thisArg, arguments, addr1, addr2) => walkCFG(cfg, cons); walkCFG(cfg, thisArg); walkCFG(cfg, arguments)
    case CFGCall(iid, info, fun, thisArg, arguments, addr1, addr2) => walkCFG(cfg, fun); walkCFG(cfg, thisArg); walkCFG(cfg, arguments)
    case CFGInternalCall(iid, info, lhs, fun, arguments, addr) => walkCFG(cfg, lhs); walkCFG(cfg, fun); walkCFG(cfg, arguments)
    case CFGAPICall(iid, model, fun, arguments) =>
    case CFGAssert(iid, info, expr, flag) => walkCFG(cfg, expr)
    case CFGCond(iid, info, expr, body) => walkCFG(cfg, expr)
    case CFGCatch(iid, info, name) => walkCFG(cfg, name)
    case CFGReturn(iid, info, expr) => walkCFG(cfg, expr)
    case CFGThrow(iid, info, expr) => walkCFG(cfg, expr)
    case CFGNoOp(iid, info, desc) =>
    case CFGAsyncCall(iid, info, modelType, callType, addr1, addr2, addr3) =>
    case CFGVarRef(info, id) => walkCFG(cfg, id)
    case CFGBin(info, first, op, second) => walkCFG(cfg, first); walkCFG(cfg, second)
    case CFGUn(info, op, expr) => walkCFG(cfg, expr)
    case CFGLoad(info, obj, index) => walkCFG(cfg, obj); walkCFG(cfg, index)
    case CFGNumber(text, num) =>
    case CFGString(str) =>
    case CFGBool(bool) =>
    case CFGNull() =>
    case CFGThis(info) =>
    case CFGUserId(info, text, kind, originalName, fromWith) =>
    case CFGTempId(text, kind) =>
    case list: List[_] => for(node <- list) walkCFG(parent, node)
    case Some(node) => walkCFG(parent, node)
    case None =>
  }

  // WIDL nodes
  def walkWIDL(parent: Any, node: Any): Unit = node match {
    case SWModule(info, attrs, name, defs) => walkWIDL(node, attrs); walkWIDL(node, defs)
    case SWInterface(info, attrs, name, parent, members) => walkWIDL(node, attrs); walkWIDL(node, parent); walkWIDL(node, members)
    case SWCallback(info, attrs, name, returnType, args) => walkWIDL(node, attrs); walkWIDL(node, returnType); walkWIDL(node, args)
    case SWDictionary(info, attrs, name, parent, members) => walkWIDL(node, attrs); walkWIDL(node, parent); walkWIDL(node, members)
    case SWException(info, attrs, name, parent, members) => walkWIDL(node, attrs); walkWIDL(node, parent); walkWIDL(node, members)
    case SWEnum(info, attrs, name, enumValueList) => walkWIDL(node, attrs); walkWIDL(node, enumValueList)
    case SWTypedef(info, attrs, typ, name) => walkWIDL(node, attrs); walkWIDL(node, typ)
    case SWImplementsStatement(info, attrs, name, parent) => walkWIDL(node, attrs)
    case SWConst(info, attrs, typ, name, value) => walkWIDL(node, attrs); walkWIDL(node, typ); walkWIDL(node, value)
    case SWAttribute(info, attrs, typ, name, exns) => walkWIDL(node, attrs); walkWIDL(node, typ); walkWIDL(node, exns)
    case SWOperation(info, attrs, qualifiers, returnType, name, args, exns) => walkWIDL(node, attrs); walkWIDL(node, qualifiers); walkWIDL(node, returnType); walkWIDL(node, args); walkWIDL(node, exns)
    case SWDictionaryMember(info, attrs, typ, name, default) => walkWIDL(node, attrs); walkWIDL(node, typ); walkWIDL(node, default)
    case SWExceptionField(info, attrs, typ, name) => walkWIDL(node, attrs); walkWIDL(node, typ)
    case SWBoolean(info, value) =>
    case SWFloat(info, value) =>
    case SWInteger(info, value) =>
    case SWString(info, str) =>
    case SWNull(info) =>
    case SWAnyType(info, suffix) => walkWIDL(node, suffix)
    case SWNamedType(info, suffix, name) => walkWIDL(node, suffix)
    case SWArrayType(info, suffix, typ) => walkWIDL(node, suffix); walkWIDL(node, typ)
    case SWSequenceType(info, suffix, typ) => walkWIDL(node, suffix); walkWIDL(node, typ)
    case SWUnionType(info, suffix, types) => walkWIDL(node, suffix); walkWIDL(node, types)
    case SWArgument(info, attributes, typ, name, default) => walkWIDL(node, attributes); walkWIDL(node, typ); walkWIDL(node, default)
    case SWId(info, name) =>
    case SWQId(info, name) => walkWIDL(node, name)
    case list: List[_] => for(node <- list) walkWIDL(parent, node)
    case Some(node) => walkWIDL(parent, node)
    case None =>
    case _ => // This line should be deleted
  }
}
