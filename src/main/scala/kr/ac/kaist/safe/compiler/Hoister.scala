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

import kr.ac.kaist.safe.errors.ExcLog
import kr.ac.kaist.safe.errors.error._
import kr.ac.kaist.safe.errors.warning._
import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.util.{ NodeUtil => NU, Span }

class Hoister(program: Program) extends ASTWalker {
  /* Error handling
   * The signal function collects errors during the Hoister phase.
   * To collect multiple errors,
   * we should return a dummy value after signaling an error.
   */
  var excLog = new ExcLog

  // getters
  def toSpan(node: ASTNode): Span = node.info.span
  def toSpan(info: ASTNodeInfo): Span = info.span
  def vd2Str(vd: VarDecl): String = vd.name.text
  def fd2Str(fd: FunDecl): String = fd.ftn.name.text
  def id2Str(id: Id): String = id.text

  // Variable declarations in for statements
  var fvds = List[VarDecl]()
  var inFor = false
  def contains(vds: List[VarDecl], vd: VarDecl): Boolean = {
    val name = vd2Str(vd)
    val span = toSpan(vd)
    vds.find(v => vd2Str(v).equals(name) && toSpan(v).equals(span)).isDefined
  }

  // Utility functions
  def assignOp(info: ASTNodeInfo): Op = Op(info, "=")
  def assignS(i: ASTNodeInfo, name: Id, expr: Expr): ExprStmt =
    ExprStmt(i, walk(AssignOpApp(i, VarRef(i, name), assignOp(i), expr)).asInstanceOf[Expr], true)
  def hoistVds(vds: List[VarDecl]): List[Stmt] =
    vds.foldRight(List[Stmt]())((vd, res) => vd match {
      case VarDecl(_, _, None, _) => res
      case VarDecl(i, n, Some(e), _) => List(assignS(i, n, e)) ++ res
    })
  def stmtUnit(info: ASTNodeInfo, stmtList: List[Stmt]): Stmt = {
    if (stmtList.length == 1) stmtList.head
    else StmtUnit(info, stmtList)
  }
  // Get the declared variable and function names and the names on lhs of assignments
  // in the current lexical scope
  class hoistWalker(node: Any, isTopLevel: Boolean) extends ASTWalker {
    var varDecls = List[VarDecl]()
    var funDecls = List[FunDecl]()
    var varNames = List[(Span, String)]()
    def doit(): (List[VarDecl], List[FunDecl], List[(Span, String)]) =
      { walk(node); (varDecls, funDecls, varNames) }
    override def walk(node: Any): Any = node match {
      case fd: FunDecl =>
        funDecls ++= List(fd); fd
      case vd @ VarDecl(i, n, _, strict) =>
        val vds = List(VarDecl(i, n, None, strict))
        varDecls ++= vds
        if (inFor) fvds ++= vds
        vd
      case ForVar(info, vars, cond, action, body) =>
        val oldInFor = inFor
        inFor = true
        val vds = walk(vars).asInstanceOf[List[VarDecl]]
        inFor = oldInFor
        ForVar(info, vds,
          walk(cond).asInstanceOf[Option[Expr]],
          walk(action).asInstanceOf[Option[Expr]],
          walk(body).asInstanceOf[Stmt])
      case ForVarIn(info, vd, expr, body) =>
        val oldInFor = inFor
        inFor = true
        val walkedVd = walk(vd).asInstanceOf[VarDecl]
        inFor = oldInFor
        ForVarIn(info, walkedVd,
          walk(expr).asInstanceOf[Expr], walk(body).asInstanceOf[Stmt])
      case fe: FunExpr => fe
      case gp: GetProp => gp
      case sp: SetProp => sp
      case ae @ AssignOpApp(_, VarRef(i, name), _, _) =>
        varNames ++= List((toSpan(i), id2Str(name)))
        ae
      case _: Comment => node
      case _ => super.walk(node)
    }
  }

  // Remove function declarations in the current lexical scope
  object rmFunDeclWalker extends ASTWalker {
    override def walk(node: Any): Any = node match {
      case fd: FunDecl => EmptyStmt(fd.info)
      case fe: FunExpr => fe
      case gp: GetProp => gp
      case sp: SetProp => sp
      case _: Comment => node
      case _ => super.walk(node)
    }
  }

  /* The main entry function */
  def doit(): Program = {
    walkUnit(program)
    NU.simplifyWalker.walk(walk(program).asInstanceOf[Program]).asInstanceOf[Program]
  }

  class LocalBlock(_outer: LocalBlock) {
    val outer: LocalBlock = _outer
    var vds: List[VarDecl] = List[VarDecl]()
    var fds: List[FunDecl] = List[FunDecl]()
    var blocks: List[LocalBlock] = List[LocalBlock]()
    var assigns: List[(Span, String)] = List[(Span, String)]()
  }
  class VarBlock(_outer: LocalBlock) extends LocalBlock(_outer)
  class FunBlock(_name: String, _span: Span, _params: List[Id], _outer: LocalBlock)
      extends LocalBlock(_outer) {
    val name: String = _name
    val span: Span = _span
    val params: List[Id] = _params
  }

  var currentBlock: LocalBlock = new VarBlock(null)
  def addVds(vds: List[VarDecl]): Unit = currentBlock.vds ++= vds
  def addFd(fd: FunDecl): Unit = currentBlock.fds ++= List(fd)
  def declareV(bl: LocalBlock, name: String): Option[Span] = {
    bl.vds.find(vd => vd2Str(vd).equals(name)) match {
      case Some(vd) => Some(toSpan(vd))
      case None => None
    }
  }
  def declareVR(bl: LocalBlock, name: String): Option[Span] =
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
  def declareF(bl: LocalBlock, name: String): Option[Span] = {
    bl.fds.find(fd => fd2Str(fd).equals(name)) match {
      case Some(fd) => Some(toSpan(fd))
      case None => None
    }
  }
  def declareFR(bl: LocalBlock, name: String): Option[Span] =
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
  def nestedF(bl: LocalBlock, name: String, span: Span): Unit = bl.outer match {
    case b: VarBlock => nestedF(b, name, span)
    case f: FunBlock if f.name.equals(name) =>
      excLog.signal(ShadowingWarning(f.span, ShadowingFunc, name, span, ShadowingFunc))
    case f: FunBlock => nestedF(f, name, span)
    case _ =>
  }
  // find the enclosing function block
  def enclosingFuns(bl: LocalBlock): List[FunBlock] = bl match {
    case f: FunBlock => List(f) ++ (enclosingFuns(f.outer))
    case b: VarBlock if (b.outer != null) => enclosingFuns(b.outer)
    case _ => List()
  }

  def checkLocalEnv(bl: FunBlock, name: String): Unit =
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
  def collectAssigns(block: LocalBlock): List[(Span, String)] = block match {
    case f: FunBlock => f.assigns
    case v: VarBlock => v.assigns ++ collectAssigns(v.outer)
    case _ => List()
  }

  // introduce a new block
  def newBlock(stmts: List[SourceElement]): Unit = {
    val oldVB = currentBlock
    currentBlock = new VarBlock(oldVB)
    oldVB.blocks ++= List[LocalBlock](currentBlock)
    walkUnit(stmts)
    currentBlock = oldVB
  }

  override def walkUnit(node: Any): Unit = node match {
    case ae @ AssignOpApp(_, VarRef(i, name), _, _) =>
      currentBlock.assigns ++= List((i.span, name.text))

    case VarStmt(_, vds) =>
      addVds(vds)
      vds.foldLeft(List[(Span, String)]())((res, vd) => {
        val name = vd2Str(vd)
        // 1. var vs var
        //   1) multiple names in a single var statement
        res.find(p => p._2.equals(name)) match {
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
        collectAssigns(currentBlock).find(ass => ass._2.equals(name)) match {
          case Some((span, _)) =>
            excLog.signal(ShadowingWarning(span, ShadowingVar, name, toSpan(vd), ShadowingVar))
          case _ =>
        }
        res ++ List((toSpan(vd), name))
      })
      vds.foreach(walkUnit)

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
      blocks.zipWithIndex.foreach(p =>
        declareV(p._1, name) match {
          case Some(span) =>
            blocks.takeRight(size - p._2 - 1).foreach(b =>
              declareV(b, name) match {
                case Some(sp) =>
                  excLog.signal(ShadowingWarning(span, ShadowingVar, name, sp, ShadowingVar))
                case _ =>
              })
          case _ =>
        })
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

    case ABlock(_, stmts, _) => newBlock(stmts)

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
        blocks.zipWithIndex.foreach(p =>
          declareF(p._1, name) match {
            case Some(span) =>
              blocks.takeRight(size - p._2 - 1).foreach(b =>
                declareF(b, name) match {
                  case Some(sp) =>
                    excLog.signal(ShadowingWarning(span, ShadowingFunc, name, sp, ShadowingFunc))
                  case _ =>
                })
            case _ =>
          })
      }
      walkUnit(ftn)
      addFd(fd)

    case Functional(_, _, _, stmts, n, params, bodyS) =>
      // 1. parameter vs parameter
      //   1) multiple names in the parameter list of a single function
      params.foldLeft(List[(Span, String)]())((res, param) => {
        val name = id2Str(param)
        res.find(p => p._2.equals(name)) match {
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
      walkUnit(stmts)
      currentBlock = oldVB

    case SourceElements(_, stmts, strict) =>
      walkUnit(stmts)

    case Switch(_, cond, frontCases, default, backCases) =>
      walkUnit(cond); walkUnit(frontCases)
      default match {
        case Some(stmts) => newBlock(stmts)
        case None =>
      }
      walkUnit(backCases)

    case Case(_, cond, body) =>
      walkUnit(cond); newBlock(body)

    case Try(_, body, catchBlock, fin) =>
      newBlock(body); walkUnit(catchBlock)
      fin match {
        case Some(stmts) => newBlock(stmts)
        case None =>
      }

    case Catch(_, _, body) => newBlock(body)

    case fa @ FunApp(info, fun, List(StringLiteral(_, _, str, _))) if (NU.isEval(fun)) =>
      try {
        Parser.scriptToAST(List(("evalParse", (1, 1), str)))
      } catch {
        case e: ParserError =>
          excLog.signal(EvalArgSyntaxError(toSpan(fa), str))
      }
      super.walkUnit(node)

    case _: Comment =>
    case _ => super.walkUnit(node)
  }

  def isInVd(vd: VarDecl, ds: List[VarDecl], vars: List[(Span, String)]): Boolean =
    ds.exists(d => vd2Str(d).equals(vd2Str(vd)))
  def isInFd(fd: FunDecl, ds: List[FunDecl]): Boolean =
    ds.exists(d => fd2Str(d).equals(fd2Str(fd)))
  def isVdInFd(vd: VarDecl, ds: List[FunDecl]): Boolean =
    ds.exists(d => fd2Str(d).equals(vd2Str(vd)))
  def hoist(body: List[SourceElement], isTopLevel: Boolean, params: List[Id], strict: Boolean): (List[FunDecl], List[VarDecl], List[SourceElement]) = {
    val param_names = params.map(id2Str)
    val (vdss, fdss, varss) = body.map(s => new hoistWalker(s, isTopLevel).doit).unzip3
    // hoisted variable declarations
    val vds = vdss.flatten.asInstanceOf[List[VarDecl]]
    // hoisted function declarations
    val fds = fdss.flatten.map(walk).asInstanceOf[List[FunDecl]]
    val vars = varss.flatten.asInstanceOf[List[(Span, String)]]
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
      walk(rmFunDeclWalker.walk(body)).asInstanceOf[List[SourceElement]]
    )
  }

  override def walk(node: Any): Any = node match {
    case Program(info, TopLevel(i, Nil, Nil, program)) =>
      val (fds, vds, new_program) =
        program.foldLeft((
          List[FunDecl](),
          List[VarDecl](),
          List[SourceElements]()
        ))((r, ses) => {
          val (fs, vs, ss) = hoist(ses.body, true, Nil, ses.strict)
          (r._1 ++ fs, r._2 ++ vs, r._3 ++ List(SourceElements(ses.info, ss, ses.strict)))
        })
      Program(info, TopLevel(i, fds, vds, new_program))
    case (pgm: Program) => throw new BeforeHoisterError("Program", pgm.info)
    case FunDecl(info, Functional(j, Nil, Nil, SourceElements(i, body, str), name, params, bodyS), strict) =>
      val (fds, vds, new_body) = hoist(body, false, params, strict)
      FunDecl(info, Functional(j, fds, vds, SourceElements(i, new_body, str), name, params, bodyS), strict)
    case (fd: FunDecl) => throw new BeforeHoisterError("Function declarations", fd.info)
    case FunExpr(info, Functional(j, Nil, Nil, SourceElements(i, body, strict), name, params, bodyS)) =>
      val (fds, vds, new_body) = hoist(body, false, params, strict)
      FunExpr(info, Functional(j, fds, vds, SourceElements(i, new_body, strict), name, params, bodyS))
    case (fe: FunExpr) => throw new BeforeHoisterError("Function expressions", fe.info)
    case GetProp(info, prop, Functional(j, Nil, Nil, SourceElements(i, body, strict), name, params, bodyS)) =>
      val (fds, vds, new_body) = hoist(body, false, params, strict)
      GetProp(info, prop, Functional(j, fds, vds, SourceElements(i, new_body, strict), name, params, bodyS))
    case (gp: GetProp) => throw new BeforeHoisterError("Function expressions", gp.info)
    case SetProp(info, prop, Functional(j, Nil, Nil, SourceElements(i, body, strict), name, params, bodyS)) =>
      val (fds, vds, new_body) = hoist(body, false, params, strict)
      SetProp(info, prop, Functional(j, fds, vds, SourceElements(i, new_body, strict), name, params, bodyS))
    case (sp: SetProp) => throw new BeforeHoisterError("Function expressions", sp.info)
    case VarStmt(info, vds) => stmtUnit(info, hoistVds(vds))
    case ForVar(info, vars, cond, action, body) =>
      val new_info = NU.spanInfoAll(vars)
      ABlock(
        new_info,
        List(
          stmtUnit(new_info, hoistVds(vars)),
          walk(For(info, None, cond, action, body)).asInstanceOf[Stmt]
        ),
        false
      )
    case ForVarIn(info, VarDecl(i, n, None, _), expr, body) =>
      walk(ForIn(info, VarRef(i, n), expr, body))
    case ForVarIn(info, VarDecl(i, n, Some(e), _), expr, body) =>
      ABlock(
        info,
        List(
          stmtUnit(info, List(walk(assignS(i, n, e)).asInstanceOf[Stmt])),
          walk(ForIn(info, VarRef(i, n), expr, body)).asInstanceOf[Stmt]
        ),
        false
      )
    case LabelStmt(info, label, ForVar(i, vars, cond, action, body)) =>
      val new_info = NU.spanInfoAll(vars)
      ABlock(
        new_info,
        List(
          stmtUnit(info, hoistVds(vars)),
          LabelStmt(info, label,
            walk(For(i, None, cond, action, body)).asInstanceOf[Stmt])
        ),
        false
      )
    case LabelStmt(info, label, ForVarIn(finfo, VarDecl(i, n, Some(e), _), expr, body)) =>
      ABlock(info, List(
        stmtUnit(info, List(walk(assignS(i, n, e)).asInstanceOf[Stmt])),
        LabelStmt(info, label,
          walk(ForIn(info, VarRef(i, n), expr, body)).asInstanceOf[Stmt])
      ),
        false)

    case LabelStmt(info, label, stmt) =>
      LabelStmt(info, label, walk(stmt).asInstanceOf[Stmt])

    case _: Comment => node
    case _ => super.walk(node)
  }
}
