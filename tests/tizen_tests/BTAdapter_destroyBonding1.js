/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
var adapter = tizen.bluetooth.getDefaultAdapter();

 function gotDevice(device) {

      adapter.destroyBonding(device.address, function() {
        __result1 = 1;
       },
       function(e) {
        __result2 = e.name;
       });

 }

 var deviceAddress = "35:F4:59:D1:7A:03";
 adapter.getDevice(deviceAddress, gotDevice, function(e) {
 } );

var __expect1 = 1;
var __expect2 = "SecurityError";