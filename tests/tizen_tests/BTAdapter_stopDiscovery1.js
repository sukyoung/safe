/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
var adapter = tizen.bluetooth.getDefaultAdapter();

// Calls this method whenever user finds one of the device
function cancelDiscovery() {
   adapter.stopDiscovery(function() {
     __result1 = 1;
   },
    function (e) {
      __result2 = e.name;
    });
}

function startDiscovery() {

  var discoverDevicesSuccessCallback = {
      onstarted: function() {
      },
      ondevicefound: function(device) {
          cancelDiscovery();
      },
      ondevicedisappeared: function(address) {
      },
      onfinished: function(devices) {
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
 var __expect2 = "UnknownError";
