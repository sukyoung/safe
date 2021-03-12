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

package kr.ac.kaist.safe.util

// Utilities for JavaScript operators.
// Used by NodeUtil to handle operators in IR nodes.
sealed abstract class EJSOp(
    val name: String,
    val arity: EJSArity,
    val typ: EJSOpType
) {
  override def toString: String = name
  def trans: EJSOp = this match {
    case EJSLt => EJSGte // < --> >=
    case EJSGt => EJSLte // > --> <=
    case EJSLte => EJSGt // <= --> >
    case EJSGte => EJSLt // >= --> <
    case EJSEq => EJSNEq // == --> !=
    case EJSNEq => EJSEq // != --> ==
    case EJSSEq => EJSSNEq // === --> !==
    case EJSSNEq => EJSSEq // !== --> ===
    case EJSIn => EJSNotIn // in --> notIn
    case EJSInstOf => EJSNotInstOf // instanceof --> notInstanceof
    case EJSNotIn => EJSIn // notIn --> in
    case EJSNotInstOf => EJSInstOf // notInstanceof --> instanceof
    case _ => this
  }
}
object EJSOp {
  def apply(name: String): EJSOp = name match {
    case "*" => EJSMul
    case "/" => EJSDiv
    case "%" => EJSRem
    case "<<" => EJSShiftLeft
    case ">>" => EJSShiftSRight
    case ">>>" => EJSShiftUSRight
    case "&" => EJSBitAnd
    case "^" => EJSBitXor
    case "|" => EJSBitOr
    case "<" => EJSLt
    case ">" => EJSGt
    case "<=" => EJSLte
    case ">=" => EJSGte
    case "instanceof" => EJSInstOf
    case "in" => EJSIn
    case "notInstanceof" => EJSNotInstOf
    case "notIn" => EJSNotIn
    case "==" => EJSEq
    case "!=" => EJSNEq
    case "===" => EJSSEq
    case "!==" => EJSSNEq
    case "&&" => EJSLogAnd
    case "||" => EJSLogOr
    case "++" => EJSInc
    case "--" => EJSDec
    case "~" => EJSBitNot
    case "!" => EJSLogNot
    case "delete" => EJSDelete
    case "void" => EJSVoid
    case "typeof" => EJSTypeOf
    case "+" => EJSPos
    case "-" => EJSNeg
    case _ => EJSEtc
  }
}

// Unary Operators
// 11.4 Unary Bitwise Operators
case object EJSDelete extends EJSOp("delete", EJSUn, EJSEtcType) // 11.4.1 delete Operator
case object EJSVoid extends EJSOp("void", EJSUn, EJSEtcType) // 11.4.2 void Operator
case object EJSTypeOf extends EJSOp("typeof", EJSUn, EJSEtcType) // 11.4.3 typeof Operator
case object EJSInc extends EJSOp("++", EJSUn, EJSAddType) // 11.4.4 pre ++ Operator
case object EJSDec extends EJSOp("--", EJSUn, EJSAddType) // 11.4.5 pre -- Operator
case object EJSPos extends EJSOp("+", EJSUn, EJSAddType) // 11.4.6 + Operator
case object EJSNeg extends EJSOp("-", EJSUn, EJSAddType) // 11.4.7 - Operator
case object EJSBitNot extends EJSOp("~", EJSUn, EJSBitType) // 11.4.8 ~ Operator
case object EJSLogNot extends EJSOp("!", EJSUn, EJSLogicType) // 11.4.9 ! Operator

// Binary Operators
// 11.5 Multiplicative Operators
case object EJSMul extends EJSOp("*", EJSBin, EJSMulType) // 11.5.1 * Operator
case object EJSDiv extends EJSOp("/", EJSBin, EJSMulType) // 11.5.2 / Operator
case object EJSRem extends EJSOp("%", EJSBin, EJSMulType) // 11.5.3 % Operator
// 11.6 Additive Operators
case object EJSAdd extends EJSOp("+", EJSBin, EJSAddType) // 11.6.1 + Operator
case object EJSSub extends EJSOp("-", EJSBin, EJSAddType) // 11.6.2 - Operator
// 11.7 Bitwise Shift Operators
case object EJSShiftLeft extends EJSOp("<<", EJSBin, EJSShiftType) // 11.7.1 << Operator
case object EJSShiftSRight extends EJSOp(">>", EJSBin, EJSShiftType) // 11.7.2 >> Operator
case object EJSShiftUSRight extends EJSOp(">>>", EJSBin, EJSShiftType) // 11.7.3 >> Operator
// 11.10 Binary Bitwise Operators
case object EJSBitAnd extends EJSOp("&", EJSBin, EJSBitType) // & Operator
case object EJSBitXor extends EJSOp("^", EJSBin, EJSBitType) // ^ Operator
case object EJSBitOr extends EJSOp("|", EJSBin, EJSBitType) // | Operator
// 11.8 Relational Operators
case object EJSLt extends EJSOp("<", EJSBin, EJSNonEqType) // 11.8.1 < Operator
case object EJSGt extends EJSOp(">", EJSBin, EJSNonEqType) // 11.8.2 > Operator
case object EJSLte extends EJSOp("<=", EJSBin, EJSNonEqType) // 11.8.3 <= Operator
case object EJSGte extends EJSOp(">=", EJSBin, EJSNonEqType) // 11.8.4 >= Operator
case object EJSInstOf extends EJSOp("instanceof", EJSBin, EJSNonEqType) // 11.8.6 instanceof Operator
case object EJSIn extends EJSOp("in", EJSBin, EJSNonEqType) // 11.8.7 in Operator
case object EJSNotInstOf extends EJSOp("notInstanceof", EJSBin, EJSNonEqType) // not instanceof operator for assert
case object EJSNotIn extends EJSOp("notIn", EJSBin, EJSNonEqType) // not in operator for assert
// 11.9 Equality Operators
case object EJSEq extends EJSOp("==", EJSBin, EJSEqType) // 11.9.1 == Operator
case object EJSNEq extends EJSOp("!=", EJSBin, EJSEqType) // 11.9.2 != Operator
case object EJSSEq extends EJSOp("===", EJSBin, EJSEqType) // 11.9.4 === Operator
case object EJSSNEq extends EJSOp("!==", EJSBin, EJSEqType) // 11.9.5 !== Operator
// 11.11 Binary Logical Operators
case object EJSLogAnd extends EJSOp("&&", EJSBin, EJSLogicType) // && Operator
case object EJSLogOr extends EJSOp("||", EJSBin, EJSLogicType) // || Operator

// not yet defined arity
case object EJSEtcAdd extends EJSOp("+", EJSNotYet, EJSAddType) // + Operator (Not yet decided exactly. BIN_ARITH_ADD_ADDITION or UN_ARITH_ADDITION)
case object EJSEtcSub extends EJSOp("-", EJSNotYet, EJSAddType) // - Operator (Not yet decided exactly. BIN_ARITH_ADD_SUBTRACTION or UN_ARITH_SUBTRACTION)
case object EJSEtc extends EJSOp("", EJSNotYet, EJSEtcType) // etc

////////////////////////////////////////////////////////////////
// Operator Arity
////////////////////////////////////////////////////////////////

sealed abstract class EJSArity
case object EJSBin extends EJSArity
case object EJSUn extends EJSArity
case object EJSNotYet extends EJSArity

////////////////////////////////////////////////////////////////
// Operator Type
////////////////////////////////////////////////////////////////

trait EJSOpType

// arithmetic type
sealed abstract class EJSArithType extends EJSOpType
case object EJSMulType extends EJSArithType
case object EJSAddType extends EJSArithType

// bitwise type
sealed abstract class EJSBitwiseType extends EJSOpType
case object EJSShiftType extends EJSBitwiseType
case object EJSBitType extends EJSBitwiseType

// comparison type
sealed abstract class EJSCompType extends EJSOpType
case object EJSEqType extends EJSCompType
case object EJSNonEqType extends EJSCompType

// logical type
case object EJSLogicType extends EJSOpType

// etc type
case object EJSEtcType extends EJSOpType
