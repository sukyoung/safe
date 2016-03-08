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

import kr.ac.kaist.safe.exceptions.StaticError
import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.safe_util.{ NodeFactory => NF, NodeUtil => NU, Span }
import kr.ac.kaist.safe.useful.ErrorLog
import kr.ac.kaist.safe.Safe

/**
 * Eliminates ambiguities in an AST that can be resolved solely by knowing what
 * kind of entity a name refers to.  This class specifically handles
 * the following:
 *  - Variable/function/label references have unique internal names.
 *  - All name references that are undefined or used incorrectly are
 *    treated as static errors.
 */
class Disambiguator(program: Program) extends ASTWalker {
  /* Error handling
   * The signal function collects errors during the disambiguation phase.
   * To collect multiple errors,
   * we should return a dummy value after signaling an error.
   */
  val errors: ErrorLog = new ErrorLog
  def signal(msg: String, node: Node): Unit = errors.signal(msg, node)
  def signal(node: Node, msg: String): Unit = errors.signal(msg, node)
  def signal(error: StaticError): Unit = errors.signal(error)
  val assignErrors: ErrorLog = new ErrorLog
  def assignSignal(msg: String, node: Node): Unit = assignErrors.signal(msg, node)

  def getErrors(): List[StaticError] = errors.errors

  /* Environment for renaming identifiers. */
  type Env = List[(String, String)]
  val emptyLabel = ("empty", "empty")
  var env: Env = Safe.config.predVars.map(v => (v, v)) ++
    Safe.config.predFuns.map(f => (f, f)) ++ List(
      ("alert", "alert"),
      (NU.internalPrint, NU.internalPrint)
    )
  // The first Env is the label set of an enclosing IterationStatement
  // The second Env is the label set of an enclosing SwitchStatement
  // The third Env is the label set of an enclosing statement
  // The fourth Env is the label set of the current statement
  val emptyLabEnv = (List(), List(), List(), List())
  var labEnv: (Env, Env, Env, Env) = emptyLabEnv
  val thisName = "this"
  val argName = "arguments"

  /* 15.1.4 Constructor Properties of the Global Object
   * Object, Function, Array, String, Boolean, Number, Date, RegExp, Error,
   * EvalError, RangeError, ReferenceError, SyntaxError, TypeError, URIError
   */
  def addEnv(name: String, newid: Id): Unit = newid.uniqueName match {
    case None => signal("Identifier " + name + " is not bound.", newid)
    case Some(uniq) => env = (name, uniq) :: env
  }
  def addEnv(id: Id, newid: Id): Unit = newid.uniqueName match {
    case None => signal("Identifier " + id.text + " is not bound.", id)
    case Some(uniq) => env = (id.text, uniq) :: env
  }
  def addEnv(newid: Id): Unit = newid.uniqueName match {
    case None => signal("Identifier " + newid.text + " is not bound.", newid)
    case Some(uniq) => env = (newid.text, uniq) :: env
  }
  def addLEnv(id: Id, newid: Id): Unit = newid.uniqueName match {
    case None => signal("Identifier " + id.text + " is not bound.", id)
    case Some(uniq) =>
      labEnv = (labEnv._1, labEnv._2, labEnv._4 ++ labEnv._3, (id.text, uniq) :: labEnv._4)
  }
  def setLEnvCur(lab: (String, String)): Unit =
    labEnv = (labEnv._1, labEnv._2, labEnv._4 ++ labEnv._3, List(lab))
  def setLEnv(n: ASTNode): (Env, Env, Env, Env) = {
    val oldLEnv = labEnv
    labEnv = (labEnv._1, labEnv._2, labEnv._4 ++ labEnv._3, List())
    oldLEnv
  }
  def resetLEnv(lenv: (Env, Env, Env, Env)): Unit = labEnv = lenv

  def getEnvNoCheck(id: Id): String = {
    val name = id.text
    env.find(p => p._1.equals(name)) match {
      case Some((_, uniq)) => uniq
      case None =>
        val new_name = newId(id).uniqueName.get
        env = (name, new_name) :: env
        new_name
    }
  }

  def getEnvCheck(id: Id): String = {
    val name = id.text
    env.find(p => p._1.equals(name)) match {
      case Some((_, uniq)) => uniq
      case None =>
        if (!inWith)
          assignSignal("Identifier " + id.text + " is not bound.", id)
        name
    }
  }

  def inEnv(id: Id): Boolean = {
    val name = id.text
    env.find(p => p._1.equals(name)) match {
      case Some(_) => true
      case None => false
    }
  }
  def setEnv(envs: (Env, (Env, Env, Env, Env))): Unit =
    { env = envs._1; labEnv = envs._2 }

  def newId(span: Span, n: String): Id =
    Id(NF.makeASTNodeInfo(span), n, Some(n), false)
  def newId(id: Id): Id = id match {
    case Id(info, text, _, _) =>
      if (toplevel) Id(info, text, Some(text), false)
      else Id(info, text, Some(NU.freshName(text)), false)
  }
  def newId(id: Id, uniq: String): Id = id match {
    case Id(info, text, _, _) => Id(info, text, Some(uniq), false)
  }
  def newLabel(label: Label): Label = label match {
    case Label(info, id) => Label(info, newId(id))
  }
  def newLabel(label: Label, uniq: String): Label = label match {
    case Label(info, id) => Label(info, newId(id, uniq))
  }
  def newPropId(id: Id): PropId = id match {
    case Id(info, text, _, _) =>
      PropId(info, Id(info, text, Some(text), false))
  }

  def signalDataAccProp(name: String, n: Member): Unit =
    signal("ObjectLiteral may not have a data property and an accessor property with the same name. \"" + name + "\"", n)
  def signalGetProp(name: String, n: Member): Unit =
    signal("ObjectLiteral may not have multiple getter properties with the same name. \"" + name + "\"", n)
  def signalSetProp(name: String, n: Member): Unit =
    signal("ObjectLiteral may not have multiple setter properties with the same name. \"" + name + "\"", n)

  def checkDuplicatedProperty(members: List[Member]): Unit = {
    var member1Str: String = ""
    var member2Str: String = ""
    for (member1 <- members) {
      member1Str = NU.member2Str(member1)
      for (member2 <- members if member1.ne(member2)) {
        member2Str = NU.member2Str(member2)
        if (member1Str.equals(member2Str)) (member1, member2) match {
          case (Field(_, _, _), GetProp(_, _, _)) =>
            signalDataAccProp(member1Str, member2)
          case (Field(_, _, _), SetProp(_, _, _)) =>
            signalDataAccProp(member1Str, member2)
          case (GetProp(_, _, _), Field(_, _, _)) =>
            signalDataAccProp(member1Str, member2)
          case (SetProp(_, _, _), Field(_, _, _)) =>
            signalDataAccProp(member1Str, member2)
          case (GetProp(_, _, _), GetProp(_, _, _)) =>
            signalGetProp(member1Str, member2)
          case (SetProp(_, _, _), SetProp(_, _, _)) =>
            signalSetProp(member1Str, member2)
          case _ =>
        }
      }
    }
  }

  /* The main entry function */
  def doit(): Program = {
    val result = NU.simplifyWalker.walk(walk(program).asInstanceOf[Program]).asInstanceOf[Program]
    if (!hasAssign) for (e <- assignErrors.errors) signal(e)
    result
  }

  var toplevel = false
  /* IterationStatement: DoWhile, While, For, ForIn */
  var inIterator = false
  var inSwitch = false
  var inFunctionBody = false
  var inWith = false
  var hasAssign = false
  def isAssignOp(op: Op): Boolean = op match {
    case Op(_, text) => text.equals("++") || text.equals("--")
  }
  def mkInIterator(): Unit = {
    inIterator = true
    if (labEnv._4.isEmpty) setLEnvCur(emptyLabel)
    labEnv = (labEnv._4 ++ labEnv._1, labEnv._2, labEnv._3, labEnv._4)
  }
  def mkInSwitch(): Unit = {
    inSwitch = true
    if (labEnv._4.isEmpty) setLEnvCur(emptyLabel)
    labEnv = (labEnv._1, labEnv._4 ++ labEnv._2, labEnv._3, labEnv._4)
  }
  def functional(i: ASTNodeInfo, span: Span, name: Id, params: List[Id], fds: List[FunDecl],
    vds: List[VarDecl], body: SourceElements, bodyS: String): Functional = {
    val old_toplevel = toplevel
    toplevel = false
    labEnv = emptyLabEnv
    addEnv(argName, newId(newId(span, argName)))
    val pairs_params = params.map(p => (p, newId(p)))
    pairs_params.foreach(p => addEnv(p._1, p._2))
    fds.foreach(fd => addEnv(fd.ftn.name, newId(fd.ftn.name)))
    val new_vds = vds.foldLeft(List[VarDecl]())((vds, vd) => vd match {
      case VarDecl(info, id, _, strict) => params.find(p => p.text.equals(id.text)) match {
        case None =>
          val new_id = newId(id)
          addEnv(id, new_id)
          vds :+ VarDecl(info, new_id, None, strict)
        case _ => vds
      }
    })
    val new_fds = fds.map(walk).asInstanceOf[List[FunDecl]]
    val oldInFunctionBody = inFunctionBody
    inFunctionBody = true
    val new_body = body match {
      case SourceElements(i, stmts, strict) =>
        SourceElements(i, stmts.map(walk).asInstanceOf[List[SourceElement]], strict)
    }
    inFunctionBody = oldInFunctionBody
    toplevel = old_toplevel
    Functional(i, new_fds, new_vds, new_body, name, pairs_params.map(p => p._2), bodyS)
  }

  override def walk(node: Any): Any = node match {
    case Program(info, TopLevel(it, fds, vds, body)) =>
      fds.foreach(fd => addEnv(fd.ftn.name, newId(fd.info.span, fd.ftn.name.text)))
      val new_vds = vds.map(p => p match {
        case VarDecl(i, id, _, strict) =>
          val new_id = newId(i.span, id.text)
          addEnv(id, new_id)
          VarDecl(info, new_id, None, strict)
      })
      val new_fds = fds.map(walk).asInstanceOf[List[FunDecl]]
      toplevel = true
      Program(
        info,
        TopLevel(it, new_fds, new_vds,
          body.map(walk).asInstanceOf[List[SourceElements]])
      )

    case SourceElements(info, stmts, strict) =>
      SourceElements(
        info,
        stmts.map(walk).asInstanceOf[List[Stmt]], strict
      )

    case FunDecl(info, Functional(i, fds, vds, body, name, params, bodyS), strict) =>
      val old_env = (env, labEnv)
      val new_name = newId(name, getEnvNoCheck(name))
      val result = FunDecl(
        info,
        functional(i, info.span, new_name, params, fds, vds, body, bodyS), strict
      )
      setEnv(old_env._1, old_env._2)
      result

    case FunExpr(info, Functional(i, fds, vds, body, name, params, bodyS)) =>
      val old_env = (env, labEnv)
      val old_toplevel = toplevel
      toplevel = false
      val new_name = newId(name)
      addEnv(name, new_name)
      val result = FunExpr(
        info,
        functional(i, info.span, new_name, params, fds, vds, body, bodyS)
      )
      setEnv(old_env)
      toplevel = old_toplevel
      result
    case GetProp(info, prop, Functional(i, fds, vds, body, name, params, bodyS)) =>
      val old_env = (env, labEnv)
      val new_prop = newPropId(name)
      val new_name = new_prop.id
      val result = GetProp(info, new_prop,
        functional(i, info.span, new_name, params, fds, vds, body, bodyS))
      setEnv(old_env)
      result
    case SetProp(info, prop, Functional(i, fds, vds, body, name, params, bodyS)) =>
      val old_env = (env, labEnv)
      val new_prop = newPropId(name)
      val new_name = new_prop.id
      val result = SetProp(info, new_prop,
        functional(i, info.span, new_name, params, fds, vds, body, bodyS))
      setEnv(old_env)
      result

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
      val new_target = target match {
        case Some(label) => labEnv._3.find(p => p._1.equals(label.id.text)) match {
          case None => labEnv._2.find(p => p._1.equals(label.id.text)) match {
            case None =>
              signal("Break occurs outside of a label.", br)
              return EmptyStmt(info)
            case Some((_, uniq)) => Some(newLabel(label, uniq))
          }
          case Some((_, uniq)) => Some(newLabel(label, uniq))
        }
        case None =>
          if (!inIterator && !inSwitch) {
            signal("Break occurs outside of an iterator or a switch.", br)
            return EmptyStmt(info)
          } else None
      }
      Break(info, new_target)

    case Catch(info, id, body) =>
      val old_toplevel = toplevel
      toplevel = false
      val old_env = (env, labEnv)
      val new_id = newId(id)
      addEnv(id, new_id)
      setLEnv(node.asInstanceOf[ASTNode])
      val result = Catch(info, new_id, walk(body).asInstanceOf[List[Stmt]])
      setEnv(old_env)
      toplevel = old_toplevel
      result
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
        signal("Continue occurs outside of an iterator.", c);
        return EmptyStmt(info)
      } else {
        val new_target = target match {
          case Some(label) => labEnv._1.find(p => p._1.equals(label.id.text)) match {
            case None =>
              signal("Continue occurs outside of a label.", c)
              return EmptyStmt(info)
            case Some((_, uniq)) => Some(newLabel(label, uniq))
          }
          case None => None
        }
        Continue(info, new_target)
      }
    case _: DoWhile =>
      val old_env = (env, labEnv)
      val oldInIterator = inIterator
      mkInIterator
      val result = super.walk(node)
      inIterator = oldInIterator
      setEnv(old_env)
      result
    case _: For =>
      val old_env = (env, labEnv)
      val oldInIterator = inIterator
      mkInIterator
      val result = super.walk(node)
      inIterator = oldInIterator
      val newEnv = env
      setEnv(old_env)
      env = newEnv
      result
    case _: ForIn =>
      val old_env = (env, labEnv)
      hasAssign = true
      val oldInIterator = inIterator
      mkInIterator
      val result = super.walk(node)
      inIterator = oldInIterator
      val newEnv = env
      setEnv(old_env)
      env = newEnv
      result
    case fv: ForVar =>
      signal("ForVar should be replaced by the hoister.", fv)
    case fv: ForVarIn =>
      signal("ForVarIn should be replaced by the hoister.", fv)
    case ls @ LabelStmt(info, label @ Label(_, Id(_, name, _, _)), stmt) =>
      labEnv._4.find(p => p._1.equals(name)) match {
        case Some(_) =>
          signal("Multiple declarations of the label: " + name + ".", ls)
          ls
        case None =>
          val old_env = (env, labEnv)
          val new_label = newLabel(label)
          addLEnv(label.id, new_label.id)
          val result = LabelStmt(info, new_label, walk(stmt).asInstanceOf[Stmt])
          setEnv(old_env)
          result
      }

    /* 12.9 The return Statement
     * An ECMAScript program is considered syntactically incorrect if it contains
     * a return statement that is not within a FunctionBody.
     */
    case rt @ Return(info, expr) =>
      if (!inFunctionBody)
        signal("Return occurs outside of a function body.", rt)
      val oldLEnv = setLEnv(node.asInstanceOf[ASTNode])
      val result = super.walk(rt)
      resetLEnv(oldLEnv)
      result

    case _: Switch =>
      val old_env = (env, labEnv)
      val oldInSwitch = inSwitch
      mkInSwitch
      val result = super.walk(node)
      inSwitch = oldInSwitch
      setEnv(old_env)
      result
    case VarRef(info, id) => VarRef(info, newId(id, getEnvCheck(id)))
    case vs: VarStmt => signal("VarStmt should be replaced by the hoister.", vs)
    case _: While =>
      val old_env = (env, labEnv)
      val oldInIterator = inIterator
      mkInIterator
      val result = super.walk(node)
      inIterator = oldInIterator
      setEnv(old_env)
      result
    case ObjectExpr(info, members) =>
      checkDuplicatedProperty(members)
      val oldLEnv = setLEnv(node.asInstanceOf[ASTNode])
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result
    case _: With =>
      val old_env = (env, labEnv)
      val oldInWith = inWith
      inWith = true
      setLEnv(node.asInstanceOf[ASTNode])
      val result = super.walk(node)
      inWith = oldInWith
      setEnv(old_env)
      result
    case AssignOpApp(info, Parenthesized(_, lhs), op, right) if lhs.isInstanceOf[LHS] =>
      val oldLEnv = setLEnv(node.asInstanceOf[ASTNode])
      val result = walk(AssignOpApp(info, lhs.asInstanceOf[LHS], op, right))
      resetLEnv(oldLEnv)
      result
    case AssignOpApp(info, VarRef(_, id), op, right) =>
      hasAssign = true
      val oldLEnv = setLEnv(node.asInstanceOf[ASTNode])
      val old_toplevel = toplevel
      val new_id = if (!inEnv(id)) {
        toplevel = true
        newId(id)
      } else newId(id, getEnvNoCheck(id))
      toplevel = old_toplevel
      val result = AssignOpApp(info, VarRef(info, new_id),
        op, walk(right).asInstanceOf[Expr])
      resetLEnv(oldLEnv)
      if (!inEnv(id)) {
        toplevel = true
        addEnv(id, newId(id))
        toplevel = old_toplevel
      }
      result
    case _: AssignOpApp =>
      hasAssign = true
      val oldLEnv = setLEnv(node.asInstanceOf[ASTNode])
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result
    case _: UnaryAssignOpApp =>
      hasAssign = true
      val oldLEnv = setLEnv(node.asInstanceOf[ASTNode])
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result
    case n: PrefixOpApp =>
      if (isAssignOp(n.op)) hasAssign = true
      val oldLEnv = setLEnv(node.asInstanceOf[ASTNode])
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result
    case RegularExpression(info, body, flags) => {
      val regexp = "RegExp"
      New(info, FunApp(info, VarRef(info, Id(info, regexp, Some(regexp), false)),
        List(
          StringLiteral(info, "\"", NU.escape(body)),
          StringLiteral(info, "\"", flags)
        )))
    }
    case _: Comment => node
    case _: ASTNode =>
      val oldLEnv = setLEnv(node.asInstanceOf[ASTNode])
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result
    case _ => super.walk(node)
  }
}
