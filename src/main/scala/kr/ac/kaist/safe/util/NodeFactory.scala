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

package kr.ac.kaist.safe.util

import kr.ac.kaist.safe.nodes._
import kr.ac.kaist.safe.util.{ NodeUtil => NU }

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
  private var comment: Option[Comment] = None
  def initComment: Unit = { comment = None }
  def commentLog(span: Span, message: String): Unit =
    if (NU.getKeepComments) {
      if (!comment.isDefined ||
        (!comment.get.txt.startsWith("/*") && !comment.get.txt.startsWith("//")))
        comment = Some[Comment](makeComment(span, message))
      else {
        val com = comment.get
        if (!com.txt.equals(message))
          comment = Some[Comment](makeComment(
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

  def makeEmptyStmt(span: Span): EmptyStmt =
    new EmptyStmt(makeASTNodeInfo(span))

  def makeExprStmt(span: Span, expr: Expr): ExprStmt =
    new ExprStmt(makeASTNodeInfo(span), expr)

  def makeDoWhile(span: Span, body: Stmt, cond: Expr): DoWhile =
    new DoWhile(makeASTNodeInfo(span), body, cond)

  def makeWhile(span: Span, cond: Expr, body: Stmt): While =
    new While(makeASTNodeInfo(span), cond, body)

  def makeForIn(span: Span, lhs: LHS, expr: Expr, body: Stmt): ForIn =
    new ForIn(makeASTNodeInfo(span), lhs, expr, body)

  def makeForVarIn(span: Span, vd: VarDecl, expr: Expr, body: Stmt): ForVarIn =
    new ForVarIn(makeASTNodeInfo(span), vd, expr, body)

  def makeWith(span: Span, expr: Expr, stmt: Stmt): With =
    new With(makeASTNodeInfo(span), expr, stmt)

  def makeThrow(span: Span, expr: Expr): Throw =
    new Throw(makeASTNodeInfo(span), expr)

  def makeDebugger(span: Span): Debugger =
    new Debugger(makeASTNodeInfo(span))

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

  def makeThis(span: Span): This =
    new This(makeASTNodeInfo(span))

  def makeNull(span: Span): Null =
    new Null(makeASTNodeInfo(span))

  def makeBool(span: Span, bool: Boolean): Bool =
    new Bool(makeASTNodeInfo(span), bool)

  def makeVarRef(span: Span, id: Id): VarRef =
    new VarRef(makeASTNodeInfo(span), id)

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

  def makePropId(span: Span, id: Id): PropId =
    new PropId(makeASTNodeInfo(span), id)

  def makePropStr(span: Span, str: String): PropStr =
    new PropStr(makeASTNodeInfo(span), str)

  def makePropNum(span: Span, num: NumberLiteral): PropNum =
    new PropNum(makeASTNodeInfo(span), num)

  def makeId(span: Span, name: String, uniq: String): Id =
    makeId(span, name, Some(uniq))

  def makeId(span: Span, name: String): Id =
    makeId(span, name, None)

  def makeId(span: Span, name: String, uniq: Option[String]): Id =
    new Id(makeASTNodeInfo(span), name, uniq, false)

  def makeOp(span: Span, name: String): Op =
    new Op(makeASTNodeInfo(span), name)

  def makeComment(span: Span, comment: String): Comment =
    new Comment(makeASTNodeInfo(span), comment)

  def makeNoOp(span: Span, desc: String): NoOp =
    NU.makeNoOp(makeASTNodeInfo(span), desc)
}
