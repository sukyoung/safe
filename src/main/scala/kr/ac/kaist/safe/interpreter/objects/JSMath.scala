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

package kr.ac.kaist.safe.interpreter.objects

import kr.ac.kaist.safe.interpreter._
import kr.ac.kaist.safe.interpreter.{ InterpreterPredefine => IP }
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util._

class JSMath(_I: Interpreter, _proto: JSObject)
    extends JSObject(_I, _proto, "Math", false, propTable) {
  /*
   * 15.8 The Math Object
   */
  def init(): Unit = {
    /*
     * 15.8.1 Value Properties of the Math Object
     * [[Writable]]: false, [[Enumerable]]: false, [[Configurable]]: false
     */
    property.put("E", I.IH.numProp(2.7182818284590452354))
    property.put("LN10", I.IH.numProp(2.302585092994046))
    property.put("LN2", I.IH.numProp(0.6931471805599453))
    property.put("LOG2E", I.IH.numProp(1.4426950408889634))
    property.put("LOG10E", I.IH.numProp(0.4342944819032518))
    property.put("PI", I.IH.numProp(3.1415926535897932))
    property.put("SQRT1_2", I.IH.numProp(0.7071067811865476))
    property.put("SQRT2", I.IH.numProp(1.4142135623730951))
    /*
     * 15.8.2 Function Properties of the Math Object
     */
    property.put("abs", I.IH.objProp(I.IS.MathAbs))
    property.put("acos", I.IH.objProp(I.IS.MathAcos))
    property.put("asin", I.IH.objProp(I.IS.MathAsin))
    property.put("atan", I.IH.objProp(I.IS.MathAtan))
    property.put("atan2", I.IH.objProp(I.IS.MathAtan2))
    property.put("ceil", I.IH.objProp(I.IS.MathCeil))
    property.put("cos", I.IH.objProp(I.IS.MathCos))
    property.put("exp", I.IH.objProp(I.IS.MathExp))
    property.put("floor", I.IH.objProp(I.IS.MathFloor))
    property.put("log", I.IH.objProp(I.IS.MathLog))
    property.put("max", I.IH.objProp(I.IS.MathMax))
    property.put("min", I.IH.objProp(I.IS.MathMin))
    property.put("pow", I.IH.objProp(I.IS.MathPow))
    property.put("random", I.IH.objProp(I.IS.MathRandom))
    property.put("round", I.IH.objProp(I.IS.MathRound))
    property.put("sin", I.IH.objProp(I.IS.MathSin))
    property.put("sqrt", I.IH.objProp(I.IS.MathSqrt))
    property.put("tan", I.IH.objProp(I.IS.MathTan))
  }

  override def callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    val args: List[EJSNumber] = I.IH.arrayToList(argsObj).map(x => I.IH.toNumber(x))
    val y: EJSNumber = method match {
      case I.IS.MathAbs => _abs(args.head)
      case I.IS.MathAcos => _acos(args.head)
      case I.IS.MathAsin => _asin(args.head)
      case I.IS.MathAtan => _atan(args.head)
      case I.IS.MathAtan2 => _atan2(args.head, args(1))
      case I.IS.MathCeil => _ceil(args.head)
      case I.IS.MathCos => _cos(args.head)
      case I.IS.MathExp => _exp(args.head)
      case I.IS.MathFloor => _floor(args.head)
      case I.IS.MathLog => _log(args.head)
      case I.IS.MathMax => _max(args)
      case I.IS.MathMin => _min(args)
      case I.IS.MathPow => _pow(args.head, args(1))
      case I.IS.MathRandom => _random()
      case I.IS.MathRound => _round(args.head)
      case I.IS.MathSin => _sin(args.head)
      case I.IS.MathSqrt => _sqrt(args.head)
      case I.IS.MathTan => _tan(args.head)
    }
    I.IS.comp.setReturn(PVal(IRVal(y)))
  }

  /*
   * 15.8.2 Function Properties of the Math Object
   */
  def _abs(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.abs(x.num))
  def _acos(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.acos(x.num))
  def _asin(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.asin(x.num))
  def _atan(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.atan(x.num))
  def _atan2(y: EJSNumber, x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.atan2(y.num, x.num))
  def _ceil(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.ceil(x.num))
  def _cos(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.cos(x.num))
  def _exp(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.exp(x.num))
  def _floor(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.floor(x.num))
  def _log(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.log(x.num))
  def _max(xs: List[EJSNumber]): EJSNumber = I.IH.mkIRNum(
    xs.foldRight(xs.head.num)((x, y) => scala.math.max(x.num, y))
  )
  def _min(xs: List[EJSNumber]): EJSNumber = I.IH.mkIRNum(
    xs.foldRight(xs.head.num)((x, y) => scala.math.min(x.num, y))
  )
  def _pow(x: EJSNumber, y: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.pow(x.num, y.num))
  def _random(): EJSNumber = I.IH.mkIRNum(scala.math.random)
  def _round(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.round(x.num))
  def _sin(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.sin(x.num))
  def _sqrt(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.sqrt(x.num))
  def _tan(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.tan(x.num))
}
