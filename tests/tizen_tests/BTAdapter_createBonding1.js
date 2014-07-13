/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1, __result2;
function onBondingSuccess(device) {
  __result1= device.name;
 }

 function onError(e) {
 __result2= e.name;
 }

 var adapter = tizen.bluetooth.getDefaultAdapter();
 adapter.createBonding("35:F4:59:D1:7A:03", onBondingSuccess, onError);



var __expect1 = "bluetooth";
var __expect2 = "SecurityError";