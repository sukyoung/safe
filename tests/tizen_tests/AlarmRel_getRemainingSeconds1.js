/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

var a1 = new tizen.AlarmRelative(tizen.alarm.PERIOD_HOUR, tizen.alarm.PERIOD_MINUTE);
var b1 = a1.getRemainingSeconds();
var a2 = new tizen.AlarmRelative(tizen.alarm.PERIOD_HOUR, tizen.alarm.PERIOD_MINUTE);
tizen.alarm.add(a2, "org.tizen.browser");
var b2 = a2.getRemainingSeconds();

var __result1 = b1;
var __expect1 = null;
var __result2 = b2;
var __expect2 = 3599;