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
import kr.ac.kaist.safe.util._

class JSDatePrototype(I: Interpreter, proto: JSObject)
    extends JSDate(I, proto, "Date", true, propTable) {

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

  override def callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    I.IS.tb match {
      case tb: JSDate => method match {
        case I.IS.DatePrototypeToString => DPtoString()
        // 15.9.5.3 - 15.9.5.7
        case I.IS.DatePrototypeValueOf => valueOf()
        case I.IS.DatePrototypeGetTime => getTime()
        case I.IS.DatePrototypeGetFullYear => getFullYear()
        case I.IS.DatePrototypeGetUTCFullYear => getUTCFullYear()
        case I.IS.DatePrototypeGetMonth => getMonth()
        case I.IS.DatePrototypeGetUTCMonth => getUTCMonth()
        case I.IS.DatePrototypeGetDate => getDate()
        case I.IS.DatePrototypeGetUTCDate => getUTCDate()
        case I.IS.DatePrototypeGetDay => getDay()
        case I.IS.DatePrototypeGetUTCDay => getUTCDay()
        case I.IS.DatePrototypeGetHours => getHours()
        case I.IS.DatePrototypeGetUTCHours => getUTCHours()
        case I.IS.DatePrototypeGetMinutes => getMinutes()
        case I.IS.DatePrototypeGetUTCMinutes => getUTCMinutes()
        case I.IS.DatePrototypeGetSeconds => getSeconds()
        case I.IS.DatePrototypeGetUTCSeconds => getUTCSeconds()
        case I.IS.DatePrototypeGetMilliseconds => getMilliseconds()
        case I.IS.DatePrototypeGetUTCMilliseconds => getUTCMilliseconds()
        case I.IS.DatePrototypeGetTimezoneOffset => getTimezoneOffset()
        case I.IS.DatePrototypeSetTime => setTime(argsObj.get("0"))
        // 15.9.5.28 - 15.9.5.42
        case I.IS.DatePrototypeToISOString => DPtoString()
        // 15.9.5.44
      }
      case _ => I.IS.comp.setThrow(IP.typeError, I.IS.span)
    }
  }

  def toISOString(t: EJSNumber): Unit = {
    val date = "%02d-%02d-%02d".format(
      DH.yearFromTime(t).num.toInt,
      DH.monthFromTime(t).num.toInt + 1,
      DH.dateFromTime(t).num.toInt
    )
    val time = "T%02d:%02d:%02d.%03d".format(
      DH.hourFromTime(t).num.toInt,
      DH.minFromTime(t).num.toInt,
      DH.secFromTime(t).num.toInt,
      DH.msFromTime(t).num.toInt
    )
    val timezone = "Z"
    I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR(date + time + timezone)))
  }
  def DPtoString(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => toISOString(t)
      case _ => I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR("NaN")))
    }
  // 15.9.5.3 - 15.9.5.7
  def valueOf(): Unit =
    I.IS.comp.setReturn(I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn))
  def getTime(): Unit =
    I.IS.comp.setReturn(I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn))
  def getFullYear(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH.yearFromTime(DH.localTime(t)))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def getUTCFullYear(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH.yearFromTime(t))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def getMonth(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH.monthFromTime(DH.localTime(t)))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def getUTCMonth(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH.monthFromTime(t))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def getDate(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH.dateFromTime(DH.localTime(t)))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def getUTCDate(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH.dateFromTime(t))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def getDay(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH.weekDay(DH.localTime(t)))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def getUTCDay(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH.weekDay(t))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def getHours(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH.hourFromTime(DH.localTime(t)))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def getUTCHours(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH.hourFromTime(t))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def getMinutes(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH.minFromTime(DH.localTime(t)))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def getUTCMinutes(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH.minFromTime(t))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def getSeconds(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH.secFromTime(DH.localTime(t)))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def getUTCSeconds(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH.secFromTime(t))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def getMilliseconds(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH.msFromTime(DH.localTime(t)))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def getUTCMilliseconds(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(DH.msFromTime(t))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def getTimezoneOffset(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => I.IS.comp.setReturn(PVal(IRVal(I.IH.mkIRNum(t.num - DH.localTime(t).num))))
      case _ => I.IS.comp.setReturn(PVal(IRVal(IP.NaN)))
    }
  def setTime(time: Val): Unit = {
    val v = PVal(IRVal(DH.timeClip(I.IH.toNumber(time))))
    I.IS.tb.asInstanceOf[JSDate].putProp(IP.pvpn, I.IH.mkDataProp(v))
    I.IS.comp.setReturn(v)
  }
  def toISOString(): Unit =
    I.IS.tb.asInstanceOf[JSDate].get(IP.pvpn) match {
      case PVal(IRVal(t: EJSNumber)) => toISOString(t)
      case _ => I.IS.comp.setReturn(PVal(I.IH.mkIRStrIR("NaN")))
    }
}
