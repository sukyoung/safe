/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var a = new tizen.AlarmRelative(tizen.alarm.PERIOD_HOUR);
var b = new tizen.AlarmRelative(tizen.alarm.PERIOD_HOUR, 2 * tizen.alarm.PERIOD_MINUTE);

var __result1 = a.id;
var __expect1 = null;
var __result2 = a.delay;
var __expect2 = 3600;
var __result3 = a.period;
var __expect3 = null;

var __result4 = b.id;
var __expect4 = null;
var __result5 = b.delay;
var __expect5 = 3600;
var __result6 = b.period;
var __expect6 = 120;