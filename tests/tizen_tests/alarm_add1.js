/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var date = new Date(2013, 5, 30);
var a = new tizen.AlarmAbsolute(date);
var b = new tizen.AlarmAbsolute(date);
var appControl = new tizen.ApplicationControl("http://tizen.org/appcontrol/operation/view", "http://www.tizen.org");
tizen.alarm.add(a, "org.tizen.browser", appControl);

var __result3;
try {
  tizen.alarm.add(b, "org.tizen.browser", 1);
}catch(e){
  __result3 = e.name;
}

var __result1 = a.id;
var __expect1 = "161478968"
var __result2 = a.date.toString();
var __expect2 = "Thu May 30 2013 00:00:00 GMT+0900 (...)"
var __expect3 = "TypeMismatchError"