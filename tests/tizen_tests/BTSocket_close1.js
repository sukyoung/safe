/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
var adapter = tizen.bluetooth.getDefaultAdapter();
var read;
 function onSocketConnected(socket) {
    socket.onclose = function() {
      __result2 = 1;
    };

    // Sends data to peer.
    socket.writeData ([1,2,3]);
    read = socket.readData();
    __result1 = socket.close();
 }

 function onSocketError(e) {
 }

 function onDeviceReady(device) {
   device.connectToServiceByUUID("5BCE9431-6C75-32AB-AFE0-2EC108A30860", onSocketConnected, onSocketError );
 }
 function onSetPowered() {
    adapter.getDevice("35:F4:59:D1:7A:03", onDeviceReady, function(e) { });
 }

 adapter.setPowered(true, onSetPowered, function(e) {});


var __expect1 = undefined;
var __expect2 = 1;