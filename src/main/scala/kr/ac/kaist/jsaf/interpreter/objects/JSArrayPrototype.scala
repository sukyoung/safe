/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter.objects

import kr.ac.kaist.jsaf.nodes.{IRUndef, IRNull}
import kr.ac.kaist.jsaf.nodes_util.{EJSCompletionType => CT}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.interpreter.{InterpreterPredefine => IP, _}

class JSArrayPrototype(_I: Interpreter, _proto: JSObject)
  extends JSArray(_I, _proto, "Array", true, propTable) {
  def init(): Unit = {
    /*
     * 15.4.4 Properties of the Array Prototype Object
     */
    // 15.4.5.2 length
    property.put("length", I.IH.mkDataProp(PVal(I.IH.mkIRNum(0)), true, false, false))
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
  override def __callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    method match {
      case I.IS.ArrayPrototypeToString => _toString(argsObj)
      // 15.4.4.3
      case I.IS.ArrayPrototypeConcat => _concat(I.IH.arrayToList(argsObj))
      case I.IS.ArrayPrototypeJoin => _join(argsObj._get("0"))
      case I.IS.ArrayPrototypePop => _pop()
      case I.IS.ArrayPrototypePush => _push(I.IH.arrayToList(argsObj))
      case I.IS.ArrayPrototypeReverse => _reverse()
      // 15.4.4.9
      case I.IS.ArrayPrototypeSlice => _slice(argsObj._get("0"), argsObj._get("1"))
      case I.IS.ArrayPrototypeSort => _sort(argsObj._get("0"))
      case I.IS.ArrayPrototypeSplice => _splice(argsObj._get("0"), argsObj._get("1"), I.IH.arrayToList(argsObj).drop(2))
      // 15.4.4.13 - 15.4.4.22
    }
  }

  def _toString(argsObj: JSObject): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case array: JSObject =>
        val func: JSFunction = array._get("join") match {
          // func is callable only if it is an object.
          case o:JSFunction if I.IH.isCallable(o) => o
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

  def _concat(args: List[Val]): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSObject =>
        val a = I.IS.ArrayConstructor.construct(Nil)
        var n: Long = 0
        for (e <- args) {
          e match {
            case array: JSArray =>
              val len: Long = I.IH.toUint32(array._get("length"))
              for (k <- 0L until len) {
                val p = k.toString
                if (array._hasProperty(p)) {
                  a._defineOwnProperty(n.toString,
                                       I.IH.mkDataProp(array._get(p), true, true, true),
                                       false)
                }
                n += 1
              }
            case _ =>
              a._defineOwnProperty(n.toString,
                                   I.IH.mkDataProp(e, true, true, true), false)
              n += 1
          }
        }
        I.IS.comp.setReturn(a)
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def _join(separator: Val): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSObject =>
        val len = I.IH.toUint32(o._get("length"))
        val sep = separator match {
          case IP.undefV => ","
          case _ => I.IH.toString(separator)
        }
        val ts = (x: Val) => {
          if (I.IH.isUndef(x) || I.IH.isNull(x)) ""
          else I.IH.toString(x)
        }
        len match {
          case 0 => I.IS.comp.setReturn(PVal(I.IH.mkIRStr("")))
          case _ =>
            val str = I.IH.arrayToList(o).foldRight("")(
              (x: Val, y: String) => sep+ts(x)+y
            )
            I.IS.comp.setReturn(PVal(I.IH.mkIRStr(str.substring(sep.length))))
        }
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def _pop(): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSObject =>
        val len = I.IH.toUint32(o._get("length"))
        len match {
          case 0 =>
            o._put("length", PVal(I.IH.mkIRNum(0)), true)
            I.IS.comp.setReturn(IP.undefV)
          case _ =>
            val indx = (len - 1).toString
            val element = o._get(indx)
            o._delete(indx, true)
            o._put("length", PVal(I.IH.mkIRStr(indx)), true)
            I.IS.comp.setReturn(element)
        }
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def _push(args: List[Val]): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSObject=>
        var n = I.IH.toUint32(o._get("length"))
        for (e <- args) {
          o._put(n.toString, e, true)
          n += 1
        }
        o._put("length", PVal(I.IH.mkIRNum(n)), true)
        I.IS.comp.setReturn(PVal(I.IH.mkIRNum(n)))
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def _reverse(): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSObject =>
        val len: Long = I.IH.toUint32(o._get("length"))
        val middle: Long = scala.math.floor(len / 2).toLong
        var lower: Long = 0
        while (lower != middle) {
          val upper: Long = len - lower - 1
          val (upperP, lowerP) = (upper.toString, lower.toString)
          val (upperValue, lowerValue) = (o._get(upperP), o._get(lowerP))
          val (upperExists, lowerExists) = (o._hasProperty(upperP), o._hasProperty(lowerP))
          if (lowerExists && upperExists) {
            o._put(lowerP, upperValue, true)
            o._put(upperP, lowerValue, true)
          } else if (!lowerExists && upperExists) {
            o._put(lowerP, upperValue, true)
            o._delete(upperP, true)
          } else if (lowerExists && !upperExists) {
            o._delete(lowerP, true)
            o._put(upperP, lowerValue, true)
          }
          lower += 1
        }
        I.IS.comp.setReturn(o)
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def _slice(start: Val, end: Val): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSObject =>
        val a = I.IS.ArrayConstructor.construct(Nil)
        val len = I.IH.toUint32(o._get("length"))
        var k: Long = I.IH.toInteger(start).getNum.toLong match {
          case relativeStart if relativeStart < 0 => (len + relativeStart) max 0
          case relativeStart => relativeStart min len
        }
        val fin: Long = (if (I.IH.isUndef(end)) len else I.IH.toInteger(end).getNum.toLong) match {
          case relativeEnd if relativeEnd < 0 => (len + relativeEnd) max 0
          case relativeEnd => relativeEnd min len
        }
        var n: Long = 0
        while (k < fin) {
          val pk = k.toString
          if (o._hasProperty(pk)) {
            val kValue = o._get(pk)
            a._defineOwnProperty(n.toString, I.IH.mkDataProp(kValue, true, true, true), false)
          }
          k += 1
          n += 1
        }
        I.IS.comp.setReturn(a)
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def _sort(comparefn: Val): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSArray =>
        val len = I.IH.toUint32(o._get("length"))
        for (i <- 0L until len) {
          if (o.__isAccessorProp(i.toString) ||
              o.__isDataProp(i.toString) && !o.__isWritable(i.toString)) {
            I.IS.comp.setReturn(o)
            return
          }
        }
        if (o.__isSparse()) {
          if (!o.extensible) {
            I.IS.comp.setReturn(o)
            return
          }
          for (i <- 0L until len) {
            if (o.__isDataProp(i.toString) && !o.__isConfigurable(i.toString)) {
              I.IS.comp.setReturn(o)
              return
            }
          }
          if (o.proto != IP.nullObj) {
            val proto = o.proto
            for (i <- 0L until len) {
              if (proto._hasProperty(i.toString))
                o._defineOwnProperty(i.toString,
                                     I.IH.mkDataProp(o._get(i.toString), true, true, true),
                                     false)
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
                    comparefn.asInstanceOf[JSFunction]._call(IP.undefV, argsObj)
                    I.IS.env = oldEnv
                    I.IS.tb = oldTb
                    if(I.IS.comp.Type == CT.RETURN) I.IH.toNumber(I.IS.comp.value).getNum < 0
                    else if(I.IS.comp.Type == CT.THROW) throw new ThrowException(I.IS.comp.error) // TODO: !
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
          if (o._hasProperty(i.toString)) {
            ov match {
              case Some(v) => o._put(i.toString, v, false)
              case None => o._delete(i.toString, false)
            }
          } else {
            ov match {
              case Some(v) => o._defineOwnProperty(i.toString,
                                                   I.IH.mkDataProp(v, true, true, true),
                                                   false)
              case None =>
            }
          }
        }
        I.IS.comp.setReturn(o)
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }

  def _splice(start: Val, deleteCount: Val, items: List[Val]): Unit = {
    I.IH.toObject(I.IS.tb) match {
      case o: JSObject =>
        val a = I.IS.ArrayConstructor.construct(Nil)
        val len = I.IH.toUint32(o._get("length"))
        val actualStart = I.IH.toInteger(start).getNum.toLong match {
          case relativeStart if relativeStart < 0 => (len + relativeStart) max 0
          case relativeStart => relativeStart min len
        }
        val actualDeleteCount = (I.IH.toInteger(deleteCount).getNum.toLong max 0) min (len - actualStart)
        var k: Long = 0
        while (k < actualDeleteCount) {
          val from = (actualStart + k).toString
          val fromPresent = o._hasProperty(from)
          if (fromPresent) {
            val fromValue = o._get(from)
            a._defineOwnProperty(k.toString, I.IH.mkDataProp(fromValue, true, true, true), false)
          }
          k += 1
        }
        val itemCount = items.length
        if (itemCount < actualDeleteCount) {
          k = actualStart
          while (k < len - actualDeleteCount) {
            val from = (k + actualDeleteCount).toString
            val to = (k + itemCount).toString
            val fromPresent = o._hasProperty(from)
            if (fromPresent) {
              val fromValue = o._get(from)
              o._put(to, fromValue, true)
            } else {
              o._delete(to, true)
            }
            k += 1
          }
          k = len
          while (k > len - actualDeleteCount + itemCount) {
            o._delete((k - 1).toString, true)
            k -= 1
          }
        } else if (itemCount > actualDeleteCount) {
          k = len - actualDeleteCount
          while (k > actualStart) {
            val from = (k + actualDeleteCount - 1).toString
            val to = (k + itemCount - 1).toString
            val fromPresent = o._hasProperty(from)
            if (fromPresent) {
              val fromValue = o._get(from)
              o._put(to, fromValue, true)
            } else {
              o._delete(to, true)
            }
            k -= 1
          }
        }
        k = actualStart
        for (e <- items) {
          o._put(k.toString, e, true)
          k += 1
        }
        o._put("length", PVal(I.IH.mkIRNum(len - actualDeleteCount + itemCount)), true)
        I.IS.comp.setReturn(a)
      case err:JSError => I.IS.comp.setThrow(err, I.IS.span)
    }
  }
}
