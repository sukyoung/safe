/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.compiler

import _root_.java.util.{List => JList}
import kr.ac.kaist.jsaf.exceptions.JSAFError.error
import kr.ac.kaist.jsaf.exceptions.StaticError
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{ NodeFactory => NF }
import kr.ac.kaist.jsaf.nodes_util.{ NodeUtil => NU }
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.ErrorLog
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.scala_src.useful.Sets._
import kr.ac.kaist.jsaf.useful.HasAt

/* Rewrites a JavaScript source code using the with statement
 * to another one without using the with statement.
 */
class WithRewriter(program: Program, forTest: Boolean) extends Walker {

  /* Error handling
   * The signal function collects errors during the AST->IR translation.
   * To collect multiple errors,
   * we should return a dummy value after signaling an error.
   */
  val errors: ErrorLog = new ErrorLog
  def signal(msg:String, hasAt:HasAt) = errors.signal(msg, hasAt)
  def signal(hasAt:HasAt, msg:String) = errors.signal(msg, hasAt)
  def signal(error: StaticError) = errors.signal(error)
  def getErrors(): JList[StaticError] = toJavaList(errors.errors)

  abstract class Env()
  case class EmptyEnv() extends Env
  case class ConsEnv(withs: List[Id], names: List[List[String]], isNested: Boolean) extends Env

  def doit() = NU.simplifyWalker.walk(walk(program, EmptyEnv()).asInstanceOf[Program])

  // For WithRewriter tests...
  def freshNameTest() = "$f_" + System.nanoTime
  val toObjectInfo = NF.makeSpanInfo(NF.makeSpan("_gen_toObject"))
  def mkId(name: String) = SId(toObjectInfo, name, None, false)
  def mkVarRef(id: Id) = SVarRef(toObjectInfo, id)
  val toObjectFnId = mkId("toObject_" + freshNameTest)
  val paramExpr = mkVarRef(mkId("x"))
  def mkEq(name: String) =
    SInfixOpApp(toObjectInfo, mkVarRef(mkId("type")), SOp(toObjectInfo, "=="),
                SStringLiteral(toObjectInfo, "\"", name))
  def mkRes(name: LHS) =
    SReturn(toObjectInfo,
            Some(SNew(toObjectInfo, SFunApp(toObjectInfo, name, List(paramExpr)))))
  def mkIf(tname: String, cname: LHS, falseB: Option[Stmt]) =
    SIf(toObjectInfo, mkEq(tname), mkRes(cname), falseB)
  val false3 = Some(SReturn(toObjectInfo, Some(paramExpr)))
  val false2 = Some(mkIf("boolean", mkVarRef(mkId("Boolean")), false3))
  val false1 = Some(mkIf("string", mkVarRef(mkId("String")), false2))
  val typeofCall =
    Some(SFunApp(toObjectInfo, mkVarRef(mkId("typeof")), List(paramExpr)))
  val toObjectBody =
    SSourceElements(toObjectInfo,
                    List(SVarStmt(toObjectInfo,
                                  List(SVarDecl(toObjectInfo, mkId("type"), typeofCall, false))),
                         mkIf("number", mkVarRef(mkId("Number")), false1)), false)
  val toObjectFnDecl =
      SFunDecl(toObjectInfo,
               SFunctional(List(), List(), toObjectBody, toObjectFnId, List(mkId("x"))),
               false)

  // For SAFE
  val internalSymbol = NU.internalSymbol
  def freshName(info: ASTSpanInfo) = {
    val name = NU.freshName("alpha")
    SId(info, name, Some(name), true)
  }
  val toObjectName = NU.toObjectName
  def toObjectId(info: ASTSpanInfo) = SId(info, toObjectName, Some(toObjectName), false)

  def assignOp(info: ASTSpanInfo) = SOp(info, "=")
  def inOp(info: ASTSpanInfo) = SOp(info, "in")
  def paren(expr: Expr) = SParenthesized(NU.getInfo(expr), expr)
  def splitNames(names: List[List[String]]) = names match {
    case hd::tl => (hd, tl)
    case _ => (Nil, Nil)
  }
  def mkBody(fds: List[FunDecl], vds: List[VarDecl], name: Id,
             params: List[Id], body: SourceElements, env: Env, node: HasAt) = env match {
    case EmptyEnv() =>
      SSourceElements(body.getInfo,
                      walk(toList(body.getBody), env).asInstanceOf[List[SourceElement]],
                      body.isStrict)
    case ConsEnv(withs, names, isNested) =>
      val (first, rest) = splitNames(names)
      var ids = params.map(_.getText)++
                fds.map(fd => fd match { case SFunDecl(_,f,_) => f.getName.getText })++
                vds.map(vd => vd match { case SVarDecl(_,n,_,_) => n.getText })++first
      ids = (List(name.getText))++ids
      SSourceElements(body.getInfo,
                      walk(toList(body.getBody),
                           new ConsEnv(withs,ids::rest,isNested)).asInstanceOf[List[SourceElement]],
                      body.isStrict)
  }
  def mkFunctional(fds: List[FunDecl], vds: List[VarDecl], name: Id,
                   params: List[Id], body: SourceElements, env: Env, node: HasAt) =
    SFunctional(fds.map(fd => walk(fd, env).asInstanceOf[FunDecl]),
                vds.map(vd => walk(vd, env).asInstanceOf[VarDecl]),
                mkBody(fds, vds, name, params, body, env, node), name, params)

  def walk(node: Any, env: Env): Any = node match {
    case SArrayExpr(info, elements) =>
      SArrayExpr(info, elements.map(e => walk(e, env)).asInstanceOf[List[Option[Expr]]])
    case aoa@SAssignOpApp(info, lhs, op, right) =>
      val lhsWalk = walk(lhs, env).asInstanceOf[LHS]
      val exprWalk = walk(right, env).asInstanceOf[Expr]
      env match {
        case EmptyEnv() => SAssignOpApp(info, lhsWalk, op, exprWalk)
        case ConsEnv(withs, names, isNested) => withs match {
          case Nil =>
            signal("Non-empty with-rewriting environment should have at least one with-object name.", aoa)
            aoa
          case alpha::others =>
            val (first, rest) = splitNames(names)
            NU.unwrapParen(lhs) match {
              case SVarRef(vinfo, id) =>
                if (first.contains(id.getText)) SAssignOpApp(vinfo, lhs, op, exprWalk)
                else {
                  val idInAlpha = SInfixOpApp(vinfo,
                                              SStringLiteral(vinfo, "\"", id.getText),
                                              inOp(vinfo), SVarRef(vinfo, alpha))
                  val alphaDotLhs = SDot(vinfo, SVarRef(vinfo, alpha), id)
                  val alphaOpExpr = SAssignOpApp(vinfo, alphaDotLhs, op, right)
                  val alphaOpWalk = SAssignOpApp(vinfo, alphaDotLhs, op, exprWalk)
                  val lhsOpWalk = SAssignOpApp(vinfo, lhs, op, exprWalk)
                  others match {
                    case Nil =>
                      if (isNested) paren(SCond(vinfo, idInAlpha, alphaOpExpr, aoa))
                      else paren(SCond(vinfo, idInAlpha, alphaOpWalk, lhsOpWalk))
                    case more =>
                      if (isNested) paren(SCond(vinfo, idInAlpha, alphaOpExpr,
                                                walk(aoa, new ConsEnv(more, rest, isNested)).asInstanceOf[Expr]))
                      else paren(SCond(vinfo, idInAlpha, alphaOpWalk,
                                       walk(lhsOpWalk, new ConsEnv(more, rest, true)).asInstanceOf[Expr]))
                  }
                }
              case _ => SAssignOpApp(info, lhsWalk, op, exprWalk)
            }
        }
    }
    // For WithRewriter test...
    case SStmtUnit(info, stmts) =>
      SStmtUnit(info, walk(stmts, env).asInstanceOf[List[Stmt]])
    case SBlock(info, stmts, b) =>
      SBlock(info, walk(stmts, env).asInstanceOf[List[Stmt]], b)
    case SBracket(info, obj, index) =>
      SBracket(info, walk(obj, env).asInstanceOf[LHS],
               walk(index, env).asInstanceOf[Expr])
    case SCase(info, cond, body) =>
      SCase(info, walk(cond, env).asInstanceOf[Expr],
            body.map(b => walk(b, env)).asInstanceOf[List[Stmt]])
    case c@SCatch(info, id, body) => env match {
      case EmptyEnv() =>
        SCatch(info, id, body.map(b => walk(b, env)).asInstanceOf[List[Stmt]])
      case ConsEnv(withs, names, isNested) =>
        val (first, rest) = splitNames(names)
        SCatch(info, id,
               body.map(b => walk(b, new ConsEnv(withs, List(List(id.getText)++first)++rest, isNested))).asInstanceOf[List[Stmt]])
    }
    case SCond(info, cond, trueBranch, falseBranch) =>
      SCond(info, walk(cond, env).asInstanceOf[Expr],
            walk(trueBranch, env).asInstanceOf[Expr],
            walk(falseBranch, env).asInstanceOf[Expr])
    case SDoWhile(info, body, cond) =>
      SDoWhile(info, walk(body, env).asInstanceOf[Stmt],
               walk(cond, env).asInstanceOf[Expr])
    case SDot(info, obj, member) =>
      SDot(info, walk(obj, env).asInstanceOf[LHS], member)
    case SExprList(info, exprs) =>
      SExprList(info, exprs.map(e => walk(e, env)).asInstanceOf[List[Expr]])
    case SExprStmt(info, expr, isInternal) =>
      SExprStmt(info, walk(expr, env).asInstanceOf[Expr], isInternal)
    case SField(info, prop, expr) =>
      SField(info, prop, walk(expr, env).asInstanceOf[Expr])
    case SFor(info, init, cond, action, body) =>
      SFor(info, walk(init, env).asInstanceOf[Option[Expr]],
           walk(cond, env).asInstanceOf[Option[Expr]],
           walk(action, env).asInstanceOf[Option[Expr]],
           walk(body, env).asInstanceOf[Stmt])
    case fi@SForIn(info, lhs, expr, body) =>
      val lhsWalk = walk(lhs, env).asInstanceOf[LHS]
      val exprWalk = walk(expr, env).asInstanceOf[Expr]
      val bodyWalk = walk(body, env).asInstanceOf[Stmt]
      val fiWalk = SForIn(info, lhsWalk, exprWalk, bodyWalk)
      env match {
        case EmptyEnv() => fiWalk
        case ConsEnv(Nil, names, isNested) =>
          signal("Non-empty with-rewriting environment should have at least one with-object name.", fi)
          fi
        case ConsEnv(alpha::others, names, isNested) => lhs match {
          case SVarRef(vinfo, id) =>
            val (first, rest) = splitNames(names)
            if (first.contains(id.getText)) SForIn(info, lhs, exprWalk, bodyWalk)
            else {
              val lhsInAlpha = SInfixOpApp(vinfo,
                                           SStringLiteral(vinfo, "\"", id.getText),
                                           inOp(vinfo), SVarRef(vinfo, alpha))
              val alphaDotLhs = SDot(vinfo, SVarRef(vinfo, alpha), id)
              val alphaDotLhsOp = SForIn(vinfo, alphaDotLhs, expr, body)
              val alphaDotLhsOpWalk = SForIn(vinfo, alphaDotLhs, exprWalk, bodyWalk)
              val lhsOpWalk = SForIn(vinfo, lhs, exprWalk, bodyWalk)
              others match {
                case Nil =>
                  if (isNested) SIf(vinfo, lhsInAlpha, alphaDotLhsOp, Some(fi.asInstanceOf[Stmt]))
                  else SIf(vinfo, lhsInAlpha, alphaDotLhsOpWalk, Some(lhsOpWalk.asInstanceOf[Stmt]))
                case more =>
                  if (isNested) SIf(vinfo, lhsInAlpha, alphaDotLhsOp,
                                    Some(walk(fi, new ConsEnv(more, rest, isNested)).asInstanceOf[Stmt]))
                  else SIf(vinfo, lhsInAlpha, alphaDotLhsOpWalk,
                           Some(walk(lhsOpWalk, new ConsEnv(more, rest, true)).asInstanceOf[Stmt]))
              }
            }
          case _ => fiWalk
        }
      }
    case fv:ForVar =>
      signal("ForVar should be replaced by the hoister.", fv)
      fv
    case fv:ForVarIn =>
      signal("ForVarIn should be replaced by the hoister.", fv)
      fv
    case fa@SFunApp(info, fun, args) =>
      val mapArgsWalk = args.map(e => walk(e, env).asInstanceOf[Expr])
      val funAppWalk = SFunApp(info, walk(fun, env).asInstanceOf[LHS], mapArgsWalk)
      env match {
        case EmptyEnv() => funAppWalk
        case ConsEnv(withs, names, isNested) => withs match {
          case Nil =>
            signal("Non-empty with-rewriting environment should have at least one with-object name.", fa)
            fa
          case alpha::others => fun match {
            case SVarRef(vinfo, id) =>
              val (first, rest) = splitNames(names)
              val faMapArgsWalk = SFunApp(vinfo, fun, mapArgsWalk)
              if (first.contains(id.getText)) faMapArgsWalk
              else {
                val lhsInAlpha = SInfixOpApp(vinfo,
                                             SStringLiteral(vinfo, "\"", id.getText),
                                             inOp(vinfo), SVarRef(vinfo, alpha))
                val alphaDotLhs = SDot(vinfo, SVarRef(vinfo, alpha), id)
                val alphaDotLhsExpr = SFunApp(vinfo, alphaDotLhs, args)
                val alphaDotLhsExprWalk = SFunApp(vinfo, alphaDotLhs, mapArgsWalk)
                others match {
                  case Nil =>
                    if (isNested) paren(SCond(vinfo, lhsInAlpha, alphaDotLhsExpr, fa))
                    else paren(SCond(vinfo, lhsInAlpha, alphaDotLhsExprWalk, faMapArgsWalk))
                  case more =>
                    if (isNested) paren(SCond(vinfo, lhsInAlpha, alphaDotLhsExpr,
                                              walk(fa, new ConsEnv(more, rest, isNested)).asInstanceOf[Expr]))
                    else paren(SCond(vinfo, lhsInAlpha, alphaDotLhsExprWalk,
                                     walk(faMapArgsWalk, new ConsEnv(more, rest, true)).asInstanceOf[Expr]))
                }
              }
            case _ => funAppWalk
          }
        }
    }
    case fd@SFunDecl(info, SFunctional(fds, vds, body, name, params), strict) =>
      SFunDecl(info, mkFunctional(fds, vds, name, params, body, env, fd), strict)
    case fe@SFunExpr(info, SFunctional(fds, vds, body, name, params)) =>
      SFunExpr(info, mkFunctional(fds, vds, name, params, body, env, fe))
    case gp@SGetProp(info, prop, SFunctional(fds, vds, body, name, params)) =>
      SGetProp(info, prop, mkFunctional(fds, vds, name, Nil, body, env, gp))
    case id@SId(info, text, _, _) => env match {
      case EmptyEnv() => id
      case ConsEnv(withs, names, isNested) => withs match {
        case Nil =>
          signal("Non-empty with-rewriting environment should have at least one with-object name.", id)
          id
        case alpha::others =>
          val (first, rest) = splitNames(names)
          if (first.contains(text)) id
          else {
            val idInAlpha = SInfixOpApp(info,
                                        SStringLiteral(info, "\"", text),
                                        inOp(info), SVarRef(info, alpha))
            val alphaDotId = SDot(info, SVarRef(info, alpha), id)
            others match {
              case Nil => paren(SCond(info, idInAlpha, alphaDotId, id.asInstanceOf[Expr]))
              case more => paren(SCond(info, idInAlpha, alphaDotId,
                                       walk(id, new ConsEnv(more, rest, isNested)).asInstanceOf[Expr]))
            }
          }
      }
    }
    case SIf(info, cond, trueBranch, falseBranch) => falseBranch match {
      case None =>
        SIf(info, walk(cond, env).asInstanceOf[Expr],
            walk(trueBranch, env).asInstanceOf[Stmt],
            None)
      case Some(falseStmt) =>
        SIf(info, walk(cond, env).asInstanceOf[Expr],
            walk(trueBranch, env).asInstanceOf[Stmt],
            Some(walk(falseStmt, env).asInstanceOf[Stmt]))
    }
    case SInfixOpApp(info, left, op, right) =>
      SInfixOpApp(info, walk(left, env).asInstanceOf[Expr],
                  op, walk(right, env).asInstanceOf[Expr])
    case SLabelStmt(info, label, stmt) =>
      SLabelStmt(info, label, walk(stmt, env).asInstanceOf[Stmt])
    case SNew(info, lhs) =>
      SNew(info, walk(lhs, env).asInstanceOf[LHS])
    case SObjectExpr(info, members) =>
      SObjectExpr(info, members.map(m => walk(m, env)).asInstanceOf[List[Member]])
    case SParenthesized(info, expr) =>
      SParenthesized(info, walk(expr, env).asInstanceOf[Expr])
    case poa@SPrefixOpApp(info, op, right) => env match {
      case EmptyEnv() => SPrefixOpApp(info, op, walk(right, env).asInstanceOf[Expr])
      case ConsEnv(withs, names, isNested) => withs match {
        case Nil =>
          signal("Non-empty with-rewriting environment should have at least one with-object name.", poa)
          poa
        case alpha::others => right match {
          case SVarRef(vinfo, id) =>
            val (first, rest) = splitNames(names)
            if (first.contains(id.getText)) poa
            else {
              val idInAlpha = SInfixOpApp(vinfo,
                                          SStringLiteral(vinfo, "\"", id.getText),
                                          inOp(vinfo), SVarRef(vinfo, alpha))
              val opAlphaDotId = SPrefixOpApp(vinfo, op, SDot(vinfo, SVarRef(vinfo, alpha), id))
              others match {
                case Nil => paren(SCond(vinfo, idInAlpha, opAlphaDotId, poa))
                case more => paren(SCond(vinfo, idInAlpha, opAlphaDotId,
                                         walk(poa, new ConsEnv(more, rest, isNested)).asInstanceOf[Expr]))
              }
            }
          case _ => SPrefixOpApp(info, op, walk(right, env).asInstanceOf[Expr])
        }
      }
    }
    case SProgram(info, STopLevel(fds, vds, ses)) =>
      SProgram(info, STopLevel((if (forTest) List(toObjectFnDecl) else Nil)++
                               fds.map(fd => walk(fd, env).asInstanceOf[FunDecl]),
                               vds.map(vd => walk(vd, env).asInstanceOf[VarDecl]),
                               ses.map(s => walk(s, env).asInstanceOf[SourceElements])))
    case SSourceElements(info, stmts, strict) =>
      SSourceElements(info, stmts.map(s => walk(s, env).asInstanceOf[SourceElement]), strict)
    case SReturn(info, expr) =>
      SReturn(info, walk(expr, env).asInstanceOf[Option[Expr]])
    case sp@SSetProp(info, prop, SFunctional(fds, vds, body, name, params)) =>
      SSetProp(info, prop, mkFunctional(fds, vds, name, params, body, env, sp))
    case SSwitch(info, cond, frontCases, defjs, backCases) =>
      SSwitch(info, walk(cond, env).asInstanceOf[Expr],
              walk(frontCases, env).asInstanceOf[List[Case]],
              walk(defjs, env).asInstanceOf[Option[List[Stmt]]],
              walk(backCases, env).asInstanceOf[List[Case]])
    case SThrow(info, expr) =>
      SThrow(info, walk(expr, env).asInstanceOf[Expr])
    case STry(info, body, catchBlock, fin) =>
      STry(info, body.map(b => walk(b, env)).asInstanceOf[List[Stmt]],
           walk(catchBlock, env).asInstanceOf[Option[Catch]],
           fin.map(f => walk(f, env)).asInstanceOf[Option[List[Stmt]]])
    case ua@SUnaryAssignOpApp(info, lhs, op) => env match {
      case EmptyEnv() => SUnaryAssignOpApp(info, walk(lhs, env).asInstanceOf[LHS], op)
      case ConsEnv(withs, names, isNested) => withs match {
        case Nil =>
          signal("Non-empty with-rewriting environment should have at least one with-object name.", ua)
          ua
        case alpha::others => lhs match {
          case SVarRef(vinfo, id) =>
            val (first, rest) = splitNames(names)
            if (first.contains(id.getText)) ua
            else {
              val lhsInAlpha = SInfixOpApp(vinfo,
                                           SStringLiteral(vinfo, "\"", id.getText),
                                           inOp(vinfo), SVarRef(vinfo, alpha))
              val alphaDotLhsOp = SUnaryAssignOpApp(vinfo, SDot(vinfo, SVarRef(vinfo, alpha), id), op)
              others match {
                case Nil => paren(SCond(vinfo, lhsInAlpha, alphaDotLhsOp, ua))
                case more => paren(SCond(vinfo, lhsInAlpha, alphaDotLhsOp,
                                         walk(ua, new ConsEnv(more, rest, isNested)).asInstanceOf[Expr]))
              }
            }
          case _ => SUnaryAssignOpApp(info, walk(lhs, env).asInstanceOf[LHS], op)
        }
      }
    }
    case SVarDecl(info, name, expr, strict) =>
      SVarDecl(info, name, walk(expr, env).asInstanceOf[Option[Expr]], strict)
    case vr@SVarRef(info, id) => env match {
      case EmptyEnv() => vr
      case ConsEnv(withs, names, isNested) => withs match {
        case Nil =>
          signal("Non-empty with-rewriting environment should have at least one with-object name."+id.getText, vr)
          vr
        case alpha::others =>
          val (first, rest) = splitNames(names)
          if (first.contains(id.getText)) vr
          else {
            val idInAlpha = SInfixOpApp(info, SStringLiteral(info, "\"", id.getText),
                                        inOp(info),
                                        SVarRef(info, alpha))
            val alphaDotId = SDot(info, SVarRef(info, alpha), id)
            others match {
              case Nil =>
                paren(SCond(info, idInAlpha, alphaDotId, vr))
              case more =>
                paren(SCond(info, idInAlpha, alphaDotId,
                            walk(vr, new ConsEnv(more, rest, isNested)).asInstanceOf[Expr]))
          }
        }
      }
    }
    case vs:VarStmt =>
      signal("VarStmt should be replaced by the hoister.", vs)
      vs
    case SWhile(info, cond, body) =>
      SWhile(info, walk(cond, env).asInstanceOf[Expr],
             walk(body, env).asInstanceOf[Stmt])
    /*
     * rewrite_s[|with (e) s | Gamma|] =
     *   If Gamma = EmptyEnv
     *   Then { alpha = toObject(rewrite_e[|e | Gamma|]);
     *          rewrite_s[|s | <alpha, [], false>|]
     *   Else Let Gamma = ConsEnv(phi, varphi, beta> where alpha \not\in phi
     *        { alpha = toObject(rewrite_e[|e | Gamma|]);
     *          rewrite_s[|s | <phi alpha, varphi, beta>|]
     */
    case SWith(info, expr, stmt) =>
      val fresh = if (forTest) SId(info, freshNameTest, None, true) else freshName(info)
      val vs =
          if (forTest)
            SVarStmt(info,
                     List(SVarDecl(info, fresh,
                                   Some(SFunApp(info, SVarRef(info, toObjectFnId),
                                                List(walk(expr, env)).asInstanceOf[List[Expr]])),
                                   false)))
          else SExprStmt(info,
                         SAssignOpApp(info, SVarRef(info, fresh), assignOp(info),
                                      SFunApp(info, SVarRef(info, toObjectId(info)),
                                              List(walk(expr, env)).asInstanceOf[List[Expr]])),
                         true)
      val body = env match {
        case EmptyEnv() =>
          walk(stmt, new ConsEnv(List(fresh), List(List()), false))
        case ConsEnv(withs, names, isNested) =>
          walk(stmt, new ConsEnv(fresh::withs, List()::names, isNested))
      }
      SBlock(info, List(vs, body.asInstanceOf[Stmt]), false)
    case xs:List[_] => xs.map(x => walk(x, env))
    case xs:Option[_] => xs.map(x => walk(x, env))
    case _ => node
  }
}
