/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var a = new tizen.AlarmAbsolute(new Date());
tizen.alarm.add(a, "org.tizen.browser");
var __result1 = tizen.alarm.removeAll();

var __expect1 = undefined;