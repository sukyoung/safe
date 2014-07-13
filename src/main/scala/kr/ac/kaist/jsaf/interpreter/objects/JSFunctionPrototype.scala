/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter.objects

import kr.ac.kaist.jsaf.interpreter._
import kr.ac.kaist.jsaf.interpreter.{InterpreterPredefine => IP, _}

class JSFunctionPrototype(_I: Interpreter, _proto: JSObject)
  extends JSFunction13(_I, _proto, "Function", true, propTable, IP.undefFtn, EmptyEnv()) {
  def init(): Unit = {
    // 15.3.4 Properties of the Function Prototype Object
    property.put("length", I.IH.numProp(0))
    property.put("constructor", I.IH.objProp(I.IS.FunctionConstructor))
    property.put("toString", I.IH.objProp(I.IS.FunctionPrototypeToString))
    property.put("apply", I.IH.objProp(I.IS.FunctionPrototypeApply))
    property.put("call", I.IH.objProp(I.IS.FunctionPrototypeCall))
    /*
    property.put("bind", I.IH.objProp(I.IS.FunctionPrototypeBind))
    */
  }

  ////////////////////////////////////////////////////////////////////////////////
  // Basic
  ////////////////////////////////////////////////////////////////////////////////

  override def __callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    val args: Array[Val] = I.IH.argsObjectToArray(argsObj, 2)
    method match {
      case I.IS.FunctionPrototypeToString => _toString()
      case I.IS.FunctionPrototypeApply => _apply(args(0), args(1))
      case I.IS.FunctionPrototypeCall => _call(args(0), I.IH.arrayToList(argsObj).drop(1))
      //case I.IS.FunctionPrototypeBind => bind(IS, args(0), args(1))
    }
  }

  ////////////////////////////////////////////////////////////////////////////////
  // 15.3.4 Properties of the Function Prototype Object
  ////////////////////////////////////////////////////////////////////////////////

  /* 15.3.4
   * The Function prototype object is itself a Function object (its [[Class]] is "Function") that,
   * when invoked, accepts any arguments and returns undefined.
   */
  override def _call(tb: Val, argsObj: JSObject): Unit =  I.IS.comp.setReturn(IP.undefV)

  // 15.3.4.2 toString()
  def _toString(): Unit = {
    if (!I.IH.isCallable(I.IS.tb)) I.IS.comp.setThrow(IP.typeError, I.IS.span)
    else I.IS.comp.setReturn(PVal(I.IH.mkIRStr("[object Function]")))
  }

  // 15.3.4.3 Function.prototype.apply (thisArg, argArray)
  def _apply(thisArg: Val, argArray: Val): Unit = {
    if (!I.IH.isCallable(I.IS.tb)) I.IS.comp.setThrow(IP.typeError, I.IS.span)
    else {
      val func: JSFunction = I.IS.tb.asInstanceOf[JSFunction]
      if (I.IH.isNull(argArray) || I.IH.isUndef(argArray)) {
        val prop: PropTable = propTable
        prop.put("length", I.IH.numProp(0))

        val a: JSObject = I.IH.newObj(prop)
        val argsList: JSObject = I.IH.newArgObj(func, func.code.getArgs.size(), a, I.IS.strict)

        func._call(thisArg, argsList)
      } else {
        argArray match {
          case args: JSObject =>
            val len: Val = args._get("length")
            val n: Long = I.IH.toUint32(len)
            val prop: PropTable = propTable
            var index: Long = 0
            while (index < n) {
              val indexName: String = index.toString
              val nextArg: Val = args._get(indexName)
              prop.put(indexName, I.IH.mkDataProp(nextArg))
              index += 1
            }
            prop.put("length", I.IH.numProp(index))

            val a: JSObject = I.IH.newObj(prop)
            val argsList: JSObject = I.IH.newArgObj(func, func.code.getArgs.size(), a, I.IS.strict)

            func._call(thisArg, argsList)
          case _ => I.IS.comp.setThrow(IP.typeError, I.IS.span)
        }
      }
    }
  }


  // 15.3.4.4 Function.prototype.call (thisArg [ , arg1 [ , arg2, ... ] ] )
  def _call(thisArg: Val, args: List[Val]): Unit = {
    if (!I.IH.isCallable(I.IS.tb)) I.IS.comp.setThrow(IP.typeError, I.IS.span)
    else {
      val func: JSFunction = I.IS.tb.asInstanceOf[JSFunction]

      val prop: PropTable = propTable
      for ((v, i) <- args.zipWithIndex) {
        prop.put(i.toString, I.IH.mkDataProp(v))
      }
      prop.put("length", I.IH.numProp(args.size))

      val a: JSObject = I.IH.newObj(prop)
      val argsList: JSObject = I.IH.newArgObj(func, func.code.getArgs.size(), a, I.IS.strict)

      func._call(thisArg, argsList)
    }
  }

  /*
  // 15.3.4.5 bind(thisArg, [, arg1 [, arg2, ... ]])
  // TODO:
  def bind(IS: InterpreterState, thisArg: Val, argsObj: Val): Unit = throwErr(nyiError, I.IS.span)
  */
}
