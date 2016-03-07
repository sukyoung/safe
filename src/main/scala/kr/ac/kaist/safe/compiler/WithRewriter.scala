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

import kr.ac.kaist.safe.exceptions.SAFEError.error
import kr.ac.kaist.safe.exceptions.StaticError
import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.safe_util.{ NodeFactory => NF, NodeUtil => NU }
import kr.ac.kaist.safe.useful.ErrorLog

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
  def signal(msg: String, node: Node): Unit = errors.signal(msg, node)
  def signal(node: Node, msg: String): Unit = errors.signal(msg, node)
  def signal(error: StaticError): Unit = errors.signal(error)
  def getErrors: List[StaticError] = errors.errors

  abstract class Env()
  case class EmptyEnv() extends Env
  case class ConsEnv(withs: List[Id], names: List[List[String]], isNested: Boolean) extends Env

  def doit: Program = NU.simplifyWalker.walk(walk(program, EmptyEnv()).asInstanceOf[Program]).asInstanceOf[Program]

  // For WithRewriter tests...
  def freshNameTest: String = "$f_" + System.nanoTime
  val toObjectInfo = NF.makeASTNodeInfo(NU.makeSpan("_gen_toObject"))
  def mkId(name: String): Id = Id(toObjectInfo, name, None, false)
  def mkVarRef(id: Id): VarRef = VarRef(toObjectInfo, id)
  val toObjectFnId = mkId("toObject_" + freshNameTest)
  val paramExpr = mkVarRef(mkId("x"))
  def mkEq(name: String): InfixOpApp =
    InfixOpApp(toObjectInfo, mkVarRef(mkId("type")), Op(toObjectInfo, "=="),
      StringLiteral(toObjectInfo, "\"", name))
  def mkRes(name: LHS): Return =
    Return(
      toObjectInfo,
      Some(New(toObjectInfo, FunApp(toObjectInfo, name, List(paramExpr))))
    )
  def mkIf(tname: String, cname: LHS, falseB: Option[Stmt]): If =
    If(toObjectInfo, mkEq(tname), mkRes(cname), falseB)
  val false3 = Some(Return(toObjectInfo, Some(paramExpr)))
  val false2 = Some(mkIf("boolean", mkVarRef(mkId("Boolean")), false3))
  val false1 = Some(mkIf("string", mkVarRef(mkId("String")), false2))
  val typeofCall =
    Some(FunApp(toObjectInfo, mkVarRef(mkId("typeof")), List(paramExpr)))
  val toObjectBody =
    SourceElements(
      toObjectInfo,
      List(
        VarStmt(
          toObjectInfo,
          List(VarDecl(toObjectInfo, mkId("type"), typeofCall, false))
        ),
        mkIf("number", mkVarRef(mkId("Number")), false1)
      ), false
    )
  val toObjectFnDecl =
    FunDecl(
      toObjectInfo,
      Functional(toObjectInfo, List(), List(), toObjectBody, toObjectFnId, List(mkId("x")), NF.generatedString),
      false
    )

  // For SAFE
  val internalSymbol = NU.internalSymbol
  def freshName(info: ASTNodeInfo): Id = {
    val name = NU.freshName("alpha")
    Id(info, name, Some(name), true)
  }
  val toObjectName = NU.toObjectName
  def toObjectId(info: ASTNodeInfo): Id = Id(info, toObjectName, Some(toObjectName), false)

  def assignOp(info: ASTNodeInfo): Op = Op(info, "=")
  def inOp(info: ASTNodeInfo): Op = Op(info, "in")
  def paren(expr: Expr): Parenthesized = Parenthesized(expr.info, expr)
  def splitNames(names: List[List[String]]): (List[String], List[List[String]]) = names match {
    case hd :: tl => (hd, tl)
    case _ => (Nil, Nil)
  }
  def mkBody(fds: List[FunDecl], vds: List[VarDecl], name: Id,
    params: List[Id], body: SourceElements, env: Env, node: Node): SourceElements = env match {
    case EmptyEnv() =>
      SourceElements(
        body.info,
        walk(body.body, env).asInstanceOf[List[SourceElement]],
        body.strict
      )
    case ConsEnv(withs, names, isNested) =>
      val (first, rest) = splitNames(names)
      var ids = params.map(_.text) ++
        fds.map(fd => fd match { case FunDecl(_, f, _) => f.name.text }) ++
        vds.map(vd => vd match { case VarDecl(_, n, _, _) => n.text }) ++ first
      ids = (List(name.text)) ++ ids
      SourceElements(
        body.info,
        walk(
        body.body,
        new ConsEnv(withs, ids :: rest, isNested)
      ).asInstanceOf[List[SourceElement]],
        body.strict
      )
  }
  def mkFunctional(info: ASTNodeInfo, fds: List[FunDecl], vds: List[VarDecl], name: Id,
    params: List[Id], bodyS: String, body: SourceElements, env: Env, node: Node): Functional =
    Functional(info, fds.map(fd => walk(fd, env).asInstanceOf[FunDecl]),
      vds.map(vd => walk(vd, env).asInstanceOf[VarDecl]),
      mkBody(fds, vds, name, params, body, env, node), name, params, bodyS)

  def walk(node: Any, env: Env): Any = node match {
    case ArrayExpr(info, elements) =>
      ArrayExpr(info, elements.map(e => walk(e, env)).asInstanceOf[List[Option[Expr]]])
    case aoa @ AssignOpApp(info, lhs, op, right) =>
      val lhsWalk = walk(lhs, env).asInstanceOf[LHS]
      val exprWalk = walk(right, env).asInstanceOf[Expr]
      env match {
        case EmptyEnv() => AssignOpApp(info, lhsWalk, op, exprWalk)
        case ConsEnv(withs, names, isNested) => withs match {
          case Nil =>
            signal("Non-empty with-rewriting environment should have at least one with-object name.", aoa)
            aoa
          case alpha :: others =>
            val (first, rest) = splitNames(names)
            NU.unwrapParen(lhs) match {
              case VarRef(vinfo, id) =>
                if (first.contains(id.text)) AssignOpApp(vinfo, lhs, op, exprWalk)
                else {
                  val idInAlpha = InfixOpApp(
                    vinfo,
                    StringLiteral(vinfo, "\"", id.text),
                    inOp(vinfo), VarRef(vinfo, alpha)
                  )
                  val alphaDotLhs = Dot(vinfo, VarRef(vinfo, alpha), id)
                  val alphaOpExpr = AssignOpApp(vinfo, alphaDotLhs, op, right)
                  val alphaOpWalk = AssignOpApp(vinfo, alphaDotLhs, op, exprWalk)
                  val lhsOpWalk = AssignOpApp(vinfo, lhs, op, exprWalk)
                  others match {
                    case Nil =>
                      if (isNested) paren(Cond(vinfo, idInAlpha, alphaOpExpr, aoa))
                      else paren(Cond(vinfo, idInAlpha, alphaOpWalk, lhsOpWalk))
                    case more =>
                      if (isNested) paren(Cond(vinfo, idInAlpha, alphaOpExpr,
                        walk(aoa, new ConsEnv(more, rest, isNested)).asInstanceOf[Expr]))
                      else paren(Cond(vinfo, idInAlpha, alphaOpWalk,
                        walk(lhsOpWalk, new ConsEnv(more, rest, true)).asInstanceOf[Expr]))
                  }
                }
              case _ => AssignOpApp(info, lhsWalk, op, exprWalk)
            }
        }
      }
    // For WithRewriter test...
    case StmtUnit(info, stmts) =>
      StmtUnit(info, walk(stmts, env).asInstanceOf[List[Stmt]])
    case Block(info, stmts, b) =>
      Block(info, walk(stmts, env).asInstanceOf[List[Stmt]], b)
    case Bracket(info, obj, index) =>
      Bracket(info, walk(obj, env).asInstanceOf[LHS],
        walk(index, env).asInstanceOf[Expr])
    case Case(info, cond, body) =>
      Case(info, walk(cond, env).asInstanceOf[Expr],
        body.map(b => walk(b, env)).asInstanceOf[List[Stmt]])
    case c @ Catch(info, id, body) => env match {
      case EmptyEnv() =>
        Catch(info, id, body.map(b => walk(b, env)).asInstanceOf[List[Stmt]])
      case ConsEnv(withs, names, isNested) =>
        val (first, rest) = splitNames(names)
        Catch(info, id,
          body.map(b => walk(b, new ConsEnv(withs, List(List(id.text) ++ first) ++ rest, isNested))).asInstanceOf[List[Stmt]])
    }
    case Cond(info, cond, trueBranch, falseBranch) =>
      Cond(info, walk(cond, env).asInstanceOf[Expr],
        walk(trueBranch, env).asInstanceOf[Expr],
        walk(falseBranch, env).asInstanceOf[Expr])
    case DoWhile(info, body, cond) =>
      DoWhile(info, walk(body, env).asInstanceOf[Stmt],
        walk(cond, env).asInstanceOf[Expr])
    case Dot(info, obj, member) =>
      Dot(info, walk(obj, env).asInstanceOf[LHS], member)
    case ExprList(info, exprs) =>
      ExprList(info, exprs.map(e => walk(e, env)).asInstanceOf[List[Expr]])
    case ExprStmt(info, expr, isInternal) =>
      ExprStmt(info, walk(expr, env).asInstanceOf[Expr], isInternal)
    case Field(info, prop, expr) =>
      Field(info, prop, walk(expr, env).asInstanceOf[Expr])
    case For(info, init, cond, action, body) =>
      For(info, walk(init, env).asInstanceOf[Option[Expr]],
        walk(cond, env).asInstanceOf[Option[Expr]],
        walk(action, env).asInstanceOf[Option[Expr]],
        walk(body, env).asInstanceOf[Stmt])
    case fi @ ForIn(info, lhs, expr, body) =>
      val lhsWalk = walk(lhs, env).asInstanceOf[LHS]
      val exprWalk = walk(expr, env).asInstanceOf[Expr]
      val bodyWalk = walk(body, env).asInstanceOf[Stmt]
      val fiWalk = ForIn(info, lhsWalk, exprWalk, bodyWalk)
      env match {
        case EmptyEnv() => fiWalk
        case ConsEnv(Nil, names, isNested) =>
          signal("Non-empty with-rewriting environment should have at least one with-object name.", fi)
          fi
        case ConsEnv(alpha :: others, names, isNested) => lhs match {
          case VarRef(vinfo, id) =>
            val (first, rest) = splitNames(names)
            if (first.contains(id.text)) ForIn(info, lhs, exprWalk, bodyWalk)
            else {
              val lhsInAlpha = InfixOpApp(
                vinfo,
                StringLiteral(vinfo, "\"", id.text),
                inOp(vinfo), VarRef(vinfo, alpha)
              )
              val alphaDotLhs = Dot(vinfo, VarRef(vinfo, alpha), id)
              val alphaDotLhsOp = ForIn(vinfo, alphaDotLhs, expr, body)
              val alphaDotLhsOpWalk = ForIn(vinfo, alphaDotLhs, exprWalk, bodyWalk)
              val lhsOpWalk = ForIn(vinfo, lhs, exprWalk, bodyWalk)
              others match {
                case Nil =>
                  if (isNested) If(vinfo, lhsInAlpha, alphaDotLhsOp, Some(fi.asInstanceOf[Stmt]))
                  else If(vinfo, lhsInAlpha, alphaDotLhsOpWalk, Some(lhsOpWalk.asInstanceOf[Stmt]))
                case more =>
                  if (isNested) If(vinfo, lhsInAlpha, alphaDotLhsOp,
                    Some(walk(fi, new ConsEnv(more, rest, isNested)).asInstanceOf[Stmt]))
                  else If(vinfo, lhsInAlpha, alphaDotLhsOpWalk,
                    Some(walk(lhsOpWalk, new ConsEnv(more, rest, true)).asInstanceOf[Stmt]))
              }
            }
          case _ => fiWalk
        }
      }
    case fv: ForVar =>
      signal("ForVar should be replaced by the hoister.", fv)
      fv
    case fv: ForVarIn =>
      signal("ForVarIn should be replaced by the hoister.", fv)
      fv
    case fa @ FunApp(info, fun, args) =>
      val mapArgsWalk = args.map(e => walk(e, env).asInstanceOf[Expr])
      val funAppWalk = FunApp(info, walk(fun, env).asInstanceOf[LHS], mapArgsWalk)
      env match {
        case EmptyEnv() => funAppWalk
        case ConsEnv(withs, names, isNested) => withs match {
          case Nil =>
            signal("Non-empty with-rewriting environment should have at least one with-object name.", fa)
            fa
          case alpha :: others => fun match {
            case VarRef(vinfo, id) =>
              val (first, rest) = splitNames(names)
              val faMapArgsWalk = FunApp(vinfo, fun, mapArgsWalk)
              if (first.contains(id.text)) faMapArgsWalk
              else {
                val lhsInAlpha = InfixOpApp(
                  vinfo,
                  StringLiteral(vinfo, "\"", id.text),
                  inOp(vinfo), VarRef(vinfo, alpha)
                )
                val alphaDotLhs = Dot(vinfo, VarRef(vinfo, alpha), id)
                val alphaDotLhsExpr = FunApp(vinfo, alphaDotLhs, args)
                val alphaDotLhsExprWalk = FunApp(vinfo, alphaDotLhs, mapArgsWalk)
                others match {
                  case Nil =>
                    if (isNested) paren(Cond(vinfo, lhsInAlpha, alphaDotLhsExpr, fa))
                    else paren(Cond(vinfo, lhsInAlpha, alphaDotLhsExprWalk, faMapArgsWalk))
                  case more =>
                    if (isNested) paren(Cond(vinfo, lhsInAlpha, alphaDotLhsExpr,
                      walk(fa, new ConsEnv(more, rest, isNested)).asInstanceOf[Expr]))
                    else paren(Cond(vinfo, lhsInAlpha, alphaDotLhsExprWalk,
                      walk(faMapArgsWalk, new ConsEnv(more, rest, true)).asInstanceOf[Expr]))
                }
              }
            case _ => funAppWalk
          }
        }
      }
    case fd @ FunDecl(info, Functional(i, fds, vds, body, name, params, bodyS), strict) =>
      FunDecl(info, mkFunctional(i, fds, vds, name, params, bodyS, body, env, fd), strict)
    case fe @ FunExpr(info, Functional(i, fds, vds, body, name, params, bodyS)) =>
      FunExpr(info, mkFunctional(i, fds, vds, name, params, bodyS, body, env, fe))
    case gp @ GetProp(info, prop, Functional(i, fds, vds, body, name, params, bodyS)) =>
      GetProp(info, prop, mkFunctional(i, fds, vds, name, Nil, bodyS, body, env, gp))
    case id @ Id(info, text, _, _) => env match {
      case EmptyEnv() => id
      case ConsEnv(withs, names, isNested) => withs match {
        case Nil =>
          signal("Non-empty with-rewriting environment should have at least one with-object name.", id)
          id
        case alpha :: others =>
          val (first, rest) = splitNames(names)
          if (first.contains(text)) id
          else {
            val idInAlpha = InfixOpApp(
              info,
              StringLiteral(info, "\"", text),
              inOp(info), VarRef(info, alpha)
            )
            val alphaDotId = Dot(info, VarRef(info, alpha), id)
            others match {
              case Nil => paren(Cond(info, idInAlpha, alphaDotId, id.asInstanceOf[Expr]))
              case more => paren(Cond(info, idInAlpha, alphaDotId,
                walk(id, new ConsEnv(more, rest, isNested)).asInstanceOf[Expr]))
            }
          }
      }
    }
    case If(info, cond, trueBranch, falseBranch) => falseBranch match {
      case None =>
        If(info, walk(cond, env).asInstanceOf[Expr],
          walk(trueBranch, env).asInstanceOf[Stmt],
          None)
      case Some(falseStmt) =>
        If(info, walk(cond, env).asInstanceOf[Expr],
          walk(trueBranch, env).asInstanceOf[Stmt],
          Some(walk(falseStmt, env).asInstanceOf[Stmt]))
    }
    case InfixOpApp(info, left, op, right) =>
      InfixOpApp(info, walk(left, env).asInstanceOf[Expr],
        op, walk(right, env).asInstanceOf[Expr])
    case LabelStmt(info, label, stmt) =>
      LabelStmt(info, label, walk(stmt, env).asInstanceOf[Stmt])
    case New(info, lhs) =>
      New(info, walk(lhs, env).asInstanceOf[LHS])
    case ObjectExpr(info, members) =>
      ObjectExpr(info, members.map(m => walk(m, env)).asInstanceOf[List[Member]])
    case Parenthesized(info, expr) =>
      Parenthesized(info, walk(expr, env).asInstanceOf[Expr])
    case poa @ PrefixOpApp(info, op, right) => env match {
      case EmptyEnv() => PrefixOpApp(info, op, walk(right, env).asInstanceOf[Expr])
      case ConsEnv(withs, names, isNested) => withs match {
        case Nil =>
          signal("Non-empty with-rewriting environment should have at least one with-object name.", poa)
          poa
        case alpha :: others => right match {
          case VarRef(vinfo, id) =>
            val (first, rest) = splitNames(names)
            if (first.contains(id.text)) poa
            else {
              val idInAlpha = InfixOpApp(
                vinfo,
                StringLiteral(vinfo, "\"", id.text),
                inOp(vinfo), VarRef(vinfo, alpha)
              )
              val opAlphaDotId = PrefixOpApp(vinfo, op, Dot(vinfo, VarRef(vinfo, alpha), id))
              others match {
                case Nil => paren(Cond(vinfo, idInAlpha, opAlphaDotId, poa))
                case more => paren(Cond(vinfo, idInAlpha, opAlphaDotId,
                  walk(poa, new ConsEnv(more, rest, isNested)).asInstanceOf[Expr]))
              }
            }
          case _ => PrefixOpApp(info, op, walk(right, env).asInstanceOf[Expr])
        }
      }
    }
    case Program(info, TopLevel(i, fds, vds, ses)) =>
      Program(info, TopLevel(i, (if (forTest) List(toObjectFnDecl) else Nil) ++
        fds.map(fd => walk(fd, env).asInstanceOf[FunDecl]),
        vds.map(vd => walk(vd, env).asInstanceOf[VarDecl]),
        ses.map(s => walk(s, env).asInstanceOf[SourceElements])))
    case SourceElements(info, stmts, strict) =>
      SourceElements(info, stmts.map(s => walk(s, env).asInstanceOf[SourceElement]), strict)
    case Return(info, expr) =>
      Return(info, walk(expr, env).asInstanceOf[Option[Expr]])
    case sp @ SetProp(info, prop, Functional(i, fds, vds, body, name, params, bodyS)) =>
      SetProp(info, prop, mkFunctional(i, fds, vds, name, params, bodyS, body, env, sp))
    case Switch(info, cond, frontCases, defjs, backCases) =>
      Switch(info, walk(cond, env).asInstanceOf[Expr],
        walk(frontCases, env).asInstanceOf[List[Case]],
        walk(defjs, env).asInstanceOf[Option[List[Stmt]]],
        walk(backCases, env).asInstanceOf[List[Case]])
    case Throw(info, expr) =>
      Throw(info, walk(expr, env).asInstanceOf[Expr])
    case Try(info, body, catchBlock, fin) =>
      Try(info, body.map(b => walk(b, env)).asInstanceOf[List[Stmt]],
        walk(catchBlock, env).asInstanceOf[Option[Catch]],
        fin.map(f => walk(f, env)).asInstanceOf[Option[List[Stmt]]])
    case ua @ UnaryAssignOpApp(info, lhs, op) => env match {
      case EmptyEnv() => UnaryAssignOpApp(info, walk(lhs, env).asInstanceOf[LHS], op)
      case ConsEnv(withs, names, isNested) => withs match {
        case Nil =>
          signal("Non-empty with-rewriting environment should have at least one with-object name.", ua)
          ua
        case alpha :: others => lhs match {
          case VarRef(vinfo, id) =>
            val (first, rest) = splitNames(names)
            if (first.contains(id.text)) ua
            else {
              val lhsInAlpha = InfixOpApp(
                vinfo,
                StringLiteral(vinfo, "\"", id.text),
                inOp(vinfo), VarRef(vinfo, alpha)
              )
              val alphaDotLhsOp = UnaryAssignOpApp(vinfo, Dot(vinfo, VarRef(vinfo, alpha), id), op)
              others match {
                case Nil => paren(Cond(vinfo, lhsInAlpha, alphaDotLhsOp, ua))
                case more => paren(Cond(vinfo, lhsInAlpha, alphaDotLhsOp,
                  walk(ua, new ConsEnv(more, rest, isNested)).asInstanceOf[Expr]))
              }
            }
          case _ => UnaryAssignOpApp(info, walk(lhs, env).asInstanceOf[LHS], op)
        }
      }
    }
    case VarDecl(info, name, expr, strict) =>
      VarDecl(info, name, walk(expr, env).asInstanceOf[Option[Expr]], strict)
    case vr @ VarRef(info, id) => env match {
      case EmptyEnv() => vr
      case ConsEnv(withs, names, isNested) => withs match {
        case Nil =>
          signal("Non-empty with-rewriting environment should have at least one with-object name." + id.text, vr)
          vr
        case alpha :: others =>
          val (first, rest) = splitNames(names)
          if (first.contains(id.text)) vr
          else {
            val idInAlpha = InfixOpApp(info, StringLiteral(info, "\"", id.text),
              inOp(info),
              VarRef(info, alpha))
            val alphaDotId = Dot(info, VarRef(info, alpha), id)
            others match {
              case Nil =>
                paren(Cond(info, idInAlpha, alphaDotId, vr))
              case more =>
                paren(Cond(info, idInAlpha, alphaDotId,
                  walk(vr, new ConsEnv(more, rest, isNested)).asInstanceOf[Expr]))
            }
          }
      }
    }
    case vs: VarStmt =>
      signal("VarStmt should be replaced by the hoister.", vs)
      vs
    case While(info, cond, body) =>
      While(info, walk(cond, env).asInstanceOf[Expr],
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
    case With(info, expr, stmt) =>
      val fresh = if (forTest) Id(info, freshNameTest, None, true) else freshName(info)
      val vs =
        if (forTest)
          VarStmt(
            info,
            List(VarDecl(info, fresh,
              Some(FunApp(info, VarRef(info, toObjectFnId),
                List(walk(expr, env)).asInstanceOf[List[Expr]])),
              false))
          )
        else ExprStmt(
          info,
          AssignOpApp(info, VarRef(info, fresh), assignOp(info),
            FunApp(info, VarRef(info, toObjectId(info)),
              List(walk(expr, env)).asInstanceOf[List[Expr]])),
          true
        )
      val body = env match {
        case EmptyEnv() =>
          walk(stmt, new ConsEnv(List(fresh), List(List()), false))
        case ConsEnv(withs, names, isNested) =>
          walk(stmt, new ConsEnv(fresh :: withs, List() :: names, isNested))
      }
      Block(info, List(vs, body.asInstanceOf[Stmt]), false)
    case xs: List[_] => xs.map(x => walk(x, env))
    case xs: Option[_] => xs.map(x => walk(x, env))
    case _ => node
  }
}
