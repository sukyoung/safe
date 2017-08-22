/**
 * *****************************************************************************
 * Copyright (c) 2016-2017, KAIST.
 * All rights reserved.
 *
 * Use is subject to license terms.
 *
 * This distribution may include materials developed by third parties.
 * ****************************************************************************
 */

package kr.ac.kaist.safe.interpreter.objects

import kr.ac.kaist.safe.interpreter._
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util._

class JSMath(I: Interpreter, proto: JSObject)
    extends JSObject(I, proto, "Math", false, propTable) {
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
      case I.IS.MathAbs => JSAbs(args.head)
      case I.IS.MathAcos => JSAcos(args.head)
      case I.IS.MathAsin => JSAsin(args.head)
      case I.IS.MathAtan => JSAtan(args.head)
      case I.IS.MathAtan2 => JSAtan2(args.head, args(1))
      case I.IS.MathCeil => JSCeil(args.head)
      case I.IS.MathCos => JSCos(args.head)
      case I.IS.MathExp => JSExp(args.head)
      case I.IS.MathFloor => JSFloor(args.head)
      case I.IS.MathLog => JSLog(args.head)
      case I.IS.MathMax => JSMax(args)
      case I.IS.MathMin => JSMin(args)
      case I.IS.MathPow => JSMow(args.head, args(1))
      case I.IS.MathRandom => JSRandom()
      case I.IS.MathRound => JSRound(args.head)
      case I.IS.MathSin => JSSin(args.head)
      case I.IS.MathSqrt => JSSqrt(args.head)
      case I.IS.MathTan => JSTan(args.head)
    }
    I.IS.comp.setReturn(PVal(IRVal(y)))
  }

  /*
   * 15.8.2 Function Properties of the Math Object
   */
  def JSAbs(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.abs(x.num))
  def JSAcos(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.acos(x.num))
  def JSAsin(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.asin(x.num))
  def JSAtan(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.atan(x.num))
  def JSAtan2(y: EJSNumber, x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.atan2(y.num, x.num))
  def JSCeil(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.ceil(x.num))
  def JSCos(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.cos(x.num))
  def JSExp(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.exp(x.num))
  def JSFloor(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.floor(x.num))
  def JSLog(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.log(x.num))
  def JSMax(xs: List[EJSNumber]): EJSNumber = I.IH.mkIRNum(
    xs.foldRight(xs.head.num)((x, y) => scala.math.max(x.num, y))
  )
  def JSMin(xs: List[EJSNumber]): EJSNumber = I.IH.mkIRNum(
    xs.foldRight(xs.head.num)((x, y) => scala.math.min(x.num, y))
  )
  def JSMow(x: EJSNumber, y: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.pow(x.num, y.num))
  def JSRandom(): EJSNumber = I.IH.mkIRNum(scala.math.random)
  def JSRound(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.round(x.num))
  def JSSin(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.sin(x.num))
  def JSSqrt(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.sqrt(x.num))
  def JSTan(x: EJSNumber): EJSNumber = I.IH.mkIRNum(scala.math.tan(x.num))
}
