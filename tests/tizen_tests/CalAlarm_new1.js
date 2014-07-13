/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result2;
try {
    var alarm = new tizen.CalendarAlarm();
} catch(e) {
   __result2 = e.name;
}

var __result1 = alarm;
var __expect1 = undefined;
var __expect2 = "TypeMismatchError";

var alarm1 = new tizen.CalendarAlarm(new tizen.TimeDuration(30, "MINS"), "SOUND");

var __result3 = alarm1.before.length;
var __expect3 = 30;
var __result4 = alarm1.method;
var __expect4 = "SOUND";