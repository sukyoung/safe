/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
var date = new Date(2013, 5, 30);
var a = new tizen.AlarmAbsolute(date);
tizen.alarm.add(a, "org.tizen.browser");
__result1 = tizen.alarm.remove(a.id);
var al;
try {
    al = tizen.alarm.get(a.id);
}catch(e){
    __result2 = e.name;
}

var __expect1 = undefined;
var __expect2 = "NotFoundError"
