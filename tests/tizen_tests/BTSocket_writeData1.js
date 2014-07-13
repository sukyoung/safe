/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;
var adapter = tizen.bluetooth.getDefaultAdapter();

 function onSocketConnected(socket) {
    // Sends data to peer.
    __result1 = socket.writeData ([1,2,3]);
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

var __expect1 = 1;