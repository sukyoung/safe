/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3;
function onSuccessCallback(battery) {
  __result1 = battery.level;
  __result2 = battery.isCharging;
}

function onErrorCallback(error) {
  __result3 = error.name;
}

tizen.systeminfo.getPropertyValue("BATTERY", onSuccessCallback, onErrorCallback);


var __expect1 = 90.2;
var __expect2 = false;
var __expect3 = "UnknownError";
