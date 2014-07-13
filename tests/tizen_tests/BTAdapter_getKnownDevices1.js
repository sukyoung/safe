/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
var adapter = tizen.bluetooth.getDefaultAdapter();

 function onGotDevices(devices) {
   __result1 = devices[0].name;
 }

 function onError(e) {
   __result2 = e.name;
 }

 function onBluetoothsetPowered() {
    adapter.getKnownDevices(onGotDevices, onError);
 }

 // Turns on Bluetooth
 adapter.setPowered(true, onBluetoothsetPowered);

var __expect1 = "bluetooth";
var __expect2 = "UnknownError";