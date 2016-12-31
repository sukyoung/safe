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
import kr.ac.kaist.safe.{ PRED_FUNS, PRED_VARS }

/**
 * Eliminates ambiguities in an AST that can be resolved solely by knowing what
 * kind of entity a name refers to.  This class specifically handles
 * the following:
 *  - Variable/function/label references have unique internal names.
 *  - All name references that are undefined or used incorrectly are
 *    treated as static errors.
 */
class Disambiguator(program: Program) {
  ////////////////////////////////////////////////////////////////
  // results
  ////////////////////////////////////////////////////////////////

  lazy val result: Program =
    NU.SimplifyWalker.walk(DisambWalker.walk(program))
  lazy val excLog: ExcLog =
    if (hasAssign) normalExcLog
    else normalExcLog + assExcLog

  ////////////////////////////////////////////////////////////////
  // private mutable
  ////////////////////////////////////////////////////////////////

  // temporal exception log
  private val normalExcLog = new ExcLog
  private val assExcLog = new ExcLog

  // inclusion checking
  private var toplevel = false
  private var inIterator = false // DoWhile, While, For, ForIn
  private var inSwitch = false
  private var inFunctionBody = false
  private var inWith = false
  private var hasAssign = false

  // environment for renaming identifiers.
  private type Env = List[(String, String)]
  private val EMPTY_LABEL = ("empty", "empty")
  private var env: Env = PRED_VARS.map(v => (v, v)) ++
    PRED_FUNS.map(f => (f, f)) ++ List(
      ("alert", "alert")
    )

  // label environment
  private case class LabEnv(
    encIter: Env, // encIter  : enclosing IterationStatement
    encSwitch: Env, // encSwitch: enclosing SwitchStatement
    encStmt: Env, // encStmt  : enclosing statement
    curStmt: Env // curStmt  : current statement
  )
  private val EMPTY_LAB_ENV = LabEnv(List(), List(), List(), List())
  private var labEnv: LabEnv = EMPTY_LAB_ENV

  // default "arguments" name
  private val ARGS_NAME = "arguments"

  ////////////////////////////////////////////////////////////////
  // helper function
  ////////////////////////////////////////////////////////////////

  /* 15.1.4 Constructor Properties of the Global Object
   * Object, Function, Array, String, Boolean, Number, Date, RegExp, Error,
   * EvalError, RangeError, ReferenceError, SyntaxError, TypeError, URIError
   */

  // add environment
  private def addEnv(name: String, newid: Id): Unit = newid.uniqueName match {
    case None => normalExcLog.signal(IdNotBoundError(name, newid))
    case Some(uniq) => env = (name, uniq) :: env
  }
  private def addEnv(id: Id, newid: Id): Unit = newid.uniqueName match {
    case None => normalExcLog.signal(IdNotBoundError(id.text, id))
    case Some(uniq) => env = (id.text, uniq) :: env
  }
  private def addEnv(newid: Id): Unit = newid.uniqueName match {
    case None => normalExcLog.signal(IdNotBoundError(newid.text, newid))
    case Some(uniq) => env = (newid.text, uniq) :: env
  }

  // add label environment
  private def addLEnv(id: Id, newid: Id): Unit = newid.uniqueName match {
    case None => normalExcLog.signal(IdNotBoundError(id.text, id))
    case Some(uniq) =>
      labEnv = LabEnv(labEnv.encIter, labEnv.encSwitch, labEnv.curStmt ++ labEnv.encStmt, (id.text, uniq) :: labEnv.curStmt)
  }

  // set current label environment
  private def setLEnvCur(lab: (String, String)): Unit =
    labEnv = LabEnv(labEnv.encIter, labEnv.encSwitch, labEnv.curStmt ++ labEnv.encStmt, List(lab))

  // set label environment
  private def setLEnv(n: ASTNode): LabEnv = {
    val oldLEnv = labEnv
    labEnv = LabEnv(labEnv.encIter, labEnv.encSwitch, labEnv.curStmt ++ labEnv.encStmt, List())
    oldLEnv
  }

  // reset label environment
  private def resetLEnv(lenv: LabEnv): Unit = labEnv = lenv

  // get environemt without id checking
  private def getEnvNoCheck(id: Id): String = {
    val name = id.text
    env.find { case (n, _) => n.equals(name) } match {
      case Some((_, uniq)) => uniq
      case None =>
        val newName = newId(id).uniqueName.get
        env = (name, newName) :: env
        newName
    }
  }

  // get environemt with id checking
  private def getEnvCheck(id: Id): String = {
    val name = id.text
    env.find { case (n, _) => n.equals(name) } match {
      case Some((_, uniq)) => uniq
      case None =>
        if (!inWith)
          assExcLog.signal(IdNotBoundError(id.text, id))
        name
    }
  }

  // environment inclusion check
  private def inEnv(id: Id): Boolean = {
    val name = id.text
    env.find { case (n, _) => n.equals(name) } match {
      case Some(_) => true
      case None => false
    }
  }

  // set environment
  private def setEnv(envs: (Env, LabEnv)): Unit = envs match {
    case (e, le) => env = e; labEnv = le
  }

  // create new id
  private def newId(span: Span, n: String): Id =
    Id(NU.makeASTNodeInfo(span), n, Some(n), false)
  private def newId(id: Id): Id = id match {
    case Id(info, text, _, _) =>
      if (toplevel) Id(info, text, Some(text), false)
      else Id(info, text, Some(NU.freshName(text)), false)
  }
  private def newId(id: Id, uniq: String): Id = id match {
    case Id(info, text, _, _) => Id(info, text, Some(uniq), false)
  }

  // create new label
  private def newLabel(label: Label): Label = label match {
    case Label(info, id) => Label(info, newId(id))
  }
  private def newLabel(label: Label, uniq: String): Label = label match {
    case Label(info, id) => Label(info, newId(id, uniq))
  }

  // create new property id
  private def newPropId(id: Id): PropId = id match {
    case Id(info, text, _, _) =>
      PropId(info, Id(info, text, Some(text), false))
  }

  // check duplicated property
  private def checkDuplicatedProperty(members: List[Member]): Unit = {
    var member1Str: String = ""
    var member2Str: String = ""
    for (member1 <- members) {
      member1Str = member1.toString
      for (member2 <- members if member1.ne(member2)) {
        member2Str = member2.toString
        if (member1Str.equals(member2Str)) (member1, member2) match {
          case (Field(_, _, _), GetProp(_, _, _)) =>
            normalExcLog.signal(DataAccPropError(member1Str, member2))
          case (Field(_, _, _), SetProp(_, _, _)) =>
            normalExcLog.signal(DataAccPropError(member1Str, member2))
          case (GetProp(_, _, _), Field(_, _, _)) =>
            normalExcLog.signal(DataAccPropError(member1Str, member2))
          case (SetProp(_, _, _), Field(_, _, _)) =>
            normalExcLog.signal(DataAccPropError(member1Str, member2))
          case (GetProp(_, _, _), GetProp(_, _, _)) =>
            normalExcLog.signal(GetPropError(member1Str, member2))
          case (SetProp(_, _, _), SetProp(_, _, _)) =>
            normalExcLog.signal(SetPropError(member1Str, member2))
          case _ =>
        }
      }
    }
  }

  // assign operator check
  private def isAssignOp(op: Op): Boolean = op match {
    case Op(_, text) => text.equals("++") || text.equals("--")
  }

  // enter iterator
  private def mkInIterator(): Unit = {
    inIterator = true
    if (labEnv.curStmt.isEmpty) setLEnvCur(EMPTY_LABEL)
    labEnv = LabEnv(labEnv.curStmt ++ labEnv.encIter, labEnv.encSwitch, labEnv.encStmt, labEnv.curStmt)
  }

  // enter switch statement
  private def mkInSwitch(): Unit = {
    inSwitch = true
    if (labEnv.curStmt.isEmpty) setLEnvCur(EMPTY_LABEL)
    labEnv = LabEnv(labEnv.encIter, labEnv.curStmt ++ labEnv.encSwitch, labEnv.encStmt, labEnv.curStmt)
  }

  // walker for disambiguating ASTNode
  private object DisambWalker extends ASTWalker {
    def functional(i: ASTNodeInfo, span: Span, name: Id, params: List[Id], fds: List[FunDecl],
      vds: List[VarDecl], body: SourceElements, bodyS: String): Functional = {
      val oldToplevel = toplevel
      toplevel = false
      labEnv = EMPTY_LAB_ENV
      addEnv(ARGS_NAME, newId(newId(span, ARGS_NAME)))
      val pairsParams = params.map(p => (p, newId(p)))
      pairsParams.foreach { case (p, nid) => addEnv(p, nid) }
      fds.foreach(fd => addEnv(fd.ftn.name, newId(fd.ftn.name)))
      val newVds = vds.foldLeft(List[VarDecl]())((vds, vd) => vd match {
        case VarDecl(info, id, _, strict) => params.find(p => p.text.equals(id.text)) match {
          case None =>
            val nid = newId(id)
            addEnv(id, nid)
            vds :+ VarDecl(info, nid, None, strict)
          case _ => vds
        }
      })
      val newFds = fds.map(walk)
      val oldInFunctionBody = inFunctionBody
      inFunctionBody = true
      val newBody = body match {
        case SourceElements(i, stmts, strict) =>
          SourceElements(i, stmts.map(walk), strict)
      }
      inFunctionBody = oldInFunctionBody
      toplevel = oldToplevel
      Functional(i, newFds, newVds, newBody, name, pairsParams.map { case (_, nid) => nid }, bodyS)
    }

    override def walk(node: Program): Program = node match {
      case Program(info, TopLevel(it, fds, vds, body)) =>
        fds.foreach(fd => addEnv(fd.ftn.name, newId(fd.info.span, fd.ftn.name.text)))
        val newVds = vds.map(p => p match {
          case VarDecl(i, id, _, strict) =>
            val nid = newId(i.span, id.text)
            addEnv(id, nid)
            VarDecl(info, nid, None, strict)
        })
        val newFds = fds.map(walk)
        toplevel = true
        Program(info, TopLevel(it, newFds, newVds, body.map(walk)))
    }

    override def walk(node: SourceElements): SourceElements = node match {
      case SourceElements(info, stmts, strict) =>
        SourceElements(info, stmts.map(walk), strict)
    }

    override def walk(node: FunDecl): FunDecl = node match {
      case FunDecl(info, Functional(i, fds, vds, body, name, params, bodyS), strict) =>
        val oldEnv = (env, labEnv)
        val newName = newId(name, getEnvNoCheck(name))
        val result = FunDecl(
          info,
          functional(i, info.span, newName, params, fds, vds, body, bodyS), strict
        )
        setEnv(oldEnv)
        result
    }

    override def walk(node: LHS): LHS = node match {
      case FunExpr(info, Functional(i, fds, vds, body, name, params, bodyS)) =>
        val oldEnv = (env, labEnv)
        val oldToplevel = toplevel
        toplevel = false
        val newName = newId(name)
        addEnv(name, newName)
        val result = FunExpr(
          info,
          functional(i, info.span, newName, params, fds, vds, body, bodyS)
        )
        setEnv(oldEnv)
        toplevel = oldToplevel
        result
      case VarRef(info, id) => VarRef(info, newId(id, getEnvCheck(id)))
      case ObjectExpr(info, members) =>
        checkDuplicatedProperty(members)
        val oldLEnv = setLEnv(node)
        val result = super.walk(node)
        resetLEnv(oldLEnv)
        result
      case RegularExpression(info, body, flags) => {
        val regexp = "RegExp"
        New(info, FunApp(info, VarRef(info, Id(info, regexp, Some(regexp), false)),
          List(
            StringLiteral(info, "\"", body, true),
            StringLiteral(info, "\"", flags, false)
          )))
      }
      case _ =>
        val oldLEnv = setLEnv(node)
        val result = super.walk(node)
        resetLEnv(oldLEnv)
        result
    }

    override def walk(node: Member): Member = node match {
      case GetProp(info, prop, Functional(i, fds, vds, body, name, params, bodyS)) =>
        val oldEnv = (env, labEnv)
        val newProp = newPropId(name)
        val newName = newProp.id
        val result = GetProp(info, newProp,
          functional(i, info.span, newName, params, fds, vds, body, bodyS))
        setEnv(oldEnv)
        result
      case SetProp(info, prop, Functional(i, fds, vds, body, name, params, bodyS)) =>
        val oldEnv = (env, labEnv)
        val newProp = newPropId(name)
        val newName = newProp.id
        val result = SetProp(info, newProp,
          functional(i, info.span, newName, params, fds, vds, body, bodyS))
        setEnv(oldEnv)
        result
      case _ =>
        val oldLEnv = setLEnv(node)
        val result = super.walk(node)
        resetLEnv(oldLEnv)
        result
    }

    override def walk(node: Catch): Catch = node match {
      case Catch(info, id, body) =>
        val oldToplevel = toplevel
        toplevel = false
        val oldEnv = (env, labEnv)
        val nid = newId(id)
        addEnv(id, nid)
        setLEnv(node)
        val result = Catch(info, nid, body.map(walk))
        setEnv(oldEnv)
        toplevel = oldToplevel
        result
    }

    override def walk(node: Stmt): Stmt = node match {
      /* 12.8 The break Statement
       * A program is considered syntactically incorrect if either of the following is true:
       *  - The program contains a break statement without the optional Identifier,
       *    which is not nested, directly or indirectly (but not crossing function boundaries),
       *    within an IterationStatement or a SwitchStatement.
       *  - The program contains a break statement with the optional Identifier,
       *    where Identifier does not appear in the label set of an enclosing
       *    (but not crossing function boundaries) Statement.
       */
      case br @ Break(info, target) =>
        val newTarget = target match {
          case Some(label) => labEnv.encStmt.find { case (n, _) => n.equals(label.id.text) } match {
            case None => labEnv.encSwitch.find { case (n, _) => n.equals(label.id.text) } match {
              case None =>
                normalExcLog.signal(OutsideBreakError(br, "a label"))
                return EmptyStmt(info)
              case Some((_, uniq)) => Some(newLabel(label, uniq))
            }
            case Some((_, uniq)) => Some(newLabel(label, uniq))
          }
          case None =>
            if (!inIterator && !inSwitch) {
              normalExcLog.signal(OutsideBreakError(br, "an iterator or a switch."))
              return EmptyStmt(info)
            } else None
        }
        Break(info, newTarget)

      /* 12.7 The continue Statement
       * A program is considered syntactically incorrect if either of the following is true:
       *  - The program contains a continue statement without the optional Identifier,
       *    which is not nested, directly or indirectly (but not crossing function boundaries),
       *    within an IterationStatement.
       *  - The program contains a continue statement with the optional Identifier,
       *    where Identifier does not appear in the label set of an enclosing
       *    (but not crossing function boundaries) IterationStatement.
       */
      case c @ Continue(info, target) =>
        if (!inIterator) {
          normalExcLog.signal(OutsideContError(c, "an iterator."))
          return EmptyStmt(info)
        } else {
          val newTarget = target match {
            case Some(label) => labEnv.encIter.find { case (n, _) => n.equals(label.id.text) } match {
              case None =>
                normalExcLog.signal(OutsideContError(c, "a label."))
                return EmptyStmt(info)
              case Some((_, uniq)) => Some(newLabel(label, uniq))
            }
            case None => None
          }
          Continue(info, newTarget)
        }

      case _: DoWhile =>
        val oldEnv = (env, labEnv)
        val oldInIterator = inIterator
        mkInIterator
        val result = super.walk(node)
        inIterator = oldInIterator
        setEnv(oldEnv)
        result

      case _: For =>
        val oldEnv = (env, labEnv)
        val oldInIterator = inIterator
        mkInIterator
        val result = super.walk(node)
        inIterator = oldInIterator
        val newEnv = env
        setEnv(oldEnv)
        env = newEnv
        result

      case _: ForIn =>
        val oldEnv = (env, labEnv)
        hasAssign = true
        val oldInIterator = inIterator
        mkInIterator
        val result = super.walk(node)
        inIterator = oldInIterator
        val newEnv = env
        setEnv(oldEnv)
        env = newEnv
        result

      case fv: ForVar =>
        normalExcLog.signal(NotReplacedByHoisterError(fv))
        fv

      case fv: ForVarIn =>
        normalExcLog.signal(NotReplacedByHoisterError(fv))
        fv

      case ls @ LabelStmt(info, label @ Label(_, Id(_, name, _, _)), stmt) =>
        labEnv.curStmt.find { case (n, _) => n.equals(name) } match {
          case Some(_) =>
            normalExcLog.signal(MultipleLabelDeclError(name, ls))
            ls
          case None =>
            val oldEnv = (env, labEnv)
            val nlabel = newLabel(label)
            addLEnv(label.id, nlabel.id)
            val result = LabelStmt(info, nlabel, walk(stmt))
            setEnv(oldEnv)
            result
        }

      /* 12.9 The return Statement
       * An ECMAScript program is considered syntactically incorrect if it contains
       * a return statement that is not within a FunctionBody.
       */
      case rt @ Return(info, expr) =>
        if (!inFunctionBody)
          normalExcLog.signal(OutsideRetrunError(rt))
        val oldLEnv = setLEnv(node)
        val result = super.walk(rt)
        resetLEnv(oldLEnv)
        result

      case _: Switch =>
        val oldEnv = (env, labEnv)
        val oldInSwitch = inSwitch
        mkInSwitch
        val result = super.walk(node)
        inSwitch = oldInSwitch
        setEnv(oldEnv)
        result

      case vs: VarStmt =>
        normalExcLog.signal(NotReplacedByHoisterError(vs))
        vs

      case _: While =>
        val oldEnv = (env, labEnv)
        val oldInIterator = inIterator
        mkInIterator
        val result = super.walk(node)
        inIterator = oldInIterator
        setEnv(oldEnv)
        result

      case _: With =>
        val oldEnv = (env, labEnv)
        val oldInWith = inWith
        inWith = true
        setLEnv(node)
        val result = super.walk(node)
        inWith = oldInWith
        setEnv(oldEnv)
        result

      case _ =>
        val oldLEnv = setLEnv(node)
        val result = super.walk(node)
        resetLEnv(oldLEnv)
        result
    }

    override def walk(node: Expr): Expr = node match {
      case AssignOpApp(info, Parenthesized(_, lhs), op, right) if lhs.isInstanceOf[LHS] =>
        val oldLEnv = setLEnv(node)
        val result = walk(AssignOpApp(info, lhs.asInstanceOf[LHS], op, right))
        resetLEnv(oldLEnv)
        result

      case AssignOpApp(info, VarRef(_, id), op, right) =>
        hasAssign = true
        val oldLEnv = setLEnv(node)
        val oldToplevel = toplevel
        val nid = if (!inEnv(id)) {
          toplevel = true
          newId(id)
        } else newId(id, getEnvNoCheck(id))
        toplevel = oldToplevel
        val result = AssignOpApp(info, VarRef(info, nid), op, walk(right))
        resetLEnv(oldLEnv)
        if (!inEnv(id)) {
          toplevel = true
          addEnv(id, newId(id))
          toplevel = oldToplevel
        }
        result

      case _: AssignOpApp =>
        hasAssign = true
        val oldLEnv = setLEnv(node)
        val result = super.walk(node)
        resetLEnv(oldLEnv)
        result

      case _: UnaryAssignOpApp =>
        hasAssign = true
        val oldLEnv = setLEnv(node)
        val result = super.walk(node)
        resetLEnv(oldLEnv)
        result

      case n: PrefixOpApp =>
        if (isAssignOp(n.op)) hasAssign = true
        val oldLEnv = setLEnv(node)
        val result = super.walk(node)
        resetLEnv(oldLEnv)
        result

      case _ =>
        val oldLEnv = setLEnv(node)
        val result = super.walk(node)
        resetLEnv(oldLEnv)
        result
    }
  }

  ////////////////////////////////////////////////////////////////
  // calculate results
  ////////////////////////////////////////////////////////////////

  (result, excLog)
}
