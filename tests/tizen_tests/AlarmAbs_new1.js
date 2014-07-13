/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var date = new Date(2013, 5, 30);
var a = new tizen.AlarmAbsolute(date);
var b = new tizen.AlarmAbsolute(date, 2 * tizen.alarm.PERIOD_DAY);
var c = new tizen.AlarmAbsolute(date, ["SA", "SU"]);

var __result1 = a.id;
var __expect1 = null
var __result2 = a.date.toString();
var __expect2 = "Thu May 30 2013 00:00:00 GMT+0900 (...)"
var __result3 = a.period;
var __expect3 = null
var __result4 = a.daysOfTheWeek.length;
var __expect4 = 0

var __result5 = b.id;
var __expect5 = null
var __result6 = b.date.toString();
var __expect6 = "Thu May 30 2013 00:00:00 GMT+0900 (...)"
var __result7 = b.period;
var __expect7 = 172800
var __result8 = b.daysOfTheWeek.length;
var __expect8 = 0

var __result9 = c.id;
var __expect9 = null
var __result10 = c.date.toString();
var __expect10 = "Thu May 30 2013 00:00:00 GMT+0900 (...)"
var __result11 = c.period;
var __expect11 = null
var __result12 = c.daysOfTheWeek[0];
var __expect12 = "SA"
var __result13 = c.daysOfTheWeek[1];
var __expect13 = "SU"