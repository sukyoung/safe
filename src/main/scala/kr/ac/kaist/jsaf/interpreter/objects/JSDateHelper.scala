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

class JSDateHelper(IH: InterpreterHelper) {
  /*
   * By the Division Algorithm, a = qb + r
   * for some integer q and real number r such that 0 <= r < b
   * a modulo b = r
   */
  def __modulo(a: Double, b: Double): Double = a % b match {
    case r if r < 0 => r + b
    case r => r
  }

  /*
   * 15.9 Date Objects
   * 15.9.1.1 Time Values and Time Range
   */
  val __initYear = 1970.0
  val __yearRange = 285616.0
  val __msRange = 8640000000000000.0
  /*
   * 15.9.1.2 Day Number and Time within Day
   */
  val _msPerDay = 86400000.0
  def _day(t: IRNumber): IRNumber = if (IH.isNaN(t)) IP.NaN else
    IH.mkIRNum(scala.math.floor(t.getNum / _msPerDay))
  def _timeWithinDay(t: IRNumber): IRNumber = if (IH.isNaN(t)) IP.NaN else
    IH.mkIRNum(t.getNum - scala.math.floor(t.getNum / _msPerDay) * _msPerDay)

  /*
   * 15.9.1.3 Year Number
   */
  def _daysInYear(y: IRNumber): IRNumber = if (IH.isNaN(y)) IP.NaN else
    y.getNum match {
      case yn if (__modulo(yn, 4) != 0) => IH.mkIRNum(365)
      case yn if (__modulo(yn, 4) == 0 && __modulo(yn, 100) != 0) => IH.mkIRNum(366)
      case yn if (__modulo(yn, 100) == 0 && __modulo(yn, 400) != 0) => IH.mkIRNum(365)
      case yn if (__modulo(yn, 400) == 0) => IH.mkIRNum(366)
    }
  def _dayFromYear(y: IRNumber): IRNumber = if (IH.isNaN(y)) IP.NaN else {
    val yn = y.getNum
    IH.mkIRNum(365 * (yn - 1970) + scala.math.floor((yn - 1969)/4)
      - scala.math.floor((yn - 1901)/100) + scala.math.floor((yn - 1601)/400))
  }
  def _timeFromYear(y: IRNumber): IRNumber = if (IH.isNaN(y)) IP.NaN else
    IH.mkIRNum(_msPerDay * _dayFromYear(y).getNum)
  def _yearFromTime(t: IRNumber): IRNumber = if (IH.isNaN(t)) IP.NaN else {
    var (l, r): (Double, Double) = (__initYear - __yearRange, __initYear + __yearRange)
    while (l <= r) {
      val mid = scala.math.floor((l + r)/2)
      if (_timeFromYear(IH.mkIRNum(mid)).getNum <= t.getNum) l = mid + 1
      else r = mid - 1
    }
    IH.mkIRNum(r)
  }
  def _inLeapYear(t: IRNumber): IRNumber = if (IH.isNaN(t)) IP.NaN else
    _daysInYear(_yearFromTime(t)).getNum.intValue match {
      case 365 => IH.mkIRNum(0)
      case 366 => IH.mkIRNum(1)
    }

  /*
   * 15.9.1.4 Month Number
   */
  def _monthFromTime(t: IRNumber): IRNumber = if (IH.isNaN(t)) IP.NaN else {
    val inLeapYear = _inLeapYear(t).getNum
    _dayWithinYear(t).getNum match {
      case d if (0 <= d && d < 31) => IH.mkIRNum(0)
      case d if (31 <= d && d < 59 + inLeapYear) => IH.mkIRNum(1)
      case d if (59 + inLeapYear <= d && d < 90 + inLeapYear) => IH.mkIRNum(2)
      case d if (90 + inLeapYear <= d && d < 120 + inLeapYear) => IH.mkIRNum(3)
      case d if (120 + inLeapYear <= d && d < 151 + inLeapYear) => IH.mkIRNum(4)
      case d if (151 + inLeapYear <= d && d < 181 + inLeapYear) => IH.mkIRNum(5)
      case d if (181 + inLeapYear <= d && d < 212 + inLeapYear) => IH.mkIRNum(6)
      case d if (212 + inLeapYear <= d && d < 243 + inLeapYear) => IH.mkIRNum(7)
      case d if (243 + inLeapYear <= d && d < 273 + inLeapYear) => IH.mkIRNum(8)
      case d if (273 + inLeapYear <= d && d < 304 + inLeapYear) => IH.mkIRNum(9)
      case d if (304 + inLeapYear <= d && d < 334 + inLeapYear) => IH.mkIRNum(10)
      case d if (334 + inLeapYear <= d && d < 365 + inLeapYear) => IH.mkIRNum(11)
    }
  }
  def _dayWithinYear(t: IRNumber): IRNumber = if (IH.isNaN(t)) IP.NaN else
    IH.mkIRNum(_day(t).getNum - _dayFromYear(_yearFromTime(t)).getNum)

  /*
   * 15.9.1.5 Date Number
   */
  def _dateFromTime(t: IRNumber): IRNumber = if (IH.isNaN(t)) IP.NaN else {
    val (dayWithinYear, inLeapYear) =
      (_dayWithinYear(t).getNum, _inLeapYear(t).getNum)
    _monthFromTime(t).getNum.intValue match {
      case 0 => IH.mkIRNum(dayWithinYear + 1)
      case 1 => IH.mkIRNum(dayWithinYear - 30)
      case 2 => IH.mkIRNum(dayWithinYear - 58 - inLeapYear)
      case 3 => IH.mkIRNum(dayWithinYear - 89 - inLeapYear)
      case 4 => IH.mkIRNum(dayWithinYear - 119 - inLeapYear)
      case 5 => IH.mkIRNum(dayWithinYear - 150 - inLeapYear)
      case 6 => IH.mkIRNum(dayWithinYear - 180 - inLeapYear)
      case 7 => IH.mkIRNum(dayWithinYear - 211 - inLeapYear)
      case 8 => IH.mkIRNum(dayWithinYear - 242 - inLeapYear)
      case 9 => IH.mkIRNum(dayWithinYear - 272 - inLeapYear)
      case 10 => IH.mkIRNum(dayWithinYear - 303 - inLeapYear)
      case 11 => IH.mkIRNum(dayWithinYear - 333 - inLeapYear)
    }
  }
  
  /*
   * 15.9.1.6 Week Day
   */
  def _weekDay(t: IRNumber): IRNumber = if (IH.isNaN(t)) IP.NaN else
    IH.mkIRNum(__modulo(_day(t).getNum + 4, 7))

  /*
   * TODO: Implement the followings
   * 15.9.1.7 Local Time Zone Adjustment
   * 15.9.1.8 Daylight Saving Time Adjustment
   * 15.9.1.9 Local Time
   */
  def _localTime(t: IRNumber): IRNumber = if (IH.isNaN(t)) IP.NaN else
    t
  def _utc(t: IRNumber): IRNumber = if (IH.isNaN(t)) IP.NaN else
    t

  /*
   * 15.9.1.10 Hours, Minutes, Second, and Milliseconds
   */
  val _hoursPerDay = 24
  val _minutesPerHour = 60
  val _secondsPerMinute = 60
  val _msPerSecond = 1000
  val _msPerMinute = 60000 // = _msPerSecond * _secondsPerMinute
  val _msPerHour = 3600000 // = _msPerMinute * _minutesPerHour
  def _hourFromTime(t: IRNumber): IRNumber = if (IH.isNaN(t)) IP.NaN else
    IH.mkIRNum(__modulo(scala.math.floor(t.getNum / _msPerHour), _hoursPerDay))
  def _minFromTime(t: IRNumber): IRNumber = if (IH.isNaN(t)) IP.NaN else
    IH.mkIRNum(__modulo(scala.math.floor(t.getNum / _msPerMinute), _minutesPerHour))
  def _secFromTime(t: IRNumber): IRNumber = if (IH.isNaN(t)) IP.NaN else
    IH.mkIRNum(__modulo(scala.math.floor(t.getNum / _msPerSecond), _secondsPerMinute))
  def _msFromTime(t: IRNumber): IRNumber = if (IH.isNaN(t)) IP.NaN else
    IH.mkIRNum(__modulo(t.getNum, _msPerSecond))

  /*
   * 15.9.1.11 MakeTime(hour, min, sec, ms)
   */
  def toInteger(v: IRNumber): IRNumber = v match {
    case n if IH.isNaN(n) => IP.plusZero
    case n if (IH.isZero(n) || IH.isInfinite(n)) => n
    case n => IH.mkIRNum(math.signum(n.getNum) * math.floor(math.abs(n.getNum)))
  }
  def _makeTime(hour: IRNumber, min: IRNumber, sec: IRNumber, ms: IRNumber): IRNumber =
    if (IH.isNaN(hour) || IH.isNaN(min) || IH.isNaN(sec) || IH.isNaN(ms)) IP.NaN
    else if (IH.isInfinite(hour) || IH.isInfinite(min) || IH.isInfinite(sec) || IH.isInfinite(ms)) IP.NaN
    else {
      val (h, m, s, milli) = (toInteger(hour), toInteger(min), toInteger(sec), toInteger(ms))
      IH.mkIRNum(h.getNum * _msPerHour + m.getNum * _msPerMinute + s.getNum * _msPerSecond + milli.getNum)
    }

  /*
   * 15.9.1.12 MakeDay(year, month, date)
   */
  def __lessYMD(y1: IRNumber, m1: IRNumber, d1: IRNumber, y2: IRNumber, m2: IRNumber, d2: IRNumber) =
    if (y1.getNum != y2.getNum) y1.getNum < y2.getNum
    else if (m1.getNum != m2.getNum) m1.getNum < m2.getNum
    else d1.getNum < d2.getNum
  def _makeDay(year: IRNumber, month: IRNumber, date: IRNumber): IRNumber =
    if (IH.isNaN(year) || IH.isNaN(month) || IH.isNaN(date)) IP.NaN
    else if (IH.isInfinite(year) || IH.isInfinite(month) || IH.isInfinite(date)) IP.NaN
    else {
      val (y, m, dt) = (toInteger(year), toInteger(month), toInteger(date))
      val ym = IH.mkIRNum(y.getNum + scala.math.floor(m.getNum / 12))
      val mn = IH.mkIRNum(__modulo(m.getNum, 12))
      val dd = IH.mkIRNum(1)
      var (l, r, t): (Double, Double, Double) = (-__msRange, __msRange, 0.0)
      val (nl, nr) = (IH.mkIRNum(l), IH.mkIRNum(r))
      if (__lessYMD(ym, mn, dd, _yearFromTime(nl), _monthFromTime(nl), _dateFromTime(nl)) ||
          __lessYMD(_yearFromTime(nr), _monthFromTime(nr), _dateFromTime(nr), ym, mn, dd)) IP.NaN
      else {
        while (l <= r) {
          val mid = scala.math.floor((l + r)/2)
          val nmid = IH.mkIRNum(mid)
          val (ymid, mmid, dmid) = (_yearFromTime(nmid), _monthFromTime(nmid), _dateFromTime(nmid))
          if (__lessYMD(ymid, mmid, dmid, ym, mn, dd)) l = mid + 1
          else if (__lessYMD(ym, mn, dd, ymid, mmid, dmid)) r = mid - 1
          else {
            t = mid
            l = r + 1 // break
          }
        }
        IH.mkIRNum(_day(IH.mkIRNum(t)).getNum + dt.getNum - 1)
      }
    }

  /*
   * 15.9.1.13 MakeDate(day, time)
   */
  def _makeDate(day: IRNumber, time: IRNumber): IRNumber =
    if (IH.isNaN(day) || IH.isNaN(time)) IP.NaN
    else if (IH.isInfinite(day) || IH.isInfinite(time)) IP.NaN
    else IH.mkIRNum(day.getNum * _msPerDay + time.getNum)

  /*
   * 15.9.1.14 TimeClip(time)
   */
  def _timeClip(time: IRNumber): IRNumber =
    if (IH.isNaN(time)) IP.NaN
    else if (IH.isInfinite(time)) IP.NaN
    else if (scala.math.abs(time.getNum) > __msRange) IP.NaN
    else IH.mkIRNum(toInteger(time).getNum + IP.plusZero.getNum)

  /*
   * TODO:
   * 15.9.1.15 Date Time String Format
   * 15.9.1.15.1 Extended years
   */
}
