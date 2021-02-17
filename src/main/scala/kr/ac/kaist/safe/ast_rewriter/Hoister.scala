/**
 * *****************************************************************************
 * Copyright (c) 2016-2018, KAIST.
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
import kr.ac.kaist.safe.errors.warning._
import kr.ac.kaist.safe.nodes.ast._
import kr.ac.kaist.safe.parser.Parser
import kr.ac.kaist.safe.util.{ NodeUtil => NU, Span }

class Hoister(program: Program) {
  ////////////////////////////////////////////////////////////////
  // results
  ////////////////////////////////////////////////////////////////

  lazy val result: Program = {
    UnitWalker.walk(program)
    NU.SimplifyWalker.walk(HoistTopWalker.walk(program))
  }
  lazy val excLog: ExcLog = new ExcLog

  ////////////////////////////////////////////////////////////////
  // private mutable
  ////////////////////////////////////////////////////////////////

  // variable declarations in for statements
  private var fvds: List[VarDecl] = List()
  private var inFor = false
  private var currentBlock: LocalBlock = new VarBlock(null)

  ////////////////////////////////////////////////////////////////
  // helper function
  ////////////////////////////////////////////////////////////////

  // getters
  private def toSpan(node: ASTNode): Span = node.info.span
  private def toSpan(info: ASTNodeInfo): Span = info.span
  private def vd2Str(vd: VarDecl): String = vd.name.text
  private def fd2Str(fd: FunDecl): String = fd.ftn.name.text
  private def id2Str(id: Id): String = id.text

  // check contain
  private def contains(vds: List[VarDecl], vd: VarDecl): Boolean = {
    val name = vd2Str(vd)
    val span = toSpan(vd)
    vds.find(v => vd2Str(v).equals(name) && toSpan(v).equals(span)).isDefined
  }

  // Get the declared variable and function names and the names on lhs of assignments
  // in the current lexical scope
  private class HoistWalker(node: ASTNode) extends ASTWalker {
    var varDecls = List[VarDecl]()
    var funDecls = List[FunDecl]()
    var varNames = List[(Span, String)]()
    def doit(): (List[VarDecl], List[FunDecl], List[(Span, String)]) =
      { walk(node); (varDecls, funDecls, varNames) }

    override def walk(node: FunDecl): FunDecl = node match {
      case fd: FunDecl =>
        funDecls ++= List(fd); fd
    }

    override def walk(node: VarDecl): VarDecl = node match {
      case vd @ VarDecl(i, n, _, strict) =>
        val vds = List(VarDecl(i, n, None, strict))
        varDecls ++= vds
        if (inFor) fvds ++= vds
        vd
    }

    override def walk(node: LHS): LHS = node match {
      case fe: FunExpr => fe
      case _ => super.walk(node)
    }

    override def walk(node: Member): Member = node match {
      case gp: GetProp => gp
      case sp: SetProp => sp
      case _ => super.walk(node)
    }

    override def walk(node: Expr): Expr = node match {
      case ae @ AssignOpApp(_, VarRef(i, name), _, _) =>
        varNames ++= List((toSpan(i), id2Str(name)))
        ae
      case _ => super.walk(node)
    }

    override def walk(node: Stmt): Stmt = node match {
      case ForVar(info, vars, cond, action, body) =>
        val oldInFor = inFor
        inFor = true
        val vds = vars.map(walk)
        inFor = oldInFor
        ForVar(info, vds, cond.map(walk), action.map(walk), walk(body))
      case ForVarIn(info, vd, expr, body) =>
        val oldInFor = inFor
        inFor = true
        val walkedVd = walk(vd)
        inFor = oldInFor
        ForVarIn(info, walkedVd, walk(expr), walk(body))
      case _ => super.walk(node)
    }
  }

  // Remove function declarations in the current lexical scope
  private object RmFunDeclWalker extends ASTWalker {
    override def walk(node: LHS): LHS = node match {
      case fe: FunExpr => fe
      case _ => super.walk(node)
    }

    override def walk(node: Stmt): Stmt = node match {
      case fd: FunDecl => EmptyStmt(fd.info)
      case _ => super.walk(node)
    }

    override def walk(node: Member): Member = node match {
      case gp: GetProp => gp
      case sp: SetProp => sp
      case _ => super.walk(node)
    }
  }

  private object UnitWalker extends ASTUnitWalker {
    override def walk(node: Program): Unit = node match {
      case Program(info, body) =>
        join(walk(info), walk(body))
    }

    override def walk(node: VarDecl): Unit = node match {
      case VarDecl(info, name, expr, isStrict) =>
        join(walk(info) :: walk(name) :: walkOpt(expr): _*)
    }

    override def walk(node: Expr): Unit = node match {
      case ae @ AssignOpApp(_, VarRef(i, name), _, _) =>
        currentBlock.assigns ++= List((i.span, name.text))
      case _ => super.walk(node)
    }

    override def walk(node: Stmt): Unit = node match {
      case VarStmt(_, vds) =>
        addVds(vds)
        vds.foldLeft(List[(Span, String)]())((res, vd) => {
          val name = vd2Str(vd)
          // 1. var vs var
          //   1) multiple names in a single var statement
          res.find { case (_, n) => n.equals(name) } match {
            case Some((span, _)) =>
              excLog.signal(ShadowingWarning(span, ShadowingVar, name, toSpan(vd), ShadowingVar))
            case _ =>
          }
          // 4. var vs function
          //   1) variable and function with the same name
          currentBlock.fds.find(fd2Str(_).equals(name)) match {
            case Some(fd) =>
              excLog.signal(ShadowingWarning(toSpan(vd), ShadowingVar, name, toSpan(fd), ShadowingFunc))
            case _ =>
          }
          // 5. parameter vs (var or function)
          enclosingFuns(currentBlock) match {
            case immediate :: rest =>
              //   5) parameter and variable with the same name
              immediate.params.find(id2Str(_).equals(name)) match {
                case Some(param) =>
                  excLog.signal(ShadowingWarning(toSpan(param), ShadowingParam, name, toSpan(vd), ShadowingVar))
                case _ =>
              }
              //   7) parameter and nested variable with the same name
              rest.foreach(b =>
                b.params.find(id2Str(_).equals(name)) match {
                  case Some(param) =>
                    excLog.signal(ShadowingWarning(toSpan(param), ShadowingParam, name, toSpan(vd), ShadowingVar))
                  case _ =>
                })
            case _ =>
          }
          // 6. assignments vs var
          collectAssigns(currentBlock).find { case (_, n) => n.equals(name) } match {
            case Some((span, _)) =>
              excLog.signal(ShadowingWarning(span, ShadowingVar, name, toSpan(vd), ShadowingVar))
            case _ =>
          }
          res ++ List((toSpan(vd), name))
        })
        vds.foreach(walk)

      case ABlock(_, stmts, _) => newBlock(stmts)

      case Switch(_, cond, frontCases, default, backCases) =>
        walk(cond); frontCases.foreach(walk)
        default match {
          case Some(stmts) => newBlock(stmts)
          case None =>
        }
        backCases.foreach(walk)

      case Try(_, body, catchBlock, fin) =>
        newBlock(body); catchBlock.foreach(walk)
        fin match {
          case Some(stmts) => newBlock(stmts)
          case None =>
        }

      case _ => super.walk(node)
    }

    override def walk(node: LHS): Unit = node match {
      case VarRef(_, id) =>
        val name = id2Str(id)
        declareV(currentBlock, name) match {
          case Some(span) =>
            currentBlock.blocks.filter(_.isInstanceOf[VarBlock]).foreach(bl => {
              // 1. var vs var
              //   2) multiple variable names in nested blocks
              //      and the name is used in an outer block
              declareVR(bl, name) match {
                case Some(sp) =>
                  excLog.signal(ShadowingWarning(span, ShadowingVar, name, sp, ShadowingVar))
                case _ =>
              }
              // 4. var vs function
              //   2) multiple variable and function names in nested blocks
              //      and the name is used in an outer block
              declareFR(bl, name) match {
                case Some(sp) =>
                  excLog.signal(ShadowingWarning(span, ShadowingVar, name, sp, ShadowingFunc))
                case _ =>
              }
            })
          case _ =>
        }
        declareF(currentBlock, name) match {
          case Some(span) =>
            currentBlock.blocks.filter(_.isInstanceOf[VarBlock]).foreach(bl => {
              // 4. var vs function
              //   2) multiple variable and function names in nested blocks
              //      and the name is used in an outer block
              declareVR(bl, name) match {
                case Some(sp) =>
                  excLog.signal(ShadowingWarning(span, ShadowingFunc, name, sp, ShadowingVar))
                case _ =>
              }
            })
          case _ =>
        }
        // 1. var vs var
        //   3) multiple variable names in parallel blocks and the name is used in an outer block
        val blocks = currentBlock.blocks.filter(_.isInstanceOf[VarBlock])
        val size = blocks.length
        blocks.zipWithIndex.foreach {
          case (block, index) =>
            declareV(block, name) match {
              case Some(span) =>
                blocks.takeRight(size - index - 1).foreach(b =>
                  declareV(b, name) match {
                    case Some(sp) =>
                      excLog.signal(ShadowingWarning(span, ShadowingVar, name, sp, ShadowingVar))
                    case _ =>
                  })
              case _ =>
            }
        }
        // multiple names in a local environment
        enclosingFuns(currentBlock) match {
          case immediate :: rest =>
            checkLocalEnv(immediate, name)
            immediate.vds.find(vd2Str(_).equals(name)) match {
              case Some(vd) =>
                // 4. var vs function
                //   4) if any transitively enclosing FunBlock has the same name
                rest.foreach(b => if (b.name.equals(name))
                  excLog.signal(ShadowingWarning(b.span, ShadowingFunc, name, toSpan(vd), ShadowingVar)))
              case _ =>
            }
          case _ =>
        }

      case _ => super.walk(node)
    }

    override def walk(node: FunDecl): Unit = node match {
      case fd @ FunDecl(info, ftn, strict) =>
        val name = fd2Str(fd)
        val fdSpan = toSpan(info)
        // 4. var vs function
        //   1) variable and function with the same name
        currentBlock.vds.find(vd2Str(_).equals(name)) match {
          case Some(vd) =>
            excLog.signal(ShadowingWarning(toSpan(vd), ShadowingVar, name, toSpan(fd), ShadowingFunc))
          case _ =>
        }
        // 5. parameter vs (var or function)
        enclosingFuns(currentBlock) match {
          case immediate :: rest =>
            //   6) parameter and function with the same name
            immediate.params.find(id2Str(_).equals(name)) match {
              case Some(param) =>
                excLog.signal(ShadowingWarning(toSpan(param), ShadowingParam, name, toSpan(fd), ShadowingFunc))
              case _ =>
            }
            //   8) parameter and nested function with the same name
            rest.foreach(b =>
              b.params.find(id2Str(_).equals(name)) match {
                case Some(param) =>
                  excLog.signal(ShadowingWarning(toSpan(param), ShadowingParam, name, toSpan(fd), ShadowingFunc))
                case _ =>
              })
          case _ =>
        }

        // 3. function vs function
        //   1) multiple function names in the same block
        currentBlock.fds.find(f => fd2Str(f).equals(name)) match {
          case Some(f) =>
            excLog.signal(ShadowingWarning(toSpan(f), ShadowingFunc, name, fdSpan, ShadowingFunc))
          case _ =>
        }
        if (currentBlock.outer != null) { // if (!toplevel)
          // 3. function vs function
          //   1) multiple function names in nested blocks
          declareF(currentBlock.outer, name) match {
            case Some(span) =>
              excLog.signal(ShadowingWarning(span, ShadowingFunc, name, fdSpan, ShadowingFunc))
            case _ =>
          }
          // 3. function vs function
          //   1) multiple function names in parallel blocks
          val blocks = currentBlock.outer.blocks.filter(_.isInstanceOf[VarBlock])
          val size = blocks.length
          blocks.zipWithIndex.foreach {
            case (block, index) =>
              declareF(block, name) match {
                case Some(span) =>
                  blocks.takeRight(size - index - 1).foreach(b =>
                    declareF(b, name) match {
                      case Some(sp) =>
                        excLog.signal(ShadowingWarning(span, ShadowingFunc, name, sp, ShadowingFunc))
                      case _ =>
                    })
                case _ =>
              }
          }
        }
        walk(ftn)
        addFd(fd)
    }

    override def walk(node: Functional): Unit = node match {
      case Functional(_, _, _, stmts, n, params, bodyS) =>
        // 1. parameter vs parameter
        //   1) multiple names in the parameter list of a single function
        params.foldLeft(List[(Span, String)]())((res, param) => {
          val name = id2Str(param)
          res.find { case (_, n) => n.equals(name) } match {
            case Some((span, _)) =>
              excLog.signal(ShadowingWarning(span, ShadowingParam, name, toSpan(param), ShadowingParam))
            case _ =>
          }
          res ++ List((toSpan(param), name))
        })
        // introduce a new block
        val oldVB = currentBlock
        currentBlock = new FunBlock(id2Str(n), toSpan(n), params, oldVB)
        oldVB.blocks ++= List[LocalBlock](currentBlock)
        walk(stmts)
        currentBlock = oldVB
    }

    override def walk(node: Stmts): Unit = node match {
      case Stmts(_, stmts, strict) =>
        stmts.foreach(walk)
    }

    override def walk(node: Case): Unit = node match {
      case Case(_, cond, body) =>
        walk(cond); newBlock(body)
    }

    override def walk(node: Catch): Unit = node match {
      case Catch(_, _, body) => newBlock(body)
    }
  }

  private class LocalBlock(inOuter: LocalBlock) {
    val outer: LocalBlock = inOuter
    var vds: List[VarDecl] = List[VarDecl]()
    var fds: List[FunDecl] = List[FunDecl]()
    var blocks: List[LocalBlock] = List[LocalBlock]()
    var assigns: List[(Span, String)] = List[(Span, String)]()
  }
  private class VarBlock(inOuter: LocalBlock) extends LocalBlock(inOuter)
  private class FunBlock(inName: String, inSpan: Span, inParams: List[Id], inOuter: LocalBlock)
    extends LocalBlock(inOuter) {
    val name: String = inName
    val span: Span = inSpan
    val params: List[Id] = inParams
  }
  private def addVds(vds: List[VarDecl]): Unit = currentBlock.vds ++= vds
  private def addFd(fd: FunDecl): Unit = currentBlock.fds ++= List(fd)
  private def declareV(bl: LocalBlock, name: String): Option[Span] = {
    bl.vds.find(vd => vd2Str(vd).equals(name)) match {
      case Some(vd) => Some(toSpan(vd))
      case None => None
    }
  }
  private def declareVR(bl: LocalBlock, name: String): Option[Span] =
    declareV(bl, name) match {
      case None =>
        bl.blocks.filter(_.isInstanceOf[VarBlock]).foreach(bl =>
          declareVR(bl, name) match {
            case None =>
            case res => return res
          })
        None
      case res => res
    }
  private def declareF(bl: LocalBlock, name: String): Option[Span] = {
    bl.fds.find(fd => fd2Str(fd).equals(name)) match {
      case Some(fd) => Some(toSpan(fd))
      case None => None
    }
  }
  private def declareFR(bl: LocalBlock, name: String): Option[Span] =
    declareF(bl, name) match {
      case None =>
        bl.blocks.filter(_.isInstanceOf[VarBlock]).foreach(bl =>
          declareFR(bl, name) match {
            case None =>
            case res => return res
          })
        None
      case res => res
    }
  // 3. function vs function
  //   2) a transitively enclosing function is the same name
  private def nestedF(bl: LocalBlock, name: String, span: Span): Unit = bl.outer match {
    case b: VarBlock => nestedF(b, name, span)
    case f: FunBlock if f.name.equals(name) =>
      excLog.signal(ShadowingWarning(f.span, ShadowingFunc, name, span, ShadowingFunc))
    case f: FunBlock => nestedF(f, name, span)
    case _ =>
  }
  // find the enclosing function block
  private def enclosingFuns(bl: LocalBlock): List[FunBlock] = bl match {
    case f: FunBlock => List(f) ++ (enclosingFuns(f.outer))
    case b: VarBlock if (b.outer != null) => enclosingFuns(b.outer)
    case _ => List()
  }

  private def checkLocalEnv(bl: FunBlock, name: String): Unit =
    // a reference to an enclosing function
    if (bl.name.equals(name)) {
      nestedF(bl, name, bl.span)
      // 4. var vs function
      //   3) multiple variable and function names in a local environment
      bl.vds.find(vd2Str(_).equals(name)) match {
        case Some(vd) =>
          excLog.signal(ShadowingWarning(bl.span, ShadowingFunc, name, toSpan(vd), ShadowingVar))
        case _ =>
      }
    }

  // collect the assignments in the enclosing function
  private def collectAssigns(block: LocalBlock): List[(Span, String)] = block match {
    case f: FunBlock => f.assigns
    case v: VarBlock => v.assigns ++ collectAssigns(v.outer)
    case _ => List()
  }

  // introduce a new block
  private def newBlock(stmts: List[Stmt]): Unit = {
    val oldVB = currentBlock
    currentBlock = new VarBlock(oldVB)
    oldVB.blocks ++= List[LocalBlock](currentBlock)
    stmts.foreach(UnitWalker.walk)
    currentBlock = oldVB
  }

  private object HoistTopWalker extends ASTWalker {
    // utility functions
    private def assignOp(info: ASTNodeInfo): Op = Op(info, "=")
    private def assignS(i: ASTNodeInfo, name: Id, expr: Expr): ExprStmt =
      ExprStmt(i, walk(AssignOpApp(i, VarRef(i, name), assignOp(i), expr)), true)
    private def hoistVds(vds: List[VarDecl]): List[Stmt] =
      vds.foldRight(List[Stmt]())((vd, res) => vd match {
        case VarDecl(_, _, None, _) => res
        case VarDecl(i, n, Some(e), _) => List(assignS(i, n, e)) ++ res
      })
    private def stmtUnit(info: ASTNodeInfo, stmtList: List[Stmt]): Stmt = {
      if (stmtList.length == 1) stmtList.head
      else StmtUnit(info, stmtList)
    }

    // hoist
    private def isInVd(vd: VarDecl, ds: List[VarDecl], vars: List[(Span, String)]): Boolean =
      ds.exists(d => vd2Str(d).equals(vd2Str(vd)))
    private def isInFd(fd: FunDecl, ds: List[FunDecl]): Boolean =
      ds.exists(d => fd2Str(d).equals(fd2Str(fd)))
    private def isVdInFd(vd: VarDecl, ds: List[FunDecl]): Boolean =
      ds.exists(d => fd2Str(d).equals(vd2Str(vd)))
    private def hoist(body: List[Stmt], params: List[Id], strict: Boolean): (List[FunDecl], List[VarDecl], List[Stmt]) = {
      val (vdss, fdss, varss) = body.map(s => new HoistWalker(s).doit).unzip3
      // hoisted variable declarations
      val vds = vdss.flatten
      // hoisted function declarations
      val fds = fdss.flatten.map(walk)
      val vars = varss.flatten
      // duplicated variable declarations removed
      // first-come wins
      val vdsUniq = vds.foldLeft(List[VarDecl]())((res, vd) =>
        if (isInVd(vd, res, vars)) res
        else res ++ List(vd))
      // duplicated function declarations removed
      // last-come wins
      val fdsUniq = fds.foldRight(List[FunDecl]())((fd, res) =>
        if (isInFd(fd, res)) res
        else List(fd) ++ res)
      // variables with the same names with functions removed
      // function wins
      val vdsUniq2 = vdsUniq.foldRight(List[VarDecl]())((vd, res) =>
        if (isVdInFd(vd, fdsUniq)) res
        else List(vd) ++ res)
      // Set the strict field of function and variable declarations
      (
        fdsUniq.map(fd => fd match { case FunDecl(i, f, _) => FunDecl(i, f, strict) }),
        vdsUniq2.map(vd => vd match { case VarDecl(i, n, e, _) => VarDecl(i, n, e, strict) }),
        body.map(RmFunDeclWalker.walk).map(walk)
      )
    }

    override def walk(node: Program): Program = node match {
      case Program(info, TopLevel(i, Nil, Nil, program)) =>
        val (fds, vds, newProgram) =
          program.foldLeft((
            List[FunDecl](),
            List[VarDecl](),
            List[Stmts]()
          )) {
            case ((f, v, s), ses) => {
              val (fs, vs, ss) = hoist(ses.body, Nil, ses.strict)
              (f ++ fs, v ++ vs, s ++ List(Stmts(ses.info, ss, ses.strict)))
            }
          }
        Program(info, TopLevel(i, fds, vds, newProgram))
      case (pgm: Program) =>
        excLog.signal(BeforeHoisterError("Program", pgm)); pgm
    }

    override def walk(node: FunDecl): FunDecl = node match {
      case FunDecl(info, Functional(j, Nil, Nil, Stmts(i, body, str), name, params, bodyS), strict) =>
        val (fds, vds, newBody) = hoist(body, params, strict)
        FunDecl(info, Functional(j, fds, vds, Stmts(i, newBody, str), name, params, bodyS), strict)
      case (fd: FunDecl) =>
        excLog.signal(BeforeHoisterError("Function declarations", fd)); fd
    }

    override def walk(node: LHS): LHS = node match {
      case FunExpr(info, Functional(j, Nil, Nil, Stmts(i, body, strict), name, params, bodyS)) =>
        val (fds, vds, newBody) = hoist(body, params, strict)
        FunExpr(info, Functional(j, fds, vds, Stmts(i, newBody, strict), name, params, bodyS))
      case (fe: FunExpr) =>
        excLog.signal(BeforeHoisterError("Function expressions", fe)); fe
      case _ => super.walk(node)
    }

    override def walk(node: Member): Member = node match {
      case GetProp(info, prop, Functional(j, Nil, Nil, Stmts(i, body, strict), name, params, bodyS)) =>
        val (fds, vds, newBody) = hoist(body, params, strict)
        GetProp(info, prop, Functional(j, fds, vds, Stmts(i, newBody, strict), name, params, bodyS))
      case (gp: GetProp) =>
        excLog.signal(BeforeHoisterError("Function expressions", gp)); gp
      case SetProp(info, prop, Functional(j, Nil, Nil, Stmts(i, body, strict), name, params, bodyS)) =>
        val (fds, vds, newBody) = hoist(body, params, strict)
        SetProp(info, prop, Functional(j, fds, vds, Stmts(i, newBody, strict), name, params, bodyS))
      case (sp: SetProp) =>
        excLog.signal(BeforeHoisterError("Function expressions", sp)); sp
      case _ => super.walk(node)
    }

    override def walk(node: Stmt): Stmt = node match {
      case VarStmt(info, vds) => stmtUnit(info, hoistVds(vds))
      case ForVar(info, vars, cond, action, body) =>
        val newInfo = ASTNodeInfo(Span.merge(vars, Span()))
        ABlock(
          newInfo,
          List(
            stmtUnit(newInfo, hoistVds(vars)),
            walk(For(info, None, cond, action, body))
          ),
          false
        )
      case ForVarIn(info, VarDecl(i, n, None, _), expr, body) =>
        walk(ForIn(info, VarRef(i, n), expr, body))
      case ForVarIn(info, VarDecl(i, n, Some(e), _), expr, body) =>
        ABlock(
          info,
          List(
            stmtUnit(info, List(walk(assignS(i, n, e)))),
            walk(ForIn(info, VarRef(i, n), expr, body))
          ),
          false
        )
      case LabelStmt(info, label, ForVar(i, vars, cond, action, body)) =>
        val newInfo = ASTNodeInfo(Span.merge(vars, Span()))
        ABlock(
          newInfo,
          List(
            stmtUnit(info, hoistVds(vars)),
            LabelStmt(info, label,
              walk(For(i, None, cond, action, body)))
          ),
          false
        )
      case LabelStmt(info, label, ForVarIn(finfo, VarDecl(i, n, Some(e), _), expr, body)) =>
        ABlock(info, List(
          stmtUnit(info, List(walk(assignS(i, n, e)))),
          LabelStmt(info, label,
            walk(ForIn(info, VarRef(i, n), expr, body)))
        ),
          false)
      case LabelStmt(info, label, stmt) =>
        LabelStmt(info, label, walk(stmt))
      case _ => super.walk(node)
    }
  }

  ////////////////////////////////////////////////////////////////
  // calculate results
  ////////////////////////////////////////////////////////////////

  (result, excLog)
}
