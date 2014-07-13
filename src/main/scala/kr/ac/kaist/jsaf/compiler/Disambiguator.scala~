/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ***************************************************************************** */

package kr.ac.kaist.jsaf.compiler

import _root_.java.util.{List => JList}
import kr.ac.kaist.jsaf.Samsung
import kr.ac.kaist.jsaf.Shell
import kr.ac.kaist.jsaf.ShellParameters
import kr.ac.kaist.jsaf.exceptions.StaticError
import kr.ac.kaist.jsaf.nodes._
import kr.ac.kaist.jsaf.nodes_util.{ NodeFactory => NF }
import kr.ac.kaist.jsaf.nodes_util.{ NodeUtil => NU }
import kr.ac.kaist.jsaf.nodes_util.Span
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.ErrorLog
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.useful.HasAt

/**
 * Eliminates ambiguities in an AST that can be resolved solely by knowing what
 * kind of entity a name refers to.  This class specifically handles
 * the following:
 *  - Variable/function/label references have unique internal names.
 *  - All name references that are undefined or used incorrectly are
 *    treated as static errors.
 */
class Disambiguator(program: Program, disambiguateOnly: Boolean) extends Walker {
  /* Error handling
   * The signal function collects errors during the disambiguation phase.
   * To collect multiple errors,
   * we should return a dummy value after signaling an error.
   */
  val errors: ErrorLog = new ErrorLog
  def signal(msg:String, hasAt:HasAt) = errors.signal(msg, hasAt)
  def signal(hasAt:HasAt, msg:String) = errors.signal(msg, hasAt)
  def signal(error: StaticError) = errors.signal(error)
  val assignErrors: ErrorLog = new ErrorLog
  def assignSignal(msg:String, hasAt:HasAt) = assignErrors.signal(msg, hasAt)

  def getErrors(): JList[StaticError] = toJavaList(errors.errors)

  /* Environment for renaming identifiers. */
  type Env = List[(String, String)]
  val emptyLabel = ("empty", "empty")
  val pred = if (Shell.pred != null) Shell.pred
             else if (Samsung.pred != null) Samsung.pred
             else new Predefined(new ShellParameters())
  var env: Env = pred.vars.map(v => (v,v)) ++
                 pred.funs.map(f => (f,f)) ++ List(("alert", "alert"), // alert???
                                                   (NU.internalPrint, NU.internalPrint))
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
  def addEnv(name: String, newid: Id): Unit = toOption(newid.getUniqueName) match {
    case None => signal("Identifier " + name + " is not bound.", newid)
    case Some(uniq) => env = (name, uniq)::env
  }
  def addEnv(id: Id, newid: Id): Unit = toOption(newid.getUniqueName) match {
    case None => signal("Identifier " + id.getText + " is not bound.", id)
    case Some(uniq) => env = (id.getText, uniq)::env
  }
  def addEnv(newid: Id): Unit = toOption(newid.getUniqueName) match {
    case None => signal("Identifier " + newid.getText + " is not bound.", newid)
    case Some(uniq) => env = (newid.getText, uniq)::env
  }
  def addLEnv(id: Id, newid: Id): Unit = toOption(newid.getUniqueName) match {
    case None => signal("Identifier " + id.getText + " is not bound.", id)
    case Some(uniq) =>
      labEnv = (labEnv._1, labEnv._2, labEnv._4++labEnv._3, (id.getText, uniq)::labEnv._4)
  }
  def setLEnvCur(lab: (String, String)) =
    labEnv = (labEnv._1, labEnv._2, labEnv._4++labEnv._3, List(lab))
  def setLEnv(n:AbstractNode) = {
    val oldLEnv = labEnv
    labEnv = (labEnv._1, labEnv._2, labEnv._4++labEnv._3, List())
    oldLEnv
  }
  def resetLEnv(lenv: (Env, Env, Env, Env)) = labEnv = lenv

  def getEnvNoCheck(id: Id): String = {
    val name = id.getText
    env.find(p => p._1.equals(name)) match {
      case Some((_, uniq)) => uniq
      case None =>
        val new_name = newId(id).getUniqueName.get
        env = (name, new_name)::env
        new_name
    }
  }

  def getEnvCheck(id: Id): String = {
    val name = id.getText
    env.find(p => p._1.equals(name)) match {
      case Some((_, uniq)) => uniq
      case None =>
        if (!inWith && disambiguateOnly)
          assignSignal("Identifier " + id.getText + " is not bound.", id)
        name
    }
  }

  def inEnv(id: Id): Boolean = {
    val name = id.getText
    env.find(p => p._1.equals(name)) match {
      case Some(_) => true
      case None => false
    }
  }
  def setEnv(envs: (Env, (Env,Env,Env,Env))) =
    { env = envs._1; labEnv = envs._2 }

  def newId(span: Span, n: String) =
    SId(NF.makeSpanInfo(span), n, Some(n), false)
  def newId(id: Id): Id = id match {
    case SId(info, text, _, _) =>
      if (toplevel) SId(info, text, Some(text), false)
      else SId(info, text, Some(NU.freshName(text)), false)
  }
  def newId(id: Id, uniq: String): Id = id match {
    case SId(info, text, _, _) => SId(info, text, Some(uniq), false)
  }
  def newLabel(label: Label): Label = label match {
    case SLabel(info, id) => SLabel(info, newId(id))
  }
  def newLabel(label: Label, uniq: String): Label = label match {
    case SLabel(info, id) => SLabel(info, newId(id, uniq))
  }
  def newPropId(id: Id) = id match {
    case SId(info, text, _, _) =>
      SPropId(info, SId(info, text, Some(text), false))
  }

  def signalDataAccProp(name: String, n: Member) =
    signal("ObjectLiteral may not have a data property and an accessor property with the same name. \"" + name + "\"", n)
  def signalGetProp(name: String, n: Member) =
    signal("ObjectLiteral may not have multiple getter properties with the same name. \"" + name + "\"", n)
  def signalSetProp(name: String, n: Member) =
    signal("ObjectLiteral may not have multiple setter properties with the same name. \"" + name + "\"", n)

  def checkDuplicatedProperty(members: List[Member]) = {
    var member1Str: String = ""
    var member2Str: String = ""
    for(member1 <- members) {
      member1Str = NU.member2Str(member1)
      for(member2 <- members if member1.ne(member2)) {
        member2Str = NU.member2Str(member2)
        if (member1Str.equals(member2Str)) (member1, member2) match {
          case (SField(_,_,_), SGetProp(_,_,_)) =>
            signalDataAccProp(member1Str, member2)
          case (SField(_,_,_), SSetProp(_,_,_)) =>
            signalDataAccProp(member1Str, member2)
          case (SGetProp(_,_,_), SField(_,_,_)) =>
            signalDataAccProp(member1Str, member2)
          case (SSetProp(_,_,_), SField(_,_,_)) =>
            signalDataAccProp(member1Str, member2)
          case (SGetProp(_,_,_), SGetProp(_,_,_)) =>
            signalGetProp(member1Str, member2)
          case (SSetProp(_,_,_), SSetProp(_,_,_)) =>
            signalSetProp(member1Str, member2)
          case _ =>
        }}}
  }

  /* The main entry function */
  def doit() = {
    val result = NU.simplifyWalker.walk(walk(program).asInstanceOf[Program])
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
  def isAssignOp(op: Op) = op match {
    case SOp(_, text) => text.equals("++") || text.equals("--")
  }
  def mkInIterator() = {
    inIterator = true
    if (labEnv._4.isEmpty) setLEnvCur(emptyLabel)
    labEnv = (labEnv._4++labEnv._1, labEnv._2, labEnv._3, labEnv._4)
  }
  def mkInSwitch()   = {
    inSwitch   = true
    if (labEnv._4.isEmpty) setLEnvCur(emptyLabel)
    labEnv = (labEnv._1, labEnv._4++labEnv._2, labEnv._3, labEnv._4)
  }
  def functional(span: Span, name: Id, params: List[Id], fds: List[FunDecl],
                 vds: List[VarDecl], body: List[SourceElement]) = {
    val old_toplevel = toplevel
    toplevel = false
    labEnv = emptyLabEnv
    addEnv(argName, newId(newId(span, argName)))
    val pairs_params = params.map(p => (p, newId(p)))
    pairs_params.foreach(p => addEnv(p._1, p._2))
    fds.foreach(fd => addEnv(fd.getFtn.getName, newId(fd.getFtn.getName)))
    val new_vds = vds.foldLeft(List[VarDecl]())((vds, vd) => vd match {
        case SVarDecl(info, id, _) => params.find(p => p.getText.equals(id.getText)) match {
          case None =>
            val new_id = newId(id)
            addEnv(id, new_id)
            vds:+SVarDecl(info, new_id, None)
          case _ => vds
        }})
    val new_fds = fds.map(walk).asInstanceOf[List[FunDecl]]
    val oldInFunctionBody = inFunctionBody
    inFunctionBody = true
    val new_body = body.map(walk).asInstanceOf[List[SourceElement]]
    inFunctionBody = oldInFunctionBody
    toplevel = old_toplevel
    SFunctional(new_fds, new_vds, new_body, name, pairs_params.map(p => p._2))
  }

  override def walk(node: Any): Any = node match {
    case SProgram(info, STopLevel(fds, vds, body), comments) =>
      fds.foreach(fd => addEnv(fd.getFtn.getName, newId(fd.getInfo.getSpan, fd.getFtn.getName.getText)))
      val new_vds = vds.map(p => p match {
          case SVarDecl(i, id, _) =>
            val new_id = newId(i.getSpan, id.getText)
            addEnv(id, new_id)
            SVarDecl(info, new_id, None)})
      val new_fds = fds.map(walk).asInstanceOf[List[FunDecl]]
      toplevel = true
      SProgram(info,
               STopLevel(new_fds,
                         new_vds,
                         body.map(walk).asInstanceOf[List[Stmt]]),
               comments)

    case SFunDecl(info, SFunctional(fds, vds, body, name, params)) =>
      val old_env = (env, labEnv)
      val new_name = newId(name, getEnvNoCheck(name))
      val result = SFunDecl(info,
                            functional(info.getSpan, new_name, params, fds, vds, body))
      setEnv(old_env._1, old_env._2)
      result

    case SFunExpr(info, SFunctional(fds, vds, body, name, params)) =>
      val old_env = (env, labEnv)
      val old_toplevel = toplevel
      toplevel = false
      val new_name = newId(name)
      addEnv(name, new_name)
      val result = SFunExpr(info,
                            functional(info.getSpan, new_name, params, fds, vds, body))
      setEnv(old_env)
      toplevel = old_toplevel
      result
    case SGetProp(info, prop, SFunctional(fds, vds, body, name, params)) =>
      val old_env = (env, labEnv)
      val new_prop = newPropId(name)
      val new_name = new_prop.getId
      val result = SGetProp(info, new_prop,
                            functional(info.getSpan, new_name, params, fds, vds, body))
      setEnv(old_env)
      result
    case SSetProp(info, prop, SFunctional(fds, vds, body, name, params)) =>
      val old_env = (env, labEnv)
      val new_prop = newPropId(name)
      val new_name = new_prop.getId
      val result = SSetProp(info, new_prop,
                            functional(info.getSpan, new_name, params, fds, vds, body))
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
    case br@SBreak(info, target) =>
      val new_target = target match {
        case Some(label) => labEnv._3.find(p => p._1.equals(label.getId.getText)) match {
          case None => labEnv._2.find(p => p._1.equals(label.getId.getText)) match {
            case None =>
              signal("Break occurs outside of a label.", br)
              return SEmptyStmt(info)
            case Some((_, uniq)) => Some(newLabel(label, uniq))
          }
          case Some((_, uniq)) => Some(newLabel(label, uniq))
        }
        case None =>
          if (!inIterator && !inSwitch) {
            signal("Break occurs outside of an iterator or a switch.", br)
            return SEmptyStmt(info)
          } else None
      }
      SBreak(info, new_target)

    case SCatch(info, id, body) =>
      val old_toplevel = toplevel
      toplevel = false
      val old_env = (env, labEnv)
      val new_id = newId(id)
      addEnv(id, new_id)
      setLEnv(node.asInstanceOf[AbstractNode])
      val result = SCatch(info, new_id, walk(body).asInstanceOf[List[Stmt]])
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
    case c@SContinue(info, target) =>
      if (!inIterator) {
        signal("Continue occurs outside of an iterator.", c); c
        return SEmptyStmt(info)
      } else {
        val new_target = target match {
          case Some(label) => labEnv._1.find(p => p._1.equals(label.getId.getText)) match {
            case None =>
              signal("Continue occurs outside of a label.", c)
              return SEmptyStmt(info)
            case Some((_, uniq)) => Some(newLabel(label, uniq))
          }
          case None => None
        }
        SContinue(info, new_target)
      }
    case _:DoWhile =>
      val old_env = (env, labEnv)
      val oldInIterator = inIterator
      mkInIterator
      val result = super.walk(node)
      inIterator = oldInIterator
      setEnv(old_env)
      result
    case _:For =>
      val old_env = (env, labEnv)
      val oldInIterator = inIterator
      mkInIterator
      val result = super.walk(node)
      inIterator = oldInIterator
      val newEnv = env
      setEnv(old_env)
      env = newEnv
      result
    case _:ForIn =>
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
    case fv:ForVar =>
      signal("ForVar should be replaced by the hoister.", fv)
    case fv:ForVarIn =>
      signal("ForVarIn should be replaced by the hoister.", fv)
    case ls@SLabelStmt(info, label@SLabel(_,SId(_,name,_,_)), stmt) =>
      labEnv._4.find(p => p._1.equals(name)) match {
        case Some(_) =>
          signal("Multiple declarations of the label: " + name + ".", ls)
          ls
        case None =>
          val old_env = (env, labEnv)
          val new_label = newLabel(label)
          addLEnv(label.getId, new_label.getId)
          val result = SLabelStmt(info, new_label, walk(stmt).asInstanceOf[Stmt])
          setEnv(old_env)
          result
      }

    /* 12.9 The return Statement
     * An ECMAScript program is considered syntactically incorrect if it contains
     * a return statement that is not within a FunctionBody.
     */
    case rt@SReturn(info, expr) =>
      if (!inFunctionBody)
        signal("Return occurs outside of a function body.", rt)
      val oldLEnv = setLEnv(node.asInstanceOf[AbstractNode])
      val result = super.walk(rt)
      resetLEnv(oldLEnv)
      result

    case _:Switch =>
      val old_env = (env, labEnv)
      val oldInSwitch = inSwitch
      mkInSwitch
      val result = super.walk(node)
      inSwitch = oldInSwitch
      setEnv(old_env)
      result
    case SVarRef(info, id) => SVarRef(info, newId(id, getEnvCheck(id)))
    case vs:VarStmt => signal("VarStmt should be replaced by the hoister.", vs)
    case _:While =>
      val old_env = (env, labEnv)
      val oldInIterator = inIterator
      mkInIterator
      val result = super.walk(node)
      inIterator = oldInIterator
      setEnv(old_env)
      result
    case SObjectExpr(info, members) =>
      checkDuplicatedProperty(members)
      val oldLEnv = setLEnv(node.asInstanceOf[AbstractNode])
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result
    case _:With =>
      val old_env = (env, labEnv)
      val oldInWith = inWith
      inWith = true
      setLEnv(node.asInstanceOf[AbstractNode])
      val result = super.walk(node)
      inWith = oldInWith
      setEnv(old_env)
      result
    case SAssignOpApp(info, SParenthesized(_, lhs), op, right) if lhs.isInstanceOf[LHS] =>
      val oldLEnv = setLEnv(node.asInstanceOf[AbstractNode])
      val result = walk(SAssignOpApp(info, lhs.asInstanceOf[LHS], op, right))
      resetLEnv(oldLEnv)
      result
    case SAssignOpApp(info, SVarRef(_, id), op, right) =>
      hasAssign = true
      val oldLEnv = setLEnv(node.asInstanceOf[AbstractNode])
      val old_toplevel = toplevel
      val new_id = if (!inEnv(id)) {
                     toplevel = true
                     newId(id)
                   } else newId(id, getEnvNoCheck(id))
      toplevel = old_toplevel
      val result = SAssignOpApp(info, SVarRef(info, new_id),
                                op, walk(right).asInstanceOf[Expr])
      resetLEnv(oldLEnv)
      if (!inEnv(id)) {
        toplevel = true
        addEnv(id, newId(id))
        toplevel = old_toplevel
      }
      result
    case _:AssignOpApp =>
      hasAssign = true
      val oldLEnv = setLEnv(node.asInstanceOf[AbstractNode])
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result
    case _:UnaryAssignOpApp =>
      hasAssign = true
      val oldLEnv = setLEnv(node.asInstanceOf[AbstractNode])
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result
    case n:PrefixOpApp =>
      if (isAssignOp(n.getOp)) hasAssign = true
      val oldLEnv = setLEnv(node.asInstanceOf[AbstractNode])
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result
    case SRegularExpression(info, body, flags) =>
      val regexp = "RegExp"
      SNew(info, SFunApp(info, SVarRef(info, SId(info, regexp, Some(regexp), false)),
                         List(SStringLiteral(info, "\"", body),
                              SStringLiteral(info, "\"", flags))))
    case _: AbstractNode =>
      val oldLEnv = setLEnv(node.asInstanceOf[AbstractNode])
      val result = super.walk(node)
      resetLEnv(oldLEnv)
      result
    case _ => super.walk(node)
  }
}
