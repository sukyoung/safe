/*******************************************************************************
    Copyright (c) 2013-2014, S-Core, KAIST.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
  ******************************************************************************/

package kr.ac.kaist.jsaf.analysis.typing.models.builtin

import kr.ac.kaist.jsaf.analysis.cfg.{CFGExpr, CFG, InternalError, FunctionId}
import kr.ac.kaist.jsaf.analysis.typing.domain._
import kr.ac.kaist.jsaf.analysis.typing.domain.{BoolFalse => F, BoolTrue => T}
import kr.ac.kaist.jsaf.analysis.typing.models._
import kr.ac.kaist.jsaf.analysis.typing._
import kr.ac.kaist.jsaf.analysis.typing.{AccessHelper=>AH}
import kr.ac.kaist.jsaf.analysis.typing.AddressManager._

object BuiltinMath extends ModelData {

  sealed abstract class AbsNumberPattern
  case object NumTopPattern extends AbsNumberPattern
  case object NumBotPattern extends AbsNumberPattern
  case object InfinityPattern extends AbsNumberPattern
  case object PosInfPattern extends AbsNumberPattern
  case object NegInfPattern extends AbsNumberPattern
  case object NaNPattern extends AbsNumberPattern
  case object UIntPattern extends AbsNumberPattern
  case object NUIntPattern extends AbsNumberPattern
  case class UIntSinglePattern(value : Double) extends AbsNumberPattern {
    override def toString() = value.toLong.toString
  }
  case class NUIntSinglePattern(value : Double) extends AbsNumberPattern {
    override def toString() = value.toString
  }

  def forMatch(v: AbsNumber): AbsNumberPattern = v.getAbsCase match {
    case AbsTop => NumTopPattern
    case AbsBot => NumBotPattern
    case AbsSingle => v.getSingle match {
      case _ if AbsNumber.isPosInf(v) => PosInfPattern
      case _ if AbsNumber.isNegInf(v) => NegInfPattern
      case _ if AbsNumber.isNaN(v) => NaNPattern
      case Some(n) if AbsNumber.isUIntSingle(v) => UIntSinglePattern(n)
      case Some(n) => NUIntSinglePattern(n)
      case _ => throw new InternalError("Impossible case.")
    }
    case AbsMulti =>
      if (AbsNumber.isInfinity(v)) InfinityPattern
      else if (AbsNumber.isUInt(v)) UIntPattern
      else NUIntPattern
  }

  val ConstLoc = newSystemLoc("MathConst", Recent)

  private val prop_const: List[(String, AbsProperty)] = List(
    ("@class",      AbsConstValue(PropValue(AbsString.alpha("Math")))),
    ("@proto",      AbsConstValue(PropValue(ObjectValue(Value(ObjProtoLoc), F, F, F)))),
    ("@extensible", AbsConstValue(PropValue(T))),
    ("constructor", AbsConstValue(PropValue(ObjectValue(Value(BuiltinObject.ConstLoc), F, F, F)))),
    ("E",       AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(2.7182818284590452354), F, F, F)))),
    ("LN10",    AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(2.302585092994046), F, F, F)))),
    ("LN2",     AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0.6931471805599453), F, F, F)))),
    ("LOG2E",   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1.4426950408889634), F, F, F)))),
    ("LOG10E",  AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0.4342944819032518), F, F, F)))),
    ("PI",      AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(3.1415926535897932), F, F, F)))),
    ("SQRT1_2", AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(0.7071067811865476), F, F, F)))),
    ("SQRT2",   AbsConstValue(PropValue(ObjectValue(AbsNumber.alpha(1.4142135623730951), F, F, F)))),
    // 15.8.2 Function Properties of the Math Object
    ("abs",     AbsBuiltinFunc("Math.abs",    1)),
    ("acos",    AbsBuiltinFunc("Math.acos",   1)),
    ("asin",    AbsBuiltinFunc("Math.asin",   1)),
    ("atan",    AbsBuiltinFunc("Math.atan",   1)),
    ("atan2",   AbsBuiltinFunc("Math.atan2",  1)),
    ("ceil",    AbsBuiltinFunc("Math.ceil",   1)),
    ("cos",     AbsBuiltinFunc("Math.cos",    1)),
    ("exp",     AbsBuiltinFunc("Math.exp",    1)),
    ("floor",   AbsBuiltinFunc("Math.floor",  1)),
    ("log",     AbsBuiltinFunc("Math.log",    1)),
    ("max",     AbsBuiltinFunc("Math.max",    2)),
    ("min",     AbsBuiltinFunc("Math.min",    2)),
    ("pow",     AbsBuiltinFunc("Math.pow",    2)),
    ("random",  AbsBuiltinFunc("Math.random", 0)),
    ("round",   AbsBuiltinFunc("Math.round",  1)),
    ("sin",     AbsBuiltinFunc("Math.sin",    1)),
    ("sqrt",    AbsBuiltinFunc("Math.sqrt",   1)),
    ("tan",     AbsBuiltinFunc("Math.tan",    1))
  )

  def getInitList(): List[(Loc, List[(String, AbsProperty)])] = List(
    (ConstLoc, prop_const)
  )

  def getSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("name" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((HeapBot, ContextBot),(HeapBot, ContextBot))
        }
        )),
      ("Math.abs" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0") /* Value */
          val v = Helper.toNumber(Helper.toPrimitive_better(h, v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern|NaNPattern|UIntPattern|NUIntPattern => Value(v)
            case NegInfPattern|PosInfPattern|InfinityPattern =>  Value(Infinity)
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.abs(n)))
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.abs(n)))
            case _ =>  Value(NumTop)
          }
          ((Helper.ReturnStore(h, rtn), ctx), (he, ctxe))
        })),
      ("Math.acos" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0") /* Value */
          val v = Helper.toNumber(Helper.toPrimitive_better(h, v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern => Value(v)
            case NaNPattern|InfinityPattern|PosInfPattern|NegInfPattern =>  Value(NaN)
            case UIntSinglePattern(n) =>
              if (-1>n || 1 < n) Value(NaN)
              else Value(AbsNumber.alpha(scala.math.acos(n)))
            case NUIntSinglePattern(n) =>
              if (-1>n || 1 < n) Value(NaN)
              else Value(AbsNumber.alpha(scala.math.acos(n)))
            case _ =>  Value(NumTop)
          }
          ((Helper.ReturnStore(h, rtn), ctx), (he, ctxe))
        })),
      ("Math.asin" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0") /* Value */
          val v = Helper.toNumber(Helper.toPrimitive_better(h, v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern => Value(v)
            case NaNPattern|InfinityPattern|PosInfPattern|NegInfPattern =>  Value(NaN)
            case UIntSinglePattern(n) =>
              if (-1>n || 1<n) Value(NaN)
              else Value(AbsNumber.alpha(scala.math.asin(n)))
            case NUIntSinglePattern(n) =>
              if (-1>n || 1<n) Value(NaN)
              else Value(AbsNumber.alpha(scala.math.asin(n)))
            case _ =>  Value(NumTop)
          }
          ((Helper.ReturnStore(h, rtn), ctx), (he, ctxe))
        })),
      ("Math.atan" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0") /* Value */
          val v = Helper.toNumber(Helper.toPrimitive_better(h, v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern|NaNPattern =>  Value(v)
            case InfinityPattern =>  Value(NUInt)
            case PosInfPattern =>  Value(AbsNumber.alpha(scala.math.Pi/2))
            case NegInfPattern =>  Value(AbsNumber.alpha(-scala.math.Pi/2))
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.atan(n)))
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.atan(n)))
            case _ =>  Value(NumTop)
          }
          ((Helper.ReturnStore(h, rtn), ctx), (he, ctxe))
        })),
      ("Math.atan2" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0") /* Value */
          val v_2 = getArgValue(h, ctx, args, "1") /* Value */
          val vy = Helper.toNumber(Helper.toPrimitive_better(h, v_1))
          val vx = Helper.toNumber(Helper.toPrimitive_better(h, v_2))
          val rtn = (forMatch(vy), forMatch(vx)) match {
            case (NumBotPattern, _) =>  Value(NumBot)
            case (_, NumBotPattern) =>  Value(NumBot)
            case (NaNPattern, _) =>  Value(NaN)
            case (_, NaNPattern) =>  Value(NaN)
            case (NumTopPattern, _) =>  Value(NumTop)
            case (_, NumTopPattern) =>  Value(NumTop)

            case (UIntPattern|NUIntPattern, PosInfPattern) =>  Value(AbsNumber.alpha(0))
            case (UIntSinglePattern(n), PosInfPattern) =>  Value(AbsNumber.alpha(0))
            case (NUIntSinglePattern(n), PosInfPattern) =>  Value(AbsNumber.alpha(0))

            case (UIntPattern|NUIntPattern, NegInfPattern) =>  Value(UInt)
            case (UIntSinglePattern(n), NegInfPattern) =>  Value(AbsNumber.alpha(scala.math.Pi))
            case (NUIntSinglePattern(n), NegInfPattern) =>
              if (n < 0) Value(AbsNumber.alpha(scala.math.Pi))
              else Value(AbsNumber.alpha(-scala.math.Pi))

            case (UIntPattern|NUIntPattern, InfinityPattern) =>  Value(UInt)

            case (PosInfPattern, UIntPattern|NUIntPattern) =>  Value(AbsNumber.alpha(scala.math.Pi/2))
            case (PosInfPattern, UIntSinglePattern(n)) =>  Value(AbsNumber.alpha(scala.math.Pi/2))
            case (PosInfPattern, NUIntSinglePattern(n)) =>  Value(AbsNumber.alpha(scala.math.Pi/2))

            case (NegInfPattern, UIntPattern|NUIntPattern) =>  Value(AbsNumber.alpha(-scala.math.Pi/2))
            case (NegInfPattern, UIntSinglePattern(n)) =>  Value(AbsNumber.alpha(-scala.math.Pi/2))
            case (NegInfPattern, NUIntSinglePattern(n)) =>  Value(AbsNumber.alpha(-scala.math.Pi/2))

            case (PosInfPattern, PosInfPattern) =>  Value(AbsNumber.alpha(scala.math.Pi/4))
            case (PosInfPattern, NegInfPattern) =>  Value(AbsNumber.alpha(3*scala.math.Pi/4))
            case (NegInfPattern, PosInfPattern) =>  Value(AbsNumber.alpha(-scala.math.Pi/4))
            case (NegInfPattern, NegInfPattern) =>  Value(AbsNumber.alpha(-3*scala.math.Pi/4))

            case (InfinityPattern, InfinityPattern|PosInfPattern|NegInfPattern) =>  Value(NUInt)
            case (InfinityPattern|PosInfPattern|NegInfPattern, InfinityPattern) =>  Value(NUInt)

            case (UIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(scala.math.atan2(n1, n2)))
            case (UIntSinglePattern(n1), NUIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(scala.math.atan2(n1, n2)))
            case (NUIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(scala.math.atan2(n1, n2)))
            case (NUIntSinglePattern(n1), NUIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(scala.math.atan2(n1, n2)))

            case _ =>  Value(NumTop)
          }
          ((Helper.ReturnStore(h, rtn), ctx), (he, ctxe))
        })),
      ("Math.ceil" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0") /* Value */
          val v = Helper.toNumber(Helper.toPrimitive_better(h, v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern|NaNPattern|InfinityPattern|PosInfPattern|NegInfPattern|UIntPattern =>  Value(v)
            case UIntSinglePattern(n) =>  Value(v)
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.ceil(n)))
            case _ =>  Value(NumTop)
          }
          ((Helper.ReturnStore(h, rtn), ctx), (he, ctxe))
        })),
      ("Math.cos" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0") /* Value */
          val v = Helper.toNumber(Helper.toPrimitive_better(h, v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern =>  Value(NumBot)
            case NaNPattern|InfinityPattern|PosInfPattern|NegInfPattern =>  Value(NaN)
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.cos(n)))
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.cos(n)))
            case _ =>  Value(NumTop)
          }
          ((Helper.ReturnStore(h, rtn), ctx), (he, ctxe))
        })),
      ("Math.exp" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0") /* Value */
          val v = Helper.toNumber(Helper.toPrimitive_better(h, v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern|NaNPattern|PosInfPattern|NUIntPattern =>  Value(v)
            case InfinityPattern|UIntPattern|NumTopPattern =>  Value(NumTop)
            case NegInfPattern =>  Value(AbsNumber.alpha(0))
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.exp(n)))
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.exp(n)))
            case _ =>  Value(NumTop)
          }
          ((Helper.ReturnStore(h, rtn), ctx), (he, ctxe))
        })),
      ("Math.floor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0") /* Value */
          val v = Helper.toNumber(Helper.toPrimitive_better(h, v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern|NumTopPattern|NaNPattern|InfinityPattern|PosInfPattern|NegInfPattern|UIntPattern =>  Value(v)
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.floor(n)))
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.floor(n)))
            case _ =>  Value(NumTop)
          }
          ((Helper.ReturnStore(h, rtn), ctx), (he, ctxe))
        })),
      ("Math.max" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val n_arglen = getArgValue(h, ctx, args, "length")._1._4
          def n_arg = (i : Int) => Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, i.toString)))
          val n_1 =
            forMatch(n_arglen) match {
              case UIntSinglePattern(n) =>
                if (n == 0)
                  NegInf
                else {
                  val n_3 =
                    if ((0 to n.toInt -1).exists((i:Int) => NaN <= n_arg(i)))
                      NaN
                    else
                      NumBot
                  n_3 + (0 to n.toInt -1).foldLeft[AbsNumber](n_arg(0))(
                    (an, i)=> {
                          if (Value(BoolTop) <= Operator.bopGreaterEq(Value(n_arg(i)), Value(an)))
                            an + n_arg(i)
                          else if (Value(BoolTrue) == Operator.bopGreaterEq(Value(n_arg(i)), Value(an)))
                            n_arg(i)
                          else
                            an
                    })
                }
              case NUIntSinglePattern(_)|NaNPattern|NegInfPattern|PosInfPattern|InfinityPattern|UIntPattern|NUIntPattern|NumTopPattern => NumTop
              case NumBotPattern => NumBot
            }
          ((Helper.ReturnStore(h, Value(n_1)), ctx), (he, ctxe))
        })),
      ("Math.min" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val n_arglen = getArgValue(h, ctx, args, "length")._1._4
          def n_arg = (i : Int) => Helper.toNumber(Helper.toPrimitive_better(h, getArgValue(h, ctx, args, i.toString)))
          val n_1 =
            forMatch(n_arglen) match {
              case UIntSinglePattern(n) =>
                if (n == 0)
                  PosInf
                else {
                  val n_3 =
                    if ((0 to n.toInt -1).exists((i:Int) => NaN <= n_arg(i)))
                      NaN
                    else
                      NumBot
                  n_3 + (0 to n.toInt -1).foldLeft[AbsNumber](n_arg(0))(
                    (an, i)=> {
                          if (Value(BoolTop) <= Operator.bopLessEq(Value(n_arg(i)), Value(an)))
                            an + n_arg(i)
                          else if (Value(BoolTrue) == Operator.bopLessEq(Value(n_arg(i)), Value(an)))
                            n_arg(i)
                          else
                            an
                    })
                }
              case NUIntSinglePattern(_)|NaNPattern|NegInfPattern|PosInfPattern|InfinityPattern|UIntPattern|NUIntPattern|NumTopPattern => NumTop
              case NumBotPattern => NumBot
            }
          ((Helper.ReturnStore(h, Value(n_1)), ctx), (he, ctxe))
        })),
      ("Math.pow" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0") /* Value */
          val v_2 = getArgValue(h, ctx, args, "1") /* Value */
          val vx = Helper.toNumber(Helper.toPrimitive_better(h, v_1))
          val vy = Helper.toNumber(Helper.toPrimitive_better(h, v_2))
          val rtn = (forMatch(vx), forMatch(vy)) match {
            case (NumTopPattern, _) => Value(vx)
            case (_, NumTopPattern) => Value(vy)

            case (NumBotPattern, _) => Value(NumBot)
            case (_, NumBotPattern) => Value(NumBot)

            case (_, NaNPattern) =>  Value(NaN)
            case (_, UIntSinglePattern(0)) =>  Value(AbsNumber.alpha(1))
            case (NaNPattern, _) =>  Value(NaN)

            case (UIntSinglePattern(0), PosInfPattern) =>  Value(AbsNumber.alpha(0))
            case (UIntSinglePattern(0), NegInfPattern) =>  Value(PosInf)
            case (UIntSinglePattern(0), UIntSinglePattern(n)) =>  Value(AbsNumber.alpha(0))
            case (UIntSinglePattern(0), NUIntSinglePattern(n)) =>
              if (n<0) Value(PosInf)
              else Value(AbsNumber.alpha(0))

            case (PosInfPattern|NegInfPattern|InfinityPattern, PosInfPattern) =>  Value(PosInf)
            case (PosInfPattern|NegInfPattern|InfinityPattern, NegInfPattern) =>  Value(AbsNumber.alpha(0))

            case (PosInfPattern, UIntSinglePattern(n)) =>  Value(PosInf)
            case (PosInfPattern, NUIntSinglePattern(n)) =>
              if (0<n) Value(PosInf)
              else Value(AbsNumber.alpha(0))

            case (NegInfPattern, UIntSinglePattern(n)) =>
              if (n%2!=1) Value(PosInf)
              else Value(NegInf)
            case (NegInfPattern, NUIntSinglePattern(n)) =>
              if (0<n) {
                if (n%2!=1) Value(PosInf)
                else Value(NegInf)
              } else {
                Value(AbsNumber.alpha(0))
              }

            case (InfinityPattern, UIntSinglePattern(n)) =>
              if (n%2!=1) Value(PosInf)
              else Value(NumTop)
            case (InfinityPattern, NUIntSinglePattern(n)) =>
              if (n>0) {
                if (n%2 != 1) Value(PosInf)
                else Value(NumTop)
              } else {
                Value(AbsNumber.alpha(0))
              }

            case (UIntSinglePattern(n), PosInfPattern) =>
              if (n < -1 || 1 < n) Value(PosInf)
              else if (-1 < n && n < 1) Value(AbsNumber.alpha(0))
              else Value(NaN)
            case (NUIntSinglePattern(n), PosInfPattern) =>
              if (n < -1 || 1 < n) Value(PosInf)
              else if (-1 < n && n < 1) Value(AbsNumber.alpha(0))
              else Value(NaN)


            case (UIntSinglePattern(n), NegInfPattern) =>
              if (n < -1 || 1 < n) Value(AbsNumber.alpha(0))
              else if (-1 < n && n < 1) Value(PosInf)
              else Value(NaN)
            case (NUIntSinglePattern(n), NegInfPattern) =>
              if (n < -1 || 1 < n) Value(PosInf)
              else if (-1 < n && n < 1) Value(AbsNumber.alpha(0))
              else Value(NaN)

            case (UIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(scala.math.pow(n1, n2)))
            case (UIntSinglePattern(n1), NUIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(scala.math.pow(n1, n2)))
            case (NUIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(scala.math.pow(n1, n2)))
            case (NUIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
              val intnum = n2.toInt
              val diff:Double = n2 - intnum.toDouble
              if (n1 < 0 && (diff != 0)) Value(NaN)
              else Value(AbsNumber.alpha(scala.math.pow(n1, n2)))


            case _ => Value(NumTop)
          }
          ((Helper.ReturnStore(h, rtn), ctx), (he, ctxe))
        })),
      ("Math.log" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0") /* Value */
          val v = Helper.toNumber(Helper.toPrimitive_better(h, v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern|PosInfPattern =>  Value(v)
            case NaNPattern|NegInfPattern =>  Value(NaN)
            case UIntSinglePattern(n) =>
              if (n<0) Value(NaN)
              else if (n==0) Value(NegInf)
              else Value(AbsNumber.alpha(scala.math.log(n)))
            case NUIntSinglePattern(n) =>
              if (n<0) Value(NaN)
              else Value(AbsNumber.alpha(scala.math.log(n)))
            case _ =>  Value(NumTop)
          }
          ((Helper.ReturnStore(h, rtn), ctx), (he, ctxe))
        })),
      ("Math.random" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          ((Helper.ReturnStore(h, Value(NumTop)), ctx), (he, ctxe))
        })),
      ("Math.round" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0") /* Value */
          val v = Helper.toNumber(Helper.toPrimitive_better(h, v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern|NaNPattern|InfinityPattern|PosInfPattern|NegInfPattern|UIntPattern|NUIntPattern => Value(v)
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.round(n)))
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.round(n)))
            case _ =>  Value(NumTop)
          }
          ((Helper.ReturnStore(h, rtn), ctx), (he, ctxe))
        })),
      ("Math.sin" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0") /* Value */
          val v = Helper.toNumber(Helper.toPrimitive_better(h, v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern => Value(v)
            case NaNPattern|InfinityPattern|PosInfPattern|NegInfPattern =>  Value(NaN)
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.sin(n)))
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.sin(n)))
            case _ =>  Value(NumTop)
          }
          ((Helper.ReturnStore(h, rtn), ctx), (he, ctxe))
        })),
      ("Math.sqrt" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0") /* Value */
          val v = Helper.toNumber(Helper.toPrimitive_better(h, v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern|PosInfPattern => Value(v)
            case NaNPattern|NegInfPattern =>  Value(NaN)
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.sqrt(n)))
            case NUIntSinglePattern(n) =>
              if (n<0)   Value(NaN)
              else Value(AbsNumber.alpha(scala.math.sqrt(n)))
            case _ =>  Value(NumTop)
          }
          ((Helper.ReturnStore(h, rtn), ctx), (he, ctxe))
        })),
      ("Math.tan" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val v_1 = getArgValue(h, ctx, args, "0") /* Value */
          val v = Helper.toNumber(Helper.toPrimitive_better(h, v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern => Value(v)
            case NaNPattern|InfinityPattern|PosInfPattern|NegInfPattern =>  Value(NaN)
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.tan(n)))
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.tan(n)))
            case _ =>  Value(NumTop)
          }
          ((Helper.ReturnStore(h, rtn), ctx), (he, ctxe))
        }))
    )
  }

  def getPreSemanticMap(): Map[String, SemanticFun] = {
    Map(
      ("Global.parseInt" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          // 15.1.2.2 parseInt(string, radix)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* string */
          val v_2 = getArgValue_pre(h, ctx, args, "1", PureLocalLoc) /* radix */

          val inputString = PreHelper.toString(PreHelper.toPrimitive(v_1))
          // TODO: Simple implementation. Must be revised. Not the same as the original.
          val r = Operator.ToInt32(v_2)

          val value = Operator.parseInt(inputString, r)
          val rtn = Value(value)

          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctx))
        })
        ),
      ("Math.abs" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* Value */
          val v = PreHelper.toNumber(PreHelper.toPrimitive(v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern|NaNPattern|UIntPattern|NUIntPattern => Value(v)
            case NegInfPattern|PosInfPattern|InfinityPattern =>  Value(Infinity)
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.abs(n)))
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.abs(n)))
            case _ =>  Value(NumTop)
          }
          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctxe))
        })),
      ("Math.acos" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* Value */
          val v = PreHelper.toNumber(PreHelper.toPrimitive(v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern => Value(v)
            case NaNPattern|InfinityPattern|PosInfPattern|NegInfPattern =>  Value(NaN)
            case UIntSinglePattern(n) =>
              if (-1>n || 1 < n) Value(NaN)
              else Value(AbsNumber.alpha(scala.math.acos(n)))
            case NUIntSinglePattern(n) =>
              if (-1>n || 1 < n) Value(NaN)
              else Value(AbsNumber.alpha(scala.math.acos(n)))
            case _ =>  Value(NumTop)
          }
          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctxe))
        })),
      ("Math.asin" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* Value */
          val v = PreHelper.toNumber(PreHelper.toPrimitive(v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern => Value(v)
            case NaNPattern|InfinityPattern|PosInfPattern|NegInfPattern =>  Value(NaN)
            case UIntSinglePattern(n) =>
              if (-1>n || 1<n) Value(NaN)
              else Value(AbsNumber.alpha(scala.math.asin(n)))
            case NUIntSinglePattern(n) =>
              if (-1>n || 1<n) Value(NaN)
              else Value(AbsNumber.alpha(scala.math.asin(n)))
            case _ =>  Value(NumTop)
          }
          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctxe))
        })),
      ("Math.atan" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* Value */
          val v = PreHelper.toNumber(PreHelper.toPrimitive(v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern|NaNPattern =>  Value(v)
            case InfinityPattern =>  Value(NUInt)
            case PosInfPattern =>  Value(AbsNumber.alpha(scala.math.Pi/2))
            case NegInfPattern =>  Value(AbsNumber.alpha(-scala.math.Pi/2))
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.atan(n)))
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.atan(n)))
            case _ =>  Value(NumTop)
          }
          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctxe))
        })),
      ("Math.atan2" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* Value */
          val v_2 = getArgValue_pre(h, ctx, args, "1", PureLocalLoc) /* Value */
          val vy = PreHelper.toNumber(PreHelper.toPrimitive(v_1))
          val vx = PreHelper.toNumber(PreHelper.toPrimitive(v_2))
          val rtn = (forMatch(vy), forMatch(vx)) match {
            case (NumBotPattern, _) =>  Value(NumBot)
            case (_, NumBotPattern) =>  Value(NumBot)
            case (NaNPattern, _) =>  Value(NaN)
            case (_, NaNPattern) =>  Value(NaN)
            case (NumTopPattern, _) =>  Value(NumTop)
            case (_, NumTopPattern) =>  Value(NumTop)

            case (UIntPattern|NUIntPattern, PosInfPattern) =>  Value(AbsNumber.alpha(0))
            case (UIntSinglePattern(n), PosInfPattern) =>  Value(AbsNumber.alpha(0))
            case (NUIntSinglePattern(n), PosInfPattern) =>  Value(AbsNumber.alpha(0))

            case (UIntPattern|NUIntPattern, NegInfPattern) =>  Value(UInt)
            case (UIntSinglePattern(n), NegInfPattern) =>  Value(AbsNumber.alpha(scala.math.Pi))
            case (NUIntSinglePattern(n), NegInfPattern) =>
              if (n < 0) Value(AbsNumber.alpha(scala.math.Pi))
              else Value(AbsNumber.alpha(-scala.math.Pi))

            case (UIntPattern|NUIntPattern, InfinityPattern) =>  Value(UInt)

            case (PosInfPattern, UIntPattern|NUIntPattern) =>  Value(AbsNumber.alpha(scala.math.Pi/2))
            case (PosInfPattern, UIntSinglePattern(n)) =>  Value(AbsNumber.alpha(scala.math.Pi/2))
            case (PosInfPattern, NUIntSinglePattern(n)) =>  Value(AbsNumber.alpha(scala.math.Pi/2))

            case (NegInfPattern, UIntPattern|NUIntPattern) =>  Value(AbsNumber.alpha(-scala.math.Pi/2))
            case (NegInfPattern, UIntSinglePattern(n)) =>  Value(AbsNumber.alpha(-scala.math.Pi/2))
            case (NegInfPattern, NUIntSinglePattern(n)) =>  Value(AbsNumber.alpha(-scala.math.Pi/2))

            case (PosInfPattern, PosInfPattern) =>  Value(AbsNumber.alpha(scala.math.Pi/4))
            case (PosInfPattern, NegInfPattern) =>  Value(AbsNumber.alpha(3*scala.math.Pi/4))
            case (NegInfPattern, PosInfPattern) =>  Value(AbsNumber.alpha(-scala.math.Pi/4))
            case (NegInfPattern, NegInfPattern) =>  Value(AbsNumber.alpha(-3*scala.math.Pi/4))

            case (InfinityPattern, InfinityPattern|PosInfPattern|NegInfPattern) =>  Value(NUInt)
            case (InfinityPattern|PosInfPattern|NegInfPattern, InfinityPattern) =>  Value(NUInt)

            case (UIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(scala.math.atan2(n1, n2)))
            case (UIntSinglePattern(n1), NUIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(scala.math.atan2(n1, n2)))
            case (NUIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(scala.math.atan2(n1, n2)))
            case (NUIntSinglePattern(n1), NUIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(scala.math.atan2(n1, n2)))

            case _ =>  Value(NumTop)
          }
          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctxe))
        })),
      ("Math.ceil" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* Value */
          val v = PreHelper.toNumber(PreHelper.toPrimitive(v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern|NaNPattern|InfinityPattern|PosInfPattern|NegInfPattern|UIntPattern =>  Value(v)
            case UIntSinglePattern(n) =>  Value(v)
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.ceil(n)))
            case _ =>  Value(NumTop)
          }
          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctxe))
        })),
      ("Math.cos" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* Value */
          val v = PreHelper.toNumber(PreHelper.toPrimitive(v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern =>  Value(NumBot)
            case NaNPattern|InfinityPattern|PosInfPattern|NegInfPattern =>  Value(NaN)
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.cos(n)))
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.cos(n)))
            case _ =>  Value(NumTop)
          }
          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctxe))
        })),
      ("Math.exp" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* Value */
          val v = PreHelper.toNumber(PreHelper.toPrimitive(v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern|NaNPattern|PosInfPattern|NUIntPattern =>  Value(v)
            case InfinityPattern|UIntPattern|NumTopPattern =>  Value(NumTop)
            case NegInfPattern =>  Value(AbsNumber.alpha(0))
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.exp(n)))
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.exp(n)))
            case _ =>  Value(NumTop)
          }
          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctxe))
        })),
      ("Math.floor" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* Value */
          val v = PreHelper.toNumber(PreHelper.toPrimitive(v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern|NumTopPattern|NaNPattern|InfinityPattern|PosInfPattern|NegInfPattern|UIntPattern =>  Value(v)
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.floor(n)))
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.floor(n)))
            case _ =>  Value(NumTop)
          }
          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctxe))
        })),
      ("Math.max" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val n_arglen = getArgValue_pre(h, ctx, args, "length", PureLocalLoc)._1._4
          def n_arg = (i : Int) => PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, i.toString, PureLocalLoc)))
          val n_1 =
            forMatch(n_arglen) match {
              case UIntSinglePattern(n) =>
                if (n == 0)
                  NegInf
                else {
                  val n_3 =
                    if ((0 to n.toInt -1).exists((i:Int) => NaN <= n_arg(i)))
                      NaN
                    else
                      NumBot
                  n_3 + (0 to n.toInt -1).foldLeft[AbsNumber](NumBot)(
                    (an, i)=> {
                      (0 to n.toInt -1).foldLeft(an)(
                        (ann, j) => {
                          if (Value(BoolTrue) <= Operator.bopGreaterEq(Value(n_arg(i)), Value(n_arg(j))))
                            ann + n_arg(i)
                          else
                            ann
                        })
                    })
                }
              case NUIntSinglePattern(_)|NaNPattern|NegInfPattern|PosInfPattern|InfinityPattern|UIntPattern|NUIntPattern|NumTopPattern => NumTop
              case NumBotPattern => NumBot
            }
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(n_1)), ctx), (he, ctxe))
        })),
      ("Math.min" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val n_arglen = getArgValue_pre(h, ctx, args, "length", PureLocalLoc)._1._4
          def n_arg = (i : Int) => PreHelper.toNumber(PreHelper.toPrimitive(getArgValue_pre(h, ctx, args, i.toString, PureLocalLoc)))
          val n_1 =
            forMatch(n_arglen) match {
              case UIntSinglePattern(n) =>
                if (n == 0)
                  PosInf
                else {
                  val n_3 =
                    if ((0 to n.toInt -1).exists((i:Int) => NaN <= n_arg(i)))
                      NaN
                    else
                      NumBot
                  n_3 + (0 to n.toInt -1).foldLeft[AbsNumber](NumBot)(
                    (an, i)=> {
                      (0 to n.toInt -1).foldLeft(an)(
                        (ann, j) => {
                          if (Value(BoolTrue) <= Operator.bopLessEq(Value(n_arg(i)), Value(n_arg(j))))
                            ann + n_arg(i)
                          else
                            ann
                        })
                    })
                }
              case NUIntSinglePattern(_)|NaNPattern|NegInfPattern|PosInfPattern|InfinityPattern|UIntPattern|NUIntPattern|NumTopPattern => NumTop
              case NumBotPattern => NumBot
            }
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(n_1)), ctx), (he, ctxe))
        })),
      ("Math.pow" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* Value */
          val v_2 = getArgValue_pre(h, ctx, args, "1", PureLocalLoc) /* Value */
          val vx = PreHelper.toNumber(PreHelper.toPrimitive(v_1))
          val vy = PreHelper.toNumber(PreHelper.toPrimitive(v_2))
          val rtn = (forMatch(vx), forMatch(vy)) match {
            case (NumTopPattern, _) => Value(vx)
            case (_, NumTopPattern) => Value(vy)

            case (NumBotPattern, _) => Value(NumBot)
            case (_, NumBotPattern) => Value(NumBot)

            case (_, NaNPattern) =>  Value(NaN)
            case (_, UIntSinglePattern(0)) =>  Value(AbsNumber.alpha(1))
            case (NaNPattern, _) =>  Value(NaN)

            case (UIntSinglePattern(0), PosInfPattern) =>  Value(AbsNumber.alpha(0))
            case (UIntSinglePattern(0), NegInfPattern) =>  Value(PosInf)
            case (UIntSinglePattern(0), UIntSinglePattern(n)) =>  Value(AbsNumber.alpha(0))
            case (UIntSinglePattern(0), NUIntSinglePattern(n)) =>
              if (n<0) Value(PosInf)
              else Value(AbsNumber.alpha(0))

            case (PosInfPattern|NegInfPattern|InfinityPattern, PosInfPattern) =>  Value(PosInf)
            case (PosInfPattern|NegInfPattern|InfinityPattern, NegInfPattern) =>  Value(AbsNumber.alpha(0))

            case (PosInfPattern, UIntSinglePattern(n)) =>  Value(PosInf)
            case (PosInfPattern, NUIntSinglePattern(n)) =>
              if (0<n) Value(PosInf)
              else Value(AbsNumber.alpha(0))

            case (NegInfPattern, UIntSinglePattern(n)) =>
              if (n%2!=1) Value(PosInf)
              else Value(NegInf)
            case (NegInfPattern, NUIntSinglePattern(n)) =>
              if (0<n) {
                if (n%2!=1) Value(PosInf)
                else Value(NegInf)
              } else {
                Value(AbsNumber.alpha(0))
              }

            case (InfinityPattern, UIntSinglePattern(n)) =>
              if (n%2!=1) Value(PosInf)
              else Value(NumTop)
            case (InfinityPattern, NUIntSinglePattern(n)) =>
              if (n>0) {
                if (n%2 != 1) Value(PosInf)
                else Value(NumTop)
              } else {
                Value(AbsNumber.alpha(0))
              }

            case (UIntSinglePattern(n), PosInfPattern) =>
              if (n < -1 || 1 < n) Value(PosInf)
              else if (-1 < n && n < 1) Value(AbsNumber.alpha(0))
              else Value(NaN)
            case (NUIntSinglePattern(n), PosInfPattern) =>
              if (n < -1 || 1 < n) Value(PosInf)
              else if (-1 < n && n < 1) Value(AbsNumber.alpha(0))
              else Value(NaN)


            case (UIntSinglePattern(n), NegInfPattern) =>
              if (n < -1 || 1 < n) Value(AbsNumber.alpha(0))
              else if (-1 < n && n < 1) Value(PosInf)
              else Value(NaN)
            case (NUIntSinglePattern(n), NegInfPattern) =>
              if (n < -1 || 1 < n) Value(PosInf)
              else if (-1 < n && n < 1) Value(AbsNumber.alpha(0))
              else Value(NaN)

            case (UIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(scala.math.pow(n1, n2)))
            case (UIntSinglePattern(n1), NUIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(scala.math.pow(n1, n2)))
            case (NUIntSinglePattern(n1), UIntSinglePattern(n2)) =>  Value(AbsNumber.alpha(scala.math.pow(n1, n2)))
            case (NUIntSinglePattern(n1), NUIntSinglePattern(n2)) =>
              val intnum = n2.toInt
              val diff:Double = n2 - intnum.toDouble
              if (n1 < 0 && (diff != 0)) Value(NaN)
              else Value(AbsNumber.alpha(scala.math.pow(n1, n2)))


            case _ => Value(NumTop)
          }
          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctxe))
        })),
      ("Math.log" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* Value */
          val v = PreHelper.toNumber(PreHelper.toPrimitive(v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern|PosInfPattern =>  Value(v)
            case NaNPattern|NegInfPattern =>  Value(NaN)
            case UIntSinglePattern(n) =>
              if (n<0) Value(NaN)
              else if (n==0) Value(NegInf)
              else Value(AbsNumber.alpha(scala.math.log(n)))
            case NUIntSinglePattern(n) =>
              if (n<0) Value(NaN)
              else Value(AbsNumber.alpha(scala.math.log(n)))
            case _ =>  Value(NumTop)
          }
          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctxe))
        })),
      ("Math.random" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          ((PreHelper.ReturnStore(h, PureLocalLoc, Value(NumTop)), ctx), (he, ctxe))
        })),
      ("Math.round" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* Value */
          val v = PreHelper.toNumber(PreHelper.toPrimitive(v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern|NaNPattern|InfinityPattern|PosInfPattern|NegInfPattern|UIntPattern|NUIntPattern => Value(v)
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.round(n)))
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.round(n)))
            case _ =>  Value(NumTop)
          }
          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctxe))
        })),
      ("Math.sin" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* Value */
          val v = PreHelper.toNumber(PreHelper.toPrimitive(v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern => Value(v)
            case NaNPattern|InfinityPattern|PosInfPattern|NegInfPattern =>  Value(NaN)
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.sin(n)))
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.sin(n)))
            case _ =>  Value(NumTop)
          }
          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctxe))
        })),
      ("Math.sqrt" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* Value */
          val v = PreHelper.toNumber(PreHelper.toPrimitive(v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern|PosInfPattern => Value(v)
            case NaNPattern|NegInfPattern =>  Value(NaN)
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.sqrt(n)))
            case NUIntSinglePattern(n) =>
              if (n<0)   Value(NaN)
              else Value(AbsNumber.alpha(scala.math.sqrt(n)))
            case _ =>  Value(NumTop)
          }
          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctxe))
        })),
      ("Math.tan" -> (
        (sem: Semantics, h: Heap, ctx: Context, he: Heap, ctxe: Context, cp: ControlPoint, cfg: CFG, fun: String, args: CFGExpr) => {
          val PureLocalLoc = cfg.getPureLocal(cp)
          val v_1 = getArgValue_pre(h, ctx, args, "0", PureLocalLoc) /* Value */
          val v = PreHelper.toNumber(PreHelper.toPrimitive(v_1))
          val rtn = forMatch(v) match {
            case NumBotPattern => Value(v)
            case NaNPattern|InfinityPattern|PosInfPattern|NegInfPattern =>  Value(NaN)
            case UIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.tan(n)))
            case NUIntSinglePattern(n) =>  Value(AbsNumber.alpha(scala.math.tan(n)))
            case _ =>  Value(NumTop)
          }
          ((PreHelper.ReturnStore(h, PureLocalLoc, rtn), ctx), (he, ctxe))
        }))
    )
  }

  def getDefMap(): Map[String, AccessFun] = {
    Map(
      ("Math.abs" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.acos" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.asin" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.atan" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.atan2" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.ceil" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.cos"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.exp"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.floor"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.max"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.min"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.pow"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.log"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.random"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.round" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.sin"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.sqrt"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.tan"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        }))
    )
  }

  def getUseMap(): Map[String, AccessFun] = {
    Map(
      ("Math.abs" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + ((SinglePureLocalLoc, "@return"))
        })),
      ("Math.acos" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + ((SinglePureLocalLoc, "@return"))
        })),
      ("Math.asin" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + ((SinglePureLocalLoc, "@return"))
        })),
      ("Math.atan" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + ((SinglePureLocalLoc, "@return"))
        })),
      ("Math.atan2" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1") + ((SinglePureLocalLoc, "@return"))
        })),
      ("Math.ceil" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + ((SinglePureLocalLoc, "@return"))
        })),
      ("Math.cos"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + ((SinglePureLocalLoc, "@return"))
        })),
      ("Math.exp"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + ((SinglePureLocalLoc, "@return"))
        })),
      ("Math.floor"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + ((SinglePureLocalLoc, "@return"))
        })),
      ("Math.max"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val n_arglen = getArgValue(h, ctx, args, "length")._1._4
          val LP1 = getArgValue_use(h, ctx, args, "length")
          val LP2 = forMatch(n_arglen) match {
            case UIntSinglePattern(n) =>
              (0 until n.toInt).foldLeft(LPBot)((lpset, i) => lpset ++ getArgValue_use(h, ctx, args, i.toString))
            case _ => LPBot
          }
          LP1 ++ LP2 + ((SinglePureLocalLoc, "@return"))
        })),
      ("Math.min"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          val n_arglen = getArgValue(h, ctx, args, "length")._1._4
          val LP1 = getArgValue_use(h, ctx, args, "length")
          val LP2 = forMatch(n_arglen) match {
            case UIntSinglePattern(n) =>
              (0 until n.toInt).foldLeft(LPBot)((lpset, i) => lpset ++ getArgValue_use(h, ctx, args, i.toString))
            case _ => LPBot
          }
          LP1 ++ LP2 + ((SinglePureLocalLoc, "@return"))
        })),
      ("Math.pow"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") ++ getArgValue_use(h, ctx, args, "1") + ((SinglePureLocalLoc, "@return"))
        })),
      ("Math.log"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + ((SinglePureLocalLoc, "@return"))
        })),
      ("Math.random"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          LPSet((SinglePureLocalLoc, "@return"))
        })),
      ("Math.round" -> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + ((SinglePureLocalLoc, "@return"))
        })),
      ("Math.sin"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + ((SinglePureLocalLoc, "@return"))
        })),
      ("Math.sqrt"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + ((SinglePureLocalLoc, "@return"))
        })),
      ("Math.tan"-> (
        (h: Heap, ctx: Context, cfg: CFG, fun: String, args: CFGExpr, fid: FunctionId) => {
          getArgValue_use(h, ctx, args, "0") + ((SinglePureLocalLoc, "@return"))
        }))
    )
  }
}
