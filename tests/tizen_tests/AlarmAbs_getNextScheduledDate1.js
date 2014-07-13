/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var date = new Date(2013, 5, 30);
var a1 = new tizen.AlarmAbsolute(date);
var b1 = a1.getNextScheduledDate();
var a2 = new tizen.AlarmAbsolute(date);
tizen.alarm.add(a2, "org.tizen.browser");
var b2 = a2.getNextScheduledDate();

var __result1 = b1;
var __expect1 = null
var __result2 = b2.toString();
var __expect2 = "Thu May 30 2013 00:00:00 GMT+0900 (...)"
