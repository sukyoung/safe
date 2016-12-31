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

package kr.ac.kaist.safe.ast_rewriter

import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.util.{ NodeUtil => NU, Span }

/* Rewrites a JavaScript source code using the with statement
 * to another one without using the with statement.
 */
class WithRewriter(program: Program, forTest: Boolean) {
  ////////////////////////////////////////////////////////////////
  // results
  ////////////////////////////////////////////////////////////////

  lazy val result: Program =
    NU.SimplifyWalker.walk(WithRewriteWalker.walk(program, EmptyEnv))
  lazy val excLog: ExcLog = new ExcLog

  ////////////////////////////////////////////////////////////////
  // private global
  ////////////////////////////////////////////////////////////////

  // environment
  private sealed abstract class Env
  private case object EmptyEnv extends Env
  private case class ConsEnv(
    withs: List[Id],
    names: List[List[String]],
    isNested: Boolean
  ) extends Env

  // default values
  private val TO_OBJ_INFO = NU.makeASTNodeInfo(Span("genToObject"))
  private lazy val TO_OBJ_FN_ID = mkId("toObject" + freshNameTest)
  private lazy val PARAM_EXPR = mkVarRef(mkId("x"))
  private lazy val FALSE = Some(mkIf("string", mkVarRef(mkId("String")),
    Some(mkIf("boolean", mkVarRef(mkId("Boolean")),
      Some(Return(TO_OBJ_INFO, Some(PARAM_EXPR)))))))
  private lazy val TYPEOF_CALL =
    Some(FunApp(TO_OBJ_INFO, mkVarRef(mkId("typeof")), List(PARAM_EXPR)))
  private lazy val TO_OBJ_BODY =
    SourceElements(
      TO_OBJ_INFO,
      List(
        VarStmt(
          TO_OBJ_INFO,
          List(VarDecl(TO_OBJ_INFO, mkId("type"), TYPEOF_CALL, false))
        ),
        mkIf("number", mkVarRef(mkId("Number")), FALSE)
      ), false
    )
  private lazy val TO_OBJ_FN_DECL =
    FunDecl(
      TO_OBJ_INFO,
      Functional(TO_OBJ_INFO, List(), List(), TO_OBJ_BODY, TO_OBJ_FN_ID, List(mkId("x")), NU.GENERATED_STR),
      false
    )

  ////////////////////////////////////////////////////////////////
  // helper function
  ////////////////////////////////////////////////////////////////

  // for WithRewriter tests
  private def freshNameTest: String = "$f" + System.nanoTime

  // creation helpers
  private def mkId(name: String): Id = Id(TO_OBJ_INFO, name, None, false)
  private def mkVarRef(id: Id): VarRef = VarRef(TO_OBJ_INFO, id)
  private def mkEq(name: String): InfixOpApp =
    InfixOpApp(TO_OBJ_INFO, mkVarRef(mkId("type")), Op(TO_OBJ_INFO, "=="),
      StringLiteral(TO_OBJ_INFO, "\"", name, false))
  private def mkRes(name: LHS): Return =
    Return(
      TO_OBJ_INFO,
      Some(New(TO_OBJ_INFO, FunApp(TO_OBJ_INFO, name, List(PARAM_EXPR))))
    )
  private def mkIf(tname: String, cname: LHS, falseB: Option[Stmt]): If =
    If(TO_OBJ_INFO, mkEq(tname), mkRes(cname), falseB)

  private object WithRewriteWalker extends ASTWalker {
    // utility
    def freshName(info: ASTNodeInfo): Id = {
      val name = NU.freshName("alpha")
      Id(info, name, Some(name), true)
    }
    def toObjectId(info: ASTNodeInfo): Id = Id(info, NU.INTERNAL_TO_OBJ, Some(NU.INTERNAL_TO_OBJ), false)
    def assignOp(info: ASTNodeInfo): Op = Op(info, "=")
    def inOp(info: ASTNodeInfo): Op = Op(info, "in")
    def paren(expr: Expr): Parenthesized = Parenthesized(expr.info, expr)
    def splitNames(names: List[List[String]]): (List[String], List[List[String]]) = names match {
      case hd :: tl => (hd, tl)
      case _ => (Nil, Nil)
    }
    def mkBody(fds: List[FunDecl], vds: List[VarDecl], name: Id,
      params: List[Id], body: SourceElements, env: Env, node: ASTNode): SourceElements = env match {
      case EmptyEnv =>
        SourceElements(body.info, body.body.map(walk(_, env)), body.strict)
      case ConsEnv(withs, names, isNested) =>
        val (first, rest) = splitNames(names)
        var ids = params.map(_.text) ++
          fds.map(fd => fd match { case FunDecl(_, f, _) => f.name.text }) ++
          vds.map(vd => vd match { case VarDecl(_, n, _, _) => n.text }) ++ first
        ids = (List(name.text)) ++ ids
        SourceElements(
          body.info,
          body.body.map(walk(_, new ConsEnv(withs, ids :: rest, isNested))),
          body.strict
        )
    }
    def mkFunctional(info: ASTNodeInfo, fds: List[FunDecl], vds: List[VarDecl], name: Id,
      params: List[Id], bodyS: String, body: SourceElements, env: Env, node: ASTNode): Functional =
      Functional(info, fds.map(walk(_, env)),
        vds.map(walk(_, env)),
        mkBody(fds, vds, name, params, body, env, node), name, params, bodyS)

    // walk overloading
    def walk(node: Program, env: Env): Program = node match {
      case Program(info, TopLevel(i, fds, vds, ses)) =>
        Program(info, TopLevel(i, (if (forTest) List(TO_OBJ_FN_DECL) else Nil) ++
          fds.map(fd => walk(fd, env)),
          vds.map(vd => walk(vd, env)),
          ses.map(s => walk(s, env))))
    }

    def walk(node: SourceElement, env: Env): SourceElement = node match {
      case s: Stmt => walk(s, env)
    }

    def walk(node: Stmt, env: Env): Stmt = node match {
      // for WithRewriter test
      case StmtUnit(info, stmts) =>
        StmtUnit(info, stmts.map(walk(_, env)))
      case ABlock(info, stmts, b) =>
        ABlock(info, stmts.map(walk(_, env)), b)
      case DoWhile(info, body, cond) =>
        DoWhile(info, walk(body, env), walk(cond, env))
      case ExprStmt(info, expr, isInternal) =>
        ExprStmt(info, walk(expr, env), isInternal)
      case For(info, init, cond, action, body) =>
        For(info, init.map(walk(_, env)), cond.map(walk(_, env)),
          action.map(walk(_, env)), walk(body, env))
      case fi @ ForIn(info, lhs, expr, body) =>
        val lhsWalk = walk(lhs, env)
        val exprWalk = walk(expr, env)
        val bodyWalk = walk(body, env)
        val fiWalk = ForIn(info, lhsWalk, exprWalk, bodyWalk)
        env match {
          case EmptyEnv => fiWalk
          case ConsEnv(Nil, names, isNested) =>
            excLog.signal(NoWithObjError(fi))
            fi
          case ConsEnv(alpha :: others, names, isNested) => lhs match {
            case VarRef(vinfo, id) =>
              val (first, rest) = splitNames(names)
              if (first.contains(id.text)) ForIn(info, lhs, exprWalk, bodyWalk)
              else {
                val lhsInAlpha = InfixOpApp(
                  vinfo,
                  StringLiteral(vinfo, "\"", id.text, false),
                  inOp(vinfo), VarRef(vinfo, alpha)
                )
                val alphaDotLhs = Dot(vinfo, VarRef(vinfo, alpha), id)
                val alphaDotLhsOp = ForIn(vinfo, alphaDotLhs, expr, body)
                val alphaDotLhsOpWalk = ForIn(vinfo, alphaDotLhs, exprWalk, bodyWalk)
                val lhsOpWalk = ForIn(vinfo, lhs, exprWalk, bodyWalk)
                others match {
                  case Nil =>
                    if (isNested) If(vinfo, lhsInAlpha, alphaDotLhsOp, Some(fi))
                    else If(vinfo, lhsInAlpha, alphaDotLhsOpWalk, Some(lhsOpWalk))
                  case more =>
                    if (isNested) If(vinfo, lhsInAlpha, alphaDotLhsOp,
                      Some(walk(fi, new ConsEnv(more, rest, isNested))))
                    else If(vinfo, lhsInAlpha, alphaDotLhsOpWalk,
                      Some(walk(lhsOpWalk, new ConsEnv(more, rest, true))))
                }
              }
            case _ => fiWalk
          }
        }
      case fv: ForVar =>
        excLog.signal(NotReplacedByHoisterError(fv))
        fv
      case fv: ForVarIn =>
        excLog.signal(NotReplacedByHoisterError(fv))
        fv
      case If(info, cond, trueBranch, falseBranch) => falseBranch match {
        case None =>
          If(info, walk(cond, env), walk(trueBranch, env), None)
        case Some(falseStmt) =>
          If(info, walk(cond, env), walk(trueBranch, env), Some(walk(falseStmt, env)))
      }
      case LabelStmt(info, label, stmt) =>
        LabelStmt(info, label, walk(stmt, env))
      case Return(info, expr) =>
        Return(info, expr.map(walk(_, env)))
      case Switch(info, cond, frontCases, defjs, backCases) =>
        Switch(info, walk(cond, env),
          frontCases.map(walk(_, env)),
          defjs.map(_.map(walk(_, env))),
          backCases.map(walk(_, env)))
      case Throw(info, expr) =>
        Throw(info, walk(expr, env))
      case Try(info, body, catchBlock, fin) =>
        Try(info, body.map(walk(_, env)),
          catchBlock.map(walk(_, env)),
          fin.map(_.map(walk(_, env))))
      case vs: VarStmt =>
        excLog.signal(NotReplacedByHoisterError(vs))
        vs
      case While(info, cond, body) =>
        While(info, walk(cond, env), walk(body, env))
      /*
       * rewriteS[|with (e) s | Gamma|] =
       *   If Gamma = EmptyEnv
       *   Then alpha = toObject(rewriteE[|e | Gamma|]);
       *        rewriteS[|s | <alpha, [], false>|]
       *   Else Let Gamma = ConsEnv(phi, varphi, beta> where alpha \not\in phi
       *        alpha = toObject(rewriteE[|e | Gamma|]);
       *        rewriteS[|s | <phi alpha, varphi, beta>|]
       */
      case With(info, expr, stmt) =>
        val fresh = if (forTest) Id(info, freshNameTest, None, true) else freshName(info)
        val vs =
          if (forTest)
            VarStmt(
              info,
              List(VarDecl(info, fresh,
                Some(FunApp(info, VarRef(info, TO_OBJ_FN_ID),
                  List(walk(expr, env)))),
                false))
            )
          else ExprStmt(
            info,
            AssignOpApp(info, VarRef(info, fresh), assignOp(info),
              FunApp(info, VarRef(info, toObjectId(info)),
                List(walk(expr, env)))),
            true
          )
        val body = env match {
          case EmptyEnv =>
            walk(stmt, new ConsEnv(List(fresh), List(List()), false))
          case ConsEnv(withs, names, isNested) =>
            walk(stmt, new ConsEnv(fresh :: withs, List() :: names, isNested))
        }
        ABlock(info, List(vs, body), false)
      case _ => node
    }

    def walk(node: LHS, env: Env): LHS = node match {
      case ArrayExpr(info, elements) =>
        ArrayExpr(info, elements.map(_.map(walk(_, env))))
      case ObjectExpr(info, members) =>
        ObjectExpr(info, members.map(m => walk(m, env)))
      case Parenthesized(info, expr) =>
        Parenthesized(info, walk(expr, env))
      case fe @ FunExpr(info, Functional(i, fds, vds, body, name, params, bodyS)) =>
        FunExpr(info, mkFunctional(i, fds, vds, name, params, bodyS, body, env, fe))
      case Bracket(info, obj, index) =>
        Bracket(info, walk(obj, env), walk(index, env))
      case Dot(info, obj, member) =>
        Dot(info, walk(obj, env), member)
      case New(ninfo, fa @ FunApp(info, fun, args)) =>
        val mapArgsWalk = args.map(e => walk(e, env))
        val funAppWalk = New(ninfo, FunApp(info, walk(fun, env), mapArgsWalk))
        env match {
          case EmptyEnv => funAppWalk
          case ConsEnv(withs, names, isNested) => withs match {
            case Nil =>
              excLog.signal(NoWithObjError(fa))
              fa
            case alpha :: others => fun match {
              case VarRef(vinfo, id) =>
                val (first, rest) = splitNames(names)
                val faMapArgsWalk = New(ninfo, FunApp(vinfo, fun, mapArgsWalk))
                if (first.contains(id.text)) faMapArgsWalk
                else {
                  val lhsInAlpha = InfixOpApp(
                    vinfo,
                    StringLiteral(vinfo, "\"", id.text, false),
                    inOp(vinfo), VarRef(vinfo, alpha)
                  )
                  val alphaDotLhs = Dot(vinfo, VarRef(vinfo, alpha), id)
                  val alphaDotLhsExpr = New(ninfo, FunApp(vinfo, alphaDotLhs, args))
                  val alphaDotLhsExprWalk = New(ninfo, FunApp(vinfo, alphaDotLhs, mapArgsWalk))
                  others match {
                    case Nil =>
                      if (isNested) paren(Cond(vinfo, lhsInAlpha, alphaDotLhsExpr, fa))
                      else paren(Cond(vinfo, lhsInAlpha, alphaDotLhsExprWalk, faMapArgsWalk))
                    case more =>
                      if (isNested) paren(Cond(vinfo, lhsInAlpha, alphaDotLhsExpr,
                        walk(fa, new ConsEnv(more, rest, isNested))))
                      else paren(Cond(vinfo, lhsInAlpha, alphaDotLhsExprWalk,
                        walk(faMapArgsWalk, new ConsEnv(more, rest, true))))
                  }
                }
              case _ => funAppWalk
            }
          }
        }
      case fa @ FunApp(info, fun, args) =>
        val mapArgsWalk = args.map(e => walk(e, env))
        val funAppWalk = FunApp(info, walk(fun, env), mapArgsWalk)
        env match {
          case EmptyEnv => funAppWalk
          case ConsEnv(withs, names, isNested) => withs match {
            case Nil =>
              excLog.signal(NoWithObjError(fa))
              fa
            case alpha :: others => fun match {
              case VarRef(vinfo, id) =>
                val (first, rest) = splitNames(names)
                val faMapArgsWalk = FunApp(vinfo, fun, mapArgsWalk)
                if (first.contains(id.text)) faMapArgsWalk
                else {
                  val lhsInAlpha = InfixOpApp(
                    vinfo,
                    StringLiteral(vinfo, "\"", id.text, false),
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
                        walk(fa, new ConsEnv(more, rest, isNested))))
                      else paren(Cond(vinfo, lhsInAlpha, alphaDotLhsExprWalk,
                        walk(faMapArgsWalk, new ConsEnv(more, rest, true))))
                  }
                }
              case _ => funAppWalk
            }
          }
        }
      case vr @ VarRef(info, id) => env match {
        case EmptyEnv => vr
        case ConsEnv(withs, names, isNested) => withs match {
          case Nil =>
            excLog.signal(NoWithObjError(vr))
            vr
          case alpha :: others =>
            val (first, rest) = splitNames(names)
            if (first.contains(id.text)) vr
            else {
              val idInAlpha = InfixOpApp(info, StringLiteral(info, "\"", id.text, false),
                inOp(info),
                VarRef(info, alpha))
              val alphaDotId = Dot(info, VarRef(info, alpha), id)
              others match {
                case Nil =>
                  paren(Cond(info, idInAlpha, alphaDotId, vr))
                case more =>
                  paren(Cond(info, idInAlpha, alphaDotId,
                    walk(vr, new ConsEnv(more, rest, isNested))))
              }
            }
        }
      }
      case _ => node
    }

    def walk(node: Case, env: Env): Case = node match {
      case Case(info, cond, body) =>
        Case(info, walk(cond, env), body.map(walk(_, env)))
    }

    def walk(node: Catch, env: Env): Catch = node match {
      case c @ Catch(info, id, body) => env match {
        case EmptyEnv =>
          Catch(info, id, body.map(walk(_, env)))
        case ConsEnv(withs, names, isNested) =>
          val (first, rest) = splitNames(names)
          Catch(info, id,
            body.map(b => walk(b, new ConsEnv(withs, List(List(id.text) ++ first) ++ rest, isNested))))
      }
    }

    def walk(node: FunDecl, env: Env): FunDecl = node match {
      case fd @ FunDecl(info, Functional(i, fds, vds, body, name, params, bodyS), strict) =>
        FunDecl(info, mkFunctional(i, fds, vds, name, params, bodyS, body, env, fd), strict)
    }

    def walk(node: Member, env: Env): Member = node match {
      case Field(info, prop, expr) =>
        Field(info, prop, walk(expr, env))
      case gp @ GetProp(info, prop, Functional(i, fds, vds, body, name, params, bodyS)) =>
        GetProp(info, prop, mkFunctional(i, fds, vds, name, Nil, bodyS, body, env, gp))
      case sp @ SetProp(info, prop, Functional(i, fds, vds, body, name, params, bodyS)) =>
        SetProp(info, prop, mkFunctional(i, fds, vds, name, params, bodyS, body, env, sp))
    }

    def walk(node: SourceElements, env: Env): SourceElements = node match {
      case SourceElements(info, stmts, strict) =>
        SourceElements(info, stmts.map(walk(_, env)), strict)
    }

    def walk(node: VarDecl, env: Env): VarDecl = node match {
      case VarDecl(info, name, expr, strict) =>
        VarDecl(info, name, expr.map(walk(_, env)), strict)
    }

    def walk(node: Expr, env: Env): Expr = node match {
      case aoa @ AssignOpApp(info, lhs, op, right) =>
        val lhsWalk = walk(lhs, env)
        val exprWalk = walk(right, env)
        env match {
          case EmptyEnv => AssignOpApp(info, lhsWalk, op, exprWalk)
          case ConsEnv(withs, names, isNested) => withs match {
            case Nil =>
              excLog.signal(NoWithObjError(aoa))
              aoa
            case alpha :: others =>
              val (first, rest) = splitNames(names)
              lhs.unwrapParen match {
                case VarRef(vinfo, id) =>
                  if (first.contains(id.text)) AssignOpApp(vinfo, lhs, op, exprWalk)
                  else {
                    val idInAlpha = InfixOpApp(
                      vinfo,
                      StringLiteral(vinfo, "\"", id.text, false),
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
                          walk(aoa, new ConsEnv(more, rest, isNested))))
                        else paren(Cond(vinfo, idInAlpha, alphaOpWalk,
                          walk(lhsOpWalk, new ConsEnv(more, rest, true))))
                    }
                  }
                case _ => AssignOpApp(info, lhsWalk, op, exprWalk)
              }
          }
        }
      case l: LHS => walk(l, env)
      case Cond(info, cond, trueBranch, falseBranch) =>
        Cond(info, walk(cond, env), walk(trueBranch, env), walk(falseBranch, env))
      case ExprList(info, exprs) =>
        ExprList(info, exprs.map(walk(_, env)))
      case InfixOpApp(info, left, op, right) =>
        InfixOpApp(info, walk(left, env), op, walk(right, env))
      case poa @ PrefixOpApp(info, op, right) => env match {
        case EmptyEnv => PrefixOpApp(info, op, walk(right, env))
        case ConsEnv(withs, names, isNested) => withs match {
          case Nil =>
            excLog.signal(NoWithObjError(poa))
            poa
          case alpha :: others => right match {
            case VarRef(vinfo, id) =>
              val (first, rest) = splitNames(names)
              if (first.contains(id.text)) poa
              else {
                val idInAlpha = InfixOpApp(
                  vinfo,
                  StringLiteral(vinfo, "\"", id.text, false),
                  inOp(vinfo), VarRef(vinfo, alpha)
                )
                val opAlphaDotId = PrefixOpApp(vinfo, op, Dot(vinfo, VarRef(vinfo, alpha), id))
                others match {
                  case Nil => paren(Cond(vinfo, idInAlpha, opAlphaDotId, poa))
                  case more => paren(Cond(vinfo, idInAlpha, opAlphaDotId,
                    walk(poa, new ConsEnv(more, rest, isNested))))
                }
              }
            case _ => PrefixOpApp(info, op, walk(right, env))
          }
        }
      }
      case ua @ UnaryAssignOpApp(info, lhs, op) => env match {
        case EmptyEnv => UnaryAssignOpApp(info, walk(lhs, env), op)
        case ConsEnv(withs, names, isNested) => withs match {
          case Nil =>
            excLog.signal(NoWithObjError(ua))
            ua
          case alpha :: others => lhs match {
            case VarRef(vinfo, id) =>
              val (first, rest) = splitNames(names)
              if (first.contains(id.text)) ua
              else {
                val lhsInAlpha = InfixOpApp(
                  vinfo,
                  StringLiteral(vinfo, "\"", id.text, false),
                  inOp(vinfo), VarRef(vinfo, alpha)
                )
                val alphaDotLhsOp = UnaryAssignOpApp(vinfo, Dot(vinfo, VarRef(vinfo, alpha), id), op)
                others match {
                  case Nil => paren(Cond(vinfo, lhsInAlpha, alphaDotLhsOp, ua))
                  case more => paren(Cond(vinfo, lhsInAlpha, alphaDotLhsOp,
                    walk(ua, new ConsEnv(more, rest, isNested))))
                }
              }
            case _ => UnaryAssignOpApp(info, walk(lhs, env), op)
          }
        }
      }
      case _ => node
    }
  }

  ////////////////////////////////////////////////////////////////
  // calculate results
  ////////////////////////////////////////////////////////////////

  (result, excLog)
}
