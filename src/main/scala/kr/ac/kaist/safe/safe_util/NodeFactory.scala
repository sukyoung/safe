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

package kr.ac.kaist.safe.safe_util

import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.safe_util.{ NodeUtil => NU }
import kr.ac.kaist.safe.scala_useful.Lists._
import kr.ac.kaist.safe.scala_useful.Options._
import kr.ac.kaist.safe.useful.Useful

import edu.rice.cs.plt.tuple.{ Option => JOption }

import java.lang.{ Double => JDouble }
import java.lang.{ Integer => JInt }
import java.util.{ List => JList }
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.math.BigInteger
import java.math.BigDecimal
import java.util.ArrayList
import java.util.Arrays
import java.util.Collections
import java.util.Set
import java.util.StringTokenizer
import scala.collection.mutable.{ HashMap => MHashMap }

object NodeFactory {
  val generatedString = "<>generated String Literal"
  private var comment = none[Comment]
  def initComment: Unit = { comment = none[Comment] }
  def commentLog(span: Span, message: String): Unit =
    if (NU.getKeepComments) {
      if (!comment.isDefined ||
        (!comment.get.txt.startsWith("/*") && !comment.get.txt.startsWith("//")))
        comment = some[Comment](makeComment(span, message))
      else {
        val com = comment.get
        if (!com.txt.equals(message))
          comment = some[Comment](makeComment(
            NU.spanAll(com.info.span, span),
            com.txt + "\n" + message
          ))
      }
    }

  def makeASTNodeInfo(span: Span): ASTNodeInfo =
    if (NU.getKeepComments && comment.isDefined) {
      val result = new ASTNodeInfo(span, comment)
      comment = None
      result
    } else new ASTNodeInfo(span, None)

  def makeASTNodeInfo(span: Span, comment: String): ASTNodeInfo =
    new ASTNodeInfo(span, Some(makeComment(span, comment)))

  def makeTopLevel(info: ASTNodeInfo, body: List[SourceElement], strict: Boolean): TopLevel =
    makeTopLevel(info, Nil, Nil, List(new SourceElements(info, body, strict)))

  def makeTopLevel(info: ASTNodeInfo, body: List[SourceElements]): TopLevel =
    makeTopLevel(info, Nil, Nil, body)

  def makeTopLevel(info: ASTNodeInfo, fds: List[FunDecl], vds: List[VarDecl],
    body: List[SourceElements]): TopLevel =
    new TopLevel(info, fds, vds, body)

  def makeProgram(span: Span, elements: JList[SourceElement], strict: Boolean): Program = {
    val info = makeASTNodeInfo(span)
    makeProgram(info, makeTopLevel(info, toList(elements), strict))
  }

  def makeProgram(info: ASTNodeInfo, body: JList[SourceElement], strict: Boolean): Program =
    makeProgram(info, makeTopLevel(info, toList(body), strict))

  def makeProgram(info: ASTNodeInfo, toplevel: TopLevel): Program =
    new Program(info, toplevel)

  def makeFunctional(info: ASTNodeInfo, name: Id, fds: List[FunDecl], vds: List[VarDecl],
    body: List[SourceElement], params: List[Id], bodyS: String, strict: Boolean): Functional =
    new Functional(info, fds, vds, new SourceElements(info, body, strict), name, params, bodyS)

  def makeFunDecl(span: Span, name: Id, params: JList[Id],
    body: JList[SourceElement], bodyS: String, strict: Boolean): FunDecl = {
    val info = makeASTNodeInfo(span)
    new FunDecl(
      info,
      makeFunctional(info, name, Nil, Nil, toList(body), toList(params), bodyS, strict)
    )
  }

  def makeFunExpr(span: Span, name: Id, params: JList[Id],
    body: JList[SourceElement], bodyS: String, strict: Boolean): FunExpr = {
    val info = makeASTNodeInfo(span)
    new FunExpr(
      info,
      makeFunctional(info, name, Nil, Nil, toList(body), toList(params), bodyS, strict)
    )
  }

  def makeABlock(span: Span, stmts: JList[Stmt]): ABlock =
    new ABlock(makeASTNodeInfo(span), toList(stmts))

  def makeVarStmt(span: Span, vds: JList[VarDecl]): VarStmt =
    new VarStmt(makeASTNodeInfo(span), toList(vds))

  def makeEmptyStmt(span: Span): EmptyStmt =
    new EmptyStmt(makeASTNodeInfo(span))

  def makeExprStmt(span: Span, expr: Expr): ExprStmt =
    new ExprStmt(makeASTNodeInfo(span), expr)

  def makeIf(span: Span, cond: Expr, trueB: Stmt, falseB: JOption[Stmt]): If =
    new If(makeASTNodeInfo(span), cond, trueB, toOption(falseB))

  def makeDoWhile(span: Span, body: Stmt, cond: Expr): DoWhile =
    new DoWhile(makeASTNodeInfo(span), body, cond)

  def makeWhile(span: Span, cond: Expr, body: Stmt): While =
    new While(makeASTNodeInfo(span), cond, body)

  def makeFor(span: Span, init: JOption[Expr], cond: JOption[Expr],
    action: JOption[Expr], body: Stmt): For =
    new For(makeASTNodeInfo(span), toOption(init), toOption(cond), toOption(action), body)

  def makeForVar(span: Span, vars: JList[VarDecl], cond: JOption[Expr],
    action: JOption[Expr], body: Stmt): ForVar =
    new ForVar(makeASTNodeInfo(span), toList(vars), toOption(cond), toOption(action), body)

  def makeForIn(span: Span, lhs: LHS, expr: Expr, body: Stmt): ForIn =
    new ForIn(makeASTNodeInfo(span), lhs, expr, body)

  def makeForVarIn(span: Span, vd: VarDecl, expr: Expr, body: Stmt): ForVarIn =
    new ForVarIn(makeASTNodeInfo(span), vd, expr, body)

  def makeContinue(span: Span, target: JOption[Label]): Continue =
    new Continue(makeASTNodeInfo(span), toOption(target))

  def makeBreak(span: Span, target: JOption[Label]): Break =
    new Break(makeASTNodeInfo(span), toOption(target))

  def makeReturn(span: Span, expr: JOption[Expr]): Return =
    new Return(makeASTNodeInfo(span), toOption(expr))

  def makeWith(span: Span, expr: Expr, stmt: Stmt): With =
    new With(makeASTNodeInfo(span), expr, stmt)

  def makeSwitch(span: Span, expr: Expr, front: JList[Case]): Switch =
    new Switch(makeASTNodeInfo(span), expr, toList(front), None, Nil)

  def makeSwitch(span: Span, expr: Expr, front: JList[Case],
    defaultC: JList[Stmt], back: JList[Case]): Switch =
    new Switch(makeASTNodeInfo(span), expr, toList(front), Some(toList(defaultC)), toList(back))

  def makeLabelStmt(span: Span, label: Label, stmt: Stmt): LabelStmt =
    new LabelStmt(makeASTNodeInfo(span), label, stmt)

  def makeThrow(span: Span, expr: Expr): Throw =
    new Throw(makeASTNodeInfo(span), expr)

  def makeTry(span: Span, body: JList[Stmt], catchB: Catch): Try =
    new Try(makeASTNodeInfo(span), toList(body), Some(catchB), None)

  def makeTry(span: Span, body: JList[Stmt], fin: JList[Stmt]): Try =
    new Try(makeASTNodeInfo(span), toList(body), None, Some(toList(fin)))

  def makeTry(span: Span, body: JList[Stmt], catchB: Catch, fin: JList[Stmt]): Try =
    new Try(makeASTNodeInfo(span), toList(body), Some(catchB), Some(toList(fin)))

  def makeDebugger(span: Span): Debugger =
    new Debugger(makeASTNodeInfo(span))

  def makeVarDecl(span: Span, name: Id, expr: JOption[Expr]): VarDecl =
    new VarDecl(makeASTNodeInfo(span), name, toOption(expr))

  def makeCase(span: Span, cond: Expr, body: JList[Stmt]): Case =
    new Case(makeASTNodeInfo(span), cond, toList(body))

  def makeCatch(span: Span, id: Id, body: JList[Stmt]): Catch =
    new Catch(makeASTNodeInfo(span), id, toList(body))

  def makeExprList(span: Span, es: JList[Expr]): ExprList =
    new ExprList(makeASTNodeInfo(span), toList(es))

  def makeCond(span: Span, cond: Expr, trueB: Expr, falseB: Expr): Cond =
    new Cond(makeASTNodeInfo(span), cond, trueB, falseB)

  def makeInfixOpApp(span: Span, left: Expr, op: Op, right: Expr): InfixOpApp =
    new InfixOpApp(makeASTNodeInfo(span), left, op, right)

  def makePrefixOpApp(span: Span, op: Op, right: Expr): PrefixOpApp =
    new PrefixOpApp(makeASTNodeInfo(span), op, right)

  def makeUnaryAssignOpApp(span: Span, lhs: LHS, op: Op): UnaryAssignOpApp =
    new UnaryAssignOpApp(makeASTNodeInfo(span), lhs, op)

  def makeAssignOpApp(span: Span, lhs: LHS, op: Op, right: Expr): AssignOpApp =
    new AssignOpApp(makeASTNodeInfo(span), lhs, op, right)

  def makeBracket(span: Span, lhs: LHS, index: Expr): Bracket =
    new Bracket(makeASTNodeInfo(span), lhs, index)

  def makeDot(span: Span, lhs: LHS, member: Id): Dot =
    new Dot(makeASTNodeInfo(span), lhs, member)

  def makeNew(span: Span, lhs: LHS): New =
    new New(makeASTNodeInfo(span), lhs)

  def makeFunApp(span: Span, lhs: LHS, args: JList[Expr]): FunApp =
    new FunApp(makeASTNodeInfo(span), lhs, toList(args))

  def makeThis(span: Span): This =
    new This(makeASTNodeInfo(span))

  def makeNull(span: Span): Null =
    new Null(makeASTNodeInfo(span))

  def makeBool(span: Span, bool: Boolean): Bool =
    new Bool(makeASTNodeInfo(span), bool)

  def makeVarRef(span: Span, id: Id): VarRef =
    new VarRef(makeASTNodeInfo(span), id)

  def makeArrayNumberExpr(span: Span, elmts: JList[JDouble]): Expr = {
    if (elmts.size > 1000)
      new ArrayNumberExpr(makeASTNodeInfo(span), toList(elmts))
    else
      new ArrayExpr(makeASTNodeInfo(span), toList(elmts).map(e => Some(makeNumberLiteral(span, e.toString, e).asInstanceOf[Expr])))
  }

  def makeArrayExpr(span: Span, elmts: JList[JOption[Expr]]): ArrayExpr =
    new ArrayExpr(makeASTNodeInfo(span), toList(elmts).map(toOption))

  def makeObjectExpr(span: Span, elmts: JList[Member]): ObjectExpr =
    new ObjectExpr(makeASTNodeInfo(span), toList(elmts))

  def makeParenthesized(span: Span, expr: Expr): Parenthesized =
    new Parenthesized(makeASTNodeInfo(span), expr)

  /*
     * DecimalLiteral ::=
     *   DecimalIntegerLiteral . DecimalDigits? ExponentPart?
     * | DecimalIntegerLiteral ExponentPart?
     * | . DecimalDigits ExponentPart?
     *
     * DecimalIntegerLiteral ::=
     *   0
     * | NonZeroDigit DecimalDigits?
     *
     * DecimalDigit ::= 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
     *
     * NonZeroDigit ::= 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
     *
     * ExponentPart ::= (e | E) (+ | -)? DecimalDigit+
     */
  def makeNumberLiteral(writer: BufferedWriter, span: Span,
    beforeDot: String, dot: String,
    afterDot: String, exponent: String): NumberLiteral = {
    if ((beforeDot + dot).equals("") ||
      ((beforeDot + afterDot).equals("") && !dot.equals("")) ||
      (!beforeDot.equals("") && dot.equals("") && !afterDot.equals("")))
      NU.log(writer, "Syntax Error: expected a numeral but got " +
        beforeDot + dot + afterDot + exponent)
    if (!beforeDot.equals("") && !beforeDot.equals("0") && beforeDot.charAt(0) == '0')
      NU.log(writer, "Syntax Error: a numeral begins with 0.")
    if (dot.equals("")) {
      if (exponent.equals("")) makeIntLiteral(span, new BigInteger(beforeDot))
      else {
        var exp = 0
        val second = exponent.charAt(1)
        if (Character.isDigit(second))
          exp = JInt.parseInt(exponent.substring(1))
        else if (second.equals('-'))
          exp = -1 * JInt.parseInt(exponent.substring(2))
        else exp = JInt.parseInt(exponent.substring(2))
        if (exp < 0) {
          var str = beforeDot + dot + afterDot + exponent
          str = new BigDecimal(str).toString
          makeDoubleLiteral(span, str, JDouble.valueOf(str))
        } else makeIntLiteral(span, new BigInteger(beforeDot).multiply(BigInteger.TEN.pow(exp)))
      }
    } else {
      val str = beforeDot + dot + afterDot + exponent
      makeDoubleLiteral(span, str, JDouble.valueOf(str))
    }
  }

  def makeNumberLiteral(writer: BufferedWriter, span: Span, beforeDot: String): JDouble = {
    if (beforeDot.equals(""))
      NU.log(writer, "Syntax Error: expected a numeral but got " + beforeDot)
    if (!beforeDot.equals("") && !beforeDot.equals("0") && beforeDot.charAt(0) == '0')
      NU.log(writer, "Syntax Error: a numeral begins with 0.")
    JDouble.valueOf(beforeDot)
  }

  def makeNumberLiteral(span: Span, str: String, doubleVal: Double): NumberLiteral =
    if (str.endsWith(".0"))
      new IntLiteral(makeASTNodeInfo(span), new BigInteger(str.substring(0, str.length - 2), 10), 10)
    else new DoubleLiteral(makeASTNodeInfo(span), str, doubleVal)

  def makeIntLiteral(span: Span, intVal: BigInteger, radix: Int = 10): IntLiteral =
    new IntLiteral(makeASTNodeInfo(span), intVal, radix)

  def makeDoubleLiteral(span: Span, str: String, doubleVal: Double): DoubleLiteral =
    new DoubleLiteral(makeASTNodeInfo(span), str, doubleVal)

  def makeHexIntegerLiteral(span: Span, num: String): IntLiteral =
    makeIntLiteral(span, new BigInteger(num, 16), 16)

  def makeOctalIntegerLiteral(span: Span, num: String): IntLiteral =
    makeIntLiteral(span, new BigInteger(num, 8), 8)

  def makeStringLiteral(span: Span, str: String, quote: String): StringLiteral =
    new StringLiteral(makeASTNodeInfo(span), quote, str)

  def makeRegularExpression(span: Span, body: String, flags: String): RegularExpression =
    new RegularExpression(makeASTNodeInfo(span), body, flags)

  def makeField(span: Span, prop: Property, expr: Expr): Field =
    new Field(makeASTNodeInfo(span), prop, expr)

  def makeGetProp(span: Span, prop: Property, body: JList[SourceElement], bodyS: String, strict: Boolean): GetProp = {
    val info = makeASTNodeInfo(span)
    new GetProp(info, prop,
      makeFunctional(info, NU.prop2Id(prop), Nil, Nil, toList(body), Nil, bodyS, strict))
  }

  def makeSetProp(span: Span, prop: Property, id: Id,
    body: JList[SourceElement], bodyS: String, strict: Boolean): SetProp = {
    val info = makeASTNodeInfo(span)
    new SetProp(info, prop,
      makeFunctional(info, NU.prop2Id(prop), Nil, Nil, toList(body), List(id), bodyS, strict))
  }

  def makePropId(span: Span, id: Id): PropId =
    new PropId(makeASTNodeInfo(span), id)

  def makePropStr(span: Span, str: String): PropStr =
    new PropStr(makeASTNodeInfo(span), str)

  def makePropNum(span: Span, num: NumberLiteral): PropNum =
    new PropNum(makeASTNodeInfo(span), num)

  def makeId(span: Span, name: String, uniq: String): Id =
    makeId(span, name, some(uniq))

  def makeId(span: Span, name: String): Id =
    makeId(span, name, None)

  def makeId(span: Span, name: String, uniq: Option[String]): Id =
    new Id(makeASTNodeInfo(span), name, uniq, false)

  def makeOp(span: Span, name: String): Op =
    new Op(makeASTNodeInfo(span), name)

  def makeLabel(span: Span, id: Id): Label =
    new Label(makeASTNodeInfo(span), id)

  def makeComment(span: Span, comment: String): Comment =
    new Comment(makeASTNodeInfo(span), comment)

  def makeNoOp(span: Span, desc: String): NoOp =
    makeNoOp(makeASTNodeInfo(span), desc)

  def makeNoOp(info: ASTNodeInfo, desc: String): NoOp =
    new NoOp(info, desc)
}
