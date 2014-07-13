/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter.objects

import kr.ac.kaist.jsaf.nodes.IRNumber
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.interpreter.{InterpreterPredefine => IP, _}

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

  override def __callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    val args: List[IRNumber] = I.IH.arrayToList(argsObj).map(x => I.IH.toNumber(x))
    val y: IRNumber = method match {
      case I.IS.MathAbs => _abs(args.get(0))
      case I.IS.MathAcos => _acos(args.get(0))
      case I.IS.MathAsin => _asin(args.get(0))
      case I.IS.MathAtan => _atan(args.get(0))
      case I.IS.MathAtan2 => _atan2(args.get(0), args.get(1))
      case I.IS.MathCeil => _ceil(args.get(0))
      case I.IS.MathCos => _cos(args.get(0))
      case I.IS.MathExp => _exp(args.get(0))
      case I.IS.MathFloor => _floor(args.get(0))
      case I.IS.MathLog => _log(args.get(0))
      case I.IS.MathMax => _max(args)
      case I.IS.MathMin => _min(args)
      case I.IS.MathPow => _pow(args.get(0), args.get(1))
      case I.IS.MathRandom => _random()
      case I.IS.MathRound => _round(args.get(0))
      case I.IS.MathSin => _sin(args.get(0))
      case I.IS.MathSqrt => _sqrt(args.get(0))
      case I.IS.MathTan => _tan(args.get(0))
    }
    I.IS.comp.setReturn(PVal(y))
  }

  /*
   * 15.8.2 Function Properties of the Math Object
   */
  def _abs(x: IRNumber): IRNumber = I.IH.mkIRNum(scala.math.abs(x.getNum))
  def _acos(x: IRNumber): IRNumber = I.IH.mkIRNum(scala.math.acos(x.getNum))
  def _asin(x: IRNumber): IRNumber = I.IH.mkIRNum(scala.math.asin(x.getNum))
  def _atan(x: IRNumber): IRNumber = I.IH.mkIRNum(scala.math.atan(x.getNum))
  def _atan2(y: IRNumber, x: IRNumber): IRNumber = I.IH.mkIRNum(scala.math.atan2(y.getNum, x.getNum))
  def _ceil(x: IRNumber): IRNumber = I.IH.mkIRNum(scala.math.ceil(x.getNum))
  def _cos(x: IRNumber): IRNumber = I.IH.mkIRNum(scala.math.cos(x.getNum))
  def _exp(x: IRNumber): IRNumber = I.IH.mkIRNum(scala.math.exp(x.getNum))
  def _floor(x: IRNumber): IRNumber = I.IH.mkIRNum(scala.math.floor(x.getNum))
  def _log(x: IRNumber): IRNumber = I.IH.mkIRNum(scala.math.log(x.getNum))
  def _max(xs: List[IRNumber]): IRNumber = I.IH.mkIRNum(
    xs.foldRight(xs.get(0).getNum)((x, y) => scala.math.max(x.getNum, y)))
  def _min(xs: List[IRNumber]): IRNumber = I.IH.mkIRNum(
    xs.foldRight(xs.get(0).getNum)((x, y) => scala.math.min(x.getNum, y)))
  def _pow(x: IRNumber, y: IRNumber): IRNumber = I.IH.mkIRNum(scala.math.pow(x.getNum, y.getNum))
  def _random(): IRNumber = I.IH.mkIRNum(scala.math.random)
  def _round(x: IRNumber): IRNumber = I.IH.mkIRNum(scala.math.round(x.getNum))
  def _sin(x: IRNumber): IRNumber = I.IH.mkIRNum(scala.math.sin(x.getNum))
  def _sqrt(x: IRNumber): IRNumber = I.IH.mkIRNum(scala.math.sqrt(x.getNum))
  def _tan(x: IRNumber): IRNumber = I.IH.mkIRNum(scala.math.tan(x.getNum))
}
