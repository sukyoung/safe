/*******************************************************************************
    Copyright (c) 2012-2014, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.compiler

import java.lang.{Integer => JInteger}
import java.util.{List => JList}
import kr.ac.kaist.jsaf.bug_detector._
import kr.ac.kaist.jsaf.exceptions.ParserError
import kr.ac.kaist.jsaf.exceptions.StaticError
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{NodeUtil => NU}
import kr.ac.kaist.jsaf.nodes_util.Span
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.useful.HasAt
import kr.ac.kaist.jsaf.useful.Triple

class Hoister(program: Program) extends Walker {
  /* Error handling
   * The signal function collects errors during the Hoister phase.
   * To collect multiple errors,
   * we should return a dummy value after signaling an error.
   */
  var errors = List[BugInfo]()
  private def isUserCode(span: Span): Boolean =
    !span.getFileName.containsSlice("jquery")
  def signal(span: Span, bugKind: Int, arg1: String, arg2: String): Unit =
    if (isUserCode(span))
      errors ++= List(new BugInfo(span, bugKind, arg1, arg2))
  def getErrors(): JList[BugInfo] = toJavaList(errors)

  // getters
  def toSpan(node: ASTNode): Span = node.getInfo.getSpan
  def toSpan(info: ASTSpanInfo): Span = info.getSpan
  def vd2Str(vd: VarDecl): String = vd.getName.getText
  def fd2Str(fd: FunDecl): String = fd.getFtn.getName.getText
  def id2Str(id: Id): String = id.getText

  // Variable declarations in for statements
  var fvds = List[VarDecl]()
  var inFor = false
  def contains(vds: List[VarDecl], vd: VarDecl) = {
    val name = vd2Str(vd)
    val span = toSpan(vd)
    vds.find(v => vd2Str(v).equals(name) && toSpan(v).equals(span)).isDefined
  }

  // Utility functions
  def assignOp(info: ASTSpanInfo) = SOp(info, "=")
  def assignS(i: ASTSpanInfo, name: Id, expr: Expr) =
    SExprStmt(i, walk(SAssignOpApp(i, SVarRef(i, name), assignOp(i), expr)).asInstanceOf[Expr], true)
  def hoistVds(vds: List[VarDecl]) =
    vds.foldRight(List[Stmt]())((vd, res) => vd match {
                                case SVarDecl(_,_,None, _) => res
                                case SVarDecl(i,n,Some(e), _) => List(assignS(i,n,e))++res
                               })
  def stmtUnit(info: ASTSpanInfo, stmtList: List[Stmt]): Stmt = {
    if(stmtList.length == 1) stmtList.head
    else SStmtUnit(info, stmtList)
  }
  // Get the declared variable and function names and the names on lhs of assignments
  // in the current lexical scope
  class hoistWalker(node: Any, isTopLevel: Boolean) extends Walker {
    var varDecls = List[VarDecl]()
    var funDecls = List[FunDecl]()
    var varNames = List[(Span, String)]()
    def doit() = { walk(node); (varDecls, funDecls, varNames) }
    override def walk(node: Any) = node match {
      case fd:FunDecl => funDecls ++= List(fd); fd
      case vd@SVarDecl(i, n, _, strict) =>
        val vds = List(SVarDecl(i, n, None, strict))
        varDecls ++= vds
        if (inFor) fvds ++= vds
        vd
      case SForVar(info, vars, cond, action, body) =>
        val oldInFor = inFor
        inFor = true
        val vds = walk(vars).asInstanceOf[List[VarDecl]]
        inFor = oldInFor
        SForVar(info, vds,
                walk(cond).asInstanceOf[Option[Expr]],
                walk(action).asInstanceOf[Option[Expr]],
                walk(body).asInstanceOf[Stmt])
      case SForVarIn(info, vd, expr, body) =>
        val oldInFor = inFor
        inFor = true
        val walkedVd = walk(vd).asInstanceOf[VarDecl]
        inFor = oldInFor
        SForVarIn(info, walkedVd,
                  walk(expr).asInstanceOf[Expr], walk(body).asInstanceOf[Stmt])
      case fe:FunExpr => fe
      case gp:GetProp => gp
      case sp:SetProp => sp
      case ae@SAssignOpApp(_, SVarRef(i, name), _, _) =>
        varNames ++= List((toSpan(i), id2Str(name)))
        ae
      case _: Comment => node
      case _ => super.walk(node)
    }
  }

  // Remove function declarations in the current lexical scope
  object rmFunDeclWalker extends Walker {
    override def walk(node: Any) = node match {
      case fd:FunDecl => SEmptyStmt(fd.getInfo)
      case fe:FunExpr => fe
      case gp:GetProp => gp
      case sp:SetProp => sp
      case _: Comment => node
      case _ => super.walk(node)
    }
  }

  /* The main entry function */
  def doit() = {
    walkUnit(program)
    NU.simplifyWalker.walk(walk(program).asInstanceOf[Program])
  }

  class LocalBlock(_outer: LocalBlock) {
    val outer: LocalBlock = _outer
    var vds: List[VarDecl] = List[VarDecl]()
    var fds: List[FunDecl] = List[FunDecl]()
    var blocks: List[LocalBlock] = List[LocalBlock]()
    var assigns:List[(Span, String)] = List[(Span, String)]()
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
                    case res => return res})
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
                    case res => return res})
        None
      case res => res
    }
  // 3. function vs function
  //   2) a transitively enclosing function is the same name
  def nestedF(bl: LocalBlock, name: String, span: String): Unit = bl.outer match {
    case b:VarBlock => nestedF(b, name, span)
    case f:FunBlock if f.name.equals(name) =>
      signal(f.span, ShadowedFuncByFunc, name, span)
    case f:FunBlock => nestedF(f, name, span)
    case _ =>
  }
  // find the enclosing function block
  def enclosingFuns(bl: LocalBlock): List[FunBlock] = bl match {
    case f:FunBlock => List(f)++(enclosingFuns(f.outer))
    case b:VarBlock if (b.outer != null) => enclosingFuns(b.outer)
    case _ => List()
  }

  def checkLocalEnv(bl: FunBlock, name: String) =
    // a reference to an enclosing function
    if (bl.name.equals(name)) {
      nestedF(bl, name, bl.span.toStringWithoutFiles)
      // 4. var vs function
      //   3) multiple variable and function names in a local environment
      bl.vds.find(vd2Str(_).equals(name)) match {
             case Some(vd) =>
               signal(bl.span, ShadowedFuncByVar, name, toSpan(vd).toStringWithoutFiles)
             case _ =>
      }
    }

  // collect the assignments in the enclosing function
  def collectAssigns(block: LocalBlock): List[(Span, String)] = block match {
    case f:FunBlock => f.assigns
    case v:VarBlock => v.assigns ++ collectAssigns(v.outer)
    case _ => List()
  }

  // introduce a new block
  def newBlock(stmts: List[SourceElement]) = {
    val oldVB = currentBlock
    currentBlock = new VarBlock(oldVB)
    oldVB.blocks ++= List[LocalBlock](currentBlock)
    walkUnit(stmts)
    currentBlock = oldVB
  }

  override def walkUnit(node: Any): Unit = node match {
    case ae@SAssignOpApp(_, SVarRef(i, name), _, _) =>
      currentBlock.assigns ++= List((i.getSpan, name.getText))

    case SVarStmt(_, vds) =>
      addVds(vds)
      vds.foldLeft(List[(Span, String)]())((res, vd) => {
                    val name = vd2Str(vd)
                    // 1. var vs var
                    //   1) multiple names in a single var statement
                    res.find(p => p._2.equals(name)) match {
                      case Some((span, _)) =>
                        signal(span, ShadowedVarByVar, name, toSpan(vd).toStringWithoutFiles)
                      case _ =>
                    }
                    // 4. var vs function
                    //   1) variable and function with the same name
                    currentBlock.fds.find(fd2Str(_).equals(name)) match {
                      case Some(fd) =>
                        signal(toSpan(vd), ShadowedVarByFunc, name, toSpan(fd).toStringWithoutFiles)
                      case _ =>
                    }
                    // 5. parameter vs (var or function)
                    enclosingFuns(currentBlock) match {
                      case immediate::rest =>
                    //   5) parameter and variable with the same name
                        immediate.params.find(id2Str(_).equals(name)) match {
                          case Some(param) =>
                            signal(toSpan(param), ShadowedParamByVar, name, toSpan(vd).toStringWithoutFiles)
                          case _ =>
                        }
                    //   7) parameter and nested variable with the same name
                        rest.foreach(b =>
                          b.params.find(id2Str(_).equals(name)) match {
                            case Some(param) =>
                              signal(toSpan(param), ShadowedParamByVar, name, toSpan(vd).toStringWithoutFiles)
                            case _ =>
                          })
                      case _ =>
                    }
                    // 6. assignments vs var
                    collectAssigns(currentBlock).find(ass => ass._2.equals(name)) match {
                      case Some((span, _)) =>
                        signal(span, ShadowedVarByVar, name, toSpan(vd).toStringWithoutFiles)
                      case _ =>
                    }
                    res++List((toSpan(vd), name))
                  })
      vds.foreach(walkUnit)

    case SVarRef(_, id) =>
      val name = id2Str(id)
      declareV(currentBlock, name) match {
        case Some(span) =>
         currentBlock.blocks.filter(_.isInstanceOf[VarBlock]).foreach(bl => {
                       // 1. var vs var
                       //   2) multiple variable names in nested blocks
                       //      and the name is used in an outer block
                       declareVR(bl, name) match {
                         case Some(sp) =>
                          signal(span, ShadowedVarByVar, name, sp.toStringWithoutFiles)
                         case _ => }
                       // 4. var vs function
                       //   2) multiple variable and function names in nested blocks
                       //      and the name is used in an outer block
                       declareFR(bl, name) match {
                         case Some(sp) =>
                           signal(span, ShadowedVarByFunc, name, sp.toStringWithoutFiles)
                         case _ => }
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
                           signal(span, ShadowedFuncByVar, name, sp.toStringWithoutFiles)
                         case _ => }
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
                 blocks.takeRight(size-p._2-1).foreach(b =>
                   declareV(b, name) match {
                     case Some(sp) =>
                       signal(span, ShadowedVarByVar, name, sp.toStringWithoutFiles)
                     case _ => })
               case _ => })
      // multiple names in a local environment
      enclosingFuns(currentBlock) match {
        case immediate::rest =>
          checkLocalEnv(immediate, name)
          immediate.vds.find(vd2Str(_).equals(name)) match {
            case Some(vd) =>
              // 4. var vs function
              //   4) if any transitively enclosing FunBlock has the same name
              rest.foreach(b => if (b.name.equals(name))
                                  signal(b.span, ShadowedFuncByVar, name, toSpan(vd).toStringWithoutFiles))
            case _ =>
          }
        case _ =>
      }

    case SBlock(_, stmts, _) => newBlock(stmts)

    case fd@SFunDecl(info, ftn, strict) =>
      val name = fd2Str(fd)
      val fdSpan = toSpan(info).toStringWithoutFiles
      // 4. var vs function
      //   1) variable and function with the same name
      currentBlock.vds.find(vd2Str(_).equals(name)) match {
                      case Some(vd) =>
                        signal(toSpan(vd), ShadowedVarByFunc, name, toSpan(fd).toStringWithoutFiles)
                      case _ =>
                    }
      // 5. parameter vs (var or function)
      enclosingFuns(currentBlock) match {
        case immediate::rest =>
      //   6) parameter and function with the same name
          immediate.params.find(id2Str(_).equals(name)) match {
            case Some(param) =>
              signal(toSpan(param), ShadowedParamByFunc, name, toSpan(fd).toStringWithoutFiles)
            case _ =>
          }
      //   8) parameter and nested function with the same name
          rest.foreach(b =>
            b.params.find(id2Str(_).equals(name)) match {
              case Some(param) =>
                signal(toSpan(param), ShadowedParamByFunc, name, toSpan(fd).toStringWithoutFiles)
              case _ =>
            })
        case _ =>
      }

      // 3. function vs function
      //   1) multiple function names in the same block
      currentBlock.fds.find(f => fd2Str(f).equals(name)) match {
        case Some(f) =>
          signal(toSpan(f), ShadowedFuncByFunc, name, fdSpan)
        case _ =>
      }
      if (currentBlock.outer != null) { // if (!toplevel)
        // 3. function vs function
        //   1) multiple function names in nested blocks
        declareF(currentBlock.outer, name) match {
          case Some(span) =>
            signal(span, ShadowedFuncByFunc, name, fdSpan)
          case _ =>
        }
        // 3. function vs function
        //   1) multiple function names in parallel blocks
        val blocks = currentBlock.outer.blocks.filter(_.isInstanceOf[VarBlock])
        val size = blocks.length
        blocks.zipWithIndex.foreach(p =>
               declareF(p._1, name) match {
                 case Some(span) =>
                   blocks.takeRight(size-p._2-1).foreach(b =>
                     declareF(b, name) match {
                       case Some(sp) =>
                         signal(span, ShadowedFuncByFunc, name, sp.toStringWithoutFiles)
                       case _ => })
                 case _ => })
      }
      walkUnit(ftn)
      addFd(fd)

    case SFunctional(_, _, stmts, n, params) =>
      // 1. parameter vs parameter
      //   1) multiple names in the parameter list of a single function
      params.foldLeft(List[(Span, String)]())((res, param) => {
                        val name = id2Str(param)
                        res.find(p => p._2.equals(name)) match {
                          case Some((span, _)) =>
                            signal(span, ShadowedParamByParam, name, toSpan(param).toStringWithoutFiles)
                          case _ =>
                        }
                        res++List((toSpan(param), name))
                      })
      // introduce a new block
      val oldVB = currentBlock
      currentBlock = new FunBlock(id2Str(n), toSpan(n), params, oldVB)
      oldVB.blocks ++= List[LocalBlock](currentBlock)
      walkUnit(stmts)
      currentBlock = oldVB

    case SSourceElements(_, stmts, strict) =>
      walkUnit(stmts)

    case SSwitch(_, cond, frontCases, default, backCases) =>
      walkUnit(cond); walkUnit(frontCases)
      default match {
        case Some(stmts) => newBlock(stmts)
        case None =>
      }
      walkUnit(backCases)

    case SCase(_, cond, body) => walkUnit(cond); newBlock(body)

    case STry(_, body, catchBlock, fin) =>
      newBlock(body); walkUnit(catchBlock)
      fin match {
        case Some(stmts) => newBlock(stmts)
        case None =>
      }

    case SCatch(_, _, body) => newBlock(body)

    case fa@SFunApp(info, fun, List(SStringLiteral(_, _, str))) if (NU.isEval(fun)) =>
      try {
        Parser.scriptToAST(toJavaList(List(new Triple("evalParse", new JInteger(1), str))))
      } catch {
        case e:ParserError =>
          signal(toSpan(fa), EvalArgSyntax, str, null)
      }
      super.walkUnit(node)

    case _: Comment =>
    case _ => super.walkUnit(node)
  }

  def isInVd(vd: VarDecl, ds: List[VarDecl], vars: List[(Span, String)]) =
    ds.exists(d => vd2Str(d).equals(vd2Str(vd)))
  def isInFd(fd: FunDecl, ds: List[FunDecl]) =
    ds.exists(d => fd2Str(d).equals(fd2Str(fd)))
  def isVdInFd(vd: VarDecl, ds: List[FunDecl]) =
    ds.exists(d => fd2Str(d).equals(vd2Str(vd)))
  def hoist(body: List[SourceElement], isTopLevel: Boolean, params: List[Id], strict: Boolean) = {
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
                               else res++List(vd))
    // duplicated function declarations removed
    // last-come wins
    val fdsUniq = fds.foldRight(List[FunDecl]())((fd, res) =>
                                if (isInFd(fd, res)) res
                                else List(fd)++res)
    // variables with the same names with functions removed
    // function wins
    val vdsUniq2 = vdsUniq.foldRight(List[VarDecl]())((vd, res) =>
                                     if (isVdInFd(vd, fdsUniq)) res
                                     else List(vd)++res)
    // Set the strict field of function and variable declarations
    (fdsUniq.map(fd => fd match { case SFunDecl(i,f,_) => SFunDecl(i,f,strict) }),
     vdsUniq2.map(vd => vd match { case SVarDecl(i,n,e,_) => SVarDecl(i,n,e,strict) }),
     walk(rmFunDeclWalker.walk(body)).asInstanceOf[List[SourceElement]])
  }

  override def walk(node: Any): Any = node match {
    case SProgram(info, STopLevel(Nil, Nil, program)) =>
      val (fds, vds, new_program) =
          program.foldLeft((List[FunDecl](),
                            List[VarDecl](),
                            List[SourceElements]()))((r,ses) => {
                          val (fs,vs,ss) = hoist(toList(ses.getBody), true, Nil, ses.isStrict)
                          (r._1++fs, r._2++vs, r._3++List(SSourceElements(ses.getInfo, ss, ses.isStrict)))})
      SProgram(info, STopLevel(fds, vds, new_program))
    case pgm:Program =>
      throw new StaticError("Program before the hoisting phase should not have hoisted declarations.",
                            Some(pgm))
      pgm
    case SFunDecl(info, SFunctional(Nil, Nil, SSourceElements(i, body, str), name, params), strict) =>
      val (fds, vds, new_body) = hoist(body, false, params, strict)
      SFunDecl(info, SFunctional(fds, vds, SSourceElements(i, new_body, str), name, params), strict)
    case fd:FunDecl =>
      throw new StaticError("Function declarations before the hoisting phase should not have hoisted declarations.",
                            Some(fd))
      fd
    case SFunExpr(info, SFunctional(Nil, Nil, SSourceElements(i, body, strict), name, params)) =>
      val (fds, vds, new_body) = hoist(body, false, params, strict)
      SFunExpr(info, SFunctional(fds, vds, SSourceElements(i, new_body, strict), name, params))
    case fe:FunExpr =>
      throw new StaticError("Function expressions before the hoisting phase should not have hoisted declarations.",
                            Some(fe))
      fe
    case SGetProp(info, prop, SFunctional(Nil, Nil, SSourceElements(i, body, strict), name, params)) =>
      val (fds, vds, new_body) = hoist(body, false, params, strict)
      SGetProp(info, prop, SFunctional(fds, vds, SSourceElements(i, new_body, strict), name, params))
    case gp:GetProp =>
      throw new StaticError("Function expressions before the hoisting phase should not have hoisted declarations.",
                            Some(gp))
      gp
    case SSetProp(info, prop, SFunctional(Nil, Nil, SSourceElements(i, body, strict), name, params)) =>
      val (fds, vds, new_body) = hoist(body, false, params, strict)
      SSetProp(info, prop, SFunctional(fds, vds, SSourceElements(i, new_body, strict), name, params))
    case sp:SetProp =>
      throw new StaticError("Function expressions before the hoisting phase should not have hoisted declarations.",
                            Some(sp))
      sp
    case SVarStmt(info, vds) => stmtUnit(info, hoistVds(vds))
    case SForVar(info, vars, cond, action, body) =>
      val new_info = NU.spanInfoAll(vars)
      SBlock(new_info,
             List(stmtUnit(new_info, hoistVds(vars)),
                  walk(SFor(info, None, cond, action, body)).asInstanceOf[Stmt]),
             false)
    case SForVarIn(info, SVarDecl(i,n,None,_), expr, body) =>
      walk(SForIn(info, SVarRef(i,n), expr, body))
    case SForVarIn(info, SVarDecl(i,n,Some(e),_), expr, body) =>
      SBlock(info,
             List(stmtUnit(info, List(walk(assignS(i,n,e)).asInstanceOf[Stmt])),
                  walk(SForIn(info, SVarRef(i,n), expr, body)).asInstanceOf[Stmt]),
             false)
    case SLabelStmt(info, label, SForVar(i, vars, cond, action, body)) =>
      val new_info = NU.spanInfoAll(vars)
      SBlock(new_info,
             List(stmtUnit(info, hoistVds(vars)),
                  SLabelStmt(info, label,
                             walk(SFor(i, None, cond, action, body)).asInstanceOf[Stmt])),
             false)
    case SLabelStmt(info, label, SForVarIn(finfo, SVarDecl(i,n,Some(e),_), expr, body)) =>
      SBlock(info, List(stmtUnit(info, List(walk(assignS(i,n,e)).asInstanceOf[Stmt])),
                        SLabelStmt(info, label,
                                   walk(SForIn(info, SVarRef(i,n), expr, body)).asInstanceOf[Stmt])),
             false)

    case SLabelStmt(info, label, stmt) =>
      SLabelStmt(info, label, walk(stmt).asInstanceOf[Stmt])

    case _: Comment => node
    case _ => super.walk(node)
  }
}
