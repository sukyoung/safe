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

class JSDatePrototype(_I: InterpreterMain, _proto: JSObject)
    extends JSDate(_I, _proto, "Date", true, propTable) {

  val DH: JSDateHelper = new JSDateHelper(I.IH)

  def init(): Unit = {
    /*
     * 15.9.5 Properties of the Date Prototype Object
     * [[Writable]]: false, [[Enumerable]]: false, [[Configurable]]: false
     */
    property.put(IP.pvpn, I.IH.mkDataProp(PVal(IRVal(IP.NaN))))
    property.put("constructor", I.IH.objProp(I.IS.DateConstructor))
    property.put("toString", I.IH.objProp(I.IS.DatePrototypeToString))
    // 15.9.5.3 - 15.9.5.7
    property.put("valueOf", I.IH.objProp(I.IS.DatePrototypeValueOf))
    property.put("getTime", I.IH.objProp(I.IS.DatePrototypeGetTime))
    property.put("getFullYear", I.IH.objProp(I.IS.DatePrototypeGetFullYear))
    property.put("getUTCFullYear", I.IH.objProp(I.IS.DatePrototypeGetUTCFullYear))
    property.put("getMonth", I.IH.objProp(I.IS.DatePrototypeGetMonth))
    property.put("getUTCMonth", I.IH.objProp(I.IS.DatePrototypeGetUTCMonth))
    property.put("getDate", I.IH.objProp(I.IS.DatePrototypeGetDate))
    property.put("getUTCDate", I.IH.objProp(I.IS.DatePrototypeGetUTCDate))
    property.put("getDay", I.IH.objProp(I.IS.DatePrototypeGetDay))
    property.put("getUTCDay", I.IH.objProp(I.IS.DatePrototypeGetUTCDay))
    property.put("getHours", I.IH.objProp(I.IS.DatePrototypeGetHours))
    property.put("getUTCHours", I.IH.objProp(I.IS.DatePrototypeGetUTCHours))
    property.put("getMinutes", I.IH.objProp(I.IS.DatePrototypeGetMinutes))
    property.put("getUTCMinutes", I.IH.objProp(I.IS.DatePrototypeGetUTCMinutes))
    property.put("getSeconds", I.IH.objProp(I.IS.DatePrototypeGetSeconds))
    property.put("getUTCSeconds", I.IH.objProp(I.IS.DatePrototypeGetUTCSeconds))
    property.put("getMilliseconds", I.IH.objProp(I.IS.DatePrototypeGetMilliseconds))
    property.put("getUTCMilliseconds", I.IH.objProp(I.IS.DatePrototypeGetUTCMilliseconds))
    property.put("getTimezoneOffset", I.IH.objProp(I.IS.DatePrototypeGetTimezoneOffset))
    property.put("setTime", I.IH.objProp(I.IS.DatePrototypeSetTime))
    // 15.9.5.28 - 15.9.5.42
    property.put("toISOString", I.IH.objProp(I.IS.DatePrototypeToISOString))
    // 15.9.5.44
  }

  override def __callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    I.IS.tb match {
      case tb: JSDate => method match {
        case I.IS.DatePrototypeToString => _toString()
        // 15.9.5.3 - 15.9.5.7
        case I.IS.DatePrototypeValueOf => _valueOf()
        case I.IS.DatePrototypeGetTime => _getTime()
        case I.IS.DatePrototypeGetFullYear => _getFullYear()
        case I.IS.DatePrototypeGetUTCFullYear => _getUTCFullYear()
        case I.IS.DatePrototypeGetMonth => _getMonth()
        case I.IS.DatePrototypeGetUTCMonth => _getUTCMonth()
        case I.IS.DatePrototypeGetDate => _getDate()
        case I.IS.DatePrototypeGetUTCDate => _getUTCDate()
        case I.IS.DatePrototypeGetDay => _getDay()
        case I.IS.DatePrototypeGetUTCDay => _getUTCDay()
        case I.IS.DatePrototypeGetHours => _getHours()
        case I.IS.DatePrototypeGetUTCHours => _getUTCHours()
        case I.IS.DatePrototypeGetMinutes => _getMinutes()
        case I.IS.DatePrototypeGetUTCMinutes => _getUTCMinutes()
        case I.IS.DatePrototypeGetSeconds => _getSeconds()
        case I.IS.DatePrototypeGetUTCSeconds => _getUTCSeconds()
        case I.IS.DatePrototypeGetMilliseconds => _getMilliseconds()
        case I.IS.DatePrototypeGetUTCMilliseconds => _getUTCMilliseconds()
        case I.IS.DatePrototypeGetTimezoneOffset => _getTimezoneOffset()
        case I.IS.DatePrototypeSetTime => _setTime(argsObj._get("0"))
        // 15.9.5.28 - 15.9.5.42
        case I.IS.DatePrototypeToISOString => _toString()
        // 15.9.5.44
      }
      case _ => I.IS.comp.setThrow(IP.typeError, I.IS.span)
    }
  }

  def __toISOString(t: EJSNumber): Unit = {
    val date = "%02d-%02d-%02d".format(
      DH._yearFromTime(t).num.toInt,
      DH._monthFromTime(t).num.toInt + 1,
      DH._dateFromTime(t).num.toInt
    )
    val time = "T%02d:%02d:%02d.%03d".format(
      DH._hourFromTime(t).num.toInt,
      DH._minFromTime(t).num.toInt,
      DH._secFromTime(t).num.toInt,
      DH._msFromTime(t).num.toInt
    )
    val timezone = "Z"
    I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR(date + time + timezone)))
  }
  def _toString(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => __toISOString(t)
      case _ => I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR("NaN")))
    }
  // 15.9.5.3 - 15.9.5.7
  def _valueOf(): Unit =
    I.IS.comp.setReturn(I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn))
  def _getTime(): Unit =
    I.IS.comp.setReturn(I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn))
  def _getFullYear(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH._yearFromTime(DH._localTime(t)))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _getUTCFullYear(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH._yearFromTime(t))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _getMonth(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH._monthFromTime(DH._localTime(t)))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _getUTCMonth(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH._monthFromTime(t))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _getDate(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH._dateFromTime(DH._localTime(t)))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _getUTCDate(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH._dateFromTime(t))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _getDay(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH._weekDay(DH._localTime(t)))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _getUTCDay(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH._weekDay(t))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _getHours(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH._hourFromTime(DH._localTime(t)))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _getUTCHours(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH._hourFromTime(t))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _getMinutes(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH._minFromTime(DH._localTime(t)))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _getUTCMinutes(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH._minFromTime(t))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _getSeconds(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH._secFromTime(DH._localTime(t)))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _getUTCSeconds(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH._secFromTime(t))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _getMilliseconds(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH._msFromTime(DH._localTime(t)))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _getUTCMilliseconds(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH._msFromTime(t))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _getTimezoneOffset(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(I.IH.mkIRNum(t.num - DH._localTime(t).num))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def _setTime(time: Val): Unit = {
    val v = PVal(IRVal(DH._timeClip(I.IH.toNumber(time))))
    I.IS.tb.asInstanceOf[JSDate].__putProp(IP.pvpn, I.IH.mkDataProp(v))
    I.IS.comp.setReturn(v)
  }
  def _toISOString(): Unit =
    I.IS.tb.asInstanceOf[JSDate]._get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => __toISOString(t)
      case _ => I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR("NaN")))
    }
}
