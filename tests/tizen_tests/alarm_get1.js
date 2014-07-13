/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result3, __result4;
var date = new Date(2013, 5, 30);
var a = new tizen.AlarmAbsolute(date);
tizen.alarm.add(a, "org.tizen.browser");
var al1 = tizen.alarm.get(a.id);

var al2, al3;
try {
    al2 = tizen.alarm.get(1);
    }catch(e){
    __result3 = e.name;
    }
try {
    al3 = tizen.alarm.get(null);
    }catch(e){
    __result4 = e.name;
    }


var __result1 = al1.id;
var __expect1 = "161478968"
var __result2 = al1.date.toString();
var __expect2 = "Thu May 30 2013 00:00:00 GMT+0900 (...)"
var __expect3 = "NotFoundError"
var __expect4 = "InvalidValuesError"