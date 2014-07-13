/*******************************************************************************
    Copyright (c) 2012-2013, KAIST, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

package kr.ac.kaist.jsaf.interpreter.objects

import kr.ac.kaist.jsaf.nodes.{IRString, IRNumber}
import kr.ac.kaist.jsaf.scala_src.nodes._
import kr.ac.kaist.jsaf.scala_src.useful.Lists._
import kr.ac.kaist.jsaf.scala_src.useful.Options._
import kr.ac.kaist.jsaf.interpreter.{InterpreterPredefine => IP, _}

class JSDateConstructor(_I: Interpreter, _proto: JSObject)
  extends JSFunction13(_I, _proto, "Function", true, propTable, _I.IH.dummyFtn(7), EmptyEnv(), true) {

  val DH: JSDateHelper = new JSDateHelper(I.IH)

  def init(): Unit = {
    /*
     * 15.9.4 Properties of the Date Constructor
     * [[Writable]]: false, [[Enumerable]]: false, [[Configurable]]: false
     */
    property.put("prototype", I.IH.mkDataProp(I.IS.DatePrototype))
    property.put("parse", I.IH.objProp(I.IS.DateParse))
    property.put("UTC", I.IH.objProp(I.IS.DateUTC))
    property.put("now", I.IH.objProp(I.IS.DateNow))
  }

  /*
   * 15.9.3 The Date Constructor
   * 15.9.3.1 new Date(year, month[, date[, hours[, minutes[, seconds[, ms]]]]])
   */
  def construct(year: Val, month: Val, date: Val,
            hours: Val, minutes: Val, seconds: Val, ms: Val): JSDate = {
    val (y, m) = (I.IH.toNumber(year), I.IH.toNumber(month))
    val dt = if (!I.IH.isUndef(date)) I.IH.toNumber(date) else I.IH.mkIRNum(1)
    val h = if (!I.IH.isUndef(hours)) I.IH.toNumber(hours) else I.IH.mkIRNum(0)
    val min = if (!I.IH.isUndef(minutes)) I.IH.toNumber(minutes) else I.IH.mkIRNum(0)
    val s = if (!I.IH.isUndef(seconds)) I.IH.toNumber(seconds) else I.IH.mkIRNum(0)
    val milli = if (!I.IH.isUndef(ms)) I.IH.toNumber(ms) else I.IH.mkIRNum(0)
    val yi = I.IH.toInteger(PVal(y)).getNum
    val yr = if (!I.IH.isNaN(y) && 0 <= yi && yi <= 99) I.IH.mkIRNum(1900 + yi) else y
    val finalDate = DH._makeDate(DH._makeDay(yr, m, dt), DH._makeTime(h, min, s, milli))
    new JSDate(I, I.IS.DatePrototype, "Date", true, I.IH.numPropTable(finalDate.getNum))
  }
  /*
   * 15.9.3.2 new Date(value)
   */
  def construct(value: Val): JSDate = {
    val V = I.IH.toPrimitive(value, "Number") match {
      case v@PVal(_:IRString) => __parse(v)
      case v => I.IH.toNumber(v)
    }
    new JSDate(I, I.IS.DatePrototype, "Date", true, I.IH.numPropTable(DH._timeClip(V).getNum))
  }
  /*
   * 15.9.3.3 new Date()
   */
  def construct(): JSDate =
    new JSDate(I, I.IS.DatePrototype, "Date", true, I.IH.numPropTable(__now.getNum))

  override def _construct(argsObj: JSObject): JSDate = {
    argsObj._get("length") match {
      case PVal(n:IRNumber) if n.getNum == 0 => construct()
      case PVal(n:IRNumber) if n.getNum == 1 => construct(argsObj._get("0"))
      case PVal(n:IRNumber) if n.getNum >= 2 => construct(argsObj._get("0"),
                                                          argsObj._get("1"),
                                                          argsObj._get("2"),
                                                          argsObj._get("3"),
                                                          argsObj._get("4"),
                                                          argsObj._get("5"),
                                                          argsObj._get("6"))
    }
  }

  override def __callBuiltinFunction(method: JSFunction, argsObj: JSObject): Unit = {
    method match {
      case I.IS.DateParse => _parse(argsObj._get("0"))
      case I.IS.DateUTC => _utc(argsObj._get("0"),
                                argsObj._get("1"),
                                argsObj._get("2"),
                                argsObj._get("3"),
                                argsObj._get("4"),
                                argsObj._get("5"),
                                argsObj._get("6"))
      case I.IS.DateNow => _now()
    }
  }

  override def _call(tb: Val, argsObj: JSObject): Unit = {
    I.IS.DatePrototype.__toISOString(__now)
  }

  /*
   * 15.9.4.2 Date.parse(string)
   */
  def _parse(string: Val): Unit =
    I.IS.comp.setReturn(PVal(__parse(string)))
  def __parse(string: Val): IRNumber = {
    /*
     * "YYYY-MM-DDTHH:mm:ss.sss"
     * This format includes date-only forms:
     * "YYYY"
     * "YYYY-MM"
     * "YYYY-MM-DD"
     * It also includes date-time forms that consist of one of the above
     * date-only forms immediately followed by one of the following time forms
     * with an optional time zone offset appended:
     * "THH:mm"
     * "THH:mm:ss"
     * "THH:mm:ss.sss"
     * Optional time zone offsets:
     * "Z"
     * "+HH:mm"
     * "-HH:mm"
     * Note: "1989-12-12T09:30.123" is acceptable by the following Regex.
     *       Time zone offset is acceptable only if time is given.
     */
    val format1 = ("""(\d\d\d\d)(?:-(\d\d))?(?:-(\d\d))?"""
                + """(?:T(\d\d)\:(\d\d)(?:\:(\d\d))?(?:\.(\d\d\d))?"""
                + """(?:Z|(?:(\+|-)(\d\d)\:(\d\d)))?)?""").r
    val format2 = ("""(January|February|March|April|May|June|July|August|September|October|November|December)"""
                + """ (\d?\d) (\d+)"""
                + """(?: (\d?\d)\:(\d\d)(?:\:(\d\d)(?:\.(\d\d\d))?)?"""
                + """(?: Z| (\+|-)(\d\d)(\d\d))?)?""").r
    val format3 = ("""(\d?\d)/(\d?\d)/(\d+)"""
                + """(?: (\d?\d)\:(\d\d)(?:\:(\d\d)(?:\.(\d\d\d))?)?"""
                + """(?: Z| (\+|-)(\d\d)(\d\d))?)?""").r
    try {
      val format1(year, month, date, hours, minutes, seconds, ms, tzs, tzh, tzm) =
        I.IH.toString(string)
      val (yr, m, dt) = (year, month, date) match {
        case (yr:String, null, null) =>
          (yr.toDouble, 1.0, 1.0)
        case (yr:String, m:String, null) =>
          (yr.toDouble, m.toDouble, 1.0)
        case (yr:String, m:String, dt:String) =>
          (yr.toDouble, m.toDouble, dt.toDouble)
      }
      val (h, min, s, milli) = (hours, minutes, seconds, ms) match {
        case (null, null, null, null) =>
          (0.0, 0.0, 0.0, 0.0)
        case (h:String, min:String, null, null) =>
          (h.toDouble, min.toDouble, 0.0, 0.0)
        case (h:String, min:String, s:String, null) =>
          (h.toDouble, min.toDouble, s.toDouble, 0.0)
        case (h:String, min:String, s:String, milli:String) =>
          (h.toDouble, min.toDouble, s.toDouble, milli.toDouble)
      }
      val tc = DH._timeClip(
                 DH._makeDate(
                   DH._makeDay(I.IH.mkIRNum(yr),
                               I.IH.mkIRNum(m - 1),
                               I.IH.mkIRNum(dt)),
                   DH._makeTime(I.IH.mkIRNum(h),
                                I.IH.mkIRNum(min),
                                I.IH.mkIRNum(s),
                                I.IH.mkIRNum(milli))))
      val tzoffset = (tzs, tzh, tzm) match {
        case (null, null, null) => I.IH.mkIRNum(0)
        case (s@"+", h:String, m:String) =>
          DH._timeClip(
            DH._makeDate(
              DH._makeDay(I.IH.mkIRNum(DH.__initYear),
                          I.IH.mkIRNum(0.0),
                          I.IH.mkIRNum(1.0)),
              DH._makeTime(I.IH.mkIRNum(h.toDouble),
                           I.IH.mkIRNum(m.toDouble),
                           I.IH.mkIRNum(0.0),
                           I.IH.mkIRNum(0.0))))
        case (s@"-", h:String, m:String) =>
          I.IH.mkIRNum(
            -DH._timeClip(
               DH._makeDate(
                 DH._makeDay(I.IH.mkIRNum(DH.__initYear),
                             I.IH.mkIRNum(0.0),
                             I.IH.mkIRNum(1.0)),
                 DH._makeTime(I.IH.mkIRNum(h.toDouble),
                              I.IH.mkIRNum(m.toDouble),
                              I.IH.mkIRNum(0.0),
                              I.IH.mkIRNum(0.0)))).getNum)
      }
      I.IH.mkIRNum(tc.getNum - tzoffset.getNum)
    } catch {
      case _:MatchError => try {
        val format2(month, date, year, hours, minutes, seconds, ms, tzs, tzh, tzm) =
          I.IH.toString(string)
        val mToDouble = Map("January" -> 1.0, "February" -> 2.0, "March" -> 3.0,
                         "April" -> 4.0, "May" -> 5.0, "June" -> 6.0,
                         "July" -> 7.0, "August" -> 8.0, "September" -> 9.0,
                         "October" -> 10.0, "November" -> 11.0, "December" -> 12.0)
        val (yr, m, dt) = (year, month, date) match {
          case (yr:String, null, null) =>
            (yr.toDouble, 1.0, 1.0)
          case (yr:String, m:String, null) =>
            (yr.toDouble, mToDouble(m), 1.0)
          case (yr:String, m:String, dt:String) =>
            (yr.toDouble, mToDouble(m), dt.toDouble)
        }
        val (h, min, s, milli) = (hours, minutes, seconds, ms) match {
          case (null, null, null, null) =>
            (0.0, 0.0, 0.0, 0.0)
          case (h:String, min:String, null, null) =>
            (h.toDouble, min.toDouble, 0.0, 0.0)
          case (h:String, min:String, s:String, null) =>
            (h.toDouble, min.toDouble, s.toDouble, 0.0)
          case (h:String, min:String, s:String, milli:String) =>
            (h.toDouble, min.toDouble, s.toDouble, milli.toDouble)
        }
        val tc = DH._timeClip(
                   DH._makeDate(
                     DH._makeDay(I.IH.mkIRNum(yr),
                                 I.IH.mkIRNum(m - 1),
                                 I.IH.mkIRNum(dt)),
                     DH._makeTime(I.IH.mkIRNum(h),
                                  I.IH.mkIRNum(min),
                                  I.IH.mkIRNum(s),
                                  I.IH.mkIRNum(milli))))
        val tzoffset = (tzs, tzh, tzm) match {
          case (null, null, null) => I.IH.mkIRNum(0)
          case (s@"+", h:String, m:String) =>
            DH._timeClip(
              DH._makeDate(
                DH._makeDay(I.IH.mkIRNum(DH.__initYear),
                            I.IH.mkIRNum(0.0),
                            I.IH.mkIRNum(1.0)),
                DH._makeTime(I.IH.mkIRNum(h.toDouble),
                             I.IH.mkIRNum(m.toDouble),
                             I.IH.mkIRNum(0.0),
                             I.IH.mkIRNum(0.0))))
          case (s@"-", h:String, m:String) =>
            I.IH.mkIRNum(
              -DH._timeClip(
                 DH._makeDate(
                   DH._makeDay(I.IH.mkIRNum(DH.__initYear),
                               I.IH.mkIRNum(0.0),
                               I.IH.mkIRNum(1.0)),
                   DH._makeTime(I.IH.mkIRNum(h.toDouble),
                                I.IH.mkIRNum(m.toDouble),
                                I.IH.mkIRNum(0.0),
                                I.IH.mkIRNum(0.0)))).getNum)
        }
        I.IH.mkIRNum(tc.getNum - tzoffset.getNum)
      } catch {
        case _:MatchError => try {
          val format3(month, date, year, hours, minutes, seconds, ms, tzs, tzh, tzm) =
            I.IH.toString(string)
          val (yr, m, dt) = (year, month, date) match {
            case (yr:String, null, null) =>
              (yr.toDouble, 1.0, 1.0)
            case (yr:String, m:String, null) =>
              (yr.toDouble, m.toDouble, 1.0)
            case (yr:String, m:String, dt:String) =>
              (yr.toDouble, m.toDouble, dt.toDouble)
          }
          val (h, min, s, milli) = (hours, minutes, seconds, ms) match {
            case (null, null, null, null) =>
              (0.0, 0.0, 0.0, 0.0)
            case (h:String, min:String, null, null) =>
              (h.toDouble, min.toDouble, 0.0, 0.0)
            case (h:String, min:String, s:String, null) =>
              (h.toDouble, min.toDouble, s.toDouble, 0.0)
            case (h:String, min:String, s:String, milli:String) =>
              (h.toDouble, min.toDouble, s.toDouble, milli.toDouble)
          }
          val tc = DH._timeClip(
                     DH._makeDate(
                       DH._makeDay(I.IH.mkIRNum(yr),
                                   I.IH.mkIRNum(m - 1),
                                   I.IH.mkIRNum(dt)),
                       DH._makeTime(I.IH.mkIRNum(h),
                                    I.IH.mkIRNum(min),
                                    I.IH.mkIRNum(s),
                                    I.IH.mkIRNum(milli))))
          val tzoffset = (tzs, tzh, tzm) match {
            case (null, null, null) => I.IH.mkIRNum(0)
            case (s@"+", h:String, m:String) =>
              DH._timeClip(
                DH._makeDate(
                  DH._makeDay(I.IH.mkIRNum(DH.__initYear),
                              I.IH.mkIRNum(0.0),
                              I.IH.mkIRNum(1.0)),
                  DH._makeTime(I.IH.mkIRNum(h.toDouble),
                               I.IH.mkIRNum(m.toDouble),
                               I.IH.mkIRNum(0.0),
                               I.IH.mkIRNum(0.0))))
            case (s@"-", h:String, m:String) =>
              I.IH.mkIRNum(
                -DH._timeClip(
                   DH._makeDate(
                     DH._makeDay(I.IH.mkIRNum(DH.__initYear),
                                 I.IH.mkIRNum(0.0),
                                 I.IH.mkIRNum(1.0)),
                     DH._makeTime(I.IH.mkIRNum(h.toDouble),
                                  I.IH.mkIRNum(m.toDouble),
                                  I.IH.mkIRNum(0.0),
                                  I.IH.mkIRNum(0.0)))).getNum)
          }
          I.IH.mkIRNum(tc.getNum - tzoffset.getNum)
        } catch {
          case _:MatchError => IP.NaN
        }
      }
    }
  }
  /*
   * 15.9.4.3 Date.UTC(year, month[, date[, hours[, minutes[, seconds[, ms]]]]])
   */
  def _utc(year: Val, month: Val, date: Val,
           hours: Val, minutes: Val, seconds: Val, ms: Val): Unit = {
    val (y, m) = (I.IH.toNumber(year), I.IH.toNumber(month))
    val dt = if (!I.IH.isUndef(date)) I.IH.toNumber(date) else I.IH.mkIRNum(1)
    val h = if (!I.IH.isUndef(hours)) I.IH.toNumber(hours) else I.IH.mkIRNum(0)
    val min = if (!I.IH.isUndef(minutes)) I.IH.toNumber(minutes) else I.IH.mkIRNum(0)
    val s = if (!I.IH.isUndef(seconds)) I.IH.toNumber(seconds) else I.IH.mkIRNum(0)
    val milli = if (!I.IH.isUndef(ms)) I.IH.toNumber(ms) else I.IH.mkIRNum(0)
    val yi = I.IH.toInteger(PVal(y)).getNum
    val yr = if (!I.IH.isNaN(y) && 0 <= yi && yi <= 99) I.IH.mkIRNum(1900 + yi) else y
    val tc = DH._timeClip(
               DH._makeDate(DH._makeDay(yr, m, dt),
                            DH._makeTime(h, min, s, milli)))
    I.IS.comp.setReturn(PVal(tc))
  }
  /*
   * 15.9.4.4 Date.now()
   */
  def __now(): IRNumber = {
    // TODO: Check whether java.util.Calendar.getInstance.getTimeInMillis gives
    //       the JavaScript time value
    val date = new java.util.Date()
    I.IH.mkIRNum(date.getTime)
  }
  def _now(): Unit = I.IS.comp.setReturn(PVal(__now))
}
