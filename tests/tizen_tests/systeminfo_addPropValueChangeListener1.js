/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function onSuccessCallback(battery) {
     __result1 = battery.level;
     __result2 = battery.isCharging;
 }


 var __result3 = tizen.systeminfo.addPropertyValueChangeListener("BATTERY", onSuccessCallback, {lowThreshold : 0.2});
 var __expect3 = 1;


var __expect1 = 0.2;
var __expect2 = false;
