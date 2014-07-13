/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/

function onSuccessCallback(battery) {
 }


 var watchId = tizen.systeminfo.addPropertyValueChangeListener("BATTERY", onSuccessCallback, {lowThreshold : 0.2});
 var __result1 = tizen.systeminfo.removePropertyValueChangeListener(watchId);
 var __expect1 = undefined;

