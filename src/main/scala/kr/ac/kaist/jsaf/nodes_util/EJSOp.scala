/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.nodes_util

// Enumeration of JavaScript Operator
object EJSOp {
  def isBinary(op: Int): Boolean = (op / 1000) % 10 == 1 // 1xxx
  def isUnary(op: Int): Boolean = (op / 1000) % 10 == 2 // 2xxx
  def isArithmetic(op: Int): Boolean = (op / 100) % 10 == 1 // x1xx
  def isBitwise(op: Int): Boolean = (op / 100) % 10 == 2 // x2xx
  def isComparison(op: Int): Boolean = (op / 100) % 10 == 3 // x3xx
  def isLogical(op: Int): Boolean = (op / 100) % 10 == 4 // x4xx
  def isETC(op: Int): Boolean = (op / 100) % 10 == 9 // x9xx
  // TODO: xx?x
  def isEquality(op: Int): Boolean = (op / 10) % 100 == 32 // x32x

  def strToEJSOp(op: String): Int = {
    if(op.equals("*")) BIN_ARITH_MUL_MULTIPLICATION
    else if(op.equals("/")) BIN_ARITH_MUL_DIVISION
    else if(op.equals("%")) BIN_ARITH_MUL_REMINDER
    else if(op.equals("<<")) BIN_BIT_SHIFT_LEFT
    else if(op.equals(">>")) BIN_BIT_SHIFT_SRIGHT
    else if(op.equals(">>>")) BIN_BIT_SHIFT_USRIGHT
    else if(op.equals("&")) BIN_BIT_BIT_AND
    else if(op.equals("^")) BIN_BIT_BIT_XOR
    else if(op.equals("|")) BIN_BIT_BIT_OR
    else if(op.equals("<")) BIN_COMP_REL_LESS
    else if(op.equals(">")) BIN_COMP_REL_GREATER
    else if(op.equals("<=")) BIN_COMP_REL_LESSEQUAL
    else if(op.equals(">=")) BIN_COMP_REL_GREATEREQUAL
    else if(op.equals("instanceof")) BIN_COMP_REL_INSTANCEOF
    else if(op.equals("in")) BIN_COMP_REL_IN
    else if(op.equals("notInstanceof")) BIN_COMP_REL_NOTINSTANCEOF
    else if(op.equals("notIn")) BIN_COMP_REL_NOTIN
    else if(op.equals("==")) BIN_COMP_EQ_EQUAL
    else if(op.equals("!=")) BIN_COMP_EQ_NEQUAL
    else if(op.equals("===")) BIN_COMP_EQ_SEQUAL
    else if(op.equals("!==")) BIN_COMP_EQ_SNEQUAL
    else if(op.equals("&&")) BIN_LOG_AND
    else if(op.equals("||")) BIN_LOG_OR
    else if(op.equals("++")) UN_ARITH_INCREMENT
    else if(op.equals("--")) UN_ARITH_DECREMENT
    else if(op.equals("~")) UN_BIT_BIT_NOT
    else if(op.equals("!")) UN_BIT_LOG_NOT
    else if(op.equals("delete")) UN_ETC_DELETE
    else if(op.equals("void")) UN_ETC_VOID
    else if(op.equals("typeof")) UN_ETC_TYPEOF
    else if(op.equals("+")) ETC_PLUS
    else if(op.equals("-")) ETC_MINUS
    else ETC_UNKNOWN
  }

  val BIN = 1000 // Binary Operators
    val BIN_ARITH = 1100
      val BIN_ARITH_MUL = 1110 // 11.5 Multiplicative Operators
        val BIN_ARITH_MUL_MULTIPLICATION = 1111 // 11.5.1 * Operator
        val BIN_ARITH_MUL_DIVISION = 1112 // 11.5.2 / Operator
        val BIN_ARITH_MUL_REMINDER = 1113 // 11.5.3 % Operator
      val BIN_ARITH_ADD = 1120 // 11.6 Additive Operators
        val BIN_ARITH_ADD_ADDITION = 1121 // 11.6.1 + Operator
        val BIN_ARITH_ADD_SUBTRACTION = 1122 // 11.6.2 - Operator
    val BIN_BIT = 1200
      val BIN_BIT_SHIFT = 1210 // 11.7 Bitwise Shift Operators
        val BIN_BIT_SHIFT_LEFT = 1211 // 11.7.1 << Operator
        val BIN_BIT_SHIFT_SRIGHT = 1212 // 11.7.2 >> Operator
        val BIN_BIT_SHIFT_USRIGHT = 1213 // 11.7.3 >>> Operator
      val BIN_BIT_BIT = 1220 // 11.10 Binary Bitwise Operators
        val BIN_BIT_BIT_AND = 1221 // & Operator
        val BIN_BIT_BIT_XOR = 1222 // ^ Operator
        val BIN_BIT_BIT_OR = 1223 // | Operator
    val BIN_COMP = 1300
      val BIN_COMP_REL = 1310 // 11.8 Relational Operators
        val BIN_COMP_REL_LESS = 1311 // 11.8.1 < Operator
        val BIN_COMP_REL_GREATER = 1312 // 11.8.2 > Operator
        val BIN_COMP_REL_LESSEQUAL = 1313 // 11.8.3 <= Operator
        val BIN_COMP_REL_GREATEREQUAL = 1314 // 11.8.4 >= Operator
        val BIN_COMP_REL_INSTANCEOF = 1315 // 11.8.6 instanceof Operator
        val BIN_COMP_REL_IN = 1316 // 11.8.7 in Operator
        val BIN_COMP_REL_NOTINSTANCEOF = 1317 // not instanceof operator for assert
        val BIN_COMP_REL_NOTIN = 1318 // not in operator for assert
      val BIN_COMP_EQ = 1320 // 11.9 Equality Operators
        val BIN_COMP_EQ_EQUAL = 1321 // 11.9.1 == Operator
        val BIN_COMP_EQ_NEQUAL = 1322 // 11.9.2 != Operator
        val BIN_COMP_EQ_SEQUAL = 1323 // 11.9.4 === Operator
        val BIN_COMP_EQ_SNEQUAL = 1324 // 11.9.5 !== Operator
    val BIN_LOG = 1400 // 11.11 Binary Logical Operators
      val BIN_LOG_AND = 1401 // && Operator
      val BIN_LOG_OR = 1402 // || Operator

  val UN = 2000 // Unary Operators
    val UN_ARITH = 2100
      val UN_ARITH_INCREMENT = 2101 // 11.4.4 pre ++ Operator
      val UN_ARITH_DECREMENT = 2102 // 11.4.5 pre -- Operator
      val UN_ARITH_ADDITION = 2103 // 11.4.6 + Operator
      val UN_ARITH_SUBTRACTION = 2104 // 11.4.7 - Operator
    val UN_BIT = 2200
      val UN_BIT_BIT = 2210
        val UN_BIT_BIT_NOT = 2211 // 11.4.8 ~ Operator
      val UN_BIT_LOG = 2220
        val UN_BIT_LOG_NOT = 2221 // 11.4.9 ! Operator
    val UN_ETC = 2900
      val UN_ETC_DELETE = 2901 // 11.4.1 delete Operator
      val UN_ETC_VOID = 2902 // 11.4.2 void Operator
      val UN_ETC_TYPEOF = 2903 // 11.4.3 typeof Operator

  val ETC = 9000
    val ETC_PLUS = 9001 // + Operator (Not yet decided exactly. BIN_ARITH_ADD_ADDITION or UN_ARITH_ADDITION)
    val ETC_MINUS = 9002 // - Operator (Not yet decided exactly. BIN_ARITH_ADD_SUBTRACTION or UN_ARITH_SUBTRACTION)
    val ETC_UNKNOWN = 0 // ...
}
