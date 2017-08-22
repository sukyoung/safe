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
import kr.ac.kaist.safe.util._

class JSDateHelper(IH: InterpreterHelper) {
  /*
   * By the Division Algorithm, a = qb + r
   * for some integer q and real number r such that 0 <= r < b
   * a modulo b = r
   */
  def modulo(a: Double, b: Double): Double = a % b match {
    case r if r < 0 => r + b
    case r => r
  }

  /*
   * 15.9 Date Objects
   * 15.9.1.1 Time Values and Time Range
   */
  val initYear: Double = 1970.0
  val yearRange: Double = 285616.0
  val msRange: Double = 8640000000000000.0
  /*
   * 15.9.1.2 Day Number and Time within Day
   */
  val msPerDay: Double = 86400000.0
  def day(t: EJSNumber): EJSNumber = if (IH.isNaN(t)) IP.NaN else
    IH.mkIRNum(scala.math.floor(t.num / msPerDay))
  def timeWithinDay(t: EJSNumber): EJSNumber = if (IH.isNaN(t)) IP.NaN else
    IH.mkIRNum(t.num - scala.math.floor(t.num / msPerDay) * msPerDay)

  /*
   * 15.9.1.3 Year Number
   */
  def daysInYear(y: EJSNumber): EJSNumber = if (IH.isNaN(y)) IP.NaN else
    y.num match {
      case yn if modulo(yn, 4) != 0 => IH.mkIRNum(365)
      case yn if modulo(yn, 4) == 0 && modulo(yn, 100) != 0 => IH.mkIRNum(366)
      case yn if modulo(yn, 100) == 0 && modulo(yn, 400) != 0 => IH.mkIRNum(365)
      case yn if modulo(yn, 400) == 0 => IH.mkIRNum(366)
    }
  def dayFromYear(y: EJSNumber): EJSNumber = if (IH.isNaN(y)) IP.NaN else {
    val yn = y.num
    IH.mkIRNum(365 * (yn - 1970) + scala.math.floor((yn - 1969) / 4)
      - scala.math.floor((yn - 1901) / 100) + scala.math.floor((yn - 1601) / 400))
  }
  def timeFromYear(y: EJSNumber): EJSNumber = if (IH.isNaN(y)) IP.NaN else
    IH.mkIRNum(msPerDay * dayFromYear(y).num)
  def yearFromTime(t: EJSNumber): EJSNumber = if (IH.isNaN(t)) IP.NaN else {
    var (l, r): (Double, Double) = (initYear - yearRange, initYear + yearRange)
    while (l <= r) {
      val mid = scala.math.floor((l + r) / 2)
      if (timeFromYear(IH.mkIRNum(mid)).num <= t.num) l = mid + 1
      else r = mid - 1
    }
    IH.mkIRNum(r)
  }
  def inLeapYear(t: EJSNumber): EJSNumber = if (IH.isNaN(t)) IP.NaN else
    daysInYear(yearFromTime(t)).num.intValue match {
      case 365 => IH.mkIRNum(0)
      case 366 => IH.mkIRNum(1)
    }

  /*
   * 15.9.1.4 Month Number
   */
  def monthFromTime(t: EJSNumber): EJSNumber = if (IH.isNaN(t)) IP.NaN else {
    val isInLeapYear = inLeapYear(t).num
    dayWithinYear(t).num match {
      case d if 0 <= d && d < 31 => IH.mkIRNum(0)
      case d if 31 <= d && d < 59 + isInLeapYear => IH.mkIRNum(1)
      case d if 59 + isInLeapYear <= d && d < 90 + isInLeapYear => IH.mkIRNum(2)
      case d if 90 + isInLeapYear <= d && d < 120 + isInLeapYear => IH.mkIRNum(3)
      case d if 120 + isInLeapYear <= d && d < 151 + isInLeapYear => IH.mkIRNum(4)
      case d if 151 + isInLeapYear <= d && d < 181 + isInLeapYear => IH.mkIRNum(5)
      case d if 181 + isInLeapYear <= d && d < 212 + isInLeapYear => IH.mkIRNum(6)
      case d if 212 + isInLeapYear <= d && d < 243 + isInLeapYear => IH.mkIRNum(7)
      case d if 243 + isInLeapYear <= d && d < 273 + isInLeapYear => IH.mkIRNum(8)
      case d if 273 + isInLeapYear <= d && d < 304 + isInLeapYear => IH.mkIRNum(9)
      case d if 304 + isInLeapYear <= d && d < 334 + isInLeapYear => IH.mkIRNum(10)
      case d if 334 + isInLeapYear <= d && d < 365 + isInLeapYear => IH.mkIRNum(11)
    }
  }
  def dayWithinYear(t: EJSNumber): EJSNumber = if (IH.isNaN(t)) IP.NaN else
    IH.mkIRNum(day(t).num - dayFromYear(yearFromTime(t)).num)

  /*
   * 15.9.1.5 Date Number
   */
  def dateFromTime(t: EJSNumber): EJSNumber = if (IH.isNaN(t)) IP.NaN else {
    val (isDayWithinYear, isInLeapYear) =
      (dayWithinYear(t).num, inLeapYear(t).num)
    monthFromTime(t).num.intValue match {
      case 0 => IH.mkIRNum(isDayWithinYear + 1)
      case 1 => IH.mkIRNum(isDayWithinYear - 30)
      case 2 => IH.mkIRNum(isDayWithinYear - 58 - isInLeapYear)
      case 3 => IH.mkIRNum(isDayWithinYear - 89 - isInLeapYear)
      case 4 => IH.mkIRNum(isDayWithinYear - 119 - isInLeapYear)
      case 5 => IH.mkIRNum(isDayWithinYear - 150 - isInLeapYear)
      case 6 => IH.mkIRNum(isDayWithinYear - 180 - isInLeapYear)
      case 7 => IH.mkIRNum(isDayWithinYear - 211 - isInLeapYear)
      case 8 => IH.mkIRNum(isDayWithinYear - 242 - isInLeapYear)
      case 9 => IH.mkIRNum(isDayWithinYear - 272 - isInLeapYear)
      case 10 => IH.mkIRNum(isDayWithinYear - 303 - isInLeapYear)
      case 11 => IH.mkIRNum(isDayWithinYear - 333 - isInLeapYear)
    }
  }

  /*
   * 15.9.1.6 Week Day
   */
  def weekDay(t: EJSNumber): EJSNumber = if (IH.isNaN(t)) IP.NaN else
    IH.mkIRNum(modulo(day(t).num + 4, 7))

  /*
   * TODO: Implement the followings
   * 15.9.1.7 Local Time Zone Adjustment
   * 15.9.1.8 Daylight Saving Time Adjustment
   * 15.9.1.9 Local Time
   */
  def localTime(t: EJSNumber): EJSNumber = if (IH.isNaN(t)) IP.NaN else
    t
  def utc(t: EJSNumber): EJSNumber = if (IH.isNaN(t)) IP.NaN else
    t

  /*
   * 15.9.1.10 Hours, Minutes, Second, and Milliseconds
   */
  val hoursPerDay: Int = 24
  val minutesPerHour: Int = 60
  val secondsPerMinute: Int = 60
  val msPerSecond: Int = 1000
  val msPerMinute: Int = 60000 // = msPerSecond * secondsPerMinute
  val msPerHour: Int = 3600000 // = msPerMinute * minutesPerHour
  def hourFromTime(t: EJSNumber): EJSNumber = if (IH.isNaN(t)) IP.NaN else
    IH.mkIRNum(modulo(scala.math.floor(t.num / msPerHour), hoursPerDay))
  def minFromTime(t: EJSNumber): EJSNumber = if (IH.isNaN(t)) IP.NaN else
    IH.mkIRNum(modulo(scala.math.floor(t.num / msPerMinute), minutesPerHour))
  def secFromTime(t: EJSNumber): EJSNumber = if (IH.isNaN(t)) IP.NaN else
    IH.mkIRNum(modulo(scala.math.floor(t.num / msPerSecond), secondsPerMinute))
  def msFromTime(t: EJSNumber): EJSNumber = if (IH.isNaN(t)) IP.NaN else
    IH.mkIRNum(modulo(t.num, msPerSecond))

  /*
   * 15.9.1.11 MakeTime(hour, min, sec, ms)
   */
  def toInteger(v: EJSNumber): EJSNumber = v match {
    case n if IH.isNaN(n) => IP.plusZero
    case n if IH.isZero(n) || IH.isInfinite(n) => n
    case n => IH.mkIRNum(math.signum(n.num) * math.floor(math.abs(n.num)))
  }
  def makeTime(
    hour: EJSNumber,
    min: EJSNumber,
    sec: EJSNumber,
    ms: EJSNumber
  ): EJSNumber =
    if (IH.isNaN(hour) || IH.isNaN(min) || IH.isNaN(sec) || IH.isNaN(ms)) IP.NaN
    else if (IH.isInfinite(hour) || IH.isInfinite(min) || IH.isInfinite(sec) || IH.isInfinite(ms)) IP.NaN
    else {
      val (h, m, s, milli) = (toInteger(hour), toInteger(min), toInteger(sec), toInteger(ms))
      IH.mkIRNum(h.num * msPerHour + m.num * msPerMinute + s.num * msPerSecond + milli.num)
    }

  /*
   * 15.9.1.12 MakeDay(year, month, date)
   */
  def lessYMD(
    y1: EJSNumber,
    m1: EJSNumber,
    d1: EJSNumber,
    y2: EJSNumber,
    m2: EJSNumber,
    d2: EJSNumber
  ): Boolean =
    if (y1.num != y2.num) y1.num < y2.num
    else if (m1.num != m2.num) m1.num < m2.num
    else d1.num < d2.num
  def makeDay(year: EJSNumber, month: EJSNumber, date: EJSNumber): EJSNumber =
    if (IH.isNaN(year) || IH.isNaN(month) || IH.isNaN(date)) IP.NaN
    else if (IH.isInfinite(year) || IH.isInfinite(month) || IH.isInfinite(date)) IP.NaN
    else {
      val (y, m, dt) = (toInteger(year), toInteger(month), toInteger(date))
      val ym = IH.mkIRNum(y.num + scala.math.floor(m.num / 12))
      val mn = IH.mkIRNum(modulo(m.num, 12))
      val dd = IH.mkIRNum(1)
      var (l, r, t): (Double, Double, Double) = (-msRange, msRange, 0.0)
      val (nl, nr) = (IH.mkIRNum(l), IH.mkIRNum(r))
      if (lessYMD(ym, mn, dd, yearFromTime(nl), monthFromTime(nl), dateFromTime(nl)) ||
        lessYMD(yearFromTime(nr), monthFromTime(nr), dateFromTime(nr), ym, mn, dd)) IP.NaN
      else {
        while (l <= r) {
          val mid = scala.math.floor((l + r) / 2)
          val nmid = IH.mkIRNum(mid)
          val (ymid, mmid, dmid) = (yearFromTime(nmid), monthFromTime(nmid), dateFromTime(nmid))
          if (lessYMD(ymid, mmid, dmid, ym, mn, dd)) l = mid + 1
          else if (lessYMD(ym, mn, dd, ymid, mmid, dmid)) r = mid - 1
          else {
            t = mid
            l = r + 1 // break
          }
        }
        IH.mkIRNum(day(IH.mkIRNum(t)).num + dt.num - 1)
      }
    }

  /*
   * 15.9.1.13 MakeDate(day, time)
   */
  def makeDate(day: EJSNumber, time: EJSNumber): EJSNumber =
    if (IH.isNaN(day) || IH.isNaN(time)) IP.NaN
    else if (IH.isInfinite(day) || IH.isInfinite(time)) IP.NaN
    else IH.mkIRNum(day.num * msPerDay + time.num)

  /*
   * 15.9.1.14 TimeClip(time)
   */
  def timeClip(time: EJSNumber): EJSNumber =
    if (IH.isNaN(time)) IP.NaN
    else if (IH.isInfinite(time)) IP.NaN
    else if (scala.math.abs(time.num) > msRange) IP.NaN
    else IH.mkIRNum(toInteger(time).num + IP.plusZero.num)

  /*
   * TODO:
   * 15.9.1.15 Date Time String Format
   * 15.9.1.15.1 Extended years
   */
}
