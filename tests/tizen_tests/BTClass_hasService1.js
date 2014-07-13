/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
var adapter = tizen.bluetooth.getDefaultAdapter();
 adapter.getDevice("12:34:56:78:9A:BC", function(device) {
    __result1 = device.deviceClass.hasService(tizen.bluetooth.deviceService.POSITIONING);
 }, function(e) {
    __result2 = e.name;
 });


 __expect1 = true;
 __expect2 = "UnknownError"
