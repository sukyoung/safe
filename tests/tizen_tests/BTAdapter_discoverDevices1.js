/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2, __result3, __result4;
var adapter = tizen.bluetooth.getDefaultAdapter();

function startDiscovery() {

  var discoverDevicesSuccessCallback = {
      onstarted: function() {
        __result1 = 1;
      },
      ondevicefound: function(device) {
        __result2 = device.name;
      },
      ondevicedisappeared: function(address) {
        __result3 = address;
      },
      onfinished: function(devices) {
        __result4 = devices[0].name;
      }
  };

  // Starts searching for nearby devices, for 12 sec.
  adapter.discoverDevices(discoverDevicesSuccessCallback, function(e){
  });
 }

 function onSetPoweredError(e) {
 }

 adapter.setPowered(true, startDiscovery, onSetPoweredError);


 var __expect1 = 1;
 var __expect2 = "bluetooth";
 var __expect3 = "3:1:2";
 var __expect4 = "bluetooth";
