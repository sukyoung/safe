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
import kr.ac.kaist.safe.interpreter.{ InterpreterPredefine => IP }
import kr.ac.kaist.safe.nodes.ir._
import kr.ac.kaist.safe.util.{ EJSCompletionType => CT }

class JSArrayPrototype(I: Interpreter, proto: JSObject)
    extends JSArray(I, proto, "Array", true, propTable) {
  def init(): Unit = {
    /*
     * 15.4.4 Properties of the Array Prototype Object
     */
    // 15.4.5.2 length
    property.put("length", I.IH.mkDataProp(PVal(IRVal(I.IH.mkIRNum(0))), true, false, false))
    property.put("constructor", I.IH.objProp(I.IS.ArrayConstructor))
    property.put("toString", I.IH.objProp(I.IS.ArrayPrototypeToString))
    // 15.4.4.3
    property.put("concat", I.IH.objProp(I.IS.ArrayPrototypeConcat))
    property.put("join", I.IH.objProp(I.IS.ArrayPrototypeJoin))
    property.put("pop", I.IH.objProp(I.IS.ArrayPrototypePop))
    property.put("push", I.IH.objProp(I.IS.ArrayPrototypePush))
    property.put("reverse", I.IH.objProp(I.IS.ArrayPrototypeReverse))
    // 15.4.4.9
    property.put("slice", I.IH.objProp(I.IS.ArrayPrototypeSlice))
    property.put("sort", I.IH.objProp(I.IS.ArrayPrototypeSort))
    property.put("splice", I.IH.objProp(I.IS.ArrayPrototypeSplice))
    // 15.4.4.13 - 15.4.4.22
  }

  /*
   * It is permitted for the this to be an object for which the value of the
   * [[Class]] internal property is not "Array".
   */
  override def callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    method match {
      case I.IS.ArrayPrototypeToString => JSToString(argsObj)
      // 15.4.4.3
      case I.IS.ArrayPrototypeConcat => JSConcat(I.IH.arrayToList(argsObj))
      case I.IS.ArrayPrototypeJoin => JSJoin(argsObj.get("0"))
      case I.IS.ArrayPrototypePop => JSPop()
      case I.IS.ArrayPrototypePush => JSPush(I.IH.arrayToList(argsObj))
      case I.IS.ArrayPrototypeReverse => JSReverse()
      // 15.4.4.9
      case I.IS.ArrayPrototypeSlice => JSSlice(argsObj.get("0"), argsObj.get("1"))
      case I.IS.ArrayPrototypeSort => JSSort(argsObj.get("0"))
      case I.IS.ArrayPrototypeSplice => JSSplice(argsObj.get("0"), argsObj.get("1"), I.IH.arrayToList(argsObj).drop(2))
      // 15.4.4.13 - 15.4.4.22
    }
  }

  def JSToString(argsObj: JSObject): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case array: JSObject =>
        val func: JSFunction = array.get("join") match {
          // func is callable only if it is an object.
          case o: JSFunction if I.IH.isCallable(o) => o
          case _ => I.IS.ObjectPrototypeToString
        }
        val oldEnv = I.IS.env
        val oldTb = I.IS.tb
        I.IH.call(I.IS.info, I.IS.tb, argsObj, func)
        I.IS.env = oldEnv
        I.IS.tb = oldTb
      case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def JSConcat(args: List[Val]): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSObject =>
        val a = I.IS.ArrayConstructor.construct(Nil)
        var n: Long = 0
        for (e <- args) {
          e match {
            case array: JSArray =>
              val len: Long = I.IH.toUint32(array.get("length"))
              for (k <- 0L until len) {
                val p = k.toString
                if (array.hasProperty(p)) {
                  a.defineOwnProperty(
                    n.toString,
                    I.IH.mkDataProp(array.get(p), true, true, true),
                    false
                  )
                }
                n += 1
              }
            case _ =>
              a.defineOwnProperty(
                n.toString,
                I.IH.mkDataProp(e, true, true, true), false
              )
              n += 1
          }
        }
        I.IS.comp.setReturn(a)
      case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def JSJoin(separator: Val): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSObject =>
        val len = I.IH.toUint32(o.get("length"))
        val sep = separator match {
          case IP.undefV => ","
          case _ => I.IH.toString(separator)
        }
        val ts = (x: Val) => {
          if (I.IH.isUndef(x) || I.IH.isNull(x)) ""
          else I.IH.toString(x)
        }
        len match {
          case 0 => I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR("")))
          case _ =>
            val str = I.IH.arrayToList(o).foldRight("")(
              (x: Val, y: String) => sep + ts(x) + y
            )
            I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR(str.substring(sep.length))))
        }
      case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def JSPop(): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSObject =>
        val len = I.IH.toUint32(o.get("length"))
        len match {
          case 0 =>
            o.put("length", PVal(IRVal(I.IH.mkIRNum(0))), true)
            I.IS.comp.setReturn(IP.undefV)
          case _ =>
            val indx = (len - 1).toString
            val element = o.get(indx)
            o.delete(indx, true)
            o.put("length", PVal(I.IH.mkIRStrIR(indx)), true)
            I.IS.comp.setReturn(element)
        }
      case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def JSPush(args: List[Val]): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSObject =>
        var n = I.IH.toUint32(o.get("length"))
        for (e <- args) {
          o.put(n.toString, e, true)
          n += 1
        }
        o.put("length", PVal(IRVal(I.IH.mkIRNum(n))), true)
        I.IS.comp.setReturn(PVal(IRVal(I.IH.mkIRNum(n))))
      case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def JSReverse(): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSObject =>
        val len: Long = I.IH.toUint32(o.get("length"))
        val middle: Long = scala.math.floor(len / 2).toLong
        var lower: Long = 0
        while (lower != middle) {
          val upper: Long = len - lower - 1
          val (upperP, lowerP) = (upper.toString, lower.toString)
          val (upperValue, lowerValue) = (o.get(upperP), o.get(lowerP))
          val (upperExists, lowerExists) = (o.hasProperty(upperP), o.hasProperty(lowerP))
          if (lowerExists && upperExists) {
            o.put(lowerP, upperValue, true)
            o.put(upperP, lowerValue, true)
          } else if (!lowerExists && upperExists) {
            o.put(lowerP, upperValue, true)
            o.delete(upperP, true)
          } else if (lowerExists && !upperExists) {
            o.delete(lowerP, true)
            o.put(upperP, lowerValue, true)
          }
          lower += 1
        }
        I.IS.comp.setReturn(o)
      case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def JSSlice(start: Val, end: Val): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSObject =>
        val a = I.IS.ArrayConstructor.construct(Nil)
        val len = I.IH.toUint32(o.get("length"))
        var k: Long = I.IH.toInteger(start).num.toLong match {
          case relativeStart if relativeStart < 0 => (len + relativeStart) max 0
          case relativeStart => relativeStart min len
        }
        val fin: Long = (if (I.IH.isUndef(end)) len else I.IH.toInteger(end).num.toLong) match {
          case relativeEnd if relativeEnd < 0 => (len + relativeEnd) max 0
          case relativeEnd => relativeEnd min len
        }
        var n: Long = 0
        while (k < fin) {
          val pk = k.toString
          if (o.hasProperty(pk)) {
            val kValue = o.get(pk)
            a.defineOwnProperty(n.toString, I.IH.mkDataProp(kValue, true, true, true), false)
          }
          k += 1
          n += 1
        }
        I.IS.comp.setReturn(a)
      case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def JSSort(comparefn: Val): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSArray =>
        val len = I.IH.toUint32(o.get("length"))
        for (i <- 0L until len) {
          if (o.isAccessorProp(i.toString) ||
            o.isDataProp(i.toString) && !o.isWritable(i.toString)) {
            I.IS.comp.setReturn(o)
            return
          }
        }
        if (o.isSparse) {
          if (!o.extensible) {
            I.IS.comp.setReturn(o)
            return
          }
          for (i <- 0L until len) {
            if (o.isDataProp(i.toString) && !o.isConfigurable(i.toString)) {
              I.IS.comp.setReturn(o)
              return
            }
          }
          if (o.proto != IP.nullObj) {
            val proto = o.proto
            for (i <- 0L until len) {
              if (proto.hasProperty(i.toString))
                o.defineOwnProperty(
                  i.toString,
                  I.IH.mkDataProp(o.get(i.toString), true, true, true),
                  false
                )
            }
          }
        }
        def sortCompare(j: Option[Val], k: Option[Val]): Boolean = {
          j match {
            case None => false
            case Some(x) => k match {
              case None => true
              case Some(y) =>
                if (I.IH.isUndef(x)) false
                else if (I.IH.isUndef(y)) true
                else if (!I.IH.isUndef(comparefn)) {
                  if (!I.IH.isCallable(comparefn))
                    throw new ThrowException(IP.typeError)
                  else {
                    val prop = propTable
                    prop.put("0", I.IH.mkDataProp(x, true, true, true))
                    prop.put("1", I.IH.mkDataProp(y, true, true, true))
                    prop.put("length", I.IH.numProp(2))
                    val a = I.IH.newObj(prop)
                    val argsObj = I.IH.newArgObj(comparefn.asInstanceOf[JSFunction], 2, a, I.IS.strict)

                    val oldEnv = I.IS.env
                    val oldTb = I.IS.tb
                    comparefn.asInstanceOf[JSFunction].call(IP.undefV, argsObj)
                    I.IS.env = oldEnv
                    I.IS.tb = oldTb
                    if (I.IS.comp.Type == CT.RETURN) I.IH.toNumber(I.IS.comp.value).num < 0
                    else if (I.IS.comp.Type == CT.THROW) throw new ThrowException(I.IS.comp.error) // TODO: !
                    else throw new InterpreterError("Array.prototype.sortCompare: ", I.IS.span)
                  }
                } else {
                  val (xString, yString) = (I.IH.toString(x), I.IH.toString(y))
                  xString < yString
                }
            }
          }
        }
        val list: List[Option[Val]] = I.IH.arrayToOptionList(o)
        for ((ov, i) <- list.sortWith(sortCompare).zipWithIndex) {
          if (o.hasProperty(i.toString)) {
            ov match {
              case Some(v) => o.put(i.toString, v, false)
              case None => o.delete(i.toString, false)
            }
          } else {
            ov match {
              case Some(v) => o.defineOwnProperty(
                i.toString,
                I.IH.mkDataProp(v, true, true, true),
                false
              )
              case None =>
            }
          }
        }
        I.IS.comp.setReturn(o)
      case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def JSSplice(start: Val, deleteCount: Val, items: List[Val]): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSObject =>
        val a = I.IS.ArrayConstructor.construct(Nil)
        val len = I.IH.toUint32(o.get("length"))
        val actualStart = I.IH.toInteger(start).num.toLong match {
          case relativeStart if relativeStart < 0 => (len + relativeStart) max 0
          case relativeStart => relativeStart min len
        }
        val actualDeleteCount = (I.IH.toInteger(deleteCount).num.toLong max 0) min (len - actualStart)
        var k: Long = 0
        while (k < actualDeleteCount) {
          val from = (actualStart + k).toString
          val fromPresent = o.hasProperty(from)
          if (fromPresent) {
            val fromValue = o.get(from)
            a.defineOwnProperty(k.toString, I.IH.mkDataProp(fromValue, true, true, true), false)
          }
          k += 1
        }
        val itemCount = items.length
        if (itemCount < actualDeleteCount) {
          k = actualStart
          while (k < len - actualDeleteCount) {
            val from = (k + actualDeleteCount).toString
            val to = (k + itemCount).toString
            val fromPresent = o.hasProperty(from)
            if (fromPresent) {
              val fromValue = o.get(from)
              o.put(to, fromValue, true)
            } else {
              o.delete(to, true)
            }
            k += 1
          }
          k = len
          while (k > len - actualDeleteCount + itemCount) {
            o.delete((k - 1).toString, true)
            k -= 1
          }
        } else if (itemCount > actualDeleteCount) {
          k = len - actualDeleteCount
          while (k > actualStart) {
            val from = (k + actualDeleteCount - 1).toString
            val to = (k + itemCount - 1).toString
            val fromPresent = o.hasProperty(from)
            if (fromPresent) {
              val fromValue = o.get(from)
              o.put(to, fromValue, true)
            } else {
              o.delete(to, true)
            }
            k -= 1
          }
        }
        k = actualStart
        for (e <- items) {
          o.put(k.toString, e, true)
          k += 1
        }
        o.put("length", PVal(IRVal(I.IH.mkIRNum(len - actualDeleteCount + itemCount))), true)
        I.IS.comp.setReturn(a)
      case err: JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }
}
