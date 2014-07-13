/*******************************************************************************
    Copyright (c) 2013, S-Core.
    All rights reserved.

    Use is subject to license terms.

    This distribution may include materials developed by third parties.
 ******************************************************************************/
var __result1;
try {
  var adapter = tizen.bluetooth.getDefaultAdapter();
 } catch (err) {
  __result1 = err.name;
 }

 var __expect1 = "SecurityError";
 var __result2 = adapter.name;
 var __expect2 = "bluetooth";
 var __result3 = adapter.address;
 var __expect3 = "3:1:2";
 var __result4 = adapter.powered;
 var __expect4 = false;
 var __result5 = adapter.visible;
 var __expect5 = false;

