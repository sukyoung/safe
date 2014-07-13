/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var date = new Date(2013, 5, 30);
var a = new tizen.AlarmAbsolute(date);
tizen.alarm.add(a, "org.tizen.browser");
var al = tizen.alarm.getAll();

var __result1 = al[0].id;
var __expect1 = "161478968"
var __result2 = al[0].date.toString();
var __expect2 = "Thu May 30 2013 00:00:00 GMT+0900 (...)"
var __result3 = al.length;
var __expect3 = 1;
